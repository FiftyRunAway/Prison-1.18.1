package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

import java.util.List;

public class HulkRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public List<PotionEffect> constantEffects() {
        return List.of(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1),
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0),
                new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
    }

    @Override
    public String getTechName() {
        return "hulk";
    }

    @Override
    public String getName() {
        return "Халк";
    }

    @Override
    public String getDescription() {
        return "Даёт постоянную силу II, сопротивление I и медлительность II";
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
