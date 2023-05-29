package org.vitaliistf.services;

import org.vitaliistf.AppConfiguration;
import org.vitaliistf.models.Coin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoinService {
    Connection connection;

    public CoinService() {
        try {
            this.connection = DriverManager.getConnection(AppConfiguration.DB_URL, AppConfiguration.DB_USER , AppConfiguration.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Coin> getCoinsByPortfolioId(String id) {
        List<Coin> coins = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM Coin WHERE portfolioId = ?")) {
            statement.setString(1, id);
            statement.execute();
            ResultSet set = statement.getResultSet();
            while(set.next()) {
                Coin coin = new Coin(set.getString(1),
                                    set.getString(2),
                                    set.getDouble(3),
                                    set.getString(4),
                                    set.getDouble(5));
                if(coin.getAmount() > 0) {
                    coins.add(coin);
                } else {
                    delete(coin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coins;
    }

    public Optional<Coin> getById(String id) {
        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM Coin WHERE id = ?")) {
            statement.setString(1, id);
            statement.execute();
            ResultSet set = statement.getResultSet();
            if(set.next()) {
                Coin coin = new Coin(set.getString(1),
                        set.getString(2),
                        set.getDouble(3),
                        set.getString(4),
                        set.getDouble(5));
                if(coin.getAmount() > 0) {
                    return Optional.of(coin);
                } else {
                    delete(coin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void save(Coin coin) {
        try {
            PreparedStatement statement;
            if(getById(coin.getId()).isPresent()) {
                statement = connection.prepareStatement("UPDATE coin SET amount=?, avgbuyingprice=? WHERE id = ?");
                statement.setDouble(1, coin.getAmount());
                statement.setDouble(2, coin.getAvgBuyingPrice());
                statement.setString(3, coin.getId());
            } else {
                statement = connection.prepareStatement("INSERT INTO Coin VALUES (?,?,?,?,?)");
                statement.setString(1, coin.getId());
                statement.setString(2, coin.getSymbol());
                statement.setDouble(3, coin.getAmount());
                statement.setString(4, coin.getPortfolioId());
                statement.setDouble(5, coin.getAvgBuyingPrice());
            }
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Coin coin) {
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM coin WHERE id = ?")) {
            statement.setString(1, coin.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
