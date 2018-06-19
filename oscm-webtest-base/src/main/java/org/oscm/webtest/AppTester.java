/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: Feb 7, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.webtest;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.LoginException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
/**
 * Helper class for integration web tests using selenium and java mail.
 * 
 * @author miethaner
 */
public class AppTester extends WebTester {

    // property keys
    private static final String APP_SECURE = "app.secure";
    private static final String APP_HTTP_URL = "app.http.url";
    private static final String APP_HTTPS_URL = "app.https.url";
    public static final String APP_ADMIN_USER_ID = "app.user.id";
    public static final String APP_ADMIN_USER_PWD = "app.user.password";

    // path schemas
    private static final String APP_PATH_SEGMENT = "/oscm-app/default.jsf";


    List<WebElement> tableRows = new ArrayList<WebElement> ();

    private String base;
    
    public AppTester() throws Exception {
        super();

        String base = loadUrl(APP_SECURE, APP_HTTPS_URL, APP_HTTP_URL);
        if(base.contains("https"))
        {
            baseUrl = "https://" + base;
        }else {
            baseUrl = "http://" + base;
        }
    }
    


    /**
     * Attempts a login to the OSCM portal with the given credentials. Note that
     * this method assumes the webdriver to be at the login page.
     * 
     * @param user
     *            the user name
     * @param password
     *            the password
     * @throws InterruptedException 
     * @throws Exception 
     */
    public void loginApp(String user, String password) throws LoginException, InterruptedException {
        
        String url = "https://" + user + ":" + password + "@" + base +APP_PATH_SEGMENT;
        driver.get(url);
        driver.manage().window().maximize();
    
        wait(IMPLICIT_WAIT);
        
        if(!verifyFoundElement(AppHtmlElements.APP_FORM_CONFIGURATIONSETTING)) 
        {
            logger.info("Login to APP successfully with userid:" + user);
        }else {
            String info = "Login to APP failed with userid:" + user;
            logger.info(info);
            throw new LoginException(info);
        }
    }


    
    /**
     * Navigates the webdriver to the given page of the OSCM portal.
     * 
     * @param page
     *            the page of the portal
     * @throws Exception 
     */
    public void visitApp() throws Exception {
        String target = baseUrl + APP_PATH_SEGMENT;
        driver.navigate().to(target);

        String actualTitle = driver.getTitle();
        if (actualTitle==null || !actualTitle.contentEquals(AppHtmlElements.APP_TITLE))
        {
            logger.info("Navigate to " + target + " failed : HTTP Status 404 - Not Found");
            throw new Exception("Page not found!");
        }else {
            logger.info("Navigate to " + target + " successfully");
        }
    }
    
    /**
     * Logs out the current user from the OSCM portal. Note that this method
     * assumes that there is a logged in user and that the driverApp is at a portal
     * page.
     * @throws Exception 
     */
    public void logoutApp() throws Exception {
       
    }

    /**
     * Reads the error message from the page notification.
     * 
     * @return the error message
     * @throws NoSuchElementException
     *             if error message is not present
     */
    public String readErrorMessage() {
        WebElement element = driver.findElement(By.id(PortalHtmlElements.PORTAL_SPAN_ERRORS));
        return element.findElement(By.className(PortalHtmlElements.PORTAL_ERRORCLASS))
                .getText();
    }

    /**
     * Reads the info message from the page notification.
     * 
     * @return the info message
     * @throws NoSuchElementException
     *             if info message is not present
     */
    public String readInfoMessage() {
        WebElement element = driver.findElement(By.id(PortalHtmlElements.PORTAL_SPAN_INFOS));
        return element.findElement(By.className(PortalHtmlElements.PORTAL_INFOCLASS))
                .getText();
    }

    public boolean getPortalExecutionResult() {
        waitForElement(PortalHtmlElements.PORTAL_DIV_SHOWMESSAGE, 10);
        if(!verifyFoundElement(PortalHtmlElements.PORTAL_SPAN_ERRORS)
                && verifyFoundElement(PortalHtmlElements.PORTAL_SPAN_INFOS))
        {
            logger.info(readInfoMessage());
            return true;
        }else {
            logger.info(readErrorMessage());
            return false;
        }
    }
    /**
     * Verifies if found the required element
     * 
     * @param id
     *            the element id
     * @param value
     *            the value to compare with
     * @return true if equal
     * @throws NoSuchElementException
     *             if element is not present
     */
    public boolean verifyFoundElement(String id) {
        
        try {
        if(driver.findElement(By.id(id))!=null)
            return true;
        }catch(NoSuchElementException e) {
            return false;
        }
        return false;
    }

    
    public List<WebElement> getContentConfigurationSettings(String formId) {
    
    WebElement baseTableBody = driver.findElement(By.xpath("//div[@id='"+ formId+"']/table/tbody"));
    List<WebElement> tableRows = baseTableBody.findElements(By.tagName("tr"));
        
    return tableRows;
    
    }
    
    public String returnControllerId(List<WebElement> tableRows, int index) {

        WebElement td = tableRows.get(index).findElement(By.xpath("//td[0]"));
        return td.getText();
    }
    
    public String returnOrgId(List<WebElement> tableRows, int index) {
        
        WebElement td = tableRows.get(index).findElement(By.xpath("//td[1]"));
        WebElement input = td.findElement(By.xpath("//*[ends-with(@id,'"+ AppHtmlElements.APP_INPUT_END_EXISTORGID +"')]"));
        return input.getAttribute("value");
    }
    
    public void clickRemoveLink(List<WebElement> tableRows, int index) {
        
        WebElement td = tableRows.get(index).findElement(By.xpath("//td[2]"));
        WebElement href = td.findElement(By.tagName("a"));
        href.click();
    }
    


}
