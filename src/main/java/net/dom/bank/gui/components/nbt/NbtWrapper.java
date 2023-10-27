// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.nbt;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;

public interface NbtWrapper
{
    ItemStack setString(@NotNull final ItemStack p0, final String p1, final String p2);
    
    ItemStack removeTag(@NotNull final ItemStack p0, final String p1);
    
    ItemStack setBoolean(@NotNull final ItemStack p0, final String p1, final boolean p2);
    
    @Nullable
    String getString(@NotNull final ItemStack p0, final String p1);
}
