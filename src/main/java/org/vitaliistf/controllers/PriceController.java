package org.vitaliistf.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.vitaliistf.util.PrintFormatter;
import org.vitaliistf.coingecko.CG;
import org.vitaliistf.menu.Menu;
import org.vitaliistf.sessions.Session;
import org.vitaliistf.sessions.UserSessionManager;

public class PriceController {

    public static String handleMessage(Message message, SendMessage sendMessage) {
        String request = message.getText();
        long chatId = message.getChatId();
        Session session = UserSessionManager.getSession(chatId);
        String response = "";
        if(request.equals(Menu.PRICE_BUTTON)) {
            session.setAttribute("command", "price");
            response = "Enter symbol or pair:";
            sendMessage.setReplyMarkup(Menu.backButtonOnly());
        }
        return response;
    }

    public static String enterSymbol(Message message) {
        Session session = UserSessionManager.getSession(message.getChatId());

        String request = message.getText();
        StringBuilder response = new StringBuilder("\uD83D\uDFE2 The price of ");

        double price = CG.getPriceUSD(request.toLowerCase());

        if(price <= 0) {
            return "\uD83D\uDFE0 Bad symbol or pair.";
        }
        session.clearAttributes();
        response.append(request.toUpperCase())
                .append(" is: ")
                .append(PrintFormatter.formatNumber(price))
                .append(" ")
                .append("USD".toUpperCase())
                .append(".");
        return response.toString();
    }

}
