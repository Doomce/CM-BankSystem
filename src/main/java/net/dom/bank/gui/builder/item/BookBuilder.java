// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.gui.builder.item;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import org.jetbrains.annotations.Contract;
import net.dom.bank.gui.components.util.Legacy;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import net.dom.bank.gui.components.exception.GuiException;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.util.EnumSet;

public class BookBuilder extends BaseItemBuilder<BookBuilder>
{
    private static final EnumSet<Material> BOOKS;
    
    BookBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
        if (!BookBuilder.BOOKS.contains(itemStack.getType())) {
            throw new GuiException("BookBuilder requires the material to be a WRITABLE_BOOK/WRITTEN_BOOK!");
        }
    }
    
    @NotNull
    @Contract("_ -> this")
    public BookBuilder author(@Nullable final Component author) {
        final BookMeta bookMeta = (BookMeta)this.getMeta();
        if (author == null) {
            bookMeta.setAuthor((String)null);
            this.setMeta((ItemMeta)bookMeta);
            return this;
        }
        bookMeta.setAuthor(Legacy.SERIALIZER.serialize(author));
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public BookBuilder generation(@Nullable final BookMeta.Generation generation) {
        final BookMeta bookMeta = (BookMeta)this.getMeta();
        bookMeta.setGeneration(generation);
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public BookBuilder page(@NotNull final Component... pages) {
        return this.page(Arrays.asList(pages));
    }
    
    @NotNull
    @Contract("_ -> this")
    public BookBuilder page(@NotNull final List<Component> pages) {
        final BookMeta bookMeta = (BookMeta)this.getMeta();
        for (final Component page : pages) {
            bookMeta.addPage(new String[] { Legacy.SERIALIZER.serialize(page) });
        }
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }
    
    @NotNull
    @Contract("_, _ -> this")
    public BookBuilder page(final int page, @NotNull final Component data) {
        final BookMeta bookMeta = (BookMeta)this.getMeta();
        bookMeta.setPage(page, Legacy.SERIALIZER.serialize(data));
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }
    
    @NotNull
    @Contract("_ -> this")
    public BookBuilder title(@Nullable final Component title) {
        final BookMeta bookMeta = (BookMeta)this.getMeta();
        if (title == null) {
            bookMeta.setTitle((String)null);
            this.setMeta((ItemMeta)bookMeta);
            return this;
        }
        bookMeta.setTitle(Legacy.SERIALIZER.serialize(title));
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }
    
    static {
        BOOKS = EnumSet.of(Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);
    }
}
