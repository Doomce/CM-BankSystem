package net.dom.bank.Prompts;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.bukkit.plugin.Plugin;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.Objects.UserPreferences;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.conversations.Prompt;
import net.dom.bank.FormattedString;
import net.dom.bank.Bank;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;

public class InterestRefusePrompt extends StringPrompt
{
    public String getPromptText(ConversationContext Cc) {
        return Bank.getInstance().getLang().Text(new FormattedString("Interest.refuseConfirmation"));
    }
    
    public Prompt acceptInput(ConversationContext Cc, String input) {
        Player p = (Player)Cc.getForWhom();
        if (input.equalsIgnoreCase(p.getName())) {
            double fine = (double)Cc.getSessionData((Object)"Fine");
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)Bank.getInstance(), () -> {
                long IBAN = (long)Cc.getSessionData((Object)"IBAN");
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction tx = session.beginTransaction();
                BankUser User = session.load(BankUser.class, IBAN);
                User.subtractMoney(fine);
                User.setSettings(new UserPreferences());
                User.setLevel(AccountLevel.BASIC);
                tx.commit();
                Bank.log.info(User.getIBAN() + " -" + fine + ". INTEREST FINE. Balance after: " + User.getMoney() + ";");
                session.close();
                Bank.getInstance().getLang().sendRawMessage(new FormattedString("Interest.changedIntoBasicFine").Replace("%profit%", fine), p);
                return;
            });
        }
        return Prompt.END_OF_CONVERSATION;
    }
}
