// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.testtargets;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.CPAcheckerResult.Result;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.reachedset.UnmodifiableReachedSet;
import org.sosy_lab.cpachecker.util.CFAUtils;

public class TestTargetProvider implements Statistics {

  private static TestTargetProvider instance = null;

  private final CFA cfa;
  private final TestTargetType type;
  private final ImmutableSet<CFAEdge> initialTestTargets;
  private final Set<CFAEdge> uncoveredTargets;
  private boolean printTargets = false;
  private boolean runParallel;
  private TestTargetAdaption optimization;

  private TestTargetProvider(
      final CFA pCfa,
      final boolean pRunParallel,
      final TestTargetType pType,
      final TestTargetAdaption pGoalAdaption) {
    cfa = pCfa;
    runParallel = pRunParallel;
    type = pType;
    optimization = pGoalAdaption;

    Set<CFAEdge> targets = extractEdgesByCriterion(type.getEdgeCriterion(), pGoalAdaption);

    if (runParallel) {
      uncoveredTargets = Collections.synchronizedSet(targets);
    } else {
      uncoveredTargets = targets;
    }
    initialTestTargets = ImmutableSet.copyOf(uncoveredTargets);
  }

  private Set<CFAEdge> extractEdgesByCriterion(
      final Predicate<CFAEdge> criterion, final TestTargetAdaption pAdaption) {
    Set<CFAEdge> edges = new HashSet<>();
    for (CFANode node : cfa.getAllNodes()) {
      edges.addAll(CFAUtils.allLeavingEdges(node).filter(criterion).toSet());
    }
    edges = pAdaption.adaptTestTargets(edges);
    return edges;
  }

  public static int getTotalNumberOfTestTargets() {
    if (instance == null) {
      return 0;
    }
    return instance.initialTestTargets.size();
  }

  public static int getNumberOfUncoveredTestTargets() {
    if (instance == null) {
      return 0;
    }
    return instance.uncoveredTargets.size();
  }

  public static Set<CFAEdge> getTestTargets(
      final CFA pCfa,
      final boolean pRunParallel,
      final TestTargetType pType,
      TestTargetAdaption pTargetOptimization) {
    if (instance == null
        || pCfa != instance.cfa
        || instance.type != pType
        || instance.optimization != pTargetOptimization) {
      instance = new TestTargetProvider(pCfa, pRunParallel, pType, pTargetOptimization);
    }
    Preconditions.checkState(instance.runParallel || !pRunParallel);
    return instance.uncoveredTargets;
  }

  public static String getCoverageInfo() {
    Preconditions.checkNotNull(instance);
    return (instance.initialTestTargets.size() - instance.uncoveredTargets.size())
        + " of "
        + instance.initialTestTargets.size()
        + " covered";
  }

  public static Statistics getTestTargetStatisitics(boolean pPrintTestGoalInfo) {
    Preconditions.checkNotNull(instance);
    instance.printTargets = pPrintTestGoalInfo;
    return instance;
  }


  @Override
  public @Nullable String getName() {
    return "Testtargets";
  }

  @Override
  public void printStatistics(PrintStream pOut, Result pResult, UnmodifiableReachedSet pReached) {
    int numCovered = initialTestTargets.size() - uncoveredTargets.size();
    double testTargetCoverage =
        initialTestTargets.isEmpty() ? 0 : (double) numCovered / initialTestTargets.size();
    pOut.printf("Test target coverage: %.2f%%%n", testTargetCoverage * 100);
    pOut.println("Number of total test targets: " + initialTestTargets.size());
    pOut.println("Number of covered test targets: " + numCovered);
    pOut.println("Number of uncovered test targets: " + uncoveredTargets.size());

    if (printTargets) {
    pOut.println("Initial test targets: ");
    for (CFAEdge edge : initialTestTargets) {
      pOut.println(edge.toString());
    }

    pOut.println("Test targets that have not been covered: ");
    for (CFAEdge edge : uncoveredTargets) {
      pOut.println(edge.toString());
    }
    }
  }
}
