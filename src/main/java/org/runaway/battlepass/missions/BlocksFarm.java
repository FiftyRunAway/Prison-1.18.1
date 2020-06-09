package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.enums.TypeMessage;
import org.runaway.events.custom.PlayerBlockBreakEvent;
import org.runaway.mines.Mine;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

public class BlocksFarm extends IMission implements Listener {

    private String mine_name;
    private Mine mine;

    private ItemStack block;
    private String block_name;

    private String preloaded_desc;

    @EventHandler
    private void onBlockFarm(PlayerBlockBreakEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        BattlePass.missions.forEach(weeklyMission -> weeklyMission.getMissions().forEach(mission -> {
            if (mission.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
                if (!mission.isCompleted(gamer)) {
                    BlocksFarm bf = (BlocksFarm) mission;
                    if (bf.block != null) {
                        if (bf.block.getType().equals(event.getBlock().getType()) && bf.block.getType().getMaxDurability() == event.getBlock().getType().getMaxDurability()) {
                            if (bf.mine == null || event.fromMine(bf.mine))
                                bf.addValue(gamer);
                        }
                    } else {
                        if (bf.mine == null || event.fromMine(bf.mine))
                            bf.addValue(gamer);
                    }
                }
            }
        }));
    }

    @Override
    public void addValue(Gamer gamer) {
        int result = (int)getValues().get(gamer.getGamer()) + Math.toIntExact(Math.round(gamer.getBoosterBlocks()));
        getValues().put(gamer.getGamer(), result);

        checkLevel(gamer);
    }

    @Override
    protected void init() {
        String block_string = this.getDescriptionDetails()[1].toString().toLowerCase();

        this.block = null;
        this.mine = getMineString(this.getDescriptionDetails()[2].toString().toLowerCase());
        if (this.mine != null) this.mine_name = ChatColor.GRAY + ChatColor.stripColor(Utils.colored(Utils.upCurLetter(this.mine.getName(), 1)));

        if (!block_string.equals("null") && !block_string.equals("none")) {
            String[] splt = block_string.split("#");
            this.block_name = ChatColor.GRAY + ChatColor.stripColor(Utils.colored(splt[1].replace("_", " ")));
            try {
                String[] split = splt[0].split("-");
                this.block = new ItemStack(Material.valueOf(split[0].toUpperCase()), 1, Short.parseShort(split[1]));
            } catch (IllegalArgumentException ex) {
                Vars.sendSystemMessage(TypeMessage.ERROR, "Illegal argument for BlocksFarm mission - " + block_string);
            }
        }
        this.preloaded_desc = "Ломайте "
                + (this.block == null ? "любые блоки" : (ChatColor.GRAY + this.block_name)) + " на "
                + (this.mine == null ? "любой шахте" : "шахте " + this.mine_name);
    }

    @Override
    public String getDescription() {
        return this.preloaded_desc;
    }

    @Override
    public String getArgumentsString() {
        return "blocks_value block_pattern|none mine_name|none";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
