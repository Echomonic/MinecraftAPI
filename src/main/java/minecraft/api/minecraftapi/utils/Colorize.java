package minecraft.api.minecraftapi.utils;

import com.google.common.base.Strings;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Colorize {

    public static void message(String message, CommandSender sender){

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String color(String message){

        String split = ChatColor.translateAlternateColorCodes('&', message).replace("%%bold%%", ChatColor.BOLD.toString());

        split.split("\n");


        return split;
    }

    public static void actionBar(String text, Player player){
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"),(byte)2);

        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
    //    public static void title(String text, Player player, String textSub){
//        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"));
//        PacketPlayOutTitle packetSub = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', textSub) + "\"}"));
//
//
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetSub);
//
//    }
//    public static void title(String text, String textSub){
//        for(Player player : Bukkit.getOnlinePlayers()) {
//            PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"));
//            PacketPlayOutTitle packetSub = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', textSub) + "\"}"));
//
//
//            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSub);
//        }
//    }
    public static void sendErrorMessage(CommandSender player, ErrorMessage message){
        player.sendMessage(message.getMessage());
    }

    public static String getProgressBar(int current, int max, int totalBars, String nonCompletedSymbol, String completedSymbol, ChatColor completedColor,
                                        ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + completedSymbol, progressBars)
                + Strings.repeat("" + notCompletedColor + nonCompletedSymbol, totalBars - progressBars);
    }

    public static List<String> colorizeList(List<String> lore){




        return lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }
    static String[] colors = new String[]{"&c", "&e", "&a", "&3", "&9"};

    public static String rainbowString(String s){



        return new Rainbow(s).getText();
    }
    private static class Rainbow {

        private int place = 0;
        private String text = "You did not provide any text.";
        private String fancyText = "§You did not provide any text"; // gets reset anyway
        //Arrays.asList("§4", "§c", "§6", "§e", "§a", "§2", "§b", "§3", "§5", "§d");
        private final List<String> RAINBOW = Arrays.asList("§c", "§e", "§a", "§3", "§9"); // 10 strings

        public Rainbow(String text){
            place = 0;
            if(text != null){
                this.text = text;
            }
            updateFancy();
        }
        private void updateFancy(){
            int spot = place;
            String fancyText = "";
            for(char l : text.toCharArray()){
                String letter = Character.toString(l);
                if(!letter.equalsIgnoreCase(" ")){
                    String t1 = fancyText;
                    fancyText = t1 + RAINBOW.get(spot) + letter;
                    if(spot == RAINBOW.size() - 1){
                        spot = 0;
                    } else{
                        spot++;
                    }
                } else {
                    String t1 = fancyText;
                    fancyText = t1 + letter;
                }
            }
            this.fancyText = fancyText;
        }

        public void moveRainbow(){
            if(RAINBOW.size() - 1 == place){
                place = 0;
            } else {
                place++;
            }
            updateFancy();
        }
        public String getOriginalText(){
            return text;
        }
        public String getText(){
            return fancyText;
        }
        public void setPlace(int place){
            if(place > RAINBOW.size() - 1){
                return;
            }
            this.place = place;
            updateFancy();
        }
        public int getPlace(){
            return place;
        }
        public List<String> getRainbow(){
            return RAINBOW;
        }

    }

}
