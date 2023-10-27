package net.dom.bank.controllers;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import net.md_5.bungee.api.ChatColor;
import net.dom.bank.Util.Heads;
import java.util.ArrayList;
import net.dom.bank.gui.guis.GuiItem;
import org.bukkit.block.Block;
import net.dom.bank.gui.guis.BaseGui;
import net.dom.bank.gui.guis.PaginatedGui;
import org.bukkit.Material;
import net.dom.bank.gui.builder.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.dom.bank.gui.guis.Gui;
import net.dom.bank.Util.CardFiles;
import org.bukkit.entity.Player;
import net.dom.bank.Bank;

public class AdminGUIS
{
    private final Bank bank;
    
    public AdminGUIS(Bank pl) {
        this.bank = pl;
    }
    
    public void ATMList(Player p, CardFiles Config) {
        p.closeInventory();
        PaginatedGui gui = (Gui.paginated().rows(3).pageSize(18)).title(Component.text("ATMS")).disableAllInteractions().create();
        gui.setItem(3, 2, ItemBuilder.from(Material.PAPER).name(Component.text("PRAEITAS PUSLAPIS")).asGuiItem(event -> gui.previous()));
        gui.setItem(3, 8, ItemBuilder.from(Material.PAPER).name(Component.text("KITAS PUSLAPIS")).asGuiItem(event -> gui.next()));
        gui.setItem(3, 4, ItemBuilder.from(Material.PAPER).name(Component.text("GAUTI ATM GALVĄ")).asGuiItem(event -> {
            this.bank.getModules().CardMng.giveATMHead(p);
            gui.close(p);
        }));
        BaseGui baseGui2;
        gui.setItem(3, 6, ItemBuilder.from(Material.PAPER).name(Component.text("PRIDĖTI BANKOMATĄ")).lore(Component.text("TURI ŽIŪRĖTI Į ATM GALVĄ")).asGuiItem(event -> {
            this.bank.getModules().CardMng.setATMBlock(p);
            gui.close(p);
        }));
        if (Config.AllATMs() != null) {
            for (int id : Config.AllATMs()) {
                Block block = p.getWorld().getBlockAt(Config.getATMLoc(id));
                GuiItem atmItem = this.ATMItem(id, block).asGuiItem(event -> {
                    if (event.getCurrentItem().getType().equals(Material.AIR)) {
                    }
                    else {
                        if (event.isShiftClick() && event.isRightClick()) {
                            this.bank.getModules().CardMng.removeATMBlock(p, id);
                        }
                        if (event.isLeftClick()) {
                            this.bank.getModules().CardMng.atmSetState(p, block, !this.bank.getModules().CardMng.atmStateCheck(block));
                        }
                        gui.close(p);
                    }
                });
                gui.addItem(atmItem);
            }
        }
        gui.open(p);
    }
    
    private ItemBuilder ATMItem(int id, Block block) {
        try {
            if (!this.bank.getModules().CardMng.atmBlockCheck(block)) {
                return ItemBuilder.from(Material.AIR);
            }
            ArrayList<String> lore = new ArrayList<String>();
            ItemStack material;
            if (this.bank.getModules().CardMng.atmStateCheck(block)) {
                material = Heads.createSkull(this.bank.getConfig().getString("GUIS.ATMList.head-on"));
            }
            else {
                material = Heads.createSkull(this.bank.getConfig().getString("GUIS.ATMList.head-off"));
            }
            ItemMeta meta = material.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Bankomatas nr." + id);
            lore.add(ChatColor.GRAY + "Vieta (X,Y,Z): " + ChatColor.YELLOW + "" + block.getLocation().toVector());
            lore.add("");
            lore.add(ChatColor.GREEN + "Bankomato įjungimas:");
            if (this.bank.getModules().CardMng.atmStateCheck(block)) {
                meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Bankomatas nr." + id);
                lore.set(2, ChatColor.RED + "Bankomato išjungimas:");
            }
            lore.add(ChatColor.GRAY + "Kairysis pelės klavišas.");
            lore.add(ChatColor.GRAY + "Bankomato ištrynimas: SHIFT+");
            lore.add(ChatColor.GRAY + "Dešinysis pelės klavišas.");
            meta.setLore(lore);
            material.setItemMeta(meta);
            return ItemBuilder.from(material);
        }
        catch (NullPointerException ignore) {
            return ItemBuilder.from(Material.AIR);
        }
    }
}
