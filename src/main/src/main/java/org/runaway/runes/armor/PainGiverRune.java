package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class PainGiverRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.05) {
            gamer.addEffect(PotionEffectType.POISON, 100, 2);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "paingiver";
    }

    @Override
    public String getName() {
        return "Отравщик";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить врагу тошноту";
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
