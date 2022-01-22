package org.runaway.runes.sword;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class TrapRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.77) {
            gamer.addEffect(PotionEffectType.SLOW, 60, 2);
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "trap";
    }

    @Override
    public String getName() {
        return "Ловушка";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить медлительность III";
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
