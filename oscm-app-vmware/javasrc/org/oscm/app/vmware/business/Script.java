/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2016-05-24                                                       
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.business;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.io.IOUtils;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.vmware.persistence.DataAccessService;
import org.oscm.app.vmware.remote.bes.ServiceParamRetrieval;
import org.oscm.app.vmware.remote.vmware.ManagedObjectAccessor;
import org.oscm.app.vmware.remote.vmware.ServiceConnection;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.FileTransferInformation;
import com.vmware.vim25.GuestPosixFileAttributes;
import com.vmware.vim25.GuestProcessInfo;
import com.vmware.vim25.GuestProgramSpec;
import com.vmware.vim25.GuestWindowsFileAttributes;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NamePasswordAuthentication;
import com.vmware.vim25.VimPortType;

public class Script {

    private static final Logger logger = LoggerFactory.getLogger(Script.class);

    private static final String WINDOWS_GUEST_FILE_PATH = "C:\\Windows\\Temp\\runonce.bat";
    private static final String LINUX_GUEST_FILE_PATH = "/tmp/runonce.sh";

    private String guestUserId;
    private String guestPassword;
    private String script;
    private boolean isWindows;

    private class TrustAllTrustManager implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @SuppressWarnings("unused")
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @SuppressWarnings("unused")
        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

    public Script(String guestUserId, String guestPassword, String scriptURL,
            boolean isWindows) throws Exception {

        this.guestUserId = guestUserId;
        this.guestPassword = guestPassword;
        this.isWindows = isWindows;

        logger.debug("userid: " + guestUserId + " pwd: " + guestPassword
                + " script: " + scriptURL);

        // TODO load certificate from vSphere host and install somehow
        disableSSL();

        script = downloadFile(scriptURL);
    }

    private void disableSSL() throws Exception {
        // Declare a host name verifier that will automatically enable
        // the connection. The host name verifier is invoked during
        // the SSL handshake.
        javax.net.ssl.HostnameVerifier verifier = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };

        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager trustManager = new TrustAllTrustManager();
        trustAllCerts[0] = trustManager;

        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        javax.net.ssl.SSLSessionContext sslsc = sc.getServerSessionContext();
        sslsc.setSessionTimeout(0);
        sc.init(null, trustAllCerts, null);

        javax.net.ssl.HttpsURLConnection
                .setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(verifier);
    }

    private String downloadFile(String url) throws Exception {
        HttpURLConnection conn = null;
        int returnErrorCode = HttpsURLConnection.HTTP_OK;
        StringWriter writer = new StringWriter();
        try {
            URL urlSt = new URL(url);
            conn = (HttpURLConnection) urlSt.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestMethod("GET");
            try (InputStream in = conn.getInputStream();) {
                IOUtils.copy(in, writer, "UTF-8");
            }
            returnErrorCode = conn.getResponseCode();
        } catch (Exception e) {
            logger.error("Failed to download script file " + url, e);
            throw new Exception("Failed to download script file " + url);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (HttpsURLConnection.HTTP_OK != returnErrorCode) {
            throw new Exception("Failed to download script file " + url);
        }

        return writer.toString();
    }

    private void uploadScriptFileToVM(VimPortType vimPort,
            ManagedObjectReference vmwInstance,
            ManagedObjectReference fileManagerRef,
            NamePasswordAuthentication auth, String script, String hostname)
            throws Exception {

        String fileUploadUrl = null;
        if (isWindows) {
            GuestWindowsFileAttributes guestFileAttributes = new GuestWindowsFileAttributes();
            guestFileAttributes.setAccessTime(DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(new GregorianCalendar()));
            guestFileAttributes
                    .setModificationTime(DatatypeFactory.newInstance()
                            .newXMLGregorianCalendar(new GregorianCalendar()));
            fileUploadUrl = vimPort.initiateFileTransferToGuest(fileManagerRef,
                    vmwInstance, auth, WINDOWS_GUEST_FILE_PATH,
                    guestFileAttributes, script.length(), true);
        } else {
            GuestPosixFileAttributes guestFileAttributes = new GuestPosixFileAttributes();
            guestFileAttributes.setPermissions(Long.valueOf(500));
            guestFileAttributes.setAccessTime(DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(new GregorianCalendar()));
            guestFileAttributes
                    .setModificationTime(DatatypeFactory.newInstance()
                            .newXMLGregorianCalendar(new GregorianCalendar()));
            fileUploadUrl = vimPort.initiateFileTransferToGuest(fileManagerRef,
                    vmwInstance, auth, LINUX_GUEST_FILE_PATH,
                    guestFileAttributes, script.length(), true);
        }

        fileUploadUrl = fileUploadUrl.replaceAll("\\*", hostname);
        logger.debug("Uploading the file to :" + fileUploadUrl);

        HttpURLConnection conn = null;
        int returnErrorCode = HttpsURLConnection.HTTP_OK;

        try {
            URL urlSt = new URL(fileUploadUrl);
            conn = (HttpURLConnection) urlSt.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Length",
                    Long.toString(script.length()));
            try (OutputStream out = conn.getOutputStream();) {
                out.write(script.getBytes());
            }
            returnErrorCode = conn.getResponseCode();
        } catch (Exception e) {
            logger.error("Failed to upload file.", e);
            throw new Exception("Failed to upload file.");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (HttpsURLConnection.HTTP_OK != returnErrorCode) {
            throw new Exception("Failed to upload file. HTTP response code: "
                    + returnErrorCode);
        }
    }

    private String insertServiceParameter(VMPropertyHandler ph)
            throws Exception {

        logger.debug("Script before patching :" + script);

        ServiceParamRetrieval sp = new ServiceParamRetrieval(ph);

        final String LINE_ENDING = (isWindows ? "\r\n" : "\n");
        String firstLine = script.substring(0, script.indexOf(LINE_ENDING));
        String rest = script.substring(script.indexOf(LINE_ENDING) + 1,
                script.length());

        StringBuffer sb = new StringBuffer();
        if (isWindows) {
            addServiceParametersForWindowsVms(ph, sp, LINE_ENDING, sb);
        } else {
            addServiceParametersForLinuxVms(ph, sp, LINE_ENDING, sb);
        }

        String patchedScript;
        if (isWindows) {
            patchedScript = sb.toString() + LINE_ENDING + firstLine
                    + LINE_ENDING + rest;
        } else {
            patchedScript = firstLine + LINE_ENDING + sb.toString()
                    + LINE_ENDING + rest;
        }

        logger.debug("Patched script :" + patchedScript);
        return patchedScript;
    }

    private void addServiceParametersForLinuxVms(VMPropertyHandler ph,
            ServiceParamRetrieval sp, String lineEnding, StringBuffer sb)
            throws Exception, APPlatformException {

        String value = sp
                .getServiceSetting(VMPropertyHandler.TS_LINUX_ROOT_PWD);
        sb.append(VMPropertyHandler.TS_LINUX_ROOT_PWD + "='" + value + "'"
                + lineEnding);

        int numNics = Integer.parseInt(
                sp.getServiceSetting(VMPropertyHandler.TS_NUMBER_OF_NICS));
        while (numNics > 0) {
            String param = getIndexedParam(VMPropertyHandler.TS_NIC1_DNS_SERVER,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append(param + "='" + value + "'" + lineEnding);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_DNS_SUFFIX,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append(param + "='" + value + "'" + lineEnding);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_GATEWAY, numNics);
            value = sp.getServiceSetting(param);
            sb.append(param + "='" + value + "'" + lineEnding);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_IP_ADDRESS,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append(param + "='" + value + "'" + lineEnding);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append(param + "='" + value + "'" + lineEnding);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_SUBNET_MASK,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append(param + "='" + value + "'" + lineEnding);
            numNics--;
        }

        sb.append(VMPropertyHandler.TS_INSTANCENAME + "='"
                + ph.getInstanceName() + "'" + lineEnding);

        value = sp.getServiceSetting(VMPropertyHandler.TS_SCRIPT_URL);
        sb.append(VMPropertyHandler.TS_SCRIPT_URL + "='" + value + "'"
                + lineEnding);

        value = sp.getServiceSetting(VMPropertyHandler.TS_SCRIPT_USERID);
        sb.append(VMPropertyHandler.TS_SCRIPT_USERID + "='" + value + "'"
                + lineEnding);

        value = sp.getServiceSetting(VMPropertyHandler.TS_SCRIPT_PWD);
        sb.append(VMPropertyHandler.TS_SCRIPT_PWD + "='" + value + "'"
                + lineEnding);

        value = sp.getServiceSetting(VMPropertyHandler.TS_DOMAIN_NAME);
        sb.append(VMPropertyHandler.TS_DOMAIN_NAME + "='" + value + "'"
                + lineEnding);

        value = sp.getServiceSetting(VMPropertyHandler.REQUESTING_USER);
        sb.append(VMPropertyHandler.REQUESTING_USER + "='" + value + "'"
                + lineEnding);
    }

    private void addServiceParametersForWindowsVms(VMPropertyHandler ph,
            ServiceParamRetrieval sp, String DELIMITER, StringBuffer sb)
            throws Exception, APPlatformException {

        String value = sp
                .getServiceSetting(VMPropertyHandler.TS_WINDOWS_DOMAIN_ADMIN);
        sb.append("set " + VMPropertyHandler.TS_WINDOWS_DOMAIN_ADMIN + "="
                + value + DELIMITER);
        value = sp.getServiceSetting(
                VMPropertyHandler.TS_WINDOWS_DOMAIN_ADMIN_PWD);
        sb.append("set " + VMPropertyHandler.TS_WINDOWS_DOMAIN_ADMIN_PWD + "="
                + value + DELIMITER);
        value = sp.getServiceSetting(VMPropertyHandler.TS_WINDOWS_DOMAIN_JOIN);
        sb.append("set " + VMPropertyHandler.TS_WINDOWS_DOMAIN_JOIN + "="
                + value + DELIMITER);
        value = sp.getServiceSetting(VMPropertyHandler.TS_DOMAIN_NAME);
        sb.append("set " + VMPropertyHandler.TS_DOMAIN_NAME + "=" + value
                + DELIMITER);
        value = sp.getServiceSetting(
                VMPropertyHandler.TS_WINDOWS_LOCAL_ADMIN_PWD);
        sb.append("set " + VMPropertyHandler.TS_WINDOWS_LOCAL_ADMIN_PWD + "="
                + value + DELIMITER);
        value = sp.getServiceSetting(VMPropertyHandler.TS_WINDOWS_WORKGROUP);
        sb.append("set " + VMPropertyHandler.TS_WINDOWS_WORKGROUP + "=" + value
                + DELIMITER);

        int numNics = Integer.parseInt(
                sp.getServiceSetting(VMPropertyHandler.TS_NUMBER_OF_NICS));
        while (numNics > 0) {
            String param = getIndexedParam(VMPropertyHandler.TS_NIC1_DNS_SERVER,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append("set " + param + "=" + value + DELIMITER);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_DNS_SUFFIX,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append("set " + param + "=" + value + DELIMITER);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_GATEWAY, numNics);
            value = sp.getServiceSetting(param);
            sb.append("set " + param + "=" + value + DELIMITER);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_IP_ADDRESS,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append("set " + param + "=" + value + DELIMITER);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append("set " + param + "=" + value + DELIMITER);

            param = getIndexedParam(VMPropertyHandler.TS_NIC1_SUBNET_MASK,
                    numNics);
            value = sp.getServiceSetting(param);
            sb.append("set " + param + "=" + value + DELIMITER);

            numNics--;
        }

        sb.append("set " + VMPropertyHandler.TS_INSTANCENAME + "="
                + ph.getInstanceName() + DELIMITER);

        value = sp.getServiceSetting(VMPropertyHandler.TS_SCRIPT_URL);
        sb.append("set " + VMPropertyHandler.TS_SCRIPT_URL + "=" + value
                + DELIMITER);

        value = sp.getServiceSetting(VMPropertyHandler.TS_SCRIPT_USERID);
        sb.append("set " + VMPropertyHandler.TS_SCRIPT_USERID + "=" + value
                + DELIMITER);

        value = sp.getServiceSetting(VMPropertyHandler.TS_SCRIPT_PWD);
        sb.append("set " + VMPropertyHandler.TS_SCRIPT_PWD + "=" + value
                + DELIMITER);

        value = sp.getServiceSetting(VMPropertyHandler.REQUESTING_USER);
        sb.append("set " + VMPropertyHandler.REQUESTING_USER + "=" + value
                + DELIMITER);
    }

    private String getIndexedParam(String param, int index) {
        return param.replace('1', Integer.toString(index).charAt(0));
    }

    public void execute(VMwareClient vmw, ManagedObjectReference vmwInstance,
            VMPropertyHandler paramHandler) throws Exception {

        logger.debug("");

        String vcenter = paramHandler
                .getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VimPortType vimPort = vmw.getConnection().getService();
        ServiceConnection conn = new ServiceConnection(vimPort,
                vmw.getConnection().getServiceContent());
        ManagedObjectAccessor moa = new ManagedObjectAccessor(conn);
        ManagedObjectReference guestOpManger = vmw.getConnection()
                .getServiceContent().getGuestOperationsManager();
        ManagedObjectReference fileManagerRef = (ManagedObjectReference) moa
                .getDynamicProperty(guestOpManger, "fileManager");
        ManagedObjectReference processManagerRef = (ManagedObjectReference) moa
                .getDynamicProperty(guestOpManger, "processManager");

        NamePasswordAuthentication auth = new NamePasswordAuthentication();
        auth.setUsername(guestUserId);
        auth.setPassword(guestPassword);
        auth.setInteractiveSession(false);

        String scriptPatched = insertServiceParameter(paramHandler);

        DataAccessService das = new DataAccessService(paramHandler.getLocale());
        URL vSphereURL = new URL(das.getCredentials(vcenter).getURL());

        uploadScriptFileToVM(vimPort, vmwInstance, fileManagerRef, auth,
                scriptPatched, vSphereURL.getHost());
        logger.debug("Executing CreateTemporaryFile guest operation");
        String tempFilePath = vimPort.createTemporaryFileInGuest(fileManagerRef,
                vmwInstance, auth, "", "", "");
        logger.debug("Successfully created a temporary file at: " + tempFilePath
                + " inside the guest");

        GuestProgramSpec spec = new GuestProgramSpec();

        if (isWindows) {
            spec.setProgramPath(WINDOWS_GUEST_FILE_PATH);
            spec.setArguments(" > " + tempFilePath);
        } else {
            spec.setProgramPath(LINUX_GUEST_FILE_PATH);
            spec.setArguments(" > " + tempFilePath + " 2>&1");
        }

        logger.debug("Starting the specified program inside the guest");
        long pid = vimPort.startProgramInGuest(processManagerRef, vmwInstance,
                auth, spec);
        logger.debug("Process ID of the program started is: " + pid + "");

        List<GuestProcessInfo> procInfo = null;
        List<Long> pidsList = new ArrayList<Long>();
        pidsList.add(pid);
        do {
            logger.debug("Waiting for the process to finish running.");
            try {
                procInfo = vimPort.listProcessesInGuest(processManagerRef,
                        vmwInstance, auth, pidsList);
            } catch (Exception e) {
                logger.warn(
                        "listProcessesInGuest() failed. setting new Linux root password for authentication");

                if (isWindows) {
                    auth.setPassword(paramHandler.getServiceSetting(
                            VMPropertyHandler.TS_WINDOWS_LOCAL_ADMIN_PWD));
                } else {
                    auth.setPassword(paramHandler.getServiceSetting(
                            VMPropertyHandler.TS_LINUX_ROOT_PWD));
                }
            }
            Thread.sleep(5 * 1000);
        } while (procInfo != null && procInfo.get(0).getEndTime() == null);

        if (procInfo != null && procInfo.get(0).getExitCode() != 0) {
            logger.error(
                    "Script return code: " + procInfo.get(0).getExitCode());
            FileTransferInformation fileTransferInformation = null;
            fileTransferInformation = vimPort.initiateFileTransferFromGuest(
                    fileManagerRef, vmwInstance, auth, tempFilePath);
            String fileDownloadUrl = fileTransferInformation.getUrl()
                    .replaceAll("\\*", vSphereURL.getHost());
            logger.debug(
                    "Downloading the output file from :" + fileDownloadUrl);
            String scriptOutput = downloadFile(fileDownloadUrl);
            logger.error("Script execution output: " + scriptOutput);
        }

    }
}
