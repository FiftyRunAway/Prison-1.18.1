package org.runaway.cases;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.managers.GamerManager;
import org.runaway.menu.MenuAnimation;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.design.MenuDesigner;
import org.runaway.menu.design.Row;
import org.runaway.menu.design.RowType;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.Utils;
import org.runaway.enums.EMessage;
import org.runaway.enums.MoneyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Case {

    private boolean animation;
    private HashMap<ItemStack, Float> drop;
    private Location location;
    private String name;
    private ItemStack key;
    private Material casef;
    private int xmoney;
    private Inventory drops;

    private static Map<Player, Long> CLICK_COOLDOWNS;

    public Case(boolean animation, HashMap<ItemStack, Float> drop, Location location, String name, ItemStack key, Material casef, int xmoney, Inventory drops) {
        this.animation = animation;
        this.drop = drop;
        this.location = location;
        this.name = name;
        this.key = key;
        this.casef = casef;
        this.xmoney = xmoney;
        this.drops = drops;

        CLICK_COOLDOWNS = new HashMap<>();
    }

    public boolean open(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);

        if (event.getClickedBlock().getType().toString().equals(getCase().toString())) {
            if (event.getClickedBlock().getLocation().equals(getLocation())) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);

                    // Avoid command spam
                    Long oldCooldown = CLICK_COOLDOWNS.get(player);
                    if (oldCooldown != null) {
                        if (System.currentTimeMillis() < oldCooldown) {
                            return false;
                        }
                    }
                    CLICK_COOLDOWNS.put(player, System.currentTimeMillis() + 200); // 0.2 seconds cooldown

                    if (player.isSneaking()) {
                        gamer.sendMessage(EMessage.CASEOPENSHIFT);
                        return false;
                    }
                    if ((player.getInventory().getItemInMainHand() != null || player.getInventory().getItemInMainHand().getAmount() > 0) && player.getInventory().getItemInMainHand().getType().toString().equals(getKey().getType().toString())) {
                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        if (!isAnimation()) {
                            Inventory inventory = Bukkit.createInventory(null, 27, getName());
                            if (!addDrop(inventory)) {
                                int money = new Random().nextInt(getXMoney()) + 1;
                                gamer.depositMoney(money);
                                gamer.sendActionbar(Utils.colored("&a+" + money + " " + MoneyType.RUBLES.getShortName()));
                                return true;
                            }
                            event.getPlayer().openInventory(inventory);
                            return true;
                        } else {
                            ArrayList<ItemStack> items = new ArrayList<>();
                            ArrayList<ItemStack> inFrame = new ArrayList<>();
                            while (items.size() <= 30) {
                                getDrop().forEach((itemStack, aFloat) -> {
                                    if (Utils.getRandom(aFloat)) {
                                        if (inFrame.size() < 9) {
                                            inFrame.add(itemStack);
                                            return;
                                        }
                                        items.add(itemStack);
                                    }
                                });
                            }
                            StandardMenu menu = StandardMenu.create(3, getName());
                            MenuDesigner designer = MenuDesigner.create();
                            designer.setDesign(new Row(RowType.NUMBER, 1), "XXXXYXXXX");
                            //designer.setDesign(new Row(RowType.NUMBER, 2), "123456789");
                            designer.setDesign(new Row(RowType.NUMBER, 3), "XXXXYXXXX");
                            designer.setItem("X", ExampleItems.glass(6));
                            designer.setItem("Y", ExampleItems.glass(5));

                            MenuAnimation animation = MenuAnimation.create();
                            animation.start(Main.getInstance(), menu);
                            menu.setAnimation(animation);
                            /*
                            for (int i = 0; i <= 15; i++) {
                                if (inFrame.size() == 9) inFrame.remove(8);
                                inFrame.add(items.get(0));
                                items.remove(0);
                                for (int s = 1; s < 10; s++) {
                                    designer.setItem(String.valueOf(s), inFrame.get(s - 1));
                                }
                                animation.setFrame(i, designer);
                            }
                            AtomicInteger startDelay = new AtomicInteger(20);
                            animation.setDelayBetweenLoops(startDelay.get());

                            animation.setTaskToDoEveryFrame(e -> {
                                animation.setDelayBetweenLoops(startDelay.getAndIncrement());
                                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSUREPLATE_CLICK_ON, 1, 1);
                            });
                            animation.setAnimationEndEvent(e -> {
                                player.closeInventory();
                            });*/
                            animation.start(Main.getInstance(), menu);
                            player.openInventory(menu.build());
                        }
                    } else {
                        gamer.sendMessage(EMessage.KEYOPENCASE);
                    }
                } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    player.openInventory(getDropsMenu());
                }
            }
        }
        return false;
    }

    private boolean addDrop(Inventory inventory) {
        AtomicInteger i = new AtomicInteger();
        getDrop().forEach((itemStack, aFloat) -> {
            if (Utils.getRandom(aFloat)) {
                inventory.setItem(ThreadLocalRandom.current().nextInt(26), itemStack);
                i.incrementAndGet();
            }
        });
        return i.get() != 0;
    }

    private boolean isAnimation() {
        return animation;
    }

    private HashMap<ItemStack, Float> getDrop() {
        return drop;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    private ItemStack getKey() {
        return key;
    }

    private Material getCase() {
        return casef;
    }

    private int getXMoney() {
        return xmoney;
    }

    private Inventory getDropsMenu() { return drops; }
}
