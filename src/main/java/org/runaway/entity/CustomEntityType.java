package org.runaway.entity;

import net.minecraft.server.v1_12_R1.Entity;
import org.runaway.entity.mobs.Zombie;

public enum CustomEntityType {

    ZOMBIE(54, Zombie.class);

    private int id;
    private Class<? extends Entity> entityClass;

    CustomEntityType(int id, Class<? extends Entity> entityClass) {
        this.id = id;
        this.entityClass = entityClass;
    }

    public int getId() {
        return id;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }
}
