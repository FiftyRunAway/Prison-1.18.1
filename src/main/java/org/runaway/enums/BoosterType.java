package org.runaway.enums;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public enum BoosterType {
    MONEY("денег"),
    BLOCKS("блоков");

    private String name;

    BoosterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
