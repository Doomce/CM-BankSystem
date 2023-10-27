package net.dom.bank.Database;

import org.hibernate.boot.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import net.dom.bank.Objects.BankUser;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cache.ehcache.internal.EhcacheRegionFactory;
import org.mariadb.jdbc.Driver;
import java.util.HashMap;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import net.dom.bank.Bank;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;

public class HibernateUtil
{
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (HibernateUtil.sessionFactory == null) {
            try {
                if (Bank.getInstance().getConfig() == null) {
                    Bank.log.severe("config.yml not found");
                    Bukkit.getPluginManager().disablePlugin((Plugin)Bank.getInstance());
                }
                ConfigurationSection section;
                if ((section = Bank.getInstance().getConfig().getConfigurationSection("database")) == null) {
                    throw new IllegalArgumentException("database section in configuration not found");
                }
                final StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
                final HashMap<String, Object> settings = new HashMap<String, Object>();
                settings.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
                settings.put("hibernate.connection.driver_class", Driver.class.getName());
                settings.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
                settings.put("hibernate.connection.url", "jdbc:mariadb://" + section.getString("host") + ":" + section.getString("port") + "/" + section.getString("database") + "?useSSL=" + section.getString("ssl"));
                settings.put("hibernate.connection.username", section.getString("user"));
                settings.put("hibernate.connection.password", section.getString("password"));
                settings.put("hibernate.hbm2ddl.auto", "update");
                settings.put("hibernate.cache.region.factory_class", EhcacheRegionFactory.class.getName());
                settings.put("hibernate.cache.use_second_level_cache", true);
                settings.put("hibernate.cache.use_query_cache", true);
                settings.put("hibernate.cache.provider_configuration_file_resource_path", "/ehcache.xml");
                section = section.getConfigurationSection("properties");
                if (section == null) {
                    throw new IllegalArgumentException("database.properties section in configuration not found");
                }
                for (final String key : section.getKeys(false)) {
                    settings.put("hibernate.hikari." + key, section.getString(key));
                }
                settings.put("hibernate.cache.ehcache.missing_cache_strategy", "create");
                settings.put("hibernate.hikari.dataSource.cachePrepStmts", "true");
                settings.put("hibernate.hikari.dataSource.prepStmtCacheSize", "250");
                settings.put("hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048");
                settings.put("hibernate.hikari.dataSource.useUnicode", "true");
                settings.put("hibernate.hikari.dataSource.characterEncoding", "utf8");
                registryBuilder.applySettings(settings);
                HibernateUtil.registry = registryBuilder.build();
                final MetadataSources sources = new MetadataSources(HibernateUtil.registry).addAnnotatedClass(BankUser.class);
                final Metadata metadata = sources.getMetadataBuilder().build();
                HibernateUtil.sessionFactory = metadata.getSessionFactoryBuilder().build();
            }
            catch (Exception e) {
                if (HibernateUtil.registry != null) {
                    StandardServiceRegistryBuilder.destroy(HibernateUtil.registry);
                }
                e.printStackTrace();
            }
        }
        return HibernateUtil.sessionFactory;
    }
    
    public static void shutdown() {
        if (HibernateUtil.registry != null) {
            StandardServiceRegistryBuilder.destroy(HibernateUtil.registry);
        }
    }
}
