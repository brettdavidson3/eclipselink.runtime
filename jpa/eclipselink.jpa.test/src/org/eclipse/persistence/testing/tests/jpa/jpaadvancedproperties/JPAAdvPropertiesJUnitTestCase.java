/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties;

import java.util.ArrayList;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.JPAPropertiesRelationshipTableManager;
 


/**
 * JUnit test case(s) for the TopLink JPAAdvPropertiesJUnitTestCase.
 */
public class JPAAdvPropertiesJUnitTestCase extends JUnitTestCase {
    private static String persistenceUnitName = "JPAADVProperties";
    

    public JPAAdvPropertiesJUnitTestCase() {
        super();
    }
    
    public JPAAdvPropertiesJUnitTestCase(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("JPA Advanced Properties Model");
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testSetup"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testSessionXMLProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testSessionEventListenerProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testExceptionHandlerProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testNativeSQLProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testCacheStatementsAndItsSizeProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testBatchwritingProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testCopyDescriptorNamedQueryToSessionProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testLoggingTyperProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testProfilerTyperProperty"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new JPAPropertiesRelationshipTableManager().replaceTables(JUnitTestCase.getServerSession(persistenceUnitName));
        clearCache();
    }

    
    public void testSessionEventListenerProperty() {
        
        EntityManager em = createEntityManager(persistenceUnitName);
        ServerSession session = getServerSession();
        try {
            //Create new customer
            beginTransaction(em);
            Customer customer = ModelExamples.customerExample1();
            em.persist(customer);
            Integer customerId = customer.getCustomerId();            
            commitTransaction(em);
            
            //Purge it
            beginTransaction(em);
            em.remove(em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId));
            commitTransaction(em);

            Vector listeners = session.getEventManager().getListeners();
            boolean doseCustomizedSessionEventListenerExists=false;
            for (int i =0;i<listeners.size();i++){
                Object aListener = listeners.get(i);
                if(aListener instanceof CustomizedSessionEventListener){
                    doseCustomizedSessionEventListenerExists=true;
                    CustomizedSessionEventListener requiredListener = ((CustomizedSessionEventListener)aListener); 
                    if (!requiredListener.preCommitTransaction) {
                        assertTrue(" The preCommitTransaction event did not fire", true);
                    }
                    if (!requiredListener.postCommitTransaction) {
                        assertTrue("The postCommitTransaction event did not fire", true);
                    }
                    if (!requiredListener.preBeginTransaction) {
                        assertTrue("The preBeginTransaction event did not fire", true);
                    }
                    if (!requiredListener.postBeginTransaction) {
                        assertTrue("The postBeginTransaction event did not fire", true);
                    }
                    if (!requiredListener.postLogin) {
                        assertTrue("The postlogin event did not fire", true);
                    }
                    if (!requiredListener.preLogin) {
                        assertTrue("The preLogin event did not fire", true);
                    }
                }
            }
            if(!doseCustomizedSessionEventListenerExists){
                assertTrue("The session event listener specified by the property eclipselink.session-event-listener in persistence.xml not be processed properly.", true);
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }finally{
            closeEntityManager(em);
        }
    }

    public void testExceptionHandlerProperty() {
        EntityManager em = createEntityManager(persistenceUnitName);
        ServerSession session = getServerSession();
        Expression exp = new ExpressionBuilder().get("name1").equal("George W.");
        try {
            Object result = session.readObject(Customer.class, exp);
            if(!((String)result).equals("return from CustomizedExceptionHandler")){
                assertTrue("The exception handler specified by the property eclipselink.exception-handler in persistence.xml not be processed.", true);
            }
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testNativeSQLProperty() {
        EntityManager em = createEntityManager(persistenceUnitName);
        ServerSession session = getServerSession();
        if(!session.getProject().getLogin().shouldUseNativeSQL()){
            assertTrue("The native sql flag specified as true by the property eclipselink.jdbc.native-sql in persistence.xml, it however read as false.", true);
        }
        closeEntityManager(em);
    }
    
    public void testBatchwritingProperty(){
        EntityManager em = createEntityManager(persistenceUnitName);
        ServerSession session = getServerSession();
        
        if(!(session.getPlatform().usesBatchWriting() && 
           !session.getPlatform().usesJDBCBatchWriting() &&
           !session.getPlatform().usesNativeBatchWriting())){
            assertTrue("The BatcheWriting setting set to BUFFERED by the property eclipselink.jdbc.batch-writing in persistence.xml, JDBC batch writing or natvie batch writing however may be wrong.", true);
        }
        closeEntityManager(em);
    }
    
    public void testCopyDescriptorNamedQueryToSessionProperty(){
        EntityManager em = createEntityManager(persistenceUnitName);
        ServerSession session = getServerSession();
        org.eclipse.persistence.queries.DatabaseQuery query = session.getQuery("customerReadByName");
        if(query == null){
            assertTrue("The copy descriptor named query is enable by the property eclipselink.session.include.descriptor.queries in persistence.xml, one descriptor named query has not been copied to the session", true);
        }
        closeEntityManager(em);
    }
    
    public void testCacheStatementsAndItsSizeProperty() {
        EntityManager em = createEntityManager(persistenceUnitName);
        ServerSession session = getServerSession();
        if(session.getConnectionPools().size()>0){//And if connection pooling is configured,
            if(!session.getProject().getLogin().shouldCacheAllStatements()){
                assertTrue("Caching all statements flag set equals to true by property eclipselink.jdbc.cache-statements in persistence.xml, it however read as false.", true);
            }
        }

        if(!(session.getProject().getLogin().getStatementCacheSize()==100)){
            assertTrue("Statiment cache size set to 100 by property eclipselink.jdbc.cache-statements in persistence.xml, it however read as wrong size.", true);
        }
        closeEntityManager(em);
    }
    
    public void testLoggingTyperProperty(){
        EntityManager em = createEntityManager(persistenceUnitName);
        ServerSession session = getServerSession();
        if(!(session.getSessionLog() instanceof org.eclipse.persistence.logging.JavaLog)){
            assertTrue("Logging type set to JavaLog, it however has been detected as different type logger.", true);
        }
        closeEntityManager(em);
    }
    
    public void testProfilerTyperProperty(){
        if (isOnServer()) {
            // Multi-persistece-unit not work on server.
            return;
        }
		EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        if(!(session.getSessionLog() instanceof org.eclipse.persistence.tools.profiler.PerformanceProfiler)){
            assertTrue("Profiler type set to PerformanceProfiler, it however has been detected as different type Profiler.", true);
        }
        closeEntityManager(em);
        
        em = createEntityManager("JPAADVProperties2");
        if(!(session.getProfiler() instanceof org.eclipse.persistence.tools.profiler.QueryMonitor)){
            assertTrue("Profiler type set to QueryMonitor, it however has been detected as different type profiler.", true);
        }
        closeEntityManager(em);
        
        em = createEntityManager("JPAADVProperties3");
        session = getServerSession();
        if((session.getSessionLog() !=null)){
            assertTrue("no profiler has been set,it however has been detected.", true);
        }
        closeEntityManager(em);
    }
    
    
    public void testSessionXMLProperty() {
        Integer customerId;
        EntityManager em = createEntityManager(persistenceUnitName);
        
        //Create customer
        beginTransaction(em);
        try {
            Customer customer = ModelExamples.customerExample1();       
            ArrayList orders = new ArrayList();
            orders.add(ModelExamples.orderExample1());
            orders.add(ModelExamples.orderExample2());
            orders.add(ModelExamples.orderExample3());
            customer.setOrders(orders);
            em.persist(customer);
            customerId = customer.getCustomerId();
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        
        //Find customer
        beginTransaction(em);
        try {
        Customer cm1 = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
        if(cm1==null){
            assertTrue("Error finding customer ", true);    
        }

        Vector orders = (Vector)cm1.getOrders();
        if(orders == null || orders.size()!=3){
            assertTrue("Error finding order pertaining to the customer ", true);    
            }
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        
        //Query the customer
        Customer cm2 = null;
        beginTransaction(em);
        try {
            Query query = createEntityManager(persistenceUnitName).createNamedQuery("customerReadByName");
            query.setParameter("name", "George W.");
            cm2 = (Customer) query.getSingleResult();
            commitTransaction(em);
        }catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertTrue("Error executing named query 'customerReadByName'", cm2 != null);

        
        //Update customer
        beginTransaction(em);
        String originalName = null;
        try {
            Customer cm = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
            originalName=cm.getName();
            cm.setName(originalName+"-modified");
            em.merge(cm);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();
        Customer cm3 = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
        assertTrue("Error updating Customer", cm3.getName().equals(originalName+"-modified"));

        //Delete customer
        beginTransaction(em);
        try {
            em.remove(em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertTrue("Error deleting Customer", em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId) == null);
        
    }
}
