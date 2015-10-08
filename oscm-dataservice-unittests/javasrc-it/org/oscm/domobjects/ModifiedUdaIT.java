/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Mar 27, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.ejb.EJBException;

import org.junit.Test;

import org.oscm.domobjects.enums.ModificationType;
import org.oscm.domobjects.enums.ModifiedEntityType;
import org.oscm.test.ReflectiveClone;
import org.oscm.test.ReflectiveCompare;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;

/**
 * Tests of the modifieduda-related domain objects
 * 
 * @author Zhou
 */
public class ModifiedUdaIT extends DomainObjectTestBase {

    private List<DomainObjectWithHistory<?>> domObjects = new ArrayList<DomainObjectWithHistory<?>>();
    private static final long TARGET_OBJECT_KEY = 10001L;
    private static final long SUBSCRIPTION_KEY = 1001L;
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    /**
     * <b>Testcase:</b> Add new ModifiedUda objects <br>
     * <b>ExpectedResult:</b>
     * <ul>
     * <li>All objects can be retrieved from DB and are identical to provided
     * ModifiedUda objects</li>
     * <li>A history object is created for each ModifiedUda stored</li>
     * <li>History objects are created for CascadeAudit-annotated associated
     * objects</li>
     * </ul>
     * 
     * @throws Throwable
     */
    @Test
    public void testAdd() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    doTestAdd();
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                @Override
                public Void call() {
                    doTestAddCheck();
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    /**
     * <b>Testcase:</b> Modify an existing ModifiedUda object <br>
     * <b>ExpectedResult:</b>
     * <ul>
     * <li>Modification is saved to the DB</li>
     * <li>History object created for the ModifiedUda</li>
     * </ul>
     * 
     * @throws Throwable
     */
    @Test
    public void testModifyModifiedUda() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    doTestModifyModifiedUdaPrepare();
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    doTestModifyModifiedUda();
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    doTestModifyModifiedUdaCheck();
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    /**
     * <b>Testcase:</b> Delete an existing ModifiedUda object <br>
     * <b>ExpectedResult:</b>
     * <ul>
     * <li>ModifiedUda marked as deleted in the DB</li>
     * <li>History object created for the deleted ModifiedUda</li>
     * </ul>
     * 
     * @throws Throwable
     */
    @Test
    public void testDeleteModifiedUda() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    doTestDeleteModifiedUdaPrepare();
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                @Override
                public Void call() {
                    doTestDeleteModifiedUda();
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                @Override
                public Void call() {
                    doTestDeleteModifiedUdaCheck();
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    private void doTestAdd() throws NonUniqueBusinessKeyException {
        // Enter new modifiedUdas
        domObjects.clear();
        ModifiedUda modifiedUda;
        for (int i = 1; i < 10; i++) {
            modifiedUda = new ModifiedUda();
            modifiedUda.setTargetObjectKey(10000 + i);
            modifiedUda.setTargetObjectType(ModifiedEntityType.UDA_VALUE);
            modifiedUda.setValue("udaValue" + i);
            modifiedUda.setSubscriptionKey(10000 + i);
            mgr.persist(modifiedUda);
            domObjects.add((ModifiedUda) ReflectiveClone.clone(modifiedUda));
        }
    }

    private void doTestAddCheck() {
        ModifiedUda saved = null;
        ModifiedUda qry = new ModifiedUda();
        for (DomainObjectWithHistory<?> modifiedUda : domObjects) {
            // Load ModifiedUda and check values
            ModifiedUda modUda = (ModifiedUda) modifiedUda;
            qry.setTargetObjectKey(modUda.getTargetObjectKey());
            qry.setTargetObjectType(modUda.getTargetObjectType());
            qry.setSubscriptionKey(modUda.getSubscriptionKey());
            saved = (ModifiedUda) mgr.find(qry);
            assertNotNull("Cannot find '" + modUda.getTargetObjectKey()
                    + "' in DB", saved);
            assertTrue(ReflectiveCompare.showDiffs(saved, modUda),
                    ReflectiveCompare.compare(saved, modUda));
            assertEquals(modUda.getTargetObjectKey(),
                    saved.getTargetObjectKey());
            assertEquals(modUda.getTargetObjectType(),
                    saved.getTargetObjectType());
            assertEquals(modUda.getSubscriptionKey(),
                    saved.getSubscriptionKey());

            // Load history objects and check them
            List<DomainHistoryObject<?>> histObjs = mgr.findHistory(saved);
            assertEquals(1, histObjs.size());
            DomainHistoryObject<?> hist = histObjs.get(0);
            assertEquals(ModificationType.ADD, hist.getModtype());
            assertEquals("modUser", "guest", hist.getModuser());
            assertTrue(ReflectiveCompare.showDiffs(modUda, hist),
                    ReflectiveCompare.compare(modUda, hist));
            assertEquals("OBJID in history different", modUda.getKey(),
                    hist.getObjKey());
        }
    }

    private void doTestModifyModifiedUdaPrepare()
            throws NonUniqueBusinessKeyException {
        domObjects.clear();
        ModifiedUda modifiedUda = givenModifiedUda(TARGET_OBJECT_KEY,
                SUBSCRIPTION_KEY, VALUE1);
        mgr.persist(modifiedUda);
    }

    private void doTestModifyModifiedUda() {
        ModifiedUda modifiedUda = findModifiedUda(TARGET_OBJECT_KEY,
                SUBSCRIPTION_KEY);
        modifiedUda.setValue(VALUE2);
        domObjects.clear();
        domObjects.add((ModifiedUda) ReflectiveClone.clone(modifiedUda));
    }

    private void doTestModifyModifiedUdaCheck() {
        ModifiedUda modifiedUda = (ModifiedUda) domObjects.get(0);
        ModifiedUda saved = findModifiedUda(modifiedUda.getTargetObjectKey(),
                modifiedUda.getSubscriptionKey());
        // Check ModifiedUda data
        assertNotNull("Cannot find '" + modifiedUda.getKey() + "' in DB", saved);
        assertTrue(ReflectiveCompare.showDiffs(saved, modifiedUda),
                ReflectiveCompare.compare(saved, modifiedUda));
        // Load history objects and check them
        List<DomainHistoryObject<?>> histObjs = mgr.findHistory(modifiedUda);
        assertNotNull(
                "History entry 'null' for ModifiedUda " + modifiedUda.getKey(),
                histObjs);
        assertEquals("Exactly 2 history entries expected for ModifiedUda "
                + modifiedUda.getKey(), 2, histObjs.size());
        assertEquals("OBJID in history different", modifiedUda.getKey(),
                histObjs.get(1).getObjKey());
        // load modified history object (should be second)
        DomainHistoryObject<?> hist = histObjs.get(1);
        assertEquals(ModificationType.MODIFY, hist.getModtype());
        assertEquals("modUser", "guest", hist.getModuser());
        assertTrue(ReflectiveCompare.showDiffs(modifiedUda, hist),
                ReflectiveCompare.compare(modifiedUda, hist));
        assertEquals("OBJID in history different", modifiedUda.getKey(),
                hist.getObjKey());
    }

    private void doTestDeleteModifiedUdaPrepare()
            throws NonUniqueBusinessKeyException {
        domObjects.clear();
        ModifiedUda modifiedUda = givenModifiedUda(TARGET_OBJECT_KEY,
                SUBSCRIPTION_KEY, VALUE1);
        mgr.persist(modifiedUda);
    }

    private void doTestDeleteModifiedUda() {
        ModifiedUda modifiedUda = findModifiedUda(TARGET_OBJECT_KEY,
                SUBSCRIPTION_KEY);
        domObjects.clear();
        mgr.remove(modifiedUda);
        domObjects.add((ModifiedUda) ReflectiveClone.clone(modifiedUda));
    }

    private void doTestDeleteModifiedUdaCheck() {
        ModifiedUda modifiedUda = (ModifiedUda) domObjects.get(0);
        ModifiedUda saved = findModifiedUda(modifiedUda.getTargetObjectKey(),
                modifiedUda.getSubscriptionKey());
        // Check ModifiedUda data
        assertNull("Deleted ModifiedUda '" + modifiedUda.getKey()
                + "' can still be accessed via DataManager.find", saved);
        // Load ModifiedUda history objects and check them
        List<DomainHistoryObject<?>> histObjs = mgr.findHistory(modifiedUda);
        assertNotNull(
                "History entry 'null' for ModifiedUda " + modifiedUda.getKey(),
                histObjs);
        assertEquals("Exactly 2 history entries expected for ModifiedUda "
                + modifiedUda.getKey(), 2, histObjs.size());
        assertEquals("OBJID in history different", modifiedUda.getKey(),
                histObjs.get(1).getObjKey());

        // deleted history object
        DomainHistoryObject<?> hist = histObjs.get(1);
        assertEquals(ModificationType.DELETE, hist.getModtype());
    }

    private ModifiedUda givenModifiedUda(long targetObjectKey,
            long subscriptionKey, String value) {
        ModifiedUda modifiedUda = new ModifiedUda();
        modifiedUda.setTargetObjectKey(targetObjectKey);
        modifiedUda.setTargetObjectType(ModifiedEntityType.UDA_VALUE);
        modifiedUda.setSubscriptionKey(subscriptionKey);
        modifiedUda.setValue(value);
        return modifiedUda;
    }

    private ModifiedUda findModifiedUda(long targetObjectKey,
            long subscriptionKey) {
        ModifiedUda qry = new ModifiedUda();
        qry.setTargetObjectKey(targetObjectKey);
        qry.setTargetObjectType(ModifiedEntityType.UDA_VALUE);
        qry.setSubscriptionKey(subscriptionKey);
        ModifiedUda modifiedUda = (ModifiedUda) mgr.find(qry);
        return modifiedUda;
    }
}
