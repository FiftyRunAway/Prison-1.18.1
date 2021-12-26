package org.runaway.requirements;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;

@Getter
public class LocalizedBlock {
    String normalName;
    ItemStack itemStack;

    public LocalizedBlock(ItemStack itemStack) {
        this.itemStack = itemStack;
        initNormalName();
    }

    public LocalizedBlock(Material material, short data) {
        this.itemStack = new ItemStack(material, 1, data);
        initNormalName();
    }

    public LocalizedBlock(Material material) {
        this.itemStack = new ItemStack(material);
        initNormalName();
    }

    private void initNormalName() {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(this.itemStack);
        String result = nmsStack.a() + ".name";
        this.normalName = Prison.getInstance().getKeys().getProperty(result);
    }

    public int getAmount(Gamer gamer) {
        return gamer.getCurrentBlocks(getItemStack().getType().toString(), getItemStack().getDurability());
    }
}
