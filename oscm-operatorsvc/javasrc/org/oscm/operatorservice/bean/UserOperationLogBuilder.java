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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.converter.CsvCreator;
import org.oscm.operationslog.UserOperationLogQuery;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.internal.types.exception.SaaSSystemException;

/**
 * The class <code>UserOperationLogBuilder</code> prepares the data for the user
 * operation log. The SQL data is parsed and then aggregated and finally sorted.
 * 
 * @author tokoda
 */
public class UserOperationLogBuilder {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(UserOperationLogBuilder.class);

    private UserOperationLogSqlResult sqlResult;

    UserOperationLogBuilder(UserOperationLogSqlResult sqlResult) {
        this.sqlResult = sqlResult;
    }

    /**
     * Prepares the data required for the user operation log.
     * 
     * @return messages for user operation log
     */
    public String build() {
        
        StringBuffer log = new StringBuffer();
        sqlResult.execute();
        List<UserOperationLogSqlResult.RowData> result = sqlResult.getRowData();
        if (result != null && !result.isEmpty()) {
            for (UserOperationLogSqlResult.RowData resultLine : result) {
                log.append(readLine(resultLine) + "\n");
            }
        }
        
        return log.toString();
    }

    private String readLine(UserOperationLogSqlResult.RowData rowData) {
        List<String> log = new ArrayList<String>();

        String[] fieldNames = rowData.fieldNames;
        Object[] items = rowData.data;
        String moddateForHeader = null;
        // "moddate" for log header have to be considered to create log,
        // so SQL query has to contain the "moddate" of the history as first
        // column of results
        if (items.length >= fieldNames.length) {
            moddateForHeader = formatResultItem(items[0]);
            // add log type
            log.add("log");
            log.add(rowData.logType);
            // other items
            for (int i = 1; i < fieldNames.length; i++) {
                if (fieldNames[i]
                        .equals(UserOperationLogQuery.COMMON_COLUMN_OBJVERSION))
                    continue;
                log.add(fieldNames[i]);
                log.add(formatResultItem(items[i]));
            }
        } else {
            SaaSSystemException sse = new SaaSSystemException();
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    sse,
                    LogMessageIdentifier.ERROR_WRONG_DATA_FOR_USER_OPERATION_LOG);
            throw sse;
        }

        String logmsg = CsvCreator.createCsvLine(log.toArray(new String[log
                .size()]));
        String logStr = transformToLogForm(rowData.logMessageIdentifier,
                moddateForHeader, logmsg);
        return logStr;
    }

    private String transformToLogForm(LogMessageIdentifier logId,
            String moddate, String msg) {
        String log = String.format("%s FSP_INTS-BSS: INFO: %s:,%s", moddate,
                logId.getMsgId(), msg);
        return log;
    }

    private String formatResultItem(Object item) {
        if (item instanceof Date) {
            String dateStr = UserOperationLogQuery.createDateFormat().format(item);
            return dateStr;
        }
        return String.valueOf(item);
    }
}
