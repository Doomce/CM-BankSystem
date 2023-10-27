// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.item;

import net.dom.bank.gui.components.util.VersionHelper;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.bukkit.inventory.meta.ItemMeta;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.UUID;
import net.dom.bank.gui.components.exception.GuiException;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import net.dom.bank.gui.components.util.SkullUtil;
import java.lang.reflect.Field;

public final class SkullBuilder extends BaseItemBuilder<SkullBuilder>
{
    private static final Field PROFILE_FIELD;
    
    SkullBuilder() {
        super(SkullUtil.skull());
    }
    
    SkullBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
        if (!SkullUtil.isPlayerSkull(itemStack)) {
            throw new GuiException("SkullBuilder requires the material to be a PLAYER_HEAD/SKULL_ITEM!");
        }
    }
    
    @NotNull
    @Contract("_, _ -> this")
    public SkullBuilder texture(@NotNull final String texture, @NotNull final UUID profileId) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        if (SkullBuilder.PROFILE_FIELD == null) {
            return this;
        }
        final SkullMeta skullMeta = (SkullMeta)this.getMeta();
        final GameProfile profile = new GameProfile(profileId, (String)null);
        profile.getProperties().put((Object)"textures", (Object)new Property("textures", texture));
        try {
            SkullBuilder.PROFILE_FIELD.set(skullMeta, profile);
        }
        catch (IllegalArgumentException | IllegalAccessException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            ex.printStackTrace();
        }
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public SkullBuilder texture(@NotNull final String texture) {
        return this.texture(texture, UUID.randomUUID());
    }
    
    @NotNull
    @Contract("_ -> this")
    public SkullBuilder owner(@NotNull final OfflinePlayer player) {
        if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
            return this;
        }
        final SkullMeta skullMeta = (SkullMeta)this.getMeta();
        if (VersionHelper.IS_SKULL_OWNER_LEGACY) {
            skullMeta.setOwner(player.getName());
        }
        else {
            skullMeta.setOwningPlayer(player);
        }
        this.setMeta((ItemMeta)skullMeta);
        return this;
    }
    
    static {
        Field field;
        try {
            final SkullMeta skullMeta = (SkullMeta)SkullUtil.skull().getItemMeta();
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
            field = null;
        }
        PROFILE_FIELD = field;
    }
}
