package org.vitaliistf.dao;

import org.vitaliistf.AppConfiguration;
import org.vitaliistf.models.Coin;
import org.vitaliistf.models.Portfolio;
import org.vitaliistf.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PortfolioDao {

    Connection connection;

    public PortfolioDao() {
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

            CoinDao coinDao = new CoinDao();
            List<Coin> coinList;
            if(set.next()) {
                coinList = coinDao.getCoinsByPortfolioId(id);
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

            CoinDao coinDao = new CoinDao();
            List<Coin> coinList;
            while(set.next()) {
                coinList = coinDao.getCoinsByPortfolioId(set.getString(1));
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
            CoinDao coinDao = new CoinDao();
            for(Coin coin : coinDao.getCoinsByPortfolioId(portfolio.getId())) {
                coinDao.delete(coin);
            }
            TransactionDao transactionDao = new TransactionDao();
            for(Transaction transaction : transactionDao.getByPortfolioId(portfolio.getId())) {
                transactionDao.delete(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
