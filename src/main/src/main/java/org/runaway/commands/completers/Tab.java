package org.runaway.commands.completers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Tab {
    private int arg;
    private List<String> variation;

    public Tab addVariant(String var) {
        variation.add(var);
        return this;
    }
    public Tab addVariants(List<String> var) {
        variation.addAll(var);
        return this;
    }

    public Tab arg(int arg) {
        this.variation = new ArrayList<>();
        this.arg = arg;
        return this;
    }
}
