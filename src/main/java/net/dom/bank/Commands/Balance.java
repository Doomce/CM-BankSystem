package net.dom.bank.Commands;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.ObjectNotFoundException;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import net.dom.bank.Bank;

public class Balance
{
    Bank bank;
    
    public Balance(Bank bank) {
        this.bank = bank;
    }
    
    public void BalCmd(CommandSender sender, boolean isAdminCmd, String args) {

        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            if (sender instanceof Player) {
                Player player = (Player)sender;
            }
            else {
                try {
                    int iban = Integer.parseInt(args);
                    this.runConsole(iban);
                }
                catch (NumberFormatException ignore) {
                    Bank.log.info("IBAN pateiktas ne skaiciaus formatu.");
                }
            }
        });
    }
    
    private void runConsole(int targetIBAN) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            BankUser bu = session.load(BankUser.class, targetIBAN);
            Bank.log.info("Balansas: " + Bukkit.getOfflinePlayer(bu.getUUID()).getName() + "(" + bu.getUUID() + ") == " + bu.getMoney());
        }
        catch (ObjectNotFoundException ignore) {
            Bank.log.info("Neteisingas IBAN: " + targetIBAN);
            return;
        }
        tx.commit();
        session.close();
    }
}
