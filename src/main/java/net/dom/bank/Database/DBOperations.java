package net.dom.bank.Database;

import org.hibernate.Transaction;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;
import javax.persistence.NoResultException;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;
import net.dom.bank.Objects.BankUser;
import net.dom.bank.controllers.budgetController;
import net.dom.bank.Bank;

public class DBOperations
{
    private final Bank plugin;
    
    public DBOperations(Bank pl) {
        this.plugin = pl;
        this.setup();
        budgetController.initializeBudgets();
    }
    
    public Long getNonPlayerIban(String name) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<BankUser> root = criteriaQuery.from(BankUser.class);
        criteriaQuery.select(root.get("IBAN"));
        criteriaQuery.where(criteriaBuilder.like(root.get("settings"), "%\"Name\": \"" + name + "\"%"));
        try {
            return session.createQuery(criteriaQuery).setCacheable(false).getSingleResult();
        }
        catch (NoResultException ignore) {
            return null;
        }
    }
    
    public void CreateBankAccountNonPlayer(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.getNonPlayerIban(name) == null) {
                this.plugin.getModules().AccountMng.CreateBankAccount(null, name, true);
            }
        });
    }
    
    private void setup() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        String SQL = "CREATE TABLE IF NOT EXISTS `bank_accounts` (`created_date` datetime)";
        session.createNativeQuery(SQL).setCacheable(false).executeUpdate();
        tx.commit();
        session.getSessionFactory().getCache().evictEntityData(BankUser.class);
        session.close();
    }
}
