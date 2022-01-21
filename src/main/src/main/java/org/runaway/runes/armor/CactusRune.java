package org.runaway.runes.armor;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class CactusRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public String getTechName() {
        return "cactus";
    }

    @Override
    public String getName() {
        return "Кактус";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс нанести урон врагу, который нанес вам урон";
    }

    @Override
    public RuneManager.RuneRarity getRarity() {
        return RuneManager.RuneRarity.LEGENDARY;
    }

    @Override
    public RuneManager.RuneType getType() {
        return RuneManager.RuneType.ARMOR;
    }
}
