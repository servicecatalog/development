/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: Mar 31, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.oscm.app.v2_0.intf.Template;

/**
 * Represents a template file that was uploaded by a controller.
 * 
 * @author miethaner
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fileName",
        "controllerId" }))
@NamedQueries({
        @NamedQuery(name = "TemplateFile.getForControllerId", query = "SELECT tf FROM TemplateFile tf WHERE tf.controllerId = :controllerId"),
        @NamedQuery(name = "TemplateFile.getForFileAndControllerId", query = "SELECT tf FROM TemplateFile tf WHERE tf.fileName = :fileName AND tf.controllerId = :controllerId") })
public class TemplateFile implements Template {

    /**
     * The technical key of the entity.
     */
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "do_seq")
    @SequenceGenerator(name = "do_seq", allocationSize = 1000)
    private long tkey;

    /**
     * The File name of the template.
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * The plain text content of the template.
     */
    @Column(nullable = false)
    private String content;

    /**
     * The timestamp of the last change on the template.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastChange;

    /**
     * The Controller ID of the owning controller.
     */
    @Column(nullable = false)
    private String controllerId;

    /**
     * @param tkey
     *            the technical key of the template
     * @param fileName
     *            the file name
     * @param content
     *            the file content
     * @param controllerId
     *            the owning controller
     */
    public TemplateFile(long tkey, String fileName, String content,
            String controllerId) {
        this.tkey = tkey;
        this.fileName = fileName;
        this.content = content;
        this.lastChange = new Date();
        this.controllerId = controllerId;
    }

    /**
     * @return the tkey
     */
    public long getTkey() {
        return tkey;
    }

    /**
     * @param tkey
     *            the tkey to set
     */
    public void setTkey(long tkey) {
        this.tkey = tkey;
    }

    /**
     * @return the fileName
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the content
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the lastChange
     */
    @Override
    public Date getLastChange() {
        return lastChange;
    }

    /**
     * @param lastChange
     *            the lastChange to set
     */
    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    /**
     * @return the controllerId
     */
    public String getControllerId() {
        return controllerId;
    }

    /**
     * @param controllerId
     *            the controllerId to set
     */
    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }
}
