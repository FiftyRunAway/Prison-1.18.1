package org.runaway.runes.pickaxe;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class SpeedRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.135) {
            gamer.addEffect(PotionEffectType.FAST_DIGGING, 80, 0);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "digspeed";
    }

    @Override
    public String getName() {
        return "Ускорение";
    }

    @Override
    public String getDescription() {
        return "Даёт высокий шанс получить быстрое копание I";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.RARE;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.PICKAXE;
    }
}
