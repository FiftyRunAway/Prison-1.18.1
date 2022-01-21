package org.runaway.rebirth.skills;

import org.bukkit.Material;
import org.runaway.rebirth.RSkill;

public class Sale extends RSkill {

    @Override
    protected String getName() {
        return "Скидка";
    }

    @Override
    protected Material getMaterial() {
        return Material.EXPERIENCE_BOTTLE;
    }

    @Override
    protected String getLore() {
        return "Снижение цен практически на всё!";
    }

    @Override
    protected int getMaximumLevel() {
        return 5;
    }

    @Override
    public String getValueDescription() {
        return "%";
    }

    @Override
    protected int getStep() {
        return 5;
    }
}
