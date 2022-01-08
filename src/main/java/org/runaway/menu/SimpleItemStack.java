package org.runaway.menu;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;

@Builder @Getter
public class SimpleItemStack {
    private Material material;
    private int durability;
}
