package org.runaway.trainer;

import org.runaway.enums.EStat;

public enum TypeTrainings {
    CASHBACK(EStat.CASHBACK_TRAINER),
    UPGRADE(EStat.UPGRADE_TRAINER),
    LUCK(EStat.LUCK_TRAINER),
    GYM(EStat.GYM_TRAINER);

    EStat value;

    TypeTrainings(EStat value) {
        this.value = value;
    }

    public EStat getValue() {
        return value;
    }
}
