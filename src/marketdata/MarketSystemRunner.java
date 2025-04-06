package marketdata;

import java.util.Scanner;

public class MarketSystemRunner {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            while (true) {
                displayMainMenu();
                int choice = getUserChoice();
                
                if (choice == 0) {
                    break;
                }
                
                processChoice(choice);
            }
            
            System.out.println("Exiting Market System...");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static void displayMainMenu() {
        System.out.println("\n===== MARKET DATA SYSTEM =====");
        System.out.println("1. Stock Market Data Management");
        System.out.println("2. CSGO Skin Market Management");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        while (scanner.hasNextLine()) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    displayMainMenu();
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                displayMainMenu();
            }
        }
        return 0;
    }
    
    private static void processChoice(int choice) throws Exception {
        switch (choice) {
            case 1:
                // Run stock market data system
                MarketDataRunner.main(new String[0]);
                break;
            case 2:
                // Run CSGO skin market system
                CSGOSkinRunner.main(new String[0]);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
}
