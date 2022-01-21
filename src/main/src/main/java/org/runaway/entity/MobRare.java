package org.runaway.entity;

import lombok.Getter;

@Getter
public enum MobRare {
    DEFAULT(1, 1, ""),
    RARE(1.2, 1, "&c");

    double multiplier;
    double additionalDamage;
    String namePrefix;

    MobRare(double multiplier, double additionalDamage, String namePrefix) {
        this.multiplier = multiplier;
        this.additionalDamage = additionalDamage;
        this.namePrefix = namePrefix;
    }
}
