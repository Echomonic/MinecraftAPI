package minecraft.api.minecraftapi.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GlassType {

    BLACK_STAINED_GLASS_PANE(new ItemBuilder(Material.STAINED_GLASS_PANE,(byte)15).build()),
    STAINED_GLASS_PANE(new ItemBuilder(Material.STAINED_GLASS_PANE).build()),
    BLUE_STAINED_GLASS_PANE(new ItemBuilder(Material.STAINED_GLASS_PANE,(byte)11).build()),
    LIME_STAINED_GLASS_PANE(new ItemBuilder(Material.STAINED_GLASS_PANE,(byte)5).build()),
    DARK_GRAY_STAINED_GLASS_PANE(new ItemBuilder(Material.STAINED_GLASS_PANE,(byte)8).build()),
    RED_STAINED_GLASS_PANE(new ItemBuilder(Material.STAINED_GLASS_PANE,(byte)14).build()),
    CYAN_STAINED_GLASS_PANE(new ItemBuilder(Material.STAINED_GLASS_PANE,(byte)9).build()),

    ;
    private final ItemStack stack;

    GlassType(ItemStack stack){
        this.stack = stack;
    }

    public ItemStack getGlassType() {
        return stack;
    }
}
