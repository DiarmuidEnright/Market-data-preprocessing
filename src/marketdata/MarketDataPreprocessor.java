package marketdata;
import java.util.ArrayList;
import java.util.List;
public class MarketDataPreprocessor {
    public List<MarketData> preprocess(String rawData) {
        List<MarketData> list = new ArrayList<>();
        String[] lines = rawData.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            //splitting on the comma because the normal market data should be in the following format
            //TICKER, PRICE, TIME; if it is not in is format then soemthing is wrong and we need catch the exception
            if (parts.length != 3) continue;
            try {
                //attempting to fix the error since after the split the count of parts is wrong eg: !=3 and does not match the expected format
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
