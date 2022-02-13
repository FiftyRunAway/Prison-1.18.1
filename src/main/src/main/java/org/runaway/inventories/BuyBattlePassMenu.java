package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.donate.Donate;
import org.runaway.donate.DonateStat;
import org.runaway.enums.EButtons;
import org.runaway.enums.EMessage;
import org.runaway.enums.MoneyType;
import org.runaway.enums.TypeMessage;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Utils;

public class BuyBattlePassMenu implements IMenus {

    public static final int bpoints = 150;

    public BuyBattlePassMenu(Player player, Donate donate) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        Gamer gamer = GamerManager.getGamer(player);

        IMenuButton realMoney = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.GOLD_BLOCK)
                        .name("&eКупить за &6деньги")
                .addLoreLine("&r")
                        .addLoreLine("&7Таким способом вы приобретёте Боевой пропуск")
                        .addLoreLine("&7за &6реальные деньги с вашего донат-счёта.")
                        .addLoreLine("&r")
                        .addLoreLine("&eНажмите, чтобы приобрести за &a" + donate.getFinalPrice() + " " + MoneyType.REAL_RUBLES.getShortName())
                        .addGlow(true)
                .build()).setSlot(11);

        realMoney.setClickEvent(event -> {
            Player p = event.getWhoClicked();
            Gamer g = GamerManager.getGamer(p);
            if (Donate.getDonateMoney(p.getName()) < donate.getFinalPrice()) {
                p.closeInventory();
                g.sendMessage(EMessage.TRANSACTIONFAILED);
                return;
            }
            DonateMenu.process(p, donate);
        });
        menu.addButton(realMoney);

        IMenuButton bPoint = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.TNT_MINECART)
                        .name("&eКупить за &6очки Боевого пропуска")
                .addLoreLine("&r")
                .addLoreLine("&7Таким способом вы приобретёте Боевой пропуск")
                .addLoreLine("&7за &6очки Боевого пропуска.")
                .addLoreLine("&r")
                .addLoreLine("&eНажмите, чтобы приобрести за &a" + bpoints + " ОБП")
                .addGlow(true)
                .build()).setSlot(15);

        bPoint.setClickEvent(event -> {
            Player p = event.getWhoClicked();
            Gamer g = GamerManager.getGamer(p);
            if (g.getIntStatistics(DonateStat.BPOINT) < bpoints) {
                p.closeInventory();
                g.sendMessage(EMessage.TRANSACTIONFAILED);
                return;
            }
            process(p, donate);
        });
        menu.addButton(bPoint);

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(26);
        back.setClickEvent(event -> new DonateMenu(event.getWhoClicked()));
        menu.addButton(back);

        menu.open(gamer);
    }

    private static void process(Player player, Donate donate) {
        Gamer gamer = GamerManager.getGamer(player);
        new Confirmation(player, () -> {
            gamer.increaseIntStatistics(DonateStat.BPOINT, -bpoints);
            player.sendMessage(Utils.colored(EMessage.TRANSACTIONSUCCESS.getMessage()
                    .replace("%donate%", ChatColor.UNDERLINE + donate.getIcon().getItemMeta().getDisplayName())
                    .replace("%money%", bpoints + " БПО")));

            Donate.getDonate(donate.getIcon().getType(), gamer);

            gamer.sendTitle("&bПоздравляем!", "&fПокупка совершена");
            if (!player.isOp()) {
                Donate.saveDonateLog(TypeMessage.SUCCESS,
                        "Player " + player.getName() +
                                " buy donate [" + donate.getIcon().getItemMeta().getDisplayName() +
                                "] for a " + bpoints + " battlepass points");
            }
        });
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Меню доната • Боевой пропуск";
    }
}
