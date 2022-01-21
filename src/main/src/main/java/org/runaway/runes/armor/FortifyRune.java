package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class FortifyRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.07) {
            gamer.addEffect(PotionEffectType.WEAKNESS, 100, 2);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "fortify";
    }

    @Override
    public String getName() {
        return "Укрепление";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс наложить врагу слабость";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.RARE;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.ARMOR;
    }
}
