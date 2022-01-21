package org.runaway.runes.armor.boots;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

import java.util.List;

public class AntiGravityRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public List<PotionEffect> constantEffects() {
        return List.of(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
    }

    @Override
    public String getTechName() {
        return "antigravity";
    }

    @Override
    public String getName() {
        return "Анти-гравитация";
    }

    @Override
    public String getDescription() {
        return "Даёт постоянный эффект прыжка III";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.LEGENDARY;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.BOOTS;
    }
}
