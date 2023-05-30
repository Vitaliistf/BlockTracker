package org.vitaliistf.models;

import org.vitaliistf.services.CoinService;
import org.vitaliistf.util.PrintFormatter;

import java.util.List;

public class Portfolio {
    private final String id;
    private final long userId;
    private final String name;
    private final List<Coin> coins;

    public Portfolio(long userId, String name, List<Coin> coins) {
        this.id = userId + name;
        this.userId = userId;
        this.name = name;
        this.coins = coins;
    }

    public String getName() {
        return name;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public long getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public double getPortfolioValue() {
        double result = 0;

        for(Coin coin : coins) {
            result += coin.getValue();
        }

        return result;
    }

    public double getPNL() {
        double result = 0;
        for(Coin coin : coins) {
            result += coin.getPNL();
        }
        return result;
    }

    public double getPNLPercent() {
        return (getPNL() / getInvested()) * 100;
    }

    @Override
    public String toString() {
        List<Coin> coinList = new CoinService().getCoinsByPortfolioId(id);
        if(coinList.isEmpty()) {
            return "\nYou don't have any coin here.";
        } else {
            StringBuilder result = new StringBuilder();
            result.append("<pre>\n")
                    .append("  ")
                    .append(PrintFormatter.formatString("Coin", 6))
                    .append(PrintFormatter.formatString("Amount", 9))
                    .append(PrintFormatter.formatString("Value", 14))
                    .append(PrintFormatter.formatString("PNL%", 6))
                    .append("\n");

            for (Coin coin : coinList) {
                result.append("\n").append(coin);
            }
            result.append("</pre>")
                    .append("\n\nPNL: ")
                    .append(PrintFormatter.formatCurrency(getPNL()))
                    .append("\nInvested: ")
                    .append(PrintFormatter.formatCurrency(getInvested()));
            return result.toString();
        }
    }

    public double getInvested() {
        double result = 0;
        for(Coin coin : coins) {
            result += coin.getInvested();
        }
        return result;
    }

    public String generalToString() {
        return "\uD83D\uDCBC" +
                PrintFormatter.formatString(name, 11) +
                PrintFormatter.formatCurrencyWithOverflow(getPortfolioValue()) +
                PrintFormatter.formatPercent(getPNLPercent());
    }
}
