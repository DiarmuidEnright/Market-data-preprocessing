package marketdata;

/**
 * Service for validating CSGO skins using multiple data sources
 * before they are added to the database
 */
public class SkinValidationService {
    private SteamAPIClient steamAPIClient;
    private FloatDBAPIClient floatDBAPIClient;
    private boolean requireBothApisForValidation = false;
    
    public SkinValidationService() {
        this.steamAPIClient = new SteamAPIClient();
        this.floatDBAPIClient = new FloatDBAPIClient();
    }
    
    /**
     * Set whether both APIs must succeed for validation to pass
     * @param required True if both APIs must validate, false if either is sufficient
     */
    public void setRequireBothApisForValidation(boolean required) {
        this.requireBothApisForValidation = required;
    }
    
    /**
     * Validates a skin using both Steam API and Float DB API
     * 
     * @param weaponType The weapon type (e.g., "AK-47", "M4A4")
     * @param skinName The skin name (e.g., "Asiimov", "Dragon Lore")
     * @param condition The condition (e.g., "Factory New", "Field-Tested")
     * @param isStatTrak Whether the skin is StatTrak™
     * @return true if the skin exists in either API, false otherwise
     */
    public boolean validateSkin(String weaponType, String skinName, String condition, boolean isStatTrak) {
        System.out.println("Validating skin: " + weaponType + " | " + skinName + " (" + condition + ")" + 
                          (isStatTrak ? " StatTrak™" : ""));
        
        // Check Steam API first
        boolean existsInSteam = steamAPIClient.validateSkinExists(weaponType, skinName, condition, isStatTrak);
        
        if (existsInSteam) {
            System.out.println("Skin validated via Steam API: " + weaponType + " | " + skinName);
            
            if (requireBothApisForValidation) {
                // If both APIs are required, continue checking Float DB
                boolean existsInFloatDB = floatDBAPIClient.validateSkinExists(weaponType, skinName, condition, isStatTrak);
                
                if (existsInFloatDB) {
                    System.out.println("Skin also validated via Float DB API: " + weaponType + " | " + skinName);
                    return true;
                } else {
                    System.out.println("Skin validation failed in Float DB API: " + weaponType + " | " + skinName);
                    return false;
                }
            } else {
                // If either API is sufficient, return true immediately
                return true;
            }
        }
        
        // If not found in Steam or if both APIs are required but Steam failed,
        // try Float DB API as a fallback
        boolean existsInFloatDB = floatDBAPIClient.validateSkinExists(weaponType, skinName, condition, isStatTrak);
        
        if (existsInFloatDB) {
            System.out.println("Skin validated via Float DB API: " + weaponType + " | " + skinName);
            return true;
        }
        
        System.out.println("Skin validation failed in both APIs: " + weaponType + " | " + skinName);
        return false;
    }
    
    /**
     * Get a float value for a skin using its inspect link
     * 
     * @param inspectLink The Steam inspect link for the skin
     * @return The float value or -1 if not found
     */
    public double getFloatValue(String inspectLink) {
        return floatDBAPIClient.getFloatValue(inspectLink);
    }
}