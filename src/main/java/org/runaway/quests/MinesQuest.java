package org.runaway.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.runaway.enums.EConfig;

import java.util.ArrayList;
import java.util.List;

public class MinesQuest {

    private static ArrayList<MinesQuest> quests;

    private String name;
    private String commandName;
    private List<String> content;
    private double stepMoney;
    private int startBlocks;

    private MinesQuest(String name, String commandName, List<String> content, double stepMoney, int startBlocks) {
        this.name = name;
        this.commandName = commandName;
        this.content = content;
        this.stepMoney = stepMoney;
        this.startBlocks = startBlocks;
    }

    public static void load() {
        quests = new ArrayList<>();

        EConfig.MINE_QUESTS.getConfig().getKeys(false).forEach(s -> {
            ConfigurationSection section = EConfig.MINE_QUESTS.getConfig().getConfigurationSection(s);
            quests.add(
                    new MinesQuest(
                            section.getString("name"),
                            section.getName().toLowerCase(),
                            section.getStringList("content"),
                            section.getDouble("step-money"),
                            section.getInt("start-blocks")));
        });
    }

    public static MinesQuest getByName(String commandName) {
        for (MinesQuest quest : quests) {
            if (commandName.toLowerCase().equals(quest.getCommandName())) return quest;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getContent() {
        return content;
    }

    public double getStepMoney() {
        return stepMoney;
    }

    public int getStartBlocks() {
        return startBlocks;
    }
}
