import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

enum BrowserType {
    Chrome, Firefox, PhantomJS, Internet_Explorer
}

public class HomePage {

    /*
        NOTES:
        Headless firefox https://developer.mozilla.org/en-US/Firefox/Headless_mode
     */


    private WebDriver driver; // = new ChromeDriver();
    private FirefoxDriver ffDriver;
    private static String homePageRoot = "https://www.davita.com/";
    private PageHelper pageHelper = new PageHelper();
    private boolean runHeadless = true;
    private boolean usePhantomJsDriver = true;
    private String screenShotSaveFolder = "C:\\Gary\\ScreenShots\\";
    BrowserType selectedBrowserType = BrowserType.Firefox;    //BrowserType.Chrome;  //BrowserType.PhantomJS;
    private int maxBrowsers = 3;
    private boolean testAllBrowsers = false;  //true;
    List<TestSettings> testSettings = new ArrayList<TestSettings>();

    public HomePage() throws Exception
    {
        System.out.println("In HomePage() constructor");
        //System.out.println("selectedBrowserType = " + selectedBrowserType.name());
        testSettings = pageHelper.ReadTestSettingsFile(testSettings);

        if (!testAllBrowsers) {
            if (selectedBrowserType == BrowserType.PhantomJS) {
                SetPhantomJsDriver();
            } else if (selectedBrowserType == BrowserType.Chrome) {
                SetChromeDriver();
            } else if (selectedBrowserType == BrowserType.Firefox) {
                SetFireFoxDriver();
            }
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

    @Test   //xpath lookup in this method does not work with headless phantomJS
    public void TestHomePage() throws Exception {
        System.out.println("Testing HomePage");
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
                TestHomePageElements();
            }
        } else {
            TestHomePageElements();
        }
    }

    public void TestHomePageElements() throws Exception {
        //first check the url
        String expectedUrl = homePageRoot;
        String actualUrl = CheckPageUrl();
        assertEquals(expectedUrl, actualUrl);

        int startIndex = 0;  //used for instances when you do not want to start at the first element to test

        for (int x = startIndex; x < testSettings.size(); x++) {
            TestSettings ts = testSettings.get(x);
            String expected = ts.get_expectedValue();
            String xPath = ts.get_xPath();

            System.out.println("Element type being checked is <" + xPath.substring(xPath.lastIndexOf("/") + 1).trim());

            String actual;

            actual = CheckElementWithXPath(xPath);

            assertEquals(expected, actual);
            String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

            pageHelper.captureScreenShot(driver, browserUsed + expected.replace(' ', '_'), screenShotSaveFolder);
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
        pageHelper.NavigateToPage(this.driver, homePageRoot);

        return this.driver.getCurrentUrl();
    }


    //@Test
    public String CheckHeading()  throws Exception {
        if (this.driver.getCurrentUrl() != homePageRoot) {
            pageHelper.NavigateToPage(this.driver, homePageRoot);
        }
        String expected = "Empower Yourself with Kidney Knowledge";
        String heading = this.driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div/ul/li/div/div/h1")).getText();
        System.out.println("Checking heading: \"" + heading + "\"");

        return heading;
    }


    public String CheckElementWithXPath(String headingXPath)  throws Exception {
        String heading = "";

        if (this.driver.getCurrentUrl() != homePageRoot) {
            pageHelper.NavigateToPage(this.driver, homePageRoot);
        }
        heading = this.driver.findElement(By.xpath(headingXPath)).getText();

        //System.out.println("heading = " + heading);
        //System.out.println("Checking heading: \"" + heading + "\"");
        System.out.println("Checking " + ElementTypeLookup(headingXPath) + " with TagName: \"" + heading + "\"");
        return heading;
    }

    public String CheckElementWithCssSelector(String headingCssSelector) throws Exception {
        //dv-band-hero__content__main__title
        String heading = "";

        if (this.driver.getCurrentUrl() != homePageRoot) {
            pageHelper.NavigateToPage(this.driver, homePageRoot);
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

        if (this.driver.getCurrentUrl() != homePageRoot) {
            pageHelper.NavigateToPage(this.driver, homePageRoot);
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
        if (elementTag.toLowerCase().indexOf("h") >= 0 && elementTag.length() == 2) {
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
        pageHelper.NavigateFFToPage(this.ffDriver, homePageRoot);
        return this.ffDriver.getCurrentUrl();
    }

    public void TestHomePageElements() throws Exception {

        //first check the url
        String expectedUrl = homePageRoot;
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
}
