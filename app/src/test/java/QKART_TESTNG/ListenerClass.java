package QKART_TESTNG;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListenerClass implements ITestListener {
    // private WebDriver driver;

    // // Constructor to accept the WebDriver instance
    // public ListenerClass(WebDriver driver) {
    //     this.driver = driver;
    // }

    public void onStart(ITestContext context) {
        System.out.println("onStart Method Started" );
        
    }

    
    public void onTestStart(ITestResult result) {
        System.out.println("New Test Started: " + result.getName() + "Taking Screenshot !");
        captureScreenshot(QKART_Tests.driver, result.getName()+ "_Start");
    }

    
    public void onTestSuccess(ITestResult result) {
        System.out.println("On Test Success Method: " + result.getName() + "Taking Screenshot !");
        captureScreenshot(QKART_Tests.driver, result.getName()+"_Success");
    }

   
    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed: " + result.getName()+ "Taking Screenshot !");
        captureScreenshot(QKART_Tests.driver,result.getName()+"_Failed");
    }

    private void captureScreenshot(WebDriver driver, String screenshotName) {
        if (driver instanceof TakesScreenshot) {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String screenshotFileName = "screenshot_" + screenshotName + "_" + timeStamp + ".png";
            String screenshotDirectory = "screenshots/";

            try {
                FileUtils.copyFile(screenshotFile, new File(screenshotDirectory + screenshotFileName));
                System.out.println("Screenshot captured: " + screenshotFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

}
