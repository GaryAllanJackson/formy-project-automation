import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class PageHelper {

    //private String testFileName = "C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestFiles\\TestSettingsFile.txt";

    //bold
    public static final String ANSI_BOLD = "\u001B[1m";

    //region { System.out Colors }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    //public static final String ANSI_BLACK_ALT = "\033[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    //endregion

    //region {System out background colors }
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static final String COMBO_YELLOW_BLACK = "\u001B[43m;\u001B[30m";
    public static final String FRAMED = "\u001B[51m";
    //endregion



    //region { Properties }
    private String _logFileName;
    public String get_logFileName() {
        return _logFileName;
    }
    public void set_logFileName(String _logFileName) {
        this._logFileName = _logFileName;
    }
    public static final String sectionStartFormatLeft =  "╔══════════════[ ";
    public static final String sectionStartFormatRight = " ]══════════════╗";
    public static final String sectionEndFormatLeft =    "╚══════════════[ ";
    public static final String sectionEndFormatRight =   " ]══════════════╝";
    private int screenShotsTaken = 0;
    private int maxScreenShotsToTake = 0;


    private String _helpFileName;
    public String get_helpFileName() {
        return _helpFileName;
    }

    public void set_helpFileName(String _helpFileName) {
        this._helpFileName = _helpFileName;
    }

    //endregion


    /* ****************************************************************
     *  DESCRIPTION:
     *  Navigates to the web address passed in
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress) throws InterruptedException{
        driver.get(webAddress);
        Thread.sleep(10000);
    }

    /* ****************************************************************
     *  DESCRIPTION:
     *  Navigates to the web address passed in and sleeps for the number of milliseconds passed in
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress, int milliseconds) throws InterruptedException{
        //UpdateTestResults("In NavigateToPage waiting " + milliseconds + " milliseconds!");
        if (milliseconds > 0) {
            driver.get(webAddress);
            Thread.sleep(milliseconds);
        }
        else
        {
            NavigateToPage(driver, webAddress);
        }
    }


    /* ****************************************************************
     *  Saves a screenshot to the
     **************************************************************** */
    public void captureScreenShot(WebDriver driver, String screenShotName, String screenShotFolder, boolean isError) {

        if ((maxScreenShotsToTake > 0 && screenShotsTaken < maxScreenShotsToTake) || (maxScreenShotsToTake == 0) || isError) {

            try {
                //get the original dimensions and save them
                Dimension originalDimension = driver.manage().window().getSize();
                int height = originalDimension.height;
                int width = originalDimension.width;

                //reset the browser dimensions to capture all content
                Dimension dimension = GetWindowContentDimensions(driver);
                driver.manage().window().setSize(dimension);

                screenShotName = MakeValidFileName(screenShotName);

                //take the screen shot
                TakesScreenshot ts = (TakesScreenshot) driver;
                File source = ts.getScreenshotAs(OutputType.FILE);
                if (screenShotFolder != null && !screenShotFolder.isEmpty() && Files.exists(Paths.get(screenShotFolder))) {
                    if (!screenShotFolder.endsWith("\\")) {
                        screenShotFolder = screenShotFolder + "\\";
                    }
                    FileUtils.copyFile(source, new File(screenShotFolder + screenShotName + ".png"));
                } else {
                    if (!Files.exists(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"))) {
                        Files.createDirectory(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"));
                    }
                    FileUtils.copyFile(source, new File("./ScreenShots/" + screenShotName + ".png"));
                }
                //System.out.println("Screenshot taken");
                if (!isError) {
                    UpdateTestResults("     Screenshot taken");
                } else {
                    UpdateTestResults(ANSI_RED + "Screenshot taken - Error condition!" + ANSI_RESET);
                }
                //resize the browser to the original dimensions
                driver.manage().window().setSize(originalDimension);
                screenShotsTaken++;
            } catch (Exception e) {
                //System.out.println(ANSI_RED + "Exception while taking screenshot (" + screenShotName + "): " + e.getMessage() + ANSI_RESET);
                UpdateTestResults(ANSI_RED + "Exception while taking screenshot (" + screenShotName + "): " + e.getMessage() + ANSI_RESET);
            }
        }
    }

    private String MakeValidFileName(String screenShotName) {
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyz1234567890_-";
        String cleanValue = "";
        //System.out.println("Allowed Characters: (" + allowedCharacters + ")");
        for (int x=0;x<=(screenShotName.length()-1);x++)
        {
            //System.out.println(screenShotName.substring(x,x + 1).toLowerCase());
            if (allowedCharacters.indexOf(screenShotName.substring(x,x + 1).toLowerCase()) >= 0)
            {
                cleanValue = cleanValue + screenShotName.substring(x, x + 1);
            }
        }
        return cleanValue;
    }


    /* ****************************************************************
     *   DESCRIPTION:
     *   This method gets the dimensions of the content area so that
     *   the screen dimensions can be reset before a screen capture to ensure
     *   that all content is in the captured image.
     ***************************************************************** */
    private Dimension GetWindowContentDimensions(WebDriver driver)
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int contentHeight = ((Number) js.executeScript("return document.documentElement.scrollHeight")).intValue();
        int contentWidth = ((Number) js.executeScript("return document.documentElement.scrollWidth")).intValue();

        return new Dimension(contentWidth, contentHeight);
    }

    public void SetWindowContentDimensions(WebDriver driver, int width, int height)
    {
        Dimension sessionDimension = new Dimension(width, height);
        driver.manage().window().setSize(sessionDimension);
    }

    /* *******************************************************************************************
        Description: This method reads in the test file, parsing each line and creating a new <TestSettings>
        object, placing the xPath string into the xPath Property, placing the Expected value string into the
        Expected value Property and adding that to the List<TestSettings> ArrayList
     ******************************************************************************************* */
    public List<TestSettings> ReadTestSettingsFile(List<TestSettings> testSettings, String testFileName) throws Exception {
        TestSettings test;
        int requiredFields = 5;
        int lineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(testFileName))) {
            String line;
            String tempLine = "";
            String [] lineValues;
            //UpdateTestResults(ANSI_PURPLE + "----------[ Start of Reading Test Settings file File ]--------------" + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + sectionStartFormatLeft + " Start of Reading Test Settings file File " + sectionStartFormatRight + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + "Reading " + testFileName +  " file" + ANSI_RESET);
            while ((line = br.readLine()) != null) {
                lineCount++;
                //line comments in this file are indicated with ###
                if (line.indexOf("###") < 0) {
                    //UpdateTestResults("Line check #2: " + line);
                    if (line.indexOf("╠") >= 0) {
                        tempLine = "";
                    }
                    if (line.indexOf("╣") >= 0) {
                        if (tempLine.length() > 0) {
                            line = tempLine + line;
                        }
                        line = line.substring(1, line.length() - 1);

                        test = new TestSettings();
                        lineValues = line.split(" ; ");
                        if (lineValues.length != requiredFields) {
                            UpdateTestResults(ANSI_RED + "[ Incorrect file format." + requiredFields + " fields required separated by semi-colons.  Retrieved " + lineValues.length + " fields on line: " + lineCount + ". ]" + ANSI_RESET);
                        }
                        test.set_xPath(lineValues[0].trim());
                        test.set_expectedValue(lineValues[1].trim());
                        test.set_searchType(lineValues[2].trim());
                        test.setPerformWrite(Boolean.parseBoolean(lineValues[3].trim()));
                        test.set_isCrucial(Boolean.parseBoolean(lineValues[4].trim()));
                        testSettings.add(test);
                        // Show input to user
                        UpdateTestResults(ANSI_PURPLE + "     Reading Test File values(xPath = " + test.get_xPath() + ") - (Expected Value = " + test.get_expectedValue() + ")" + ANSI_RESET);
                    }
                    else {
                        tempLine += line + "\r\n";
                    }
                }
            }
            //UpdateTestResults(ANSI_PURPLE + "----------[ End of Reading Test Settings file File ]--------------" + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + sectionEndFormatLeft + "End of Reading Test Settings file File " + sectionEndFormatRight + ANSI_RESET);
            return testSettings;
        }
    }



    /* ******************************************************************
     * Description: This method reads the test configuration file
     * and populates the ConfigSettings variable with these settings
     * which in turn direct the test to use the selected browser and
     * to test the configured site.
     ****************************************************************** */
    public ConfigSettings ReadConfigurationSettings(String configurationFile, boolean isExecutedFromMain) throws Exception  {
        PrintSamples();
        ConfigSettings configSettings = new ConfigSettings();
        String configValue;

        File configFile = new File(configurationFile);
        if (!configFile.exists() && isExecutedFromMain) {
            Scanner scanner = new Scanner(System.in);
            UpdateTestResults("Configuration File not found (" + configurationFile + ")");
            UpdateTestResults("Enter the path to the config file: ");
            String tempconfigurationFile = scanner.nextLine();
            configurationFile = tempconfigurationFile;
            UpdateTestResults("configurationFile = " + configurationFile);
        }
        else if (!configFile.exists() && !isExecutedFromMain) {
            UpdateTestResults( ANSI_RED + ANSI_BOLD + "Configuration File not found! (" + configurationFile + ")");
            UpdateTestResults("Place the configuration file in the location above with the name specified and re-run the test.\r\nExiting!!!");
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(configurationFile))) {
            String line;
            UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionStartFormatLeft + "Reading Config (" + configurationFile +  ") file" + sectionStartFormatRight + ANSI_RESET);
            while ((line = br.readLine()) != null) {
                if (line.substring(0,2).indexOf("//") < 0) {
                    configValue = line.substring(line.indexOf("=") + 1);
                    if (line.toLowerCase().indexOf("browsertype") >= 0) {
                        configSettings.set_browserType(configValue);
                        UpdateTestResults("     browserType = " + configSettings.get_browserType().toString());
                    }
                    else if (line.toLowerCase().indexOf("testpageroot") >= 0) {
                        configSettings.set_testPageRoot(configValue);
                        UpdateTestResults("     testPageRoot = " + configSettings.get_testPageRoot());
                    }
                    else if (line.toLowerCase().indexOf("runheadless") >= 0) {
                        configSettings.set_runHeadless(Boolean.parseBoolean(configValue));
                        UpdateTestResults("     runHeadless = " + configSettings.get_runHeadless().toString());
                    }
                    else if (line.toLowerCase().indexOf("screenshotsavefolder") >= 0) {
                        configSettings .set_screenShotSaveFolder(configValue);
                        UpdateTestResults("     screenShotSaveFolder = " + configSettings.get_screenShotSaveFolder());
                    }
                    else if (line.toLowerCase().indexOf("testallbrowsers") >= 0) {
                        configSettings.set_testAllBrowsers(Boolean.parseBoolean(configValue));
                        UpdateTestResults("     testAllBrowsers = " + configSettings.get_testAllBrowsers().toString());
                    }
                    else if (line.toLowerCase().indexOf("testfilename") >= 0) {
                        configSettings.set_testSettingsFile(configValue);
                        UpdateTestResults("     testFileName = " + configSettings.get_testSettingsFile());
                    }
                    else if (line.toLowerCase().indexOf("testfoldername") >= 0) {
                        configSettings.set_testFolderName(configValue);
                        UpdateTestResults("     testFolderName = " + configSettings.get_testFolderName());
                    }
                    else if (line.toLowerCase().indexOf("specifytestfiles") >= 0) {
                        configSettings.set_specifyFileNames(Boolean.parseBoolean(configValue));
                        UpdateTestResults("     specifytestfilenames = " + configSettings.get_specifyFileNames());
                    }
                    else if (line.toLowerCase().indexOf("folderfilefiltertype") >= 0) {
                        configSettings.set_folderFileFilterType(configValue);
                        UpdateTestResults("     FolderFileFilterType = " + configSettings.get_folderFileFilterType());
                    }
                    else if (line.toLowerCase().indexOf("folderfilefilter") >= 0) {
                        configSettings.set_folderFileFilter(configValue);
                        UpdateTestResults("     FolderFileFilter = " + configSettings.get_folderFileFilter());
                    }
                    else if (line.toLowerCase().indexOf("maxscreenshotstotake") >= 0) {
                        if (configValue != null && !configValue.isEmpty()) {
                            configSettings.set_maxScreenShots(parseInt(configValue));
                            maxScreenShotsToTake = parseInt(configValue);
                        }
                        else {
                            configSettings.set_maxScreenShots(0);
                            maxScreenShotsToTake = 0;
                        }

                        UpdateTestResults("     screenShotSaveFolder = " + configSettings.get_screenShotSaveFolder());
                    }
                }
            }
            if (!configSettings.get_specifyFileNames()) {
                //UpdateTestResults( FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + "---[ Start - Retrieving Files in specified folder. ]---" + ANSI_RESET);
                UpdateTestResults( FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionStartFormatLeft + " Start - Retrieving Files in specified folder." + sectionStartFormatRight + ANSI_RESET);
                configSettings.reset_testFiles();
                File temp = new File(configSettings.get_testFolderName());
                configSettings = GetAllFilesInFolder(temp, "txt", configSettings);
                //UpdateTestResults("2. configSettings.get_testFiles() = " + configSettings.get_testFiles());
                UpdateTestResults(FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionEndFormatLeft + "End Retrieving Files in specified folder." + sectionEndFormatRight + ANSI_RESET);
            }
        }
        catch (Exception e) {
            UpdateTestResults(ANSI_RED + "The following error occurred while attempting to read the configuration file:" + configurationFile + "\\r\\n" + e.getMessage() + ANSI_RESET);
        }
        //UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + "----------[ End of Reading Configuration File ]--------------" + ANSI_RESET);
        UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionEndFormatLeft + "End of Reading Configuration File" + sectionEndFormatRight + ANSI_RESET);
        return configSettings;
    }

    /* ******************************************************************
    * Description: This method gets all files in the folder passed in
    *              based on the the config settings allowing filtering of
    *              files based on what the files start with, contain, or
    *              ends with.
    * ****************************************************************** */
   public ConfigSettings GetAllFilesInFolder(final File folder, String extension, ConfigSettings configSettings) {
       //List<String> testFiles = new ArrayList<>();
       for (final File fileEntry : folder.listFiles()) {
           String temp;
           if (fileEntry.isFile()) {
               temp = fileEntry.getAbsoluteFile().toString(); //  fileEntry.getName();
               if (configSettings.get_folderFileFilterType().toLowerCase().equals("ends_with")) {
                   if (temp.toLowerCase().endsWith(configSettings.get_folderFileFilter().toLowerCase())) {
                       configSettings.set_testSettingsFile(temp);
                       UpdateTestResults(temp);
                   }
               }
               else if (configSettings.get_folderFileFilterType().toLowerCase().equals("starts_with")) {
                   if (temp.toLowerCase().startsWith(configSettings.get_folderFileFilter().toLowerCase())) {
                       configSettings.set_testSettingsFile(temp);
                       UpdateTestResults(temp);
                   }
               }
               else if (configSettings.get_folderFileFilterType().toLowerCase().equals("contains")) {
                   if (temp.toLowerCase().contains(configSettings.get_folderFileFilter().toLowerCase())) {
                       configSettings.set_testSettingsFile(temp);
                       UpdateTestResults(temp);
                   }
               }
               //region { Recursive Directory reading not needed }
//            if (fileEntry.isDirectory()) {
//                listFilesForFolder(fileEntry);
//            } else {
//                System.out.println(fileEntry.getName());
//            }
           //endregion
           }
       }
       //return testFiles;
       return configSettings;
   }

   /* ***************************************************************************
    *  DESCRIPTION:
    *  Adds a message to the List<String> testResults and writes out the current status to
    *  the log file and then to the screen.
    *  (testResults is not necessary and may be removed or you can write all test
    *  results out when the program ends in the destructor.)
    **************************************************************************** */
    public List<String> UpdateTestResults(String testMessage, List<String> testResults) {
        if (testResults != null) {
            testResults.add(testMessage);
        }
        try {
            WriteToFile(get_logFileName(), testMessage);
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
        else if ((testMessage.indexOf("--[") > 0 || testMessage.indexOf("══[") > 0) && (testMessage.toLowerCase().indexOf("end") > 0 || testMessage.toLowerCase().indexOf("revert") > 0))
        {
            System.out.println(testMessage);
            System.out.println("");
        }
        else {
            System.out.println(testMessage);
        }
        return testResults;
    }

    public void UpdateTestResults(String testMessage) {
        //UpdateTestResults(testMessage, null);
        if ((testMessage.indexOf("--[") > 0 || testMessage.indexOf("══[") > 0) && testMessage.toLowerCase().indexOf("end") > 0)
        {
            System.out.println(testMessage);
            System.out.println();
        }
        else {
            System.out.println(testMessage);
        }
    }

    public void WriteToFile(String fileName, String fileContents) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(fileContents);
            writer.newLine();
            writer.close();
        }
        catch(Exception ex) {
            //System.out.println("The following error occurred when attempting to write to the test log file:" + ex.getMessage());
            UpdateTestResults(ANSI_RED + ANSI_BOLD + "The following error occurred when attempting to write to the test log file:" + ex.getMessage());
        }
    }


    public void PrintSamples() throws Exception {

        try {
            File helpFile = new File(get_helpFileName());
            if (helpFile.exists()) {
                helpFile.delete();
            }
            WriteToFile(get_helpFileName(), "Test File Format:");
            WriteToFile(get_helpFileName(), "### ╔══════════════════════════════════╦═════════════════════════╦═══════════════════════╦════════════════════════════════════════╦══════════════════════════╗");
            WriteToFile(get_helpFileName(), "### ║ ╠[URL/XPath/CssSelector/TagName] ; [Action/Expected value] ; [Element Lookup Type] ; [Perform Action other than Read Value] ; [Critical Assertion] ╣   ║");
            WriteToFile(get_helpFileName(), "### ╚══════════════════════════════════╩═════════════════════════╩═══════════════════════╩════════════════════════════════════════╩══════════════════════════╝");
            WriteToFile(get_helpFileName(), "### Each test script begins with ╠ (alt + 204) and ends with ╣ (alt + 185).  These delimiters allow for tests to span multiple lines.");
            WriteToFile(get_helpFileName(), "### Each parameter is separated by a space + semi-colon + space.");
            WriteToFile(get_helpFileName(), "### The first parameter is one of the following: url to navigate to, or Element (xPath, CssSelector, Tag Name, ClassName, ID)");
            WriteToFile(get_helpFileName(), "### The second parameter is the action to take or expected value to retrieve.  For URLs both are required separated by a space then (alt + 206), ");
            WriteToFile(get_helpFileName(), "###     ╬ then space. ' ╬ '  optionally add a second space + (alt + 206) + space delimiter to add a time delay (thread sleep value in milliseconds) to give the event time to complete.");
            WriteToFile(get_helpFileName(), "###     The format is: Action ╬ Expected Value ╬ Time delay before making the assertion");
            WriteToFile(get_helpFileName(), "###     For context menu navigation the action can be a chain of up or down arrow keys as well to navigate to the desired menu item.");
            WriteToFile(get_helpFileName(), "### The third parameter is the type of check to perform and will be ignored for performing Navigation where that is irrelevant");
            WriteToFile(get_helpFileName(), "###        acceptable values are xPath, CssSelector, Tag Name, ClassName, ID and n/a");
            WriteToFile(get_helpFileName(), "### The fourth parameter is the PerformAction boolean field.  Set this to true when performing an action other than retrieving the text value of the element.");
            WriteToFile(get_helpFileName(), "###        Examples of when this is true: Text should be entered, a click occurs, a wait, or Navigating, switching Tabs.");
            WriteToFile(get_helpFileName(), "### The fifth parameter is the IsCrucial boolean.  This indicates that should this step fail, all testing should stop!");
            WriteToFile(get_helpFileName(), "###       When true, if the assertion fails all testing stops immediately.");
            WriteToFile(get_helpFileName(), "###       When false, if the assertion fails, the tests continue and the failed results appear in red text.");
            WriteToFile(get_helpFileName(), "###       All successful assertions will appear in green text.  All failed assertions will appear in red text.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The examples below are are not all inclusive but rather attempt to provide you with a basic understanding\r\n so that you can make the necessary changes to accomplish a testing task.");
            WriteToFile(get_helpFileName(), "When an error occurs it is most likely due to an element not found and a screenshot will automatically be taken.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "=========[ NAVIGATION ]========================================================================================================================================");
            WriteToFile(get_helpFileName(), "All Navigation steps should be marked as crucial, as all subsequent checks require that navigation to complete successfully!!!");
            WriteToFile(get_helpFileName(), "To Navigate and mark that step as crucial");
            WriteToFile(get_helpFileName(), "╠https://www.w3schools.com/bootstrap/tryit.asp?filename=trybs_ref_comp_dropdown-menu&stacked=h ; Navigate ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To Navigate, assert that the URL is what follows the ╬ character and to wait 4 seconds before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "╠https://formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To Navigate and Authenticate with username and password and assert that the URL is what follows the ╬ character and to wait 4 seconds before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "╠https://username:password@formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Navigate and Authenticate with username and password:");
            WriteToFile(get_helpFileName(), "╠https://username:password@formy-project.herokuapp.com/form ; Navigate ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To Navigate, assert that the URL, add a time delay and set the browser dimensions to 800 width by 800 height:");
            WriteToFile(get_helpFileName(), "╠https://formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000  ╬ w=800 h=800 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ CHECK URL WITHOUT NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "To check a URL without navigating and to make it non-curcial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠n/a ; URL ╬ https://formy-project.herokuapp.com/thanks ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ WAITING FOR ITEMS TO BE AVAILABLE ]==================================================================================================================");
            WriteToFile(get_helpFileName(), "To wait for a specific amount of time before continuing to allow for page loading or script completion");
            WriteToFile(get_helpFileName(), "To wait for 5 seconds before continuing onto the next step.");
            WriteToFile(get_helpFileName(), "╠n/a ; Wait ╬ 5000 ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ FILLING IN TEXT FIELDS ]=============================================================================================================================");
            WriteToFile(get_helpFileName(), "To fill in a field by ID and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; John ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To fill in a field by ID and to make it non-crucial when it contains a reserved command like click.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; sendkeys ╬ click ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ CHECKING A CHECKBOX/RADIOBUTTON - CLICKING A BUTTON ]================================================================================================");
            WriteToFile(get_helpFileName(), "To click an element by ID");
            WriteToFile(get_helpFileName(), "╠checkbox-2 ; click ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ CLICKING AN ELEMENT THAT FORCES NAVIGATION ]=========================================================================================================");
            WriteToFile(get_helpFileName(), "To click an element by xPath that navigates to a new page and check the url of the new page after waiting 5 seconds for the page to load.");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[1]/h4[3] ; click ╬ https://www.davita.com/education ╬ 5000 ; xPath ; true ; false╣");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ RETRIEVING TEXT FROM AN ELEMENT ]====================================================================================================================");
            WriteToFile(get_helpFileName(), "Retrieving text is usually non-crucial and test execution can usually continue so the following examples are all non-crucial.  Update based on your requirements.");
            WriteToFile(get_helpFileName(), "To retrieve the text of an element by ClassName and make the assertion non-crucial");
            WriteToFile(get_helpFileName(), "╠alert ; The form was successfully submitted! ; ClassName ; false ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To retrieve the text of an element by xPath");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/div[1]/div[1]/ul[1]/li[1]/div[1]/div[1]/h1[1] ; Empower Yourself with Kidney Knowledge ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");

            WriteToFile(get_helpFileName(), "=========[ RETRIEVING TEXT FROM AN ELEMENT IN AN IFRAME ]=======================================================================================================");
            WriteToFile(get_helpFileName(), "When you are attempting to access an element in an iFrame, you must first switch to that iframe.");
            WriteToFile(get_helpFileName(), "The syntax for doing so is placed in the second parameter using the key phrase Switch to iframe ");
            WriteToFile(get_helpFileName(), "followed by the name in square brackets as shown in the following example.");
            WriteToFile(get_helpFileName(), "To retrieve the text of an element in an iFrame by xPath");
            WriteToFile(get_helpFileName(), "╠/html/body/select ; Switch to iframe [iframeResult] ╬ Volvo ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");


            WriteToFile(get_helpFileName(), "=========[ CLICK AN ELEMENT IN AN IFRAME ]=====================================================================================================================");
            WriteToFile(get_helpFileName(), "To click an element by xPath in an iFrame");
            WriteToFile(get_helpFileName(), "╠/html/body/div/div/ul/li[1]/a ; Switch to iframe [iframeResult] ╬ click ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To select an option from an HTML Select (drop down/list) element.");
            WriteToFile(get_helpFileName(), "╠option[value='1'] ; click ; CssSelector ; true ; false╣");
            WriteToFile(get_helpFileName(), "===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ TAKING SCREENSHOTS ]================================================================================================================================");
            WriteToFile(get_helpFileName(), "To take a screen shot/print screen.  The browser will be resized automatically to capture all page content.");
            WriteToFile(get_helpFileName(), "╠n/a ; ScreenShot ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "=========[ SWITCHING BROWSER TABS ]============================================================================================================================");
            WriteToFile(get_helpFileName(), "Some actions are related and to avoid unnecessary steps the enter key will be pressed after right clicking and arrowing to a particular item.");
            WriteToFile(get_helpFileName(), "To Right click on an element, move down to the first menu item, click it to open in a new tab and switch to the new tab:");
            WriteToFile(get_helpFileName(), "╠//*[@id=\"rso\"]/div[1]/div/div[1]/div/div/div[1]/a ; right click ╬ Keys.Arrow_Down ╬ Switch to tab ; xPath ; true ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To Switch back to the first tab after switching to the second tab");
            WriteToFile(get_helpFileName(), "╠n/a ; Switch to tab 0 ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
        }
        catch (Exception ex) {
            UpdateTestResults(ANSI_RED + ANSI_BOLD + "Error Writing Help File" + ANSI_RESET);
        }
    }


    public List<TestSettings> ReadTestSettingsFileOld(List<TestSettings> testSettings, String testFileName) throws Exception {
        TestSettings test;
        int requiredFields = 5;
        /*if (testFileName == null || testFileName.isEmpty())
        {
            testFileName = this.testFileName;
        }*/
        try (BufferedReader br = new BufferedReader(new FileReader(testFileName))) {
            String line;
            String [] lineValues;
            //System.out.println(ANSI_PURPLE + "Reading " + testFileName +  " file" + ANSI_RESET);
            //UpdateTestResults(ANSI_PURPLE + "----------[ Start of Reading Test Settings file File ]--------------" + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + sectionStartFormatLeft + "Start of Reading Test Settings file File " + sectionStartFormatRight + ANSI_RESET);

            UpdateTestResults(ANSI_PURPLE + "Reading " + testFileName +  " file" + ANSI_RESET);
            while ((line = br.readLine()) != null) {
                //line comments in this file are indicated with ###
                if (line.indexOf("###") < 0) {
                    if (line.indexOf("╚") >= 0) {
                        UpdateTestResults("The character is recognized: " + line);
                    }
                    test = new TestSettings();
                    lineValues = line.split(";");
                    if (lineValues.length != requiredFields) {
                        //System.out.println(ANSI_RED + "[ Incorrect file format." + requiredFields + " fields required separated by semi-colons ]" + ANSI_RESET);
                        UpdateTestResults(ANSI_RED + "[ Incorrect file format." + requiredFields + " fields required separated by semi-colons ]" + ANSI_RESET);
                    }
                    //test.set_xPath(line.substring(0, line.indexOf(":")).trim());
                    test.set_xPath(lineValues[0].trim());
                    test.set_expectedValue(lineValues[1].trim());
                    test.set_searchType(lineValues[2].trim());
                    test.setPerformWrite(Boolean.parseBoolean(lineValues[3].trim()));
                    test.set_isCrucial(Boolean.parseBoolean(lineValues[4].trim()));
                    testSettings.add(test);
                    // Show input to user
                    UpdateTestResults(ANSI_PURPLE + "     Reading Test File values(xPath = " + test.get_xPath() + ") - (Expected Value = " + test.get_expectedValue() + ")" + ANSI_RESET);
                }
            }
            //UpdateTestResults(ANSI_PURPLE + "----------[ End of Reading Test Settings file File ]--------------" + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + sectionEndFormatLeft + "End of Reading Test Settings file File" + sectionEndFormatRight + ANSI_RESET);
            return testSettings;
        }
    }


    //region { Commented Out Code }
//    private void UpdateTestResults(String testMessage) {
//        testResults.add(testMessage);
//        System.out.println(testMessage);
//    }


    /*
    // the below methods were written exclusively for the Firefox driver but are no longer needed
    // as all browsers are using WebDriver.
     public void NavigateFFToPage(FirefoxDriver driver, String webAddress) throws InterruptedException{
        driver.get(webAddress);
        Thread.sleep(10000);
    }

    public void captureScreenShotFF(FirefoxDriver driver, String screenShotName, String screenShotFolder) {
        try {
            //get the original dimensions and save them
            Dimension originalDimension = driver.manage().window().getSize();
            int height = originalDimension.height;
            int width = originalDimension.width;

            //reset the browser dimensions to capture all content
            Dimension dimension = GetFFWindowContentDimensions(driver);
            driver.manage().window().setSize(dimension);

            //take the screen shot
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            if (screenShotFolder != null && !screenShotFolder.isEmpty() && Files.exists(Paths.get(screenShotFolder))) {
                FileUtils.copyFile(source, new File(screenShotFolder + screenShotName+".png"));
            }
            else {
                if (!Files.exists(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"))) {
                    Files.createDirectory(Paths.get("C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestDavita\\ScreenShots"));
                }
                FileUtils.copyFile(source, new File("./ScreenShots/" + screenShotName + ".png"));
            }
            System.out.println("Screenshot taken");

            //resize the browser to the original dimensions
            driver.manage().window().setSize(originalDimension);
        }
        catch (Exception e) {
            System.out.println("Exception while taking screenshot " + e.getMessage());
        }
    }



    private Dimension GetFFWindowContentDimensions(FirefoxDriver driver)
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int contentHeight = ((Number) js.executeScript("return document.documentElement.scrollHeight")).intValue();
        int contentWidth = ((Number) js.executeScript("return document.documentElement.scrollWidth")).intValue();

        return new Dimension(contentWidth, contentHeight);
    }*/
    //endregion
}
