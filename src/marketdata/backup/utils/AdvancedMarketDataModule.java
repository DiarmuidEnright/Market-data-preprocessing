package marketdata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
public class AdvancedMarketDataModule {
    private MarketDataPreprocessor preprocessor;
    private MarketDataCleaner cleaner;
    public AdvancedMarketDataModule(){
        //calling the cleaning and processing modules
        preprocessor = new MarketDataPreprocessor();
        cleaner = new MarketDataCleaner();
    }
    public List<MarketData> processRawData(String rawData){
        // preprocessing the raw data from the source
        List<MarketData> list = preprocessor.preprocess(rawData);
        //returning the cleaned data taken from the preprocesser
        return cleaner.cleanData(list);
    }
    public Map<String, List<Double>> calculateEMA(List<MarketData> data, int period){
        Map<String, List<MarketData>> groups = new HashMap<>();
        for(MarketData md : data){
            groups.computeIfAbsent(md.getSymbol(), k -> new ArrayList<>()).add(md);
        }
        Map<String, List<Double>> emaResult = new HashMap<>();
        double multiplier = 2.0 / (period + 1);
        for(Map.Entry<String, List<MarketData>> entry : groups.entrySet()){
            List<MarketData> list = entry.getValue();
            Collections.sort(list, Comparator.comparingLong(MarketData::getTimestamp));
            List<Double> emaList = new ArrayList<>();
            double ema = list.get(0).getPrice();
            emaList.add(ema);
            for(int i = 1; i < list.size(); i++){
                double price = list.get(i).getPrice();
                ema = (price - ema) * multiplier + ema;
                emaList.add(ema);
            }
            emaResult.put(entry.getKey(), emaList);
        }
        return emaResult;
    }
    public Map<String, String> generateTradeSignals(List<MarketData> data, int period){
        Map<String, List<MarketData>> groups = new HashMap<>();
        for(MarketData md : data){
            groups.computeIfAbsent(md.getSymbol(), k -> new ArrayList<>()).add(md);
        }
        Map<String, String> signals = new HashMap<>();
        double multiplier = 2.0 / (period + 1);
        for(Map.Entry<String, List<MarketData>> entry : groups.entrySet()){
            List<MarketData> list = entry.getValue();
            Collections.sort(list, Comparator.comparingLong(MarketData::getTimestamp));
            double ema = list.get(0).getPrice();
            for(int i = 1; i < list.size(); i++){
                double price = list.get(i).getPrice();
                ema = (price - ema) * multiplier + ema;
            }
            double lastPrice = list.get(list.size() - 1).getPrice();
            signals.put(entry.getKey(), lastPrice > ema ? "BUY" : "SELL");
        }
        return signals;
    }
    public static void main(String[] args){
        if(args.length < 2){
            System.out.println("Usage: java AdvancedMarketDataModule <rawDataFilePath> <emaPeriod>");
            return;
        }
        String rawData = "";
        try{
            rawData = new String(Files.readAllBytes(Paths.get(args[0])));
        }catch(Exception e){
            System.out.println("Error reading file");
            return;
        }
        int period = Integer.parseInt(args[1]);
        AdvancedMarketDataModule module = new AdvancedMarketDataModule();
        List<MarketData> data = module.processRawData(rawData);
        Map<String, List<Double>> ema = module.calculateEMA(data, period);
        Map<String, String> signals = module.generateTradeSignals(data, period);
        ema.forEach((k, v) -> System.out.println(k + " EMA: " + v.get(v.size()-1)));
        signals.forEach((k, v) -> System.out.println(k + " Signal: " + v));
    }
}
