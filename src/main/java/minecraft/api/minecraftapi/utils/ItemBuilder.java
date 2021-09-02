package minecraft.api.minecraftapi.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class ItemBuilder {


    private ItemStack stack;

    public ItemBuilder(Material mat) {
        stack = new ItemStack(mat);
    }

    public ItemBuilder(Material mat, short sh) {
        stack = new ItemStack(mat, 1, sh);
    }

    /**
     *
     * @return items
     *
     * @implNote getting the the meta of the item
     */

    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }

    /**
     *
     * @param color, used for setting the color of leather armor
     * @return this
     *
     */
    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setType(Material material) {
        stack.setType(material);
        return this;
    }


    /**
     *
     * @param glow, Adding a enchantment then removing it giving it the glint.
     * @return this
     *
     */

    public ItemBuilder setGlow (boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }
        return this;
    }

    /**
     *
     * @param unbreakable, Just allows the item to not break/have durability.
     * @return this
     */
    public ItemBuilder setUnbreakable (boolean unbreakable) {
        ItemMeta meta = stack.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     *
     * @param color, To set the the banner color
     * @return this
     *
     */
    public ItemBuilder setBannerColor (DyeColor color) {
        BannerMeta meta = (BannerMeta) stack.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta(meta);
        return this;
    }

    /**
     *
     * @param amount, sets the amount given to the player or how
     *                much is put in a gui
     * @return this
     *
     */
    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(Colorize.color(displayname));
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemStack (ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(ArrayList<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore (String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setHeadTexture(String url){
        ItemMeta headMeta = getItemMeta();

        if(url.isEmpty()){
            return this;
        }
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        setItemMeta(headMeta);
        return this;
    }
    public ItemStack build() {
        return stack;
    }

}
