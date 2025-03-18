package marketdata;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// SQLite implementation of the MarketDataRepository interface
public class SQLiteMarketDataRepository implements MarketDataRepository {
    private Connection connection;
    private String dbPath;
    
    // Constructor for the SQLite repository
    // @param dbPath the path to the SQLite database file
    public SQLiteMarketDataRepository(String dbPath) {
        this.dbPath = dbPath;
    }
    
    @Override
    public void initialize() throws Exception {
        // Establish connection to SQLite database
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        
        // Create market_data table if it doesn't exist
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS market_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "symbol TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "timestamp INTEGER NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Create an index on symbol for faster lookups
            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_market_data_symbol ON market_data(symbol)"
            );
            
            // Create an index on timestamp for faster time-based queries
            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_market_data_timestamp ON market_data(timestamp)"
            );
            
            // Create a unique index on symbol and timestamp to prevent duplicates
            stmt.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS idx_market_data_symbol_timestamp ON market_data(symbol, timestamp)"
            );
        }
    }
    
    @Override
    public int saveMarketData(List<MarketData> data) throws Exception {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        
        // Use INSERT OR IGNORE to skip duplicates based on the unique index
        String sql = "INSERT OR IGNORE INTO market_data (symbol, price, timestamp) VALUES (?, ?, ?)";
        int count = 0;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Disable auto-commit for batch operations
            connection.setAutoCommit(false);
            
            for (MarketData md : data) {
                pstmt.setString(1, md.getSymbol());
                pstmt.setDouble(2, md.getPrice());
                pstmt.setLong(3, md.getTimestamp());
                pstmt.addBatch();
                count++;
            }
            
            // Execute batch and get the actual number of rows inserted
            int[] updateCounts = pstmt.executeBatch();
            connection.commit();
            
            // Count only the rows that were actually inserted (not ignored)
            count = 0;
            for (int updateCount : updateCounts) {
                if (updateCount > 0) {
                    count++;
                }
            }
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
        
        return count;
    }
    
    @Override
    public List<MarketData> getAllMarketData() throws Exception {
        List<MarketData> result = new ArrayList<>();
        String sql = "SELECT symbol, price, timestamp FROM market_data ORDER BY timestamp";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String symbol = rs.getString("symbol");
                double price = rs.getDouble("price");
                long timestamp = rs.getLong("timestamp");
                
                result.add(new MarketData(symbol, price, timestamp));
            }
        }
        
        return result;
    }
    
    @Override
    public List<MarketData> getMarketDataBySymbol(String symbol) throws Exception {
        List<MarketData> result = new ArrayList<>();
        String sql = "SELECT symbol, price, timestamp FROM market_data WHERE symbol = ? ORDER BY timestamp";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, symbol);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String sym = rs.getString("symbol");
                    double price = rs.getDouble("price");
                    long timestamp = rs.getLong("timestamp");
                    
                    result.add(new MarketData(sym, price, timestamp));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<MarketData> getMarketDataByTimeRange(long startTime, long endTime) throws Exception {
        List<MarketData> result = new ArrayList<>();
        String sql = "SELECT symbol, price, timestamp FROM market_data WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, startTime);
            pstmt.setLong(2, endTime);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String symbol = rs.getString("symbol");
                    double price = rs.getDouble("price");
                    long timestamp = rs.getLong("timestamp");
                    
                    result.add(new MarketData(symbol, price, timestamp));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<String> getAllSymbols() throws Exception {
        List<String> result = new ArrayList<>();
        String sql = "SELECT DISTINCT symbol FROM market_data ORDER BY symbol";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                result.add(rs.getString("symbol"));
            }
        }
        
        return result;
    }
    
    // Note: Make sure the pipeline is accepting the correct data tomorrow
    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}