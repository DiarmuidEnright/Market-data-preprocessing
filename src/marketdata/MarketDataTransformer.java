package marketdata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MarketDataTransformer {
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
            //finding the moving average of all of the incoming data
            for (int i = 0; i <= list.size() - window; i++) {
                double sum = 0;
                for (int j = i; j < i + window; j++) {
                    //adding the price to the sum
                    sum += list.get(j).getPrice();
                }
                //adding the average (total sum/ amount of value in the window eg: window) to our averages ArrayList
                averages.add(sum / window);
            }
            //adding to the HashMap with the key being the default entry for the map and the values being the found averages
            movingAverages.put(entry.getKey(), averages);
        }
        //returning the HashMap
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
                    //comparing the interval for the pricing window
                    sum += md.getPrice();
                    count++;
                } else {
                    //adding the sample for count 1 of the price at some timestamp for the symbol
                    sampled.add(new MarketData(md.getSymbol(), sum / count, currentIntervalStart));
                    currentIntervalStart = md.getTimestamp();
                    sum = md.getPrice();
                    count = 1;
                }
            }
            if (count > 0) sampled.add(new MarketData(entry.getKey(), sum / count, currentIntervalStart));
            //adding the resampled data to our resamples HashMap using the default entry keys
            resampled.put(entry.getKey(), sampled);
        }
        //returning
        return resampled;
    }
}
