package org.runaway.runes.sword;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class ParalyzeRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        return false;
    }

    @Override
    public String getTechName() {
        return "paralyze";
    }

    @Override
    public String getName() {
        return "Паралич";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс ударить молнией, наложить медлительность III и усталость от добычи III";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.LEGENDARY;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.SWORD;
    }
}
