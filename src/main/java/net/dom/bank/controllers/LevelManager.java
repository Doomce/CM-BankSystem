package net.dom.bank.controllers;

import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.hibernate.Transaction;
import org.hibernate.Session;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import org.bukkit.entity.Player;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import net.dom.bank.FormattedString;
import java.util.ArrayList;
import org.bukkit.inventory.meta.ItemMeta;
import net.dom.bank.Objects.AccountLevel;
import java.util.HashMap;
import net.dom.bank.Bank;

public class LevelManager
{
    private final Bank bank;
    public static final double TRPartBasic = 0.05;
    public static final double TRPartSilver = 0.02;
    private static final double TRPartPremium = 0.0;
    private static final double DayBasic = 0.0;
    private static final double DaySilver = 0.12;
    private static final double DayPremium = 0.35;
    private static final double dayMoneyBasic = 4000.0;
    private static final double dayMoneySilver = 50000.0;
    private static final double dayMoneyPremium = 110000.0;
    private static final double BasicMoneyBypassTax = 0.1;
    private static final double SilverMoneyBypassTax = 0.04;
    private static final double PremiumMoneyBypassTax = 0.15;
    private final double planUpgradeCost = 40.0;
    private final HashMap<Long, Double> dayMoney;
    
    public LevelManager(Bank bank) {
        this.dayMoney = new HashMap<Long, Double>();
        this.bank = bank;
    }
    
    private double getDayMoney(long iban) {
        return this.dayMoney.getOrDefault(iban, 0.0);
    }
    
    public void addDayMoney(long iban, double amount) {
        if (this.dayMoney.containsKey(iban)) {
            this.dayMoney.replace(iban, this.dayMoney.getOrDefault(iban, 0.0) + amount);
            return;
        }
        this.dayMoney.put(iban, amount);
    }
    
    private double getDayMoneyLimit(AccountLevel accLvl) {
        switch (accLvl) {
            case PREMIUM: {
                return 110000.0;
            }
            case SILVER: {
                return 50000.0;
            }
            case BASIC: {
                return 4000.0;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    ItemMeta Lore(ItemMeta meta, long iban, AccountLevel lvl) {
        List<String> lore = new ArrayList<String>();
        AccountLevel nextLvl = AccountLevel.BASIC;
        meta.setDisplayName(this.bank.getLang().Text(new FormattedString("GUIS.Main.LvlItem.Name").Replace("%packet%", WordUtils.capitalizeFully(lvl.toString()))));
        lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.LvlItem.lore.day-money").Replace("%current%", this.getDayMoney(iban)).Replace("%limit%", this.getDayMoneyLimit(lvl))));
        lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.LvlItem.lore.line-break")));
        switch (lvl) {
            case BASIC: {
                nextLvl = AccountLevel.SILVER;
                lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.LvlItem.lore.upgrade").Replace("%next%", nextLvl.toString()).Replace("%cost%", 40.0)));
                break;
            }
            case SILVER: {
                nextLvl = AccountLevel.BASIC;
                lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.LvlItem.lore.downgrade").Replace("%previous%", nextLvl.toString())));
                break;
            }
            case PREMIUM:
            case INTEREST:
            case LOAN:
            case RESTRICTED: {
                lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.LvlItem.lore.not-change")));
                break;
            }
        }
        lore.add(this.bank.getLang().Text(new FormattedString("GUIS.Main.LvlItem.lore.line-break")));
        meta.setLore(lore);
        return meta;
    }
    
    double TransactionTax(AccountLevel lvl, double money, long iban) {
        double tax = 0.0;
        switch (lvl) {
            case BASIC: {
                tax += money * 0.05;
                if (this.getDayMoney(iban) < 4000.0) {
                    break;
                }
                tax += money * 0.1;
                break;
            }
            case SILVER:
            case INTEREST: {
                tax += money * 0.02;
                if (this.getDayMoney(iban) < 50000.0) {
                    break;
                }
                tax += money * 0.04;
                break;
            }
            case PREMIUM: {
                tax += money * 0.0;
                if (this.getDayMoney(iban) < 110000.0) {
                    break;
                }
                tax += money * 0.15;
                break;
            }
        }
        return tax;
    }
    
    double DayTax(AccountLevel lvl) {
        double tax = 0.0;
        switch (lvl) {
            case BASIC: {
                tax = 0.0;
                break;
            }
            case SILVER: {
                tax += 0.12;
                break;
            }
            case PREMIUM: {
                tax += 0.35;
                break;
            }
        }
        return tax;
    }
    
    private void setAccLvl(Player p, long iban, AccountLevel lvl, double cost) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        BankUser bankUser = session.load(BankUser.class, iban);
        if (bankUser.getMoney() < cost) {
            session.close();
            this.bank.getLang().sendMessage(new FormattedString("notEnoughMoney"), p);
            return;
        }
        Transaction tx = session.beginTransaction();
        budgetController.addMoney("Budget", cost);
        bankUser.subtractMoney(cost);
        bankUser.setLevel(lvl);
        session.save(bankUser);
        tx.commit();
        Bank.log.info(bankUser.getIBAN() + " -" + cost + ". PLAN CHANGE. Balance after: " + bankUser.getMoney() + ";");
        session.close();
        this.bank.getLang().sendMessage(new FormattedString("AccLevel.Successful").Replace("%level%", lvl.toString()), p);
    }
    
    void prepareChangeAccountLevel(Player p, long IBAN, AccountLevel lvl) {
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            switch (lvl) {
                case PREMIUM:
                case INTEREST:
                case LOAN:
                case RESTRICTED: {
                    this.bank.getLang().sendMessage(new FormattedString("AccLevel.CannotChange"), p);
                    break;
                }
                case BASIC: {
                    this.setAccLvl(p, IBAN, AccountLevel.SILVER, 40.0);
                    break;
                }
                case SILVER: {
                    this.setAccLvl(p, IBAN, AccountLevel.BASIC, 0.0);
                    break;
                }
            }
        });
    }
}
