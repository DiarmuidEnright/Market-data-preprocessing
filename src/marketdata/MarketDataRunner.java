package marketdata;
public class MarketDataRunner {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java MarketDataRunner <command> [arguments...]");
            System.out.println("Commands:");
            System.out.println("  workflow <rawDataFilePath>");
            System.out.println("  processor <rawDataFilePath>");
            System.out.println("  incremental <baseDataFilePath> <incrementDataFilePath>");
            System.out.println("  advanced <rawDataFilePath> <emaPeriod>");
            System.out.println("  writer <rawDataFilePath> <outputDirectory>");
            System.out.println("  cleaner <rawDataFilePath> <outputDirectory>");
            return;
        }
        String command = args[0];
        switch (command.toLowerCase()) {
            case "workflow":
                if (args.length < 2) {
                    System.out.println("Usage: workflow <rawDataFilePath>");
                    return;
                }
                MarketDataWorkflow.main(new String[] { args[1] });
                break;
            case "processor":
                if (args.length < 2) {
                    System.out.println("Usage: processor <rawDataFilePath>");
                    return;
                }
                MarketDataProcessor.main(new String[] { args[1] });
                break;
            case "incremental":
                if (args.length < 3) {
                    System.out.println("Usage: incremental <baseDataFilePath> <incrementDataFilePath>");
                    return;
                }
                MarketDataIncrementalProcessor.main(new String[] { args[1], args[2] });
                break;
            case "advanced":
                if (args.length < 3) {
                    System.out.println("Usage: advanced <rawDataFilePath> <emaPeriod>");
                    return;
                }
                AdvancedMarketDataModule.main(new String[] { args[1], args[2] });
                break;
            case "writer":
                if (args.length < 3) {
                    System.out.println("Usage: writer <rawDataFilePath> <outputDirectory>");
                    return;
                }
                CleanedMarketDataWriter.main(new String[] { args[1], args[2] });
                break;
            case "cleaner":
                if (args.length < 3) {
                    System.out.println("Usage: cleaner <rawDataFilePath> <outputDirectory>");
                    return;
                }
                AdvancedMarketDataCleaner.main(new String[] { args[1], args[2] });
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }
}
