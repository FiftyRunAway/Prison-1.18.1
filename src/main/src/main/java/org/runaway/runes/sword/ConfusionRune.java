package org.runaway.runes.sword;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class ConfusionRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.065) {
            gamer.addEffect(PotionEffectType.CONFUSION, 100, 2);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "confusion";
    }

    @Override
    public String getName() {
        return "Конфузия";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить на врага головокружение III";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.EPIC;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.SWORD;
    }
}
