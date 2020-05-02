package org.runaway.donate.features;

public class BossNotify implements IFeature {

    private boolean notify;

    @Override
    public IFeature setValue(Object value) {
        notify = Boolean.parseBoolean(value.toString());
        return this;
    }

    @Override
    public Object getValue() {
        return this.notify;
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
