package org.runaway.battlepass;

import java.util.ArrayList;
import java.util.Date;

public class WeeklyMission {

    private String name;
    private ArrayList<IMission> missions;
    private Date openDate;

    WeeklyMission(String name, ArrayList<IMission> missions, Date date) {
        this.name = name;
        this.missions = missions;
        this.openDate = date;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<IMission> getMissions() {
        return this.missions;
    }

    public Date getDate() {
        return openDate;
    }
}
