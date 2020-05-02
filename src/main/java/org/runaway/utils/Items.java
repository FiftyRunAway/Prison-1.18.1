package org.runaway.utils;

/*
 * Created by _RunAway_ on 23.1.2019
 */

public abstract class Items {

    private final short data;
    private final int amount;
    private final String name;
    private final Lore lore;
    private final boolean unbreakable;

    public abstract static class Builder<T extends Builder<T>> {
        private short data = 0;
        private int amount = 1;
        private String name = "";
        private Lore lore = null;
        private boolean unbreakable = false;

        public T data(short data) {
            this.data = data;
            return self();
        }

        public T amount(int amount) {
            this.amount = amount;
            return self();
        }

        public T name(String name) {
            this.name = Utils.colored(name);
            return self();
        }

        public T lore(Lore lore) {
            this.lore = lore;
            return self();
        }

        public T unbreakable() {
            this.unbreakable = unbreakable;
            return self();
        }

        protected abstract T self();
        public abstract Items build();
    }

    public Items(Builder<?> builder) {
        data = builder.data;
        amount = builder.amount;
        name = builder.name;
        lore = builder.lore;
        unbreakable = builder.unbreakable;
    }

    public short getData() {
        return data;
    }

    public int getAmount() {
        return amount;
    }

    public Lore getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    //
//    public ItemStack item() {
//        ItemStack stack = new ItemStack(this.material, this.amount);
//        stack.setDurability(this.data);
//        ItemMeta meta = stack.getItemMeta();
//        meta.setDisplayName(this.name);
//        if (this.lore != null) meta.setLore(this.lore.getList());
//        stack.setItemMeta(meta);
//        return stack;
//    }
//
//    public static ItemStack glass(int data) {
//        return glass(data, "");
//    }
//
//    public static ItemStack glass(int data, String name) {
//        return new BuilderItems(Material.STAINED_GLASS_PANE).data((short) data).name(name).build().item();
//    }
//
//    // APPLE-0-&7Обычное_яблочко-4-/&cЛучший предмет/&5Всея Руси!
//    public static ItemStack unserializerString(String s) {
//        String[] split = s.split("-");
//        ArrayList<String> lores = null;
//        if (!split[4].equals("null")) {
//            String[] lore = split[4].split("/");
//            lores = new ArrayList<>();
//            Collections.addAll(lores, lore);
//        }
//        return new BuilderItems(Material.valueOf(split[0])).
//                data(Short.parseShort(split[1])).
//                amount(Integer.parseInt(split[3])).
//                name(split[2]).lore(new Lore.BuilderLore().addList(lores).build())
//                .build().item();
//    }
}
