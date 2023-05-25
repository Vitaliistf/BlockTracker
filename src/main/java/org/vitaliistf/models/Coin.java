package org.vitaliistf.models;

import org.vitaliistf.util.PrintFormatter;
import org.vitaliistf.coingecko.CG;

public class Coin {
    private final String id;
    private final String symbol;
    private final double amount;
    private final String portfolioId;
    private final double avgBuyingPrice;

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol.toUpperCase();
    }

    public double getAmount() {
        return amount;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public double getAvgBuyingPrice() {
        return avgBuyingPrice;
    }

    public Coin(String id, String symbol, double amount, String portfolioId, double avgBuyingPrice) {
        this.id = id;
        this.symbol = symbol.toUpperCase();
        this.amount = amount;
        this.portfolioId = portfolioId;
        this.avgBuyingPrice = avgBuyingPrice;
    }

    public Coin(String symbol, double amount, String portfolioId, double avgBuyingPrice) {
        this.id = portfolioId + symbol;
        this.symbol = symbol.toUpperCase();
        this.amount = amount;
        this.portfolioId = portfolioId;
        this.avgBuyingPrice = avgBuyingPrice;
    }

    public double getValue() {
        return amount * CG.getPriceUSD(symbol);
    }

    public double getPNL() {
        return (CG.getPriceUSD(symbol) - avgBuyingPrice) * amount;
    }

    public double getPNLPercent() {
        return (getPNL()/ getValue())*100;
    }

    public double getInvested() {
        return amount * avgBuyingPrice;
    }

    @Override
    public String toString() {
        return "\uD83D\uDD38" +
                PrintFormatter.formatString(symbol, 6) +
                PrintFormatter.formatNumber(amount) +
                PrintFormatter.formatNumber(getValue()) +
                "USD  " +
                PrintFormatter.formatPercent(getPNLPercent());
    }
}
