package com.company;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<String, User> userMap = new HashMap();
        ArrayList<Team> teamList = new ArrayList();


        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    HashMap m = new HashMap();
                    m.put("username", name);
                    m.put("teams", teamList);

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

                        User tempName = userMap.get(username);

                        if (tempName == null) {
                            tempName = new User();
                            tempName.password = logPass;
                            tempName = userMap.put(username, tempName);
                        } else if (!logPass.equals(tempName.password)) {
                            Spark.halt(403);
                        }

                        Session session = request.session();
                        session.attribute("username", username);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/add-team",
                ((request, response) -> {
                    Team team = new Team();
                    team.id = teamList.size() + 1;
                    team.teamName = request.queryParams("teamName");
                    team.sport = request.queryParams("sport");
                    team.record= request.queryParams("record");
                    teamList.add(team);
                    response.redirect("/");
                    return "";
                })
        );

    }


    static void addTestTeam(ArrayList<Team> teamTest) {
        teamTest.add(new Team());
    }
}
