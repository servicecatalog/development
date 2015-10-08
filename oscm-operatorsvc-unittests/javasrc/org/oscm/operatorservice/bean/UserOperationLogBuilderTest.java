/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: tokoda                                                      
 *                                                                              
 *  Creation Date: Oct 7, 2011                                                      
 *                                                                              
 *  Completion Time: Oct 7, 2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.operatorservice.bean;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import org.oscm.dataservice.local.DataService;
import org.oscm.operationslog.UserOperationLogEntityType;

public class UserOperationLogBuilderTest {

    @Test
    public void testBuild() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        List<Object[]> marketplaceResult = new ArrayList<Object[]>();

        Object[] mplResult1 = new Object[8];
        mplResult1[0] = sdf.parse("2011-01-01 00:00:00.000"); // moddate
        mplResult1[1] = "ADD"; // op
        mplResult1[2] = "adduser"; // user
        mplResult1[3] = new Date(0); // objversion
        mplResult1[4] = "Fujitsu"; // marketplace
        mplResult1[5] = "AXA"; // organization
        mplResult1[6] = "e7eb9a1c"; // id
        mplResult1[7] = "TRUE"; // grobal
        marketplaceResult.add(mplResult1);

        Object[] mplResult2 = new Object[8];
        mplResult2[0] = sdf.parse("2012-12-12 00:00:00.000"); // moddate
        mplResult2[1] = "DELETE"; // op
        mplResult2[2] = "moduser"; // user
        mplResult1[3] = new Date(0); // objversion
        mplResult2[4] = "EST"; // marketplace
        mplResult2[5] = "SSS"; // organization
        mplResult2[6] = "aaa1aaa1"; // id
        mplResult2[7] = "FALSE"; // grobal
        marketplaceResult.add(mplResult2);

        List<Object[]> marketplaceEntryResult = new ArrayList<Object[]>();

        Object[] mplEntryResult1 = new Object[11];
        mplEntryResult1[0] = sdf.parse("2011-01-01 00:00:00.111"); // moddate
        mplEntryResult1[1] = "ADD"; // op
        mplEntryResult1[2] = "adduser"; // user
        mplEntryResult1[3] = new Date(0); // objversion
        mplEntryResult1[4] = "Fujitsu"; // marketplace
        mplEntryResult1[5] = "Service1"; // service
        mplEntryResult1[6] = "sup1"; // supplier
        mplEntryResult1[7] = "abcdefg8"; // id
        mplEntryResult1[8] = "3"; // position
        mplEntryResult1[9] = "FALSE"; // visible for anonymous
        mplEntryResult1[10] = "TRUE"; // edition visible in catalog
        marketplaceEntryResult.add(mplEntryResult1);

        Object[] mplEntryResult2 = new Object[11];
        mplEntryResult2[0] = sdf.parse("2011-01-01 11:11:11.000"); // moddate
        mplEntryResult2[1] = "DELETE"; // op
        mplEntryResult2[2] = "moduser"; // user
        mplEntryResult2[3] = new Date(0); // objversion
        mplEntryResult2[4] = "EST"; // marketplace
        mplEntryResult2[5] = "Service2"; // service
        mplEntryResult2[6] = "sup2"; // supplier
        mplEntryResult2[7] = "1234567h"; // id
        mplEntryResult2[8] = "6"; // position
        mplEntryResult2[9] = "TRUE"; // visible for anonymous
        mplEntryResult2[10] = "FALSE"; // edition visible in catalog
        marketplaceEntryResult.add(mplEntryResult2);

        Query queryMock = mock(Query.class);
        when(queryMock.getResultList()).thenReturn(marketplaceResult,
                marketplaceEntryResult);

        DataService dmMock = mock(DataService.class);
        when(dmMock.createNativeQuery(anyString())).thenReturn(queryMock);

        UserOperationLogSqlResult sql = new UserOperationLogSqlResult(dmMock,
                UserOperationLogEntityType.MARKETPLACE, 100000, 200000);
        UserOperationLogBuilder builder = new UserOperationLogBuilder(sql);

        String result = builder.build();

        String expectedLine1 = "01/01/2011_00:00:00.000 FSP_INTS-BSS: INFO: 30041:,log,MARKETPLACE,op,ADD,user,adduser,marketplace,Fujitsu,organization,AXA,id,e7eb9a1c,global,TRUE";
        String expectedLine2 = "01/01/2011_00:00:00.111 FSP_INTS-BSS: INFO: 30042:,log,MARKETPLACE_ENTRY,op,ADD,user,adduser,marketplace,Fujitsu,service,Service1,supplier,sup1,id,abcdefg8,position,3,visible for anonymous,FALSE,edition visible in catalog,TRUE";
        String expectedLine3 = "01/01/2011_11:11:11.000 FSP_INTS-BSS: INFO: 30042:,log,MARKETPLACE_ENTRY,op,DELETE,user,moduser,marketplace,EST,service,Service2,supplier,sup2,id,1234567h,position,6,visible for anonymous,TRUE,edition visible in catalog,FALSE";
        String expectedLine4 = "12/12/2012_00:00:00.000 FSP_INTS-BSS: INFO: 30041:,log,MARKETPLACE,op,DELETE,user,moduser,marketplace,EST,organization,SSS,id,aaa1aaa1,global,FALSE";
        String lineSeparator = "\n";
        String expected = expectedLine1 + lineSeparator + expectedLine2
                + lineSeparator + expectedLine3 + lineSeparator + expectedLine4
                + lineSeparator;

        assertEquals(expected, result);
    }
}
