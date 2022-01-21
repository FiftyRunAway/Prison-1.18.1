package org.runaway.runes.armor;

import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class EnlightenedRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.05) {
            gamer.getPlayer().setHealth(gamer.getPlayer().getHealth() + 4);
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "enlightened";
    }

    @Override
    public String getName() {
        return "Просветлённый";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс на восстановление здоровья, когда вас атакуют";
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
