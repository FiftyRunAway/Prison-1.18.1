package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.MoneyType;
import org.runaway.enums.TypeMessage;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.quests.MinesQuest;
import org.runaway.utils.Lore;
import org.runaway.utils.Vars;

import java.util.ArrayList;

public class MinesQuestMenu implements IMenus {

    public static StandardMenu getMenu(Player p, MinesQuest quest) {
        Gamer gamer = GamerManager.getGamer(p);
        if (quest.getContent().size() * 9 > 54) {
            gamer.sendMessage(EMessage.DISFUNCTION);
            return null;
        }
        StandardMenu menu = StandardMenu.create(quest.getContent().size(),
                ChatColor.YELLOW + "Задания шахты: " + quest.getName());
        int r = 0, am = 1;
        for (String s : quest.getContent()) {
            String[] var = s.split(" ");
            Material mat = null;
            String name = null;
            ItemStack is = null;
            ItemMeta im = null;
            int data = 0;
            try {
                mat = Material.getMaterial(var[0].split(":")[0]);
                name = String.valueOf(var[1]).replaceAll("_", " ");

                is = new ItemStack(mat);
                im = is.getItemMeta();
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + "Квест на блок &e" + name));

            } catch (Exception e) {
                Vars.sendSystemMessage(TypeMessage.ERROR, "Problem with mine quests config: " + quest.getCommandName());
                e.printStackTrace();
            }
            int playerLevel = getLevel(p, mat);
            int blocksBroken = gamer.getCurrentBlocks(mat.toString());
            assert is != null;
            for (int in = r * 9; in < (r + 1) * 9; in++) {
                ItemStack itemStack = is.clone();
                itemStack.setAmount(am);
                ItemMeta itemMeta = im.clone();

                ArrayList<String> lores = new ArrayList<>();
                lores.add(" ");
                boolean completed = false;
                if (playerLevel >= am) {
                    lores.add("§7Статус: §eВыполнено");
                    completed = true;
                } else if (playerLevel + 1 == am) {
                    lores.add("§7Статус: §2Ожидание выполнения");
                } else {
                    lores.add("§7Статус: §cНе выполнено");
                }
                if (!completed) {
                    lores.add("§7Награда:");
                    double needm = getMoney(am, quest.getStepMoney());
                    lores.add(" §7• §eДеньги: §a" + Math.ceil(needm) + " " + MoneyType.RUBLES.getShortName());
                    lores.add("§7Требования:");
                    double need = getBlocks(am, quest.getStartBlocks());
                    lores.add(" §7• §eБлоки: " + (blocksBroken < need ? "§c" : "§2") + blocksBroken + "/" + Math.round(need));
                    itemMeta.setLore(lores);
                    itemStack.setItemMeta(itemMeta);

                    if (playerLevel + 1 == am) {
                        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
                        Material finalMat = mat;
                        int finalAm = am;
                        String finalName = name;
                        btn.setClickEvent(event -> {
                            Player player = event.getWhoClicked();
                            Gamer g = GamerManager.getGamer(player);
                            if (g.getCurrentBlocks(finalMat.toString()) < getBlocks(finalAm, quest.getStartBlocks())) {
                                g.sendMessage(EMessage.LEVELNEEDBLOCKS);
                                return;
                            }
                            upLevel(player, finalMat);
                            player.closeInventory();
                            double money = getMoney(finalAm, quest.getStepMoney());
                            g.sendTitle("&fКвест &e" + finalName.toLowerCase() + " &fвыполнен!",
                                    "&8[&a+" + money + " " + MoneyType.RUBLES.getShortName() + "&8]");
                            g.depositMoney(money, true);
                        });
                        menu.addButton(btn.setSlot(in));
                    } else {
                        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(itemStack).setSlot(in));
                    }
                } else {
                    menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(
                            new Item.Builder(Material.BARRIER)
                                    .lore(new Lore.BuilderLore().addList(lores).build())
                                    .name("&7Квест на блок &e" + name)
                                    .amount(am)
                                    .data((short) data)
                                    .build().item()).setSlot(in));
                }
                am++;
            }
            am = 1;
            r++;
        }
        return menu;
    }

    private static int getLevel(Player player, Material material) {
        try {
            return GamerManager.getGamer(player).getMineQuests().getOrDefault(material.name(), 0);
        } catch (Exception e) {
            return 0;
        }
    }

    private static void upLevel(Player player, Material material) {
        Gamer gamer = GamerManager.getGamer(player);
        gamer.getMineQuests().put(material.name(), getLevel(player, material) + 1);
    }

    private static double getMoney(int numberStep, double step) {
        return Math.pow(Math.ceil(step), numberStep);
    }

    private static double getBlocks(int numberStep, int startBlocks) {
        return Math.pow(2, startBlocks + numberStep);
    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }
}
