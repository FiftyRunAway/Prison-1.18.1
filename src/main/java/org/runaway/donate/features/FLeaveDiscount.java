package org.runaway.donate.features;

public class FLeaveDiscount extends IFeature {

    @Override
    public Object getValue() {
        return Integer.parseInt(this.value.toString());
    }

    @Override
    public String getName() {
        return "&aСкидка на покидание фракции";
    }

    @Override
    public int getCode() {
        return 0;
    }
}
