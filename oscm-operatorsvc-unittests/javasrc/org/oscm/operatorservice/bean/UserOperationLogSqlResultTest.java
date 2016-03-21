/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: tokoda                                                     
 *                                                                              
 *  Creation Date: Oct 11, 2011                                                      
 *                                                                              
 *  Completion Time: Oct 11, 2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.operatorservice.bean;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import org.oscm.dataservice.local.DataService;
import org.oscm.operationslog.UserOperationLogEntityType;

public class UserOperationLogSqlResultTest {

    @Test
    public void testExecute() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        List<Object[]> result1 = new ArrayList<Object[]>();
        result1.add(new Object[] { sdf.parse("2011-01-01 00:00:00.000"), "A",
                "", BigInteger.valueOf(1), });
        result1.add(new Object[] { sdf.parse("2011-01-01 00:00:00.000"), "B",
                "", BigInteger.valueOf(0) });
        result1.add(new Object[] { sdf.parse("2011-01-04 00:00:00.001"), "C",
                "", BigInteger.valueOf(0) });

        List<Object[]> result2 = new ArrayList<Object[]>();
        result2.add(new Object[] { sdf.parse("2011-01-01 00:00:00.000"), "D",
                "", BigInteger.valueOf(2) });
        result2.add(new Object[] { sdf.parse("2011-01-02 00:00:00.000"), "E",
                "", BigInteger.valueOf(1) });
        result2.add(new Object[] { sdf.parse("2011-01-04 00:00:00.000"), "F",
                "", BigInteger.valueOf(0) });

        Query queryMock = mock(Query.class);
        when(queryMock.getResultList()).thenReturn(result1, result2);

        DataService dmMock = mock(DataService.class);
        when(dmMock.createNativeQuery(anyString())).thenReturn(queryMock);

        UserOperationLogSqlResult sql = new UserOperationLogSqlResult(dmMock,
                UserOperationLogEntityType.MARKETPLACE, 100000, 200000);
        sql.execute();
        List<UserOperationLogSqlResult.RowData> rowData = sql.getRowData();

        UserOperationLogSqlResult.RowData rowData1 = rowData.get(0);
        assertEquals("B", rowData1.data[1]);
        assertEquals(sdf.parse("2011-01-01 00:00:00.000"), rowData1.data[0]);
        assertEquals("MARKETPLACE", rowData1.logType);

        UserOperationLogSqlResult.RowData rowData2 = rowData.get(1);
        assertEquals("A", rowData2.data[1]);
        assertEquals(sdf.parse("2011-01-01 00:00:00.000"), rowData2.data[0]);
        assertEquals("MARKETPLACE", rowData2.logType);

        UserOperationLogSqlResult.RowData rowData3 = rowData.get(2);
        assertEquals("D", rowData3.data[1]);
        assertEquals(sdf.parse("2011-01-01 00:00:00.000"), rowData3.data[0]);
        assertEquals("MARKETPLACE_ENTRY", rowData3.logType);

        UserOperationLogSqlResult.RowData rowData4 = rowData.get(3);
        assertEquals("E", rowData4.data[1]);
        assertEquals(sdf.parse("2011-01-02 00:00:00.000"), rowData4.data[0]);
        assertEquals("MARKETPLACE_ENTRY", rowData4.logType);

        UserOperationLogSqlResult.RowData rowData5 = rowData.get(4);
        assertEquals("F", rowData5.data[1]);
        assertEquals(sdf.parse("2011-01-04 00:00:00.000"), rowData5.data[0]);
        assertEquals("MARKETPLACE_ENTRY", rowData5.logType);

        UserOperationLogSqlResult.RowData rowData6 = rowData.get(5);
        assertEquals("C", rowData6.data[1]);
        assertEquals(sdf.parse("2011-01-04 00:00:00.001"), rowData6.data[0]);
        assertEquals("MARKETPLACE", rowData6.logType);
    }
}
