package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.runaway.Gamer;
import org.runaway.donate.Kit;
import org.runaway.donate.Privs;
import org.runaway.enums.EButtons;
import org.runaway.managers.GamerManager;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ItemBuilder;
import oshi.jna.platform.mac.SystemB;

import java.util.Arrays;

public class KitsMenu implements IMenus {

    public static StandardMenu getMenu(Gamer gamer) {
        StandardMenu menu = StandardMenu.create(4, "&eНаборы донатеров");

        Privs privs = gamer.getPrivilege();

        for (Privs priv : Privs.values()) {
            Kit privKit = priv.getKit();
            if (privKit == null) continue;

            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(privKit.getIcon(gamer, priv)).setSlot(priv.getSlot());
            btn.setClickEvent(event -> {
                Gamer g = GamerManager.getGamer(event.getWhoClicked());
                if (Kit.canHave(g, privKit)) {
                    privKit.getRewards().forEach(reward -> reward.giveReward(gamer));
                    g.sendMessage("Набор получен!");
                    g.setQuestValue("kit", String.valueOf(System.currentTimeMillis()));
                }
            });

            if (privs.equals(priv)) {
                UpdateMenu.builder()
                        .gamerLive(gamer)
                        .kitsUpdate(true)
                        .start(0)
                        .build().update(menu, btn);
            }
            menu.addButton(btn);
        }

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(35);
        back.setClickEvent(event -> new PrivilageMenu(gamer.getPlayer()));
        menu.addButton(back);
        return menu;
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Наборы донатеров";
    }
}
