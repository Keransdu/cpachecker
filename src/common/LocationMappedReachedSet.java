package common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cfa.objectmodel.CFANode;
import cpa.common.CPAAlgorithm;
import cpa.common.interfaces.AbstractElementWithLocation;
import cpa.common.interfaces.Precision;
import cpa.common.interfaces.ReachedSet;

/**
 * A class that can be used by CPAs to build specialized versions of
 * the "reached" set used by CPAAlgorithm.
 *
 * It keeps the AbstractElements grouped by program locations (CFANodes),
 * using a Map for the implementation
 *
 * @see CPAAlgorithm.createReachedSet
 *
 * @author Alberto Griggio <alberto.griggio@disi.unitn.it>
 */
public class LocationMappedReachedSet implements ReachedSet {

  public class LocationMappedProjection implements Collection<AbstractElementWithLocation> {
    private final Map<CFANode, Set<AbstractElementWithLocation>> repr;
    private int numElems;

    private class Iter implements Iterator<AbstractElementWithLocation> {
      private final Iterator<Map.Entry<CFANode, Set<AbstractElementWithLocation>>> outer;
      private Iterator<AbstractElementWithLocation> inner;

      Iter(Iterator<Map.Entry<CFANode, Set<AbstractElementWithLocation>>> it) {
        outer = it;
        advanceInner();
      }

      private void advanceInner() {
        inner = null;
        while (inner == null && outer.hasNext()) {
          Set<AbstractElementWithLocation> s = outer.next().getValue();
          if (!s.isEmpty()) {
            inner = s.iterator();
          }
        }
      }

      @Override
      public boolean hasNext() {
        if (inner == null) return false;
        if (inner.hasNext()) return true;
        advanceInner();
        return inner != null && inner.hasNext();
      }

      @Override
      public AbstractElementWithLocation next() {
        return inner.next();
      }

      @Override
      public void remove() {
        throw new RuntimeException("Remove not supported!");
      }
    }

    public LocationMappedProjection() {
      repr = new HashMap<CFANode, Set<AbstractElementWithLocation>>();
      numElems = 0;
    }

    public Set<AbstractElementWithLocation> get(CFANode loc) {
      if (repr.containsKey(loc)) {
        return repr.get(loc);
      } else {
        return null;
      }
    }

    @Override
    public boolean add(AbstractElementWithLocation elem) {
      // AbstractElementWithLocation e = (AbstractElementWithLocation)elem;
      CFANode loc = elem.getLocationNode();
      if (!repr.containsKey(loc)) {
        repr.put(loc, new HashSet<AbstractElementWithLocation>());
      }
      Set<AbstractElementWithLocation> s = repr.get(loc);
      boolean added = s.add(elem);
      if (added) {
        ++numElems;
      }
      return added;
    }

    @Override
    public boolean addAll(Collection<? extends AbstractElementWithLocation> elems) {
      boolean added = false;
      for (AbstractElementWithLocation e : elems) {
        added |= add(e);
      }
      return added;
    }

    @Override
    public void clear() {
      repr.clear();
      numElems = 0;
    }

    @Override
    public boolean contains(Object o) {
      if (!(o instanceof AbstractElementWithLocation)) return false;
      AbstractElementWithLocation e = (AbstractElementWithLocation)o;
      CFANode loc = e.getLocationNode();
      if (!repr.containsKey(loc)) return false;
      return repr.get(loc).contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> elems) {
      for (Object o : elems) {
        if (!contains(o)) return false;
      }
      return true;
    }

    @Override
    public boolean isEmpty() {
      /*if (repr.isEmpty()) return true;
      for (Set<AbstractElementWithLocation> s : repr.values()) {
        if (!s.isEmpty()) return false;
      }
      return true;*/
      return (0 == numElems);
    }

    @Override
    public Iterator<AbstractElementWithLocation> iterator() {
      return new Iter(repr.entrySet().iterator());
    }

    @Override
    public boolean remove(Object o) {
      if (!(o instanceof AbstractElementWithLocation)) return false;
      AbstractElementWithLocation e = (AbstractElementWithLocation)o;
      CFANode loc = e.getLocationNode();
      if (!repr.containsKey(loc)) return false;
      Set<AbstractElementWithLocation> s = repr.get(loc);
      boolean ret = s.remove(o);
      if (ret) {
        --numElems;
      }
      if (s.isEmpty()) {
        repr.remove(loc);
      }
      return ret;
    }

    @Override
    public boolean removeAll(Collection<?> elems) {
      boolean changed = false;
      for (Object o : elems) {
        changed |= remove(o);
      }
      return changed;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
      throw new RuntimeException("retainAll not implemented!");
    }

    @Override
    public int size() {
      return numElems;
    }

    @Override
    public Object[] toArray() {
      throw new RuntimeException("toArray not implemented!");
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
      throw new RuntimeException("toArray not implemented!");
    }
  }
  
  private final HashSet<Pair<AbstractElementWithLocation,Precision>> data;
  private final HashMap<AbstractElementWithLocation,Integer> entryCount;
  private final LocationMappedProjection elements;
  
  public LocationMappedReachedSet() {
    data = new HashSet<Pair<AbstractElementWithLocation,Precision>>();
    entryCount = new HashMap<AbstractElementWithLocation,Integer>();
    elements = new LocationMappedProjection();
  }

  public boolean add(Pair<AbstractElementWithLocation, Precision> pPair) {
    if (data.add(pPair)) {
      Integer nEntries = entryCount.get(pPair.getFirst());
      if (nEntries != null) {
        ++nEntries;
      } else {
        entryCount.put(pPair.getFirst(), 1);
        elements.add(pPair.getFirst());
      }
      return true;
    }
    return false;
  }

  public boolean addAll(
                     Collection<Pair<AbstractElementWithLocation, Precision>> pToAdd) {
    boolean ret = false;
    for (Pair<AbstractElementWithLocation, Precision> pair : pToAdd) {
      if (data.add(pair)) {
        ret = true;
        Integer nEntries = entryCount.get(pair.getFirst());
        if (nEntries != null) {
          ++nEntries;
        } else {
          entryCount.put(pair.getFirst(), 1);
          elements.add(pair.getFirst());
        }
      }
    }
    return ret;
  }

  public void clear() {
    data.clear();
    entryCount.clear();
    elements.clear();
  }

  public boolean removeAll(
                        Collection<Pair<AbstractElementWithLocation, Precision>> pToRemove) {
    boolean ret = false;
    for (Pair<AbstractElementWithLocation, Precision> pair : pToRemove) {
      if (data.remove(pair)) {
        ret = true;
        Integer nEntries = entryCount.get(pair.getFirst());
        assert (nEntries != null);
        assert (nEntries >= 1);
        if (1 == nEntries) {
          entryCount.remove(pair.getFirst());
          elements.remove(pair.getFirst());
        } else {
          --nEntries;
        }
      }
    }
    return ret;
  }

  public Iterator<Pair<AbstractElementWithLocation, Precision>> iterator() {
    return data.iterator();
  }
  
  public Collection<AbstractElementWithLocation> getAbstractElementSet() {
    return elements;
  }
}
