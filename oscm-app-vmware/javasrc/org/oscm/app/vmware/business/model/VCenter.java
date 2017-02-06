/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-05-24
 *
 *******************************************************************************/

package org.oscm.app.vmware.business.model;

import java.io.Serializable;
import java.util.List;

public class VCenter implements Serializable {

    private static final long serialVersionUID = 8786633560910413059L;

    private List<Datacenter> datacenter;
    private String name;
    private String identifier;
    private int tkey;
    private String url;
    private String userid;
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Datacenter> getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(List<Datacenter> datacenter) {
        this.datacenter = datacenter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getTkey() {
        return tkey;
    }

    public void setTkey(int tkey) {
        this.tkey = tkey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof VCenter)) {
            return false;
        }

        VCenter other = (VCenter) obj;
        return other.getTkey() == tkey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        return prime * result + tkey;
    }

}
