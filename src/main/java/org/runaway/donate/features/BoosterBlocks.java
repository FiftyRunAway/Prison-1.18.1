package org.runaway.donate.features;

public class BoosterBlocks implements IFeature {

    private double booster;

    @Override
    public IFeature setValue(Object value) {
        booster = Double.parseDouble(value.toString());
        return this;
    }

    @Override
    public Object getValue() {
        return this.booster;
    }

    @Override
    public String getName() {
        return "&aПостоянный бустер блоков";
    }

    @Override
    public int getCode() {
        return 4;
    }
}
