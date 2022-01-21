package org.runaway.enums;

import java.util.LinkedList;

/*
 * Created by _RunAway_ on 27.1.2019
 */

public enum Prison {
    BOSSES(new LinkedList<>()),
    DONATE(new LinkedList<>());

    private Object object;

    Prison(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
