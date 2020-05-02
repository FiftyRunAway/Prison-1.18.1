package org.runaway;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FancyText {

    private String text;
    int letters;
    private EnumChatFormat format;

    public FancyText(String base, EnumChatFormat format) {
        this.text = "{\"text\":\"" + "" + "\"" + ",\"extra\":[" + "{\"text\":\"" + base + "\"}";
        this.format = format;
    }

    public FancyText addText(String message) {
        this.text = text + (",{\"text\":\" " + message + "\"}");
        return this;
    }

    public void addHoverableText(String hoverable, String message) {
        this.text = text + ",{\"text\":\"" + hoverable + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + message + "\"}}";
    }

    public ClickableFancyText addClickableText(String clickable) {
        this.text = text + ",{\"text\":\"" + clickable + "\",\"clickEvent\":";
        return new ClickableFancyText(this);
    }

    public FancyText closeText() {
        this.text = text + "]}";
        return this;
    }

    public void sendText(Player player) {
        IChatBaseComponent chat = IChatBaseComponent.ChatSerializer.a(text);
        assert chat != null;
        chat.getChatModifier().setColor(format);
        PacketPlayOutChat packet = new PacketPlayOutChat(chat, ChatMessageType.CHAT);
        Nms.getCraftPlayer(player).playerConnection.sendPacket(packet);
    }

    static class Nms {
        public EnumChatFormat getNmsColor(ChatColor chat){
            return EnumChatFormat.valueOf(chat.name());
        }

        static EntityPlayer getCraftPlayer(Player player){
            return ((CraftPlayer)player).getHandle();
        }
    }

    public static class ClickableFancyText {
        FancyText text;

        ClickableFancyText(FancyText instance){
            text = instance;
        }

        public ClickableFancyText openlink(String url){
            text.text = text.text + "{action:open_url,value:" + url + "}";
            return this;
        }


        public ClickableFancyText runCommand(String command){
            text.text = text.text + "{\"action\":\"run_command\",\"value\":\"" + command + "\"}";
            return this;
        }

        public ClickableFancyText chatSuggestion(String chat){
            text.text = text.text + "{\"action\":\"suggest_command\",\"value\":\"" + chat + "\"}";
            return this;
        }

        public ClickableFancyText openFile(String file){
            text.text = text.text + "{\"action\":\"open_file\",\"value\":\"" + file + "\"}";
            return this;
        }

        public ClickableFancyText then() {
            text.text = text.text + ",";
            return this;
        }

        public void close() {
            text.text = text.text + "}";

        }
    }
}
