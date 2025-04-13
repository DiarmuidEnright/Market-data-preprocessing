package marketdata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Client for interacting with Steam API to validate CSGO skin information
 */
public class SteamAPIClient {
    private static final String STEAM_MARKET_API_BASE = "https://steamcommunity.com/market/search/render/?query=";
    private static final String STEAM_MARKET_PARAMS = "&appid=730&norender=1"; // 730 is the app ID for CSGO
    
    /**
     * Validates if a CSGO skin exists in the Steam marketplace
     * 
     * @param weaponType The weapon type (e.g., "AK-47", "M4A4")
     * @param skinName The skin name (e.g., "Asiimov", "Dragon Lore")
     * @param condition The condition (e.g., "Factory New", "Field-Tested")
     * @param isStatTrak Whether the skin is StatTrak™
     * @return true if the skin exists, false otherwise
     */
    public boolean validateSkinExists(String weaponType, String skinName, String condition, boolean isStatTrak) {
        try {
            // Construct the search query similar to how it appears on Steam Market
            String searchQuery = weaponType + " | " + skinName + " " + condition;
            if (isStatTrak) {
                searchQuery = "StatTrak™ " + searchQuery;
            }
            
            // URL encode the search query
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());
            String apiUrl = STEAM_MARKET_API_BASE + encodedQuery + STEAM_MARKET_PARAMS;
            
            // Make HTTP request to Steam API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // Set a user agent to avoid being blocked
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Check if the response contains results
                String jsonResponse = response.toString();
                boolean hasResults = jsonResponse.contains("\"total_count\":") && 
                                    !jsonResponse.contains("\"total_count\":0");
                
                return hasResults;
            } else {
                System.out.println("Error connecting to Steam API: HTTP " + responseCode);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error validating skin with Steam API: " + e.getMessage());
            return false;
        }
    }
}