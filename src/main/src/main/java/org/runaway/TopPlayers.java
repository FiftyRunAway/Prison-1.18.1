package org.runaway;

import java.util.*;

/*
 * Created by _RunAway_ on 5.5.2019
 */

public class TopPlayers {

    private Map<String, Long> topValues;
    private String description;

    TopPlayers(Map<String, Long> values, String description) {
        this.topValues = values;
        this.description = description;
    }

    void setTopValues(Map<String, Long> map) {
        this.topValues = map;
    }


    public Map<String, Long> getTopValues() {
        Prison.getInstance().forceUpdateTop();
        return this.topValues;
    }

    public String getDescription() {
        return description;
    }
}
