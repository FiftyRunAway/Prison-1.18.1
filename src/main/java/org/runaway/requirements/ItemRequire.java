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
    public RequireResult canAccess(Gamer gamer) {
        int amount = gamer.getAmount(getPrisonItem(), isIgnoreLevel());
        return new RequireResult(amount >= getAmount(), amount);
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
        RequireResult requireResult = canAccess(gamer);
        return (requireResult.isAccess() ? "&a" : "&c") + getName() + " ► " + requireResult.getAmount() + "/" + getAmount();
    }

    @Override
    public void doAfter(Gamer gamer, RequireResult requireResult) {
        if(requireResult.isAccess()) {
            if(isTakeAfter()) {
                gamer.removeItem(prisonItem, getAmount(), isIgnoreLevel());
            }
        } else {
            gamer.sendMessage("&aУсловие \"" + getName() + "\" не выполнено! У вас недостаточно предметов.");
        }
    }
}
