package com.company;

/**
 * Created by holdenhughes on 10/31/15.
 */
public class Team {
    String teamName;
    String sport;
    String record;
    int id;

    public Team(int id, String teamName, String sport, String record) {
        this.id = id;
        this.teamName = teamName;
        this.sport = sport;
        this.record = record;
    }
}
