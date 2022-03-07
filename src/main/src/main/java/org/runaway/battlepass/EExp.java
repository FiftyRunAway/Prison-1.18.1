package org.runaway.battlepass;

import lombok.Getter;

public enum EExp {
    OPEN_CHEST(150);

    @Getter
    private int experience;

    EExp(int experience) {
        this.experience = experience;
    }
}
