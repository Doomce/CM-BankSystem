// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.components.util;

import org.jetbrains.annotations.NotNull;
import java.util.regex.Matcher;
import net.dom.bank.gui.components.exception.GuiException;
import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import java.util.regex.Pattern;

public final class VersionHelper
{
    private static final String NMS_VERSION;
    private static final int V1_11 = 1110;
    private static final int V1_13 = 1130;
    private static final int V1_14 = 1140;
    private static final int V1_16_5 = 1165;
    private static final int V1_12 = 1120;
    private static final int CURRENT_VERSION;
    private static final boolean IS_PAPER;
    public static final boolean IS_COMPONENT_LEGACY;
    public static final boolean IS_ITEM_LEGACY;
    public static final boolean IS_UNBREAKABLE_LEGACY;
    public static final boolean IS_PDC_VERSION;
    public static final boolean IS_SKULL_OWNER_LEGACY;
    public static final boolean IS_CUSTOM_MODEL_DATA;
    
    private static boolean checkPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        }
        catch (ClassNotFoundException ignored) {
            return false;
        }
    }
    
    private static int getCurrentVersion() {
        final Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
        final StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            final String patch = matcher.group("patch");
            if (patch == null) {
                stringBuilder.append("0");
            }
            else {
                stringBuilder.append(patch.replace(".", ""));
            }
        }
        final Integer version = Ints.tryParse(stringBuilder.toString());
        if (version == null) {
            throw new GuiException("Could not retrieve server version!");
        }
        return version;
    }
    
    private static String getNmsVersion() {
        final String version = Bukkit.getServer().getClass().getPackage().getName();
        return version.substring(version.lastIndexOf(46) + 1);
    }
    
    public static Class<?> craftClass(@NotNull final String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + VersionHelper.NMS_VERSION + "." + name);
    }
    
    static {
        NMS_VERSION = getNmsVersion();
        CURRENT_VERSION = getCurrentVersion();
        IS_PAPER = checkPaper();
        IS_COMPONENT_LEGACY = (!VersionHelper.IS_PAPER || VersionHelper.CURRENT_VERSION < 1165);
        IS_ITEM_LEGACY = (VersionHelper.CURRENT_VERSION < 1130);
        IS_UNBREAKABLE_LEGACY = (VersionHelper.CURRENT_VERSION < 1110);
        IS_PDC_VERSION = (VersionHelper.CURRENT_VERSION >= 1140);
        IS_SKULL_OWNER_LEGACY = (VersionHelper.CURRENT_VERSION < 1120);
        IS_CUSTOM_MODEL_DATA = (VersionHelper.CURRENT_VERSION >= 1140);
    }
}
