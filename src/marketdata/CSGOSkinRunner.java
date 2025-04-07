package marketdata;

import java.util.List;
import java.util.Scanner;
//random commit counter: 1
public class CSGOSkinRunner {
    //for now we will have the db empty just to make sure everything is functional
    private static final String DB_PATH = "marketdata.db";
    private static CSGOSkinRepository repository;
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            //init the repo and the scanner to take inputs for the Runner
            repository = new CSGOSkinRepository(DB_PATH);
            repository.initialize();
            scanner = new Scanner(System.in);
            
            while (true) {
                displayMenu();
                int choice = getUserChoice();
                
                if (choice == 0) {
                    break;
                }
                
                processChoice(choice);
            }
            
            System.out.println("Exiting CSGO Skin Data Manager...");
            repository.close();
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void displayMenu() {
        //formatted the same as the stock data for consistency
        System.out.println("\n===== CSGO SKIN DATA MANAGER =====");
        System.out.println("1. Add new skin data");
        System.out.println("2. View all skins");
        System.out.println("3. View skins by weapon type");
        System.out.println("4. View skins by rarity");
        System.out.println("5. View StatTrak™ skins");
        System.out.println("6. View average prices by weapon type");
        System.out.println("0. Exit");
        System.out.println("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private static void processChoice(int choice) throws Exception {
        switch (choice) {
            case 1:
                addNewSkinData();
                break;
            case 2:
                viewAllSkins();
                break;
            case 3:
                viewSkinsByWeaponType();
                break;
            case 4:
                viewSkinsByRarity();
                break;
            case 5:
                viewStatTrakSkins();
                break;
            case 6:
                viewAveragePricesByWeaponType();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private static void addNewSkinData() throws Exception {
        System.out.println("Enter skin details:");
        
        System.out.print("Weapon Type (e.g., AK-47, M4A4): ");
        String weaponType = scanner.nextLine();
        
        System.out.print("Skin Name (e.g., Asiimov, Dragon Lore): ");
        String skinName = scanner.nextLine();
        
        System.out.print("Condition (Factory New, Minimal Wear, Field-Tested, Well-Worn, Battle-Scarred): ");
        String condition = scanner.nextLine();
        
        System.out.print("Is StatTrak™? (yes/no): ");
        boolean isStatTrak = scanner.nextLine().toLowerCase().startsWith("y");
        
        System.out.print("Rarity (Consumer Grade, Industrial Grade, Mil-Spec, Restricted, Classified, Covert): ");
        String rarity = scanner.nextLine();
        
        System.out.print("Wear (Float Value 0-1): ");
        double floatValue = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Price (USD): ");
        double price = Double.parseDouble(scanner.nextLine());
        
        String symbol = weaponType + "|" + skinName;
        long timestamp = System.currentTimeMillis() / 1000;
        String wear = getWearCategory(floatValue);
        
        CSGOSkinData skinData = new CSGOSkinData(symbol, price, timestamp,
            weaponType, skinName, condition, isStatTrak, rarity, wear, floatValue);
            
        repository.saveSkinData(skinData);
        System.out.println("Skin data saved successfully!");
    }
    
    private static void viewAllSkins() throws Exception {
        List<CSGOSkinData> skins = repository.getAllSkins();
        displaySkins(skins);
    }
    
    private static void viewSkinsByWeaponType() throws Exception {
        System.out.print("Enter weapon type: ");
        String weaponType = scanner.nextLine();
        List<CSGOSkinData> skins = repository.getSkinsByWeaponType(weaponType);
        displaySkins(skins);
    }
    
    private static void viewSkinsByRarity() throws Exception {
        System.out.print("Enter rarity: ");
        String rarity = scanner.nextLine();
        List<CSGOSkinData> skins = repository.getSkinsByRarity(rarity);
        displaySkins(skins);
    }
    
    private static void viewStatTrakSkins() throws Exception {
        List<CSGOSkinData> skins = repository.getStatTrakSkins();
        displaySkins(skins);
    }
    
    private static void viewAveragePricesByWeaponType() throws Exception {
        System.out.print("Enter weapon type: ");
        String weaponType = scanner.nextLine();
        double avgPrice = repository.getAveragePriceByWeaponType(weaponType);
        System.out.printf("Average price for %s skins: $%.2f%n", weaponType, avgPrice);
    }
    
    private static void displaySkins(List<CSGOSkinData> skins) {
        if (skins.isEmpty()) {
            System.out.println("No skins found.");
            return;
        }
        
        System.out.println("\nFound " + skins.size() + " skins:");
        System.out.println("----------------------------------------");
        for (CSGOSkinData skin : skins) {
            System.out.println(skin.toString());
        }
        System.out.println("----------------------------------------");
    }
    //static float cutoffs for now but in the future I want to make the cutoffs dependant on the skins that are entered and use the floatdb to check the float caps and min for each entry
    private static String getWearCategory(double floatValue) {
        if (floatValue <= 0.07) return "Factory New";
        if (floatValue <= 0.15) return "Minimal Wear";
        if (floatValue <= 0.37) return "Field-Tested";
        if (floatValue <= 0.44) return "Well-Worn";
        return "Battle-Scarred";
    }
}
