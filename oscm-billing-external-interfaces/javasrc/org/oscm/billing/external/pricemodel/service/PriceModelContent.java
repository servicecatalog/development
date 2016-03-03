/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                  
 *                                                                                                                                 
 *  Creation Date: 17.10.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.pricemodel.service;

import java.io.Serializable;

/**
 * Tag and content of an external price model for a specific locale. IANA media
 * types are used for content type. See
 * http://www.iana.org/assignments/media-types/media-types.xhtml
 * 
 * @TODO replace implementation with Interface & move implementation to
 *       oscm-file-billing-adapter and oscm-portal
 */
public class PriceModelContent implements Serializable {

    private static final long serialVersionUID = -8724898141388542458L;

    private String contentType;
    private byte[] content;
    private String tag;
    private String filename;

    public PriceModelContent() {
    }

    public PriceModelContent(String contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
        this.tag = null;
    }

    public PriceModelContent(String contentType, byte[] content, String tag) {
        this.contentType = contentType;
        this.content = content;
        this.tag = tag;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
