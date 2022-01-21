package org.runaway.runes.sword;

import org.bukkit.attribute.Attribute;
import org.runaway.Gamer;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class LifeStealRune implements Rune {

    @Override
    public boolean act(Gamer gamer) {
        if (Math.random() < 0.07) {
            int steal = 2;
            double maxHealth = gamer.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (gamer.getPlayer().getHealth() + steal < maxHealth) {
                gamer.getPlayer().setHealth(gamer.getPlayer().getHealth() + steal);
            } else {
                gamer.getPlayer().setHealth(maxHealth);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getTechName() {
        return "lifesteal";
    }

    @Override
    public String getName() {
        return "Вор жизней";
    }

    @Override
    public String getDescription() {
        return "Даёт шанс украсть здоровье врага";
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
