package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class BMoneyFirst extends PassivePerks {

    @Override
    protected String getName() {
        return "Миллионер I";
    }

    @Override
    protected String getDescription() {
        return "+0.1x к ускорителю денег";
    }

    @Override
    public int getLevel() {
        return 22;
    }

    @Override
    public int getSlot() {
        return 16;
    }
}
