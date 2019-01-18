import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.List;

public class PageHelper {

    private String testFileName = "C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestFiles\\TestSettingsFile.txt";

    /* ****************************************************************
     *  Navigates to the web address passed in
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress) throws InterruptedException{
        driver.get(webAddress);
        Thread.sleep(10000);
    }

    public void NavigateFFToPage(FirefoxDriver driver, String webAddress) throws InterruptedException{
        driver.get(webAddress);
        Thread.sleep(10000);
    }

    /* ****************************************************************
     *  Saves a screenshot to the
     **************************************************************** */
    public void captureScreenShot(WebDriver driver, String screenShotName, String screenShotFolder) {
        try {
            //get the original dimensions and save them
            Dimension originalDimension = driver.manage().window().getSize();
            int height = originalDimension.height;
            int width = originalDimension.width;

            //reset the browser dimensions to capture all content
            Dimension dimension = GetWindowContentDimensions(driver);
            driver.manage().window().setSize(dimension);

            //take the screen shot
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            if (screenShotFolder != null && !screenShotFolder.isEmpty() && Files.exists(Paths.get(screenShotFolder))) {
                FileUtils.copyFile(source, new File(screenShotFolder + screenShotName+".png"));
            }
            else {
                if (!Files.exists(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"))) {
                    Files.createDirectory(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"));
                }
                FileUtils.copyFile(source, new File("./ScreenShots/" + screenShotName + ".png"));
            }
            System.out.println("Screenshot taken");

            //resize the browser to the original dimensions
            driver.manage().window().setSize(originalDimension);
        }
        catch (Exception e) {
            System.out.println("Exception while taking screenshot " + e.getMessage());
        }
    }



    /* ****************************************************************
     *   This method gets the dimensions of the content area so that
     *   the screen dimensions can be reset before a screen capture to ensure
     *   that all content is in the captured image.
     ***************************************************************** */
    private Dimension GetWindowContentDimensions(WebDriver driver)
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int contentHeight = ((Number) js.executeScript("return document.documentElement.scrollHeight")).intValue();
        int contentWidth = ((Number) js.executeScript("return document.documentElement.scrollWidth")).intValue();

        return new Dimension(contentWidth, contentHeight);
    }


    public void captureScreenShotFF(FirefoxDriver driver, String screenShotName, String screenShotFolder) {
        try {
            //get the original dimensions and save them
            Dimension originalDimension = driver.manage().window().getSize();
            int height = originalDimension.height;
            int width = originalDimension.width;

            //reset the browser dimensions to capture all content
            Dimension dimension = GetFFWindowContentDimensions(driver);
            driver.manage().window().setSize(dimension);

            //take the screen shot
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            if (screenShotFolder != null && !screenShotFolder.isEmpty() && Files.exists(Paths.get(screenShotFolder))) {
                FileUtils.copyFile(source, new File(screenShotFolder + screenShotName+".png"));
            }
            else {
                if (!Files.exists(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"))) {
                    Files.createDirectory(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"));
                }
                FileUtils.copyFile(source, new File("./ScreenShots/" + screenShotName + ".png"));
            }
            System.out.println("Screenshot taken");

            //resize the browser to the original dimensions
            driver.manage().window().setSize(originalDimension);
        }
        catch (Exception e) {
            System.out.println("Exception while taking screenshot " + e.getMessage());
        }
    }

    private Dimension GetFFWindowContentDimensions(FirefoxDriver driver)
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int contentHeight = ((Number) js.executeScript("return document.documentElement.scrollHeight")).intValue();
        int contentWidth = ((Number) js.executeScript("return document.documentElement.scrollWidth")).intValue();

        return new Dimension(contentWidth, contentHeight);
    }

    public List<TestSettings> ReadTestSettingsFile(List<TestSettings> testSettings) throws Exception {
        TestSettings test;
        try (BufferedReader br = new BufferedReader(new FileReader(testFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                test = new TestSettings();
                //test.set_xPath(line.substring(0, line.indexOf(":")).replace("\"","").trim());
                test.set_xPath(line.substring(0, line.indexOf(":")).trim());
                int start = line.indexOf(":") + 1;
                int end = line.length();
                //test.set_expectedValue(line.substring(start, end).replace("\"","").trim());
                test.set_expectedValue(line.substring(start, end).trim());
                testSettings.add(test);
                // process the line.
                System.out.println("(" + test.get_xPath() + ") - (" + test.get_expectedValue() + ")");
            }
            return testSettings;
        }
    }
}
