package org.runaway.battlepass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.items.Item;
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

    public Item.Builder getIcon(Gamer gamer) {
        Material mat = this.mission.isCompleted(gamer) ? Material.MAGENTA_STAINED_GLASS_PANE :
                (this.mission.isPinned(gamer) ? Material.BLUE_STAINED_GLASS_PANE : Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        return new Item.Builder(mat)
                .name(ChatColor.GREEN + mission.getName())
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString(mission.getDescription() +
                            " • " + ChatColor.AQUA +
                                (this.mission.isCompleted(gamer) ? mission.getValue() : gamer.getBpData().getOrDefault(mission.hashCode(), 0)) + ChatColor.GRAY +
                            " / " + ChatColor.AQUA + mission.getValue())
                        .addString(ChatColor.GRAY + "Опыт • " + ChatColor.GREEN + mission.getExperience())
                        .build());
    }
}
