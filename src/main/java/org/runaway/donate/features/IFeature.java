package org.runaway.donate.features;

public interface IFeature {

    IFeature setValue(Object value);
    Object getValue();
    String getName();
    int getCode();
}
