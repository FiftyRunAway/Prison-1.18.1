package org.runaway.passiveperks.perks;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.passiveperks.PassivePerks;

public class Speed extends PassivePerks {

    @Override
    protected String getName() {
        return "Скороход";
    }

    @Override
    protected String getDescription() {
        return "Скорость I навсегда";
    }

    @Override
    protected int getLevel() {
        return 12;
    }

    @Override
    public int getSlot() {
        return 34;
    }

    @Override
    public boolean isEffectAction() {
        return true;
    }

    @Override
    public void getPerkAction(Gamer gamer) {
        gamer.addEffect(PotionEffectType.SPEED, 999999, 0);
    }
}
