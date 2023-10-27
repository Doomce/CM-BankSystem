// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.nbt;

import java.util.Objects;
import org.bukkit.Bukkit;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class LegacyNbt implements NbtWrapper
{
    public static final String PACKAGE_NAME;
    public static final String NMS_VERSION;
    private static Method getStringMethod;
    private static Method setStringMethod;
    private static Method setBooleanMethod;
    private static Method hasTagMethod;
    private static Method getTagMethod;
    private static Method setTagMethod;
    private static Method removeTagMethod;
    private static Method asNMSCopyMethod;
    private static Method asBukkitCopyMethod;
    private static Constructor<?> nbtCompoundConstructor;
    
    @Override
    public ItemStack setString(@NotNull final ItemStack itemStack, final String key, final String value) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        final Object nmsItemStack = asNMSCopy(itemStack);
        final Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
        setString(itemCompound, key, value);
        setTag(nmsItemStack, itemCompound);
        return asBukkitCopy(nmsItemStack);
    }
    
    @Override
    public ItemStack removeTag(@NotNull final ItemStack itemStack, final String key) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        final Object nmsItemStack = asNMSCopy(itemStack);
        final Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
        remove(itemCompound, key);
        setTag(nmsItemStack, itemCompound);
        return asBukkitCopy(nmsItemStack);
    }
    
    @Override
    public ItemStack setBoolean(@NotNull final ItemStack itemStack, final String key, final boolean value) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        final Object nmsItemStack = asNMSCopy(itemStack);
        final Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
        setBoolean(itemCompound, key, value);
        setTag(nmsItemStack, itemCompound);
        return asBukkitCopy(nmsItemStack);
    }
    
    @Nullable
    @Override
    public String getString(@NotNull final ItemStack itemStack, final String key) {
        if (itemStack.getType() == Material.AIR) {
            return null;
        }
        final Object nmsItemStack = asNMSCopy(itemStack);
        final Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();
        return getString(itemCompound, key);
    }
    
    private static void setString(final Object itemCompound, final String key, final String value) {
        try {
            LegacyNbt.setStringMethod.invoke(itemCompound, key, value);
        }
        catch (IllegalAccessException ex) {}
        catch (InvocationTargetException ex2) {}
    }
    
    private static void setBoolean(final Object itemCompound, final String key, final boolean value) {
        try {
            LegacyNbt.setBooleanMethod.invoke(itemCompound, key, value);
        }
        catch (IllegalAccessException ex) {}
        catch (InvocationTargetException ex2) {}
    }
    
    private static void remove(final Object itemCompound, final String key) {
        try {
            LegacyNbt.removeTagMethod.invoke(itemCompound, key);
        }
        catch (IllegalAccessException ex) {}
        catch (InvocationTargetException ex2) {}
    }
    
    private static String getString(final Object itemCompound, final String key) {
        try {
            return (String)LegacyNbt.getStringMethod.invoke(itemCompound, key);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            return null;
        }
    }
    
    private static boolean hasTag(final Object nmsItemStack) {
        try {
            return (boolean)LegacyNbt.hasTagMethod.invoke(nmsItemStack, new Object[0]);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            return false;
        }
    }
    
    public static Object getTag(final Object nmsItemStack) {
        try {
            return LegacyNbt.getTagMethod.invoke(nmsItemStack, new Object[0]);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            return null;
        }
    }
    
    private static void setTag(final Object nmsItemStack, final Object itemCompound) {
        try {
            LegacyNbt.setTagMethod.invoke(nmsItemStack, itemCompound);
        }
        catch (IllegalAccessException ex) {}
        catch (InvocationTargetException ex2) {}
    }
    
    private static Object newNBTTagCompound() {
        try {
            return LegacyNbt.nbtCompoundConstructor.newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            return null;
        }
    }
    
    public static Object asNMSCopy(final ItemStack itemStack) {
        try {
            return LegacyNbt.asNMSCopyMethod.invoke(null, itemStack);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            return null;
        }
    }
    
    public static ItemStack asBukkitCopy(final Object nmsItemStack) {
        try {
            return (ItemStack)LegacyNbt.asBukkitCopyMethod.invoke(null, nmsItemStack);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            return null;
        }
    }
    
    private static Class<?> getNMSClass(final String className) {
        try {
            return Class.forName("net.minecraft.server." + LegacyNbt.NMS_VERSION + "." + className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    private static Class<?> getCraftItemStackClass() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + LegacyNbt.NMS_VERSION + ".inventory.CraftItemStack");
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    static {
        PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();
        NMS_VERSION = LegacyNbt.PACKAGE_NAME.substring(LegacyNbt.PACKAGE_NAME.lastIndexOf(46) + 1);
        try {
            LegacyNbt.getStringMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("getString", String.class);
            LegacyNbt.removeTagMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("remove", String.class);
            LegacyNbt.setStringMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("setString", String.class, String.class);
            LegacyNbt.setBooleanMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("setBoolean", String.class, Boolean.TYPE);
            LegacyNbt.hasTagMethod = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("hasTag", (Class<?>[])new Class[0]);
            LegacyNbt.getTagMethod = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("getTag", (Class<?>[])new Class[0]);
            LegacyNbt.setTagMethod = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("setTag", getNMSClass("NBTTagCompound"));
            LegacyNbt.nbtCompoundConstructor = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getDeclaredConstructor((Class<?>[])new Class[0]);
            LegacyNbt.asNMSCopyMethod = Objects.requireNonNull(getCraftItemStackClass()).getMethod("asNMSCopy", ItemStack.class);
            LegacyNbt.asBukkitCopyMethod = Objects.requireNonNull(getCraftItemStackClass()).getMethod("asBukkitCopy", getNMSClass("ItemStack"));
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
