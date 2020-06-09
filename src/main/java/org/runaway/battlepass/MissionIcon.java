package org.runaway.battlepass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.utils.Items;
import org.runaway.utils.Lore;

public class MissionIcon extends Items {

    private final IMission mission;

    public static class Builder extends Items.Builder<MissionIcon.Builder> {
        private final IMission mission;

        Builder(IMission mission) {
            this.mission = mission;
        }

        @Override public MissionIcon build() { return new MissionIcon(this); }

        @Override protected MissionIcon.Builder self() {
            return this;
        }
    }

    private MissionIcon(MissionIcon.Builder builder) {
        super(builder);
        this.mission = builder.mission;
    }

    public ItemStack getIcon(Gamer gamer) {
        return new Item.Builder(Material.STAINED_GLASS_PANE)
                .data((short) (this.mission.isCompleted(gamer) ? 4 : 5))
                .name(ChatColor.GREEN + mission.getName())
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString(ChatColor.GRAY + mission.getDescription() +
                            " • " + ChatColor.AQUA +
                                (this.mission.isCompleted(gamer) ? mission.getValue() : mission.getValues().get(gamer.getGamer())) + ChatColor.GRAY +
                            " / " + ChatColor.AQUA + mission.getValue())
                        .addString(ChatColor.GRAY + "Опыт • " + ChatColor.GREEN + mission.getExperience())
                        .build())
                .build().item();
    }
}
