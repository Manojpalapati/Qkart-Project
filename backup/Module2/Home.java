package QKART_SANITY_LOGIN.Module1;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Home {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app";

    public Home(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToHome() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    public Boolean PerformLogout() throws InterruptedException {
        try {
            // Find and click on the Logout Button
            WebElement logout_button = driver.findElement(By.className("MuiButton-text"));
            logout_button.click();

            // Wait for Logout to Complete
            Thread.sleep(3000);

            return true;
        } catch (Exception e) {
            // Error while logout
            return false;
        }
    }

    /*
     * Returns Boolean if searching for the given product name occurs without any
     * errors
     */
    public Boolean searchForProduct(String product) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Clear the contents of the search box and Enter the product name in the search
            // box
            WebElement searchBox = driver.findElement(By.xpath("//*[@id='root']/div/div/div[1]/div[2]/div/input"));
            searchBox.clear();
            searchBox.sendKeys(product);
            Thread.sleep(3000);
            return true;
        } catch (Exception e) {
            System.out.println("Error while searching for a product: " + e.getMessage());
            return false;
        }
    }

    /*
     * Returns Array of Web Elements that are search results and return the same
     */
    public List<WebElement> getSearchResults() {
        List<WebElement> searchResults = new ArrayList<WebElement>() {
        };
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Find all webelements corresponding to the card content section of each of
            // search results
            searchResults = driver.findElements(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div/div"));

            return searchResults;
        } catch (Exception e) {
            System.out.println("There were no search results: " + e.getMessage());
            return searchResults;

        }
    }

    /*
     * Returns Boolean based on if the "No products found" text is displayed
     */
    public Boolean isNoResultFound() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Check the presence of "No products found" text in the web page. Assign status
            // = true if the element is *displayed* else set status = false
            WebElement noProductsFoundElement = driver.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div/h4")); 
            status = noProductsFoundElement.isDisplayed();
            return status;
        } catch (Exception e) {
            return status;
        }
    }

    /*
     * Return Boolean if add product to cart is successful
     */
    public Boolean addProductToCart(String productName) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            /*
             * Iterate through each product on the page to find the WebElement corresponding
             * to the matching productName
             * 
             * Click on the "ADD TO CART" button for that element
             * 
             * Return true if these operations succeeds
             */
            List<WebElement> productElements = driver.findElements(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div/div")); 
        
        for (WebElement productElement : productElements) {
            WebElement productTitleElement = productElement.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div/div")); 
        
            String productTitle = productTitleElement.getText();
            
            if (productTitle.contains(productName)) {
                // Click on the "ADD TO CART" button for this product
                WebElement addToCartButton = productElement.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div[1]/div/div[2]/button"));
                addToCartButton.click();
                
                return true;
            }
        }
            System.out.println("Unable to find the given product");
            return false;
        } catch (Exception e) {
            System.out.println("Exception while performing add to cart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting the status of clicking on the checkout button
     */
    public Boolean clickCheckout() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find and click on the the Checkout button                             
            WebElement checkoutbutton = driver.findElement(By.xpath("//button[text()='Checkout']"));
            checkoutbutton.click();
            return status;
        } catch (Exception e) {
            System.out.println("Exception while clicking on Checkout: " + e.getMessage());
            return status;
        }
    }

    /*
     * Return Boolean denoting the status of change quantity of product in cart
     * operation
     */
    public Boolean changeProductQuantityinCart(String productName, int quantity) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 06: MILESTONE 5

            // Find the item on the cart with the matching productName

            // Increment or decrement the quantity of the matching product until the current
            // quantity is reached (Note: Keep a look out when then input quantity is 0,
            // here we need to remove the item completely from the cart)

            List<WebElement> cartItems = driver.findElements(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div/div"));

            // Initialize a flag to check if the product was found in the cart
            boolean productFound = false;
    
            // Iterate through each cart item to find the one with the matching product name
            for (WebElement cartItem : cartItems) {
                // Assuming the product name is inside the cart item element (modify this accordingly)
                String cartItemProductName = cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div/div/div[2]/div[1]")).getText();
    
                // Check if the product name in the cart matches the provided productName
                if (cartItemProductName.equals(productName)) {
                    productFound = true;
    
                    // Get the current quantity (modify this accordingly based on your HTML structure)
                    int currentQuantity = Integer.parseInt(cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/div")).getText());
    
                    // Handle the quantity update based on the input quantity
                    while (currentQuantity != quantity){
                        if ( currentQuantity < quantity){
                            cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/button[2]")).click();
                            Thread.sleep(2000);
                        }else if (currentQuantity > quantity){
                            cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/button[1]")).click();
                            Thread.sleep(2000);
                        }
                        //update the currentQuantity here
                        currentQuantity = Integer.parseInt(cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/div")).getText());
                    }
                   // if (quantity == 0) {
                        // Remove the item completely from the cart (click on a remove button or implement accordingly)
                    //    cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/button[1]")).click();
                   // } else {
                        // Update the quantity by clicking the increment or decrement buttons (modify as needed)
                    //    int quantityDifference = quantity - currentQuantity;
                    //    if (quantityDifference > 0) {
                     //       for (int i = 0; i < quantityDifference; i++) {
                      //             cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/button[2]")).click();
                      //      }
                     //   } else if (quantityDifference < 0) {
            //                 for (int i = 0; i < -quantityDifference; i++) {
            //                     cartItem.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/button[1]")).click();
            //                 }
            //             }
            //         }
            //         break; // Exit the loop once the product is found and updated
                 }
             }
    
            if (!productFound) {
                System.out.println("Unable to find the cart item with the given product name");
                return false;
            }
    
            return true;
        } catch (Exception e) {
            if (quantity == 0)
                return true;
            System.out.println("Exception occurred when updating cart: " + e.getMessage());
            return false;
        }
          
    }
    /*
     * Return Boolean denoting if the cart contains items as expected
     */
    public Boolean verifyCartContents(List<String> expectedCartContents) {
        try {
            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> cartContents = cartParent.findElements(By.className("css-zgtx0t"));

            ArrayList<String> actualCartContents = new ArrayList<String>() {
            };
            for (WebElement cartItem : cartContents) {
                actualCartContents.add(cartItem.findElement(By.className("css-1gjj37g")).getText().split("\n")[0]);
            }

            for (String expected : expectedCartContents) {
                if (!actualCartContents.contains(expected)) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println("Exception while verifying cart contents: " + e.getMessage());
            return false;
        }
    }
}
