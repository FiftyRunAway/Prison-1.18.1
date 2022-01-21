package org.runaway.commands.completers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabBuilder {
    private List<Tab> tabs = new ArrayList<>();
    private String permission;

    public TabBuilder addTab(Tab tab) {
        tabs.add(tab);
        return this;
    }

    public HashMap<Integer, List<String>> getResult() {

        HashMap<Integer, List<String>> list = new HashMap<>();
        tabs.forEach(tab ->
                list.put(tab.getArg(), tab.getVariation()));

        return list;
    }

    public TabBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }
}
