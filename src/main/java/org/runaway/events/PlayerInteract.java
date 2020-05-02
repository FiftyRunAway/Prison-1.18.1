package org.runaway.events;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.inventories.MainMenu;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;
import org.runaway.board.Board;
import org.runaway.enums.*;
import org.runaway.inventories.BlockShopMenu;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Created by _RunAway_ on 20.1.2019
 */

public class PlayerInteract implements Listener {

    private static HashMap<String, Double> prices = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        Block block = event.getClickedBlock();
        if (player.getInventory().getItemInMainHand().getType().equals(Material.PAPER) && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Меню")) {
            new MainMenu(player);
        }

        // Локации
        addLocation(player, "&aПодвал", "prison.vault");
        addLocation(player, "&bЛедяная шахта", "prison.ice");
        addLocation(player, "&eГладиаторская арена", "prison.glad");

        if (block == null) return;
        Main.cases.forEach(aCase -> aCase.open(event));
        if (block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST)) {
            Sign sign = (Sign)block.getState();
            String[] lines = sign.getLines();
            if (lines.length == 4 && ChatColor.stripColor(lines[1]).equalsIgnoreCase("Нажми, чтобы") && ChatColor.stripColor(lines[2]).equalsIgnoreCase("всё продать")) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    sellAll(player);
                } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    new BlockShopMenu(player);
                }
            }
        }

        //Сундук (сокровища)
        if (block.getType().equals(Material.CHEST) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            try {
                if (!BlockBreak.chests.containsKey(player.getName())) return;
                if (BlockBreak.chests.get(player.getName()).equals(block.getLocation())) {
                    block.setType(Material.AIR);
                    int money = ThreadLocalRandom.current().nextInt(10) + 5;
                    player.sendMessage(Utils.colored(EMessage.TREASUREOPEN.getMessage()).replace("%reward%", Board.FormatMoney(money)));
                    if (BlockBreak.treasure_holo.containsKey(player.getName())) {
                        BlockBreak.treasure_holo.get(player.getName()).delete();
                        BlockBreak.treasure_holo.remove(player.getName());
                    }
                    gamer.depositMoney(money);
                    BlockBreak.chests.remove(player.getName());
                }
            } catch (Exception ex) { }
        }
    }

    private void addLocation(Player player, String name, String perm) {
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR) || player.getInventory().getItemInMainHand().getItemMeta().getLore() == null) {
            return;
        }
        if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(name)) {
            if (!player.hasPermission(perm)) {
                if (Main.usePermissionsEx) {
                    PermissionsEx.getUser(player.getName()).addPermission(perm);
                    gamer.sendMessage(EMessage.ACTIVATELOCATION);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }
            } else {
                gamer.sendMessage(EMessage.ALREADYHAVE);
            }
        }
    }

    //Продажа предметов
    public static void sellAll(Player player) {
        double tod = 0.0; int amount = 0;
        for (int h = 0; h <= 35; ++h) {
            ItemStack itemStack = player.getInventory().getItem(h);
            if (itemStack != null && itemStack.getAmount() != 0) {
                if (prices.containsKey(itemStack.getType() + "|" + itemStack.getDurability())) {
                    tod += prices.get(itemStack.getType() + "|" + itemStack.getDurability()) * itemStack.getAmount();
                    amount += itemStack.getAmount();
                    player.getInventory().setItem(h, null);
                }
            }
        }
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        if (amount == 0) {
            gamer.sendMessage(EMessage.NOBLOCKSFORSALE);
            return;
        }
        tod *= gamer.getBoosterMoney();
        String ret = String.valueOf(new BigDecimal(tod).setScale(2, RoundingMode.UP).doubleValue());
        String format = Utils.colored(EMessage.ACTIONBARSELL.getMessage()).replace("%amount%", String.valueOf(amount)).replace("%money%", ret).replace("%booster%", String.valueOf(gamer.getBoosterMoney()));
        gamer.depositMoney(tod);

        Utils.sendClickableMessage(gamer, format, "boosters");
    }

    //Подгрузка цен
    public void loadShop() {
        try {
            EConfig.SHOP.getConfig().getStringList("shop").forEach(s -> {
                String[] var = s.split(" ");
                Material mat = Material.getMaterial(var[0]);
                double price = Double.parseDouble(var[1]);
                int data = Integer.parseInt(var[2]);
                prices.put(mat.toString() + "|" + data, price);
            });
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading shop prices!");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }
}
