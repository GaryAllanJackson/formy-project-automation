import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public static final String subsectionLeft = "     «══════════════[ ";
    public static final String subsectionRight = " ]══════════════════════════════════════════»";
    public static final String indent5 = "     ";
    public static final String indent8 = "        ";
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
     *  DESCRIPTION:
     *  Checks to ensure that Screenshots are configured to be taken.
     *  If screenshots are configured, it checks the current number of
     *  screenshots against the total taken to ensure that it doesn't
     *  take more than configured.  For error screen captures, this
     *  count is ignored so that issues can be troubleshot by the user.
     *  Saves the current screen dimensions, calls the resize method to
     *  expand the screen to capture the entire page and saves a
     *  screenshot to the the specified folder.
     *  If a screenshot folder is not configured screenshots will be saved in
     *  the are not
     **************************************************************** */
    public void captureScreenShot(WebDriver driver, String screenShotName, String screenShotFolder, boolean isError) {

        //if ((maxScreenShotsToTake > 0 && screenShotsTaken < maxScreenShotsToTake) || (maxScreenShotsToTake == 0) || isError) {
        if ((maxScreenShotsToTake > 0 && screenShotsTaken < maxScreenShotsToTake) || (maxScreenShotsToTake == 0)) {
            try {
                //get the original dimensions and save them
                Dimension originalDimension = driver.manage().window().getSize();
                int height = originalDimension.height;
                int width = originalDimension.width;
                UpdateTestResults("==================[gaj debugging]=========================================");
                UpdateTestResults("Original Browser Dimensions height: " + height + " Width: " + width);
                //reset the browser dimensions to capture all content
                Dimension dimension = GetWindowContentDimensions(driver);
                driver.manage().window().setSize(dimension);
                UpdateTestResults("Resized Browser Dimensions height: " + dimension.height + " Width: " + dimension.width);
                UpdateTestResults("==================[gaj debugging]=========================================");

                screenShotName = MakeValidFileName(screenShotName);

                //take the screen shot
                TakesScreenshot ts = (TakesScreenshot) driver;
                File source = ts.getScreenshotAs(OutputType.FILE);
                if (screenShotFolder != null && !screenShotFolder.isEmpty() && Files.exists(Paths.get(screenShotFolder))) {
                    if (!screenShotFolder.endsWith("\\")) {
                        screenShotFolder = screenShotFolder + "\\";
                    }
                    FileUtils.copyFile(source, new File(screenShotFolder + screenShotName + ".png"));
                } else { //this will never happen, as the configuration folder is set in the calling method for errors
                    if (!Files.exists(Paths.get("Config/ScreenShots"))) {
                        Files.createDirectory(Paths.get("Config/ScreenShots"));
                    }
                    FileUtils.copyFile(source, new File("Config/ScreenShots/" + screenShotName + ".png"));
                }
                if (!isError) {
                    UpdateTestResults(indent5 + "Screenshot taken");
                } else {
                    UpdateTestResults(ANSI_RED + "Screenshot taken - Error condition!" + ANSI_RESET);
                }
                //resize the browser to the original dimensions
                driver.manage().window().setSize(originalDimension);
                //increment the counter only for non-error conditions
//                if (!isError) {
                    screenShotsTaken++;
//                }
            } catch (Exception e) {
                UpdateTestResults(ANSI_RED + "Exception while taking screenshot (" + screenShotName + "): " + e.getMessage() + ANSI_RESET);
            }
        }
        else if (isError) {
            UpdateTestResults(indent5 + "Screenshot (" + screenShotName + ") for error condition not taken due to screenshot limit.  Increase MaxScreenShotsToTake in configuration file to capture this screenshot.", null);
        }
        else {
            UpdateTestResults(indent5 + "Screenshot (" + screenShotName + ") not taken due to screenshot limit.  Increase MaxScreenShotsToTake in configuration file to capture this screenshot.", null);
        }
    }

    /* ****************************************************************
     *   DESCRIPTION:
     *   This method removes all characters not specified below from
     *   the screen screenshot name passed in to create a legitimate
     *   file name.
     ***************************************************************** */
    private String MakeValidFileName(String screenShotName) {
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyz1234567890_-";
        String cleanValue = "";

        for (int x=0;x<=(screenShotName.length()-1);x++)
        {
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

    /* ****************************************************************
     *   DESCRIPTION:
     *   This method sets the dimensions of the content area to the
     *   dimensions specified by the test script.
     ***************************************************************** */
    public void SetWindowContentDimensions(WebDriver driver, int width, int height)
    {
        Dimension sessionDimension = new Dimension(width, height);
        driver.manage().window().setSize(sessionDimension);
    }

    /* *******************************************************************************************
     *   DESCRIPTION:
     *   This method reads in the test file, parsing each line and creating a new <TestSettings>
     *   object, placing the xPath string into the xPath Property, placing the Expected value string into the
     *   Expected value Property and adding that to the List<TestSettings> ArrayList
     ******************************************************************************************* */
    public List<TestSettings> ReadTestSettingsFile(List<TestSettings> testSettings, String testFileName) throws Exception {
        TestSettings test;
        int requiredFields = 5;
        int lineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(testFileName))) {
            String line;
            String tempLine = "";
            String [] lineValues;
            UpdateTestResults(ANSI_PURPLE + sectionStartFormatLeft + " Start of Reading Test Settings File " + sectionStartFormatRight + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + "Reading file: " + ANSI_RESET + testFileName);
            while ((line = br.readLine()) != null) {
                lineCount++;
                //line comments in this file are indicated with ###
                if (line.indexOf("###") < 0) {
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
                        UpdateTestResults(ANSI_PURPLE + indent5 + "Reading Test File values(xPath = " + test.get_xPath() + ") - (Expected Value = " + test.get_expectedValue() + ")" + ANSI_RESET);
                    }
                    else {
                        tempLine += line + "\r\n";
                    }
                }
            }
            UpdateTestResults(ANSI_PURPLE + sectionEndFormatLeft + "  End of Reading Test Settings File  " + sectionEndFormatRight + ANSI_RESET);
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
                        UpdateTestResults(ANSI_YELLOW + indent5 + "browserType = " + ANSI_RESET + configSettings.get_browserType().toString());
                    }
                    else if (line.toLowerCase().indexOf("testpageroot") >= 0) {
                        configSettings.set_testPageRoot(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "testPageRoot = "  + ANSI_RESET + configSettings.get_testPageRoot());
                    }
                    else if (line.toLowerCase().indexOf("runheadless") >= 0) {
                        configSettings.set_runHeadless(Boolean.parseBoolean(configValue));
                        UpdateTestResults(ANSI_YELLOW + indent5 + "runHeadless = "  + ANSI_RESET + configSettings.get_runHeadless().toString());
                    }
                    else if (line.toLowerCase().indexOf("screenshotsavefolder") >= 0) {
                        configSettings .set_screenShotSaveFolder(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "screenShotSaveFolder = "  + ANSI_RESET + configSettings.get_screenShotSaveFolder());
                    }
                    else if (line.toLowerCase().indexOf("testallbrowsers") >= 0) {
                        configSettings.set_testAllBrowsers(Boolean.parseBoolean(configValue));
                        UpdateTestResults(ANSI_YELLOW + indent5 + "testAllBrowsers = "  + ANSI_RESET + configSettings.get_testAllBrowsers().toString());
                    }
                    else if (line.toLowerCase().indexOf("testfilename") >= 0) {
                        configSettings.set_testSettingsFile(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "testFileName = "  + ANSI_RESET + configSettings.get_testSettingsFile());
                    }
                    else if (line.toLowerCase().indexOf("testfoldername") >= 0) {
                        configSettings.set_testFolderName(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "testFolderName = "  + ANSI_RESET + configSettings.get_testFolderName());
                    }
                    else if (line.toLowerCase().indexOf("specifytestfiles") >= 0) {
                        configSettings.set_specifyFileNames(Boolean.parseBoolean(configValue));
                        UpdateTestResults(ANSI_YELLOW + indent5 + "specifytestfilenames = "  + ANSI_RESET + configSettings.get_specifyFileNames());
                    }
                    else if (line.toLowerCase().indexOf("folderfilefiltertype") >= 0) {
                        configSettings.set_folderFileFilterType(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "FolderFileFilterType = "  + ANSI_RESET + configSettings.get_folderFileFilterType());
                    }
                    else if (line.toLowerCase().indexOf("folderfilefilter") >= 0) {
                        configSettings.set_folderFileFilter(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "FolderFileFilter = "  + ANSI_RESET + configSettings.get_folderFileFilter());
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
                        UpdateTestResults(ANSI_YELLOW + indent5 + "screenShotSaveFolder = "  + ANSI_RESET + configSettings.get_screenShotSaveFolder());
                    }
                }
            }
            if (!configSettings.get_specifyFileNames()) {
                UpdateTestResults( FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionStartFormatLeft + " Start - Retrieving Files in specified folder." + sectionStartFormatRight + ANSI_RESET);
                configSettings.reset_testFiles();
                File temp = new File(configSettings.get_testFolderName());
                configSettings = GetAllFilesInFolder(temp, "txt", configSettings);
                UpdateTestResults(FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionEndFormatLeft + "End Retrieving Files in specified folder." + sectionEndFormatRight + ANSI_RESET);
            }
        }
        catch (Exception e) {
            UpdateTestResults(ANSI_RED + "The following error occurred while attempting to read the configuration file:" + configurationFile + "\\r\\n" + e.getMessage() + ANSI_RESET);
        }
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
            if (testMessage.contains("╗") || testMessage.contains("╝")) {
                WriteToFile(get_logFileName(), PadSection(CleanMessage(testMessage)));
                if (testMessage.contains("end") || testMessage.contains("╝")) {
                    WriteToFile(get_logFileName(),"");
                }
            }
            else {
                WriteToFile(get_logFileName(), CleanMessage(testMessage));
                if (testMessage.startsWith("Successful") || testMessage.startsWith("Failed")) {
                    WriteToFile(get_logFileName(), "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (testMessage.indexOf("Successful") >= 0) {
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
            System.out.println(PadSection(testMessage));
            System.out.println("");
        }
        else if (testMessage.indexOf("[") > 0)
        {
            System.out.println(PadSection(testMessage));
        }
        else {
            System.out.println(testMessage);
        }
        return testResults;
    }

    /* ****************************************************************
     *   DESCRIPTION:
     *   This method removes all ANSI code that does console colorization
     *   so that the plain text can be written to the file.
     ***************************************************************** */
    private String CleanMessage(String testMessage) {

        String cleanMessage = testMessage.replace(ANSI_YELLOW,"")
                .replace(ANSI_RED,"").replace(ANSI_GREEN,"")
                .replace(ANSI_BLUE, "").replace(ANSI_PURPLE,"")
                .replace(ANSI_RESET,"").replace(ANSI_CYAN,"")
                .replace(ANSI_BOLD,"").replace(ANSI_YELLOW_BACKGROUND,"")
                .replace(ANSI_GREEN_BACKGROUND,"").replace(FRAMED,"")
                .replace(ANSI_PURPLE_BACKGROUND,"");

        return cleanMessage;
    }

    /* ****************************************************************
     *   DESCRIPTION:
     *   This method pads the section outline format to a specific length
     *   with additional "═" characters to meet the maxCharacters length.
     ***************************************************************** */
    private String PadSection(String sectionTitle) {
        final int maxCharacters = 130;
        String s = "═";
        int n = sectionTitle.length() < maxCharacters ? maxCharacters - sectionTitle.length() : 0;

        String sRepeated;
        if (n > 0) {
            sRepeated = IntStream.range(0, n).mapToObj(i -> s).collect(Collectors.joining(""));
            sectionTitle = sectionTitle.replace("╗", sRepeated + "╗").replace("╝", sRepeated + "╝");
        }
        return sectionTitle;
    }

    /* ****************************************************************
     *   DESCRIPTION:
     *   This overloaded method writes to the standard output but not
     *   to the log file.
     ***************************************************************** */
    public void UpdateTestResults(String testMessage) {
        //UpdateTestResults(testMessage, null);
        if (testMessage.contains("╗") || testMessage.contains("╝")) {
            testMessage = PadSection(testMessage);
        }
        if ((testMessage.indexOf("--[") > 0 || testMessage.indexOf("══[") > 0) && testMessage.toLowerCase().indexOf("end") > 0)
        {
            System.out.println(testMessage);
            System.out.println();
        }
        else {
            System.out.println(testMessage);
        }
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Writes the passed in file contents into the passed in file.
     *  Will append to an existing file or create a new one if the file doesn't
     *  already exist.
     **************************************************************************** */
    public void WriteToFile(String fileName, String fileContents) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(fileContents);
            writer.newLine();
            writer.close();
        }
        catch(Exception ex) {
            UpdateTestResults(ANSI_RED + ANSI_BOLD + "The following error occurred when attempting to write to the test log file:" + ex.getMessage());
        }
    }

    /* ***************************************************************************
     *  DESCRIPTION:
     *  Creates the help file that describes and outlines the format for both
     *  the Configuration File as well as the Test Script Files.
     *  Checks to see if the file already exists and if it does exist, it is
     *  deleted and then recreated, else it is just created.
     **************************************************************************** */
    public void PrintSamples() throws Exception {

        try {
            File helpFile = new File(get_helpFileName());
            if (helpFile.exists()) {
                helpFile.delete();
            }
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              CONFIGURATION FILE FORMAT                                                                                                 ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "// NOTES: Lines beginning with double slashes denote comments, in the configuration file, and will be ignored by the configuration reader.");
            WriteToFile(get_helpFileName(), "// Configuration files are key=value pairs where you are setting a configurable value using the equal assignment operator");
            WriteToFile(get_helpFileName(), "// TestFileName - names beginning with this are used to point to the file/files containing the test setting commands.");
            WriteToFile(get_helpFileName(), "//    -   The Test Setting Commands file is a described in detail below under the Test File Format Section");
            WriteToFile(get_helpFileName(), "// ScreenShotSaveFolder - folder where screenshots should be saved - Must already exist");
            WriteToFile(get_helpFileName(), "// BrowserType values: Firefox, Chrome, PhantomJS");
            WriteToFile(get_helpFileName(), "// RunHeadless - can be true to run headless or false to show the browser, but PhantomJs is always headless");
            WriteToFile(get_helpFileName(), "// TestAllBrowsers - can be true or false.  If false, BrowserType must be set.  If true, BrowserType is ignored and the program will cycle through all browsers.");
            WriteToFile(get_helpFileName(), "// SpecifyTestFiles - Can be true to specifiy each file and the order that files are run, or false to select a folder of files that will be ordered alphabetically.");
            WriteToFile(get_helpFileName(), "// TestFolderName - will contain the folder where test files exist when SpecifyTestFiles is false.");
            WriteToFile(get_helpFileName(), "// FolderFileFilterType - type of filtering you want to use to select similarly named files within a folder options are: ");
            WriteToFile(get_helpFileName(), "//    -   [Starts With], [Contains] and [Ends With] ");
            WriteToFile(get_helpFileName(), "//    -   [Starts With] - will select only the test files starting with the filter entered");
            WriteToFile(get_helpFileName(), "//    -   [Contains] - will select only test files containing the filter entered");
            WriteToFile(get_helpFileName(), "//    -   [Ends With] - will select only test files ending with the filter entered");
            WriteToFile(get_helpFileName(), "// FolderFileFilter - the filter used to select only matching files within the Test Folder.");
            //WriteToFile(get_helpFileName(), "// MaxScreenShotsToTake - the maximum number of screen non-error shots to take.");
            WriteToFile(get_helpFileName(), "// MaxScreenShotsToTake - the maximum number of screen shots to take including any unscheduled screenshots taken due to an error.");
            WriteToFile(get_helpFileName(), "//    -   When -1, only errors will create screen shots.");
            WriteToFile(get_helpFileName(), "//    -   When 0, there is no limit and all screenshots will be taken.");
            WriteToFile(get_helpFileName(), "//    -   When any other number, that number of screenshots or less will be taken depending upon the test and the max set.");
            WriteToFile(get_helpFileName(), "//    -   Errors like, Element not found, will create a screenshot to allow you to see the page the application was on when the error occurred.");
            WriteToFile(get_helpFileName(), "// In the example configuration file provided below, a single specific test file is being tested, the screen shot folder is specified, ");
            //WriteToFile(get_helpFileName(), "// but only errors will create screenshots, only the test Chrome browser will be used and will be visible, the TestFolderName specified, ");
            WriteToFile(get_helpFileName(), "// but no screenshots will be taken, only the test Chrome browser will be used and will be visible, the TestFolderName specified, ");
            WriteToFile(get_helpFileName(), "// FolderFileFilterType specified, and FolderFileFilter specified  are all disregarded because SpecifiyTestFiles is true, meaning only files specifically specified will be used.");
            WriteToFile(get_helpFileName(), "// The commented test file lines were included so that you can see that duplicate TestFileName0 keys can be used as well as uniquely ");
            WriteToFile(get_helpFileName(), "// named incremental TestFileNames like TestFileName1, TestFileName2 etc.. can be used.  Just ensure that they are not preceded by comment characters, if intended to run.");
            WriteToFile(get_helpFileName(), "//TestFileName0=C:\\TestSettings2.txt");
            WriteToFile(get_helpFileName(), "//TestFileName1=C:\\TestSettings2.txt");
            WriteToFile(get_helpFileName(), "TestFileName0=C:\\TestSettings.txt");
            WriteToFile(get_helpFileName(), "ScreenShotSaveFolder=C:\\ScreenShots\\MySite");
            WriteToFile(get_helpFileName(), "MaxScreenShotsToTake=-1");
            WriteToFile(get_helpFileName(), "BrowserType=Chrome");
            WriteToFile(get_helpFileName(), "RunHeadless=false");
            WriteToFile(get_helpFileName(), "TestAllBrowsers=false");
            WriteToFile(get_helpFileName(), "SpecifyTestFiles=true");
            WriteToFile(get_helpFileName(), "TestFolderName=C:\\MyTestFolder\\");
            WriteToFile(get_helpFileName(), "FolderFileFilterType=Starts_With");
            WriteToFile(get_helpFileName(), "FolderFileFilter=MyPhrase");

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              TEST FILE FORMAT                                                                                                          ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "### All lines beginning with ### are comments, in the Test File, and are disregarded by the Test Application.");
            WriteToFile(get_helpFileName(), "### ╔══════════════════════════════════╦═════════════════════════╦═══════════════════════╦════════════════════════════════════════╦══════════════════════════╗");
            WriteToFile(get_helpFileName(), "### ║ ╠[URL/XPath/CssSelector/TagName] ; [Action/Expected value] ; [Element Lookup Type] ; [Perform Action other than Read Value] ; [Critical Assertion] ╣   ║");
            WriteToFile(get_helpFileName(), "### ╚══════════════════════════════════╩═════════════════════════╩═══════════════════════╩════════════════════════════════════════╩══════════════════════════╝");
            WriteToFile(get_helpFileName(), "### Each test script begins with ╠ (alt + 204) and ends with ╣ (alt + 185).  These line delimiters allow for tests to span multiple lines.");
            WriteToFile(get_helpFileName(), "### Each parameter is separated by a space + semi-colon + space.");
            WriteToFile(get_helpFileName(), "### The first parameter is one of the following: url to navigate to, or Element (xPath, CssSelector, Tag Name, ClassName, ID)");
            WriteToFile(get_helpFileName(), "### The second parameter is the action to take or expected value to retrieve.  For URLs both are required separated by a space then (alt + 206), ");
            WriteToFile(get_helpFileName(), "###     ╬ then space. ' ╬ '  optionally add a second space + (alt + 206) + space delimiter to add a time delay (thread sleep value in milli-seconds) to give the event time to complete.");
            WriteToFile(get_helpFileName(), "###     The format is: Action ╬ Expected Value ╬ Time delay before making the assertion.  Some commands allow for many delimited values in this field.");
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
            WriteToFile(get_helpFileName(), "###  The examples below are are not all inclusive but rather attempt to provide you with a basic understanding\r\n###  so that you can make the necessary changes to accomplish a testing task.");
            WriteToFile(get_helpFileName(), "###  When an error occurs it is most likely due to an element not found and a screenshot will automatically be taken,\r\n### if you haven't set or reached the maximum number of screenshots allowed.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  =========[ NAVIGATION ]========================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  All Navigation steps should be marked as crucial, as all subsequent checks require that navigation to complete successfully!!!");
            WriteToFile(get_helpFileName(), "###  To Navigate and mark that step as crucial");
            WriteToFile(get_helpFileName(), "╠https://www.w3schools.com/bootstrap/tryit.asp?filename=trybs_ref_comp_dropdown-menu&stacked=h ; Navigate ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To Navigate, assert that the URL is what follows the ╬ character and to wait 4 thousand milli-seconds before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "╠https://formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To Navigate and Authenticate with username and password and assert that the URL is what follows the ╬ character and to wait 4 thousand milli-seconds before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "╠https://username:password@formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To Navigate and Authenticate with username and password:");
            WriteToFile(get_helpFileName(), "╠https://username:password@formy-project.herokuapp.com/form ; Navigate ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To Navigate, assert that the URL, add a time delay and set the browser dimensions to 800 width by 800 height:");
            WriteToFile(get_helpFileName(), "╠https://formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000  ╬ w=800 h=800 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ CHECK URL WITHOUT NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check a URL without navigating and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠n/a ; URL ╬ https://formy-project.herokuapp.com/thanks ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ CHECK GET REQUEST STATUS WITHOUT NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check the Get Requests status of a URL without navigating and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  The Space between check and get is optional as shown below.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; checkget ╬ 200 ; n/a ; false ; true╣");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check get ╬ 200 ; n/a ; false ; true╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ CHECK POST REQUEST STATUS WITHOUT NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check the Post Requests status of a URL without navigating and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  The Space between check and get is optional as shown below.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; checkpost ╬ 200 ; n/a ; false ; true╣");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check post ╬ 200 ; n/a ; false ; true╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ CHECK DOCUMENT READY STATE COMPLETE WITHOUT NAVIGATION AS A POST NAVIGATION STEP]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check that the document ready state is complete after previously navigating to a new page and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  Use page as the accessor.  This will be most useful for triggered navigation.");
            WriteToFile(get_helpFileName(), "╠page ; wait ╬ 15 ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            //check document ready complete
            WriteToFile(get_helpFileName(), "###  =========[ CHECK DOCUMENT READY STATE COMPLETE WITH NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check that the document ready state is complete with navigation and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  Use page along with the URL as the accessor separated by a space.  This is useful for explicit navigation.");
            WriteToFile(get_helpFileName(), "╠page https://semantic-ui.com/modules/dropdown.html ; wait ╬ 15 ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");


            WriteToFile(get_helpFileName(), "###  =========[ CHECK ALL PAGE LINKS ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check all page links and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "###  This will check for a status code of 200 for all links on the page but will report the status code for all links.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check links ; n/a ; false ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ CHECK ALL PAGE IMAGE SRC TAGS ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check all page image src tags, to ensure a source exists and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "###  The src tag will be checked to see if it exists and if it returns a status code of 200 for all image sources but will report the status of all image sources.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check images src ; n/a ; false ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");


            WriteToFile(get_helpFileName(), "###  =========[ CHECK ALL PAGE IMAGE ALT TAGS ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To check all page image alt tags, for ADA compliance and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  The alt tag will checked to see if it exists and is not empty.  Empty tags will be flagged as failed.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check images alt ; n/a ; false ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ WAITING A SPECIFIC AMOUNT OF TIME FOR ITEMS TO BE AVAILABLE ]==================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To wait for a specific amount of time before continuing to allow for page loading or script completion");
            WriteToFile(get_helpFileName(), "###  To wait for 5 thousand milli-seconds before continuing onto the next step.");
            WriteToFile(get_helpFileName(), "╠n/a ; Wait ╬ 5000 ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ WAITING FOR THE PRESENCE OF AN ELEMENT ]==================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To wait for an element to be present, requires checking for the element using an accessor unlike waiting a specific amount of time.");
            WriteToFile(get_helpFileName(), "###  To wait for for a maximum of 15 seconds for an element to be present and making this check crucial, use the following.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "╠/html/body/div[4]/div/div[2]/div[4]/div[1]/div[2]/div ; wait ╬ 15 ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ WAITING FOR DOCUMENT READY STATE COMPLETE ]==================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To wait for the page to fully load and document state to be complete, use the following command.");
            WriteToFile(get_helpFileName(), "###  Please note that the accessor is set to page and that an accessor type is present.  Any Accessor Type must be present, although it is not used,");
            WriteToFile(get_helpFileName(), "###  to distinguish this document ready state complete wait from a time interval wait.");
            WriteToFile(get_helpFileName(), "###  To wait for for a maximum of 15 seconds for document state complete and to make this check crucial, use the following.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "//╠page ; wait ╬ 15 ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ FILLING IN TEXT FIELDS ]=============================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To fill in a field by ID and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; John ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "To fill in a field by ID and to make it non-crucial when it contains a reserved command like click.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; sendkeys ╬ click ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ CHECKING A CHECKBOX/RADIOBUTTON - CLICKING A BUTTON ]================================================================================================");
            WriteToFile(get_helpFileName(), "###  To click an element by ID");
            WriteToFile(get_helpFileName(), "╠checkbox-2 ; click ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ CLICKING AN ELEMENT THAT FORCES NAVIGATION ]=========================================================================================================");
            WriteToFile(get_helpFileName(), "###  To click an element by xPath that navigates to a new page and check the url of the new page after waiting 5 thousand milli-seconds for the page to load.");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[1]/h4[3] ; click ╬ https://www.davita.com/education ╬ 5000 ; xPath ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ DOUBLE CLICKING AN ELEMENT ]=========================================================================================================");
            WriteToFile(get_helpFileName(), "###  To double click an element by ID.  If this is a text field, double clicking it will select the first word.");
            WriteToFile(get_helpFileName(), "╠first-name ; doubleclick ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ SELECTING A MENU ITEM ELEMENT ]=========================================================================================================");
            WriteToFile(get_helpFileName(), "###  When sending up, down, right and left arrows as a list of keystrokes, you can use the sendkeys action with a space afterward and an optional time between sending,");
            WriteToFile(get_helpFileName(), "###  along with a list of keys to send, to mimic human typing.  The default interval is 400 milli-seconds but in the example below 600 milli-seconds is used.");
            WriteToFile(get_helpFileName(), "╠/html/body/div[4]/div/div[2]/div[4]/div[1]/div[2]/div ; sendkeys 600 ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Right ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Return ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  You can send these as individual commands as shown below if you need to change the time between commands or to track a particular issue when sending commands.");
            WriteToFile(get_helpFileName(), "╠ /html/body/div[4]/div/div[2]/div[4]/div[1]/div[2]/div ; Keys.ARROW_RIGHT ; xPath ; true ; false ╣");
            WriteToFile(get_helpFileName(), "╠ n/a ; Wait ╬ 400 ; n/a ; true ; false ╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");

            WriteToFile(get_helpFileName(), "###  =========[ RETRIEVING TEXT FROM AN ELEMENT ]====================================================================================================================");
            WriteToFile(get_helpFileName(), "###  Retrieving text is usually non-crucial and test execution can usually continue so the following examples are all non-crucial.  Update based on your requirements.");
            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element by ClassName and make the assertion non-crucial");
            WriteToFile(get_helpFileName(), "╠alert ; The form was successfully submitted! ; ClassName ; false ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element by xPath");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/div[1]/div[1]/ul[1]/li[1]/div[1]/div[1]/h1[1] ; Empower Yourself with Kidney Knowledge ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");

            WriteToFile(get_helpFileName(), "###  =========[ RETRIEVING TEXT FROM AN ELEMENT IN AN IFRAME ]=======================================================================================================");
            WriteToFile(get_helpFileName(), "###  When you are attempting to access an element in an iFrame, you must first switch to that iframe.");
            WriteToFile(get_helpFileName(), "###  The syntax for doing so is placed in the second parameter using the key phrase Switch to iframe ");
            WriteToFile(get_helpFileName(), "###  followed by the name in square brackets as shown in the following example.");
            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element in an iFrame by xPath");
            WriteToFile(get_helpFileName(), "╠/html/body/select ; Switch to iframe [iframeResult] ╬ Volvo ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");


            WriteToFile(get_helpFileName(), "###  =========[ CLICK AN ELEMENT IN AN IFRAME ]=====================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To click an element by xPath in an iFrame");
            WriteToFile(get_helpFileName(), "╠/html/body/div/div/ul/li[1]/a ; Switch to iframe [iframeResult] ╬ click ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To select an option from an HTML Select (drop down/list) element.");
            WriteToFile(get_helpFileName(), "╠option[value='1'] ; click ; CssSelector ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ TAKING SCREENSHOTS ]================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  To take a screen shot/print screen.  The browser will be resized automatically to capture all page content.");
            WriteToFile(get_helpFileName(), "╠n/a ; ScreenShot ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  =========[ SWITCHING BROWSER TABS ]============================================================================================================================");
            WriteToFile(get_helpFileName(), "###  Some actions are related and to avoid unnecessary steps the enter key will be pressed after right clicking and arrowing to a particular item.");
            WriteToFile(get_helpFileName(), "###  To Right click on an element, move down to the first menu item, click it to open in a new tab and switch to the new tab:");
            WriteToFile(get_helpFileName(), "╠//*[@id=\"rso\"]/div[1]/div/div[1]/div/div/div[1]/a ; right click ╬ Keys.Arrow_Down ╬ Switch to tab ; xPath ; true ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To Switch back to the first tab after switching to the second tab");
            WriteToFile(get_helpFileName(), "╠n/a ; Switch to tab 0 ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  ===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
        }
        catch (Exception ex) {
            UpdateTestResults(ANSI_RED + ANSI_BOLD + "Error Writing Help File" + ANSI_RESET);
        }
    }




    //region { Commented Out Code }
//    public List<TestSettings> ReadTestSettingsFileOld(List<TestSettings> testSettings, String testFileName) throws Exception {
//        TestSettings test;
//        int requiredFields = 5;
//        try (BufferedReader br = new BufferedReader(new FileReader(testFileName))) {
//            String line;
//            String [] lineValues;
//            UpdateTestResults(ANSI_PURPLE + sectionStartFormatLeft + "Start of Reading Test Settings file File " + sectionStartFormatRight + ANSI_RESET);
//
//            UpdateTestResults(ANSI_PURPLE + "Reading file: "  + ANSI_RESET + testFileName);
//            while ((line = br.readLine()) != null) {
//                //line comments in this file are indicated with ###
//                if (line.indexOf("###") < 0) {
//                    if (line.indexOf("╚") >= 0) {
//                        UpdateTestResults("The character is recognized: " + line);
//                    }
//                    test = new TestSettings();
//                    lineValues = line.split(";");
//                    if (lineValues.length != requiredFields) {
//                        UpdateTestResults(ANSI_RED + "[ Incorrect file format." + requiredFields + " fields required separated by semi-colons ]" + ANSI_RESET);
//                    }
//                    test.set_xPath(lineValues[0].trim());
//                    test.set_expectedValue(lineValues[1].trim());
//                    test.set_searchType(lineValues[2].trim());
//                    test.setPerformWrite(Boolean.parseBoolean(lineValues[3].trim()));
//                    test.set_isCrucial(Boolean.parseBoolean(lineValues[4].trim()));
//                    testSettings.add(test);
//                    // Show input to user
//                    UpdateTestResults(ANSI_PURPLE + "     Reading Test File values(xPath = " + test.get_xPath() + ") - (Expected Value = " + test.get_expectedValue() + ")" + ANSI_RESET);
//                }
//            }
//            UpdateTestResults(ANSI_PURPLE + sectionEndFormatLeft + "End of Reading Test Settings file File" + sectionEndFormatRight + ANSI_RESET);
//            return testSettings;
//        }
//    }

    //endregion
}
