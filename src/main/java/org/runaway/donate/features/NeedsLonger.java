package org.runaway.donate.features;

public class NeedsLonger extends IFeature {

    @Override
    public Object getValue() {
        return Integer.parseInt(value.toString());
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
