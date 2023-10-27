package net.dom.bank.controllers;

import java.util.ArrayList;
import java.util.Arrays;

import org.hibernate.Transaction;
import java.util.Iterator;
import org.hibernate.Session;
import org.bukkit.plugin.Plugin;
import java.util.logging.Level;
import net.dom.bank.Objects.UserPreferences;
import java.util.UUID;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import org.bukkit.Bukkit;
import java.util.List;
import net.dom.bank.Bank;

public class budgetController
{
    private static Bank bank;
    private static List<String> budgetsNames;
    private static List<Budget> budgets;
    
    public budgetController(Bank plugin) {
        budgetController.bank = plugin;
    }
    
    public static void setupBudget(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(budgetController.bank, () -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            for (Budget budget : budgetController.budgets) {
                if (budget.isUpdated) {
                    Transaction tx = session.beginTransaction();
                    session.get(BankUser.class, budget.iban).setMoney(budget.money);
                    tx.commit();
                    session.close();
                    return;
                } else if (budget.budgetName.equalsIgnoreCase(name)) {
                    session.close();
                    return;
                } else {
                    continue;
                }
            }
            Budget var2 = new Budget();
            Long iban = Bank.getDB().getNonPlayerIban(name);
            if (iban == null) {
                Transaction tx = session.beginTransaction();
                BankUser User = new BankUser();
                User.setLevel(AccountLevel.RESTRICTED);
                User.setUUID(new UUID(0L, 0L));
                UserPreferences prefs = new UserPreferences();
                prefs.setName(name);
                User.setSettings(prefs);
                session.save(User);
                var2.iban = User.getIBAN();
                var2.money = 0.0;
                tx.commit();
            }
            else {
                var2.iban = iban;
                var2.money = session.get(BankUser.class, iban).getMoney();
            }
            var2.budgetName = name;
            budgetController.budgets.add(var2);
            session.close();
            Bank.log.log(Level.INFO, "API: Initializing budget named- " + name + ".");
        });
    }
    
    public static void initializeBudgets() {
        budgetController.budgetsNames.forEach(budgetController::setupBudget);
    }
    
    public static Long getIBAN(String name) {
        for (Budget budget : budgetController.budgets) {
            if (budget.budgetName.equalsIgnoreCase(name)) {
                return budget.iban;
            }
        }
        return null;
    }
    
    public static void addMoney(String name, double amount) {
        for (Budget budget : budgetController.budgets) {
            if (budget.budgetName.equalsIgnoreCase(name)) {
                budget.money += amount;
                budget.isUpdated = true;
                setupBudget(budget.budgetName);
            }
        }
    }
    
    public static void subtractMoney(String name, double amount) {
        for (Budget budget : budgetController.budgets) {
            if (budget.budgetName.equalsIgnoreCase(name)) {
                budget.money -= amount;
                budget.isUpdated = true;
                setupBudget(budget.budgetName);
            }
        }
    }
    
    public static double getBalance(String name) {
        for (Budget budget : budgetController.budgets) {
            if (budget.budgetName.equalsIgnoreCase(name)) {
                return budget.money;
            }
        }
        return 0.0;
    }
    
    static {
        budgetsNames = Arrays.asList("Budget", "Insurance");
        budgets = new ArrayList<>();
    }
    
    private static class Budget
    {
        String budgetName;
        Long iban;
        double money;
        boolean isUpdated;
    }
}
