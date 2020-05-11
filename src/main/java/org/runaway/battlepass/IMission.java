package org.runaway.battlepass;

import org.runaway.Gamer;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.utils.Utils;

import java.util.HashMap;

public abstract class IMission {

    private HashMap<String, Object> values;
    private Object[] descDetails;

    protected abstract void init();

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
        return String.valueOf(getDescriptionDetails()[getLenghtArguments() - 1]);
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

        if (isCompleted(gamer)) {
            gamer.getPlayer().sendMessage(Utils.colored(EMessage.BPMISSION.getMessage().replace("%name%", Utils.upCurLetter(getName().toLowerCase(), 1))));
            gamer.sendActionbar(Utils.colored("&dПолучено " + getExperience() + " опыта"));
            gamer.setStatistics(EStat.BATTLEPASS_SCORE, (int)gamer.getStatistics(EStat.BATTLEPASS_SCORE) + getExperience());

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
        return (int)getValues().get(gamer.getGamer()) >= getValue();
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
}