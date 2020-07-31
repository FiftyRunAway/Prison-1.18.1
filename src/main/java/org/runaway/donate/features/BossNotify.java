package org.runaway.donate.features;

public class BossNotify extends IFeature {

    @Override
    public Object getValue() {
        return Boolean.parseBoolean(this.value.toString());
    }

    @Override
    public String getName() {
        return "&aУведомления о появлении боссов";
    }

    @Override
    public int getCode() {
        return 2;
    }
}
