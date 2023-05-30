package org.vitaliistf.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.vitaliistf.menu.Menu;
import org.vitaliistf.sessions.Session;
import org.vitaliistf.sessions.UserSessionManager;

public class DispatcherController {

    public static final String INVALID_INPUT = "\uD83D\uDD34 Invalid input. Try again.";

    public static String handle(Message message, SendMessage sendMessage) {
        String result;
        String request = message.getText();
        sendMessage.setReplyMarkup(Menu.getMainMenuKeyboard());
        if(request.startsWith("/start")) {
            result = greeting();
        } else if(request.startsWith("\uD83D")) {
            result = handleCommand(message, sendMessage);
        } else {
            result = handleNotCommand(message, sendMessage);
        }
        result += "\n\n@Block_Tracker_bot";
        return result;
    }

    private static String handleCommand(Message message, SendMessage sendMessage) {
        Session session = UserSessionManager.getSession(message.getChatId());
        session.clearAttributes();
        String request = message.getText().toLowerCase();
        if (request.startsWith("\uD83D\uDCC8")) {
            return PriceController.handleMessage(message, sendMessage);
        } else if (request.startsWith("\uD83D\uDCBC") || request.startsWith("\uD83D\uDDD1️")) {
            return PortfolioController.handleMessage(message, sendMessage);
        } else if (request.startsWith("\uD83D\uDCC3")) {
            return TransactionController.handleMessage(message, sendMessage);
        } else if (request.startsWith("\uD83D\uDC40")) {
            return WatchlistController.handleMessage(message, sendMessage);
        } else if (request.startsWith("\uD83D\uDD19")) {
            sendMessage.setReplyMarkup(Menu.getMainMenuKeyboard());
            return "\uD83D\uDFE2 Getting you back.";
        } else {
            return INVALID_INPUT;
        }
    }

    private static String handleNotCommand(Message message, SendMessage sendMessage) {
        Session session = UserSessionManager.getSession(message.getChatId());
        String command = (String) session.getAttribute("command");

        if(command == null) {
            return INVALID_INPUT;
        }

        sendMessage.setReplyMarkup(Menu.backButtonOnly());

        switch (command) {
            case "removeCoinInWatchlist":
                return WatchlistController.removeCoin(message);
            case "watchCoin":
                return WatchlistController.showCoin(message);
            case "newCoinInWatchlist":
                return WatchlistController.addCoin(message);
            case "newPortfolio":
                return PortfolioController.addNewPortfolio(message);
            case "price":
                return PriceController.enterSymbol(message);
            case "newTransaction":
                return TransactionController.createNewTransaction(message);
            default:
                return INVALID_INPUT;
        }
    }

    private static String greeting() {
        return "\uD83E\uDE99 Welcome to BlockTracker! \uD83E\uDE99\n" +
                "Repository: https://github.com/Vitaliistf/BlockTracker\n" +
                "The main things this bot can do are described in README on GitHub.\n" +
                "Anyway, it is also fine to explore bot yourself.\uD83D\uDE0E";
    }

}
