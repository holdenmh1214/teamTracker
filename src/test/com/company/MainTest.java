package com.company;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by holdenhughes on 11/3/15.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.execute("DROP TABLE teams");
        conn.close();
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "1234");
        User user = Main.selectUser(conn, "Alice");
        endConnection(conn);

        assertTrue(user != null);
    }

    @Test
    public void testTeam() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "1234");
        Main.insertTeam(conn, 1, "", "", "");
        Team team = Main.selectTeam(conn, 1);
        endConnection(conn);

        assertTrue(team != null);
    }

    @Test
    public void testTeams() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        Main.insertUser(conn, "Bob", "");
        Main.insertTeam(conn, 1, "", "", "");
        Main.insertTeam(conn, 2, "", "", "");
        Main.insertTeam(conn, 3, "", "", "");
        ArrayList<Team> teams = Main.selectTeams(conn);
        endConnection(conn);

        assertTrue(teams.size() == 3);
    }


}