package org.runaway.runes.pickaxe;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class BlastRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.05) {
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "blast";
    }

    @Override
    public String getName() {
        return "Взрыв";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс сломать 3*2";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.LEGENDARY;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.PICKAXE;
    }
}
