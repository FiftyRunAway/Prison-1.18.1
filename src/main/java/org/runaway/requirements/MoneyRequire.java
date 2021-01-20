package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.runaway.Gamer;

@Builder @Getter
public class MoneyRequire implements Require {
    double amount;
    boolean takeAfter;

    @Override
    public RequireResult canAccess(Gamer gamer, boolean sendMessage) {
        RequireResult requireResult = new RequireResult(gamer.getMoney() >= getAmount(), (int) gamer.getMoney());
        if(!requireResult.isAccess() && sendMessage) {
            gamer.sendMessage("&aУсловие \"" + getName() + "\" не выполнено! У вас недостаточно денег.");
        }
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
        RequireResult requireResult = canAccess(gamer, false);
        return (requireResult.isAccess() ? "&a" : "&c") + getName() + " ► " + requireResult.getAmount() + "/" + getAmount();
    }

    @Override
    public void doAfter(Gamer gamer) {
        if(isTakeAfter()) {
            gamer.withdrawMoney(getAmount());
        }
    }
}
