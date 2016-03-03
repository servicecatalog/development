/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Oct 28, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack.proxy;

/**
 * @author farmaki
 * 
 */
public class ProxySettings {

    public static final String HTTPS_PROXY_HOST = "https.proxyHost";
    public static final String HTTPS_PROXY_PORT = "https.proxyPort";
    public static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts";

    public static final String HTTPS_PROXY_USER = "https.proxyUser";
    public static final String HTTPS_PROXY_PASSWORD = "https.proxyPassword";

    public static boolean useProxyByPass(String url) {

        String nonProxy = System.getProperty(HTTP_NON_PROXY_HOSTS);
        if (nonProxy != null) {
            String[] split = nonProxy.split("\\|");
            for (int i = 0; i < split.length; i++) {
                String np = split[i].trim();
                if (np.length() > 0) {
                    boolean wcStart = np.startsWith("*");
                    boolean wcEnd = np.endsWith("*");
                    if (wcStart) {
                        np = np.substring(1);
                    }
                    if (wcEnd) {
                        np = np.substring(0, np.length() - 1);
                    }
                    if (wcStart && wcEnd && url.contains(np)) {
                        return true;
                    }
                    if (wcStart && url.endsWith(np)) {
                        return true;
                    }
                    if (wcEnd && url.startsWith(np)) {
                        return true;
                    }
                    if (np.equals(url)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
