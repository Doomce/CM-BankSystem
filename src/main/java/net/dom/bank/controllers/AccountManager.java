package net.dom.bank.controllers;

import java.util.logging.Level;

import org.hibernate.Transaction;
import net.dom.bank.Objects.UserPreferences;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.FormattedString;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import net.md_5.bungee.api.ChatColor;
import net.dom.bank.Util.Heads;
import java.util.ArrayList;
import net.dom.bank.gui.builder.item.ItemBuilder;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;
import java.util.Collections;

import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import java.util.List;
import java.util.UUID;
import net.dom.bank.Bank;

public class AccountManager
{
    private final double AccountCreationTax;
    Bank bank;
    
    public AccountManager(Bank b) {
        this.AccountCreationTax = 20.0;
        this.bank = b;
    }
    
    public List<Long> allBankAccounts(UUID uid) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<BankUser> root = criteriaQuery.from(BankUser.class);
        criteriaQuery.select(root.get("IBAN"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("UUID"), uid));
        List<Long> results = session.createQuery(criteriaQuery).setCacheable(true).getResultList();
        session.close();
        if (results == null) {
            return Collections.emptyList();
        }
        return results;
    }
    
    private int getMaxAccounts(Player p) {
        if (p.hasPermission("Bank.Admin")) {
            return 8;
        }
        if (p.hasPermission("Bank.Elite")) {
            return 4;
        }
        return 2;
    }
    
    private boolean haveMaxAccounts(@Nullable Player p, boolean bypass) {
        return !bypass && this.allBankAccounts(p.getUniqueId()).size() > this.getMaxAccounts(p);
    }
    
    public ItemBuilder CreateBankItem(Player p, int bankAccs) {
        int maxAcc = this.getMaxAccounts(p);
        ArrayList<String> lore = new ArrayList<String>();
        ItemStack material = Heads.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemMeta iMeta = material.getItemMeta();
        iMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Sąskaitos kūrimas");
        lore.add(ChatColor.YELLOW + "   " + bankAccs + " " + ChatColor.GRAY + "" + ChatColor.BOLD + "/ " + ChatColor.RED + maxAcc);
        iMeta.setLore(lore);
        iMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Sąskaitos kūrimas");
        material.setItemMeta(iMeta);
        return ItemBuilder.from(material);
    }
    
    public void CreateBankAccount(@NotNull Player p, @Nullable String name, boolean isAdmin) {
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            BankUser User = new BankUser();
            UUID uid = p.getUniqueId();
            if (Bank.CheckCoolDown(p.getUniqueId())) {
                if (!p.hasPermission("Bank.Create")) {
                    this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
                }
                else if (Bank.econ.getBalance(p) < this.AccountCreationTax && !isAdmin) {
                    this.bank.getLang().sendMessage(new FormattedString("notEnoughRealMoney").Replace("%cost%", this.AccountCreationTax), p);
                }
                else if (this.haveMaxAccounts(p, isAdmin)) {
                    this.bank.getLang().sendMessage(new FormattedString("tooManyBankAccounts").Replace("%maxaccounts%", this.getMaxAccounts(p)), p);
                }
                else {
                    User.setLevel(AccountLevel.BASIC);
                    User.setUUID(uid);
                    if (name != null) {
                        UserPreferences prefs = new UserPreferences();
                        prefs.setName(name);
                        User.setSettings(prefs);
                    }
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction tx = session.beginTransaction();
                    session.save(User);
                    tx.commit();
                    session.close();
                    this.bank.getLang().sendMessage(new FormattedString("successfulBankCreated"), p);
                }
            }
        });
    }
    
    public void CloseAccount(Player p, BankUser user, boolean admin) {
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            if (user.getMoney() > 0.0) {
                this.bank.getLang().sendMessage(new FormattedString("CloseAcc.AreMoney"), p);
            }
            else {
                if (!admin) {
                    if (user.getLevel().equals(AccountLevel.RESTRICTED)) {
                        this.bank.getLang().sendMessage(new FormattedString("restricted"), p);
                        return;
                    }
                    else if (user.getLevel().ordinal() >= 3) {
                        this.bank.getLang().sendMessage(new FormattedString("CloseAcc.InterestOrLoan"), p);
                        return;
                    }
                }
                if (user.getCardID() != null) {
                    this.bank.getModules().CardMng.blockCard(p, user.getIBAN(), user.getCardID());
                }
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction tx = session.beginTransaction();
                BankUser bU = session.load(BankUser.class, user.getIBAN());
                Bank.log.log(Level.INFO, "Closed " + bU.getIBAN() + " account by: " + p.getName());
                session.delete(bU);
                tx.commit();
                session.close();
                this.bank.getLang().sendMessage(new FormattedString("CloseAcc.Successful"), p);
            }
        });
    }
}
