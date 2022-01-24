package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class Killer extends PassivePerks {

    @Override
    protected String getName() {
        return "Киллер";
    }

    @Override
    protected String getDescription() {
        return "Повышает ваш урон на 10%";
    }

    @Override
    public int getLevel() {
        return 17;
    }

    @Override
    public int getSlot() {
        return 20;
    }
}
