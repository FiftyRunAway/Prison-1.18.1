package org.runaway.enums;

/*
 * Created by _RunAway_ on 3.2.2019
 */

public enum ServerStatus {
    NORMAL("&aРаботает"),
    ZBT("&aЗБТ"),
    PREVENTIVEWORKS("&eПрафилактические работы"),
    HACKED("&cВзломан"),
    ERROR("&cОшибка сервера"),
    WIPE("&eВайп");

    private String status;

    ServerStatus(String status) {
        this.status = status;
    }


    public String getStatus() {
        return status;
    }
}
