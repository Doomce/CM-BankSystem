package net.dom.bank.controllers;

import net.dom.bank.Prompts.BalanceOperationPrompt;
import org.bukkit.conversations.ConversationFactory;
import java.util.HashMap;
import org.bukkit.conversations.Conversation;
import org.hibernate.Transaction;
import org.apache.commons.lang.WordUtils;
import net.dom.bank.Objects.AccountLevel;
import org.bukkit.OfflinePlayer;
import org.hibernate.Session;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import org.bukkit.Bukkit;
import net.dom.bank.FormattedString;
import net.dom.bank.Objects.BalanceOperation;
import org.bukkit.entity.Player;
import net.dom.bank.Bank;

public class BalanceManager
{
    private final Bank bank;
    
    public BalanceManager(Bank plugin) {
        this.bank = plugin;
    }
    
    public void prepareBalanceOperation(Player p, long iban, BalanceOperation balanceOperation) {
        if (!p.hasPermission("Bank." + balanceOperation.toString().toLowerCase())) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        this.setupValueConv(p, iban, balanceOperation).begin();
    }
    
    public void promptValue(Player p, double amount, long iban, BalanceOperation balanceOperation) {
        if (balanceOperation.equals(BalanceOperation.DEPOSIT)) {
            this.bankDeposit(p, amount, iban, false);
        }
        else {
            this.bankWithdraw(p, amount, iban, false);
        }
    }
    
    public void bankBalance(Player p, long iban) {
        if (!p.hasPermission("Bank.Balance")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            BankUser bankUser = session.load(BankUser.class, iban);
            this.bank.getLang().sendMessage(new FormattedString("BalanceActions.Balance").Replace("%balance%", bankUser.getMoney()), p);
            session.close();
        });
    }
    
    private void bankDeposit(Player p, double amount, long iban, boolean admin) {
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            if (Bank.econ.getBalance(p) < amount && !admin) {
                this.bank.getLang().sendRawMessage(new FormattedString("notEnoughRealMoney"), p);
            }
            else {
                Session session = HibernateUtil.getSessionFactory().openSession();
                BankUser bankUser = session.load(BankUser.class, iban);
                if (bankUser.getLevel().equals(AccountLevel.RESTRICTED) && !admin) {
                    session.close();
                    this.bank.getLang().sendRawMessage(new FormattedString("restricted"), p);
                }
                else {
                    double bankTax = this.bank.getModules().Packets.TransactionTax(bankUser.getLevel(), amount, bankUser.getIBAN());
                    double addedAmount = amount - bankTax;
                    Transaction tx = session.beginTransaction();
                    Bank.econ.withdrawPlayer(p, amount);
                    bankUser.addMoney(addedAmount);
                    budgetController.addMoney("Budget", bankTax);
                    session.save(bankUser);
                    tx.commit();
                    Bank.log.info(bankUser.getIBAN() + " +" + amount + ". ATM DEPOSIT. Balance after: " + bankUser.getMoney() + ";");
                    this.bank.getModules().Packets.addDayMoney(bankUser.getIBAN(), amount);
                    session.close();
                    this.bank.getLang().sendRawMessage(new FormattedString("BalanceActions.SuccessDeposit").Replace("%amount%", amount).Replace("%tax%", bankTax).Replace("%packet%", WordUtils.capitalizeFully(bankUser.getLevel().toString())).Replace("%deposited%", addedAmount).Replace("%balance%", bankUser.getMoney()), p);
                }
            }
        });
    }
    
    private void bankWithdraw(Player p, double amount, long iban, boolean admin) {
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            BankUser bankUser = session.load(BankUser.class, iban);
            if (bankUser.getLevel().equals(AccountLevel.RESTRICTED) && !admin) {
                session.close();
                this.bank.getLang().sendRawMessage(new FormattedString("restricted"), p);
            }
            else if (bankUser.getLevel().equals(AccountLevel.INTEREST) && !admin) {
                session.close();
                this.bank.getLang().sendRawMessage(new FormattedString("Interest.balanceOperationNotAllowed"), p);
            }
            else if (bankUser.getMoney() < amount) {
                session.close();
                this.bank.getLang().sendRawMessage(new FormattedString("notEnoughMoney"), p);
            }
            else {
                double bankTax = this.bank.getModules().Packets.TransactionTax(bankUser.getLevel(), amount, bankUser.getIBAN());
                double subtractedAmount = amount - bankTax;
                Transaction tx = session.beginTransaction();
                budgetController.addMoney("Budget", bankTax);
                Bank.econ.depositPlayer(p, subtractedAmount);
                bankUser.subtractMoney(amount);
                session.save(bankUser);
                tx.commit();
                Bank.log.info(bankUser.getIBAN() + " -" + amount + ". ATM WITHDRAW. Balance after: " + bankUser.getMoney() + ";");
                this.bank.getModules().Packets.addDayMoney(bankUser.getIBAN(), amount);
                this.bank.getLang().sendRawMessage(new FormattedString("BalanceActions.SuccessWithdraw").Replace("%amount%", amount).Replace("%tax%", bankTax).Replace("%packet%", WordUtils.capitalizeFully(bankUser.getLevel().toString())).Replace("%withdrawen%", subtractedAmount).Replace("%balance%", bankUser.getMoney()), p);
                session.close();
            }
        });
    }
    
    private Conversation setupValueConv(Player p, long iban, BalanceOperation action) {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("IBAN", iban);
        data.put("Action", action);
        ConversationFactory factory = new ConversationFactory(this.bank);
        factory.withFirstPrompt(new BalanceOperationPrompt()).withTimeout(25).withInitialSessionData(data).withEscapeSequence("atsaukti").withEscapeSequence("atšaukti").withLocalEcho(false);
        return factory.buildConversation(p);
    }
}
