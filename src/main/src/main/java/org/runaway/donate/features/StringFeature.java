package org.runaway.donate.features;

public class StringFeature extends IFeature {

    private String name;

    public IFeature setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Object getValue() {
        return String.valueOf(this.value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getCode() {
        return 5;
    }
}
