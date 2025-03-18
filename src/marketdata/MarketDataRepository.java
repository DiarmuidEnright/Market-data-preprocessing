package marketdata;

import java.util.List;

// Repository interface for persisting market data
public interface MarketDataRepository {
    // Initialize the repository (create tables, etc.)
    void initialize() throws Exception;
    
    // Save a list of market data entries
    // @param data the market data to save
    // @return the number of entries successfully saved
    int saveMarketData(List<MarketData> data) throws Exception;
    
    // Retrieve all market data from the repository
    // @return a list of all stored market data
    List<MarketData> getAllMarketData() throws Exception;
    
    // Retrieve market data for a specific symbol
    // @param symbol the stock symbol to search for
    // @return a list of market data entries for the given symbol
    List<MarketData> getMarketDataBySymbol(String symbol) throws Exception;
    
    // Retrieve market data within a time range
    // @param startTime the start timestamp (inclusive)
    // @param endTime the end timestamp (inclusive)
    // @return a list of market data entries within the time range
    List<MarketData> getMarketDataByTimeRange(long startTime, long endTime) throws Exception;
    
    // Get distinct symbols in the repository
    // @return a list of all unique symbols
    List<String> getAllSymbols() throws Exception;
    
    // Close any resources held by the repository
    void close() throws Exception;
}