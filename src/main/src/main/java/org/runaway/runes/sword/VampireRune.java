package org.runaway.runes.sword;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class VampireRune implements Rune {

    @Override
    public boolean act(Gamer gamer) { return false; }

    @Override
    public String getTechName() {
        return "vampire";
    }

    @Override
    public String getName() {
        return "Вампиризм";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс восстановить 1 сердце при атаке";
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
