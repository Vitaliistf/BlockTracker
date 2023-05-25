package org.vitaliistf.models;

import org.vitaliistf.util.PrintFormatter;

import java.time.LocalDate;

public class Transaction {

    private final long userId;
    private final String portfolioId;
    private final String symbol;
    private final double amount;
    private final double price;
    private final boolean isBuy;
    private final LocalDate date;

    public Transaction(long userId, String portfolioId, String symbol, double amount, double price, boolean isBuy, LocalDate date) {
        this.userId = userId;
        this.portfolioId = portfolioId;
        this.symbol = symbol;
        this.amount = amount;
        this.price = price;
        this.isBuy = isBuy;
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public String getSymbol() {
        return symbol.toUpperCase();
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return PrintFormatter.formatString(isBuy ? "\uD83D\uDFE2 Buy" : "\uD83D\uDD34 Sell", 9) +
                PrintFormatter.formatString(symbol, 7) +
                PrintFormatter.formatNumber(amount) +
                PrintFormatter.formatNumber(price) +
                PrintFormatter.formatDate(date.toString());
    }
}
