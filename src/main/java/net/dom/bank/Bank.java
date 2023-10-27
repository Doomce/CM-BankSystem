package net.dom.bank;

import java.util.HashSet;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.command.CommandExecutor;
import net.dom.bank.Commands.CommandManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import net.dom.bank.Util.Timer;
import net.dom.bank.controllers.modulesController;
import net.dom.bank.Database.DBOperations;
import java.util.UUID;
import java.util.Set;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.economy.Economy;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bank extends JavaPlugin
{
    public static Logger log;
    public static Economy econ;
    public static Permission perms;
    public String pluginName;
    public static Set<UUID> CoolDown;
    private static Bank Instance;
    private static Language Lang;
    private static DBOperations DB;
    private static modulesController modules;
    private static Timer Timer;
    private static API api;
    
    public Bank() {
        this.pluginName = "BankSystem";
    }
    
    public static Bank getInstance() {
        return Bank.Instance;
    }
    
    public static DBOperations getDB() {
        return Bank.DB;
    }
    
    public static modulesController getModule() {
        return Bank.modules;
    }
    
    public modulesController getModules() {
        return Bank.modules;
    }
    
    public Language getLang() {
        return Bank.Lang;
    }
    
    public static boolean CheckCoolDown(final UUID Uid) {
        if (Bank.CoolDown.contains(Uid)) {
            return false;
        }
        Bank.CoolDown.add(Uid);
        Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)Bank.Instance, () -> Bank.CoolDown.remove(Uid), 40L);
        return true;
    }
    
    public void onEnable() {
        Bank.Instance = this;
        Bank.log = this.getLogger();
        this.setupEconomy();
        this.saveDefaultConfig();
        final CommandManager CMD = new CommandManager(this);
        this.getCommand("bank").setExecutor((CommandExecutor)CMD);
        Bank.Lang = new Language(this);
        Bank.modules = new modulesController(this, this.getServer().getPluginManager());
        Bank.DB = new DBOperations(this);
        api = new API(this);
        Bank.Timer = new Timer(this);
    }
    
    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        Bank.econ = (Economy)rsp.getProvider();
        return Bank.econ != null;
    }
    
    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration((Class)Permission.class);
        Bank.perms = rsp.getProvider();
        return Bank.perms != null;
    }
    
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks((Plugin)this);
        HandlerList.unregisterAll((Plugin)this);
    }
    
    static {
        Bank.econ = null;
        Bank.perms = null;
        Bank.CoolDown = new HashSet<UUID>();
    }
}
