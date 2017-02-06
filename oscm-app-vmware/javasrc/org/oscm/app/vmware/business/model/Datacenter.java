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

public class Datacenter implements Serializable {

    private static final long serialVersionUID = 6505684243592524786L;
    private List<Cluster> cluster;
    private String name;
    private String id;
    private int vcenter_tkey;
    private int tkey;

    public List<Cluster> getCluster() {
        return cluster;
    }

    public void setCluster(List<Cluster> cluster) {
        this.cluster = cluster;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVcenter_tkey() {
        return vcenter_tkey;
    }

    public void setVcenter_tkey(int vcenter_tkey) {
        this.vcenter_tkey = vcenter_tkey;
    }

    public int getTkey() {
        return tkey;
    }

    public void setTkey(int tkey) {
        this.tkey = tkey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
