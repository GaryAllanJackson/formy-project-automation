import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

enum BrowserType {
    Chrome, Firefox, PhantomJS, Internet_Explorer
}

public class HomePage {

    //region { NOTES }
    /* *********************************************************************
     *  NOTES:
     *   Headless firefox https://developer.mozilla.org/en-US/Firefox/Headless_mode
     *   Moved to file:
     *       //private static String homePageRoot = "https://www.davita.com/";
     *
     *   Completely removed:
     *      //private boolean usePhantomJsDriver = true;
     *      //private FirefoxDriver ffDriver;
     *      //private String testFileName = "C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestFiles\\TestSettingsFile.txt";
     ********************************************************************* */
    //endregion

    private String configurationFile = "C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestFiles\\testSetup.config";
    private WebDriver driver;
    private static String testPage = "https://www.davita.com/";
    private PageHelper pageHelper = new PageHelper();
    private boolean runHeadless = true;
    private String screenShotSaveFolder = "C:\\Gary\\ScreenShots\\";
    private BrowserType _selectedBrowserType; // = BrowserType.Firefox;    //BrowserType.Chrome;  //BrowserType.PhantomJS;
    private int maxBrowsers = 3;
    private boolean testAllBrowsers = false;  //true;
    List<TestSettings> testSettings = new ArrayList<TestSettings>();
    private String testFileName;

    public BrowserType get_selectedBrowserType() {
        return _selectedBrowserType;
    }

    public void set_selectedBrowserType(BrowserType newValue) {
        if (newValue == BrowserType.Chrome) {
            this._selectedBrowserType = BrowserType.Chrome;
        }
        else if (newValue == BrowserType.Firefox) {
            this._selectedBrowserType = BrowserType.Firefox;
        }
        else {
            this._selectedBrowserType = BrowserType.PhantomJS;
        }
    }

    /* ****************************************************************
     * Description: Default Constructor.  Reads the configuration file
     * and the associated test file and when a site is not being tested
     * using all browsers, it sets the browser that will be used for the
     * test.
     **************************************************************** */
    public HomePage() throws Exception
    {
        ConfigureTestEnvironment();
        testSettings = pageHelper.ReadTestSettingsFile(testSettings, testFileName);
        System.out.println("---------[ Beginning Configuration ]-----------------");
        System.out.println("Configured Browser Selection = " + get_selectedBrowserType());

        if (!testAllBrowsers) {
            if (get_selectedBrowserType() == BrowserType.PhantomJS) {
                SetPhantomJsDriver();
            } else if (get_selectedBrowserType() == BrowserType.Chrome) {
                SetChromeDriver();
            } else if (get_selectedBrowserType() == BrowserType.Firefox) {
                SetFireFoxDriver();
            }
            System.out.println("---------[ Ending Configuration ]-----------------");
        }
    }

    @Test   //xpath lookup in this method does not work with headless phantomJS
    public void TestHomePage() throws Exception {
        //System.out.println("Testing " + testPage);
        if (testAllBrowsers) {
            for (int b = 0; b < maxBrowsers; b++) {
                switch (b) {
                    case 0:
                        SetChromeDriver();
                        break;
                    case 1:
                        SetFireFoxDriver();
                        break;
                    case 2:
                        SetPhantomJsDriver();
                        break;
                    default:
                        SetPhantomJsDriver();
                        break;
                }
                TestPageElements();
            }
        } else {
            TestPageElements();
        }
    }



    private void ConfigureTestEnvironment() {
        String tmpBrowserType;
        ConfigSettings configSettings = pageHelper.ReadConfigurationSettings(configurationFile);

        if (configSettings != null) {
            //System.out.println("setting config values");
            tmpBrowserType = configSettings.get_browserType().toLowerCase();
            System.out.println("tmpBrowserType = " + tmpBrowserType);

            if (tmpBrowserType.indexOf("chrome") >= 0) {
                set_selectedBrowserType(BrowserType.Chrome);
                SetChromeDriver();
                System.out.println("selectedBrowserType = " + get_selectedBrowserType().toString());
            }
            else if (tmpBrowserType.indexOf("firefox") >= 0) {
                //this.selectedBrowserType = BrowserType.Firefox;
                set_selectedBrowserType(BrowserType.Firefox);
                SetFireFoxDriver();
                System.out.println("selectedBrowserType = " + get_selectedBrowserType().toString());
            }
            else {
                //this.selectedBrowserType = BrowserType.PhantomJS;
                set_selectedBrowserType(BrowserType.PhantomJS);
                SetPhantomJsDriver();
                System.out.println("selectedBrowserType = " + get_selectedBrowserType().toString());
            }

            this.testPage = configSettings.get_testPageRoot();
            this.runHeadless = configSettings.get_runHeadless();
            this.screenShotSaveFolder = configSettings.get_screenShotSaveFolder();
            this.testAllBrowsers = configSettings.get_testAllBrowsers();
            this.testFileName = configSettings.get_testSettingsFile();
        }
        else {
            System.out.println("configSettings is null!!!");
        }
    }


    private void SetPhantomJsDriver() {
        System.out.println("Setting PhantomJSDriver");
        File src=new File("C:\\Gary\\PhantomJS\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, src.getAbsolutePath());
        //IMPORTANT: for phantomJS you may need to add a user agent for automation testing as the default user agent is old
        // and may not be supported by the website.
        capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
        this.driver = new PhantomJSDriver(capabilities);
        //this.driver.manage().window().maximize();
    }

    private void SetChromeDriver() {
        System.out.println("Setting ChromeDriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");
        if (runHeadless) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("window-size=1400,800");
            options.addArguments("headless");
            driver = new ChromeDriver(options);
        } else {
            driver = new ChromeDriver();
        }
    }

    private void SetFireFoxDriver() {
        System.out.println("Setting FireFoxDriver");
        File gecko = new File("c:\\GeckoDriver\\geckodriver-v0.23.0-win64\\geckodriver.exe");
        System.setProperty("webdriver.gecko.driver", gecko.getAbsolutePath());
        FirefoxOptions options = new FirefoxOptions();
        //options.setCapability("marionette", false);

        if (runHeadless) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("-headless");
            options.setBinary(firefoxBinary);
            driver = new FirefoxDriver(options);
        } else {
            driver = new FirefoxDriver();
        }
    }



    public void TestPageElements() throws Exception {

        //first check the url
//        String expectedUrl = testPage;
//        String actualUrl = CheckPageUrl();
//        assertEquals(expectedUrl, actualUrl);

        int startIndex = 0;  //used for instances when you do not want to start at the first element to test

        for (int x = startIndex; x < testSettings.size(); x++) {
            TestSettings ts = testSettings.get(x);
            String expected = ts.get_expectedValue();
            String xPath = ts.get_xPath();

            //get value and check against expected
            if (!ts.getPerformWrite()) {
                System.out.println("Element type being checked is <" + xPath.substring(xPath.lastIndexOf("/") + 1).trim());

                String actual;

                actual = CheckElementWithXPath(xPath);

                assertEquals(expected, actual);
                String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

                pageHelper.captureScreenShot(driver, browserUsed + expected.replace(' ', '_'), screenShotSaveFolder);
            }
            else {  //set a value or perform a click
                System.out.println("Performing non-read action");
                Boolean status;
                if (ts.get_searchType().toLowerCase().indexOf("xpath") >= 0) {
                    System.out.println("Performing XPath non-read action");
                    status = PerformXPathAction(ts.get_xPath(), ts.get_expectedValue());
                    if (ts.get_expectedValue().toLowerCase().indexOf("-") >= 0) {
                        //url has changed, check url against expected value
                        String expectedUrl = ts.get_expectedValue().substring(ts.get_expectedValue().indexOf("-") + 1).trim();
                        String actualUrl = GetCurrentPageUrl();
                        assertEquals(expectedUrl, actualUrl);
                        if (expectedUrl.equals(actualUrl)) {
                            System.out.println("Successful Post Action results Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ")");
                        }
                        else if (!expectedUrl.equals(actualUrl)) {
                            System.out.println("Failed Post Action results Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ")");
                        }
                    }
                }
                else if (ts.get_searchType().toLowerCase().indexOf("cssselector") >= 0) {
                    System.out.println("Performing CssSelector non-read action");
                    status = PerformCssSelectorAction(ts.get_xPath(), ts.get_expectedValue());
                }
                else if (ts.get_searchType().toLowerCase().indexOf("n/a") >=0) {
                    if (ts.get_expectedValue().toLowerCase().indexOf("navigate") >= 0) {
                        String navigateUrl = ts.get_xPath();
                        String expectedUrl = ts.get_expectedValue().substring(ts.get_expectedValue().indexOf("-") + 1).trim();
                        this.testPage = navigateUrl;
                        String actualUrl = CheckPageUrl();
                        assertEquals(expectedUrl, actualUrl);
                        System.out.println("----[ Start Excplcit Navigation Event ]------------------");
                        if (expectedUrl.trim().equals(actualUrl.trim())) {
                            System.out.println("Navigation and URL Check successful!");
                        } else {
                            System.out.println("Navigation and URL Check unsuccessful! Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")");
                        }
                        System.out.println("----[ Excplcit Navigation Event ]------------------");
                    }
                }
            }
        }
        driver.quit();

        //chromedriver does not shut down from memory so you have to kill the process programmatically
        if (this.driver.toString().indexOf("Chrome") >= 0) {
            ShutDownChromeDriver();
        }
    }


    //@Test  //this works with headless phantomJS
    public String CheckPageUrl() throws Exception{
        System.out.println("In CheckPageUrl method.  Driver = " + this.driver.toString());
        pageHelper.NavigateToPage(this.driver, testPage);

        return this.driver.getCurrentUrl();
    }

    public String GetCurrentPageUrl() {
        return this.driver.getCurrentUrl();
    }


    //@Test
    public String CheckHeading()  throws Exception {
        if (this.driver.getCurrentUrl() != testPage) {
            pageHelper.NavigateToPage(this.driver, testPage);
        }
        String expected = "Empower Yourself with Kidney Knowledge";
        String heading = this.driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div/ul/li/div/div/h1")).getText();
        System.out.println("Checking heading: \"" + heading + "\"");

        return heading;
    }


    public String CheckElementWithXPath(String headingXPath)  throws Exception {
        String heading = "";

        if (this.driver.getCurrentUrl() != testPage) {
            pageHelper.NavigateToPage(this.driver, testPage);
        }
        try {
            heading = this.driver.findElement(By.xpath(headingXPath)).getText();

            //System.out.println("heading = " + heading);
            //System.out.println("Checking heading: \"" + heading + "\"");
            System.out.println("Checking " + ElementTypeLookup(headingXPath) + " with XPath: \"" + heading + "\"");
            return heading;
        } catch (Exception e) {
            //pageHelper.captureScreenShot();
            String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

            pageHelper.captureScreenShot(driver, browserUsed + "xPath_Element_Not_Found", screenShotSaveFolder);
            return null;
        }

    }

    public Boolean PerformXPathAction(String elementXPath, String value) {
        Boolean status = false;
        //if this is a click event, click it
        if (value.toLowerCase().indexOf("click") >= 0) {
            try {
                this.driver.findElement(By.xpath(elementXPath)).click();
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
            return status;
        }
        else {  //if it is not a click, send keys
            try {
                this.driver.findElement(By.xpath(elementXPath)).sendKeys(value);
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
            return status;
        }
    }

    public Boolean PerformCssSelectorAction(String elementCssSelector, String value) {
        Boolean status = false;
        //if this is a click event, click it
        if (value.toLowerCase().indexOf("click") >= 0) {
            try {
                this.driver.findElement(By.xpath(elementCssSelector)).click();
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
            return status;
        }
        else {  //if it is not a click, send keys
            try {
                this.driver.findElement(By.xpath(elementCssSelector)).sendKeys(value);
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
            return status;
        }
    }

    public String CheckElementWithCssSelector(String headingCssSelector) throws Exception {
        //dv-band-hero__content__main__title
        String heading = "";

        if (this.driver.getCurrentUrl() != testPage) {
            pageHelper.NavigateToPage(this.driver, testPage);
        }
        heading = this.driver.findElement(By.cssSelector(headingCssSelector)).getText();

        Thread.sleep(2000);
        //System.out.println("heading = " + heading);
        System.out.println("Checking heading with CssSelector: \"" + heading + "\"");
        return heading;
    }

    public String CheckElementWithTagName(String headingTagName) throws Exception {
        //dv-band-hero__content__main__title
        String heading = "";

        if (this.driver.getCurrentUrl() != testPage) {
            pageHelper.NavigateToPage(this.driver, testPage);
        }
        heading = this.driver.findElement(By.tagName(headingTagName)).getText();

        Thread.sleep(2000);
        //System.out.println("heading = " + heading);
        System.out.println("Checking " + ElementTypeLookup(headingTagName) + " with TagName: \"" + heading + "\"");

        return heading;
    }

    private String ElementTypeLookup(String xPath) {

        //String elementTag = xPath.indexOf("/") > 0 ? xPath.substring(xPath.lastIndexOf("/") + 1).trim() : xPath;
        String elementTag = xPath.substring(xPath.lastIndexOf("/") + 1).trim();
        System.out.println("Looking up elementTag: (" + elementTag + ") Length = " + elementTag.length());

        System.out.println("Difference between elementTag and h3 = " + StringUtils.difference(elementTag,"h3"));

        if (elementTag.toLowerCase().indexOf("a") >= 0 && elementTag.length() == 1) {
            return "Anchor";
        }
        if (elementTag.toLowerCase().indexOf("h") >= 0 && (elementTag.length() == 2 || elementTag.toLowerCase().indexOf("[") > 1)) {
            System.out.println("This is true!!!!");
            return "Heading";
        }
        if (elementTag.toLowerCase().indexOf("li") >= 0 && elementTag.length() == 2) {
            System.out.println("This is true!!!!");
            return "List Item";
        }
        if (elementTag.toLowerCase().indexOf("img") >= 0 && elementTag.length() == 3) {
            System.out.println("This is true!!!!");
            return "Image";
        }
        //region { values not equal but not sure why }
        /*if (elementTag.toLowerCase() == "h1") {
            System.out.println("This is true it does equal h1 !!!!");
            return "Heading";
        }
        if (elementTag.toLowerCase() == "h2") {
            System.out.println("This is true it does equal h2 !!!!");
            return "Heading";
        }
        if (elementTag.toLowerCase() == "h3") {
            System.out.println("This is true it does equal h3 !!!!");
            return "Heading";
        }
        if (elementTag.toLowerCase() == "h4") {
            System.out.println("This is true it does equal h4 !!!!");
            return "Heading";
        }
        if (elementTag.toLowerCase() == "h5") {
            System.out.println("This is true it does equal h5 !!!!");
            return "Heading";
        }
        if (elementTag.toLowerCase() == "img") {
            return "Image";
        }
        if (elementTag.toLowerCase() == "li") {
            return "List Item";
        }*/
        //endregion
        return "Indeterminate";
    }

    private void ShutDownChromeDriver() throws Exception{
        try {
            // Execute command
            //String command = "cmd /c start cmd.exe";
            String command = "taskkill /im chromedriver.exe /f";
            Process child = Runtime.getRuntime().exec(command);

            // Get output stream to write from it
            /*OutputStream out = child.getOutputStream();

            out.write("cd C:/ /r/n".getBytes());

            out.flush();
            out.write("taskkill /im chromedriver.exe /f /r/n".getBytes());

            out.write("exit /r/n".getBytes());
            out.close();*/
        } catch (IOException e) {
            System.out.println("The following error occurred while trying to shut down ChromeDriver: " + e.getMessage());
        }
    }

    //region { Commented out code }
    /*@Test
    public void FBTest() throws InterruptedException {
        WebDriver driver = new PhantomJSDriver();
        driver.get("https://www.facebook.com/");
        System.out.println(driver.getTitle());
        Thread.sleep(2000);
        driver.findElement(By.name("email")).sendKeys("Administrator");
        driver.findElement(By.name("pass")).sendKeys("12iso*help");
        driver.findElement(By.name("login")).click();
        Thread.sleep(2000);
        System.out.println("Page title is: " + driver.getTitle());
    }*/


    /*@BeforeAll
    public static void Setup(){
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");

    }*/

    //@Test
    /*public String CheckPageUrlWithFF() throws Exception{
        System.out.println(this.ffDriver.toString());
        pageHelper.NavigateFFToPage(this.ffDriver, testPage);
        return this.ffDriver.getCurrentUrl();
    }

    public void TestPageElements() throws Exception {

        //first check the url
        String expectedUrl = testPage;
        //String actualUrl = selectedBrowserType == BrowserType.Firefox ? CheckPageUrlWithFF() : CheckPageUrl();
        String actualUrl = CheckPageUrl();
        assertEquals(expectedUrl, actualUrl);
        int startIndex = 0;  //0


        String[] expectedHeadings = {"Empower Yourself with Kidney Knowledge", "Explore Home Dialysis",
                "Kidney-Friendly Recipes for a Healthier You"};
        String[] expectedHeadingsXPaths = {"//*[@id=\"content\"]/div[1]/div/ul/li/div/div/h1",
                "//*[@id=\"content\"]/div[2]/div[1]/div/div/h3", "//*[@id=\"content\"]/div[3]/div/h3"};
        String[] expectedHeadingsCssSelectors = {"dv-band-hero__content__main__title", "dv-tout__title",
                "#content.div.dv-tout.dv-tout--image.div.h3.dv-tout__title"};  //#content.div.dv-tout.dv-tout--image.div.h3
        String[] expectedHeadingsTags = {"h1", "h2", "h4"};
        String[] expectedHeadingsUsingTags = {"Empower Yourself with Kidney Knowledge", "Stay Informed", "Get Free Kidney-Friendly Cookbooks"};

            //this was the origial code being set in code
            for (int x = startIndex; x < expectedHeadings.length; x++) {
                String expected = expectedHeadings[x];
                String actual;

                actual = CheckElementWithXPath(expectedHeadingsXPaths[x]);

                assertEquals(expected, actual);
                String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

                pageHelper.captureScreenShot(driver, browserUsed + expectedHeadings[x].replace(' ', '_'), screenShotSaveFolder);
            }

        for (int x = startIndex; x < testSettings.size(); x++) {
            TestSettings ts = testSettings.get(x);
            String expected = ts.get_expectedValue();
            String xPath = ts.get_xPath();
            //System.out.println("xPath from file (" + xPath + ")");
            //System.out.println("xPath from String [] (" + expectedHeadingsXPaths[x] + ")");

            System.out.println("Element type being checked is <" + xPath.substring(xPath.lastIndexOf("/") + 1).trim());

            String actual;

            //actual = CheckElementWithXPath(expectedHeadingsXPaths[x]);
            actual = CheckElementWithXPath(xPath);

            assertEquals(expected, actual);
            String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

            pageHelper.captureScreenShot(driver, browserUsed + expectedHeadings[x].replace(' ', '_'), screenShotSaveFolder);
        }
        driver.quit();

        //chromedriver does not shut down from memory so you have to kill the process programmatically
            if (this.driver.toString().indexOf("Chrome") >= 0) {
            ShutDownChromeDriver();
        }
    }
    */
    //endregion
}
