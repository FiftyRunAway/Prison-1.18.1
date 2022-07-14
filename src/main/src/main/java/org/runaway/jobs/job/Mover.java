package org.runaway.jobs.job;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.TypeMessage;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.jobs.EJobs;
import org.runaway.jobs.Job;
import org.runaway.jobs.JobReq;
import org.runaway.jobs.JobRequriement;
import org.runaway.managers.GamerManager;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mover extends Job {

    public static final Material boxMaterial = Material.NOTE_BLOCK;
    protected static final List<Location> boxes = new ArrayList<>();
    protected static final List<Location> destinations = new ArrayList<>();

    private static void onSuccesfulDelivery(Gamer gamer) {
        gamer.setWithBox(false);
        gamer.getPlayer().removePotionEffect(PotionEffectType.SLOW);
        gamer.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        Job.addStatistics(gamer, JobRequriement.BOXES);
        double money = 0.42 * (Job.getLevel(gamer, EJobs.MOVER.getJob()) + 1);
        gamer.depositMoney(money, true);
    }

    public static void takeBoxListener(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Gamer gamer = GamerManager.getGamer(event.getPlayer());
        //Head listener
        if (Mover.getBoxes().contains(block.getLocation())) {
            if (!gamer.isInventory()) {
                gamer.sendMessage(EMessage.NOINVENTORY);
                return;
            }
            if (gamer.isWithBox()) {
                gamer.sendMessage(EMessage.TAKEBOXAWAY);
                return;
            }
            block.setType(Material.AIR);
            Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () ->
                    block.setType(Material.NOTE_BLOCK), 450L);
            gamer.addEffect(PotionEffectType.SLOW, 999999, 5);
            gamer.addItem("boxItem");
            gamer.setWithBox(true);
        }
    }

    public static void placeBoxListener(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Gamer gamer = GamerManager.getGamer(event.getPlayer());
        if (!gamer.isWithBox()) return;
        if (getDestinations().contains(block.getLocation())) {
            event.setBuild(true);
            onSuccesfulDelivery(gamer);
            Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () ->
                    block.setType(Material.AIR), 800L);
        } else {
            event.setBuild(false);
            event.setCancelled(true);
            gamer.sendMessage(EMessage.PLACEBOX);
        }
    }

    public static void saveBoxLocations() {
        List<String> locs = new ArrayList<>();
        List<String> destins = new ArrayList<>();
        boxes.forEach(box -> locs.add(Utils.serializeLocation(box)));
        destinations.forEach(destin -> destins.add(Utils.serializeLocation(destin)));

        EConfig.CONFIG.getConfig().set("moverBoxes", locs);
        EConfig.CONFIG.getConfig().set("moverDestinations", destins);
        EConfig.CONFIG.saveConfig();
    }

    public static void loadBoxLocations() {
        List<String> locs = EConfig.CONFIG.getConfig().getStringList("moverBoxes");
        List<String> destins = EConfig.CONFIG.getConfig().getStringList("moverDestinations");
        if (!locs.isEmpty()) {
            locs.forEach(loc -> boxes.add(Utils.unserializeLocation(loc)));
        }
        if (!destins.isEmpty()) {
            destins.forEach(destin -> destinations.add(Utils.unserializeLocation(destin)));
        }

        ItemStack boxItem = new ItemBuilder(Material.NOTE_BLOCK).name("&aЯщик").build();
        PrisonItem prisonItem = PrisonItem.builder()
                .vanillaName("boxItem")
                .vanillaItem(boxItem)
                .category(PrisonItem.Category.OTHER)
                .parameters(Arrays.asList(
                        ParameterManager.getMinLevelParameter(6), //мин лвл для использования предмета
                        ParameterManager.getRareParameter(PrisonItem.Rare.DEFAULT), //редкость предмета
                        ParameterManager.getCategoryParameter(PrisonItem.Category.OTHER))).build();
        ItemManager.addPrisonItem(prisonItem);

        Vars.sendSystemMessage(TypeMessage.SUCCESS, "Loaded " + locs.size() + " mover boxes locations");
    }

    public static List<Location> getBoxes() {
        return boxes;
    }

    public static List<Location> getDestinations() {
        return destinations;
    }

    @Override
    public int getLevel() {
        return 6;
    }

    @Override
    public String getName() {
        return "Грузчик";
    }

    @Override
    public String getDescrition() {
        return "Выгружайте ящики из грузовиков";
    }

    @Override
    public Material getMaterial() {
        return Material.CHEST_MINECART;
    }

    @Override
    public ArrayList<JobReq[]> getLevels() {
        ArrayList<JobReq[]> reqs = new ArrayList<>();
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 25) });
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 100),
                new JobReq(JobRequriement.MONEY, 50),
                new JobReq(JobRequriement.LEVEL, 15)});
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 250),
                new JobReq(JobRequriement.MONEY, 150),
                new JobReq(JobRequriement.LEVEL, 16)});
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 500),
                new JobReq(JobRequriement.MONEY, 200),
                new JobReq(JobRequriement.LEVEL, 17)});
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 750),
                new JobReq(JobRequriement.MONEY, 350),
                new JobReq(JobRequriement.LEVEL, 18)});

        return reqs;
    }

    @Override
    public String getConfigName() {
        return "mover";
    }

    @Override
    public JobRequriement getMainRequriement() {
        return JobRequriement.BOXES;
    }
}
