package marketdata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class AdvancedMarketDataCleaner {
    private MarketDataCleaner baseCleaner;
    public AdvancedMarketDataCleaner(){
        baseCleaner = new MarketDataCleaner();
    }
    public List<MarketData> fullCleanData(List<MarketData> data){
        data = imputeMissingValues(data);
        data = baseCleaner.removeDuplicates(data);
        data = baseCleaner.filterInvalidEntries(data);
        data = baseCleaner.filterOutliers(data, 2.0);
        data = standardizeData(data);
        data = alignTime(data);
        return data;
    }
    public List<MarketData> imputeMissingValues(List<MarketData> data){
        Map<String, Double> avgMap = data.stream()
            .filter(md -> md.getPrice() > 0 && md.getSymbol() != null && !md.getSymbol().isEmpty())
            .collect(Collectors.groupingBy(MarketData::getSymbol, Collectors.averagingDouble(MarketData::getPrice)));
        for(MarketData m : data){
            if(m.getSymbol() == null || m.getSymbol().isEmpty()){
                m.setSymbol("UNKNOWN");
            }
            if(m.getPrice() <= 0){
                Double avg = avgMap.get(m.getSymbol());
                if(avg != null){
                    m.setPrice(avg);
                }
            }
        }
        return data;
    }
    public List<MarketData> standardizeData(List<MarketData> data){
        for(MarketData m : data){
            if(m.getSymbol() != null){
                m.setSymbol(m.getSymbol().trim().toUpperCase());
            }
        }
        return data;
    }
    public List<MarketData> alignTime(List<MarketData> data){
        data.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
        return data;
    }
    public List<MarketData> detectAnomalies(List<MarketData> data, double zThreshold){
        Map<String, List<MarketData>> groups = data.stream().collect(Collectors.groupingBy(MarketData::getSymbol));
        List<MarketData> anomalies = new ArrayList<>();
        for(Map.Entry<String, List<MarketData>> entry : groups.entrySet()){
            List<MarketData> list = entry.getValue();
            double avg = list.stream().mapToDouble(MarketData::getPrice).average().orElse(0);
            double std = Math.sqrt(list.stream().mapToDouble(md -> Math.pow(md.getPrice() - avg, 2)).average().orElse(0));
            for(MarketData md : list){
                if(std > 0 && Math.abs(md.getPrice() - avg) / std > zThreshold){
                    anomalies.add(md);
                }
            }
        }
        return anomalies;
    }
    public static void main(String[] args){
        if(args.length < 2){
            System.out.println("Usage: java AdvancedMarketDataCleaner <rawDataFilePath> <outputDirectory>");
            return;
        }
        String rawData = "";
        try{
            rawData = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[0])));
        }catch(Exception e){
            System.out.println("Error reading file");
            return;
        }
        MarketDataPreprocessor preprocessor = new MarketDataPreprocessor();
        List<MarketData> data = preprocessor.preprocess(rawData);
        AdvancedMarketDataCleaner cleaner = new AdvancedMarketDataCleaner();
        List<MarketData> cleaned = cleaner.fullCleanData(data);
        List<MarketData> anomalies = cleaner.detectAnomalies(cleaned, 3.0);
        System.out.println("Cleaned data count: " + cleaned.size());
        System.out.println("Anomaly count: " + anomalies.size());
    }
}
