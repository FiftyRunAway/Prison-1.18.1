package org.runaway.runes.sword;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class BlindnessRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.5) {
            gamer.addEffect(PotionEffectType.BLINDNESS, 100, 1);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "blindness";
    }

    @Override
    public String getName() {
        return "Слепота";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить на врага слепоту";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.RARE;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.SWORD;
    }
}
