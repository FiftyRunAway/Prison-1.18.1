package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class RecoverRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        gamer.addEffect(PotionEffectType.REGENERATION, 120, 1);
        gamer.addEffect(PotionEffectType.ABSORPTION, 120, 1);
        return true;
    }

    @Override
    public String getTechName() {
        return "recover";
    }

    @Override
    public String getName() {
        return "Восстановление";
    }

    @Override
    public String getDescription() {
        return "После убийства даётся регенерация II и насыщение II";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.EPIC;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.ARMOR;
    }
}
