package marketdata;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class MarketDataProcessor {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java MarketDataProcessor <rawDataFilePath>");
            return;
        }
        String rawData = "";
        try {
            rawData = new String(Files.readAllBytes(Paths.get(args[0])));
        } catch (IOException e) {
            System.out.println("Error reading file");
            return;
        }
        MarketDataPreprocessor preprocessor = new MarketDataPreprocessor();
        List<MarketData> data = preprocessor.preprocess(rawData);
        Map<String, Double> avgPrices = data.stream().collect(Collectors.groupingBy(MarketData::getSymbol, Collectors.averagingDouble(MarketData::getPrice)));
        avgPrices.forEach((k,v) -> System.out.println(k + ": " + v));
    }
}
