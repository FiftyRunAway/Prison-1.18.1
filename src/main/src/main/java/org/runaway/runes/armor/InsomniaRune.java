package org.runaway.runes.armor;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

import java.util.List;

public class InsomniaRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public List<PotionEffect> constantEffects() {
        return List.of(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 2),
                new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1),
                new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
    }

    @Override
    public String getTechName() {
        return "insomnia";
    }

    @Override
    public String getName() {
        return "Бессонница";
    }

    @Override
    public String getDescription() {
        return "Накладывает на вас головокружение, медлительность, " +
                "но высокий шанс удвоить наносимый урон";
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
