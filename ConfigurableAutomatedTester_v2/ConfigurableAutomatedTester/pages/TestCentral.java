import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
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
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.phantomjs.PhantomJSDriverService.PHANTOMJS_CLI_ARGS;


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
    private final String configurationFile = "Config/ConfigurationSetup.xml";
    final String configurationFolder = "Config/";
    final String harFolder = configurationFolder + "HAR_files/";
    final String consoleLogFolder = configurationFolder + "Console_logs/";
    final String consoleLogCombinedFolder = configurationFolder + "Console_logs/Combined/";
    private final String logFileFolder = "Test_logs/";

    private final String csvFileFolder = "Test_CSV_Files/";
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
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
    private String logFileUniqueName = dateFormat.format(new Date());
    private final String logFileRootFileName = "TestResults_";   //root name of the log file, change this not the logfile name


    private String logFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\" + logFileFolder.replace("/","\\") + logFileRootFileName + logFileUniqueName + ".log" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/" + logFileFolder + logFileRootFileName + logFileUniqueName + ".log";
    private final String helpFileName = configurationFile.contains("\\") ?
            configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\ConfigTester_Help.txt" :
            configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/ConfigTester_Help.txt";

    private String consoleLogFileName =  consoleLogFolder + (logFileRootFileName + logFileUniqueName + ".log").substring(0, (logFileRootFileName + logFileUniqueName + ".log").lastIndexOf(".")-1) + "-Console-log.txt";
    private List<String> testFiles = new ArrayList<>();
    public List<GtmTag> GtmTagList;
    public List<GA4Tag> GA4TagList;
    public String get_testFileName() {return testFileName;}

    public String GetNewLogFileNames() {
        String oldLogFileName = logFileName;
        testHelper.DebugDisplay("Previous Logfile Name: " + logFileName);
        logFileUniqueName = dateFormat.format(new Date());
        logFileName = configurationFile.contains("\\") ?
                configurationFile.substring(0, configurationFile.lastIndexOf("\\")) + "\\" + logFileFolder.replace("/","\\") + logFileRootFileName + logFileUniqueName + ".log" :
                configurationFile.substring(0, configurationFile.lastIndexOf("/")) + "/" + logFileFolder + logFileRootFileName + logFileUniqueName + ".log";
        if (logFileName.equals(oldLogFileName)) {
            logFileUniqueName = GetNewDateTimeStamp(logFileUniqueName);
            /*int characterCode = 97;
            while (logFileName.equals(oldLogFileName)) {
                logFileName = oldLogFileName.replace(".log", (char)(characterCode) + ".log");
                characterCode ++;
            }*/
        }
        testHelper.set_logFileName(logFileName);
        readCommands.testHelper.set_logFileName(logFileName);
        writeCommands.testHelper.set_logFileName(logFileName);
        testHelper.DebugDisplay("New Logfile Name: " + logFileName);
        SetCSVFileName(logFileName);
        testHelper.set_csvFileName(_csvFileName);
        readCommands.testHelper.set_csvFileName(_csvFileName);
        writeCommands.testHelper.set_csvFileName(_csvFileName);
        consoleLogFileName =  consoleLogFolder + (logFileRootFileName + logFileUniqueName + ".log").substring(0, (logFileRootFileName + logFileUniqueName + ".log").lastIndexOf(".")-1) + "-Console-log.txt";
        testHelper.testCentral.consoleLogFileName = consoleLogFileName;
        readCommands.testCentral.consoleLogFileName = consoleLogFileName;
        writeCommands.testCentral.consoleLogFileName = consoleLogFileName;
        return logFileName;
    }

    private String GetNewDateTimeStamp(String logFileUniqueName) {
        String[] timePortion = logFileUniqueName.split("-");
        int hours = parseInt(timePortion[0]);
        int minutes = parseInt(timePortion[1]);
        int seconds = parseInt(timePortion[2]);
        String logFileNewUniqueName = logFileUniqueName;

        if (seconds < 59) {
            seconds++;
        } else if (minutes < 59) {
            seconds = 0;
            minutes++;
        } else {
            hours++;
            minutes = 0;
            seconds = 0;
        }
        logFileNewUniqueName = hours + "-" + minutes + "-" + seconds;


        return logFileNewUniqueName;
    }

    //region { WebDriver Browser Driver Configured Locations }
    private final String phantomJsDriverPath = "C:\\Gary\\Java Utilities\\BrowserDrivers\\phantomjs.exe"; //"/gary/java utilities/BrowserDrivers/phantomjs.exe";
    private final String internetExplorerDriverPath = "/gary/java utilities/BrowserDrivers/IEDriverServer.exe";
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

    List<String> consoleLogEntries;

    String _imageMagicFilePath = null;

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

    void set_imageMagicFilePath(String imageMagicFilePath) {
        this._imageMagicFilePath = imageMagicFilePath;
    }
    public String get_imageMagicFilePath() {
        return this._imageMagicFilePath;
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
    void TearDown() throws Exception {
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



    /***********************************************************
     * Description: This method writes to the default console
     *              log file and is intended to run prior to any
     *              Navigation step, to gather all log information
     *              from the current page before Navigating.
     *              This is not triggerable from the test steps.
     *
     ***********************************************************/
    void WriteLogContent() {
        //use the following URL for testing
        //driver.get("https://testkru.com/TestUrls/TestConsoleLogs");
        try {
            LogEntries entry = driver.manage().logs().get(LogType.BROWSER);
            // Retrieving all logs
            List<LogEntry> logs = entry.getAll();

            if (logs.size() > 0) {
                // Printing details separately
                testHelper.UpdateTestResults("Writing console log information to file.", false);
                for (LogEntry e : logs) {
                    testHelper.UpdateTestResults("Message: " + e.getMessage(), true);
                    testHelper.UpdateTestResults("Level: " + e.getLevel(), true);
                }
                SaveConsoleLogFile(consoleLogFileName, logs, "all", false);
                testHelper.UpdateTestResults("Console log information written file.", false);
            } else if (consoleLogEntries != null && consoleLogEntries.size() > 0) {
                WriteLogContentAlt();
            } else {
                testHelper.UpdateTestResults("No console log information to write!", false);
            }
        } catch (Exception ex) {
            //do nothing if you cannot get logging to work for other browsers
        }
    }

    /****************************************************************
     * Description: This method writes to the default console
     *              log file and is intended to run prior to any
     *              Navigation step, to gather all log information
     *              from the current page before Navigating.
     *              This is not triggerable from the test steps.
     *              This alternate method uses the consoleLogEntries
     *              array, which is populated when an explicit
     *              Save Console Log command is issued, which for
     *              some unknown reason, clears the Console log.
     *              This may not have a complete console log listing.
     ****************************************************************/
    void WriteLogContentAlt() {
        //use the following URL for testing
        //driver.get("https://testkru.com/TestUrls/TestConsoleLogs");
        if (consoleLogEntries.size() > 0) {
            // Printing details separately
            testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + AppConstants.subsectionArrowLeft +  "[ Writing console log information to file ]" + AppConstants.subsectionArrowRight, false);
            for (String e : consoleLogEntries) {
                testHelper.UpdateTestResults("Message: " + e.substring(0,e.indexOf("-")), true);
                testHelper.UpdateTestResults("Level: " + e.substring(e.indexOf("-"),e.length() - e.indexOf("-")), true);
            }
            //SaveFile(consoleLogFileName, logs);
            SaveConsoleLogFileAlt(consoleLogFileName, consoleLogEntries); //, "all", false);
            testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + AppConstants.subsectionArrowLeft +  "[ Console log information written file.", false);
        } else {
            testHelper.UpdateTestResults("No console log information to write!", false);
        }
    }



    /***********************************************************
     * Description: This method either writes to the default console
     *              log file or to an altenately supplied file
     *              and is only triggerable from the test steps.
     * @param ts - Test step initiating this command
     * @param fileStepIndex - File step index
     ***********************************************************/
    public void WriteLogContent(TestStep ts, String fileStepIndex) {

        String fileName = consoleLogFileName;
        String altFileName = GetArgumentValue(ts,0,null);
        String level = GetArgumentValue(ts,1,null);
        Boolean messageOnly = Boolean.parseBoolean(GetArgumentValue(ts,2,"false"));
        //use the following URL for testing
        //driver.get("https://testkru.com/TestUrls/TestConsoleLogs");

        //because by default the console log will be written using the default console log file name
        //a unique name must be used to write to the console log of a test step command
        if (!testHelper.IsNullOrEmpty(altFileName)) {
            fileName = altFileName;
        } else {
            fileName = fileName.replace("-Console-log.txt","-Console-log-alt.txt");
        }

        LogEntries entry = driver.manage().logs().get(LogType.BROWSER);
        // Retrieving all logs
        List<LogEntry> logs = entry.getAll();
        if (consoleLogEntries == null) {
            consoleLogEntries = new ArrayList<>();
        }
        if (logs.size() > 0) {
            // Printing details separately
            for (LogEntry e : logs) {
                testHelper.UpdateTestResults("Message: " + e.getMessage(),true);
                testHelper.UpdateTestResults("Level: " + e.getLevel(),true);
                consoleLogEntries.add(e.getMessage() + "-" + e.getLevel());
            }
            testHelper.UpdateTestResults(AppConstants.indent5 + "Writing to Console log file (" + fileName  + ") for step " + fileStepIndex, true);
            testHelper.UpdateTestResults(AppConstants.indent8 + "Saving console logs(" + logs.size() + ") information to file for step " + fileStepIndex , true);
            SaveConsoleLogFile(fileName, logs, level, messageOnly);
            testHelper.UpdateTestResults(AppConstants.indent8 + "Console log information saved to file for step " + fileStepIndex , true);
        } else {
            testHelper.UpdateTestResults(AppConstants.indent5 + "No console log information to write! for step " + fileStepIndex , true);
        }
    }

    /***********************************************************
     * Description: This method either writes to the default console
     *              log file or to an altenately supplied file
     *              and is only triggerable from the test steps.
     * @param fileName - name of the file where the console log
     *                 will be written.
     * @param logs - List of Console Log Entries
     * @param level - Log level to limit file contents
     * @param messageOnly - true if only message should be written
     *                    false to include level too.
     ***********************************************************/
    private void SaveConsoleLogFile(String fileName, List<LogEntry> logs, String level, Boolean messageOnly) {
        String sFileName = testHelper.GetUnusedFileName(fileName);
        String message;

        //clean up the console log message removing backslash and unintentional double quotes
        try {
            // Printing details separately
            for (LogEntry e : logs) {
                message = e.getMessage().replace("\\","");
                message = message.replace("\"\"","\"");

                while (message.indexOf(",\",") > 0) {
                    message = message.replace(",\",",",\"\",");
                }
                if (!messageOnly) {
                    if (testHelper.IsNullOrEmpty(level) || level.toLowerCase().equals(e.getLevel().toString().toLowerCase()) || level.toLowerCase().equals("all")) {
                        testHelper.WriteToFile(sFileName, "Message: " + message + " - Level:" + e.getLevel());
                    }
                } else {
                    testHelper.WriteToFile(sFileName, "Message: " + message);
                }
            }
        } catch (Exception ex) {
            System.out.println (ex.toString());
            System.out.println("Could not find file " + sFileName);
        }
    }

    /****************************************************************
     * Description: Alternate Console Log Save Method, written so that
     *              when log is cleared by explicit save console log
     *              command, the log can be saved prior to page navigation
     *              and prior to application end
     * @param consoleLogFileName - name of the file being saved
     * @param consoleLogEntries - log entries saved from explicit saves
     *
     **********************************************************************/
    private void SaveConsoleLogFileAlt(String consoleLogFileName, List<String> consoleLogEntries) {
        //, String all, boolean messageOnly) {
        String sFileName = testHelper.GetUnusedFileName(consoleLogFileName);
        String message;
        String level;

        try {
            // Printing details separately
            testHelper.WriteToFile(sFileName, "Page URL: " + driver.getCurrentUrl());
            for (String e : consoleLogEntries) {
                message = e.substring(0,e.indexOf("-"));
                message = message.replace("\\", "");
                message = message.replace("\"\"", "\"");
                level = e.substring(e.indexOf("-"),e.length() - e.indexOf("-"));
                while (message.indexOf(",\",") > 0) {
                    message = message.replace(",\",", ",\"\",");
                }
                testHelper.WriteToFile(sFileName, "Message: " + message + " - Level:" + level);
            }
        } catch (Exception ex) {
            System.out.println (ex.toString());
            System.out.println("Could not find file " + sFileName);
        }
    }

    public void CombineLogContent(TestStep ts, String fileStepIndex) {
        String rootFileName = !testHelper.IsNullOrEmpty(GetSpecificArgumentValue(ts, "root file name", "=",null)) ?
                GetSpecificArgumentValue(ts, "root file name", "=",null) : GetLastModifiedFileName(consoleLogFolder);
        if ( rootFileName.indexOf("log.txt") < 0) {
            rootFileName = rootFileName.substring(0, rootFileName.indexOf("log") + 3) + rootFileName.substring(rootFileName.indexOf(".txt"));
        }
        String combinedFileName = rootFileName.replace("log.txt", "log-combined.txt");
        Boolean excludeLevel = false;
        Boolean excludeConsoleApi = false;
        String excludePhrases = "";
        List<String> excludePhraseList;
        String includePhrase;
        long maxFiles;
        String fileName;
        File logFile;
        Scanner logReader;
        String fileContents = "";
        String lineContent = "";
        File tmp = new File(consoleLogFileName);
        String configFullPath = tmp.getAbsolutePath();
        String removeAdditionalHeaders;
        String temp = "";
        int lengthIndicator;
        String alternateFileName;
        String tempFileName;

        configFullPath = configFullPath.substring(0, configFullPath.lastIndexOf("\\"));
        configFullPath = configFullPath.endsWith("/") ? configFullPath : configFullPath + "\\";

        lengthIndicator = 0;
        if (rootFileName.length() > 0 ) {

            excludeLevel = Boolean.parseBoolean(GetSpecificArgumentValue(ts, "exclude level", "=", "false"));
            excludeConsoleApi = Boolean.parseBoolean(GetSpecificArgumentValue(ts, "exclude console-api", "=", "false"));
            excludePhrases = GetSpecificArgumentValue(ts, "exclude=", "=",null);
            excludePhraseList = Arrays.asList(excludePhrases.split(","));
            includePhrase = GetSpecificArgumentValue(ts, "include start", "=", null);
            removeAdditionalHeaders = GetSpecificArgumentValue(ts, "remove additional headers", "=",null);
            lengthIndicator = GetSpecificArgumentValue(ts, "length break indicator", "=","0").length() > 0 ? Integer.parseInt(GetSpecificArgumentValue(ts, "length break indicator", "=","0")) : 0;
            alternateFileName = GetSpecificArgumentValue(ts, "alternate file name", "=", null);
            tempFileName = !testHelper.IsNullOrEmpty(alternateFileName) ? alternateFileName : combinedFileName;
            //next, find the max file number to append
            maxFiles = GetNumberOfFilesInFolder(consoleLogFolder, rootFileName);
            testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.indent5 + AppConstants.subsectionArrowLeft + " [ Combining " + maxFiles + " Console log files using root name (" + rootFileName  + ") \n" + AppConstants.indent8 + "into (" + tempFileName + ") for step " + fileStepIndex + " ] "+ AppConstants.subsectionArrowRight, true);
            //read all files in the folder
            for (int x = 0; x <= maxFiles; x++) {
                if (x == 0) {
                    fileName = rootFileName;
                } else {
                    fileName = rootFileName.replace("log.txt", "log" + x + ".txt");
                }
                try {
                    logFile = new File(configFullPath + fileName);
                    if (logFile.exists()) {
                        logReader = new Scanner(logFile);
                        while (logReader.hasNextLine()) {
                            lineContent = logReader.nextLine();
                            //remove level, if configured
                            //testHelper.DebugDisplay("Before Exclude level excludeLevel = " + excludeLevel);
                            if (excludeLevel) {
                                if (lineContent.indexOf("Level:INFO") > -1) {
                                    lineContent = lineContent.replace(";\" - Level:INFO", ";");
                                    lineContent = lineContent.replace("\" - Level:INFO", ";");
                                } else if (lineContent.indexOf("Level:WARN") > -1) {
                                    lineContent = lineContent.replace(";\" - Level:WARN", ";");
                                    lineContent = lineContent.replace("\" - Level:WARN", ";");
                                } else if (lineContent.indexOf("Level:ERROR") > -1) {
                                    lineContent = lineContent.replace(";\" - Level:ERROR", ";");
                                    lineContent = lineContent.replace("\" - Level:ERROR", ";");
                                } else if (lineContent.indexOf("Level:DEBUG") > -1) {
                                    lineContent = lineContent.replace(";\" - Level:DEBUG", ";");
                                    lineContent = lineContent.replace("\" - Level:DEBUG", ";");
                                }
                            }

                            //remove console-api or -api prefix if configured
                            if (excludeConsoleApi) {
                                int start = lineContent.toLowerCase().indexOf("-api ");
                                if (start > -1) {
                                    start = start + "-api ".length();
                                    start = lineContent.toLowerCase().indexOf(" ", start);
                                    lineContent = lineContent.substring(start);
                                }
                            }
                            //remove all excluded phrases
                            for (int i = 0; i < excludePhraseList.size(); i++) {
                                lineContent = lineContent.replace(excludePhraseList.get(i), "");
                            }

                            //start each line with the included phrase, removing everything prior to that phrase
                            if (includePhrase.length() > 0) {
                                int start = lineContent.indexOf(includePhrase);
                                if (start > -1) {
                                    lineContent = lineContent.substring(start);
                                } else {
                                    lineContent = "";
                                }
                            }

                            //remove subsequent headers if header end delimiter configured
                            if (fileContents.length() > 0 && !testHelper.IsNullOrEmpty(removeAdditionalHeaders) && lineContent.trim().length() > 0) {
                                if (lineContent.indexOf(removeAdditionalHeaders) > -1) {
                                    lineContent = lineContent.substring(lineContent.indexOf(removeAdditionalHeaders) + removeAdditionalHeaders.length());
                                }
                            }

                            if (lineContent.trim().length() > 0) {
                                if (lengthIndicator > 0 && temp.length() + lineContent.length() > lengthIndicator) {
                                    fileContents += "-----------------------------------\r\n";
                                    temp = "";
                                }
                                temp += lineContent + "\r\n";
                                fileContents += lineContent + "\r\n";
                            }
                        }
                        logReader.close();
                    }
                } catch (Exception e) {
                    testHelper.DebugDisplay("Error: " + e.getMessage());
                }
            }
            testHelper.DeleteFile(consoleLogCombinedFolder + tempFileName);
            testHelper.WriteToFile(consoleLogCombinedFolder + tempFileName, fileContents);

            testHelper.UpdateTestResults(AppConstants.ANSI_CYAN + AppConstants.indent5 + AppConstants.subsectionArrowLeft + " [ Combined Console log files saved into (" + tempFileName + ") for step " + fileStepIndex + " ] " + AppConstants.subsectionArrowRight, true);
        }
    }


    public String GetLastModifiedFileName(String directoryFilePath)
    {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        File chosenFile = null;
        String fileNamePrefix = "TestResults_";

        if (files != null)
        {
            for (File file : files)
            {
                if (file.lastModified() > lastModifiedTime) // && file.getName().startsWith(fileNamePrefix))
                {
                    chosenFile = file;
                    lastModifiedTime = file.lastModified();
                }
            }
        }
        if (chosenFile.getName() != null) {
            return chosenFile.getName();
        } else {
            return consoleLogFileName;
        }
    }

    /*****************************************************************
     * Description: Gets Named arguments from Test Steps, if value present
     *              else, returns default value passed in.
     * @param ts
     * @param searchPhrase
     * @param valueDelimiter
     * @param defaultValue
     * @return
     *****************************************************************/
    String GetSpecificArgumentValue(TestStep ts, String searchPhrase, String valueDelimiter, String defaultValue) {
        String temp;
        String returnValue = defaultValue;
        //testHelper.DebugDisplay("searchPhrase = " + searchPhrase + "  valueDelimiter = " + valueDelimiter + " defaultValue = " + defaultValue);
        for (int x=0;x<ts.ArgumentList.size();x++) {
            temp = GetArgumentValue(ts, x, null);
            //testHelper.DebugDisplay("temp = " + temp);
            if (!testHelper.IsNullOrEmpty(temp)) {
                if (temp.indexOf(searchPhrase) > -1) {
                    if (testHelper.IsNullOrEmpty(valueDelimiter)) {
                        returnValue = temp;
                        //returnValue = defaultValue;
                        break;
                    } else if (temp.indexOf(valueDelimiter) < 0) {
                        returnValue = defaultValue;
                        break;
                    } else {
                        returnValue = temp.substring(temp.indexOf(valueDelimiter) + 1);
                        break;
                    }
                }
            }
        }
        return returnValue;
    }

    long GetNumberOfFilesInFolder(String folderName) {
        long maxFiles = 10000;

        try (Stream<Path> files = Files.list(Paths.get(folderName))) {
            maxFiles = files.count();
        } catch (Exception e) {
            maxFiles = 10000;
        }
        return maxFiles;
    }

    /********************************************************************
     * Description: Gets the number of files in the folder that match the
     *              rootFileName passed in and ignores all other files.
     * @param folderName - folder to search
     * @param rootFileName - root file name that must be included in the
     *                     fileName to be counted.
     * @return - Returns the number of files that match the criteria.
     *********************************************************************/
    long GetNumberOfFilesInFolder(String folderName, String rootFileName) {
        long maxFiles = 10000;
        String fileRoot = rootFileName.substring(0,rootFileName.lastIndexOf(".")-1);
        if (fileRoot.length() <= 0) {
            return GetNumberOfFilesInFolder(folderName);
        }

        File directory = new File(folderName);
        if (!(directory.exists() && directory.isDirectory()))
        {
            System.out.println(String.format("Directory %s does not exist", directory));
            return 0;
        }
        FileFilter logFilefilter = new FileFilter() {
            public boolean accept(File file) {
                if (file.getName().startsWith(fileRoot)) {
                    return true;
                }
                return false;
            }
        };
        File[] files = directory.listFiles(logFilefilter);
        maxFiles = files.length;

        return maxFiles;
    }


    /**************************************************************
     *  Description: This method writes the HAR content to a file.
     *               Currently, this has been called from the TearDown
     *               but may be moved at a later time
     ***************************************************************/
    private void WriteHarContent() {

        try {
            Har har = proxy.getHar();
            testHelper.UpdateTestResults("Start Writing HAR file for end of test!", true);
            SaveHarFile(har, testPage);

            /*
            List<HarEntry> entries = proxy.getHar().getLog().getEntries();

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
            testHelper.UpdateTestResults(AppConstants.subsectionArrowLeft + "Error writing HAR file: " + ex.getMessage() + AppConstants.subsectionArrowRight, false);
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
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Log File Folder = " + AppConstants.ANSI_RESET  + logFileFolder, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Log File Name = " + AppConstants.ANSI_RESET  + logFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "CSV File Folder = " + AppConstants.ANSI_RESET  + csvFileFolder, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "CSV File Name = " + AppConstants.ANSI_RESET  + get_csvFileName(), false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Console log Folder = " + AppConstants.ANSI_RESET  + consoleLogFolder, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Console log File Name = " + AppConstants.ANSI_RESET  + consoleLogFileName, false);

        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Help File Name = " + AppConstants.ANSI_RESET + helpFileName, false);
        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Har File Folder = " + AppConstants.ANSI_RESET + harFolder, false);

        testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.indent5 +  "Default Console Log File Name = " + AppConstants.ANSI_RESET + consoleLogFileName, false);
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
                try {
                    SetPhantomJsDriver();
                } catch(Exception e) {
                    testHelper.DebugDisplay(e.getCause() + " " + e.getMessage());
                }
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
            set_imageMagicFilePath(configSettings.get_imageMagickPath());
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
        String originalLogFileName = logFileName;

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
            //for individual log files when running multiple tests, but need a configuration setting for this
            if (fileIndex > 0) {
                //if (testFiles.size() >= (fileIndex + 2)) {
                if (testFiles.size() > 1) {
                    WriteLogContent();  //write log content before changing log file names
                    GetNewLogFileNames();
                    if (createCSVStatusFiles.equals("many")) {
                        SetCSVFileName(logFileName);
                    }
                }
            }

            if (this.createCSVStatusFiles.equals("many")) {
                SetCSVFileName(testFileName);  //added for individual CSV files
            } else if (this.createCSVStatusFiles.equals("one") && testHelper.get_csvFileName() == null) {
                //SetCSVFileName(logFileName);
                SetCSVFileName(originalLogFileName);
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
            testHelper.DebugDisplay("testFiles.size() = " + testFiles.size() + " and fileIndex = " + fileIndex);
        }
        WriteLogContent();
        if (is_executedFromMain()) {
            TearDown();
        }
    }

    /****************************************************************************
     *  DESCRIPTION:
     *    Runs all tests read in from all test settings files between the
     *    Entire Site Command Start and the Entire Site Command End commands.
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
        int waitTime = 1000;
        Boolean isCrucial = tsInput.get_crucial() != null ? tsInput.get_crucial() : false;
        String oldUrl = "";

        //get the domain or page path to restrict the testing
        domainRestriction = GetArgumentValue(tsInput, 0, null);
        //get the pages that should also be scanned for additional hrefs
        ArrayList<String> scanPages = GetScanPages(tsInput);
        //if domain restriction is a domain URL, add that to the pages to scan
        if (!testHelper.IsNullOrEmpty(domainRestriction) && domainRestriction.indexOf("http") > -1) {
            //scanPages.add(domainRestriction);
            scanPages.add(0,domainRestriction);
        }

        //Get Page links from all pages configured for scanning
        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Adding Links from Pages configured for scan ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        ArrayList<String> links = GetPageLinks(scanPages, fileIndex,0,0, isCrucial, domainRestriction);
        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Adding Links from Pages configured for scan ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);

        //start with the first command in the list
        for (int pageIndex=0;pageIndex<links.size();pageIndex++) {
            href = links.get(pageIndex).trim();
            //testHelper.DebugDisplay("href = " + href + "\r\noldUrl = " + oldUrl);
            if (href != oldUrl) {
                //testHelper.DebugDisplay("In the if href = " + href + "\r\noldUrl = " + oldUrl);
                oldUrl = href;
                isCrucial = tsInput.get_crucial() != null ? tsInput.get_crucial() : false;
                //skip non-domain and in page links
                if (href.indexOf(domainRestriction) > -1 && (href.indexOf("#") <= -1)) {
                    testHelper.UpdateTestResults(AppConstants.ANSI_BLUE_BRIGHT + AppConstants.iFrameSectionTopLeft + testHelper.PrePostPad("[ Entire Site Check checking URL: " + href + "]", "═", 9, 157) + AppConstants.iFrameSectionTopRight + AppConstants.ANSI_RESET, false);
                    CreateNavigateTestStepAndNavigate(href, fileIndex, pageIndex, pageIndex, isCrucial, waitTime);
                    //links = GetPageLinks(links, scanPages);  //you can increase the size of the array while traversing it?
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

                        if ((isConditionalBlock && (conditionalSuccessful || (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()))) || (!isConditionalBlock && !conditionalSuccessful)) {
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
                    testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_BLUE_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Domain ((" + domainRestriction + ") restriction exception: " + href + "]", "═", 9, 157) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, false);
                }
            }
        }
    }

    /*********************************************************************
     * Description: Spiders the site retrieving a list of Page URLs from all
     *              anchor tags.
     * @param ts    - Test Step Object
     * @param fileStepIndex - File Step Index
     **********************************************************************/
    public void SpiderSite(TestStep ts, String fileStepIndex) {
        String fileName = GetArgumentValue(ts, 0, null);
        //get the domain or page path to restrict the testing
        String domainRestriction = GetArgumentValue(ts, 1, null);
        //get the sitemap page or page with most links
        String siteMapPage = GetArgumentValue(ts, 2, null);
        String htmlPage;
        String filteredPages = "";
        int fileIndex;
        ArrayList<String> links = null;
        TestStep tsTmp;
        int start = 0, end = 0, hrefStart = 0, hrefEnd = 0;
        String link;
        Boolean isFound;
        int waitTime = 500;
        String invalidLinks = ".gif,.woff,.ttf,.jpg,.js,.png,.ico,.mx,.jpeg,.css,#,mailto,file://,tel:,javascript:";
        String[] invalidLinkList = invalidLinks.split(",");
        Boolean isValid;
        int hrefSpaceIndex;

        //testHelper.DebugDisplay("domainRestriction = " + domainRestriction);
        testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Spidering Site ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        testHelper.DeleteFile(fileName);
        if (!testHelper.IsNullOrEmpty(fileName)) {
            try {
                fileIndex = parseInt(fileStepIndex.substring(1, fileStepIndex.indexOf("_")));
                CreateNavigateTestStepAndNavigate(domainRestriction, fileIndex, 0, 0, ts.get_crucial(), waitTime);
                links = GetPageLinks(links, domainRestriction);
                if (!testHelper.IsNullOrEmpty(siteMapPage)) {
                    isFound = CheckLinkIsUnique(links, siteMapPage, domainRestriction);
                    if (!isFound) {
                        links.add(siteMapPage);
                    }
                }

                links = GetUrlDecodedScanPages(ts, links, domainRestriction, 3);

                if (links != null && links.size() > 0) {
                    for (int x = 0; x < links.size(); x++) {
                        fileIndex = fileIndex + 1;
                        tsTmp = new TestStep();
                        tsTmp.set_command("spyder");
                        tsTmp.ArgumentList.add(new Argument(links.get(x)));
                        try {
                            htmlPage = readCommands.GetHttpResponse(tsTmp, fileStepIndex);
                        } catch (SSLException sslException) {
                            //if there is an ssl exception, skip that page and get the next one
                            continue;
                        } catch(ConnectException connectException) {
                            continue;
                        } catch (Exception ex) {
                            continue;
                        }
                        while (htmlPage.indexOf("<a") > -1) {
                            htmlPage = htmlPage.substring(htmlPage.indexOf("<a") - 1);
                            start = htmlPage.indexOf("<a");
                            end = htmlPage.indexOf(">", start);
                            hrefStart = htmlPage.indexOf("href=\"", start);
                            if (hrefStart > -1) {
                                hrefEnd = htmlPage.indexOf("\"", hrefStart + "href=\"".length() + 1);
                                //if the end parenthesis is missed and a blank space is found, use the blank space as the end position
                                hrefSpaceIndex = htmlPage.indexOf(" ", hrefStart + "href=\"".length() + 1);
                                if (hrefSpaceIndex > hrefStart && hrefSpaceIndex < hrefEnd) {
                                    hrefEnd = hrefSpaceIndex;
                                }
                                if (hrefEnd > -1 && hrefEnd > hrefStart) {
                                    hrefStart += "href=\"".length();// + 1;
                                    if (hrefEnd > hrefStart) {
                                        link = GetFullyFormedAnchorLink(htmlPage, hrefStart, hrefEnd, domainRestriction);
                                        if (link.length() > 0) {
                                            //ensure link is unique and not a duplicate
                                            isFound = CheckLinkIsUnique(links, link, domainRestriction);
                                            isValid = CheckLinkIsValid(link, invalidLinkList);
                                            //ensure domain restriction for any potential links
                                            if (!isFound && isValid && link.length() > 0) {
                                                links.add(link.trim());
                                            }
                                        }
                                    }
                                }
                            }
                            //remove the part of the string up to
                            if (hrefEnd > hrefStart) {
                                htmlPage = htmlPage.substring(hrefEnd);
                                //testHelper.DebugDisplay("Removing first part of string - ideal");
                            } else if (hrefStart > end) {
                                htmlPage = htmlPage.substring(hrefStart);
                                //testHelper.DebugDisplay("Removing first part of string - second best");
                            } else if (end > start) {
                                htmlPage = htmlPage.substring(end);
                                //testHelper.DebugDisplay("Removing first part of string - third best");
                            } else {
                                htmlPage = htmlPage.substring(start + 5);
                                //testHelper.DebugDisplay("Removing first part of string - worst");
                            }
                        }
                    }
                }

                int urlCount = 0;
                for (int i = 0; i < links.size(); i++) {
                    isValid = CheckLinkIsValid(links.get(i), invalidLinkList);
                    if (isValid && links.get(i).startsWith(domainRestriction)) {
                        filteredPages += links.get(i) + "\r\n";
                        urlCount = urlCount + 1;
                    }
                }
                testHelper.DeleteFile(fileName);
                testHelper.WriteToFile(fileName, filteredPages);
                testHelper.UpdateTestResults("Successful Spider Crawl! " + urlCount +  " URLs saved to " + fileName + " for step " + fileStepIndex, true );
            } catch (Exception e) {
                testHelper.DebugDisplay("Error: " + e.getMessage() + " - " + e.fillInStackTrace());
                testHelper.UpdateTestResults("Failure, Spider Crawl error " + e.getMessage() + " for step " + fileStepIndex, true );
            }
        }
        testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Spidering Site ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
    }

    /*************************************************************************************
     * Description: Gets a list of links, decodes them, checks to ensure that each is
     *              unique and calls another method to ensure that the page complies with
     *              the domain or Page Path restriction based on the test file.
     * @param ts
     * @param links
     * @param domainRestriction
     * @param startIndex
     * @return
     **************************************************************************************/
    private ArrayList<String> GetUrlDecodedScanPages(TestStep ts,  ArrayList<String> links, String domainRestriction, int startIndex) {
        String additionalLinkPage;
        Boolean isFound;

        for (int i = startIndex; i < ts.ArgumentList.size(); i++) {
            additionalLinkPage = GetArgumentValue(ts, i, null);
            try {
                //additionalLinkPage = java.net.URLDecoder.decode(additionalLinkPage, StandardCharsets.UTF_8.name());
                additionalLinkPage = UrlDecode(additionalLinkPage);
            } catch (Exception ex) {
                additionalLinkPage = additionalLinkPage.replace("&amp;","&");
            }

            isFound = CheckLinkIsUnique(links, additionalLinkPage, domainRestriction);
            if (!isFound) {
                links.add(additionalLinkPage);
            }
        }
        return links;
    }

    /*******************************************************************
     * Description: Checks that the link is valid by comparing the link
     *              against a list of imvalid links basically ensuring
     *              that non-page links are eliminated.
     * @param link
     * @param invalidLinkList
     * @return
     *******************************************************************/
    private Boolean CheckLinkIsValid(String link, String[] invalidLinkList) {
        //checks to ensure that no non-page links are listed
        Boolean isValid = true;
        for (int i=0;i<invalidLinkList.length;i++) {
            if (link.indexOf(invalidLinkList[i]) > -1) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    /***********************************************************
     * Description: URL Decodes the value passed in and returns
     *              the decoded value to the calling method.
     *              In the event of an Unsupported Exception, it
     *              replaces the encoded ampersand (&amp;) with
     *              the unencoded ampersand (&)
     * @param urlValue
     * @return
     ***************************************************************/
    String UrlDecode(String urlValue) {
       String returnValue = urlValue;
       try {
           returnValue = java.net.URLDecoder.decode(urlValue, StandardCharsets.UTF_8.name());
       } catch (UnsupportedEncodingException unsupportedEncodingException) {
           returnValue  = returnValue.replace("&amp;","&");
       } catch (Exception ex) {
           returnValue = urlValue;
       }
       return returnValue;
    }

    private Boolean CheckLinkIsUnique(ArrayList<String> links, String link, String domainRestriction) {
        //checks to ensure that all links are unique
        Boolean isFound = false;

        //ensure domain restriction for any potential links
        if (link.length() > 0 && link.startsWith(domainRestriction)) {
            //check links to ensure unique
            if (links.size() > 0) {
                for (int i = 0; i < links.size(); i++) {
                    if (links.get(i).trim().equals(link.trim())) {
                        isFound = true;
                        break;
                    }
                }
            }
        }
        return isFound;
    }

    private String GetFullyFormedAnchorLink(String htmlPage, int hrefStart, int hrefEnd, String domainRestriction) {
        String link;
        link = htmlPage.substring(hrefStart, hrefEnd).trim();
        link = link.replace("\"","");
        //testHelper.DebugDisplay("#1 link = " + link);
        if (link.indexOf("http") < 0) {
            if (link.length() > 0) {
                if (link.startsWith("/")) {
                    link = (domainRestriction + link.substring(1)).trim();
                } else {
                    link = (domainRestriction + link).trim();
                }
            }
        } else if (!link.startsWith(domainRestriction)) {
            link = "";
        }
        return link;
    }

    public void SpiderSite_old(TestStep ts, String fileStepIndex) {
        //get the output file name
        String fileName = GetArgumentValue(ts, 0, null);
        //get the domain or page path to restrict the testing
        String domainRestriction = GetArgumentValue(ts, 1, null);
        //get the sitemap page or page with most links
        String siteMapPage = GetArgumentValue(ts, 2, null);
        int fileIndex;
        int waitTime = 100;
        String filteredPages = "";

        testHelper.DeleteFile(fileName);
        if (!testHelper.IsNullOrEmpty(fileName)) {

            try {
                fileIndex = parseInt(fileStepIndex.substring(1, fileStepIndex.indexOf("_")));

                domainRestriction = GetArgumentValue(ts, 1, null);
                //get the pages that should also be scanned for additional hrefs including the domain restriction
                //ArrayList<String> scanPages = GetScanPages(ts);

                String href;
                ArrayList<String> newLinks;

                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Spidering Site ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
                //go to the domain restriction page configured
                CreateNavigateTestStepAndNavigate(domainRestriction, fileIndex, 0, 0, ts.get_crucial(), waitTime);
                //get initial list of links
                ArrayList<String> links = null;
                links = GetPageLinks(links, domainRestriction);
                int oldSize = links.size();
                int diff;
                int linkSize = links.size();
                //testHelper.DebugDisplay("#1 Links retrieved = " + links.size());
                CreateNavigateTestStepAndNavigate(siteMapPage, fileIndex, 0, 0, ts.get_crucial(), waitTime);
                links = GetPageLinks(links, domainRestriction);
                //testHelper.DebugDisplay("#2 Links retrieved = " + links.size());
                //for (int pageIndex=0;pageIndex<links.size();pageIndex++) {
                for (int pageIndex = 0; pageIndex < linkSize; pageIndex++) {
                    href = links.get(pageIndex);
                    if (href.indexOf(domainRestriction) > -1) {
                        CreateNavigateTestStepAndNavigate(href, fileIndex, pageIndex, pageIndex, ts.get_crucial(), waitTime);
                        links = GetPageLinks(links, domainRestriction);  //can you increase the size of the array while traversing it?
                        diff = oldSize > links.size() ? oldSize - links.size() : links.size() - oldSize;
                        //testHelper.DebugDisplay("#" + pageIndex + 3 + " Links retrieved = " + diff);
                        oldSize = diff;
                    }
                }

                for (String link : links) {
                    if (!testHelper.IsNullOrEmpty(domainRestriction) && !testHelper.IsNullOrEmpty(link) && link.indexOf(domainRestriction) > -1) {
                        filteredPages += link + "\r\n";
                    } else if (testHelper.IsNullOrEmpty(domainRestriction)) {
                        filteredPages += link + "\r\n";
                    }
                }
                testHelper.WriteToFile(fileName, filteredPages);
                filteredPages = "-------------------------------------";
            } catch(Exception e) {
                testHelper.DebugDisplay("Error: " + e.getMessage() + " - " + e.fillInStackTrace());
            } finally {
                testHelper.DeleteFile(fileName);
                testHelper.WriteToFile(fileName, filteredPages);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End Spidering Site ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
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
        testHelper.UpdateTestResults( AppConstants.indent8 + AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Retrieving Scan Pages ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);

        for (int x=1;x<tsInput.ArgumentList.size();x++) {
            pageUrl = GetArgumentValue(tsInput, x, null);

            if (!testHelper.IsNullOrEmpty(pageUrl)) {
                scanPages.add(pageUrl);
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

    private ArrayList GetPageLinks(ArrayList<String> scanPages, int fileIndex, int pageIndex, int altIndex, Boolean isCrucial, String domainRestriction) {
        List<WebElement> anchorTags;
        ArrayList<String> links = new ArrayList<>();
        Boolean isFound = false;
        String currentUrl = driver.getCurrentUrl();
        scanPages.add(0,currentUrl);
        int waitTime = 1000;
        Boolean isValid = false;
        String anchorURL = "";

        for (int z=0;z<scanPages.size();z++) {
            if (!scanPages.get(z).equals(driver.getCurrentUrl())) {
                CreateNavigateTestStepAndNavigate(scanPages.get(z), fileIndex, pageIndex, altIndex, isCrucial,waitTime);
            }
            anchorTags = driver.findElements(By.cssSelector("a"));
            for (int x = 0; x < anchorTags.size(); x++) {
                isFound = false;
                isValid = false;
                anchorURL = anchorTags.get(x).getAttribute("href");
                if (!testHelper.IsNullOrEmpty(anchorURL) &&  anchorURL.indexOf("#") < 0) {
                    for (int y = 0; y < links.size(); y++) {
                        if (links.get(y).equals(anchorURL)) {
                            isFound = true;
                            break;
                        }
                    }
                } else {
                    isFound = true;
                }
                if (!testHelper.IsNullOrEmpty(anchorURL) && domainRestriction.length() > 0) {
                    if (domainRestriction.startsWith("http")) {
                        if (anchorURL.startsWith(domainRestriction)) {
                            isValid = true;
                        }
                    } else {
                        if (anchorURL.indexOf(domainRestriction) > -1) {
                            isValid = true;
                        }
                    }
                } else if (!testHelper.IsNullOrEmpty(anchorURL)) {
                    //if there is no domain restriction all links are valid
                    isValid = true;
                }

                if (!isFound && isValid) {
                    links.add(anchorTags.get(x).getAttribute("href"));
                }
            }
        }
        return links;
    }


    /******************************************************************************************
     * Description: Retrieves all href values on the page with the exception of the
     *              invalidLinks list items to eliminate image links.
     * @param pages - Array of Pages where href values will be retrieved.
     * @param domainRestriction - a string that limits the href values to just those that contain
     *                            the value passed in.
     * @return - returns a list of href values (Links)
     *****************************************************************************************/
    private ArrayList<String> GetPageLinks(ArrayList<String> pages, String domainRestriction) {
        List<WebElement> anchorTags = driver.findElements(By.cssSelector("a"));
        Boolean isFound = false;
        String invalidLinks = ".gif,.woff,.ttf,.jpg,.js,.png,.ico,.mx,.jpeg,.css,#";
        String[] invalidLinkList = invalidLinks.split(",");
        Boolean isValid;

        if (pages != null) {
            //check that each link is not already in the arraylist passed in
            for (int x = 0; x < anchorTags.size(); x++) {
                isFound = CheckLinkIsUnique(pages,anchorTags.get(x).getAttribute("href"),domainRestriction);
                isValid = CheckLinkIsValid(anchorTags.get(x).getAttribute("href"),invalidLinkList);
                if (!isFound && isValid) {
                    pages.add(anchorTags.get(x).getAttribute("href"));
                }
            }
            return pages;
        } else {
            pages = new ArrayList<>();
            for (int x = 0; x < anchorTags.size(); x++) {
                isValid = CheckLinkIsValid(anchorTags.get(x).getAttribute("href"),invalidLinkList);
                isFound = CheckLinkIsUnique(pages,anchorTags.get(x).getAttribute("href"),domainRestriction);
                if (isValid && !isFound && anchorTags.get(x).getAttribute("href").indexOf(domainRestriction) > -1) {
                    pages.add(anchorTags.get(x).getAttribute("href"));
                }
            }
            return pages;
        }
    }

    /****************************************************************************
     *  DESCRIPTION:
     *    Gets all unique page links and returns them in a String Array.
     *
     **************************************************************************** */
    private ArrayList<String> GetPageLinks() {
        String invalidLinks = ".gif,.woff,.ttf,.jpg,.js,.png,.ico,.mx,.jpeg,.css";
        String[] invalidLinkList = invalidLinks.split(",");
        List<WebElement> anchorTags = driver.findElements(By.cssSelector("a"));
        ArrayList<String> links = new ArrayList<>();
        Boolean isFound = false;
        Boolean isValid = true;

        for (int x=0;x<anchorTags.size();x++){
            isFound = false;
            isValid = true;
            for (int y=0;y<links.size();y++){
                if (!testHelper.IsNullOrEmpty(links.get(y)) && !testHelper.IsNullOrEmpty(anchorTags.get(x).getAttribute("href")) && links.get(y).equals(anchorTags.get(x).getAttribute("href"))) {
                    isFound = true;
                    break;
                }
            }

            //invalidate any links that are not pages
            for (int z=0;z<invalidLinkList.length;z++) {
                if (!testHelper.IsNullOrEmpty(anchorTags.get(x).getAttribute("href")) && anchorTags.get(x).getAttribute("href").indexOf(invalidLinkList[z]) >-1) {
                    isValid = false;
                    break;
                }
            }

            if (!isFound && isValid) {
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
            }
        }
        return pages;
    }

    /****************************************************************************
     *  DESCRIPTION:
     *    This method creates a navigation test step to navigate to the href
     *    passed in.  This method was designed to work with the Entire Site Command
     *    to create the Navigation test step for each URL retrieved in that command
     *    and proceed to the next page.
     **************************************************************************** */
    public void CreateNavigateTestStepAndNavigate(String href, int fileIndex, int stepNum, int pageIndex, Boolean isCrucial, int waitTime) {
        TestStep navStep;
        List<Argument> argumentList;
        Argument argument, argument2;
        String fileStepIndex = "F" + fileIndex + "_S" + stepNum + "_Iteration:" + pageIndex;
        if (waitTime <= 0) {
            waitTime = 500;
        }
        try {
            navStep = new TestStep();
            argumentList = navStep.ArgumentList;
            navStep.set_command("navigate");
            navStep.set_actionType("write");
            navStep.set_crucial(isCrucial);
            navStep.set_expectedValue(href);
            argument = new Argument();
            argument.set_parameter(href);
            argumentList.add(argument);
            argument2 = new Argument();
            //argument2.set_parameter("1000");
            argument2.set_parameter(Integer.toString(waitTime));
            argumentList.add(argument2);
            navStep.setArgumentList(argumentList);
            /*for (int x=0;x<argumentList.size();x++) {
                testHelper.DebugDisplay("argumentList.get(" + x + ").get_parameter() = " + argumentList.get(x).get_parameter());
            }*/
            testHelper.UpdateTestResults("Performing Site Navigation to " + href + " for step " + fileStepIndex,true);
            PerformExplicitNavigation(navStep, fileStepIndex);
        } catch(Exception e) {
            testHelper.UpdateTestResults("Failed Site Navigation for href = " + href + " for step " + fileStepIndex,true);
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
        testHelper.set_csvFileName(configurationFolder + csvFileFolder + csvFileName);  //added for individual CSV files
        set_csvFileName(configurationFolder + csvFileFolder + csvFileName);
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
        if (!AreArgumentsCombined(ts,2)) {
            PerformExplicitNavigationNew(ts, fileStepIndex);
            return;
        }
        WriteLogContent();
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
        //testHelper.DebugDisplay("Calling CheckPageURL(" + delayMilliSeconds + ")");
        String actualUrl = CheckPageUrl(delayMilliSeconds);
        String timingMessage = "";
        if (expectedUrl != null && expectedUrl.trim().length() > 0) {
            //testHelper.DebugDisplay("expectedUrl != null and length > 0 " + expectedUrl);
            //testHelper.DebugDisplay("actualUrl = " + actualUrl);
            //testHelper.DebugDisplay("ts.get_crucial() = " + ts.get_crucial());
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
            //testHelper.DebugDisplay("#2 actualUrl = " + actualUrl);
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

    /********************************************************************************
     * Description: Performs Explicit Navigation Test Step reading the arguments as
     *              named arguments.
     * @param ts - test step Object
     * @param fileStepIndex - File Step index
     * @throws Exception
     ********************************************************************************/
    void PerformExplicitNavigationNew(TestStep ts, String fileStepIndex) throws Exception {
        if (AreArgumentsCombined(ts,2)) {
            PerformExplicitNavigationNew(ts, fileStepIndex);
            return;
        }
        WriteLogContent();
        String navigateUrl = GetArgumentValue(ts, 0, null);
        String delayTime = GetArgumentValue(ts, 1, null);
        String windowDimensionHeight = GetSpecificArgumentValue(ts, "h","=",null);
        String windowDimensionWidth = GetSpecificArgumentValue(ts, "w","=",null);
        String pageTimingFE = GetSpecificArgumentValue(ts, "fe","=",null);
        String pageTimingBE = GetSpecificArgumentValue(ts, "be","=",null);
        String indentMargin = ts.get_command().toLowerCase().equals(AppCommands.Navigate) ? AppConstants.indent5 : AppConstants.indent8;
        String subIndent = indentMargin.equals(AppConstants.indent5) ? AppConstants.indent8 : AppConstants.indent5 + AppConstants.indent8;

        //String pageTimings = GetArgumentValue(ts, 3, null);
        double frontEndTiming = 0;
        double backEndTiming = 0;
        String expectedUrl = null;
        int delayMilliSeconds = 0;

        if (navigateUrl != null && !navigateUrl.isEmpty()) {
            testHelper.UpdateTestResults( indentMargin + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start Explicit Navigation Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
            expectedUrl = ts.get_expectedValue() != null ? ts.get_expectedValue().trim() : null;

            if (delayTime != null && !delayTime.isEmpty()) {
                delayMilliSeconds = parseInt(delayTime.trim());
            }
            if (windowDimensionHeight != null && windowDimensionWidth != null) {
                //testHelper.DebugDisplay("windowDimensionHeight = " + windowDimensionHeight + " and windowDimensionWidth = " + windowDimensionWidth);
                int width = parseInt(windowDimensionWidth);
                int height = parseInt(windowDimensionHeight);

                testHelper.UpdateTestResults(subIndent + "Setting browser dimensions to (Width=" + width + " Height=" + height + ")", true);
                testHelper.SetWindowContentDimensions(driver, width, height);
            }
            //testHelper.DebugDisplay("pageTimingFE = " + pageTimingFE + " - pageTimingBE = " + pageTimingBE);
            if (pageTimingFE != null && pageTimingBE != null) {
                backEndTiming = parseDouble(pageTimingBE);
                frontEndTiming =  parseDouble(pageTimingFE);
            }
        }
        testPage = navigateUrl;
        //Explicit Navigation Event
        testHelper.UpdateTestResults(subIndent + "Navigating to " + navigateUrl + " for step " + fileStepIndex, true);
        testHelper.set_fileStepIndex(fileStepIndex);
        //testHelper.DebugDisplay("Calling CheckPageURL(" + delayMilliSeconds + ")");
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
            //testHelper.DebugDisplay("#2 actualUrl = " + actualUrl);
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

    /***************************************************************************
     * Description: Checks if arguments that can be combined are combined or
     *              if they are listed individually.
     * @param ts    - Test Step Object
     * @param startIndex - Argument Start index to begin checking
     * @return - boolean value (True, if Combined, else False)
     ***************************************************************************/
     Boolean AreArgumentsCombined(TestStep ts, int startIndex) {
        String testValue;
        int firstOccurrence;
        int secondOccurrence;
        Boolean returnValue = false;

        for (int i=startIndex;i<ts.ArgumentList.size();i++) {
            testValue = GetArgumentValue(ts, i, null);
            firstOccurrence = testValue.indexOf("=");
            secondOccurrence = testValue.lastIndexOf("=");
            if (firstOccurrence != secondOccurrence) {
                returnValue = true;
                break;
            }
        }
        return returnValue;
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

    /***********************************************************
     * Description: Saves the HAR file to the fileName passed in.
     * @param har - the HAR content
     * @param sFileName - the fileName where the HAR content is
     *                  to be saved.
     ***********************************************************/
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

    private void SaveFile(String fileName, List<LogEntry> logs) {
        String sFileName = testHelper.GetUnusedFileName(fileName);
        String message;

        //clean up the console log message removing backslash and unintentional double quotes
        try {
            // Printing details separately
            for (LogEntry e : logs) {
                message = e.getMessage().replace("\\","");
                message = message.replace("\"\"","\"");
                //message = message.replace(",\",",",\"\",");
                while (message.indexOf(",\",") > 0) {
                    message = message.replace(",\",",",\"\",");
                }
                testHelper.WriteToFile(sFileName, "Message: " + message + " - Level:" + e.getLevel());
                //testHelper.WriteToFile(sFileName, "Message: " + e.getMessage() + " - Level:" + e.getLevel());
            }
        } catch (Exception ex) {
            System.out.println (ex.toString());
            System.out.println("Could not find file " + sFileName);
        }
    }
    //endregion


    //region { Set Driver Methods }
    private void SetPhantomJsDriver_test() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability("takesScreenshot", true);
        File src = new File(phantomJsDriverPath);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, src.getAbsolutePath());
        /*
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
        ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:\\Utility\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        caps.setJavascriptEnabled(true);
         */
        String [] phantomJsArgs = {"--web-security=no", "--ignore-ssl-errors=yes"};
        caps.setCapability(PHANTOMJS_CLI_ARGS, phantomJsArgs);
        driver = new PhantomJSDriver(caps);
    }


    /****************************************************************************
     *  DESCRIPTION:
     *  Sets the WebDriver to the PhantomJs Driver
     **************************************************************************** */
    private void SetPhantomJsDriver() throws Exception {
        testHelper.UpdateTestResults( AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "PhantomJSDriver]" + AppConstants.ANSI_RESET , true);
        //BrowserMobProxyServer bm = new BrowserMobProxyServer(); //added 11/20/2023 testing
        //bm.start(0);
        //seleniumProxy = ClientUtil.createSeleniumProxy(bm);
        seleniumProxy = getSeleniumProxy(getProxyServer());

        //TODO: WORKING HERE
        File src = new File(phantomJsDriverPath);
        testHelper.DebugDisplay("#1 working here! src.getAbsolutePath() = " + src.getAbsolutePath());
        System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
        //WebDriverManager.phantomjs().setup();  //for some reason after maven update this no longer works!

        /***********[ Attempting to get log information here ]*******/
        //ChromeOptions options = new ChromeOptions();
        /*
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        testHelper.DebugDisplay("logPrefs.getEnabledLogTypes() = " + logPrefs.getEnabledLogTypes());
        options.setCapability("goog:loggingPrefs",logPrefs);  //this is the magic line

         */
        /************************************************************/
        DesiredCapabilities capabilities = new DesiredCapabilities();       //added 11/20/2023 testing
        //seleniumProxy = getSeleniumProxy(getProxyServer());               //added 11/20/2023 testing
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);    //added 11/20/2023 testing
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, src.getAbsolutePath());
        //capabilities.setAcceptInsecureCerts(true);
        /*capabilities.setCapability("web-security",false);
        capabilities.setCapability("ssl-protocol","any");
        capabilities.setCapability("ignore-ssl-errors",true);*/
        /*capabilities.setCapability(PHANTOMJS_CLI_ARGS,
               String[]("--web-security=no",
                "--ssl-protocol=any",
                "--ignore-ssl-errors=yes"));*/
        driver = new PhantomJSDriver();
        driver.manage().window().maximize(); //added 8-14-2019
        testHelper.set_is_Maximized(true);

    }

    /****************************************************************************
     *  DESCRIPTION: (old phantomJS Driver initialization.)
     *  Sets the WebDriver to the PhantomJs Driver
     **************************************************************************** */
    private void SetPhantomJsDriver_old() {

        try {
            testHelper.UpdateTestResults(AppConstants.indent5 + "[" + AppConstants.ANSI_GREEN + "Setting " + AppConstants.ANSI_RESET + "PhantomJSDriver]" + AppConstants.ANSI_RESET, true);
            File src = new File(phantomJsDriverPath);
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, src.getAbsolutePath());
            testHelper.DebugDisplay("#1 OK so far!");
            //IMPORTANT: for phantomJS you may need to add a user agent for automation testing as the default user agent is old
            // and may not be supported by the website.
            // debugging capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
            System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
            testHelper.DebugDisplay("#2 OK so far!");
            this.driver = new PhantomJSDriver(capabilities);
            driver.manage().window().maximize(); //added 8-14-2019
            testHelper.set_is_Maximized(true);
        } catch(Exception e) {
            testHelper.DebugDisplay(e.getMessage() + " \r\n" + e.getCause() + " \r\n" + e.getStackTrace());
        }
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
            //Assert.fail("invalid Host Address");
        }
        return seleniumProxy;
    }

    /*
    private void SetChromeDriver_original() {
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
            testHelper.set_is_Maximized(false);
        } else {
            driver = new ChromeDriver();
            driver.manage().window().maximize(); //added 8-14-2019
            testHelper.set_is_Maximized(true);
        }
    }*/

    /*
    private void SetChromeDriver_bareMinimum() {
        System.setProperty("webdriver.chrome.driver", "C:\\Gary\\Java Utilities\\BrowserDrivers\\chromedriver.exe");
        //ChromeOptions chromeOptions = new ChromeOptions();
        driver= new ChromeDriver();
    }*/


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
            /******[ Moved ChromeOptions declaration here 8/14/2023 ]**********/
            ChromeOptions options = new ChromeOptions();

            //proxy.start(80);
            DesiredCapabilities capabilities = new DesiredCapabilities();
            seleniumProxy = getSeleniumProxy(getProxyServer());
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
            //TODO: Create a configuration that allows the network traffic to be saved when configured
            //IMPORTANT: the line below adds network traffic to the HAR file and increases the size of the file exponentially.
            //a 280K-320K file will be about 95Megs!!!
            //if (CaptureNetworkRequests) {
            //proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
            //}

            /***********[ Attempting to get log information here ]*******/
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            testHelper.DebugDisplay("logPrefs.getEnabledLogTypes() = " + logPrefs.getEnabledLogTypes());
            options.setCapability("goog:loggingPrefs",logPrefs);  //this is the magic line
            /************************************************************/
            WebDriverManager.chromedriver().setup();

            if (runHeadless) {
                //ChromeOptions options = new ChromeOptions();
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
                //ChromeOptions options = new ChromeOptions();
                options.setCapability(CapabilityType.PROXY, seleniumProxy);
                options.setAcceptInsecureCerts(true);
                //options.setCapability(CapabilityType.PROXY, proxy);
                driver = new ChromeDriver(options);
                //driver = WebDriverManager.chromedriver().create(); //alternate attempt to create the driver

                //driver = new ChromeDriver();
                driver.manage().window().maximize(); //added 8-14-2019
                testHelper.set_is_Maximized(true);
            }
            proxy.newHar();
        } catch(Exception ex) {
            testHelper.UpdateTestResults("Failure Error Setting ChromeDriver: " + ex.getMessage()  + " for step SetChromeDriver", true);
            //testHelper.UpdateTestResults("Failure Error Setting ChromeDriver: " + ex.getStackTrace() + " for step SetChromeDriver", true);
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
        /***********[ Attempting to get log information here ]*******/
        //TODO: Figure out the correct logPrefs for FireFox to get console log events
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        testHelper.DebugDisplay("logPrefs.getEnabledLogTypes() = " + logPrefs.getEnabledLogTypes());


        FirefoxOptions options = new FirefoxOptions();
        //options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        options.setCapability(CapabilityType.PROXY, seleniumProxy);     //added 11/21/2023
        options.setAcceptInsecureCerts(true);                           //added 11/21/2023
        options.setCapability("moz:firefoxOptions",logPrefs);
        /************************************************************/

        if (runHeadless) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("-headless");
            driver = new FirefoxDriver(options);
            testHelper.set_is_Maximized(false);
        } else {
            //driver = new FirefoxDriver();
            /****[ Attempting to add logging ]*********/
            driver = new FirefoxDriver(options);
            /*****************************************/
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
        /*
        if (runHeadless) {
            DesiredCapabilities capab = DesiredCapabilities.internetExplorer();
            capab.setCapability("headless", true);

        } */
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
        String xPathId;
        if (childElement != null ) {
            if (childElement.getAttribute("id") != null && childElement.getAttribute("id").length() > 0) {
                xPathId = GenerateIdXPath(childElement, current);
                if (xPathId != null) {
                    return GenerateIdXPath(childElement, current);
                }
            }
        }
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

    String GenerateIdXPath(WebElement childElement, String current) {
        String tagName = childElement.getTagName().toLowerCase();
        current = current == null ? "" : current;
        List<WebElement> elements = driver.findElements(By.xpath("//" + tagName + "[@id=\"" + childElement.getAttribute("id") + "\"] " + current));
        if (elements.size() == 1) {
            return "//" + tagName + "[@id=\"" + childElement.getAttribute("id") + "\"] " + current;
            //return "//*[@id=\"" + childElement.getAttribute("id") + "\"]";
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

    /*************************************************************
     * Description: Creates a GTM tag object based on the
     *              Test file arguments.
     * @param ts - Test Step Object
     * @param defaultValue - (not currently in use) intended to
     *                     be used as the field values in the event
     *                     that the actual value is not present.
     * @return - Returns a GTM tag object
     ***************************************************************/
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

    /********************************************************************
     * Description: Creates a GA4 Tag object based on the Test Step Arguments.
     * @param ts - Test Step Object
     * @param defaultValue - (not currently in use) intended to
     *                           be used as the field values in the event
     *                           that the actual value is not present.
     * @return - Returns a GA4 Tag object
     ********************************************************************/
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

    /***********************************************************
     * Description: Gets all Test Step arguments and returns
     *              them as an array.
     * @param ts    - Test Step Object
     * @return      - An Array of Test Step arguments
     ***********************************************************/
    public ArrayList<String> GetAllArguments(TestStep ts) {
        //String [] returnArguments = new String[];
        ArrayList<String> returnArguments = new ArrayList<String>();

        for(int x=0;x< ts.ArgumentList.size();x++) {
            if (!testHelper.IsNullOrEmpty(ts.ArgumentList.get(x).get_parameter())) {
                returnArguments.add(ts.ArgumentList.get(x).get_parameter());
            }
        }
        return returnArguments;
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
