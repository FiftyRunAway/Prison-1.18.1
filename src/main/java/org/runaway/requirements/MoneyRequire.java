package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.runaway.Gamer;

@Builder @Getter
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
        return (isTakeAfter() ? "Отдайте" : "Накопите") + " " + getAmount() + "$";
    }

    @Override
    public Object getValue() {
        return getAmount();
    }

    @Override
    public String getLoreString(Gamer gamer) {
        RequireResult requireResult = canAccess(gamer);
        return (requireResult.isAccess() ? "&a" : "&c") + getName() + " ► " + requireResult.getAmount() + "/" + getAmount();
    }

    @Override
    public void doAfter(Gamer gamer, RequireResult requireResult) {
        if(requireResult.isAccess()) {
            if(isTakeAfter()) {
                gamer.withdrawMoney(getAmount());
            }
        } else {
            gamer.sendMessage("&aУсловие \"" + getName() + "\" не выполнено! У вас недостаточно денег.");
        }
    }
}
