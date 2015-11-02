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

        //addTestTeam(teamList); **DOESN'T WORK....**


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

                        User name = userMap.get(username);

                        if (name == null) {
                            name = new User();
                            name.password = logPass;
                            name = userMap.put(username, name);
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
                    Team team = new Team();
                    team.teamName = request.queryParams("teamName");
                    team.sport = request.queryParams("sport");
                    team.record= request.queryParams("record");
                    team.id = teamList.size() + 1;
                    teamList.add(team);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.get(
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

        Spark.get(
                "/edit-team",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String id = request.queryParams("id");
                    m.put("id", id);
                    return new ModelAndView(m, "edit-team.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/edit-team",
                (request, response) -> {
                    try {
                        String id = request.queryParams("id");
                        int idNum = Integer.valueOf(id);
                        Team team = teamList.get(idNum - 1);
                        team.teamName = request.queryParams("editName");
                        team.sport = request.queryParams("editSport");
                        team.record = request.queryParams("editRecord");
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                }
        );
    }

   /* static void addTestTeam(ArrayList<Team> test){
        test.add(new Team("Hawks", "Football","10",0));

    }*/
}
