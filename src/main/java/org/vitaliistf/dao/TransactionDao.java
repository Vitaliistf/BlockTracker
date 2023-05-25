package org.vitaliistf.dao;

import org.vitaliistf.AppConfiguration;
import org.vitaliistf.models.Coin;
import org.vitaliistf.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDao {

    Connection connection;

    public TransactionDao() {
        try {
            this.connection = DriverManager.getConnection(AppConfiguration.DB_URL, AppConfiguration.DB_USER , AppConfiguration.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getByPortfolioId(String portfolioId) {
        List<Transaction> transactionList = new ArrayList<>();
        try(PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM Transaction WHERE portfolioId = ?")) {
            statement.setString(1, portfolioId);
            statement.execute();
            ResultSet set = statement.getResultSet();

            while(set.next()) {
                Transaction transaction = new Transaction(
                        set.getLong(1),
                        set.getString(2),
                        set.getString(3),
                        set.getDouble(4),
                        set.getDouble(5),
                        set.getBoolean(6),
                        set.getDate(7).toLocalDate()
                );
                transactionList.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionList;
    }

    public void save(Transaction transaction) {
        try {
            CoinDao coinDao = new CoinDao();
            Optional<Coin> coinOptional = coinDao.getById(transaction.getPortfolioId() + transaction.getSymbol());
            String symbol = transaction.getSymbol();
            double amount;
            String portfolioId = transaction.getPortfolioId();
            double avgBuyingPrice;
            double value;
            if(coinOptional.isPresent() && transaction.isBuy()) {
                amount = transaction.getAmount() + coinOptional.get().getAmount();
                value = amount * coinOptional.get().getAvgBuyingPrice() + transaction.getAmount() * transaction.getPrice();
            } else if (coinOptional.isPresent() && coinOptional.get().getAmount() >= transaction.getAmount() ) {
                amount = coinOptional.get().getAmount() - transaction.getAmount();
                value = amount * coinOptional.get().getAvgBuyingPrice() - transaction.getAmount() * transaction.getPrice();
            } else {
                amount = transaction.getAmount();
                value = transaction.getAmount() * transaction.getPrice();
            }
            avgBuyingPrice = value / amount;
            coinDao.save(new Coin(symbol, amount, portfolioId, avgBuyingPrice));

            PreparedStatement statement;

            statement = connection.prepareStatement("INSERT INTO Transaction VALUES (?,?,?,?,?,?,?)");
            statement.setLong(1, transaction.getUserId());
            statement.setString(2, transaction.getPortfolioId());
            statement.setString(3, transaction.getSymbol());
            statement.setDouble(4, transaction.getAmount());
            statement.setDouble(5, transaction.getPrice());
            statement.setBoolean(6, transaction.isBuy());
            statement.setDate(7, Date.valueOf(transaction.getDate()));
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Transaction transaction) {
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM transaction WHERE portfolioid = ?")) {
            statement.setString(1, transaction.getPortfolioId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
