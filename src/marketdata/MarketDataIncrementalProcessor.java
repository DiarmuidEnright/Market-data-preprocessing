package marketdata;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class MarketDataIncrementalProcessor {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java MarketDataIncrementalProcessor <baseDataFilePath> <incrementDataFilePath>");
            return;
        }
        String baseData = "";
        String incData = "";
        try {
            baseData = new String(Files.readAllBytes(Paths.get(args[0])));
            incData = new String(Files.readAllBytes(Paths.get(args[1])));
        } catch (IOException e) {
            System.out.println("Error reading files");
            return;
        }
        MarketDataPreprocessor preprocessor = new MarketDataPreprocessor();
        List<MarketData> baseList = preprocessor.preprocess(baseData);
        List<MarketData> incList = preprocessor.preprocess(incData);
        baseList.addAll(incList);
        Map<String, Double> avgPrices = baseList.stream().collect(Collectors.groupingBy(MarketData::getSymbol, Collectors.averagingDouble(MarketData::getPrice)));
        avgPrices.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
