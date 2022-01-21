package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.runaway.Gamer;
import org.runaway.entity.Attributable;
import org.runaway.entity.MobManager;
import org.runaway.entity.MobType;
import org.runaway.enums.EStat;

import java.util.Locale;

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
            gamer.sendMessage("&cУсловие \"" + getName() + "&c\" не выполнено! У вас недостаточно убитых мобов.");
        }
        return requireResult;
    }

    @Override
    public String getName() {
        Attributable attributable = MobManager.getAttributable(getMobName());
        if (getMobName().toLowerCase(Locale.ROOT).equals("rat")) {
            return "&aТюремная крыса";
        }
        if (getMobName().toLowerCase(Locale.ROOT).equals("zombie")) {
            return "&aЗомби";
        }
        if(attributable == null) {
            return "Моба еще не существует";
        }
        return "Убейте " + attributable.getName();
    }

    @Override
    public Object getValue() {
        return getAmount();
    }

    @Override
    public String getLoreString(Gamer gamer) {
        RequireResult requireResult = canAccess(gamer, false);
        String color = requireResult.isAccess() ? "&a" : "&c";
        return color + getName() + color + " ► " + requireResult.getAmount() + "/" + getAmount() + (requireResult.isAccess() ? "" : "&4✘");
    }
}