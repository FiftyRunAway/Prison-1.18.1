package org.runaway.runes.sword;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class ObliterateRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        return false;
    }

    @Override
    public String getTechName() {
        return "obliterate";
    }

    @Override
    public String getName() {
        return "Уничтожение";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс отбросить игрока назад";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.EPIC;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.SWORD;
    }
}
