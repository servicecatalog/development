/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: Feb 7, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.webtest;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SubjectTerm;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscm.webtest.PortalHtmlElements;
/**
 * Helper class for integration web tests using selenium and java mail.
 * 
 * @author miethaner
 */
public class WebTester {
    
    private static final Logger logger = Logger.getLogger(WebTester.class);

    public static final int IMPLICIT_WAIT = 10;

    // property keys
    private static final String BES_SECURE = "bes.secure";
    private static final String BES_HTTP_URL = "bes.http.url";
    private static final String BES_HTTPS_URL = "bes.https.url";
    public static final String BES_ADMIN_USER_ID = "bes.user.id";
    public static final String BES_ADMIN_USER_PWD = "bes.user.password";
    
    private static final String APP_SECURE = "app.secure";
    private static final String APP_HTTP_URL = "app.http.url";
    private static final String APP_HTTPS_URL = "app.https.url";
    public static final String APP_ADMIN_USER_ID = "app.user.id";
    public static final String APP_ADMIN_USER_PWD = "app.user.password";
    
    private static final String EMAIL_ADDRESS = "email.address";
    private static final String EMAIL_HOST = "email.host";
    private static final String EMAIL_USER = "email.user";
    private static final String EMAIL_PASSWORD = "email.password";
    private static final String EMAIL_PROTOCOL = "email.protocol";

    // path schemas
    private static final String PROPERTY_PATH = "../oscm-devruntime/javares/local/%s/webtest.properties";
    private static final String BASE_PATH_PORTAL = "%s/oscm-portal/%s";
    private static final String BASE_PATH_MARKETPLACE = "%s/oscm-portal/marketplace/%s";

    // email parameters
    private static final String MAIL_INBOX = "INBOX";

    // web element keys
    private static final String ATTRIUBTE_VALUE = "value";

    
    
    private String basePortalUrl="";

    
    private HtmlUnitDriver driver;    
    private String address;
    private Session mailSession;
    private Properties prop;
    private Properties emailProp;
    
    public WebTester() throws Exception {

        loadPropertiesFile();

        basePortalUrl = loadUrl(BES_SECURE, BES_HTTPS_URL, BES_HTTP_URL);

        driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT,
                TimeUnit.SECONDS);
        
        initMailSession();
        visitPortal("");
    }
    
    /**
     *  Load properties from personal devruntime folder
     * 
     */
    private void loadPropertiesFile() throws Exception {

        Map<String, String> env = System.getenv();
        String localhost = env.get("HOSTNAME");
        if (StringUtils.isEmpty(localhost)) {
            localhost = InetAddress.getLocalHost().getHostName();
        }
        String filePath = String.format(PROPERTY_PATH, localhost);
        
        prop = new Properties();
        FileInputStream fis = new FileInputStream(filePath);
        prop.load(fis);
        fis.close();

    }

    /**
     * load Url
     * @param prefix
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    private String loadUrl(String secureUrl, String httpsUrl, String httpUrl) throws NoSuchFieldException, SecurityException {
     
        boolean secure = Boolean.parseBoolean(prop.getProperty(secureUrl));
    
        if (secure) {
            return prop.getProperty(httpUrl);
        } else {
            return prop.getProperty(httpsUrl);
        }  
    }
 
    public String getPropertie(String propertie) {
        return prop.getProperty(propertie);
    }

   
    /**
     * initialize java mail session
     */
    private void initMailSession() {

        address = prop.getProperty(EMAIL_ADDRESS);
        String host = prop.getProperty(EMAIL_HOST);
        final String user = prop.getProperty(EMAIL_USER);
        final String password = prop.getProperty(EMAIL_PASSWORD);
        String protocol = prop.getProperty(EMAIL_PROTOCOL);
        
        emailProp = new Properties();
        emailProp.setProperty("mail.store.protocol", protocol);
        emailProp.setProperty("mail.host", host);
        emailProp.setProperty("mail.user", user);
        emailProp.setProperty("mail.from", address);
        emailProp.setProperty("mail.debug", "true");
    
        mailSession = Session.getInstance(emailProp, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
    }
    /**
     * Closes all open resources of the helper
     */
    public void close() {
        driver.close();
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
    public void loginPortal(String user, String password) throws LoginException, InterruptedException {
        

        WebElement userInput = driver.findElement(By.id(PortalHtmlElements.PORTAL_INPUT_USERID));
        userInput.sendKeys(user);

        WebElement pwdInput = driver
                .findElement(By.name(PortalHtmlElements.PORTAL_INPUT_PASSWORD));
        pwdInput.sendKeys(password);

        driver.findElement(By.id(PortalHtmlElements.PORTAL_BUTTON_LOGIN)).click();

        wait(IMPLICIT_WAIT);
        
        if(!verifyFoundElement(PortalHtmlElements.PORTAL_DIV_LOGIN_FAILED)) 
        {
            logger.info("Login to OSCM Portal successfully with userid:" + user);
        }else {
            String info = "Login to OSCM Portal failed with userid:" + user;
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
    public void visitPortal(String segments) throws Exception {
        String target = String.format(BASE_PATH_PORTAL, basePortalUrl, segments);
        driver.navigate().to(target);

        String actualTitle = driver.getTitle();
        if (actualTitle==null || !actualTitle.contentEquals(PortalHtmlElements.PORTAL_TITLE))
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
     */
    public void logoutPortal() {
        driver.findElement(By.id(PortalHtmlElements.PORTAL_LINK_LOGOUT)).click();

        logger.info("Login out from OSCM Portal successfully");
    }

    /**
     * Attempts a login to the OSCM marketplace with the given credentials. Note
     * that this method assumes the webdriver to be at the login page.
     * 
     * @param user
     *            the user name
     * @param password
     *            the password
     */
    public void loginMarketplace(String user, String password) {
        WebElement userInput = driver
                .findElement(By.id(PortalHtmlElements.MARKETPLACE_INPUT_USERID));
        userInput.sendKeys(user);

        WebElement pwdInput = driver
                .findElement(By.id(PortalHtmlElements.MARKETPLACE_INPUT_PASSWORD));
        pwdInput.sendKeys(password);

        driver.findElement(By.id(PortalHtmlElements.MARKETPLACE_BUTTON_LOGIN)).click();

        System.out.println("Login OSCM Marketplace");
    }

    /**
     * Navigates the webdriver to the given page of the OSCM marketplace.
     * 
     * @param page
     *            the page of the portal
     */
    public void visitMarketplace(String context) {
        String target = String.format(BASE_PATH_MARKETPLACE, basePortalUrl, context);

        driver.navigate().to(target);

        System.out.println("Goto " + target);
    }

    /**
     * Logs out the current user from the OSCM marketplace. Note that this
     * method assumes that there is a logged in user and that the driverApp is at a
     * marketplace page.
     */
    public void logoutMarketplace() {
        driver.findElement(By.id(PortalHtmlElements.MARKETPLACE_LINK_LOGOUT)).click();

        System.out.println("Logout OSCM Marketplace");
    }

    /**
     * found the text between two given text in String
     * @param msg
     * @param before
     * @param after
     * @return
     */
    public String foundTextBetween(String msg, String before, String after) {

        msg = msg.substring(msg.indexOf(before) + before.length(), msg.indexOf(after));
 
        return msg;
       
    }
    
    /**
     * found the text between two given text in String
     * @param msg
     * @return
     */
    public String getCreatedId(String msg) {

        return foundTextBetween(msg, "ID ", " has");
       
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
    
    
    /**
     * Verifies if the content of the element with the given id is equal to the
     * given value.
     * 
     * @param id
     *            the element id
     * @param value
     *            the value to compare with
     * @return true if equal
     * @throws NoSuchElementException
     *             if element is not present
     */
    public boolean verifyEqualElement(String id, String value) {
        WebElement element = driver.findElement(By.id(id));
        if(element == null) return false;
        
        String attribute = element.getAttribute(ATTRIUBTE_VALUE);

        if (attribute != null && attribute.equals(value)) {
            System.out.println("Element with id " + id + " and value " + value
                    + " is valid");
            return true;
        } else {
            System.out.println("Element with id " + id + " is invalid (" + value
                    + " != " + attribute + ")");
            return false;
        }
    }

    /**
     * Clicks the element with the given id.
     * 
     * @param id
     *            the element id
     * @throws NoSuchElementException
     *             if element is not present
     */
    public void clickElement(String id) {
        driver.findElement(By.id(id)).click();

        System.out.println("Clicked the element with id " + id);
    }

    /**
     * Reads the value of the element with the given id. This is used for fields
     * that use the value attribute, e.g. input fields.
     *
     * @return the value of the element
     * @throws NoSuchElementException
     *             if element is not present
     */
    public String readValue(String id) {
        WebElement element = driver.findElement(By.id(id));
        return element.getAttribute(ATTRIUBTE_VALUE);
    }

    /**
     * Reads the text of the element with the given id. This is used for text
     * within an element, e.g. &lt;p id="id"&gt;text&lt;/p&gt;
     *
     * @return the text of the element
     * @throws NoSuchElementException
     *             if element is not present
     */
    public String readText(String id) {
        WebElement element = driver.findElement(By.id(id));
        return element.getText();
    }

    /**
     * Takes the given value as input for the element with the given id.
     * 
     * @param id
     *            the element id
     * @param value
     *            the input value
     * @throws NoSuchElementException
     *             if element is not present
     */
    public void writeValue(String id, String value) {
        WebElement element = driver.findElement(By.id(id));
        element.sendKeys(value);
    }

    /**
     * Selects in the dropdown (select) element with the given id the option
     * with the given value.
     * 
     * @param id
     *            the element id
     * @param value
     *            the option value
     * @throws NoSuchElementException
     *             if element is not present
     */
    public void selectDropdown(String id, String value) {
        Select select = new Select(driver.findElement(By.id(id)));
        select.selectByValue(value);
    }

    /**
     * Submits the form with the given id.
     * 
     * @param id
     *            the element id
     * @throws NoSuchElementException
     *             if element is not present
     */
    public void submitForm(String id) {
        driver.findElement(By.id(id)).submit();

        System.out.println("Submitted form with id " + id);
    }

    /**
     * Waits for the element with the given id to be present or until the given
     * amount of seconds has passed.
     * 
     * @param id
     *            the element id
     * @param seconds
     *            the seconds until timeout
     * @throws TimeoutException
     *             if the timeout is reached
     */
    public void waitForElement(String id, int seconds) {
        (new WebDriverWait(driver, seconds))
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    /**
     * Waits for the element with the given id to be present or until the given
     * amount of seconds has passed.
     * 
     * @param id
     *            the element id
     * @param seconds
     *            the seconds until timeout
     * @throws TimeoutException
     *             if the timeout is reached
     */
    public void wait(int seconds) {
        (new WebDriverWait(driver, seconds)).withTimeout(seconds, TimeUnit.SECONDS);
    }
    
    /**
     * Reads the latest email with the given subject from the email inbox.
     * 
     * @param subject
     *            the email subject
     * @return the email body or null if no email was found
     * @throws Exception
     */
    public String readLatestEmailWithSubject(String subject) throws Exception {
        Store store = mailSession.getStore();
        store.connect();

        Folder folder = store.getFolder(MAIL_INBOX);
        folder.open(Folder.READ_WRITE);

        Message[] messages = null;

        messages = folder.search(new SubjectTerm(subject));

        String body = null;
        if (messages.length > 0) {
            Message latest = messages[0];

            for (Message m : messages) {
                if (latest.getSentDate().compareTo(m.getSentDate()) < 0) {
                    latest = m;
                }
            }
            body = (String) latest.getContent();
        }

        folder.close(false);
        store.close();

        return body;
    }

    /**
     * Returns the current URL that the webdriver is visiting.
     * 
     * @return the current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Returns the configured email address.
     * 
     * @return the email address
     */
    public String getEmailAddress() {
        return address;
    }
    
    public void log (String msg) {
        logger.info(msg);
    }
}
