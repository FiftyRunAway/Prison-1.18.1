package org.runaway.runes.armor;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class SaviorRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public String getTechName() {
        return "savior";
    }

    @Override
    public String getName() {
        return "Спаситель";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс сократь получаемый урон вдвое";
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
