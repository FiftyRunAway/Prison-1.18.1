package org.runaway.donate.features;

public abstract class IFeature {

    public Object value;

    public IFeature setValue(Object value) {
        this.value = value;
        return this;
    }

    public abstract Object getValue();
    public abstract String getName();
    public abstract int getCode();
}
