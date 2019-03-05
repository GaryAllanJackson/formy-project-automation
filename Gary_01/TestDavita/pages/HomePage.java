import com.sun.javafx.geom.Edge;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.ie.*;
import io.restassured.RestAssured;
import org.openqa.selenium.support.Color;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.util.stream.LongStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

enum BrowserType {
    Chrome, Firefox, PhantomJS, Internet_Explorer, Edge
}

public class HomePage {

    //region { NOTES }
    /*
    *╔═══════════════════════════════════════════════════════════════════════════════╗
     *  NOTES:
     *   Headless firefox https://developer.mozilla.org/en-US/Firefox/Headless_mode
     *   Moved to file:
     *       //private static String homePageRoot = "https://www.davita.com/";
     *
     *   Setup Instructions:
     *      First, mark the Entities, pages and test folders as Source Root.
     *      Second, to run Internet Explorer, you must add the path to your System Path variable.
     *      (Control Panel -> System - Advanced System Settings -> Environment Variables -> User variables for xUser -> Path)
     *          - Ensure a semi-colon separates this item from the previous item and add a semi-colon afterward.
     *          - Test this by opening a NEW cmd window and typing the following: echo %path%
     *          - The new path entered should be listed among the semi-colon delimited paths.
     *      Update the following items if necessary:
     *      configurationFile,
     *      configurationFolder,
     *      logFileName,
     *      chromeDriverPath,
     *      fireFoxDriverPath,
     *      phantomJsDriverPath,
     *      internetExplorerDriverPath,
     *      edgeDriverPath
     *
     *      Future updates:
     *      1.  Need to add switch to frame for iFrames
     *          driver.switch_to.frame('NAME')                  - (Completed!!!)
     *      2.  Need to separate debug output from required output and make
     *          it configurable whether extra information is output.
     ╚═══════════════════════════════════════════════════════════════════════════════╝ */
    //endregion

    //region { constants }
    private final String parameterDelimiter = " ╬ ";  //made parameter delimiter a constant

    //endregion

    //region { Configuration Variables }
    private String configurationFile = "Config/ConfigurationSetup.tconfig";
    private String configurationFolder = "Config/";
    private static String testPage = "https://www.myWebsite.com/";
    private boolean runHeadless = true;
    private String screenShotSaveFolder;
    private BrowserType _selectedBrowserType; // = BrowserType.Firefox;    //BrowserType.Chrome;  //BrowserType.PhantomJS;
    private int maxBrowsers = 3;

    //endregion
    private WebDriver driver;
    private PageHelper pageHelper = new PageHelper();
    private boolean testAllBrowsers = false;  //true;
    List<TestSettings> testSettings = new ArrayList<TestSettings>();
    private String testFileName;
    List<String> testResults = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
    private String logFileUniqueName = dateFormat.format(new Date());
    private String logFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\TestResults_" + logFileUniqueName + ".log" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/TestResults_" + logFileUniqueName + ".log";
    private String helpFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\ConfigTester_Help.txt" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/ConfigTester_Help.txt";
    List<String> testFiles = new ArrayList<>();
    private String chromeDriverPath = "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe";
    private String fireFoxDriverPath = "/GeckoDriver/geckodriver-v0.23.0-win64/geckodriver.exe";
    private String phantomJsDriverPath = "/Gary/PhantomJS/phantomjs-2.1.1-windows/bin/phantomjs.exe";
    //private String internetExplorerDriverPath = "/Users/gjackson/Downloads/IEDriverServer_x64_3.9.0/IEDriverServer.exe";
    private String internetExplorerDriverPath = "/Users/gjackson/Downloads/IEDriverServer_x64_3.11.1/IEDriverServer.exe";
    private String edgeDriverPath = "/Users/gjackson/Downloads/EdgeDriver/MicrosoftWebDriver.exe";

    private boolean _executedFromMain = false;
    private int brokenLinksStatusCode;




    //region { Properties }

    public boolean is_executedFromMain() {
        return _executedFromMain;
    }

    public void set_executedFromMain(boolean _executedFromMain) {
        this._executedFromMain = _executedFromMain;
    }

    public BrowserType get_selectedBrowserType() {
        return _selectedBrowserType;
    }

    public void set_selectedBrowserType(BrowserType newValue) {
        if (newValue == BrowserType.Chrome) {
            this._selectedBrowserType = BrowserType.Chrome;
        } else if (newValue == BrowserType.Firefox) {
            this._selectedBrowserType = BrowserType.Firefox;
        } else if (newValue == BrowserType.Internet_Explorer) {
            this._selectedBrowserType = BrowserType.Internet_Explorer;
        } else if (newValue == BrowserType.Edge) {
            this._selectedBrowserType = BrowserType.Edge;
        }  else {
            this._selectedBrowserType = BrowserType.PhantomJS;
        }
    }
    //endregion


    public void StupidUtility() {

        //String template = "╠//*[@id=\"bp-page-2\"]/div[2]/div[2] ; Test ; xPath ; false ; false╣";
        //String template = "╠//*[@id=\"bp-page-2\"]/div[2]/div[number] ; Test ; xPath ; false ; false╣";

        Scanner scanner = new Scanner(System.in);
        pageHelper.UpdateTestResults("Configuration File not found (" + configurationFile + ")");
        pageHelper.UpdateTestResults("Enter the template that you want to replicate, place the word number in brackets [number] for the item to be replaced: ");
        String template = scanner.nextLine();
        pageHelper.UpdateTestResults("Enter the low number that you want to start with:");
        int startNumber = parseInt(scanner.nextLine());
        pageHelper.UpdateTestResults("Enter the high number that you want to end with:");
        int endNumber = parseInt(scanner.nextLine());
        pageHelper.UpdateTestResults("Enter the value that you want to increment by (default is 1):");
        int incrementNumber = parseInt(scanner.nextLine());

        for (int x=startNumber;x<endNumber;x+=incrementNumber) {
            pageHelper.UpdateTestResults(template.replace("[number]", "[" + x + "]"));
        }


//region { old}
//        for (int x=2;x<173;x+=5) {
//            pageHelper.UpdateTestResults(template.replace("[number]", "[" + x + "]"));
//            pageHelper.UpdateTestResults(template.replace("[number]", "[" + (x + 1) + "]"));
//        }
        //endregion
    }

    public void ColorUtility() {
//        for i in range(0, 16):
//        for j in range(0, 16):
        String code;
        for (int i=0;i<=16;i++) {
            for (int j = 0; j <= 16; j++) {
                code = Integer.toString((i * 16 + j));
//            pageHelper.UpdateTestResults("\u001b[48;5;" + code + "m " + code.ljust(4));
                //sys.stdout.write(u"\u001b[48;5;" + code + "m " + code.ljust(4))
                //print u "\u001b[0m"
            }
        }
    }

    /* ****************************************************************
     * Description: Default Constructor.  Reads the configuration file
     * and the associated test file and when a site is not being tested
     * using all browsers, it sets the browser that will be used for the
     * test.
     **************************************************************** */
    public void HomePageStart(boolean isStartedFromMain) throws Exception {
        this.set_executedFromMain(isStartedFromMain);

        File tmp = new File(configurationFile);
        pageHelper.UpdateTestResults(pageHelper.ANSI_YELLOW +  "Config File absolute path = " + pageHelper.ANSI_RESET + tmp.getAbsolutePath());
        pageHelper.UpdateTestResults(pageHelper.ANSI_YELLOW +  "Log File Name = " + pageHelper.ANSI_RESET  + logFileName);
        pageHelper.UpdateTestResults(pageHelper.ANSI_YELLOW +  "Help File Name = " + pageHelper.ANSI_RESET + helpFileName);
        //pageHelper.UpdateTestResults("");

        pageHelper.set_logFileName(logFileName);
        pageHelper.set_helpFileName(helpFileName);
        boolean status = ConfigureTestEnvironment();
        if (!status) {
            return;
        }

        //testSettings = pageHelper.ReadTestSettingsFile(testSettings, testFileName);
        pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_GREEN_BACKGROUND + pageHelper.ANSI_BLUE + pageHelper.ANSI_BOLD + pageHelper.sectionStartFormatLeft + "Beginning Configuration" + pageHelper.sectionStartFormatRight + pageHelper.ANSI_RESET, testResults);
        pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + pageHelper.indent5 + "Configured Browser Selection = " + pageHelper.ANSI_RESET + get_selectedBrowserType(), testResults);

        if (!testAllBrowsers) {
            if (get_selectedBrowserType() == BrowserType.PhantomJS) {
                SetPhantomJsDriver();
            } else if (get_selectedBrowserType() == BrowserType.Chrome) {
                SetChromeDriver();
            } else if (get_selectedBrowserType() == BrowserType.Firefox) {
                SetFireFoxDriver();
            } else if (get_selectedBrowserType() == BrowserType.Internet_Explorer) {
                SetInternetExplorerDriver();
            }else if (get_selectedBrowserType() == BrowserType.Edge) {
                SetEdgeDriver();
            }
            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_GREEN_BACKGROUND + pageHelper.ANSI_BLUE + pageHelper.ANSI_BOLD + pageHelper.sectionEndFormatLeft + "Ending Configuration" + pageHelper.sectionEndFormatRight + pageHelper.ANSI_RESET, testResults);
        }
    }



    public HomePage() throws Exception {
        //created this default constructor so that this could function as an application
        //and run from the main method in Form.java so that if the configuration file is
        // not in the proper location, the correct path can be specified as input.
    }


    /* ***************************************************************************
     *  DESCRIPTION:
     *    Runs all of the tests read in from the test settings file.
     **************************************************************************** */
    @Test   //xpath lookup in this method does not work with headless phantomJS
    public void TestHomePage() throws Exception {
        HomePageStart(is_executedFromMain());
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
                    case 3:
                        SetInternetExplorerDriver();
                        break;
                    case 4:
                        SetEdgeDriver();
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


    /* ***************************************************************************
     *  DESCRIPTION:
     *  Calls the ReadConfigurationSettings method, to read the config file.
     *  Sets configurable variables using those values.
     *************************************************************************** */
//    private void ConfigureTestEnvironment() throws Exception {
    private boolean ConfigureTestEnvironment() throws Exception {
        String tmpBrowserType;
        pageHelper.UpdateTestResults(pageHelper.ANSI_YELLOW + "Executed From Main or as Standalone App = " + pageHelper.ANSI_RESET + is_executedFromMain());
        ConfigSettings configSettings = pageHelper.ReadConfigurationSettings(configurationFile, is_executedFromMain());

        if (configSettings != null) {
            tmpBrowserType = configSettings.get_browserType().toLowerCase();
            if (tmpBrowserType.indexOf("chrome") >= 0) {
                set_selectedBrowserType(BrowserType.Chrome);
            } else if (tmpBrowserType.indexOf("firefox") >= 0) {
                set_selectedBrowserType(BrowserType.Firefox);
            } else if (tmpBrowserType.indexOf("internetexplorer") >= 0 || tmpBrowserType.indexOf("internet explorer") >= 0) {
                set_selectedBrowserType(BrowserType.Internet_Explorer);
            } else if (tmpBrowserType.indexOf("edge") >= 0) {
                set_selectedBrowserType(BrowserType.Edge);
            }else {
                set_selectedBrowserType(BrowserType.PhantomJS);
            }
            testFiles = configSettings.get_testFiles();
            this.testPage = configSettings.get_testPageRoot();
            this.runHeadless = configSettings.get_runHeadless();
            this.screenShotSaveFolder = configSettings.get_screenShotSaveFolder();
            this.testAllBrowsers = configSettings.get_testAllBrowsers();

            if (testFiles.size() > 1) {
                this.testFileName = testFiles.get(0);
            } else {
                this.testFileName = configSettings.get_testSettingsFile();
            }
            return true;
        } else {
            pageHelper.UpdateTestResults("configSettings is null!!!", testResults);
            return false;
        }
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the PhantomJs Driver
     **************************************************************************** */
    private void SetPhantomJsDriver() {
        pageHelper.UpdateTestResults( pageHelper.indent5 + "[" + pageHelper.ANSI_GREEN + "Setting " + pageHelper.ANSI_RESET + "PhantomJSDriver]" + pageHelper.ANSI_RESET , testResults);
        File src = new File(phantomJsDriverPath);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, src.getAbsolutePath());
        //IMPORTANT: for phantomJS you may need to add a user agent for automation testing as the default user agent is old
        // and may not be supported by the website.
        capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
        this.driver = new PhantomJSDriver(capabilities);
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Chrome Driver
     **************************************************************************** */
    private void SetChromeDriver() {
        //pageHelper.UpdateTestResults(pageHelper.indent5 + "[Setting ChromeDriver]", testResults);
        pageHelper.UpdateTestResults( pageHelper.indent5 + "[" + pageHelper.ANSI_GREEN + "Setting " + pageHelper.ANSI_RESET + "ChromeDriver]" + pageHelper.ANSI_RESET , testResults);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        if (runHeadless) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("window-size=1400,800");
            options.addArguments("headless");
            driver = new ChromeDriver(options);
        } else {
            driver = new ChromeDriver();
        }
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the FireFox Driver
     **************************************************************************** */
    private void SetFireFoxDriver() {
        //pageHelper.UpdateTestResults(pageHelper.indent5 + "[Setting FireFoxDriver]", testResults);
        pageHelper.UpdateTestResults( pageHelper.indent5 + "[" + pageHelper.ANSI_GREEN + "Setting " + pageHelper.ANSI_RESET + "FireFoxDriver]" + pageHelper.ANSI_RESET , testResults);
        File gecko = new File(fireFoxDriverPath);
        System.setProperty("webdriver.gecko.driver", gecko.getAbsolutePath());
        FirefoxOptions options = new FirefoxOptions();
        //options.setCapability("marionette", false);
        String loggingLevel = "fatal";   //"trace"
        options.setCapability("marionette.logging", "trace");

        if (runHeadless) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("-headless");
            options.setBinary(firefoxBinary);
            driver = new FirefoxDriver(options);
        } else {
            driver = new FirefoxDriver();
        }
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Internet Explorer Driver
     **************************************************************************** */
    private void SetInternetExplorerDriver() {
        //internetExplorerDriverPath
        pageHelper.UpdateTestResults( pageHelper.indent5 + "[" + pageHelper.ANSI_GREEN + "Setting " + pageHelper.ANSI_RESET + "InternetExplorerDriver]" + pageHelper.ANSI_RESET , testResults);
        File internetExplorer = new File(internetExplorerDriverPath);
        pageHelper.UpdateTestResults("internetExplorer.getAbsolutePath() = " + internetExplorer.getAbsolutePath());

        System.setProperty("webdriver.ie.driver", internetExplorer.getAbsolutePath());
        File tmp = new File("C:\\Temp\\");

        DesiredCapabilities capab = DesiredCapabilities.internetExplorer();
        capab.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        driver = new InternetExplorerDriver(capab);
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Not working yet: (Sets the WebDriver to the FireFox Driver)
     **************************************************************************** */
    private void SetEdgeDriver() {
        pageHelper.UpdateTestResults( pageHelper.indent5 + "[" + pageHelper.ANSI_GREEN + "Setting " + pageHelper.ANSI_RESET + "EdgeDriver]" + pageHelper.ANSI_RESET , testResults);
        File edge = new File(edgeDriverPath);
        pageHelper.UpdateTestResults("edge.getAbsolutePath() = " + edge.getAbsolutePath());

        System.setProperty("webdriver.edge.driver", edge.getAbsolutePath());
//        File tmp = new File("C:\\Temp\\");
//        EdgeOptions options = new EdgeOptions();
//        options.setCapability();
        driver = new EdgeDriver();
    }


    /* ***************************************************************************
     *  DESCRIPTION:
     *    Runs all tests read in from the test settings file.
     **************************************************************************** */
    public void TestPageElements() throws Exception {
        if (this.driver == null) {
            return;
        }
        int startIndex = 0;  //used for instances when you do not want to start at the first element to test
        String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
        boolean revertToParent = false;
        //boolean isError = false;

        for (int fileIndex = 0; fileIndex < testFiles.size(); fileIndex++) {
            testFileName = testFiles.get(fileIndex);
            testSettings = new ArrayList<>();
            testSettings = pageHelper.ReadTestSettingsFile(testSettings, testFileName);
            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + pageHelper.ANSI_YELLOW + pageHelper.sectionStartFormatLeft + "Running Test Script" + pageHelper.sectionStartFormatRight + pageHelper.ANSI_RESET, testResults);
            for (int x = startIndex; x < testSettings.size(); x++) {
                if (revertToParent) {
                    driver.switchTo().defaultContent();
                    pageHelper.UpdateTestResults( PageHelper.ANSI_CYAN + pageHelper.sectionEndFormatLeft + "End Switch to IFrame - Reverting to defaultContent" + pageHelper.sectionEndFormatRight + PageHelper.ANSI_RESET);
                    revertToParent = false;
                }
                TestSettings ts = testSettings.get(x);
                String expected = ts.get_expectedValue();
                String accessor = ts.get_xPath();
                String fileStepIndex = "_F" + fileIndex + "_S" + x + "_";
                String fileStepIndexForLog = "F" + fileIndex + "_S" + x;

                //if switching to an iframe, switch first
                if (expected.toLowerCase().contains("switch to iframe"))
                {
                    //String [] expectedItems = expected.split(" - ");
                    String [] expectedItems = expected.split(parameterDelimiter);
                    String frameName = expectedItems[0].substring(expectedItems[0].indexOf("[") + 1, expectedItems[0].indexOf("]"));
                    //pageHelper.UpdateTestResults(PageHelper.ANSI_CYAN + "-------[ Switching to iFrame: " + frameName + " for step " + fileStepIndexForLog + " ]-------" + PageHelper.ANSI_RESET);
                    pageHelper.UpdateTestResults(PageHelper.ANSI_CYAN + pageHelper.sectionStartFormatLeft +  "Switching to iFrame: " + frameName + " for step " + fileStepIndexForLog + pageHelper.sectionStartFormatRight + PageHelper.ANSI_RESET);
                    driver.switchTo().frame(frameName);
                    expected = expectedItems[1];
                    revertToParent = true;
                }

                //get value and check against expected
                if (!ts.getPerformWrite()) {
                    if (!ts.get_searchType().toLowerCase().equals("n/a")) {
                        //refactored and moved to separate method
                        CheckElementText(browserUsed, ts, expected, accessor, fileStepIndex, fileStepIndexForLog);
                    }
                    else {
                        if (ts.get_expectedValue().toLowerCase().contains("check") && (ts.get_expectedValue().toLowerCase().contains("post") ||
                                ts.get_expectedValue().toLowerCase().contains("get"))) {
                            //refactored and moved to separate method
                            CheckGetPostStatus(ts, fileStepIndexForLog);
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("links")) {
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking page links for " + ts.get_xPath());
                            checkBrokenLinks(ts.get_xPath());
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("image"))
                        {
                            if (ts.get_expectedValue().toLowerCase().contains("alt")) {
                                checkADAImages(ts.get_xPath(), "alt");
                            }
                            else if (ts.get_expectedValue().toLowerCase().contains("src")) {
                                checkADAImages(ts.get_xPath(), "src");
                            }
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("count")) {
                            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
                            String [] checkItems = expectedItems[0].split(" ");
                            String page = ts.get_xPath().toLowerCase().equals("n/a") ? driver.getCurrentUrl() : ts.get_xPath().trim();
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking count of " + checkItems[2] + " on page " + page);
                            int expectedCount = parseInt(expectedItems[1]);
//                            pageHelper.UpdateTestResults("Sending values ts.get_xPath() = " + ts.get_xPath() + " checkItems[2].trim() = " + checkItems[2].trim());
//                            pageHelper.UpdateTestResults(" - expectedCount = " + expectedCount + " fileStepIndexForLog = " + fileStepIndexForLog);
//                            pageHelper.UpdateTestResults(" - ts.get_isCrucial() = " + ts.get_isCrucial());
                            checkElementCount(ts.get_xPath(), checkItems[2].trim(), expectedCount, fileStepIndexForLog, ts.get_isCrucial());
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("contrast")) {
                            //checkColorContrast
                            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
                            String [] checkItems = expectedItems[0].split(" ");
                            //String [] acceptibleRanges = expectedItems.length > 1 ? expectedItems[1].split(" ") : null;
                            String acceptibleRanges = expectedItems.length > 1 ? expectedItems[1] : null;
                            String page = ts.get_xPath().toLowerCase().equals("n/a") ? driver.getCurrentUrl() : ts.get_xPath().trim();
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking color contrast of " + checkItems[2] + " on page " + page);
                            checkColorContrast(ts.get_xPath(), checkItems[2].trim(), fileStepIndexForLog, ts.get_isCrucial(), acceptibleRanges);
                        }
                        //add in check all elements for a particular text, src, alt value
//                        else if (ts.get_expectedValue().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("all")) {
//                            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
//                            String [] checkItems = expectedItems[0].split(" ");
//                        }
                    }
                } else {  //set a value or perform a click
                    Boolean status;
                    int dashCount = ts.get_expectedValue().contains(parameterDelimiter) ? StringUtils.countMatches(ts.get_expectedValue(), parameterDelimiter) : 0;
                    //pageHelper.UpdateTestResults("ts.get_expectedValue() = " + ts.get_expectedValue());
                    //GAJ working here perform all non read actions below that use an accessor
                    if (((ts.get_searchType().toLowerCase().indexOf("xpath") >= 0) || (ts.get_searchType().toLowerCase().indexOf("cssselector") >= 0) ||
                            (ts.get_searchType().toLowerCase().indexOf("tagname") >= 0) || (ts.get_searchType().toLowerCase().indexOf("id") >= 0) ||
                            (ts.get_searchType().toLowerCase().indexOf("classname") >= 0))
                            && (!ts.get_expectedValue().toLowerCase().contains("sendkeys") && !ts.get_expectedValue().toLowerCase().contains("wait"))){
                        pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing " + ts.get_searchType() + " " + fileStepIndexForLog + " non-read action", testResults);
                        String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
                        String subAction = null;

                        //check if switching to an iFrame
                        if (!ts.get_expectedValue().toLowerCase().contains("switch to iframe")) {
                            status = PerformAction(ts.get_searchType(), ts.get_xPath(), ts.get_expectedValue(), fileStepIndex);
                        }
                        else {
                            subAction = expectedItems[1];
                            status = PerformAction(ts.get_searchType(), ts.get_xPath(), subAction, fileStepIndex);
                        }

                        //if not a right click context command
                        if (ts.get_expectedValue().toLowerCase().indexOf(parameterDelimiter) >= 0 && subAction == null && !ts.get_expectedValue().toLowerCase().contains("right click")
                                && !ts.get_expectedValue().toLowerCase().contains("sendkeys")) {
                            //url has changed, check url against expected value
                            String expectedUrl = ts.get_expectedValue().substring(ts.get_expectedValue().indexOf(parameterDelimiter) + 3).trim();

                            if (dashCount > 1) {
                                int delayMilliSeconds = parseInt(ts.get_expectedValue().substring(ts.get_expectedValue().lastIndexOf(parameterDelimiter) + 3).trim());
                                DelayCheck(delayMilliSeconds, fileStepIndex);
                                expectedUrl = expectedUrl.substring(0, expectedUrl.indexOf(parameterDelimiter));
                            }
                            String actualUrl = GetCurrentPageUrl();
                            if (ts.get_isCrucial()) {
                                assertEquals(expectedUrl, actualUrl);
                            } else {
                                try {
                                    assertEquals(expectedUrl, actualUrl);
                                } catch (AssertionError ae) {
                                    //isError = true;
                                    //if the non-crucial test fails, take a screenshot and keep processing remaining tests
                                    if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                                        pageHelper.captureScreenShot(driver, browserUsed  + ts.get_searchType()  + fileStepIndex + "Element_Not_Found" + expected.replace(' ', '_'), configurationFolder, true);
                                    } else {
                                        pageHelper.captureScreenShot(driver, browserUsed + ts.get_searchType()  + fileStepIndex + "Element_Not_Found" + expected.replace(' ', '_'), screenShotSaveFolder, true);
                                    }
                                }
                            }
                            if (expectedUrl.equals(actualUrl)) {
                                pageHelper.UpdateTestResults("Successful Post Action results for step " + fileStepIndexForLog +  " Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ")", testResults);
                            } else if (!expectedUrl.equals(actualUrl)) {
                                pageHelper.UpdateTestResults("Failed Post Action results for step " + fileStepIndexForLog + " Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ")", testResults);
                            }
                        }
                    }  else if (ts.get_expectedValue().toLowerCase().contains("sendkeys")) {
                        String [] keysToSend = ts.get_expectedValue().split(parameterDelimiter);
                        int timeDelay = 400;
                        if (keysToSend[0].trim().toLowerCase().contains("sendkeys") && keysToSend[0].trim().toLowerCase().contains(" "))
                        {
                            timeDelay = parseInt(keysToSend[0].split(" ")[1]);
                        }
                        for (String item: keysToSend) {
                            if (!item.toLowerCase().contains("sendkeys")) {
                                status = PerformAction(ts.get_searchType(), ts.get_xPath(), "sendkeys" + parameterDelimiter + item, fileStepIndex);
                                DelayCheck(timeDelay, fileStepIndex);
                            }
                        }
                    } else if (ts.get_expectedValue().toLowerCase().contains("wait") && ts.get_searchType().toLowerCase().indexOf("n/a") < 0) {
                        //wait for a speficic element to load
                        WaitForElement(ts, fileStepIndexForLog);
                    }
                    else if (ts.get_searchType().toLowerCase().indexOf("n/a") >= 0) {
                        //perform all non-read actions below that do not use an accessor
                        if (ts.get_expectedValue().toLowerCase().indexOf("navigate") >= 0) {
                            PerformExplicitNavigation(ts, fileStepIndexForLog, dashCount);
                        }
                        else if (ts.get_expectedValue().toLowerCase().indexOf("wait") >= 0 || ts.get_expectedValue().toLowerCase().indexOf("delay") >= 0) {
                            int delayMilliSeconds = 0;
                            if (ts.get_expectedValue().indexOf("╬") > 0) {
                                delayMilliSeconds = parseInt(ts.get_expectedValue().substring(ts.get_expectedValue().lastIndexOf(parameterDelimiter) + 3).trim());
                            } else {
                                delayMilliSeconds = parseInt(ts.get_xPath());
                            }
                            DelayCheck(delayMilliSeconds, fileStepIndex);
                        }
                        else if (ts.get_expectedValue().toLowerCase().indexOf("screenshot") >= 0) {
                            //scheduled screenshot capture action
                            PerformScreenShotCapture(browserUsed + ts.get_expectedValue() + fileStepIndex);
                        }
                        else if (ts.get_expectedValue().toLowerCase().indexOf("url") >= 0) {
                            //check url without navigation
                            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
                            String expectedUrl = null;
                            if (dashCount > 0) {
                                expectedUrl = expectedItems[1].trim();
                                if (dashCount > 1) {
                                    int delayMilliSeconds = parseInt(expectedItems[2].trim());
                                    DelayCheck(delayMilliSeconds, fileStepIndex);
                                }
                            }
                            String actualUrl = GetCurrentPageUrl();
                            assertEquals(expectedUrl, actualUrl);
                            if (expectedUrl.trim().equals(actualUrl.trim())) {
                                pageHelper.UpdateTestResults(PageHelper.ANSI_GREEN + "URL Check successful for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")" + pageHelper.ANSI_RESET, testResults);
                            } else {
                                pageHelper.UpdateTestResults(PageHelper.ANSI_RED + "URL Check unsuccessful for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")" + pageHelper.ANSI_RESET, testResults);
                            }
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("switch to tab")) {
                            if (ts.get_expectedValue().toLowerCase().contains("0")) {
                                SwitchToTab(false, fileStepIndex);
                            }
                            else {
                                SwitchToTab(true, fileStepIndex);
                            }
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("login")) {
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Peforming login for step " + fileStepIndexForLog, testResults);
                            String [] loginItems = ts.get_expectedValue().split(" ");
                            login(ts.get_xPath(), loginItems[1], loginItems[2], fileStepIndexForLog);
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Login complete for step " + fileStepIndexForLog, testResults);
                        }
                    }
                }
            }
            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + pageHelper.ANSI_YELLOW + pageHelper.sectionEndFormatLeft + "End of Test Script" + pageHelper.sectionEndFormatRight + pageHelper.ANSI_RESET, testResults);
        }
        driver.quit();

        //chromedriver does not shut down from memory so you have to kill the process programmatically
        if (this.driver.toString().indexOf("Chrome") >= 0) {
            ShutDownChromeDriver();
        }
    }



    public void login(String url, String email, String password, String fileStepIndexForLog) throws Exception {
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        try {
            driver.switchTo().alert();
            //Selenium-WebDriver Java Code for entering Username & Password as below:
            driver.findElement(By.id("userID")).sendKeys(email);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.switchTo().alert().accept();
            driver.switchTo().defaultContent();
        }
        catch (Exception ex)
        {
            //if the alert doesn't show up, you already have context and are logged in
        }
        //region { Unfinished version }
//        driver.get(url);
//        //Passing the AutoIt Script here
//        //Runtime.getRuntime().exec("D:\\Selenium\\workspace\\AutoItFiles\\ExecutableFiles\\FirefoxBrowser.exe");
//        driver.findElement
//        loginpage.setEmail(email);
//        loginpage.setPassword(password);
//        loginpage.clickOnLogin();
        //endregion
    }


    /* ************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the status of the Get or Post against the
     *      expected value.
     ************************************************************ */
    private void PerformExplicitNavigation(TestSettings ts, String fileStepIndexForLog, int dashCount) throws Exception {
        String navigateUrl = ts.get_xPath();

        String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
        String expectedUrl = null;
        int delayMilliSeconds = 0;
        if (dashCount > 0) {
            expectedUrl = expectedItems[1].trim();

            if (dashCount > 1) {
                delayMilliSeconds = parseInt(expectedItems[2].trim());
            }
            if (dashCount > 2) {
                String dimensions = expectedItems[3].trim();
                int wStart;
                int hStart;
                int width;
                int height;
                if (dimensions.toLowerCase().contains("w=") && dimensions.toLowerCase().contains("h=")) {
                    wStart = dimensions.toLowerCase().indexOf("w=");
                    hStart = dimensions.toLowerCase().indexOf("h=");
                    if (wStart < hStart) {
                        width = parseInt(dimensions.substring(dimensions.indexOf("w=") + 2, dimensions.indexOf("h=")).trim());
                        height = parseInt(dimensions.substring(dimensions.indexOf("h=") + 2, dimensions.length()).trim());
                    }
                    else {
                        height= parseInt(dimensions.substring(dimensions.indexOf("h=") + 2, dimensions.indexOf("w=")).trim());
                        width = parseInt(dimensions.substring(dimensions.indexOf("w=") + 2, dimensions.length()).trim());
                    }
                    pageHelper.UpdateTestResults(pageHelper.indent5 + "Setting browser dimensions to (Width=" + width + " Height=" + height, testResults);
                    pageHelper.SetWindowContentDimensions(driver, width, height);
                }
            }
        }
        this.testPage = navigateUrl;
        //Explicit Navigation Event
        pageHelper.UpdateTestResults( pageHelper.subsectionLeft + "Start Explicit Navigation Event" + pageHelper.subsectionRight, testResults);
        pageHelper.UpdateTestResults(pageHelper.indent8 + "Navigating to " + navigateUrl + " for step " + fileStepIndexForLog);
        String actualUrl = CheckPageUrl(delayMilliSeconds);
        if (expectedUrl != null && expectedUrl.trim().length() > 0) {
            if (ts.get_isCrucial()) {
                assertEquals(expectedUrl, actualUrl);
            }
            else {
                try
                {
                    assertEquals(expectedUrl, actualUrl);
                }
                catch (AssertionError ae) {
                    //do nothing, this just traps the assertion error so that processing can continue
                }
            }
            if (expectedUrl.trim().equals(actualUrl.trim())) {
                pageHelper.UpdateTestResults(pageHelper.indent8 + "Successful Navigation and URL Check for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")", testResults);
            } else {
                pageHelper.UpdateTestResults(pageHelper.indent8 + "Failed Navigation and URL Check for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")", testResults);
            }
        }
        //[ End Explicit Navigation Event
        pageHelper.UpdateTestResults(pageHelper.subsectionLeft + " End Explicit Navigation Event " + pageHelper.subsectionRight, testResults);
    }

    /* ************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the status of the Get or Post against the
     *      expected value.
     ************************************************************ */
    private void CheckGetPostStatus(TestSettings ts, String fileStepIndexForLog) {
        int expectedStatus = 200;
        int actualStatus;

        if (ts.get_expectedValue().contains(parameterDelimiter)) {
            expectedStatus = parseInt(ts.get_expectedValue().split(parameterDelimiter)[1]);
        }
        if (ts.get_expectedValue().toLowerCase().contains("post")) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking Post status of " + ts.get_xPath());
            actualStatus = httpResponseCodeViaPost(ts.get_xPath());
        }
        else if (ts.get_expectedValue().toLowerCase().contains("get")) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking Get status of " + ts.get_xPath());
            actualStatus = httpResponseCodeViaGet(ts.get_xPath());
        }
        else {
            ImproperlyFormedTest(fileStepIndexForLog);
            actualStatus = -1;
        }
        if (actualStatus != -1) {
            if (ts.get_isCrucial()) {
                assertEquals(expectedStatus, actualStatus);
            } else {
                try {
                    assertEquals(expectedStatus, actualStatus);
                } catch (AssertionError ae) {
                    // do not capture screen shot here, if element not found, check methods will capture screen shot
                }
            }
        }
        if (expectedStatus == actualStatus) {
            pageHelper.UpdateTestResults("Successful comparison results at step " + fileStepIndexForLog + " Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ")\r\n", testResults);
        } else if (expectedStatus != actualStatus) {
            pageHelper.UpdateTestResults("Failed comparison results at step " + fileStepIndexForLog + " Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ")\r\n", testResults);
        }
    }


    /* ************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the text of the element against the expected value.
     ************************************************************ */
    private void CheckElementText(String browserUsed, TestSettings ts, String expected, String accessor, String fileStepIndex, String fileStepIndexForLog) throws Exception {
        String actual = "";

        if (ts.get_searchType().toLowerCase().equals("xpath")) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Element type being checked at step " + fileStepIndexForLog + " by xPath: " + accessor, testResults);
            actual = CheckElementWithXPath(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals("cssselector")) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Element type being checked at step " + fileStepIndexForLog + " by CssSelector: " + accessor, testResults);
            actual = CheckElementWithCssSelector(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals("tagname")) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Element type being checked at step " + fileStepIndexForLog + " by TagName: " + accessor, testResults);
            actual = CheckElementWithTagName(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals("classname")) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Element type being checked at step " + fileStepIndexForLog + " by ClassName: " + accessor, testResults);
            actual = CheckElementWithClassName(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals("id")) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Element type being checked at step " + fileStepIndexForLog + " by Id: " + accessor, testResults);
            actual = CheckElementWithId(ts, fileStepIndex);
        }

        if (ts.get_isCrucial()) {
            assertEquals(expected, actual);
        } else {
            try {
                assertEquals(expected, actual);
            } catch (AssertionError ae) {
                // do not capture screen shot here, if element not found, check methods will capture screen shot
            }
        }
        if (expected.equals(actual)) {
            pageHelper.UpdateTestResults("Successful comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", testResults);
        } else if (!expected.equals(actual)) {
            pageHelper.UpdateTestResults("Failed comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", testResults);
            if (screenShotSaveFolder != null && !screenShotSaveFolder.isEmpty()) {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "Assert_Fail", screenShotSaveFolder, false);
            }
        }
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Method reports improperly formatted tests to the
     *      user with the test step so that it can be fixed.
     ************************************************************ */
    private void ImproperlyFormedTest(String fileStepIndexForLog) {
        pageHelper.UpdateTestResults("Imporperly formatted test for step " + fileStepIndexForLog);
    }


    /* ************************************************************
     * DESCRIPTION:
     *      Performs a thread sleep to allow for items to load and
     *      is intended to be used prior to making an assertion
     *      that depends upon some change like a navigation or
     *      new items populating the page.
     ************************************************************ */
    private void DelayCheck(int milliseconds, String fileStepIndex) throws InterruptedException {
        String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
        pageHelper.UpdateTestResults(pageHelper.indent5 + "Sleeping for " + milliseconds + " milliseconds for script " + fileStepIndexForLog, testResults);
        Thread.sleep(milliseconds);
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Waits a maximum of maxTimeInSeconds, which can come from
     *      the test command or default to 10 seconds, for the presence
     *      of the element or page.
     *      Reports if the element or page was present within the
     *      maxTimeInSeconds time limit.
     ************************************************************ */
    private void WaitForElement(TestSettings ts, String fileStepIndexForLog) {
        Boolean pageLoadComplete = false;
        String accessorType = ts.get_searchType().toLowerCase().trim();
        String accessor = ts.get_xPath().trim();
        int maxTimeInSeconds = ts.get_expectedValue().trim().contains(" ╬ ") ? parseInt(ts.get_expectedValue().split(" ╬ ")[1]) : 10;
        if (ts.get_xPath().toLowerCase().trim().contains("page"))
        {
            accessorType = "page";
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Waiting a maximum of " + maxTimeInSeconds + " seconds for page load to complete at step " + fileStepIndexForLog, testResults);
        }
        else {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Waiting a maximum of " + maxTimeInSeconds + " seconds for presence of element " + accessor + " at step " + fileStepIndexForLog, testResults);
        }
        //pageHelper.UpdateTestResults("Waiting for element: accessor = " + accessor);
        WebElement element = null;

        switch (accessorType) {
            case "xpath":
                element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor)));
                break;
            case "id":
                element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.id(accessor)));
                break;
            case "tagname":
                element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.tagName(accessor)));
                break;
            case "classname":
                element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.className(accessor)));
                break;
            case "cssselector":
                element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(accessor)));
                break;
            case "page":  //wait for page load
                if (ts.get_xPath().trim().contains(" ")) {
                    try {
                        pageHelper.NavigateToPage(driver, ts.get_xPath().trim().split(" ")[1]);

                    } catch (Exception ex) {
                        pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed to navigate error: " + ex.getMessage());
                    }
                }
                pageLoadComplete = new WebDriverWait(driver, maxTimeInSeconds).until(
                        webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                break;
            default:  //default to xpath if missing
                element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor)));
                break;
        }

        if (!ts.get_xPath().toLowerCase().trim().contains("page")) {
            if (element != null) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful load of element " + accessor + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndexForLog, testResults);
            }
            else {
                pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed to load element " + accessor + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndexForLog, testResults);
            }
        }
        else {
            if (pageLoadComplete) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful load of page " + GetCurrentPageUrl() + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndexForLog, testResults);
            }
            else {
                pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed to load element " + GetCurrentPageUrl() + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndexForLog, testResults);
            }
        }
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Calls the NavigateToPage method passing the driver
     *      and the destination URL, where a default 10 second
     *      wait happens to allow the page to load
     *      and then returns the current URL
     ************************************************************ */
    public String CheckPageUrl(int delayMilliSeconds) throws Exception {
        //pageHelper.UpdateTestResults("In CheckPageUrl method.  Driver = " + this.driver.toString(), testResults);
        pageHelper.NavigateToPage(this.driver, testPage, delayMilliSeconds);

        return this.driver.getCurrentUrl();
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Returns the URL of the current page.
     ************************************************************ */
    public String GetCurrentPageUrl() {
        return this.driver.getCurrentUrl();
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Returns the status code of the url passed in for a
     *      GET request.
     ************************************************************ */
    public int httpResponseCodeViaGet(String url) {
        return RestAssured.get(url).statusCode();
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Returns the status code of the url passed in for a
     *      POST request.
     ************************************************************ */
    public int httpResponseCodeViaPost(String url) {
        return RestAssured.post(url).statusCode();
    }


    /* ************************************************************
     * DESCRIPTION:
     *      Retrieves all anchor tags in a page and reports the
     *      status of all anchor tags that have an href attribute.
     ************************************************************ */
    public void checkBrokenLinks(String url) {
        driver.get(url);
        int linkCount = 0;

        //Get all the links on the page
        List<WebElement> links = driver.findElements(By.cssSelector("a"));

        String href;
        //region { variables for retrieving attributes currently not in use}
//        String text;
//        String name;
//        String id;
        //endregion

        pageHelper.UpdateTestResults(pageHelper.indent5 + "Retrieved " + links.size() + " anchor tags");
        for(WebElement link : links) {
            href = link.getAttribute("href");
            //region { Other attributes not being used }
//            text = link.getText();
//            name = link.getAttribute("name");
//            id = link.getAttribute("id");
            //pageHelper.UpdateTestResults("Testing...TagName = " + name + " Tag id = " + id);
            //endregion

            if (href != null) {
                linkCount++;
                brokenLinksStatusCode = httpResponseCodeViaGet(href);

                if (200 != brokenLinksStatusCode) {
                    pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed link test " + href + " gave a response code of " + brokenLinksStatusCode + pageHelper.ANSI_RESET);
                } else {
                    pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful link test " + href + " gave a response code of " + brokenLinksStatusCode + pageHelper.ANSI_RESET);
                }
            }
            // region { Removed code for reporting on anchor tags with no href attribute }
//            else {
//                pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "href is null for link with text: " + text  + pageHelper.ANSI_RESET);
//            }
            //endregion
        }
        pageHelper.UpdateTestResults(pageHelper.indent5 + "Discovered " + linkCount + " links amongst " + links.size() + " anchor tags.\r\n");
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Checks all image tags for the presence of the checkType
     *      property passed in (alt, src).
     *      For alt property, it checks that the alt tag is present
     *      and that it contains information and displays a
     *      success or fail message accorgingly.
     *      For src property, it checks that the src tag is present
     *      and that it resolves to a 200 response status.
     ************************************************************ */
    public void checkADAImages(String url, String checkType) {
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        List<WebElement> images = driver.findElements(By.cssSelector("img"));
        String altTag;
        String imgSrc;
        int altTagCount = 0;
        int brokenImageSrcStatusCode = 0;

        pageHelper.UpdateTestResults(pageHelper.indent5 + "Retrieved " + images.size() + " image tags", testResults);
        for(WebElement link : images) {
            altTag = link.getAttribute("alt");
            imgSrc = link.getAttribute("src");
            if (checkType.toLowerCase().trim().equals("alt")) {
                if (altTag != null && !altTag.trim().isEmpty()) {
                    altTagCount++;
                    pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful image alt tag found: " + altTag + " for img src: " + imgSrc + pageHelper.ANSI_RESET, testResults);
                } else {
                    pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed image alt tag missing for img src: " + imgSrc + pageHelper.ANSI_RESET, testResults);
                }
            }else if (checkType.toLowerCase().trim().equals("src")) {
                if (imgSrc != null && !imgSrc.trim().isEmpty()) {
                    altTagCount++;
                    try {
                        brokenImageSrcStatusCode = httpResponseCodeViaGet(imgSrc);
                    }
                    catch (Exception ex) {
                        pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed Error when attempting to validate image src " + imgSrc + " Error: " + ex.getMessage() + pageHelper.ANSI_RESET, testResults);
                    }
                    if (200 != brokenImageSrcStatusCode) {
                        pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + pageHelper.ANSI_RESET, testResults);
                    } else {
                        pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + pageHelper.ANSI_RESET, testResults);
                    }
                }
                else {
                    if (altTag != null) {
                        pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed image src tag missing for image with alt tag: " + altTag + pageHelper.ANSI_RESET, testResults);
                    } else {
                        pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed image src tag missing." + pageHelper.ANSI_RESET, testResults);
                    }
                }
            }
        }
        pageHelper.UpdateTestResults(pageHelper.indent5 + "Discovered " + altTagCount + " image " + checkType.toLowerCase().trim()  + " attributes  amongst " + images.size() + " image tags.\r\n", testResults);
    }

    public void checkColorContrast(String url, String checkElement, String fileStepIndex, boolean isCrucial, String acceptibleRanges)
    {
        //useful links for this functionality
        //https://stackoverflow.com/questions/23220575/how-to-get-element-color-with-selenium
        //https://stackoverflow.com/questions/24669787/how-to-verify-text-color-in-selenium-webdriver
        String color;
        String backColor;
        String color_hex[];
        String backColor_hex[];
        String actual_hex;
        String backActual_hex;
        String cHex;
        String bHex;
        int treeClimb = 0;
        String [] rangeVariables = acceptibleRanges != null ? acceptibleRanges.split(" ") : null;
        int brightnessStandard = 125;
        int contrastStandard = 500;
        List<WebElement> elements = driver.findElements(By.cssSelector(checkElement));
        String overRideMessage = "";

        if (rangeVariables != null) {
            if (rangeVariables[0].toLowerCase().contains("b=")) {
                brightnessStandard = parseInt(rangeVariables[0].split("=")[1]);
                overRideMessage = "Brightness value overridden to: " + brightnessStandard;
                if (rangeVariables.length > 1) {
                    if (rangeVariables[1].toLowerCase().contains("d=")) {
                        contrastStandard = parseInt(rangeVariables[1].split("=")[1]);
                        overRideMessage += "\r\nDifference value overridden to: " + contrastStandard;
                    }
                }
            }
            else if (rangeVariables[0].toLowerCase().contains("d=")) {
                contrastStandard = parseInt(rangeVariables[0].split("=")[1]);
                overRideMessage = "Difference value overridden to: " + contrastStandard;
                if (rangeVariables.length > 1) {
                    if (rangeVariables[1].toLowerCase().contains("b=")) {
                        brightnessStandard = parseInt(rangeVariables[1].split("=")[1]);
                        overRideMessage += "\r\nBrightness value overridden to: " + brightnessStandard;
                    }
                }
            }
            pageHelper.UpdateTestResults(overRideMessage, testResults);
        }



        pageHelper.UpdateTestResults(pageHelper.indent5 + "Retrieved " + elements.size() + " " + checkElement + " tags.", testResults);
        for(WebElement element : elements) {
            treeClimb = 0;
            color = element.getCssValue("color").trim();
            backColor = element.getCssValue("background-color").trim();
//            pageHelper.UpdateTestResults("color = " + color);

//            pageHelper.UpdateTestResults("backColor missing: " + (backColor.isEmpty() || backColor == null));
            color_hex = color.replace("rgba(", "").split(",");
            backColor_hex = backColor.replace("rgba(", "").split(",");
            cHex = Color.fromString(color).asHex();
            bHex = Color.fromString(backColor).asHex();
            WebElement parent = null;
//            pageHelper.UpdateTestResults("color_hex = " + color_hex);

            while (cHex.equals(bHex)) {
                try {
                    //pageHelper.UpdateTestResults("Font color and background-color match!!!!");
                    treeClimb++;
                    if (parent == null) {
                        parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].parentNode;", element);
                    } else {
                        parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].parentNode;", parent);
                    }
                    backColor = parent.getCssValue("background-color").trim();
                    backColor_hex = backColor.replace("rgba(", "").split(",");
                    bHex = Color.fromString(backColor).asHex();
                } catch (Exception ex) {
                    //in case you walk the entire tree and no difference is found
                    break;
                }
            }
            actual_hex = String.format("#%02x%02x%02x", Integer.parseInt(color_hex[0].trim()), Integer.parseInt(color_hex[1].trim()), Integer.parseInt(color_hex[2].trim()));
            backActual_hex = String.format("#%02x%02x%02x", Integer.parseInt(backColor_hex[0].trim()), Integer.parseInt(backColor_hex[1].trim()), Integer.parseInt(backColor_hex[2].trim()));

            // reference: https://www.w3.org/TR/AERT/#color-contrast
            //color brightness The rage for color brightness difference is 125.
            //brightness = (299*R + 587*G + 114*B) / 1000
            String [] foreColors = color.substring(color.indexOf("(") + 1, color.indexOf(")")).split(",");
            String [] backColors = backColor.substring(backColor.indexOf("(") + 1, backColor.indexOf(")")).split(",");
            double foreColorBrightness = ((parseInt(foreColors[0].trim()) * 299) + (parseInt(foreColors[1].trim()) * 587) + (parseInt(foreColors[2].trim()) * 114)) / 1000;
            double backColorBrightness = ((parseInt(backColors[0].trim()) * 299) + (parseInt(backColors[1].trim()) * 587) + (parseInt(backColors[2].trim()) * 114)) / 1000;
            double brightness;
            double contrast;

            //color difference The range for color difference is 500.
            //(maximum (Red value 1, Red value 2) - minimum (Red value 1, Red value 2)) + (maximum (Green value 1, Green value 2) - minimum (Green value 1, Green value 2)) + (maximum (Blue value 1, Blue value 2) - minimum (Blue value 1, Blue value 2))
            int maxRed = parseInt(foreColors[0].trim()) > (parseInt(backColors[0].trim())) ? parseInt(foreColors[0].trim()) : (parseInt(backColors[0].trim()));
            int minRed= parseInt(foreColors[0].trim()) > (parseInt(backColors[0].trim())) ? (parseInt(backColors[0].trim())) :  parseInt(foreColors[0].trim());
            int maxGreen = parseInt(foreColors[1].trim()) > (parseInt(backColors[1].trim())) ? parseInt(foreColors[1].trim()) : (parseInt(backColors[1].trim()));
            int minGreen = parseInt(foreColors[1].trim()) > (parseInt(backColors[1].trim())) ? (parseInt(backColors[1].trim())) :  parseInt(foreColors[1].trim());
            int maxBlue = parseInt(foreColors[2].trim()) > (parseInt(backColors[2].trim())) ? parseInt(foreColors[2].trim()) : (parseInt(backColors[2].trim()));
            int minBlue = parseInt(foreColors[2].trim()) > (parseInt(backColors[2].trim())) ? (parseInt(backColors[2].trim())) :  parseInt(foreColors[2].trim());

            contrast = (maxRed - minRed) + (maxGreen - minGreen) + (maxBlue - minBlue);



            if (foreColorBrightness > backColorBrightness) {
                brightness = foreColorBrightness - backColorBrightness;
            } else {
                brightness = backColorBrightness - foreColorBrightness;
            }

            String backColorAncestor = treeClimb > 0 ? "^" + treeClimb : "";

            //if (brightness > 123 && ) {
            if (brightness >= brightnessStandard && contrast >= contrastStandard) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Good brightness and Good contrast forecolor(" + color + ") Fore-Color Brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + " Brightness Difference: " + brightness + " Color Difference: " + contrast + pageHelper.ANSI_RESET);
            } else if (brightness >= brightnessStandard && contrast < contrastStandard) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_WHITE_BACKGROUND + pageHelper.ANSI_RED + "Good brightness Warning contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + pageHelper.ANSI_RESET);
            } else if (brightness < brightnessStandard && contrast >= contrastStandard) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_WHITE_BACKGROUND + pageHelper.ANSI_RED + "Warning brightness and Good contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + pageHelper.ANSI_RESET);
            } else {
                pageHelper.UpdateTestResults( pageHelper.ANSI_RED + "Warning brightness and Warning contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast +   pageHelper.ANSI_RESET);
            }


            //region { testing }
//            String colorString = element.getAttribute("class");
//            String[] arrColor = colorString.split("#");
            //assertTrue(arrColor[1].equals("008000"));
            //end testing
//            pageHelper.UpdateTestResults(pageHelper.indent5 + element + " font color = " + color + "(" + actual_hex + ") and background color = " + backColor + "(" + backActual_hex + ")");
//            pageHelper.UpdateTestResults(pageHelper.indent5 + " font color = " + color + "(" + actual_hex + ") and background color = " + backColor + "(" + backActual_hex + ")");
//            pageHelper.UpdateTestResults("cHex = " + cHex + " bHex = " + bHex);
//            pageHelper.UpdateTestResults("cHex = " + cHex + " bHex = " + bHex + " arrColor[1] = " + (colorString.isEmpty() || arrColor.length < 2 ? "n/a" : arrColor[1]));
//            for (int x=0;x<arrColor.length;x++) {
//                pageHelper.UpdateTestResults( "Style Class settings arrColor[" + x + "] = " + arrColor[x]);
//            }
            //endregion
        }

    }


    /* ************************************************************
     * DESCRIPTION:
     *      Performs a count of all checkElement tags for the url
     *      passed in or the current url, if not passed in, and
     *      compares that count to the expectedCount passed in.
     *      If this test is marked as crucial, all testing stops
     *      if the counts do not match.
     *      If this test is not marked as crucial, testing
     *      continues and the status is reported.
     ************************************************************ */
    private void checkElementCount(String url, String checkElement, int expectedCount, String fileStepIndex, boolean isCrucial) {
        int actualCount;
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        List<WebElement> elements = driver.findElements(By.cssSelector(checkElement));
        actualCount = elements.size();

        if (isCrucial) {
            assertEquals(expectedCount, actualCount);
        }
        else {
            try {
                assertEquals(expectedCount, actualCount);
            }
            catch (AssertionError ae) {
                //do nothing, just trap the error so that testing can continue
            }

            if (actualCount != expectedCount) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed count of " + checkElement + " tags do not match.  Expected: " + expectedCount + "  Actual: " + actualCount + pageHelper.ANSI_RESET);
            } else {
                pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful count of " + checkElement + " tags match.  Expected: " + expectedCount + "  Actual: " + actualCount + pageHelper.ANSI_RESET);
            }
        }
    }




    /* ************************************************************
     * DESCRIPTION:
     *      Returns the text of an element using its xPath accessor.
     ************************************************************ */
   // public String CheckElementWithXPath(String accessor, TestSettings ts, String fileStepIndex) throws Exception {
    public String CheckElementWithXPath(TestSettings ts, String fileStepIndex) throws Exception {
        String actualValue = null;
        String accessor = ts.get_xPath();

        try {
            //pageHelper.UpdateTestResults("CheckElementWithXPath iframeResult in try block " + ts.get_expectedValue().toLowerCase());
            String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
            //actualValue = this.driver.findElement(By.xpath(accessor)).getText();
            String typeOfElement = this.driver.findElement(By.xpath(accessor)).getAttribute("type");
            if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.xpath(accessor)));
                //wait until element is present commented out and functionality pushed to separate stand-alone action
                //Select select = new Select((new WebDriverWait(driver,10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor))));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            }
            else {
                actualValue = this.driver.findElement(By.xpath(accessor)).getText();
                //wait until element is present commented out and functionality pushed to separate  stand-alone action
                //actualValue = (new WebDriverWait(driver,10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor))).getText();
            }
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
        } catch (Exception e) {
            String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "xPath_Element_Not_Found", configurationFolder, true);
            } else {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "xPath_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        }
        finally {
            return actualValue;
        }
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Returns the text of an element using its CssSelector accessor.
     ************************************************************ */
    //public String CheckElementWithCssSelector(String accessor, String fileStepIndex) throws Exception {
    public String CheckElementWithCssSelector(TestSettings ts, String fileStepIndex) throws Exception {
        String accessor = ts.get_xPath();
        String actualValue = null;

        try {
            String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
            //actualValue = this.driver.findElement(By.cssSelector(accessor)).getText();
            String typeOfElement = this.driver.findElement(By.cssSelector(accessor)).getAttribute("type");
            if (typeOfElement != null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.cssSelector(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            } else {
                actualValue = this.driver.findElement(By.cssSelector(accessor)).getText();
            }
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by CssSelector: " + accessor + " for script " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
        } catch (Exception e) {
            String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "CssSelector_Element_Not_Found", configurationFolder, true);
            } else {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "CssSelector_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        }
        finally {
            return actualValue;
        }
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Returns the text of an element using its TagName accessor.
     ************************************************************ */
    //public String CheckElementWithTagName(String accessor, String fileStepIndex) throws Exception {
    public String CheckElementWithTagName(TestSettings ts, String fileStepIndex) throws Exception {
        String accessor = ts.get_xPath();
        String actualValue = null;

        try {
            //actualValue = this.driver.findElement(By.tagName(accessor)).getText();
            String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
            String typeOfElement = this.driver.findElement(By.tagName(accessor)).getAttribute("type");
            if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.tagName(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            }
            else {
                actualValue = this.driver.findElement(By.tagName(accessor)).getText();
            }
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by TagName: " + ElementTypeLookup(accessor) + " for script. " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
        } catch (Exception e) {
            String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "TagName_Element_Not_Found", configurationFolder, true);
            }
            else {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "TagName_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        }
        finally {
            return actualValue;
        }
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Returns the text of an element using its ClassName accessor.
     ************************************************************ */
    //private String CheckElementWithClassName(String accessor, String fileStepIndex) throws Exception {
    private String CheckElementWithClassName(TestSettings ts, String fileStepIndex) throws Exception {
        String accessor = ts.get_xPath();
        String actualValue = null;

        try {
            //actualValue = this.driver.findElement(By.className(accessor)).getText();
            String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
            String typeOfElement = this.driver.findElement(By.className(accessor)).getAttribute("type");
            if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.className(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            }
            else {
                actualValue = this.driver.findElement(By.className(accessor)).getText();
            }
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by ClassName: " + accessor + " for script. " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
        } catch (Exception e) {
            String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "ClassName_Element_Not_Found", configurationFolder, true);
            }
            else {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "ClassName_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        }
        finally {
            return actualValue;
        }
    }

    /* ************************************************************
     * DESCRIPTION:
     *      Returns the text of an element using its Id accessor.
     ************************************************************ */
   //public String CheckElementWithId(String accessor, String fileStepIndex)  throws Exception {
    public String CheckElementWithId(TestSettings ts, String fileStepIndex)  throws Exception {
        String accessor = ts.get_xPath();
        //boolean revertToParent = false;
        String actualValue = null;

        try {
           String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
           String typeOfElement = this.driver.findElement(By.id(accessor)).getAttribute("type");
           if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
               Select select = new Select(this.driver.findElement(By.id(accessor)));
               WebElement option = select.getFirstSelectedOption();
               actualValue = option.getText();
           }
           else {
               actualValue = this.driver.findElement(By.id(accessor)).getText();
           }
           pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by ID: " + accessor + " for script." + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
       } catch (Exception e) {
           String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
           if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
               pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "Id_Element_Not_Found", configurationFolder, true);
           }
           else {
               pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "Id_Element_Not_Found", screenShotSaveFolder, true);
           }
           actualValue = null;
       }
        finally {
            return actualValue;
        }
   }

    //region { Perform non-text retrieval actions }
    /* ************************************************************
     * DESCRIPTION:
     *      Performs non-text retrieval actions such as clicking,
     *      navigating, waiting, taking screen shots etc...
     ************************************************************ */
    public Boolean PerformAction(String accesssorType, String accessor, String value, String fileStepIndex) {
        Boolean status = false;
        final String click = "click";
        final String sendKeys = "sendkeys";
        final String rightClick = "right click";
        final String keys = "keys.";
        final String doubleClick = "doubleclick";

        //if this is a click event, click it
        if (value.toLowerCase().contains(click) && !value.toLowerCase().contains(sendKeys)) {
            try {
                if (accesssorType.toLowerCase().equals("xpath")) {
                    if (!value.toLowerCase().contains(rightClick)) {
                        if (!value.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.xpath(accessor)).click();
                        }
                        else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.xpath(accessor))).build().perform();
                        }
                    }
                    else {  //right click element
                        Actions action = new Actions(driver);
                        if (!value.toLowerCase().contains(keys)) {
                            action.contextClick(driver.findElement(By.xpath(accessor))).build().perform();
                        }
                        else {
                            action.contextClick(driver.findElement(By.xpath(accessor))).build().perform();
                            SelectFromContextMenu(value, fileStepIndex);
                        }
                    }
                }
                else if (accesssorType.toLowerCase().equals("id")) {
                    if (!value.toLowerCase().contains(rightClick)) {
                        if (!value.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.id(accessor)).click();
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.id(accessor))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (!value.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.id(accessor))).build().perform();
                        }
                        else {
                            action.contextClick(driver.findElement(By.id(accessor))).build().perform();
                            SelectFromContextMenu(value, fileStepIndex);
                        }
                    }
                }
                else if (accesssorType.toLowerCase().equals("classname")) {
                    if (!value.toLowerCase().contains(rightClick)) {
                        if (!value.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.className(accessor)).click();
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.className(accessor))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (!value.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.className(accessor))).build().perform();
                        }
                        else {
                            action.contextClick(driver.findElement(By.className(accessor))).build().perform();
                            SelectFromContextMenu(value, fileStepIndex);
                        }
                    }
                }
                else if (accesssorType.toLowerCase().equals("cssselector")) {
                    if (!value.toLowerCase().contains(rightClick)) {
                        if (!value.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.cssSelector(accessor)).click();
                        }
                        else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.cssSelector(accessor))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (!value.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.cssSelector(accessor))).build().perform();
                        }
                        else {
                            action.contextClick(driver.findElement(By.cssSelector(accessor))).build().perform();
                            SelectFromContextMenu(value, fileStepIndex);
                        }
                    }
                }
                else if (accesssorType.toLowerCase().equals("tagname")) {
                    if (!value.toLowerCase().contains(rightClick)) {
                        if (!value.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.tagName(accessor)).click();
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.tagName(accessor))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (!value.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.tagName(accessor))).build().perform();
                        }
                        else {
                            action.contextClick(driver.findElement(By.tagName(accessor))).build().perform();
                            SelectFromContextMenu(value, fileStepIndex);
                        }
                    }
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        } else if (value.toLowerCase().indexOf("screenshot") >= 0) {
            try {
                PerformScreenShotCapture(value);
                status = true;
            }catch (Exception e) {
                status = false;
            }
        } else {  //if it is not a click, send keys or screenshot
            try {
                //use sendkeys as the command when sending keywords to a form
                if (value.contains(sendKeys)) {
                    String [] values = value.split(parameterDelimiter);
                    value = values.length > 0 ? values[1].trim() : "";
                }
                if (value.contains(keys) || value.toLowerCase().contains(keys)) {
                    if (accesssorType.toLowerCase().equals("xpath")) {
                        this.driver.findElement(By.xpath(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals("id")) {
                        this.driver.findElement(By.id(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals("classname")) {
                        this.driver.findElement(By.className(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals("cssselector")) {
                        this.driver.findElement(By.cssSelector(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals("tagname")) {
                        this.driver.findElement(By.tagName(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                } else {
                    //pageHelper.UpdateTestResults("Not sending reserved Key strokes.");
                    if (accesssorType.toLowerCase().equals("xpath")) {
                        this.driver.findElement(By.xpath(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals("id")) {
                        this.driver.findElement(By.id(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals("classname")) {
                        this.driver.findElement(By.className(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals("cssselector")) {
                        this.driver.findElement(By.cssSelector(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals("tagname")) {
                        this.driver.findElement(By.tagName(accessor)).sendKeys(value);
                    }
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        }
        return status;
    }

    //SelectFromContextMenu(value, fileStepIndex);
    private void SelectFromContextMenu(String value, String fileStepIndex) throws AWTException, InterruptedException {
        String [] additionalCommands = value.split(parameterDelimiter);
        int downCount = 0;
        int upCount = 0;
        int leftCount = 0;
        int rightCount = 0;
        String arrowDown = "Keys.Arrow_Down";
        boolean switchToTab = false;
        for (String item : additionalCommands) {
            if (item.toLowerCase().trim().contains("keys.arrow_down"))
            {
                downCount++;
            }
            if (item.toLowerCase().trim().contains("keys.arrow_up"))
            {
                upCount++;
            }
            if (item.toLowerCase().trim().contains("keys.arrow_left"))
            {
                leftCount++;
            }
            if (item.toLowerCase().trim().contains("keys.arrow_right"))
            {
                rightCount++;
            }
            if (item.toLowerCase().trim().contains("switch to tab")) {
                switchToTab = true;
            }
        }

        Robot robot = new Robot();

        for (int x=0;x<downCount;x++) {
            robot.keyPress(KeyEvent.VK_DOWN);
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing Key down action!", testResults);
        }
        for (int x=0;x<upCount;x++) {
            robot.keyPress(KeyEvent.VK_UP);
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing Key up action!", testResults);
        }
        for (int x=0;x<leftCount;x++) {
            robot.keyPress(KeyEvent.VK_LEFT);
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing Key left action!", testResults);
        }
        for (int x=0;x<rightCount;x++) {
            robot.keyPress(KeyEvent.VK_RIGHT);
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing Key right action!", testResults);
        }
        //it is assumed that you will always do this once you select the proper context menu item
        robot.keyPress(KeyEvent.VK_ENTER);

          //need to remove this and add a check for it in the test step
        if (switchToTab) {
            DelayCheck(3000, fileStepIndex);
            SwitchToTab(true, fileStepIndex);
            //DelayCheck(7000, fileStepIndex);
            //SwitchToTab(false, fileStepIndex);
            //SwitchBackToMainTab(fileStepIndex);
        }
    }

    //private void SwitchToNewTab(boolean isChild, String fileStepIndex) {
    private void SwitchToTab(boolean isChild, String fileStepIndex) {
        int tab = 0;
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        tab = isChild ? 1 : 0;
        //String handleName = tabs.get(1);
        String handleName = tabs.get(tab);
        driver.switchTo().window(handleName);
        System.setProperty("current.window.handle", handleName);
        pageHelper.UpdateTestResults(pageHelper.indent5 + "Switched to New tab with url = " + driver.getCurrentUrl(), testResults);
    }



    /* ************************************************************
     * DESCRIPTION:
     *      Performs a screen shot capture by calling the
     *      screen shot capture method in the pageHelper class.
     ************************************************************ */
    private void PerformScreenShotCapture(String value) {
        String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
        //pageHelper.captureScreenShot(driver, browserUsed + "ScreenShotAction_" + value, screenShotSaveFolder);
        pageHelper.captureScreenShot(driver, value, screenShotSaveFolder, false);
    }
    //endregion

   //region {Lookup Methods}
    private String ElementTypeLookup(String xPath) {
        //NOTE: When checking string equality in Java you must use the "".Equals("") method.
        // Using the == operator checks the memory address not the value
        String elementTag = xPath.substring(xPath.lastIndexOf("/") + 1).trim();

        if (elementTag.toLowerCase().startsWith("a") && (elementTag.length() == 1 || elementTag.toLowerCase().indexOf("[") > 1)) {
            return "Anchor";
        }
        if (elementTag.toLowerCase().startsWith("h") && (elementTag.length() == 2 || elementTag.toLowerCase().indexOf("[") > 1)) {
            return "Heading";
        }
        if (elementTag.toLowerCase().startsWith("li") && elementTag.length() >= 2) {
            return "List Item";
        }
        if (elementTag.toLowerCase().startsWith("span") && elementTag.length() >= 4) {
            return "Span";
        }
        if (elementTag.toLowerCase().startsWith("div") && elementTag.length() >= 3) {
            return "Div";
        }
        if (elementTag.toLowerCase().startsWith("p") && elementTag.length() >= 1) {
            return "Paragraph";
        }
        if (elementTag.toLowerCase().startsWith("img") && elementTag.length() >= 3) {
            return "Image";
        }
        if (elementTag.toLowerCase().startsWith("select") || elementTag.toLowerCase().equals("select")) {
            return "Select";
        }
        else {
            pageHelper.UpdateTestResults(pageHelper.indent5 + "Failed to find element type for elementTag: (" + elementTag + ") Length = " + elementTag.length(), testResults);
        }
        return "Indeterminate";
    }

    private CharSequence GetKeyValue(String value, String fileStepIndex) {
        value = value.toLowerCase().trim();
        String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
        pageHelper.UpdateTestResults(pageHelper.indent5 + "Replacing (" + value + ") with corresponding Key value keyword for step " + fileStepIndexForLog);

        if (value.equals("keys.enter"))
        {
            return Keys.ENTER;
        }
        else if (value.equals("keys.return"))
        {
            return Keys.RETURN;
        }
        else if (value.equals("keys.arrow_down"))
        {
            return Keys.ARROW_DOWN;
        }
        else if (value.equals("keys.arrow_up"))
        {
            return Keys.ARROW_UP;
        }
        else if (value.equals("keys.arrow_left"))
        {
            return Keys.ARROW_LEFT;
        }
        else if (value.equals("keys.arrow_right"))
        {
            return Keys.ARROW_RIGHT;
        }
        else if (value.equals("keys.back_space"))
        {
            return Keys.BACK_SPACE;
        }
        else if (value.equals("keys.cancel"))
        {
            return Keys.CANCEL;
        }
        else if (value.equals("keys.escape"))
        {
            return Keys.ESCAPE;
        }
        else if (value.equals("keys.tab"))
        {
            return Keys.TAB;
        }
        else
        {
            pageHelper.UpdateTestResults(PageHelper.ANSI_RED + "Key: " + value + fileStepIndex + " not mapped!" + pageHelper.ANSI_RESET, testResults);
        }
        return value;
    }
    //endregion

    private void ShutDownChromeDriver() throws Exception{
        try {
            // Execute command
            //String command = "cmd /c start cmd.exe";
            String command = "taskkill /im chromedriver.exe /f";
            Process child = Runtime.getRuntime().exec(command);

            //region { Unnecessary code }
            // Get output stream to write from it
            /*OutputStream out = child.getOutputStream();

            out.write("cd C:/ /r/n".getBytes());

            out.flush();
            out.write("taskkill /im chromedriver.exe /f /r/n".getBytes());

            out.write("exit /r/n".getBytes());
            out.close();*/
            //endregion
        } catch (IOException e) {
            pageHelper.UpdateTestResults("The following error occurred while trying to shut down ChromeDriver: " + e.getMessage(), testResults);
        }
    }



    //region { Refactored Methods soon to be removed }
     /*
    //alternate way of opening a new tab and navigating to it then switching to that tab
    public String openNewTab(String url) {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("window.parent = window.open('parent');");
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        String handleName = tabs.get(1);
        driver.switchTo().window(handleName);
        System.setProperty("current.window.handle", handleName);
        driver.get(url);
        return handleName;
    }
    */

/*
    private void SwitchBackToMainTab(String fileStepIndex) {
        pageHelper.UpdateTestResults(pageHelper.indent5 + "Switching to the original tab", testResults);
        //new Actions(driver).sendKeys(driver.findElement(By.tagName("html")), Keys.CONTROL).sendKeys(driver.findElement(By.tagName("html")),Keys.NUMPAD1).build().perform();
        //new Actions(driver).sendKeys(Keys.CONTROL + "\t");
        pageHelper.UpdateTestResults("Switched back to Main tab with url = " + driver.getCurrentUrl());
    }
*/
    /*public Boolean PerformXPathAction(String accessor, String value) {
        Boolean status = false;
        //if this is a click event, click it
        if (value.toLowerCase().indexOf("click") >= 0) {
            try {
                this.driver.findElement(By.xpath(accessor)).click();
                status = true;
            } catch (Exception e) {
                status = false;
            }
            //return status;
        } else if (value.toLowerCase().indexOf("screenshot") >= 0) {
            try {
                PerformScreenShotCapture(value);
                status = true;
            }catch (Exception e) {
                status = false;
            }
        } else {  //if it is not a click, send keys
            try {
                if (value.contains("Keys.")) {
                    this.driver.findElement(By.xpath(accessor)).sendKeys(GetKeyValue(value));
                } else {
                    this.driver.findElement(By.xpath(accessor)).sendKeys(value);
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        }
        return status;
    }

    public Boolean PerformClassAction(String accessor, String value) {
        Boolean status = false;
        //if this is a click event, click it
        if (value.toLowerCase().indexOf("click") >= 0) {
            try {
                this.driver.findElement(By.className(accessor)).click();
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
            //return status;
        }
        else if (value.toLowerCase().indexOf("screenshot") >= 0) {
            try {
                PerformScreenShotCapture(value);
                status = true;
            }catch (Exception e) {
                status = false;
            }
        }
        else {  //if it is not a click, send keys
            try {
                //
                if (value.contains("Keys.")) {
                    this.driver.findElement(By.className(accessor)).sendKeys(GetKeyValue(value));
                } else {
                    this.driver.findElement(By.className(accessor)).sendKeys(value);
                }
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
        }
        return status;
    }

    public Boolean PerformTagNameAction(String accessor, String value) {
        Boolean status = false;
        //if this is a click event, click it
        if (value.toLowerCase().indexOf("click") >= 0) {
            try {
                this.driver.findElement(By.tagName(accessor)).click();
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
        }
        else if (value.toLowerCase().indexOf("screenshot") >= 0) {
            try {
                PerformScreenShotCapture(value);
                status = true;
            }catch (Exception e) {
                status = false;
            }
        }
        else {  //if it is not a click, send keys
            try {
                //
                if (value.contains("Keys.")) {
                    this.driver.findElement(By.tagName(accessor)).sendKeys(GetKeyValue(value));
                } else {
                    this.driver.findElement(By.tagName(accessor)).sendKeys(value);
                }
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
        }
        return status;
    }

    public Boolean PerformCssSelectorAction(String accessor, String value) {
        Boolean status = false;
        //if this is a click event, click it
        if (value.toLowerCase().indexOf("click") >= 0) {
            try {
                this.driver.findElement(By.cssSelector(accessor)).click();
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
        } else if (value.toLowerCase().indexOf("screenshot") >= 0) {
            try {
                PerformScreenShotCapture(value);
                status = true;
            }catch (Exception e) {
                status = false;
            }
        }
        else {  //if it is not a click, send keys
            try {
                if (value.contains("Keys.")) {
                    this.driver.findElement(By.cssSelector(accessor)).sendKeys(GetKeyValue(value));
                } else {
                    this.driver.findElement(By.cssSelector(accessor)).sendKeys(value);
                }
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
        }
        return status;
    }

    public Boolean PerformIdAction(String accessor, String value) {
        Boolean status = false;
        //if this is a click event, click it
        if (value.toLowerCase().indexOf("click") >= 0) {
            try {
                this.driver.findElement(By.id(accessor)).click();
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
        }
        else if (value.toLowerCase().indexOf("screenshot") >= 0) {
            try {
                PerformScreenShotCapture(value);
                status = true;
            }catch (Exception e) {
                status = false;
            }
        }
        else {  //if it is not a click, send keys
            try {
                //
                if (value.contains("Keys.")) {
                    this.driver.findElement(By.id(accessor)).sendKeys(GetKeyValue(value));
                } else {
                    this.driver.findElement(By.id(accessor)).sendKeys(value);
                }
                status = true;
            }
            catch(Exception e) {
                status = false;
            }
        }
        return status;
    }
    */
    //endregion



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
    */

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Adds a message to the List<String> testResults and writes out the current status to
     *  the log file and then to the screen.
     *  (testResults is not necessary and may be removed or you can write all test
     *  results out when the program ends in the destructor.)
     **************************************************************************** */
    /*private void UpdateTestResults(String testMessage) {
        testResults.add(testMessage);
        try {
            pageHelper.WriteToFile(logFileName, testMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (testMessage.indexOf("Successful") >= 0) {
            // System.out.println((char)27 + "[31m" + testMessage);
            System.out.println(pageHelper.ANSI_GREEN + testMessage + pageHelper.ANSI_RESET);
        }
        else if (testMessage.indexOf("Failed") >= 0) {
            System.out.println(pageHelper.ANSI_RED + testMessage + pageHelper.ANSI_RESET);
        }
        else if (testMessage.indexOf("Navigation") >= 0) {
            System.out.println(pageHelper.ANSI_BLUE + testMessage + pageHelper.ANSI_RESET);
        }
        else {
            System.out.println(testMessage);
        }
    }*/
    //endregion
}
