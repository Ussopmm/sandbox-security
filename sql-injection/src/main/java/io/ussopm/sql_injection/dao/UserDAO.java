package io.ussopm.sql_injection.dao;


import io.ussopm.sql_injection.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/TestDB";
        String user = "postgres";
        String password = "Eldos2812";
        return DriverManager.getConnection(url, user, password);
    }

    //TODO -> Vulnerable to SQL Injection
    public List<User> getUsersByUsername(String username) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM t_user WHERE username = '" + username + "'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId((long) rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    //TODO -> SOLUTION FOR VULNERABILITY
    public User getUserByUsername(String username) {
        String query = "SELECT * FROM t_user WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId((long) rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //TODO -> UNION SELECT BASED SQL INJECTION
    public List<User> getUserById(String userId) {
        List<User> users = new ArrayList<>();
        //Vulnerable code
        String query = "SELECT id, username FROM t_user WHERE id = " + userId;
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId((long) rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    //TODO -> SOLUTION FOR VULNERABILITY
    public User getUserByIdSecure(long userId) {
        String query = "SELECT id, username FROM t_user WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId((long) rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
