package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class BBlocksFirst extends PassivePerks {

    @Override
    protected String getName() {
        return "Копатель онлайн";
    }

    @Override
    protected String getDescription() {
        return "+0.1x к ускорителю блоков";
    }

    @Override
    public int getLevel() {
        return 22;
    }

    @Override
    public int getSlot() {
        return 10;
    }
}
