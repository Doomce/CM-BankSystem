package net.dom.bank;

import net.dom.bank.Database.HibernateUtil;
import net.dom.bank.Objects.AccountLevel;
import net.dom.bank.Objects.BankUser;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class API
{
    protected Bank pl;

    public API(Bank plugin) {
        this.pl = plugin;
    }

    public void setupBusinessAccount(Long iban, String compName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        BankUser bankUser = session.load(BankUser.class, iban);
        if (bankUser == null) return;
        if (bankUser.getLevel().equals(AccountLevel.RESTRICTED)) return;
        Transaction tx = session.beginTransaction();
        bankUser.setLevel(AccountLevel.BUSINESS);
        tx.commit();
        session.close();
    }

}
