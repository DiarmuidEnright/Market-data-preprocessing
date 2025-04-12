package marketdata;

import java.util.Scanner;

public class MarketSystemRunner {
    private static final Scanner scanner = new Scanner(System.in);
    // scanning the main for the choice from the user as input
    public static void main(String[] args) {
        try {
            while (true) {
                displayMainMenu();
                // take the choice at the input here
                int choice = getUserChoice();
                
                if (choice == 0) {
                    // break if the value is not one of these
                    break;
                }
                // run through the user choice
                processChoice(choice);
            }
            
            System.out.println("Exiting Market System...");
            
        } catch (Exception e) {
            // return the error if something is wrong
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // close the scanner
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
                // move to the next line and then trim the input
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    displayMainMenu();
                    // move to the next if it exists
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
                // print the error message to the user
                System.out.println("Invalid choice. Please try again.");
        }
    }
}
