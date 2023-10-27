// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.nbt;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class Pdc implements NbtWrapper
{
    private static final Plugin PLUGIN;
    
    @Override
    public ItemStack setString(@NotNull final ItemStack itemStack, final String key, final String value) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(Pdc.PLUGIN, key), PersistentDataType.STRING, (Object)value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    @Override
    public ItemStack removeTag(@NotNull final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().remove(new NamespacedKey(Pdc.PLUGIN, key));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    @Override
    public ItemStack setBoolean(@NotNull final ItemStack itemStack, final String key, final boolean value) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(Pdc.PLUGIN, key), PersistentDataType.BYTE, (Object)(byte)(value ? 1 : 0));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    @Nullable
    @Override
    public String getString(@NotNull final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }
        return (String)meta.getPersistentDataContainer().get(new NamespacedKey(Pdc.PLUGIN, key), PersistentDataType.STRING);
    }
    
    static {
        PLUGIN = (Plugin)JavaPlugin.getProvidingPlugin((Class)Pdc.class);
    }
}
