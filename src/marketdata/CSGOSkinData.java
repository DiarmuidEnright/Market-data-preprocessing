package marketdata;

public class CSGOSkinData extends MarketData {
    // private defs of the different values to be defined in the db abd then returned
    private String weaponType;     // e.g., "AK-47", "M4A4", "Knife"
    private String skinName;       // e.g., "Asiimov", "Dragon Lore"
    private String condition;      // e.g., "Factory New", "Field-Tested"
    private boolean isStatTrak;    // Whether the skin has StatTrak™
    private String rarity;         // e.g., "Consumer Grade", "Covert"
    private String wear;           // Float value category (0-1)
    private double floatValue;     // Exact float value

    public CSGOSkinData(String symbol, double price, long timestamp,
                       String weaponType, String skinName, String condition,
                       boolean isStatTrak, String rarity, String wear,
                       double floatValue) {
        //declaring the values, this is super so that the values are accessable
        // this is the same as out init
        super(symbol, price, timestamp);
        this.weaponType = weaponType;
        this.skinName = skinName;
        this.condition = condition;
        this.isStatTrak = isStatTrak;
        this.rarity = rarity;
        this.wear = wear;
        this.floatValue = floatValue;
    }

    // Getters
    public String getWeaponType() { return weaponType; }
    public String getSkinName() { return skinName; }
    public String getCondition() { return condition; }
    public boolean isStatTrak() { return isStatTrak; }
    public String getRarity() { return rarity; }
    public String getWear() { return wear; }
    public double getFloatValue() { return floatValue; }

    // Setters
    public void setWeaponType(String weaponType) { this.weaponType = weaponType; }
    public void setSkinName(String skinName) { this.skinName = skinName; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setStatTrak(boolean statTrak) { isStatTrak = statTrak; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public void setWear(String wear) { this.wear = wear; }
    public void setFloatValue(double floatValue) { this.floatValue = floatValue; }

    @Override
    public String toString() {
        //format and then return the values as shown in the def
        // cleanup the function calls in the future
        return String.format("%s | %s (%s) %s | Wear: %.4f | Price: $%.2f",
            weaponType,
            skinName,
            condition,
            isStatTrak ? "StatTrak™" : "",
            floatValue,
            getPrice());
    }
}
