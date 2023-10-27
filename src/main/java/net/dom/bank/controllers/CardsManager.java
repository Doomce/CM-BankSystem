// 
// Decompiled by Procyon v0.5.36
// 

package net.dom.bank.controllers;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.Skull;
import net.dom.bank.Util.Heads;
import net.dom.bank.Objects.AccountLevel;

import java.util.ArrayList;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.conversations.Conversation;
import org.jetbrains.annotations.Nullable;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;
import org.bukkit.Material;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.bukkit.inventory.ItemStack;
import net.dom.bank.Objects.BankCard;

import net.dom.bank.Objects.BankUser;
import net.dom.bank.Database.HibernateUtil;
import org.bukkit.Bukkit;
import net.dom.bank.FormattedString;
import org.bukkit.entity.Player;
import net.dom.bank.Util.CardFiles;
import org.bukkit.NamespacedKey;
import net.dom.bank.Bank;
import org.bukkit.event.Listener;

public class CardsManager implements Listener
{
    private static final String noPerm = "noPermission";
    private static final String adminPerm = "Bank.Admin";
    private final Bank bank;
    final NamespacedKey nmsCardId;
    final NamespacedKey nmsOwner;
    final NamespacedKey nmsIban;
    final NamespacedKey nmsContactless;
    final NamespacedKey nmsATM;
    final NamespacedKey nmsATMState;
    private static final double cardCost = 5.0;
    public static final int maxIncorrectPins = 3;
    final CardFiles cardFiles;
    
    public CardsManager(Bank b) {
        this.bank = b;
        this.nmsCardId = new NamespacedKey(this.bank, "bank-card-id");
        this.nmsOwner = new NamespacedKey(this.bank, "bank-card-owner");
        this.nmsIban = new NamespacedKey(this.bank, "bank-card-iban");
        this.nmsContactless = new NamespacedKey(this.bank, "bank-card-contactless");
        this.nmsATM = new NamespacedKey(this.bank, "bank-atm-data");
        this.nmsATMState = new NamespacedKey(this.bank, "bank-atm-data-state");
        this.cardFiles = new CardFiles(b);
    }
    
    public void giveCard(Player p, long iban) {
        if (!p.hasPermission("Bank.Card.Get")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        if (p.getInventory().firstEmpty() == -1) {
            this.bank.getLang().sendMessage(new FormattedString("notEnoughSpaceInventory"), p);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            BankUser user = session.load(BankUser.class, iban);
            if (user.getMoney() < 5.0) {
                session.close();
                this.bank.getLang().sendMessage(new FormattedString("BankCards.notEnoughMoney").Replace("%cost%", 5.0), p);
            }
            else {
                int cardID;
                Transaction tx = session.beginTransaction();
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
                Root<BankUser> root = criteriaQuery.from(BankUser.class);
                criteriaQuery.select(criteriaBuilder.max(root.get("cardID")));
                try {
                    cardID = session.createQuery(criteriaQuery).setCacheable(false).getSingleResult();
                    ++cardID;
                }
                catch (NullPointerException ignore) {
                    cardID = 24380;
                }
                user.setCardID(cardID);
                user.subtractMoney(5.0);
                Bank.log.info(user.getIBAN() + " -" + 5.0 + ". Pay for Card. Balance after: " + user.getMoney() + ";");
                budgetController.addMoney("Budget", 5.0);
                session.save(user);
                tx.commit();
                session.close();
                BankCard bankCard = new BankCard(cardID, iban, p);
                p.getInventory().addItem(this.generateBankCardItemStack(bankCard));
                this.bank.getLang().sendMessage(new FormattedString("BankCards.gotCard"), p);
            }
        });
    }
    
    public void blockCard(Player p, long iban, int id) {
        BankCard card = new BankCard(id, iban, null);
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> this.cardFiles.BanCard(card));
        this.bank.getLang().sendRawMessage(new FormattedString("BankCards.Banned"), p);
    }
    
    private ItemStack generateBankCardItemStack(BankCard bankCard) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta iMeta = stack.getItemMeta();
        assert iMeta != null;
        iMeta.setDisplayName(this.bank.getLang().Text(new FormattedString("BankCards.Item.Name")));
        iMeta.setLore(this.bank.getLang().TextList(new FormattedString("BankCards.Item.Lore")));
        iMeta.getPersistentDataContainer().set(this.nmsCardId, PersistentDataType.INTEGER, bankCard.CardID);
        iMeta.getPersistentDataContainer().set(this.nmsOwner, PersistentDataType.STRING, bankCard.Owner.getName());
        iMeta.getPersistentDataContainer().set(this.nmsIban, PersistentDataType.LONG, bankCard.IBAN);
        iMeta.getPersistentDataContainer().set(this.nmsContactless, PersistentDataType.DOUBLE, bankCard.ContactlessLimit);
        stack.setItemMeta(iMeta);
        return stack;
    }
    
    @EventHandler
    public void onInteractATM(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.getClickedBlock() == null && event.getItem() == null && !event.getItem().getType().isBlock()) {
            return;
        }
        Block block = event.getClickedBlock();
        if (!block.getType().equals(Material.PLAYER_HEAD) && !block.getType().equals(Material.PLAYER_WALL_HEAD)) {
            return;
        }
        if (!this.atmBlockCheck(block)) {
            return;
        }
        Player p = event.getPlayer();
        if (!this.atmStateCheck(block)) {
            this.bank.getLang().sendMessage(new FormattedString("GUIS.ATM.state-Off"), p);
            return;
        }
        if (p.isConversing() || !Bank.CheckCoolDown(p.getUniqueId())) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> this.conversation(p, block));
    }
    
    private void conversation(Player p, Block block) {
        if (!p.hasPermission("Bank.Card.Use") && !p.hasPermission("Bank.ATM.Use")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        if (!p.getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
            return;
        }
        BankCard bankCard = this.checkBankCardItemStack(p, p.getInventory().getItemInMainHand());
        if (bankCard == null) {
            return;
        }
        if (this.isPinRequired(p, 0.0, bankCard)) {
            ConversationFactory factory = new ConversationFactory(this.bank);
            factory.withFirstPrompt(new ValidatingPrompt() {
                @NotNull
                public String getPromptText(@NotNull ConversationContext context) {
                    return CardsManager.this.bank.getLang().Text(new FormattedString("BankCards.pinPrompt"));
                }
                
                protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
                    String substring = bankCard.Owner.getUniqueId().toString().substring(32);
                    if (input.equalsIgnoreCase(substring)) {
                        CardsManager.this.openATM(p, bankCard, CardsManager.this.atmBlockID(block));
                        return true;
                    }
                    if (bankCard.getPinTries() >= 3) {
                        CardsManager.this.blockCard(p, bankCard.IBAN, bankCard.CardID);
                        return true;
                    }
                    bankCard.addPinTry();
                    CardsManager.this.bank.getLang().sendRawMessage(new FormattedString("BankCards.pinIncorrect"), p);
                    return false;
                }
                
                @Nullable
                protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                    return Prompt.END_OF_CONVERSATION;
                }
            }).withTimeout(25).withEscapeSequence("atsaukti").withLocalEcho(false);
            Conversation conversation = factory.buildConversation(p);
            conversation.begin();
        }
        else {
            Bukkit.getScheduler().runTask(this.bank, () -> this.openATM(p, bankCard, this.atmBlockID(block)));
        }
    }
    
    private void openATM(Player p, BankCard bankCard, int atmID) {
        this.bank.getModules().GuiMng.BankATM(p, bankCard.IBAN, atmID);
    }
    
    public boolean isPinRequired(Player p, double amount, BankCard bankCard) {
        return !p.getUniqueId().toString().equals(bankCard.Owner.getUniqueId().toString()) && (!bankCard.Owner.isOnline() || amount >= bankCard.ContactlessLimit);
    }
    
    public BankCard checkBankCardItemStack(Player p, ItemStack itemStack) {
        ItemMeta iMeta = itemStack.getItemMeta();
        BankCard card;
        try {
            assert iMeta != null;
            PersistentDataContainer itemData = iMeta.getPersistentDataContainer();
            int var1 = itemData.get(this.nmsCardId, PersistentDataType.INTEGER);
            long var2 = itemData.get(this.nmsIban, PersistentDataType.LONG);
            double var3 = itemData.get(this.nmsContactless, PersistentDataType.DOUBLE);
            Player var4 = Bukkit.getPlayer(itemData.get(this.nmsOwner, PersistentDataType.STRING));
            if (this.cardFiles.isCardInBList(var1)) {
                itemData.remove(this.nmsCardId);
                itemData.remove(this.nmsOwner);
                itemData.remove(this.nmsIban);
                itemData.remove(this.nmsContactless);
                p.getInventory().getItemInMainHand().setItemMeta(iMeta);
                this.bank.getLang().sendMessage(new FormattedString("BankCards.bannedFound"), p);
                return null;
            }
            card = new BankCard(var1, var2, var4, var3);
        }
        catch (NullPointerException ignore) {
            return null;
        }
        return card;
    }
    
    public ItemMeta setupCardData(ItemMeta meta, BankUser bankUser) {
        meta.setDisplayName(this.bank.getLang().Text(new FormattedString("GUIS.Main.Card.Name")));
        List<String> lore = new ArrayList<>();
        Integer id = bankUser.getCardID();
        if (id != null) {
            if (this.cardFiles.isCardInBList(id)) {
                lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.Card.Lore.Card-Banned").Replace("%date%", this.cardFiles.BannedCardDate(id))));
                lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.Card.Lore.Card-Not-Have")));
            }
            else {
                lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.Card.Lore.Have-Card").Replace("%id%", id)));
            }
        }
        else {
            lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.Card.Lore.Card-Not-Have")));
        }
        lore.addAll(this.bank.getLang().TextList(new FormattedString("GUIS.Main.Card.Lore.Card-Desc").Replace("%cost%", 5.0)));
        meta.setLore(lore);
        return meta;
    }
    
    public void cardItemAction(Player p, BankUser bankUser) {
        if (bankUser.getLevel().equals(AccountLevel.RESTRICTED) || bankUser.getLevel().equals(AccountLevel.BUSINESS)) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        try {
            int id = bankUser.getCardID();
            if (this.cardFiles.isCardInBList(id)) {
                this.giveCard(p, bankUser.getIBAN());
                return;
            }
            this.blockCard(p, bankUser.getIBAN(), id);
        }
        catch (NullPointerException ignore) {
            this.giveCard(p, bankUser.getIBAN());
        }
    }
    
    public void giveATMHead(Player p) {
        if (!p.hasPermission("Bank.ATM.Create")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        if (p.getInventory().firstEmpty() == -1) {
            this.bank.getLang().sendMessage(new FormattedString("notEnoughSpaceInventory"), p);
            return;
        }
        ItemStack atmItem = Heads.createSkull(this.bank.getConfig().getString("ATMS.head"));
        ItemMeta iMeta = atmItem.getItemMeta();
        assert iMeta != null;
        iMeta.setDisplayName(this.bank.getConfig().getString("GUIS.ATMS.name"));
        atmItem.setItemMeta(iMeta);
        p.getInventory().addItem(atmItem);
    }
    
    public void setATMBlock(Player p) {
        if (!p.hasPermission("Bank.Admin")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        Block targetBlock = p.getTargetBlock(null, 3);
        if (!targetBlock.getType().equals(Material.PLAYER_HEAD) && !targetBlock.getType().equals(Material.PLAYER_WALL_HEAD)) {
            return;
        }
        Skull blockState = (Skull)targetBlock.getState();
        if (blockState.getPersistentDataContainer().has(this.nmsATM, PersistentDataType.INTEGER)) {
            p.sendMessage("Jau cia yra bankomatas");
            return;
        }
        int atmId = this.cardFiles.GenerateATMID();
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> this.cardFiles.AddATMToFile(targetBlock.getLocation(), atmId));
        blockState.getPersistentDataContainer().set(this.nmsATM, PersistentDataType.INTEGER, atmId);
        blockState.getPersistentDataContainer().set(this.nmsATMState, PersistentDataType.DOUBLE, 1.0);
        blockState.update();
        p.sendMessage("Nustatei ATM.");
    }
    
    public void removeATMBlock(Player p, int id) {
        if (!p.hasPermission("Bank.Admin")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        if (!this.cardFiles.isATMExists(id)) {
            p.sendMessage("Tokio id nėra.");
            return;
        }
        Block block = p.getWorld().getBlockAt(this.cardFiles.getATMLoc(id));
        Skull blockState = (Skull)block.getState();
        blockState.getPersistentDataContainer().remove(this.nmsATM);
        blockState.getPersistentDataContainer().remove(this.nmsATMState);
        blockState.update();
        block.setType(Material.AIR);
        Bukkit.getScheduler().runTaskAsynchronously(this.bank, () -> this.cardFiles.RemoveATM(id));
        p.sendMessage("Ištrynei ATM.");
    }

    public boolean atmBlockCheck(Block targetBlock) {
        Skull blockState = (Skull)targetBlock.getState();
        return blockState.getPersistentDataContainer().has(this.nmsATM, PersistentDataType.INTEGER);
    }

    public int atmBlockID(Block targetBlock) {
        Skull blockState = (Skull)targetBlock.getState();
        return blockState.getPersistentDataContainer().get(this.nmsATM, PersistentDataType.INTEGER);
    }

    public boolean atmStateCheck(Block targetBlock) {
        Skull blockState = (Skull)targetBlock.getState();
        return blockState.getPersistentDataContainer().get(this.nmsATMState, PersistentDataType.DOUBLE) > 0.0;
    }

    public void atmSetState(Player p, Block targetBlock, boolean state) {
        Skull blockState = (Skull)targetBlock.getState();
        if (state) {
            blockState.getPersistentDataContainer().set(this.nmsATMState, PersistentDataType.DOUBLE, 1.0);
            blockState.update();
            p.sendMessage("Įjungei ATM");
            return;
        }
        blockState.getPersistentDataContainer().set(this.nmsATMState, PersistentDataType.DOUBLE, 0.0);
        blockState.update();
        p.sendMessage("Išjungei ATM");
    }
    
    @EventHandler
    public void atmBreak(BlockBreakEvent event) {
        Block targetBlock = event.getBlock();
        if ((targetBlock.getType().equals(Material.PLAYER_HEAD) || targetBlock.getType().equals(Material.PLAYER_WALL_HEAD)) && this.atmBlockCheck(targetBlock)) {
            event.setCancelled(true);
        }
    }
    
    public void atmListGui(Player p) {
        if (!p.hasPermission("Bank.Admin")) {
            this.bank.getLang().sendMessage(new FormattedString("noPermission"), p);
            return;
        }
        this.bank.getModules().GuiMng.Admin.ATMList(p, this.cardFiles);
    }
}
