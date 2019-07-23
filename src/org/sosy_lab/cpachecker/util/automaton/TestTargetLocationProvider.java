/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
 */
package org.sosy_lab.cpachecker.util.automaton;

import static com.google.common.collect.FluentIterable.from;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.Specification;

public class TestTargetLocationProvider implements TargetLocationProvider {

  private final Set<CFAEdge> testTargets;

  public TestTargetLocationProvider(final Set<CFAEdge> pTestTargets) {
    testTargets = pTestTargets;
  }

  @Override
  public ImmutableSet<CFANode> tryGetAutomatonTargetLocations(
      final CFANode pRootNode, final Specification pSpecification) {
    return from(testTargets)
        .transform(
            new Function<CFAEdge, CFANode>() {

              @Override
              public CFANode apply(CFAEdge edge) {
                return edge.getSuccessor();
              }
            })
        .toSet();
  }
}
