package org.runaway.runes.armor;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class MoltenRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.05) {
            gamer.getPlayer().setFireTicks(100);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "molten";
    }

    @Override
    public String getName() {
        return "Расплавленный";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс поджечь врага";
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
