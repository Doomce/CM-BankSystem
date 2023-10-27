package net.dom.bank.controllers;

import net.dom.bank.gui.components.GuiType;
import java.time.LocalDate;
import net.dom.bank.Objects.UserPreferences;
import net.md_5.bungee.api.ChatColor;
import java.util.ArrayList;
import org.hibernate.Session;
import org.bukkit.event.inventory.ClickType;
import net.dom.bank.Objects.BalanceOperation;
import org.bukkit.Material;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import java.util.List;

import net.dom.bank.gui.guis.GuiItem;
import net.dom.bank.gui.builder.item.ItemBuilder;
import net.dom.bank.Util.Heads;
import net.kyori.adventure.text.Component;
import net.dom.bank.gui.guis.Gui;
import org.bukkit.Bukkit;
import net.dom.bank.FormattedString;
import java.util.UUID;
import org.bukkit.entity.Player;
import net.dom.bank.Bank;

public class GUIS
{
    private final Bank bank;
    public final AdminGUIS Admin;
    
    public GUIS(Bank plugin) {
        this.bank = plugin;
        this.Admin = new AdminGUIS(plugin);
    }
    
    public void BankAccounts(Player p, UUID uid) {
        if (!Bank.CheckCoolDown(p.getUniqueId())) {
            return;
        }
        if (!p.hasPermission("Bank.Main")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            List<Long> bankAccounts;
            if (uid == null) {
                bankAccounts = Bank.getModule().AccountMng.allBankAccounts(p.getUniqueId());
            }
            else {
                bankAccounts = Bank.getModule().AccountMng.allBankAccounts(uid);
            }
            Gui gui = Gui.gui().title(Component.text("Banko saskaitos")).rows(1).disableAllInteractions().create();
            bankAccounts.forEach(iban -> {
                ItemStack material = Heads.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q4ODk2NmY0M2IzMTljMWY2MjM0MmY4ZmNjMjVkYWFmNzljNzlhYTNmNDE0YTdlODA0MDdiYWEwZWY5N2NmOSJ9fX0=");
                ItemMeta iMeta = material.getItemMeta();
                iMeta.setDisplayName(this.bank.getLang().Text(new FormattedString("GUIS.BankAccounts.Item.Name").Replace("%iban%", iban)));
                material.setItemMeta(iMeta);
                GuiItem guiItem = ItemBuilder.from(material).asGuiItem(event -> this.BankAccountManagement(p, iban));
                gui.addItem(guiItem);
            });
            gui.setItem(8, Bank.getModule().AccountMng.CreateBankItem(p, bankAccounts.size()).asGuiItem(event -> {
                Bank.getModule().AccountMng.CreateBankAccount(p, null, false);
                gui.close(p);
            }));
            Bukkit.getScheduler().runTask(this.bank, () -> gui.open(p));
        });
    }
    
    public void BankAccountManagement(Player p, long IBAN) {
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            BankUser User = session.get(BankUser.class, IBAN);
            session.close();
            Gui gui = Gui.gui().title(Component.text("Saskaitos valdymas")).rows(1).disableAllInteractions().create();
            gui.addItem(this.InfoItem(User).asGuiItem(event -> {
                event.setCancelled(true);
                if (event.isShiftClick() && !User.getLevel().equals(AccountLevel.RESTRICTED)) {
                    if (User.getLevel().equals(AccountLevel.INTEREST)) {
                        this.bank.getModules().Interest.prepareInterestRefuse(p, User);
                    }
                    else {
                        this.bank.getModules().Interest.PrepareModifyBankAccountToInterest(p, User.getMoney(), User.getIBAN());
                    }
                    gui.close(p);
                }
            }));
            if (User.getLevel().equals(AccountLevel.BASIC) || User.getLevel().equals(AccountLevel.SILVER)) {
                gui.addItem(this.TaxItem(User).asGuiItem(event -> {
                    if (event.isShiftClick()) {
                        this.bank.getModules().Packets.prepareChangeAccountLevel(p, User.getIBAN(), User.getLevel());
                        gui.close(p);
                    }
                }));
            }
            gui.addItem(new GuiItem(Material.AIR));
            gui.addItem(this.CardItem(User).asGuiItem(event -> {
                if (event.isShiftClick()) {
                    this.bank.getModules().CardMng.cardItemAction(p, User);
                    gui.close(p);
                }
            }));
            gui.addItem(new GuiItem(Material.AIR));
            gui.addItem(this.BalMngItem(User).asGuiItem(event -> {
                if (event.isRightClick()) {
                    this.bank.getModules().BalMng.prepareBalanceOperation(p, IBAN, BalanceOperation.DEPOSIT);
                }
                if (event.isLeftClick()) {
                    this.bank.getModules().BalMng.prepareBalanceOperation(p, IBAN, BalanceOperation.WITHDRAW);
                }
                gui.close(p);
            }));
            gui.addItem(new GuiItem(Material.AIR));
            gui.addItem(new GuiItem(Material.AIR));
            gui.addItem(this.CloseItem().asGuiItem(event -> {
                if (event.getClick().equals(ClickType.CONTROL_DROP)) {
                    this.bank.getModules().AccountMng.CloseAccount(p, User, false);
                    p.closeInventory();
                }
            }));
            Bukkit.getScheduler().runTask(this.bank, () -> {
                p.closeInventory();
                gui.open(p);
            });
        });
    }
    
    private ItemBuilder InfoItem(BankUser bu) {
        ItemStack material = Heads.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q5MWY1MTI2NmVkZGM2MjA3ZjEyYWU4ZDdhNDljNWRiMDQxNWFkYTA0ZGFiOTJiYjc2ODZhZmRiMTdmNGQ0ZSJ9fX0=");
        ItemMeta meta = material.getItemMeta();
        meta.setDisplayName(this.bank.getLang().Text(new FormattedString("GUIS.Main.InfoItem.Name")));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(this.bank.getLang().Text(new FormattedString("AccLevelPrefix." + bu.getLevel().toString().toUpperCase())));
        Integer id = bu.getCardID();
        if (id != null && this.bank.getModules().CardMng.cardFiles.isCardInBList(id)) {
            lore.set(0, this.bank.getLang().Text(new FormattedString("AccLevelPrefix.WARNING")));
        }
        lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.InfoItem.Lore.section")));
        UserPreferences prefs = bu.getSettings();
        if (prefs != null && prefs.getName() != null) {
            lore.add(ChatColor.RED + prefs.getName());
            lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.InfoItem.Lore.section")));
        }
        if (bu.getLevel().equals(AccountLevel.LOAN) || bu.getLevel().equals(AccountLevel.INTEREST)) {
            LocalDate expire = prefs.getExpiryDate().toLocalDate();
            double rate = prefs.getRate();
            lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.InfoItem.Lore.interest").Replace("%date%", expire).Replace("%rate%", rate)));
        }
        else if (bu.getLevel().equals(AccountLevel.RESTRICTED)) {
            lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.InfoItem.Lore.restricted")));
        }
        else {
            lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.InfoItem.Lore.options")));
        }
        lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.InfoItem.Lore.section")));
        lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.InfoItem.Lore.ibanInfo").Replace("%iban%", bu.getIBAN())));
        lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.InfoItem.Lore.custom")));
        meta.setLore(lore);
        material.setItemMeta(meta);
        return ItemBuilder.from(material);
    }
    
    private ItemBuilder TaxItem(BankUser bu) {
        ItemStack material = Heads.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjI4ZDk4Y2U0N2ZiNzdmOGI2MDRhNzY2ZGRkMjU0OTIzMjU2NGY5NTYyMjVjNTlmM2UzYjdiODczYTU4YzQifX19");
        material.setItemMeta(this.bank.getModules().Packets.Lore(material.getItemMeta(), bu.getIBAN(), bu.getLevel()));
        return ItemBuilder.from(material);
    }
    
    private ItemBuilder CardItem(BankUser bu) {
        ItemStack material = Heads.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVjMjUyMjhiZWJjZWFhYmU5YjhlZTliMzVkY2RkNTljMmQ5ODc4ODA1YTRmNjNmZTI1NDczNmQ5YzNkOWFiIn19fQ==");
        ItemMeta meta = material.getItemMeta();
        material.setItemMeta(this.bank.getModules().CardMng.setupCardData(meta, bu));
        return ItemBuilder.from(material);
    }
    
    private ItemBuilder BalMngItem(BankUser bu) {
        ItemStack material = Heads.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjI4ZDk4Y2U0N2ZiNzdmOGI2MDRhNzY2ZGRkMjU0OTIzMjU2NGY5NTYyMjVjNTlmM2UzYjdiODczYTU4YzQifX19");
        ItemMeta mmeta = material.getItemMeta();
        mmeta.setDisplayName(this.bank.getLang().Text(new FormattedString("GUIS.Main.Balance.Name")));
        mmeta.setLore(this.bank.getLang().TextList(new FormattedString("GUIS.Main.Balance.lore").Replace("%amount%", bu.getMoney())));
        material.setItemMeta(mmeta);
        return ItemBuilder.from(material);
    }
    
    private ItemBuilder CloseItem() {
        ItemStack material = Heads.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY5NTkwNThjMGMwNWE0MTdmZDc1N2NiODViNDQxNWQ5NjZmMjczM2QyZTdjYTU0ZjdiYTg2OGUzMjQ5MDllMiJ9fX0=");
        ItemMeta meta = material.getItemMeta();
        meta.setDisplayName(this.bank.getLang().Text(new FormattedString("GUIS.Main.Close.Name")));
        meta.setLore(this.bank.getLang().TextList(new FormattedString("GUIS.Main.Close.Lore")));
        material.setItemMeta(meta);
        return ItemBuilder.from(material);
    }
    
    public void BankATM(Player p, long IBAN, int id) {
        p.closeInventory();
        Gui gui = Gui.gui().title(Component.text("Bankomatas Nr." + id)).type(GuiType.HOPPER).disableAllInteractions().create();
        GuiItem DepositItem = this.ATMItem(BalanceOperation.DEPOSIT).asGuiItem(event -> {
            this.bank.getModules().BalMng.prepareBalanceOperation(p, IBAN, BalanceOperation.DEPOSIT);
            gui.close(p);
        });
        GuiItem WithdrawItem = this.ATMItem(BalanceOperation.WITHDRAW).asGuiItem(event -> {
            this.bank.getModules().BalMng.prepareBalanceOperation(p, IBAN, BalanceOperation.WITHDRAW);
            gui.close(p);
        });
        GuiItem BalanceItem = this.ATMItem(BalanceOperation.BALANCE).asGuiItem(event -> {
            this.bank.getModules().BalMng.bankBalance(p, IBAN);
            gui.close(p);
        });
        gui.addItem(DepositItem);
        gui.addItem(new GuiItem(Material.AIR));
        gui.addItem(BalanceItem);
        gui.addItem(new GuiItem(Material.AIR));
        gui.addItem(WithdrawItem);
        gui.open(p);
    }
    
    private ItemBuilder ATMItem(BalanceOperation Operation) {
        ItemStack material = Heads.createSkull(this.bank.getConfig().getString("GUIS.ATM." + Operation.toString() + ".head"));
        ItemMeta meta = material.getItemMeta();
        meta.setDisplayName(this.bank.getLang().Text(new FormattedString("GUIS.ATM." + Operation + ".Name")));
        meta.setLore(this.bank.getLang().TextList(new FormattedString("GUIS.ATM." + Operation + ".lore")));
        material.setItemMeta(meta);
        return ItemBuilder.from(material);
    }
}
