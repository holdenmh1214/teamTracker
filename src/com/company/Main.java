package com.company;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS teams (id IDENTITY, user_id INT, team_name VARCHAR," +
                " sport VARCHAR, record VARCHAR)");
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name=?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            user = new User();
            user.id = results.getInt("id");
            user.password = results.getString("password");
        }
        return user;
    }

    public static void insertTeam(Connection conn, int userId, String teamName, String sport, String record)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO teams VALUES (NULL, ?, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, teamName);
        stmt.setString(3, sport);
        stmt.setString(4, record);
        stmt.execute();
    }

    public static Team selectTeam(Connection conn, int id) throws SQLException {
        Team team = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM teams " +
                "INNER JOIN users ON teams.user_id = users.id WHERE teams.id=?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            team = new Team();
            team.id = results.getInt("teams.id");
            team.teamName = results.getString("teams.team_name");
            team.sport = results.getString("teams.sport");
            team.record = results.getString("teams.record");
        }
        return team;
    }

    public static ArrayList<Team> selectTeams(Connection conn) throws SQLException {
        ArrayList<Team> teams = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM teams");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            Team team = new Team();
            team.id = results.getInt("teams.id");
            team.teamName = results.getString("teams.team_name");
            team.sport = results.getString("teams.sport");
            team.record = results.getString("teams.record");
            teams.add(team);
        }
        return teams;
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");

        createTables(conn);



        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    ArrayList<Team> teams = selectTeams(conn);
                    HashMap m = new HashMap();
                    m.put("username", name);
                    m.put("teams", teams);

                    if (name == null) {
                        return new ModelAndView(m, "not-logged-in.html");
                    }

                    return new ModelAndView(m, "logged-in.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    String logPass = request.queryParams("password");

                        if (username.isEmpty() || logPass.isEmpty()) {
                            Spark.halt(403);
                        }

                    User name = selectUser(conn, username);

                    if (name == null) {
                        insertUser(conn, username, logPass);
                    } else if (!logPass.equals(name.password)) {
                        Spark.halt(403);
                    }

                        Session session = request.session();
                        session.attribute("username", username);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/add-team",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    String teamName = request.queryParams("teamName");
                    String sport = request.queryParams("sport");
                    String record = request.queryParams("record");
                    try {
                        User me = selectUser(conn, username);
                        insertTeam(conn, me.id, teamName, sport, record);
                    } catch (Exception e) {
                    }
                    response.redirect("/");
                    return "";
                })
        );

       /* Spark.get(
                "/remove-team",
                ((request, response) -> {
                    String idNum = request.queryParams("id");

                    try{
                        int id = Integer.valueOf(idNum);
                        teamList.remove(id-1);
                        for(int i = 0; i< teamList.size(); i++){
                            teamList.get(i).id = i + 1;
                        }
                    } catch (Exception e) {
                    }

                    response.redirect("/");
                    return "";
                })
        );

        Spark.get(  //Have to remember this needs to be a "get" function........
                "/edit-teamName",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String id = request.queryParams("id");
                    m.put("id", id);
                    return new ModelAndView(m, "edit-teamName.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/edit-teamName",
                (request, response) -> {
                    try {
                        String id = request.queryParams("id");
                        int idNum = Integer.valueOf(id);
                        Team team = teamList.get(idNum - 1);
                        team.teamName = request.queryParams("editName");
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                }
        );

        Spark.get(
                "/edit-teamSport",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String id = request.queryParams("id");
                    m.put("id", id);
                    return new ModelAndView(m, "edit-teamSport.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/edit-teamSport",
                (request, response) -> {
                    try {
                        String id = request.queryParams("id");
                        int idNum = Integer.valueOf(id);
                        Team team = teamList.get(idNum - 1);
                        team.sport = request.queryParams("editSport");
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                }
        );

        Spark.get(
                "/edit-teamRecord",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String id = request.queryParams("id");
                    m.put("id", id);
                    return new ModelAndView(m, "edit-teamRecord.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/edit-teamRecord",
                (request, response) -> {
                    try {
                        String id = request.queryParams("id");
                        int idNum = Integer.valueOf(id);
                        Team team = teamList.get(idNum - 1);
                        team.record = request.queryParams("editRecord");
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                }
        );*/
    }


}
