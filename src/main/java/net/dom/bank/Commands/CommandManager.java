package net.dom.bank.Commands;

import java.util.UUID;
import net.dom.bank.FormattedString;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;
import net.dom.bank.Bank;
import org.bukkit.command.CommandExecutor;

public class CommandManager implements CommandExecutor
{
    private Bank b;
    
    public CommandManager(Bank bank) {
        this.b = bank;
    }
    
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (label.equalsIgnoreCase("bank")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("budgets")) {
                    if (sender instanceof Player) {
                        Player p = (Player)sender;
                        if (!p.hasPermission("Bank.Budgets")) {
                            this.b.getLang().sendMessage(new FormattedString("noPermission"), p);
                            return false;
                        }
                        this.b.getModules().GuiMng.BankAccounts(p, new UUID(0L, 0L));
                    }
                    return false;
                }
            }
            else if (args.length == 2) {
                if (sender instanceof Player) {
                    Player p = (Player)sender;
                    if (args[0].equalsIgnoreCase("admin")) {
                        if (args[1].equalsIgnoreCase("atm")) {
                            Bank.getModule().CardMng.atmListGui(p);
                        }
                        if (args[1].equalsIgnoreCase("reload")) {
                            this.b.getLang().loadLangFile();
                        }
                    }
                    return false;
                }
            }
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("admin")) {
                    if (args[1].equalsIgnoreCase("balance")) {}
                    if (args[1].equalsIgnoreCase("create") && !(sender instanceof Player)) {}
                }
            }
            else {
                if (sender instanceof Player) {
                    Player p = (Player)sender;
                    Bank.getModule().GuiMng.BankAccounts(p, null);
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
