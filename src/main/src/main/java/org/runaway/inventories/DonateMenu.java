package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.runaway.Gamer;
import org.runaway.enums.EButtons;
import org.runaway.enums.MoneyType;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.donate.Donate;
import org.runaway.enums.EMessage;
import org.runaway.enums.TypeMessage;
import org.runaway.managers.GamerManager;
import org.runaway.menu.SimpleItemStack;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.button.MenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.*;

public class DonateMenu implements IMenus {

    private void buy(Donate donate, Player player) {
        Gamer gamer = GamerManager.getGamer(player);
        String d = Donate.getPex(donate.getIcon().getType());
        if ((gamer.getOfflineDonateValue(d).equals("1")) || (donate.getIcon().getType().equals(Material.TNT) && gamer.hasBattlePass())) {
            gamer.sendMessage(EMessage.TRANSACTIONTWICE);
            player.closeInventory();
            return;
        }
        if (Donate.getDonateMoney(player.getName()) < donate.getFinalPrice()) {
            player.closeInventory();
            gamer.sendMessage(EMessage.TRANSACTIONFAILED);
            return;
        }
        new Confirmation(player, () -> {
            if (Donate.withdrawDonateMoney(player.getName(), donate.getFinalPrice())) {
                player.sendMessage(Utils.colored(EMessage.TRANSACTIONSUCCESS.getMessage()
                        .replace("%donate%", ChatColor.UNDERLINE + donate.getIcon().getItemMeta().getDisplayName())
                        .replace("%money%", donate.getFinalPrice() + " " + MoneyType.REAL_RUBLES.getShortName())));

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
        Gamer gamer = GamerManager.getGamer(player);
        for (int i = 0; i < Prison.value_donate; i++) {
            Donate donate = (Donate) Utils.donate.get(i);
            MenuButton mn = DefaultButtons.OPEN.getButtonOfItemStack(Donate.icons.get(donate).icon());
            mn.setSlot(donate.getSlot());
            mn.setClickEvent(event -> buy(donate, event.getWhoClicked()));
            menu.addButton(mn);

            if (!gamer.hasBattlePass() &&
                    mn.getItem().getType().equals(Material.TNT)) {
                UpdateMenu.builder()
                        .updateType(new SimpleItemStack[]{ SimpleItemStack.builder()
                                .material(Material.TNT_MINECART)
                                .durability(0).build() })
                        .gamerLive(gamer)
                        .build().update(menu, mn);
            }
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(44);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        IMenuButton privs = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&aДонат-группы").build().item()).setSlot(40);
        privs.setClickEvent(event -> new PrivilageMenu(event.getWhoClicked()));
        menu.addButton(privs);

        IMenuButton dm = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(EButtons.SACK_OF_MONEY.getItemStack()).name("&eБаланс: &a" + TopsBanner.FormatMoney(Donate.getDonateMoney(player.getName())) + " " + MoneyType.REAL_RUBLES.getShortName())
                .setLore(new Lore.BuilderLore().addSpace().addString("&7>> &c&nНажмите, чтобы пополнить").build().getList()).build());
        dm.setClickEvent(event -> {
            Gamer g = GamerManager.getGamer(event.getWhoClicked());
            Utils.sendSiteMessage(g);
            event.getWhoClicked().closeInventory();
        });
        menu.addButton(dm.setSlot(39));
        menu.addButton(dm.clone().setSlot(41));

        menu.open(GamerManager.getGamer(player));
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
