package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.enums.EConfig;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.button.MenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Vars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UpItemsMenu implements IMenus {

    private static StandardMenu menu, spade, pickaxe, spickaxe, sword, helmet, chestplate, legs, boots, bow, axe, fish_rod, shears;

    private static HashMap<String, ArrayList<ItemStack>> items = new HashMap<>();

    UpItemsMenu(Player gamer) {
        gamer.openInventory(menu.build());
    }

    public static void load() {
        try {
            menu = StandardMenu.create(6, "&eВыберите предмет прокачки");
            spade = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eЛопата");
            menu.addChild("&eВыберите предмет прокачки &7• &eЛопата", spade);
            pickaxe = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eКирка");
            menu.addChild("&eВыберите предмет прокачки &7• &eКирка", pickaxe);
            spickaxe = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eУлучшенная кирка");
            menu.addChild("&eВыберите предмет прокачки &7• &eУлучшенная кирка", spickaxe);
            sword = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eМеч");
            menu.addChild("&eВыберите предмет прокачки &7• &eМеч", sword);
            helmet = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eШлем");
            menu.addChild("&eВыберите предмет прокачки &7• &eШлем", helmet);
            chestplate = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eНагрудник");
            menu.addChild("&eВыберите предмет прокачки &7• &eНагрудник", chestplate);
            legs = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eШтаны");
            menu.addChild("&eВыберите предмет прокачки &7• &eШтаны", legs);
            boots = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eБотинки");
            menu.addChild("&eВыберите предмет прокачки &7• &eБотинки", boots);
            bow = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eЛук");
            menu.addChild("&eВыберите предмет прокачки &7• &eЛук", bow);
            axe = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eТопор");
            menu.addChild("&eВыберите предмет прокачки &7• &eТопор", axe);
            fish_rod = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eУдочка");
            menu.addChild("&eВыберите предмет прокачки &7• &eУдочка", fish_rod);
            shears = StandardMenu.create(3, "&eВыберите предмет прокачки &7• &eНожницы");
            menu.addChild("&eВыберите предмет прокачки &7• &eНожницы", shears);

            items.put("spade", new ArrayList<>()); items.put("pickaxe", new ArrayList<>()); items.put("spickaxe", new ArrayList<>()); items.put("chestplate", new ArrayList<>());
            items.put("sword", new ArrayList<>()); items.put("helmet", new ArrayList<>()); items.put("leggings", new ArrayList<>()); items.put("boots", new ArrayList<>());
            items.put("bow", new ArrayList<>()); items.put("axe", new ArrayList<>()); items.put("rod", new ArrayList<>()); items.put("shears", new ArrayList<>());

//            EConfig.ITEMS.getConfig().getKeys(false).forEach(s -> {
//                if (s.equals("none")) return;
//                final String[] last = {EConfig.ITEMS.getConfig().getConfigurationSection(s).getKeys(false).toArray()[0].toString()};
//                EConfig.ITEMS.getConfig().getConfigurationSection(s).getKeys(false).forEach(item -> {
//                    ConfigurationSection section = EConfig.ITEMS.getConfig().getConfigurationSection(s + "." + item);
//                    String type = "";
//                    if (ChatColor.stripColor(section.get("name").toString()).contains("Улучшенная")) type += ("s");
//                    if (section.get("type").toString().split("_").length > 1) {
//                        type += section.get("type").toString().split("_")[1].toLowerCase();
//                    } else type += section.get("type").toString().toLowerCase();
//                    if (section.contains("item_level") && section.getInt("item_level") > 0) {
//                        if (!item.equals(last[0])) {
//                            last[0] = last[0] + "_" + (section.getInt("item_level") - 1);
//                        }
//
//                        String i = item + "_" + section.getInt("item_level");
//                        ItemStack stack = ItemManager.getPrisonItem(i).getItemStack();
//                        if (section.getInt("item_level") > 1 && ItemManager.getPrisonItem(ItemManager.getPrisonItem(i).getNextPrisonItem()).getUpgradeRequireList() != null) {
//                            ItemUtils.addLore(stack, "&r", "&dТребования:");
//                            ItemUtils.addLore(stack, (ItemManager.getPrisonItem(ItemManager.getPrisonItem(i).getNextPrisonItem()).getUpgradeRequireList().getLore(null)));
//                        }
//                        items.get(type).add(stack);
//                    }
//                });
//            });

            EConfig.UPGRADE.getConfig().getConfigurationSection("upgrades").getKeys(false).forEach(s -> {
                ConfigurationSection section = EConfig.UPGRADE.getConfig().getConfigurationSection("upgrades." + s);
                String type = "";
                if (ChatColor.stripColor(section.get("name").toString()).contains("Улучшенная")) type += ("s");
                if (section.get("type").toString().split("_").length > 1) {
                    type += section.get("type").toString().split("_")[1].toLowerCase();
                } else type += section.get("type").toString().toLowerCase();
                if (section.getInt("lorelevel") > 0) {
                    PrisonItem pi = ItemManager.getPrisonItem(s + "_" + section.getInt("lorelevel"));

                    ItemStack stack = pi.getItemStack();
                    if (section.getInt("lorelevel") > 1) {
                        ItemUtils.addLore(stack, "&r", "&dТребования:");
                        ItemUtils.addLore(stack, pi.getUpgradeRequireList().getLore(null));
                    }
                    items.get(type).add(stack);
                }
            });

            AtomicInteger i = new AtomicInteger(0);
            items.get("helmet").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                helmet.addButton(bt);
            });

            i.set(0);
            items.get("chestplate").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                chestplate.addButton(bt);
            });

            i.set(0);
            items.get("leggings").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                legs.addButton(bt);
            });

            i.set(0);
            items.get("boots").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                boots.addButton(bt);
            });

            i.set(0);
            items.get("spade").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                spade.addButton(bt);
            });

            i.set(0);
            items.get("axe").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                axe.addButton(bt);
            });

            i.set(0);
            items.get("sword").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                sword.addButton(bt);
            });

            i.set(0);
            items.get("pickaxe").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                pickaxe.addButton(bt);
            });

            i.set(0);
            items.get("spickaxe").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                spickaxe.addButton(bt);
            });

            i.set(0);
            items.get("bow").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                bow.addButton(bt);
            });

            i.set(0);
            items.get("rod").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                fish_rod.addButton(bt);
            });

            i.set(0);
            items.get("shears").forEach(itemStack -> {
                MenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                bt.setClickEvent(event -> give(itemStack, event.getWhoClicked())); bt.setSlot(i.getAndIncrement());
                shears.addButton(bt);
            });

            MenuButton h = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.LEATHER_HELMET).name("&aШлем").build().item(), "&eВыберите предмет прокачки &7• &eШлем");
            h.setSlot(11); h.setClickEvent(event -> event.getWhoClicked().openInventory(helmet.build())); menu.addButton(h);

            MenuButton ch = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.LEATHER_CHESTPLATE).name("&aНагрудник").build().item(), "&eВыберите предмет прокачки &7• &eНагрудник");
            ch.setSlot(20); ch.setClickEvent(event -> event.getWhoClicked().openInventory(chestplate.build())); menu.addButton(ch);

            MenuButton leg = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.LEATHER_LEGGINGS).name("&aШтаны").build().item(), "&eВыберите предмет прокачки &7• &eШтаны");
            leg.setSlot(29); leg.setClickEvent(event -> event.getWhoClicked().openInventory(legs.build())); menu.addButton(leg);

            MenuButton boot = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.LEATHER_BOOTS).name("&aБотинки").build().item(), "&eВыберите предмет прокачки &7• &eБотинки");
            boot.setSlot(38); boot.setClickEvent(event -> event.getWhoClicked().openInventory(boots.build())); menu.addButton(boot);

            MenuButton sw = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.WOOD_SWORD).name("&aМеч").build().item(), "&eВыберите предмет прокачки &7• &eМеч");
            sw.setSlot(21); sw.setClickEvent(event -> event.getWhoClicked().openInventory(sword.build())); menu.addButton(sw);

            MenuButton pic = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.WOOD_PICKAXE).name("&aКирка").build().item(), "&eВыберите предмет прокачки &7• &eКирка");
            pic.setSlot(19); pic.setClickEvent(event -> event.getWhoClicked().openInventory(pickaxe.build())); menu.addButton(pic);

            MenuButton spic = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.GOLD_PICKAXE).name("&aУлучшенная кирка").build().item(), "&eВыберите предмет прокачки &7• &eУлучшенная кирка");
            spic.setSlot(14); spic.setClickEvent(event -> event.getWhoClicked().openInventory(spickaxe.build())); menu.addButton(spic);

            MenuButton ax = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.WOOD_AXE).name("&aТопор").build().item(), "&eВыберите предмет прокачки &7• &eТопор");
            ax.setSlot(15); ax.setClickEvent(event -> event.getWhoClicked().openInventory(axe.build())); menu.addButton(ax);

            MenuButton fish = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.FISHING_ROD).name("&aУдочка").build().item(), "&eВыберите предмет прокачки &7• &eУдочка");
            fish.setSlot(16); fish.setClickEvent(event -> event.getWhoClicked().openInventory(fish_rod.build())); menu.addButton(fish);

            MenuButton b = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.BOW).name("&aЛук").build().item(), "&eВыберите предмет прокачки &7• &eЛук");
            b.setSlot(23); b.setClickEvent(event -> event.getWhoClicked().openInventory(bow.build())); menu.addButton(b);

            MenuButton sp = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.WOOD_SPADE).name("&aЛопата").build().item(), "&eВыберите предмет прокачки &7• &eЛопата");
            sp.setSlot(24); sp.setClickEvent(event -> event.getWhoClicked().openInventory(spade.build())); menu.addButton(sp);

            MenuButton sh = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.SHEARS).name("&aНожницы").build().item(), "&eВыберите предмет прокачки &7• &eНожницы");
            sh.setSlot(25); sh.setClickEvent(event -> event.getWhoClicked().openInventory(shears.build())); menu.addButton(sh);

            MenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()); back.setSlot(26);
            spade.addButton(back); axe.addButton(back); pickaxe.addButton(back); spickaxe.addButton(back);
            sword.addButton(back); bow.addButton(back); fish_rod.addButton(back); shears.addButton(back);
            helmet.addButton(back); chestplate.addButton(back); legs.addButton(back); boots.addButton(back);

            IMenuButton backs = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(53);
            backs.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
            menu.addButton(backs);

            Vars.sendSystemMessage(TypeMessage.SUCCESS, "All upgrade items were loaded successfully to menu!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with load upgrade items in menu!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    private static void give(ItemStack is, Player player) {
        if (player.hasPermission("prison.admin")) {
            player.getInventory().addItem(is);
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 15, 15);
        }
    }

    @Override
    public int getRows() { return 6; }

    @Override
    public String getName() { return ChatColor.YELLOW + "Выберите предмет прокачки"; }
}
