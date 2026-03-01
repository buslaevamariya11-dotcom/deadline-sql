package ru.netology.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {

    private static final QueryRunner runner = new QueryRunner();

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app",
                "user",
                "pass"
        );
    }

    public static String getAuthCode(String login) {
        String sql = "SELECT ac.code " +
                "FROM auth_codes ac " +
                "JOIN users u ON ac.user_id = u.id " +
                "WHERE u.login = ? " +
                "ORDER BY ac.created DESC " +
                "LIMIT 1;";

        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>(), login);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUserStatus(String login) {
        String sql = "SELECT status FROM users WHERE login = ?;";

        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>(), login);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetUserStatus(String login) {
        String sql = "UPDATE users SET status = 'active' WHERE login = ?;";

        try (Connection conn = getConnection()) {
            runner.update(conn, sql, login);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanDatabase() {
        try (Connection conn = getConnection()) {

            runner.update(conn, "SET FOREIGN_KEY_CHECKS=0;");
            runner.update(conn, "DELETE FROM cards;");
            runner.update(conn, "DELETE FROM auth_codes;");
            runner.update(conn, "SET FOREIGN_KEY_CHECKS=1;");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
