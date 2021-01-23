package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.runaway.Gamer;
import org.runaway.entity.MobManager;
import org.runaway.enums.EStat;

@Builder
@Getter
public class MobsRequire implements Require {
    String mobName;
    int amount;

    @Override
    public RequireResult canAccess(Gamer gamer, boolean sendMessage) {
        if (gamer == null) return new RequireResult(true, 0);
        int mobKills = gamer.getMobKills(getMobName());
        RequireResult requireResult = new RequireResult(mobKills >= getAmount(), mobKills);
        if(!requireResult.isAccess() && sendMessage) {
            gamer.sendMessage("&cУсловие \"" + getName() + "\" не выполнено! У вас недостаточно убитых мобов.");
        }
        return requireResult;
    }

    @Override
    public String getName() {
        return "Убейте " /*+ MobManager.getAttributable(getMobName()).getName()*/;
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
}