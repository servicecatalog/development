/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 2016/11/09                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.common.ui;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

/**
 * @author tateiwamext
 *
 */
public class ServerInfoModel extends ExtendedDataModel<ServerInfo> {

    private ServerInfo[] currentPageData;

    private Integer rowKey;

    /*
     * (non-Javadoc)
     * 
     * @see org.ajax4jsf.model.ExtendedDataModel#getRowKey()
     */
    @Override
    public Object getRowKey() {
        return this.rowKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ajax4jsf.model.ExtendedDataModel#setRowKey(java.lang.Object)
     */
    @Override
    public void setRowKey(Object o) {
        this.rowKey = (Integer) o;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ajax4jsf.model.ExtendedDataModel#walk(javax.faces.context.
     * FacesContext, org.ajax4jsf.model.DataVisitor, org.ajax4jsf.model.Range,
     * java.lang.Object)
     */
    @Override
    public void walk(FacesContext fc, DataVisitor dv, Range range, Object arg) {
        // TODO Auto-generated method stub
        SequenceRange seq = (SequenceRange) range;
        this.currentPageData = getRange(seq.getFirstRow(), seq.getRows());

        for (int i = 0; i < this.currentPageData.length; i++) {
            dv.process(fc, Integer.valueOf(i), arg);
        }
    }

    /**
     * @param start
     * @param count
     * @return
     */
    private ServerInfo[] getRange(int start, int count) {
        List<ServerInfo> r = new ArrayList<>();
        int limit = start + count < getRowCount() ? start + count
                : getRowCount();
        for (int i = start; i < limit; i++) {
            r.add(new ServerInfo("instance-" + String.valueOf(i),
                    String.valueOf(i), "Small", "ACTIVE", "1.2.3.4",
                    "192.168.1.1\n192.168.1.2"));
        }
        return r.toArray(new ServerInfo[r.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.model.DataModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return getLength();
    }

    /**
     * @return
     */
    private int getLength() {
        return 6;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.model.DataModel#getRowData()
     */
    @Override
    public ServerInfo getRowData() {
        return this.currentPageData[this.rowKey.intValue()];
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.model.DataModel#getRowIndex()
     */
    @Override
    public int getRowIndex() {
        // TODO Auto-generated method stub
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.model.DataModel#getWrappedData()
     */
    @Override
    public Object getWrappedData() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.model.DataModel#isRowAvailable()
     */
    @Override
    public boolean isRowAvailable() {
        return this.rowKey != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.model.DataModel#setRowIndex(int)
     */
    @Override
    public void setRowIndex(int arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
     */
    @Override
    public void setWrappedData(Object arg0) {
        // TODO Auto-generated method stub

    }

}
