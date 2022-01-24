package org.runaway.passiveperks.perks;

import org.runaway.passiveperks.PassivePerks;

public class Vaccine extends PassivePerks {

    @Override
    protected String getName() {
        return "Вакцина";
    }

    @Override
    protected String getDescription() {
        return "Нужды в 1.5 раза реже";
    }

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public int getSlot() {
        return 52;
    }
}
