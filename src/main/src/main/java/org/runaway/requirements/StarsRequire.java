package org.runaway.requirements;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;

@Builder @Getter
public class StarsRequire implements Require {
    int amount;

    @Override
    public RequireResult canAccess(Gamer gamer, boolean sendMessage) {
        if (gamer == null) return new RequireResult(true, 0);
        int stars = getAmount(gamer.getPlayer(), ItemManager.getPrisonItem("star").getItemStack());
        RequireResult requireResult = new RequireResult(stars >= getAmount(), stars);
        if(!requireResult.isAccess() && sendMessage) {
            gamer.sendMessage("&cУсловие \"" + getName() + "\" не выполнено! У вас недостаточно звёздочек в инвентаре.");
        }
        return requireResult;
    }

    public static int getAmount(Player arg0, ItemStack arg1) {
        if (arg1 == null)
            return 0;
        int amount = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack slot = arg0.getInventory().getItem(i);
            if (slot == null || !slot.isSimilar(arg1))
                continue;
            amount += slot.getAmount();
        }
        return amount;
    }

    @Override
    public String getName() {
        return "Добывайте звёздочки";
    }

    @Override
    public Object getValue() {
        return getAmount();
    }

    @Override
    public String getLoreString(Gamer gamer) {
        RequireResult requireResult = canAccess(gamer, false);
        return (requireResult.isAccess() ? "&a" : "&c") + getName() + " ► " + requireResult.getAmount() + "/" + getAmount() + (requireResult.isAccess() ? "" : "&4✘");
    }

    @Override
    public void doAfter(Gamer gamer) {
        removeItems(gamer.getPlayer().getInventory(), Material.NETHER_STAR, getAmount());
    }

    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }
}
