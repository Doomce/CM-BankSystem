package net.dom.bank.Prompts;

import net.dom.bank.Util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.conversations.Prompt;
import net.dom.bank.FormattedString;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.plugin.java.JavaPlugin;
import net.dom.bank.Bank;
import org.bukkit.conversations.StringPrompt;

public class InterestCreationPrompt extends StringPrompt
{
    private Bank plugin;
    
    public InterestCreationPrompt() {
        this.plugin = JavaPlugin.getPlugin(Bank.class);
    }
    
    public String getPromptText(ConversationContext Cc) {
        return this.plugin.getLang().Text(new FormattedString("Interest.conversationDays"));
    }
    
    public Prompt acceptInput(ConversationContext Cc, String input) {
        Player p = (Player)Cc.getForWhom();
        try {
            int days = Integer.parseInt(input);
            if (days < 7) {
                Bank.getInstance().getLang().sendRawMessage(new FormattedString("tooLittleDays"), p);
                return Prompt.END_OF_CONVERSATION;
            }
            if (days > 28) {
                Bank.getInstance().getLang().sendRawMessage(new FormattedString("tooManyDays"), p);
                return Prompt.END_OF_CONVERSATION;
            }
            Cc.setSessionData("Days", days);
            double money = (double)Cc.getSessionData("Money");
            double Increment = Bank.getModule().Interest.CalculateInterestRate(money, days);
            double profit = Utils.RoundNumber(Increment * money);
            double rate = Utils.RoundNumber(Increment * 100.0);
            this.plugin.getLang().sendRawMessage(new FormattedString("Interest.calculatedMsg").Replace("%money%", money).Replace("%days%", days).Replace("%rate%", rate).Replace("%profit%", profit), p);
            return new InterestCreationPrompt_2();
        }
        catch (NumberFormatException ignore) {
            Bank.getInstance().getLang().sendRawMessage(new FormattedString("incorrectValue"), p);
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
