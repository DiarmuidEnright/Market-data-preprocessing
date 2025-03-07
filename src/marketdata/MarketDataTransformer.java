package marketdata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MarketDataTransformer {
    public static void main(String[] args) {
        MarketDataTransformer transformer = new MarketDataTransformer();
        System.out.println("Transformation complete.");
    }
    public Map<String, List<Double>> getMovingAverage(List<MarketData> data, int window) {
        Map<String, List<MarketData>> grouped = new HashMap<>();
        for (MarketData md : data) {
            grouped.computeIfAbsent(md.getSymbol(), k -> new ArrayList<>()).add(md);
        }
        Map<String, List<Double>> movingAverages = new HashMap<>();
        for (Map.Entry<String, List<MarketData>> entry : grouped.entrySet()) {
            List<MarketData> list = entry.getValue();
            Collections.sort(list, Comparator.comparingLong(MarketData::getTimestamp));
            List<Double> averages = new ArrayList<>();
            for (int i = 0; i <= list.size() - window; i++) {
                double sum = 0;
                for (int j = i; j < i + window; j++) {
                    sum += list.get(j).getPrice();
                }
                averages.add(sum / window);
            }
            movingAverages.put(entry.getKey(), averages);
        }
        return movingAverages;
    }
    public Map<String, List<MarketData>> resampleByInterval(List<MarketData> data, long interval) {
        Map<String, List<MarketData>> grouped = new HashMap<>();
        for (MarketData md : data) {
            grouped.computeIfAbsent(md.getSymbol(), k -> new ArrayList<>()).add(md);
        }
        Map<String, List<MarketData>> resampled = new HashMap<>();
        for (Map.Entry<String, List<MarketData>> entry : grouped.entrySet()) {
            List<MarketData> list = entry.getValue();
            Collections.sort(list, Comparator.comparingLong(MarketData::getTimestamp));
            List<MarketData> sampled = new ArrayList<>();
            long currentIntervalStart = list.get(0).getTimestamp();
            double sum = 0;
            int count = 0;
            for (MarketData md : list) {
                if (md.getTimestamp() - currentIntervalStart < interval) {
                    sum += md.getPrice();
                    count++;
                } else {
                    sampled.add(new MarketData(md.getSymbol(), sum / count, currentIntervalStart));
                    currentIntervalStart = md.getTimestamp();
                    sum = md.getPrice();
                    count = 1;
                }
            }
            if (count > 0) sampled.add(new MarketData(entry.getKey(), sum / count, currentIntervalStart));
            resampled.put(entry.getKey(), sampled);
        }
        return resampled;
    }
}
