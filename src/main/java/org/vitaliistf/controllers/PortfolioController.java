package org.vitaliistf.controllers;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.vitaliistf.services.PortfolioService;
import org.vitaliistf.util.PrintFormatter;
import org.vitaliistf.menu.Menu;
import org.vitaliistf.models.Portfolio;
import org.vitaliistf.sessions.Session;
import org.vitaliistf.sessions.UserSessionManager;

import java.util.*;

public class PortfolioController {

    public static String handleMessage(Message message, SendMessage sendMessage) {
        String request = message.getText();
        long chatId = message.getChatId();
        String response = "";
        if (request.startsWith(Menu.NEW_PORTFOLIO_BUTTON)) {
            Session session = UserSessionManager.getSession(chatId);
            session.setAttribute("command", "newPortfolio");

            response = "How do we name the portfolio?";
            sendMessage.setReplyMarkup(Menu.backButtonOnly());
        } else if (request.startsWith(Menu.SHOW_PORTFOLIOS_BUTTON)) {
            response = showAllPortfolios(message, sendMessage);
        } else if (request.startsWith(Menu.PORTFOLIO_EMOJI)) {
            response = showPortfolio(message, sendMessage);
        } else if (request.startsWith(Menu.DELETE_PORTFOLIO_BUTTON)) {
            response = deletePortfolio(message);
        }
        return response;
    }

    private static String deletePortfolio(Message message) {
        String request = message.getText();
        PortfolioService portfolioService = new PortfolioService();
        List<Portfolio> portfolioList = portfolioService.getByUserId(message.getChatId());

        for(Portfolio portfolio : portfolioList) {
            if(portfolio.getName().equals(request.substring(21))) {
                portfolioService.delete(portfolio);
                return "\uD83D\uDFE2 Portfolio " + portfolio.getName() + " was deleted successfully.";
            }
        }
        return "\uD83D\uDD34 Invalid input. It seems like you don't have portfolio with such name.\n";
    }

    private static String showPortfolio(Message message, SendMessage sendMessage) {
        StringBuilder result = new StringBuilder();
        List<Portfolio> portfolioList = new PortfolioService().getByUserId(message.getChatId());
        String portfolioName = message.getText().substring(3);
        for(Portfolio portfolio : portfolioList) {
            if(portfolio.getId().equals(message.getChatId() + portfolioName)) {
                result.append("\uD83D\uDCBC Portfolio: ")
                        .append(portfolioName)
                        .append("\n")
                        .append(portfolio);
                sendMessage.setParseMode(ParseMode.HTML);
                sendMessage.setReplyMarkup(Menu.getPortfolioMenuKeyboard(portfolioName));
                return result.toString();
            }
        }
        sendMessage.setReplyMarkup(Menu.getMainMenuKeyboard());
        return "\uD83D\uDD34 Invalid input. It seems like you don't have portfolio with such name.";
    }

    private static String showAllPortfolios(Message message, SendMessage sendMessage) {
        long chatId = message.getChatId();
        StringBuilder response = new StringBuilder();
        List<Portfolio> portfolioList = new PortfolioService().getByUserId(chatId);
        response.append("\uD83D\uDCBC Your portfolios:\n");
        if(portfolioList.isEmpty()) {
            response.append("\nYou don't have any portfolio here.");
        } else {
            response.append("<pre>\n")
                    .append(PrintFormatter.formatString("Portfolio", 13))
                    .append(PrintFormatter.formatString("Value", 14))
                    .append(PrintFormatter.formatString("PNL%", 6))
                    .append("\n");
            for(Portfolio portfolio : portfolioList) {
                response.append("\n").append(portfolio.generalToString());
            }
            response.append("</pre>");
            sendMessage.setParseMode(ParseMode.HTML);
        }
        sendMessage.setReplyMarkup(Menu.getAllPortfoliosMenuKeyboard(message.getChatId()));
        return response.toString();
    }

    public static String addNewPortfolio(Message message) {
        Session session = UserSessionManager.getSession(message.getChatId());
        PortfolioService portfolioService = new PortfolioService();
        if (portfolioService.getById(message.getChatId()+message.getText()).isPresent()) {
            return "\uD83D\uDD34 Invalid input. It seems like you have portfolio with such name.";
        } else {
            portfolioService.save(new Portfolio(message.getChatId(), message.getText(), new ArrayList<>()));
        }
        session.clearAttributes();
        return "\uD83D\uDFE2 New portfolio \"" + message.getText() + "\" has been created.";
    }

}

