package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class FreezeRune implements Rune {

    public boolean act(Gamer gamer) {
        if (Math.random() < 0.08) {
            gamer.addEffect(PotionEffectType.SLOW, 100, 2);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "freeze";
    }

    @Override
    public String getName() {
        return "Заморозка";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить врагу медлительность";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.COMMON;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.ARMOR;
    }
}
