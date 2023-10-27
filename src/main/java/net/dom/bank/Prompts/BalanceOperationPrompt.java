package net.dom.bank.Prompts;

import org.bukkit.entity.Player;
import org.bukkit.conversations.Prompt;
import net.dom.bank.FormattedString;
import net.dom.bank.Bank;
import net.dom.bank.Objects.BalanceOperation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;

public class BalanceOperationPrompt extends StringPrompt
{
    public String getPromptText(ConversationContext Cc) {
        BalanceOperation Action = (BalanceOperation)Cc.getSessionData("Action");
        if (Action.equals(BalanceOperation.DEPOSIT)) {
            return Bank.getInstance().getLang().Text(new FormattedString("BalanceActions.Prompts.Deposit"));
        }
        return Bank.getInstance().getLang().Text(new FormattedString("BalanceActions.Prompts.WithDraw"));
    }
    
    public Prompt acceptInput(ConversationContext Cc, String input) {
        Player p = (Player)Cc.getForWhom();
        try {
            double amount = Double.parseDouble(input);
            if (input.length() > 6) {
                throw new NumberFormatException();
            }
            if (amount <= 0.0) {
                throw new NumberFormatException();
            }
            BalanceOperation Action = (BalanceOperation)Cc.getSessionData("Action");
            long IBAN = (long)Cc.getSessionData("IBAN");
            Bank.getModule().BalMng.promptValue(p, amount, IBAN, Action);
        }
        catch (NumberFormatException ignore) {
            Bank.getInstance().getLang().sendRawMessage(new FormattedString("incorrectValue"), p);
        }
        return Prompt.END_OF_CONVERSATION;
    }
}
