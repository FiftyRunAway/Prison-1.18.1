package org.runaway.runes.armor;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class StormCallerRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.056) {
            Player player = gamer.getPlayer();
            World world = player.getWorld();
            world.strikeLightning(player.getLocation());
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "stormcaller";
    }

    @Override
    public String getName() {
        return "Зов бури";
    }

    @Override
    public String getDescription() {
        return "Даёт небольшой шанс ударить молнией врага";
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
