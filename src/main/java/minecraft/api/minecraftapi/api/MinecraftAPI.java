package minecraft.api.minecraftapi.api;

import com.google.common.reflect.ClassPath;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import minecraft.api.minecraftapi.Main;
import minecraft.api.minecraftapi.utils.Colorize;
import minecraft.api.minecraftapi.utils.GlassType;
import minecraft.api.minecraftapi.utils.Size;
import minecraft.api.minecraftapi.utils.SkinSetter;
import io.netty.handler.codec.DecoderException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class MinecraftAPI {

    public static String VERSION = Bukkit.getVersion().getClass().getName().split("\\.")[3];
    public static Version LOCAL_VERSION = Version.get(VERSION);

    public enum Version {
        v1_8("1_8", 0),
        v1_9("1_9", 1),
        v1_10("1_10", 2),
        v1_11("1_11", 3),
        v1_12("1_12", 4),
        v1_13("1_13", 5),
        v1_14("1_14", 6),
        v1_15("1_15", 7),
        v1_16("1_16", 8),
        v1_17("1_17", 9),
        v1_18("1_18", 10),
        v1_19("1_19", 11);

        private final int order;
        private final String key;

        Version(String key, int v) {
            this.key = key;
            order = v;
        }

        /**
         * @param other Checking if the server is running a version equal to or higher than the other.
         * @return Returns if the var order is >= than {@param other}
         */
        public boolean greaterThanOrEqualTo(Version other) {
            return order >= other.order;
        }

        /**
         * @param other Checking if the server is running a version equal to or lower than the {@param other}.
         * @return Returns if the var order is >= than {@param other}
         */

        public boolean lessThanOrEqualTo(Version other) {
            return order <= other.order;
        }

        /**
         * @param v: specified version then is translated into a key of the version.
         * @return Null if {@param v} doesn't exist other wise return the k.
         */

        public static Version get(String v) {
            for (Version k : Version.values()) {
                if (v.contains(k.key)) {
                    return k;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public static class PacketAPI {

        /**
         * @param player   The receiver of the title packet.
         * @param text     The text that you want to be showed on the title.
         * @param colorize True if you want the text to be able to be colored other wise false.
         */

        private static void sendTitle(Player player, String text, boolean colorize) {
            try {
                Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                if (colorize) {
                    Object titleChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(enumTitle, "{\"text\":\"" +
                            ChatColor.translateAlternateColorCodes('&', text) + "\"}");

                    Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("PacketPlayOutChat").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                    Object sentPacket = constructor.newInstance(enumTitle, titleChat, 1, 1, 1);
                    sendPacket(player, sentPacket);
                }  else {
                    Object titleChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(enumTitle, "{\"text\":\"" + text + "\"}");

                    Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("PacketPlayOutChat").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                    Object sentPacket = constructor.newInstance(enumTitle, titleChat, 1, 1, 1);
                    sendPacket(player, sentPacket);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        static HashMap<Player, Double> health = new HashMap<>();
        static HashMap<Player, Location> loc = new HashMap<>();


        public static void setSkin(Player player, String skinName){

            SkinSetter setter = new SkinSetter();


            String uuidData = setter.get("https://api.mojang.com/users/profiles/minecraft/%s" , skinName);
            String uuid = setter.getUUID(uuidData);
            String skinData = setter.get("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false" , uuid);
            String skin = setter.getSkin(skinData);
            String signature = setter.getSig(skinData);


            CraftPlayer cp = ((CraftPlayer) player);

            GameProfile profile = cp.getProfile();

            sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle()));
            sendPacketNotFor(player,new PacketPlayOutEntityDestroy(cp.getEntityId()));

            try {
                profile.getProperties().removeAll("textures");
                profile.getProperties().put("textures", new Property("textures", skin,signature));
            }catch (DecoderException | InternalException | IndexOutOfBoundsException exception){
                player.sendMessage(ChatColor.RED + "There was a problem getting the skin!");
                return;
            }
            health.put(player,player.getHealth());
            loc.put(player,player.getLocation());


            cp.setHealth(0);

            cp.spigot().respawn();

            sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle()));


            cp.setHealth(health.get(player));

            new BukkitRunnable(){
                @Override
                public void run() {


                    sendPacketNotFor(player,new PacketPlayOutNamedEntitySpawn(cp.getHandle()));
                    cp.teleport(loc.get(player));

                }
            }.runTaskLater(Main.instance,1l);


        }

        public static void setSkinForOthers(Player player, String skinName){


            SkinSetter setter = new SkinSetter();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                onlinePlayer.hidePlayer(player);


                String uuidData = setter.get("https://api.mojang.com/users/profiles/minecraft/%s" , skinName);
                String uuid = setter.getUUID(uuidData);
                String skinData = setter.get("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false" , uuid);
                String skin = setter.getSkin(skinData);
                String signature = setter.getSig(skinData);


                CraftPlayer cp = ((CraftPlayer) player);

                GameProfile profile = cp.getProfile();


                try {
                    profile.getProperties().removeAll("textures");
                    profile.getProperties().put("textures", new Property("textures", skin,signature));
                }catch (DecoderException | InternalException | IndexOutOfBoundsException exception){
                    player.sendMessage(ChatColor.RED + "There was a problem getting the skin!");
                    return;
                }


                onlinePlayer.showPlayer(player);
            }

        }
        public static void setSkinForOthers(Player player, String[] signatureAndTexture){



            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                onlinePlayer.hidePlayer(player);

                String skin = signatureAndTexture[0];
                String signature = signatureAndTexture[1];

                CraftPlayer cp = ((CraftPlayer) player);

                GameProfile profile = cp.getProfile();


                try {
                    profile.getProperties().removeAll("textures");
                    profile.getProperties().put("textures", new Property("textures", skin,signature));
                }catch (DecoderException | InternalException | IndexOutOfBoundsException exception){
                    player.sendMessage(ChatColor.RED + "There was a problem getting the skin!");
                    return;
                }


                onlinePlayer.showPlayer(player);
            }

        }
        static void sendPacket(Packet<?> packet){
            for(Player player : Bukkit.getOnlinePlayers()){
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            }


        }
        static void sendPacketNotFor(Player player,Packet<?> packet){
            for(Player targets : Bukkit.getOnlinePlayers()){
                if(targets.getUniqueId().toString().equals(player.getUniqueId().toString())) continue;
                ((CraftPlayer)targets).getHandle().playerConnection.sendPacket(packet);
            }


        }

        /**
         * @param player   The player you want so send the actionbar to.
         * @param text     The text that you want to be showed on the action bar.
         * @param colorize True if you want the text to be able to be colored other wise false.
         */

        private static void sendActionbar(Player player, String text, boolean colorize) {
            try {
                Object aBString = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}");

                Object titleChat;
                if (colorize) {
                    titleChat = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(aBString, "{\"text\":\"" +
                            ChatColor.translateAlternateColorCodes('&', text) + "\"}");

                } else {
                    titleChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(aBString, "{\"text\":\"" + text + "\"}");

                }
                Constructor<?> actionBarConstructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
                sendPacket(player, actionBarConstructor.newInstance(titleChat, (byte) 2));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param player A "receiver" of the "packet" that is sent through reflection or normal packet outputs.
         * @param packet The "packet" that is sent to the player, the most recommended one is reflection and caching the methods.
         *               Keeps the development clean and multi-version.
         */
        private static void sendPacket(Player player, Object packet) {
            try {
                Object handle = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param player A "receiver" of the "packet" that is sent through reflection or normal packet outputs.
         * @param packet The "packet" that is sent to the player, the most recommended one is reflection and caching the methods.
         *               Keeps the development clean and multi-version.
         */
        private static void sendPacketNotFor(Player player, Object packet) {
            for(Player players : Bukkit.getOnlinePlayers()) {
                if(!player.getUniqueId().toString().equalsIgnoreCase(players.getUniqueId().toString())) {
                    sendPacket(players,packet);
                }else{
                    return;
                }
            }
        }

        /**
         * @param clazz The class that the jvm is searching for in the api/library (Spigot-API / Spigot-Server).
         * @return Returns the null if the class isn't found otherwise we return the nms class.
         * @throws ClassNotFoundException Throws just in-case the class is doesn't exist.
         */

        public static Class<?> getNMSClass(String clazz) throws ClassNotFoundException {
            String VERSION = Bukkit.getVersion().getClass().getName().split("\\.")[3];
            String NMS_PACKAGE = "net.minecraft.server";
            if (!clazz.isEmpty() || clazz != null) {
                return Class.forName(NMS_PACKAGE + VERSION + "." + clazz);
            }

            return null;
        }

        /**
         * @param clazz The class that the jvm is searching for in the api/library (Spigot-API / Spigot-Server).
         * @return Returns the null if the class isn't found otherwise we return the craftbukkit class.
         * @throws ClassNotFoundException Throws just in-case the class is doesn't exist.
         */
        public static Class<?> getCraftBukkitClass(String clazz) throws ClassNotFoundException {
            String VERSION = Bukkit.getVersion().getClass().getName().split("\\.")[3];
            String NMS_PACKAGE = "org.bukkit.craftbukkit";
            if (!clazz.isEmpty() || clazz != null) {
                return Class.forName(NMS_PACKAGE + VERSION + "." + clazz);
            }

            return null;
        }

        public static CommandMap getCommandMap(Server server) {

            try {
                Field field = server.getClass().getDeclaredField("commandMap");

                field.setAccessible(true);


                return (CommandMap) field.get(server);

            } catch (Exception e) {

            }


            return null;
        }

        public static World getPlayerWorld(Player player) {
            return player.getWorld();
        }
    }

    public static class CommandAPI {


        /**
         * @param fallBack Commands fallBack prefix, such as /bukkit:help, /hypixel:kaboom, and etc.
         * @param command  that is going to registered by the command map.
         * @implNote Doesn't require command section plugin.yml!
         */
        public static void registerCommand(String fallBack, Command command) {
            try {

                PacketAPI.getCommandMap(Bukkit.getServer()).register(fallBack, command);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * @param fallBack Commands fallBack prefix, such as /bukkit:help, /hypixel:kaboom, and etc.
         * @param commands all the commands that you want to be registered.
         * @implNote Doesn't require command section plugin.yml!
         */
        @SafeVarargs
        public static void registerCommands(String fallBack, Class<? extends Command>... commands) {
            for(Class<? extends Command> c : commands){
                try {
                    Objects.requireNonNull(PacketAPI.getCommandMap(Bukkit.getServer())).register(fallBack,c.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


        /**
         * @param sender if the sender is not a player we just send them a message and go on.
         */

        public static void isSenderPlayer(CommandSender sender) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to perform this command!");
                return;
            }
        }

        /**
         * @param sender that we block from using the specified arguments.
         * @param args used in the command method.
         * @param disabledArgs the arguments that are disabled.
         */

        public static void disabledArgsForConsole(CommandSender sender, String[] args, String... disabledArgs) {

            Arrays.stream(disabledArgs).filter(Objects::nonNull).forEach(disabled -> {
                if (args.length >= 1) {

                    for (int i = 0; true; i++) {

                        if ((args[i].isEmpty() || disabled.isEmpty()) && disabled == null) {
                            continue;
                        }
                        if (args[i].equalsIgnoreCase(disabled) && !(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "You must be a player to perform this command!");
                            return;
                        }
                        return;
                    }
                }
            });
        }

        /**
         *
         * @param command, The command that is going to be disabled when ever the player runs it.
         * @param event, The event is used so that we can check if the message contains the command!
         */
        public static void disabledCommand(String command, PlayerCommandPreprocessEvent event){
            Player player = event.getPlayer();

            if(event.getMessage().contains(command)){
                player.sendMessage(Colorize.color("&cThis command is currently disabled!"));
                event.setCancelled(true);
                return;
            }

        }


        /**
         *
         * @param commands, The commands that is going to be disabled when ever the player runs it.
         * @param event, The event is used so that we can check if the message contains the command!
         */
        public static void disabledCommands(PlayerCommandPreprocessEvent event, String... commands){
            Player player = event.getPlayer();

            Arrays.stream(commands).filter(Objects::nonNull).forEach(command ->{
                String[] args = event.getMessage().split(" ");
                String cmd = args[0].replace("/","").toLowerCase();
                if(cmd.equalsIgnoreCase(command)){
                    player.sendMessage(Colorize.color("&cThis command is currently disabled!"));
                    event.setCancelled(true);
                    return;
                }
            });

        }

        /**
         * @param sender that we block from using the specified arguments.
         * @param args used in the command method.
         * @param disabledArgs the arguments that are disabled.
         */

        public static void disabledArgsForAll(CommandSender sender, String[] args, String... disabledArgs) {

            Arrays.stream(disabledArgs).filter(String::isEmpty).forEach(disabled -> {
                if (args.length >= 1) {

                    for (int i = 0; true; i++) {

                        if ((args[i].isEmpty() || disabled.isEmpty()) && disabled == null) {
                            continue;
                        }
                        if (args[i].equalsIgnoreCase(disabled)) {
                            sender.sendMessage(ChatColor.RED + "This argument is still under development!");
                            return;
                        }
                        return;
                    }
                }
            });
        }
        @SneakyThrows
        public static void registerAllCommandsInPackage(String f,String a){
            final ClassLoader l = Main.instance.getClass().getClassLoader();

            ClassPath c = ClassPath.from(l);

            for(ClassPath.ClassInfo i : c.getTopLevelClasses(a)){
                Class<? extends Command> t = (Class<? extends Command>) i.load();

                    registerCommand(f,t.newInstance());

            }
        }
        /**
         * @param sender     The sender of the command that we are also checking.
         * @param permission The permission that we are checking.
         * @return If the sender has permission of {@param permission} return true other wise return false.
         */

        public static boolean senderHasPermission(CommandSender sender, String permission) {
            if(sender instanceof Player) {
                if (sender.hasPermission(permission)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param sender Used so that we can get the player instead of the player.
         * @return Player if the sender is not null, and if sender is not a console.
         */
        public static Player getPlayer(CommandSender sender) {
            try {
                if (!(sender instanceof Player)) {
                    return null;
                }
            }catch (Exception e){

            }
            return (Player) sender;
        }

    }

    public static abstract class Gui implements InventoryHolder {

        private final Inventory inventory;

        //How many slots there are going to be in the inventory.
        private int slots;
        //What the title of the gui is going to be. (Supports Color codes!)
        private String title;

        /**
         *
         * This also creates the gui.
         *
         * @param title for the gui.
         * @param slots for the gui.
         */
        public Gui(String title, Size slots){
            this.title = title;
            this.slots = slots.getGuiSize();
            inventory = Bukkit.createInventory(null,slots.getGuiSize(),ChatColor.translateAlternateColorCodes('&',title));
        }


        //Just an option if we ever want do to something with the other dev.echo.skyblock.guis.
        private InventoryType type;

        /**
         *
         * This also creates the gui but with the designated type.
         *
         * @param title for the gui.
         * @param type so that we can use the different types of dev.echo.skyblock.guis.
         */

        public Gui(String title, InventoryType type){
            this.title = title;
            this.type = type;
            inventory = Bukkit.createInventory(null,type,ChatColor.translateAlternateColorCodes('&',title));
        }

        /**
         *This method is apart of InventoryHolder and just needs an inventory to not return null.
         *
         * @return inventory so that the method doesn't return null.
         */

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        /**
         * Just sets the slots of the inventory.
         *
         *
         * @param slots If there is some animation in the gui you can set the slots then open in again and etc.
         */

        public void setSlots(int slots) {
            this.slots = slots;
        }

        /**
         *
         * @param title just sets the title of the gui.
         */

        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * Gets the title from the constructor.
         *
         *
         * @return title
         */

        public String getTitle() {
            return title;
        }

        /**
         * Gets the slots of the from the constructor.
         *
         *
         * @return slots
         */
        public int getSlots() {
            return slots;
        }

        /**
         *
         * @param startingSlot where is starts filling the slots with the list of items/itemStacks
         * @param itemStacks the items that are going to be put in the gui and go through the slots.
         */
        public void setItems(int startingSlot, ArrayList<ItemStack> itemStacks){
            Arrays.stream(itemStacks.toArray()).forEach(items ->{
                for(int i = startingSlot; i <= inventory.getSize(); i++){
                    if(!isSlotNull(i)){
                        inventory.setItem(i, (ItemStack) items);
                    }
                }
            });
        }

        /**
         * This just checks to see if the item is null or not.
         *
         * @return false if the item is null otherwise we return true.
         */
        public boolean isSlotNull(int slots){
            return inventory.getItem(slots) == null || inventory.getItem(slots).getType() == Material.AIR;
        }
        /**
         * This uses a array of integers instead of just a integer by it's self.
         *
         * @return false if the item is null otherwise we return true.
         */
        public boolean areSlotsNull(int... slots){
            for (PrimitiveIterator.OfInt it = Arrays.stream(slots).iterator(); it.hasNext(); ) {
                int slot = it.next();
                if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {

                    return true;
                }
            }
            return false;
        }
        /**
         * Just checks over an list of integers to see if the item in the inventory is null or not.
         *
         * @return false if the item is null otherwise we return true.
         */
        public boolean isSlotNull(List<Integer> slots) {
            for (int i = 0; i < slots.size(); i++) {
                if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {

                    return true;
                }
            }
            return false;
        }

        /**
         *
         * @param slot, where the item is going to get placed in the gui.
         * @param stack, The item that is getting inserted into the gui
         */
        public void addItem(int slot, ItemStack stack){
            if(isSlotNull(slot)) {
                inventory.setItem(slot, stack);
            }
        }
        public void setItem(int slot, ItemStack stack){
            inventory.setItem(slot, stack);
        }

        public void setItems(int slot, List<ItemStack> stack){
            for(int i = 0; i < stack.size(); i++) {
                inventory.setItem(slot, stack.get(i));
            }
        }


        /**
         *
         * @param slots, where the item is going to get placed in the gui.
         * @param stack, The item that is getting inserted into the gui
         */
        public void addItemToSlots(ItemStack stack, int... slots){
            for(PrimitiveIterator.OfInt it = Arrays.stream(slots).iterator(); it.hasNext();){
                int slot = it.next();
                inventory.setItem(slot,stack);
            }
        }

        /**
         *
         * @implNote Not finished gotta finish 2 more sizes.
         *
         * @param type, used so that there is continuity between classes.
         * @param size, to allow compatibility for the ring
         */


        public void ringInventory(GlassType type, Size size){
            ItemStack stack = type.getGlassType();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(" ");
            meta.setLore(null);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_POTION_EFFECTS);

            stack.setItemMeta(meta);
            switch (size){

                case FIFTY_FOUR:

                    for(int i = 0; i < inventory.getSize(); i++){
                        if(isSlotNull(i)){
                            inventory.setItem(i,stack);
                        }
                    }
                    for(int x = 10; x < 44; x++){
                        if(!isSlotNull(x)){
                            inventory.setItem(x,new ItemStack(Material.AIR));
                        }
                    }
                    addItemToSlots(stack,8,18,27,36,17,26,35,45);
                    break;
                case TWENTY_SEVEN:

                    for(int i = 0; i < inventory.getSize(); i++){
                        if(isSlotNull(i)){
                            inventory.setItem(i,stack);
                        }
                    }
                    for(int x = 10; x < 17; x++){
                        if(!isSlotNull(x)){
                            inventory.setItem(x,new ItemStack(Material.AIR));
                        }
                    }
                    break;
            }
        }

        /**
         *
         * @param player, that we send the gui to.
         */
        public void open(Player player){
            player.openInventory(inventory);
        }
    }

    public static class MobAPI{

        /**
         *
         * @param type, The type of entity that we are preventing to spawn.
         * @param event, Just to make the code work (got lazy xD).
         */

        public static void cancelMobTypeFromSpawning(EntityType type, CreatureSpawnEvent event){

            if(event.getEntity().getType().equals(type)){
                event.setCancelled(true);
                return;
            }
        }

    }

}
