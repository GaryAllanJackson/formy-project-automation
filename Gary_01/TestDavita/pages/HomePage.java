import okio.Timeout;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

enum BrowserType {
    Chrome, Firefox, PhantomJS, Internet_Explorer
}

public class HomePage {



    private WebDriver driver; // = new ChromeDriver();
    private FirefoxDriver ffDriver;
    private static String homePageRoot = "https://www.davita.com/";
    private PageHelper pageHelper = new PageHelper();
    private boolean runHeadless = true;
    private boolean usePhantomJsDriver = true;
    private String screenShotSaveFolder = "C:\\Gary\\ScreenShots\\";
    BrowserType selectedBrowserType = BrowserType.Firefox;    //BrowserType.Chrome;  //BrowserType.PhantomJS;
    //Headless firefox https://developer.mozilla.org/en-US/Firefox/Headless_mode
    private int maxBrowsers = 3;
    List<TestSettings> testSettings = new ArrayList<TestSettings>();

    public HomePage() throws Exception
    {
        System.out.println("In HomePage() constructor");
        System.out.println("selectedBrowserType = " + selectedBrowserType.name());
        testSettings = pageHelper.ReadTestSettingsFile(testSettings);

        /*if (selectedBrowserType == BrowserType.PhantomJS) {
            SetPhantomJsDriver();
        }
        else if (selectedBrowserType == BrowserType.Chrome) {
            SetChromeDriver();
        }
        else if (selectedBrowserType == BrowserType.Firefox) {
            SetFireFoxDriver();
        }*/
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

            /*for (int x = startIndex; x < expectedHeadings.length; x++) {
                String expected = expectedHeadings[x];
                String actual;

                actual = CheckSpecificHeading(expectedHeadingsXPaths[x]);

                assertEquals(expected, actual);
                String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

                pageHelper.captureScreenShot(driver, browserUsed + expectedHeadings[x].replace(' ', '_'), screenShotSaveFolder);
            }*/

            for (int x = startIndex; x < testSettings.size(); x++) {
                TestSettings ts = testSettings.get(x);
                String expected = ts.get_expectedValue();
                XPath xPath = XPathFactory.newInstance().newXPath(ts.get_xPath());
                String actual;

                actual = CheckSpecificHeading("\"" + xPath + "\"");

                assertEquals(expected, actual);
                String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

                pageHelper.captureScreenShot(driver, browserUsed + expectedHeadings[x].replace(' ', '_'), screenShotSaveFolder);
            }
            driver.quit();
        }
    }

   /*@Test
   private void ReadAndRunTests()
   {

       try (BufferedReader br = new BufferedReader(new FileReader(file))) {
           String line;
           while ((line = br.readLine()) != null) {
               // process the line.
           }
       }



   }*/


    //@Test  //this works with headless phantomJS
    public String CheckPageUrl() throws Exception{
        System.out.println(this.driver.toString());
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


    public String CheckSpecificHeading(String headingXPath)  throws Exception {
        String heading = "";

        if (this.driver.getCurrentUrl() != homePageRoot) {
            pageHelper.NavigateToPage(this.driver, homePageRoot);
            heading = this.driver.findElement(By.xpath(headingXPath)).getText();
        }

        //System.out.println("heading = " + heading);
        System.out.println("Checking heading: \"" + heading + "\"");
        return heading;
    }

    public String CheckSpecificHeadingWithCssSelector(String headingCssSelector) throws Exception {
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

    public String CheckSpecificHeadingWithTagName(String headingTagName) throws Exception {
        //dv-band-hero__content__main__title
        String heading = "";

        if (this.driver.getCurrentUrl() != homePageRoot) {
            pageHelper.NavigateToPage(this.driver, homePageRoot);
        }
        heading = this.driver.findElement(By.tagName(headingTagName)).getText();

        Thread.sleep(2000);
        //System.out.println("heading = " + heading);
        System.out.println("Checking heading with TagName: \"" + heading + "\"");
        return heading;
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
    }*/
}
