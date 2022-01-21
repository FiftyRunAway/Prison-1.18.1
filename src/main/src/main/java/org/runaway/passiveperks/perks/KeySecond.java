package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class KeySecond extends PassivePerks {

    @Override
    protected String getName() {
        return "Счастливчик II";
    }

    @Override
    protected String getDescription() {
        return "Ещё +10% к выпадению ключей";
    }

    @Override
    protected int getLevel() {
        return 17;
    }

    @Override
    public int getSlot() {
        return 24;
    }
}
