package marketdata;
public class MarketData {
    private String symbol;
    private double price;
    private long timestamp;
    public MarketData(String symbol, double price, long timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
    }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setPrice(double price) { this.price = price; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
