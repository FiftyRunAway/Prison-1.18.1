package org.runaway.rebirth;

import org.runaway.rebirth.skills.Sale;

public enum ESkill {
    SALE(new Sale());

    private RSkill skill;

    ESkill(RSkill skill) {
        this.skill = skill;
    }

    public RSkill getSkill() {
        return skill;
    }
}
