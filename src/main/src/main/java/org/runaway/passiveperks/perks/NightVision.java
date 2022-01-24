package org.runaway.passiveperks.perks;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.passiveperks.PassivePerks;

public class NightVision extends PassivePerks {

    @Override
    protected String getName() {
        return "Оборотень";
    }

    @Override
    protected String getDescription() {
        return "Ночное зрение I навсегда";
    }

    @Override
    public int getLevel() {
        return 7;
    }

    @Override
    public int getSlot() {
        return 38;
    }

    @Override
    public boolean isEffectAction() {
        return true;
    }

    @Override
    public void getPerkAction(Gamer gamer) {
        gamer.addEffect(PotionEffectType.NIGHT_VISION, 999999, 0);
    }
}
