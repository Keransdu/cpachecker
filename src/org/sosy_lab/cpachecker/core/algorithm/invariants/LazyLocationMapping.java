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
package org.sosy_lab.cpachecker.core.algorithm.invariants;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.reachedset.LocationMappedReachedSet;
import org.sosy_lab.cpachecker.core.reachedset.UnmodifiableReachedSet;
import org.sosy_lab.cpachecker.util.AbstractStates;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LazyLocationMapping {
  private final UnmodifiableReachedSet reachedSet;

  private final AtomicReference<Multimap<CFANode, AbstractState>> statesByLocationRef = new AtomicReference<>();

  public LazyLocationMapping(UnmodifiableReachedSet pReachedSet) {
    this.reachedSet = Objects.requireNonNull(pReachedSet);
  }

  public Iterable<AbstractState> get(CFANode pLocation) {
    if (reachedSet instanceof LocationMappedReachedSet) {
      return AbstractStates.filterLocation(reachedSet, pLocation);
    }
    if (statesByLocationRef.get() == null) {
      Multimap<CFANode, AbstractState> statesByLocation = HashMultimap.create();
      for (AbstractState state : reachedSet) {
        for (CFANode location : AbstractStates.extractLocations(state)) {
          statesByLocation.put(location, state);
        }
      }
      this.statesByLocationRef.set(statesByLocation);
      return statesByLocation.get(pLocation);
    }
    return statesByLocationRef.get().get(pLocation);
  }
}