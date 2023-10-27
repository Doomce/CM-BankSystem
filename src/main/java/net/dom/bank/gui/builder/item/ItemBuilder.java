// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.item;

import org.bukkit.OfflinePlayer;
import java.lang.reflect.Field;
import org.bukkit.inventory.meta.ItemMeta;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import org.bukkit.inventory.meta.SkullMeta;
import net.dom.bank.gui.components.util.SkullUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder>
{
    ItemBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
    }
    
    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder from(@NotNull final ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }
    
    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder from(@NotNull final Material material) {
        return new ItemBuilder(new ItemStack(material));
    }
    
    @NotNull
    @Contract(" -> new")
    public static BannerBuilder banner() {
        return new BannerBuilder();
    }
    
    @NotNull
    @Contract("_ -> new")
    public static BannerBuilder banner(@NotNull final ItemStack itemStack) {
        return new BannerBuilder(itemStack);
    }
    
    @NotNull
    @Contract("_ -> new")
    public static BookBuilder book(@NotNull final ItemStack itemStack) {
        return new BookBuilder(itemStack);
    }
    
    @NotNull
    @Contract(" -> new")
    public static FireworkBuilder firework() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_ROCKET));
    }
    
    @NotNull
    @Contract("_ -> new")
    public static FireworkBuilder firework(@NotNull final ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }
    
    @NotNull
    @Contract(" -> new")
    public static MapBuilder map() {
        return new MapBuilder();
    }
    
    @NotNull
    @Contract("_ -> new")
    public static MapBuilder map(@NotNull final ItemStack itemStack) {
        return new MapBuilder(itemStack);
    }
    
    @NotNull
    @Contract(" -> new")
    public static SkullBuilder skull() {
        return new SkullBuilder();
    }
    
    @NotNull
    @Contract("_ -> new")
    public static SkullBuilder skull(@NotNull final ItemStack itemStack) {
        return new SkullBuilder(itemStack);
    }
    
    @NotNull
    @Contract(" -> new")
    public static FireworkBuilder star() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_STAR));
    }
    
    @NotNull
    @Contract("_ -> new")
    public static FireworkBuilder star(@NotNull final ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }
    
    @Deprecated
    public ItemBuilder setSkullTexture(@NotNull final String texture) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        final SkullMeta skullMeta = (SkullMeta)this.getMeta();
        final GameProfile profile = new GameProfile(UUID.randomUUID(), (String)null);
        profile.getProperties().put((Object)"textures", (Object)new Property("textures", texture));
        try {
            final Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            ex.printStackTrace();
        }
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }
    
    @Deprecated
    public ItemBuilder setSkullOwner(@NotNull final OfflinePlayer player) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        final SkullMeta skullMeta = (SkullMeta)this.getMeta();
        skullMeta.setOwningPlayer(player);
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }
}
