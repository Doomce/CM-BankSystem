// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.gui;

import net.dom.bank.gui.guis.BaseGui;
import java.util.function.Consumer;
import net.dom.bank.gui.components.util.Legacy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import net.dom.bank.gui.components.GuiType;
import net.dom.bank.gui.guis.Gui;

public final class SimpleBuilder extends BaseGuiBuilder<Gui, SimpleBuilder>
{
    private GuiType guiType;
    
    public SimpleBuilder(@NotNull final GuiType guiType) {
        this.guiType = guiType;
    }
    
    @NotNull
    @Contract("_ -> this")
    public SimpleBuilder type(@NotNull final GuiType guiType) {
        this.guiType = guiType;
        return this;
    }
    
    @NotNull
    @Contract(" -> new")
    @Override
    public Gui create() {
        final String title = Legacy.SERIALIZER.serialize(this.getTitle());
        Gui gui;
        if (this.guiType == null || this.guiType == GuiType.CHEST) {
            gui = new Gui(this.getRows(), title, this.getModifiers());
        }
        else {
            gui = new Gui(this.guiType, title, this.getModifiers());
        }
        final Consumer<Gui> consumer = ((BaseGuiBuilder<Gui, B>)this).getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}
