// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.gui;

import net.dom.bank.gui.guis.BaseGui;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;
import net.dom.bank.gui.components.util.Legacy;
import net.dom.bank.gui.guis.StorageGui;

public final class StorageBuilder extends BaseGuiBuilder<StorageGui, StorageBuilder>
{
    @NotNull
    @Contract(" -> new")
    @Override
    public StorageGui create() {
        final StorageGui gui = new StorageGui(this.getRows(), Legacy.SERIALIZER.serialize(this.getTitle()), this.getModifiers());
        final Consumer<StorageGui> consumer = ((BaseGuiBuilder<StorageGui, B>)this).getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}
