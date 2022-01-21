package org.runaway.runes.sword;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class DoubleDamageRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public String getTechName() {
        return "doubledamage";
    }

    @Override
    public String getName() {
        return "Двойной урон";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс нанести двойной урон";
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
