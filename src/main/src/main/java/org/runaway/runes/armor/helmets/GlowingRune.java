package org.runaway.runes.armor.helmets;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

import java.util.List;

public class GlowingRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public List<PotionEffect> constantEffects() {
        return List.of(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
    }

    @Override
    public String getTechName() {
        return "glowing";
    }

    @Override
    public String getName() {
        return "Свечение";
    }

    @Override
    public String getDescription() {
        return "Даёт постоянный эффект ночного зрения";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.RARE;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.HELMET;
    }
}
