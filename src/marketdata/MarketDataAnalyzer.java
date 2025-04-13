package marketdata;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MarketDataAnalyzer {
    private List<MarketData> data;
    
    public MarketDataAnalyzer(List<MarketData> data) {
        this.data = data;
    }
    
    public MarketDataAnalyzer() {
        this(new ArrayList<>());
    }
    
    public void analyze() {
        System.out.println("No data to analyze.");
    }
    
    public Map<String, Double> getAveragePrice() {
        return data.stream().collect(
            Collectors.groupingBy(
                MarketData::getSymbol,
                Collectors.averagingDouble(MarketData::getPrice)
            )
        );
    }
    
    public Map<String, Double> getMinPrice() {
        return data.stream().collect(
            Collectors.groupingBy(
                MarketData::getSymbol,
                Collectors.collectingAndThen(
                    Collectors.minBy((a, b) -> Double.compare(a.getPrice(), b.getPrice())),
                    opt -> opt.get().getPrice()
                )
            )
        );
    }
    
    public Map<String, Double> getMaxPrice() {
        return data.stream().collect(
            Collectors.groupingBy(
                MarketData::getSymbol,
                Collectors.collectingAndThen(
                    Collectors.maxBy((a, b) -> Double.compare(a.getPrice(), b.getPrice())),
                    opt -> opt.get().getPrice()
                )
            )
        );
    }
    
    public Map<String, Double> getStandardDeviation() {
        Map<String, List<Double>> pricesBySymbol = new HashMap<>();
        for (MarketData md : data) {
            pricesBySymbol.computeIfAbsent(md.getSymbol(), k -> new ArrayList<>()).add(md.getPrice());
        }
        
        Map<String, Double> stddev = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : pricesBySymbol.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(d -> d).average().orElse(0.0);
            double variance = entry.getValue().stream().mapToDouble(d -> Math.pow(d - avg, 2)).average().orElse(0.0);
            stddev.put(entry.getKey(), Math.sqrt(variance));
        }
        return stddev;
    }
    
    public Map<String, Long> countEntries() {
        return data.stream().collect(
            Collectors.groupingBy(
                MarketData::getSymbol,
                Collectors.counting()
            )
        );
    }
}
