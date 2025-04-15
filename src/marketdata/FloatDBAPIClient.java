package marketdata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Client for interacting with Float DB API to check if a CSGO skin exists
 * and retrieve float values
 */
public class FloatDBAPIClient {
    private static final String FLOAT_DB_API_BASE = "https://api.csgofloat.com/";
    private static final String ITEM_INFO_ENDPOINT = "?url=";
    // Added backoff timing to handle rate limiting
    private static final int RATE_LIMIT_BACKOFF_MS = 2000;
    private long lastRequestTime = 0;

    /**
     * Checks if a CSGO skin exists in the Float DB database
     * 
     * @param weaponType The weapon type (e.g., "AK-47", "M4A4")
     * @param skinName The skin name (e.g., "Asiimov", "Dragon Lore")
     * @param condition The condition (e.g., "Factory New", "Field-Tested")
     * @param isStatTrak Whether the skin is StatTrak™
     * @return true if the skin exists, false otherwise
     */
    public boolean validateSkinExists(String weaponType, String skinName, String condition, boolean isStatTrak) {
        try {
            // Apply rate limiting
            applyRateLimit();
            
            // Construct a market hash name similar to how it appears on Steam Market
            String marketHashName = constructMarketHashName(weaponType, skinName, condition, isStatTrak);
            
            // For Float DB, we'd typically use an inspect link, but for basic validation,
            // we can use a more general query approach
            String encodedQuery = URLEncoder.encode(marketHashName, StandardCharsets.UTF_8.toString());
            String apiUrl = FLOAT_DB_API_BASE + "v1/search?item=" + encodedQuery + "&limit=1";
            
            // Make HTTP request to Float DB API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // Set a user agent to avoid being blocked
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // Add additional headers to appear more like a browser
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setRequestProperty("Connection", "keep-alive");
            
            // Update last request time
            lastRequestTime = System.currentTimeMillis();
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Check if the response indicates the skin exists
                String jsonResponse = response.toString();
                boolean hasResults = jsonResponse.contains("\"items\":") && 
                                    !jsonResponse.contains("\"items\":[]");
                
                // Additional check to ensure it's the exact skin
                if (hasResults) {
                    // Check if the response contains the exact skin we're looking for
                    boolean exactMatch = jsonResponse.contains("\"market_hash_name\":\"" + marketHashName + "\"");
                    return exactMatch;
                }
                
                return false;
            } else if (responseCode == 429) {
                // Handle rate limiting by waiting longer next time
                System.out.println("Rate limit hit with Float DB API. Backing off.");
                return false;
            } else {
                System.out.println("Error connecting to Float DB API: HTTP " + responseCode);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error validating skin with Float DB API: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves the float value for a specific skin using its inspect link
     * 
     * @param inspectLink The Steam inspect link for the skin
     * @return The float value or -1 if not found
     */
    public double getFloatValue(String inspectLink) {
        try {
            if (inspectLink == null || inspectLink.isEmpty()) {
                return -1;
            }
            
            // Apply rate limiting
            applyRateLimit();
            
            String encodedLink = URLEncoder.encode(inspectLink, StandardCharsets.UTF_8.toString());
            String apiUrl = FLOAT_DB_API_BASE + ITEM_INFO_ENDPOINT + encodedLink;
            
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "application/json");
            
            // Update last request time
            lastRequestTime = System.currentTimeMillis();
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Parse the float value from the response
                String jsonResponse = response.toString();
                if (jsonResponse.contains("\"floatvalue\":")) {
                    int startIndex = jsonResponse.indexOf("\"floatvalue\":") + 13;
                    int endIndex = jsonResponse.indexOf(",", startIndex);
                    if (endIndex == -1) {
                        endIndex = jsonResponse.indexOf("}", startIndex);
                    }
                    
                    if (startIndex > 0 && endIndex > 0) {
                        String floatStr = jsonResponse.substring(startIndex, endIndex).trim();
                        return Double.parseDouble(floatStr);
                    }
                }
                
                return -1;
            } else if (responseCode == 429) {
                // Handle rate limiting by waiting longer next time
                System.out.println("Rate limit hit with Float DB API for float value. Backing off.");
                return -1;
            } else {
                System.out.println("Error connecting to Float DB API for float value: HTTP " + responseCode);
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Error getting float value from Float DB API: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Helper method to construct a market hash name for a skin
     */
    private String constructMarketHashName(String weaponType, String skinName, String condition, boolean isStatTrak) {
        String hashName = weaponType + " | " + skinName + " (" + condition + ")";
        if (isStatTrak) {
            hashName = "StatTrak™ " + hashName;
        }
        return hashName;
    }
    
    /**
     * Applies rate limiting to avoid 429 errors
     */
    private void applyRateLimit() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRequestTime;
        
        if (lastRequestTime > 0 && elapsedTime < RATE_LIMIT_BACKOFF_MS) {
            try {
                long sleepTime = RATE_LIMIT_BACKOFF_MS - elapsedTime;
                System.out.println("Rate limiting: Sleeping for " + sleepTime + "ms before next request");
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
