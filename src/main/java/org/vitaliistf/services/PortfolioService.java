package org.vitaliistf.services;

import org.vitaliistf.AppConfiguration;
import org.vitaliistf.models.Coin;
import org.vitaliistf.models.Portfolio;
import org.vitaliistf.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PortfolioService {

    Connection connection;

    public PortfolioService() {
        try {
            this.connection = DriverManager.getConnection(AppConfiguration.DB_URL, AppConfiguration.DB_USER , AppConfiguration.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Portfolio> getById(String id) {
        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM Portfolio WHERE id = ?")) {
            statement.setString(1, id);
            statement.execute();
            ResultSet set = statement.getResultSet();

            CoinService coinService = new CoinService();
            List<Coin> coinList;
            if(set.next()) {
                coinList = coinService.getCoinsByPortfolioId(id);
                Portfolio portfolio = new Portfolio(set.getLong(2), set.getString(1), coinList);
                return Optional.of(portfolio);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Portfolio> getByUserId(long userId) {
        List<Portfolio> portfolioList = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM Portfolio WHERE userId = ?")) {
            statement.setLong(1, userId);
            statement.execute();
            ResultSet set = statement.getResultSet();

            CoinService coinService = new CoinService();
            List<Coin> coinList;
            while(set.next()) {
                coinList = coinService.getCoinsByPortfolioId(set.getString(1));
                Portfolio portfolio = new Portfolio(set.getLong(2), set.getString(3), coinList);
                portfolioList.add(portfolio);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return portfolioList;
    }

    public void save(Portfolio portfolio) {
        try {
            PreparedStatement statement;
            if(getById(portfolio.getId()).isPresent()) {
                statement = connection.prepareStatement("UPDATE Portfolio SET userId = ?, name = ? WHERE id = ?");
                statement.setLong(1, portfolio.getUserId());
                statement.setString(2, portfolio.getName());
                statement.setString(3, portfolio.getId());
            } else {
                statement = connection.prepareStatement("INSERT INTO Portfolio VALUES (?,?,?)");
                statement.setString(1, portfolio.getId());
                statement.setLong(2, portfolio.getUserId());
                statement.setString(3, portfolio.getName());
            }
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Portfolio portfolio) {
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM Portfolio WHERE id = ?")) {
            statement.setString(1, portfolio.getId());
            statement.execute();
            CoinService coinService = new CoinService();
            for(Coin coin : coinService.getCoinsByPortfolioId(portfolio.getId())) {
                coinService.delete(coin);
            }
            TransactionService transactionService = new TransactionService();
            for(Transaction transaction : transactionService.getByPortfolioId(portfolio.getId())) {
                transactionService.delete(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
