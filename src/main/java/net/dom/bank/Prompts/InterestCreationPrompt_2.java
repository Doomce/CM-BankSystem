package net.dom.bank.Prompts;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.bukkit.plugin.Plugin;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import net.dom.bank.Objects.UserPreferences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.conversations.Prompt;
import net.dom.bank.FormattedString;
import net.dom.bank.Bank;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;

public class InterestCreationPrompt_2 extends StringPrompt
{
    public String getPromptText(ConversationContext Cc) {
        return Bank.getInstance().getLang().Text(new FormattedString("Interest.confirmation"));
    }
    
    public Prompt acceptInput(ConversationContext Cc, String input) {
        Player p = (Player)Cc.getForWhom();
        Bank pl = Bank.getInstance();
        if (input.equalsIgnoreCase(p.getName())) {
            Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                double money = (double)Cc.getSessionData("Money");
                int days = (int)Cc.getSessionData("Days");
                long iban = (long)Cc.getSessionData("IBAN");
                UserPreferences pref = pl.getModules().Interest.InterestBankAccountSetup(new UserPreferences(), days, money);
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction tx = session.beginTransaction();
                BankUser User = session.load(BankUser.class, iban);
                User.setSettings(pref);
                User.setLevel(AccountLevel.INTEREST);
                tx.commit();
                session.close();
                pl.getLang().sendRawMessage(new FormattedString("Interest.changedIntoInterest"), p);
            });
            return Prompt.END_OF_CONVERSATION;
        }
        pl.getLang().sendRawMessage(new FormattedString("cancelled"), p);
        return Prompt.END_OF_CONVERSATION;
    }
}
