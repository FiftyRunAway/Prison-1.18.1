package org.runaway.runes.sword;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class WitherRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.078) {
            gamer.addEffect(PotionEffectType.WITHER, 40, 2);
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "wither";
    }

    @Override
    public String getName() {
        return "Опустошение";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить опустошение III";
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
