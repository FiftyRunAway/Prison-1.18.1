package org.runaway.requirements;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;

import java.util.Locale;

@Getter
public class LocalizedBlock {
    String normalName;
    ItemStack itemStack;

    public LocalizedBlock(ItemStack itemStack) {
        this.itemStack = itemStack;
        initNormalName();
    }

    public LocalizedBlock(Material material) {
        this.itemStack = new ItemStack(material);
        initNormalName();
    }

    private void initNormalName() {
        String result = "block.minecraft." + this.itemStack.getType().toString().toLowerCase(Locale.ROOT); // TODO check it
        this.normalName = Prison.getInstance().getKeys().getProperty(result);
    }

    public int getAmount(Gamer gamer) {
        return gamer.getCurrentBlocks(getItemStack().getType().toString());
    }
}
