import com.sun.xml.internal.fastinfoset.util.StringArray;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.List;

public class PageHelper {

    private String testFileName = "C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestFiles\\TestSettingsFile.txt";

    //region { System.out Colors }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    //endregion

    /* ****************************************************************
     *  DESCRIPTION:
     *  Navigates to the web address passed in
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress) throws InterruptedException{
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

            screenShotName = MakeValidFileName(screenShotName);

            //take the screen shot
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            if (screenShotFolder != null && !screenShotFolder.isEmpty() && Files.exists(Paths.get(screenShotFolder))) {
                if (!screenShotFolder.endsWith("\\"))
                {
                    screenShotFolder = screenShotFolder + "\\";
                }
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
            System.out.println(ANSI_RED + "Exception while taking screenshot (" + screenShotName + "): " + e.getMessage() + ANSI_RESET);
        }
    }

    private String MakeValidFileName(String screenShotName) {
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyz1234567890_-";
        String cleanValue = "";
        //System.out.println("Allowed Characters: (" + allowedCharacters + ")");
        for (int x=0;x<(screenShotName.length()-1);x++)
        {
            //System.out.println(screenShotName.substring(x,x + 1).toLowerCase());
            if (allowedCharacters.indexOf(screenShotName.substring(x,x + 1).toLowerCase()) >= 0)
            {
                cleanValue = cleanValue + screenShotName.substring(x, x + 1);
            }
        }
        return cleanValue;
    }


    /* ****************************************************************
     *   DESCRIPTION:
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


    /* *******************************************************************************************
        Description: This method reads in the test file, parsing each line and creating a new <TestSettings>
        object, placing the xPath string into the xPath Property, placing the Expected value string into the
        Expected value Property and adding that to the List<TestSettings> ArrayList
     ******************************************************************************************* */
    public List<TestSettings> ReadTestSettingsFile(List<TestSettings> testSettings, String testFileName) throws Exception {
        TestSettings test;
        /*if (testFileName == null || testFileName.isEmpty())
        {
            testFileName = this.testFileName;
        }*/
        try (BufferedReader br = new BufferedReader(new FileReader(testFileName))) {
            String line;
            String [] lineValues;
            System.out.println(ANSI_PURPLE + "----------[ Start of Reading Test Settings file File ]--------------" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Reading " + testFileName +  " file" + ANSI_RESET);
            while ((line = br.readLine()) != null) {
                //line comments in this file are indicated with ###
                if (line.indexOf("###") < 0) {
                    test = new TestSettings();
                    lineValues = line.split(";");
                    //test.set_xPath(line.substring(0, line.indexOf(":")).trim());
                    test.set_xPath(lineValues[0].trim());
                    test.set_expectedValue(lineValues[1].trim());
                    test.set_searchType(lineValues[2].trim());
                    test.setPerformWrite(Boolean.parseBoolean(lineValues[3].trim()));
                    test.set_isCrucial(Boolean.parseBoolean(lineValues[4].trim()));
                    testSettings.add(test);
                    // Show input to user
                    System.out.println(ANSI_PURPLE + "Reading Test File values(xPath = " + test.get_xPath() + ") - (Expected Value = " + test.get_expectedValue() + ")" + ANSI_RESET);
                }
            }
            System.out.println(ANSI_PURPLE + "----------[ End of Reading Test Settings file File ]--------------" + ANSI_RESET);
            return testSettings;
        }
    }



    /* ******************************************************************
     * Description: This method reads the test configuration file
     * and populates the ConfigSettings variable with these settings
     * which in turn direct the test to use the selected browser and
     * to test the configured site.
     ****************************************************************** */
    public ConfigSettings ReadConfigurationSettings(String configurationFile) {
        ConfigSettings configSettings = new ConfigSettings();
        String configValue;
        //System.out.println("Reading " + configurationFile +  " file");
        try (BufferedReader br = new BufferedReader(new FileReader(configurationFile))) {
            String line;
            System.out.println("----------[ Start of Reading Configuration File ]--------------");
            System.out.println("Reading " + configurationFile +  " file");
            while ((line = br.readLine()) != null) {
                //if (line.substring(0,2) != "//") {
                if (line.substring(0,2).indexOf("//") < 0) {
                    configValue = line.substring(line.indexOf("=") + 1);
                    //System.out.println("configValue = " + configValue);
                    if (line.toLowerCase().indexOf("browsertype") >= 0) {
                        configSettings.set_browserType(configValue);
                        System.out.println("browserType = " + configSettings.get_browserType().toString());
                    }
                    else if (line.toLowerCase().indexOf("testpageroot") >= 0) {
                        configSettings.set_testPageRoot(configValue);
                        System.out.println("testPageRoot = " + configSettings.get_testPageRoot());
                    }
                    else if (line.toLowerCase().indexOf("runheadless") >= 0) {
                        configSettings.set_runHeadless(Boolean.parseBoolean(configValue));
                        System.out.println("runHeadless = " + configSettings.get_runHeadless().toString());
                    }
                    else if (line.toLowerCase().indexOf("screenshotsavefolder") >= 0) {
                        configSettings .set_screenShotSaveFolder(configValue);
                        System.out.println("screenShotSaveFolder = " + configSettings.get_screenShotSaveFolder());
                    }
                    else if (line.toLowerCase().indexOf("testallbrowsers") >= 0) {
                        configSettings.set_testAllBrowsers(Boolean.parseBoolean(configValue));
                        System.out.println("testAllBrowsers = " + configSettings.get_testAllBrowsers().toString());
                    }
                    else if (line.toLowerCase().indexOf("testfilename") >= 0) {
                        configSettings.set_testSettingsFile(configValue);
                        System.out.println("testFileName = " + configSettings.get_testSettingsFile());
                    }
                }
            }
        }
        catch (Exception e) {
            //configSettings = null;
            System.out.println("The following error occurred while attempting to read the configuration file:" + configurationFile + "\\r\\n" + e.getMessage());
        }
        //System.out.println("testSetup.config file read.");
        System.out.println("----------[ End of Reading Configuration File ]--------------");
        return configSettings;
    }

    public void WriteToFile(String fileName, String fileContents) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(fileContents);
            writer.newLine();
            writer.close();
        }
        catch(Exception ex) {
            System.out.println("The following error occurred when attempting to write to the test log file:" + ex.getMessage());
        }
    }

    //region { Commented Out Code }
//    private void UpdateTestResults(String testMessage) {
//        testResults.add(testMessage);
//        System.out.println(testMessage);
//    }


    /*
    // the below methods were written exclusively for the Firefox driver but are no longer needed
    // as all browsers are using WebDriver.
     public void NavigateFFToPage(FirefoxDriver driver, String webAddress) throws InterruptedException{
        driver.get(webAddress);
        Thread.sleep(10000);
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
    }*/
    //endregion
}
