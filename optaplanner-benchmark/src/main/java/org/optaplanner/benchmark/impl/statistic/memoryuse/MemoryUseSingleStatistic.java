/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic.memoryuse;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationStatisticPoint;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class MemoryUseSingleStatistic extends SingleStatistic<MemoryUseStatisticPoint> {

    private long timeMillisThresholdInterval;

    private MemoryUseSingleStatisticListener listener;

    private List<MemoryUseStatisticPoint> pointList;

    public MemoryUseSingleStatistic(SingleBenchmarkResult singleBenchmarkResult) {
        this(singleBenchmarkResult, 1000L);
    }

    public MemoryUseSingleStatistic(SingleBenchmarkResult singleBenchmarkResult, long timeMillisThresholdInterval) {
        super(singleBenchmarkResult, ProblemStatisticType.MEMORY_USE);
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
        listener = new MemoryUseSingleStatisticListener();
        pointList = new ArrayList<MemoryUseStatisticPoint>();
    }

    public List<MemoryUseStatisticPoint> getPointList() {
        return pointList;
    }

    @Override
    public void setPointList(List<MemoryUseStatisticPoint> pointList) {
        this.pointList = pointList;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void open(Solver solver) {
        ((DefaultSolver) solver).addSolverPhaseLifecycleListener(listener);
    }

    public void close(Solver solver) {
        ((DefaultSolver) solver).removeSolverPhaseLifecycleListener(listener);
    }
    
    private class MemoryUseSingleStatisticListener extends SolverPhaseLifecycleListenerAdapter {

        private long nextTimeMillisThreshold = timeMillisThresholdInterval;

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
            if (timeMillisSpend >= nextTimeMillisThreshold) {
                pointList.add(new MemoryUseStatisticPoint(timeMillisSpend, MemoryUseMeasurement.create()));

                nextTimeMillisThreshold += timeMillisThresholdInterval;
                if (nextTimeMillisThreshold < timeMillisSpend) {
                    nextTimeMillisThreshold = timeMillisSpend;
                }
            }
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return MemoryUseStatisticPoint.buildCsvLine("timeMillisSpend", "usedMemory", "maxMemory");
    }

    @Override
    protected MemoryUseStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new MemoryUseStatisticPoint(Long.valueOf(csvLine.get(0)),
                new MemoryUseMeasurement(Long.valueOf(csvLine.get(1)), Long.valueOf(csvLine.get(2))));
    }

}
