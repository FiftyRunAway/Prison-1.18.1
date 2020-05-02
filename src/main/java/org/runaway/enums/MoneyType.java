package org.runaway.enums;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public enum MoneyType {
    RUBLES("р.");

    private String shortname;

    MoneyType(String shortname) {
        this.shortname = shortname;
    }

    public String getShortName() {
        return this.shortname;
    }
}
