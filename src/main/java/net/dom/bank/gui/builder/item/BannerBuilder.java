// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.item;

import java.util.Collection;
import org.bukkit.Tag;
import net.dom.bank.gui.components.util.VersionHelper;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.jetbrains.annotations.Contract;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.DyeColor;
import net.dom.bank.gui.components.exception.GuiException;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import java.util.EnumSet;
import org.bukkit.Material;

public final class BannerBuilder extends BaseItemBuilder<BannerBuilder>
{
    private static final Material DEFAULT_BANNER;
    private static final EnumSet<Material> BANNERS;
    
    BannerBuilder() {
        super(new ItemStack(BannerBuilder.DEFAULT_BANNER));
    }
    
    BannerBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
        if (!BannerBuilder.BANNERS.contains(itemStack.getType())) {
            throw new GuiException("BannerBuilder requires the material to be a banner!");
        }
    }
    
    @NotNull
    @Contract("_ -> this")
    public BannerBuilder baseColor(@NotNull final DyeColor color) {
        final BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.setBaseColor(color);
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }
    
    @NotNull
    @Contract("_, _ -> this")
    public BannerBuilder pattern(@NotNull final DyeColor color, @NotNull final PatternType pattern) {
        final BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.addPattern(new Pattern(color, pattern));
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public BannerBuilder pattern(@NotNull final Pattern... pattern) {
        return this.pattern(Arrays.asList(pattern));
    }
    
    @NotNull
    @Contract("_ -> this")
    public BannerBuilder pattern(@NotNull final List<Pattern> patterns) {
        final BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        for (final Pattern it : patterns) {
            bannerMeta.addPattern(it);
        }
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }
    
    @NotNull
    @Contract("_, _, _ -> this")
    public BannerBuilder pattern(final int index, @NotNull final DyeColor color, @NotNull final PatternType pattern) {
        return this.pattern(index, new Pattern(color, pattern));
    }
    
    @NotNull
    @Contract("_, _ -> this")
    public BannerBuilder pattern(final int index, @NotNull final Pattern pattern) {
        final BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.setPattern(index, pattern);
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public BannerBuilder setPatterns(@NotNull final List<Pattern> patterns) {
        final BannerMeta bannerMeta = (BannerMeta)this.getMeta();
        bannerMeta.setPatterns((List)patterns);
        this.setMeta((ItemMeta)bannerMeta);
        return this;
    }
    
    static {
        if (VersionHelper.IS_ITEM_LEGACY) {
            DEFAULT_BANNER = Material.valueOf("BANNER");
            BANNERS = EnumSet.of(Material.valueOf("BANNER"));
        }
        else {
            DEFAULT_BANNER = Material.WHITE_BANNER;
            BANNERS = EnumSet.copyOf((Collection<Material>)Tag.BANNERS.getValues());
        }
    }
}
