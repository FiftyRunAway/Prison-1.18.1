package org.runaway.requirements;

import lombok.Getter;
import org.runaway.Gamer;

@Getter
public class MoneyRequire implements Require {
    double amount;
    boolean takeAfter;

    @Override
    public RequireResult canAccess(Gamer gamer) {
        RequireResult requireResult = new RequireResult(gamer.getMoney() >= getAmount(), (int) gamer.getMoney());
        return requireResult;
    }

    @Override
    public String getName() {
        return "null";
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getLoreString(Gamer gamer) {
        return null;
    }

    @Override
    public void doAfter(Gamer gamer, RequireResult requireResult) {

    }
}
