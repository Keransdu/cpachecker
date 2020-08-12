// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.sosy_lab.cpachecker.util.AbstractStates.extractStateByType;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import org.sosy_lab.common.collect.PathCopyingPersistentTreeMap;
import org.sosy_lab.common.collect.PersistentMap;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.FormulaReportingState;
import org.sosy_lab.cpachecker.core.interfaces.Graphable;
import org.sosy_lab.cpachecker.core.interfaces.NonMergeableAbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Partitionable;
import org.sosy_lab.cpachecker.cpa.arg.Splitable;
import org.sosy_lab.cpachecker.util.AbstractStates;
import org.sosy_lab.cpachecker.util.predicates.AbstractionFormula;
import org.sosy_lab.cpachecker.util.predicates.pathformula.PathFormula;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.java_smt.api.BooleanFormula;

/**
 * AbstractState for Symbolic Predicate Abstraction CPA
 */
public abstract class PredicateAbstractState
    implements AbstractState, Partitionable, Serializable, Splitable {

  private static final long serialVersionUID = -265763837277453447L;

  public static boolean containsAbstractionState(AbstractState state) {
    return AbstractStates.extractStateByType(state, PredicateAbstractState.class)
        .isAbstractionState();
  }

  public static PredicateAbstractState getPredicateState(AbstractState pState) {
    return checkNotNull(extractStateByType(pState, PredicateAbstractState.class));
  }

  public static BooleanFormula getBlockFormula(PredicateAbstractState pState) {
    checkArgument(pState.isAbstractionState());
    return pState.getAbstractionFormula().getBlockFormula().getFormula();
  }

  /**
   * Marker type for abstract states that were generated by computing an
   * abstraction.
   */
  private static class AbstractionState extends PredicateAbstractState
      implements Graphable, FormulaReportingState {

    private static final long serialVersionUID = 8341054099315063986L;

    private transient PredicateAbstractState mergedInto = null;

    private AbstractionState(PathFormula pf,
        AbstractionFormula pA, PersistentMap<CFANode, Integer> pAbstractionLocations) {
      super(pf, pA, pAbstractionLocations);
      // Check whether the pathFormula of an abstraction element is just "true".
      // partialOrder relies on this for optimization.
      //Preconditions.checkArgument(bfmgr.isTrue(pf.getFormula()));
      // Check uncommented because we may pre-initialize the path formula
      // with an invariant.
      // This is no problem for the partial order because the invariant
      // is always the same when the location is the same.
    }

    private AbstractionState(
        PathFormula pf,
        AbstractionFormula pA,
        PersistentMap<CFANode, Integer> pAbstractionLocations,
        PredicateAbstractState pPreviousAbstractState) {
      super(pf, pA, pAbstractionLocations, pPreviousAbstractState);
    }

    @Override
    public Object getPartitionKey() {
      if (super.abstractionFormula.isFalse()) {
        // put unreachable states in a separate partition to avoid merging
        // them with any reachable states
        return Boolean.FALSE;
      } else {
        return null;
      }
    }

    @Override
    public boolean isAbstractionState() {
      return true;
    }

    @Override
    public String toString() {
      return "Abstraction location: true, Abstraction: " + super.abstractionFormula;
    }

    @Override
    public String toDOTLabel() {
      return super.abstractionFormula.toString();
    }

    @Override
    public boolean shouldBeHighlighted() {
      return true;
    }

    @Override
    public BooleanFormula getFormulaApproximation(FormulaManagerView pManager) {
      return super.abstractionFormula.asFormulaFromOtherSolver(pManager);
    }

    @Override
    PredicateAbstractState getMergedInto() {
      return mergedInto;
    }

    @Override
    void setMergedInto(PredicateAbstractState pMergedInto) {
      Preconditions.checkNotNull(pMergedInto);
      mergedInto = pMergedInto;
    }
  }

  private static class NonAbstractionState extends PredicateAbstractState {
    private static final long serialVersionUID = -6912172362012773999L;
    /**
     * The abstract state this element was merged into.
     * Used for fast coverage checks.
     */
    private transient PredicateAbstractState mergedInto = null;

    private NonAbstractionState(PathFormula pF, AbstractionFormula pA,
        PersistentMap<CFANode, Integer> pAbstractionLocations) {
      super(pF, pA, pAbstractionLocations);
    }

    private NonAbstractionState(
        PathFormula pF,
        AbstractionFormula pA,
        PersistentMap<CFANode, Integer> pAbstractionLocations,
        PredicateAbstractState pPreviousAbstractState) {
      super(pF, pA, pAbstractionLocations, pPreviousAbstractState);
    }

    @Override
    public boolean isAbstractionState() {
      return false;
    }

    @Override
    PredicateAbstractState getMergedInto() {
      return mergedInto;
    }

    @Override
    void setMergedInto(PredicateAbstractState pMergedInto) {
      Preconditions.checkNotNull(pMergedInto);
      mergedInto = pMergedInto;
    }

    @Override
    public Object getPartitionKey() {
      return getAbstractionFormula();
    }

    @Override
    public String toString() {
      return "Abstraction location: false";
    }
  }

  public static class InfeasibleDummyState extends NonAbstractionState
      implements NonMergeableAbstractState, Graphable {
    private static final long serialVersionUID = 4845812617465441779L;

    private InfeasibleDummyState(
        PathFormula pF,
        AbstractionFormula pA,
        PersistentMap<CFANode, Integer> pAbstractionLocations) {
      super(pF, pA, pAbstractionLocations);
    }

    @Override
    public String toString() {
      return "Dummy location";
    }

    @Override
    public String toDOTLabel() {
      return toString();
    }

    @Override
    public boolean shouldBeHighlighted() {
      return true;
    }
  }

  public static PredicateAbstractState mkAbstractionState(
      PathFormula pF, AbstractionFormula pA,
      PersistentMap<CFANode, Integer> pAbstractionLocations) {
    return new AbstractionState(pF, pA, pAbstractionLocations);
  }

  public static PredicateAbstractState mkAbstractionState(
      PathFormula pF,
      AbstractionFormula pA,
      PersistentMap<CFANode, Integer> pAbstractionLocations,
      PredicateAbstractState pPreviousAbstractionState) {
    return new AbstractionState(pF, pA, pAbstractionLocations, pPreviousAbstractionState);
  }

  public static PredicateAbstractState mkNonAbstractionStateWithNewPathFormula(PathFormula pF,
      PredicateAbstractState oldState) {
    return new NonAbstractionState(pF, oldState.getAbstractionFormula(),
                                        oldState.getAbstractionLocationsOnPath());
  }

  public static PredicateAbstractState mkNonAbstractionStateWithNewPathFormula(
      PathFormula pF,
      PredicateAbstractState oldState,
      PredicateAbstractState pPreviousAbstractionState) {
    return new NonAbstractionState(
        pF,
        oldState.getAbstractionFormula(),
        oldState.getAbstractionLocationsOnPath(),
        pPreviousAbstractionState);
  }

  static PredicateAbstractState mkNonAbstractionState(
      PathFormula pF,
      AbstractionFormula pA,
      PersistentMap<CFANode, Integer> pAbstractionLocations) {
    return new NonAbstractionState(pF, pA, pAbstractionLocations);
  }

  static PredicateAbstractState mkInfeasibleDummyState(
      PathFormula pF,
      AbstractionFormula pA,
      PersistentMap<CFANode, Integer> pAbstractionLocations) {
    return new InfeasibleDummyState(pF, pA, pAbstractionLocations);
  }

  /** The path formula for the path from the last abstraction node to this node.
   * it is set to true on a new abstraction location and updated with a new
   * non-abstraction location */
  private PathFormula pathFormula;

  /** The abstraction which is updated only on abstraction locations */
  private AbstractionFormula abstractionFormula;

  /** How often each abstraction location was visited on the path to the current state. */
  private final transient PersistentMap<CFANode, Integer> abstractionLocations;

  private final AbstractionState previousAbstractionState;

  private PredicateAbstractState(PathFormula pf, AbstractionFormula a,
      PersistentMap<CFANode, Integer> pAbstractionLocations) {
    this.pathFormula = pf;
    this.abstractionFormula = a;
    this.abstractionLocations = pAbstractionLocations;
    this.previousAbstractionState = null;
  }

  private PredicateAbstractState(
      PathFormula pf,
      AbstractionFormula a,
      PersistentMap<CFANode, Integer> pAbstractionLocations,
      PredicateAbstractState pPreviousAbstractionState) {
    this.pathFormula = pf;
    this.abstractionFormula = a;
    this.abstractionLocations = pAbstractionLocations;
    this.previousAbstractionState = (AbstractionState) pPreviousAbstractionState;
  }

  public abstract boolean isAbstractionState();

  PredicateAbstractState getMergedInto() {
    throw new UnsupportedOperationException("Assuming wrong PredicateAbstractStates were merged!");
  }

  /**
   * Mark this state as merged with another state.
   *
   * @param pMergedInto the state that should be set as merged
   */
  void setMergedInto(PredicateAbstractState pMergedInto) {
    throw new UnsupportedOperationException("Merging wrong PredicateAbstractStates!");
  }

  public PersistentMap<CFANode, Integer> getAbstractionLocationsOnPath() {
    return abstractionLocations;
  }

  public AbstractionFormula getAbstractionFormula() {
    return abstractionFormula;
  }

  public PredicateAbstractState getPreviousAbstractionState() {
    return previousAbstractionState;
  }

  /**
   * Replace the abstraction formula part of this element.
   * THIS IS POTENTIALLY UNSOUND!
   *
   * Call this function only during refinement if you also change all successor
   * elements and consider the coverage relation.
   */
  void setAbstraction(AbstractionFormula pAbstractionFormula) {
    if (isAbstractionState()) {
      abstractionFormula = checkNotNull(pAbstractionFormula);
    } else {
      throw new UnsupportedOperationException("Changing abstraction formula is only supported for abstraction elements");
    }
  }

  public PathFormula getPathFormula() {
    return pathFormula;
  }

  protected Object readResolve() {
    if (this instanceof AbstractionState) {
      // consistency check
      /*Pair<String,Integer> splitName;
      FormulaManagerView mgr = GlobalInfo.getInstance().getFormulaManager();
      SSAMap ssa = pathFormula.getSsa();

      for (String var : mgr.extractFreeVariableMap(abstractionFormula.asInstantiatedFormula()).keySet()) {
        splitName = FormulaManagerView.parseName(var);

        if (splitName.getSecond() == null) {
          if (ssa.containsVariable(splitName.getFirst())) {
            throw new StreamCorruptedException("Proof is corrupted, abort reading");
          }
          continue;
        }

        if(splitName.getSecond()!=ssa.getIndex(splitName.getFirst())) {
          throw new StreamCorruptedException("Proof is corrupted, abort reading");
        }
      }*/

      return new AbstractionState(
          pathFormula, abstractionFormula, PathCopyingPersistentTreeMap.of());
    }
    return new NonAbstractionState(
        pathFormula, abstractionFormula, PathCopyingPersistentTreeMap.of());
  }

  @Override
  public AbstractState forkWithReplacements(Collection<AbstractState> pReplacementStates) {
    for (AbstractState state : pReplacementStates) {
      if (state instanceof PredicateAbstractState) {
        return state;
      }
    }
    return this;
  }
}