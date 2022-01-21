package org.runaway.utils;

import lombok.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.runaway.Gamer;

@Builder
public class Text {
    private String eventValue, hover, message, suffix;
    private ClickEvent.Action action;

    public static void send(Text text, Gamer gamer) {
        if (text.suffix != null) {
            text.message = text.message + " " + text.suffix;
        }
        TextComponent component = new net.md_5.bungee.api.chat.TextComponent(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(Utils.colored(
                Vars.getPrefix() + text.message)));
        if (text.hover != null)
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.
                    Text(Utils.colored(text.hover))));
        if (text.action.equals(ClickEvent.Action.RUN_COMMAND)) {
            text.eventValue = "/" + text.eventValue;
        } else if (text.action.equals(ClickEvent.Action.OPEN_URL)) {
            text.eventValue = "https://" + text.eventValue;
        }
        component.setClickEvent(new ClickEvent(text.action, text.eventValue));
        gamer.getPlayer().spigot().sendMessage(component);
    }
}
