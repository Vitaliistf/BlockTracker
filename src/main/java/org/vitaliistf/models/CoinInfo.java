package org.vitaliistf.models;

import com.google.gson.annotations.SerializedName;
import org.vitaliistf.util.PrintFormatter;

public class CoinInfo {
    @SerializedName("id")
    private String id;
    @SerializedName("symbol")
    private String symbol;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("current_price")
    private double currentPrice;
    @SerializedName("market_cap")
    private long marketCap;
    @SerializedName("market_cap_rank")
    private int marketCapRank;
    @SerializedName("fully_diluted_valuation")
    private long fullyDilutedValuation;
    @SerializedName("total_volume")
    private double totalVolume;
    @SerializedName("high_24h")
    private double high24h;
    @SerializedName("low_24h")
    private double low24h;
    @SerializedName("price_change_24h")
    private double priceChange24h;
    @SerializedName("price_change_percentage_24h")
    private double priceChangePercentage24h;
    @SerializedName("market_cap_change_24h")
    private double marketCapChange24h;
    @SerializedName("market_cap_change_percentage_24h")
    private double marketCapChangePercentage24h;
    @SerializedName("circulating_supply")
    private double circulatingSupply;
    @SerializedName("total_supply")
    private double totalSupply;
    @SerializedName("max_supply")
    private double maxSupply;
    @SerializedName("ath")
    private double ath;
    @SerializedName("ath_change_percentage")
    private double athChangePercentage;
    @SerializedName("ath_date")
    private String athDate;
    @SerializedName("atl")
    private double atl;
    @SerializedName("atl_change_percentage")
    private double atlChangePercentage;
    @SerializedName("atl_date")
    private String atlDate;
    @SerializedName("roi")
    private Object roi;
    @SerializedName("last_update")
    private String lastUpdate;

    public String getImage() {
        return image;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public String toString() {

        return "\uD83E\uDE99" +
                name.toUpperCase() +
                ":\n" +
                "\n•Rank: " + marketCapRank +
                "\n•Symbol: " + symbol.toUpperCase() +
                "\n•Current price: " + PrintFormatter.formatNumber(currentPrice) +
                "\n•Market cap: " + marketCap +
                "\n•Fully diluted valuation: " + fullyDilutedValuation +
                "\n•Total volume: " + PrintFormatter.getFullLengthNumber(totalVolume) +
                "\n•High 24h: " + PrintFormatter.getFullLengthNumber(high24h) +
                "\n•Low 24h: " + PrintFormatter.getFullLengthNumber(low24h) +
                "\n•Price change 24h: " + PrintFormatter.getFullLengthNumber(priceChange24h) +
                "\n•Price change percentage 24h: " + PrintFormatter.formatPercent(priceChangePercentage24h) +
                "\n•Market cap change 24h: " + PrintFormatter.getFullLengthNumber(marketCapChange24h) +
                "\n•Market cap change percentage 24h: " + PrintFormatter.formatPercent(marketCapChangePercentage24h) +
                "\n•Circulating supply: " + PrintFormatter.formatNumber(circulatingSupply) +
                "\n•Total supply: " + PrintFormatter.formatNumber(totalSupply) +
                "\n•Max supply: " + PrintFormatter.formatNumber(maxSupply) +
                "\n•ATH: " + PrintFormatter.formatNumber(ath) +
                "\n•ATH change percentage: " + PrintFormatter.formatPercent(athChangePercentage) +
                "\n•ATH date: " + PrintFormatter.formatDate(athDate) +
                "\n•ATL: " + PrintFormatter.formatNumber(atl) +
                "\n•ATL: change percentage: " + PrintFormatter.formatPercent(atl) +
                "%\n•ATL date: " + PrintFormatter.formatDate(atlDate);
    }

    public String generalToString() {
        return PrintFormatter.formatString(Integer.toString(marketCapRank), 5) +
                PrintFormatter.formatString(symbol.toUpperCase(), 7) +
                PrintFormatter.formatNumber(currentPrice) +
                PrintFormatter.formatPercent(priceChangePercentage24h);
    }
}
