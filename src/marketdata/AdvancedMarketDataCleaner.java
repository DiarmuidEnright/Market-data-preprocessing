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
        //cleaning the data using the functions we defined earlier
        data = imputeMissingValues(data);
        data = baseCleaner.removeDuplicates(data);
        data = baseCleaner.filterInvalidEntries(data);
        data = baseCleaner.filterOutliers(data, 2.0);
        data = standardizeData(data);
        data = alignTime(data);
        //returning the data when it is cleaned
        // might add some regex in the future to make sure the returned data is in the correct format
        return data;
    }
    
    public List<MarketData> imputeMissingValues(List<MarketData> data){
        Map<String, Double> avgMap = data.stream()
            .filter(md -> md.getPrice() > 0 && md.getSymbol() != null && !md.getSymbol().isEmpty())
            .collect(Collectors.groupingBy(MarketData::getSymbol, Collectors.averagingDouble(MarketData::getPrice)));
        for(MarketData m : data){
            //replacing the values for cases where the tickers are missing
            if(m.getSymbol() == null || m.getSymbol().isEmpty()){
                m.setSymbol("UNKNOWN");
            }
            if(m.getPrice() <= 0){
                Double avg = avgMap.get(m.getSymbol());
                if(avg != null){
                    //setting the price to be the avg if the price called is for some reason negative to somewhat normlize the data we are working with
                    m.setPrice(avg);
                }
            }
        }
        return data;
    }
    
    public List<MarketData> standardizeData(List<MarketData> data){
        for(MarketData m : data){
            //cleaning the ticket a bit and normalizing it for consistency in the return statement
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
        //identifying the anoms in the market data and then adding them to md
        for(Map.Entry<String, List<MarketData>> entry : groups.entrySet()){
            List<MarketData> list = entry.getValue();
            //take the avg and the md values for the total market price  data that we have
            double avg = list.stream().mapToDouble(MarketData::getPrice).average().orElse(0);
            double std = Math.sqrt(list.stream().mapToDouble(md -> Math.pow(md.getPrice() - avg, 2)).average().orElse(0));
            for(MarketData md : list){
                //if the std of the data and the abs(price -avg) eg: the std of the value for that one price devided by the total std is > our threshhold then we add the data point to the set set of anoms
                if(std > 0 && Math.abs(md.getPrice() - avg) / std > zThreshold){
                    anomalies.add(md);
                }
            }
        }
        return anomalies;
    }
}
