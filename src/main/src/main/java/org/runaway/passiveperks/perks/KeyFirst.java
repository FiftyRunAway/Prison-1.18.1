package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class KeyFirst extends PassivePerks {

    @Override
    protected String getName() {
        return "Счастливчик I";
    }

    @Override
    protected String getDescription() {
        return "+10% к выпадению ключей";
    }

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public int getSlot() {
        return 46;
    }
}
