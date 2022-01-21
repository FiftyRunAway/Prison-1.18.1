package org.runaway.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageInfo {
    private double damage;
    private long lastDamageTime;

    public DamageInfo(double damage, long lastDamageTime) {
        this.damage = damage;
        this.lastDamageTime = lastDamageTime;
    }

    public void addDamage(double damage) {
        this.damage += damage;
    }
}
