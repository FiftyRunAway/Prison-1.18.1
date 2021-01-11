package org.runaway.fishing;

import org.runaway.fishing.fishes.Burbot;
import org.runaway.fishing.fishes.Catfish;
import org.runaway.fishing.fishes.Perch;
import org.runaway.fishing.fishes.Pike;

public enum EFish {
    CATFISH(new Catfish()),
    BURBOT(new Burbot()),
    PIKE(new Pike()),
    PERCH(new Perch());

    private final Fish fish;

    EFish(Fish fish) {
        this.fish = fish;
    }

    public Fish getFish() {
        return fish;
    }
}
