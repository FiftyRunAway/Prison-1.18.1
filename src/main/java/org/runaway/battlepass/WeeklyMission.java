package org.runaway.battlepass;

import java.util.ArrayList;

public class WeeklyMission {

    private String name;

    private ArrayList<IMission> missions;

    WeeklyMission(String name, ArrayList<IMission> missions) {
        this.name = name;
        this.missions = missions;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<IMission> getMissions() {
        return this.missions;
    }
}
