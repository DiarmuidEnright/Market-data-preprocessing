package marketdata;

import java.util.ArrayList;
import java.util.List;

public class SampleCSGOData {
    public static void main(String[] args) {
        try {
            CSGOSkinRepository repository = new CSGOSkinRepository("marketdata.db");
            repository.initialize();
            
            long baseTimestamp = System.currentTimeMillis() / 1000;
            List<CSGOSkinData> sampleData = new ArrayList<>();
            
            // Add popular AWP skins
            sampleData.add(new CSGOSkinData(
                "AWP|Dragon Lore", 10500.00, baseTimestamp,
                "AWP", "Dragon Lore", "Factory New",
                false, "Covert", "Factory New", 0.01
            ));
            
            sampleData.add(new CSGOSkinData(
                "AWP|Asiimov", 85.50, baseTimestamp + 60,
                "AWP", "Asiimov", "Field-Tested",
                true, "Covert", "Field-Tested", 0.25
            ));
            
            // Add AK-47 skins
            sampleData.add(new CSGOSkinData(
                "AK-47|Wild Lotus", 4800.00, baseTimestamp + 120,
                "AK-47", "Wild Lotus", "Minimal Wear",
                false, "Covert", "Minimal Wear", 0.12
            ));
            
            sampleData.add(new CSGOSkinData(
                "AK-47|Redline", 15.80, baseTimestamp + 180,
                "AK-47", "Redline", "Field-Tested",
                true, "Classified", "Field-Tested", 0.22
            ));
            
            // Add M4A4 skins
            sampleData.add(new CSGOSkinData(
                "M4A4|Howl", 3200.00, baseTimestamp + 240,
                "M4A4", "Howl", "Factory New",
                false, "Contraband", "Factory New", 0.03
            ));
            
            sampleData.add(new CSGOSkinData(
                "M4A4|Neo-Noir", 45.60, baseTimestamp + 300,
                "M4A4", "Neo-Noir", "Minimal Wear",
                false, "Covert", "Minimal Wear", 0.11
            ));
            
            // Add knife skins
            sampleData.add(new CSGOSkinData(
                "Butterfly Knife|Doppler", 1250.00, baseTimestamp + 360,
                "Butterfly Knife", "Doppler", "Factory New",
                true, "Covert", "Factory New", 0.01
            ));
            
            sampleData.add(new CSGOSkinData(
                "Karambit|Fade", 1800.00, baseTimestamp + 420,
                "Karambit", "Fade", "Factory New",
                false, "Covert", "Factory New", 0.02
            ));
            
            // Add some cheaper skins
            sampleData.add(new CSGOSkinData(
                "USP-S|Kill Confirmed", 28.50, baseTimestamp + 480,
                "USP-S", "Kill Confirmed", "Well-Worn",
                false, "Covert", "Well-Worn", 0.42
            ));
            
            sampleData.add(new CSGOSkinData(
                "Glock-18|Fade", 850.00, baseTimestamp + 540,
                "Glock-18", "Fade", "Factory New",
                false, "Restricted", "Factory New", 0.01
            ));
            
            // Save all sample data
            for (CSGOSkinData skin : sampleData) {
                repository.saveSkinData(skin);
            }
            
            System.out.println("Successfully added " + sampleData.size() + " sample CSGO skins to the database.");
            repository.close();
            
        } catch (Exception e) {
            System.err.println("Error adding sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
