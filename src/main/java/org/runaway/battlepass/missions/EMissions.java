package org.runaway.battlepass.missions;

import org.runaway.battlepass.IMission;

public enum EMissions {
    KEYFARM(KeyFarm.class),
    WOODFARM(WoodFarm.class),
    BLOCKSFARM(BlocksFarm.class),
    FISHFARM(FishFarm.class),
    KILLSFARM(KillsFarm.class),
    TREASUREFARM(TreasureFarm.class);

    private Class<? extends IMission> mission;

    EMissions(Class<? extends IMission> mission) {
        this.mission = mission;
    }

    public Class<? extends IMission> getMissionClass() {
        return this.mission;
    }
}
