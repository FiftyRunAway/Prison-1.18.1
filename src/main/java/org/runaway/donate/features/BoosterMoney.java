package org.runaway.donate.features;

public class BoosterMoney extends IFeature {

    @Override
    public Object getValue() {
        return Double.parseDouble(this.value.toString());
    }

    @Override
    public String getName() {
        return "&aПостоянный бустер денег";
    }

    @Override
    public int getCode() {
        return 3;
    }
}
