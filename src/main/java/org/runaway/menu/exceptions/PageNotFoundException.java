package org.runaway.menu.exceptions;

import org.runaway.menu.MenuUtil;

public class PageNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private String message;

    public PageNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public void printStackTrace() {

        MenuUtil.out("");
        MenuUtil.out("&cAn " + getClass().getSimpleName() + " was thrown by Menu Interface.");
        MenuUtil.out("&cReason: &4" + message);
        for (StackTraceElement element : getStackTrace()) {

            MenuUtil.out("&c-&4 " + element.toString());

        }
        MenuUtil.out("");

    }

}