package net.dom.bank.Commands;

import org.bukkit.conversations.Conversable;
import java.util.Map;
import org.bukkit.plugin.Plugin;
import org.bukkit.conversations.ConversationFactory;
import java.util.HashMap;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.Objects.BankUser;
import java.util.UUID;
import net.dom.bank.Bank;

public class AccountManagement
{
    private final Double TaxModifer_0;
    private final int MaxAccounts = 2;
    private final Bank bank;
    
    public AccountManagement(Bank bank) {
        this.TaxModifer_0 = 0.05;
        this.bank = bank;
    }
    
    private BankUser CreateNewUser( UUID uid) {
        BankUser var1 = new BankUser();
        var1.setUUID(uid);
        var1.setLevel(AccountLevel.BASIC);
        var1.setSettings(null);
        return var1;
    }
    
    public void ChangeAccountLevel( Player p,  long IBAN,  AccountLevel lvl,  int Type2) {
        if (Type2 > 0) {
            if (lvl.equals(AccountLevel.BASIC)) {
                p.sendMessage("KEISTI I Silver");
            }
            else if (lvl.equals(AccountLevel.SILVER)) {
                p.sendMessage("KEISTI I Premium");
            }
            else {
                p.sendMessage("nebegalima aukstinti.");
            }
        }
        if (Type2 < 0) {
            if (lvl.equals(AccountLevel.PREMIUM)) {
                p.sendMessage("KEISTI I Silver");
            }
            else if (lvl.equals(AccountLevel.SILVER)) {
                p.sendMessage("KEISTI I BASIC");
            }
            else {
                p.sendMessage("nebegalima nuzeminti.");
            }
        }
    }
    
    public void runConsole( String uuid,  int type) {
        try {
            UUID uid = UUID.fromString(uuid);
            if (Bukkit.getOfflinePlayer(uid) == null) {
                Bank.log.info("Player not exist");
            }
            Bank.getModule().AccountMng.CreateBankAccount(null, null, false);
            Bank.log.info("Bank account created for " + Bukkit.getOfflinePlayer(uid).getName() + ";");
        }
        catch (IllegalArgumentException ignore) {
            Bank.log.info("Bad UUID Format");
        }
    }
    
    private Conversation BuildConversation(Player p, Prompt cl, long IBAN) {
        HashMap<Object, Object> data = new HashMap<Object, Object>();
        data.put("IBAN", IBAN);
        ConversationFactory Cf = new ConversationFactory(this.bank);
        Cf.withFirstPrompt(cl).withTimeout(25).withEscapeSequence("at\u0161aukti").withInitialSessionData(data).withEscapeSequence("atsaukti").withLocalEcho(false);
        Conversation conversation = Cf.buildConversation(p);
        return conversation;
    }
}
