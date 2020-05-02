package org.runaway.donate.features;

public class FLeaveDiscount implements IFeature {

    private int discount;

    @Override
    public IFeature setValue(Object value) {
        this.discount = Integer.parseInt(value.toString());
        return this;
    }

    @Override
    public Object getValue() {
        return this.discount;
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
