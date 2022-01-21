package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class SmokeBombRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.07) {
            gamer.addEffect(PotionEffectType.SLOW, 100, 0);
            gamer.addEffect(PotionEffectType.BLINDNESS, 100, 0);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "smokebomb";
    }

    @Override
    public String getName() {
        return "Дымовая бомба";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить врагу слепоту и медлительность, чтобы сбежать";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.LEGENDARY;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.ARMOR;
    }
}
