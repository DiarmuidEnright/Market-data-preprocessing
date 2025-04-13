package marketdata;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MarketDataCleaner {
    public List<MarketData> removeDuplicates(List<MarketData> data) {
        Set<String> seen = new HashSet<>();
        List<MarketData> cleaned = new ArrayList<>();
        for (MarketData md : data) {
            String key = md.getSymbol() + "_" + md.getTimestamp();
            if (!seen.contains(key)) {
                seen.add(key);
                cleaned.add(md);
            }
        }
        return cleaned;
    }
    
    public List<MarketData> filterInvalidEntries(List<MarketData> data) {
        return data.stream().filter(md -> md.getPrice() > 0).collect(Collectors.toList());
    }
    
    public List<MarketData> filterOutliers(List<MarketData> data, double threshold) {
        Map<String, List<MarketData>> groups = data.stream().collect(Collectors.groupingBy(MarketData::getSymbol));
        List<MarketData> result = new ArrayList<>();
        for (Map.Entry<String, List<MarketData>> entry : groups.entrySet()) {
            List<MarketData> list = entry.getValue();
            double avg = list.stream().mapToDouble(MarketData::getPrice).average().orElse(0);
            double std = Math.sqrt(list.stream().mapToDouble(md -> Math.pow(md.getPrice() - avg, 2)).average().orElse(0));
            for (MarketData md : list) {
                if (std != 0 && Math.abs(md.getPrice() - avg) > threshold * std) continue;
                result.add(md);
            }
        }
        return result;
    }
    
    public List<MarketData> cleanData(List<MarketData> data) {
        List<MarketData> cleaned = removeDuplicates(data);
        cleaned = filterInvalidEntries(cleaned);
        cleaned = filterOutliers(cleaned, 2.0);
        return cleaned;
    }
}
