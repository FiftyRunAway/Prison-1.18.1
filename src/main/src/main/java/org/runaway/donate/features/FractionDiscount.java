package org.runaway.donate.features;

public class FractionDiscount extends IFeature {

    @Override
    public Object getValue() {
        return Integer.parseInt(this.value.toString());
    }

    @Override
    public String getName() {
        return "&aСкидка на вход (выход) во фракцию (%)";
    }

    @Override
    public int getCode() {
        return 0;
    }
}
