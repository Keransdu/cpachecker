/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2012  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.cpa.predicate;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.sosy_lab.cpachecker.cfa.objectmodel.CFANode;
import org.sosy_lab.cpachecker.core.interfaces.AbstractElement;
import org.sosy_lab.cpachecker.core.interfaces.FormulaReportingElement;
import org.sosy_lab.cpachecker.core.interfaces.Partitionable;
import org.sosy_lab.cpachecker.util.predicates.AbstractionFormula;
import org.sosy_lab.cpachecker.util.predicates.PathFormula;
import org.sosy_lab.cpachecker.util.predicates.interfaces.Formula;
import org.sosy_lab.cpachecker.util.predicates.interfaces.FormulaManager;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * AbstractElement for Symbolic Predicate Abstraction CPA
 */
public abstract class PredicateAbstractElement implements AbstractElement, Partitionable, FormulaReportingElement, Serializable {

  private static final long serialVersionUID = -265763837277453447L;

  public final static Predicate<PredicateAbstractElement> FILTER_ABSTRACTION_ELEMENTS = new Predicate<PredicateAbstractElement>() {
    @Override
    public boolean apply(PredicateAbstractElement ae) {
      return ae.isAbstractionElement();
    }
  };

  /**
   * Marker type for abstract elements that were generated by computing an
   * abstraction.
   */
  private static class AbstractionElement extends PredicateAbstractElement {

    private static final long serialVersionUID = 8341054099315063986L;

    private AbstractionElement(PathFormula pf, AbstractionFormula pA) {
      super(pf, pA);
      // Check whether the pathFormula of an abstraction element is just "true".
      // partialOrder relies on this for optimization.
      Preconditions.checkArgument(pf.getFormula().isTrue());
    }

    @Override
    public Object getPartitionKey() {
      if (super.abstractionFormula.asFormula().isFalse()) {
        // put unreachable states in a separate partition to avoid merging
        // them with any reachable states
        return Boolean.FALSE;
      } else {
        return null;
      }
    }

    @Override
    public boolean isAbstractionElement() {
      return true;
    }

    @Override
    public String toString() {
      return "Abstraction location: true, Abstraction: " + super.abstractionFormula;
    }
  }

  private static class NonAbstractionElement extends PredicateAbstractElement {

    private static final long serialVersionUID = -6912172362012773999L;
    /**
     * The abstract element this element was merged into.
     * Used for fast coverage checks.
     */
    private transient PredicateAbstractElement mergedInto = null;

    private NonAbstractionElement(PathFormula pF, AbstractionFormula pA) {
      super(pF, pA);
    }

    @Override
    public boolean isAbstractionElement() {
      return false;
    }

    @Override
    PredicateAbstractElement getMergedInto() {
      return mergedInto;
    }

    @Override
    void setMergedInto(PredicateAbstractElement pMergedInto) {
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

  static class ComputeAbstractionElement extends PredicateAbstractElement {

    private static final long serialVersionUID = -3961784113582993743L;
    private transient final CFANode location;

    public ComputeAbstractionElement(PathFormula pf, AbstractionFormula pA, CFANode pLoc) {
      super(pf, pA);
      location = pLoc;
    }

    @Override
    public boolean isAbstractionElement() {
      return false;
    }

    @Override
    public Object getPartitionKey() {
      return this;
    }

    @Override
    public String toString() {
      return "Abstraction location: true, Abstraction: <TO COMPUTE>";
    }

    public CFANode getLocation() {
      return location;
    }
  }

  static PredicateAbstractElement abstractionElement(PathFormula pF, AbstractionFormula pA) {
    return new AbstractionElement(pF, pA);
  }

  static PredicateAbstractElement nonAbstractionElement(PathFormula pF, AbstractionFormula pA) {
    return new NonAbstractionElement(pF, pA);
  }

  /** The path formula for the path from the last abstraction node to this node.
   * it is set to true on a new abstraction location and updated with a new
   * non-abstraction location */
  private final PathFormula pathFormula;

  /** The abstraction which is updated only on abstraction locations */
  private AbstractionFormula abstractionFormula;

  private PredicateAbstractElement(PathFormula pf, AbstractionFormula a) {
    this.pathFormula = pf;
    this.abstractionFormula = a;
  }

  public abstract boolean isAbstractionElement();

  PredicateAbstractElement getMergedInto() {
    throw new UnsupportedOperationException("Assuming wrong PredicateAbstractElements were merged!");
  }

  void setMergedInto(PredicateAbstractElement pMergedInto) {
    throw new UnsupportedOperationException("Merging wrong PredicateAbstractElements!");
  }

  public AbstractionFormula getAbstractionFormula() {
    return abstractionFormula;
  }

  /**
   * Replace the abstraction formula part of this element.
   * THIS IS POTENTIALLY UNSOUND!
   *
   * Call this function only during refinement if you also change all successor
   * elements and consider the coverage relation.
   */
  void setAbstraction(AbstractionFormula pAbstractionFormula) {
    if (isAbstractionElement()) {
      abstractionFormula = checkNotNull(pAbstractionFormula);
    } else {
      throw new UnsupportedOperationException("Changing abstraction formula is only supported for abstraction elements");
    }
  }

  public PathFormula getPathFormula() {
    return pathFormula;
  }

  @Override
  public Formula getFormulaApproximation(FormulaManager manager) {
    return getAbstractionFormula().asFormula();
  }
}