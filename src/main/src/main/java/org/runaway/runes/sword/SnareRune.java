package org.runaway.runes.sword;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class SnareRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.08) {
            gamer.addEffect(PotionEffectType.SLOW, 60, 0);
            gamer.addEffect(PotionEffectType.SLOW_DIGGING, 60, 0);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "snare";
    }

    @Override
    public String getName() {
        return "Западня";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить медлительность I и усталость от добычи I";
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
