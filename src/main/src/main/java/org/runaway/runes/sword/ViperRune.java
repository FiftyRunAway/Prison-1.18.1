package org.runaway.runes.sword;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class ViperRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.04) {
            gamer.addEffect(PotionEffectType.POISON, 100, 1);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "viper";
    }

    @Override
    public String getName() {
        return "Гадюка";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс отравить врага";
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
