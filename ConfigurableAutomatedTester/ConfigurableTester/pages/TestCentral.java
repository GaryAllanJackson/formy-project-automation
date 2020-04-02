import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import org.bson.BSONObject;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.*;

//import javax.swing.text.Document;
//import java.security.Timestamp;


//import javax.swing.text.Document;
//import java.security.Timestamp;
enum BrowserTypes {
    Chrome, Firefox, PhantomJS, Internet_Explorer, Edge
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
     *      1.  Look at Login. (Currently working on iOS)
     *          Not working on iOS but works on Windows 7.
     *      2.  Screenshot comparison using Image Magic
     *          (https://www.swtestacademy.com/visual-testing-imagemagick-selenium/)
     *      3.  Greater Than and Less Than Operators.
     *          This would be a good addition when used with Conditional Blocks.
     *      4.  Parsing text retrieved from an element and performing actions on this such as:
     *              a. Addition
     *              b. Subtraction
     *              c. Multiplication
     *              d. Division
     *          i.  Test provides the element accessor and in arguments:
     *              a. the delimiter
     *              b. First Number index
     *              c. Second Number Index
     *              d. Operator Index or the operator
     *              String [] elements = element.split(arg1);
     *
     ╚═══════════════════════════════════════════════════════════════════════════════╝ */
     //endregion

    //region { Accessor Type String Constants and Persist String Constants }
    private final String persistStringCheckValue = "persiststring";
    private final String persistedStringCheckValue = "persistedstring";
    private final String xpathCheckValue = "xpath";
    private final String cssSelectorCheckValue = "cssselector";
    private final String tagNameCheckValue = "tagname";
    private final String idCheckValue = "id";
    private final String classNameCheckValue = "classname";
    //endregion

    //region { Application Configuration Variables }
    private String configurationFile = "Config/ConfigurationSetup.xml";
    private String configurationFolder = "Config/";
    private static String testPage = "https://www.myWebsite.com/";
    private boolean runHeadless = true;
    private String screenShotSaveFolder;
    private BrowserTypes _selectedBrowserType; // = BrowserType.Firefox;    //BrowserType.Chrome;  //BrowserType.PhantomJS;
    private String createCSVStatusFiles = "none";
    //endregion

    private WebDriver driver;
    private TestHelper testHelper = new TestHelper();
    private HelperUtilities helperUtilities = new HelperUtilities();
    private boolean testAllBrowsers = false;  //true;
    private List<TestStep> testSteps = new ArrayList<>();
    private String testFileName;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
    private String logFileUniqueName = dateFormat.format(new Date());
    private String logFileRootFileName = "TestResults_";   //root name of the log file, change this not the logfile name
    private String logFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\" + logFileRootFileName + logFileUniqueName + ".log" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/" + logFileRootFileName + logFileUniqueName + ".log";
    private String helpFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\ConfigTester_Help.txt" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/ConfigTester_Help.txt";
    private List<String> testFiles = new ArrayList<>();

    //region { WebDriver Browser Driver Configured Locations }
    //private String chromeDriverPath = "/gary/java utilities/BrowserDrivers/chromedriver.exe";
    //private String fireFoxDriverPath = "/gary/java utilities/BrowserDrivers/geckodriver.exe";
    //private String phantomJsDriverPath = "/gary/java utilities/BrowserDrivers/phantomjs.exe";
    private String internetExplorerDriverPath = "/gary/java utilities/BrowserDrivers/IEDriverServer.exe";
    //private String edgeDriverPath = "/gary/java utilities/BrowserDrivers/msedgedriver.exe";
    //endregion

    //local global variables for values that need to live outside of a single method
    private boolean _executedFromMain = false;
    private MongoClient mongoClient = null;
    private Connection sqlConnection = null;
    private MongoClientURI mongoClientUri = null;
    private String persistedString = null;
    private String uniqueId = null;
    private boolean conditionalSuccessful = false;
    private String jsonContent = null;
    private String xmlContent = null;

    //region { Properties }
    boolean is_executedFromMain() {
        return _executedFromMain;
    }

    void set_executedFromMain(boolean _executedFromMain) {
        this._executedFromMain = _executedFromMain;
    }

    BrowserTypes get_selectedBrowserType() {
        return _selectedBrowserType;
    }

    void set_selectedBrowserType(BrowserTypes newValue) {
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
     *  Exception - When ColorUtility is enabled may
     *                  throw exception.
     ***************************************************************/
    public TestCentral() {
        //throws Exception {
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
    void ConfigurableTestController() throws Exception {
        TestCentralStart(is_executedFromMain());
        if (testAllBrowsers) {
            int maxBrowsers = 3;

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
                        break;*/
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

    /***********************************************************
     * Description: This is the TearDown method that will run
     *              after all processing to free up resources
     *              even in the event of a crucial assertion
     *              failure or an error condition.
     *
     * @throws Exception - May throw exception if driver is null
     *                  or if the called method throws a
     *                  SQL Exception.
     ***********************************************************/
    @AfterAll
    private void TearDown() throws Exception {
        try {
            driver.close();
            driver.quit();
        } catch(Exception e) {
            //the driver was never instantiated so do nothing here
        }
        PerformCleanup();
    }



    /**************************************************************************
     * Description: Default Constructor.  Reads the configuration file
     *              and the associated test file and when a site is not being
     *              tested using all browsers, it sets the browser that will
     *              be used for the test.
     * @param isStartedFromMain - indicates if this application was run
     *                            as a standalone application or as a JUnit
     *                            test within the IDE.
     *                          If true, run as standalone.
     *                          If false, run as JUnit test.
     *  Exception
     ***************************************************************************/
    void TestCentralStart(boolean isStartedFromMain) {
        this.set_executedFromMain(isStartedFromMain);
        testHelper.set_executedFromMain(isStartedFromMain);
        if (isStartedFromMain) {
            logFileName = logFileName.replace(logFileRootFileName, "StandAlone_" + logFileRootFileName);
        }

        File tmp = new File(configurationFile);
        testHelper.CreateSectionHeader("[ Starting Test Application Initialization ]", AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, true, false, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Config File absolute path = " + AppConstants.ANSI_RESET + tmp.getAbsolutePath(), false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Log File Name = " + AppConstants.ANSI_RESET  + logFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Help File Name = " + AppConstants.ANSI_RESET + helpFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 + "Executed From Main or as JUnit Test = " + AppConstants.ANSI_RESET + (is_executedFromMain() ? "Standalone App" : "JUnit Test"), false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 + "Running on "  + AppConstants.ANSI_RESET + (HelperUtilities.isWindows() ? "Windows" : "Mac"), false);
        testHelper.CreateSectionHeader("[ End Test Application Initialization ]", AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, false, false, false);
        testHelper.UpdateTestResults("", false);

        testHelper.set_logFileName(logFileName);
        testHelper.set_helpFileName(helpFileName);

        boolean status = ConfigureTestEnvironment();
        if (!status) {
            return;
        }

        testHelper.CreateSectionHeader("[ Beginning Configuration ]", AppConstants.FRAMED + AppConstants.ANSI_GREEN_BACKGROUND_BRIGHT + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, true, true, false);
        testHelper.UpdateTestResults(AppConstants.indent5 + "NOTE: Test Steps are numbered by File underscore then Test Step for traceability.\r\n" +
                AppConstants.indent5 + "Test Files and Test Steps begin at 0, so F0_S0 is the first Test File and the first Test Step in that file.", false);
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
            } else if (get_selectedBrowserType() == BrowserTypes.Edge) {
                SetEdgeDriver();
            }
            testHelper.CreateSectionHeader("[ Ending Configuration ]", AppConstants.FRAMED + AppConstants.ANSI_GREEN_BACKGROUND_BRIGHT + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, false, true, false);
        }
    }


    /****************************************************************************
     *  DESCRIPTION:
     *  Calls the ReadConfigurationSettings method, to read the config file.
     *  Sets configurable variables using those values.
     *************************************************************************** */
    private boolean ConfigureTestEnvironment() {
        String tmpBrowserType;
        ConfigSettings configSettings = testHelper.ReadConfigurationSettingsXmlFile(configurationFile, is_executedFromMain());

        if (configSettings != null) {
            tmpBrowserType = configSettings.get_browserType().toLowerCase();
            if (tmpBrowserType.contains("chrome")) {
                set_selectedBrowserType(BrowserTypes.Chrome);
            } else if (tmpBrowserType.contains("firefox")) {
                set_selectedBrowserType(BrowserTypes.Firefox);
            }  else if (tmpBrowserType.indexOf("internetexplorer") >= 0 || tmpBrowserType.indexOf("internet explorer") >= 0) {
                set_selectedBrowserType(BrowserTypes.Internet_Explorer);
            } else if (tmpBrowserType.indexOf("edge") >= 0) {
                set_selectedBrowserType(BrowserTypes.Edge);
            } else {
                set_selectedBrowserType(BrowserTypes.PhantomJS);
            }
            testFiles = configSettings.get_testFiles();
            testPage = configSettings.get_testPageRoot();
            this.runHeadless = configSettings.get_runHeadless();
            this.screenShotSaveFolder = configSettings.get_screenShotSaveFolder();
            this.testAllBrowsers = configSettings.get_testAllBrowsers();
            this.createCSVStatusFiles = configSettings.get_createCsvStatusFiles();

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
    void TestPageElements() throws Exception {
        if (this.driver == null) {
            return;
        }
        int startIndex = 0;  //used for instances when you do not want to start at the first element to test
        boolean revertToParent = false;
        String csvFileName;
        testHelper.set_csvFileName(null);

        for (int fileIndex = 0; fileIndex < testFiles.size(); fileIndex++) {
            testFileName = testFiles.get(fileIndex);
            testHelper.set_testFileName(testFileName);
            //Start - reset this for each test file
            //moved this here so that the Unique Identifier is created for each test file.
            uniqueId = testHelper.GetUniqueIdentifier();
            testSteps = new ArrayList<>();
            jsonContent = null;
            xmlContent = null;
            CloseOpenConnections();
            persistedString = null;
            boolean isConditionalBlock = false;
            conditionalSuccessful = false;
            testSteps = testHelper.ReadTestSettingsXmlFile(testSteps, testFileName);
            //testHelper.DebugDisplay("testHelper.get_csvFileName() = " + testHelper.get_csvFileName());
            if (this.createCSVStatusFiles.equals("many")) {
                SetCSVFileName(testFileName);  //added for individual CSV files
            } else if (this.createCSVStatusFiles.equals("one") && testHelper.get_csvFileName() == null) {
                SetCSVFileName(logFileName);
            } else if (this.createCSVStatusFiles.equals("none")) {
                testHelper.set_csvFileName(null);
            }
            testHelper.WriteToFile(testHelper.get_csvFileName(),"File And Step Number,Test Performed,Execution Status,Variable Output,Test File Name");
            //End - reset this for each test file
            testHelper.CreateSectionHeader("[ Running Test Script ]", AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW_BRIGHT, true, true, true);
            testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + "Running Test Script file: " + AppConstants.ANSI_RESET + testFileName, true);
            testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + "CSV Status file: " + AppConstants.ANSI_RESET + (testHelper.get_csvFileName() != null ? testHelper.get_csvFileName() : "N/A"), true);
//            if (1 == 1) {
//                return;
//            }
            for (int x = startIndex; x < testSteps.size(); x++) {
                if (revertToParent) {
                    driver.switchTo().defaultContent();
                    testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                    revertToParent = false;
                }
                TestStep ts = testSteps.get(x);
                String fileStepIndex = "F" + fileIndex + "_S" + x;
                if (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) {
                    isConditionalBlock = ts.get_isConditionalBlock();
                    testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + AppConstants.iFrameSectionTopLeft + testHelper.PrePostPad("[ Start of Conditional Block ]", "═", 9, 157) + AppConstants.iFrameSectionTopRight + AppConstants.ANSI_RESET, false);
                } else if (ts.get_command().toLowerCase().equals(AppCommands.End_Conditional)) {
                    isConditionalBlock = false;
                    conditionalSuccessful = false;
                    testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End of Conditional Block ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                }

                //region { Debug output statements for conditional block dependencies }
//                testHelper.DebugDisplay("isConditionalBlock = " + isConditionalBlock);
//                testHelper.DebugDisplay("conditionalSuccessful = " + conditionalSuccessful);
//                testHelper.DebugDisplay("ts.get_isConditionalBlock() = " + ts.get_isConditionalBlock());
                //endregion

                if ((isConditionalBlock && (conditionalSuccessful || (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()))) || (!isConditionalBlock && !conditionalSuccessful))
                {
                    if (ts.get_command().toLowerCase().contains(AppCommands.Switch_To_IFrame)) {
                        CheckiFrameArgumentOrder(ts);
                        String frameName = GetArgumentValue(ts, 0, null);
                        testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionTopLeft + testHelper.PrePostPad("[ Switching to iFrame: " + frameName + " for step " + fileStepIndex + " ]", "═", 9, 157) + AppConstants.iFrameSectionTopRight + AppConstants.ANSI_RESET, false);
                        if (frameName != null && !frameName.isEmpty()) {
                            driver.switchTo().frame(frameName);
                        }
                        revertToParent = true;
                    }

                    //testHelper.DebugDisplay("Command:" + ts.get_command() + " Action Type:" + ts.get_actionType().toLowerCase());
                    if (ts.get_actionType() != null && ts.get_actionType().toLowerCase().equals("write")) {
                        PerformWriteActions(ts, fileStepIndex);
                    } else {
                        PerformReadActions(ts, fileStepIndex);
                    }
                    if (revertToParent) {
                        driver.switchTo().defaultContent();
                        testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                        revertToParent = false;
                    }
                } else {
                    testHelper.UpdateTestResults("Conditional Failed!!!  Skipping Command: " + ts.get_command() + " for Step: " + fileStepIndex, true);
                }
            }
            testHelper.CreateSectionHeader("[ End of Test Script ]", AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW_BRIGHT, false, true, true);
        }
        if (is_executedFromMain()) {
            TearDown();
//            driver.close();
//            driver.quit();
//            PerformCleanup();
        }
    }

    private void SetCSVFileName(String testFileName) {
       String csvFileName = testFileName.contains(".xml") ? testFileName.replace(".xml", "_" + logFileUniqueName + ".csv") :
               testFileName.replace(".log", ".csv");
               //testFileName.replace(".log", "_" + logFileUniqueName + ".csv");
       if (csvFileName.contains("\\")) {
           csvFileName = csvFileName.substring(testFileName.lastIndexOf("\\") + 1);
       } else if (csvFileName.contains("/")) {
           csvFileName =  csvFileName.substring(testFileName.lastIndexOf("/") + 1);
       }
        //testHelper.UpdateTestResults("configurationFolder + csvFileName = '" + configurationFolder + "' '" + csvFileName + "'", false);
        testHelper.set_csvFileName(configurationFolder + csvFileName);  //added for individual CSV files
    }

    /*****************************************************************
     * Description: Created to clean up any resources that have not been
     *              destroyed/closed at this point.
     * @throws SQLException - Sql Server Object exception
     *****************************************************************/
    private void PerformCleanup() throws SQLException {
        CloseOpenConnections();
        //if (this.driver.toString().indexOf("Chrome") >= 0) {
        if (get_selectedBrowserType().equals(BrowserTypes.Chrome) && HelperUtilities.isWindows()) {
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
        if (HelperUtilities.isWindows()) {
            try {
                // Execute command
                String command = "taskkill /im chromedriver.exe /f";
                Process child = Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                testHelper.UpdateTestResults("The following error occurred while trying to shut down ChromeDriver: " + e.getMessage(), true);
            }
        }
    }

    /*********************************************************************
     * Description: Will close any open connections that have not been
     *              explicitly closed with a Test Step.
     ******************************************************************* */
    private void CloseOpenConnections() throws SQLException {
        String forgotToCloseConnectionMessage = "Application Safety Clean Up Process Closed open *DB* Connection!!!\r\n" +
                "Be sure to close all open database connections with a Close Connection command.\r\n" +
                "Closing connections ensures that all available database connections are not consumed and releases resources properly.";
        if (sqlConnection != null || mongoClient != null) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ WARNING!!! - RESOURCES NOT PROPERLY RELEASED ]", "═", 9, 157) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, false);
            if (sqlConnection != null) {
                sqlConnection.close();
                testHelper.UpdateTestResults(forgotToCloseConnectionMessage.replace("*DB*", "SQL Server"), true);
            }
            if (mongoClient != null) {
                mongoClient.close();
                testHelper.UpdateTestResults(forgotToCloseConnectionMessage.replace("*DB*", "MongoDB"), true);
            }
            testHelper.UpdateTestResults(AppConstants.ANSI_RED_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ WARNING!!! - RESOURCES NOT PROPERLY RELEASED ]", "═", 9, 157) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, false);
            testHelper.UpdateTestResults("", true);
        }
    }

    /*************************************************************************
     * Description: Closes a database connection object based on the parameters
     *              passed in.
     * @param databaseConnectionType - type of database connection to close
     * @param fileStepIndex -
     * @throws SQLException - Sql Server exception
     *************************************************************************/
    private void CloseOpenConnections(String databaseConnectionType, String fileStepIndex) throws SQLException {
        if (databaseConnectionType.equals(AppConstants.SqlServer)) {
            if (sqlConnection != null) {
                sqlConnection.close();
                sqlConnection = null;
                testHelper.UpdateTestResults("Successful closing of open Sql Server Connection for step " + fileStepIndex, true);
            }
        } else if (databaseConnectionType.equals(AppConstants.MongoDb)) {
            if (mongoClient != null) {
                mongoClient.close();
                mongoClient = null;
                testHelper.UpdateTestResults("Successful closing of open MongoDb Connection for step " + fileStepIndex, true);
            }
        }
    }


    /*********************************************************************
     * Description: This method returns the browser used as a string
     * @return Browser used by the driver.
     *********************************************************************/
    private String GetBrowserUsed() {
        return this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
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
            if (ts.get_command().toLowerCase().contains(AppCommands.CheckImage) || ts.get_command().toLowerCase().contains(AppCommands.Check_Image) ) {
                CheckImageSrcAlt(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.CheckAHref) || ts.get_command().toLowerCase().contains(AppCommands.Check_A_Href)) {
                CheckAnchorHref(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.PersistString) || ts.get_command().toLowerCase().equals(AppCommands.Persist_String)) {
                PersistValueController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_JSON)) {
                JsonController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_XML)) {
                XmlController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.ParseAndCalculateDouble)) {
                ParseAndCalculateDoubleController(ts, fileStepIndex);
            }  else if (ts.get_command().toLowerCase().equals(AppCommands.ParseAndCalculateLong)) {
                ParseAndCalculateLongController(ts, fileStepIndex);
            } else {
                CheckElementText(ts, fileStepIndex);
            }
        } else {
            if (ts.get_command().toLowerCase().contains(AppCommands.Check) && (ts.get_command().toLowerCase().contains(AppCommands.Post) ||
                    ts.get_command().toLowerCase().contains(AppCommands.Get))) {
                //refactored and moved to separate method
                CheckGetPostStatus(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Links)) {
                String url = GetArgumentValue(ts, 0, testPage);
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking page links for " + url, false);
                CheckBrokenLinks(ts, url, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Image))  {
                String url = GetArgumentValue(ts, 0, null);
                if (ts.get_command().toLowerCase().contains(AppCommands.Alt)) {
                    CheckADAImages(ts, url, AppCommands.Alt, fileStepIndex);
                } else if (ts.get_command().toLowerCase().contains(AppCommands.Src)) {
                    CheckADAImages(ts, url, AppCommands.Src, fileStepIndex);
                }
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Count)) {
                CheckElementCountController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Contrast)) {
                ColorContrastController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Query)) {
                //perform a database query
                DatabaseQueryController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Find)) {
                FindPhraseController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Get_JSON) || ts.get_command().toLowerCase().equals(AppCommands.Save_JSON) ) {
                JsonController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Get_XML) || ts.get_command().toLowerCase().equals(AppCommands.Save_XML) ) {
                XmlController(ts, fileStepIndex);
            }
        }
    }




    /*****************************************************************
     * Description: This method performs all Write related actions.
     *              This method is used for initial routing of all Write
     *              type actions determining which command is configured
     *              and routing to the associated method and passing along any
     *              required parameters.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************/
    private void PerformWriteActions(TestStep ts, String fileStepIndex) throws Exception {
        //Perform all non read actions below that use an accessor
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? GetArgumentValue(ts, 1, null) : null;

        if (ts.get_accessorType() != null && (((ts.get_accessorType().toLowerCase().contains(xpathCheckValue)) || (ts.get_accessorType().toLowerCase().contains(cssSelectorCheckValue)) ||
                (ts.get_accessorType().toLowerCase().contains(tagNameCheckValue)) || (ts.get_accessorType().toLowerCase().contains(idCheckValue)) ||
                (ts.get_accessorType().toLowerCase().contains(classNameCheckValue)))
                && (!ts.get_command().toLowerCase().contains(AppCommands.SendKeys) && !ts.get_command().toLowerCase().contains(AppCommands.Send_Keys)
                && !ts.get_command().toLowerCase().contains(AppCommands.Wait) && !ts.get_command().toLowerCase().contains(persistStringCheckValue)))) {
            PerformAccessorActionController(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().contains(AppCommands.SendKeys) || ts.get_command().toLowerCase().contains(AppCommands.Send_Keys) ||
                (command != null && (command.toLowerCase().equals(AppCommands.SendKeys) || command.toLowerCase().equals(AppCommands.Send_Keys)))) {
            SendKeysController(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().contains(AppCommands.WaitFor)) {
            //wait for a speficic element to load
            WaitForElement(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Connect_To_Database)) {
            String databaseType = GetArgumentValue(ts, 0, null);
            if (databaseType.toLowerCase().equals(AppCommands.MongoDb) || databaseType.toLowerCase().contains(AppCommands.Mongo)) {
                //connect to mongo db or close an open mongo db connection
                SetMongoClient(ts, fileStepIndex);
            } else if (databaseType.toLowerCase().contains(AppConstants.SqlServer.toLowerCase())) {
                //establish a connection to a sql server database - connection lives until closed or end of the test
                SetSqlServerClient(ts, fileStepIndex);
            } else {
                ArgumentOrderErrorMessage(ts, ts.get_command());
            }
        } else if (ts.get_command().toLowerCase().equals(AppCommands.CloseDatabaseConnection) || ts.get_command().toLowerCase().equals(AppCommands.CloseDatabase) ) {
            String databaseType = GetArgumentValue(ts, 0, null);
            if (databaseType.toLowerCase().equals(AppConstants.MongoDb.toLowerCase()) || databaseType.toLowerCase().contains(AppCommands.Mongo)) {
                CloseOpenConnections(AppConstants.MongoDb, fileStepIndex);
            } else if (databaseType.toLowerCase().contains(AppConstants.SqlServer.toLowerCase())) {
                CloseOpenConnections(AppConstants.SqlServer, fileStepIndex);
            }
        } else if (ts.get_command() != null && ts.get_command().toLowerCase().contains(persistStringCheckValue)) {
            PersistValueController(ts,fileStepIndex);
        } else if (ts.get_accessorType() == null || ts.get_accessorType().toLowerCase().contains("n/a")) {
            //TODO: FIGURE OUT WHAT YOU WERE TROUBLESHOOTING WITH THESE MESSAGES WHEN YOU SET THIS APPLICATION ASIDE
//                        pageHelper.UpdateTestResults("SearchType = n/a  - Good so far - Accessor: " + ts.get_xPath() +
//                                " Expected Value:" + ts.get_expectedValue() + " Lookup Type: " + ts.get_searchType() +
//                                " Perform Action: " + ts.getPerformWrite() + " IsCrucial: " + ts.get_isCrucial());
            //perform all non-read actions below that do not use an accessor
            if (ts.get_command().toLowerCase().contains(AppCommands.Navigate)) {
                PerformExplicitNavigation(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Wait) || ts.get_command().toLowerCase().equals(AppCommands.Delay)) {
                int delayMilliSeconds = GetArgumentNumericValue(ts, 0, 0);
                DelayCheck(delayMilliSeconds, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.ScreenShot)) {
                //scheduled screenshot capture action
                testHelper.UpdateTestResults(AppConstants.indent5 + "Taking Screenshot for step " + fileStepIndex, false);
                CheckScreenShotArgumentOrder(ts);
                String fileName = GetArgumentValue(ts, 0, null);
                String stringDimensions = GetArgumentValue(ts, 1, null);
                if (stringDimensions != null) {
                    SetScreenShotDimensions(stringDimensions);
                }
                if (fileName == null) {
                    //PerformScreenShotCapture(GetBrowserUsed() + "_" + ts.get_expectedValue() + "_" + fileStepIndex + "_", fileStepIndex);
                    PerformScreenShotCapture(GetBrowserUsed() + "_" + ts.get_command() + "_" + fileStepIndex + "_", fileStepIndex);
                }else {
                    PerformScreenShotCapture(fileName, fileStepIndex);
                }
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.URL)) {
                CheckUrlWithoutNavigation(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.SwitchToTab)) {
                SwitchToTab(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Login)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing login for step " + fileStepIndex, true);
                String userId = GetArgumentValue(ts, 0, null);
                String password = GetArgumentValue(ts, 1, null);
                String url = GetArgumentValue(ts, 2, GetCurrentPageUrl());
                if (testHelper.CheckIsUrl(url)) {
                    Login(url, userId, password, fileStepIndex);
                    testHelper.UpdateTestResults(AppConstants.indent5 + "Login complete for step " + fileStepIndex, true);
                } else {
                    ArgumentOrderErrorMessage(ts, ts.get_command());
                }
            } else if (ts.get_command().toLowerCase().contains(AppCommands.CreateTestPage) || ts.get_command().toLowerCase().contains(AppCommands.Create_Test_Page)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Create Test Page for step " + fileStepIndex, true);
                CheckCreateTestFileArgumentOrder(ts);
                //String createTestFileName = CreateTestPage(ts, fileStepIndex);
                String createTestFileName = CreateTestPage(ts);
                testHelper.UpdateTestResults("Create Test Page results written to file: " + createTestFileName, false);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Close_Child_Tab)) {
                CloseOpenChildTab(ts, fileStepIndex);
            }  else if (ts.get_command().toLowerCase().equals(AppCommands.Compare_Images)) {
                CompareImagesController(ts, fileStepIndex);
            }
        }
    }


    /************************************************************************
     * Description: This method parses the string dimensions passed in
     *              into integer values and then creates a Dimension object
     *              and saves that to the testHelper.savedDimension variable
     *              to control the dimensions of the screenshot that will be
     *              taken.
     * @param stringDimensions - Dimensions delimited by space and preceded
     *                         with the dimension identifier.
     ***********************************************************************/
    private void SetScreenShotDimensions(String stringDimensions) {
        int wStart = stringDimensions.toLowerCase().indexOf("w=");
        int hStart = stringDimensions.toLowerCase().indexOf("h=");
        int width;
        int height;
        if (wStart < hStart) {
            width = parseInt(stringDimensions.substring(stringDimensions.indexOf("w=") + 2, stringDimensions.indexOf("h=")).trim());
            height = parseInt(stringDimensions.substring(stringDimensions.indexOf("h=") + 2).trim());
            testHelper.savedDimension = stringDimensions != null ? new Dimension(width, height) : null;
        } else {
            height= parseInt(stringDimensions.substring(stringDimensions.indexOf("h=") + 2, stringDimensions.indexOf("w=")).trim());
            width = parseInt(stringDimensions.substring(stringDimensions.indexOf("w=") + 2).trim());
            testHelper.savedDimension = stringDimensions != null ? new Dimension(width, height) : null;
        }
    }


    //region { Controller Methods, used to control the program flow for the complicated items }
    //region {Controller Notes}
    /*
        Controller methods are acting as intermediaries helping to parse and test testStep values
        to ensure all values are present and correct before calling the actual command methods.
     */
    //endregion
    /*************************************************************************
     * Description: Control method used to Retrieve JSON from an API end point,
     *              persist it into a local variable and this method also
     *              Querries the local JSON variable for values.
     * @param ts - Test Step Object containing all related information
     *             for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @throws Exception - May throw exception if JSON retrieval fails.
     *************************************************************************/
    private void JsonController(TestStep ts, String fileStepIndex) throws Exception {
        if (ts.get_command().toLowerCase().equals(AppCommands.Get_JSON)) {
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_PURPLE_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start JSON Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
            jsonContent = GetHttpResponse(ts, fileStepIndex);
            if (jsonContent != null && !jsonContent.isEmpty()) {
                conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Successful JSON content retrieval for step " + fileStepIndex, true);
            } else {
                conditionalSuccessful = false;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Failed to retrieve JSON content for step " + fileStepIndex, true);
            }
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_PURPLE_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End JSON Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_JSON)) {
            QueryJSON(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Save_JSON)) {
            SaveJsonToFile(ts, fileStepIndex);
        }
    }

    /*************************************************************************
     * Description: Control method used to Retrieve XML from an API end point,
     *              persist it into a local variable and this method also
     *              Querries the local XML variable for values.
     * @param ts - Test Step Object containing all related information
     *             for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @throws Exception - May throw exception if JSON retrieval fails.
     *************************************************************************/
    private void XmlController(TestStep ts, String fileStepIndex) throws Exception {
        if (ts.get_command().toLowerCase().equals(AppCommands.Get_XML)) {
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_CYAN_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start XML Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
            xmlContent = GetHttpResponse(ts, fileStepIndex);
            if (!testHelper.IsNullOrEmpty(xmlContent)) {
                conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Successful XML content retrieval for step " + fileStepIndex, true);
            } else {
                conditionalSuccessful = false;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Failed to retrieve XML content for step " + fileStepIndex, true);
            }
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_CYAN_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End XML Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_XML)) {
            QueryXML(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Save_XML)) {
            SaveXmlToFile(ts, fileStepIndex);
        }
    }

    /***************************************************************************************
     * Description: This method retrieves the text from a page element and returns it to
     *              the calling method.
     * @param ts -  Test Step Object containing all related information
     *              for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return  - Text from page element.
     ***************************************************************************************/
    private String GetElementText(TestStep ts, String fileStepIndex) {
        String actual = null;  //element equation retrieved from the page
        switch (ts.get_accessorType().toLowerCase()) {
            case xpathCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by xPath: " + ts.get_accessor(), true);
                actual = CheckElementWithXPath(ts, fileStepIndex);
                break;
            case cssSelectorCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by CssSelector: " + ts.get_accessor(), true);
                actual = CheckElementWithCssSelector(ts, fileStepIndex);
                break;
            case tagNameCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by TagName: " + ts.get_accessor(), true);
                actual = CheckElementWithTagName(ts, fileStepIndex);
                break;
            case classNameCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by ClassName: " + ts.get_accessor(), true);
                actual = CheckElementWithClassName(ts, fileStepIndex);
                break;
            case idCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by Id: " + ts.get_accessor(), true);
                actual = CheckElementWithId(ts, fileStepIndex);
                break;
        }
        return actual;
    }

    /***************************************************************************************
     * Description: This method retrieves the simple calculation from a page element,
     *              parses that calculation text based on the arguments and performs
     *              the intended calculation using double data types and if the expected
     *              value is present, compares the calculated value to the expected
     *              value and reports the success or failure of that comparison.
     * @param ts -  Test Step Object containing all related information
     *              for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************************************/
    private void ParseAndCalculateDoubleController(TestStep ts, String fileStepIndex) {
        int firstIndex, secondIndex, operatorIndex;
        double firstNumber;
        double secondNumber;
        double resultNumber = 0;
        String delimiter, operator;
        String accessorPersist;
        String [] equation;
        delimiter = GetArgumentValue(ts, 0, " ");
        firstIndex = GetArgumentNumericValue(ts, 1, 0);
        secondIndex= GetArgumentNumericValue(ts, 2, 2);
        operatorIndex = GetArgumentNumericValue(ts, 3, 1);
        accessorPersist = GetArgumentValue(ts, 4, "persist");

        String actual = GetElementText(ts, fileStepIndex);
        if (actual != null && actual.contains(delimiter)) {
            equation = actual.split(delimiter);
            //region {Debugging information}
//            testHelper.DebugDisplay("delimiter = (" + delimiter + ")");
//            testHelper.DebugDisplay("firstNumber = equation[" + firstIndex + "] = " + equation[firstIndex]);
//            testHelper.DebugDisplay("secondNumber = equation[" + secondIndex + "] = " + equation[secondIndex]);
//            testHelper.DebugDisplay("operatorIndex = equation[" + operatorIndex + "] = " + equation[operatorIndex]);
//            testHelper.DebugDisplay("accessorPersist = " + accessorPersist);
            //endregion
            firstNumber = Double.parseDouble(equation[firstIndex]);
            secondNumber = Double.parseDouble(equation[secondIndex]);
            operator = equation[operatorIndex];
            if (operator.equals("+")) {
                resultNumber = firstNumber + secondNumber;
            } else if (operator.equals("-")) {
                resultNumber = firstNumber - secondNumber;
            } else if (operator.equals("*")) {
                resultNumber = firstNumber * secondNumber;
            } else if (operator.equals("/")) {
                resultNumber = firstNumber / secondNumber;
            }
            testHelper.UpdateTestResults("Solving equation: " + firstNumber + " " + operator + " " + secondNumber + " = " + resultNumber + " for step " + fileStepIndex, true);

            if (ts.get_expectedValue() != null) {
                double expected = Double.parseDouble(ts.get_expectedValue());
                if (resultNumber == expected) {
                    testHelper.UpdateTestResults("Successful Parse and Calculate Double Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                } else {
                    testHelper.UpdateTestResults("Failed Parse and Calculate Double Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                }
            }

            if (accessorPersist.toLowerCase().equals("persist")) {
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Calculated Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
                testHelper.UpdateTestResults("Persisting Calculated value: (" + resultNumber +  ")", true);
                persistedString = String.valueOf(resultNumber);
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting Calculated Value, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
            } else {
                TestStep testStep = new TestStep();
                testStep.set_command(AppCommands.SendKeys);
                testStep.set_crucial(ts.get_crucial());
                testStep.set_isConditionalBlock(ts.get_isConditionalBlock());
                testStep.set_accessor(accessorPersist);
                testStep.set_actionType("write");
                testStep.set_accessorType(ts.get_accessorType());
                Argument argument = new Argument();
                argument.set_parameter(String.valueOf(resultNumber));
                List<Argument> arguments = new ArrayList<>();
                arguments.add(argument);
                testStep.ArgumentList = arguments;
                PerformAction(testStep, String.valueOf(resultNumber), fileStepIndex);
            }
        }
    }

    /***************************************************************************************
     * Description: This method retrieves the simple calculation from a page element,
     *              parses that calculation text based on the arguments and performs
     *              the intended calculation using long data types and if the expected
     *              value is present, compares the calculated value to the expected
     *              value and reports the success or failure of that comparison.
     * @param ts -  Test Step Object containing all related information
     *              for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************************************/
    private void ParseAndCalculateLongController(TestStep ts, String fileStepIndex) {
        int firstIndex, secondIndex, operatorIndex;
        long firstNumber;
        long secondNumber;
        long resultNumber = 0;
        String delimiter, operator;
        String accessorPersist;
        //https://timesofindia.indiatimes.com/poll.cms
        String [] equation;
        delimiter = GetArgumentValue(ts, 0, " ");
        firstIndex = GetArgumentNumericValue(ts, 1, 0);
        secondIndex= GetArgumentNumericValue(ts, 2, 2);
        operatorIndex = GetArgumentNumericValue(ts, 3, 1);
        accessorPersist = GetArgumentValue(ts, 4, "persist");

        String actual = GetElementText(ts, fileStepIndex);
        if (actual != null && actual.contains(delimiter)) {
            equation = actual.split(delimiter);
            //region {Debugging code testing how decimals affect long conversion}
//            equation[firstIndex] = equation[firstIndex] + ".5";
//            equation[secondIndex] = equation[secondIndex] + ".8";
//            testHelper.DebugDisplay("delimiter = (" + delimiter + ")");
//            testHelper.DebugDisplay("firstNumber = equation[" + firstIndex + "] = " + equation[firstIndex]);
//            testHelper.DebugDisplay("secondNumber = equation[" + secondIndex + "] = " + equation[secondIndex]);
//            testHelper.DebugDisplay("operatorIndex = equation[" + operatorIndex + "] = " + equation[operatorIndex]);
//            testHelper.DebugDisplay("accessorPersist = " + accessorPersist);
            //endregion
            firstNumber = !equation[firstIndex].contains(".") ? Long.parseLong(equation[firstIndex]) : Long.parseLong(equation[firstIndex].substring(0, equation[firstIndex].indexOf(".")));
            secondNumber = !equation[secondIndex].contains(".") ? Long.parseLong(equation[secondIndex]) : Long.parseLong(equation[secondIndex].substring(0, equation[secondIndex].indexOf(".")));

            operator = equation[operatorIndex];
            if (operator.equals("+")) {
                resultNumber = firstNumber + secondNumber;
            } else if (operator.equals("-")) {
                resultNumber = firstNumber - secondNumber;
            } else if (operator.equals("*")) {
                resultNumber = firstNumber * secondNumber;
            } else if (operator.equals("/")) {
                resultNumber = firstNumber / secondNumber;
            }
            testHelper.UpdateTestResults("Solving equation: " + firstNumber + " " + operator + " " + secondNumber + " = " + resultNumber + " for step " + fileStepIndex, true);

            if (ts.get_expectedValue() != null) {
                double expected = Double.parseDouble(ts.get_expectedValue());
                if (resultNumber == expected) {
                    testHelper.UpdateTestResults("Successful Parse and Calculate Long Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                } else {
                    testHelper.UpdateTestResults("Failed Parse and Calculate Long Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                }
            }

            if (accessorPersist.toLowerCase().equals("persist")) {
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Calculated Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
                testHelper.UpdateTestResults(AppConstants.indent8 + "Persisting Calculated value: (" + resultNumber +  ")", true);
                persistedString = String.valueOf(resultNumber);
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting Calculated Value, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
            } else {
                TestStep testStep = new TestStep();
                testStep.set_command(AppCommands.SendKeys);
                testStep.set_crucial(ts.get_crucial());
                testStep.set_isConditionalBlock(ts.get_isConditionalBlock());
                testStep.set_accessor(accessorPersist);
                testStep.set_actionType("write");
                testStep.set_accessorType(ts.get_accessorType());
                Argument argument = new Argument();
                argument.set_parameter(String.valueOf(resultNumber));
                List<Argument> arguments = new ArrayList<>();
                arguments.add(argument);
                testStep.ArgumentList = arguments;
                PerformAction(testStep, String.valueOf(resultNumber), fileStepIndex);
            }
        }
    }

    /******************************************************************************
     * DESCRIPTION: Control method used to Check the count of a specific element type.
     *  @param ts - Test Step Object containing all related information
      *           for the particular test step.
      * @param fileStepIndex - the file index and the step index.
     ******************************************************************************/
    private void CheckElementCountController(TestStep ts, String fileStepIndex) {
        String checkItem = GetArgumentValue(ts, 0, null);
        String url = GetArgumentValue(ts,1,null);
        url = (url != null && url.contains("!=")) ? null:
                (url != null && url.contains("=")) ? null : url;
        String page = (url == null || url.equals("!=") || url.equals("=")) ? driver.getCurrentUrl() : url;

        if (checkItem != null) {
            testHelper.UpdateTestResults(AppConstants.indent5 + "Checking count of " + checkItem + " on page " + page, false);
            int expectedCount = ts.get_expectedValue() != null ? parseInt(ts.get_expectedValue()) : 0;
            CheckElementCount(ts, url, checkItem, expectedCount, fileStepIndex, ts.get_crucial());
        } else {
            ImproperlyFormedTest(fileStepIndex);
        }
    }


    /******************************************************************************
     * DESCRIPTION: Control method used to find a Phrase.
     *  @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
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
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     *
     ******************************************************************************/
    private void PersistValueController(TestStep ts, String fileStepIndex) throws Exception {
        persistedString = null;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Element Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisting value found by: " + ts.get_accessorType() + " accessor: " + ts.get_accessor(), true);
        persistedString = PersistValue(ts, ts.get_accessor(), fileStepIndex);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisted value = (" + persistedString + ")", true);
        conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock() && persistedString != null); // ? true : false;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting action, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
    }


    /**************************************************************************************
     * Description: This method is the controller method for Persisting Values.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param valueToPersist - Value that will be stored in the persistedString variable.
     * @param fileStepIndex - the file index and the step index.
     **************************************************************************************/
    private void PersistProvidedValueController(TestStep ts, String valueToPersist, String fileStepIndex) {
        persistedString = null;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Sub-command Element Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisting value as part of command:" + ts.get_command() + " found by: " + ts.get_accessorType() + " accessor: " + ts.get_accessor() + " for step " + fileStepIndex, true);
        persistedString = valueToPersist;
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisted value = (" + persistedString + ")", true);
        conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock() && persistedString != null);  // ? true : false;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting Sub-command action, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
    }


    /*********************************************************************************************
     * DESCRIPTION: Control method for performing all non-read actions that have an Accessor.
     *
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     * @throws InterruptedException - May throw Interrupted Exception especially when working with
     *                              context sensitive elements.
     **********************************************************************************************/
    private void PerformAccessorActionController(TestStep ts, String fileStepIndex) throws InterruptedException {
        boolean status;
        testHelper.UpdateTestResults(AppConstants.indent5 + "Performing action using " + ts.get_accessorType() + " " + fileStepIndex + " non-read action", true);
        String subAction = null;
        int delayMilliSeconds = 0;

        //check if switching to an iFrame
        if (ts.get_command() != null && !ts.get_command().toLowerCase().contains(AppCommands.Switch_To_IFrame)) {
            status = PerformAction(ts, null, fileStepIndex);
        } else {
            //subAction can either be the expected value or a command to perform like click
            if (ts.get_command() != null && ts.get_command().toLowerCase().contains(AppCommands.Switch_To_IFrame)) {
                subAction = GetArgumentValue(ts, 1, null);
            }
            status = PerformAction(ts, subAction, fileStepIndex);
        }

        //if not a right click context command
        if (!ts.get_command().toLowerCase().contains(AppCommands.Right_Click) && !ts.get_command().toLowerCase().contains(AppCommands.SendKeys)
                && !ts.get_command().toLowerCase().contains(AppCommands.Send_Keys) && !ts.get_command().toLowerCase().contains(AppCommands.Switch_To_IFrame)) {
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
                            testHelper.CaptureScreenShot(driver, GetBrowserUsed() + ts.get_accessorType() + fileStepIndex + "Element_Not_Found" + ts.get_accessor().replace(' ', '_'), configurationFolder, true, fileStepIndex);
                        } else {
                            testHelper.CaptureScreenShot(driver, GetBrowserUsed() + ts.get_accessorType() + fileStepIndex + "Element_Not_Found" + ts.get_accessor().replace(' ', '_'), screenShotSaveFolder, true, fileStepIndex);
                        }
                    }
                }
            }
            //if there is an expectedValue in a click event it is to validate that a new page has been navigated to
            if (expectedUrl != null) {
                if (expectedUrl.equals(actualUrl)) {
                    testHelper.UpdateTestResults("Successful Post Action results.  Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ") for step " + fileStepIndex, true);
                } else if (!expectedUrl.equals(actualUrl)) {
                    testHelper.UpdateTestResults("Failed Post Action results.  Expected URL: (" + expectedUrl + ") Actual URL: (" + actualUrl + ") for step " + fileStepIndex, true);
                }
            }
        }
    }



    /******************************************************************************
     * DESCRIPTION: Control method used to Check the count of a specific element type.
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     ******************************************************************************/
    private void SendKeysController(TestStep ts, String fileStepIndex) throws InterruptedException {
        Boolean isNumeric = false;
        String item;
        int timeDelay = 400;
        int counter = 0;

        isNumeric = CheckArgumentNumeric(ts, ts.ArgumentList.size() -1);
        if (isNumeric && (ts.get_command().toLowerCase().contains(AppCommands.SendKeys) || ts.get_command().toLowerCase().contains(AppCommands.Send_Keys)))
        {
            timeDelay = GetArgumentNumericValue(ts, ts.ArgumentList.size() -1, 400);
        }

        for (Argument argument : ts.ArgumentList) {
            item = argument.get_parameter();
            //if this is a switch to iframe command skip the first argument.
            if ((ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) && counter > 0) || !ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame)) {
                //if this is a switch to iframe command skip the "sendkeys" subcommand
                if (!item.toLowerCase().contains(AppCommands.SendKeys) && !item.toLowerCase().contains(AppCommands.Send_Keys)) {
                    boolean status = PerformAction(ts, item, fileStepIndex);
                    DelayCheck(timeDelay, fileStepIndex);
                }
            }
            counter++;
        }
    }


    /******************************************************************************
     * DESCRIPTION: Control method used to Check Color Contrast a value.
     *               actionType: read
     *               arg1: Type of Element to check against background
     *               arg2: [Optional and not recommended]
     *                      - Allows Overriding Acceptable Contrast settings b
     *                        for color brightness default is (125)
     *               arg3: [Optional and not recommended]
     *                      - Allows Overriding Acceptable Contrast settings d
     *                        for color difference default is (500)
     *
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     ****************************************************************************** */
    private void ColorContrastController(TestStep ts, String fileStepIndex) {
        CheckColorContrastArgumentOrder(ts);
        //region { Debugging - Looping to see all arguments }
//        for(int x=0; x < ts.ArgumentList.size();x++) {
//            testHelper.DebugDisplay("For loop ts.ArgumentList.get(" + x + ").get_parameter() = " + ts.ArgumentList.get(x).get_parameter());
//        }
        //endregion
        String tagType = GetArgumentValue(ts, 0, null);

        testHelper.UpdateTestResults(AppConstants.indent5 + "Checking color contrast of " + tagType + " on page " + testPage + " for step " + fileStepIndex, false);
        CheckColorContrast(ts, fileStepIndex);
    }


    /********************************************************************************
     * DESCRIPTION: Control method used to Query Databases for a particular field value
     * NOT FULLY IMPLEMENTED - The method that this method calls is not fully baked.
     *                      Need to decide on the different databases that will be
     *                      supported for this.
     *                      Initial concept was with MongoDB but it was a blind exercise
     *                      in which I did not have any direct access to the database
     *                      to check against.
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     ********************************************************************************/
    private void DatabaseQueryController(TestStep ts, String fileStepIndex)  {
        try {
            if (ts.get_command().toLowerCase().contains(AppCommands.Mongo)) {
                testHelper.UpdateTestResults("Found query then mongo....", false);
                //make sure that this connection has been established
                if (mongoClient != null) {
                    testHelper.UpdateTestResults("Found query, and mongo and in the if before RunMongoQuery....", false);
                    RunMongoQuery(ts, fileStepIndex);
                } else {
                    testHelper.UpdateTestResults("Connection is not available!!!", false);
                }
                testHelper.UpdateTestResults("Found query, and mongo after the if before RunMongoQuery....", false);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Sql_Server) || ts.get_command().toLowerCase().contains(AppCommands.SqlServer)) {
                testHelper.CreateSectionHeader("[ Start Sql Server Query Event ]", "", AppConstants.ANSI_CYAN, true, false, true);
                RunSqlServerQuery(ts, fileStepIndex);
                testHelper.CreateSectionHeader("[ End Sql Server Query Event ]", "", AppConstants.ANSI_CYAN, false, false, true);
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failure in DatabaseQueryController for step " + fileStepIndex  + "\r\n" + e.getMessage() + AppConstants.ANSI_RESET, true);
        }
    }


    /********************************************************************************************
     * Description: This method is the controller method for the Compare Images command.
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     * @throws Exception - May throw uncaught ImageMagick or File System Errors.
     *******************************************************************************************/
    private void CompareImagesController(TestStep ts, String fileStepIndex) throws Exception {
        String baseLineImage = GetArgumentValue(ts,0, null);
        String actualImage = GetArgumentValue(ts, 1, null);
        String differenceImage = GetArgumentValue(ts, 2, null);
        String globalDifferenceImage = GetArgumentValue(ts, 3, null);
        double acceptableDifference = GetArgumentNumericDoubleValue(ts, 4, 0);

        if (!testHelper.IsNullOrEmpty(baseLineImage) && !testHelper.IsNullOrEmpty(actualImage) && !testHelper.IsNullOrEmpty(differenceImage)) {
            helperUtilities.testHelper = testHelper;
            helperUtilities.set_acceptableDifference(acceptableDifference);
            testHelper.CreateSectionHeader("[ Start Image Comparison Test ]", "", AppConstants.ANSI_CYAN, true, false, true);
            testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Image Comparison for step " + fileStepIndex + "\r\n" +
                    AppConstants.indent8 + "(Baseline)Expected Image:" + baseLineImage + "\r\n" +
                    AppConstants.indent8 +  "Actual Image: " + actualImage, true);
            if (!testHelper.IsNullOrEmpty(globalDifferenceImage)) {
                helperUtilities.differenceFileForParent = new File(testHelper.GetUnusedFileName(globalDifferenceImage));
                //region { Refactored - remove if no longer necessary }
//                if (helperUtilities.isWindows()) {
//                    //helperUtilities.differenceFileForParent = new File(GetArgumentValue(ts, 3, helperUtilities.GetParentFolder(differenceImage).toString()));
//                    //helperUtilities.differenceFileForParent = new File(globalDifferenceImage != null ? globalDifferenceImage : helperUtilities.GetParentFolder(differenceImage).toString());
//                    helperUtilities.differenceFileForParent = new File(testHelper.GetUnusedFileName(globalDifferenceImage));
//                } else {
//                    //helperUtilities.differenceFileForParent = new File(globalDifferenceImage != null ? globalDifferenceImage : helperUtilities.EscapeMacPath(helperUtilities.GetParentFolder(differenceImage).toString()));
//                    helperUtilities.differenceFileForParent = new File(testHelper.GetUnusedFileName(globalDifferenceImage));
//                }
                //endregion
            } else {
                helperUtilities.differenceFileForParent = null;
            }
            helperUtilities.set_executedFromMain(is_executedFromMain());
            helperUtilities.CompareImagesWithImageMagick(baseLineImage, actualImage, differenceImage, fileStepIndex);
            testHelper.CreateSectionHeader("[ End Image Comparison Test ]", "", AppConstants.ANSI_CYAN, false, false, true);
        }
    }


    /*******************************************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the text of the element against the expected value.
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *
     * IMPORTANT NOTE: NOTICED THAT FOR INPUT CONTROLS GETTING TEXT IS NOT WORKING.
     *                 ADDED CHECK IN CHECKELEMENTWITHXXX MEHTODS AND IF TEXT IS NULL
     *                 FOR INPUT TYPE=TEXT RETURN THE VALUE ATTRIBUTE INSTEAD.
     ****************************************************************************************/
    private void CheckElementText(TestStep ts, String fileStepIndex) throws Exception {
        String actual = "";
        boolean notEqual = false;
        final String elementTypeCheckedAtStep = "Element type being checked at step ";
        String expected = ts.get_expectedValue();

        switch (ts.get_accessorType().toLowerCase()) {
            case xpathCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by xPath: " + ts.get_accessor(), true);
                actual = CheckElementWithXPath(ts, fileStepIndex);
                break;
            case cssSelectorCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by CssSelector: " + ts.get_accessor(), true);
                actual = CheckElementWithCssSelector(ts, fileStepIndex);
                break;
            case tagNameCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by TagName: " + ts.get_accessor(), true);
                actual = CheckElementWithTagName(ts, fileStepIndex);
                break;
            case classNameCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by ClassName: " + ts.get_accessor(), true);
                actual = CheckElementWithClassName(ts, fileStepIndex);
                break;
            case idCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by Id: " + ts.get_accessor(), true);
                actual = CheckElementWithId(ts, fileStepIndex);
                break;
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
                testHelper.CreateSectionHeader(AppConstants.indent5 +"[ Start of Persistence Usage Error ]", "", AppConstants.ANSI_RED, true, false, true);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "ERROR: No value previously persisted!!! " + AppConstants.ANSI_RESET + "Using empty string () instead of null for comparison.", true);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "IMPORTANT:" + AppConstants.ANSI_RESET + " A value must first be persisted before that persisted value can be used for comparison.", false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "NOTE:" + AppConstants.ANSI_RESET + " Values persisted in one test file are reset before the start of the next test file.", false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.indent5 + "Any values you want persisted for comparison, must first be persisted in the test file performing the comparison!!!",false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.indent5 + "Refer to the help file for more information regarding persisting and comparing persisted values.", false);
                testHelper.CreateSectionHeader(AppConstants.indent5 +"[ End of Persistence Usage Error ]", "", AppConstants.ANSI_RED, false, false, true);
                expected = "";
            }
        }

        if (ts.get_expectedValue() != null && ts.get_expectedValue().contains("<")) {
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
            //testHelper.UpdateTestResults("Successful equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex + " \r\n", true);
            testHelper.UpdateTestResults("Successful equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex,  true);
            conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
        } else if (!expected.equals(actual) && notEqual) {
            //testHelper.UpdateTestResults("Successful NOT equal (!=) comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex + " \r\n", true);
            testHelper.UpdateTestResults("Successful NOT equal (!=) comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex, true);
            conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
        } else if (!expected.equals(actual) && !notEqual) {
            //testHelper.UpdateTestResults("Failed equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex + " \r\n", true);
            testHelper.UpdateTestResults("Failed equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex, true);
            conditionalSuccessful = false;
            if (screenShotSaveFolder != null && !screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + fileStepIndex + "Assert_Fail", screenShotSaveFolder, false, fileStepIndex);
            }
        } else if (expected.equals(actual) && notEqual) {
            //testHelper.UpdateTestResults("Failed not equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex + " \r\n", true);
            testHelper.UpdateTestResults("Failed not equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex, true);
            conditionalSuccessful = false;
            if (screenShotSaveFolder != null && !screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + fileStepIndex + "Assert_Fail", screenShotSaveFolder, false, fileStepIndex);
            }
        }
    }

    /*********************************************************************
     * DESCRIPTION:
     *      Retrieves the value of the element using the configured accessor
     *      and returns it to the calling method where it will be
     *      persisted in a string variable.
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     * @param accessor - The element accessor
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************** */
    private String PersistValue(TestStep ts, String accessor, String fileStepIndex) throws Exception  {
        String actual = null;
        switch (ts.get_accessorType().toLowerCase()) {
            case xpathCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by xPath: " + accessor, true);
                actual = CheckElementWithXPath(ts, fileStepIndex);
                break;
            case cssSelectorCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by CssSelector: " + accessor, true);
                actual = CheckElementWithCssSelector(ts, fileStepIndex);
                break;
            case tagNameCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by TagName: " + accessor, true);
                actual = CheckElementWithTagName(ts, fileStepIndex);
                break;
            case classNameCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by ClassName: " + accessor, true);
                actual = CheckElementWithClassName(ts, fileStepIndex);
                break;
            case idCheckValue:
                testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by Id: " + accessor, true);
                actual = CheckElementWithId(ts, fileStepIndex);
                break;
        }
        return actual;
    }



    /*******************************************************************************
     * DESCRIPTION:  Checks an Image Src or Alt attribute
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *******************************************************************************/
    private void CheckImageSrcAlt(TestStep ts, String fileStepIndex) {
        String actualValue="";
        String srcAlt = GetArgumentValue(ts, 0, "src");
        testHelper.UpdateTestResults(AppConstants.indent8 + "Checking Image " + srcAlt + " for " + ts.get_expectedValue() + " for step " + fileStepIndex, true);

        switch (ts.get_accessorType().toLowerCase()) {
            case xpathCheckValue:
                actualValue = this.driver.findElement(By.xpath(ts.get_accessor())).getAttribute(srcAlt);
                break;
            case cssSelectorCheckValue:
                actualValue = this.driver.findElement(By.cssSelector(ts.get_accessor())).getAttribute(srcAlt);
                break;
            case tagNameCheckValue:
                actualValue = this.driver.findElement(By.tagName(ts.get_accessor())).getAttribute(srcAlt);
                break;
            case classNameCheckValue:
                actualValue = this.driver.findElement(By.className(ts.get_accessor())).getAttribute(srcAlt);
                break;
            case idCheckValue:
                actualValue = this.driver.findElement(By.id(ts.get_accessor())).getAttribute(srcAlt);
                break;
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
            testHelper.UpdateTestResults(AppConstants.indent8 + "Successful Image " + srcAlt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
        } else {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed Image " + srcAlt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            conditionalSuccessful = false;
        }
    }


    /*******************************************************************************
     * DESCRIPTION: Checks the Anchor href attribute.
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *******************************************************************************/
    private void CheckAnchorHref(TestStep ts, String fileStepIndex) {
        String actualValue="";
        //Not wired for checking text because text is already wired up through the default assert method
        String hrefTxt = "href";

        switch (ts.get_accessorType().toLowerCase()) {
            case xpathCheckValue:
                actualValue = this.driver.findElement(By.xpath(ts.get_accessor())).getAttribute(hrefTxt);
                break;
            case cssSelectorCheckValue:
                actualValue = this.driver.findElement(By.cssSelector(ts.get_accessor())).getAttribute(hrefTxt);
                break;
            case tagNameCheckValue:
                actualValue = this.driver.findElement(By.tagName(ts.get_accessor())).getAttribute(hrefTxt);
                break;
            case classNameCheckValue:
                actualValue = this.driver.findElement(By.className(ts.get_accessor())).getAttribute(hrefTxt);
                break;
            case idCheckValue:
                actualValue = this.driver.findElement(By.id(ts.get_accessor())).getAttribute(hrefTxt);
                break;
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
            testHelper.UpdateTestResults(AppConstants.indent8 + "Successful Anchor " + hrefTxt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            //conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
            conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || conditionalSuccessful;
        } else {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed Anchor " + hrefTxt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            conditionalSuccessful = false;
        }
    }


    /*************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the status of the Get or Post against the
     *      expected value.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ************************************************************ */
    private void CheckGetPostStatus(TestStep ts, String fileStepIndex) {
        int expectedStatus = ts.get_expectedValue() != null ? parseInt(ts.get_expectedValue()) : 200;   //GetArgumentNumericValue(ts, 0, 200);
        int actualStatus;
        String url = GetArgumentValue(ts, 0, null);
        String getPost = "Get";

        if (url != null) {
            if (ts.get_command().toLowerCase().contains(AppCommands.Post)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking Post status of " + url, false);
                actualStatus = httpResponseCodeViaPost(url);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Get)) {
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
                //testHelper.UpdateTestResults("Successful comparison results.  Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ") for step " + fileStepIndex + "\r\n", true);
                //testHelper.UpdateTestResults("Successful comparison results.  Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ") for step " + fileStepIndex, true);
                testHelper.UpdateTestResults("Successful " + getPost + " HTTP Response.  Expected: (" + expectedStatus + ") Actual: (" + actualStatus + ") for step " + fileStepIndex, true);
                conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
            } else if (expectedStatus != actualStatus) {
                //testHelper.UpdateTestResults("Failed comparison results.  Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ") for step " + fileStepIndex + "\r\n", true);
                //testHelper.UpdateTestResults("Failed comparison results.  Expected value: (" + expectedStatus + ") Actual value: (" + actualStatus + ") for step " + fileStepIndex, true);
                testHelper.UpdateTestResults("Failed " + getPost + " HTTP Response.  Expected: (" + expectedStatus + ") Actual: (" + actualStatus + ") for step " + fileStepIndex, true);
                conditionalSuccessful = false;
            }
        } else {
            testHelper.UpdateTestResults("Error: Required URL not provided as Argument 1 aborting for step " + fileStepIndex, true);
            conditionalSuccessful = false;
        }
    }

    /********************************************************************
     * DESCRIPTION: Returns the URL of the current page.
     * @return - Returns the current driver URL (URL of the current page).
     ********************************************************************/
    String GetCurrentPageUrl() {
        return this.driver.getCurrentUrl();
    }


    /*************************************************************
     * DESCRIPTION: Returns the status code of the url passed in for a
     *              GET request.
     * @param url - url to check
     ************************************************************ */
    int httpResponseCodeViaGet(String url) {
        return RestAssured.get(url).statusCode();
    }


    /*************************************************************
     * DESCRIPTION:
     *      Returns the status code of the url passed in for a
     *      POST request.
     * @param url - url to check
     ************************************************************ */
    int httpResponseCodeViaPost(String url) {
        return RestAssured.post(url).statusCode();
    }


    /*************************************************************
     * DESCRIPTION: Retrieves all anchor tags in a page and
     *              reports the status of all anchor tags that
     *              have an href attribute.
     * @param url - url to check
     ************************************************************ */
    void CheckBrokenLinks(TestStep ts, String url, String fileStepIndex) {
        if (!driver.getCurrentUrl().equals(url)) {
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
                } catch (Exception nse) {
                    text = text.isEmpty() ? "[Possible Image] " : text;
                }
            }

            if (href != null) {
                linkCount++;
                int brokenLinksStatusCode = httpResponseCodeViaGet(href);
                if (200 != brokenLinksStatusCode) {
                    testHelper.UpdateTestResults("Failed link test " + href + " gave a response code of " + brokenLinksStatusCode + " for step " + fileStepIndex, true);
                    conditionalSuccessful = false;
                } else {
                    testHelper.UpdateTestResults( "Successful link test text: " + text + " href: " + href + " xPath: " + GenerateXPath(link, "") + " gave a response code of " + brokenLinksStatusCode + " for step " + fileStepIndex, true);
                    conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
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
    void CheckADAImages(TestStep ts, String url, String checkType, String fileStepIndex) {
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
            altTag = link != null ? link.getAttribute("alt") : null;
            imgSrc = link != null ? link.getAttribute("src") : null;
            if (checkType.toLowerCase().trim().equals("alt")) {
                if (altTag != null && !altTag.trim().isEmpty()) {
                    altTagCount++;
                    testHelper.UpdateTestResults("Successful image alt tag found: " + altTag + " for img src: " + imgSrc + " for step " + fileStepIndex, true);
                    //conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                    conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || conditionalSuccessful;
                } else {
                    testHelper.UpdateTestResults("Failed image alt tag missing for img src: " + imgSrc + " for step " + fileStepIndex, true);
                    conditionalSuccessful = false;
                }
            } else if (checkType.toLowerCase().trim().equals("src")) {
                if (imgSrc != null && !imgSrc.trim().isEmpty()) {
                    altTagCount++;
                    try {
                        brokenImageSrcStatusCode = httpResponseCodeViaGet(imgSrc);
                    } catch (Exception ex) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed Error when attempting to validate image src " + imgSrc + " Error: " + ex.getMessage() + " for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
                        conditionalSuccessful = false;
                    }
                    if (200 != brokenImageSrcStatusCode) {
                        testHelper.UpdateTestResults("Failed image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + " for step " + fileStepIndex, true);
                        conditionalSuccessful = false;
                    } else {
                        testHelper.UpdateTestResults("Successful image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + " for step " + fileStepIndex, true);
                        //conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                        conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || conditionalSuccessful;
                    }
                } else {
                    if (altTag != null) {
                        testHelper.UpdateTestResults("Failed image src tag missing for image with alt tag: " + altTag + " for step " + fileStepIndex, true);
                        conditionalSuccessful = false;
                    } else {
                        testHelper.UpdateTestResults("Failed image src tag missing for step " + fileStepIndex, true);
                        conditionalSuccessful = false;
                    }
                }
            }
        }
        testHelper.UpdateTestResults(AppConstants.indent5 + "Discovered " + altTagCount + " image " + checkType.toLowerCase().trim()  + " attributes  amongst " + images.size() + " image tags.\r\n", true);
    }

    /*********************************************************************************************
     * DESCRIPTION:
     *      Performs a count of all checkElement tags for the url
     *      passed in or the current url, if not passed in, and
     *      compares that count to the expectedCount passed in.
     *      If this test is marked as crucial, all testing stops
     *      if the counts do not match.
     *      If this test is not marked as crucial, testing
     *      continues and the status is reported.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param url - URL if checking page other than current page
     * @param checkElement - Element Type to count
     * @param expectedCount - The number of elements expected
     * @param fileStepIndex - the file index and the step index.
     * @param isCrucial - Indicates if this is a crucial or non-crucial assertion
     ***********************************************************************************************/
    private void CheckElementCount(TestStep ts, String url, String checkElement, int expectedCount, String fileStepIndex, boolean isCrucial) {
        int actualCount;
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        String comparisonType = CheckComparisonOperator(GetArgumentValue(ts, ts.ArgumentList.size()-1, "="));

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

            if (comparisonType.equals("=")) {
                if (actualCount != expectedCount) {
                    testHelper.UpdateTestResults("Failed to match count of '" + checkElement + "' tags.  Expected: " + expectedCount + "  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    conditionalSuccessful = false;
                } else {
                    testHelper.UpdateTestResults("Successful matching count of '" + checkElement + "' tags.  Expected: " + expectedCount + "  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    //conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                    conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || conditionalSuccessful;
                }
            } else {
                if (actualCount != expectedCount) {
                    testHelper.UpdateTestResults("Successful not equal count of '" + checkElement + "' tags.  Expected: " + expectedCount + " !=  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    conditionalSuccessful = false;
                } else {
                    testHelper.UpdateTestResults("Failed not equal count of '" + checkElement + "' tags.  Expected: " + expectedCount + " !=  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || conditionalSuccessful;
                }
            }
        }
    }


    /*************************************************************
     * DESCRIPTION: Retrieves element text or element value,
     *              if text is null, using its xPath accessor
     *              and returns it to the calling method.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - Actual Element Text
     ************************************************************ */
    String CheckElementWithXPath(TestStep ts, String fileStepIndex) {
        String actualValue;
        String accessor = ts.get_accessor();
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? GetArgumentValue(ts, 1, null) : null;

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
                if (command != null && command.toLowerCase().equals(persistStringCheckValue)) {
                    PersistProvidedValueController(ts, actualValue, fileStepIndex);
                }

                //region {Wait for element code - not being used but an idea that could be implemented}
                //testHelper.DebugDisplay("actualValue = " + actualValue);
                //wait until element is present commented out and functionality pushed to separate  stand-alone action
                //actualValue = (new WebDriverWait(driver,10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor))).getText();
                //endregion
            }

            if (!ts.get_command().toLowerCase().contains(persistStringCheckValue) && (command == null || !command.toLowerCase().contains(persistStringCheckValue))) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by XPath: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "xPath_Element_Not_Found", configurationFolder, true, fileStepIndex);
            } else {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "xPath_Element_Not_Found", screenShotSaveFolder, true, fileStepIndex);
            }
            actualValue = null;
        }
        return actualValue;
    }


    /***************************************************************************
     * DESCRIPTION: Retrieves element text or element value,
     *              if text is null, using its CssSelector accessor
     *              and returns it to the calling method.
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - Actual Element text
     **************************************************************************/
    String CheckElementWithCssSelector(TestStep ts, String fileStepIndex) {
        String accessor = ts.get_accessor();
        String actualValue;
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? GetArgumentValue(ts, 1, null) : null;

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
                if (command != null && command.toLowerCase().equals(persistStringCheckValue)) {
                    PersistProvidedValueController(ts, actualValue, fileStepIndex);
                }
            }

            if (!ts.get_command().toLowerCase().contains(persistStringCheckValue) && (command == null || !command.toLowerCase().contains(persistStringCheckValue))) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by CssSelector: " + accessor + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by CssSelector: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "CssSelector_Element_Not_Found", configurationFolder, true, fileStepIndex);
            } else {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "CssSelector_Element_Not_Found", screenShotSaveFolder, true, fileStepIndex);
            }
            actualValue = null;
        }
        return actualValue;
    }


    /*************************************************************
     * DESCRIPTION: Retrieves element text or element value,
     *              if text is null, using its TagName accessor
     *              and returns it to the calling method.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - Actual element text
     ************************************************************ */
    String CheckElementWithTagName(TestStep ts, String fileStepIndex) {
        String accessor = ts.get_accessor();
        String actualValue;
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? GetArgumentValue(ts, 1, null) : null;

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
                if (command != null && command.toLowerCase().equals(persistStringCheckValue)) {
                    PersistProvidedValueController(ts, actualValue, fileStepIndex);
                }
            }

            if (!ts.get_command().toLowerCase().contains(persistStringCheckValue) && (command == null || !command.toLowerCase().contains(persistStringCheckValue))) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by TagName: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by TagName: " + ElementTypeLookup(accessor) + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "TagName_Element_Not_Found", configurationFolder, true, fileStepIndex);
            } else {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "TagName_Element_Not_Found", screenShotSaveFolder, true, fileStepIndex);
            }
            actualValue = null;
        }
        return actualValue;
    }


    /*************************************************************
     * DESCRIPTION: Retrieves element text or element value,
     *              if text is null, using its ClassName accessor
     *              and returns it to the calling method.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - Actual element text.
     *************************************************************/
     String CheckElementWithClassName(TestStep ts, String fileStepIndex) {
        String accessor = ts.get_accessor();
        String actualValue;
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? GetArgumentValue(ts, 1, null) : null;

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
                if (command != null && command.toLowerCase().equals(persistStringCheckValue)) {
                    PersistProvidedValueController(ts, actualValue, fileStepIndex);
                }
            }

            if (!ts.get_command().toLowerCase().contains(persistStringCheckValue) && (command == null || !command.toLowerCase().contains(persistStringCheckValue))) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by ClassName: " + accessor + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            }  else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by ClassName: " + accessor + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "ClassName_Element_Not_Found", configurationFolder, true, fileStepIndex);
            } else {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "ClassName_Element_Not_Found", screenShotSaveFolder, true, fileStepIndex);
            }
            actualValue = null;
        }
        return actualValue;
    }


    /*************************************************************
     * DESCRIPTION: Retrieves element text or element value,
     *              if text is null, using its Id accessor
     *              and returns it to the calling method.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - Actual element text
     ************************************************************ */
    String CheckElementWithId(TestStep ts, String fileStepIndex) {
        String accessor = ts.get_accessor();
        String actualValue;
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? GetArgumentValue(ts, 1, null) : null;

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
                if (command != null && command.toLowerCase().equals(persistStringCheckValue)) {
                    PersistProvidedValueController(ts, actualValue, fileStepIndex);
                }
            }

            if (!ts.get_command().toLowerCase().contains(persistStringCheckValue) && (command == null || !command.toLowerCase().contains(persistStringCheckValue))) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by ID: " + accessor + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by ID: " + accessor + " for script " + fileStepIndex + " Actual Value: " + actualValue, true);
            }
        } catch (Exception e) {
            if (screenShotSaveFolder == null || screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "Id_Element_Not_Found", configurationFolder, true, fileStepIndex);
            } else {
                testHelper.CaptureScreenShot(driver, GetBrowserUsed() + "_" + fileStepIndex + "Id_Element_Not_Found", screenShotSaveFolder, true, fileStepIndex);
            }
            actualValue = null;
        }
        return actualValue;
    }


    /***********************************************************************
     * DESCRIPTION: If a Tag is passed in as an argument, search the text
     *              of all tags of that type for the phrase, but if no tag
     *              is passed in, search the text of all page elements for
     *              the phrase.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ********************************************************************** */
    private void FindPhrase(TestStep ts, String fileStepIndex) {
        String cssSelector = GetArgumentValue(ts, 0, "*");
        String searchType = GetArgumentValue(ts, 1, "equals");
        boolean wasFound = false;

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
                    foundElements.add(GenerateXPath(element, ""));
                }
            }
        } else {
            for (WebElement element : elements) {
                try {
                    if (element.getText().equals(ts.get_expectedValue().trim())) {
                        wasFound = true;
                        foundElements.add(GenerateXPath(element, ""));
                    }
                } catch (Exception e) {
                    //do nothing likely stale element exception
                }
            }
        }

        if (!wasFound) {
            String message = "Failed to find (" + ts.get_expectedValue().trim() + ") searching all elements for step " + fileStepIndex;
            if (!cssSelector.trim().isEmpty()) {
                message = "Failed to find (" + ts.get_expectedValue().trim() + ") searching all " + cssSelector + " elements for step " + fileStepIndex;
                conditionalSuccessful = false;
            }
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + message + AppConstants.ANSI_RESET, true);
        } else {
            conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || conditionalSuccessful;
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
                            //testHelper.UpdateTestResults("Error y = " + y + " and x = " + x + " - " + io.getMessage(), false);
                        }
                    }
                }
            }
            for (String foundElement : foundElements) {
                testHelper.UpdateTestResults("Successful found (" + ts.get_expectedValue().trim() + ") in element: " + foundElement + " for step " + fileStepIndex, true);
            }
        }
    }



    //region { Perform Action Methods}
    /*************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the status of the Get or Post against the
     *      expected value.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ************************************************************ */
    private void PerformExplicitNavigation(TestStep ts, String fileStepIndex) throws Exception {
        String navigateUrl = GetArgumentValue(ts, 0, null);
        String delayTime = GetArgumentValue(ts, 1, null);
        String windowDimensions = GetArgumentValue(ts, 2, null);
        String indentMargin = ts.get_command().toLowerCase().equals(AppCommands.Navigate) ? AppConstants.indent5 : AppConstants.indent8;
        String subIndent = indentMargin.equals(AppConstants.indent5) ? AppConstants.indent8 : AppConstants.indent5 + AppConstants.indent8;
        //TODO: ADDING TIMING CHECK FOR FRONT END AND BACK END PAGE LOAD TIMINGS
        String pageTimings = GetArgumentValue(ts, 3, null);
        double frontEndTiming = 0;
        double backEndTiming = 0;

        if (!testHelper.CheckIsUrl(navigateUrl) || (delayTime != null && TestHelper.tryParse(delayTime) == null) || (windowDimensions != null && !windowDimensions.toLowerCase().contains("w=")) ||
                (pageTimings != null && !pageTimings.contains("be"))) {
            SortNavigationArguments(ts, navigateUrl, delayTime, windowDimensions, pageTimings, "navigate");
            navigateUrl =  GetArgumentValue(ts, 0, null);
            delayTime = GetArgumentValue(ts, 1, null);
            windowDimensions = GetArgumentValue(ts, 2, null);
            pageTimings = GetArgumentValue(ts, 3, null);
        }

        String expectedUrl = null;
        int delayMilliSeconds = 0;

        if (navigateUrl != null && !navigateUrl.isEmpty()) {
            testHelper.UpdateTestResults( indentMargin + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Explicit Navigation Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
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
                        height = parseInt(dimensions.substring(dimensions.indexOf("h=") + 2).trim());
                    } else {
                        height= parseInt(dimensions.substring(dimensions.indexOf("h=") + 2, dimensions.indexOf("w=")).trim());
                        width = parseInt(dimensions.substring(dimensions.indexOf("w=") + 2).trim());
                    }
                    testHelper.UpdateTestResults(subIndent + "Setting browser dimensions to (Width=" + width + " Height=" + height + ")", true);
                    testHelper.SetWindowContentDimensions(driver, width, height);
                }
            }
            if (pageTimings != null && !pageTimings.isEmpty()) {
                //first find out if BE or FE is first
                if (pageTimings.toLowerCase().contains("be=") && pageTimings.toLowerCase().contains("fe=")) {
                    if (pageTimings.toLowerCase().indexOf("be=") < pageTimings.toLowerCase().indexOf("fe=")) {
                        frontEndTiming = parseDouble(pageTimings.substring(pageTimings.toLowerCase().indexOf("fe=") + 3));
                        backEndTiming =  parseDouble(pageTimings.substring(pageTimings.toLowerCase().indexOf("be=") + 3, pageTimings.toLowerCase().indexOf("fe=") - 1));
                    } else {
                        backEndTiming = parseDouble(pageTimings.substring(pageTimings.toLowerCase().indexOf("be=") + 3));
                        frontEndTiming =  parseDouble(pageTimings.substring(pageTimings.toLowerCase().indexOf("fe=") + 3, pageTimings.toLowerCase().indexOf("be=") - 1));
                    }
                }
            }
        }
        testPage = navigateUrl;
        //Explicit Navigation Event
        testHelper.UpdateTestResults(subIndent + "Navigating to " + navigateUrl + " for step " + fileStepIndex, true);

        String actualUrl = CheckPageUrl(delayMilliSeconds);
        String timingMessage = "";
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
                if (frontEndTiming > 0 && backEndTiming > 0) {
                    timingMessage = GetTimingMessage(frontEndTiming, backEndTiming, subIndent);
                    testHelper.UpdateTestResults(subIndent + "Successful Navigation and URL Check.  Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")\r\n" + timingMessage + " for step " + fileStepIndex, true);
                } else {
                    testHelper.UpdateTestResults(subIndent + "Successful Navigation and URL Check.  Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ") for step " + fileStepIndex, true);
                }
                //subIndent
            } else {
                if (frontEndTiming > 0 && backEndTiming > 0) {
                    timingMessage = GetTimingMessage(frontEndTiming, backEndTiming, subIndent);
                    testHelper.UpdateTestResults(subIndent + "Failed Navigation and URL Check.  Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ")\r\n" + timingMessage + " for step " + fileStepIndex, true);
                } else {
                    testHelper.UpdateTestResults(subIndent + "Failed Navigation and URL Check.  Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ") for step " + fileStepIndex, true);
                }
            }
        }
        testHelper.UpdateTestResults( indentMargin + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Explicit Navigation Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
    }

    /****************************************************************************
     * Description: Gets the BackEnd and FrontEnd Timing Message based on the
     *              BackEnd and FrontEnd testStep settings indicating the success
     *              or failure of these timings.
     * @param frontEndTiming - time limit set for the front end load complete
     * @param backEndTiming - time limit set for the back end load complete
     * @param subIndent - indentation to follow to align with the calling method
     * @return - Returns the Success/Failure of the Back and Front end load timing
     ****************************************************************************/
    String GetTimingMessage(Double frontEndTiming, Double backEndTiming, String subIndent) {
        String timingMessage = null;

        if (frontEndTiming >= testHelper.get_frontEndPageLoadDuration() && backEndTiming >= testHelper.get_backEndPageLoadDuration()) {
            timingMessage = subIndent + AppConstants.ANSI_GREEN_BRIGHT + "Successful Backend Load Expected time (" + backEndTiming + " seconds) actual (" + testHelper.get_backEndPageLoadDuration() + " seconds)\r\n" +
                    subIndent + "Successful Frontend Load Expected time (" + frontEndTiming + " seconds) actual (" + testHelper.get_frontEndPageLoadDuration() + " seconds)" + AppConstants.ANSI_RESET;
        } else if (frontEndTiming < testHelper.get_frontEndPageLoadDuration() && backEndTiming < testHelper.get_backEndPageLoadDuration()) {
            timingMessage = subIndent + AppConstants.ANSI_RED + "Failed Backend Load Expected time (" + backEndTiming + " seconds) actual (" + testHelper.get_backEndPageLoadDuration() + " seconds)\r\n" +
                    subIndent + "Failed Frontend Load Expected time (" + frontEndTiming + " seconds) actual (" + testHelper.get_frontEndPageLoadDuration() + " seconds)" + AppConstants.ANSI_RESET;
        } else {
            if (backEndTiming < testHelper.get_backEndPageLoadDuration()) {
                timingMessage = subIndent + AppConstants.ANSI_RED + "Failed Backend Load Expected time (" + backEndTiming + " seconds) actual (" + testHelper.get_backEndPageLoadDuration() + " seconds)\r\n" + AppConstants.ANSI_RESET +
                        subIndent + AppConstants.ANSI_GREEN_BRIGHT + "Successful Frontend Load Expected time (" + frontEndTiming + " seconds) actual (" + testHelper.get_frontEndPageLoadDuration() + " seconds)" + AppConstants.ANSI_RESET;
            }
            if (frontEndTiming < testHelper.get_frontEndPageLoadDuration()) {
                timingMessage = subIndent + AppConstants.ANSI_GREEN_BRIGHT + "Successful Backend Load Expected time (" + backEndTiming + " seconds) actual (" + testHelper.get_backEndPageLoadDuration() + " seconds)\r\n" + AppConstants.ANSI_RESET +
                        subIndent + AppConstants.ANSI_RED + "Failed Frontend Load Expected time (" + frontEndTiming + " seconds) actual (" + testHelper.get_frontEndPageLoadDuration() + " seconds)" + AppConstants.ANSI_RESET;
            }
        }
        return timingMessage;
    }


    /*******************************************************************************
     * DESCRIPTION: Performs non-text retrieval actions such as clicking,
     *              navigating, waiting, taking screen shots etc...
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param subAction - used when a step contains multiple actions in one command
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************************** */
    Boolean PerformAction(TestStep ts, String subAction, String fileStepIndex) {
        boolean status;
        String command = ts.get_command().toLowerCase().contains(AppCommands.Switch_To_IFrame) ? subAction : ts.get_command();

        //if this is a click event, click it
        if ((command.toLowerCase().contains(AppCommands.Click)) && !command.contains(AppCommands.SendKeys) && !command.contains(AppCommands.Send_Keys)) {
            if (command.toLowerCase().contains(AppCommands.DoubleClick)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing double click on " + ts.get_accessor() + " using " + ts.get_accessorType() + " for step " + fileStepIndex, true);
            } else if (command.toLowerCase().contains(AppCommands.Right_Click)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing right click on " + ts.get_accessor() + " using " + ts.get_accessorType() + " for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing click on " + ts.get_accessor() + " using " + ts.get_accessorType() + " for step " + fileStepIndex, true);
            }
            try {
                switch (ts.get_accessorType().toLowerCase()) {
                    case xpathCheckValue:
                        if (!command.toLowerCase().contains(AppCommands.Right_Click)) {
                            if (!command.toLowerCase().contains(AppCommands.DoubleClick)) {
                                if (get_selectedBrowserType().equals(BrowserTypes.Internet_Explorer)) {
                                    JavascriptExecutor js = (JavascriptExecutor) driver;
                                    js.executeScript("arguments[0].click()", this.driver.findElement(By.xpath(ts.get_accessor())));
                                } else {
                                    this.driver.findElement(By.xpath(ts.get_accessor())).click();
                                }
                                testHelper.UpdateTestResults("Successful - Click performed for step " + fileStepIndex, false);
                            } else {
                                //doubleclick
                                Actions action = new Actions(driver);
                                action.doubleClick(driver.findElement(By.xpath(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Double Click performed for step " + fileStepIndex, false);
                            }
                        } else {  //right click element
                            Actions action = new Actions(driver);
                            if (command.toLowerCase().contains(AppCommands.Keys)) {
                                action.contextClick(driver.findElement(By.xpath(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Right Click performed for step " + fileStepIndex, false);
                            } else {
                                action.contextClick(driver.findElement(By.xpath(ts.get_accessor()))).build().perform();
                                SelectFromContextMenu(ts, fileStepIndex);
                                testHelper.UpdateTestResults("Successful - Right Click and Context menu sendkeys performed for step " + fileStepIndex, false);
                            }
                        }
                        break;
                    case idCheckValue:
                        if (!command.toLowerCase().contains(AppCommands.Right_Click)) {
                            if (!command.toLowerCase().contains(AppCommands.DoubleClick)) {
                                //click
                                if (get_selectedBrowserType().equals(BrowserTypes.Internet_Explorer)) {
                                    JavascriptExecutor js = (JavascriptExecutor) driver;
                                    js.executeScript("arguments[0].click()", this.driver.findElement(By.id(ts.get_accessor())));
                                } else {
                                    this.driver.findElement(By.id(ts.get_accessor())).click();
                                }
                                testHelper.UpdateTestResults("Successful - Click performed for step " + fileStepIndex, false);
                            } else {
                                //doubleclick
                                Actions action = new Actions(driver);
                                action.doubleClick(driver.findElement(By.id(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Double Click performed for step " + fileStepIndex, false);
                            }
                        } else {  //right click element
                            Actions action = new Actions(driver);
                            if (!command.toLowerCase().contains(AppCommands.Keys)) {
                                action.contextClick(this.driver.findElement(By.id(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Right Click performed for step " + fileStepIndex, false);
                            } else {
                                action.contextClick(driver.findElement(By.id(ts.get_accessor()))).build().perform();
                                SelectFromContextMenu(ts, fileStepIndex);
                                testHelper.UpdateTestResults("Successful - Right Click and Context menu sendkeys performed for step " + fileStepIndex, false);
                            }
                        }
                        break;
                    case classNameCheckValue:
                        if (!command.toLowerCase().contains(AppCommands.Right_Click)) {
                            if (!command.toLowerCase().contains(AppCommands.DoubleClick)) {
                                //click
                                if (get_selectedBrowserType().equals(BrowserTypes.Internet_Explorer)) {
                                    JavascriptExecutor js = (JavascriptExecutor) driver;
                                    js.executeScript("arguments[0].click()", this.driver.findElement(By.className(ts.get_accessor())));
                                } else {
                                    this.driver.findElement(By.className(ts.get_accessor())).click();
                                }
                                testHelper.UpdateTestResults("Successful - Click performed for step " + fileStepIndex, false);
                            } else {
                                //doubleclick
                                Actions action = new Actions(driver);
                                action.doubleClick(driver.findElement(By.className(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Double Click performed for step " + fileStepIndex, false);
                            }
                        } else {  //right click element
                            Actions action = new Actions(driver);
                            if (!command.toLowerCase().contains(AppCommands.Keys)) {
                                action.contextClick(this.driver.findElement(By.className(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Right Click performed for step " + fileStepIndex, false);
                            } else {
                                action.contextClick(driver.findElement(By.className(ts.get_accessor()))).build().perform();
                                SelectFromContextMenu(ts, fileStepIndex);
                                testHelper.UpdateTestResults("Successful - Right Click and Context menu sendkeys performed for step " + fileStepIndex, false);
                            }
                        }
                        break;
                    case cssSelectorCheckValue:
                        if (!command.toLowerCase().contains(AppCommands.Right_Click)) {
                            if (!command.toLowerCase().contains(AppCommands.DoubleClick)) {
                                //click
                                if (get_selectedBrowserType().equals(BrowserTypes.Internet_Explorer)) {
                                    JavascriptExecutor js = (JavascriptExecutor) driver;
                                    js.executeScript("arguments[0].click()", this.driver.findElement(By.cssSelector(ts.get_accessor())));
                                } else {
                                    this.driver.findElement(By.cssSelector(ts.get_accessor())).click();
                                }
                                testHelper.UpdateTestResults("Successful - Click performed for step " + fileStepIndex, false);
                            } else {
                                //doubleclick
                                Actions action = new Actions(driver);
                                action.doubleClick(driver.findElement(By.cssSelector(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Double Click performed for step " + fileStepIndex, false);
                            }
                        } else {  //right click element
                            Actions action = new Actions(driver);
                            if (!command.toLowerCase().contains(AppCommands.Keys)) {
                                action.contextClick(this.driver.findElement(By.cssSelector(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Right Click performed for step " + fileStepIndex, false);
                            } else {
                                action.contextClick(driver.findElement(By.cssSelector(ts.get_accessor()))).build().perform();
                                SelectFromContextMenu(ts, fileStepIndex);
                                testHelper.UpdateTestResults("Successful - Right Click and Context menu sendkeys performed for step " + fileStepIndex, false);
                            }
                        }
                        break;
                    case tagNameCheckValue:
                        if (!command.toLowerCase().contains(AppCommands.Right_Click)) {
                            if (!command.toLowerCase().contains(AppCommands.DoubleClick)) {
                                //click
                                if (get_selectedBrowserType().equals(BrowserTypes.Internet_Explorer)) {
                                    JavascriptExecutor js = (JavascriptExecutor) driver;
                                    js.executeScript("arguments[0].click()",  this.driver.findElement(By.tagName(ts.get_accessor())));
                                } else {
                                    this.driver.findElement(By.tagName(ts.get_accessor())).click();
                                }
                                testHelper.UpdateTestResults("Successful - Click performed for step " + fileStepIndex, false);
                            } else {
                                //doubleclick
                                Actions action = new Actions(driver);
                                action.doubleClick(driver.findElement(By.tagName(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Double Click performed for step " + fileStepIndex, false);
                            }
                        } else {  //right click element
                            Actions action = new Actions(driver);
                            if (!command.toLowerCase().contains(AppCommands.Keys)) {
                                action.contextClick(this.driver.findElement(By.tagName(ts.get_accessor()))).build().perform();
                                testHelper.UpdateTestResults("Successful - Right Click performed for step " + fileStepIndex, false);
                            } else {
                                action.contextClick(driver.findElement(By.tagName(ts.get_accessor()))).build().perform();
                                SelectFromContextMenu(ts, fileStepIndex);
                                testHelper.UpdateTestResults("Successful - Right Click and Context menu sendkeys performed for step " + fileStepIndex, false);
                            }
                        }
                        break;
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        } else if (command.toLowerCase().contains("screenshot")) {
            try {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Taking Screenshot for step " + fileStepIndex, true);
                subAction = GetArgumentValue(ts, 0, subAction);
                PerformScreenShotCapture(subAction, fileStepIndex);
                status = true;
            } catch (Exception e) {
                status = false;
            }
        } else {  //if it is not a click, send keys or screenshot
            try {
                //use sendkeys as the command when sending keywords to a form
                //region { local constants }
                //made the timestamp/unique id replacement string a constant
                String uidReplacementChars = "**_uid_**";
                if (command.contains(AppCommands.SendKeys)) {
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
                    testHelper.UpdateTestResults(AppConstants.indent5 + "Performing SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                }
                if (subAction.contains(AppCommands.Keys) || subAction.toLowerCase().contains(AppCommands.Keys)) {
                    testHelper.UpdateTestResults(AppConstants.indent8 + "Performing special SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                    switch (ts.get_accessorType().toLowerCase()) {
                        case xpathCheckValue:
                            this.driver.findElement(By.xpath(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                            break;
                        case idCheckValue:
                            this.driver.findElement(By.id(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                            break;
                        case classNameCheckValue:
                            this.driver.findElement(By.className(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                            break;
                        case cssSelectorCheckValue:
                            this.driver.findElement(By.cssSelector(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                            break;
                        case tagNameCheckValue:
                            this.driver.findElement(By.tagName(ts.get_accessor())).sendKeys(GetKeyValue(subAction, fileStepIndex));
                            break;
                    }
                } else {
                    if (subAction.contains(uidReplacementChars)) {
                        testHelper.UpdateTestResults(AppConstants.indent5 + "Replacing Unique Identifier placeholder for step " + fileStepIndex, true);
                    }
                    subAction = subAction.replace(uidReplacementChars, uniqueId);
                    testHelper.UpdateTestResults(AppConstants.indent8 + "Performing default SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                    switch (ts.get_accessorType().toLowerCase()) {
                        case xpathCheckValue:
                            this.driver.findElement(By.xpath(ts.get_accessor())).sendKeys(subAction);
                            break;
                        case idCheckValue:
                            this.driver.findElement(By.id(ts.get_accessor())).sendKeys(subAction);
                            break;
                        case classNameCheckValue:
                            this.driver.findElement(By.className(ts.get_accessor())).sendKeys(subAction);
                            break;
                        case cssSelectorCheckValue:
                            this.driver.findElement(By.cssSelector(ts.get_accessor())).sendKeys(subAction);
                            break;
                        case tagNameCheckValue:
                            this.driver.findElement(By.tagName(ts.get_accessor())).sendKeys(subAction);
                            break;
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
     * @param url - URl of the page to navigate to, if blank use current page.
     * @param email - userid or email address used to login
     * @param password - password used to login
     * @param fileStepIndex - the file index and the step index.
     *                      (Do not remove this parameter will implement when method is fixed!!!)
     ******************************************************************** */
    public void Login(String url, String email, String password, String fileStepIndex) {
        testHelper.UpdateTestResults("Login method reached start before any code.", false);
        boolean urlIsNA = url.toLowerCase().trim().equals("n/a") ? true : false;

        //if url  is provided, navigate first
        if (url != null && !url.isEmpty() && !urlIsNA) {
            driver.get(url);
        }
        testHelper.UpdateTestResults("Login method reached for step " + fileStepIndex, false);

        try {
            //#1 attempt to access the alert window and send the username, tab to next input, then password
            testHelper.UpdateTestResults("Switched to Alert first login method", false);
            //driver.switchTo().alert();
            driver.switchTo().alert().sendKeys(email + Keys.TAB + password + Keys.RETURN);
            testHelper.UpdateTestResults("Switched to Alert first login method - after", false);
        } catch (Exception ex) {
            try {
                //#2 if the alert window is not present, reload the page
                if (!isAlertPresent()) {
                    driver.get(testPage);
                }
                //switch to the alert window, find the username field and send the username
                driver.switchTo().alert();
                testHelper.UpdateTestResults("Switched to Alert second login method", false);
                driver.findElement(By.id("username")).sendKeys(email);
                //find the password field and send the password
                driver.findElement(By.id("password")).sendKeys(password);
                testHelper.UpdateTestResults("Sent Credentials email: " + email + " Password: " + password, false);
                //press the ok button
                driver.switchTo().alert().accept();
                testHelper.UpdateTestResults("Switched to Alert second login method - after", false);
                //switch back to the main window
                driver.switchTo().defaultContent();
                testHelper.UpdateTestResults("Switched to default context", false);
                testHelper.UpdateTestResults("Completed login sequence without error.", true);
            } catch (Exception ex1) {
                //if the above two methods do not work, prepend the credentials to the URL and attempt to log in that way
                testHelper.UpdateTestResults("Exception " + ex.getMessage(), false);
                //if the url is not present, use the current url
                if (url == null || url.isEmpty() || !urlIsNA) {
                    url = testPage;
                }
                testHelper.UpdateTestResults("Switched to Alert third login method", false);
                //place the username and password into the URL
                String newUrl = url.replace("://", "://" + email + ":" + password + "@");
                //navgate to the credential encoded url to authenticate
                driver.get(newUrl);
                testHelper.UpdateTestResults("Switched to Alert third login method - after", false);
                //if the alert doesn't show up, you already have context and are logged in
            }
        }
    }


    /***********************************************************************************
     * Description: Sends key commands to the context window.
     *              The context window cannot be accessed directly but can be accessed
     *              after right clicking an element that exposes the context menu.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @throws AWTException - Abstract Window Toolkit exception has occurred - for capturing Robot errors
     * @throws InterruptedException - thrown when thread is sleeping or waiting and is then interrupted
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
     * @param fileStepIndex - the file index and the step index.
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
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *********************************************************************** */
    private void WaitForElement(TestStep ts, String fileStepIndex) {
        Boolean pageLoadComplete = false;
        String accessorType = ts.get_accessorType() != null ? ts.get_accessorType().toLowerCase().trim() : null;
        String accessor = ts.get_accessor()!= null ? ts.get_accessor().trim() : null;
        CheckWaitArgumentOrder(ts);
        String elementIdentifier = ts.get_command().toLowerCase().trim().contains(AppCommands.Page) ? GetArgumentValue(ts, 0, "n/a") : GetArgumentValue(ts, 0, null);
        int maxTimeInSeconds = GetArgumentNumericValue(ts, 1, AppConstants.DefaultElementWaitTimeInSeconds);

        //check that this argument is present
        if ((elementIdentifier == null || elementIdentifier.isEmpty()) && (accessorType == null || accessorType.isEmpty())) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + AppConstants.indent5 + "Improperly formatted test step.  Skipping step " + fileStepIndex, true);
            return;
        }

        if (ts.get_command().toLowerCase().trim().contains(AppCommands.Page)) {
            accessorType = "page";
            testHelper.UpdateTestResults(AppConstants.indent5 + "Waiting a maximum of " + maxTimeInSeconds + " seconds for page load to complete at step " + fileStepIndex, true);
        } else {
            testHelper.UpdateTestResults(AppConstants.indent5 + "Waiting a maximum of " + maxTimeInSeconds + " seconds for presence of element " + accessor + " at step " + fileStepIndex, true);
        }

        if (accessorType == null || elementIdentifier == null || accessor == null ) {
            return;
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
                            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to navigate error: " + ex.getMessage() + " for step " + fileStepIndex, true);
                        }
                    }
                    pageLoadComplete = new WebDriverWait(driver, maxTimeInSeconds).until(
                            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                    break;
                default:  //default to xpath if missing
                    element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath(accessor)));
                    break;
            }
            if (!ts.get_command().toLowerCase().trim().contains(AppCommands.Page)) {
                if (element != null) {
                    testHelper.UpdateTestResults("Successful load of element " + accessor + " within max time setting of " + maxTimeInSeconds + " for step " + fileStepIndex, true);
                }
            } else {
                if (pageLoadComplete) {
                    testHelper.UpdateTestResults("Successful load of page " + GetCurrentPageUrl() + " within max time setting of " + maxTimeInSeconds + " for step " + fileStepIndex, true);
                }
            }
        } catch (TimeoutException ae) {
            if (ts.get_command().toLowerCase().trim().contains(AppCommands.Page)) {
                //TODO: INVESTIGATE WHY YOU ADDED AL+ IN THE INITIAL MESSAGE
                //testHelper.UpdateTestResults("Failed to find the element " + GetCurrentPageUrl() + " within the set max time of " + maxTimeInSeconds + " for step " + fileStepIndex + " AL+", true);
                testHelper.UpdateTestResults("Failed to find the element " + GetCurrentPageUrl() + " within the set max time of " + maxTimeInSeconds + " for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults("Failed to load element " + accessor + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndex, true);
            }
            if (ts.get_crucial()){
                throw (ae);
            }
        }
    }


    /***********************************************************************
     * DESCRIPTION: Switches to a different tab either the child or
     *      the parent tab.  This method is only used with the Right click
     *      context menu if specified as an argument.
     * @param isChild - Is Child tab
     * @param fileStepIndex - the file index and the step index.
     ********************************************************************* */
    private void SwitchToTab(boolean isChild, String fileStepIndex) {
        int tab;
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        tab = isChild ? 1 : 0;
        //String handleName = tabs.get(1);
        String handleName = tabs.get(tab);
        driver.switchTo().window(handleName);
        System.setProperty("current.window.handle", handleName);
        testHelper.UpdateTestResults(AppConstants.indent5 + "Switched to New tab with url = " + driver.getCurrentUrl() + " for step " + fileStepIndex, true);
    }


    /*************************************************************************************
     * Description: This method allows for switching between any of the open tabs.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *************************************************************************************/
    private void SwitchToTab(TestStep ts, String fileStepIndex) {
        int tab = GetArgumentNumericValue(ts, 0, 0);
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        int tabsSize = tabs.size();
        if (tabsSize > tab) {
            String handleName = tabs.get(tab);
            driver.switchTo().window(handleName);
            System.setProperty("current.window.handle", handleName);
            testHelper.UpdateTestResults(AppConstants.indent5 + "Switched to New tab with url = " + driver.getCurrentUrl() + " for step " + fileStepIndex, true);
        } else {
            testHelper.UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_RED_BRIGHT + "Unable to switch to tab!!!  Either the Tab does not exist or the mouse changed the context while attempting to access the context menu for step " + fileStepIndex + ".\r\n" +
                    "Both the keyboard and mouse must not be used when attempting to access context menus or when switching tabs.\r\n" +
                    "It is also possible the a notification event switched the context such as email receipt or an OS notification.\r\n" +
                    "If this continues to be a problem, open just 1 tab per test and use the switch to tab argument in the right click command that accesses the context menu." + AppConstants.ANSI_RESET, true);
        }
    }

    /*********************************************************************************
     * Description: This method closes the open tab specified by the argument.
     *              If the main tab or a non-existent tab number is passed in
     *              it calls an error message display method to display the issue
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ********************************************************************************/
    private void CloseOpenChildTab(TestStep ts, String fileStepIndex) {
        int tab = GetArgumentNumericValue(ts, 0, 0);
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        try {
            if (tab > 0 && tab < tabs.size()) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Closing Child tab for step " + fileStepIndex, true);
                String handleName = tabs.get(tab);
                driver.switchTo().window(handleName);
                driver.close();
                testHelper.UpdateTestResults("Successful Closing of Child tab (" + tab + ") for step " + fileStepIndex, true);
            } else {
                //display error message
                //ArgumentOrderErrorMessage(ts, ts.get_command());
                CloseAllOpenChildTabs(fileStepIndex);
            }
        }catch (Exception ex) {
            testHelper.UpdateTestResults("Failed Closing of Child tab (" + tab + ")\r\n" + ex.getMessage() + " for step " + fileStepIndex, true);
        }
    }

    /*********************************************************************************
     * Description: This method closes all open tabs besides the main tab.
     * @param fileStepIndex - the file index and the step index.
     ********************************************************************************/
    private void CloseAllOpenChildTabs(String fileStepIndex) {
        testHelper.UpdateTestResults(AppConstants.indent5 + "Closing All Child tabs for step " + fileStepIndex, true);
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        String originalHandle = tabs.get(0);

        try {
            if (tabs.size() > 1) {
                for (int index = tabs.size() - 1; index > 0; index--) {
                    String handleName = tabs.get(index);
                    driver.switchTo().window(handleName);
                    driver.close();
                }
                testHelper.UpdateTestResults("Successful Closing of All Child tabs for step " + fileStepIndex, true);
            }
            //region {original method but what if this was issued when on a child tab }
//        String originalHandle = driver.getWindowHandle();
            //Do something to open new tabs

//        for(String handle : driver.getWindowHandles()) {
//            if (!handle.equals(originalHandle)) {
//                driver.switchTo().window(handle);
//                driver.close();
//            }
//        }
            //endregion
            driver.switchTo().window(originalHandle);
        } catch (Exception ex) {
            testHelper.UpdateTestResults("Failed Closing of All Child tabs\r\n" + ex.getMessage() + " for step " + fileStepIndex, true);
        }
    }

    /*************************************************************
     * DESCRIPTION: Performs a screen shot capture by calling the
     *      screen shot capture method in the pageHelper class.
     * @param value - file name or portion of file name
     * @param fileStepIndex - the file index and the step index.
     ************************************************************ */
    private void PerformScreenShotCapture(String value, String fileStepIndex) {
        String delimiter = System.getProperty("file.separator");
        if (!value.contains(delimiter)) {
            testHelper.CaptureScreenShot(driver, value, screenShotSaveFolder, false, fileStepIndex);
        } else {
            String folder = value.substring(0, value.lastIndexOf(delimiter));
            String fileName = value.substring(value.lastIndexOf(delimiter));
            testHelper.CaptureScreenShot(driver, fileName, folder, false, fileStepIndex);
        }
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
    String CheckPageUrl(int delayMilliSeconds) throws Exception {
        testHelper.NavigateToPage(this.driver, testPage, delayMilliSeconds);

        if (!isAlertPresent()) {
            return this.driver.getCurrentUrl();
        } else {
            return null;
        }
    }


    /*********************************************************************
     * DESCRIPTION: This method Checks the URL without performing a
     *              navigation action.
     *              Compares what was passed in against the current URL.
     *              Allows for separation of functionality and concerns
     *              in test steps.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************** */
    private void CheckUrlWithoutNavigation(TestStep ts, String fileStepIndex) throws InterruptedException {
        String expectedUrl = ts.get_expectedValue();

        if (ts.ArgumentList != null && ts.ArgumentList.size() > 0) {
            int delayMilliSeconds = GetArgumentNumericValue(ts, 0, AppConstants.DefaultTimeDelay);
            DelayCheck(delayMilliSeconds, fileStepIndex);
        }
        String actualUrl = GetCurrentPageUrl();
        assertEquals(expectedUrl, actualUrl);
        if (expectedUrl.trim().equals(actualUrl.trim())) {
            testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful URL Check.  Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ") for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
        } else {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed URL Check.   Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ") for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
        }
    }


    /******************************************************************************************
     * DESCRIPTION: This method checks brightness and degree of difference in color between
     *              the font color and the background color.
     *              If the background color cannot be found on the container element acting
     *              as the background, this method climbs the ancestral hierarchy until it
     *              finds the color used for the background.
     *
     * This page was used as a reference: https://www.w3.org/TR/AERT/#color-contrast
     * The ADA Approved range for Color Brightness difference is 125.
     * Color Brightness Formula = (299*R + 587*G + 114*B) / 1000
     *
     * The ADA Approved range for Color Difference is 500.
     * Color Difference Formula = (maximum (Red value 1, Red value 2) - minimum (Red value 1, Red value 2)) +
     *                            (maximum (Green value 1, Green value 2) - minimum (Green value 1, Green value 2)) +
     *                            (maximum (Blue value 1, Blue value 2) - minimum (Blue value 1, Blue value 2))
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************************************/
    void CheckColorContrast(TestStep ts, String fileStepIndex) {
        String tagType = GetArgumentValue(ts, 0, null);
        String bContrast = GetArgumentValue(ts, 1, AppConstants.DefaultContrastBrightnessSetting );
        String dContrast = GetArgumentValue(ts, 2, AppConstants.DefaultContrastDifferenceSetting);
        bContrast = bContrast.contains("=") ? bContrast.substring(bContrast.indexOf("=") + 1).trim() : bContrast;
        dContrast = dContrast.contains("=") ? dContrast.substring(dContrast.indexOf("=") + 1).trim() : dContrast;
        int treeClimb;  // = 0;
        String color;
        String backColor;
//        String color_hex[];
//        String backColor_hex[];
        String cHex, bHex;
        int brightnessStandard = bContrast.equals("125") ? 125 : parseInt(bContrast);
        int contrastStandard = dContrast.equals("500") ? 500 : parseInt(dContrast);
        boolean anyFailure = false;

        List<WebElement> elements = driver.findElements(By.cssSelector(tagType));
        testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowLeft + testHelper.PrePostPad(AppConstants.ANSI_RESET + "[ Start Check Color Contrast ]" + AppConstants.ANSI_YELLOW, "═", 9, 80) + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        if (!bContrast.equals("125") || !dContrast.equals("500")) {
            AdaApprovedContrastValuesOverriddenMessage(brightnessStandard, contrastStandard);
        }

        for(WebElement element : elements) {
            treeClimb = 0;
            color = element.getCssValue("color").trim();
            backColor = element.getCssValue("background-color").trim();


            cHex = Color.fromString(color).asHex();
            bHex = Color.fromString(backColor).asHex();
            WebElement parent = null;

            while (cHex.equals(bHex)) {
                try {
                    //testHelper.UpdateTestResults("Font color and background-color match!!!!", false);
                    treeClimb++;
                    if (parent == null) {
                        parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].parentNode;", element);
                    } else {
                        parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].parentNode;", parent);
                    }
                    backColor = parent.getCssValue("background-color").trim();
                    bHex = Color.fromString(backColor).asHex();
                } catch (Exception ex) {
                    //in case you walk the entire tree and no difference is found
                    break;
                }
            }

            // reference: https://www.w3.org/TR/AERT/#color-contrast
            //color brightness The rage for color brightness difference is 125.
            //brightness = (299*R + 587*G + 114*B) / 1000
            String[] foreColors = color.substring(color.indexOf("(") + 1, color.indexOf(")")).split(",");
            String[] backColors = backColor.substring(backColor.indexOf("(") + 1, backColor.indexOf(")")).split(",");
            double foreColorBrightness = ((parseInt(foreColors[0].trim()) * 299) + (parseInt(foreColors[1].trim()) * 587) + (parseInt(foreColors[2].trim()) * 114)) / 1000;
            double backColorBrightness = ((parseInt(backColors[0].trim()) * 299) + (parseInt(backColors[1].trim()) * 587) + (parseInt(backColors[2].trim()) * 114)) / 1000;
            double brightness;
            double contrast;

            //color difference The range for color difference is 500.
            //(maximum (Red value 1, Red value 2) - minimum (Red value 1, Red value 2)) + (maximum (Green value 1, Green value 2) - minimum (Green value 1, Green value 2)) + (maximum (Blue value 1, Blue value 2) - minimum (Blue value 1, Blue value 2))
            int maxRed = parseInt(foreColors[0].trim()) > (parseInt(backColors[0].trim())) ? parseInt(foreColors[0].trim()) : (parseInt(backColors[0].trim()));
            int minRed = parseInt(foreColors[0].trim()) > (parseInt(backColors[0].trim())) ? (parseInt(backColors[0].trim())) : parseInt(foreColors[0].trim());
            int maxGreen = parseInt(foreColors[1].trim()) > (parseInt(backColors[1].trim())) ? parseInt(foreColors[1].trim()) : (parseInt(backColors[1].trim()));
            int minGreen = parseInt(foreColors[1].trim()) > (parseInt(backColors[1].trim())) ? (parseInt(backColors[1].trim())) : parseInt(foreColors[1].trim());
            int maxBlue = parseInt(foreColors[2].trim()) > (parseInt(backColors[2].trim())) ? parseInt(foreColors[2].trim()) : (parseInt(backColors[2].trim()));
            int minBlue = parseInt(foreColors[2].trim()) > (parseInt(backColors[2].trim())) ? (parseInt(backColors[2].trim())) : parseInt(foreColors[2].trim());

            contrast = (maxRed - minRed) + (maxGreen - minGreen) + (maxBlue - minBlue);

            if (foreColorBrightness > backColorBrightness) {
                brightness = foreColorBrightness - backColorBrightness;
            } else {
                brightness = backColorBrightness - foreColorBrightness;
            }

            String backColorAncestor = treeClimb > 0 ? "^" + treeClimb : "";

            testHelper.UpdateTestResults(AppConstants.indent8 + "Element being checked: " + GenerateXPath(element,"") + " for step " + fileStepIndex, false);
            if (brightness >= brightnessStandard && contrast >= contrastStandard) {
                testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Good brightness and Good contrast (Brightness Difference: " + brightness + " Color Difference: " + contrast + ")\r\n - forecolor(" + color + ") forecolor Brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + AppConstants.ANSI_RESET, false);
            } else if (brightness >= brightnessStandard && contrast < contrastStandard) {
                testHelper.UpdateTestResults( AppConstants.ANSI_GREEN + "Good brightness " + AppConstants.ANSI_RED + "Warning contrast (" + AppConstants.ANSI_GREEN + " Brightness Difference: " + brightness + AppConstants.ANSI_RED + " Color Difference: " + contrast + ")\r\n - forecolor(" + color + ") brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + AppConstants.ANSI_RESET , false);
                anyFailure = true;
            } else if (brightness < brightnessStandard && contrast >= contrastStandard) {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Warning brightness" + AppConstants.ANSI_GREEN +" and Good contrast (" + AppConstants.ANSI_RED + "Brightness Difference:: " + brightness + AppConstants.ANSI_GREEN + " Color Difference: " + contrast + ")\r\n" + AppConstants.ANSI_RED + " - forecolor(" + color + ") brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + AppConstants.ANSI_RESET, false);
                anyFailure = true;
            } else {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Warning brightness and Warning contrast (Contrast: " + brightness + " Color Difference: " + contrast + ")\r\n - forecolor(" + color + ") brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness +  AppConstants.ANSI_RESET, false);
                anyFailure = true;
            }
            testHelper.UpdateTestResults("", true);
        }
        conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? !anyFailure : conditionalSuccessful;
        testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowLeft + testHelper.PrePostPad(AppConstants.ANSI_RESET + "[ End Check Color Contrast ]" + AppConstants.ANSI_YELLOW, "═", 9, 80) + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
    }


    //region { Create Test Page Methods }
    /****************************************************************************
     * DESCRIPTION: This method Creates an XML Test file or a text file, depending
     *          upon the test configuration, with element properties and
     *          attributes to allow users to more quickly create test files
     *          without inspecting each page element to do so.
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     *
     * @return - Returns the file name of the created test page
     *************************************************************************** */
    //private String CreateTestPage(TestStep ts, String fileStepIndex) {
    private String CreateTestPage(TestStep ts) {
        String cssSelector = GetArgumentValue(ts, 0, "*");
        String newFileName =  GetArgumentValue(ts, 1, "/config/newTestFile.txt");
        String tagsToSkip = GetArgumentValue(ts, 2, null);
        String [] skipTags = tagsToSkip.split(",");
        boolean formatted = ts.get_command().toLowerCase().contains(AppCommands.Format) ? true : false;

        //region { add feature - additional argument that allows existing file to be overwritten or kept and a new filename created if file exists }
        //String updateFileNameMessage;

//        String fileName = testHelper.GetUnusedFileName(newFileName);
//        if (!newFileName.equals(fileName)) {
//            updateFileNameMessage = "A File with the original file name existed.\r\n" +
//                    AppConstants.indent8 + "File name updated from: " + newFileName + " to " + fileName;
//            testHelper.UpdateTestResults(AppConstants.indent8 + updateFileNameMessage, true);
//        }
        //endregion

        //delete this file if it exists
        try {
            testHelper.DeleteFile(newFileName);
        } catch(Exception ex) {
            //let the delete file method handle this exception
        }

        //elements to skip if all elements used (*) - don't put this within the cssSelector assignment in case it is not provided
        testHelper.UpdateTestResults("Skipping Configured Tags To Skip: " + tagsToSkip, false);

        try {
            //boolean wasFound = false;
            boolean canProceed;  // = true;
            List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
            //List<String> foundElements = new ArrayList<>();
            String elementType;
            String elementXPath;
            String elementText;
            String elementHref;
            String elementSrc;
            String outputDescription;
            String elementAltText;
            String inputType;
            boolean isVisible = true;
            String script;

            if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(true));
                testHelper.WriteToFile(newFileName, CreateNavigationXmlTestStep(testPage, "TRUE"));
                testHelper.WriteToFile(newFileName, CreateScreenShotTestStep("FALSE"));
            } else {
                testHelper.WriteToFile(newFileName, "URL being used: " + testPage);
            }

            for (WebElement element : elements) {
                try {
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
                    }

                    if (canProceed) {
                        elementXPath = GenerateXPath(element, "");
                        elementText = element.getText();

                        if (formatted) {
                            if (!elementType.equals("img")) {
                                if (isVisible) {
                                    if (elementText != null && !elementText.isEmpty()) {
                                        outputDescription = CreateReadActionXmlTestStep(elementXPath, elementText, "FALSE");
                                    }
                                } else {
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
                                outputDescription += outputDescription.contains(">assert<") ? "\r\n" : "";
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
                                    outputDescription = CreateClickWriteActionXmlTestStep(elementXPath, AppCommands.Click, "FALSE");
                                }
                            }
                        } else if (elementType.equals("select") && formatted) {
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
                } catch (Exception fex) {
                    if (fex.getMessage().equals("stale element reference: element is not attached to the page document")) {
                        continue;
                    }
                }
            }
            if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(false));
            }
            testHelper.WriteToFile(newFileName, "");
        } catch (Exception ex) {
            testHelper.UpdateTestResults("Error: " + ex.getMessage(), false);
            /*if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(false));
            }
            testHelper.WriteToFile(newFileName, "");*/
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
        String returnValue;

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
    String CreateNavigationXmlTestStep(String testPage, String isCrucial) {
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
        testHelper.UpdateTestResults(returnValue, true);

        return returnValue;
    }

    /***************************************************************************
     * Description: This method Creates the ScreenShot test step.
     * @param isCrucial - True or false string value indicating whether the
     *                  step is marked as crucial
     * @return - returns a ScreenShot XML Test Step
     **************************************************************************/
    private String CreateScreenShotTestStep(String isCrucial) {
        String returnValue = "";

        if (testPage != null && !testPage.isEmpty()) {
            returnValue = "\t<step>\n" +
                    "\t\t<command>screenshot</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t</step>";
        }

        testHelper.UpdateTestResults(returnValue, true);
        return returnValue;
    }



    /**************************************************************************
     * Description: This Creates a Test Step that Selects an Option from
     *              a Select list.
     * @param elementXPath - xPath for the element
     * @param selectedItem - value to select
     * @param isCrucial - Flag to set the crucial
     * @return - SendKeys XML Test Step
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
     * @return - Click XML Test Step
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
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
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
     * @return - SendKeys XML Test Step
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
     * @return - Href read action XML Test Step
     *************************************************************************************/
    private String CreateAHrefReadActionXmlTestStep(String elementXPath, String elementHref, String isCrucial) {
        String returnValue = "";
        if (elementXPath != null && !elementXPath.isEmpty() && elementHref != null && !elementHref.isEmpty()) {
            returnValue = "\t<step>\r\n" +
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
     * @return - Image Src and Alt XML Test Steps
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
     * @return - Assert Command XML Test Step
     **********************************************************************************/
    private String CreateReadActionXmlTestStep(String elementXPath, String elementText, String isCrucial) {

        if (elementText.contains("<")) {
            elementText = "<![CDATA[ " + elementText.trim() + " ]]>";
        }

        return "\t<step>\r\n" +
                "\t\t<command>assert</command>\r\n" +
                "\t\t<actionType>read</actionType>\r\n" +
                "\t\t<expectedValue>" + elementText + "</expectedValue>\r\n" +
                "\t\t<crucial>"+ isCrucial + "</crucial>\r\n" +
                "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                "\t\t<accessorType>xPath</accessorType>\r\n" +
                "\t</step>";
    }
    //endregion

    //endregion

    //region { SQL Server Methods }
    /**************************************************************************
     * Description: Opens a Sql Server connection and sets the global
     *              sqlConnection object so that it can be used
     *              throughout the rest of the test.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
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
            if (sqlConnectionString != null) {
                sqlConnection = DriverManager.getConnection(sqlConnectionString);
                testHelper.UpdateTestResults("Successful establishment of connection to SQL Server Database for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults("Failed to establish a connection to the SQL Server for step " + fileStepIndex, true);
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults("Failure", true);
            testHelper.UpdateTestResults("Failed to establish a connection to the SQL Server.\r\n Error Message: " + e.getMessage() + " for step " + fileStepIndex, true);
        }
    }

    /******************************************************************************************
     * Description: Runs a Sql Server query that returns one field, retrieves that fields value
     *              and compares it to the expected value.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @throws SQLException - SQL Server Exception
     ******************************************************************************************/
    private void RunSqlServerQuery(TestStep ts, String fileStepIndex) throws SQLException {
        String sqlTable = GetArgumentValue(ts, 0, null);
        String sqlField = GetArgumentValue(ts, 1, null);
        String whereClause = GetArgumentValue(ts, 2, null);
        String sqlStatement = sqlTable.toLowerCase().contains("select") ? sqlTable : null;
        String actual = null;
        String comparisonType = CheckComparisonOperator(GetArgumentValue(ts, ts.ArgumentList.size()-1, "="));

        if (sqlTable.toLowerCase().startsWith("where ") || (sqlField != null &&  sqlField.toLowerCase().contains("where "))) {
            ArgumentOrderErrorMessage(ts, "sql server query");
            return;
        }

        if (sqlConnection == null) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to find active Sql Server connection to the SQL Server for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
            conditionalSuccessful = false;
        }

        Statement statement = sqlConnection.createStatement();
        ResultSet resultSet;

        try {
            if (sqlStatement == null || sqlStatement.isEmpty()) {
                sqlStatement = "Select " + sqlField + " from " + sqlTable + " " + whereClause;
            }

            testHelper.UpdateTestResults(AppConstants.indent5 + "Executing Sql Statement: " + sqlStatement, true);

            resultSet = statement.executeQuery(sqlStatement);
            if (resultSet != null) {
                resultSet.next();
                actual = resultSet.getString(1);
            }

            if (ts.get_crucial()) {
                if ("=".equals(comparisonType)) {
                    assertEquals(ts.get_expectedValue(), actual);
                } else {
                    assertNotEquals(ts.get_expectedValue(), actual);
                }
            } else {
                if ("=".equals(comparisonType)) {
                    if (ts.get_expectedValue() != null && actual != null && ts.get_expectedValue().trim().equals(actual.trim())) {
                        testHelper.UpdateTestResults("Successful Sql Query.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actual + ") for step " + fileStepIndex, true);
                        conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                    } else {
                        testHelper.UpdateTestResults("Failed Sql Server.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actual + ") for step " + fileStepIndex, true);
                        conditionalSuccessful = false;
                    }
                } else {
                    if (ts.get_expectedValue() != null && actual != null && !ts.get_expectedValue().trim().equals(actual.trim())) {
                        testHelper.UpdateTestResults("Successful Sql Server Query.  Expected: (" + ts.get_expectedValue() + ") != Actual: (" + actual + ") for step " + fileStepIndex, true);
                        conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                    } else {
                        testHelper.UpdateTestResults("Failed Sql Server Query.  Expected: (" + ts.get_expectedValue() + ") != Actual: (" + actual + ") for step " + fileStepIndex, true);
                        conditionalSuccessful = false;
                    }
                }
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults("Failed to execute query successfully.\r\n Error: " + e.getMessage() + " for step " + fileStepIndex, true);
            conditionalSuccessful = false;
        }
    }
    //endregion

    //region { JSON API EndPoint Methods }


    /****************************************************************************
     * DESCRIPTION: This method starts by checking if a URL parameter was
     *              supplied and if so, calls the navigation method to
     *              perform a Navigation event and make that the current page.
     *              Next, it retrieves the http response from the current
     *              page and returns it to the calling method where it will
     *              be persisted in a global variable for use throughout
     *              the application.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - the retrieved http response (JSON/XML) if successful else null
     * @throws Exception - Possible Exception attempting to retrieve JSON/XML
     *                      from API endpoint
     * *******************************************************************************/
    private String GetHttpResponse(TestStep ts, String fileStepIndex) throws Exception {
        String url = GetArgumentValue(ts, 0, GetCurrentPageUrl());
        StringBuffer response = new StringBuffer();


        if (url != null && !url.isEmpty()) {
            testHelper.setNavigationMessageIndent(AppConstants.indent8 + AppConstants.indent5);
            PerformExplicitNavigation(ts, fileStepIndex);
            testHelper.setNavigationMessageIndent(null);
            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int responseCode = con.getResponseCode();
            String color = (responseCode == 200) ? AppConstants.ANSI_GREEN_BRIGHT : AppConstants.ANSI_RED_BRIGHT;
            testHelper.UpdateTestResults(AppConstants.indent8 + color + "Response Code " + responseCode + " for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        return response.toString();
    }




    /************************************************************************************
     * Description: This method searches the retrieved JSON for the Key/value pair
     *              and displays the count of all matching keys and whether one or more
     *              key contained the specific value.  If a key/value matches the search
     *              criteria the test is marked as successful, else the test is marked as
     *              failed.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***********************************************************************************/
    private void QueryJSON(TestStep ts, String fileStepIndex) {
        if (!testHelper.IsNullOrEmpty(jsonContent)) {
            String jsonTemp = jsonContent;
            ArrayList<String> searchList = new ArrayList<>();
            int count  = 0;
            int endPos;
            String searchString = "\"" + ts.get_accessor() + "\":" + ts.get_expectedValue();
            String searchKey = "\"" + ts.get_accessor() + "\":";
            int keyPos = 0;

            testHelper.UpdateTestResults(AppConstants.indent8 + "Searching JSON for " + searchString + " for step " + fileStepIndex,true);

            if (jsonTemp == null || jsonTemp.isEmpty()) {
                testHelper.UpdateTestResults(AppConstants.indent8 + "JSON not previously retrieved prior to query attempt for step " + fileStepIndex + " skipping.",true);
                return;
            }

            String elementContent;
            while (jsonTemp.contains(searchKey)) {
                endPos = 0;
                keyPos = jsonTemp.indexOf(searchKey);
                endPos = keyPos + searchString.length();
                elementContent = "";

                if (jsonTemp.substring(keyPos, endPos).equals(searchString)) {
                    elementContent = jsonTemp.substring(keyPos, endPos).trim();
                    elementContent = elementContent.substring(elementContent.indexOf(":") + 1).trim();
                    searchList.add(elementContent);
                }

                jsonTemp = jsonTemp.substring(endPos + 1);
                count++;
            }

            if (searchList.size() > 0) {
                testHelper.UpdateTestResults("Successful JSON Search.  Searched all " + ts.get_accessor() + " for: (" +  ts.get_expectedValue() + ") Found: (" +  searchList.get(0) + ")\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + ts.get_accessor()  + " keys with " + searchList.size() + " values containing the expected value for step " + fileStepIndex, true);
                if (ts.get_crucial()) {
                    assertEquals( ts.get_expectedValue(), searchList.get(0));
                }
            } else {
                testHelper.UpdateTestResults("Failed JSON Search.  Searched for: (" +  searchString + ") but did not find this!\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + searchString + " keys but none contained the expected value for step " + fileStepIndex, true);
                //If this is crucial and the search string is not found, force an assertion failure
                if (ts.get_crucial()) {
                    assertEquals(ts.get_expectedValue(), null);
                }
            }
        } else {
            testHelper.UpdateTestResults("Failed JSON Search.  JSON content was not previously retrieved successfully\r\n." +
                    "Either the Get JSON retrieval step failed or no Get JSON step preceded this JSON Query attempt for step " + fileStepIndex, true);
        }
    }



    /*****************************************************************************
     * Description: Saves previously retrieved JSON to the file specified in the
     *              command argument.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *****************************************************************************/
    private void SaveJsonToFile(TestStep ts, String fileStepIndex) {
        String fileName = GetArgumentValue(ts, 0, null);
        String overWriteExisting = GetArgumentValue(ts, 1, "false");
        boolean overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        String originalFileName = fileName;
        String updateFileNameMessage = "";

        if (overWriteExisting.contains("\\") || overWriteExisting.contains("//")) {
            ArgumentOrderErrorMessage(ts, "save json");
            String [] items = {fileName, overWriteExisting};
            RearrangeArgumentOrder(ts, items, ts.get_command());
            fileName = GetArgumentValue(ts, 0, null);
            originalFileName = fileName;
            overWriteExisting = GetArgumentValue(ts, 1, "false");
            overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        }

        if (jsonContent != null && !jsonContent.isEmpty() && fileName != null) {
            if (!overwriteExistingFile) {
                fileName = testHelper.GetUnusedFileName(fileName);
                if (!originalFileName.equals(fileName)) {
                    updateFileNameMessage = "A File with the original file name existed.\r\n" +
                            AppConstants.indent8 + "File name updated from: " + originalFileName + " to " + fileName;
                    testHelper.UpdateTestResults(AppConstants.indent8 + updateFileNameMessage, true);
                }
            } else {
                testHelper.DeleteFile(fileName);
            }

            testHelper.UpdateTestResults(AppConstants.indent8 + "Saving JSON to file:" + fileName + " for step " + fileStepIndex, true);
            testHelper.WriteToFile(fileName, jsonContent);
            testHelper.UpdateTestResults("Successful JSON saved to file " + fileName + " for step " + fileStepIndex, true);
        } else {
            String errorMessage;
            if (jsonContent != null && !jsonContent.isEmpty()) {
                errorMessage = "Aborting!!!  No JSON content was previously retrieved.";
            } else {
                errorMessage = "Aborting!!!  No File Name was specified as the destination for the downloaded JSON content.";
            }
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failure JSON not saved to file because: " + errorMessage + " for step " + fileStepIndex, true);
        }
    }


    /************************************************************************************
     * Description: This method searches the retrieved XML for the Element/Node value
     *              and displays the count of all matching Elements/Nodes and whether
     *              one or more Elements/Nodes contained the specific value.
     *              If an Element/Node matches the search criteria the test is
     *              marked as successful, else the test is marked as failed.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***********************************************************************************/
    private void QueryXML(TestStep ts, String fileStepIndex) {
        if (!testHelper.IsNullOrEmpty(xmlContent)) {
            String xmlTemp = xmlContent;
            //testHelper.DebugDisplay("xmlTemp = " + xmlTemp);
            ArrayList<String> searchList = new ArrayList<>();
            int count  = 0;
            int startPos, endPos;
            String elementStart = "<" + ts.get_accessor() + ">";
            String elementEnd = !ts.get_accessor().contains(" ") ?  "</" + ts.get_accessor() + ">" : "</" + ts.get_accessor().substring(0, ts.get_accessor().indexOf(" ")).trim() + ">";
            String elementContent;
            //testHelper.DebugDisplay("elementEnd = " + elementEnd);
            while (xmlTemp.contains(elementStart)) {
                startPos = xmlTemp.indexOf(elementStart) + elementStart.length();
                endPos = xmlTemp.indexOf(elementEnd);
                elementContent = xmlTemp.substring(startPos, endPos).trim();
                //testHelper.DebugDisplay("elementContent(" + count + ") = " + elementContent);
                if (elementContent.equals(ts.get_expectedValue())) {
                    searchList.add(elementContent);
                }
                xmlTemp = xmlTemp.substring(endPos + elementEnd.length());
                count++;
            }

            testHelper.UpdateTestResults(AppConstants.indent8 + "Searching XML for " + elementStart + ts.get_expectedValue() + elementEnd + " for step " + fileStepIndex,true);
            if (searchList.size() > 0) {
                testHelper.UpdateTestResults("Successful XML Search.  Searched for: (" +  ts.get_expectedValue() + ") Found: (" +  searchList.get(0) + ")\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + elementStart + elementEnd + " elements with " + searchList.size() + " elements containing the expected value for step " + fileStepIndex, true);
                if (ts.get_crucial()) {
                    assertEquals( ts.get_expectedValue(), searchList.get(0));
                }
            } else {
                testHelper.UpdateTestResults("Failed XML Search.  Searched for: (" +  elementStart + ts.get_expectedValue() + elementEnd + ") but did not find this!\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + elementStart + elementEnd + " elements but none contained the expected value for step " + fileStepIndex, true);
                //If this is crucial and the search string is not found, force an assertion failure
                if (ts.get_crucial()) {
                    assertEquals(ts.get_expectedValue(), null);
                }
            }
        } else {
            testHelper.UpdateTestResults("Failed XML Search.  XML content was not previously retrieved successfully\r\n." +
                    "Either the Get XML retrieval step failed or no Get XML step preceded this XML Query attempt for step " + fileStepIndex, true);
        }
    }

    /*****************************************************************************
     * Description: Saves previously retrieved XML to the file specified in the
     *              command argument.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *****************************************************************************/
    private void SaveXmlToFile(TestStep ts, String fileStepIndex) {
        String fileName = GetArgumentValue(ts, 0, null);
        String overWriteExisting = GetArgumentValue(ts, 1, "false");
        boolean overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        String originalFileName = fileName;
        String updateFileNameMessage = "";

        if (overWriteExisting.contains("\\") || overWriteExisting.contains("//")) {
            ArgumentOrderErrorMessage(ts, "save xml");
            String [] items = {fileName, overWriteExisting};
            RearrangeArgumentOrder(ts, items, ts.get_command());
            fileName = GetArgumentValue(ts, 0, null);
            originalFileName = fileName;
            overWriteExisting = GetArgumentValue(ts, 1, "false");
            overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        }

        if (!testHelper.IsNullOrEmpty(xmlContent) && !testHelper.IsNullOrEmpty(fileName)) {
            if (!overwriteExistingFile) {
                fileName = testHelper.GetUnusedFileName(fileName);
                if (!originalFileName.equals(fileName)) {
                    updateFileNameMessage = "A File with the original file name existed.\r\n" +
                            AppConstants.indent8 + "File name updated from: " + originalFileName + " to " + fileName;
                    testHelper.UpdateTestResults(AppConstants.indent8 + updateFileNameMessage, true);
                }
            } else {
                testHelper.DeleteFile(fileName);
            }

            testHelper.UpdateTestResults(AppConstants.indent8 + "Saving XML to file:" + fileName + " for step " + fileStepIndex, true);
            testHelper.WriteToFile(fileName, xmlContent);
            testHelper.UpdateTestResults("Successful XML saved to file " + fileName + " for step " + fileStepIndex, true);
        } else {
            String errorMessage;
            if (xmlContent != null && !xmlContent.isEmpty()) {
                errorMessage = "Aborting!!!  No XML content was previously retrieved.";
            } else {
                errorMessage = "Aborting!!!  No File Name was specified as the destination for the downloaded XML content.";
            }
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failure XML not saved to file because: " + errorMessage + " for step " + fileStepIndex, true);
        }
    }
    //endregion


    //region {Partially implemented MongoDb connectivity }
    /***************************************************************************
     * DESCRIPTION: Method under development!!!
     *              Eventually this method will run single value
     *              MongoDB queries just like the corresponding SQL Server
     *              method.
     *              DO NOT REMOVE ANY CODE FROM THIS METHOD UNTIL IT HAS
     *              BEEN FULLY FLESHED OUT AS THE COMMENTED CODE MAY
     *              PROVE USEFUL!!!!!
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************************/
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


    /***********************************************************************
     * DESCRIPTION: Method under development!!!
     *              Creates a new MongoDb Client Connection or closes
     *              an open connection.
     *
     * IMPORTANT: Once able to successfully connect to and query the
     *              database, figure out what is worth logging but for now
     *              do not log anything except to the screen.
     *
     *              DO NOT REMOVE ANY CODE FROM THIS METHOD UNTIL IT HAS
     *              BEEN FULLY FLESHED OUT AS THE COMMENTED CODE MAY
     *              PROVE USEFUL!!!!!
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***********************************************************************/
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
        //File src = new File(phantomJsDriverPath);
        //System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
        WebDriverManager.phantomjs().setup();

        driver = new PhantomJSDriver();
        driver.manage().window().maximize(); //added 8-14-2019
        testHelper.set_is_Maximized(true);
    }



    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Chrome Driver
     *  This method has been updated to use the WebDriverManager so there is
     *  no longer a need to download the ChromeDriver as the WebDriverManager
     *  will automatically do this.
     **************************************************************************** */
    private void SetChromeDriver() {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "ChromeDriver]" + AppConstants.ANSI_RESET , true);
        //System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        WebDriverManager.chromedriver().setup();

        if (runHeadless) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("window-size=1400,800");
            options.addArguments("headless");
            options.addArguments("--dns-prefetch-disable");
            //options.addArguments("acceptInsecureCerts=true");
            options.setAcceptInsecureCerts(true);
            options.setPageLoadStrategy(PageLoadStrategy.NONE);
            driver = new ChromeDriver(options);
            testHelper.set_is_Maximized(false);
        } else {
            driver = new ChromeDriver();
            driver.manage().window().maximize(); //added 8-14-2019
            testHelper.set_is_Maximized(true);
        }
    }

    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the FireFox Driver
     *  This method has been updated to use the WebDriverManager so there is
     *  no longer a need to download the gecko/FireFoxDriver as the WebDriverManager
     *  will automatically do this.
     **************************************************************************** */
    private void SetFireFoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        if (runHeadless) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("-headless");
            FirefoxOptions options = new FirefoxOptions();
            driver = new FirefoxDriver(options);
            testHelper.set_is_Maximized(false);
        } else {
            driver = new FirefoxDriver();
            driver.manage().window().maximize(); //added 8-14-2019
            testHelper.set_is_Maximized(true);
        }
    }



    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Internet Explorer Driver
     *  This has been commented out because Internet Explorer runs incredibly
     *  slowly when sending text.
     **************************************************************************** */
    private void SetInternetExplorerDriver_new() {
        WebDriverManager.iedriver().setup();
        if (runHeadless) {
            testHelper.UpdateTestResults("The Internet Explorer Browser does not support headless execution.  Running with Graphical User Interface.", true);
        }
//        if (runHeadless) {
//            DesiredCapabilities capab = DesiredCapabilities.internetExplorer();
//            capab.setCapability("headless", true);
//
//        }
        driver = new InternetExplorerDriver();
        driver.manage().window().maximize();
    }


    private void SetInternetExplorerDriver() {
        //testHelper.UpdateTestResults("The Internet Explorer Browser was fully implemented but ran too slowly to be useful.  Please select another browser.", true);
        //internetExplorerDriverPath


        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "InternetExplorerDriver]" + AppConstants.ANSI_RESET , true);

        //testHelper.UpdateTestResults("**********************************************************************************", true);
        testHelper.UpdateTestResults(testHelper.PrePostPad("*", "*", 1, 151), true);

        testHelper.UpdateTestResults("IMPORTANT: INTERNET EXPLORER HAS LIMITED IMPLEMENTATION!!!\r\n" +
                "Click functionality required Internet Explorer specific code implementation, but further specific actions for this browser have yet to be implemented.\r\n" +
                "Right Click and Double Click actions have not been implemented and will not likely work until a specific implementation is implemented.\r\n", true);
        //testHelper.UpdateTestResults("**********************************************************************************", true);
        testHelper.UpdateTestResults(testHelper.PrePostPad("*", "*", 1, 151), true);
        File internetExplorer = new File(internetExplorerDriverPath);
        //testHelper.UpdateTestResults("internetExplorer.getAbsolutePath() = " + internetExplorer.getAbsolutePath(), true);

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("webdriver.ie.driver", internetExplorer.getAbsolutePath());
        File tmp = new File("C:\\Temp\\");

        DesiredCapabilities capab = DesiredCapabilities.internetExplorer();
        //capab.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capab.setCapability("nativeEvents", false);
        capab.setCapability("unexpectedAlertBehaviour", "accept");
        capab.setCapability("ignoreProtectedModeSettings", true);
        capab.setCapability("disable-popup-blocking", true);
        capab.setCapability("enablePersistentHover", true);
        capab.setCapability("ignoreZoomSetting", true);
        driver = new InternetExplorerDriver(capab);
    }


    /****************************************************************************
     *  DESCRIPTION:
     *  RESEARCH DATE: 6/30/2019
     *  Not working yet: (Sets the WebDriver to the Edge Driver,
     *                    which is not available for Windows 7 yet)
     *  Think this reference is wrong but saving just in case.
     *  (https://stackoverflow.com/questions/51621782/osprocess-checkforerror-createprocess-error-193-1-is-not-a-valid-win32-appl)
     *  -------------------------------------------------------------------------
     *  UPDATE 3/27/2020: Sets the WebDriver to the Edge Driver
     *  This method has been updated to use the WebDriverManager so there is
     *  no longer a need to download the ChromeDriver as the WebDriverManager
     *  will automatically do this.
     **************************************************************************** */
    private void SetEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        driver.manage().window().maximize();
        if (runHeadless) {
            testHelper.UpdateTestResults("The Edge Browser does not support headless execution.  Running with Graphical User Interface.", true);
        }
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
     * @param fileStepIndex - the file index and the step index.
     * @return - Corresponding Key object variable.
     ***************************************************************/
    private CharSequence GetKeyValue(String value, String fileStepIndex) {
        value = value.toLowerCase().trim();
        testHelper.UpdateTestResults(AppConstants.indent5 + "Replacing (" + value + ") with corresponding Key value keyword for step " + fileStepIndex, false);

        switch (value) {
            case "keys.enter":
                return Keys.ENTER;
            case "keys.return":
                return Keys.RETURN;
            case "keys.arrow_down":
                return Keys.ARROW_DOWN;
            case "keys.arrow_up":
                return Keys.ARROW_UP;
            case "keys.arrow_left":
                return Keys.ARROW_LEFT;
            case "keys.arrow_right":
                return Keys.ARROW_RIGHT;
            case "keys.back_space":
                return Keys.BACK_SPACE;
            case "keys.cancel":
                return Keys.CANCEL;
            case "keys.escape":
                return Keys.ESCAPE;
            case "keys.tab":
                return Keys.TAB;
            default:
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Key: " + value + " " + fileStepIndex + " not mapped!" + AppConstants.ANSI_RESET, true);
                break;
        }
        return value;
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
    private String GenerateXPath(WebElement childElement, String current) {
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
                return GenerateXPath(parentElement, "/" + childTag + "[" + count + "]"+current);
            }
        }
        return null;
    }

    /**************************************************************************
     * Description: Retrieves the argument value at the specified index
     *              and returns it if available or it returns the defaultValue
     *              parameter passed in.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param index - index of the argument list element to retrieve
     * @param defaultValue - value to use if retrieved value is null.
     * @return - retrieved value if not null else defaultValue passed in.
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
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param index - index of the argument list element to retrieve
     * @param defaultValue - value to use if retrieved value is null.
     * @return - retrieved value if not null else defaultValue passed in.
     ****************************************************************/
    private int GetArgumentNumericValue(TestStep ts, int index, int defaultValue) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;

        if (arg!=null) {
            return parseInt(arg.get_parameter());
        } else {
            return defaultValue;
        }
    }

    private double GetArgumentNumericDoubleValue(TestStep ts, int index, int defaultValue) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;

        if (arg!=null) {
            return parseDouble(arg.get_parameter());
        } else {
            return defaultValue;
        }
    }

    /*****************************************************************
     * Description: Checks if the Argument retrieved at the specified
     *              index is numeric.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param index - index of the argument list element to check.
     * @return - True if numeric, else False
     *****************************************************************/
    private Boolean CheckArgumentNumeric(TestStep ts, int index) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;
        boolean status = true;

        try {
            if (arg != null && arg.get_parameter() != null) {
                int returnValueCheck = Integer.parseInt(arg.get_parameter());
            }
        } catch (NumberFormatException ne){
            status = false;
        }

        return status;
    }

    /*****************************************************************************
     * Description: This method sorts the Arguments in the Navigation Command.
     *              It is called when it is determined that the arguments are
     *              out of order, it then rearranges the arguments putting them
     *              in the proper order.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param navigateUrl - URL to navigate to
     * @param delayTime     - time to wait for navigation to complete
     * @param windowDimensions - window dimensions to set for the browser
     * @param sortField - field found to be out of argument order
     *****************************************************************************/
    private void SortNavigationArguments(TestStep ts, String navigateUrl, String delayTime, String windowDimensions, String pageTimings, String sortField) {
        String [] items;
        //String[] tempItems = new String[] {"navigate", "delayTime", "windowDimensions", "pageTimings"};
        String[] tempItems = new String[] {"", "", "", ""};

        items = new String[] {navigateUrl, delayTime, windowDimensions, pageTimings};
        for (int x=0;x<items.length;x++) {
            if (items[x] != null) {
                if (items[x].contains("http")) {
                    tempItems[0] = items[x];
                } else if (items[x].toLowerCase().contains("w=")) {
                    tempItems[2] = items[x];
                } else if (items[x].toLowerCase().contains("fe")) {
                    tempItems[3] = items[x];
                } else if (TestHelper.tryParse(items[x]) != null) {
                    tempItems[1] = items[x];
                }
            }
        }

        RearrangeArgumentOrder(ts, tempItems, ts.get_command());
    }



    /***************************************************************************************
     * Description: This method tests the Wait command arguments to determine if they are
     *              out of order.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     ***************************************************************************************/
    private void CheckWaitArgumentOrder(TestStep ts) {
        String value1 = GetArgumentValue(ts, 0, null);
        String value2 = GetArgumentValue(ts, 1, null);
        String [] items = {value2, value1};

        if (TestHelper.tryParse(value2) == null) {
            if (TestHelper.tryParse(value1) != null && ts.get_command().toLowerCase().contains(AppCommands.Page)) {
                RearrangeArgumentOrder(ts, items, ts.get_command());
            }
        }
        if (testHelper.CheckIsUrl(value2) && ts.get_command().toLowerCase().contains(AppCommands.Page)) {
            RearrangeArgumentOrder(ts, items, ts.get_command());
        }
    }



    /********************************************************************************
     *  Description: This method checks the order of the Switch to Iframe command
     *               arguments to ensure that they are in the correct order.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     ********************************************************************************/
    private void CheckiFrameArgumentOrder(TestStep ts) {
        //String value1 = GetArgumentValue(ts, 0, null);
        String value2; // = GetArgumentValue(ts, 1, null);
        List<Argument> remainingItems;
        remainingItems = ts.ArgumentList;

        //find the name of the iframe, move it to the first argument, and  but keep all other arguments in the existing order after the name
        int nameIndex = FindInArgumentList(ts.ArgumentList, "IFrameName");
        if (nameIndex != 0) {
            ArgumentOrderErrorMessage(ts, ts.get_command());
            value2 = GetArgumentValue(ts, nameIndex, null);
            ts.ArgumentList.remove(nameIndex);  //remove this item
            ts.ArgumentList.add(0, new Argument(value2));
        }

        //if this is an iframe it could have 1 or many arguments, get any past the initial 2 and reappend them after the rearrangement
        if (remainingItems.size() > 2) {
            Argument item;
            int arraySize = remainingItems.size();
            for (int x=2;x < arraySize ;x++) {
                item = new Argument();
                item.set_parameter(remainingItems.get(x).get_parameter());
//                testHelper.DebugDisplay("remainingItems.get(x).get_parameter() = " + remainingItems.get(x).get_parameter());
                ts.ArgumentList.add(item);
            }
        }
    }

    /*****************************************************************************************
     * Description: This method searches the list of arguments for to find the argument that
     *              should be first and currently is used exclusively to find the iFrame name
     *              but could be used for other commands, if necessry.
     * @param argumentList - List of Arguments provided for the command object.
     * @param searchString - The phrase to search for in the argumentList.
     * @return - Returns the index of the phrase it is searching for in the argumentList.
     *****************************************************************************************/
    private int FindInArgumentList(List<Argument> argumentList, String searchString) {
        int returnValue = 0;

        if (searchString != null && searchString.toLowerCase().equals("iframename")) {
            for (int x = 0; x < argumentList.size(); x++) {
                if (!argumentList.get(x).get_parameter().contains(AppCommands.Keys) && !argumentList.get(x).get_parameter().contains(AppCommands.Click)) {
                    returnValue = x;
                    break;
                }
            }
        }
        return returnValue;
    }

    /****************************************************************************************
     * Description: Checks the Order of Arguments supplied for the ScreenShot command
     *              to ensure that they are in the proper order or calls a subsequent
     *              method to rearrange the arguments into the proper order.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     ****************************************************************************************/
    private void CheckScreenShotArgumentOrder(TestStep ts) {
        String value1 = GetArgumentValue(ts, 0, null);
        String value2 = GetArgumentValue(ts, 1, null);

        if ((value1 != null && (value1.contains("w=") || value1.contains("h="))) || (value2 != null && (value2.contains(System.getProperty("file.separator"))
                || value2.contains(".png")))) {
            String [] items = {value2, value1};
            RearrangeArgumentOrder(ts, items, ts.get_command());
        }
    }

    /*********************************************************************************
     * Description: This method checks the order of the Create Test File command
     *              arguments to ensure that they are in the correct order.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     *********************************************************************************/
    private void CheckCreateTestFileArgumentOrder(TestStep ts) {
        String selector = GetArgumentValue(ts, 0, "*");
        String fileName = GetArgumentValue(ts, 1, null);
        String exclusionList = GetArgumentValue(ts, 2, null);
        //place the filename into temp
        String tempFileName = selector.contains(".") || selector.contains("\\") || selector.contains("//") ? selector :
                exclusionList.contains(".") || exclusionList.contains("\\") || exclusionList.contains("//") ?
                        exclusionList : fileName;
        String tempExclusionList = selector.contains(",") ? selector : fileName.contains(",") ? fileName : exclusionList;
        String tempSelector = (tempFileName.equals(fileName) && tempExclusionList.equals(exclusionList)) ? selector :
                (tempFileName.equals(exclusionList) && tempExclusionList.equals(fileName)) ? selector :
                        (tempFileName.equals(selector) && tempExclusionList.equals(fileName)) ? exclusionList :
                        (tempFileName.equals(exclusionList) && tempExclusionList.equals(selector)) ? fileName : "*";

        if (fileName == null || fileName.isEmpty() || !fileName.equals(tempFileName) || !exclusionList.equals(tempExclusionList)
                || (selector != null && !selector.equals(tempSelector))) {
            String [] items = {tempSelector, tempFileName, tempExclusionList};
            RearrangeArgumentOrder(ts, items, ts.get_command());
        }
    }


    /***********************************************************************************
     * Description: This method checks the order of arguments for the Check Contrast
     *              command to ensure that they are in the correct order.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     ***********************************************************************************/
    private void CheckColorContrastArgumentOrder(TestStep ts) {
        String tagType = GetArgumentValue(ts, 0, null);
        String bContrast = GetArgumentValue(ts, 1, null);
        String dContrast = GetArgumentValue(ts, 2, null);
        String tempTagType = !tagType.contains("=") ? tagType : !bContrast.contains("=") ? bContrast : dContrast;
        String tempbContrast = (tagType.contains("b") && tagType.contains("=")) ? tagType : (bContrast.contains("b") && bContrast.contains("=")) ? bContrast
                : (dContrast.contains("b") && dContrast.contains("=")) ? dContrast : null;
        String tempdContrast = (tagType.contains("d") && tagType.contains("=")) ? tagType : (bContrast.contains("d") && bContrast.contains("=")) ? bContrast
                : (dContrast.contains("d") && dContrast.contains("=")) ? dContrast : null;

        if (tagType.contains("=") || (bContrast != null && bContrast.toLowerCase().contains("d=")) || (dContrast != null && dContrast.toLowerCase().contains("b="))) {
            String [] items = {tempTagType, tempbContrast, tempdContrast};
            RearrangeArgumentOrder(ts, items, ts.get_command());
        }
    }


    /*************************************************************************************
     * Description: This method takes an array of arguments and updaates the TestStep's
     *              Argument List placing arguments in the proper order when it is determined
     *              by a check argument order method that they are out of order.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param items - array of arguments
     * @param command - Actual command being executed as it may sometimes differ here
     *                as a sub-command may be executing as part of the main command.
     *************************************************************************************/
    private void RearrangeArgumentOrder(TestStep ts, String[] items, String command) {
        ArgumentOrderErrorMessage(ts, command);
        Argument item;
        ts.ArgumentList = new ArrayList<>();
        for (String currentItem: items) {
            //testHelper.DebugDisplay("currentItem = " + currentItem);
            item = new Argument();
            item.set_parameter(currentItem);
            ts.ArgumentList.add(item);
        }
    }



    /*******************************************************************************
     * Description: Displays an Argument Order Error Message based on the command
     *              found in the ts if present or the command string variable.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param command - Actual command being executed as it may sometimes differ here
     *                as a sub-command may be executing as part of the main command.
     *******************************************************************************/
    private void ArgumentOrderErrorMessage(TestStep ts, String command) {
        String problemCommand = ts != null ? ts.get_command() : command;
        String errorStartDecorator = testHelper.PrePostPad("[ Argument Order Error!!! - Attempting to reorder ]", "*", 9, 100) + "\r\n";
        String errorEndDecorator = "\r\n" + testHelper.PrePostPad("*", "*", 9, 100) ;
        String errorMessage = "";
        if (problemCommand.toLowerCase().equals(AppCommands.Navigate)) {
            errorMessage = "Navigation command arguments out of order!!! \r\n" +
                            "Refer to the help file for the proper order, shown below!!!\r\n" +
                            "\t<arg1>>Navigation Url</arg1>\r\n\t<arg2>Delay Time</arg2>\r\n\t<arg3>Browser Window Dimensions</arg3>\r\n\t<arg4>Page Load Max Timings</arg4>";
        } else if (problemCommand.toLowerCase().equals(AppCommands.Switch_To_IFrame)) {
            errorMessage = "Switch to IFrame arguments out of order!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "Argument 1 is always required, the remaining arguments depend upon the sub command.\r\n" +
                    "\t<arg1>IFrame Name</arg1>\r\n\t<arg2>sub command</arg2>\r\n\t<arg3>depends on subcommand</arg3>";
        } else if (problemCommand.toLowerCase().contains(AppCommands.WaitForPage)) {
            errorMessage =  "Wait for Page command arguments out of order!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "\t<arg1>Navigate URL or n/a</arg1>\r\n\t<arg2>wait time</arg2>";
        } else if (problemCommand.toLowerCase().contains(AppCommands.Login)) {
            errorMessage =  "Login command arguments out of order, ABORTING!!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "This application will not attempt to try to distinguish between the user name and password!";
            errorStartDecorator = errorStartDecorator.replace("Attempting to reorder", "Not Attempting to reorder");
        } else if (problemCommand.toLowerCase().contains(AppCommands.Create) && problemCommand.toLowerCase().contains(AppCommands.Test)) {
            errorMessage = "Create Test Page arguments out of order!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "Arguments 1 and 2 are always required but argument 3 is optional.\r\n" +
                    "\t<arg1>Selector which is Tag Type or * for all tags</arg1>\r\n\t<arg2>File name for test file being created</arg2>\r\n\t<arg3>html elements to exclude if argument 1 is *</arg3>";
        } else if (problemCommand.toLowerCase().contains(AppCommands.Connect_To_Database)) {
            errorMessage = "Connect to Database arguments out of order, ABORTING!!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "This application will not attempt to try to distinguish between the arguments for this command!";
            errorStartDecorator = errorStartDecorator.replace("Attempting to reorder", "Not Attempting to reorder");
        } else if (problemCommand.toLowerCase().contains(AppCommands.SQL_Server_Query)) {
            errorMessage = "Sql Server Query arguments out of order, ABORTING!!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "This application will not attempt to try to distinguish between the arguments for this command!";
            errorStartDecorator = errorStartDecorator.replace("Attempting to reorder", "Not Attempting to reorder");
        } else if (problemCommand.toLowerCase().contains(AppCommands.Save_JSON)) {
            errorMessage = "Save JSON arguments out of order!!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "Arguments 1, the file name is always required but argument 2 is optional.\r\n" +
                    "\t<arg1>JSON filename</arg1>\r\n\t<arg2>overwrite or true or false to create a new file name</arg2>";
        } else if (problemCommand.toLowerCase().contains(AppCommands.Check_Contrast)) {
            errorMessage = "Check Contrast arguments out of order!!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "Arguments 1, the element to check against the background is always required but arguments 2 and 3 are optional.\r\n" +
                    "\t<arg1>HTML TagName or * for all tags</arg1>\r\n\t<arg2>b=integer - color brightness</arg2>\r\n\t<arg3>d=integer - color difference</arg3>";
        } else if (problemCommand.toLowerCase().contains(AppCommands.Close_Child_Tab)) {
            errorStartDecorator = errorStartDecorator.replace("Argument Order Error!!! - Attempting to reorder", "Argument Error!!! - Skipping invalid command!!!");
            errorMessage = "The tab number argument was either not supplied, was too large, or was for the main tab." +
                    "1.\tThe tab to be closed must always be specified!!!!\r\n" +
                    "2.\tA tab that does not exist cannot be closed!!!!\r\n" +
                    "3.\tThe main tab cannot be closed until the test ends!!!!\r\n" +
                    "This test step has been aborted but any subsequent test steps will execute!\r\n" +
                    "Valid values for this command are 1 or higher, but ensure that the tab exists.\r\n" +
                    "Do not attempt to close a tab that does not exist!!!";
        } else if (problemCommand.toLowerCase().contains(AppCommands.ScreenShot)) {
            errorMessage = "ScreenShot arguments out of order!!!!\r\n" +
                    "Refer to the help file for the proper order!!!\r\n" +
                    "Argument 1, the filename for saving the screen shot is optional but should be provided if providing the dimensions.\r\n" +
                    "Argument 2, the specified dimensions for saving the image so that it can be compared to an image of equal size is optional.\r\n" +
                    "The dimensions are numeric values and the identifiers for those dimensions are string values w= for width and h= for height.\r\n" +
                    "\t<arg1>c:\\ScreenShots\\Actual\\ActualImage.png</arg1>\r\n\t<arg2>w=1400 h=1000</arg2>";
        }

        testHelper.UpdateTestResults( AppConstants.ANSI_RED_BRIGHT + errorStartDecorator +
                errorMessage + errorEndDecorator + AppConstants.ANSI_RESET, true);
    }


    /**************************************************************
     * DESCRIPTION:  Check to see if an Alert window is present.
     *
     * @return - true if alert window present, else return false
     **************************************************************/
    boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /****************************************************************
     * Description: Checks the argument value passed in to determine
     *              if it is one of the acceptable comparison
     *              operators.
     *
     * @param comparisonType - Operator used for comparison, can be
     *                       "=" or "!="
     *                       (quotes used for clarity only).
     * @return - returns the comparison type as a string.
     ****************************************************************/
    private String CheckComparisonOperator(String comparisonType) {
        //in case the comparison type is not passed in, default it to equals.
        if (comparisonType == null || (!comparisonType.equals("!=") && !comparisonType.equals("="))) {
            comparisonType = "=";
        }
        return comparisonType;
    }

    /*************************************************************
     * DESCRIPTION:
     *      Method reports improperly formatted tests to the
     *      user with the test step so that it can be fixed.
     * @param fileStepIndex - indicates the file and step where
     *                            this command was issued.
     ************************************************************ */
    private void ImproperlyFormedTest(String fileStepIndex) {
        testHelper.UpdateTestResults("Improperly formatted test for step " + fileStepIndex, true);
    }

    /*******************************************************************
     * Description: This method writes a message to the log and console
     *              if the default contrast values are overridden by the
     *              test step to alert the user and for future reference.
     * @param brightnessStandard - Brightness Level that is overriding the
     *                           Approved Brightness Level.
     * @param contrastStandard - Contrast Level that is overriding the
     *                         Approved Contrast Level.
     *********************************************************************/
    private void AdaApprovedContrastValuesOverriddenMessage(int brightnessStandard, int contrastStandard) {
        String defaultCheckValuesOverridden;
        defaultCheckValuesOverridden = "*******************[ ATTENTION!!! ]*************************\r\n" +
                AppConstants.indent8 + AppConstants.indent5 + "Test has overridden Default ADA Approved Contrast values.\r\n" +
                AppConstants.indent8 + AppConstants.indent5 + "Instead of:\r\n" + "" +
                AppConstants.indent8 + AppConstants.indent5 + " (Brightness Contrast: " + AppConstants.DefaultContrastBrightnessSetting +
                " - Difference Contrast: " + AppConstants.DefaultContrastDifferenceSetting + ")\r\n" +
                AppConstants.indent8 + AppConstants.indent5 + "Test step has set these to:\r\n" +
                AppConstants.indent8 + AppConstants.indent5 + "(Brightness Contrast: " + brightnessStandard + " - Difference Contrast: " + contrastStandard + ")\r\n" +
                AppConstants.indent8 + "**********************************************************";

        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.ANSI_RED + defaultCheckValuesOverridden + AppConstants.ANSI_RESET, true);
    }
    //endregion


    //region { Refactored Methods no longer used but not yet removed }
    /**************************************************************************
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
            testHelper.UpdateTestResults("color_hex = " + color_hex, false);

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
//                testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTWHITE + AppConstants.ANSI_RED + "Good brightness Warning contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + AppConstants.ANSI_RESET, false);
                testHelper.UpdateTestResults(AppConstants.ANSI_WHITE_BACKGROUND_BRIGHT + AppConstants.ANSI_RED + "Good brightness Warning contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + AppConstants.ANSI_RESET, false);
            } else if (brightness < brightnessStandard && contrast >= contrastStandard) {
                //testHelper.UpdateTestResults(AppConstants.ANSI_BRIGHTWHITE + AppConstants.ANSI_RED + "Warning brightness and Good contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + AppConstants.ANSI_RESET, false);
                testHelper.UpdateTestResults(AppConstants.ANSI_WHITE_BACKGROUND_BRIGHT + AppConstants.ANSI_RED + "Warning brightness and Good contrast forecolor(" + color + ") brightness: " + foreColorBrightness + " backcolor(" + backColor + ")" + backColorAncestor + " brightness: " + backColorBrightness + " Contrast: " + brightness + " Color Difference: " + contrast + AppConstants.ANSI_RESET, false);
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

    /*****************************************************************************
     * DESCRIPTION: This method Searches the global JSON string downloaded in the
     *              GetJsonContent method for the expected value.
     *              Since this is actually a search of the string and not a true
     *              query, if the search string is found, the search and found
     *              values are the search string for reporting, else a not found
     *              message is displayed.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *****************************************************************************/
    private void QueryJSONContains(TestStep ts, String fileStepIndex) {
        if (jsonContent != null && !jsonContent.isEmpty()) {
            String searchString = "\"" + ts.get_accessor() + "\":" + ts.get_expectedValue();
            testHelper.UpdateTestResults(AppConstants.indent8 + "Searching JSON for " + searchString + " for step " + fileStepIndex,true);
            if (jsonContent.contains(searchString)) {
                testHelper.UpdateTestResults("Successful JSON Search for step " + fileStepIndex + " Searched for: (" + searchString + ") Found: (" + searchString + ")", true);
                if (ts.get_crucial()) {
                    assertEquals(searchString, searchString);
                }
            } else {
                testHelper.UpdateTestResults("Failed JSON Search for step " + fileStepIndex + " Searched for: (" + searchString + ") but did not find this!", true);
                //If this is crucial and the search string is not found, force an assertion failure
                if (ts.get_crucial()) {
                    assertEquals(searchString, null);
                }
            }
        }
    }

    /****************************************************************************
     * DESCRIPTION: This method starts by checking if a URL parameter was
     *              supplied and if so, calls the navigation method to
     *              perform a Navigation event and make that the current page.
     *              Next, it downloads the XML from the current page and stores
     *              it in a global variable where it can later be queried by
     *              another method.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - the retrieved XML if successful else null
     * @throws Exception - Possible Exception attempting to retrieve XML from API endpoint
     ****************************************************************************/
    private String GetXmlContent(TestStep ts, String fileStepIndex) throws Exception {
        String xmlResponse = null;
        String url = GetArgumentValue(ts, 0, null);
        if (url != null && !url.isEmpty()) {
            testHelper.setNavigationMessageIndent(AppConstants.indent8 + AppConstants.indent5);
            PerformExplicitNavigation(ts, fileStepIndex);
            testHelper.setNavigationMessageIndent(null);

            xmlResponse = driver.getPageSource();     //driver.getPageSource();
            if (!testHelper.IsNullOrEmpty(xmlResponse)) {
                //testHelper.DebugDisplay("xmlResponse= " + xmlResponse);
                return xmlResponse;
            }
        }
        return xmlResponse;
    }

    /****************************************************************************
     * DESCRIPTION: This method starts by checking if a URL parameter was
     *              supplied and if so, calls the navigation method to
     *              perform a Navigation event and make that the current page.
     *              Next, it downloads the JSON from the current page and stores
     *              it in a global variable where it can later be queried by
     *              another method.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - the retrieved JSON if successful else null
     * @throws Exception - Possible Exception attempting to retrieve JSON from API endpoint
     ****************************************************************************/
    private String GetJsonContent(TestStep ts, String fileStepIndex)throws Exception {
        String jsonResponse;
        String url = GetArgumentValue(ts, 0, null);
        if (url != null && !url.isEmpty()) {
            testHelper.setNavigationMessageIndent(AppConstants.indent8 + AppConstants.indent5);
            PerformExplicitNavigation(ts, fileStepIndex);
            testHelper.setNavigationMessageIndent(null);
        }
        //DO NOT REMOVE THIS.  MAY TRANSFORM THIS FROM A STRING INTO A JSON OBJECT TO SEE IF EASIER TO QUERY
        //JsonObject jsonObject = (JSON)driver.getPageSource();
        jsonResponse = driver.getPageSource();

        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            return jsonResponse;
        }
        return jsonResponse;
    }
    //endregion


    //region {Updated and Replaced Methods}
    /*private void SetFireFoxDriver_old() {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "FireFoxDriver]" + AppConstants.ANSI_RESET , true);
        File gecko = new File(fireFoxDriverPath);
        System.setProperty("webdriver.gecko.driver", gecko.getAbsolutePath());
        FirefoxOptions options = new FirefoxOptions();
        //options.setCapability("marionette", false);
        String loggingLevel = "fatal";   //"trace"
        options.setCapability("marionette.logging", "trace");
        options.setBinary(gecko.getAbsolutePath());

        if (runHeadless) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("-headless");
            options.setBinary(firefoxBinary);
            driver = new FirefoxDriver(options);
            testHelper.set_is_Maximized(false);
        } else {
            driver = new FirefoxDriver();
            driver.manage().window().maximize(); //added 8-14-2019
            testHelper.set_is_Maximized(true);
        }
    }

    private void SetEdgeDriver_old() {
        testHelper.UpdateTestResults("The Edge Browser Driver was not available at the time this application was created.  Please select another browser.", true);

        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "EdgeDriver]" + AppConstants.ANSI_RESET , true);
        File edge = new File(edgeDriverPath);
        testHelper.UpdateTestResults("edge.getAbsolutePath() = " + edge.getAbsolutePath(), true);

        System.setProperty("webdriver.edge.driver", edge.getAbsolutePath());
        //System.setProperty("webdriver.chrome.driver", edge.getAbsolutePath());
//        File tmp = new File("C:\\Temp\\");
          //EdgeOptions options = new EdgeOptions();
          //options.
//        options.setCapability();
        driver = new EdgeDriver();
    }
    private void SetInternetExplorerDriver_old() {
            testHelper.UpdateTestResults("The Internet Explorer Browser was fully implemented but ran too slowly to be useful.  Please select another browser.", true);
            //internetExplorerDriverPath

            testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "InternetExplorerDriver]" + AppConstants.ANSI_RESET , true);
            File internetExplorer = new File(internetExplorerDriverPath);
            testHelper.UpdateTestResults("internetExplorer.getAbsolutePath() = " + internetExplorer.getAbsolutePath(), true);

            System.setProperty("webdriver.ie.driver", internetExplorer.getAbsolutePath());
            File tmp = new File("C:\\Temp\\");

            DesiredCapabilities capab = DesiredCapabilities.internetExplorer();
            capab.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            driver = new InternetExplorerDriver(capab);

    }

     private void SetPhantomJsDriver_old() {
        /*
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "PhantomJSDriver]" + AppConstants.ANSI_RESET , true);
        File src = new File(phantomJsDriverPath);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, src.getAbsolutePath());
        //IMPORTANT: for phantomJS you may need to add a user agent for automation testing as the default user agent is old
        // and may not be supported by the website.
        capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
        this.driver = new PhantomJSDriver(capabilities);
        driver.manage().window().maximize(); //added 8-14-2019
        testHelper.set_is_Maximized(true);
        }
     */



}

