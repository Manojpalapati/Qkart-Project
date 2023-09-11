package QKART_SANITY_LOGIN.Module1;

import java.sql.Driver;
import java.util.List;
import javax.lang.model.element.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResult {
    WebElement parentElement;

    public SearchResult(WebElement SearchResultElement) {
        this.parentElement = SearchResultElement;
    }

    /*
     * Return title of the parentElement denoting the card content section of a
     * search result
     */
    public String getTitleofResult() {
        // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
        // Find the element containing the title (product name) of the search result and
        // assign the extract title text to titleOfSearchResult
        try {

        WebElement titleElement = parentElement.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div/div"));
        String titleOfSearchResult = titleElement.getText();
        return titleOfSearchResult;
        } catch (Exception e){
            System.out.println("Exception while getting title of search result: " + e.getMessage());
            return "";
        }

        
    }

    /*
     * Return Boolean denoting if the open size chart operation was successful
     */
    public Boolean openSizechart() {
        try {

            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            // Find the link of size chart in the parentElement and click on it
            WebElement sizeChartLink = parentElement.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div[1]/div/div[1]/button"));
            sizeChartLink.click();
            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the close size chart operation was successful
     */
    public Boolean closeSizeChart(WebDriver driver) {
        try {
            Thread.sleep(2000);
            Actions action = new Actions(driver);

            // Clicking on "ESC" key closes the size chart modal
            action.sendKeys(Keys.ESCAPE);
            action.perform();
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing the size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean based on if the size chart exists
     */
    public Boolean verifySizeChartExists() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            /*
             * Check if the size chart element exists. If it exists, check if the text of
             * the element is "SIZE CHART". If the text "SIZE CHART" matches for the
             * element, set status = true , else set to false
             */
            WebElement sizeChartElement = parentElement.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div[1]/div[2]/div[1]/div/div[1]/button"));
            String sizeChartText = sizeChartElement.getText();
            if (sizeChartText.equals("SIZE CHART")){
                status = true;
            }
            return status;
        } catch (Exception e) {
            return status;
        }
    }

    /*
     * Return Boolean if the table headers and body of the size chart matches the
     * expected values
     */
    public Boolean validateSizeChartContents(List<String> expectedTableHeaders, List<List<String>> expectedTableBody,
            WebDriver driver) {
        Boolean status = true;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            /*
             * Locate the table element when the size chart modal is open
             * 
             * Validate that the contents of expectedTableHeaders is present as the table
             * header in the same order
             * 
             * Validate that the contents of expectedTableBody are present in the table body
             * in the same order
             */
            WebElement sizeChartTable = parentElement.findElement(By.className("css-yg30e6"));
            List <WebElement> tableHeaders = sizeChartTable.findElements(By.className("css-1rg3xbn"));
            List <WebElement> tableRows = sizeChartTable.findElements(By.xpath("/html/body/div[3]/div[3]/div/div/table/tbody/tr/td"));
            for (int i = 0; i < expectedTableHeaders.size(); i++) {
                String expectedHeader = expectedTableHeaders.get(i);
                String actualHeader = tableHeaders.get(i).getText();
                if (!expectedHeader.equals(actualHeader)){
                    return false;
                }
            }
            for (int i = 1; i < expectedTableBody.size(); i++) {
                List<WebElement> tableCells = tableRows.get(i).findElements(By.className("/html/body/div[3]/div[3]/div/div/table/tbody/tr/td"));
                List<String> expectedRow = expectedTableBody.get(i - 1);

                for (int j = 0; j < expectedRow.size(); j++) {
                    String expectedCell = expectedRow.get(j);
                    String actualCell = tableCells.get(j).getText();
                    if (!expectedCell.equals(actualCell)) {
                        return false;
                    }
                }
            }
            return status;

        } catch (Exception e) {
            System.out.println("Error while validating chart contents");
            return false;
        }
    }

    /*
     * Return Boolean based on if the Size drop down exists
     */
    public Boolean verifyExistenceofSizeDropdown(WebDriver driver) {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            // If the size dropdown exists and is displayed return true, else return false
            WebElement sizeDropdown = parentElement.findElement(By.xpath("//*[@id='uncontrolled-native']"));
            status = sizeDropdown.isDisplayed();
            return status;
        } catch (Exception e) {
            System.out.println("Error while verifying the Existance of Dropdown");
            return status;
        }
    }
}