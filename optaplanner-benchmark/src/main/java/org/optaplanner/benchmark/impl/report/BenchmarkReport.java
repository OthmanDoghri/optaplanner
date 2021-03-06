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

package org.optaplanner.benchmark.impl.report;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.drools.core.util.StringUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.optaplanner.benchmark.api.ranking.SolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpendNumberFormat;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.ScoreUtils;

public class BenchmarkReport {

    public static final int CHARTED_SCORE_LEVEL_SIZE = 5;

    private final PlannerBenchmarkResult plannerBenchmarkResult;

    private Locale locale = null;
    private Comparator<SolverBenchmarkResult> solverRankingComparator = null;
    private SolverRankingWeightFactory solverRankingWeightFactory = null;
    private File summaryDirectory = null;
    private List<File> bestScoreSummaryChartFileList = null;
    private List<File> bestScoreScalabilitySummaryChartFileList = null;
    private List<File> winningScoreDifferenceSummaryChartFileList = null;
    private List<File> worstScoreDifferencePercentageSummaryChartFileList = null;
    private File averageCalculateCountSummaryChartFile = null;
    private File timeSpendSummaryChartFile = null;
    private File timeSpendScalabilitySummaryChartFile = null;
    private List<File> bestScorePerTimeSpendSummaryChartFileList = null;

    private Integer defaultShownScoreLevelIndex = null;
    private List<String> warningList = null;

    private File htmlOverviewFile = null;

    public BenchmarkReport(PlannerBenchmarkResult plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
    }

    public PlannerBenchmarkResult getPlannerBenchmarkResult() {
        return plannerBenchmarkResult;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Comparator<SolverBenchmarkResult> getSolverRankingComparator() {
        return solverRankingComparator;
    }

    public void setSolverRankingComparator(Comparator<SolverBenchmarkResult> solverRankingComparator) {
        this.solverRankingComparator = solverRankingComparator;
    }

    public SolverRankingWeightFactory getSolverRankingWeightFactory() {
        return solverRankingWeightFactory;
    }

    public void setSolverRankingWeightFactory(SolverRankingWeightFactory solverRankingWeightFactory) {
        this.solverRankingWeightFactory = solverRankingWeightFactory;
    }

    public File getSummaryDirectory() {
        return summaryDirectory;
    }

    public List<File> getBestScoreSummaryChartFileList() {
        return bestScoreSummaryChartFileList;
    }

    public List<File> getBestScoreScalabilitySummaryChartFileList() {
        return bestScoreScalabilitySummaryChartFileList;
    }

    public List<File> getWinningScoreDifferenceSummaryChartFileList() {
        return winningScoreDifferenceSummaryChartFileList;
    }

    public List<File> getWorstScoreDifferencePercentageSummaryChartFileList() {
        return worstScoreDifferencePercentageSummaryChartFileList;
    }

    public File getAverageCalculateCountSummaryChartFile() {
        return averageCalculateCountSummaryChartFile;
    }

    public File getTimeSpendSummaryChartFile() {
        return timeSpendSummaryChartFile;
    }

    public File getTimeSpendScalabilitySummaryChartFile() {
        return timeSpendScalabilitySummaryChartFile;
    }

    public List<File> getBestScorePerTimeSpendSummaryChartFileList() {
        return bestScorePerTimeSpendSummaryChartFileList;
    }

    public Integer getDefaultShownScoreLevelIndex() {
        return defaultShownScoreLevelIndex;
    }

    public List<String> getWarningList() {
        return warningList;
    }

    public File getHtmlOverviewFile() {
        return htmlOverviewFile;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public String getJavaVersion() {
        return "Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";
    }

    public String getJavaVM() {
        return "Java " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version")
                + " (" + System.getProperty("java.vm.vendor") + ")";
    }

    public String getOperatingSystem() {
        return System.getProperty("os.name") + " " + System.getProperty("os.arch")
                + " " + System.getProperty("os.version");
    }

    /**
     * @return sometimes null (only during development)
     */
    public String getPlannerVersion() {
        return SolverFactory.class.getPackage().getImplementationVersion();
    }

    public String getRelativePathToBenchmarkReportDirectory(File file) {
        String benchmarkReportDirectoryPath = plannerBenchmarkResult.getBenchmarkReportDirectory().getAbsolutePath();
        String filePath = file.getAbsolutePath();
        if (!filePath.startsWith(benchmarkReportDirectoryPath)) {
            throw new IllegalArgumentException("The filePath (" + filePath
                    + ") does not start with the benchmarkReportDirectoryPath (" + benchmarkReportDirectoryPath + ").");
        }
        String relativePath = filePath.substring(benchmarkReportDirectoryPath.length());
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public void writeReport() {
        summaryDirectory = new File(plannerBenchmarkResult.getBenchmarkReportDirectory(), "summary");
        summaryDirectory.mkdir();
        plannerBenchmarkResult.accumulateResults(this);
        fillWarningList();
        writeBestScoreSummaryCharts();
        writeBestScoreScalabilitySummaryChart();
        writeWinningScoreDifferenceSummaryChart();
        writeWorstScoreDifferencePercentageSummaryChart();
        writeAverageCalculateCountPerSecondSummaryChart();
        writeTimeSpendSummaryChart();
        writeTimeSpendScalabilitySummaryChart();
        writeBestScorePerTimeSpendSummaryChart();
        for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            if (problemBenchmarkResult.hasAnySuccess()) {
                for (ProblemStatistic problemStatistic : problemBenchmarkResult.getProblemStatisticList()) {
                    problemStatistic.writeGraphFiles(this);
                }
            }
        }
        determineDefaultShownScoreLevelIndex();
        writeHtmlOverviewFile();
    }

    protected void fillWarningList() {
        warningList = new ArrayList<String>();
        String javaVmName = System.getProperty("java.vm.name");
        if (javaVmName != null && javaVmName.contains("Client VM")) {
            warningList.add("The Java VM (" + javaVmName + ") is the Client VM."
                    + " Consider starting the java process with the argument \"-server\" to get better results.");
        }
        int availableProcessors = getAvailableProcessors();
        if (plannerBenchmarkResult.getParallelBenchmarkCount() > availableProcessors) {
            warningList.add("The parallelBenchmarkCount (" + plannerBenchmarkResult.getParallelBenchmarkCount()
                    + ") is higher than the number of availableProcessors (" + availableProcessors + ").");
        }
    }

    private void writeBestScoreSummaryCharts() {
        // Each scoreLevel has it's own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<DefaultCategoryDataset>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String planningProblemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.isSuccess()) {
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getScore());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        datasetList.get(i).addValue(levelValues[i], solverLabel, planningProblemLabel);
                    }
                }
            }
        }
        bestScoreSummaryChartFileList = new ArrayList<File>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Score level " + scoreLevelIndex, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart("Best score level " + scoreLevelIndex + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScoreSummaryChartFileList.add(writeChartToImageFile(chart, "bestScoreSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeBestScoreScalabilitySummaryChart() {
        // Each scoreLevel has it's own dataset and chartFile
        List<List<XYSeries>> seriesListList = new ArrayList<List<XYSeries>>(
                CHARTED_SCORE_LEVEL_SIZE);
        int solverBenchmarkIndex = 0;
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.isSuccess()) {
                    long problemScale = singleBenchmarkResult.getProblemBenchmarkResult().getProblemScale();
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getScore());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesListList.size()) {
                            seriesListList.add(new ArrayList<XYSeries>(
                                    plannerBenchmarkResult.getSolverBenchmarkResultList().size()));
                        }
                        List<XYSeries> seriesList = seriesListList.get(i);
                        while (solverBenchmarkIndex >= seriesList.size()) {
                            seriesList.add(new XYSeries(solverLabel));
                        }
                        seriesList.get(solverBenchmarkIndex).add((double) problemScale, levelValues[i]);
                    }
                }
            }
            solverBenchmarkIndex++;
        }
        bestScoreScalabilitySummaryChartFileList = new ArrayList<File>(seriesListList.size());
        int scoreLevelIndex = 0;
        for (List<XYSeries> seriesList : seriesListList) {
            XYPlot plot = createScalabilityPlot(seriesList,
                    "Problem scale", NumberFormat.getInstance(locale),
                    "Score level " + scoreLevelIndex, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart(
                    "Best score scalability level " + scoreLevelIndex + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScoreScalabilitySummaryChartFileList.add(
                    writeChartToImageFile(chart, "bestScoreScalabilitySummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeWinningScoreDifferenceSummaryChart() {
        // Each scoreLevel has it's own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<DefaultCategoryDataset>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String planningProblemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.isSuccess()) {
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getWinningScoreDifference());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        datasetList.get(i).addValue(levelValues[i], solverLabel, planningProblemLabel);
                    }
                }
            }
        }
        winningScoreDifferenceSummaryChartFileList = new ArrayList<File>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Winning score difference level " + scoreLevelIndex, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart("Winning score difference level " + scoreLevelIndex
                    + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            winningScoreDifferenceSummaryChartFileList.add(
                    writeChartToImageFile(chart, "winningScoreDifferenceSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeWorstScoreDifferencePercentageSummaryChart() {
        // Each scoreLevel has it's own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<DefaultCategoryDataset>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String planningProblemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.isSuccess()) {
                    double[] levelValues = singleBenchmarkResult.getWorstScoreDifferencePercentage().getPercentageLevels();
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        datasetList.get(i).addValue(levelValues[i], solverLabel, planningProblemLabel);
                    }
                }
            }
        }
        worstScoreDifferencePercentageSummaryChartFileList = new ArrayList<File>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Worst score difference percentage level " + scoreLevelIndex,
                    NumberFormat.getPercentInstance(locale));
            JFreeChart chart = new JFreeChart("Worst score difference percentage level " + scoreLevelIndex
                    + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            worstScoreDifferencePercentageSummaryChartFileList.add(
                    writeChartToImageFile(chart, "worstScoreDifferencePercentageSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeAverageCalculateCountPerSecondSummaryChart() {
        List<XYSeries> seriesList = new ArrayList<XYSeries>(plannerBenchmarkResult.getSolverBenchmarkResultList().size());
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.isSuccess()) {
                    long problemScale = singleBenchmarkResult.getProblemBenchmarkResult().getProblemScale();
                    long averageCalculateCountPerSecond = singleBenchmarkResult.getAverageCalculateCountPerSecond();
                    series.add((Long) problemScale, (Long) averageCalculateCountPerSecond);
                }
            }
            seriesList.add(series);
        }
        XYPlot plot = createScalabilityPlot(seriesList,
                "Problem scale", NumberFormat.getInstance(locale),
                "Average calculate count per second", NumberFormat.getInstance(locale));
        JFreeChart chart = new JFreeChart("Average calculate count summary (higher is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        averageCalculateCountSummaryChartFile = writeChartToImageFile(chart, "averageCalculateCountSummary");
    }

    private void writeTimeSpendSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String planningProblemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.isSuccess()) {
                    long timeMillisSpend = singleBenchmarkResult.getTimeMillisSpend();
                    dataset.addValue(timeMillisSpend, solverLabel, planningProblemLabel);
                }
            }
        }
        CategoryPlot plot = createBarChartPlot(dataset, "Time spend", new MillisecondsSpendNumberFormat(locale));
        JFreeChart chart = new JFreeChart("Time spend summary (lower time is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        timeSpendSummaryChartFile = writeChartToImageFile(chart, "timeSpendSummary");
    }

    private void writeTimeSpendScalabilitySummaryChart() {
        List<XYSeries> seriesList = new ArrayList<XYSeries>(plannerBenchmarkResult.getSolverBenchmarkResultList().size());
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.isSuccess()) {
                    long problemScale = singleBenchmarkResult.getProblemBenchmarkResult().getProblemScale();
                    long timeMillisSpend = singleBenchmarkResult.getTimeMillisSpend();
                    series.add((Long) problemScale, (Long) timeMillisSpend);
                }
            }
            seriesList.add(series);
        }
        XYPlot plot = createScalabilityPlot(seriesList,
                "Problem scale", NumberFormat.getInstance(locale),
                "Time spend", new MillisecondsSpendNumberFormat(locale));
        JFreeChart chart = new JFreeChart("Time spend scalability summary (lower is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        timeSpendScalabilitySummaryChartFile = writeChartToImageFile(chart, "timeSpendScalabilitySummary");
    }

    private void writeBestScorePerTimeSpendSummaryChart() {
        // Each scoreLevel has it's own dataset and chartFile
        List<List<XYSeries>> seriesListList = new ArrayList<List<XYSeries>>(
                CHARTED_SCORE_LEVEL_SIZE);
        int solverBenchmarkIndex = 0;
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.isSuccess()) {
                    long timeMillisSpend = singleBenchmarkResult.getTimeMillisSpend();
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getScore());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesListList.size()) {
                            seriesListList.add(new ArrayList<XYSeries>(
                                    plannerBenchmarkResult.getSolverBenchmarkResultList().size()));
                        }
                        List<XYSeries> seriesList = seriesListList.get(i);
                        while (solverBenchmarkIndex >= seriesList.size()) {
                            seriesList.add(new XYSeries(solverLabel));
                        }
                        seriesList.get(solverBenchmarkIndex).add((Long) timeMillisSpend, (Double) levelValues[i]);
                    }
                }
            }
            solverBenchmarkIndex++;
        }
        bestScorePerTimeSpendSummaryChartFileList = new ArrayList<File>(seriesListList.size());
        int scoreLevelIndex = 0;
        for (List<XYSeries> seriesList : seriesListList) {
            XYPlot plot = createScalabilityPlot(seriesList,
                    "Time spend", new MillisecondsSpendNumberFormat(locale),
                    "Score level " + scoreLevelIndex, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart(
                    "Best score per time spend level " + scoreLevelIndex + " summary (higher left is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScorePerTimeSpendSummaryChartFileList.add(
                    writeChartToImageFile(chart, "bestScorePerTimeSpendSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    // ************************************************************************
    // Chart helper methods
    // ************************************************************************

    private CategoryPlot createBarChartPlot(DefaultCategoryDataset dataset,
            String yAxisLabel, NumberFormat yAxisNumberFormat) {
        CategoryAxis xAxis = new CategoryAxis("Data");
        xAxis.setCategoryMargin(0.40);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setNumberFormatOverride(yAxisNumberFormat);
        BarRenderer renderer = createBarChartRenderer(yAxisNumberFormat);
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

    private BarRenderer createBarChartRenderer(NumberFormat numberFormat) {
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition positiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setBasePositiveItemLabelPosition(positiveItemLabelPosition);
        ItemLabelPosition negativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setBaseNegativeItemLabelPosition(negativeItemLabelPosition);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, numberFormat));
        renderer.setBaseItemLabelsVisible(true);
        return renderer;
    }

    private XYPlot createScalabilityPlot(List<XYSeries> seriesList,
            String xAxisLabel, NumberFormat xAxisNumberFormat,
            String yAxisLabel, NumberFormat yAxisNumberFormat) {
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setNumberFormatOverride(xAxisNumberFormat);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setNumberFormatOverride(yAxisNumberFormat);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        int seriesIndex = 0;
        for (XYSeries series : seriesList) {
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(series);
            plot.setDataset(seriesIndex, seriesCollection);
            XYItemRenderer renderer = createScalabilityPlotRenderer(yAxisNumberFormat);
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

    private XYItemRenderer createScalabilityPlotRenderer(NumberFormat numberFormat) {
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
        // Use dashed line
        renderer.setSeriesStroke(0, new BasicStroke(
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {2.0f, 6.0f}, 0.0f
        ));
        return renderer;
    }

    private File writeChartToImageFile(JFreeChart chart, String fileNameBase) {
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File summaryChartFile = new File(summaryDirectory, fileNameBase + ".png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(summaryChartFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing summaryChartFile: " + summaryChartFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return summaryChartFile;
    }

    private void determineDefaultShownScoreLevelIndex() {
        defaultShownScoreLevelIndex = Integer.MAX_VALUE;
        for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            if (problemBenchmarkResult.hasAnySuccess()) {
                double[] winningScoreLevels = ScoreUtils.extractLevelDoubles(
                        problemBenchmarkResult.getWinningSingleBenchmarkResult().getScore());
                int[] differenceCount = new int[winningScoreLevels.length];
                for (int i = 0; i < differenceCount.length; i++) {
                    differenceCount[i] = 0;
                }
                for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                    if (singleBenchmarkResult.isSuccess()) {
                        double[] scoreLevels = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getScore());
                        for (int i = 0; i < scoreLevels.length; i++) {
                            if (scoreLevels[i] != winningScoreLevels[i]) {
                                differenceCount[i] = differenceCount[i] + 1;
                            }
                        }
                    }
                }
                int firstInterestingLevel = differenceCount.length - 1;
                for (int i = 0; i < differenceCount.length; i++) {
                    if (differenceCount[i] > 0) {
                        firstInterestingLevel = i;
                        break;
                    }
                }
                if (defaultShownScoreLevelIndex > firstInterestingLevel) {
                    defaultShownScoreLevelIndex = firstInterestingLevel;
                }
            }
        }
    }

    private void writeHtmlOverviewFile() {
        File benchmarkReportDirectory = plannerBenchmarkResult.getBenchmarkReportDirectory();
        WebsiteResourceUtils.copyResourcesTo(benchmarkReportDirectory);

        htmlOverviewFile = new File(benchmarkReportDirectory, "index.html");
        Configuration freemarkerCfg = new Configuration();
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setLocale(locale);
        freemarkerCfg.setClassForTemplateLoading(BenchmarkReport.class, "");

        String templateFilename = "benchmarkReport.html.ftl";
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("benchmarkReport", this);
        model.put("reportHelper", new ReportHelper());

        Writer writer = null;
        try {
            Template template = freemarkerCfg.getTemplate(templateFilename);
            writer = new OutputStreamWriter(new FileOutputStream(htmlOverviewFile), "UTF-8");
            template.process(model, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read templateFilename (" + templateFilename
                    + ") or write htmlOverviewFile (" + htmlOverviewFile + ").", e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException("Can not process Freemarker templateFilename (" + templateFilename
                    + ") to htmlOverviewFile (" + htmlOverviewFile + ").", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
