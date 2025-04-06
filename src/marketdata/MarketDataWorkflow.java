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
            System.out.println("Usage: java MarketDataWorkflow <rawDataFilePath> [--load]");
            System.out.println("  --load: Load and analyze previously stored data instead of processing new data");
            return;
        }
        
        // Check if we should load previously stored data
        boolean loadMode = args.length > 1 && args[1].equals("--load");
        
        try (MarketDataPersistenceService persistenceService = new MarketDataPersistenceService()) {
            List<MarketData> cleanedData;
            
            if (loadMode) {
                // Load data from the database instead of processing a file
                System.out.println("\n==================== LOADING PERSISTED DATA ====================\n");
                cleanedData = persistenceService.retrieveAllMarketData();
                System.out.println("Loaded " + cleanedData.size() + " market data entries from database");
                
                // If no data found, exit
                if (cleanedData.isEmpty()) {
                    System.out.println("No persisted data found. Please process some data first.");
                    return;
                }
            } else {
                // Process new data from file
                String rawData = "";
                try {
                    rawData = new String(Files.readAllBytes(Paths.get(args[0])));
                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                    return;
                }
                
                // Start processing raw market data: cleaning and transforming in one go
                System.out.println("\n==================== DATA PROCESSING BLOCK ====================\n");
                
                // Clean the raw input data in a single step
                cleanedData = new AdvancedMarketDataCleaner().fullCleanData(new MarketDataPreprocessor().preprocess(rawData));
                
                // Persist the cleaned data to the database
                try {
                    int savedCount = persistenceService.persistMarketData(cleanedData);
                    System.out.println("Successfully persisted " + savedCount + " market data entries to database");
                } catch (Exception e) {
                    System.out.println("Error persisting data: " + e.getMessage());
                }
            }
            
            // Transform the data (whether loaded from DB or newly processed)
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
                //formatting the value for the return
                String avgStr = String.format(Locale.US, "%.2f", avgPrices.get(symbol));
                String minStr = String.format(Locale.US, "%.2f", minPrices.get(symbol));
                String maxStr = String.format(Locale.US, "%.2f", maxPrices.get(symbol));
                String countStr = String.valueOf(counts.get(symbol));
                System.out.println(String.format("%-10s %-15s %-15s %-15s %-10s", symbol, avgStr, minStr, maxStr, countStr));
            }
            
            // Display database statistics
            if (!loadMode) {
                System.out.println("\n==================== PERSISTENCE SUMMARY ====================\n");
                try {
                    List<String> symbols = persistenceService.getAvailableSymbols();
                    Map<String, Double> avgPricesStored = persistenceService.getAveragePriceBySymbol();
                    
                    System.out.println("Available symbols in database: " + symbols.size());
                    System.out.println("Total records in database: " + persistenceService.retrieveAllMarketData().size());
                    
                    System.out.println("\n--- Database Average Prices ---\n");
                    for (String symbol : symbols) {
                        System.out.println(symbol + ": " + String.format(Locale.US, "%.2f", avgPricesStored.get(symbol)));
                    }
                } catch (Exception e) {
                    System.out.println("Error retrieving database statistics: " + e.getMessage());
                }
            }
            
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error in workflow: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
