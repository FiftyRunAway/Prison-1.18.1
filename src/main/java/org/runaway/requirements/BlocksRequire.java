package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.enums.EStat;

@Builder @Getter
public class BlocksRequire implements Require {
    LocalizedBlock localizedBlock;
    int amount;

    @Override
    public RequireResult canAccess(Gamer gamer, boolean sendMessage) {
        int blocks = getLocalizedBlock() == null ? (int) gamer.getDoubleStatistics(EStat.BLOCKS) : getLocalizedBlock().getAmount(gamer);
        RequireResult requireResult = new RequireResult(blocks >= getAmount(), blocks);
        if(!requireResult.isAccess() && sendMessage) {
            gamer.sendMessage("&aУсловие \"" + getName() + "\" не выполнено! У вас недостаточно вскопанных блоков.");
        }
        return requireResult;
    }

    @Override
    public String getName() {
        return "Вскопайте " + (getLocalizedBlock() != null ? "блок " + getLocalizedBlock().getNormalName() : "блоки");
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