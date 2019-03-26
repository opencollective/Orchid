// Generated by delombok at Sun Mar 24 19:34:08 CDT 2019
package com.eden.orchid.api.generators;

import com.caseyjbrooks.clog.Clog;
import com.copperleaf.krow.HorizontalAlignment;
import com.copperleaf.krow.KrowTable;
import com.eden.orchid.Orchid;
import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.theme.pages.OrchidPage;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildMetrics {
    private final OrchidContext context;

    @Inject
    public BuildMetrics(OrchidContext context) {
        this.context = context;
    }

    private int progress;
    private int maxProgress;
    private int totalPageCount;
    private Map<String, GeneratorMetrics> generatorMetricsMap;
    private GeneratorMetrics compositeMetrics;

// Measure Indexing Phase
//----------------------------------------------------------------------------------------------------------------------
    public void startIndexing(Set<OrchidGenerator> generators) {
        generatorMetricsMap = new HashMap<>();
        compositeMetrics = null;
        progress = 0;
        totalPageCount = 0;
        maxProgress = generators.size();
    }

    public void startIndexingGenerator(String generator) {
        ensureMetricsExist(generator);
        generatorMetricsMap.get(generator).startIndexing();
        context.broadcast(Orchid.Lifecycle.ProgressEvent.fire(this, "indexing", progress, maxProgress));
    }

    public void stopIndexingGenerator(String generator, int numberOfPages) {
        ensureMetricsExist(generator);
        generatorMetricsMap.get(generator).stopIndexing();
        progress++;
        totalPageCount += numberOfPages;
    }

    public void stopIndexing() {
        context.broadcast(Orchid.Lifecycle.ProgressEvent.fire(this, "indexing", maxProgress, maxProgress));
    }

// Measure Generation Phase
//----------------------------------------------------------------------------------------------------------------------
    public void startGeneration() {
        progress = 0;
        maxProgress = totalPageCount;
    }

    public void startGeneratingGenerator(String generator) {
        ensureMetricsExist(generator);
        generatorMetricsMap.get(generator).startGenerating();
    }

    public void stopGeneratingGenerator(String generator) {
        ensureMetricsExist(generator);
        generatorMetricsMap.get(generator).stopGenerating();
    }

    public void onPageGenerated(OrchidPage page, long millis) {
        if (page.isIndexed()) {
            progress++;
            context.broadcast(Orchid.Lifecycle.ProgressEvent.fire(this, "building", progress, maxProgress, millis));
            if (page.getGenerator() != null) {
                ensureMetricsExist(page.getGenerator().getKey());
                generatorMetricsMap.get(page.getGenerator().getKey()).addPageGenerationTime(millis);
            }
        }
    }

    public void stopGeneration() {
        compositeMetrics = new GeneratorMetrics("TOTAL");
        generatorMetricsMap.values().stream().peek(compositeMetrics::compose).forEach(this::setColumnWidths);
        setColumnWidths(compositeMetrics);
        context.broadcast(Orchid.Lifecycle.ProgressEvent.fire(this, "building", maxProgress, maxProgress, 0));
    }

// Print Metrics
//----------------------------------------------------------------------------------------------------------------------
    private void ensureMetricsExist(String generator) {
        if (generator != null && !generatorMetricsMap.containsKey(generator)) {
            generatorMetricsMap.put(generator, new GeneratorMetrics(generator));
        }
    }

    public String getSummary() {
        if (compositeMetrics == null) throw new IllegalStateException("Cannot get build summary: build not complete");
        return Clog.format("Generated {} {} in {}", compositeMetrics.getPageCount() + "", (compositeMetrics.getPageCount() == 1) ? "page" : "pages", compositeMetrics.getTotalTime());
    }

    public KrowTable getDetail() {
        if (compositeMetrics == null) throw new IllegalStateException("Cannot get build summary: build not complete");
        titleColumnWidth = "Generator".length();
        pageCountColumnWidth = "Page Count".length();
        indexingTimeColumnWidth = "Indexing Time".length();
        generationTimeColumnWidth = "Generation Time".length();
        meanPageTimeColumnWidth = "Mean Page Generation Time".length();
        medianPageTimeColumnWidth = "Median Page Generation Time".length();
        KrowTable table = new KrowTable();
        table.columns("Page Count", "Indexing Time", "Generation Time", "Mean Page Generation Time", "Median Page Generation Time");
        List<GeneratorMetrics> metricsList = new ArrayList<>(generatorMetricsMap.values());
        metricsList.add(compositeMetrics);
        for (GeneratorMetrics metric : metricsList) {
            if (metric.getPageCount() == 0) continue;
            table.cell("Page Count", metric.getKey(), cell -> {
                cell.setContent("" + metric.getPageCount());
                return null;
            });
            table.cell("Indexing Time", metric.getKey(), cell -> {
                cell.setContent("" + metric.getIndexingTime());
                return null;
            });
            table.cell("Generation Time", metric.getKey(), cell -> {
                cell.setContent("" + metric.getGeneratingTime());
                return null;
            });
            table.cell("Mean Page Generation Time", metric.getKey(), cell -> {
                cell.setContent("" + metric.getMeanPageTime());
                return null;
            });
            table.cell("Median Page Generation Time", metric.getKey(), cell -> {
                cell.setContent("" + metric.getMedianPageTime());
                return null;
            });
        }
        table.column("Page Count", cell -> {
            cell.setWrapTextAt(pageCountColumnWidth);
            return null;
        });
        table.column("Indexing Time", cell -> {
            cell.setWrapTextAt(indexingTimeColumnWidth);
            return null;
        });
        table.column("Generation Time", cell -> {
            cell.setWrapTextAt(generationTimeColumnWidth);
            return null;
        });
        table.column("Mean Page Generation Time", cell -> {
            cell.setWrapTextAt(meanPageTimeColumnWidth);
            return null;
        });
        table.column("Median Page Generation Time", cell -> {
            cell.setWrapTextAt(medianPageTimeColumnWidth);
            return null;
        });
        table.table(cell -> {
            cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return null;
        });
        table.row("TOTAL", cell -> {
            cell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            return null;
        });
        return table;
    }

    private void setColumnWidths(GeneratorMetrics metric) {
        titleColumnWidth = Math.max(titleColumnWidth, metric.getKey().length());
        pageCountColumnWidth = Math.max(pageCountColumnWidth, (metric.getPageCount() + "").length());
        indexingTimeColumnWidth = Math.max(indexingTimeColumnWidth, metric.getIndexingTime().length());
        generationTimeColumnWidth = Math.max(generationTimeColumnWidth, metric.getGeneratingTime().length());
        meanPageTimeColumnWidth = Math.max(meanPageTimeColumnWidth, metric.getMeanPageTime().length());
        medianPageTimeColumnWidth = Math.max(medianPageTimeColumnWidth, metric.getMedianPageTime().length());
    }

    private int titleColumnWidth;
    private int pageCountColumnWidth;
    private int indexingTimeColumnWidth;
    private int generationTimeColumnWidth;
    private int meanPageTimeColumnWidth;
    private int medianPageTimeColumnWidth;

    @java.lang.SuppressWarnings("all")
    public OrchidContext getContext() {
        return this.context;
    }

    @java.lang.SuppressWarnings("all")
    public int getProgress() {
        return this.progress;
    }

    @java.lang.SuppressWarnings("all")
    public int getMaxProgress() {
        return this.maxProgress;
    }

    @java.lang.SuppressWarnings("all")
    public int getTotalPageCount() {
        return this.totalPageCount;
    }

    @java.lang.SuppressWarnings("all")
    public GeneratorMetrics getCompositeMetrics() {
        return this.compositeMetrics;
    }

    @java.lang.SuppressWarnings("all")
    public int getTitleColumnWidth() {
        return this.titleColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public int getPageCountColumnWidth() {
        return this.pageCountColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public int getIndexingTimeColumnWidth() {
        return this.indexingTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public int getGenerationTimeColumnWidth() {
        return this.generationTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public int getMeanPageTimeColumnWidth() {
        return this.meanPageTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public int getMedianPageTimeColumnWidth() {
        return this.medianPageTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public void setProgress(final int progress) {
        this.progress = progress;
    }

    @java.lang.SuppressWarnings("all")
    public void setMaxProgress(final int maxProgress) {
        this.maxProgress = maxProgress;
    }

    @java.lang.SuppressWarnings("all")
    public void setTotalPageCount(final int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    @java.lang.SuppressWarnings("all")
    public void setGeneratorMetricsMap(final Map<String, GeneratorMetrics> generatorMetricsMap) {
        this.generatorMetricsMap = generatorMetricsMap;
    }

    @java.lang.SuppressWarnings("all")
    public void setCompositeMetrics(final GeneratorMetrics compositeMetrics) {
        this.compositeMetrics = compositeMetrics;
    }

    @java.lang.SuppressWarnings("all")
    public void setTitleColumnWidth(final int titleColumnWidth) {
        this.titleColumnWidth = titleColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public void setPageCountColumnWidth(final int pageCountColumnWidth) {
        this.pageCountColumnWidth = pageCountColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public void setIndexingTimeColumnWidth(final int indexingTimeColumnWidth) {
        this.indexingTimeColumnWidth = indexingTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public void setGenerationTimeColumnWidth(final int generationTimeColumnWidth) {
        this.generationTimeColumnWidth = generationTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public void setMeanPageTimeColumnWidth(final int meanPageTimeColumnWidth) {
        this.meanPageTimeColumnWidth = meanPageTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public void setMedianPageTimeColumnWidth(final int medianPageTimeColumnWidth) {
        this.medianPageTimeColumnWidth = medianPageTimeColumnWidth;
    }

    @java.lang.SuppressWarnings("all")
    public Map<String, GeneratorMetrics> getGeneratorMetricsMap() {
        return this.generatorMetricsMap;
    }
}
