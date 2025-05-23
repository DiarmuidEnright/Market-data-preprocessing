package marketdata;
public class MarketData {
    //this is for the general format of the incoming data and should not change
    //need to add checking to make sure that the data matches this format in the future
    private String symbol;
    private double price;
    private long timestamp;
    public MarketData(String symbol, double price, long timestamp){
        //public def of the market data
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
    }
    // run the functions and return what is needed if something is returned
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setPrice(double price) { this.price = price; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
