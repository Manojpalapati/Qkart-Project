package QKART_SANITY_LOGIN.Module1;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addresString) {
        try {
            /*
             * Click on the "Add new address" button, enter the addressString in the address
             * text box and click on the "ADD" button to save the address
             */
            WebElement addNewAddress = driver.findElement(By.xpath("//*[@id='add-new-btn']"));
            addNewAddress.click();
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='root']/div/div[2]/div[1]/div/div[2]/div[1]/div/textarea[1]")));
            WebElement Addressbox = driver.findElement(By.xpath("//*[@id='root']/div/div[2]/div[1]/div/div[2]/div[1]/div/textarea[1]"));
            Addressbox.clear();
            Addressbox.sendKeys(addresString);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='root']/div/div[2]/div[1]/div/div[2]/div[2]/button[1]")));
            
            List <WebElement> buttons = driver.findElements(By.xpath("//*[@id='root']/div/div[2]/div[1]/div/div[2]/div[2]/button"));
            for (WebElement button : buttons){
                if(button.getText().equals("ADD")){
                    button.click();
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("css-yg30e6")));
                    return true;
                }

            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;

        }
    }

    /*
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            /*
             * Iterate through all the address boxes to find the address box with matching
             * text, addressToSelect and click on it
             */
            List<WebElement> addressBoxes = driver.findElements(By.xpath("//*[@id='root']/div/div[2]/div[1]/div/div[1]/div"));

        for (WebElement addressBox : addressBoxes) {
            String addressText = addressBox.getText();

            if (addressText.contains(addressToSelect)) {
                addressBox.click();
                WebDriverWait wait = new WebDriverWait(driver, 10); // Adjust the timeout as needed
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='PLACE ORDER']")));
                return true;
            }
        } 
            System.out.println("Unable to find the given address"+ addressToSelect);
            return false;
        } catch (Exception e) {
            System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    /*
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find the "PLACE ORDER" button and click on it

           WebElement placeOrder = driver.findElement(By.xpath("//button[text()='PLACE ORDER']"));
            placeOrder.click();
            WebDriverWait wait = new WebDriverWait(driver, 10); // Adjust the timeout as needed
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'SnackbarContent-root SnackbarItem-contentRoot')]")));
            return true;

        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 07: MILESTONE 6
            WebElement popupmessage = driver.findElement(By.xpath("//*[@id='notistack-snackbar']"));
            String messageText = popupmessage.getText();
            String expectedMessage = "You do not have enough balance in your wallet for this purchase";
        if (messageText.equals(expectedMessage)) {
            return true; // Message matches
        } else {
            System.out.println("Expected message: " + expectedMessage);
            System.out.println("Actual message: " + messageText);
            return false; 
        }
        } catch (Exception e) {
            System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}
