/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: tokoda                                                      
 *                                                                              
 *  Creation Date: Oct 7, 2011                                                      
 *                                                                              
 *  Completion Time: Oct 7, 2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.operatorservice.bean;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.dataservice.local.DataService;
import org.oscm.operationslog.UserOperationLogEntityType;
import org.oscm.operationslog.UserOperationLogQuery;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.internal.types.exception.SaaSSystemException;

/**
 * Read the SQL query result for the platform revenue report.
 * 
 * @author tokoda
 */
public class UserOperationLogSqlResult {

    private static final Log4jLogger LOGGER = LoggerFactory
            .getLogger(UserOperationLogSqlResult.class);

    class RowData implements Comparable<RowData> {
        RowData(String logType, LogMessageIdentifier logMessageIdentifier,
                Object[] data, String[] fieldNames) {
            this.logType = logType;
            this.logMessageIdentifier = logMessageIdentifier;
            this.data = data;
            this.fieldNames = fieldNames;
        }

        final String logType;
        final LogMessageIdentifier logMessageIdentifier;
        final Object[] data;
        final String[] fieldNames;

        /**
         * Compare by moddate, logMsgID and objversion of user operation log
         * query result.
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(RowData o) {
            int result = ((Date) this.data[0]).compareTo((Date) o.data[0]);

            if (result != 0)
                return result;

            result = this.logMessageIdentifier.getMsgId().compareTo(
                    o.logMessageIdentifier.getMsgId());

            if (result != 0)
                return result;

            return ((BigInteger) this.data[3])
                    .compareTo((BigInteger) o.data[3]);
        }
    }

    private final List<RowData> rowData = new ArrayList<RowData>();

    private DataService dm;
    private UserOperationLogEntityType entityType;
    private long fromDate;
    private long toDate;

    UserOperationLogSqlResult(DataService dm,
            UserOperationLogEntityType entityType, long fromDate, long toDate) {
        super();
        this.dm = dm;
        this.entityType = entityType;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    List<RowData> getRowData() {
        return rowData;
    }

    /**
     * Executes the query and store the result object.
     * 
     * @return result of queries
     */
    public void execute() {
        
        for (UserOperationLogQuery logQuery : entityType.getLogQueries()) {
            List<Object[]> results = executeQuery(logQuery);
            for (Object[] result : results) {
                rowData.add(new RowData(logQuery.getLogType(), logQuery
                        .getLogMessageIdentifier(), result, logQuery
                        .getFieldNames()));
            }
        }

        sortRowData();
        
    }

    private List<Object[]> executeQuery(UserOperationLogQuery logQuery) {
        try {
            String nativeQuery = logQuery.getQuery();
            Query query = dm.createNativeQuery(nativeQuery);
            query.setParameter("startDate", new Date(fromDate));
            query.setParameter("endDate", new Date(toDate));
            return logQuery.format(query.getResultList());
        } catch (Exception e) {
            SaaSSystemException sse = new SaaSSystemException(e);
            LOGGER.logError(
                    Log4jLogger.SYSTEM_LOG,
                    sse,
                    LogMessageIdentifier.ERROR_READ_DATA_FOR_USER_OPERATION_LOG_FAILED);
            throw sse;
        }
    }

    /**
     * Sort the result of all result of rowdata.
     * 
     * @param left
     * @param right
     * @return 0:same value, less than 0:left is smaller than right, more than
     *         0:left is bigger than right
     */
    private void sortRowData() {
        Collections.sort(rowData);
    }
}
