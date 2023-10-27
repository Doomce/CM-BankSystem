// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.util;

import net.dom.bank.gui.components.nbt.LegacyNbt;
import net.dom.bank.gui.components.nbt.Pdc;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import net.dom.bank.gui.components.nbt.NbtWrapper;

public final class ItemNbt
{
    private static final NbtWrapper nbt;
    
    public static ItemStack setString(@NotNull final ItemStack itemStack, @NotNull final String key, @NotNull final String value) {
        return ItemNbt.nbt.setString(itemStack, key, value);
    }
    
    public static String getString(@NotNull final ItemStack itemStack, @NotNull final String key) {
        return ItemNbt.nbt.getString(itemStack, key);
    }
    
    public static ItemStack setBoolean(@NotNull final ItemStack itemStack, @NotNull final String key, final boolean value) {
        return ItemNbt.nbt.setBoolean(itemStack, key, value);
    }
    
    public static ItemStack removeTag(@NotNull final ItemStack itemStack, @NotNull final String key) {
        return ItemNbt.nbt.removeTag(itemStack, key);
    }
    
    private static NbtWrapper selectNbt() {
        if (VersionHelper.IS_PDC_VERSION) {
            return new Pdc();
        }
        return new LegacyNbt();
    }
    
    static {
        nbt = selectNbt();
    }
}
