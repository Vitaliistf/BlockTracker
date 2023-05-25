package org.vitaliistf.controllers;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.vitaliistf.BlockTracker;
import org.vitaliistf.dao.WatchlistDao;
import org.vitaliistf.util.PrintFormatter;
import org.vitaliistf.coingecko.CG;
import org.vitaliistf.menu.Menu;
import org.vitaliistf.models.CoinInfo;
import org.vitaliistf.sessions.Session;
import org.vitaliistf.sessions.UserSessionManager;

import java.util.List;
import java.util.Optional;

public class WatchlistController {
    public static String INVALID_COIN = "\uD83D\uDD34 Invalid input or coin is not supported.";
    public static String handleMessage(Message message, SendMessage sendMessage) {
        String request = message.getText();
        long chatId = message.getChatId();
        String response = "\uD83D\uDD34 Invalid input. Try again.";
        if (request.startsWith("\uD83D\uDC40 My Watchlist")) {
            sendMessage.setReplyMarkup(Menu.getWatchlistMenuKeyboard());
            response = showWatchlist(message, sendMessage);
        } else if (request.startsWith("\uD83D\uDC40 Add coin")) {
            Session session = UserSessionManager.getSession(chatId);
            session.setAttribute("command", "newCoinInWatchlist");

            response = "What coin do we add?";
            sendMessage.setReplyMarkup(Menu.backButtonOnly());
        } else if (request.startsWith("\uD83D\uDC40 Remove coin")) {
            Session session = UserSessionManager.getSession(chatId);
            session.setAttribute("command", "removeCoinInWatchlist");

            response = "What coin do we remove?";
            sendMessage.setReplyMarkup(Menu.backButtonOnly());
        }
        return response;
    }

    private static String showWatchlist(Message message, SendMessage sendMessage) {
        StringBuilder response = new StringBuilder();
        List<String> coins = new WatchlistDao().getByUserId(message.getChatId());
        List<CoinInfo> coinInfoList = CG.getWatchlistInfo(coins);

        if(coins.isEmpty()) {
            return "Your watchlist is empty.";
        }
        response.append("\uD83D\uDC40 Your watchlist:\n")
                .append("<pre>\n")
                .append(PrintFormatter.formatString("Rank", 5))
                .append(PrintFormatter.formatString("Symbol", 7))
                .append(PrintFormatter.formatString("Price", 9))
                .append(PrintFormatter.formatString("24h%", 5))
                .append("\n");

        for(CoinInfo coin : coinInfoList) {
            response.append("\n").append(coin.generalToString());
        }
        response.append("</pre>\n");

        response.append("\nEnter symbol to watch full info about coin.");
        Session session = UserSessionManager.getSession(message.getChatId());
        session.setAttribute("command", "watchCoin");
        sendMessage.setParseMode(ParseMode.HTML);
        return response.toString();
    }

    public static String showCoin(Message message) {
        String text = message.getText();
        Optional<CoinInfo> coinInfoOptional = CG.getCoinInfo(text);
        if(coinInfoOptional.isEmpty()) {
            return INVALID_COIN;
        }
        CoinInfo coinInfo = coinInfoOptional.get();
        Session session = UserSessionManager.getSession(message.getChatId());
        session.clearAttributes();
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        sendPhoto.setPhoto(new InputFile(coinInfo.getImage()));
        try {
            BlockTracker.getInstance().execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return coinInfo.toString();
    }

    public static String addCoin(Message message) {
        Session session = UserSessionManager.getSession(message.getChatId());
        WatchlistDao watchlistDao = new WatchlistDao();
        if (CG.getPriceUSD(message.getText().toUpperCase()) <= 0) {
            return INVALID_COIN;
        } else {
            watchlistDao.save(message.getChatId(), message.getText());
        }

        session.clearAttributes();
        return "\uD83D\uDFE2 Coin \"" + message.getText().toUpperCase() + "\" has been added to watchlist.";
    }

    public static String removeCoin(Message message) {
        Session session = UserSessionManager.getSession(message.getChatId());
        WatchlistDao watchlistDao = new WatchlistDao();
        if (CG.getPriceUSD(message.getText().toUpperCase()) <= 0) {
            return INVALID_COIN;
        } else {
            watchlistDao.delete(message.getChatId(), message.getText());
        }

        session.clearAttributes();
        return "\uD83D\uDFE2 Coin \"" + message.getText().toUpperCase() + "\" was removed from watchlist.";
    }
}
