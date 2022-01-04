package org.runaway.battlepass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<IMission> getMissions() {
        return this.missions;
    }

    public boolean isStarted() {
        return this.openDate.getTime() <= System.currentTimeMillis();
    }

    public Date getDate() {
        return openDate;
    }
}
