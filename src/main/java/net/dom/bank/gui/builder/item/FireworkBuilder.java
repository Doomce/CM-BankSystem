// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.item;

import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import java.util.List;
import org.jetbrains.annotations.Contract;
import java.util.Arrays;
import org.bukkit.FireworkEffect;
import net.dom.bank.gui.components.exception.GuiException;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class FireworkBuilder extends BaseItemBuilder<FireworkBuilder>
{
    private static final Material STAR;
    private static final Material ROCKET;
    
    FireworkBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != FireworkBuilder.STAR && itemStack.getType() != FireworkBuilder.ROCKET) {
            throw new GuiException("FireworkBuilder requires the material to be a FIREWORK_STAR/FIREWORK_ROCKET!");
        }
    }
    
    @NotNull
    @Contract("_ -> this")
    public FireworkBuilder effect(@NotNull final FireworkEffect... effects) {
        return this.effect(Arrays.asList(effects));
    }
    
    @NotNull
    @Contract("_ -> this")
    public FireworkBuilder effect(@NotNull final List<FireworkEffect> effects) {
        if (effects.isEmpty()) {
            return this;
        }
        if (this.getItemStack().getType() == FireworkBuilder.STAR) {
            final FireworkEffectMeta effectMeta = (FireworkEffectMeta)this.getMeta();
            effectMeta.setEffect((FireworkEffect)effects.get(0));
            this.setMeta((ItemMeta)effectMeta);
            return this;
        }
        final FireworkMeta fireworkMeta = (FireworkMeta)this.getMeta();
        fireworkMeta.addEffects((Iterable)effects);
        this.setMeta((ItemMeta)fireworkMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public FireworkBuilder power(final int power) {
        if (this.getItemStack().getType() == FireworkBuilder.ROCKET) {
            final FireworkMeta fireworkMeta = (FireworkMeta)this.getMeta();
            fireworkMeta.setPower(power);
            this.setMeta((ItemMeta)fireworkMeta);
        }
        return this;
    }
    
    static {
        STAR = Material.FIREWORK_STAR;
        ROCKET = Material.FIREWORK_ROCKET;
    }
}
