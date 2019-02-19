import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

enum BrowserType {
    Chrome, Firefox, PhantomJS, Internet_Explorer
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
     *      First, mark the Entities folder and the pages folder as Source Root.
     *      Second, mark the test folder as Test Source Root.
     *      Update the following items if necessary:
     *      configurationFile,
     *      configurationFolder,
     *      logFileName,
     *      chromeDriverPath,
     *      fireFoxDriverPath,
     *      phantomJsDriverPath
     *
     *      Future updates:
     *      1.  Need to add switch to frame for iFrames
     *          driver.switch_to.frame('NAME')
     *      2.  Need to separate debug output from required output and make
     *          it configurable whether extra information is output.
     ╚═══════════════════════════════════════════════════════════════════════════════╝ */
    //endregion

    private String configurationFile = "Config/ConfigurationSetup.tconfig";
    private String configurationFolder = "Config/";
    private WebDriver driver;
    private static String testPage = "https://www.davita.com/";
    private PageHelper pageHelper = new PageHelper();
    private boolean runHeadless = true;
    private String screenShotSaveFolder;
    private BrowserType _selectedBrowserType; // = BrowserType.Firefox;    //BrowserType.Chrome;  //BrowserType.PhantomJS;
    private int maxBrowsers = 3;
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
    //private String internetExplorerDriverPath;

    public BrowserType get_selectedBrowserType() {
        return _selectedBrowserType;
    }

    //bold
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String FRAMED = "\u001B[51m";

    //region { System.out  font Colors }
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    //endregion

    //region {System out background colors }
    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    //endregion


    public void set_selectedBrowserType(BrowserType newValue) {
        if (newValue == BrowserType.Chrome) {
            this._selectedBrowserType = BrowserType.Chrome;
        } else if (newValue == BrowserType.Firefox) {
            this._selectedBrowserType = BrowserType.Firefox;
        } else {
            this._selectedBrowserType = BrowserType.PhantomJS;
        }
    }

    /* ****************************************************************
     * Description: Default Constructor.  Reads the configuration file
     * and the associated test file and when a site is not being tested
     * using all browsers, it sets the browser that will be used for the
     * test.
     **************************************************************** */
    public HomePage() throws Exception {

        File tmp = new File(configurationFile);
        pageHelper.UpdateTestResults("Config File absolute path = " + tmp.getAbsolutePath());
        pageHelper.UpdateTestResults("Log File Name = " + logFileName);
        pageHelper.UpdateTestResults("Help File Name = " + helpFileName);
        pageHelper.UpdateTestResults("");

        pageHelper.set_logFileName(logFileName);
        pageHelper.set_helpFileName(helpFileName);
        ConfigureTestEnvironment();


        //testSettings = pageHelper.ReadTestSettingsFile(testSettings, testFileName);
        pageHelper.UpdateTestResults(FRAMED + ANSI_GREEN_BACKGROUND + ANSI_BLUE + ANSI_BOLD + pageHelper.sectionStartFormatLeft + "Beginning Configuration" + pageHelper.sectionStartFormatRight + ANSI_RESET, testResults);
        pageHelper.UpdateTestResults("     Configured Browser Selection = " + get_selectedBrowserType(), testResults);

        if (!testAllBrowsers) {
            if (get_selectedBrowserType() == BrowserType.PhantomJS) {
                SetPhantomJsDriver();
            } else if (get_selectedBrowserType() == BrowserType.Chrome) {
                SetChromeDriver();
            } else if (get_selectedBrowserType() == BrowserType.Firefox) {
                SetFireFoxDriver();
            }
            pageHelper.UpdateTestResults(FRAMED + ANSI_GREEN_BACKGROUND + ANSI_BLUE + ANSI_BOLD + pageHelper.sectionStartFormatLeft + "Ending Configuration" + pageHelper.sectionStartFormatRight + ANSI_RESET, testResults);
        }
    }


    /* ***************************************************************************
     *  DESCRIPTION:
     *    Runs all of the tests read in from the test settings file.
     **************************************************************************** */
    @Test   //xpath lookup in this method does not work with headless phantomJS
    public void TestHomePage() throws Exception {
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


    /* ***************************************************************************
     *  DESCRIPTION:
     *  Calls the ReadConfigurationSettings method, to read the config file.
     *  Sets configurable variables using those values.
     *************************************************************************** */
    private void ConfigureTestEnvironment() throws Exception {
        String tmpBrowserType;
        ConfigSettings configSettings = pageHelper.ReadConfigurationSettings(configurationFile);

        if (configSettings != null) {
            tmpBrowserType = configSettings.get_browserType().toLowerCase();
            if (tmpBrowserType.indexOf("chrome") >= 0) {
                set_selectedBrowserType(BrowserType.Chrome);
            } else if (tmpBrowserType.indexOf("firefox") >= 0) {
                set_selectedBrowserType(BrowserType.Firefox);
            } else {
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
        } else {
            pageHelper.UpdateTestResults("configSettings is null!!!", testResults);
        }
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the PhantomJs Driver
     **************************************************************************** */
    private void SetPhantomJsDriver() {
        pageHelper.UpdateTestResults("     [Setting PhantomJSDriver]", testResults);
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
        pageHelper.UpdateTestResults("     [Setting ChromeDriver]", testResults);
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
        pageHelper.UpdateTestResults("     [Setting FireFoxDriver]", testResults);
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
     *    Runs all tests read in from the test settings file.
     **************************************************************************** */
    public void TestPageElements() throws Exception {
        int startIndex = 0;  //used for instances when you do not want to start at the first element to test
        String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
        boolean revertToParent = false;
        //boolean isError = false;

        for (int fileIndex = 0; fileIndex < testFiles.size(); fileIndex++) {
            testFileName = testFiles.get(fileIndex);
            testSettings = new ArrayList<>();
            testSettings = pageHelper.ReadTestSettingsFile(testSettings, testFileName);
            //pageHelper.UpdateTestResults(FRAMED + ANSI_PURPLE_BACKGROUND + ANSI_YELLOW + "------[ Running Test Script ]-------" + ANSI_RESET, testResults);
            pageHelper.UpdateTestResults(FRAMED + ANSI_PURPLE_BACKGROUND + ANSI_YELLOW + pageHelper.sectionStartFormatLeft + "Running Test Script" + pageHelper.sectionStartFormatRight + ANSI_RESET, testResults);
            for (int x = startIndex; x < testSettings.size(); x++) {
                if (revertToParent) {
                    driver.switchTo().defaultContent();
                    //pageHelper.UpdateTestResults( PageHelper.ANSI_CYAN + "-------[ End Switch to IFrame - Reverting to defaultContent ]-------" + PageHelper.ANSI_RESET);
                    pageHelper.UpdateTestResults( PageHelper.ANSI_CYAN + pageHelper.sectionEndFormatLeft + "End Switch to IFrame - Reverting to defaultContent" + pageHelper.sectionEndFormatRight + PageHelper.ANSI_RESET);
                    revertToParent = false;
                }
                //isError = false;
                TestSettings ts = testSettings.get(x);
                String expected = ts.get_expectedValue();
                String accessor = ts.get_xPath();
                String fileStepIndex = "_F" + fileIndex + "_S" + x + "_";
                String fileStepIndexForLog = "F" + fileIndex + "_S" + x;
                //pageHelper.UpdateTestResults("#1 expected = " + expected);
                //if switching to an iframe, switch first
                if (expected.toLowerCase().contains("switch to iframe"))
                {
                    //String [] expectedItems = expected.split(" - ");
                    String [] expectedItems = expected.split(" ╬ ");
                    String frameName = expectedItems[0].substring(expectedItems[0].indexOf("[") + 1, expectedItems[0].indexOf("]"));
                    //pageHelper.UpdateTestResults(PageHelper.ANSI_CYAN + "-------[ Switching to iFrame: " + frameName + " for step " + fileStepIndexForLog + " ]-------" + PageHelper.ANSI_RESET);
                    pageHelper.UpdateTestResults(PageHelper.ANSI_CYAN + pageHelper.sectionStartFormatLeft +  "Switching to iFrame: " + frameName + " for step " + fileStepIndexForLog + pageHelper.sectionStartFormatRight + PageHelper.ANSI_RESET);
                    driver.switchTo().frame(frameName);
                    expected = expectedItems[1];
                    revertToParent = true;
                }

                //get value and check against expected
                if (!ts.getPerformWrite()) {
                    //pageHelper.UpdateTestResults("Element type being checked is <" + accessor.substring(accessor.lastIndexOf("/") + 1).trim(), testResults);

                    String actual = "";
                    //pageHelper.UpdateTestResults("Search Type = " + ts.get_searchType());

                    if (ts.get_searchType().toLowerCase().equals("xpath")) {
                        pageHelper.UpdateTestResults("     Element type being checked at step " + fileStepIndexForLog +  " by xPath: " + accessor, testResults);
                        //actual = CheckElementWithXPath(accessor, ts, fileStepIndex);
                        actual = CheckElementWithXPath(ts, fileStepIndex);
                    } else if (ts.get_searchType().toLowerCase().equals("cssselector")) {
                        pageHelper.UpdateTestResults("     Element type being checked at step " + fileStepIndexForLog +  " by CssSelector: " + accessor, testResults);
                        //actual = CheckElementWithCssSelector(accessor, fileStepIndex);
                        actual = CheckElementWithCssSelector(ts, fileStepIndex);
                    } else if (ts.get_searchType().toLowerCase().equals("tagname")) {
                        pageHelper.UpdateTestResults("     Element type being checked at step " + fileStepIndexForLog + " by TagName: " + accessor, testResults);
                        //actual = CheckElementWithTagName(accessor, fileStepIndex);
                        actual = CheckElementWithTagName(ts, fileStepIndex);
                    } else if (ts.get_searchType().toLowerCase().equals("classname")) {
                        pageHelper.UpdateTestResults("     Element type being checked at step " + fileStepIndexForLog + " by ClassName: " + accessor, testResults);
                        //actual = CheckElementWithClassName(accessor, fileStepIndex);
                        actual = CheckElementWithClassName(ts, fileStepIndex);
                    } else if (ts.get_searchType().toLowerCase().equals("id")) {
                        pageHelper.UpdateTestResults("     Element type being checked at step " + fileStepIndexForLog + " by Id: " + accessor, testResults);
                        //actual = CheckElementWithId(accessor, fileStepIndex);
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
                        pageHelper.UpdateTestResults("Successful comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")", testResults);
                    } else if (!expected.equals(actual)) {
                        pageHelper.UpdateTestResults("Failed comparison results at step " + fileStepIndexForLog + " Expected value: (" + expected + ") Actual value: (" + actual + ")", testResults);
                        if (screenShotSaveFolder != null && !screenShotSaveFolder.isEmpty()) {
                            //pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "AssertFail" + expected.replace(' ', '_'), screenShotSaveFolder);
                            pageHelper.captureScreenShot(driver, browserUsed + fileStepIndex + "Assert_Fail", screenShotSaveFolder, false);
                        }
                    }
                } else {  //set a value or perform a click
                    //pageHelper.UpdateTestResults("Performing non-read action", testResults);
                    Boolean status;
                    //int dashCount = ts.get_expectedValue().contains(" - ") ? StringUtils.countMatches(ts.get_expectedValue(), " - ") : 0;
                    int dashCount = ts.get_expectedValue().contains(" ╬ ") ? StringUtils.countMatches(ts.get_expectedValue(), " ╬ ") : 0;
                    //pageHelper.UpdateTestResults("dashCount = " + dashCount, testResults);

                    //GAJ working here perform all non read actions below that use an accessor
                    if ((ts.get_searchType().toLowerCase().indexOf("xpath") >= 0) || (ts.get_searchType().toLowerCase().indexOf("cssselector") >= 0) ||
                            (ts.get_searchType().toLowerCase().indexOf("tagname") >= 0) || (ts.get_searchType().toLowerCase().indexOf("id") >= 0) ||
                            (ts.get_searchType().toLowerCase().indexOf("classname") >= 0)){
                        pageHelper.UpdateTestResults("     Performing " + ts.get_searchType() + " " + fileStepIndexForLog + " non-read action", testResults);
                        //String [] expectedItems = ts.get_expectedValue().split(" - ");
                        String [] expectedItems = ts.get_expectedValue().split(" ╬ ");
                        String subAction = null;

                        if (!ts.get_expectedValue().toLowerCase().contains("switch to iframe")) {
                            status = PerformAction(ts.get_searchType(), ts.get_xPath(), ts.get_expectedValue(), fileStepIndex);
                        }
                        else {
                            subAction = expectedItems[1];
                            status = PerformAction(ts.get_searchType(), ts.get_xPath(), subAction, fileStepIndex);
                        }

                        //region { refactored and no longer used }
                        /*if (ts.get_searchType().toLowerCase().indexOf("xpath") >= 0) {
                            status = PerformXPathAction(ts.get_xPath(), ts.get_expectedValue());
                        }
                        else if (ts.get_searchType().toLowerCase().indexOf("cssselector") >= 0) {
                            status = PerformCssSelectorAction(ts.get_xPath(), ts.get_expectedValue());
                        }
                        else if (ts.get_searchType().toLowerCase().indexOf("tagname") >= 0) {
                            status = PerformTagNameAction(ts.get_xPath(), ts.get_expectedValue());
                        }
                        else if (ts.get_searchType().toLowerCase().indexOf("id") >= 0) {
                            status = PerformIdAction(ts.get_xPath(), ts.get_expectedValue());
                        }
                        else if (ts.get_searchType().toLowerCase().indexOf("classname") >= 0) {
                            status = PerformClassAction(ts.get_xPath(), ts.get_expectedValue());
                        }*/
                        //endregion
                        //if (ts.get_expectedValue().toLowerCase().indexOf(" - ") >= 0 && subAction == null) {
                        if (ts.get_expectedValue().toLowerCase().indexOf(" ╬ ") >= 0 && subAction == null && !ts.get_expectedValue().toLowerCase().contains("right click")) {
                            //url has changed, check url against expected value
                            //String expectedUrl = ts.get_expectedValue().substring(ts.get_expectedValue().indexOf(" - ") + 3).trim();
                            String expectedUrl = ts.get_expectedValue().substring(ts.get_expectedValue().indexOf(" ╬ ") + 3).trim();

                            if (dashCount > 1) {
                                //int delayMilliSeconds = parseInt(ts.get_expectedValue().substring(ts.get_expectedValue().lastIndexOf("-") + 1).trim());
                                int delayMilliSeconds = parseInt(ts.get_expectedValue().substring(ts.get_expectedValue().lastIndexOf(" ╬ ") + 3).trim());
                                DelayCheck(delayMilliSeconds, fileStepIndex);
                                //expectedUrl = expectedUrl.substring(0, expectedUrl.indexOf(" - "));
                                expectedUrl = expectedUrl.substring(0, expectedUrl.indexOf(" ╬ "));
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
                                        //pageHelper.captureScreenShot(driver, browserUsed + ts.get_searchType() + "_Element_Not_Found", configurationFolder);
                                        pageHelper.captureScreenShot(driver, browserUsed  + ts.get_searchType()  + fileStepIndex + "Element_Not_Found" + expected.replace(' ', '_'), configurationFolder, true);
                                    } else {
                                        //pageHelper.captureScreenShot(driver, browserUsed + ts.get_searchType() + "_Element_Not_Found", screenShotSaveFolder);
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
                    } else if (ts.get_searchType().toLowerCase().indexOf("n/a") >= 0) {
                        //perform all non-read actions below that do not use an accessor
                        if (ts.get_expectedValue().toLowerCase().indexOf("navigate") >= 0) {
                            String navigateUrl = ts.get_xPath();
                            //String [] expectedItems = ts.get_expectedValue().split(" - ");
                            String [] expectedItems = ts.get_expectedValue().split(" ╬ ");
                            String expectedUrl = null;
                            int delayMilliSeconds = 0;
                            if (dashCount > 0) {
                                expectedUrl = expectedItems[1].trim();
                                //pageHelper.UpdateTestResults("----[ " + ts.get_expectedValue().toLowerCase() + "]------------------", testResults);
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
                                        pageHelper.UpdateTestResults("     Setting browser dimensions to (Width=" + width + " Height=" + height, testResults);
                                        pageHelper.SetWindowContentDimensions(driver, width, height);
                                    }
                                }
                            }
                            this.testPage = navigateUrl;
                            //pageHelper.UpdateTestResults("----[ Start Explicit Navigation Event ]------------------", testResults);
                            pageHelper.UpdateTestResults(pageHelper.sectionStartFormatLeft + "Start Explicit Navigation Event" + pageHelper.sectionStartFormatRight, testResults);
                            String actualUrl = CheckPageUrl(delayMilliSeconds);
                            if (expectedUrl != null && expectedUrl.trim().length() > 0) {
                                assertEquals(expectedUrl, actualUrl);
                                if (expectedUrl.trim().equals(actualUrl.trim())) {
                                    pageHelper.UpdateTestResults("     Navigation and URL Check successful for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")", testResults);
                                } else {
                                    pageHelper.UpdateTestResults("     Navigation and URL Check unsuccessful for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")", testResults);
                                }
                            }
                            //pageHelper.UpdateTestResults("----[ End Explicit Navigation Event ]------------------", testResults);
                            pageHelper.UpdateTestResults(pageHelper.sectionEndFormatLeft + "End Explicit Navigation Event" + pageHelper.sectionEndFormatRight, testResults);
                        }
                        else if (ts.get_expectedValue().toLowerCase().indexOf("wait") >= 0 || ts.get_expectedValue().toLowerCase().indexOf("delay") >= 0) {
                            int delayMilliSeconds = 0;
                            if (ts.get_expectedValue().indexOf("╬") > 0) {
                                //delayMilliSeconds = parseInt(ts.get_expectedValue().substring(ts.get_expectedValue().lastIndexOf("-") + 1).trim());
                                delayMilliSeconds = parseInt(ts.get_expectedValue().substring(ts.get_expectedValue().lastIndexOf(" ╬ ") + 3).trim());
                            } else {
                                delayMilliSeconds = parseInt(ts.get_xPath());
                            }
                            DelayCheck(delayMilliSeconds, fileStepIndex);
                        }
                        else if (ts.get_expectedValue().toLowerCase().indexOf("screenshot") >= 0) {
                            //PerformScreenShotCapture(browserUsed + testFileName + ts.get_searchType() + fileStepIndex + ts.get_xPath());
                            PerformScreenShotCapture(browserUsed + ts.get_expectedValue() + fileStepIndex);
                        }
                        else if (ts.get_expectedValue().toLowerCase().indexOf("url") >= 0) {
                            //pageHelper.UpdateTestResults("----[ URL if before call ]------------------", testResults);
                            //String [] expectedItems = ts.get_expectedValue().split(" - ");
                            String [] expectedItems = ts.get_expectedValue().split(" ╬ ");
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
                                pageHelper.UpdateTestResults(PageHelper.ANSI_GREEN + "URL Check successful for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")" + ANSI_RESET, testResults);
                            } else {
                                pageHelper.UpdateTestResults(PageHelper.ANSI_RED + "URL Check unsuccessful for step " + fileStepIndexForLog + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")" + ANSI_RESET, testResults);
                            }
                            //pageHelper.UpdateTestResults("----[ URL if after call ]------------------", testResults);
                        }
                        else if (ts.get_expectedValue().toLowerCase().contains("switch to tab")) {
                            if (ts.get_expectedValue().toLowerCase().contains("0")) {
                                SwitchToTab(false, fileStepIndex);
                            }
                            else {
                                SwitchToTab(true, fileStepIndex);
                            }
                        }
//                        else if (ts.get_expectedValue().toLowerCase().contains("sendkeys")) {
//                            status = PerformAction(ts.get_searchType(), ts.get_xPath(), ts.get_expectedValue(), fileStepIndex);
//                        }
                    }
                }
            }
            pageHelper.UpdateTestResults(FRAMED + ANSI_PURPLE_BACKGROUND + ANSI_YELLOW + pageHelper.sectionEndFormatLeft + "End of Test Script" + pageHelper.sectionEndFormatRight + ANSI_RESET, testResults);
        }
        driver.quit();

        //chromedriver does not shut down from memory so you have to kill the process programmatically
        if (this.driver.toString().indexOf("Chrome") >= 0) {
            ShutDownChromeDriver();
        }
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
        pageHelper.UpdateTestResults("     Sleeping for " + milliseconds + " milliseconds for script " + fileStepIndexForLog, testResults);
        Thread.sleep(milliseconds);
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
     *      Returns the text of an element using its xPath accessor.
     ************************************************************ */
   // public String CheckElementWithXPath(String accessor, TestSettings ts, String fileStepIndex) throws Exception {
    public String CheckElementWithXPath(TestSettings ts, String fileStepIndex) throws Exception {
        String actualValue = null;
        String accessor = ts.get_xPath();

          //region { Removed but was initially here to ensure app was on the correct page }
//        if (this.driver.getCurrentUrl() != testPage) {
//            pageHelper.NavigateToPage(this.driver, testPage);
//        }
        //endregion
        try {
            //pageHelper.UpdateTestResults("CheckElementWithXPath iframeResult in try block " + ts.get_expectedValue().toLowerCase());
            String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
            //actualValue = this.driver.findElement(By.xpath(accessor)).getText();
            String typeOfElement = this.driver.findElement(By.xpath(accessor)).getAttribute("type");
            if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.xpath(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            }
            else {
                actualValue = this.driver.findElement(By.xpath(accessor)).getText();
            }
            pageHelper.UpdateTestResults("     Checking element by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
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
            pageHelper.UpdateTestResults("     Checking element by CssSelector: " + accessor + " for script " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
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
            pageHelper.UpdateTestResults("     Checking element by TagName: " + ElementTypeLookup(accessor) + " for script. " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
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
            pageHelper.UpdateTestResults("     Checking element by ClassName: " + accessor + " for script. " + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
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
           pageHelper.UpdateTestResults("     Checking element by ID: " + accessor + " for script." + fileStepIndexForLog + " Actual Value: \"" + actualValue + "\"", testResults);
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
        //pageHelper.UpdateTestResults("In Perform action value = " + value);
        Boolean status = false;
        final String click = "click";
        final String sendKeys = "sendkeys";
        final String rightClick = "right click";
        final String keys = "keys.";

        //if this is a click event, click it
        if (value.toLowerCase().contains(click) && !value.toLowerCase().contains(sendKeys)) {
            try {
                if (accesssorType.toLowerCase().equals("xpath")) {
                    if (!value.toLowerCase().contains(rightClick)) {
                        this.driver.findElement(By.xpath(accessor)).click();
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
                        this.driver.findElement(By.id(accessor)).click();
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
                        this.driver.findElement(By.className(accessor)).click();
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
                        this.driver.findElement(By.cssSelector(accessor)).click();
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
                        this.driver.findElement(By.tagName(accessor)).click();
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
                    String [] values = value.split(" ╬ ");
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
        String [] additionalCommands = value.split(" ╬ ");
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
        /*
        if (downCount > 0) {
            pageHelper.UpdateTestResults("     Arrowing down " + downCount + " times.", testResults);
        }
        else if (upCount > 0) {
            pageHelper.UpdateTestResults("     Arrowing up " + downCount + " times.", testResults);
        }
        else if (leftCount > 0) {
            pageHelper.UpdateTestResults("     Arrowing left " + downCount + " times.", testResults);
        }
        else if (rightCount > 0) {
            pageHelper.UpdateTestResults("     Arrowing right " + downCount + " times.", testResults);
        }*/

        Robot robot = new Robot();

        for (int x=0;x<downCount;x++) {
            robot.keyPress(KeyEvent.VK_DOWN);
            pageHelper.UpdateTestResults("     Performing Key down action!", testResults);
        }
        for (int x=0;x<upCount;x++) {
            robot.keyPress(KeyEvent.VK_UP);
            pageHelper.UpdateTestResults("     Performing Key up action!", testResults);
        }
        for (int x=0;x<leftCount;x++) {
            robot.keyPress(KeyEvent.VK_LEFT);
            pageHelper.UpdateTestResults("     Performing Key left action!", testResults);
        }
        for (int x=0;x<rightCount;x++) {
            robot.keyPress(KeyEvent.VK_RIGHT);
            pageHelper.UpdateTestResults("     Performing Key right action!", testResults);
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
        pageHelper.UpdateTestResults("     Switched to New tab with url = " + driver.getCurrentUrl(), testResults);
    }

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
        pageHelper.UpdateTestResults("     Switching to the original tab", testResults);
        //new Actions(driver).sendKeys(driver.findElement(By.tagName("html")), Keys.CONTROL).sendKeys(driver.findElement(By.tagName("html")),Keys.NUMPAD1).build().perform();
        //new Actions(driver).sendKeys(Keys.CONTROL + "\t");
        pageHelper.UpdateTestResults("Switched back to Main tab with url = " + driver.getCurrentUrl());
    }
*/

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
        pageHelper.UpdateTestResults("     Looking up elementTag: (" + elementTag + ") Length = " + elementTag.length(), testResults);

        if (elementTag.toLowerCase().indexOf("a") >= 0 && (elementTag.length() == 1 || elementTag.toLowerCase().indexOf("[") > 1)) {
            return "Anchor";
        }
        if (elementTag.toLowerCase().indexOf("h") >= 0 && (elementTag.length() == 2 || elementTag.toLowerCase().indexOf("[") > 1)) {
            //System.out.println("This is true!!!!");
            return "Heading";
        }
        if (elementTag.toLowerCase().indexOf("li") >= 0 && elementTag.length() >= 2) {
            //System.out.println("This is true!!!!");
            return "List Item";
        }
        if (elementTag.toLowerCase().indexOf("span") >= 0 && elementTag.length() >= 4) {
            //System.out.println("This is true!!!!");
            return "Span";
        }
        if (elementTag.toLowerCase().indexOf("div") >= 0 && elementTag.length() >= 3) {
            //System.out.println("This is true!!!!");
            return "Div";
        }
        if (elementTag.toLowerCase().indexOf("p") >= 0 && elementTag.length() >= 1) {
            //System.out.println("This is true!!!!");
            return "Paragraph";
        }
        if (elementTag.toLowerCase().indexOf("img") >= 0 && elementTag.length() >= 3) {
            //System.out.println("This is true!!!!");
            return "Image";
        }
        if (elementTag.toLowerCase().indexOf("select") >= 0 || elementTag.toLowerCase().equals("select")) {
            //System.out.println("This is true!!!!");
            return "Select";
        }
        //region { values not equal but not sure why - update when comparing strings must use .equals or it compares mem address}
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

    private CharSequence GetKeyValue(String value, String fileStepIndex) {
        value = value.toLowerCase().trim();
        String fileStepIndexForLog = fileStepIndex.substring(1, fileStepIndex.length() - 1);
        pageHelper.UpdateTestResults("     Replacing (" + value + ") with corresponding Key value keyword for step " + fileStepIndexForLog);

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
        else
        {
            pageHelper.UpdateTestResults(PageHelper.ANSI_RED + "Key: " + value + fileStepIndex + " not mapped!" + ANSI_RESET, testResults);
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
            System.out.println(ANSI_GREEN + testMessage + ANSI_RESET);
        }
        else if (testMessage.indexOf("Failed") >= 0) {
            System.out.println(ANSI_RED + testMessage + ANSI_RESET);
        }
        else if (testMessage.indexOf("Navigation") >= 0) {
            System.out.println(ANSI_BLUE + testMessage + ANSI_RESET);
        }
        else {
            System.out.println(testMessage);
        }
    }*/
    //endregion
}
