/**************************************************************G*********o****o****g**o****og**joob*********************
 * File: HibernateHelper.java
 * Course materials (19W) CST 8277
 * @authors (original) Stanley Pieda, Mike Norman
 */
package com.algonquincollege.cst8277.assignment1;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.algonquincollege.cst8277.assignment1.model.Ticket;

/**
 * <b>Description</b></br></br>
 * Enforce the singleton property with a private constructor for enum type
 *
 * @see <a href="https://www.pearson.com/us/higher-education/program/Bloch-Effective-Java-2nd-Edition/PGM310651.html">J. Bloch, EffectiveJava, Addison-Wesly, 2008 (ISBN-13: 978-0-321-35668-0)</a>
 *
 * @authors Stanley Pieda, mwnorman
 *
 * @date (modified) 2019 09
 * 
 * @author Jiaying Huang 040-885-198
 *
 */
public enum HibernateHelper {

    /** Only use one enum type for Singleton Design Pattern */
    INSTANCE;

    /**
     * member field to hold onto SLF4J logger
     * @authors Stanley Pieda, Mike Norman
     */
    private Logger logger;

    /**
     * member field to hold onto Hibernate factory
     * @authors Stanley Pieda, Mike Norman
     */
    private SessionFactory factory;
   
  
 
    private HibernateHelper() {
        logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        try {
            logger.info("building Hibernate configuration from hibernate.cfg.xml");
            
            //TODO
            /**
             *  @author Jiaying Huang 040-885-198
             * **/
            
            if (factory == null)
            {
               Configuration configuration = new Configuration().configure(HibernateHelper.class.getResource("/hibernate.cfg.xml"));
               configuration.addAnnotatedClass(Ticket.class);
               StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
               serviceRegistryBuilder.applySettings(configuration.getProperties());
               ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
               factory = configuration.buildSessionFactory(serviceRegistry);
            }
            
           
      }
        catch (Throwable ex) {
            factory = null;
            logger.error("Building SessionFactory failed:", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Insert Ticket into database<br/>
     * Checks factory to prevent NullPointerException<br/>
     * Checks ticket, skip DB round-trip if null
     *
     * @param ticket
     *
     * @authors Stanley Pieda, Mike Norman
     *
     */
    public void saveTicket(Ticket ticket) {
        
        
        if (factory == null) {
            logger.error("Hibernate factory is null");
            return;
        }
        if (ticket == null) {
            logger.warn("ticket is null; don't bother trying to insert into the database");
            return;
        }
        Session s = null;
        Transaction tx = null;
        try {
            logger.trace("attempting to save ticket {} to database", ticket.toString());
            s = factory.openSession();
            tx = s.beginTransaction();
            ticket.setTstamp(OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            s.save(ticket);
            tx.commit();
            logger.trace("ticket {} successfully saved to database", ticket.toString());
        }
        catch (Exception e) {
            logger.error("Something went wrong trying to insert ticket in the database:", e);
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
        finally {
            s.close();
        }
    }

    // nite-nite!
    public void shutdown() {
        if (factory != null) {
            factory.close();
            factory = null;
            logger.info("Hibernate factory shutdown");
        }
    }

}