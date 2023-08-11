import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
     *      chromeDriverPath, (No longer necessary but add Chrome.exe (not chromedriver.exe) folder to the path variable)
     *      fireFoxDriverPath, (No longer necessary but add FireFox.exe folder to the path variable)
     *      phantomJsDriverPath, (Required)
     *      internetExplorerDriverPath,  (Required)
     *      edgeDriverPath  (No longer necessary but ensure Edge.exe folder is in the path variable)
     *
     *      Recent updates:
     *      1.  Looked at Login which previously didn't work on iOS.
     *          It is currently working on iOS.
     *          Previously did not work on iOS but worked on Windows 7, 10.
     *      2.   Screenshot comparison using Image Magic (Implemented)
     *          (https://www.swtestacademy.com/visual-testing-imagemagick-selenium/)
     *      3.  The following has been achieved through the "parse and calculate double" and the
     *          "parse and calculate long" commands.
     *          Parsing text retrieved from an element and performing actions on this such as:
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
     *      4.   Updated WebDriverManager to eliminate the need to download drivers for
     *           Chrome, FireFox and Edge but currently PhantomJS and Internet Explorer still require
     *           referencing the path of the driver on the system.
     *      5.  Implemented JavaScript Variable testing so that DataLayer values could be tested.
     *          This test currently allows for testing one dataLayer value but this value cannot be persisted yet.
     *      6.  Google Tag Manager tag checking which allows for checking the following:
     *          Requires Saving the HAR file, as a test step, before testing which tags executed.
     *          Saving HAR file also populates list of GTM tags executed.
     *          The Expected values for each field are contained in the arguments elements as name=value pairs.
     *          The following Arguments are required but in no specific order:
     *              a.  Document Location  - <arg1>dl=https://www.mypage.com</arg1>
     *                  - used to help locate the correct HAR entry
     *              b.  Hit Type - <arg2>t=event</arg2>
     *              c.  Event Category - <arg3>ec=my event category</arg3>
     *              d.  Event Action - <arg4>ea=my event action</arg4>
     *              e.  Event Label - <arg5>el=my event label</arg5>
     *              f.  Tracking ID - <arg6>tid=UA-1234567-1</arg6>
     *              g.  Content Group 1 - <arg7>cg1=plp</arg7>
     *          The following Arguments are not required but if provided will be used:
     *              h.  Content Group 2 - <arg8>cg2+=GTM-A1BCDE2</arg8>
     *                  - adding the plus sign before the equal sign allows for the value begins with the value supplied.
     *              i.  Document Title - <arg9>dt=Products | Your Site</arg9>
     *      Future updates:
     *      2.  Greater Than and Less Than Operators.
     *          This would be a good addition when used with Conditional Blocks.
     *      2.  SiteMap Generator - while not part of testing, it could help to find all pages that
     *          require testing.
     *      3.  Complex Equation Command
     *          a.  Splits equation by space character.
     *          b.  Requires a List of Calculation objects
     *              The Calculation object contains the following types of fields
     *                  i.      Open Parenthesis - string can be null for no parenthesis
     *                  ii.     First Number  - Need to figure out what type works best double/long (both)
     *                  iii.    Operator - operation to perform (*, /, +, -)
     *                  iv.     Second Number - calculate with first number
     *                  v.      Close Parenthesis - string can be null for no parenthesis
     *              Iterate through the List of calculations in order to properly implement "PEMDAS"
     *                  Exponential example: - int exp = (int) Math.pow(firstNumber, secondNumber);
     *
     *
     *
     ╚═══════════════════════════════════════════════════════════════════════════════╝ */
     //endregion


    //region { Application Configuration Variables }
    private String configurationFile = "Config/ConfigurationSetup.xml";
    String configurationFolder = "Config/";
    String harFolder = configurationFolder + "HAR_files/";
    static String testPage = "https://www.myWebsite.com/";
    private boolean runHeadless = true;
    String screenShotSaveFolder;
    // = BrowserTypes.Firefox, BrowserTypes.Chrome, BrowserTypes.PhantomJS, BrowserTypes.Edge, BrowserTypes.Internet_Explorer
    private BrowserTypes _selectedBrowserType;
    private String createCSVStatusFiles = "none";
    //endregion

    WebDriver driver;
    TestHelper testHelper;
    ReadCommands readCommands;
    WriteCommands writeCommands;
    //browser mob proxy declarations
    Proxy seleniumProxy;
    public static BrowserMobProxyServer proxy;
    //end browser mob proxy declarations

//    private HelperUtilities helperUtilities = new HelperUtilities();
    private boolean testAllBrowsers = false;  //true;
    private List<TestStep> testSteps = new ArrayList<>();
    private String testFileName;
    //private String _testFileName;
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
    public List<GtmTag> GtmTagList;
    public List<GA4Tag> GA4TagList;
    public String get_testFileName() {return testFileName;}

    //region { WebDriver Browser Driver Configured Locations }
    private String phantomJsDriverPath = "/gary/java utilities/BrowserDrivers/phantomjs.exe";
    private String internetExplorerDriverPath = "/gary/java utilities/BrowserDrivers/IEDriverServer.exe";
    //private String chromeDriverPath = "/gary/java utilities/BrowserDrivers/chromedriver.exe";
    //private String fireFoxDriverPath = "/gary/java utilities/BrowserDrivers/geckodriver.exe";
    //private String edgeDriverPath = "/gary/java utilities/BrowserDrivers/msedgedriver.exe";
    //endregion

    //local global variables for values that need to live outside of a single method
    private boolean _executedFromMain = false;
    MongoClient mongoClient = null;
    Connection sqlConnection = null;
    MongoClientURI mongoClientUri = null;
    String persistedString = null;
    String uniqueId = null;
    boolean conditionalSuccessful = false;
    String jsonContent = null;
    String xmlContent = null;
    String _csvFileName;

    public void set_csvFileName(String _csvFileName) { this._csvFileName = _csvFileName;}
    public String get_csvFileName() { return _csvFileName;}

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

    private Boolean _showAdditionalGA4Parameters;
    void set_showAdditionalGA4Parameters(Boolean _showAdditionalGA4Parameters) {this._showAdditionalGA4Parameters = _showAdditionalGA4Parameters;}
    Boolean get_showAdditionalGA4Parameters() {
        return this._showAdditionalGA4Parameters;
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
        //make these objects all reference this object instance
        readCommands = new ReadCommands(this);
        writeCommands = new WriteCommands(this);
        testHelper = new TestHelper(this);

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
            WriteHarContent();
            driver.close();
            driver.quit();
            proxy.stop();
        } catch(Exception e) {
            //the driver was never instantiated so do nothing here
            testHelper.UpdateTestResults("Error Closing Application: " + e.getMessage(), false);
        }
        PerformCleanup();
    }

    /**************************************************************
     *  Description: This method writes the HAR content to a file.
     *               Currently, this has been called from the TearDown
     *               but may be moved at a later time
     ***************************************************************/
    private void WriteHarContent() {

        try {
            Har har = proxy.getHar();
            SaveHarFile(har, testPage);
            testHelper.UpdateTestResults("Start Writing HAR file for end of test!", true);
            List<HarEntry> entries = proxy.getHar().getLog().getEntries();
            /*
            for (HarEntry entry : entries) {
                //System.out.println(entry.getRequest().getUrl());
                //testHelper.UpdateTestResults(entry.getRequest().getUrl(), false);
                //testHelper.UpdateTestResults("getStatusText() = " + entry.getResponse().getStatusText(), false);
                //testHelper.UpdateTestResults(entry.getRequest().getPostData().getParams().toString(), false);
                int size = entry.getRequest().getQueryString().size();
                for (int x=0;x<size;x++) {
                    testHelper.UpdateTestResults(entry.getRequest().getQueryString().get(x).getName() + " = " + entry.getRequest().getQueryString().get(x).getValue(), false);
                }
            }*/
            testHelper.UpdateTestResults("End Writing HAR file!", true);
        } catch(Exception ex) {
            testHelper.UpdateTestResults("Error writing HAR file: " + ex.getMessage(), true);
        }
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
        readCommands = new ReadCommands(this);
        writeCommands = new WriteCommands(this);
        testHelper = new TestHelper(this);
        testHelper.set_executedFromMain(isStartedFromMain);
        //testHelper.DebugDisplay("isStartedFromMain = " + isStartedFromMain);
        if (isStartedFromMain) {
            logFileName = logFileName.replace(logFileRootFileName, "StandAlone_" + logFileRootFileName);
        }


        File tmp = new File(configurationFile);
        testHelper.CreateSectionHeader("[ Starting Test Application Initialization ]", AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, true, false, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Config File absolute path = " + AppConstants.ANSI_RESET + tmp.getAbsolutePath(), false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Log File Name = " + AppConstants.ANSI_RESET  + logFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "CSV File Name = " + AppConstants.ANSI_RESET  + logFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Help File Name = " + AppConstants.ANSI_RESET + helpFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Har File Folder = " + AppConstants.ANSI_RESET + harFolder, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "HTML Help File Name = " + AppConstants.ANSI_RESET + helpFileName.replace(".txt", ".html"), false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 + "Executed From Main or as JUnit Test = " + AppConstants.ANSI_RESET + (is_executedFromMain() ? "Standalone App" : "JUnit Test"), false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 + "Running on "  + AppConstants.ANSI_RESET + (HelperUtilities.isWindows() ? "Windows" : "Mac"), false);
        testHelper.CreateSectionHeader("[ End Test Application Initialization ]", AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, false, false, false);
        testHelper.UpdateTestResults("", false);

        testHelper.set_logFileName(logFileName);
        readCommands.testHelper.set_logFileName(logFileName);
        writeCommands.testHelper.set_logFileName(logFileName);
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
            this._showAdditionalGA4Parameters = configSettings.get_showAdditionalGa4Parameters();
            set_showAdditionalGA4Parameters(configSettings.get_showAdditionalGa4Parameters());
            readCommands.set_testPage(testPage);
            readCommands.set_showAdditionalGa4Parameters(get_showAdditionalGA4Parameters());


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
     *    Runs all tests read in from all test settings files.
     **************************************************************************** */
    void TestPageElements() throws Exception {
        if (this.driver == null) {
            return;
        } else {
            readCommands.driver = this.driver;
            writeCommands.driver = this.driver;
        }
        int startIndex = 0;  //used for instances when you do not want to start at the first element to test
        boolean revertToParent = false;
        String csvFileName;
        testHelper.set_csvFileName(null);

        for (int fileIndex = 0; fileIndex < testFiles.size(); fileIndex++) {
            testFileName = testFiles.get(fileIndex);
            testHelper.set_testFileName(testFileName);
            readCommands.set_testFileName(testFileName);
            writeCommands.set_testFileName(testFileName);
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

            if (this.createCSVStatusFiles.equals("many")) {
                SetCSVFileName(testFileName);  //added for individual CSV files
            } else if (this.createCSVStatusFiles.equals("one") && testHelper.get_csvFileName() == null) {
                SetCSVFileName(logFileName);
            } else if (this.createCSVStatusFiles.equals("none")) {
                testHelper.set_csvFileName(null);
                set_csvFileName(null);
            }

            //write headers to the CSV file for each test file so that this can act as a separator between tests or just a header for a singular test file
            testHelper.WriteToFile(testHelper.get_csvFileName(),"File And Step Number,Test Performed,Execution Status,Variable Output,Test File Name");
            //End - reset this for each test file
            testHelper.CreateSectionHeader("[ Running Test Script ]", AppConstants.FRAMED + AppConstants.ANSI_PURPLE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW_BRIGHT, true, true, true);
            testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + "Running Test Script file: " + AppConstants.ANSI_RESET + testFileName, true);
            testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + "CSV Status file: " + AppConstants.ANSI_RESET + (testHelper.get_csvFileName() != null ? testHelper.get_csvFileName() : "N/A"), true);

            for (int x = startIndex; x < testSteps.size(); x++) {
                if (revertToParent) {
                    driver.switchTo().defaultContent();
                    testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                    revertToParent = false;
                }
                TestStep ts = testSteps.get(x);
                String fileStepIndex = "F" + fileIndex + "_S" + x;
                int entireSiteEndCommand = 0;
                if (ts.get_command() != null && ts.get_command().equals(AppCommands.EntireSiteCommandsStart)) {
                    //if the start command has happened, find the end command
                    for (int index=x;index< testSteps.size();index++) {
                        if (testSteps.get(index).get_command().equals(AppCommands.EntireSiteCommandsEnd)) {
                            entireSiteEndCommand = index;
                        }
                    }
                    //this will loop from the start to the end of the all page commands
                    ExecuteEntireSiteCommands(ts, fileIndex, x, entireSiteEndCommand);
                    x = entireSiteEndCommand + 1;
                }
                if (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) {
                    isConditionalBlock = ts.get_isConditionalBlock();
                    testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + AppConstants.iFrameSectionTopLeft + testHelper.PrePostPad("[ Start of Conditional Block ]", "═", 9, 157) + AppConstants.iFrameSectionTopRight + AppConstants.ANSI_RESET, false);
                } else if (ts.get_command().toLowerCase().equals(AppCommands.End_Conditional)) {
                    isConditionalBlock = false;
                    conditionalSuccessful = false;
                    testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End of Conditional Block ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                }

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
                        writeCommands.PerformWriteActions(ts, fileStepIndex);
                    } else {
                        readCommands.PerformReadActions(ts, fileStepIndex);
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

    /****************************************************************************
     *  DESCRIPTION:
     *    Runs all tests read in from all test settings files between the
     *    Entire Site Command Start and and the Entire Site Command End commands.
     *
     **************************************************************************** */
    private void ExecuteEntireSiteCommands(TestStep tsInput, int fileIndex, int xStart, int xEnd) throws Exception {
        testFileName = testFiles.get(fileIndex);
        testHelper.set_testFileName(testFileName);
        readCommands.set_testFileName(testFileName);
        writeCommands.set_testFileName(testFileName);
        boolean revertToParent = false;
        String domainRestriction = null;
        String href = null;
        boolean isConditionalBlock = false;

        //start by getting a list of pages from anchor tag hrefs
        //ArrayList<String> links = GetPageLinks();
        //get the domain or page path to restrict the testing
        domainRestriction = GetArgumentValue(tsInput, 0, null);
        //get the pages that should also be scanned for additional hrefs
        ArrayList<String> scanPages = GetScanPages(tsInput);
        if (!testHelper.IsNullOrEmpty(domainRestriction) && domainRestriction.indexOf("http") > 0) {
            scanPages.add(domainRestriction);
        } else {

        }
        //testHelper.UpdateTestResults (AppConstants.indent8 +  AppConstants.subsectionArrowLeft + "[ Start Adding Links from Pages configured for scan ]" + AppConstants.subsectionArrowRight,true);
        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Adding Links from Pages configured for scan ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        ArrayList<String> links = GetPageLinks(scanPages, fileIndex,0,0,tsInput.get_crucial());
        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Adding Links from Pages configured for scan ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        //testHelper.UpdateTestResults (AppConstants.indent8 +  AppConstants.subsectionArrowLeft + "[ End Adding Links from Pages configured for scan ]" + AppConstants.subsectionArrowRight,true);
        //start with the first command in the list
        for (int pageIndex=0;pageIndex<links.size();pageIndex++) {
            href = links.get(pageIndex); //.getAttribute("href");
            //skip non-domain and in page links
            if (href.indexOf(domainRestriction) > -1 && (href.indexOf("#") <= -1)) {
                testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.iFrameSectionTopLeft + testHelper.PrePostPad("[ Entire Site Check checking URL: " + href + "]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                CreateNavigateTestStepAndNavigate(href, fileIndex, pageIndex, pageIndex, tsInput.get_crucial());

                //links = GetPageLinks(links, scanPages);  //can you increase the size of the array while traversing it?
                for (int x = xStart + 1; x < xEnd; x++) {
                    if (revertToParent) {
                        driver.switchTo().defaultContent();
                        testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End Switch to IFrame - Reverting to defaultContent ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                        revertToParent = false;
                    }
                    TestStep ts = testSteps.get(x);
                    String fileStepIndex = "F" + fileIndex + "_S" + x + "_Iteration:" + pageIndex;
                    if (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) {
                        isConditionalBlock = ts.get_isConditionalBlock();
                        testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + AppConstants.iFrameSectionTopLeft + testHelper.PrePostPad("[ Start of Conditional Block ]", "═", 9, 157) + AppConstants.iFrameSectionTopRight + AppConstants.ANSI_RESET, false);
                    } else if (ts.get_command().toLowerCase().equals(AppCommands.End_Conditional)) {
                        isConditionalBlock = false;
                        conditionalSuccessful = false;
                        testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW_BRIGHT + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End of Conditional Block ]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
                    }

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
                            writeCommands.PerformWriteActions(ts, fileStepIndex);
                        } else {
                            readCommands.PerformReadActions(ts, fileStepIndex);
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
                testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.iFrameSectionBottomLeft + testHelper.PrePostPad("[ End Entire Site Check checking URL:" + href + "]", "═", 9, 157) + AppConstants.iFrameSectionBottomRight + AppConstants.ANSI_RESET, false);
            } else {
                testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.ANSI_BLUE_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Domain ((" + domainRestriction + ") restriction exception: " + href + "]", "═", 9, 157) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, false);
            }
        }
    }

    /****************************************************************************
     *  DESCRIPTION:
     *    Gets a list of the pages to scan for additional links and returns the
     *    list of pages to the calling method.
     *
     **************************************************************************** */
    private ArrayList<String> GetScanPages(TestStep tsInput) {
        ArrayList<String> scanPages = new ArrayList<>();
        String pageUrl = "";
        //testHelper.DebugDisplay("Retrieving Scan Pages");
        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Retrieving Scan Pages ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);

        //scanPages.add(driver.getCurrentUrl());
        for (int x=1;x<50;x++) {
            pageUrl = GetArgumentValue(tsInput, x, null);
            //testHelper.DebugDisplay("#1 Scan Page Url: " + pageUrl);
            if (!testHelper.IsNullOrEmpty(pageUrl)) {
                scanPages.add(pageUrl);
                //testHelper.DebugDisplay("#2 Scan Page Url: " + pageUrl);
            } else {
                break;
            }
        }
        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Retrieving Scan Pages ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        return scanPages;
    }

    /****************************************************************************
     *  DESCRIPTION:
     *    Gets page links from all anchor tags and returns it to the calling
     *    method.
     *
     **************************************************************************** */
    private ArrayList GetPageLinks(ArrayList<String> scanPages, int fileIndex, int pageIndex, int altIndex, Boolean isCrucial) {
        List<WebElement> anchorTags; // = driver.findElements(By.cssSelector("a"));
        ArrayList<String> links = new ArrayList<>();
        Boolean isFound = false;
        String currentUrl = driver.getCurrentUrl();
        scanPages.add(0,currentUrl);
        for (int z=0;z<scanPages.size();z++) {
            testHelper.DebugDisplay("Getting Page links from: " + scanPages.get(z));
            if (!scanPages.get(z).equals(driver.getCurrentUrl())) {
                CreateNavigateTestStepAndNavigate(scanPages.get(z), fileIndex, pageIndex, altIndex, isCrucial);
            }
            anchorTags = driver.findElements(By.cssSelector("a"));
            for (int x = 0; x < anchorTags.size(); x++) {
                isFound = false;
                if (anchorTags.get(x).getAttribute("href").indexOf("#") < 0) {
                    for (int y = 0; y < links.size(); y++) {
                        if (links.get(y).equals(anchorTags.get(x).getAttribute("href"))) {
                            isFound = true;
                            break;
                        }
                    }
                } else {
                    isFound = true;
                }
                if (!isFound) {
                    links.add(anchorTags.get(x).getAttribute("href"));
                    //testHelper.UpdateTestResults ("Adding Link: " + anchorTags.get(x).getAttribute("href"),true);
                }
            }
        }
        return links;
    }

    /****************************************************************************
     *  DESCRIPTION:
     *    Gets all unique page links and returns them in a String Array.
     *
     **************************************************************************** */
    private ArrayList<String> GetPageLinks() {
        List<WebElement> anchorTags = driver.findElements(By.cssSelector("a"));
        ArrayList<String> links = new ArrayList<>();
        Boolean isFound = false;
        for (int x=0;x<anchorTags.size();x++){
            isFound = false;
            for (int y=0;y<links.size();y++){
                if (links.get(y).equals(anchorTags.get(x).getAttribute("href"))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                links.add(anchorTags.get(x).getAttribute("href"));
               // testHelper.DebugDisplay("Adding Link: " + anchorTags.get(x).getAttribute("href"));
            }
        }
        return links;
    }

    /****************************************************************************
     *  DESCRIPTION:
     *    Gets all unique page links that have not been previously captured
     *    and returns them in a String Array.
     *
     **************************************************************************** */
    private ArrayList<String> GetPageLinks(ArrayList<String> pages, ArrayList<String> scanPages) {
        List<WebElement> anchorTags = driver.findElements(By.cssSelector("a"));
        String javaScriptText = "return dataLayer[0].page.category.pageTemplate";
        String pageTemplate = (String) ((JavascriptExecutor) driver).executeScript(javaScriptText);
        ArrayList<String> links = new ArrayList<>();
        Boolean isFound = false;
        //String LinkPages = "plp,pcp,pcnp,rlp,rlcp,rcp,rcnp,alp";
        Boolean isScanPage = false;

        //check if this is configured as a page to scan for additional URLs
        for (int i=0;i<scanPages.size();i++) {
            if (driver.getCurrentUrl().equals(scanPages.get(i))) {
                isScanPage = true;
            }
        }
        //if this is not a scan page, return the list of links that were sent here
        if (!isScanPage) {
            return pages;
        }
        testHelper.UpdateTestResults("Checking for additional URLs to test on: " + driver.getCurrentUrl(), true);
        //only retrieve additional links from pages with the specified pageTemplate values
        for (int x = 0; x < anchorTags.size(); x++) {
            isFound = false;
            //first get a unique list of URLs
            for (int y = 0; y < links.size(); y++) {
                if (links.get(y).equals(anchorTags.get(x).getAttribute("href"))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                links.add(anchorTags.get(x).getAttribute("href"));
                //testHelper.DebugDisplay("#2 Adding Link: " + anchorTags.get(x).getAttribute("href"));
            }
        }
        //compare against list already retrieved
        for (int x = 0; x < links.size(); x++) {
            isFound = false;
            for (int y = 0; y < pages.size(); y++) {
                if (links.get(x).equals(pages.get(y))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                pages.add(links.get(x));
                //testHelper.DebugDisplay("Adding Additional Links: " + anchorTags.get(x).getAttribute("href"));
            }
        }
        return links;
    }

    public void CreateNavigateTestStepAndNavigate(String href, int fileIndex, int stepNum, int pageIndex, Boolean isCrucial) {
        TestStep navStep;
        List<Argument> argumentList;
        Argument argument, argument2;
        String fileStepIndex = "F" + fileIndex + "_S" + stepNum + "_Iteration:" + pageIndex;
        try {
            navStep = new TestStep();
            argumentList = navStep.ArgumentList;
            navStep.set_command("navigate");
            navStep.set_actionType("write");
            navStep.set_crucial(isCrucial);
            //navStep.set_crucial(true);
            navStep.set_expectedValue(href);
            argument = new Argument();
            argument.set_parameter(href);
            argumentList.add(argument);
            argument2 = new Argument();
            argument2.set_parameter("1000");
            argumentList.add(argument2);
            navStep.setArgumentList(argumentList);
            /*for (int x=0;x<argumentList.size();x++) {
                testHelper.DebugDisplay("argumentList.get(" + x + ").get_parameter() = " + argumentList.get(x).get_parameter());
            }*/
            testHelper.UpdateTestResults("Performing Entire Site Navigation to " + href + " for step " + fileStepIndex,true);
            PerformExplicitNavigation(navStep, fileStepIndex);
        } catch(Exception e) {
            testHelper.UpdateTestResults("Failed Entire Site Navigation for href = " + href + " for step " + fileStepIndex,true);
        }

    }


    /************************************************************************************
     *  Description: This method sets the CSV File Name.
     * @param testFileName
     ************************************************************************************/
    private void SetCSVFileName(String testFileName) {
       String csvFileName = testFileName.contains(".xml") ? testFileName.replace(".xml", "_" + logFileUniqueName + ".csv") :
               testFileName.replace(".log", ".csv");
       if (csvFileName.contains("\\")) {
           csvFileName = csvFileName.substring(testFileName.lastIndexOf("\\") + 1);
       } else if (csvFileName.contains("/")) {
           csvFileName =  csvFileName.substring(testFileName.lastIndexOf("/") + 1);
       }
        testHelper.set_csvFileName(configurationFolder + csvFileName);  //added for individual CSV files
        set_csvFileName(configurationFolder + csvFileName);
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
    void CloseOpenConnections() throws SQLException {
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
    void CloseOpenConnections(String databaseConnectionType, String fileStepIndex) throws SQLException {
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
    String GetBrowserUsed() {
        return this.driver.toString().substring(0, this.driver.toString().indexOf(':')) + "_";
    }


    /**************************************************************************************
     * Description: This method is the controller method for Persisting Values.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param valueToPersist - Value that will be stored in the persistedString variable.
     * @param fileStepIndex - the file index and the step index.
     **************************************************************************************/
    void PersistProvidedValueController(TestStep ts, String valueToPersist, String fileStepIndex) {
        persistedString = null;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Sub-command Element Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisting value as part of command:" + ts.get_command() + " found by: " + ts.get_accessorType() + " accessor: " + ts.get_accessor() + " for step " + fileStepIndex, true);
        persistedString = valueToPersist;
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisted value = (" + persistedString + ")", true);
        conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock() && persistedString != null);  // ? true : false;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting Sub-command action, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
    }



    /********************************************************************
     * DESCRIPTION: Returns the URL of the current page.
     * @return - Returns the current driver URL (URL of the current page).
     ********************************************************************/
    String GetCurrentPageUrl() {
        return this.driver.getCurrentUrl();
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
    void PerformExplicitNavigation(TestStep ts, String fileStepIndex) throws Exception {
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
        testHelper.set_fileStepIndex(fileStepIndex);

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


    /*************************************************************
     * DESCRIPTION:
     *      Performs a thread sleep to allow for items to load and
     *      is intended to be used prior to making an assertion
     *      that depends upon some change like a navigation or
     *      new items populating the page.
     * @param milliseconds - time in milliseconds to wait
     * @param fileStepIndex - the file index and the step index.
     ************************************************************ */
    void DelayCheck(int milliseconds, String fileStepIndex) throws InterruptedException {
        testHelper.UpdateTestResults(AppConstants.indent5 + "Sleeping for " + milliseconds + " milliseconds for script " + fileStepIndex, true);
        Thread.sleep(milliseconds);
    }


    /***********************************************************************
     * DESCRIPTION: Switches to a different tab either the child or
     *      the parent tab.  This method is only used with the Right click
     *      context menu if specified as an argument.
     * @param isChild - Is Child tab
     * @param fileStepIndex - the file index and the step index.
     ********************************************************************* */
    void SwitchToTab(boolean isChild, String fileStepIndex) {
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
    void SwitchToTab(TestStep ts, String fileStepIndex) {
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
    void CloseOpenChildTab(TestStep ts, String fileStepIndex) {
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
    void CloseAllOpenChildTabs(String fileStepIndex) {
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
     * DESCRIPTION:
     *      Calls the NavigateToPage method passing the driver
     *      and the destination URL, where a default 10 second
     *      wait happens to allow the page to load
     *      and then returns the current URL
     * @param delayMilliSeconds - time in milliseconds to wait before
     *                          testing url
     ************************************************************ */
    String CheckPageUrl(int delayMilliSeconds) throws Exception {
        //proxy.newHar(testPage);
        testHelper.NavigateToPage(this.driver, testPage, delayMilliSeconds);
        //Har har = proxy.getHar();
        //SaveHarFile(har, testPage);

        if (!isAlertPresent()) {
            return this.driver.getCurrentUrl();
        } else {
            return null;
        }
    }

    private void SaveHarFile(Har har, String sFileName) {

        sFileName = sFileName.replace("/","_").replace(":","_");
        sFileName = harFolder + sFileName + ".txt";
        sFileName = testHelper.GetUnusedFileName(sFileName);


        File harFile = new File(sFileName);
        try {
            har.writeTo(harFile);
        } catch (IOException ex) {
            System.out.println (ex.toString());
            System.out.println("Could not find file " + sFileName);
        }
    }

    //endregion


    //region { Set Driver Methods }
    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the PhantomJs Driver
     **************************************************************************** */
    private void SetPhantomJsDriver() {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "PhantomJSDriver]" + AppConstants.ANSI_RESET , true);
        File src = new File(phantomJsDriverPath);
        //System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
        WebDriverManager.phantomjs().setup();

        driver = new PhantomJSDriver();
        driver.manage().window().maximize(); //added 8-14-2019
        testHelper.set_is_Maximized(true);
    }

    /****************************************************************************
     *  DESCRIPTION: (old phantomJS Driver initialization.)
     *  Sets the WebDriver to the PhantomJs Driver
     **************************************************************************** */
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
        testHelper.set_is_Maximized(true); */
    }

    public BrowserMobProxy getProxyServer() {
        proxy = new BrowserMobProxyServer();
        proxy.setTrustAllServers(true);
        // above line is needed for application with invalid certificates
        proxy.start(9091);
        return proxy;
    }

    public Proxy getSeleniumProxy(BrowserMobProxy proxyServer) throws Exception {
        seleniumProxy = ClientUtil.createSeleniumProxy(proxyServer);
        try {
            String hostIp = Inet4Address.getLocalHost().getHostAddress();
            seleniumProxy.setHttpProxy(hostIp + ":" + proxyServer.getPort());
            seleniumProxy.setSslProxy(hostIp + ":" + proxyServer.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            testHelper.UpdateTestResults("Error in getSeleniumProxy: " + e.getMessage(), true);
            Assert.fail("invalid Host Address");
        }
        return seleniumProxy;
    }

    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Chrome Driver
     *  This method has been updated to use the WebDriverManager so there is
     *  no longer a need to download the ChromeDriver as the WebDriverManager
     *  will automatically do this.
     **************************************************************************** */
    private void SetChromeDriver(){

        try {
            testHelper.UpdateTestResults(AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "ChromeDriver]" + AppConstants.ANSI_RESET, true);
            //System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            proxy = new BrowserMobProxyServer();
            //proxy.start(80);
            DesiredCapabilities capabilities = new DesiredCapabilities();
            seleniumProxy = getSeleniumProxy(getProxyServer());
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
            WebDriverManager.chromedriver().setup();


            if (runHeadless) {
                ChromeOptions options = new ChromeOptions();
                //options.setCapability(CapabilityType.PROXY, proxy);
                options.addArguments("window-size=1400,800");
                options.addArguments("headless");
                options.addArguments("--dns-prefetch-disable");
                options.setCapability(CapabilityType.PROXY, seleniumProxy);
                //options.addArguments("acceptInsecureCerts=true");
                options.setAcceptInsecureCerts(true);
                options.setPageLoadStrategy(PageLoadStrategy.NONE);
                driver = new ChromeDriver(options);

                testHelper.set_is_Maximized(false);
            } else {
                ChromeOptions options = new ChromeOptions();
                options.setCapability(CapabilityType.PROXY, seleniumProxy);
                options.setAcceptInsecureCerts(true);
                //options.setCapability(CapabilityType.PROXY, proxy);
                driver = new ChromeDriver(options);

                //driver = new ChromeDriver();
                driver.manage().window().maximize(); //added 8-14-2019
                testHelper.set_is_Maximized(true);
            }
            proxy.newHar();
        } catch(Exception ex) {
            testHelper.UpdateTestResults("Error Setting ChromeDriver: " + ex.getMessage(), true);
        }
        //proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
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
     *  Sets the WebDriver to the Internet Explorer Driver.
     *  This method has been updated and although it uses the new WebDriverManager
     *  the path to the IEDriverServer is still required or IEDriverServer has to
     *  be added to your path variable.
     *  To work properly, as of 4/1/2020 on Windows 10 use the
     *  IEDriverServer 32 bit vestion 3.150.1 (IEDriverServer_Win32_3.150.1.zip)
     ****************************************************************************/
    private void SetInternetExplorerDriver() {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "InternetExplorerDriver]" + AppConstants.ANSI_RESET , true);

        //testHelper.UpdateTestResults("**********************************************************************************", true);
        testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW + testHelper.PrePostPad("*", "*", 1, 151), true);

        testHelper.UpdateTestResults("IMPORTANT: INTERNET EXPLORER HAS LIMITED IMPLEMENTATION!!!\r\n" +
                "Click functionality required Internet Explorer specific code implementation, but further specific actions for this browser have yet to be implemented.\r\n" +
                "Right Click and Double Click actions have not been implemented and will not likely work until a specific implementation is implemented.\r\n", true);
        //testHelper.UpdateTestResults("**********************************************************************************", true);
        testHelper.UpdateTestResults(testHelper.PrePostPad("*", "*", 1, 151) + AppConstants.ANSI_RESET, true);
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
        capab.setCapability("enableFullPageScreenshot", true);
        driver = new InternetExplorerDriver(capab);
    }

    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the Internet Explorer Driver
     *  Original: This has been commented out because Internet Explorer runs incredibly
     *              slowly when sending text.
     *  Update:  This was meant to replace the older implementation but did not work.
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
    String ElementTypeLookup(String xPath, String accessorType) {
        //NOTE: When checking string equality in Java you must use the "".Equals("") method.
        // Using the == operator checks the memory address not the value
        String elementTag = null;
        if (accessorType.equals(AppConstants.xpathCheckValue)) {
            elementTag = xPath.substring(xPath.lastIndexOf("/") + 1).trim();
        } else if (accessorType.equals(AppConstants.tagNameCheckValue)) {
            elementTag = xPath;
        } else if (accessorType.equals(AppConstants.cssSelectorCheckValue)) {
            int index = xPath.lastIndexOf(" ") > xPath.lastIndexOf(">") ? xPath.lastIndexOf(" ") : xPath.lastIndexOf(">");
            elementTag = xPath.substring(index + 1).trim();
        } else {
            return "Indeterminate";
        }

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
    CharSequence GetKeyValue(String value, String fileStepIndex) {
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
    String GenerateXPath(WebElement childElement, String current) {
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
    String GetArgumentValue(TestStep ts, int index, String defaultValue) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;

        if (arg!=null) {
            return arg.get_parameter();
        } else {
            return defaultValue;
        }
    }

    GtmTag GetGtmArguments(TestStep ts, String defaultValue) {
        GtmTag item = new GtmTag();
        String argument;
        String argumentValue;
        for(int x=0;x< ts.ArgumentList.size();x++) {
            if (!testHelper.IsNullOrEmpty(ts.ArgumentList.get(x).get_parameter())) {
                argument = ts.ArgumentList.get(x).get_parameter().split("=")[0];
                argumentValue = ts.ArgumentList.get(x).get_parameter().split("=")[1];

                if (argument.equals("dl")) {
                    item.set_DocumentLocation(argumentValue);
                } else if (argument.equals("t")) {
                    item.set_HitType(argumentValue);
                } else if (argument.equals("ec")) {
                    item.set_EventCategory(argumentValue);
                } else if (argument.equals("ea")) {
                    item.set_EventAction(argumentValue);
                } else if (argument.equals("el")) {
                    item.set_EventLabel(argumentValue);
                } else if (argument.equals("cg1")) {
                    item.set_ContentGroup1(argumentValue);
                } else if (argument.equals("cg2")) {
                    item.set_ContentGroup2(argumentValue);
                } else if (argument.equals("cg2+")) {
                    item.set_ContentGroup2("+" + argumentValue);
                }
                else if (argument.equals("dt")) {
                    item.set_DocumentTitle(argumentValue);
                }
                else if (argument.equals("tid")) {
                    item.set_TrackingId(argumentValue);
                }
            }
        }
        return item;
    }

    GA4Tag GetGa4Arguments(TestStep ts, String defaultValue) {
        GA4Tag item = new GA4Tag();
        String argument;
        String argumentValue;
        String idFieldName = "";
        GA4Parameter parameter;
        List<GA4Parameter> ga4Parameters = new ArrayList<>();
        for(int x=0;x< ts.ArgumentList.size();x++) {
            if (!testHelper.IsNullOrEmpty(ts.ArgumentList.get(x).get_parameter())) {
                argument = ts.ArgumentList.get(x).get_parameter().split("=")[0];
                argumentValue = ts.ArgumentList.get(x).get_parameter().split("=")[1];
                parameter = new GA4Parameter(argument, argumentValue);
                ga4Parameters.add(parameter);
                if (argument.equals("idfield")) {
                    idFieldName = argument;
                } else if (argument.equals("dl")) {
                    item.set_DocumentLocation(argumentValue);
                } else if (argument.equals("t")) {
                    item.set_HitType(argumentValue);
                } else if (argument.equals("ep.gtm_tag_name")) {
                    item.set_GtmTagName(argumentValue);
                } else if (argument.equals("en")) {
                    item.set_EventName(argumentValue);
                } else if (argument.equals("ep.page_template")) {
                    item.set_PageTemplate(argumentValue);
                } else if (argument.equals("ep.site_section")) {
                    item.set_SiteSection(argumentValue);
                } else if (argument.equals("ep.hit_timestamp")) {
                    item.set_HitTimeStamp(argumentValue);
                } else if (argument.equals("up.jmsa_id")) {
                //else if (argument.equals(idFieldName)) {
                    item.set_IdField(argumentValue);
                    //item.set_IdField("+" + argumentValue);
                }
                else if (argument.equals("dt")) {
                    item.set_DocumentTitle(argumentValue);
                }
                else if (argument.equals("tid")) {
                    item.set_TrackingId(argumentValue);
                }
                else if (argument.equals("ep.product_name")) {
                    item.set_ProductName(argumentValue);
                }
            }
        }
        item.set_GA4Parameters(ga4Parameters);
        return item;
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
    int GetArgumentNumericValue(TestStep ts, int index, int defaultValue) {
        Argument arg  = ts.ArgumentList != null && ts.ArgumentList.size() > index ? ts.ArgumentList.get(index) : null;

        if (arg!=null) {
            return parseInt(arg.get_parameter());
        } else {
            return defaultValue;
        }
    }

    /************************************************************************************
     * Description: This method gets the ArgumentList Parameter String
     *               property based on the index passed in and if it
     *               exists, parses that string into a Double  and
     *               returns it to the calling method.
     *               In the event that the value is null, it returns
     *               the default value passed in.
     * @param ts
     * @param index
     * @param defaultValue
     * @return
     ************************************************************************************/
    double GetArgumentNumericDoubleValue(TestStep ts, int index, int defaultValue) {
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
    Boolean CheckArgumentNumeric(TestStep ts, int index) {
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
    void CheckWaitArgumentOrder(TestStep ts) {
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
    void CheckScreenShotArgumentOrder(TestStep ts) {
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
    void CheckCreateTestFileArgumentOrder(TestStep ts) {
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
    void CheckColorContrastArgumentOrder(TestStep ts) {
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
    void RearrangeArgumentOrder(TestStep ts, String[] items, String command) {
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
    void ArgumentOrderErrorMessage(TestStep ts, String command) {
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



    /*************************************************************
     * DESCRIPTION:
     *      Method reports improperly formatted tests to the
     *      user with the test step so that it can be fixed.
     * @param fileStepIndex - indicates the file and step where
     *                            this command was issued.
     ************************************************************ */
    void ImproperlyFormedTest(String fileStepIndex) {
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
    void AdaApprovedContrastValuesOverriddenMessage(int brightnessStandard, int contrastStandard) {
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

}
