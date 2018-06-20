package org.oscm.webtest;

import javax.security.auth.login.LoginException;

import org.openqa.selenium.By;

/**
 * Helper class for integration web tests for oscm-app-sample
 */
public class AppSampleTester extends WebTester {
    private String base;
    
    public AppSampleTester() throws Exception {
        super();

        String baseUrl = loadUrl(APP_SECURE, APP_HTTPS_URL, APP_HTTP_URL);
        if(baseUrl.contains("https"))
        {
            base = baseUrl.replace("https://", "");
        }else {
            base = baseUrl.replace("http://", "");
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
    public void loginAppSamples(String user, String password, String controllerId) throws LoginException, InterruptedException {
        
        String url = "https://" + user + ":" + password + "@" + base +AppPathSegments.APP_SAMPLE_CONTROLLER;
        driver.get(url);
        driver.manage().window().maximize();
    
        wait(IMPLICIT_WAIT);
        
        if(verifyFoundElement(By.id(AppHtmlElements.APP_CONFIG_FORM1))) 
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
    public void visitAppConfig() throws Exception {
        String target = baseUrl + AppPathSegments.APP_CONFIGURATION;
        driver.navigate().to(target);

        String actualTitle = driver.getTitle();
        if (actualTitle==null || !actualTitle.contentEquals(AppHtmlElements.APP_CONFIG_TITLE))
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
    public void logoutAppConfig() throws Exception {
       
    }

}