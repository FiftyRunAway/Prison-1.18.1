package org.runaway.battlepass;

import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.mines.Mine;
import org.runaway.utils.Utils;

import java.util.HashMap;

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
    public void addValue(Gamer gamer) {
        int result = (int)getValues().get(gamer.getGamer()) + 1;
        getValues().put(gamer.getGamer(), result);

        checkLevel(gamer);
    }

    protected void checkLevel(Gamer gamer) {
        if (isCompleted(gamer)) {
            gamer.getPlayer().sendMessage(Utils.colored(EMessage.BPMISSION.getMessage().replace("%name%", Utils.upCurLetter(getName().toLowerCase(), 1))));
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
    public HashMap<String, Object> getValues() {
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

    /**
     * @return a Value of arguments
     */
    int getLenghtArguments() {
        return getArgumentsString().split(" ").length + 1;
    }

    /**
     * @return a Hashcode of mission
     */
    public int getHashCode() {
        return getLenghtArguments() +
                getName().length() +
                getValue() +
                getDescription().length() +
                getExperience() +
                getClass().getSimpleName().length();
    }

    protected void addAllValues(Gamer gamer) {
        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
                    if (!mission.isCompleted(gamer)) {
                        mission.addValue(gamer);
                    }
                }
            });
        });
    }

    protected Mine getMineString(String string) {
        if (!string.equalsIgnoreCase("none") && !string.equalsIgnoreCase("null")) {
            for (Mine m : Main.mines) {
                if (m.getMaterial().toString().equalsIgnoreCase(string)) {
                    return m;
                }
            }
        }
        return null;
    }
}