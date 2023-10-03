package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;

public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
    @Test(priority = 1, groups = "Sanity_test", description = "Verify registration happens correctly")
    @Parameters({"TC1_Username", "TC1_Password"})
    public void TestCase01(@Optional("DefaultUsername") String username,
            @Optional("DefaultPassword") String password) throws InterruptedException {
        Boolean status;
        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        //assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        logStatus("End TestCase", "Test Case 1: Verify user Registration : ",
                status ? "PASS" : "FAIL");
        //takeScreenshot(driver, "EndTestCase", "TestCase1");
    }

    @Test(priority = 2, groups = "Sanity_test", description = "Verify re-registering an already registered user fails")
    @Parameters({"TC2_Username", "TC2_Password"})
    public void TestCase02(String username, String password) throws InterruptedException {
        Boolean status;
        logStatus("Start Testcase",
                "Test Case 2: Verify User Registration with an existing username ", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
        // if (!status) {
        // logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "PASS" :
        // "FAIL");
        // return false;

        // }

        // Assert that user registration is successful
        Assert.assertTrue("User registration should be successful.", status);

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        logStatus("End TestCase", "Test Case 2: Verify user Registration : ",
                status ? "FAIL" : "PASS");
        // Assert that user registration with an existing username should fail
        Assert.assertFalse("User registration with an existing username should fail.", status);
        return;
    }

    @Test(priority = 3, groups = "Sanity_test",description = "Verify the functionality of search text box")
    @Parameters({"TC3_ProductNameToSearchFor"})
    public void TestCase03(String ProductNameToSearchFor) throws InterruptedException {
        logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");
        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct("YONEX");
        Assert.assertTrue("Failed to search for the given product: YONEX", status);


        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        // Verify the search results are available
        Assert.assertTrue("No search results were found for the given search string: YONEX",
                searchResults.size() > 0);

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            Assert.assertTrue("Test Results contain unexpected value: " + elementText,
                    elementText.toUpperCase().contains("YONEX"));
        }

        logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        Assert.assertFalse("Invalid keyword returned results", status);


        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        if (searchResults.size() == 0) {
            if (homePage.isNoResultFound()) {
                Assert.assertTrue("No products found message is not displayed",
                        homePage.isNoResultFound());
                logStatus("Step Success",
                        "Successfully validated that no products found message is displayed",
                        "PASS");
                logStatus("TestCase 3",
                        "Test Case PASS. Verified that no search results were found for the given text",
                        "PASS");
            } else {
                Assert.fail("Expected: no results, Actual: Results were available");
                logStatus("TestCase 3",
                        "Test Case Fail. Expected: no results, Actual: Results were available",
                        "FAIL");
            }
        }

        return;
    }

   @Test(priority = 4, groups = "Regression_Test", description = "Verify the existence of size chart for certain items and validate contents of size chart")
    @Parameters({"TC4_ProductNameToSearchFor"})
    public void TestCase04(String ProductNameToSearchFor) throws InterruptedException {
        logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);
            // Verify if the size chart exists for the search result
            Assert.assertTrue("Size Chart Link does not exist", result.verifySizeChartExists());

            logStatus("Step Success", "Successfully validated presence of Size Chart Link", "PASS");

            // Verify if size dropdown exists
            status = result.verifyExistenceofSizeDropdown(driver);
            logStatus("Step Success", "Validated presence of drop down", status ? "PASS" : "FAIL");
            Assert.assertTrue("Size dropdown does not exist", status);

            // Open the size chart
            Assert.assertTrue("Failed to open Size Chart", result.openSizechart());
            // Verify if the size chart contents matches the expected values
            status = result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
                    driver);
            if (status) {
                logStatus("Step Success", "Successfully validated contents of Size Chart Link",
                        "PASS");
            } else {
                logStatus("Step Failure", "Failure while validating contents of Size Chart Link",
                        "FAIL");
            }
            Assert.assertTrue("Size Chart contents do not match the expected values", status);

            // Close the size chart modal
            status = result.closeSizeChart(driver);
            Assert.assertTrue("Failed to close Size Chart modal", status);
        }
        logStatus("TestCase 4", "End Test Case: Validated Size Chart Details",
                status ? "PASS" : "FAIL");
        Assert.assertTrue("Test Case failed", status);
        return;
    }

    /*
     * Verify the complete flow of checking out and placing order for products is working correctly
     */

    @Test(priority = 5, groups = "Sanity_test", description = "Verify that a new user can add multiple products in to the cart and Checkout")
    @Parameters({"TC5_ProductNameToSearchFor", "TC5_ProductNameToSearchFor2", "TC5_AddressDetails"})
    public void TestCase05(String ProductNameToSearchFor, String ProductNameToSearchFor2,
            String AddressDetails) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue("User registration failed", status);

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        //Assert.assertTrue("User login failed", status);

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("YONEX");
        Assert.assertTrue("Product search failed", status);
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        status = homePage.searchForProduct("Tan");
        Assert.assertTrue("Product search failed", status);
        homePage.addProductToCart("Tan Leatherette Weekender Duffle");

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thanks page
        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue("Order placement and redirection to Thanks page failed", status);

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ",
                status ? "PASS" : "FAIL");
        Assert.assertTrue("Test Case failed", status);
        return;
    }


    /*
     * Verify the quantity of items in cart can be updated
     */


    @Test(priority = 6, groups = "Regression_Test", description = "Verify that the contents of the cart can be edited")
    @Parameters({"TC6_ProductNameToSearch1", "TC6_ProductNameToSearch2"})
    public void TestCase06(String ProductNameToSearch1, String ProductNameToSearch2)
            throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue("User registration failed", status);

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        //Assert.assertTrue("User login failed", status);

        homePage.navigateToHome();
        status = homePage.searchForProduct("Xtend");
        Assert.assertTrue("Product search failed", status);
        homePage.addProductToCart("Xtend Smart Watch");

        status = homePage.searchForProduct("Yarine");
        Assert.assertTrue("Product search failed", status);
        homePage.addProductToCart("Yarine Floor Lamp");
        Thread.sleep(2000);

        // Update watch quantity to 2
        status = homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);
        System.out.println("updated watch");

        Assert.assertTrue("Failed to update watch quantity", status);
        Thread.sleep(2000);

        // Update table lamp quantity to 0
        status = homePage.changeProductQuantityinCart("Yarine Floor Lamp", 0);
        // Assert.assertTrue( "Failed to update table lamp quantity", status);

        // Update watch quantity again to 1
        status = homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);
        Assert.assertTrue("Failed to update watch quantity again", status);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(
                    ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order: " + e.getMessage());
            Assert.fail("Timeout while placing order");
        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue("Order placement and redirection to Thanks page failed", status);

        homePage.navigateToHome();
        homePage.PerformLogout();

        logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ",
                status ? "PASS" : "FAIL");
        Assert.assertTrue("Test Case failed", status);
        return;
    }


    @Test(priority = 7, groups = "Sanity_test",description = "Verify that insufficient balance error is thrown when the wallet balance is not enough")
    @Parameters({"TC7_ProductName", "TC7_Qty"})
    public void TestCase07(String ProductName, int Qty) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase",
                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
                "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue("User registration failed", status); // Assert registration success

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        //Assert.assertTrue("User login failed", status); // Assert login success

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        Assert.assertTrue("Product search failed", status); // Assert product search success

        homePage.addProductToCart("Stylecon 9 Seater RHS Sofa Set ");
        homePage.changeProductQuantityinCart("Stylecon 9 Seater RHS Sofa Set ", 10);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        //Assert.assertTrue("Insufficient balance message not displayed", status);

        logStatus("End TestCase",
                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
                status ? "PASS" : "FAIL");

        return;
    }

    @Test(priority = 8, groups = "Regression_Test",
            description = "Verify that a product added to a cart is available when a new tab is added")

    public void TestCase08() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase",
                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase08");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue("User registration failed", status); // Assert registration success

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        //Assert.assertTrue("User login failed", status); // Assert login success

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        Assert.assertTrue("Product search failed", status); // Assert product search success

        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
       // Assert.assertTrue("Cart contents verification failed", status); // Assert cart contents

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase",
                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                status ? "PASS" : "FAIL");
       // takeScreenshot(driver, "EndTestCase", "TestCase08");

        return;
    }


    @Test(priority = 9, groups = "Regression_Test",description = "Verify that privacy policy and about us links are working fine")
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase",
                "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
                "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue("User registration failed", status); // Assert registration success

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        //Assert.assertTrue("User login failed", status); // Assert login success

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        Assert.assertTrue("Privacy policy link did not open in the same tab", status); // Assert
                                                                                     

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        Assert.assertTrue("Privacy Policy page heading is not displayed correctly", status); // Assert
                                                                                             

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        Assert.assertTrue("Terms of Service page heading is not displayed correctly", status); // Assert
                                                                                        

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase",
                "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                "PASS");
       // takeScreenshot(driver, "EndTestCase", "TestCase09");

        return;
    }

    @Test(priority = 10, groups = "Sanity_test",description = "Verify that the contact us dialog works fine")
    @Parameters({"TC10_ContactusUserName", "TC10_ContactUsEmail", "TC10_QueryContent"})

    public void TestCase10(String ContactusUserName, String ContactUsEmail, String QueryContent)
            throws InterruptedException {
        logStatus("Start TestCase",
                "Test Case 10: Verify that contact us option is working correctly ", "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase10");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//p[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys("crio user");
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys("criouser@gmail.com");
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys("Testing the contact us page");
        // System.out.println("Keys Inserted successfully");

        WebElement contactUs =
                driver.findElement(By.xpath("//button[contains(@class,'btn btn-primary')]"));
        Thread.sleep(2000);
        contactUs.click();
        // System.out.println("clicked successfully");
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        // Assert that the contactUs element is no longer visible, indicating that the contact form
        // has been submitted
        // Assert.assertFalse( "Contact form submission failed", contactUs.isDisplayed());

        logStatus("End TestCase",
                "Test Case 10: Verify that contact us option is working correctly ", "PASS");

        //takeScreenshot(driver, "EndTestCase", "TestCase10");

        return;
    }

    @Test(priority = 11, groups = "Regression_Test",description = "Ensure that the Advertisement Links on the QKART page are clickable")
    @Parameters({"TC11_ProductNameToSearchFor", "TC11_AddressDetails"})
    public void TestCase11(String ProductNameToSearchFor, String AddressDetails)
            throws InterruptedException {
        Boolean status = false;
        logStatus("Start TestCase",
                "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase11");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) {
            logStatus("TestCase 11",
                    "Test Case Failure. Ensure that the links on the QKART advertisement are clickable",
                    "FAIL");
           // takeScreenshot(driver, "Failure", "TestCase11");
        }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        if (!status) {
            logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Failure", "TestCase 11");
            logStatus("End TestCase",
                    "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
                    status ? "PASS" : "FAIL");
        }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        // Assert that there are 3 Advertisements available
        Assert.assertEquals("Number of Advertisements is not as expected", Advertisements.size(),
                3);

        WebElement Advertisement1 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        // Assert that Advertisement 1 is clickable by checking if the URL changed
        Assert.assertNotEquals(driver.getCurrentUrl(), currentURL,
                "Advertisement 1 is not clickable");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        // Assert that Advertisement 2 is clickable by checking if the URL changed
        Assert.assertNotEquals(driver.getCurrentUrl(), currentURL,
                "Advertisement 2 is not clickable");

        logStatus("End TestCase",
                "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
                "PASS");
        return;
    }


    @AfterSuite(alwaysRun = true)
    public static WebDriver quitDriver() {
        System.out.println("quit()");
        driver.quit();
        return driver;
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType,
                    description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


