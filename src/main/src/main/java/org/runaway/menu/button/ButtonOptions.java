package org.runaway.menu.button;

public enum ButtonOptions {

    /**
     * Cancel Event sets a rule if a click event will be canceled when button is clicked.
     */

    CANCEL_EVENT("cancelEvent",true),

    /**
     * Is Dummy sets if the button is a dummy one!
     */

    IS_DUMMY("isDummy", false);

    private boolean defaultValue;
    private String identifier;

    ButtonOptions(String identifier,boolean defaultValue){
        this.defaultValue = defaultValue;
        this.identifier = identifier;
    }

    public boolean defaultValue() {
        return defaultValue;
    }

    public String getIdentifier() {
        return identifier;
    }
}
