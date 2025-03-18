package marketdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;

/**
 * A runner class to demonstrate the market data persistence functionality
 */
public class MarketDataRunner {
    private static final String DEFAULT_DB_PATH = "marketdata.db";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try (MarketDataPersistenceService persistenceService = new MarketDataPersistenceService(DEFAULT_DB_PATH)) {
            int choice = -1;
            
            while (choice != 0) {
                displayMenu();
                try {
                    System.out.print("Enter your choice: ");
                    choice = Integer.parseInt(scanner.nextLine().trim());
                    
                    switch (choice) {
                        case 1:
                            processNewFile(scanner, persistenceService);
                            break;
                        case 2:
                            viewAllStoredData(persistenceService);
                            break;
                        case 3:
                            viewDataBySymbol(scanner, persistenceService);
                            break;
                        case 4:
                            viewDataByTimeRange(scanner, persistenceService);
                            break;
                        case 5:
                            viewSummaryStatistics(persistenceService);
                            break;
                        case 0:
                            System.out.println("Exiting...");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
                
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void displayMenu() {
        System.out.println("===== MARKET DATA PERSISTENCE DEMO =====");
        System.out.println("1. Process new data file and store");
        System.out.println("2. View all stored data");
        System.out.println("3. View data by symbol");
        System.out.println("4. View data by time range");
        System.out.println("5. View summary statistics");
        System.out.println("0. Exit");
    }
    
    private static void processNewFile(Scanner scanner, MarketDataPersistenceService persistenceService) throws Exception {
        System.out.print("Enter the path to the raw data file: ");
        String filePath = scanner.nextLine().trim();
        
        try {
            // Read and process the data
            String rawData = new String(Files.readAllBytes(Paths.get(filePath)));
            List<MarketData> data = new MarketDataPreprocessor().preprocess(rawData);
            List<MarketData> cleanedData = new AdvancedMarketDataCleaner().fullCleanData(data);
            
            // Store the processed data
            int count = persistenceService.persistMarketData(cleanedData);
            System.out.println("Successfully stored " + count + " market data entries.");
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    
    private static void viewAllStoredData(MarketDataPersistenceService persistenceService) throws Exception {
        List<MarketData> allData = persistenceService.retrieveAllMarketData();
        
        if (allData.isEmpty()) {
            System.out.println("No data found in the database.");
            return;
        }
        
        System.out.println("\nRetrieved " + allData.size() + " market data entries:");
        displayMarketDataList(allData, 10); // Show only first 10 entries
    }
    
    private static void viewDataBySymbol(Scanner scanner, MarketDataPersistenceService persistenceService) throws Exception {
        List<String> symbols = persistenceService.getAvailableSymbols();
        
        if (symbols.isEmpty()) {
            System.out.println("No symbols found in the database.");
            return;
        }
        
        System.out.println("Available symbols: " + String.join(", ", symbols));
        System.out.print("Enter symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        
        List<MarketData> symbolData = persistenceService.retrieveMarketDataBySymbol(symbol);
        if (symbolData.isEmpty()) {
            System.out.println("No data found for symbol: " + symbol);
            return;
        }
        
        System.out.println("\nRetrieved " + symbolData.size() + " entries for symbol: " + symbol);
        displayMarketDataList(symbolData, 10); // Show only first 10 entries
    }
    
    private static void viewDataByTimeRange(Scanner scanner, MarketDataPersistenceService persistenceService) throws Exception {
        System.out.print("Enter start timestamp: ");
        long startTime = Long.parseLong(scanner.nextLine().trim());
        
        System.out.print("Enter end timestamp: ");
        long endTime = Long.parseLong(scanner.nextLine().trim());
        
        List<MarketData> rangeData = persistenceService.retrieveMarketDataByTimeRange(startTime, endTime);
        if (rangeData.isEmpty()) {
            System.out.println("No data found in the specified time range.");
            return;
        }
        
        System.out.println("\nRetrieved " + rangeData.size() + " entries between " + startTime + " and " + endTime);
        displayMarketDataList(rangeData, 10); // Show only first 10 entries
    }
    
    private static void viewSummaryStatistics(MarketDataPersistenceService persistenceService) throws Exception {
        Map<String, Double> averagePrices = persistenceService.getAveragePriceBySymbol();
        
        if (averagePrices.isEmpty()) {
            System.out.println("No data available for summary statistics.");
            return;
        }
        
        System.out.println("\n--- Summary Statistics ---");
        System.out.println(String.format("%-10s %-15s", "Symbol", "Avg Price"));
        System.out.println("--------------------------");
        
        for (String symbol : averagePrices.keySet()) {
            System.out.println(String.format("%-10s %-15s", 
                    symbol, 
                    String.format(Locale.US, "%.2f", averagePrices.get(symbol))));
        }
    }
    
    private static void displayMarketDataList(List<MarketData> data, int limit) {
        System.out.println(String.format("%-10s %-15s %-15s", "Symbol", "Price", "Timestamp"));
        System.out.println("----------------------------------------");
        
        int count = 0;
        for (MarketData md : data) {
            if (count++ >= limit) {
                System.out.println("... and " + (data.size() - limit) + " more entries");
                break;
            }
            System.out.println(String.format("%-10s %-15s %-15s", 
                    md.getSymbol(), 
                    String.format(Locale.US, "%.2f", md.getPrice()),
                    md.getTimestamp()));
        }
    }
}
