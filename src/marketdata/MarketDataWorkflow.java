package marketdata;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class MarketDataWorkflow {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java MarketDataWorkflow <rawDataFilePath>");
            return;
        }
        String rawData = "";
        try {
            rawData = new String(Files.readAllBytes(Paths.get(args[0])));
        } catch (IOException e) {
            System.out.println("Error reading file");
            return;
        }
        // Start processing raw market data: cleaning and transforming in one go
        System.out.println("\n==================== DATA PROCESSING BLOCK ====================\n");
        // Clean the raw input data in a single step
        List<MarketData> cleanedData = new AdvancedMarketDataCleaner().fullCleanData(new MarketDataPreprocessor().preprocess(rawData));
        MarketDataTransformer transformer = new MarketDataTransformer();
        Map<String, List<Double>> movingAverages = transformer.getMovingAverage(cleanedData, 2);
        Map<String, List<MarketData>> resampled = transformer.resampleByInterval(cleanedData, 60);
        System.out.println("--- Transformation Results ---\n");
        System.out.println(String.format("%-10s %-25s %-20s", "Symbol", "Moving Average (last)", "Resampled Count"));
        System.out.println("---------------------------------------------------------------");
        for (String symbol : movingAverages.keySet()) {
            String maStr = movingAverages.get(symbol).isEmpty() ? "N/A" : String.format(Locale.US, "%.2f", movingAverages.get(symbol).get(movingAverages.get(symbol).size()-1));
            String resCount = String.valueOf(resampled.get(symbol).size());
            System.out.println(String.format("%-10s %-25s %-20s", symbol, maStr, resCount));
        }

        // Begin analytical assessment of the processed market data
        System.out.println("\n==================== ANALYSIS BLOCK ====================\n");
        MarketDataAnalyzer analyzer = new MarketDataAnalyzer(cleanedData);
        Map<String, Double> avgPrices = analyzer.getAveragePrice();
        Map<String, Double> minPrices = analyzer.getMinPrice();
        Map<String, Double> maxPrices = analyzer.getMaxPrice();
        Map<String, Long> counts = analyzer.countEntries();
        System.out.println("--- Analysis Results ---\n");
        System.out.println(String.format("%-10s %-15s %-15s %-15s %-10s", "Symbol", "Avg Price", "Min Price", "Max Price", "Count"));
        System.out.println("---------------------------------------------------------------------");
        for (String symbol : avgPrices.keySet()) {
            String avgStr = String.format(Locale.US, "%.2f", avgPrices.get(symbol));
            String minStr = String.format(Locale.US, "%.2f", minPrices.get(symbol));
            String maxStr = String.format(Locale.US, "%.2f", maxPrices.get(symbol));
            String countStr = String.valueOf(counts.get(symbol));
            System.out.println(String.format("%-10s %-15s %-15s %-15s %-10s", symbol, avgStr, minStr, maxStr, countStr));
        }
        System.out.println();
    }
}
