package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class BBlocksSecond extends PassivePerks {

    @Override
    protected String getName() {
        return "№1 по блокам";
    }

    @Override
    protected String getDescription() {
        return "+0.2x к ускорителю блоков";
    }

    @Override
    public int getLevel() {
        return 27;
    }

    @Override
    public int getSlot() {
        return 6;
    }
}
