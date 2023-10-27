package net.dom.bank.controllers;

import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder;

import net.dom.bank.CustomEvents.InterestEvent;

import net.dom.bank.Prompts.InterestCreationPrompt;
import org.bukkit.conversations.Conversation;
import org.hibernate.Transaction;
import org.hibernate.Session;
import net.dom.bank.Prompts.InterestRefusePrompt;
import org.bukkit.conversations.ConversationFactory;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;

import net.dom.bank.Database.HibernateUtil;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import net.dom.bank.FormattedString;
import org.bukkit.entity.Player;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.Objects.BankUser;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.time.ZonedDateTime;
import net.dom.bank.Objects.UserPreferences;
import net.dom.bank.Util.Utils;
import net.dom.bank.Bank;

public class InterestManager
{
    final Bank bank;
    private Double stockInterestModifier;
    private Double minStrictFineMoney;
    public Double minMoneyForDeposit;
    
    public InterestManager(Bank bank) {
        this.bank = bank;
        this.setup();
    }
    
    private void setup() {
        this.stockInterestModifier = 0.003;
        this.minStrictFineMoney = 50000.0;
        this.minMoneyForDeposit = 3600.0;
    }
    
    public double CalculateInterest(double start_amount) {
        double modifier = this.stockInterestModifier;
        if (start_amount >= 200000.0) {
            modifier += 0.0023;
        }
        else if (start_amount >= 100000.0) {
            modifier += 0.002;
        }
        else if (start_amount >= 55000.0) {
            modifier += 0.0016;
        }
        else if (start_amount >= 28000.0) {
            modifier += 0.0012;
        }
        else if (start_amount >= 12000.0) {
            modifier += 8.0E-4;
        }
        return modifier;
    }
    
    public double CalculateInterestRate(double start_amount, int Days) {
        return this.CalculateInterest(start_amount) * Days;
    }
    
    public double calculateInterestFine(double money, double rate, double start_amount) {
        rate /= 100.0;
        double fine = 0.0;
        if (money > start_amount) {
            fine += money - start_amount;
        }
        if (money > this.minStrictFineMoney) {
            fine += this.minStrictFineMoney * rate * 1.5;
        }
        else if (money > this.minStrictFineMoney * 2.0) {
            fine += this.minStrictFineMoney * rate * 2.5;
        }
        else if (money > this.minStrictFineMoney * 4.0) {
            fine += this.minStrictFineMoney * rate * 3.5;
        }
        else {
            fine += start_amount * rate;
        }
        return Utils.RoundNumber(fine);
    }
    
    public UserPreferences InterestBankAccountSetup(UserPreferences settings, int days, double money) {
        double modifier = this.CalculateInterest(money);
        double rate = Utils.RoundNumber(modifier * days * 100.0);
        Calendar cal = GregorianCalendar.from(ZonedDateTime.now());
        cal.add(Calendar.DATE, days);
        settings.setDayRate(modifier);
        settings.setRate(rate);
        settings.setStartSum(money);
        settings.setExpiryDate(new Date(cal.getTimeInMillis()));
        return settings;
    }
    
    public void removeInterest(BankUser user) {
        UserPreferences preferences = user.getSettings();
        if (preferences.getName() != null) {
            UserPreferences namePrefs = new UserPreferences();
            namePrefs.setName(preferences.getName());
            user.setSettings(namePrefs);
        }
        else {
            user.setSettings(new UserPreferences());
        }
        user.setLevel(AccountLevel.BASIC);
    }
    
    public void prepareInterestRefuse(Player p, BankUser user) {
        if (p.isConversing()) {
            return;
        }
        if (!p.hasPermission("Bank.Interest")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        if (!user.getLevel().equals(AccountLevel.INTEREST)) {
            this.bank.getLang().sendMessage(new FormattedString("Interest.accNotInterest"), p);
            return;
        }
        UserPreferences preferences = user.getSettings();
        LocalDate expiry = preferences.getExpiryDate().toLocalDate();
        double profit = user.getMoney() - preferences.getStartSum();
        if (expiry.isBefore(LocalDate.now()) || expiry.isEqual(LocalDate.now())) {
            Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction tx = session.beginTransaction();
                BankUser interestUser = session.get(BankUser.class, user.getIBAN());
                removeInterest(interestUser);
                tx.commit();
                session.close();
                this.bank.getLang().sendMessage(new FormattedString("Interest.changedIntoBasic").Replace("%profit%", profit), p);
            });
            Bank.log.info(user.getIBAN() + " ACCLVL set to BASIC;");
            return;
        }
        double fine = this.calculateInterestFine(user.getMoney(), preferences.getRate(), preferences.getStartSum());
        long days = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
        this.bank.getLang().sendMessage(new FormattedString("Interest.refuseCalculate").Replace("%rate%", preferences.getRate()).Replace("%profit%", profit).Replace("%date%", preferences.getExpiryDate().toLocalDate()).Replace("%days%", days).Replace("%fine%", fine), p);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("IBAN", user.getIBAN());
        data.put("Fine", fine);
        data.put("Profit", profit);
        ConversationFactory Cf = new ConversationFactory(this.bank);
        Cf.withFirstPrompt(new InterestRefusePrompt()).withTimeout(25).withInitialSessionData(data).withEscapeSequence("atsaukti").withLocalEcho(false);
        Conversation conversation = Cf.buildConversation(p);
        conversation.begin();
    }
    
    public void PrepareModifyBankAccountToInterest(Player p, double balance, long IBAN) {
        if (p.isConversing()) {
            return;
        }
        if (!p.hasPermission("Bank.Interest")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        if (balance < this.minMoneyForDeposit) {
            this.bank.getLang().sendMessage(new FormattedString("Interest.notEnoughMoney").Replace("%mindeposit%", this.minMoneyForDeposit), p);
            return;
        }
        p.closeInventory();
        HashMap<Object, Object> data = new HashMap<>();
        data.put("IBAN", IBAN);
        data.put("Money", balance);
        ConversationFactory Cf = new ConversationFactory(this.bank);
        Cf.withFirstPrompt(new InterestCreationPrompt()).withTimeout(25).withInitialSessionData(data).withEscapeSequence("atsaukti").withLocalEcho(false);
        Conversation conversation = Cf.buildConversation(p);
        conversation.begin();
    }
    
    public void InterestTask() {
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<BankUser> criteriaQuery = criteriaBuilder.createQuery(BankUser.class);
            Root<BankUser> root = criteriaQuery.from(BankUser.class);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("level"), AccountLevel.INTEREST));

            List<BankUser> results;
            try {
                results = session.createQuery(criteriaQuery).setCacheable(false).getResultList();
            }
            catch (NullPointerException ignore) {
                return;
            }
            Bank.log.info("---> Interest task");
            Transaction tx = session.beginTransaction();
            for (BankUser user : results) {
                LocalDate now = LocalDate.now();
                UserPreferences pref = user.getSettings();
                LocalDate expiry = pref.getExpiryDate().toLocalDate();
                if (expiry.isBefore(now)) {
                    return;
                } else {
                    double balance = user.getMoney();
                    double interest = pref.getDayRate();
                    double addition = Utils.RoundNumber(balance * interest);
                    user.addMoney(balance * interest);
                    session.save(user);
                    Bukkit.getScheduler().runTask(this.bank, () -> {
                        InterestEvent IE = new InterestEvent(user.getUUID(), addition);
                        Bukkit.getPluginManager().callEvent(IE);
                    });
                    Bank.log.info(user.getIBAN() + " +" + addition + ". Before: " + balance + ", After: " + user.getMoney() + ";");
                }
            }
            tx.commit();
            session.getSessionFactory().getCache().evictAllRegions();
            session.close();
            Bank.log.info("---------------->");
        });
    }
}
