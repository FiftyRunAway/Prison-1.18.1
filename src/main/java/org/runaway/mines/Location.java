package org.runaway.mines;

import java.util.ArrayList;

public class Location {

    public static ArrayList<Location> locations = new ArrayList<>();

    private String name;
    private String loc_name;

    public Location(String name, String loc_name) {
        this.name = name;
        this.loc_name = loc_name;
    }

    public String getName() {
        return name;
    }

    public String getLocName() {
        return loc_name;
    }
}
