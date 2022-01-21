package org.runaway.battlepass;

import lombok.EqualsAndHashCode;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.managers.GamerManager;
import org.runaway.mines.Mine;
import org.runaway.utils.Utils;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class IMission {

    private Object[] descDetails;

    public void init() { }

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
        int result = gamer.getBpData().getOrDefault(hashCode(), 0) + value;
        gamer.getBpData().put(hashCode(), result);

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
        return gamer.getBpData().getOrDefault(hashCode(), 0) >= getValue();
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IMission other = (IMission) obj;
        return Objects.equals(getName(), other.getName()) &&
                getValue() == other.getValue() &&
                getExperience() == other.getExperience();
    }

    protected void addAllValues(Gamer gamer) {
        addAllValues(gamer, 1);
    }

    protected void addAllValues(Gamer gamer, int value) {
        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().isAssignableFrom(this.getClass()) && !mission.isCompleted(gamer)) {
                    mission.addValue(gamer, value);
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

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(getValue(), getName(), getExperience(), getArgumentsString(), getLenghtArguments(),
                getDescription());
    }
}