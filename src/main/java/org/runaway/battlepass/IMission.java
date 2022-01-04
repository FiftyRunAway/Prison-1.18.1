package org.runaway.battlepass;

import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.mines.Mine;
import org.runaway.utils.Utils;

import java.util.*;

public abstract class IMission {

    private HashMap<String, Object> values;
    private Object[] descDetails;

    public void init() {

    }

    /**
     * @return a Maximum value of mission
     */
    public int getValue() {
        return Integer.parseInt(getDescriptionDetails()[0].toString());
    }

    /**
     * Give a name of mission
     */
    public String getName() {
        return String.valueOf(getDescriptionDetails()[getLenghtArguments() - 1]).replace("_", " ");
    }

    /**
     * @return a Description of mission
     */
    public abstract String getDescription();

    /**
     * Please don`t use that method
     *
     * @param objects
     *      have a Details of description
     */
    void setDescriptionDetails(Object... objects) {
        this.descDetails = objects;
        this.values = new HashMap<>();
    }

    /**
     * @return a Description details of mission
     */
    protected Object[] getDescriptionDetails() {
        return this.descDetails;
    }

    /**
     * @param gamer
     *      have a gamer class
     */
    public void addValue(Gamer gamer, int value) {
        int result = (int)getValues().get(gamer.getGamer()) + value;
        getValues().put(gamer.getGamer(), result);

        checkLevel(gamer);
        if (gamer.getPins().contains(this)) {
            gamer.bpStatus(this);
        }
    }

    protected void checkLevel(Gamer gamer) {
        if (isCompleted(gamer)) {
            gamer.sendMessage(Utils.colored(EMessage.BPMISSION.getMessage().replace("%name%", getName())));
            gamer.addExperienceBP(getExperience());

            BattlePass.checkLevelUp(gamer);
        }
    }

    /**
     * @return an Argument string for config header
     */
    public abstract String getArgumentsString();

    /**
     * @return a HashMap of values: <Player_name, Value>
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * @return an Item mission icon
     */
    public MissionIcon getIcon() {
        return new MissionIcon.Builder(this).build();
    }

    /**
     * @return a Value of experience to receiving
     */
    public abstract int getExperience();

    /**
     * @return a Value of status mission
     */
    public boolean isCompleted(Gamer gamer) {
        return !getValues().containsKey(gamer.getGamer()) ||
                (getValues().containsKey(gamer.getGamer()) &&
                        (int)getValues().get(gamer.getGamer()) >= getValue());
    }

    public boolean isCompletedOffline(Gamer gamer) {
        if (!EConfig.BATTLEPASS_DATA.getConfig().contains(hashCode() + "." + gamer.getGamer())) return false;
        return (int)EConfig.BATTLEPASS_DATA.getConfig().get(hashCode() + "." + gamer.getGamer()) >= getValue();
    }

    /**
     * @return a Value of pinning
     */
    public boolean isPinned(Gamer gamer) {
        List<IMission> s = BattlePass.getPinnedTasks(gamer);
        if (s == null) return false;
        for (IMission m : s) {
            if (String.valueOf(this.hashCode()).contains(String.valueOf(m.hashCode()))) return true;
        }
        return false;
    }

    /**
     * @return a Value of arguments
     */
    int getLenghtArguments() {
        return getArgumentsString().split(" ").length + 1;
    }

    /**
     * @return a Hashcode of mission
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getLenghtArguments();
        result = prime * result + getName().length();
        result = prime * result + getValue();
        result = prime * result + getDescription().length();
        result = prime * result + getExperience();
        if (getArgumentsString() != null)
            result = prime * result + getArgumentsString().length();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IMission other = (IMission) obj;
        if (!Objects.equals(getName(), other.getName()) ||
                getValue() != other.getValue() ||
                getExperience() != other.getExperience())
            return false;
        return true;
    }

    protected void addAllValues(Gamer gamer) {
        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().isAssignableFrom(this.getClass()) && !mission.isCompleted(gamer)) {
                    mission.addValue(gamer, 1);
                }
            });
        });
    }

    protected Mine getMineString(String string) {
        if (!string.equalsIgnoreCase("none") && !string.equalsIgnoreCase("null")) {
            for (Mine m : Prison.mines) {
                if (m.getMaterial().toString().equalsIgnoreCase(string)) {
                    return m;
                }
            }
        }
        return null;
    }
}