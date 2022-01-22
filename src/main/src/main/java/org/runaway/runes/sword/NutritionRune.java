package org.runaway.runes.sword;

import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class NutritionRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.06) {
            Player player = gamer.getPlayer();
            if (player.getSaturation() + 4 <= 20) {
                player.setSaturation(player.getSaturation() + 4);
            } else {
                player.setSaturation(20);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "nutrition";
    }

    @Override
    public String getName() {
        return "Питание";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс восстановить голод при атаке";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.COMMON;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.SWORD;
    }
}
