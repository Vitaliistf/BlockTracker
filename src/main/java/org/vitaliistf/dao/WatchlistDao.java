package org.vitaliistf.dao;

import org.vitaliistf.AppConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDao {

    Connection connection;

    public WatchlistDao() {
        try {
            this.connection = DriverManager.getConnection(AppConfiguration.DB_URL, AppConfiguration.DB_USER , AppConfiguration.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getByUserId(long userId) {
        List<String> coins = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM watchlist WHERE userid = ?")) {
            statement.setLong(1, userId);
            statement.execute();
            ResultSet set = statement.getResultSet();
            while(set.next()) {
                coins.add(set.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coins;
    }

    public void save(long userId, String symbol) {
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("INSERT INTO watchlist VALUES (?,?)");
            statement.setLong(1, userId);
            statement.setString(2, symbol);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(long userId, String symbol) {
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM watchlist WHERE userId = ? AND symbol = ?")) {
            statement.setLong(1, userId);
            statement.setString(2, symbol);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
