/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.optaplanner.core.impl.domain.entity.descriptor.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.util.RandomUtils;

public class ProbabilityEntitySelector extends AbstractEntitySelector implements SelectionCacheLifecycleListener {

    protected final EntitySelector childEntitySelector;
    protected final SelectionCacheType cacheType;
    protected final SelectionProbabilityWeightFactory probabilityWeightFactory;

    protected NavigableMap<Double, Object> cachedEntityMap = null;
    protected double probabilityWeightTotal = -1.0;

    public ProbabilityEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType,
            SelectionProbabilityWeightFactory probabilityWeightFactory) {
        this.childEntitySelector = childEntitySelector;
        this.cacheType = cacheType;
        this.probabilityWeightFactory = probabilityWeightFactory;
        if (childEntitySelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childEntitySelector (" + childEntitySelector
                    + ") with neverEnding (" + childEntitySelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childEntitySelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedEntityMap = new TreeMap<Double, Object>();
        ScoreDirector scoreDirector = solverScope.getScoreDirector();
        double probabilityWeightOffset = 0L;
        for (Object entity : childEntitySelector) {
            double probabilityWeight = probabilityWeightFactory.createProbabilityWeight(
                    scoreDirector, entity);
            cachedEntityMap.put(probabilityWeightOffset, entity);
            probabilityWeightOffset += probabilityWeight;
        }
        probabilityWeightTotal = probabilityWeightOffset;
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        probabilityWeightTotal = -1.0;
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isCountable() {
        return true;
    }

    public boolean isNeverEnding() {
        return true;
    }

    public long getSize() {
        return cachedEntityMap.size();
    }

    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            public boolean hasNext() {
                return true;
            }

            public Object next() {
                double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
                Map.Entry<Double, Object> entry = cachedEntityMap.floorEntry(randomOffset);
                // entry is never null because randomOffset < probabilityWeightTotal
                return entry.getValue();
            }

            public void remove() {
                throw new UnsupportedOperationException("The optional operation remove() is not supported.");
            }
        };
    }

    public ListIterator<Object> listIterator() {
        throw new IllegalStateException("The selector (" + this
                + ") does not support a ListIterator with randomSelection (true).");
    }

    public ListIterator<Object> listIterator(int index) {
        throw new IllegalStateException("The selector (" + this
                + ") does not support a ListIterator with randomSelection (true).");
    }

    public Iterator<Object> endingIterator() {
        return childEntitySelector.endingIterator();
    }

    @Override
    public String toString() {
        return "Probability(" + childEntitySelector + ")";
    }

}
