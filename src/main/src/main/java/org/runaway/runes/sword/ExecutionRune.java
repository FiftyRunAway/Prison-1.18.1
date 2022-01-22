package org.runaway.runes.sword;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class ExecutionRune implements Rune {
    @Override
    public boolean act(Gamer gamer) {
        return false;
    }

    @Override
    public String getTechName() {
        return "execute";
    }

    @Override
    public String getName() {
        return "Казнь";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс получить силу IV когда у врага низкое здоровье";
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
