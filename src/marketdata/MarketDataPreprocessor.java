package marketdata;
import java.util.ArrayList;
import java.util.List;
public class MarketDataPreprocessor {
    public List<MarketData> preprocess(String rawData) {
        List<MarketData> list = new ArrayList<>();
        String[] lines = rawData.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length != 3) continue;
            try {
                String symbol = parts[0].trim();
                double price = Double.parseDouble(parts[1].trim());
                long timestamp = Long.parseLong(parts[2].trim());
                list.add(new MarketData(symbol, price, timestamp));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return list;
    }
    public static void main(String[] args) {
        MarketDataPreprocessor preprocessor = new MarketDataPreprocessor();
        System.out.println("Preprocessing complete.");
    }
}
