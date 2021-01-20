package org.runaway.entity;

import org.bukkit.Location;

public interface IMobController {
    Attributable getAttributable();

    Location getSpawnLocation();

    int getDelay();

    void setRare(boolean rare);

    boolean isRare();

    String getSpawnerUID();

}
