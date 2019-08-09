import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.xpath.operations.Bool;
import org.openqa.selenium.WebDriver;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.ExceptionListener;
import java.io.File;
import java.io.IOException;
//import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.Integer.parseInt;
import static java.util.stream.LongStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.*;
enum BrowserTypes {
    Chrome, Firefox, PhantomJS, Internet_Explorer, Edge
}

public class TestCentral {

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
     *      3.  Screenshot comparison using Image Magic
     *          (https://www.swtestacademy.com/visual-testing-imagemagick-selenium/)
     *
     ╚═══════════════════════════════════════════════════════════════════════════════╝ */
     //endregion

    //region { constants }
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
    //private String configurationFile = "Config/ConfigurationSetup.tconfig";
    private String configurationFile = "Config/ConfigurationSetup.xml";
    private String configurationFolder = "Config/";
    private static String testPage = "https://www.myWebsite.com/";
    private boolean runHeadless = true;
    private String screenShotSaveFolder;
    private BrowserTypes _selectedBrowserType; // = BrowserType.Firefox;    //BrowserType.Chrome;  //BrowserType.PhantomJS;
    private int maxBrowsers = 3;
    //endregion

    private WebDriver driver;
    private TestHelper testHelper = new TestHelper();
    private boolean testAllBrowsers = false;  //true;
    List<TestStep> testSteps = new ArrayList<TestStep>();
    private String testFileName;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
    private String logFileUniqueName = dateFormat.format(new Date());
    private String logFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\TestResults_" + logFileUniqueName + ".log" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/TestResults_" + logFileUniqueName + ".log";
    private String helpFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\ConfigTester_Help.txt" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/ConfigTester_Help.txt";
    List<String> testFiles = new ArrayList<>();
    private String chromeDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/chromedriver.exe";
    private String fireFoxDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/geckodriver.exe";
    private String phantomJsDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/phantomjs.exe";
    private String internetExplorerDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/IEDriverServer.exe";
    private String edgeDriverPath = "/Users/gjackson/Downloads/BrowserDrivers/msedgedriver.exe";

    private boolean _executedFromMain = false;
    private int brokenLinksStatusCode;
    private MongoClient mongoClient = null;
    private Connection sqlConnection = null;
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

    public BrowserTypes get_selectedBrowserType() {
        return _selectedBrowserType;
    }

    public void set_selectedBrowserType(BrowserTypes newValue) {
        if (newValue == BrowserTypes.Chrome) {
            this._selectedBrowserType = BrowserTypes.Chrome;
        } else if (newValue == BrowserTypes.Firefox) {
            this._selectedBrowserType = BrowserTypes.Firefox;
        } else if (newValue == BrowserTypes.Internet_Explorer) {
            this._selectedBrowserType = BrowserTypes.Internet_Explorer;
        } else if (newValue == BrowserTypes.Edge) {
            this._selectedBrowserType = BrowserTypes.Edge;
        }  else {
            this._selectedBrowserType = BrowserTypes.PhantomJS;
        }
    }
    //endregion


    /**************************************************************
     * DESCRIPTION: Constructor
     * @throws Exception
     ***************************************************************/
    public TestCentral() throws Exception {
//        ColorUtility();  // This method displays colors for use in the application
        //created this default constructor so that this could function as an application
        //and run from the main method in Form.java so that if the configuration file is
        // not in the proper location, the correct path can be specified as input.
    }



    /****************************************************************************
     *  DESCRIPTION: Runs all of the tests read in from the test settings file.
     *
     **************************************************************************** */
    @Test
    public void ConfigurableTestController() throws Exception {
        TestCentralStart(is_executedFromMain());
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
                         /*
                    case 3:
                        SetInternetExplorerDriver();
                        break;
                    case 4:
                        SetEdgeDriver();
                        break;
                        */
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


    /**************************************************************************
     * Description: Default Constructor.  Reads the configuration file
     *              and the associated test file and when a site is not being
     *              tested using all browsers, it sets the browser that will
     *              be used for the test.
     * @param isStartedFromMain
     * @throws Exception
     ***************************************************************************/
    public void TestCentralStart(boolean isStartedFromMain) throws Exception {
        this.set_executedFromMain(isStartedFromMain);

        File tmp = new File(configurationFile);
        //testHelper.UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + "╔" + testHelper.PrePostPad(AppConstants.ANSI_RESET + AppConstants.ANSI_BRIGHTBLUE  +"[ Starting Test Application Initialization ]" + AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD, "═", 9, 157) + "╗" + AppConstants.ANSI_RESET, false);
        testHelper.CreateSectionHeader("[ Starting Test Application Initialization ]", AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, true, false, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTBLUE + AppConstants.indent5 +  "Config File absolute path = " + AppConstants.ANSI_RESET + tmp.getAbsolutePath(), false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTBLUE + AppConstants.indent5 +  "Log File Name = " + AppConstants.ANSI_RESET  + logFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTBLUE + AppConstants.indent5 +  "Help File Name = " + AppConstants.ANSI_RESET + helpFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTBLUE + AppConstants.indent5 + "Executed From Main or as JUnit Test = " + AppConstants.ANSI_RESET + (is_executedFromMain() ? "Standalone App" : "JUnit Test"), false);
        //testHelper.UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + "╚" + testHelper.PrePostPad(AppConstants.ANSI_RESET + AppConstants.ANSI_BRIGHTBLUE  +"[ End Test Application Initialization ]" + AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD, "═", 9, 157) + "╝" + AppConstants.ANSI_RESET, false);
        testHelper.CreateSectionHeader("[ End Test Application Initialization ]", AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, false, false, false);
        testHelper.UpdateTestResults("", false);

        testHelper.set_logFileName(logFileName);
        testHelper.set_helpFileName(helpFileName);

        boolean status = ConfigureTestEnvironment();
        if (!status) {
            return;
        }

        //testHelper.UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_GREEN_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + "╔" + testHelper.PrePostPad("[ Beginning Configuration ]", "═", 9, 157) + "╗" + AppConstants.ANSI_RESET, false);
        testHelper.CreateSectionHeader("[ Beginning Configuration ]", AppConstants.FRAMED + AppConstants.ANSI_GREEN_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, true, true, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + AppConstants.indent5 + "Configured Browser Selection = " + AppConstants.ANSI_RESET + get_selectedBrowserType(), true);

        if (!testAllBrowsers) {
            if (get_selectedBrowserType() == BrowserTypes.PhantomJS) {
                SetPhantomJsDriver();
            } else if (get_selectedBrowserType() == BrowserTypes.Chrome) {
                SetChromeDriver();
            } else if (get_selectedBrowserType() == BrowserTypes.Firefox) {
                SetFireFoxDriver();
            } else if (get_selectedBrowserType() == BrowserTypes.Internet_Explorer) {
                SetInternetExplorerDriver();
            }else if (get_selectedBrowserType() == BrowserTypes.Edge) {
                SetEdgeDriver();
            }
            //testHelper.UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_GREEN_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + "╚" + testHelper.PrePostPad("[ Ending Configuration ]", "═", 9, 157) + "╝" + AppConstants.ANSI_RESET, false);
            testHelper.CreateSectionHeader("[ Ending Configuration ]", AppConstants.FRAMED + AppConstants.ANSI_GREEN_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, false, true, false);
        }
    }


    /****************************************************************************
     *  DESCRIPTION:
     *  Calls the ReadConfigurationSettings method, to read the config file.
     *  Sets configurable variables using those values.
     *************************************************************************** */
    private boolean ConfigureTestEnvironment() throws Exception {
        String tmpBrowserType;
        ConfigSettings configSettings = testHelper.ReadConfigurationSettingsXmlFile(configurationFile, is_executedFromMain());

        if (configSettings != null) {
            tmpBrowserType = configSettings.get_browserType().toLowerCase();
            if (tmpBrowserType.indexOf("chrome") >= 0) {
                set_selectedBrowserType(BrowserTypes.Chrome);
            } else if (tmpBrowserType.indexOf("firefox") >= 0) {
                set_selectedBrowserType(BrowserTypes.Firefox);
            } /* else if (tmpBrowserTypes.indexOf("internetexplorer") >= 0 || tmpBrowserType.indexOf("internet explorer") >= 0) {
                set_selectedBrowserType(BrowserType.Internet_Explorer);
            } else if (tmpBrowserTypes.indexOf("edge") >= 0) {
                set_selectedBrowserType(BrowserType.Edge);
            } */ else {
                set_selectedBrowserType(BrowserTypes.PhantomJS);
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
           testHelper.UpdateTestResults("configSettings is null!!!", true);
            return false;
        }
    }


    /****************************************************************************
     *  DESCRIPTION:
     *    Runs all tests read in from the test settings file.
     **************************************************************************** */
    public void TestPageElements() throws Exception {
        if (this.driver == null) {
            return;
        }
        int startIndex = 0;  //used for instances when you do not want to start at the first element to test
        boolean revertToParent = false;


        for (int fileIndex = 0; fileIndex < testFiles.size(); fileIndex++) {
            testFileName = testFiles.get(fileIndex);
            //Start - reset this for each test file
            //moved this here so that the Unique Identifier is created for each test file.
            uniqueId = testHelper.GetUniqueIdentifier();
            testSteps = new ArrayList<>();
            testSteps = testHelper.ReadTestSettingsXmlFile(testSteps, testFileName);
            persistedString = null;
            //End - reset this for each test file
            //testHelper.UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_YELLOW + AppConstants.sectionLeftDown + testHelper.PrePostPad("[ Running Test Script ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
            testHelper.CreateSectionHeader("[ Running Test Script ]", AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW, true, true, true);
            testHelper.UpdateTestResults("Running Test Script file: " + testFileName, true);
            for (int x = startIndex; x < testSteps.size(); x++) {
                if (revertToParent) {
                    driver.switchTo().defaultContent();
                    testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                    revertToParent = false;
                }
                TestStep ts = testSteps.get(x);
                String fileStepIndex = "F" + fileIndex + "_S" + x;

                if (ts.get_command().toLowerCase().contains("switch to iframe"))
                {
                    String frameName = GetArgumentValue(ts, 0, null);
                    testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionTopLeft + testHelper.PrePostPad("[ Switching to iFrame: " + frameName + " for step " + fileStepIndex + " ]", "═", 9, 157) + AppConstants.iFrameSectionTopRight + AppConstants.ANSI_RESET, false);
                    if (frameName != null && !frameName.isEmpty()) {
                        driver.switchTo().frame(frameName);
                    }
                    revertToParent = true;
                }

                //testHelper.DebugDisplay("Command:" + ts.get_command() + " Action Type:" + ts.get_actionType().toLowerCase());
                if (ts.get_actionType().toLowerCase().equals("write")) {
                    PerformWriteActions(ts, fileStepIndex);
                } else {
                    PerformReadActions(ts, fileStepIndex);
                }
                if (revertToParent) {
                    driver.switchTo().defaultContent();
                    testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                    revertToParent = false;
                }
            }
            //testHelper.UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_YELLOW  + AppConstants.sectionLeftUp + testHelper.PrePostPad("[ End of Test Script ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
            testHelper.CreateSectionHeader("[ End of Test Script ]", AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW, false, true, true);
        }
        PerformCleanup();
        driver.close();
        driver.quit();
    }

    /*****************************************************************
     * Description: Created to clean up any resources that have not been
     *              destroyed/closed at this point.
     * @throws SQLException
     *****************************************************************/
    private void PerformCleanup() throws SQLException {
        CloseOpenConnections();
        if (this.driver.toString().indexOf("Chrome") >= 0) {
            ShutDownChromeDriver();
        }
    }

    /*************************************************************
     * DESCRIPTION:  Shuts down the Chrome Driver.
     *
     * FURTHER EXPLANATION:
     *      For some reason, Chrome Driver notoriously does not shut
     *      down when the test completes although other drivers do,
     *      so this fixes that by shutting down all instances of
     *      the Chrome Driver currently running on your machine.
     ************************************************************ */
    private void ShutDownChromeDriver(){
        try {
            // Execute command
            String command = "taskkill /im chromedriver.exe /f";
            Process child = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            testHelper.UpdateTestResults("The following error occurred while trying to shut down ChromeDriver: " + e.getMessage(), true);
        }
    }

    /*********************************************************************
     * Description: Will close any open connections that have not been
     *              explicitly closed with a Test Step.
     ******************************************************************* */
    private void CloseOpenConnections() throws SQLException {
        if (sqlConnection != null) {
            sqlConnection.close();
            testHelper.UpdateTestResults("Closed open Sql Server Connection!!!", true);
        }
        if (mongoClient != null) {
            mongoClient.close();
            testHelper.UpdateTestResults("Closed open MongoDb Connection!!!", true);
        }
    }

    /*************************************************************************
     * Description: Closes a database connection object based on the parameters
     *              passed in.
     * @param databaseConnectionType
     * @param fileStepIndex
     * @throws SQLException
     *************************************************************************/
    private void CloseOpenConnections(String databaseConnectionType, String fileStepIndex) throws SQLException {
        if (databaseConnectionType == AppConstants.SqlServer) {
            if (sqlConnection != null) {
                sqlConnection.close();
                testHelper.UpdateTestResults("Successful closing of open Sql Server Connection for step " + fileStepIndex, true);
            }
        } else if (databaseConnectionType == AppConstants.MongoDb) {
            if (mongoClient != null) {
                mongoClient.close();
                testHelper.UpdateTestResults("Successful closing of open MongoDb Connection for step " + fileStepIndex, true);
            }
        }
    }


    /*********************************************************************
     * Description: This method returns the browser used as a string
     * @return Browser used by the driver.
     *********************************************************************/
    private String GetBrowserUsed() {

        String browserUsed = this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";

        return browserUsed;
    }


    /**************************************************************************
     * Description: Retrieves the argument value at the specified index
     *              and returns it if available or it returns the defaultValue
     *              parameter passed in.
     * @param ts
     * @param index
     * @param defaultValue
     * @return
     **************************************************************************/
    private String GetArgumentValue(TestStep ts, int index, String defaultValue) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;

        if (arg!=null) {
            return arg.get_parameter();
        } else {
            return defaultValue;
        }
    }

    /****************************************************************
     * Description: This method gets the ArgumentList Parameter String
     *              property based on the index passed in and if it
     *              exists, parses that string into an Integer  and
     *              returns it to the calling method.
     *              In the event that the value is null, it returns
     *              the default value passed in.
     * @param ts -
     * @param index -
     * @param defaultValue -
     * @return
     ****************************************************************/
    private int GetArgumentNumericValue(TestStep ts, int index, int defaultValue) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;

        if (arg!=null) {
            return parseInt(arg.get_parameter());
        } else {
            return defaultValue;
        }
    }

    /*****************************************************************
     * Description: Checks if the Argument retrieved at the specified
     *              index is numeric.
     * @param ts
     * @param index
     * @return - True if numeric, else False
     *****************************************************************/
    private Boolean CheckArgumentNumeric(TestStep ts, int index) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;
        int returnValueCheck;
        Boolean status = true;

        try {
            returnValueCheck = Integer.parseInt(arg.get_parameter());
        } catch (NumberFormatException ne){
            status = false;
        }

        return status;
    }


    /*****************************************************************
     * Description: This method performs all Read related actions.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************/
    private void PerformReadActions(TestStep ts, String fileStepIndex) throws Exception {
        if (ts.get_accessorType() != null && !ts.get_accessorType().toLowerCase().equals("n/a")) {
            //add different types of element checks here like img src, img alt, a href
            if (ts.get_command().toLowerCase().contains("check_image") || ts.get_command().toLowerCase().contains("check image") ) {
                CheckImageSrcAlt(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains("check_a_href") || ts.get_command().toLowerCase().contains("check a href")) {
                CheckAnchorHref(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals("persiststring") || ts.get_command().toLowerCase().equals("persist string")) {
                PersistValueController(ts, fileStepIndex);
            } else {
                CheckElementText(ts, fileStepIndex);
            }
        } else {
            if (ts.get_command().toLowerCase().contains("check") && (ts.get_command().toLowerCase().contains("post") ||
                    ts.get_command().toLowerCase().contains("get"))) {
                //refactored and moved to separate method
                CheckGetPostStatus(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains("check") && ts.get_command().toLowerCase().contains("links")) {
                String url = GetArgumentValue(ts, 0, testPage);

                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking page links for " + url, false);
                CheckBrokenLinks(url);
            } else if (ts.get_command().toLowerCase().contains("check") && ts.get_command().toLowerCase().contains("image"))
            {
                String url = GetArgumentValue(ts, 0, null);
                if (ts.get_command().toLowerCase().contains("alt")) {
                    CheckADAImages(url, "alt");
                } else if (ts.get_command().toLowerCase().contains("src")) {
                    CheckADAImages(url, "src");
                }
            } else if (ts.get_command().toLowerCase().contains("check") && ts.get_command().toLowerCase().contains("count")) {
                CheckElementCountController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains("check") && ts.get_expectedValue().toLowerCase().contains("contrast")) {
                ColorContrastController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains("query")) {
                //perform a database query
                DatabaseQueryController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains("find")) {
                FindPhraseController(ts, fileStepIndex);
            }
        }
    }

    /*****************************************************************
     * Description: This method performs all Write related actions.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************/
    private void PerformWriteActions(TestStep ts, String fileStepIndex) throws Exception {
        Boolean status;
        //Perform all non read actions below that use an accessor

        if (ts.get_accessorType() != null && (((ts.get_accessorType().toLowerCase().indexOf(xpathCheckValue) >= 0) || (ts.get_accessorType().toLowerCase().indexOf(cssSelectorCheckValue) >= 0) ||
                (ts.get_accessorType().toLowerCase().indexOf(tagNameCheckValue) >= 0) || (ts.get_accessorType().toLowerCase().indexOf(idCheckValue) >= 0) ||
                (ts.get_accessorType().toLowerCase().indexOf(classNameCheckValue) >= 0))
                && (!ts.get_command().toLowerCase().contains("sendkeys") && !ts.get_command().toLowerCase().contains("send keys")
                && !ts.get_command().toLowerCase().contains("wait") && !ts.get_command().toLowerCase().contains(persistStringCheckValue)))) {
            PerformAccessorActionController(ts, fileStepIndex);
        }  else if (ts.get_command().toLowerCase().contains("sendkeys") || ts.get_command().toLowerCase().contains("send keys")) {
            SendKeysController(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().contains("wait for")) {
            //wait for a speficic element to load
            WaitForElement(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().equals("connect to database")) {
            String databaseType = GetArgumentValue(ts, 0, null);
            if (databaseType.toLowerCase().equals("mongodb") || databaseType.toLowerCase().contains("mongo")) {
                //connect to mongo db or close an open mongo db connection
                SetMongoClient(ts, fileStepIndex);
            } else if (databaseType.toLowerCase().contains("sql server")) {
                //establish a connection to a sql server database - connection lives until closed or end of the test
                SetSqlServerClient(ts, fileStepIndex);
            }
        } else if (ts.get_command().toLowerCase().equals("close database connection") || ts.get_command().toLowerCase().equals("close database") ) {
            String databaseType = GetArgumentValue(ts, 0, null);
            if (databaseType.toLowerCase().equals(AppConstants.MongoDb.toLowerCase()) || databaseType.toLowerCase().contains("mongo")) {
                CloseOpenConnections(AppConstants.MongoDb, fileStepIndex);
            } else if (databaseType.toLowerCase().contains(AppConstants.SqlServer.toLowerCase())) {
                CloseOpenConnections(AppConstants.SqlServer, fileStepIndex);
            }
        } else if (ts.get_command() != null && ts.get_command().toLowerCase().contains(persistStringCheckValue)) {
            PersistValueController(ts,fileStepIndex);
        } else if (ts.get_accessorType() == null || ts.get_accessorType().toLowerCase().indexOf("n/a") >= 0 ) {
            //TODO: FIGURE OUT WHAT YOU WERE TROUBLESHOOTING WITH THESE MESSAGES WHEN YOU SET THIS APPLICATION ASIDE
//                        pageHelper.UpdateTestResults("SearchType = n/a  - Good so far - Accessor: " + ts.get_xPath() +
//                                " Expected Value:" + ts.get_expectedValue() + " Lookup Type: " + ts.get_searchType() +
//                                " Perform Action: " + ts.getPerformWrite() + " IsCrucial: " + ts.get_isCrucial());
            //perform all non-read actions below that do not use an accessor
            if (ts.get_command().toLowerCase().indexOf("navigate") >= 0) {
                PerformExplicitNavigation(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals("wait") || ts.get_command().toLowerCase().equals("delay")) {
                int delayMilliSeconds = GetArgumentNumericValue(ts, 0, 0);
                DelayCheck(delayMilliSeconds, fileStepIndex);
            } else if (ts.get_command().toLowerCase().indexOf("screenshot") >= 0) {
                //scheduled screenshot capture action
                testHelper.UpdateTestResults(AppConstants.indent5 + "Taking Screenshot for step " + fileStepIndex, false);
                PerformScreenShotCapture(GetBrowserUsed() + "_" + ts.get_expectedValue() + "_" + fileStepIndex + "_");
            } else if (ts.get_command().toLowerCase().indexOf("url") >= 0) {
                CheckUrlWithoutNavigation(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains("switch to tab")) {
                boolean isChild = (GetArgumentNumericValue(ts, 0, 1) == 1) ? true : false;
                if (ts.get_command().toLowerCase().contains("0") || !isChild) {
                    SwitchToTab(false, fileStepIndex);
                } else {
                    SwitchToTab(true, fileStepIndex);
                }
            } else if (ts.get_command().toLowerCase().contains("login")) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing login for step " + fileStepIndex, true);
                String userId = GetArgumentValue(ts, 0, null);
                String password = GetArgumentValue(ts, 1, null);
                String url = GetArgumentValue(ts, 2, "n/a");
                Login(url, userId, password, fileStepIndex);
                testHelper.UpdateTestResults(AppConstants.indent5 + "Login complete for step " + fileStepIndex, true);
            } else if (ts.get_command().toLowerCase().contains("create_test_page") || ts.get_command().toLowerCase().contains("create test page")) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Create Test Page for step " + fileStepIndex, true);
                String createTestFileName = CreateTestPage(ts, fileStepIndex);
                testHelper.UpdateTestResults("Create Test Page results written to file: " + createTestFileName, false);
            }
        }
    }



    //region { Controller Methods, used to control the program flow for the complicated items }
    /******************************************************************************
     * DESCRIPTION: Control method used to Check the count of a specific element type.
     * @param ts
     * @param fileStepIndex
     ******************************************************************************/
    private void CheckElementCountController(TestStep ts, String fileStepIndex) {
        String checkItem = GetArgumentValue(ts, 0, null);
        String url = GetArgumentValue(ts,1,null);
        String page = url == null ? driver.getCurrentUrl() : url;

        if (checkItem != null) {
            testHelper.UpdateTestResults(AppConstants.indent5 + "Checking count of " + checkItem + " on page " + page, false);
            int expectedCount = ts.get_expectedValue() != null ? parseInt(ts.get_expectedValue()) : 0;
            CheckElementCount(url, checkItem, expectedCount, fileStepIndex, ts.get_crucial());
        } else {
            ImproperlyFormedTest(fileStepIndex);
        }
    }

    /******************************************************************************
     * DESCRIPTION: Control method used to find a Phrase.
     * @param ts
     * @param fileStepIndex
     ******************************************************************************/
    private void FindPhraseController(TestStep ts, String fileStepIndex) {
        String cssSelector = GetArgumentValue(ts, 0, "*");
        String phrase = ts.get_expectedValue();
        String message;
        String containsOrEquals = GetArgumentValue(ts, 1, "equals");

        if (cssSelector != null && !cssSelector.trim().isEmpty() ) {
            if (!containsOrEquals.toLowerCase().equals("contains")) {
                message = "Performing find searching all '" + cssSelector.trim() + "' elements for '" + phrase.trim() + "'";
            } else {
                message = "Performing find searching all '" + cssSelector.trim() + "' elements containing '" + phrase.trim() + "'";
            }
        } else {
            if (!containsOrEquals.toLowerCase().contains("contains")) {
                message = "Performing find searching all elements for '" + phrase.trim() + "'";
            } else {
                message = "Performing find searching all elements containing '" + phrase.trim() + "'";
            }
        }
       testHelper.UpdateTestResults(AppConstants.indent5 + message + " for step " + fileStepIndex, true);
        FindPhrase(ts, fileStepIndex);
    }

    /******************************************************************************
     * DESCRIPTION: Control method used to Persist a value.
     * @param ts
     *
     * @param fileStepIndex
     * @param fileStepIndex
     *
     ******************************************************************************/
    private void PersistValueController(TestStep ts, String fileStepIndex) throws Exception {
        //testHelper.UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_CYAN + "╔" + testHelper.PrePostPad("[ Start Persisting Element Value ]", "═", 9, 152) + "╗" + AppConstants.ANSI_RESET, false);
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Element Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisting value found by: " + ts.get_accessorType() + " accessor: " + ts.get_accessor(), true);
        persistedString = PersistValue(ts, ts.get_accessor(), fileStepIndex);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisted value = (" + persistedString + ")", true);
        //testHelper.UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_CYAN + "╚" + testHelper.PrePostPad("[ End Persisting action, but value persisted and usable until end of test file ]", "═", 9, 152) + "╝" + AppConstants.ANSI_RESET, false);
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting action, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
    }


    /*********************************************************************************************
     * DESCRIPTION: Control method for performing all non-read actions that have an Accessor.
     *
     * @param ts
     * @param fileStepIndex
     * @throws InterruptedException
     **********************************************************************************************/
    private void PerformAccessorActionController(TestStep ts, String fileStepIndex) throws InterruptedException {
        Boolean status;
        testHelper.UpdateTestResults(AppConstants.indent5 + "Performing action using " + ts.get_accessorType() + " " + fileStepIndex + " non-read action", true);
        String subAction = null;
        int delayMilliSeconds = 0;

        //check if switching to an iFrame
        if (ts.get_command() != null && !ts.get_command().toLowerCase().contains("switch to iframe")) {
            status = PerformAction(ts, null, fileStepIndex);
        } else {
            //subAction can either be the expected value or a command to perform like click
            if (ts.get_command().toLowerCase().contains("switch to iframe")) {
                subAction = GetArgumentValue(ts, 1, null);
            }
            status = PerformAction(ts, subAction, fileStepIndex);
        }

        //if not a right click context command
        if (!ts.get_command().toLowerCase().contains("right click") && !ts.get_command().toLowerCase().contains("sendkeys")
                && !ts.get_command().toLowerCase().contains("send keys") && !ts.get_command().toLowerCase().contains("switch to iframe")) {
            //url has changed, check url against expected value
            String expectedUrl = ts.get_expectedValue();

            if (ts.ArgumentList != null && ts.ArgumentList.size() > 1) {
                delayMilliSeconds = GetArgumentNumericValue(ts, 0, AppConstants.DefaultTimeDelay);
                DelayCheck(delayMilliSeconds, fileStepIndex);
                expectedUrl = ts.get_expectedValue();
            }

            String actualUrl = GetCurrentPageUrl();
            if (expectedUrl != null) {
                if (ts.get_crucial()) {
                    assertEquals(expectedUrl, actualUrl);
                } else {
                    try {
                        assertEquals(expectedUrl, actualUrl);
                    } catch (AssertionError ae) {
                        //if the non-crucial test fails, take a screenshot and keep processing remaining tests
                        if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                            testHelper.captureScreenShot(driver, GetBrowserUsed() + ts.get_accessorType() + fileStepIndex + "Element_Not_Found" + ts.get_accessor().replace(' ', '_'), configurationFolder, true);
                        } else {
                            testHelper.captureScreenShot(driver, GetBrowserUsed() + ts.get_accessorType() + fileStepIndex + "Element_Not_Found" + ts.get_accessor().replace(' ', '_'), screenShotSaveFolder, true);
                        }
                    }
                }
            }
            //if there is an expectedValue in a click event it is to validate that a new page has been navigated to
            if (expectedUrl != null) {
                if (expectedUrl.equals(actualUrl)) {
                    testHelper.UpdateTestResults("Successful Post Action results for step " + fileStepIndex + " Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ")", true);
                } else if (!expectedUrl.equals(actualUrl)) {
                    testHelper.UpdateTestResults("Failed Post Action results for step " + fileStepIndex + " Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ")", true);
                }
            }
        }
    }

    /******************************************************************************
     * DESCRIPTION: Control method used to Check the count of a specific element type.
     * @param ts
     * @param fileStepIndex
     ******************************************************************************/
    private void SendKeysController(TestStep ts, String fileStepIndex) throws InterruptedException {
        Boolean status;
        Boolean isNumeric = false;
        String item;
        int timeDelay = 400;

        isNumeric = CheckArgumentNumeric(ts, ts.ArgumentList.size() -1);
        if (isNumeric && (ts.get_command().toLowerCase().contains("sendkeys") || ts.get_command().toLowerCase().contains("send keys")))
        {
            timeDelay = GetArgumentNumericValue(ts, ts.ArgumentList.size() -1, 400);
        }

        for (Argument argument : ts.ArgumentList) {
            item = argument.get_parameter();
            if (!item.toLowerCase().contains("sendkeys")) {
                status = PerformAction(ts, item, fileStepIndex);
                DelayCheck(timeDelay, fileStepIndex);
            }
        }
    }



    /******************************************************************************
     * DESCRIPTION: Control method used to Check Color Contrast a value.
     * NOT FULLY IMPLEMENTED - The method that this method calls is not fully baked.
     *                      Need to find the algorithm for the proper contrast
     *                      ratio and apply that accordingly.
     * @param ts
     * @param fileStepIndex
     ****************************************************************************** */
    private void ColorContrastController(TestStep ts, String fileStepIndex) {
        //checkColorContrast - the method is not fully implemented.  Need to figure out the color ratio formula.  Notes in method.
        //region {Notes}
        //this  test step Command: Check Contrast
        //              actionType: read
        //              arg1: URL to check or n/a for current URL
        //              arg2: Type of elements to check (p, label, div etc...)
        //              arg3: Brightness
        //              arg4: Difference
        //-------------------------------------------------------------------------------
        //endregion
        String url = GetArgumentValue(ts, 0, null);
        String tagType = GetArgumentValue(ts, 1, null);
        String bContrast = GetArgumentValue(ts, 2, null);
        String dContrast = GetArgumentValue(ts, 3, null);

        String [] checkItems = {bContrast, dContrast};

        String acceptableRanges = (bContrast != null && dContrast != null) ? bContrast + " " + dContrast :
                (bContrast != null) ? bContrast : (dContrast != null) ? dContrast : null;
        String page = url.toLowerCase().equals("n/a") ? driver.getCurrentUrl() : url.trim();
        testHelper.UpdateTestResults(AppConstants.indent5 + "Checking color contrast of " + tagType + " on page " + page, false);
        checkColorContrast(url, checkItems[2].trim(), fileStepIndex, ts.get_crucial(), acceptableRanges);
    }

    /********************************************************************************
     * DESCRIPTION: Control method used to Query Databases for a particular field value
     * NOT FULLY IMPLEMENTED - The method that this method calls is not fully baked.
     *                      Need to decide on the different databases that will be
     *                      supported for this.
     *                      Initial concept was with MongoDB but it was a blind exercise
     *                      in which I did not have any direct access to the database
     *                      to check against.
     * @param ts
     * @param fileStepIndex
     ********************************************************************************/
    private void DatabaseQueryController(TestStep ts, String fileStepIndex) throws SQLException {
        //testHelper.DebugDisplay("Found query....");
        try {
            if (ts.get_command().toLowerCase().contains("mongo")) {
                testHelper.UpdateTestResults("Found query then mongo....", false);
                //make sure that this connection has been established
                if (mongoClient != null) {
                    testHelper.UpdateTestResults("Found query, and mongo and in the if before RunMongoQuery....", false);
                    RunMongoQuery(ts, fileStepIndex);
                } else {
                    testHelper.UpdateTestResults("Connection is not available!!!", false);
                }
                testHelper.UpdateTestResults("Found query, and mongo after the if before RunMongoQuery....", false);
            } else if (ts.get_command().toLowerCase().contains("sql server") || ts.get_command().toLowerCase().contains("sqlserver")) {
                //testHelper.UpdateTestResults( AppConstants.ANSI_CYAN + AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Sql Server Query Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
                testHelper.CreateSectionHeader("[ Start Sql Server Query Event ]", "", AppConstants.ANSI_CYAN, true, false, true);
                //testHelper.CreateSectionHeader("[ End of Test Script ]", AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW, false, true, true);
                RunSqlServerQuery(ts, fileStepIndex);
                //testHelper.UpdateTestResults( AppConstants.ANSI_CYAN + AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Sql Server Query Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
                testHelper.CreateSectionHeader("[ End Sql Server Query Event ]", "", AppConstants.ANSI_CYAN, false, false, true);
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failure in DatabaseQueryController for step " + fileStepIndex  + "\r\n" + e.getMessage() + AppConstants.ANSI_RESET, true);
        }
    }
    //endregion

    /*************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the text of the element against the expected value.
     *
     * @param ts   - TestStep Object
     * @param fileStepIndex - File and Step Index
     *
     * IMPORTANT NOTE: NOTICED THAT FOR INPUT CONTROLS GETTING TEXT IS NOT WORKING
     *                 NEED TO ADD CHECK AND IF TEXT IS NULL FOR INPUT TYPE=TEXT
     *                 HAVE TO GET THE VALUE ATTRIBUTE INSTEAD.
     ************************************************************ */
    private void CheckElementText(TestStep ts, String fileStepIndex) throws Exception {
        String actual = "";
        Boolean notEqual = false;
        final String elementTypeCheckedAtStep = "Element type being checked at step ";
        String expected = ts.get_expectedValue();

        if (ts.get_accessorType().toLowerCase().equals(xpathCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by xPath: " + ts.get_accessor(), true);
            actual = CheckElementWithXPath(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(cssSelectorCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by CssSelector: " + ts.get_accessor(), true);
            actual = CheckElementWithCssSelector(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(tagNameCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by TagName: " + ts.get_accessor(), true);
            actual = CheckElementWithTagName(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(classNameCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by ClassName: " + ts.get_accessor(), true);
            actual = CheckElementWithClassName(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(idCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by Id: " + ts.get_accessor(), true);
            actual = CheckElementWithId(ts, fileStepIndex);
        }

        String arg = GetArgumentValue(ts, 0, null);
        if (arg != null && arg.equals("!=")) {
            notEqual = true;
        }

        if (ts.get_expectedValue() != null && ts.get_expectedValue().toLowerCase().contains(persistedStringCheckValue)) {
            if (persistedString != null) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Grabbing " + AppConstants.ANSI_CYAN + "persisted" + AppConstants.ANSI_RESET + " value: (" + persistedString + ") for comparison.", true);
                expected = persistedString;
            } else {
                testHelper.UpdateTestResults("", false);
                //testHelper.UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_RED + "╔" + testHelper.PrePostPad("[ Start of Persistence Usage Error ]", "═", 9, 152) + "╗" + AppConstants.ANSI_RESET, true);
                testHelper.CreateSectionHeader(AppConstants.indent5 +"[ Start of Persistence Usage Error ]", "", AppConstants.ANSI_RED, true, false, true);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "ERROR: No value previously persisted!!! " + AppConstants.ANSI_RESET + "Using empty string () instead of null for comparison.", true);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "IMPORTANT:" + AppConstants.ANSI_RESET + " A value must first be persisted before that persisted value can be used for comparison.", false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "NOTE:" + AppConstants.ANSI_RESET + " Values persisted in one test file are reset before the start of the next test file.", false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.indent5 + "Any values you want persisted for comparison, must first be persisted in the test file performing the comparison!!!",false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.indent5 + "Refer to the help file for more information regarding persisting and comparing persisted values.", false);
                //testHelper.UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_RED + "╚" + testHelper.PrePostPad("[ End of Persistence Usage Error ]", "═", 9, 152) + "╝" + AppConstants.ANSI_RESET, true);
                testHelper.CreateSectionHeader(AppConstants.indent5 +"[ End of Persistence Usage Error ]", "", AppConstants.ANSI_RED, false, false, true);
                expected = "";
            }
        }


        if (ts.get_expectedValue().contains("<")) {
            expected = expected.replace("<![CDATA[ ", "").replace(" ]]>","").trim();
            expected = expected.substring(expected.indexOf("<"), expected.lastIndexOf(">"));
            if (actual.contains("<")) {
                actual = actual.substring(actual.indexOf("<"), actual.lastIndexOf(">"));
            }
        }

        //if one value is missing and the other is null, make them equivalent
        if ((expected.isEmpty() && actual == null) || (expected == null && actual.isEmpty()) ) {
            expected = "";
            actual = "";
        }

        if (ts.get_crucial()) {
            if (!notEqual) {
                assertEquals(expected, actual);
            } else {
                assertFalse(expected.equals(actual));
            }
        } else {
            try {
                if (!notEqual) {
                    assertEquals(expected, actual);
                } else {
                    assertFalse(expected.equals(actual));
                }
            } catch (AssertionError ae) {
                // do not capture screen shot here, if element not found, check methods will capture screen shot
            }
        }
        if (expected.equals(actual) && !notEqual) {
            testHelper.UpdateTestResults("Successful equal comparison results at step " + fileStepIndex + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", true);
        } else if (!expected.equals(actual) && notEqual) {
            testHelper.UpdateTestResults("Successful NOT equal (!=) comparison results at step " + fileStepIndex + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", true);
        } else if (!expected.equals(actual) && !notEqual) {
            testHelper.UpdateTestResults("Failed equal comparison results at step " + fileStepIndex + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", true);
            if (screenShotSaveFolder != null && !screenShotSaveFolder.isEmpty()) {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + fileStepIndex + "Assert_Fail", screenShotSaveFolder, false);
            }
        } else if (expected.equals(actual) && notEqual) {
            testHelper.UpdateTestResults("Failed not equal comparison results at step " + fileStepIndex + " Expected value: (" + expected + ") Actual value: (" + actual + ")\r\n", true);
            if (screenShotSaveFolder != null && !screenShotSaveFolder.isEmpty()) {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + fileStepIndex + "Assert_Fail", screenShotSaveFolder, false);
            }
        }
    }

    /*********************************************************************
     * DESCRIPTION:
     *      Retrieves the value of the element using the configured accessor
     *      and returns it to the calling method where it will be
     *      persisted in a string variable.
     * @param ts
     * @param accessor
     * @param fileStepIndex
     * @param fileStepIndex
     *
     ******************************************************************** */
    private String PersistValue(TestStep ts, String accessor, String fileStepIndex) throws Exception  {
        String actual = null;
        if (ts.get_accessorType().toLowerCase().equals(xpathCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by xPath: " + accessor, true);
            actual = CheckElementWithXPath(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(cssSelectorCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by CssSelector: " + accessor, true);
            actual = CheckElementWithCssSelector(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(tagNameCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by TagName: " + accessor, true);
            actual = CheckElementWithTagName(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(classNameCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by ClassName: " + accessor, true);
            actual = CheckElementWithClassName(ts, fileStepIndex);
        } else if (ts.get_accessorType().toLowerCase().equals(idCheckValue)) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by Id: " + accessor, true);
            actual = CheckElementWithId(ts, fileStepIndex);
        }
        return actual;
    }



    /*******************************************************************************
     * DESCRIPTION:  Checks an Image Src or Alt attribute
     *
     * @param ts
     * @param fileStepIndex
     *******************************************************************************/
    private void CheckImageSrcAlt(TestStep ts, String fileStepIndex) {
        String actualValue="";
        String srcAlt = GetArgumentValue(ts, 0, "src");

        if (ts.get_accessorType().toLowerCase().equals(xpathCheckValue)) {
            actualValue = this.driver.findElement(By.xpath(ts.get_accessor())).getAttribute(srcAlt);
        } else if (ts.get_accessorType().toLowerCase().equals(cssSelectorCheckValue)) {
            actualValue = this.driver.findElement(By.cssSelector(ts.get_accessor())).getAttribute(srcAlt);
        } else if (ts.get_accessorType().toLowerCase().equals(tagNameCheckValue)) {
            actualValue = this.driver.findElement(By.tagName(ts.get_accessor())).getAttribute(srcAlt);
        } else if (ts.get_accessorType().toLowerCase().equals(classNameCheckValue)) {
            actualValue = this.driver.findElement(By.className(ts.get_accessor())).getAttribute(srcAlt);
        } else if (ts.get_accessorType().toLowerCase().equals(idCheckValue)) {
            actualValue = this.driver.findElement(By.id(ts.get_accessor())).getAttribute(srcAlt);
        }

        if (ts.get_crucial()) {
            assertEquals(ts.get_expectedValue(), actualValue);
        } else {
            try
            {
                assertEquals(ts.get_expectedValue(), actualValue);
            } catch (AssertionError ae) {
                //do nothing, this just traps the assertion error so that processing can continue
            }
        }
        if (ts.get_expectedValue().trim().equals(actualValue.trim())) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Successful Image " + srcAlt + " Check for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")", true);
        } else {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed Image " + srcAlt + " Check for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")", true);
        }
    }


    /*******************************************************************************
     * DESCRIPTION: Checks the Anchor href attribute.
     *
     * @param ts
     * @param fileStepIndex
     *******************************************************************************/
    private void CheckAnchorHref(TestStep ts, String fileStepIndex) {
        String actualValue="";
        //Not wired for checking text because text is already wired up through the default assert method
        String hrefTxt = "href";


        if (ts.get_accessorType().toLowerCase().equals(xpathCheckValue)) {
            actualValue = this.driver.findElement(By.xpath(ts.get_accessor())).getAttribute(hrefTxt);
        } else if (ts.get_accessorType().toLowerCase().equals(cssSelectorCheckValue)) {
            actualValue = this.driver.findElement(By.cssSelector(ts.get_accessor())).getAttribute(hrefTxt);
        } else if (ts.get_accessorType().toLowerCase().equals(tagNameCheckValue)) {
            actualValue = this.driver.findElement(By.tagName(ts.get_accessor())).getAttribute(hrefTxt);
        } else if (ts.get_accessorType().toLowerCase().equals(classNameCheckValue)) {
            actualValue = this.driver.findElement(By.className(ts.get_accessor())).getAttribute(hrefTxt);
        } else if (ts.get_accessorType().toLowerCase().equals(idCheckValue)) {
            actualValue = this.driver.findElement(By.id(ts.get_accessor())).getAttribute(hrefTxt);
        }

        if (ts.get_crucial()) {
            assertEquals(ts.get_expectedValue(), actualValue);
        } else {
            try {
                assertEquals(ts.get_expectedValue(), actualValue);
            } catch (AssertionError ae) {
                //do nothing, this just traps the assertion error so that processing can continue
            }
        }
        if (ts.get_expectedValue().trim().equals(actualValue.trim())) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Successful Anchor " + hrefTxt + " Check for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")", true);
        } else {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed Anchor " + hrefTxt + " Check for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")", true);
        }
    }


    /*************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the status of the Get or Post against the
     *      expected value.
     * @param ts - TestSettings Object
     * @param fileStepIndex - File and Step Index
     ************************************************************ */
    private void CheckGetPostStatus(TestStep ts, String fileStepIndex) {
        int expectedStatus = ts.get_expectedValue() != null ? parseInt(ts.get_expectedValue()) : 200;   //GetArgumentNumericValue(ts, 0, 200);
        int actualStatus;
        String url = GetArgumentValue(ts, 0, null);

        if (url != null) {
            if (ts.get_command().toLowerCase().contains("post")) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking Post status of " + url, false);
                actualStatus = httpResponseCodeViaPost(url);
            } else if (ts.get_command().toLowerCase().contains("get")) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking Get status of " + url, false);
                actualStatus = httpResponseCodeViaGet(url);
            } else {
                ImproperlyFormedTest(fileStepIndex);
                actualStatus = -1;
            }
            if (actualStatus != -1) {
                if (ts.get_crucial()) {
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
                testHelper.UpdateTestResults("Successful comparison results at step " + fileStepIndex + " Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ")\r\n", true);
            } else if (expectedStatus != actualStatus) {
                testHelper.UpdateTestResults("Failed comparison results at step " + fileStepIndex + " Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ")\r\n", true);
            }
        } else {
            testHelper.UpdateTestResults("Error: Required URL not provided as Argument 1 aborting at step " + fileStepIndex, true);
        }
    }

    /********************************************************************
     * DESCRIPTION: Returns the URL of the current page.
     * @return
     ********************************************************************/
    public String GetCurrentPageUrl() {
        return this.driver.getCurrentUrl();
    }


    /*************************************************************
     * DESCRIPTION: Returns the status code of the url passed in for a
     *              GET request.
     * @param url - url to check
     ************************************************************ */
    public int httpResponseCodeViaGet(String url) {
        return RestAssured.get(url).statusCode();
    }


    /*************************************************************
     * DESCRIPTION:
     *      Returns the status code of the url passed in for a
     *      POST request.
     * @param url - url to check
     ************************************************************ */
    public int httpResponseCodeViaPost(String url) {
        return RestAssured.post(url).statusCode();
    }

    /*************************************************************
     * DESCRIPTION:
     *      Method reports improperly formatted tests to the
     *      user with the test step so that it can be fixed.
     * @param fileStepIndex - indicates the file and step where
     *                            this command was issued.
     ************************************************************ */
    private void ImproperlyFormedTest(String fileStepIndex) {
        testHelper.UpdateTestResults("Imporperly formatted test for step " + fileStepIndex, true);
    }


    /*************************************************************
     * DESCRIPTION: Retrieves all anchor tags in a page and
     *              reports the status of all anchor tags that
     *              have an href attribute.
     * @param url - url to check
     ************************************************************ */
    public void CheckBrokenLinks(String url) {
        if (driver.getCurrentUrl() != url) {
            driver.get(url);
        }
        int linkCount = 0;

        //Get all the links on the page
        List<WebElement> links = driver.findElements(By.cssSelector("a"));

        String href;
        //region { variables for retrieving attributes currently not in use}
        String text;

        testHelper.UpdateTestResults(AppConstants.indent5 + "Retrieved " + links.size() + " anchor tags", true);
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
                    testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed link test " + href + " gave a response code of " + brokenLinksStatusCode + AppConstants.ANSI_RESET, true);
                } else {
                   testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful link test text: " + text + " href: " + href + " xPath: " + generateXPATH(link, "") + " gave a response code of " + brokenLinksStatusCode + AppConstants.ANSI_RESET, true);
                }
            }
        }
        testHelper.UpdateTestResults(AppConstants.indent5 + "Discovered " + linkCount + " links amongst " + links.size() + " anchor tags.\r\n", true);
    }

    /*************************************************************
     * DESCRIPTION:
     *      Checks all image tags for the presence of the checkType
     *      property passed in (alt, src).
     *      For alt property, it checks that the alt tag is present
     *      and that it contains information and displays a
     *      success or fail message accorgingly.
     *      For src property, it checks that the src tag is present
     *      and that it resolves to a 200 response status.
     * @param url - url to check
     * @param checkType - Set to Image alt or src attribute
     ************************************************************ */
    public void CheckADAImages(String url, String checkType) {
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        List<WebElement> images = driver.findElements(By.cssSelector("img"));
        String altTag;
        String imgSrc;
        int altTagCount = 0;
        int brokenImageSrcStatusCode = 0;

        testHelper.UpdateTestResults(AppConstants.indent5 + "Retrieved " + images.size() + " image tags", true);
        for(WebElement link : images) {
            altTag = link.getAttribute("alt");
            imgSrc = link.getAttribute("src");
            if (checkType.toLowerCase().trim().equals("alt")) {
                if (altTag != null && !altTag.trim().isEmpty()) {
                    altTagCount++;
                    testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful image alt tag found: " + altTag + " for img src: " + imgSrc + AppConstants.ANSI_RESET, true);
                } else {
                    testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed image alt tag missing for img src: " + imgSrc + AppConstants.ANSI_RESET, true);
                }
            } else if (checkType.toLowerCase().trim().equals("src")) {
                if (imgSrc != null && !imgSrc.trim().isEmpty()) {
                    altTagCount++;
                    try {
                        brokenImageSrcStatusCode = httpResponseCodeViaGet(imgSrc);
                    } catch (Exception ex) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed Error when attempting to validate image src " + imgSrc + " Error: " + ex.getMessage() + AppConstants.ANSI_RESET, true);
                    }
                    if (200 != brokenImageSrcStatusCode) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + AppConstants.ANSI_RESET, true);
                    } else {
                        testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + AppConstants.ANSI_RESET, true);
                    }
                } else {
                    if (altTag != null) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed image src tag missing for image with alt tag: " + altTag + AppConstants.ANSI_RESET, true);
                    } else {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed image src tag missing." + AppConstants.ANSI_RESET, true);
                    }
                }
            }
        }
        testHelper.UpdateTestResults(AppConstants.indent5 + "Discovered " + altTagCount + " image " + checkType.toLowerCase().trim()  + " attributes  amongst " + images.size() + " image tags.\r\n", true);
    }

    /*************************************************************
     * DESCRIPTION:
     *      Performs a count of all checkElement tags for the url
     *      passed in or the current url, if not passed in, and
     *      compares that count to the expectedCount passed in.
     *      If this test is marked as crucial, all testing stops
     *      if the counts do not match.
     *      If this test is not marked as crucial, testing
     *      continues and the status is reported.
     * @param url -
     * @param checkElement -
     * @param expectedCount -
     * @param fileStepIndex -
     * @param isCrucial -
     ************************************************************ */
    private void CheckElementCount(String url, String checkElement, int expectedCount, String fileStepIndex, boolean isCrucial) {
        int actualCount;
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        List<WebElement> elements = driver.findElements(By.cssSelector(checkElement));
        actualCount = elements.size();

        if (isCrucial) {
            assertEquals(expectedCount, actualCount);
        } else {
            try {
                assertEquals(expectedCount, actualCount);
            } catch (AssertionError ae) {
                //do nothing, just trap the error so that testing can continue
            }

            if (actualCount != expectedCount) {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to match count of '" + checkElement + "' tags for step " + fileStepIndex + ".  Expected: " + expectedCount + "  Actual: " + actualCount + AppConstants.ANSI_RESET, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful matching count of '" + checkElement + "' tags for step " + fileStepIndex + ".  Expected: " + expectedCount + "  Actual: " + actualCount + AppConstants.ANSI_RESET, true);
            }
        }
    }


    /*************************************************************
     * DESCRIPTION:
     *      Returns the text of an element using its xPath accessor.
     * @param ts -
     * @param fileStepIndex -
     ************************************************************ */
    public String CheckElementWithXPath(TestStep ts, String fileStepIndex) throws Exception {
        String actualValue = null;
        String accessor = ts.get_accessor();

        try {
            String typeOfElement = this.driver.findElement(By.xpath(accessor)).getAttribute("type");
            if (typeOfElement!= null && ((typeOfElement.contains("select-one") || typeOfElement.contains("select-many")))) {
                Select select = new Select(this.driver.findElement(By.xpath(accessor)));
                //wait until element is present commented out and functionality pushed to separate stand-alone action
                //Select select = new Select((new WebDriverWait(driver,10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor))));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            } else {
                boolean isVisible = this.driver.findElement(By.xpath(accessor)).isDisplayed();
                if (isVisible) {
                    actualValue = this.driver.findElement(By.xpath(accessor)).getText();
                } else {
                    //String script = "return arguments[0].innerHTML";
                    String script = "return arguments[0].innerText";
                    actualValue = (String) ((JavascriptExecutor) driver).executeScript(script, this.driver.findElement(By.xpath(accessor)));
                }

                if (actualValue == null || actualValue.isEmpty()) {
                    actualValue = this.driver.findElement(By.xpath(accessor)).getAttribute("value");
                }

                //region {Wait for element code - not being used but an idea that could be implemented}
                //testHelper.DebugDisplay("actualValue = " + actualValue);
                //wait until element is present commented out and functionality pushed to separate  stand-alone action
                //actualValue = (new WebDriverWait(driver,10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor))).getText();
                //endregion
            }
            if (!ts.get_command().toLowerCase().contains(persistStringCheckValue)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "xPath_Element_Not_Found", configurationFolder, true);
            } else {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "xPath_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        } finally {
            return actualValue;
        }
    }

    /*************************************************************
     * DESCRIPTION:
     *      Returns the text of an element using its CssSelector accessor.
     ************************************************************ */
    public String CheckElementWithCssSelector(TestStep ts, String fileStepIndex) throws Exception {
        String accessor = ts.get_accessor();
        String actualValue = null;

        try {
            String typeOfElement = this.driver.findElement(By.cssSelector(accessor)).getAttribute("type");
            if (typeOfElement != null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.cssSelector(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            } else {
                boolean isVisible = this.driver.findElement(By.cssSelector(accessor)).isDisplayed();
                if (isVisible) {
                    actualValue = this.driver.findElement(By.cssSelector(accessor)).getText();
                } else {
                    //String script = "return arguments[0].innerHTML";
                    String script = "return arguments[0].innerText";
                    actualValue = (String) ((JavascriptExecutor) driver).executeScript(script, this.driver.findElement(By.cssSelector(accessor)));
                }
                if (actualValue == null || actualValue.isEmpty()) {
                    actualValue = this.driver.findElement(By.cssSelector(accessor)).getAttribute("value");
                }
            }
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by CssSelector: " + accessor + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by CssSelector: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "CssSelector_Element_Not_Found", configurationFolder, true);
            } else {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "CssSelector_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        } finally {
            return actualValue;
        }
    }

    /*************************************************************
     * DESCRIPTION: Returns the text of an element using its
     *              TagName accessor.
     * @param ts - Test Step object
     * @param fileStepIndex
     ************************************************************ */
    public String CheckElementWithTagName(TestStep ts, String fileStepIndex) throws Exception {
        String accessor = ts.get_accessor();
        String actualValue = null;

        try {
            String typeOfElement = this.driver.findElement(By.tagName(accessor)).getAttribute("type");
            if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.tagName(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            } else {
                boolean isVisible = this.driver.findElement(By.tagName(accessor)).isDisplayed();
                if (isVisible) {
                    actualValue = this.driver.findElement(By.tagName(accessor)).getText();
                } else {
                    //String script = "return arguments[0].innerHTML";
                    String script = "return arguments[0].innerText";
                    actualValue = (String) ((JavascriptExecutor) driver).executeScript(script, this.driver.findElement(By.tagName(accessor)));
                }
                if (actualValue == null || actualValue.isEmpty()) {
                    actualValue = this.driver.findElement(By.tagName(accessor)).getAttribute("value");
                }
            }
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by TagName: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by TagName: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "TagName_Element_Not_Found", configurationFolder, true);
            } else {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "TagName_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        } finally {
            return actualValue;
        }
    }

    /*************************************************************
     * DESCRIPTION: Returns the text of an element using its
     *              ClassName accessor.
     * @param ts - Test Step object
     * @param fileStepIndex
     ************************************************************ */
    private String CheckElementWithClassName(TestStep ts, String fileStepIndex) throws Exception {
        String accessor = ts.get_accessor();
        String actualValue = null;

        try {
            String typeOfElement = this.driver.findElement(By.className(accessor)).getAttribute("type");
            if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.className(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            } else {
                boolean isVisible = this.driver.findElement(By.className(accessor)).isDisplayed();
                if (isVisible) {
                    actualValue = this.driver.findElement(By.className(accessor)).getText();
                } else {
                    //String script = "return arguments[0].innerHTML";
                    String script = "return arguments[0].innerText";
                    actualValue = (String) ((JavascriptExecutor) driver).executeScript(script, this.driver.findElement(By.className(accessor)));
                }
                if (actualValue == null || actualValue.isEmpty()) {
                    actualValue = this.driver.findElement(By.className(accessor)).getAttribute("value");
                }
            }
            if (!ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by ClassName: " + accessor + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            }  else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by ClassName: " + accessor + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "ClassName_Element_Not_Found", configurationFolder, true);
            } else {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "ClassName_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        } finally {
            return actualValue;
        }
    }

    /*************************************************************
     * DESCRIPTION: Returns the text of an element using
     *              its Id accessor.
     * @param ts - Test Step object
     * @param fileStepIndex
     ************************************************************ */
    public String CheckElementWithId(TestStep ts, String fileStepIndex)  throws Exception {
        String accessor = ts.get_accessor();
        String actualValue = null;

        try {
            String typeOfElement = this.driver.findElement(By.id(accessor)).getAttribute("type");
            if (typeOfElement!= null && (typeOfElement.contains("select-one") || typeOfElement.contains("select-many"))) {
                Select select = new Select(this.driver.findElement(By.id(accessor)));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            } else {

                boolean isVisible = this.driver.findElement(By.id(accessor)).isDisplayed();
                if (isVisible) {
                    actualValue = this.driver.findElement(By.id(accessor)).getText();
                } else {
                    //String script = "return arguments[0].innerHTML";
                    String script = "return arguments[0].innerText";
                    actualValue = (String) ((JavascriptExecutor) driver).executeScript(script, this.driver.findElement(By.id(accessor)));
                }
                if (actualValue == null || actualValue.isEmpty()) {
                    actualValue = this.driver.findElement(By.id(accessor)).getAttribute("value");
                }
            }
            if (ts.get_expectedValue() != null && !ts.get_expectedValue().toLowerCase().contains(persistStringCheckValue)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by ID: " + accessor + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by ID: " + accessor + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "Id_Element_Not_Found", configurationFolder, true);
            } else {
                testHelper.captureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "Id_Element_Not_Found", screenShotSaveFolder, true);
            }
            actualValue = null;
        } finally {
            return actualValue;
        }
    }


    /***********************************************************************
     * DESCRIPTION:
     *      If a Tag is passed in as part of the Expected values, search the text
     *      of all tags of that type for the phrase, but if no tag is passed in,
     *      search the text of all page elements for the phrase.
     * @param ts - TestSettings object
     * @param fileStepIndex - File Index and Step Index within the file
     ********************************************************************** */
    private void FindPhrase(TestStep ts, String fileStepIndex) {
        String cssSelector = GetArgumentValue(ts, 0, "*");
        String searchType = GetArgumentValue(ts, 1, "equals");
        Boolean wasFound = false;

        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        List<String> foundElements = new ArrayList<>();

        //region {Implemented in the bottom for loop}
        // When searching a specific tag type for the phrase,
        // iterate through all child elements to see if one of them contains the text.
        // if a child or grandchild contains the text, eliminate the element as containing the text
        //endregion

        if (searchType.toLowerCase().equals("contains")) {
            for (WebElement element : elements) {
                if (element.getText().trim().contains(ts.get_expectedValue().trim())) {
                    wasFound = true;
                    foundElements.add(generateXPATH(element, ""));
                }
            }
        } else {
            for (WebElement element : elements) {
                if (element.getText().equals(ts.get_expectedValue().trim())) {
                    wasFound = true;
                    foundElements.add(generateXPATH(element, ""));
                }
            }
        }

        if (!wasFound) {
            String message = "Failed to find (" + ts.get_expectedValue().trim() + ") searching all elements.";
            if (!cssSelector.trim().isEmpty()) {
                message = "Failed to find (" + ts.get_expectedValue().trim() + ") searching all " + cssSelector + " elements.";
            }
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + message + AppConstants.ANSI_RESET, true);
        } else {
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
                        } catch(IndexOutOfBoundsException io) {
                            //try moving on to the next item doing nothing here
                            //pageHelper.UpdateTestResults("Error y = " + y + " and x = " + x + " - " + io.getMessage());
                        }
                    }
                }
            }
            for (int z=0;z<foundElements.size();z++) {
                testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful found (" + ts.get_expectedValue().trim() + ") in element: " + foundElements.get(z) + " for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
            }
        }
    }

    //region { Perform Action Methods}
    /*************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the status of the Get or Post against the
     *      expected value.
     * @param ts - TestSettings Object
     * @param fileStepIndex - File and Step Index
     *
     ************************************************************ */
    private void PerformExplicitNavigation(TestStep ts, String fileStepIndex) throws Exception {
        String navigateUrl = GetArgumentValue(ts, 0, null);
        String delayTime = GetArgumentValue(ts, 1, null);
        String windowDimensions = GetArgumentValue(ts, 2, null);

        if (!testHelper.CheckIsUrl(navigateUrl)) {
            ArrayList<String> arguments = SortNavigationArguments(navigateUrl, delayTime, windowDimensions, "navigate");
            navigateUrl = arguments.get(0);
            delayTime = arguments.get(1);
            windowDimensions = arguments.get(2);
            UpdateNavigationTestStepArguments(ts, navigateUrl, delayTime, windowDimensions);
        }

        if (testHelper.tryParse(delayTime) == null) {
            ArrayList<String> arguments = SortNavigationArguments(navigateUrl, delayTime, windowDimensions, "delay");
            navigateUrl = arguments.get(0);
            delayTime = arguments.get(1);
            windowDimensions = arguments.get(2);
            UpdateNavigationTestStepArguments(ts, navigateUrl, delayTime, windowDimensions);
        }


        String expectedUrl = null;
        int delayMilliSeconds = 0;

        if (navigateUrl != null && !navigateUrl.isEmpty()) {
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Explicit Navigation Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
            expectedUrl = ts.get_expectedValue() != null ? ts.get_expectedValue().trim() : null;

            if (delayTime != null && !delayTime.isEmpty()) {
                delayMilliSeconds = parseInt(delayTime.trim());
            }
            if (windowDimensions != null && !windowDimensions.isEmpty()) {
                String dimensions = windowDimensions.trim();
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
                    } else {
                        height= parseInt(dimensions.substring(dimensions.indexOf("h=") + 2, dimensions.indexOf("w=")).trim());
                        width = parseInt(dimensions.substring(dimensions.indexOf("w=") + 2, dimensions.length()).trim());
                    }
                    testHelper.UpdateTestResults(AppConstants.indent8 + "Setting browser dimensions to (Width=" + width + " Height=" + height + ")", true);
                    testHelper.SetWindowContentDimensions(driver, width, height);
                }
            }
        }
        this.testPage = navigateUrl;
        //Explicit Navigation Event

        testHelper.UpdateTestResults(AppConstants.indent8 + "Navigating to " + navigateUrl + " for step " + fileStepIndex, true);
        String actualUrl = CheckPageUrl(delayMilliSeconds);
        if (expectedUrl != null && expectedUrl.trim().length() > 0) {
            if (ts.get_crucial()) {
                assertEquals(expectedUrl, actualUrl);
            } else {
                try
                {
                    assertEquals(expectedUrl, actualUrl);
                } catch (AssertionError ae) {
                    //do nothing, this just traps the assertion error so that processing can continue
                }
            }
            if (expectedUrl.trim().equals(actualUrl.trim())) {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Successful Navigation and URL Check for step " + fileStepIndex + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Failed Navigation and URL Check for step " + fileStepIndex + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")", true);
            }
        }
        testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Explicit Navigation Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
    }

    private void UpdateNavigationTestStepArguments(TestStep ts, String navigateUrl, String delayTime, String windowDimensions) {
        ts.ArgumentList = new ArrayList<>();
        Argument item = new Argument();
        item.set_parameter(navigateUrl);
        ts.ArgumentList.add(item);
        item = new Argument();
        item.set_parameter(delayTime);
        ts.ArgumentList.add(item);
        item = new Argument();
        item.set_parameter(windowDimensions);
        ts.ArgumentList.add(item);
    }

    private ArrayList<String> SortNavigationArguments(String navigateUrl, String delayTime, String windowDimensions, String sortField) {
        Boolean status;
        String returnValue;
        ArrayList<String> arguments = new ArrayList<>();

        if (sortField.toLowerCase().equals("navigate")) {
            if (delayTime != null && !delayTime.isEmpty() && testHelper.CheckIsUrl(delayTime)) {
                returnValue = delayTime;
                //navigateUrl in delayTime, delayTime in windowDimension, windowDimension in navigateUrl
                if (navigateUrl.toLowerCase().contains("w=") || navigateUrl.toLowerCase().contains("h=")) {
                    delayTime = windowDimensions;
                    windowDimensions = navigateUrl;
                    navigateUrl = returnValue;
                    arguments.add(navigateUrl);
                    arguments.add(delayTime);
                    arguments.add(windowDimensions);
                } else if (windowDimensions.toLowerCase().contains("w=") || windowDimensions.toLowerCase().contains("h=")) {
                    //delayTime in navigateUrl, navigateUrl in delayTime, windowDimension in windowDimension
                    delayTime = navigateUrl;
                    navigateUrl = returnValue;
                    arguments.add(navigateUrl);
                    arguments.add(delayTime);
                    arguments.add(windowDimensions);
                }
            } else if (windowDimensions != null && !windowDimensions.isEmpty() && testHelper.CheckIsUrl(windowDimensions)) {
                testHelper.DebugDisplay("IN windowdimensions has URL");
                returnValue = windowDimensions;
                if (navigateUrl.toLowerCase().contains("w=") || navigateUrl.toLowerCase().contains("h=")) {
                    testHelper.DebugDisplay("IN windowdimensions has URL - navigateURL has window dimensions");
                    windowDimensions = navigateUrl;
                    navigateUrl = returnValue;
                    arguments.add(navigateUrl);
                    arguments.add(delayTime);
                    arguments.add(windowDimensions);
                } else {
                    windowDimensions = delayTime;
                    delayTime = navigateUrl;
                    navigateUrl = returnValue;
                    arguments.add(navigateUrl);
                    arguments.add(delayTime);
                    arguments.add(windowDimensions);
                }
            } else if (testHelper.tryParse(delayTime) != null) {
                //delayTime in delayTime, navigateUrl in windowDimensions, windowDimensions in navigateUrl
                returnValue = windowDimensions;
                windowDimensions = navigateUrl;
                navigateUrl = returnValue;
                arguments.add(navigateUrl);
                arguments.add(delayTime);
                arguments.add(windowDimensions);
            }
        } else if (sortField.toLowerCase().equals("delay")) {
            //if delay is the sort field that means the navigation field is correct.
            if (delayTime.toLowerCase().contains("w=") || delayTime.toLowerCase().contains("h=")) {
                if (windowDimensions != null && !windowDimensions.isEmpty()) {
                    returnValue = windowDimensions;
                    windowDimensions = delayTime;
                    delayTime = returnValue;
                    arguments.add(navigateUrl);
                    arguments.add(delayTime);
                    arguments.add(windowDimensions);
                } else {
                    windowDimensions = delayTime;
                    delayTime = Integer.toString(AppConstants.DefaultTimeDelay);
                    arguments.add(navigateUrl);
                    arguments.add(delayTime);
                    arguments.add(windowDimensions);
                }
            }
        }
        return arguments;
    }

    /*******************************************************************************
     * DESCRIPTION: Performs non-text retrieval actions such as clicking,
     *              navigating, waiting, taking screen shots etc...
     * @param ts -
     * @param subAction -
     * @param fileStepIndex -
     ***************************************************************************** */
    public Boolean PerformAction(TestStep ts, String subAction, String fileStepIndex) {
        Boolean status = false;
        final String click = "click";
        final String sendKeys = "sendkeys";
        final String rightClick = "right click";
        final String keys = "keys.";
        final String doubleClick = "doubleclick";
        String command = ts.get_command().toLowerCase().contains("switch to iframe") ? subAction : ts.get_command();

        //if this is a click event, click it
        if ((command.toLowerCase().contains("click")) && !command.contains(sendKeys)) {
            if (command.toLowerCase().contains(doubleClick)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing double click on " + ts.get_accessor() + " using " + ts.get_accessorType() + " for step " + fileStepIndex, true);
            } else if (command.toLowerCase().contains(rightClick)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing right click on " + ts.get_accessor() + " using " + ts.get_accessorType() + " for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing click on " + ts.get_accessor() + " using " + ts.get_accessorType() + " for step " + fileStepIndex, true);
            }
            try {
                if (ts.get_accessorType().toLowerCase().equals(xpathCheckValue)) {
                    if (!command.toLowerCase().contains(rightClick)) {
                        if (!command.toLowerCase().contains(doubleClick)) {
                            this.driver.findElement(By.xpath(ts.get_accessor())).click();
                            testHelper.UpdateTestResults("Click performed!", false);
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.xpath(ts.get_accessor()))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (command.toLowerCase().contains(keys)) {
                            action.contextClick(driver.findElement(By.xpath(ts.get_accessor()))).build().perform();
                        } else {
                            action.contextClick(driver.findElement(By.xpath(ts.get_accessor()))).build().perform();
                            SelectFromContextMenu(ts, fileStepIndex);
                        }
                    }
                }
                else if (ts.get_accessorType().toLowerCase().equals(idCheckValue)) {
                    if (!command.toLowerCase().contains(rightClick)) {
                        if (!command.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.id(ts.get_accessor())).click();
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.id(ts.get_accessor()))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (!command.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.id(ts.get_accessor()))).build().perform();
                        } else {
                            action.contextClick(driver.findElement(By.id(ts.get_accessor()))).build().perform();
                            SelectFromContextMenu(ts, fileStepIndex);
                        }
                    }
                } else if (ts.get_accessorType().toLowerCase().equals(classNameCheckValue)) {
                    if (!command.toLowerCase().contains(rightClick)) {
                        if (!command.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.className(ts.get_accessor())).click();
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.className(ts.get_accessor()))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
//                        if (!ts.get_command().toLowerCase().contains(keys)) {
                        if (!command.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.className(ts.get_accessor()))).build().perform();
                        } else {
                            action.contextClick(driver.findElement(By.className(ts.get_accessor()))).build().perform();
                            SelectFromContextMenu(ts, fileStepIndex);
                        }
                    }
                } else if (ts.get_accessorType().toLowerCase().equals(cssSelectorCheckValue)) {
//                    if (!ts.get_command().toLowerCase().contains(rightClick)) {
                    if (!command.toLowerCase().contains(rightClick)) {
//                        if (!ts.get_command().toLowerCase().contains(doubleClick)) {
                        if (!command.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.cssSelector(ts.get_accessor())).click();
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.cssSelector(ts.get_accessor()))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (!command.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.cssSelector(ts.get_accessor()))).build().perform();
                        } else {
                            action.contextClick(driver.findElement(By.cssSelector(ts.get_accessor()))).build().perform();
                            SelectFromContextMenu(ts, fileStepIndex);
                        }
                    }
                } else if (ts.get_accessorType().toLowerCase().equals(tagNameCheckValue)) {
                    if (!command.toLowerCase().contains(rightClick)) {
                        if (!command.toLowerCase().contains(doubleClick)) {
                            //click
                            this.driver.findElement(By.tagName(ts.get_accessor())).click();
                        } else {
                            //doubleclick
                            Actions action = new Actions(driver);
                            action.doubleClick(driver.findElement(By.tagName(ts.get_accessor()))).build().perform();
                        }
                    } else {  //right click element
                        Actions action = new Actions(driver);
                        if (!command.toLowerCase().contains(keys)) {
                            action.contextClick(this.driver.findElement(By.tagName(ts.get_accessor()))).build().perform();
                        } else {
                            action.contextClick(driver.findElement(By.tagName(ts.get_accessor()))).build().perform();
                            SelectFromContextMenu(ts, fileStepIndex);
                        }
                    }
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        } else if (command.toLowerCase().indexOf("screenshot") >= 0) {
            try {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Taking Screenshot for step " + fileStepIndex, true);
                PerformScreenShotCapture(subAction);
                status = true;
            } catch (Exception e) {
                status = false;
            }
        } else {  //if it is not a click, send keys or screenshot
            try {
                //use sendkeys as the command when sending keywords to a form
                if (command.contains(sendKeys)) {
                    //added the below structure so that the unique identifier could be used with the persisted string.
                    if (subAction.toLowerCase().contains(persistedStringCheckValue) && !subAction.trim().contains(uidReplacementChars)) {
                        testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_CYAN + "Using Persisted value (" + persistedString + ")" + AppConstants.ANSI_RESET, true);
                        subAction = persistedString;
                    } else {
                        if (subAction.trim().toLowerCase().contains(persistedStringCheckValue) && subAction.trim().contains(uidReplacementChars)) {
                            testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_CYAN + "Using Persisted value (" + persistedString + ")" + AppConstants.ANSI_RESET, true);
                            if (subAction.trim().indexOf(persistedStringCheckValue) < subAction.trim().indexOf(uidReplacementChars)) {
                                if (subAction.trim().indexOf(" ") > subAction.trim().indexOf(persistedStringCheckValue))
                                {
                                    subAction = persistedString + " " + uniqueId;
                                } else {
                                    subAction = persistedString + uniqueId;
                                }
                            } else {
                                if (subAction.trim().indexOf(" ") > subAction.trim().indexOf(uidReplacementChars))
                                {
                                    subAction = uniqueId + " " + persistedString;
                                } else {
                                    subAction =  uniqueId + persistedString;
                                }
                            }
                        }
                    }
//                    pageHelper.UpdateTestResults("value = " + value);
                    testHelper.UpdateTestResults(AppConstants.indent5 + "Performing SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                }
                if (subAction.contains(keys) || subAction.toLowerCase().contains(keys)) {
                    testHelper.UpdateTestResults(AppConstants.indent8 + "Performing special SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                    if (ts.get_accessorType().toLowerCase().equals(xpathCheckValue)) {
                        this.driver.findElement(By.xpath(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                    } else if (ts.get_accessorType().toLowerCase().equals(idCheckValue)) {
                        this.driver.findElement(By.id(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                    } else if (ts.get_accessorType().toLowerCase().equals(classNameCheckValue)) {
                        this.driver.findElement(By.className(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                    } else if (ts.get_accessorType().toLowerCase().equals(cssSelectorCheckValue)) {
                        this.driver.findElement(By.cssSelector(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                    } else if (ts.get_accessorType().toLowerCase().equals(tagNameCheckValue)) {
                        this.driver.findElement(By.tagName(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                    }
                } else {
                    if (subAction.indexOf(uidReplacementChars) > -1) {
                        testHelper.UpdateTestResults(AppConstants.indent5 + "Replacing Unique Identifier placeholder", true);
                    }
                    subAction = subAction.replace(uidReplacementChars, uniqueId);
                    testHelper.UpdateTestResults(AppConstants.indent8 + "Performing default SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                    if (ts.get_accessorType().toLowerCase().equals(xpathCheckValue)) {
                        this.driver.findElement(By.xpath(ts.get_accessor())).sendKeys(subAction);
                    } else if (ts.get_accessorType().toLowerCase().equals(idCheckValue)) {
                        this.driver.findElement(By.id(ts.get_accessor())).sendKeys(subAction);
                    } else if (ts.get_accessorType().toLowerCase().equals(classNameCheckValue)) {
                        this.driver.findElement(By.className(ts.get_accessor())).sendKeys(subAction);
                    } else if (ts.get_accessorType().toLowerCase().equals(cssSelectorCheckValue)) {
                        this.driver.findElement(By.cssSelector(ts.get_accessor())).sendKeys(subAction);
                    } else if (ts.get_accessorType().toLowerCase().equals(tagNameCheckValue)) {
                        this.driver.findElement(By.tagName(ts.get_accessor())).sendKeys(subAction);
                    }
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        }
        return status;
    }

    /*********************************************************************
     * DESCRIPTION: Logs into a Page that uses a Popup Alert style login.
     * @param url
     * @param email
     * @param password
     * @param fileStepIndex
     ******************************************************************** */
    public void Login(String url, String email, String password, String fileStepIndex) throws Exception {
        testHelper.UpdateTestResults("Login method reached start before any code.", false);
        Boolean urlIsNA = url.toLowerCase().trim().equals("n/a") ? true : false;

        //if url  is provided, navigate first
        if (url != null && !url.isEmpty() && !urlIsNA) {
            driver.get(url);
        }
        testHelper.UpdateTestResults("Login method reached", false);

        try {
            testHelper.UpdateTestResults("Switched to Alert second try", false);
            testHelper.UpdateTestResults("Switched to Alert second try after", false);
        } catch (Exception ex) {
            try {
                if (!isAlertPresent()) {
                    driver.get(testPage);
                }
                driver.switchTo().alert();
                testHelper.UpdateTestResults("Switched to Alert #1", false);
                driver.findElement(By.id("username")).sendKeys(email);
                driver.findElement(By.id("password")).sendKeys(password);
                testHelper.UpdateTestResults("Sent Credentials email: " + email + " Password: " + password, false);
                driver.switchTo().alert().accept();
                testHelper.UpdateTestResults("Switched to Alert #2", false);
                driver.switchTo().defaultContent();
                testHelper.UpdateTestResults("Switched to default context", false);
                testHelper.UpdateTestResults("Completed login sequence without error.", true);
            } catch (Exception ex1) {
                testHelper.UpdateTestResults("Exception " + ex.getMessage(), false);
                //if (url == null || url.isEmpty() || !url.toLowerCase().trim().equals("n/a")) {
                if (url == null || url.isEmpty() || !urlIsNA) {
                    url = testPage;
                }
                testHelper.UpdateTestResults("Switched to Alert Second catch", false);
                String newUrl = url.replace("://", "://" + email + ":" + password + "@");
                driver.get(newUrl);
                //if the alert doesn't show up, you already have context and are logged in
            }
        }
    }


    /***********************************************************************************
     * Description: Sends key commands to the context window.
     *              The context window cannot be accessed directly but can be accessed
     *              after right clicking an element that exposes the context menu.
     * @param ts -
     * @param fileStepIndex -
     * @throws AWTException -
     * @throws InterruptedException -
     ***********************************************************************************/
    private void SelectFromContextMenu(TestStep ts, String fileStepIndex) throws AWTException, InterruptedException {
        int downCount = 0;
        int upCount = 0;
        int leftCount = 0;
        int rightCount = 0;
        boolean switchToTab = false;
        boolean rightClick = false;
        String item;

        for (Argument argument : ts.ArgumentList) {
            item = argument.get_parameter();
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
            if (item.toLowerCase().trim().contains("keys.right_click")) {
                rightClick = true;
            }
        }

        Robot robot = new Robot();

        if (rightClick) {
            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
        }
        for (int x=0;x<downCount;x++) {
            robot.keyPress(KeyEvent.VK_DOWN);
            testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Key down action!", true);
        }
        for (int x=0;x<upCount;x++) {
            robot.keyPress(KeyEvent.VK_UP);
            testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Key up action!", true);
        }
        for (int x=0;x<leftCount;x++) {
            robot.keyPress(KeyEvent.VK_LEFT);
            testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Key left action!", true);
        }
        for (int x=0;x<rightCount;x++) {
            robot.keyPress(KeyEvent.VK_RIGHT);
            testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Key right action!", true);
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

    /*************************************************************
     * DESCRIPTION:
     *      Performs a thread sleep to allow for items to load and
     *      is intended to be used prior to making an assertion
     *      that depends upon some change like a navigation or
     *      new items populating the page.
     * @param milliseconds - time in milliseconds to wait
     * @param fileStepIndex - indicates the file and step where
     *                      this command was issued.
     ************************************************************ */
    private void DelayCheck(int milliseconds, String fileStepIndex) throws InterruptedException {
        testHelper.UpdateTestResults(AppConstants.indent5 + "Sleeping for " + milliseconds + " milliseconds for script " + fileStepIndex, true);
        Thread.sleep(milliseconds);
    }

    /*************************************************************************
     * DESCRIPTION: Waits a maximum of maxTimeInSeconds, which can come from
     *      the test command or default to 10 seconds, for the presence
     *      of the element or page.
     *      Reports if the element or page was present within the
     *      maxTimeInSeconds time limit.
     * @param ts -
     * @param fileStepIndex -
     *********************************************************************** */
    private void WaitForElement(TestStep ts, String fileStepIndex) {
        Boolean pageLoadComplete = false;
        String accessorType = ts.get_accessorType() != null ? ts.get_accessorType().toLowerCase().trim() : null;
        String accessor = ts.get_accessor()!= null ? ts.get_accessor().trim() : null;
        String elementIdentifier = ts.get_command().toLowerCase().trim().contains("page") ? GetArgumentValue(ts, 0, "n/a") : GetArgumentValue(ts, 0, null);
        int maxTimeInSeconds = GetArgumentNumericValue(ts, 1, AppConstants.DefaultElementWaitTimeInSeconds);


        //check that this argument is present
        if ((elementIdentifier == null || elementIdentifier.isEmpty()) && (accessorType == null || accessorType.isEmpty())) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + AppConstants.indent5 + "Improperly formatted test step.  Skipping step " + fileStepIndex, true);
            return;
        }

        if (ts.get_command().toLowerCase().trim().contains("page"))
        {
            accessorType = "page";
            testHelper.UpdateTestResults(AppConstants.indent5 + "Waiting a maximum of " + maxTimeInSeconds + " seconds for page load to complete at step " + fileStepIndex, true);
        } else {
            testHelper.UpdateTestResults(AppConstants.indent5 + "Waiting a maximum of " + maxTimeInSeconds + " seconds for presence of element " + accessor + " at step " + fileStepIndex, true);
        }

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
                    if (!elementIdentifier.toLowerCase().trim().contains("n/a")) {
                        try {
                            testHelper.NavigateToPage(driver, elementIdentifier);
                        } catch (Exception ex) {
                            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to navigate error: " + ex.getMessage(), true);
                        }
                    }
                    pageLoadComplete = new WebDriverWait(driver, maxTimeInSeconds).until(
                            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                    break;
                default:  //default to xpath if missing
                    element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor)));
                    break;
            }
            if (!ts.get_command().toLowerCase().trim().contains("page")) {
                if (element != null) {
                    testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful load of element " + accessor + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndex, true);
                }
            } else {
                if (pageLoadComplete) {
                    testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful load of page " + GetCurrentPageUrl() + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndex, true);
                }
            }
        } catch (TimeoutException ae) {
            if (ts.get_command().toLowerCase().trim().contains("page")) {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to find the element " + GetCurrentPageUrl() + " within the set max time of " + maxTimeInSeconds + " at step " + fileStepIndex + " AL+", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to load element " + accessor + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndex, true);
            }
            if (ts.get_crucial()){
                throw (ae);
            }
        }
    }

    /***********************************************************************
     * DESCRIPTION: Switches to a different tab either the child or
     *      the parent tab.
     * @param isChild -
     * @param fileStepIndex -
     ********************************************************************* */
    private void SwitchToTab(boolean isChild, String fileStepIndex) {
        int tab = 0;
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        tab = isChild ? 1 : 0;
        //String handleName = tabs.get(1);
        String handleName = tabs.get(tab);
        driver.switchTo().window(handleName);
        System.setProperty("current.window.handle", handleName);
        testHelper.UpdateTestResults(AppConstants.indent5 + "Switched to New tab with url = " + driver.getCurrentUrl(), true);
    }

    /*************************************************************
     * DESCRIPTION: Performs a screen shot capture by calling the
     *      screen shot capture method in the pageHelper class.
     * @param value
     ************************************************************ */
    private void PerformScreenShotCapture(String value) {
        testHelper.captureScreenShot(driver, value, screenShotSaveFolder, false);
    }

    /*************************************************************
     * DESCRIPTION:
     *      Calls the NavigateToPage method passing the driver
     *      and the destination URL, where a default 10 second
     *      wait happens to allow the page to load
     *      and then returns the current URL
     * @param delayMilliSeconds - time in milliseconds to wait before
     *                          testing url
     ************************************************************ */
    public String CheckPageUrl(int delayMilliSeconds) throws Exception {
        testHelper.NavigateToPage(this.driver, testPage, delayMilliSeconds);

        if (!isAlertPresent()) {
            return this.driver.getCurrentUrl();
        } else {
            return null;
        }
    }

    /*********************************************************************
     * DESCRIPTION:
     *      Checks the URL without performing a navigation action.
     *      Compares what was passed in against the current URL.
     * @param ts
     * @param fileStepIndex
     ******************************************************************** */
    private void CheckUrlWithoutNavigation(TestStep ts, String fileStepIndex) throws InterruptedException {
        //check url without navigation
        String expectedUrl = ts.get_expectedValue();

        if (ts.ArgumentList != null && ts.ArgumentList.size() > 0) {
            int delayMilliSeconds = GetArgumentNumericValue(ts, 0, AppConstants.DefaultTimeDelay);
            DelayCheck(delayMilliSeconds, fileStepIndex);
        }
        String actualUrl = GetCurrentPageUrl();
        assertEquals(expectedUrl, actualUrl);
        if (expectedUrl.trim().equals(actualUrl.trim())) {
            testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "URL Check successful for step " + fileStepIndex + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")" + AppConstants.ANSI_RESET, true);
        } else {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "URL Check unsuccessful for step " + fileStepIndex + " Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")" + AppConstants.ANSI_RESET, true);
        }
    }

    /**************************************************************
     * DESCRIPTION:  Check to see if an Alert window is present.
     *
     * @return - true if alert window present, else return false
     **************************************************************/
    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /****************************************************************************
     * DESCRIPTION:
     *          Creates a file with element properties and attributes to allow
     *          users to more quickly create test files without inspecting each
     *          page element to do so.
     *
     * @param ts
     *          expected values [0] = create_test_page
     *          expected values [1] = Tag type to lookup (*) for all tags, defaults
     *                              to * if left empty
     *          expected values [2] = File where the results should be written
     *          expected values [3] = Comma Delimited List of tags to skip when
     *                              all tags is the lookup type.
     *                             (Ignored for specific tag lookups.)
     *
     * @param fileStepIndex
     *
     * ╠n/a ; create_test_page  ╬ * ╬ C:\Tests Pages\TestFileOutput_A.txt ╬ html,head,title,meta,script,body,style ; n/a ; true ; false╣
     *
     * @return
     *************************************************************************** */
    private String CreateTestPage(TestStep ts, String fileStepIndex) {
        String cssSelector = GetArgumentValue(ts, 0, "*");
        String newFileName =  GetArgumentValue(ts, 1, "/config/newTestFile.txt");
        String tagsToSkip = GetArgumentValue(ts, 2, null);
        String [] skipTags = tagsToSkip.split(",");
        boolean formatted = ts.get_command().toLowerCase().contains("format") ? true : false;

        //delete this file if it exists
        try {
            testHelper.DeleteFile(newFileName);
        } catch(Exception ex) {
            //let the delete file method handle this exception
        }

        //elements to skip if all elements used (*) - don't put this within the cssSelector assignment in case it is not provided
        testHelper.UpdateTestResults("tagsToSkip = " + tagsToSkip, false);

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
            String elementAltText;
            String inputType;
            Boolean isVisible = true;
            String script;

            if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(true));
                testHelper.WriteToFile(newFileName, CreateNavigationXmlTestStep(testPage, "TRUE"));
            } else {
                testHelper.WriteToFile(newFileName, "URL being used: " + testPage);
            }

            for (WebElement element : elements) {
                canProceed = true;
                outputDescription = "";
                elementType = element.getTagName();
                isVisible = element.isDisplayed();
                if (cssSelector.equals("*")) {
                    if (skipTags != null && skipTags.length > 0) {
                        for (String skipTag : skipTags) {
                            if (elementType.equals(skipTag)) {
                                canProceed = false;
                                break;
                            }
                        }
                    }
                } else {
                    canProceed = true;
                }

                if (canProceed) {
                    elementXPath = generateXPATH(element, "");
                    elementText = element.getText();

                    if (formatted) {
                        if (!elementType.equals("img")) {
                            if (isVisible) {
                                if (elementText != null && !elementText.isEmpty()) {
                                    outputDescription = CreateReadActionXmlTestStep(elementXPath, elementText, "FALSE");
                                }
                            }
                            else {
                                if (elementText != null && !elementText.isEmpty()) {
                                    outputDescription = "\t<!-- The following element is not visible by default -->\r\n";
                                    //script = "return arguments[0].innerHTML";
                                    script = "return arguments[0].innerText";
                                    elementText = (String) ((JavascriptExecutor) driver).executeScript(script, element);
                                    outputDescription += CreateReadActionXmlTestStep(elementXPath, elementText, "FALSE");
                                }
                            }
                        }
                    } else {
                        if (elementText != null && !elementText.isEmpty()) {
                            outputDescription = "Element Type: " + elementType + " - Element xPath: " + elementXPath + " - Element Text: " + elementText;
                        }
                    }

                    if (elementType.equals("img")) {
                        elementSrc = element.getAttribute("src");
                        elementAltText = element.getAttribute("alt");
                        if (formatted) {
                            outputDescription = CreateImageReadActionsXmlTestSteps(elementXPath, elementSrc, elementAltText, "FALSE");
                        } else {
                            outputDescription += " - Element Src: " + elementSrc;
                        }
                    } else if (elementType.equals("a")) {
                        elementHref = element.getAttribute("href");
                        if (formatted && !elementHref.isEmpty()) {  //make sure that this is not an anchor
                            outputDescription += CreateAHrefReadActionXmlTestStep(elementXPath, elementHref, "FALSE");
                        } else if (!elementHref.isEmpty()) {
                            outputDescription += " - Element Href: " + elementHref;
                        } else {
                            outputDescription = "\t<!--  The following element is an Anchor, not a link. -->\r\n" + outputDescription;
                        }
                    } else if (elementType.equals("input")) {
                        inputType = element.getAttribute("type");
                        if (formatted) {
                            if (inputType.equals("text")) {
                                outputDescription = CreateSendKeysWriteActionXmlTestStep(elementXPath, "[keys to send]", "FALSE");
                            } else if (inputType.equals("button") || inputType.equals("checkbox") || inputType.equals("radio")) {
                                outputDescription = CreateClickWriteActionXmlTestStep(elementXPath, "click", "FALSE");
                            }
                        }
                    }
                    else if (elementType.equals("select") && formatted) {
                        outputDescription = CreateSelectWriteActionXmlTestStep(elementXPath, "[value of option to select]", "FALSE");
                    }
                    if (!formatted) {
                        outputDescription += " Element Visible: " + isVisible;
                    }
                    if (outputDescription != null && !outputDescription.isEmpty()) {
                        testHelper.UpdateTestResults(outputDescription, true);
                        testHelper.WriteToFile(newFileName, outputDescription);
                    }
                }
            }
            if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(false));
            }
            testHelper.WriteToFile(newFileName, "");
        } catch (Exception ex) {
            testHelper.UpdateTestResults("Error: " + ex.getMessage(), false);
        }

        return newFileName;
    }

    /**************************************************************************
     * Description: This Creates the start and end XML tags that contain the
     *              Test Steps.
     *
     * @param isStart - boolean determining if start or end should be created
     * @return - Start or End XML tags.
     **************************************************************************/
    private String CreateXmlFileStartAndEnd(boolean isStart) {
        String returnValue = "";

        if (isStart) {
            returnValue = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
                    "<testSteps>";
        } else {
            returnValue = "</testSteps>";
        }

        return returnValue;
    }

    /*******************************************************************************************
     * Description: This Creates the Navigation Test Step to navigate to the page to be tested.
     *
     * @param testPage - Page URL where testing is to begin.
     * @param isCrucial - Flag to set the crucial
     * @return - XML Test Step
     *******************************************************************************************/
    private String CreateNavigationXmlTestStep(String testPage, String isCrucial) {
        String returnValue = "";

        if (testPage != null && !testPage.isEmpty()) {
            returnValue = "\t<step>\r\n" +
                    "\t\t<!-- Navigate to a page - The driver will go to the page in arg1 and compare that URL with the expected value if provided -->\r\n" +
                    "\t\t<command>navigate</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<!-- Expected value - required only when validating successful navigation. For this command it is optional but suggested. -->\r\n" +
                    "\t\t<expectedValue>" + testPage + "</expectedValue>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<arguments>\r\n" +
                    "\t\t\t<!-- first argument expected by the command - A URL is expected for this command; It is also Required!!! -->\r\n" +
                    "\t\t\t<arg1>" + testPage + "</arg1>\r\n" +
                    "\t\t\t<!-- second argument, can be optional. For this command it is the time in milliseconds to wait before the assertion is made. -->\n" +
                    "\t\t\t<arg2>1000</arg2> \n" +
                    "\t\t\t<!-- third argument is optional and is for the window dimensions. Add them like this if desired. (w=800 h=800) -->\n" +
                    "\t\t\t<arg3></arg3> \n" +
                    "\t\t</arguments>\n" +
                    "\t</step>";
        }
        return returnValue;
    }

    /**************************************************************************
     * Description: This Creates a Test Step that Selects an Option from
     *              a Select list.
     * @param elementXPath - xPath for the element
     * @param selectedItem - value to select
     * @param isCrucial - Flag to set the crucial
     * @return - XML Test Step
     ************************************************************************ */
    private String CreateSelectWriteActionXmlTestStep(String elementXPath, String selectedItem, String isCrucial) {
        String returnValue = "";

        if (elementXPath != null && !elementXPath.isEmpty()) {
            returnValue = "\t<step>\r\n" +
                    "\t\t<!-- multiple keystroke command... SENDKEYS! -->\r\n" +
                    "\t\t<command>sendkeys</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<!-- the accessor is the target element where the key strokes will be sent to -->\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<arguments>\r\n" +
                    "\t\t\t<!-- We can have as many key strokes as we want. We will use the arg tags for storing each character/command. The order or writing would be sequetial: arg1 > arg2 > arg3 > etc. If you want to send a string, just enter the whole string in one arg tag-->\r\n" +
                    "\t\t\t<arg1>" + selectedItem + "</arg1>\r\n" +
                    "\t\t\t<!-- Last argument is the override time delay between sending keystrokes and is not required -->\r\n" +
                    "\t\t\t<arg4>500</arg4>\r\n" +
                    "\t\t</arguments>\r\n" +
                    "\t</step>";
        }
        return returnValue;
    }

    /*************************************************************************************
     * Description: This Creates a Test Step that Clicks an element.
     *
     * @param elementXPath - xPath for the element
     * @param clickCommand - Command to perform.
     * @param isCrucial - Flag to set the crucial
     * @return - XML Test Step
     *************************************************************************************/
    private String CreateClickWriteActionXmlTestStep(String elementXPath, String clickCommand, String isCrucial) {
        String returnValue = "";
        if (elementXPath != null && !elementXPath.isEmpty()) {
            returnValue = "\t<step>\n" +
                    "\t\t<!-- Click command... clicks on the element based on the accessor -->\r\n" +
                    "\t\t<command>" + clickCommand + "</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<!-- the accessor is the target element where the key strokes will be sent -->\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>" +
                    "\t</step>";
        }
        return returnValue;
    }

    /*************************************************************************************
     * Description: This Creates a Test Step that Sends key strokes to an input type element.
     *
     * @param elementXPath - xPath for the element
     * @param argumentString - Command to perform.
     * @param isCrucial - Flag to set the crucial
     * @return - XML Test Step
     *************************************************************************************/
    private String CreateSendKeysWriteActionXmlTestStep(String elementXPath, String argumentString, String isCrucial) {
        String returnValue = "";

        if (elementXPath != null && !elementXPath.isEmpty()) {
            returnValue = "\t<step>\n" +
                    "\t\t<!-- multiple keystroke command... SENDKEYS! -->\n" +
                    "\t\t<command>sendkeys</command>\n" +
                    "\t\t<actionType>write</actionType>\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\n" +
                    "\t\t<!-- the accessor is the target element where the key strokes will be sent to -->\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\n" +
                    "\t\t<accessorType>xPath</accessorType>\n" +
                    "\t\t<arguments>\n" +
                    "\t\t\t<!-- Send as many key strokes as needed. Each arg tag can store a key stroke. Arguments sent in sequetial order: arg1 > arg2 > arg3 > etc. -->\r\n" +
                    "\t\t\t<!-- When sending a string, just enter the whole string in one arg tag (arg1) -->\n" +
                    "\t\t\t<arg1>" + argumentString + "</arg1>\n" +
                    "\t\t</arguments>\n" +
                    "\t</step>";
        }
        return returnValue;
    }

    /*************************************************************************************
     * Description: This Creates a Test Step that Reads the href value of an anchor element.
     *
     * @param elementXPath - xPath for the element
     * @param elementHref - expected value of the anchor's href attribute
     * @param isCrucial  - Flag to set the crucial
     * @return - XML Test Step
     *************************************************************************************/
    private String CreateAHrefReadActionXmlTestStep(String elementXPath, String elementHref, String isCrucial) {
        String returnValue = "";
        if (elementXPath != null && !elementXPath.isEmpty() && elementHref != null && !elementHref.isEmpty()) {
            returnValue = "\r\n\t<step>\r\n" +
                    "\t\t<!-- Compares the href of the anchor link element using the accessor against the expectedValue -->\r\n" +
                    "\t\t<!--<command>CHECK A HREF</command>-->\r\n" +
                    "\t\t<command>CHECK A HREF</command>\r\n" +
                    "\t\t<actionType>read</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial +"</crucial>\r\n" +
                    "\t\t<!-- the accessor is the anchor whose href attribute will be checked against the expectedValue -->\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<expectedValue>" + elementHref + "</expectedValue>\r\n" +
                    "\t</step>";
        }
        return returnValue;
    }

    /*************************************************************************************
     * Description: This Creates up to two Test Steps if the image's src and alt
     *              attributes are present, but if not, creates one based on what is provided.
     *              The first Test Step Reads the src value of an image element.
     *              The second Test Step Reads the alt value of the image element.
     *
     * @param elementXPath  - xPath for the element
     * @param elementSrc - expected value of the image's src attribute
     * @param elementAltText - expected value of the image's alt attribute
     * @param isCrucial  - Flag to set the crucial
     * @return - XML Test Steps
     *************************************************************************************/
    private String CreateImageReadActionsXmlTestSteps(String elementXPath, String elementSrc, String elementAltText, String isCrucial) {
        String returnValue = "";

        if (elementSrc != null && !elementSrc.isEmpty()) {
            returnValue = "\t<step>\r\n" +
                    "\t\t<command>check image src</command>\r\n" +
                    "\t\t<actionType>read</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<expectedValue>" + elementSrc + "</expectedValue>\r\n" +
                    "\t</step>";
        }

        if (elementAltText != null && !elementAltText.isEmpty()) {
            returnValue += "\t<step>\r\n" +
                    "\t\t<command>check image src</command>\r\n" +
                    "\t\t<actionType>read</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<expectedValue>" + elementAltText + "</expectedValue>\r\n" +
                    "\t</step>";
        }

        return returnValue;
    }

    /**********************************************************************************
     * Description: Helper method for the Create test method that creates the assert
     *              test step for formatted tests.
     *
     * @param elementXPath - xPath for the element
     * @param elementText - expected value of the page element
     * @param isCrucial - Flag to set the crucial
     * @return - XML Test Step
     **********************************************************************************/
    private String CreateReadActionXmlTestStep(String elementXPath, String elementText, String isCrucial) {

        if (elementText.contains("<")) {
            elementText = "<![CDATA[ " + elementText.trim() + " ]]>";
        }

        String readActionTestStep = "\t<step>\r\n" +
                "\t\t<command>assert</command>\r\n" +
                "\t\t<actionType>read</actionType>\r\n" +
                "\t\t<expectedValue>" + elementText + "</expectedValue>\r\n" +
                "\t\t<crucial>"+ isCrucial + "</crucial>\r\n" +
                "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                "\t\t<accessorType>xPath</accessorType>\r\n" +
                "\t</step>";

        return readActionTestStep;
    }

    //endregion

    //region { Utility Methods }
    /**********************************************************************
     * DESCRIPTION: Generates the full xPath from the root html
     *              for the childElement passed in.
     * @param childElement - the childElement passed in
     * @param current - currently not used, send empty string
     * @return - xPath of the childElement passed in
     **********************************************************************/
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
    //endregion

    /**************************************************************************
     * Description: Opens a Sql Server connection and sets the global
     *              sqlConnection object so that it can be used
     *              throughout the rest of the test.
     * @param ts
     * @param fileStepIndex
     **************************************************************************/
    private void SetSqlServerClient(TestStep ts, String fileStepIndex) {
        String sqlDatabaseName = GetArgumentValue(ts, 1, null);
        String sqlUserId = GetArgumentValue(ts, 2, null);
        String sqlPassword = GetArgumentValue(ts, 3, null);
        String sqlConnectionString = sqlDatabaseName.contains("jdbc:sqlserver") ? sqlDatabaseName : null;

        if (sqlDatabaseName != null && sqlUserId != null && sqlPassword != null && sqlConnectionString == null) {
            sqlConnectionString = sqlConnectionString == null ? "database=" + sqlDatabaseName + ";user=" + sqlUserId + ";password=" + sqlPassword + ";" : sqlConnectionString;
            if (sqlConnectionString != null && !sqlConnectionString.isEmpty()) {
                //sqlConnectionString = "jdbc:sqlserver://localhost:1433;" + sqlConnectionString + "encrypt=true;trustServerCertificate=false;loginTimeout=30;";
                sqlConnectionString = "jdbc:sqlserver://localhost:1433;" + sqlConnectionString + "encrypt=false;trustServerCertificate=true;loginTimeout=30;";
            }
        }

        try {
            sqlConnection = DriverManager.getConnection(sqlConnectionString);
            testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful establishment of connection to SQL Server Database for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
        } catch(SQLException e) {
            testHelper.UpdateTestResults("Failure", true);
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to establish a connection to the SQL Server for step " + fileStepIndex + "\r\n Error Message: " + e.getMessage() + AppConstants.ANSI_RESET, true);
        }
    }

    /******************************************************************************************
     * Description: Runs a Sql Server query that returns one field, retrieves that fields value
     *              and compares it to the expected value.
     * @param ts
     * @param fileStepIndex
     * @throws SQLException
     ******************************************************************************************/
    private void RunSqlServerQuery(TestStep ts, String fileStepIndex) throws SQLException {
        String sqlTable = GetArgumentValue(ts, 0, null);
        String sqlField = GetArgumentValue(ts, 1, null);
        String whereClause = GetArgumentValue(ts, 2, null);
        String sqlStatement = sqlTable.toLowerCase().contains("select") ? sqlTable : null;
        String actual = null;
        String comparisonType = GetArgumentValue(ts, ts.ArgumentList.size()-1, "=");


        if (sqlConnection == null) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to find active Sql Server connection to the SQL Server for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
        }

        //in case the comparison type is not passed in, default it to equals.
        if (!comparisonType.equals("!=") && !comparisonType.equals("=")) {
            comparisonType = "=";
        }

        Statement statement = sqlConnection.createStatement();
        ResultSet resultSet = null;

        try {
            if (sqlStatement == null || sqlStatement.isEmpty()) {
                sqlStatement = "Select " + sqlField + " from " + sqlTable + " " + whereClause;
            }
            //testHelper.UpdateTestResults(AppConstants.indent8 + "sqlStatement = " + sqlStatement, true);
            testHelper.UpdateTestResults(AppConstants.indent5 + "sqlStatement = " + sqlStatement, true);

            resultSet = statement.executeQuery(sqlStatement);
            while (resultSet.next()) {
                actual = resultSet.getString(1);
                break;
            }

            if (ts.get_crucial()) {
                if (comparisonType.equals("=")) {
                    assertEquals(ts.get_expectedValue(), actual);
                } else {
                    assertNotEquals(ts.get_expectedValue(), actual);
                }
            } else {
                if (comparisonType.equals("=")) {
                    if (ts.get_expectedValue().trim().equals(actual.trim())) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful Sql Query for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") Actual: (" + actual + ")" + AppConstants.ANSI_RESET, true);
                    } else {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed Sql Server for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") Actual: (" + actual + ")" + AppConstants.ANSI_RESET, true);
                    }
                } else {
                    if (!ts.get_expectedValue().trim().equals(actual.trim())) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful Sql Query for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") != Actual: (" + actual + ")" + AppConstants.ANSI_RESET, true);
                    } else {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed Sql Server for step " + fileStepIndex + " Expected: (" + ts.get_expectedValue() + ") != Actual: (" + actual + ")" + AppConstants.ANSI_RESET, true);
                    }
                }
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to execute query successfully for step " + fileStepIndex + "\r\n Error: " + e.getMessage() + AppConstants.ANSI_RESET, true);
        }
    }


    //region { Methods under development }
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
            } else if (rangeVariables[0].toLowerCase().contains("d=")) {
                contrastStandard = parseInt(rangeVariables[0].split("=")[1]);
                overRideMessage = "Difference value overridden to: " + contrastStandard;
                if (rangeVariables.length > 1) {
                    if (rangeVariables[1].toLowerCase().contains("b=")) {
                        brightnessStandard = parseInt(rangeVariables[1].split("=")[1]);
                        overRideMessage += "\r\nBrightness value overridden to: " + brightnessStandard;
                    }
                }
            }
            testHelper.UpdateTestResults(overRideMessage, true);
        }

        testHelper.UpdateTestResults(AppConstants.indent5 + "Retrieved " + elements.size() + " " + checkElement + " tags.", true);
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
                testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Good brightness and Good contrast forecolor(" + color + ") Fore-Color Brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + " Brightness Difference: " + brightness + " Color Difference: " + contrast + AppConstants.ANSI_RESET, false);
            } else if (brightness >= brightnessStandard && contrast < contrastStandard) {
                testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTWHITE + AppConstants.ANSI_RED + "Good brightness Warning contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + AppConstants.ANSI_RESET, false);
            } else if (brightness < brightnessStandard && contrast >= contrastStandard) {
                testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTWHITE + AppConstants.ANSI_RED + "Warning brightness and Good contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + AppConstants.ANSI_RESET, false);
            } else {
                testHelper.UpdateTestResults( AppConstants.ANSI_RED + "Warning brightness and Warning contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast +   AppConstants.ANSI_RESET, false);
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


    //region {Partially implemented MongoDb connectivity }
    private void RunMongoQuery(TestStep ts, String fileStepIndex) {
        testHelper.UpdateTestResults("In RunMongoQuery method", false);
        testHelper.UpdateTestResults("RunMongoQuery....in first If Statement", false);
        String queryDataBase = GetArgumentValue(ts, 0, null);
        String queryTable = GetArgumentValue(ts, 1, null);
        String queryField = GetArgumentValue(ts, 2, null);
        String whereClause = GetArgumentValue(ts, 3, null);
        String objectElement = whereClause != null && whereClause.isEmpty() && whereClause.toLowerCase().contains("where") ? null : whereClause;
        if (objectElement != null) {
            whereClause = null;
        }

        if (queryDataBase == null || queryTable == null || queryField == null) {
            String errorMissingStructure = queryDataBase == null ? "Database" : queryTable == null ? "Table" : "Field";
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "ERROR: Invalid Query Command, " + errorMissingStructure +
                    " is missing! Aborting Test Step " + fileStepIndex, true);
            return;
        }

        testHelper.UpdateTestResults("RunMongoQuery: queryDataBase = " + queryDataBase, false);

        if (whereClause != null && !whereClause.isEmpty()) {
            testHelper.UpdateTestResults("RunMongoQuery: queryTable.toLowerCase().trim() = " + queryTable.toLowerCase().trim(), false);
            testHelper.UpdateTestResults("RunMongoQuery: queryField.toLowerCase() = " + queryField.toLowerCase(), false);

            testHelper.UpdateTestResults("whereClause = " + whereClause, false);
            MongoDatabase db = mongoClient.getDatabase(queryDataBase);
            MongoCollection<Document> col = db.getCollection(queryTable);

            List<Document> documents = (List<Document>) col.find().into(
                    new ArrayList<Document>());

            if (documents.size() > 0) {
                for (Document document : documents) {
                    testHelper.UpdateTestResults("document = " + document, false);
                }
            } else {
                testHelper.UpdateTestResults("No matching items found", false);
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
        } else {  //get the entire table of data if no where clause exists
            MongoDatabase db = mongoClient.getDatabase(queryDataBase);
            MongoCollection<Document> col = db.getCollection(queryTable);
            //List<Document> documents;
            FindIterable<Document> documents = null;
            Document doc = null;
            //if (queryParameters.length > 2) {
            if (queryField != null) {

                //region {commented code block 2}
//                    documents = (List<Document>) col.find("{" + queryParameters[3].toString() + ":" + queryParameters[4].toString() + "}").into(
//                            new ArrayList<Document>());
//                    documents = db.getCollection(queryParameters[2]).find("{ " +  queryParameters[3].toString() + ":" + queryParameters[4].toString() + " }"));
                //endregion
                BSONObject bsonObj = BasicDBObject.parse("{" + queryField.toString() + ":" + objectElement + "}");
//                    documents = db.getCollection(queryParameters[2]).find(((BasicDBObject) bsonObj)).first();
                doc = db.getCollection(queryTable).find(((BasicDBObject) bsonObj)).first();

                //NOTE: { Everything remaining in this if statement is for formatting and not necessary for the testing application }
                //code used below for troubleshooting not necessarily testing
                testHelper.UpdateTestResults("Doc = " + doc.toString(), false);
                //region { commented code block 3}
//                    pageHelper.UpdateTestResults("Doc = " + doc.toString()
//                            .replace("{{","\r\n" + pageHelper.indent5 + "{{\r\n " + pageHelper.indent5)
//                            .replace("}},","\r\n" + pageHelper.indent5 + "}},\r\n")
//                            .replace(",",",\r\n" + pageHelper.indent5));
                //endregion
                String[] docString = doc.toString().split(", ");
                int indent = 0;
                int padSize = 2;
                String tempItem = "";
                String tempItem2 = "";
                for (String item : docString) {
                    tempItem = "";
                    tempItem2 = "";
                    testHelper.UpdateTestResults("[indent set to: " + indent + "]", false);
                    //pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.trim() + " - (Unformatted)");
                    if ((item.contains("{{") || item.contains("[")) && !item.contains("[]")) {

                        while (item.indexOf("{{") > 0 || item.indexOf("[") > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("{{") + 2).trim().replace("{{", "\r\n" + testHelper.PadIndent(padSize, indent) + "{{\r\n");
                            testHelper.UpdateTestResults("tempItem = " + tempItem, false);
                            item = item.substring(item.indexOf("{{") + 2).trim();
                            testHelper.UpdateTestResults("item = " + item, false);
                            indent++;
                            testHelper.UpdateTestResults("[indent now set to: " + indent + "]", false);
                        }
                        if (item.length() > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item.trim();
                        }


                        testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW + tempItem + AppConstants.ANSI_RESET, false);
                        //region { commented code block 4 }
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
                        //endregion
                    } else if ((item.contains("}}") || item.contains("]")) && !item.contains("[]")) {
                        while (item.indexOf("}}") > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("}}") + 2).replace("}}", "\r\n" + testHelper.PadIndent(padSize, indent - 1) + "}}");
//                                tempItem += pageHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("}}") + 2).replace("}}", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "}}\r\n");
                            item = item.substring(item.indexOf("}}") + 2).trim();
                            indent--;
                        }
                        if (item.length() > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item;
//                                tempItem += pageHelper.PadIndent(padSize, indent) +  item + " - (also left over)";
                        }
//                            if (tempItem.contains("]")) {
//                                indent--;
//                            }
                        testHelper.UpdateTestResults(tempItem, false);
                        //pageHelper.UpdateTestResults(item);
                        //indent--;
                    } else {
                        testHelper.UpdateTestResults(testHelper.PadIndent(padSize, indent) + item.trim() + " - (No delimiters)", false);
                    }
                    //region { commented code block 5 }
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
                    //endregion
                }
            } else {
                List<Document> documents2;
                documents2 = (List<Document>) col.find().into(
                        new ArrayList<Document>());
            }
            //region { commented code block 6 }
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
           //endregion
        }
    }

    /* ********************************************************************
     * DESCRIPTION:
     *      Creates a new MongoDb Client Connection or closes an open
     *      connection.
     * IMPORTANT: Once able to successfully connect to and query the
     *            database, figure out what is worth logging but for now
     *            do not log anything except to the screen.
     ******************************************************************** */
    private void SetMongoClient(TestStep ts, String fileStepIndex) {
        //determine the type of mongo connection that needs to be used
        String connectionType = GetArgumentValue(ts, 3, null);
        String connectionString = GetArgumentValue(ts, 2, null);

        if (connectionType.toLowerCase().trim().equals("uri") && !connectionString.toLowerCase().contains("close")) {
            mongoClient = new MongoClient(new MongoClientURI(connectionString));
        } else if (!connectionString.toLowerCase().contains("close")) {
            //local connection?
            mongoClient = new MongoClient(connectionString);
        } else {
            mongoClient.close();  //close the connection
        }

        MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
        while (dbsCursor.hasNext()) {
            try {
                testHelper.UpdateTestResults(dbsCursor.next(), false);
                MongoDatabase db = mongoClient.getDatabase(dbsCursor.next());

                testHelper.UpdateTestResults("--[Tables - Start]----", false);
                MongoIterable<String> col = db.listCollectionNames();


                for (String table : col) {
                    testHelper.UpdateTestResults(AppConstants.indent5 + "Table = " + table, false);
                    FindIterable<Document> fields = db.getCollection(table).find();
                    testHelper.UpdateTestResults(AppConstants.indent5 + "--[Fields - Start]----", false);
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
                    testHelper.UpdateTestResults(AppConstants.indent5 + "--[Fields - End]----", false);
                }

                testHelper.UpdateTestResults("--[Tables - End]----", false);
                //col.forEach(String table : col)
                testHelper.UpdateTestResults("", false);
            } catch(Exception ex) {
                testHelper.UpdateTestResults("MongoDB error occurred: " + ex.getMessage(), false);
            }
        }
    }
    //endregion
    //endregion


    //region { Set Driver Methods }
    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the PhantomJs Driver
     **************************************************************************** */
    private void SetPhantomJsDriver() {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "PhantomJSDriver]" + AppConstants.ANSI_RESET , true);
        File src = new File(phantomJsDriverPath);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, src.getAbsolutePath());
        //IMPORTANT: for phantomJS you may need to add a user agent for automation testing as the default user agent is old
        // and may not be supported by the website.
        capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
        this.driver = new PhantomJSDriver(capabilities);
    }

    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Chrome Driver
     **************************************************************************** */
    private void SetChromeDriver() {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "ChromeDriver]" + AppConstants.ANSI_RESET , true);
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

    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the FireFox Driver
     **************************************************************************** */
    private void SetFireFoxDriver() {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "FireFoxDriver]" + AppConstants.ANSI_RESET , true);
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

    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Internet Explorer Driver
     *  This has been commented out because Internet Explorer runs incredibly
     *  slowly when sending text.
     **************************************************************************** */
    private void SetInternetExplorerDriver() {
        testHelper.UpdateTestResults("The Internet Explorer Browser was fully implemented but ran too slowly to be useful.  Please select another browser.", true);
        //internetExplorerDriverPath
        /*
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "InternetExplorerDriver]" + AppConstants.ANSI_RESET , testResults);
        File internetExplorer = new File(internetExplorerDriverPath);
        testHelper.UpdateTestResults("internetExplorer.getAbsolutePath() = " + internetExplorer.getAbsolutePath());

        System.setProperty("webdriver.ie.driver", internetExplorer.getAbsolutePath());
        File tmp = new File("C:\\Temp\\");

        DesiredCapabilities capab = DesiredCapabilities.internetExplorer();
        capab.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        driver = new InternetExplorerDriver(capab);
        */
    }

    /****************************************************************************
     *  DESCRIPTION:
     *  Not working yet: (Sets the WebDriver to the Edge Driver,
     *                    which is not available for Windows 7 yet)
     *  Think this reference is wrong but saving just in case.
     *  (https://stackoverflow.com/questions/51621782/osprocess-checkforerror-createprocess-error-193-1-is-not-a-valid-win32-appl)
     **************************************************************************** */
    private void SetEdgeDriver() {
        testHelper.UpdateTestResults("The Edge Browser Driver was not available at the time this application was created.  Please select another browser.", true);
        /*
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "EdgeDriver]" + AppConstants.ANSI_RESET , testResults);
        File edge = new File(edgeDriverPath);
        testHelper.UpdateTestResults("edge.getAbsolutePath() = " + edge.getAbsolutePath());

        System.setProperty("webdriver.edge.driver", edge.getAbsolutePath());
//        File tmp = new File("C:\\Temp\\");
//        EdgeOptions options = new EdgeOptions();
//        options.setCapability();
        driver = new EdgeDriver();
        */
    }
    //endregion

    //region {Lookup Methods}
    /**************************************************************
     * DESCRIPTION: Looks up the element type based on the last tag
     *              found in the xPath parameter.
     * @param xPath - an xPath value for the selected element
     * @return - the type of HTML element
     **************************************************************/
    private String ElementTypeLookup(String xPath) {
        //NOTE: When checking string equality in Java you must use the "".Equals("") method.
        // Using the == operator checks the memory address not the value
        String elementTag = xPath.substring(xPath.lastIndexOf("/") + 1).trim();

        if (elementTag.toLowerCase().startsWith("a") || elementTag.toLowerCase().startsWith("a[")) {
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
        if (elementTag.toLowerCase().startsWith("strong") || elementTag.toLowerCase().equals("strong")) {
            return "Strong";
        }
        if (elementTag.toLowerCase().startsWith("option") || elementTag.toLowerCase().equals("option")) {
            return "Option";
        }
        else {
           testHelper.UpdateTestResults(AppConstants.indent5 + "Failed to find element type for elementTag: (" + elementTag + ") Length = " + elementTag.length(), true);
        }
        return "Indeterminate";
    }


    /**************************************************************
     * DESCRIPTION: Looks up the Key code equivalent for the key code
     *              string passed in.
     * @param value - the key code string
     * @param fileStepIndex - the file and step index for reference.
     * @return
     ***************************************************************/
    private CharSequence GetKeyValue(String value, String fileStepIndex) {
        value = value.toLowerCase().trim();
       testHelper.UpdateTestResults(AppConstants.indent5 + "Replacing (" + value + ") with corresponding Key value keyword for step " + fileStepIndex, false);

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
            testHelper.UpdateTestResults(PageHelper.ANSI_RED + "Key: " + value + " " + fileStepIndex + " not mapped!" + AppConstants.ANSI_RESET, true);
        }
        return value;
    }

    //endregion



}
