package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class BBMoneySecond extends PassivePerks {

    @Override
    protected String getName() {
        return "Миллионер II";
    }

    @Override
    protected String getDescription() {
        return "+0.2x к ускорителю денег";
    }

    @Override
    protected int getLevel() {
        return 27;
    }

    @Override
    public int getSlot() {
        return 2;
    }
}
