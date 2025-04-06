package marketdata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service to handle market data persistence operations
 */
public class MarketDataPersistenceService implements AutoCloseable {
    private final MarketDataRepository repository;
    private static final String DEFAULT_DB_PATH = "marketdata.db";
    
    /**
     * Constructor using the default database location
     */
    public MarketDataPersistenceService() throws Exception {
        this(DEFAULT_DB_PATH);
    }
    
    /**
     * Constructor specifying the database location
     * @param dbPath path to the database file
     */
    public MarketDataPersistenceService(String dbPath) throws Exception {
        repository = new SQLiteMarketDataRepository(dbPath);
        repository.initialize();
    }
    
    /**
     * Store a list of market data entries
     * @param data the market data to store
     * @return the number of entries saved
     */
    public int persistMarketData(List<MarketData> data) throws Exception {
        return repository.saveMarketData(data);
    }
    
    /**
     * Retrieve all stored market data
     * @return a list of all market data entries
     */
    public List<MarketData> retrieveAllMarketData() throws Exception {
        return repository.getAllMarketData();
    }
    
    /**
     * Retrieve market data for a specific symbol
     * @param symbol the symbol to retrieve data for
     * @return a list of market data for the given symbol
     */
    public List<MarketData> retrieveMarketDataBySymbol(String symbol) throws Exception {
        return repository.getMarketDataBySymbol(symbol);
    }
    
    /**
     * Retrieve market data within a time range
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return a list of market data within the given range
     */
    public List<MarketData> retrieveMarketDataByTimeRange(long startTime, long endTime) throws Exception {
        return repository.getMarketDataByTimeRange(startTime, endTime);
    }
    
    /**
     * Get all available symbols
     * @return a list of distinct symbols
     */
    public List<String> getAvailableSymbols() throws Exception {
        return repository.getAllSymbols();
    }
    
    /**
     * Get a statistical summary of the stored market data
     * @return a map of symbol to average price
     */
    public Map<String, Double> getAveragePriceBySymbol() throws Exception {
        List<MarketData> allData = repository.getAllMarketData();
        return allData.stream()
                .collect(Collectors.groupingBy(
                    MarketData::getSymbol,
                    Collectors.averagingDouble(MarketData::getPrice)
                ));
    }
    
    @Override
    public void close() throws Exception {
        if (repository != null) {
            repository.close();
        }
    }
}
