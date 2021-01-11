package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.MoneyType;
import org.runaway.fishing.EFish;
import org.runaway.fishing.Fish;
import org.runaway.jobs.EJobs;
import org.runaway.jobs.Job;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class FishSellMenu implements IMenus {

    private static StandardMenu menu;

    public static void openMenu(Player player) {
        StandardMenu menu = StandardMenu.create(3, ChatColor.YELLOW + "Продажа рыбы");
        int slot = 0;
        for (EFish fish : EFish.values()) {
            Fish fs = fish.getFish();
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(fs.getIcon(GamerManager.getGamer(player)).item()).setSlot(slot++);
            btn.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer gamer = GamerManager.getGamer(p);
                double price = 0;
                int f = 0;
                for (ItemStack is : p.getInventory().getContents()) {
                    if (is == null || !is.hasItemMeta() || !is.getItemMeta().hasDisplayName()) continue;
                    if (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(fs.getName()))) {
                        double weight = Double.parseDouble(ChatColor.stripColor(is.getItemMeta().getLore().get(0)).replace("Вес: ", "").replace(" г.", ""));
                        price += weight * fs.getPriceLevel(gamer) / 1000;
                        p.getInventory().remove(is);
                        f++;
                    }
                }
                if (f > 0) {
                    gamer.depositMoney(price);
                    p.sendMessage(Utils.colored(EMessage.FISHSELLING.getMessage())
                            .replace("%name%", fs.getType().getColor() + fs.getName() + " (" + f + " шт.)")
                            .replace("%money%", new BigDecimal(price).setScale(2, RoundingMode.UP).doubleValue() + " " + MoneyType.RUBLES.getShortName()));
                }
            });
            menu.addButton(btn);
        }
        player.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Продажа рыбы";
    }
}
