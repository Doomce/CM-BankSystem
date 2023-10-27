// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.util;

import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public final class SkullUtil
{
    private static final Material SKULL;
    
    private static Material getSkullMaterial() {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return Material.valueOf("SKULL_ITEM");
        }
        return Material.PLAYER_HEAD;
    }
    
    public static ItemStack skull() {
        return VersionHelper.IS_ITEM_LEGACY ? new ItemStack(SkullUtil.SKULL, 1, (short)3) : new ItemStack(SkullUtil.SKULL);
    }
    
    public static boolean isPlayerSkull(@NotNull final ItemStack item) {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return item.getType() == SkullUtil.SKULL && item.getDurability() == 3;
        }
        return item.getType() == SkullUtil.SKULL;
    }
    
    static {
        SKULL = getSkullMaterial();
    }
}
