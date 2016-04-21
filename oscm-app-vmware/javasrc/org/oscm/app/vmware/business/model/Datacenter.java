package org.oscm.app.vmware.business.model;

import java.util.List;

public class Datacenter {
    public List<Cluster> cluster;
    public String name;
    public String id;
    public int vcenter_tkey;
    public int tkey;
}
