import com.mongodb.*;
import com.mongodb.client.*;
import com.sun.javafx.geom.Edge;
import org.apache.commons.lang3.StringUtils;
import org.bson.BSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;
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

//import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.ExceptionListener;
import java.io.File;
import java.io.IOException;
//import java.security.Timestamp;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.Integer.parseInt;
import static java.util.stream.LongStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
     *      Update the following items if necessary: (The following items point to file paths on your computer)
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
     *      1.  Need to separate debug output from required output and make
     *          it configurable whether extra information is output. -
     *          (Kinda implemented...only logged if testResults is passed..overloaded method.)
     *      2.  Look at Login.
     *          Not working for Alvaro but works for me.
     *
     ╚═══════════════════════════════════════════════════════════════════════════════╝ */
    //endregion

    //region { constants }
    private final String parameterDelimiter = " ╬ ";  //made parameter delimiter a constant
    private final String uidReplacementChars = "**_uid_**"; //made the timestamp/unique id replacement string a constant
    private final String persistStringCheckValue = "persiststring";
    private final String persistedStringCheckValue = "persistedstring";
    private final String xpathCheckValue = "xpath";
    private final String cssSelectorCheckValue = "cssselector";
    private final String tagNameCheckValue = "tagname";
    private final String idCheckValue = "id";
    private final String classNameCheckValue = "classname";
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
//    private String chromeDriverPath = "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe";
//    private String fireFoxDriverPath = "/GeckoDriver/geckodriver-v0.23.0-win64/geckodriver.exe";
//    private String phantomJsDriverPath = "/Gary/PhantomJS/phantomjs-2.1.1-windows/bin/phantomjs.exe";
//    private String internetExplorerDriverPath = "/Users/gjackson/Downloads/IEDriverServer_x64_3.11.1/IEDriverServer.exe";
//    private String edgeDriverPath = "/Users/gjackson/Downloads/EdgeDriver/MicrosoftWebDriver.exe";
    private String chromeDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/chromedriver.exe";
    private String fireFoxDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/geckodriver.exe";
    private String phantomJsDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/phantomjs.exe";
    private String internetExplorerDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/IEDriverServer.exe";
    private String edgeDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/MicrosoftWebDriver.exe";

    private boolean _executedFromMain = false;
    private int brokenLinksStatusCode;
//    private String mongoDbConnectionString;
    private MongoClient mongoClient = null;
    private MongoClientURI mongoClientUri = null;
    private String persistedString = null;
    private String uniqueId = null;


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
                //public static final String ANSI_BOLD = "\u001B[1m";
                code = Integer.toString((i * 16 + j));
                pageHelper.UpdateTestResults(  "Code u001b[" + code + "m = " + "\u001b[" + code + "m");
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
//        pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_GREEN_BACKGROUND + pageHelper.ANSI_BLUE + pageHelper.ANSI_BOLD + pageHelper.sectionStartFormatLeft + "Beginning Configuration" + pageHelper.sectionStartFormatRight + pageHelper.ANSI_RESET, testResults);
        pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_GREEN_BACKGROUND + pageHelper.ANSI_BLUE + pageHelper.ANSI_BOLD + "╔" + pageHelper.PrePostPad("[ Beginning Configuration ]", "═", 9, 157) + "╗" + pageHelper.ANSI_RESET);
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
//            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_GREEN_BACKGROUND + pageHelper.ANSI_BLUE + pageHelper.ANSI_BOLD + pageHelper.sectionEndFormatLeft + "Ending Configuration" + pageHelper.sectionEndFormatRight + pageHelper.ANSI_RESET, testResults);
            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_GREEN_BACKGROUND + pageHelper.ANSI_BLUE + pageHelper.ANSI_BOLD + "╚" + pageHelper.PrePostPad("[ Ending Configuration ]", "═", 9, 157) + "╝" + pageHelper.ANSI_RESET);
        }
    }



    public HomePage() throws Exception {
//        ColorUtility();
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
        pageHelper.UpdateTestResults(pageHelper.ANSI_YELLOW + "Executed From Main or as JUnit Test = " + pageHelper.ANSI_RESET + (is_executedFromMain() ? "Standalone App" : "JUnit Test"));
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
            options.addArguments("--dns-prefetch-disable");
            //options.addArguments("acceptInsecureCerts=true");
            options.setAcceptInsecureCerts(true);
            options.setPageLoadStrategy(PageLoadStrategy.NONE);
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

        uniqueId = GetUniqueIdentifier();
        //boolean isError = false;

        for (int fileIndex = 0; fileIndex < testFiles.size(); fileIndex++) {
            testFileName = testFiles.get(fileIndex);
            //Start - reset this for each test file
            testSettings = new ArrayList<>();
            testSettings = pageHelper.ReadTestSettingsFile(testSettings, testFileName);
            persistedString = null;
            //End - reset this for each test file
            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + pageHelper.ANSI_YELLOW + pageHelper.sectionLeftDown + pageHelper.PrePostPad("[ Running Test Script ]", "═", 9, 157) + pageHelper.sectionRightDown + pageHelper.ANSI_RESET);
//            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + pageHelper.ANSI_YELLOW + pageHelper.sectionLeftDown + pageHelper.PrePostPad("[" + pageHelper.ANSI_RESET + pageHelper.ANSI_YELLOW + " Running Test Script " + pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + "]", "═", 9, 157) + pageHelper.sectionRightDown + pageHelper.ANSI_RESET);
            for (int x = startIndex; x < testSettings.size(); x++) {
                if (revertToParent) {
                    driver.switchTo().defaultContent();
                    pageHelper.UpdateTestResults(pageHelper.ANSI_CYAN + pageHelper.iFrameSectionBottomLeft + pageHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + pageHelper.iFrameSectionBottomRight + pageHelper.ANSI_RESET);
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
                    String [] expectedItems = expected.split(parameterDelimiter);
                    String frameName = expectedItems[0].substring(expectedItems[0].indexOf("[") + 1, expectedItems[0].indexOf("]"));
                    pageHelper.UpdateTestResults(pageHelper.ANSI_CYAN + pageHelper.iFrameSectionTopLeft + pageHelper.PrePostPad("[ Switching to iFrame: " + frameName + " for step " + fileStepIndexForLog + " ]", "═", 9, 157) + pageHelper.iFrameSectionTopRight + pageHelper.ANSI_RESET);
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
                            checkElementCount(ts.get_xPath(), checkItems[2].trim(), expectedCount, fileStepIndexForLog, ts.get_isCrucial());
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("contrast")) {
                            //checkColorContrast - the method is not fully implemented.  Need to figure out the color ratio formula.  Notes in method.
                            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
                            String [] checkItems = expectedItems[0].split(" ");
                            //String [] acceptibleRanges = expectedItems.length > 1 ? expectedItems[1].split(" ") : null;
                            String acceptibleRanges = expectedItems.length > 1 ? expectedItems[1] : null;
                            String page = ts.get_xPath().toLowerCase().equals("n/a") ? driver.getCurrentUrl() : ts.get_xPath().trim();
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking color contrast of " + checkItems[2] + " on page " + page);
                            checkColorContrast(ts.get_xPath(), checkItems[2].trim(), fileStepIndexForLog, ts.get_isCrucial(), acceptibleRanges);
                        } //perform a database query
                        else if (ts.get_xPath().toLowerCase().contains("query")) {
                            pageHelper.UpdateTestResults("Found query....");
                            if (ts.get_xPath().toLowerCase().contains("mongo")) {
                                pageHelper.UpdateTestResults("Found query then mongo....");
                                //make sure that this connection has been established
                                if (mongoClient != null) {
                                    pageHelper.UpdateTestResults("Found query, and mongo and in the if before RunQuery....");
                                    RunQuery(ts, fileStepIndexForLog);
                                } else {
                                    pageHelper.UpdateTestResults("Connection is not available!!!");
                                }
                                pageHelper.UpdateTestResults("Found query, and mongo after the if before RunQuery....");
                            }
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("find")) {
                            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
                            String message;
                            if (!expectedItems[1].trim().isEmpty() ) {
                                if (!expectedItems[0].toLowerCase().contains("contains")) {
                                    message = "Performing find searching all " + expectedItems[1].trim() + " elements for " + expectedItems[2].trim();
                                }
                                else {
                                    message = "Performing find searching all " + expectedItems[1].trim() + " elements containing " + expectedItems[2].trim();
                                }
                            }
                            else {
                                if (!expectedItems[0].toLowerCase().contains("contains")) {
                                    message = "Performing find searching all elements for " + expectedItems[2].trim();
                                } else {
                                    message = "Performing find searching all elements containing " + expectedItems[2].trim();
                                }
                            }
                            pageHelper.UpdateTestResults(pageHelper.indent5 + message + " for step " + fileStepIndexForLog, testResults);
                            FindPhrase(ts, fileStepIndexForLog);
                        }

                        //add in check all elements for a particular text, src, alt value
//                        else if (ts.get_expectedValue().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("all")) {
//                            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
//                            String [] checkItems = expectedItems[0].split(" ");
//                        }
                    }
                } else {  //Perform an action like setting a value or performing a click
                    Boolean status;
                    int dashCount = ts.get_expectedValue().contains(parameterDelimiter) ? StringUtils.countMatches(ts.get_expectedValue(), parameterDelimiter) : 0;
                    //Perform all non read actions below that use an accessor
                    if (((ts.get_searchType().toLowerCase().indexOf(xpathCheckValue) >= 0) || (ts.get_searchType().toLowerCase().indexOf(cssSelectorCheckValue) >= 0) ||
                            (ts.get_searchType().toLowerCase().indexOf(tagNameCheckValue) >= 0) || (ts.get_searchType().toLowerCase().indexOf(idCheckValue) >= 0) ||
                            (ts.get_searchType().toLowerCase().indexOf(classNameCheckValue) >= 0))
                            && (!ts.get_expectedValue().toLowerCase().contains("sendkeys") && !ts.get_expectedValue().toLowerCase().contains("wait")
                            && !ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue))){
                        pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing action using " + ts.get_searchType() + " " + fileStepIndexForLog + " non-read action", testResults);
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
                    } else if (ts.get_xPath().toLowerCase().contains("connection")) {
                        //gaj working here can have different connection types if we go for the connection keyword here
                        if (ts.get_xPath().toLowerCase().contains("mongodb")) {
                            String mongoDbConnectionString = ts.get_expectedValue().trim();
                            //connect to mongo db or close an open mongo db connection
                            SetMongoClient(mongoDbConnectionString, ts);
                        } else if (ts.get_xPath().toLowerCase().contains("sql server")) {
                            pageHelper.UpdateTestResults("This feature is not implemented yet!");
                        }
                    } else if (ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                        //PrePostPad("[ NAVIGATION ]", "═", 9, 159));
//                        pageHelper.UpdateTestResults(pageHelper.indent5 + pageHelper.ANSI_CYAN + pageHelper.PrePostPad("[ Persisting value found by: " + ts.get_searchType() + " accessor: " + ts.get_xPath() + " ]", "═", 9, 154) + pageHelper.ANSI_RESET);
                        pageHelper.UpdateTestResults(pageHelper.indent5 + pageHelper.ANSI_CYAN + "╔" + pageHelper.PrePostPad("[ Start Persisting Element Value ]", "═", 9, 152) + "╗" + pageHelper.ANSI_RESET);
                        pageHelper.UpdateTestResults(pageHelper.indent8 + "Persisting value found by: " + ts.get_searchType() + " accessor: " + ts.get_xPath(), testResults);
                        persistedString = PersistValue(ts, accessor, fileStepIndex, fileStepIndexForLog, dashCount);
                        pageHelper.UpdateTestResults(pageHelper.indent8 + "Persisted value = (" + persistedString + ")", testResults);
                        pageHelper.UpdateTestResults(pageHelper.indent5 + pageHelper.ANSI_CYAN + "╚" + pageHelper.PrePostPad("[ End Persisting action, but value persisted and usable until end of test file ]", "═", 9, 152) + "╝" + pageHelper.ANSI_RESET);

                    } else if (ts.get_searchType().toLowerCase().indexOf("n/a") >= 0) {
                        pageHelper.UpdateTestResults("SearchType = n/a  - Good so far - Accessor: " + ts.get_xPath() +
                                " Expected Value:" + ts.get_expectedValue() + " Lookup Type: " + ts.get_searchType() +
                                " Perform Action: " + ts.getPerformWrite() + " IsCrucial: " + ts.get_isCrucial());
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
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Taking Screenshot for step " + fileStepIndexForLog);
                            PerformScreenShotCapture(browserUsed + ts.get_expectedValue() + fileStepIndex);
                        }
                        else if (ts.get_expectedValue().toLowerCase().indexOf("url") >= 0) {
                            CheckUrlWithoutNavigation(ts, fileStepIndex, fileStepIndexForLog, dashCount);
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
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing login for step " + fileStepIndexForLog, testResults);
                            String [] loginItems = ts.get_expectedValue().split(" ");
                            login(ts.get_xPath(), loginItems[1], loginItems[2], fileStepIndexForLog);
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Login complete for step " + fileStepIndexForLog, testResults);
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("create_test_page")) {
//                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing Create Test Page for step ");
                            pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing Create Test Page for step " + fileStepIndexForLog, testResults);
                            String createTestFileName = CreateTestPage(ts, fileStepIndexForLog);
                            pageHelper.UpdateTestResults("Create Test Page results written to file: " + createTestFileName);
                        }
                    }
                }
            }
            if (revertToParent) {
                driver.switchTo().defaultContent();
//                pageHelper.UpdateTestResults( PageHelper.ANSI_CYAN + pageHelper.sectionEndFormatLeft + "End Switch to IFrame - Reverting to defaultContent" + pageHelper.sectionEndFormatRight + PageHelper.ANSI_RESET);
                pageHelper.UpdateTestResults(pageHelper.ANSI_CYAN + pageHelper.iFrameSectionBottomLeft + pageHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + pageHelper.iFrameSectionBottomRight + pageHelper.ANSI_RESET);
                revertToParent = false;
            }
//            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + pageHelper.ANSI_YELLOW + pageHelper.sectionEndFormatLeft + "End of Test Script" + pageHelper.sectionEndFormatRight + pageHelper.ANSI_RESET, testResults);
            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + pageHelper.ANSI_YELLOW  + pageHelper.sectionLeftUp + pageHelper.PrePostPad("[ End of Test Script ]", "═", 9, 157) + pageHelper.sectionRightUp + pageHelper.ANSI_RESET);
//            pageHelper.UpdateTestResults(pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + pageHelper.ANSI_YELLOW + pageHelper.sectionLeftDown + pageHelper.PrePostPad("[" + pageHelper.ANSI_RESET + pageHelper.ANSI_YELLOW + " End of Test Script " + pageHelper.FRAMED + pageHelper.ANSI_PURPLE_BACKGROUND + "]", "═", 9, 157) + pageHelper.sectionRightDown + pageHelper.ANSI_RESET);
        }
        driver.quit();

        //chromedriver does not shut down from memory so you have to kill the process programmatically
        if (this.driver.toString().indexOf("Chrome") >= 0) {
            ShutDownChromeDriver();
        }
    }

    /* ********************************************************************
     * DESCRIPTION:
     *      Creates a new timestamp to act as a unique id so that
     *      the same test can be used over and over and append this value
     *      to create a new value.
     ******************************************************************** */
    private String GetUniqueIdentifier() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        String tsString = ts.toString().replace("-","").replace(" ","").replace(":","").replace(".","");

        //return ts.toString();
        return tsString;
    }


    /* ********************************************************************
     * DESCRIPTION:
     *      Retrieves the value of the element using the configured accessor
     *      and returns it to the calling method where it will be
     *      persisted in a string variable.
     ******************************************************************** */
    private String PersistValue(TestSettings ts, String accessor, String fileStepIndex, String fileStepIndexForLog, int dashCount) throws Exception
    {
        String actual = null;
        if (ts.get_searchType().toLowerCase().equals(xpathCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent8 + "Element text being retrieved at step " + fileStepIndexForLog + " by xPath: " + accessor, testResults);
            actual = CheckElementWithXPath(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(cssSelectorCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent8 + "Element text being retrieved at step " + fileStepIndexForLog + " by CssSelector: " + accessor, testResults);
            actual = CheckElementWithCssSelector(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(tagNameCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent8 + "Element text being retrieved at step " + fileStepIndexForLog + " by TagName: " + accessor, testResults);
            actual = CheckElementWithTagName(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(classNameCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent8 + "Element text being retrieved at step " + fileStepIndexForLog + " by ClassName: " + accessor, testResults);
            actual = CheckElementWithClassName(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(idCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent8 + "Element text being retrieved at step " + fileStepIndexForLog + " by Id: " + accessor, testResults);
            actual = CheckElementWithId(ts, fileStepIndex);
        }

        return actual;
    }


    /* ********************************************************************
     * DESCRIPTION:
     *      Checks the URL without performing a navigation action.
     *      Compares what was passed in against the current URL.
     ******************************************************************** */
    private void CheckUrlWithoutNavigation(TestSettings ts, String fileStepIndex, String fileStepIndexForLog, int dashCount) throws InterruptedException {
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


    /* ********************************************************************
     * DESCRIPTION:
     *      Creates a new MongoDb Client Connection or closes an open
     *      connection.
     ******************************************************************** */
    private void SetMongoClient(String mongoDbConnection, TestSettings ts) {

        //determine the type of mongo connection that needs to be used
//        if (ts.get_xPath().toLowerCase().trim().contains("uri") && !mongoDbConnection.toLowerCase().contains("close connection")) {
        if (ts.get_xPath().toLowerCase().trim().contains("uri") && !mongoDbConnection.toLowerCase().contains("close")) {
            //mongoClientUri = new MongoClientURI(mongoDbConnection);
            mongoClient = new MongoClient(new MongoClientURI(mongoDbConnection));
        } else if (!mongoDbConnection.toLowerCase().contains("close")) {
            //local connection?
            mongoClient = new MongoClient(mongoDbConnection);
        } else {
            mongoClient.close();  //close the connection
        }

        MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
        while (dbsCursor.hasNext()) {
            try {
                pageHelper.UpdateTestResults(dbsCursor.next());
                MongoDatabase db = mongoClient.getDatabase(dbsCursor.next());

                pageHelper.UpdateTestResults("--[Tables - Start]----");
                MongoIterable<String> col = db.listCollectionNames();


                for (String table : col) {
                    pageHelper.UpdateTestResults(pageHelper.indent5 + "Table = " + table);
                    FindIterable<Document> fields = db.getCollection(table).find();
                    pageHelper.UpdateTestResults(pageHelper.indent5 + "--[Fields - Start]----");
                    /*
                    try {
                        int maxRecords = 1;
                        int recordCount = 0;
                        if (dbsCursor.next().equals("project-tracker-dev")) {
                            pageHelper.UpdateTestResults("db." + table + ".find() = " + db.getCollection(table).find());
                            for (Document field : fields) {
                                pageHelper.UpdateTestResults(pageHelper.indent8 + "Field = " + field.toString().replace("Document{{id=", "\r\nDocument{{id="));
//                                recordCount++;
//                                if (recordCount > maxRecords) {
                                    break;
//                                }
                            }
                        }
                    } catch (MongoQueryException qx) {
                        pageHelper.UpdateTestResults("Field Retrieval MongoDb error occurred: " + qx.getErrorMessage());
                    } catch (Exception ex) {
                        pageHelper.UpdateTestResults("Field Retrieval error occurred: " + ex.getMessage());
                    }*/
                    pageHelper.UpdateTestResults(pageHelper.indent5 + "--[Fields - End]----");
                }

                pageHelper.UpdateTestResults("--[Tables - End]----");
                //col.forEach(String table : col)
                pageHelper.UpdateTestResults("");
            } catch(Exception ex) {
                pageHelper.UpdateTestResults("MongoDB error occurred: " + ex.getMessage());
            }
        }
    }

    private void CloseConnection() {
    }

    private void RunQuery(TestSettings ts, String fileStepIndexForLog) {

        pageHelper.UpdateTestResults("In RunQuery method");
        if (ts.get_xPath().toLowerCase().contains("mongo")) {
            pageHelper.UpdateTestResults("RunQuery....in first If Statement");
            String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
            String [] queryParameters = expectedItems[0].split(" ");
            pageHelper.UpdateTestResults("RunQuery: queryParameters.length = " + queryParameters.length);
            if (queryParameters.length > 5) {  //need to work through this
                pageHelper.UpdateTestResults("RunQuery: queryParameters[0].toLowerCase().trim() = " + queryParameters[0].toLowerCase().trim());
                if (queryParameters[0].toLowerCase().trim().equals("query")) {
                    pageHelper.UpdateTestResults("RunQuery: ts.get_expectedValue().toLowerCase() = " + ts.get_expectedValue().toLowerCase());
                    if (ts.get_expectedValue().toLowerCase().contains("where")) {
                        String wherePhrase = expectedItems[0].substring(expectedItems[0].indexOf("where"));
                        pageHelper.UpdateTestResults("wherePhrase = " + wherePhrase);
                        pageHelper.UpdateTestResults("queryParameters[1] = " + queryParameters[1]);
                        pageHelper.UpdateTestResults("queryParameters[2] = " + queryParameters[2]);
                        MongoDatabase db = mongoClient.getDatabase(queryParameters[1]);
                        MongoCollection<Document> col = db.getCollection(queryParameters[2]);

                        List<Document> documents = (List<Document>) col.find().into(
                                new ArrayList<Document>());

                        if (documents.size() > 0) {
                            for (Document document : documents) {
                                pageHelper.UpdateTestResults("document = " + document);
                            }
                        }
                        else {
                            pageHelper.UpdateTestResults("No matching items found");
                        }
                        //region {Commented for now}
                        /*
                        BasicDBObject whereQuery = new BasicDBObject();
                        pageHelper.UpdateTestResults("queryParameters[4] = " + queryParameters[4]);
                        pageHelper.UpdateTestResults("queryParameters[5] = " + queryParameters[5].replace(",", ""));
                        if (!queryParameters[6].contains("\"")) {
                            whereQuery.put(queryParameters[4].replace("\"", "") + " " + queryParameters[5].replace(",", "").replace("\"", ""), parseInt(queryParameters[6]));
                        } else {
                            whereQuery.put(queryParameters[4].replace("\"", "") + " " + queryParameters[5].replace(",", "").replace("\"", ""), queryParameters[6]);
                        }
                        FindIterable<Document> iterableString = col.find(whereQuery);
                        pageHelper.UpdateTestResults("iterableString = " + iterableString);
                        if (iterableString != null) {
                            for (Document item : iterableString) {
                                pageHelper.UpdateTestResults("item = " + item);
                            }
                        }
                        else {
                            pageHelper.UpdateTestResults("No matching items found");
                        }*/
                        //endregion
                    }
                }
            } else {  //get the entire table of data if no where clause exists
                MongoDatabase db = mongoClient.getDatabase(queryParameters[1]);
                MongoCollection<Document> col = db.getCollection(queryParameters[2]);
                //List<Document> documents;
                FindIterable<Document> documents = null;
                Document doc = null;
                if (queryParameters.length > 2) {

//                    documents = (List<Document>) col.find("{" + queryParameters[3].toString() + ":" + queryParameters[4].toString() + "}").into(
//                            new ArrayList<Document>());
//                    documents = db.getCollection(queryParameters[2]).find("{ " +  queryParameters[3].toString() + ":" + queryParameters[4].toString() + " }"));
                    BSONObject bsonObj = BasicDBObject.parse("{" + queryParameters[3].toString() + ":" + queryParameters[4].toString() + "}");
//                    documents = db.getCollection(queryParameters[2]).find(((BasicDBObject) bsonObj)).first();
                    doc = db.getCollection(queryParameters[2]).find(((BasicDBObject) bsonObj)).first();

                    //NOTE: { Everything remaining in this if statement is for formatting and not necessary for the testing application }
                    //code used below for troubleshooting not necessarily testing
                    pageHelper.UpdateTestResults("Doc = " + doc.toString());
//                    pageHelper.UpdateTestResults("Doc = " + doc.toString()
//                            .replace("{{","\r\n" + pageHelper.indent5 + "{{\r\n " + pageHelper.indent5)
//                            .replace("}},","\r\n" + pageHelper.indent5 + "}},\r\n")
//                            .replace(",",",\r\n" + pageHelper.indent5));
                    String[] docString = doc.toString().split(", ");
                    int indent = 0;
                    int padSize = 2;
                    String tempItem = "";
                    String tempItem2 = "";
                    for (String item: docString) {
                        tempItem = "";
                        tempItem2 = "";
                        pageHelper.UpdateTestResults("[indent set to: " + indent + "]");
                        //pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.trim() + " - (Unformatted)");
                        if ((item.contains("{{") || item.contains("[")) && !item.contains("[]")) {

                            while (item.indexOf("{{") > 0 || item.indexOf("[") > 0)
                            {
                                tempItem += pageHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("{{") + 2).trim().replace("{{", "\r\n" + pageHelper.PadIndent(padSize, indent) + "{{\r\n");
                                pageHelper.UpdateTestResults("tempItem = " + tempItem);
                                item = item.substring(item.indexOf("{{") + 2).trim();
                                pageHelper.UpdateTestResults("item = " + item);
                                indent++;
                                pageHelper.UpdateTestResults("[indent now set to: " + indent + "]");
                            }
                            if (item.length() > 0) {
                                tempItem += pageHelper.PadIndent(padSize, indent) +  item.trim();
                            }


                            pageHelper.UpdateTestResults(pageHelper.ANSI_YELLOW + tempItem + pageHelper.ANSI_RESET);
//                            while (tempItem.indexOf("[") > 0) {
//                                tempItem2 += pageHelper.PadIndent(padSize, indent) + tempItem.substring(0, tempItem.indexOf("[") + 1).trim().replace("[", "\r\n" + pageHelper.PadIndent(padSize, indent) + "[\r\n");
//                                tempItem = tempItem.substring(tempItem.indexOf("[") + 1).trim();
//                                indent++;
//                            }
//                            if (tempItem.length() > 0) {
//                                tempItem2 += pageHelper.PadIndent(padSize, indent) + tempItem.trim();
//                            }
//
//
//
//                            pageHelper.UpdateTestResults(pageHelper.ANSI_BLUE + tempItem2 + pageHelper.ANSI_RESET);
//                            pageHelper.UpdateTestResults(color + tempItem + pageHelper.ANSI_RESET);
                        }
                        else if ((item.contains("}}") || item.contains("]")) && !item.contains("[]")) {
                            while (item.indexOf("}}") > 0)
                            {
                                tempItem += pageHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("}}") + 2).replace("}}", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "}}");
//                                tempItem += pageHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("}}") + 2).replace("}}", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "}}\r\n");
                                item = item.substring(item.indexOf("}}") + 2).trim();
                                indent--;
                            }
                            if (item.length() > 0) {
                                tempItem += pageHelper.PadIndent(padSize, indent) +  item;
//                                tempItem += pageHelper.PadIndent(padSize, indent) +  item + " - (also left over)";
                            }
//                            if (tempItem.contains("]")) {
//                                indent--;
//                            }
                            pageHelper.UpdateTestResults(tempItem);
                            //pageHelper.UpdateTestResults(item);
                            //indent--;
                        }
                        else {
                            pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) +  item.trim() + " - (No delimiters)");
                        }
                       /*
                        if (item.contains("{{") || item.contains("[")) {
                            if (item.contains("{{")) {
                                if (item.contains("=") && (item.indexOf("=") < item.indexOf("{{"))) {
                                    pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("{{", pageHelper.PadIndent(padSize, indent)) + "\r\n" + pageHelper.PadIndent(padSize, indent) + "{{ ");
                                } else {
                                    pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("{{", pageHelper.PadIndent(padSize, indent)) + "{{\r\n " + pageHelper.PadIndent(padSize, indent + 1));
                                    //pageHelper.UpdateTestResults(item.replace("{{", "\r\n" + pageHelper.PadIndent(4, indent)) + "{{\r\n " + pageHelper.PadIndent(4, indent + 1));
                                }
                            }
                            if (item.contains("["))  {
                                if (item.contains("]")) {
                                    String temp = pageHelper.PadIndent(padSize, indent) + item.replace("[", "\r\n" + pageHelper.PadIndent(padSize, indent) + "[\r\n" + pageHelper.PadIndent(padSize, indent + 1));
                                    temp += pageHelper.PadIndent(padSize, indent) + temp.replace("]", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "]\r\n");

                                } else {
                                    pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("[", "\r\n" + pageHelper.PadIndent(padSize, indent) + "[\r\n" + pageHelper.PadIndent(padSize, indent + 1)));
                                }
                            }
//                            pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                            indent++;
                        } else if (item.contains("}}") || item.contains("]")) {
                            if (item.contains("}}")) {
                                pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("}}", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "}}") + ",");
                            }
                            if (item.contains("]")) {
                                pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("]", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "]\r\n"));
                            }
//                            pageHelper.UpdateTestResults(item.replace("}}", pageHelper.PadIndent(4, indent) + "\r\n" + pageHelper.PadIndent(4, indent - 1) + "}}") + ",");
//                            pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                            indent--;
                        } else {
                            pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item  + ",");
//                            pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                        } */
                       // pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                    }
                } else {
                    List<Document> documents2;
                    documents2 = (List<Document>) col.find().into(
                            new ArrayList<Document>());
                }

                /*
                if (documents != null) {
                    for (Document document : documents) {
//                        pageHelper.UpdateTestResults("document = " + document.replace(",", ",\r\n"));
                        pageHelper.UpdateTestResults("document = " + document.toString().replace(",",",\r\n"));
                    }
                }
                else {
                    pageHelper.UpdateTestResults("No matching items found");
                } */
            }
        }
    }


    /* ********************************************************************
     * DESCRIPTION:
     *      Logs into a Page that uses a Popup Alert style login.
     *
     ******************************************************************** */
    public void login(String url, String email, String password, String fileStepIndexForLog) throws Exception {
        pageHelper.UpdateTestResults("Login method reached start before any code.");
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        pageHelper.UpdateTestResults("Login method reached");

        try {
            pageHelper.UpdateTestResults("Switched to Alert second try");
            //driver.switchTo().alert();
            //driver.switchTo().alert().sendKeys(email + Keys.TAB + password + Keys.RETURN);
            pageHelper.UpdateTestResults("Switched to Alert second try after");
        }
        catch(Exception ex) {
                try {
                    if (!isAlertPresent()) {
                        driver.get(testPage);
                    }
                    driver.switchTo().alert();
                    pageHelper.UpdateTestResults("Switched to Alert #1");
                    driver.findElement(By.id("username")).sendKeys(email);
                    driver.findElement(By.id("password")).sendKeys(password);
                    pageHelper.UpdateTestResults("Sent Credentials email: " + email + " Password: " + password);
                    driver.switchTo().alert().accept();
                    pageHelper.UpdateTestResults("Switched to Alert #2");
                    driver.switchTo().defaultContent();
                    pageHelper.UpdateTestResults("Switched to default context");
                }
                catch (Exception ex1)
                {
                    pageHelper.UpdateTestResults("Exception " + ex.getMessage());
                    if (url == null || url.isEmpty() || !url.toLowerCase().trim().equals("n/a")) {
                        url = testPage;
                    }
                    pageHelper.UpdateTestResults("Switched to Alert Second catch");
                    String newUrl = url.replace("://", "://" + email + ":" + password + "@");
                    driver.get(newUrl);
                    //if the alert doesn't show up, you already have context and are logged in
                }
            }
        //region { rewritten }
        /*try {
            try {
                pageHelper.UpdateTestResults("Switched to Alert second try");
                //driver.switchTo().alert();
                //driver.switchTo().alert().sendKeys(email + Keys.TAB + password + Keys.RETURN);
                pageHelper.UpdateTestResults("Switched to Alert second try after");
            }
            catch(Exception ex) {
                if (!isAlertPresent()) {
                    driver.get(testPage);
                }
                driver.switchTo().alert();
                pageHelper.UpdateTestResults("Switched to Alert #1");
                driver.findElement(By.id("username")).sendKeys(email);
                driver.findElement(By.id("password")).sendKeys(password);
                pageHelper.UpdateTestResults("Sent Credentials email: " + email + " Password: " + password);
                driver.switchTo().alert().accept();
                pageHelper.UpdateTestResults("Switched to Alert #2");
                driver.switchTo().defaultContent();
                pageHelper.UpdateTestResults("Switched to default context");
            }
        }
        catch (Exception ex)
        {
            pageHelper.UpdateTestResults("Exception " + ex.getMessage());
            if (url == null || url.isEmpty() || !url.toLowerCase().trim().equals("n/a")) {
                url = testPage;
            }
            pageHelper.UpdateTestResults("Switched to Alert Second catch");
            String newUrl = url.replace("://", "://" + email + ":" + password + "@");
            driver.get(newUrl);
            //if the alert doesn't show up, you already have context and are logged in
        }*/
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
//        pageHelper.UpdateTestResults( pageHelper.subsectionLeft + "Start Explicit Navigation Event" + pageHelper.subsectionRight, testResults);
        pageHelper.UpdateTestResults( pageHelper.indent5 + pageHelper.subsectionArrowLeft + pageHelper.PrePostPad("[ Start Explicit Navigation Event ]", "═", 9, 80) + pageHelper.subsectionArrowRight + pageHelper.ANSI_RESET, testResults);
        pageHelper.UpdateTestResults(pageHelper.indent8 + "Navigating to " + navigateUrl + " for step " + fileStepIndexForLog);
        String actualUrl = CheckPageUrl(delayMilliSeconds);
        if (expectedUrl != null && expectedUrl.trim().length() > 0) {
//            pageHelper.UpdateTestResults("IN the expectedUrl if statement..should not be here");
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
//        pageHelper.UpdateTestResults(pageHelper.subsectionLeft + " End Explicit Navigation Event " + pageHelper.subsectionRight, testResults);
        pageHelper.UpdateTestResults( pageHelper.indent5 + pageHelper.subsectionArrowLeft + pageHelper.PrePostPad("[ End Explicit Navigation Event ]", "═", 9, 80) + pageHelper.subsectionArrowRight + pageHelper.ANSI_RESET, testResults);
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
        Boolean notEqual = false;
        final String elementTypeCheckedAtStep = "Element type being checked at step ";

        if (ts.get_searchType().toLowerCase().equals(xpathCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + elementTypeCheckedAtStep + fileStepIndexForLog + " by xPath: " + accessor, testResults);
            actual = CheckElementWithXPath(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(cssSelectorCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + elementTypeCheckedAtStep + fileStepIndexForLog + " by CssSelector: " + accessor, testResults);
            actual = CheckElementWithCssSelector(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(tagNameCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + elementTypeCheckedAtStep + fileStepIndexForLog + " by TagName: " + accessor, testResults);
            actual = CheckElementWithTagName(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(classNameCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + elementTypeCheckedAtStep + fileStepIndexForLog + " by ClassName: " + accessor, testResults);
            actual = CheckElementWithClassName(ts, fileStepIndex);
        } else if (ts.get_searchType().toLowerCase().equals(idCheckValue)) {
            pageHelper.UpdateTestResults(pageHelper.indent5 + elementTypeCheckedAtStep + fileStepIndexForLog + " by Id: " + accessor, testResults);
            actual = CheckElementWithId(ts, fileStepIndex);
        }

        if (expected.toLowerCase().contains("!=")) {
            notEqual = true;
            expected = expected.split(parameterDelimiter)[1].trim();
        }

        if (expected.toLowerCase().contains(persistedStringCheckValue)) {

            if (persistedString != null) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Grabbing " + pageHelper.ANSI_CYAN + "persisted" + pageHelper.ANSI_RESET + " value: (" + persistedString + ") for comparison.");
                expected = persistedString;
            }
            else {
                pageHelper.UpdateTestResults("");
                pageHelper.UpdateTestResults(pageHelper.indent5 + pageHelper.ANSI_RED + "╔" + pageHelper.PrePostPad("[ Start of Persistence Usage Error ]", "═", 9, 152) + "╗" + pageHelper.ANSI_RESET);
                pageHelper.UpdateTestResults(pageHelper.indent8 + pageHelper.ANSI_RED + "ERROR: No value previously persisted!!! " + pageHelper.ANSI_RESET + "Using empty string () instead of null for comparison.");
                pageHelper.UpdateTestResults(pageHelper.indent8 + pageHelper.ANSI_RED + "IMPORTANT:" + pageHelper.ANSI_RESET + " A value must first be persisted before that persisted value can be used for comparison.", testResults);
                pageHelper.UpdateTestResults(pageHelper.indent8 + pageHelper.ANSI_RED + "NOTE:" + pageHelper.ANSI_RESET + " Values persisted in one test file are reset before the start of the next test file.");
                pageHelper.UpdateTestResults(pageHelper.indent8 + pageHelper.indent5 + "Any values you want persisted for comparison, must first be persisted in the test file performing the comparison!!!");
                pageHelper.UpdateTestResults(pageHelper.indent8 + pageHelper.indent5 + "Refer to the help file for more information regarding persisting and comparing persisted values.");
                pageHelper.UpdateTestResults(pageHelper.indent5 + pageHelper.ANSI_RED + "╚" + pageHelper.PrePostPad("[ End of Persistence Usage Error ]", "═", 9, 152) + "╝" + pageHelper.ANSI_RESET);
                expected = "";
            }
            //expected = persistedString != null ? persistedString : "";
        }

        if (ts.get_isCrucial()) {
            if (!notEqual) {
                assertEquals(expected, actual);
            }
            else {
                assertFalse(expected.equals(actual));
            }
        } else {
            try {
                assertEquals(expected, actual);
            } catch (AssertionError ae) {
                // do not capture screen shot here, if element not found, check methods will capture screen shot
            }
        }
        if (expected.equals(actual) && !notEqual) {
            pageHelper.UpdateTestResults("Successful equal comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", testResults);
        } else if (!expected.equals(actual) && notEqual) {
            pageHelper.UpdateTestResults("Successful not equal comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", testResults);
        } else if (!expected.equals(actual) && !notEqual) {
            pageHelper.UpdateTestResults("Failed equal comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", testResults);
            if (screenShotSaveFolder != null && !screenShotSaveFolder.isEmpty()) {
                pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "Assert_Fail", screenShotSaveFolder, false);
            }
        } else if (expected.equals(actual) && notEqual) {
            pageHelper.UpdateTestResults("Failed not equal comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", testResults);
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

        try {
            switch (accessorType) {
                case xpathCheckValue:
                    element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor)));
                    break;
                case idCheckValue:
                    element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.id(accessor)));
                    break;
                case tagNameCheckValue:
                    element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.tagName(accessor)));
                    break;
                case classNameCheckValue:
                    element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.className(accessor)));
                    break;
                case cssSelectorCheckValue:
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
            } else {
                if (pageLoadComplete) {
                    pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful load of page " + GetCurrentPageUrl() + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndexForLog, testResults);
                }
            }
        } catch (TimeoutException ae) {
            if (ts.get_xPath().toLowerCase().trim().contains("page")) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed to find the element " + GetCurrentPageUrl() + " within the set max time of " + maxTimeInSeconds + " at step " + fileStepIndexForLog + " AL+", testResults);
            } else {
                pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed to load element " + accessor + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndexForLog, testResults);
            }
            if (ts.get_isCrucial()){
                throw (ae);
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
//        pageHelper.UpdateTestResults("In CheckPageUrl method.  Driver = " + this.driver.toString(), testResults);
        pageHelper.NavigateToPage(this.driver, testPage, delayMilliSeconds);
//        pageHelper.UpdateTestResults("In CheckPageUrl method #2.  Driver = " + this.driver.toString(), testResults);

        if (!isAlertPresent()) {
            return this.driver.getCurrentUrl();
        } else {
            return null;
        }
    }


    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } // try
        catch (Exception e) {
            return false;
        } // catch
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
        String text;

        pageHelper.UpdateTestResults(pageHelper.indent5 + "Retrieved " + links.size() + " anchor tags");
        for(WebElement link : links) {
            href = link.getAttribute("href");
            text = link.getAttribute("text");
            text = text.trim();

            if (href != null) {
                try {
                    text = text.isEmpty() ? "[Possible Image] " + link.findElement(By.tagName("img")).getAttribute("alt") : text;
                } catch (NoSuchElementException nse) {
                    text = text.isEmpty() ? "[Possible Image] " : text;
                } catch (Exception ex) {
                    text = text.isEmpty() ? "[Possible Image] " : text;
                }
            }

            if (href != null) {
                linkCount++;
                brokenLinksStatusCode = httpResponseCodeViaGet(href);

                if (200 != brokenLinksStatusCode) {
                    pageHelper.UpdateTestResults(pageHelper.ANSI_RED + "Failed link test " + href + " gave a response code of " + brokenLinksStatusCode + pageHelper.ANSI_RESET, testResults);
                } else {
                    pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful link test text: " + text + " href: " + href + " xPath: " + generateXPATH(link, "") + " gave a response code of " + brokenLinksStatusCode + pageHelper.ANSI_RESET, testResults);
                }
            }
        }
        pageHelper.UpdateTestResults(pageHelper.indent5 + "Discovered " + linkCount + " links amongst " + links.size() + " anchor tags.\r\n", testResults);
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

    /* *************************************************************************
     * DESCRIPTION:
     *     If a Tag is passed in as part of the Expected values, search the text
     *     of all tags of that type for the phrase, but if no tag is passed in,
     *     search the text of all page elements for the phrase.
     *
     ************************************************************************* */
    private void FindPhrase(TestSettings ts, String fileStepIndex) {
        String cssSelector = "*";
        String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
        if (!expectedItems[1].trim().isEmpty()) {
            cssSelector = expectedItems[1].trim();
        }
        Boolean wasFound = false;

        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        List<String> foundElements = new ArrayList<>();

        //region {Future implementation NOTE}
        // When searching a specific tag type for the phrase,
        // iterate through all child elements to see if one of them contains the text.
        // if a child or grandchild contains the text, eliminate the element as containing the text
        //endregion

        if (expectedItems[0].toLowerCase().contains("contains")) {
            for (WebElement element : elements) {
                if (element.getText().contains(expectedItems[2].trim())) {
                    wasFound = true;
                    foundElements.add(generateXPATH(element, ""));
//                    pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful found (" + expectedItems[2].trim() + ") in element: " + generateXPATH(element, "") + " for step " + fileStepIndex + pageHelper.ANSI_RESET, testResults);
                    //break;
                }
            }
        }
        else {
            for (WebElement element : elements) {
                if (element.getText().equals(expectedItems[2].trim())) {
                    wasFound = true;
                    foundElements.add(generateXPATH(element, ""));
//                    pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful found (" + expectedItems[2].trim() + ") in element: " + generateXPATH(element, "") + " for step " + fileStepIndex + pageHelper.ANSI_RESET, testResults);
                    //break;
                }
            }
        }

        if (!wasFound) {
            String message = "Failed to find (" + expectedItems[2].trim() + ") searching all elements.";
            if (!cssSelector.trim().isEmpty()) {
                message = "Failed to find (" + expectedItems[2].trim() + ") searching all " + cssSelector + " elements.";
            }
            pageHelper.UpdateTestResults(pageHelper.ANSI_RED + message + pageHelper.ANSI_RESET, testResults);
        }
        else {
            //eliminate any hierarchical elements that don't actually contain the text
            for (int y = foundElements.size() -1;y>= 0;y--) {
                for (int x = foundElements.size() - 1;x>=0;x--) {
                    if (y != x) {
                        try {
                            if (foundElements.get(y).contains(foundElements.get(x))) {
                                foundElements.remove(x);
                            } else if (foundElements.get(x).contains(foundElements.get(y))) {
                                foundElements.remove(y);
                            }
                        }
                        catch(IndexOutOfBoundsException io) {
                            //try moving on to the next item doing nothing here
                            //pageHelper.UpdateTestResults("Error y = " + y + " and x = " + x + " - " + io.getMessage());
                        }
                    }
                }
            }
            for (int z=0;z<foundElements.size();z++) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_GREEN + "Successful found (" + expectedItems[2].trim() + ") in element: " + foundElements.get(z) + " for step " + fileStepIndex + pageHelper.ANSI_RESET, testResults);
            }
        }
    }

    private String CreateTestPage(TestSettings ts, String fileStepIndex) {
        //Update Help file with the following
        //expected values [0] = create_test_page
        //expected values [1] = Tag type to lookup (*) for all tags, defaults to * if left empty
        //expected values [2] = File where the results should be written
        //expected values [3] = Comma Delimited List of tags to skip when all tags is the lookup type. (Ignored for specific tag lookups.
        //╠n/a ; create_test_page  ╬ a ╬ C:\Users\gjackson\Downloads\Ex_Files_Selenium_EssT\Ex_Files_Selenium_EssT\Exercise Files\TestFiles\Create Tests Pages\TestFileOutput_A.txt ╬ html,head,title,meta,script,body,style,a,nav,br,strong,div ; n/a ; true ; false╣
        //GAJ working here...finish this shit
        pageHelper.UpdateTestResults("In CreateTestPage #1");
        String cssSelector = "*";
        String [] expectedItems = ts.get_expectedValue().split(parameterDelimiter);
        pageHelper.UpdateTestResults("In CreateTestPage #1.  ExpectedItems length:" + expectedItems.length);
        String newFileName = "/config/newTestFile.txt";
        int tagCount = (expectedItems.length == 4 && expectedItems[3] != null && !expectedItems[3].trim().isEmpty()) ? StringUtils.countMatches(ts.get_expectedValue(), parameterDelimiter) : 0;
        String [] skipTags = new String[tagCount];

        //css selector to use
        if (expectedItems[1] != null && !expectedItems[1].trim().isEmpty()) {
            cssSelector = expectedItems[1].trim();

        }
        //name of file to write results to
        if (expectedItems[2] != null && !expectedItems[2].trim().isEmpty()) {
            newFileName = expectedItems[2].trim();
        }

        //delete this if it exists
        try {
            pageHelper.DeleteFile(newFileName);
        } catch(Exception ex) {
            //let the delete file method handle this exception
        }

        //elements to skip if all elements used (*) - don't put this within the cssSelector assignment in case it is not provided
        if (cssSelector.equals("*") && expectedItems.length == 4 && expectedItems[3] != null && !expectedItems[3].trim().isEmpty()) {
            skipTags = expectedItems[3].split(",");
            pageHelper.UpdateTestResults("skipTags.toString() = " + skipTags.toString());
        }

        try {
            Boolean wasFound = false;
            Boolean canProceed = true;
            List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
            List<String> foundElements = new ArrayList<>();
            String elementType;
            String elementXPath;
            String elementText;
            String elementHref;
            String elementSrc;
            String outputDescription;
            pageHelper.UpdateTestResults("In CreateTestPage #2 elements retrieved: " + elements.size());

            for (WebElement element : elements) {
                canProceed = true;
                elementType = element.getTagName();
                if (cssSelector.equals("*")) {
                    if (skipTags != null && skipTags.length > 0) {
                        for (String skipTag : skipTags) {
                            if (elementType.equals(skipTag)) {
                                canProceed = false;
                                break;
                            }
                        }
                    }
                }
                else {
                    canProceed = true;
                }

                if (canProceed) {
                    elementXPath = generateXPATH(element, "");

                    elementText = element.getText();

                    outputDescription = "Element Type: " + elementType + " - Element xPath: " + elementXPath + " - Element Text: " + elementText;

                    if (elementType.equals("img")) {
                        elementSrc = element.getAttribute("src");
                        outputDescription += " - Element Src: " + elementSrc;
                    } else if (elementType.equals("a")) {
                        elementHref = element.getAttribute("href");
                        outputDescription += " - Element Href: " + elementHref;
                    }

                    pageHelper.UpdateTestResults(outputDescription, testResults);
                    pageHelper.WriteToFile(newFileName, outputDescription);
                }
            }
        } catch (Exception ex) {
            pageHelper.UpdateTestResults("Error: " + ex.getMessage());
        }

        pageHelper.UpdateTestResults("In CreateTestPage #2 returning nothing");

        return newFileName;
    }

    private String generateXPATH(WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        if(childTag.equals("html")) {
            return "/html[1]"+current;
        }
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for(int i=0;i<childrenElements.size(); i++) {
            WebElement childrenElement = childrenElements.get(i);
            String childrenElementTag = childrenElement.getTagName();
            if(childTag.equals(childrenElementTag)) {
                count++;
            }
            if(childElement.equals(childrenElement)) {
                return generateXPATH(parentElement, "/" + childTag + "[" + count + "]"+current);
            }
        }
        return null;
    }

    /* *************************************************************************
     * DESCRIPTION:
     *    Partially implemented!
     *    Currently only checks brightness and degree of difference in color.
     *    Intended to and will eventually check the color contrast of
     *    fonts against the background they are used upon.
     ************************************************************************* */
    public void checkColorContrast(String url, String checkElement, String fileStepIndex, boolean isCrucial, String acceptibleRanges)
    {
        //useful links for this functionality
        //https://www.w3.org/TR/WCAG20-TECHS/G17.html - ( look here for actual color contrast calculations recommended 7:1 ratio )
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
                pageHelper.UpdateTestResults(pageHelper.ANSI_BRIGHTWHITE + pageHelper.ANSI_RED + "Good brightness Warning contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + pageHelper.ANSI_RESET);
            } else if (brightness < brightnessStandard && contrast >= contrastStandard) {
                pageHelper.UpdateTestResults(pageHelper.ANSI_BRIGHTWHITE + pageHelper.ANSI_RED + "Warning brightness and Good contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + pageHelper.ANSI_RESET);
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
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
            }
            else {
                pageHelper.UpdateTestResults(pageHelper.indent8 + "Retrieving element text by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
            }
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
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by CssSelector: " + accessor + " for script " + fileStepIndexForLog + " Actual Value: " + actualValue, testResults);
            } else {
                pageHelper.UpdateTestResults(pageHelper.indent8 + "Retrieving element text by CssSelector: " + ElementTypeLookup(accessor) + " for script " + fileStepIndexForLog + " Actual Value: " + actualValue, testResults);
            }
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
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by TagName: " + ElementTypeLookup(accessor) + " for script. " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
            } else {
                pageHelper.UpdateTestResults(pageHelper.indent8 + "Retrieving element text by TagName: " + ElementTypeLookup(accessor) + " for script " + fileStepIndexForLog + " Actual Value: " + actualValue, testResults);
            }
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
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by ClassName: " + accessor + " for script. " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
            }  else {
                pageHelper.UpdateTestResults(pageHelper.indent8 + "Retrieving element text by ClassName: " + accessor + " for script " + fileStepIndexForLog + " Actual Value: " + actualValue, testResults);
            }
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
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Checking element by ID: " + accessor + " for script." + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
            } else {
                pageHelper.UpdateTestResults(pageHelper.indent8 + "Retrieving element text by ID: " + accessor + " for script " + fileStepIndexForLog + " Actual Value: " + actualValue, testResults);
            }
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
        String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);

        //if this is a click event, click it
        if (value.toLowerCase().contains(click) && !value.toLowerCase().contains(sendKeys)) {
            if (value.toLowerCase().contains(doubleClick)) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing double click on " + accessor + " using " + accesssorType + " for step " + fileStepIndex, testResults);
            } else if (value.toLowerCase().contains(rightClick)) {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing right click on " + accessor + " using " + accesssorType + " for step " + fileStepIndex, testResults);
            } else {
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing click on " + accessor + " using " + accesssorType + " for step " + fileStepIndex, testResults);
            }
            try {
                if (accesssorType.toLowerCase().equals(xpathCheckValue)) {
                    if (!value.toLowerCase().contains(rightClick)) {
                        if (!value.toLowerCase().contains(doubleClick)) {
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
                else if (accesssorType.toLowerCase().equals(idCheckValue)) {
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
                else if (accesssorType.toLowerCase().equals(classNameCheckValue)) {
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
                else if (accesssorType.toLowerCase().equals(cssSelectorCheckValue)) {
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
                else if (accesssorType.toLowerCase().equals(tagNameCheckValue)) {
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
                pageHelper.UpdateTestResults(pageHelper.indent5 + "Taking Screenshot for step " + fileStepIndex);
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
                    value = value.replace(uidReplacementChars, uniqueId);
                    //pageHelper.UpdateTestResults("value = " + value);
                    //added the below structure so that the unique identifier could be used with the persisted string.
                    if (value.toLowerCase().contains(persistedStringCheckValue) && !values[1].trim().contains(uidReplacementChars)) {
                        value = persistedString;
                    }
                    else {
                        if (values[1].trim().toLowerCase().contains(persistedStringCheckValue) && values[1].trim().contains(uidReplacementChars)) {
                            if (values[1].trim().indexOf(persistedStringCheckValue) < values[1].trim().indexOf(uidReplacementChars)) {
                                if (values[1].trim().indexOf(" ") > values[1].trim().indexOf(persistedStringCheckValue))
                                {
                                    value = persistedString + " " + uniqueId;
                                }
                                else {
                                    value = persistedString + uniqueId;
                                }
                            } else {
                                if (values[1].trim().indexOf(" ") > values[1].trim().indexOf(uidReplacementChars))
                                {
                                    value = uniqueId + " " + persistedString;
                                }
                                else {
                                    value =  uniqueId + persistedString;
                                }
                            }
                        }
                    }
//                    pageHelper.UpdateTestResults("value = " + value);
                    pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing SendKeys value = " + value + " for step " + fileStepIndex, testResults);
                }
                if (value.contains(keys) || value.toLowerCase().contains(keys)) {
                    if (accesssorType.toLowerCase().equals(xpathCheckValue)) {
                        this.driver.findElement(By.xpath(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals(idCheckValue)) {
                        this.driver.findElement(By.id(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals(classNameCheckValue)) {
                        this.driver.findElement(By.className(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals(cssSelectorCheckValue)) {
                        this.driver.findElement(By.cssSelector(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                    else if (accesssorType.toLowerCase().equals(tagNameCheckValue)) {
                        this.driver.findElement(By.tagName(accessor)).sendKeys(GetKeyValue(value, fileStepIndex));
                    }
                } else {
                    value = value.replace(uidReplacementChars, uniqueId);
                    //pageHelper.UpdateTestResults("Not sending reserved Key strokes.");
                    pageHelper.UpdateTestResults(pageHelper.indent5 + "Performing default SendKeys value = " + value + " for step " + fileStepIndex, testResults);
                    if (accesssorType.toLowerCase().equals(xpathCheckValue)) {
                        this.driver.findElement(By.xpath(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals(idCheckValue)) {
                        this.driver.findElement(By.id(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals(classNameCheckValue)) {
                        this.driver.findElement(By.className(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals(cssSelectorCheckValue)) {
                        this.driver.findElement(By.cssSelector(accessor)).sendKeys(value);
                    }
                    else if (accesssorType.toLowerCase().equals(tagNameCheckValue)) {
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

    /* ************************************************************
     * DESCRIPTION:
     *      Switches to a different tab either the child or
     *      the parent tab.
     ************************************************************ */
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
        if (elementTag.toLowerCase().startsWith("label") || elementTag.toLowerCase().equals("label")) {
            return "Label";
        }
        if (elementTag.toLowerCase().startsWith("input") || elementTag.toLowerCase().equals("input")) {
            return "Input";
        }
        if (elementTag.toLowerCase().startsWith("button") || elementTag.toLowerCase().equals("button")) {
            return "Button";
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

    /* ************************************************************
     * DESCRIPTION:
     *      Shuts down the Chrome Driver.
     *      For some reason, Chrome Driver notoriously does not shut
     *      down when the test completes although other drivers do,
     *      so this fixes that by shutting down all instances of
     *      the Chrome Driver currently running on your machine.
     ************************************************************ */
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

    //region { Original WaitForElement code }
    /*private void WaitForElement(TestSettings ts, String fileStepIndexForLog) {
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
            case classNameCheckValue:
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
    }*/
    //endregion
}
