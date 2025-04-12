package marketdata;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//random commit counter = 4
public class CSGOSkinRepository {
    private Connection connection;
    private String dbPath;
    //setting the path to the db
    public CSGOSkinRepository(String dbPath) {
        this.dbPath = dbPath;
    }
    
    public void initialize() throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        //if the db is not found or it is empty then make it using the below SQL
        // this is probably the lazy was to do this but it works for now
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS csgo_skins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "symbol TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "timestamp INTEGER NOT NULL," +
                "weapon_type TEXT NOT NULL," +
                "skin_name TEXT NOT NULL," +
                "condition TEXT NOT NULL," +
                "is_stattrak BOOLEAN NOT NULL," +
                "rarity TEXT NOT NULL," +
                "wear TEXT NOT NULL," +
                "float_value REAL NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Create indexes for efficient querying, Harry Nguyen my beloved <3, MongoDB <3
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_csgo_weapon_type ON csgo_skins(weapon_type)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_csgo_skin_name ON csgo_skins(skin_name)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_csgo_condition ON csgo_skins(condition)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_csgo_rarity ON csgo_skins(rarity)");
            stmt.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_csgo_unique_skin ON csgo_skins(weapon_type, skin_name, condition, is_stattrak, timestamp)");
        }
    }
    
    public void saveSkinData(CSGOSkinData skin) throws Exception {
        String sql = "INSERT OR REPLACE INTO csgo_skins (symbol, price, timestamp, weapon_type, skin_name, condition, is_stattrak, rarity, wear, float_value) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        //for the user input run the methods depending on the input as like before with the market data
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, skin.getSymbol());
            pstmt.setDouble(2, skin.getPrice());
            pstmt.setLong(3, skin.getTimestamp());
            pstmt.setString(4, skin.getWeaponType());
            pstmt.setString(5, skin.getSkinName());
            pstmt.setString(6, skin.getCondition());
            pstmt.setBoolean(7, skin.isStatTrak());
            pstmt.setString(8, skin.getRarity());
            pstmt.setString(9, skin.getWear());
            pstmt.setDouble(10, skin.getFloatValue());
            
            pstmt.executeUpdate();
        }
    }
    
    public List<CSGOSkinData> getAllSkins() throws Exception {
        List<CSGOSkinData> skins = new ArrayList<>();
        //for all of the following inputs we are going to take the input as a string which is going to be our query in SQL
        String sql = "SELECT * FROM csgo_skins ORDER BY timestamp DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                skins.add(resultSetToSkinData(rs));
            }
        }
        
        return skins;
    }
    //init of the methods all called by the scanner input
    public List<CSGOSkinData> getSkinsByWeaponType(String weaponType) throws Exception {
        List<CSGOSkinData> skins = new ArrayList<>();
        //same as what we had before
        String sql = "SELECT * FROM csgo_skins WHERE LOWER(weapon_type) = LOWER(?) ORDER BY timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, weaponType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    skins.add(resultSetToSkinData(rs));
                }
            }
        }
        
        return skins;
    }
    
    public List<CSGOSkinData> getSkinsByRarity(String rarity) throws Exception {
        List<CSGOSkinData> skins = new ArrayList<>();
        String sql = "SELECT * FROM csgo_skins WHERE LOWER(rarity) = LOWER(?) ORDER BY timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, rarity);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    skins.add(resultSetToSkinData(rs));
                }
            }
        }
        
        return skins;
    }
    
    public List<CSGOSkinData> getStatTrakSkins() throws Exception {
        List<CSGOSkinData> skins = new ArrayList<>();
        String sql = "SELECT * FROM csgo_skins WHERE is_stattrak = 1 ORDER BY timestamp DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                skins.add(resultSetToSkinData(rs));
            }
        }
        
        return skins;
    }
    
    public double getAveragePriceByWeaponType(String weaponType) throws Exception {
        String sql = "SELECT AVG(price) as avg_price FROM csgo_skins WHERE LOWER(weapon_type) = LOWER(?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, weaponType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_price");
                }
            }
        }
        
        return 0.0;
    }
    
    private CSGOSkinData resultSetToSkinData(ResultSet rs) throws SQLException {
        return new CSGOSkinData(
            //in cs we have they unique identifiers for the different skins that we are working with so we want to format the outputs to display what is what
            rs.getString("symbol"),
            rs.getDouble("price"),
            rs.getLong("timestamp"),
            rs.getString("weapon_type"),
            rs.getString("skin_name"),
            rs.getString("condition"),
            rs.getBoolean("is_stattrak"),
            rs.getString("rarity"),
            rs.getString("wear"),
            rs.getDouble("float_value")
        );
    }
    //closing and cleaning up
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
