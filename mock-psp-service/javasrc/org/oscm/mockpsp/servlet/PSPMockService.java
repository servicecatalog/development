package org.oscm.mockpsp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.oscm.mockpsp.data.ParameterStorage;

/**
 * Mock service to pretend integration of psp.
 */
public class PSPMockService extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private void creditCardPage(HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();

        // print style def
        writer.print("<html>");
        writer.print("<head>");
        writer.print("<style>");
        writer.print(".form_layout {background-image:url(img/regframe_background.jpg);height: 220px;width: 400px;padding-top: 40px;padding-left: 20px;}");
        writer.print(".table_layout {padding-left: 10px;padding-top: 10px;padding-right: 20px;padding-bottom: 10px;margin-bottom: 10px;border: 1px solid rgb(0,0,0)");
        writer.print("</style>");
        writer.print("</head>");

        // print form
        writer.print("<body>");
        writer.print("<form method='post' class='form_layout'>");
        writer.print("<table class='table_layout'>");
        writer.printf(
                "<tr><td>%s</td><td><select name='%s'><option>VISA</option><option>MasterCard</option></select></td></tr>",
                "Card Brand:", "cardBrand");
        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "Card No.:", "cardNumber", "");
        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "Card Holder:", "cardHolder", "");
        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "Expiration Date", "expirationDate", "");
        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "Verification number", "verificationNumber", "");
        writer.print("</table>");
        writer.printf("<input type=\"%s\" name=\"%s\" value=\"%s\">", "submit",
                "result", "Cancel");
        writer.printf("<input type=\"%s\" name=\"%s\" value=\"%s\">", "submit",
                "result", "Register");
        writer.print("</form>");
        writer.print("</body>");
        writer.print("</html>");
    }

    private void directDebitPage(HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();

        // print style def
        writer.print("<html>");
        writer.print("<head>");
        writer.print("<style>");
        writer.print(".form_layout {background-image:url(img/regframe_background.jpg);height: 220px;width: 400px;padding-top: 40px;padding-left: 20px;}");
        writer.print(".table_layout {padding-left: 10px;padding-top: 10px;padding-right: 20px;padding-bottom: 10px;margin-bottom: 10px;border: 1px solid rgb(0,0,0)");
        writer.print("</style>");
        writer.print("</head>");

        // print form
        writer.print("<body>");
        writer.print("<form method='post' class='form_layout'>");
        writer.print("<table class='table_layout'>");

        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "Account Holder:", "accountHolder", "");
        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "IBAN.:", "ibanNumber", "");
        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "BIC", "bicNumber", "");
        writer.printf(
                "<tr><td>%s</td><td><input type='text' name='%s' value='%s'/></td></tr>",
                "Bank Name", "bankName", "");
        writer.print("</table>");
        writer.printf("<input type=\"%s\" name=\"%s\" value=\"%s\">", "submit",
                "result", "Cancel");
        writer.printf("<input type=\"%s\" name=\"%s\" value=\"%s\">", "submit",
                "result", "Register");
        writer.print("</form>");
        writer.print("</body>");
        writer.print("</html>");
    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Map<?, ?> sessionParams = ParameterStorage.getSessionParams(request
                .getParameter("sessionId"));
        String paymentCode = getParameterValue(sessionParams, "PAYMENT.CODE");
        if (paymentCode != null && paymentCode.equalsIgnoreCase("DD.RG")) {
            directDebitPage(response);
        } else {
            creditCardPage(response);
        }
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> sessionParams = (Map<String, String[]>) ParameterStorage
                .getSessionParamsOnce(request.getParameter("sessionId"));
        sessionParams.putAll(request.getParameterMap());

        String url = sendResponseToBesServer(sessionParams);
        response.sendRedirect(url);
    }

    /**
     * Gets the parameter value for the given key.
     * 
     * @param paramTable
     *            The table of parameters.
     * @param key
     *            The key to look for.
     * @return The parameter value.
     */
    private String getParameterValue(Map<?, ?> paramTable, String key) {
        if (paramTable == null) {
            return null;
        }
        Object paramValues = paramTable.get(key);
        if (paramValues != null) {
            return ((String[]) paramValues)[0];
        }
        return null;
    }

    /**
     * Sends the response call with the specified content to the BES servlet
     * handling the PSP registration.
     * 
     * @param sessionParams
     *            The parameters to retrieve the data from.
     * @return The URL to redirect the user to.
     * @throws IOException
     */
    private String sendResponseToBesServer(Map<String, ?> sessionParams)
            throws IOException {
        String redirectURL = getParameterValue(sessionParams,
                "FRONTEND.RESPONSE_URL");
        String choice = getParameterValue(sessionParams, "result");

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(redirectURL);

        Iterator<String> it = sessionParams.keySet().iterator();
        String key;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        while (it.hasNext()) {
            key = it.next();
            params.add(new BasicNameValuePair(key, getParameterValue(sessionParams, key)));
        }

        // set params
        boolean wasCancelled = "Cancel".equals(choice);
        if (wasCancelled) {
            params.add(new BasicNameValuePair("FRONTEND.REQUEST.CANCELLED", "true"));
        }

        params.add(new BasicNameValuePair("PROCESSING.RESULT", "ACK"));
        params.add(new BasicNameValuePair("PROCESSING.CODE", "000.000.000"));
        params.add(new BasicNameValuePair("PROCESSING.RETURN", "000.000.000"));
        params.add(new BasicNameValuePair("PROCESSING.RETURN.CODE", "000.000.000"));
        params.add(new BasicNameValuePair("PROCESSING.REASON", "90.00"));
        params.add(new BasicNameValuePair("PROCESSING.TIMESTAMP",String.valueOf(System.currentTimeMillis())));
        params.add(new BasicNameValuePair("IDENTIFICATION.UNIQUEID", "12345"));

        // some fake account data
        params.add(new BasicNameValuePair("ACCOUNT.NUMBER", "1100101"));
        params.add(new BasicNameValuePair("ACCOUNT.BRAND", "Mock Platin Card"));
        params.add(new BasicNameValuePair("ACCOUNT.EXPIRY_MONTH", "09"));
        params.add(new BasicNameValuePair("ACCOUNT.EXPIRY_YEAR", "19"));
        params.add(new BasicNameValuePair("ACCOUNT.BANK", "Mock Bank"));
        params.add(new BasicNameValuePair("ACCOUNT.BANKNAME", "08154711"));
        
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        
        // send the request
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        
        return EntityUtils.toString(entity);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        String trustStore = System.getProperty("PSP_MOCK_TRUSTSTORE_PATH");
        String trustStorePwd = System.getProperty("PSP_MOCK_TRUSTSTORE_PWD");

        if (trustStore != null) {
            System.setProperty("javax.net.ssl.trustStore", trustStore);
        }
        if (trustStorePwd != null) {
            System.setProperty("javax.net.ssl.trustStorePassword",
                    trustStorePwd);
        }
    }

}
