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
package org.sosy_lab.cpachecker.cpa.fsmbdd;

import java.util.HashMap;
import java.util.Map;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDDomain;
import net.sf.javabdd.BDDFactory;

import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.cpa.fsmbdd.exceptions.VariableDeclarationException;
import org.sosy_lab.cpachecker.cpa.fsmbdd.interfaces.DomainIntervalProvider;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;

/**
 * Abstract state that gets represented by a binary decision diagram.
 */
public class FsmState implements AbstractState {

  private static Map<String, BDDDomain> declaredVariables = new HashMap<String, BDDDomain>();

  /**
   * Reference to the instance of the BDD library.
   */
  private final BDDFactory bddFactory;

  /**
   * The BDD that represents the state.
   */
  private BDD stateBdd;


  /**
   * Constructor.
   */
  public FsmState(BDDFactory pBddFactory) {
    this.bddFactory = pBddFactory;

    // Initially, the state is True.
    this.stateBdd = bddFactory.one();
  }

  /**
   * Setter for the BDD (that represents the state).
   * @param bdd
   */
  public void setStateBdd(BDD bdd) {
    this.stateBdd = bdd;
  }

  /**
   * Return the BDD that represents the state.
   */
  public BDD getStateBdd() {
    return stateBdd;
  }

  /**
   *  Declare the given variable.
   *  The domain of the variable gets initialized.
   */
  public BDDDomain declareGlobal(String pVariableName, int pDomainSize) throws VariableDeclarationException {
    BDDDomain varDomain = declaredVariables.get(pVariableName);
    if (varDomain != null) {
      stateBdd = stateBdd.exist(varDomain.set());
    } else {
      varDomain = bddFactory.extDomain(pDomainSize);
      varDomain.setName(pVariableName);
      declaredVariables.put(pVariableName, varDomain);
    }

    return varDomain;
  }

  /**
   * Undefine (but not undeclare) a given variable.
   * The variable gets existential quantified in the BDD of the state.
   */
  public void undefineVariable(String pScopedVariableName) {
    BDDDomain varDomain = declaredVariables.get(pScopedVariableName);
    stateBdd = stateBdd.exist(varDomain.set());
  }

  /**
   * Return the domain of a given variable.
   * A domain represents the set of bits that are used to encode
   * a possible value of one variable.
   */
  public BDDDomain getGlobalVariableDomain(String pVariableName) throws VariableDeclarationException {
    BDDDomain varDomain = declaredVariables.get(pVariableName);
    if (varDomain == null) {
      throw new VariableDeclarationException("Variable " + pVariableName + " not declared.");
    } else {
      return varDomain;
    }
  }

  /**
   * Modify the state by conjuncting the
   * state-bdd with the given BDD.
   */
  public void addConjunctionWith(BDD bdd) {
    stateBdd = stateBdd.and(bdd);
  }

  /**
   * Modify the state: Assign a new value to the given variable.
   * After an existential quantification of the old value, we conjunct
   * the BDD of the state with the new value.
   *
   * "domainIntervalProvider" is given as argument to keep the state object small.
   *
   */
  public void doVariableAssignment(String pVariableName, DomainIntervalProvider domainIntervalProvider, CExpression pValue) throws CPATransferException {
    BDDDomain variableDomain = getGlobalVariableDomain(pVariableName);
    int literalIndex = domainIntervalProvider.mapLiteralToIndex(pValue);

    stateBdd = stateBdd.exist(bddFactory.makeSet(new BDDDomain[]{variableDomain}))
        .and(variableDomain.ithVar(literalIndex));
  }

  /**
   * Create a copy (new instance) of the state.
   */
  public FsmState cloneState() {
    FsmState result = new FsmState(bddFactory);
    result.stateBdd = this.stateBdd;

    return result;
  }

  /**
   * Create a string-representation of the state.
   */
  @Override
  public String toString() {
    int bddNodes = stateBdd.nodeCount();
    if (bddNodes > 200) {
      return String.format("BDD with %d nodes.", bddNodes);
    } else {
      return stateBdd.toStringWithDomains();
    }
  }


}
