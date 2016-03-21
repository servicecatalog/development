/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 16.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import static org.oscm.types.enumtypes.OperationParameterType.INPUT_STRING;
import static org.oscm.types.enumtypes.OperationParameterType.REQUEST_SELECT;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import org.oscm.domobjects.enums.ModificationType;
import org.oscm.test.data.Organizations;
import org.oscm.test.data.TechnicalProducts;
import org.oscm.types.enumtypes.OperationParameterType;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.ServiceAccessType;

/**
 * @author weiser
 * 
 */
public class TechnicalProductOperationIT extends DomainObjectTestBase {

    private Organization organization;
    private TechnicalProduct technicalProduct;

    @Override
    protected void dataSetup() throws Exception {
        createOrganizationRoles(mgr);
        organization = Organizations.createOrganization(mgr,
                OrganizationRoleType.TECHNOLOGY_PROVIDER);
        technicalProduct = TechnicalProducts.createTechnicalProduct(mgr,
                organization, "TP", false, ServiceAccessType.DIRECT);
    }

    @Test
    public void testAdd() throws Exception {
        final TechnicalProductOperation read = doAdd();
        verifyHistory(read, 1, ModificationType.ADD);
    }

    @Test
    public void testModify() throws Exception {
        final TechnicalProductOperation read = doModify(doAdd());
        verifyHistory(read, 2, ModificationType.MODIFY);
    }

    @Test
    public void testDelete() throws Exception {
        final TechnicalProductOperation read = doModify(doAdd());
        runTX(new Callable<Void>() {

            public Void call() throws Exception {
                TechnicalProductOperation operation = mgr.getReference(
                        TechnicalProductOperation.class, read.getKey());
                mgr.remove(operation);
                return null;
            }
        });
        verifyHistory(read, 3, ModificationType.DELETE);
    }

    @Test
    public void testDeleteTP() throws Exception {
        final TechnicalProductOperation read = doAdd();
        runTX(new Callable<Void>() {

            public Void call() throws Exception {
                TechnicalProduct tp = mgr.getReference(TechnicalProduct.class,
                        technicalProduct.getKey());
                mgr.remove(tp);
                return null;
            }
        });
        verifyHistory(read, 2, ModificationType.DELETE);
    }

    @Test
    public void isRequestParameterValuesRequired_Positive() {
        TechnicalProductOperation tpo = prepareTPO(new OperationParameterType[] {
                INPUT_STRING, INPUT_STRING, REQUEST_SELECT, INPUT_STRING });

        assertTrue(tpo.isRequestParameterValuesRequired());
    }

    @Test
    public void isRequestParameterValuesRequired_Negative() {
        TechnicalProductOperation tpo = prepareTPO(new OperationParameterType[] {
                INPUT_STRING, INPUT_STRING, INPUT_STRING });

        assertFalse(tpo.isRequestParameterValuesRequired());
    }

    @Test
    public void isRequestParameterValuesRequired_Empty() {
        TechnicalProductOperation tpo = prepareTPO(new OperationParameterType[] {});

        assertFalse(tpo.isRequestParameterValuesRequired());
    }

    private TechnicalProductOperation prepareTPO(OperationParameterType[] types) {
        TechnicalProductOperation tpo = new TechnicalProductOperation();
        for (OperationParameterType type : types) {
            OperationParameter op = new OperationParameter();
            op.setType(type);
            tpo.getParameters().add(op);
        }
        return tpo;
    }

    private TechnicalProductOperation doAdd() throws Exception {
        final TechnicalProductOperation op = new TechnicalProductOperation();
        op.setOperationId("ID");
        op.setActionUrl("actionUrl");

        final TechnicalProductOperation read = runTX(new Callable<TechnicalProductOperation>() {

            public TechnicalProductOperation call() throws Exception {
                TechnicalProduct tp = mgr.getReference(TechnicalProduct.class,
                        technicalProduct.getKey());
                op.setTechnicalProduct(tp);
                mgr.persist(op);
                return mgr.getReference(TechnicalProductOperation.class,
                        op.getKey());
            }
        });

        Assert.assertEquals("ID", read.getOperationId());
        Assert.assertEquals("actionUrl", read.getActionUrl());
        return read;
    }

    private TechnicalProductOperation doModify(
            final TechnicalProductOperation op) throws Exception {

        final TechnicalProductOperation read = runTX(new Callable<TechnicalProductOperation>() {

            public TechnicalProductOperation call() throws Exception {
                TechnicalProductOperation tpo = mgr.getReference(
                        TechnicalProductOperation.class, op.getKey());
                tpo.setActionUrl("someOtherUlr");
                return mgr.getReference(TechnicalProductOperation.class,
                        tpo.getKey());
            }
        });
        Assert.assertEquals("ID", read.getOperationId());
        Assert.assertEquals("someOtherUlr", read.getActionUrl());
        return read;
    }

    private void verifyHistory(final TechnicalProductOperation read,
            int historyEntries, ModificationType lastModType) throws Exception {
        List<DomainHistoryObject<?>> history = runTX(new Callable<List<DomainHistoryObject<?>>>() {

            public List<DomainHistoryObject<?>> call() throws Exception {
                return mgr.findHistory(read);
            }
        });
        Assert.assertNotNull(history);
        Assert.assertEquals(historyEntries, history.size());
        TechnicalProductOperationHistory oh = (TechnicalProductOperationHistory) history
                .get(history.size() - 1);
        TechnicalProductOperationData dc = read.getDataContainer();
        Assert.assertEquals(dc.getOperationId(), oh.getOperationId());
        Assert.assertEquals(read.getKey(), oh.getObjKey());
        Assert.assertEquals(lastModType, oh.getModtype());
        Assert.assertTrue(oh.getTechnicalProductObjKey() != 0);
    }

}
