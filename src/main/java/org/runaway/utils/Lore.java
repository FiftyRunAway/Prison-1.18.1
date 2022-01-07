package org.runaway.utils;

import java.util.ArrayList;
import java.util.List;

public class Lore {

    private final ArrayList<String> newstring = new ArrayList<>();

    public static class BuilderLore {
        private List<String> ns = new ArrayList<>();

        public BuilderLore addString(String string) {
            ns.add(string);
            return this;
        }

        public BuilderLore addString(String string, int i) {
            ns.add(i, string);
            return this;
        }

        public BuilderLore addList(List<String> list) {
            if (list != null) ns.addAll(list);
            return this;
        }

        public BuilderLore addLore(Lore lore) {
            ns.addAll(lore.getList());
            return this;
        }

        public BuilderLore addSpace() {
            ns.add(" ");
            return this;
        }

        public Lore build() {
            return new Lore(this);
        }
    }

    private Lore(BuilderLore builder) {
        builder.ns.forEach(s -> newstring.add(Utils.colored(s)));
    }

    public ArrayList<String> getList() {
        return newstring;
    }
}
