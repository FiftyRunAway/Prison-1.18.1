package org.runaway.donate.features;

public class NeedSleep extends IFeature {

    @Override
    public Object getValue() {
        return Integer.parseInt(value.toString());
    }

    @Override
    public String getName() {
        return "&aПотребность в сне раз в &7(мин)";
    }

    @Override
    public int getCode() {
        return 7;
    }
}
