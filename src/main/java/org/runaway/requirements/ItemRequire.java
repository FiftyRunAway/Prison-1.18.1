package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.runaway.Gamer;
import org.runaway.items.PrisonItem;

@Builder @Getter
public class ItemRequire implements Require {
    PrisonItem prisonItem;
    boolean ignoreLevel, takeAfter;
    int amount;

    @Override
    public RequireResult canAccess(Gamer gamer, boolean sendMessage) {
        if (gamer == null) return new RequireResult(true, 0);
        int amount = gamer.getAmount(getPrisonItem(), isIgnoreLevel());
        RequireResult requireResult = new RequireResult(amount >= getAmount(), amount);
        if(!requireResult.isAccess() && sendMessage) {
            gamer.sendMessage("&cУсловие \"" + getName() + "\" не выполнено! У вас недостаточно предметов.");
        }
        return requireResult;
    }

    @Override
    public String getName() {
        return (isTakeAfter() ? "Отдайте" : "Найдите") + " " + prisonItem.getName();
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
            gamer.removeItem(prisonItem, getAmount(), isIgnoreLevel());
        }
    }
}
