package org.runaway.jobs;

public enum JobRequriement {
    LEGENDARY_FISH("Легендарные рыбы", "leg_fish", true);

    private String name;
    private String cfgName;

    private boolean isTaken;

    JobRequriement(String name, String cfgName, boolean isTaken) {
        this.name = name;
        this.cfgName = cfgName;
        this.isTaken = isTaken;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public String getName() {
        return name;
    }

    public String getConfig() {
        return cfgName;
    }
}
