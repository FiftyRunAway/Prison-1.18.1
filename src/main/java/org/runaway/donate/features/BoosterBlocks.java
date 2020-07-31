package org.runaway.donate.features;

public class BoosterBlocks extends IFeature {

    @Override
    public Object getValue() {
        return Double.parseDouble(this.value.toString());
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
