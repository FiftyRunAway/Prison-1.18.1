package org.runaway.runes.sword;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class SlowMoRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.045) {
            gamer.addEffect(PotionEffectType.SLOW, 100, 1);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "slowmo";
    }

    @Override
    public String getName() {
        return "Замедлитель";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить на врага медлительность II";
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
