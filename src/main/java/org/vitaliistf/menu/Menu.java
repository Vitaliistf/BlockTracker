package org.vitaliistf.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.vitaliistf.services.PortfolioService;
import org.vitaliistf.models.Portfolio;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    public static final String BACK_BUTTON = "\uD83D\uDD19 Back";
    public static final String NEW_PORTFOLIO_BUTTON = "\uD83D\uDCBC New portfolio";
    public static final String PRICE_BUTTON = "\uD83D\uDCC8 Price";
    public static final String REMOVE_COIN_BUTTON = "\uD83D\uDC40 Remove coin";
    public static final String SHOW_PORTFOLIOS_BUTTON = "\uD83D\uDCBC Show portfolios";
    public static final String ADD_COIN_BUTTON = "\uD83D\uDC40 Add coin";
    public static final String MY_WATCHLIST_BUTTON = "\uD83D\uDC40 My Watchlist";
    public static final String DELETE_PORTFOLIO_BUTTON = "\uD83D\uDDD1️ Delete portfolio ";
    public static final String NEW_TRANSACTION_BUTTON = "\uD83D\uDCC3 New transaction ";
    public static final String SHOW_TRANSACTIONS_BUTTON = "\uD83D\uDCC3 Show transactions ";
    public static final String PORTFOLIO_EMOJI = "\uD83D\uDCBC";

    private static ReplyKeyboardMarkup buttonsToKeyboard(String[][] keyboard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for(String[] buttonRow : keyboard) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for(String button : buttonRow) {
                keyboardRow.add(button);
            }
            keyboardRows.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup chooseSellOrBuy(String portfolioName) {
        String[][] buttons = {
                {"Buy in " + portfolioName + "\uD83D\uDFE2", "Sell in " + portfolioName + "\uD83D\uDD34"},
                {BACK_BUTTON}
        };
        return buttonsToKeyboard(buttons);
    }

    public static ReplyKeyboardMarkup backButtonOnly() {
        String[][] buttons = {
                {BACK_BUTTON}
        };
        return buttonsToKeyboard(buttons);
    }

    public static ReplyKeyboardMarkup getMainMenuKeyboard(){
        String[][] buttons = {
                {PRICE_BUTTON},
                {SHOW_PORTFOLIOS_BUTTON},
                {MY_WATCHLIST_BUTTON}
        };
        return buttonsToKeyboard(buttons);
    }

    public static ReplyKeyboardMarkup getPortfolioMenuKeyboard(String portfolioName) {
        String[][] buttons = {
                {NEW_TRANSACTION_BUTTON + portfolioName, SHOW_TRANSACTIONS_BUTTON + portfolioName},
                {DELETE_PORTFOLIO_BUTTON + portfolioName},
                {BACK_BUTTON}
        };
        return buttonsToKeyboard(buttons);
    }

    public static ReplyKeyboardMarkup getWatchlistMenuKeyboard() {
        String[][] buttons = {
                {ADD_COIN_BUTTON},
                {REMOVE_COIN_BUTTON},
                {BACK_BUTTON}
        };
        return buttonsToKeyboard(buttons);
    }

    public static ReplyKeyboardMarkup getAllPortfoliosMenuKeyboard(long userId){
        PortfolioService portfolioService = new PortfolioService();
        List<Portfolio> portfolios = portfolioService.getByUserId(userId);
        String[][] buttons = new String[portfolios.size()+2][1];

        int i = 0;
        for(Portfolio portfolio : portfolios) {
            buttons[i++][0] = "\uD83D\uDCBC " + portfolio.getName();
        }
        buttons[i++][0] = NEW_PORTFOLIO_BUTTON;
        buttons[i][0] = BACK_BUTTON;

        return buttonsToKeyboard(buttons);
    }
}
