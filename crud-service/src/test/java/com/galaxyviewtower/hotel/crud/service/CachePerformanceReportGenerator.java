package com.galaxyviewtower.hotel.crud.service;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CachePerformanceReportGenerator {

    private static final String REPORT_DIR = "reports/cache-performance";
    private static final String HISTORY_DIR = "reports/cache-performance/history";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final List<Map<String, Object>> historicalData = new ArrayList<>();

    public void generateReport(Map<String, CacheStats> stats, Map<String, Double> performanceMetrics) {
        try {
            // Create reports directory if it doesn't exist
            Path reportDir = Paths.get(REPORT_DIR);
            Path historyDir = Paths.get(HISTORY_DIR);
            Files.createDirectories(reportDir);
            Files.createDirectories(historyDir);

            // Add memory metrics
            addMemoryMetrics(performanceMetrics);

            // Store historical data
            storeHistoricalData(stats, performanceMetrics);

            // Generate report filename with timestamp
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String reportPath = String.format("%s/cache_performance_report_%s.html", REPORT_DIR, timestamp);
            String csvPath = String.format("%s/cache_performance_metrics_%s.csv", REPORT_DIR, timestamp);

            // Generate HTML report
            try (FileWriter writer = new FileWriter(reportPath)) {
                writer.write(generateHtmlReport(stats, performanceMetrics));
            }

            // Generate CSV report
            try (FileWriter writer = new FileWriter(csvPath)) {
                writer.write(generateCsvReport(stats, performanceMetrics));
            }

            System.out.println("Cache performance report generated: " + reportPath);
            System.out.println("Cache performance metrics exported to: " + csvPath);
        } catch (IOException e) {
            System.err.println("Failed to generate cache performance report: " + e.getMessage());
        }
    }

    private void addMemoryMetrics(Map<String, Double> performanceMetrics) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Heap memory metrics
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        double heapUsagePercent = (double) heapUsed / heapMax * 100;
        
        // Non-heap memory metrics
        long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
        long nonHeapMax = memoryBean.getNonHeapMemoryUsage().getMax();
        double nonHeapUsagePercent = (double) nonHeapUsed / nonHeapMax * 100;

        performanceMetrics.put("Heap Memory Used (MB)", heapUsed / (1024.0 * 1024.0));
        performanceMetrics.put("Heap Memory Max (MB)", heapMax / (1024.0 * 1024.0));
        performanceMetrics.put("Heap Memory Usage (%)", heapUsagePercent);
        performanceMetrics.put("Non-Heap Memory Used (MB)", nonHeapUsed / (1024.0 * 1024.0));
        performanceMetrics.put("Non-Heap Memory Max (MB)", nonHeapMax / (1024.0 * 1024.0));
        performanceMetrics.put("Non-Heap Memory Usage (%)", nonHeapUsagePercent);
    }

    private void storeHistoricalData(Map<String, CacheStats> stats, Map<String, Double> performanceMetrics) {
        Map<String, Object> dataPoint = new HashMap<>();
        dataPoint.put("timestamp", LocalDateTime.now());
        dataPoint.put("stats", new HashMap<>(stats));
        dataPoint.put("metrics", new HashMap<>(performanceMetrics));
        historicalData.add(dataPoint);

        // Keep only last 100 data points
        if (historicalData.size() > 100) {
            historicalData.remove(0);
        }
    }

    private String generateCsvReport(Map<String, CacheStats> stats, Map<String, Double> performanceMetrics) {
        StringBuilder csv = new StringBuilder();
        
        // Add headers
        csv.append("Timestamp,");
        csv.append("Cache Name,");
        csv.append("Hit Rate,");
        csv.append("Miss Rate,");
        csv.append("Load Success Rate,");
        csv.append("Load Failure Rate,");
        csv.append("Eviction Count,");
        csv.append("Average Load Time,");
        csv.append("Heap Memory Used (MB),");
        csv.append("Heap Memory Usage (%),");
        csv.append("Non-Heap Memory Used (MB),");
        csv.append("Non-Heap Memory Usage (%)\n");

        // Add data rows
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        for (Map.Entry<String, CacheStats> entry : stats.entrySet()) {
            CacheStats cacheStats = entry.getValue();
            csv.append(String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%d,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                timestamp,
                entry.getKey(),
                cacheStats.hitRate() * 100,
                cacheStats.missRate() * 100,
                cacheStats.loadSuccessRate() * 100,
                cacheStats.loadFailureRate() * 100,
                cacheStats.evictionCount(),
                cacheStats.averageLoadPenalty(),
                performanceMetrics.get("Heap Memory Used (MB)"),
                performanceMetrics.get("Heap Memory Usage (%)"),
                performanceMetrics.get("Non-Heap Memory Used (MB)"),
                performanceMetrics.get("Non-Heap Memory Usage (%)")
            ));
        }

        return csv.toString();
    }

    private String generateHtmlReport(Map<String, CacheStats> stats, Map<String, Double> performanceMetrics) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <title>Cache Performance Report</title>\n")
            .append("    <style>\n")
            .append("        body { font-family: Arial, sans-serif; margin: 20px; }\n")
            .append("        .container { max-width: 1200px; margin: 0 auto; }\n")
            .append("        .section { margin-bottom: 30px; }\n")
            .append("        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }\n")
            .append("        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }\n")
            .append("        th { background-color: #f5f5f5; }\n")
            .append("        .metric { margin-bottom: 10px; }\n")
            .append("        .metric-label { font-weight: bold; }\n")
            .append("        .chart { margin-top: 20px; height: 300px; }\n")
            .append("        .chart-container { display: flex; flex-wrap: wrap; gap: 20px; }\n")
            .append("        .chart-wrapper { flex: 1; min-width: 400px; }\n")
            .append("    </style>\n")
            .append("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <div class=\"container\">\n")
            .append("        <h1>Cache Performance Report</h1>\n")
            .append("        <p>Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("</p>\n");

        // Memory Metrics Section
        html.append("        <div class=\"section\">\n")
            .append("            <h2>Memory Metrics</h2>\n")
            .append("            <div class=\"chart-container\">\n")
            .append("                <div class=\"chart-wrapper\">\n")
            .append("                    <canvas id=\"heapMemoryChart\"></canvas>\n")
            .append("                </div>\n")
            .append("                <div class=\"chart-wrapper\">\n")
            .append("                    <canvas id=\"nonHeapMemoryChart\"></canvas>\n")
            .append("                </div>\n")
            .append("            </div>\n")
            .append("        </div>\n");

        // Cache Statistics Section
        html.append("        <div class=\"section\">\n")
            .append("            <h2>Cache Statistics</h2>\n")
            .append("            <table>\n")
            .append("                <tr>\n")
            .append("                    <th>Cache Name</th>\n")
            .append("                    <th>Hit Rate</th>\n")
            .append("                    <th>Miss Rate</th>\n")
            .append("                    <th>Load Success Rate</th>\n")
            .append("                    <th>Load Failure Rate</th>\n")
            .append("                    <th>Eviction Count</th>\n")
            .append("                    <th>Average Load Time (ms)</th>\n")
            .append("                </tr>\n");

        for (Map.Entry<String, CacheStats> entry : stats.entrySet()) {
            CacheStats cacheStats = entry.getValue();
            html.append("                <tr>\n")
                .append("                    <td>").append(entry.getKey()).append("</td>\n")
                .append("                    <td>").append(String.format("%.2f%%", cacheStats.hitRate() * 100)).append("</td>\n")
                .append("                    <td>").append(String.format("%.2f%%", cacheStats.missRate() * 100)).append("</td>\n")
                .append("                    <td>").append(String.format("%.2f%%", cacheStats.loadSuccessRate() * 100)).append("</td>\n")
                .append("                    <td>").append(String.format("%.2f%%", cacheStats.loadFailureRate() * 100)).append("</td>\n")
                .append("                    <td>").append(cacheStats.evictionCount()).append("</td>\n")
                .append("                    <td>").append(String.format("%.2f", cacheStats.averageLoadPenalty())).append("</td>\n")
                .append("                </tr>\n");
        }

        html.append("            </table>\n")
            .append("        </div>\n");

        // Performance Charts Section
        html.append("        <div class=\"section\">\n")
            .append("            <h2>Performance Charts</h2>\n")
            .append("            <div class=\"chart-container\">\n")
            .append("                <div class=\"chart-wrapper\">\n")
            .append("                    <canvas id=\"hitRateChart\"></canvas>\n")
            .append("                </div>\n")
            .append("                <div class=\"chart-wrapper\">\n")
            .append("                    <canvas id=\"loadTimeChart\"></canvas>\n")
            .append("                </div>\n")
            .append("                <div class=\"chart-wrapper\">\n")
            .append("                    <canvas id=\"historicalHitRateChart\"></canvas>\n")
            .append("                </div>\n")
            .append("                <div class=\"chart-wrapper\">\n")
            .append("                    <canvas id=\"historicalLoadTimeChart\"></canvas>\n")
            .append("                </div>\n")
            .append("            </div>\n")
            .append("        </div>\n");

        // JavaScript for Charts
        html.append("        <script>\n")
            // Memory Charts
            .append("            // Heap Memory Chart\n")
            .append("            new Chart(document.getElementById('heapMemoryChart'), {\n")
            .append("                type: 'doughnut',\n")
            .append("                data: {\n")
            .append("                    labels: ['Used', 'Free'],\n")
            .append("                    datasets: [{\n")
            .append("                        data: [")
            .append(performanceMetrics.get("Heap Memory Used (MB)"))
            .append(", ")
            .append(performanceMetrics.get("Heap Memory Max (MB)") - performanceMetrics.get("Heap Memory Used (MB)"))
            .append("],\n")
            .append("                        backgroundColor: ['rgba(255, 99, 132, 0.2)', 'rgba(75, 192, 192, 0.2)'],\n")
            .append("                        borderColor: ['rgba(255, 99, 132, 1)', 'rgba(75, 192, 192, 1)'],\n")
            .append("                        borderWidth: 1\n")
            .append("                    }]\n")
            .append("                },\n")
            .append("                options: {\n")
            .append("                    responsive: true,\n")
            .append("                    plugins: {\n")
            .append("                        title: {\n")
            .append("                            display: true,\n")
            .append("                            text: 'Heap Memory Usage'\n")
            .append("                        }\n")
            .append("                    }\n")
            .append("                }\n")
            .append("            });\n")
            .append("\n")
            .append("            // Non-Heap Memory Chart\n")
            .append("            new Chart(document.getElementById('nonHeapMemoryChart'), {\n")
            .append("                type: 'doughnut',\n")
            .append("                data: {\n")
            .append("                    labels: ['Used', 'Free'],\n")
            .append("                    datasets: [{\n")
            .append("                        data: [")
            .append(performanceMetrics.get("Non-Heap Memory Used (MB)"))
            .append(", ")
            .append(performanceMetrics.get("Non-Heap Memory Max (MB)") - performanceMetrics.get("Non-Heap Memory Used (MB)"))
            .append("],\n")
            .append("                        backgroundColor: ['rgba(153, 102, 255, 0.2)', 'rgba(255, 159, 64, 0.2)'],\n")
            .append("                        borderColor: ['rgba(153, 102, 255, 1)', 'rgba(255, 159, 64, 1)'],\n")
            .append("                        borderWidth: 1\n")
            .append("                    }]\n")
            .append("                },\n")
            .append("                options: {\n")
            .append("                    responsive: true,\n")
            .append("                    plugins: {\n")
            .append("                        title: {\n")
            .append("                            display: true,\n")
            .append("                            text: 'Non-Heap Memory Usage'\n")
            .append("                        }\n")
            .append("                    }\n")
            .append("                }\n")
            .append("            });\n")
            .append("\n")
            // Cache Performance Charts
            .append("            // Hit Rate Chart\n")
            .append("            new Chart(document.getElementById('hitRateChart'), {\n")
            .append("                type: 'bar',\n")
            .append("                data: {\n")
            .append("                    labels: ").append(getCacheNames(stats)).append(",\n")
            .append("                    datasets: [{\n")
            .append("                        label: 'Hit Rate (%)',\n")
            .append("                        data: ").append(getHitRates(stats)).append(",\n")
            .append("                        backgroundColor: 'rgba(75, 192, 192, 0.2)',\n")
            .append("                        borderColor: 'rgba(75, 192, 192, 1)',\n")
            .append("                        borderWidth: 1\n")
            .append("                    }]\n")
            .append("                },\n")
            .append("                options: {\n")
            .append("                    responsive: true,\n")
            .append("                    scales: {\n")
            .append("                        y: {\n")
            .append("                            beginAtZero: true,\n")
            .append("                            max: 100\n")
            .append("                        }\n")
            .append("                    }\n")
            .append("                }\n")
            .append("            });\n")
            .append("\n")
            .append("            // Load Time Chart\n")
            .append("            new Chart(document.getElementById('loadTimeChart'), {\n")
            .append("                type: 'bar',\n")
            .append("                data: {\n")
            .append("                    labels: ").append(getCacheNames(stats)).append(",\n")
            .append("                    datasets: [{\n")
            .append("                        label: 'Average Load Time (ms)',\n")
            .append("                        data: ").append(getLoadTimes(stats)).append(",\n")
            .append("                        backgroundColor: 'rgba(153, 102, 255, 0.2)',\n")
            .append("                        borderColor: 'rgba(153, 102, 255, 1)',\n")
            .append("                        borderWidth: 1\n")
            .append("                    }]\n")
            .append("                },\n")
            .append("                options: {\n")
            .append("                    responsive: true,\n")
            .append("                    scales: {\n")
            .append("                        y: {\n")
            .append("                            beginAtZero: true\n")
            .append("                        }\n")
            .append("                    }\n")
            .append("                }\n")
            .append("            });\n")
            .append("\n")
            // Historical Charts
            .append("            // Historical Hit Rate Chart\n")
            .append("            new Chart(document.getElementById('historicalHitRateChart'), {\n")
            .append("                type: 'line',\n")
            .append("                data: {\n")
            .append("                    labels: ").append(getHistoricalTimestamps()).append(",\n")
            .append("                    datasets: ").append(getHistoricalHitRateData()).append("\n")
            .append("                },\n")
            .append("                options: {\n")
            .append("                    responsive: true,\n")
            .append("                    scales: {\n")
            .append("                        y: {\n")
            .append("                            beginAtZero: true,\n")
            .append("                            max: 100\n")
            .append("                        }\n")
            .append("                    }\n")
            .append("                }\n")
            .append("            });\n")
            .append("\n")
            .append("            // Historical Load Time Chart\n")
            .append("            new Chart(document.getElementById('historicalLoadTimeChart'), {\n")
            .append("                type: 'line',\n")
            .append("                data: {\n")
            .append("                    labels: ").append(getHistoricalTimestamps()).append(",\n")
            .append("                    datasets: ").append(getHistoricalLoadTimeData()).append("\n")
            .append("                },\n")
            .append("                options: {\n")
            .append("                    responsive: true,\n")
            .append("                    scales: {\n")
            .append("                        y: {\n")
            .append("                            beginAtZero: true\n")
            .append("                        }\n")
            .append("                    }\n")
            .append("                }\n")
            .append("            });\n")
            .append("        </script>\n")
            .append("    </div>\n")
            .append("</body>\n")
            .append("</html>");

        return html.toString();
    }

    private String getCacheNames(Map<String, CacheStats> stats) {
        return "[" + stats.keySet().stream()
            .map(name -> "'" + name + "'")
            .reduce((a, b) -> a + ", " + b)
            .orElse("") + "]";
    }

    private String getHitRates(Map<String, CacheStats> stats) {
        return "[" + stats.values().stream()
            .map(stats -> String.format("%.2f", stats.hitRate() * 100))
            .reduce((a, b) -> a + ", " + b)
            .orElse("") + "]";
    }

    private String getLoadTimes(Map<String, CacheStats> stats) {
        return "[" + stats.values().stream()
            .map(stats -> String.format("%.2f", stats.averageLoadPenalty()))
            .reduce((a, b) -> a + ", " + b)
            .orElse("") + "]";
    }

    private String getHistoricalTimestamps() {
        return "[" + historicalData.stream()
            .map(data -> "'" + ((LocalDateTime) data.get("timestamp")).format(DateTimeFormatter.ISO_LOCAL_TIME) + "'")
            .collect(Collectors.joining(", ")) + "]";
    }

    private String getHistoricalHitRateData() {
        Set<String> cacheNames = new HashSet<>();
        historicalData.forEach(data -> 
            ((Map<String, CacheStats>) data.get("stats")).keySet().forEach(cacheNames::add));

        return "[" + cacheNames.stream()
            .map(cacheName -> {
                List<Double> hitRates = historicalData.stream()
                    .map(data -> ((Map<String, CacheStats>) data.get("stats")).get(cacheName))
                    .map(stats -> stats.hitRate() * 100)
                    .collect(Collectors.toList());

                return String.format(
                    "{label: '%s', data: %s, borderColor: 'rgba(%d, %d, %d, 1)', backgroundColor: 'rgba(%d, %d, %d, 0.2)', fill: false}",
                    cacheName,
                    hitRates,
                    new Random(cacheName.hashCode()).nextInt(255),
                    new Random(cacheName.hashCode() + 1).nextInt(255),
                    new Random(cacheName.hashCode() + 2).nextInt(255),
                    new Random(cacheName.hashCode()).nextInt(255),
                    new Random(cacheName.hashCode() + 1).nextInt(255),
                    new Random(cacheName.hashCode() + 2).nextInt(255)
                );
            })
            .collect(Collectors.joining(", ")) + "]";
    }

    private String getHistoricalLoadTimeData() {
        Set<String> cacheNames = new HashSet<>();
        historicalData.forEach(data -> 
            ((Map<String, CacheStats>) data.get("stats")).keySet().forEach(cacheNames::add));

        return "[" + cacheNames.stream()
            .map(cacheName -> {
                List<Double> loadTimes = historicalData.stream()
                    .map(data -> ((Map<String, CacheStats>) data.get("stats")).get(cacheName))
                    .map(stats -> stats.averageLoadPenalty())
                    .collect(Collectors.toList());

                return String.format(
                    "{label: '%s', data: %s, borderColor: 'rgba(%d, %d, %d, 1)', backgroundColor: 'rgba(%d, %d, %d, 0.2)', fill: false}",
                    cacheName,
                    loadTimes,
                    new Random(cacheName.hashCode()).nextInt(255),
                    new Random(cacheName.hashCode() + 1).nextInt(255),
                    new Random(cacheName.hashCode() + 2).nextInt(255),
                    new Random(cacheName.hashCode()).nextInt(255),
                    new Random(cacheName.hashCode() + 1).nextInt(255),
                    new Random(cacheName.hashCode() + 2).nextInt(255)
                );
            })
            .collect(Collectors.joining(", ")) + "]";
    }
} 