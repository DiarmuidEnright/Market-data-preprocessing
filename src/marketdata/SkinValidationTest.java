package marketdata;

/**
 * Test class for validating the skin validation functionality
 */
public class SkinValidationTest {
    public static void main(String[] args) {
        System.out.println("Starting CSGO Skin Validation Test");
        
        // Initialize services
        SkinValidationService validationService = new SkinValidationService();
        
        // Test with likely existing skins
        testSkinValidation(validationService, "AK-47", "Asiimov", "Field-Tested", false);
        testSkinValidation(validationService, "AWP", "Dragon Lore", "Factory New", false);
        testSkinValidation(validationService, "M4A4", "Howl", "Minimal Wear", false);
        testSkinValidation(validationService, "Desert Eagle", "Blaze", "Factory New", false);
        
        // Test with StatTrak variants
        testSkinValidation(validationService, "AK-47", "Redline", "Field-Tested", true);
        
        // Test with likely non-existing skins
        testSkinValidation(validationService, "Fake Weapon", "Fake Skin", "Factory New", false);
        testSkinValidation(validationService, "AK-47", "NonExistentSkin", "Factory New", false);
        
        System.out.println("Skin Validation Test Completed");
    }
    
    private static void testSkinValidation(SkinValidationService service, 
                                         String weaponType, 
                                         String skinName, 
                                         String condition, 
                                         boolean isStatTrak) {
        System.out.println("\nTesting skin: " + weaponType + " | " + skinName + 
                          " (" + condition + ")" + (isStatTrak ? " StatTrak™" : ""));
        boolean result = service.validateSkin(weaponType, skinName, condition, isStatTrak);
        System.out.println("Validation result: " + (result ? "VALID" : "INVALID"));
    }
}