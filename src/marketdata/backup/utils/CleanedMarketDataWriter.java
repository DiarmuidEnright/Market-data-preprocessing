package marketdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CleanedMarketDataWriter {
    public void writeCleanedData(String rawDataFilePath, String outputDirectory) throws IOException {
        // Read raw data from file
        String rawData = new String(Files.readAllBytes(Paths.get(rawDataFilePath)));
        
        // Create output directory if it doesn't exist
        File outDir = new File(outputDirectory);
        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                throw new IOException("Unable to create output directory: " + outputDirectory);
            }
        }
        
        // Determine output file name; if cleanedData.txt exists, generate a new file name
        File outputFile = new File(outDir, "cleanedData.txt");
        int counter = 1;
        while (outputFile.exists()) {
            outputFile = new File(outDir, "cleanedData_" + counter + ".txt");
            counter++;
        }
        
        // Write the raw data to the output file (this can be modified to write actual cleaned data)
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(rawData);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java CleanedMarketDataWriter <rawDataFilePath> <outputDirectory>");
            return;
        }
        CleanedMarketDataWriter writer = new CleanedMarketDataWriter();
        try {
            writer.writeCleanedData(args[0], args[1]);
            System.out.println("Cleaned data written successfully.");
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}
