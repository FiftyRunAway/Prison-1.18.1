package org.runaway.donate.features;

public class NeedWash extends IFeature {
    @Override
    public Object getValue() {
        return Integer.parseInt(value.toString());
    }

    @Override
    public String getName() {
        return "&aМыться каждые &7(мин)";
    }

    @Override
    public int getCode() {
        return 8;
    }
}
