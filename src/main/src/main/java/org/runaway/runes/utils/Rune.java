package org.runaway.runes.utils;

import org.bukkit.potion.PotionEffect;
import org.runaway.Gamer;

import java.util.ArrayList;
import java.util.List;

public interface Rune {

    boolean act(Gamer gamer);

    String getTechName();

    String getName();

    String getDescription();

    RuneManager.RuneRarity getRarity();

    RuneManager.RuneType getType();

    default List<RuneManager.RuneType> getFinalTypes() {
        if (getType().equals(RuneManager.RuneType.HELMET) ||
        getType().equals(RuneManager.RuneType.CHESTPLATE) ||
        getType().equals(RuneManager.RuneType.LEGGINGS) ||
        getType().equals(RuneManager.RuneType.BOOTS)) {
            return List.of(RuneManager.RuneType.ARMOR, getType());
        }
        return List.of(getType());
    }

    default List<PotionEffect> constantEffects() {
        return new ArrayList<>();
    }
}
