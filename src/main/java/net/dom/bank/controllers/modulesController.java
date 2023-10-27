package net.dom.bank.controllers;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import net.dom.bank.Bank;

public class modulesController
{
    public InterestManager Interest;
    public LevelManager Packets;
    public AccountManager AccountMng;
    public CardsManager CardMng;
    public GUIS GuiMng;
    public BalanceManager BalMng;
    
    public modulesController(Bank plugin, PluginManager PM) {
        this.Interest = new InterestManager(plugin);
        this.Packets = new LevelManager(plugin);
        this.AccountMng = new AccountManager(plugin);
        PM.registerEvents(this.CardMng = new CardsManager(plugin), plugin);
        this.GuiMng = new GUIS(plugin);
        this.BalMng = new BalanceManager(plugin);
        new budgetController(plugin);
    }
}
