/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.arg.witnessexport;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Queues;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.ast.AExpressionStatement;
import org.sosy_lab.cpachecker.cfa.ast.FileLocation;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.Specification;
import org.sosy_lab.cpachecker.core.counterexample.AssumptionToEdgeAllocator;
import org.sosy_lab.cpachecker.core.counterexample.ConcreteState;
import org.sosy_lab.cpachecker.core.counterexample.CounterexampleInfo;
import org.sosy_lab.cpachecker.core.interfaces.ExpressionTreeReportingState;
import org.sosy_lab.cpachecker.core.interfaces.FormulaReportingState;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.cpa.predicate.PredicateAbstractState;
import org.sosy_lab.cpachecker.cpa.value.ValueAnalysisState;
import org.sosy_lab.cpachecker.cpa.value.refiner.ValueAnalysisConcreteErrorPathAllocator;
import org.sosy_lab.cpachecker.util.AbstractStates;
import org.sosy_lab.cpachecker.util.CFAUtils;
import org.sosy_lab.cpachecker.util.automaton.AutomatonGraphmlCommon.WitnessType;
import org.sosy_lab.cpachecker.util.automaton.VerificationTaskMetaData;
import org.sosy_lab.cpachecker.util.expressions.ExpressionTree;
import org.sosy_lab.cpachecker.util.expressions.ExpressionTreeFactory;
import org.sosy_lab.cpachecker.util.expressions.ExpressionTrees;
import org.sosy_lab.cpachecker.util.expressions.LeafExpression;
import org.sosy_lab.cpachecker.util.expressions.Simplifier;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaToCVisitor;
import org.sosy_lab.cpachecker.util.predicates.smt.Solver;
import org.sosy_lab.java_smt.api.BooleanFormula;

public class WitnessExporter {

  private static final class ProofInvariantProvider implements InvariantProvider {

    private final ExpressionTreeFactory<Object> factory;
    private final CFA cfa;
    private final FormulaManagerView fmgr;
    private final AssumptionToEdgeAllocator assumptionToEdgeAllocator;

    public ProofInvariantProvider(
        CFA pCfa,
        ExpressionTreeFactory<Object> pFactory,
        FormulaManagerView pFmgr,
        AssumptionToEdgeAllocator pAssumptionToEdgeAllocator) {
      cfa = pCfa;
      factory = pFactory;
      fmgr = pFmgr;
      assumptionToEdgeAllocator = pAssumptionToEdgeAllocator;
    }

    @Override
    public ExpressionTree<Object> provideInvariantFor(
        CFAEdge pEdge, Optional<? extends Collection<? extends ARGState>> pStates) {
      // TODO interface for extracting the information from states, similar to
      // FormulaReportingState
      if (!pStates.isPresent()) {
        return ExpressionTrees.getTrue();
      }
      Set<ExpressionTree<Object>> stateInvariants = new LinkedHashSet<>();
      String functionName = pEdge.getSuccessor().getFunctionName();
      for (ARGState state : pStates.get()) {
        Set<ExpressionTree<Object>> approximations = new LinkedHashSet<>();
        approximations.addAll(extractValueAnalysisInvariants(pEdge, state));
        approximations.add(extractPredicateAnalysisAbstractionStateInvariants(functionName, state));
        for (ExpressionTreeReportingState etrs :
            AbstractStates.asIterable(state).filter(ExpressionTreeReportingState.class)) {
          approximations.add(
              etrs.getFormulaApproximation(
                  cfa.getFunctionHead(functionName), pEdge.getSuccessor()));
        }
        stateInvariants.add(factory.and(approximations));
      }
      return factory.or(stateInvariants);
    }

    private ExpressionTree<Object> extractPredicateAnalysisAbstractionStateInvariants(
        String functionName, ARGState state) throws AssertionError {
      final PredicateAbstractState predState =
          AbstractStates.extractStateByType(state, PredicateAbstractState.class);
      if (predState == null || !predState.isAbstractionState()) { // if state not available
        return ExpressionTrees.getTrue();
      }

      BooleanFormula inv = ((FormulaReportingState) predState).getFormulaApproximation(fmgr);
      String invString = null;
      try {
        // filter out variables that are not global and
        // not local in the current function
        String prefix = functionName + FUNCTION_DELIMITER;
        inv =
            fmgr.filterLiterals(
                inv,
                e -> {
                  for (String name : fmgr.extractVariableNames(e)) {
                    if (name.contains(FUNCTION_DELIMITER) && !name.startsWith(prefix)) {
                      return false;
                    }
                  }
                  return true;
                });

        FormulaToCVisitor v = new FormulaToCVisitor(fmgr);
        boolean isValid = fmgr.visit(inv, v);
        if (isValid) {
          invString = v.getString();
        }
      } catch (InterruptedException e) {
        throw new AssertionError("Witnessexport was interrupted for generation of Proofwitness", e);
      }
      if (invString != null) {
        if (invString.equals("0")) {
          return ExpressionTrees.getFalse();
        }
        if (!invString.equals("1")) {
          return LeafExpression.of((Object) invString);
        }
      }
      return ExpressionTrees.getTrue(); // no new invariant
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private ImmutableCollection<ExpressionTree<Object>>
        extractValueAnalysisInvariants(
        CFAEdge pEdge, ARGState state) {
      final ValueAnalysisState valueAnalysisState =
          AbstractStates.extractStateByType(state, ValueAnalysisState.class);
      if (valueAnalysisState == null) { // if state not available
        return ImmutableSet.of();
      }

      ConcreteState concreteState =
          ValueAnalysisConcreteErrorPathAllocator.createConcreteState(valueAnalysisState);
      Iterable<AExpressionStatement> invariants =
          WitnessFactory.ASSUMPTION_FILTER
              .apply(assumptionToEdgeAllocator.allocateAssumptionsToEdge(pEdge, concreteState))
              .getExpStmts();
      ImmutableList.Builder<ExpressionTree<Object>> stateInvariants = ImmutableList.builder();
      for (AExpressionStatement expressionStatement : invariants) {
        stateInvariants.add(LeafExpression.of((Object) expressionStatement.getExpression()));
      }
      return stateInvariants.build();
    }
  }

  private static final String FUNCTION_DELIMITER = "::";

  protected final WitnessOptions options;

  protected final CFA cfa;
  private final FormulaManagerView fmgr;

  private final AssumptionToEdgeAllocator assumptionToEdgeAllocator;

  protected final ExpressionTreeFactory<Object> factory = ExpressionTrees.newFactory();
  protected final Simplifier<Object> simplifier = ExpressionTrees.newSimplifier(factory);

  protected final VerificationTaskMetaData verificationTaskMetaData;

  public WitnessExporter(
      final Configuration pConfig,
      final LogManager pLogger,
      final Specification pSpecification,
      final CFA pCFA)
      throws InvalidConfigurationException {
    Preconditions.checkNotNull(pConfig);
    options = new WitnessOptions();
    pConfig.inject(options);
    this.cfa = pCFA;
    this.fmgr = Solver.create(pConfig, pLogger, ShutdownNotifier.createDummy()).getFormulaManager();
    this.assumptionToEdgeAllocator =
        AssumptionToEdgeAllocator.create(pConfig, pLogger, pCFA.getMachineModel());
    this.verificationTaskMetaData = new VerificationTaskMetaData(pConfig, pSpecification);
  }

  public ProofInvariantProvider getProofInvariantProvider() {
    return new ProofInvariantProvider(cfa, factory, fmgr, assumptionToEdgeAllocator);
  }

  public Witness generateErrorWitness(
      final ARGState pRootState,
      final Predicate<? super ARGState> pIsRelevantState,
      final BiPredicate<ARGState, ARGState> pIsRelevantEdge,
      CounterexampleInfo pCounterExample) {

    String defaultFileName = getInitialFileName(pRootState);
    WitnessFactory writer =
        new WitnessFactory(
            options,
            cfa,
            verificationTaskMetaData,
            factory,
            simplifier,
            defaultFileName,
            WitnessType.VIOLATION_WITNESS,
            InvariantProvider.TrueInvariantProvider.INSTANCE);
    return writer.produceWitness(
        pRootState,
        pIsRelevantState,
        pIsRelevantEdge,
        Predicates.alwaysFalse(),
        Optional.empty(),
        Optional.ofNullable(pCounterExample),
        GraphBuilder.ARG_PATH);
  }

  public Witness generateTerminationErrorWitness(
      final ARGState pRoot,
      final Predicate<? super ARGState> pIsRelevantState,
      final BiPredicate<ARGState, ARGState> pIsRelevantEdge,
      final Predicate<? super ARGState> pIsCycleHead,
      final Function<? super ARGState, ExpressionTree<Object>> toQuasiInvariant) {
    String defaultFileName = getInitialFileName(pRoot);
    WitnessFactory writer =
        new WitnessFactory(
            options,
            cfa,
            verificationTaskMetaData,
            factory,
            simplifier,
            defaultFileName,
            WitnessType.VIOLATION_WITNESS,
            InvariantProvider.TrueInvariantProvider.INSTANCE);
    return writer.produceWitness(
        pRoot,
        pIsRelevantState,
        pIsRelevantEdge,
        pIsCycleHead,
        Optional.of(toQuasiInvariant),
        Optional.empty(),
        GraphBuilder.ARG_PATH);
  }

  public Witness generateProofWitness(
      final ARGState pRootState,
      final Predicate<? super ARGState> pIsRelevantState,
      final BiPredicate<ARGState, ARGState> pIsRelevantEdge,
      InvariantProvider pInvariantProvider) {

    Preconditions.checkNotNull(pRootState);
    Preconditions.checkNotNull(pIsRelevantState);
    Preconditions.checkNotNull(pIsRelevantEdge);
    Preconditions.checkNotNull(pInvariantProvider);

    String defaultFileName = getInitialFileName(pRootState);
    WitnessFactory writer =
        new WitnessFactory(
            options,
            cfa,
            verificationTaskMetaData,
            factory,
            simplifier,
            defaultFileName,
            WitnessType.CORRECTNESS_WITNESS,
            pInvariantProvider);
    return writer.produceWitness(
        pRootState,
        pIsRelevantState,
        pIsRelevantEdge,
        Predicates.alwaysFalse(),
        Optional.empty(),
        Optional.empty(),
        GraphBuilder.CFA_FULL);
  }

  protected String getInitialFileName(ARGState pRootState) {
    Deque<CFANode> worklist = Queues.newArrayDeque(AbstractStates.extractLocations(pRootState));
    Set<CFANode> visited = new HashSet<>();
    while (!worklist.isEmpty()) {
      CFANode l = worklist.pop();
      visited.add(l);
      for (CFAEdge e : CFAUtils.leavingEdges(l)) {
        Set<FileLocation> fileLocations = CFAUtils.getFileLocationsFromCfaEdge(e);
        if (!fileLocations.isEmpty()) {
          String fileName = fileLocations.iterator().next().getFileName();
          if (fileName != null) {
            return fileName;
          }
        }
        if (!visited.contains(e.getSuccessor())) {
          worklist.push(e.getSuccessor());
        }
      }
    }

    throw new RuntimeException("Could not determine file name based on abstract state!");
  }
}
