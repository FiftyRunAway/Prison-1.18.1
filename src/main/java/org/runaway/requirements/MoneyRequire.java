package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.runaway.Gamer;
import org.runaway.enums.MoneyType;

@Builder @Getter
public class MoneyRequire implements Require {
    double amount;
    boolean takeAfter;

    @Override
    public RequireResult canAccess(Gamer gamer, boolean sendMessage) {
        if (gamer == null) return new RequireResult(true, 0);
        RequireResult requireResult = new RequireResult(gamer.getMoney() >= getAmount(), (int) gamer.getMoney());
        if(!requireResult.isAccess() && sendMessage) {
            gamer.sendMessage("&cУсловие \"" + getName() + "\" не выполнено! У вас недостаточно денег.");
        }
        return requireResult;
    }

    @Override
    public String getName() {
        return (isTakeAfter() ? "Отдайте" : "Накопите") + " " + getAmount() + " " + MoneyType.RUBLES.getShortName();
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
