package org.runaway.levels;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.runaway.Gamer;
import org.runaway.enums.EConfig;
import org.runaway.requirements.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.runaway.Prison.*;

@Getter
public class GamerLevel {

    private static Map<Integer, RequireList> requires;

    private final Gamer gamer;
    private final int currentLevel;
    private boolean reachedMaxLevel;
    private int nextLevel;
    private RequireList nextRequirements;

    public GamerLevel(Gamer gamer) {
        this.gamer = gamer;
        this.currentLevel = gamer.getLevel();
        if (this.currentLevel < Gamer.toRebirth) {
            this.nextLevel = currentLevel + 1;
            this.nextRequirements = requires.get(this.nextLevel);
        } else {
            this.reachedMaxLevel = true;
        }
    }

    public static void loadRequirements() {
        requires = new HashMap<>();
        for (int i = 1; i < (Gamer.toRebirth + 1); i++) {
            ConfigurationSection section = EConfig.CONFIG.getConfig().getConfigurationSection("levels." + i);
            if (section != null) {
                int price = section.getInt("price");
                RequireList requireList = new RequireList();
                requireList.addRequire(MoneyRequire.builder().amount(price).takeAfter(true).build());
                for (String s : section.getStringList("blocks")) {
                    String[] split = s.split(":");
                    if (split.length != 2) {
                        getInstance().getLogger().log(Level.WARNING,
                                "There is a problem with requirements for the " + i + " level!" +
                                        "\nProblem with the string: " + s);
                        continue;
                    }
                    Material material;
                    try {
                        material = Material.valueOf(split[0].toUpperCase());
                    } catch (Exception e) {
                        getInstance().getLogger().log(Level.WARNING,
                                "There is a problem with requirements for the " + i + " level!" +
                                        "\nProblem with the type of material: " + split[0].toUpperCase());
                        continue;
                    }
                    int amount;
                    try {
                        amount = Integer.parseInt(split[1]);
                    } catch (Exception e) {
                        getInstance().getLogger().log(Level.WARNING,
                                "There is a problem with requirements for the " + i + " level!" +
                                        "\nProblem with the amount of blocks: " + split[2]);
                        continue;
                    }
                    requireList.addRequire(BlocksRequire.builder().localizedBlock(new LocalizedBlock(material)).amount(amount).build());
                }
                requires.put(i, requireList);
            } else {
                getInstance().getLogger().log(Level.WARNING,
                        "There is no requirements for the " + i + " level in config...");
            }
        }
    }
}
