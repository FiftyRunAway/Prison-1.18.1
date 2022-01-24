package org.runaway.passiveperks.perks;

import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.passiveperks.PassivePerks;

public class Jumper extends PassivePerks {

    @Override
    protected String getName() {
        return "Попрыгунчик";
    }

    @Override
    protected String getDescription() {
        return "Прыгучесть I навсегда";
    }

    @Override
    public int getLevel() {
        return 7;
    }

    @Override
    public int getSlot() {
        return 42;
    }

    @Override
    public boolean isEffectAction() {
        return true;
    }

    @Override
    public void getPerkAction(Gamer gamer) {
        gamer.addEffect(PotionEffectType.JUMP, 999999, 0);
    }
}
