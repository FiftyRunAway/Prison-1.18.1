package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.runaway.Gamer;
import org.runaway.enums.EStat;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.donate.Donate;
import org.runaway.enums.EMessage;
import org.runaway.enums.TypeMessage;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.button.MenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

public class DonateMenu implements IMenus {

    private void buy(Donate donate, Player player, Inventory menu) {
        Gamer gamer = GamerManager.getGamer(player);
        String d = Donate.getPex(donate.getIcon().getType());
        if (d != null && player.hasPermission(d)) {
            gamer.sendMessage(EMessage.TRANSACTIONTWICE);
            player.closeInventory();
            return;
        }
        if (Donate.getDonateMoney(player.getName()) < donate.getFinalPrice()) {
            player.closeInventory();
            gamer.sendMessage(EMessage.TRANSACTIONFAILED);
            return;
        }
        new Confirmation(player, menu, null, () -> {
            if (Donate.withdrawDonateMoney(player.getName(), donate.getFinalPrice())) {
                player.sendMessage(Utils.colored(EMessage.TRANSACTIONSUCCESS.getMessage()
                        .replace("%donate%", ChatColor.UNDERLINE + donate.getIcon().getItemMeta().getDisplayName())
                        .replace("%money%", donate.getFinalPrice() + " ₽")));

                Donate.getDonate(donate.getIcon().getType(), gamer);

                gamer.sendTitle("&bПоздравляем!", "&fПокупка совершена");
                if (!player.isOp()) {
                    Donate.saveDonateLog(TypeMessage.SUCCESS,
                            "Player " + player.getName() +
                                    " buy donate [" + donate.getIcon().getItemMeta().getDisplayName() +
                                    "] for a " + donate.getFinalPrice() + " rubles");
                }
            } else {
                gamer.sendMessage(EMessage.TRANSACTIONFAILED);
            }
        });
    }

    public DonateMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        for (int i = 0; i < Prison.value_donate; i++) {
            Donate donate = (Donate) Utils.donate.get(i);
            MenuButton mn = DefaultButtons.OPEN.getButtonOfItemStack(Donate.icons.get(donate).icon());
            mn.setSlot(donate.getSlot());
            mn.setClickEvent(event -> buy(donate, event.getWhoClicked(), menu.build()));
            menu.addButton(mn);
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(ItemManager.getPrisonItem("back").getItemStack()).setSlot(44);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        IMenuButton privs = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&aДонат-группы").build().item()).setSlot(40);
        privs.setClickEvent(event -> new PrivilageMenu(event.getWhoClicked()));
        menu.addButton(privs);

        IMenuButton dm = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GOLD_BLOCK).name("&eВаш донат-счёт пополнен на &a" + Donate.getDonateMoney(player.getName()) + " стримов")
                .lore(new Lore.BuilderLore().addSpace().addString("&7>> &c&nНажмите, чтобы пополнить").build()).build().item());
        dm.setClickEvent(event -> {
            Gamer gamer = GamerManager.getGamer(event.getWhoClicked());
            event.getWhoClicked().closeInventory();
            gamer.setChatConsumer((p, message) -> {
                if (message.startsWith("отмена")) {
                    gamer.setChatConsumer(null);
                    gamer.sendMessage(EMessage.DONATINGSTOP);
                    return;
                }
                try {
                    Donate.donate(gamer, Integer.parseInt(message));
                } catch (Exception exception) {
                    gamer.sendMessage(EMessage.INTERROR);
                }
            });
        });
        menu.addButton(dm.setSlot(39));
        menu.addButton(dm.clone().setSlot(41));

        player.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Меню доната";
    }
}
