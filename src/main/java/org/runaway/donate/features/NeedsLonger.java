package org.runaway.donate.features;

public class NeedsLonger implements IFeature {

    private int value;

    @Override
    public IFeature setValue(Object value) {
        this.value = Integer.parseInt(value.toString());
        return this;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return "&aУменьшение потребностей";
    }

    @Override
    public int getCode() {
        return 1;
    }
}
