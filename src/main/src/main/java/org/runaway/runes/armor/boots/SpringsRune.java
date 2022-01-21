package org.runaway.runes.armor.boots;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

import java.util.List;

public class SpringsRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public List<PotionEffect> constantEffects() {
        return List.of(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
    }

    @Override
    public String getTechName() {
        return "springs";
    }

    @Override
    public String getName() {
        return "Пружина";
    }

    @Override
    public String getDescription() {
        return "Даёт небольшое увеличение вашему прыжку";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.EPIC;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.BOOTS;
    }
}
