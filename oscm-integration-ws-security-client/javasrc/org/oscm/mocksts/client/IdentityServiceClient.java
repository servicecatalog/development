/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.mocksts.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.oscm.intf.IdentityService;
import org.oscm.vo.VOUser;
import com.sun.xml.wss.XWSSConstants;

/**
 * Servlet implementation class IdentityServiceClient
 */
@WebServlet("/IdentityServiceClient")
public class IdentityServiceClient extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final String BES_HTTPS_URL = "bes.https.url";
    private final String SSO_USER_KEY = "sso.user.key";
    private final String SSO_USER_PASSWORD = "sso.user.password";
    private final String PROPERTIES_FILE_DIR = "../applications/oscm-integration-ws-security-client/WEB-INF/classes/test.properties";

    private final String wsdlUrlSuffix = "/IdentityService/STS?wsdl";
    private final String targetNameSpace = "http://oscm.org/xsd";
    private final String serviceName = "IdentityService";
    private Properties props;

    private Properties loadProperties() {
        props = new Properties();
        File f = new File(PROPERTIES_FILE_DIR);
        if (f.exists()) {
            FileInputStream is = null;
            try {
                is = new FileInputStream(PROPERTIES_FILE_DIR);
                props.load(is);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Config path:" + PROPERTIES_FILE_DIR, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println(" ***** WARNING: File " + f.getAbsolutePath()
                    + " not found!");
        }
        return props;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("</head>");
        out.println("<body>");
        out.print("<h2 style=\"margin: 0px 0px 30px 0px\">User ID is: "
                + callSecuredWebservice() + "</h2>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    /**
     * invoke the IdentityService web service method
     * 
     * @param clientName
     *            the client name
     * @return execute result
     */
    private String callSecuredWebservice() {
        loadProperties();
        try {
            String wsdlUrl = props.getProperty(BES_HTTPS_URL) + wsdlUrlSuffix;
            String userKey = props.getProperty(SSO_USER_KEY);
            String password = props.getProperty(SSO_USER_PASSWORD);
            URL url = new URL(wsdlUrl);
            QName qName = new QName(targetNameSpace, serviceName);
            Service service = Service.create(url, qName);
            IdentityService identityService = service
                    .getPort(IdentityService.class);

            BindingProvider bindingProvider = (BindingProvider) identityService;
            Map<String, Object> clientRequestContext = bindingProvider
                    .getRequestContext();
            clientRequestContext.put(XWSSConstants.USERNAME_PROPERTY, userKey);
            clientRequestContext.put(XWSSConstants.PASSWORD_PROPERTY, password);
            VOUser user = new VOUser();
            user.setUserId("administrator");
            return identityService.getUser(user).getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

}
