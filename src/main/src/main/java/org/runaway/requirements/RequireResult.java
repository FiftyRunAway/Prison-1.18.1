package org.runaway.requirements;

import lombok.Getter;

@Getter
public class RequireResult {
    boolean access;
    int amount;

    public RequireResult(boolean access, int amount) {
        this.access = access;
        this.amount = amount;
    }
}
