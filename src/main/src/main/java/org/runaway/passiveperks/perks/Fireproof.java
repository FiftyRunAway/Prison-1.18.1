package org.runaway.passiveperks.perks;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.passiveperks.PassivePerks;

public class Fireproof extends PassivePerks {

    @Override
    protected String getName() {
        return "Огнеупорный";
    }

    @Override
    protected String getDescription() {
        return "Огнестойкость I навсегда";
    }

    @Override
    protected int getLevel() {
        return 12;
    }

    @Override
    public int getSlot() {
        return 28;
    }

    @Override
    public boolean isEffectAction() {
        return true;
    }

    @Override
    public void getPerkAction(Gamer gamer) {
        gamer.addEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 0);
    }
}
