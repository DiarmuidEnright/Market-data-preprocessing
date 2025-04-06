package marketdata;

import java.sql.*;
//noticed that I was having an issue and was getting duplicate values so mmade this to clean up the table when we are done with it
public class CleanupData {
    public static void main(String[] args) {
        try {
            // Clean up CSGO skins table
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:marketdata.db")) {
                try (Statement stmt = conn.createStatement()) {
                    // Delete all data from csgo_skins table
                    stmt.execute("DELETE FROM csgo_skins");
                    System.out.println("Cleaned up CSGO skins data.");
                }
            }

            // Re-add sample data
            SampleCSGOData.main(args);
            
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
