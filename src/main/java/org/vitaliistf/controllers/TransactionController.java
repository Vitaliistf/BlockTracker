package org.vitaliistf.controllers;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.vitaliistf.dao.PortfolioDao;
import org.vitaliistf.dao.TransactionDao;
import org.vitaliistf.util.PrintFormatter;
import org.vitaliistf.coingecko.CG;
import org.vitaliistf.menu.Menu;
import org.vitaliistf.models.Coin;
import org.vitaliistf.models.Portfolio;
import org.vitaliistf.models.Transaction;
import org.vitaliistf.sessions.Session;
import org.vitaliistf.sessions.UserSessionManager;

import java.time.LocalDate;
import java.util.List;

public class TransactionController {

    public static String handleMessage(Message message, SendMessage sendMessage) {
        String request = message.getText();
        String response;
        long chatId = message.getChatId();
        if(request.startsWith(Menu.NEW_TRANSACTION_BUTTON)) {
            Session session = UserSessionManager.getSession(chatId);
            session.setAttribute("transactionPortfolioId", message.getChatId() + request.substring(19));
            session.setAttribute("command", "newTransaction");

            sendMessage.setReplyMarkup(Menu.chooseSellOrBuy(request.substring(19)));
            response = "\uD83D\uDFE2 Buy or sell?";

        } else if (request.startsWith(Menu.SHOW_TRANSACTIONS_BUTTON)) {
            response = showAllTransactions(message);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup(Menu.backButtonOnly());
        } else {
            response = "\uD83D\uDD34 Invalid input.";
        }

        return response;
    }

    private static String showAllTransactions(Message message) {
        StringBuilder result = new StringBuilder("Transactions in ");
        result.append(message.getText().substring(21))
                .append(":\n\n");
        TransactionDao transactionDao = new TransactionDao();
        List<Transaction> transactionList = transactionDao.getByPortfolioId(message.getChatId() +
                message.getText().substring(21));
        result.append("<pre>")
                .append(PrintFormatter.formatString("Buy/sell", 9))
                .append(PrintFormatter.formatString("Symbol", 7))
                .append(PrintFormatter.formatString("Amount", 9))
                .append(PrintFormatter.formatString("Price", 9))
                .append(PrintFormatter.formatString("Date", 10));

        for(Transaction transaction : transactionList) {
            result.append("\n").append(transaction);
        }
        result.append("</pre>");
        return result.toString();
    }

    public static String createNewTransaction(Message message) {
        long chatId = message.getChatId();
        String text = message.getText();
        Session session = UserSessionManager.getSession(chatId);

        if(session.getAttribute("transactionBuySell") == null) {
            if(!message.getText().startsWith("Buy") && !message.getText().startsWith("Sell")) {
                return "\uD83D\uDD34 Invalid input. Try again.";
            }
            session.setAttribute("transactionBuySell", text);
            return "\uD83D\uDFE2 Enter symbol:";

        } else if (session.getAttribute("transactionSymbol") == null) {
            if(CG.getPriceUSD(text) <= 0) {
                return "\uD83D\uDD34 This coin is not supported.";
            }
            session.setAttribute("transactionSymbol", text);
            return "\uD83D\uDFE2 Now enter amount of " + text.toUpperCase() + ":";

        } else if (session.getAttribute("transactionAmount") == null){
            session.setAttribute("transactionAmount", Double.parseDouble(text));
            String buySell = (String)session.getAttribute("transactionBuySell");
            String symbol = (String) session.getAttribute("transactionSymbol");
            String portfolioId = (String) session.getAttribute("transactionPortfolioId");
            double amount = (double) session.getAttribute("transactionAmount");
            if(buySell.startsWith("Sell") && !isCoinPresentInPortfolio(symbol, portfolioId, amount)) {
                session.clearAttributes();
                return "\uD83D\uDD34 You don't have such coin in portfolio or the amount is more than you have.";
            }

            return "\uD83D\uDFE2 Now enter price of " +
                    session.getAttribute("transactionSymbol").toString().toUpperCase() + ":";

        } else {
            TransactionDao transactionDao = new TransactionDao();
            transactionDao.save(new Transaction(
                    message.getChatId(),
                    ((String) session.getAndRemoveAttribute("transactionPortfolioId")),
                    ((String) session.getAndRemoveAttribute("transactionSymbol")),
                    ((double) session.getAndRemoveAttribute("transactionAmount")),
                    Double.parseDouble(text),
                    ((String) session.getAndRemoveAttribute("transactionBuySell")).startsWith("Buy"),
                    LocalDate.now()
            ));
            session.removeAttribute("transactionPrice");
            return "\uD83D\uDFE2 New transaction is successfully added.";
        }
    }

    public static boolean isCoinPresentInPortfolio(String symbol, String portfolioId, double amount) {
        PortfolioDao portfolioDao = new PortfolioDao();
        Portfolio portfolio;
        if(portfolioDao.getById(portfolioId).isPresent()) {
            portfolio = portfolioDao.getById(portfolioId).get();
        } else {
            return false;
        }

        for(Coin coin : portfolio.getCoins()) {
            if(coin.getSymbol().equalsIgnoreCase(symbol) && coin.getAmount() >= amount) {
                return true;
            }
        }
        return false;
    }
}
