package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class VoodooRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.08) {
            gamer.addEffect(PotionEffectType.WEAKNESS, 65, 1);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "voodoo";
    }

    @Override
    public String getName() {
        return "Вуду";
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
