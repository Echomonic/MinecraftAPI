package minecraft.api.minecraftapi.utils;


import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ClickableChat {

    private final String var0;

    private TextComponent var1;

    public ClickableChat(String var0){
        this.var0 = var0;

        var1 = new TextComponent(Colorize.color(var0));
    }

    /*
     * use new ClickEvent as it has a constructor.
     */
    public ClickableChat setClickEvent(ClickEvent.Action var2, String var3){
        this.var1.setClickEvent(new ClickEvent(var2,var3));

        return this;
    }
    public ClickableChat setHoverEvent(HoverEvent.Action var2, String var3){
        this.var1.setHoverEvent(new HoverEvent(var2,new ComponentBuilder(Colorize.color(var3)).create()));

        return this;
    }
    public ClickableChat setText(String var0){
        var1.setText(Colorize.color(var0));

        return this;
    }
    public void sendToPlayer(Player var5){

        var5.spigot().sendMessage(var1);
    }

    public TextComponent getTextComponent(){


        return var1;
    }
}
