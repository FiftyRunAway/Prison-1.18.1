package org.runaway.auction;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.MoneyType;
import org.runaway.events.BlockBreak;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TrashAuction {

    private static HashMap<ItemStack, Double> items = new HashMap<>();
    public static ArrayList<Integer> times = new ArrayList();
    private static ArrayList<Location> locs = new ArrayList();
    private static HashMap<ItemStack, Double> now = new HashMap<>();
    private static ArrayList<Integer> this_times;

    public static ArrayList<Auction> auctions = new ArrayList<>();
    private static ArrayList<Hologram> holograms = new ArrayList<>();

    public static Location auction_spawn;

    private static ArrayList<String> inauc = new ArrayList<>();

    public static void load() {
        ConfigurationSection section = EConfig.CONFIG.getConfig().getConfigurationSection("auction-trash");
        section.getStringList("items").forEach(s -> items.put(UpgradeMisc.buildItem(s.split(" ")[0], false, null, false), Double.parseDouble(s.split(" ")[1])));

        Arrays.stream(section.getString("times").split(" ")).forEach(s -> times.add(Integer.parseInt(s)));
        section.getStringList("locations").forEach(s -> locs.add(Utils.unserializeLocation(s)));

        auction_spawn = Utils.unserializeLocation(EConfig.CONFIG.getConfig().getString("locations.auction_spawn"));
        this_times = new ArrayList<>(times);

        updater();
    }

    private static void startAuction() {
        if (auctions.size() > 0) closeAll();
        HashMap<ItemStack, Double> already = new HashMap<>(items);
        for (int i = 0; i < locs.size(); i++) {
            Object[] crunchifyKeys = already.keySet().toArray();
            ItemStack is = (ItemStack) crunchifyKeys[ThreadLocalRandom.current().nextInt(locs.size())];
            double cost = already.get(is);

            short durab = Short.parseShort(String.valueOf(is.getType().getMaxDurability() - ThreadLocalRandom.current().nextInt(is.getType().getMaxDurability() / 3) - is.getType().getMaxDurability() / 3));
            already.remove(is);

            ItemStack real = new ItemStack(is.getType(), 1, durab);
            ItemMeta meta = real.getItemMeta();
            meta.setUnbreakable(false);

            List<String> l = is.getItemMeta().getLore();
            if (ChatColor.stripColor(l.get(0)).toLowerCase().contains("минимальный")) {
                List<String> lore = new ArrayList<>();
                int min = Math.round(Integer.parseInt(ChatColor.stripColor(l.get(0)).toLowerCase().replace("минимальный уровень: ", "")) / 2);
                l.remove(l.get(0));
                lore.add(Utils.colored("&f&7Минимальный уровень: &f" + min));
                lore.add(Utils.colored("&8Аукционный предмет"));
                lore.addAll(l);
                meta.setLore(lore);
            } else {
                meta.setLore(is.getItemMeta().getLore());
            }

            meta.setDisplayName(is.getItemMeta().getDisplayName());
            is.getItemMeta().getEnchants().forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));
            real.setItemMeta(meta);

            now.put(real, cost);
        }

        HashMap<Location, HashMap<ItemStack, Double>> aucs = new HashMap<>();
        ArrayList<Location> locations = new ArrayList<>(locs);

        HashMap<ItemStack, Double> i = new HashMap<>();
        now.forEach((itemStack, doub) -> {
            if (locations.size() > 0) {
                i.put(itemStack, doub);
                Location now_loc = locations.get(locations.size() - 1);
                aucs.put(now_loc, i);
                Auction auction = new Auction(locations.get(locations.size() - 1), itemStack, doub);
                auctions.add(auction);
                locations.remove(now_loc);
                i.clear();
            }
        });
        Bukkit.broadcastMessage(EMessage.AUCTIONSTARTING.getMessage());
    }

    public static void closeAll() {
        auctions.clear();
        holograms.forEach(Hologram::delete);
        inauc.clear();
    }

    private static void updater() {
         // After test - delete this
        /*Date n = new Date();
        times.add(n.getHours());
        this_times.add(n.getHours());*/
        // Under this

        try {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                Date now = new Date();
                times.forEach(time -> {
                    if (now.getHours() == time) {
                        if (this_times.contains(time)) {
                            this_times.remove((Object)now.getHours());
                            startAuction();
                        }
                    } else if (now.getHours() == 1) {
                        if (!this_times.isEmpty()) this_times.clear();
                        this_times.addAll(times);
                    }
                });
            }, 0L, 1200L);
        } catch (Exception ignored) { }
    }

    public static class Auction {

        private ItemStack itemStack;
        private Hologram hologram;
        public StandardMenu menu;
        private int timeout_def = 15;

        private boolean winner_exited;

        private double now;
        private double start_price;
        private String last;
        private int timeout;
        private boolean started;
        private int bids;
        private int fastbuy;

        private boolean bought;

        private static Map<Player, Long> CLICK_COOLDOWNS;

        private BukkitTask holoTask;

        Auction(Location loc, ItemStack stack, Double d) {
            this.itemStack = stack;

            this.now = Math.round(stack.getDurability() * d);
            this.start_price = now;
            this.last = "Нет ставки";
            this.bids = 0;
            this.timeout = timeout_def;
            this.started = false;
            this.winner_exited = false;
            this.bought = false;

            this.fastbuy = (int) now * 4;

            CLICK_COOLDOWNS = new HashMap<>();

            this.menu = StandardMenu.create(1, ChatColor.YELLOW + "Аукцион");
            this.menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(itemStack).setSlot(4));

            // Exit item
            IMenuButton exit = DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(14, ChatColor.RED + "" + ChatColor.BOLD + "ВЫЙТИ"));
            exit.setClickEvent(event -> event.getWhoClicked().closeInventory());
            for (int i = 0; i < 4; i++) {
                this.menu.addButton(exit.clone().setSlot(i));
            }

            // Fast buy item
            IMenuButton fastbuy = DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(5, ChatColor.GREEN + "" + ChatColor.BOLD + "Приобрести сейчас за " + this.fastbuy + " " + MoneyType.RUBLES.getShortName()));
            fastbuy.setClickEvent(event -> {
                Player player = event.getWhoClicked();
                Gamer gamer = Main.gamers.get(player.getUniqueId());

                player.closeInventory();
                if (!bought) {
                    if (gamer.hasMoney(this.fastbuy)) {
                        if (started) holoTask.cancel();
                        inauc.remove(last);
                        now = this.fastbuy;
                        last = player.getName();
                        stopFunction();
                    } else {
                        gamer.sendMessage(EMessage.MONEYNEEDS);
                    }
                } else {
                    gamer.sendMessage(EMessage.ALREADYBOUGHT);
                }
            });

            for (int i = 5; i < 9; i++) {
                this.menu.addButton(fastbuy.clone().setSlot(i));
            }

            this.hologram = HologramsAPI.createHologram(Main.getInstance(), loc.clone().add(0.0, 1.5, 0.0));
            this.hologram.getVisibilityManager().setVisibleByDefault(true);
            holograms.add(this.hologram);

            setup();
        }

        private void annular() {
            this.now = this.start_price;
            this.bids = 0;
            this.timeout = timeout_def;
            this.started = false;
        }

        private void setup() {
            if (this.hologram == null) {
                return;
            }
            this.hologram.clearLines();

            this.hologram.appendTextLine(Utils.colored("&fПредмет &7• " + this.itemStack.getItemMeta().getDisplayName()));
            ItemLine item = this.hologram.appendItemLine(this.itemStack);
            item.setTouchHandler(player -> player.openInventory(menu.build()));
            this.hologram.appendTextLine(Utils.colored("&7(ПКМ по предмету)"));

            this.hologram.appendTextLine(Utils.colored("&fКупить сейчас &7• &a" + fastbuy + " " + MoneyType.RUBLES.getShortName()));
            this.hologram.appendTextLine(Utils.colored("&fСтавок &7• &a" + bids));
            this.hologram.appendTextLine(Utils.colored("&fНачальная стоимость &7• &a" + start_price + " " + MoneyType.RUBLES.getShortName()));
            this.hologram.appendTextLine(Utils.colored("&fПоследняя ставка &7• &a" + now + " " + MoneyType.RUBLES.getShortName()));
            this.hologram.appendTextLine(Utils.colored("&fПоставил &7• &a" + (winner_exited ? last + " &cВышел" : last)));
            this.hologram.appendTextLine(Utils.colored("&fОсталось &7• &a" + color(timeout) + timeout + " сек"));

            this.hologram.appendTextLine(" ");
            this.hologram.appendTextLine(Utils.colored("&eПКМ &fчтобы добавить &a15 " + MoneyType.RUBLES.getShortName()));
            TouchableLine l = this.hologram.appendTextLine(Utils.colored("&eПКМ + SHIFT &fчтобы добавить &a50 " + MoneyType.RUBLES.getShortName()));
            l.setTouchHandler(this::add);
        }

        private void updateHolo() {
            if (this.hologram == null) {
                return;
            }
            this.hologram.getLine(4).removeLine();
            this.hologram.insertTextLine(4, Utils.colored("&fСтавок &7• &a" + bids));

            this.hologram.getLine(6).removeLine();
            this.hologram.insertTextLine(6, Utils.colored("&fПоследняя ставка &7• &a" + now + " " + MoneyType.RUBLES.getShortName()));

            this.hologram.getLine(7).removeLine();
            this.hologram.insertTextLine(7, Utils.colored("&fПоставил &7• &a" + (winner_exited ? last + " &c(Вышел)" : last)));

            this.hologram.getLine(8).removeLine();
            this.hologram.insertTextLine(8, Utils.colored("&fОсталось &7• &a" + color(timeout) + timeout + " сек"));
        }

        private ChatColor color(int time) {
            if (time == timeout_def) return ChatColor.GRAY;
            if (time > 9) return ChatColor.GREEN;
            if (time > 5) return ChatColor.YELLOW;
            if (time > 2) return ChatColor.GOLD;
            if (time > 1) return ChatColor.RED;
            return ChatColor.DARK_RED;
        }

        public void add(Player player) {
            // Avoid command spam
            Long oldCooldown = CLICK_COOLDOWNS.get(player);
            if (oldCooldown != null) {
                if (System.currentTimeMillis() < oldCooldown) {
                    Main.gamers.get(player.getUniqueId()).sendMessage(EMessage.WAIT);
                    return;
                }
            }
            CLICK_COOLDOWNS.put(player, System.currentTimeMillis() + 1200); // 1.2 seconds cooldown

            double toAdd = 15;
            if (player.isSneaking()) toAdd = 50;

            Gamer gamer = Main.gamers.get(player.getUniqueId());
            if (!gamer.isInventory()) {
                gamer.sendMessage(EMessage.FULLINVAUCTION);
                return;
            }
            if (player.getName().equals(last)) {
                gamer.sendMessage(EMessage.AUCTIONSELF);
                return;
            }
            if (inauc.contains(player.getName())) {
                gamer.sendMessage(EMessage.MANYAUCTION);
                return;
            }
            if (Utils.getPlayers().size() == 1) {
                gamer.sendMessage(EMessage.ONEAUCTION);
                return;
            }
            if (bought) return;
            if ((double)gamer.getStatistics(EStat.MONEY) >= now + toAdd) {
                if (!started) start();
                inauc.add(player.getName());
                inauc.remove(last);
                timeout = timeout_def + 1;
                bids++;
                now += toAdd;
                last = player.getDisplayName();
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 10, 10);
                player.sendMessage(Utils.colored("&b+" + toAdd + " " + MoneyType.RUBLES.getShortName()) + " на аукцион");
            } else {
                gamer.sendMessage(EMessage.MONEYNEEDS);
            }
        }

        private void start() {
            started = true;
            if (winner_exited) winner_exited = false;
            holoTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (timeout-- == 0) {
                        started = false;
                        stopAuction();
                        cancel();
                    } else updateHolo();
                }
            }.runTaskTimer(Main.getInstance(), 0L, 20L);
        }

        private void stopAuction() {
            inauc.remove(last);
            if (Bukkit.getPlayer(last) != null) {
                stopFunction();
            } else {
                winner_exited = true;
                EStat.MONEY.setInConfig(last, (double) EStat.MONEY.getFromConfig(last) - 10 * (int) EStat.LEVEL.getFromConfig(last));

                annular();
                setup();
            }
        }

        private void stopFunction() {
            this.hologram.clearLines();
            this.hologram.appendTextLine(Utils.colored("&fПобедитель &7• &a" + last));
            this.hologram.appendTextLine(Utils.colored("&fСтоимость предмета &7• &a" + now + " " + MoneyType.RUBLES.getShortName()));
            this.hologram.appendTextLine(" ");
            this.hologram.appendTextLine(Utils.colored("&cЭтот аукцион окончен"));
            this.hologram.appendItemLine(itemStack);

            Gamer gamer = Main.gamers.get(Bukkit.getPlayer(last).getUniqueId());
            gamer.withdrawMoney(now);
            gamer.getPlayer().getInventory().addItem(itemStack);

            gamer.sendMessage(EMessage.AUCTIONWIN);



            auctions.remove(this);
            bought = true;
            if (auctions.size() == 0) {
                Bukkit.broadcastMessage(EMessage.AUCTIONCLOSE.getMessage());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inauc.clear();
                        holograms.forEach(Hologram::delete);
                        Utils.getPlayers().forEach(s -> {
                            if (BlockBreak.isLocation(Bukkit.getPlayer(s).getLocation(), "auction")) {
                                Main.gamers.get(Bukkit.getPlayer(s).getUniqueId()).teleport(Main.SPAWN);
                            }
                        });
                    }
                }.runTaskLater(Main.getInstance(), 1200L);
            }
        }
    }
}
