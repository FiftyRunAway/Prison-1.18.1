package org.runaway.donate.features;

public class BossMoney extends IFeature {

    @Override
    public Object getValue() {
        return Integer.parseInt(this.value.toString());
    }

    @Override
    public String getName() {
        return "&aПовышенная награда за босса (%)";
    }

    @Override
    public int getCode() {
        return 6;
    }
}
