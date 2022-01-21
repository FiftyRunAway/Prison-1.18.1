package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

import java.util.List;

public class BurnShieldRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public List<PotionEffect> constantEffects() {
        return List.of(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    }

    @Override
    public String getTechName() {
        return "burnshield";
    }

    @Override
    public String getName() {
        return "Огнещит";
    }

    @Override
    public String getDescription() {
        return "Даёт постоянную огнестойкость";
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
