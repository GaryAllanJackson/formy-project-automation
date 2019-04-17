import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class PageHelper {

    //private String testFileName = "C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\Gary_01\\TestFiles\\TestSettingsFile.txt";

    //a better way to read a file line by line in Java
    //List<String> allLines = Files.readAllLines(Paths.get("/Users/pankaj/Downloads/myfile.txt"));


    //bold
    public static final String ANSI_BOLD = "\u001B[1m";

    //region { System.out Colors }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    //public static final String ANSI_BLACK_ALT = "\033[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BRIGHTWHITETEXT = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BRIGHTWHITE = "\u001B[40m";
    public static final String ANSI_BRIGHTYELLOW = "\u001b[94m";
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
    public static final String sectionLeftDown =  "╔";
    public static final String sectionLeftUp =  "╚";
    public static final String sectionRightDown =  "╗";
    public static final String sectionRightUp =  "╝";
    public static final String subsectionArrowLeft = "«";
    public static final String subsectionArrowRight = "»";
    public static final String iFrameSectionTopLeft = "╒";
    public static final String iFrameSectionTopRight = "╕";
    public static final String iFrameSectionBottomLeft = "╘";
    public static final String iFrameSectionBottomRight = "╛";
    private int screenShotsTaken = 0;
    private int maxScreenShotsToTake = 0;
    private int defaultMilliSecondsForNavigation = 10000;


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
//        UpdateTestResults("In NavigateToPage with no time specification #1.");
        UpdateTestResults(indent8 + "Waiting the default wait time of " + defaultMilliSecondsForNavigation + " milliseconds for navigation to complete!");
        driver.get(webAddress);
//        UpdateTestResults("In NavigateToPage with no time specification #2.");
        Thread.sleep(defaultMilliSecondsForNavigation);
//        UpdateTestResults("In NavigateToPage with no time specification #3.");
    }


    /* ****************************************************************
     *  DESCRIPTION:
     *  Navigates to the web address passed in and sleeps for the number of milliseconds passed in
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress, int milliseconds) throws InterruptedException{

        if (milliseconds > 0) {
            UpdateTestResults(indent8 + "Waiting " + milliseconds  + " milliseconds, as directed, for navigation to complete!");
            driver.get(webAddress);
            Thread.sleep(milliseconds);
        }
        else
        {
            NavigateToPage(driver, webAddress);
            //UpdateTestResults(indent5 + "In NavigateToPage else " + milliseconds + " milliseconds!");
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

                //reset the browser dimensions to capture all content
                Dimension dimension = GetWindowContentDimensions(driver);
                driver.manage().window().setSize(dimension);

                screenShotName = MakeValidFileName(screenShotName);

                //take the screen shot
                TakesScreenshot ts = (TakesScreenshot) driver;
                File source = ts.getScreenshotAs(OutputType.FILE);

                String fileStepIndex;
                if (screenShotName.toLowerCase().contains("assert_fail")) {
                    fileStepIndex = screenShotName.substring(screenShotName.indexOf("_F") + 1, screenShotName.indexOf("_Assert_Fail"));
                }
                else {
                    fileStepIndex = screenShotName.substring(screenShotName.lastIndexOf("_F") + 1, screenShotName.lastIndexOf("_"));
                }

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
                    UpdateTestResults(indent5 + ANSI_GREEN + "Screenshot successfully taken for step " + fileStepIndex + ANSI_RESET, null);
                } else {
                    UpdateTestResults(ANSI_RED + "Screenshot taken for step " + fileStepIndex + " - Error condition!" + ANSI_RESET, null);
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
        UpdateTestResults("");
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
            //UpdateTestResults(ANSI_PURPLE + sectionStartFormatLeft + " Start of Reading Test Settings File " + sectionStartFormatRight + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + sectionLeftDown + PrePostPad("[ Start of Reading Test Settings File  ]", "═", 9, 157) + sectionRightDown + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + indent5 + "Reading file: " + ANSI_RESET + testFileName);
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
                        UpdateTestResults(ANSI_PURPLE + indent5 + "Reading Test File values (Accessor = " + test.get_xPath() + ") - (Expected Value = " + test.get_expectedValue() + ")" + ANSI_RESET);
                    }
                    else {
                        tempLine += line + "\r\n";
                    }
                }
            }
//            UpdateTestResults(ANSI_PURPLE + sectionEndFormatLeft + "  End of Reading Test Settings File  " + sectionEndFormatRight + ANSI_RESET);
            UpdateTestResults(ANSI_PURPLE + sectionLeftUp + PrePostPad("[ End of Reading Test Settings File  ]", "═", 9, 157) + sectionRightUp + ANSI_RESET);
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
        ArrayList<String> tempFiles = new ArrayList<>();

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
//            UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionStartFormatLeft + "Reading Config (" + configurationFile +  ") file" + sectionStartFormatRight + ANSI_RESET);
            UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionLeftDown + PrePostPad("[ Reading Config (" + configurationFile + ") file ]", "═", 9, 157) + sectionRightDown + ANSI_RESET);
            while ((line = br.readLine()) != null) {
                if (line.substring(0,2).indexOf("//") < 0) {
                    configValue = line.substring(line.indexOf("=") + 1);
                    if (line.toLowerCase().indexOf("browsertype") >= 0) {
                        configSettings.set_browserType(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "BrowserType = " + ANSI_RESET + configSettings.get_browserType().toString());
                    }
                    else if (line.toLowerCase().indexOf("testpageroot") >= 0) {
                        configSettings.set_testPageRoot(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "TestPageRoot = "  + ANSI_RESET + configSettings.get_testPageRoot());
                    }
                    else if (line.toLowerCase().indexOf("runheadless") >= 0) {
                        configSettings.set_runHeadless(Boolean.parseBoolean(configValue));
                        UpdateTestResults(ANSI_YELLOW + indent5 + "RunHeadless = "  + ANSI_RESET + configSettings.get_runHeadless().toString());
                    }
                    else if (line.toLowerCase().indexOf("sortspecifiedtestfiles") >= 0) {
                        //UpdateTestResults("SortSpecifiedTestFiles - line = " + line);
                        configSettings.set_sortSpecifiedTestFiles(Boolean.parseBoolean(configValue));
                        UpdateTestResults(ANSI_YELLOW + indent5 + "SortSpecifiedTestFiles = "  + ANSI_RESET + configSettings.get_sortSpecifiedTestFiles().toString());
                    }
                    else if (line.toLowerCase().indexOf("screenshotsavefolder") >= 0) {
                        configSettings .set_screenShotSaveFolder(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "ScreenShotSaveFolder = "  + ANSI_RESET + configSettings.get_screenShotSaveFolder());
                    }
                    else if (line.toLowerCase().indexOf("testallbrowsers") >= 0) {
                        configSettings.set_testAllBrowsers(Boolean.parseBoolean(configValue));
                        UpdateTestResults(ANSI_YELLOW + indent5 + "TestAllBrowsers = "  + ANSI_RESET + configSettings.get_testAllBrowsers().toString());
                    }
                    else if (line.toLowerCase().indexOf("testfilename") >= 0) {
                        tempFiles.add(line);
                        configSettings.set_testSettingsFile(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "TestFileName = "  + ANSI_RESET + configSettings.get_testSettingsFile());
                    }
                    else if (line.toLowerCase().indexOf("testfoldername") >= 0) {
                        configSettings.set_testFolderName(configValue);
                        UpdateTestResults(ANSI_YELLOW + indent5 + "TestFolderName = "  + ANSI_RESET + configSettings.get_testFolderName());
                    }
                    else if (line.toLowerCase().indexOf("specifytestfiles") >= 0) {
                        configSettings.set_specifyFileNames(Boolean.parseBoolean(configValue));
                        UpdateTestResults(ANSI_YELLOW + indent5 + "SpecifyTestFileNames = "  + ANSI_RESET + configSettings.get_specifyFileNames());
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
                        UpdateTestResults(ANSI_YELLOW + indent5 + "MaxScreenShotsToTake = "  + ANSI_RESET + configSettings.get_maxScreenShots());
                    }
                }
            }
            if (!configSettings.get_specifyFileNames()) {
//                UpdateTestResults( FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionStartFormatLeft + " Start - Retrieving Files in specified folder." + sectionStartFormatRight + ANSI_RESET);
                UpdateTestResults(FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW  + sectionLeftDown + PrePostPad("[ Start - Retrieving Files in specified folder. ]", "═", 9, 157) + sectionRightDown + ANSI_RESET);
                configSettings.reset_testFiles();
                File temp = new File(configSettings.get_testFolderName());
                configSettings = GetAllFilesInFolder(temp, "txt", configSettings);
//                UpdateTestResults(FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionEndFormatLeft + "End Retrieving Files in specified folder." + sectionEndFormatRight + ANSI_RESET);
                UpdateTestResults(FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW  + sectionLeftDown + PrePostPad("[ End Retrieving Files in specified folder. ]", "═", 9, 157) + sectionRightDown + ANSI_RESET);
            }
        }
        catch (Exception e) {
            UpdateTestResults(ANSI_RED + "The following error occurred while attempting to read the configuration file:" + configurationFile + "\r\n" + e.getMessage() + ANSI_RESET);
        }

        if (tempFiles.size() > 0 && configSettings.get_specifyFileNames() && configSettings.get_sortSpecifiedTestFiles()) {
            SortTestFiles(tempFiles, configSettings);
        }

//        UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionEndFormatLeft + "End of Reading Configuration File" + sectionEndFormatRight + ANSI_RESET);
        UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionLeftUp + PrePostPad("[ End of Reading Configuration File ]", "═", 9, 157) + sectionRightUp + ANSI_RESET);
        return configSettings;
    }


    /* ******************************************************************
     * Description: This method sorts the test files based on number.
     * First, this method does a text based sort, which will sort files
     * below double digits but it them performs a numeric sort based on the
     * number following TestFileName and orders files based on that number.
     ****************************************************************** */
    private void SortTestFiles(ArrayList<String> tempFiles, ConfigSettings configSettings) {
        configSettings.reset_testSettingsFile();
        Collections.sort(tempFiles);
        String configValue;
        int num1;
        int num2;
        String temp1;
        String temp2;

        for (int y=0;y<tempFiles.size();y++) {
            num1 = parseInt(tempFiles.get(y).substring(12, tempFiles.get(y).indexOf("=")));
//            UpdateTestResults("In outer y loop num1 = " + num1);
            for (int x = 0; x < tempFiles.size(); x++) {
                num2 = parseInt(tempFiles.get(x).substring(12, tempFiles.get(x).indexOf("=")));
//                UpdateTestResults("In inner x loop num2 = " + num2);
                if (x > y && num2 < num1) {
                    //UpdateTestResults("In inner x loop if (x = " + x + " y = " + y + "num1 = " + num1 + " num2 = " + num2);
                    temp1 = tempFiles.get(y);
                    temp2 = tempFiles.get(x);
                    tempFiles.remove(x);
                    tempFiles.remove(y);
                    tempFiles.add(y, temp2);
                    tempFiles.add(x, temp1);
                } else if (x < y && num2 > num1) {
                    //UpdateTestResults("In inner x loop if else (x = " + x + " y = " + y + "num1 = " + num1 + " num2 = " + num2);
                    temp1 = tempFiles.get(y);
                    temp2 = tempFiles.get(x);
                    tempFiles.remove(x);
                    tempFiles.remove(y);
                    tempFiles.add(x, temp1);
                    tempFiles.add(y, temp2);
                }
            }
        }

        for (int x=0;x<tempFiles.size();x++) {
            configValue = tempFiles.get(x).substring(tempFiles.get(x).indexOf("=") + 1);
            configSettings.set_testSettingsFile(configValue, x);
        }
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
                       //UpdateTestResults(temp);
                       UpdateTestResults(indent5 + ANSI_YELLOW + "File: " + ANSI_RESET + temp);
                   }
               }
               else if (configSettings.get_folderFileFilterType().toLowerCase().equals("starts_with")) {
                   if (temp.toLowerCase().startsWith(configSettings.get_folderFileFilter().toLowerCase())) {
                       configSettings.set_testSettingsFile(temp);
                       //UpdateTestResults(temp);
                       UpdateTestResults(indent5 + ANSI_YELLOW + "File: " + ANSI_RESET + temp);
                   }
               }
               else if (configSettings.get_folderFileFilterType().toLowerCase().equals("contains")) {
                   if (temp.toLowerCase().contains(configSettings.get_folderFileFilter().toLowerCase())) {
                       configSettings.set_testSettingsFile(temp);
                       //UpdateTestResults(temp);
                       UpdateTestResults(indent5 + ANSI_YELLOW + "File: " + ANSI_RESET + temp);
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
            if (testMessage.contains(sectionRightDown) || testMessage.contains(sectionRightUp)) {
                WriteToFile(get_logFileName(), PadSection(CleanMessage(testMessage)));
                if (testMessage.contains("end") || testMessage.contains(sectionRightUp)) {
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
            if (testMessage.contains("End")) {
                System.out.println("");
            }
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
            sectionTitle = sectionTitle.replace(sectionRightDown, sRepeated + sectionRightDown).replace(sectionRightUp, sRepeated + sectionRightUp);
        }
        return sectionTitle;
    }

    public String PadIndent(int padSize, int multiplier) {
        String s = " ";
        int n = padSize * multiplier;
        String sRepeated = IntStream.range(0, n).mapToObj(i -> s).collect(Collectors.joining(""));

        return sRepeated;
    }

    public String PadIndent(String chr, int padSize, String value) {
        //String s = " ";
        if (chr.isEmpty()) {
            chr = " ";
        }
        StringBuilder sb = new StringBuilder();

        int n = padSize > value.length() ? padSize - value.length() : 0;
        for (int x=1;x<= n;x++) {
            sb.append(chr);
        }
        return sb.toString();
    }

    public String PrePostPad(String value, String chr, int prePad, int totalSize) {
        if (chr.isEmpty()) {
            chr = " ";
        }
        StringBuilder sb = new StringBuilder();

        int remainingTotal = totalSize > value.length() ? totalSize - value.length() : 0;

        //int n = padSize > value.length() ? padSize - value.length() : 0;
        if (remainingTotal > 0)
            for (int x=1;x<= remainingTotal;x++) {
                sb.append(chr);
                if (x == prePad) {
                    sb.append(value);
                }
            }
        return sb.toString();
    }


    /* ****************************************************************
     *   DESCRIPTION:
     *   This overloaded method writes to the standard output but not
     *   to the log file.
     ***************************************************************** */
    public void UpdateTestResults(String testMessage) {
        //UpdateTestResults(testMessage, null);
        if (testMessage.contains(sectionRightDown) || testMessage.contains(sectionRightUp)) {
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

    public void DeleteFile(String fileName) throws Exception {
        try {
            File fileToDelete = new File(fileName);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
        catch (Exception ex) {
            UpdateTestResults("Error Deleting file: " + ex.getMessage());
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
            WriteToFile(get_helpFileName(), "// BLANK LINES ARE NOT PERMITTED!!!  If you need visual space, start the blank line with double slashes and that is acceptable.");
            WriteToFile(get_helpFileName(), "// Configuration files are key=value pairs where you are setting a configurable value using the equal assignment operator");
            WriteToFile(get_helpFileName(), "// TestFileName - names beginning with this are used to point to the file/files containing the test setting commands.");
            WriteToFile(get_helpFileName(), "//    -   The Test Setting Commands file is a described in detail below under the Test File Format Section");
            WriteToFile(get_helpFileName(), "// ScreenShotSaveFolder - folder where screenshots should be saved - Must already exist");
            WriteToFile(get_helpFileName(), "// BrowserType values: Firefox, Chrome, PhantomJS");
            WriteToFile(get_helpFileName(), "// RunHeadless - can be true to run headless or false to show the browser, but PhantomJs is always headless");
            WriteToFile(get_helpFileName(), "// TestAllBrowsers - can be true or false.  If false, BrowserType must be set.  If true, BrowserType is ignored and the program will cycle through all browsers.");
            WriteToFile(get_helpFileName(), "// SpecifyTestFiles - Can be true to specifiy each file and the order that files are run, or false to select a folder of files that will be ordered alphabetically.");
            WriteToFile(get_helpFileName(), "// SortSpecifiedTestFiles - This setting depends upon SpecifyTestFiles being true.");
            WriteToFile(get_helpFileName(), "//    -   Can be set to false to manually place the files in the order that you want them to be executed. (Default)");
            WriteToFile(get_helpFileName(), "//    -   Can be true to sort the files by the alphabetically and numerically using the number following the word TestFileName.");
            WriteToFile(get_helpFileName(), "//       -   An example of the sorted order follows: (TestFileName0, TestFileName1, TestFileName2 etc..)");
            WriteToFile(get_helpFileName(), "//       -   This forces a sort to be performed on the names so these will sort numerically.");
            WriteToFile(get_helpFileName(), "//       -   If multiple entries have the same number, like (TestFileName0, TestFileName0) those entries will also be sorted alphabetically.");
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
            WriteToFile(get_helpFileName(), "SortSpecifiedTestFiles=false");
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
            WriteToFile(get_helpFileName(), "###     ╬ then space. ' ╬ '  optionally add a second space + (alt + 206) + space delimiter to add a time delay (thread sleep value in milli-seconds)");
            WriteToFile(get_helpFileName(), "###     to give the event time to complete.");
            WriteToFile(get_helpFileName(), "###     The format is: Action ╬ Expected Value ╬ Time delay before making the assertion.  Some commands allow for many delimited values in this field.");
            WriteToFile(get_helpFileName(), "###     For context menu navigation the action can be a chain of up or down arrow keys as well to navigate to the desired menu item.");
            WriteToFile(get_helpFileName(), "###     IMPORTANT: If performing context menu navigation or using arrow keys in general, do not move your mouse or change focus from the browser running the test.");
            WriteToFile(get_helpFileName(), "###     Changing the focus will interupt the test and cause inaccurate results.");
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
            WriteToFile(get_helpFileName(), "###  When an error occurs it is most likely due to an element not found and a screenshot will automatically be taken,\r\n###  if you haven't set or reached the maximum number of screenshots allowed.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  All Navigation steps should be marked as crucial, as all subsequent checks require that navigation to complete successfully!!!");
            WriteToFile(get_helpFileName(), "###  To Navigate and mark that step as crucial");
            WriteToFile(get_helpFileName(), "╠https://www.w3schools.com/bootstrap/tryit.asp?filename=trybs_ref_comp_dropdown-menu&stacked=h ; Navigate ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To Navigate, assert that the URL is what follows the ╬ character and to wait 4 thousand milli-seconds before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "###  PLEASE NOTE: Asserting that the URL is correct does not mean that a server transfer didn't redirect the URL to a different page but leave the URL untouched.");
            WriteToFile(get_helpFileName(), "╠https://formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To Navigate and Authenticate with username and password and assert that the URL is what follows the ╬ character and to wait 4 thousand milli-seconds ");
            WriteToFile(get_helpFileName(), "###        before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "╠https://username:password@formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000 ; n/a ; true ; true╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To Navigate and Authenticate with username and password:");
            WriteToFile(get_helpFileName(), "╠https://username:password@formy-project.herokuapp.com/form ; Navigate ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To Navigate, assert that the URL, add a time delay and set the browser dimensions to 800 width by 800 height:");
            WriteToFile(get_helpFileName(), "╠https://formy-project.herokuapp.com/form ; Navigate ╬ https://formy-project.herokuapp.com/form ╬ 4000  ╬ w=800 h=800 ; n/a ; true ; true╣");
            //WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            //WriteToFile(get_helpFileName(), "###  =========[ ALERT POPUP LOGIN  ]=================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ ALERT POPUP LOGIN ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To login when presented with an alert style popup which could happen upon landing on the site or after the site redirects you, and to make this crucial.");
            WriteToFile(get_helpFileName(), "###  Please note this is for normal passwords which cannot contain spaces or characters that require escaping.");
            WriteToFile(get_helpFileName(),"╠n/a ; login username password ; n/a ; true ; true╣");
            //WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CHECK URL WITHOUT NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK URL WITHOUT NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check a URL without navigating and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠n/a ; URL ╬ https://formy-project.herokuapp.com/thanks ; n/a ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CHECK GET REQUEST STATUS WITHOUT NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK GET REQUEST STATUS WITHOUT NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check the Get Requests status of a URL without navigating and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  The Space between check and get is optional as shown below.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; checkget ╬ 200 ; n/a ; false ; true╣");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check get ╬ 200 ; n/a ; false ; true╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CHECK POST REQUEST STATUS WITHOUT NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK POST REQUEST STATUS WITHOUT NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check the Post Requests status of a URL without navigating and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  The Space between check and get is optional as shown below.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; checkpost ╬ 200 ; n/a ; false ; true╣");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check post ╬ 200 ; n/a ; false ; true╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CHECK DOCUMENT READY STATE COMPLETE WITHOUT NAVIGATION AS A POST NAVIGATION STEP]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK DOCUMENT READY STATE COMPLETE WITHOUT NAVIGATION AS A POST NAVIGATION STEP]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check that the document ready state is complete after previously navigating to a new page and to make it crucial. ");
            WriteToFile(get_helpFileName(), "###  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  Use page as the accessor.  This will be most useful for triggered navigation.");
            WriteToFile(get_helpFileName(), "╠page ; wait ╬ 15 ; xPath ; true ; true╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            //check document ready complete
//            WriteToFile(get_helpFileName(), "###  =========[ CHECK DOCUMENT READY STATE COMPLETE WITH NAVIGATION ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK DOCUMENT READY STATE COMPLETE WITH NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check that the document ready state is complete with navigation and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  Use page along with the URL as the accessor separated by a space.  This is useful for explicit navigation.");
            WriteToFile(get_helpFileName(), "╠page https://semantic-ui.com/modules/dropdown.html ; wait ╬ 15 ; xPath ; true ; true╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");


//            WriteToFile(get_helpFileName(), "###  =========[ CHECK ALL PAGE LINKS ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK ALL PAGE LINKS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check all page links and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "###  This will check for a status code of 200 for all links on the page but will report the status code for all links.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check links ; n/a ; false ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CHECK ALL PAGE IMAGE SRC TAGS ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK ALL PAGE IMAGE SRC TAGS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check all page image src tags, to ensure a source exists and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "###  The src tag will be checked to see if it exists and if it returns a status code of 200 for all image sources but will report the status of all image sources.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check images src ; n/a ; false ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");


//            WriteToFile(get_helpFileName(), "###  =========[ CHECK ALL PAGE IMAGE ALT TAGS ]=======================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECK ALL PAGE IMAGE ALT TAGS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To check all page image alt tags, for ADA compliance and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "###  The alt tag will checked to see if it exists and is not empty.  Empty tags will be flagged as failed.");
            WriteToFile(get_helpFileName(), "╠https://semantic-ui.com/modules/dropdown.html ; check images alt ; n/a ; false ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ WAITING A SPECIFIC AMOUNT OF TIME FOR ITEMS TO BE AVAILABLE ]==================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ WAITING A SPECIFIC AMOUNT OF TIME FOR ITEMS TO BE AVAILABLE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To wait for a specific amount of time before continuing to allow for page loading or script completion");
            WriteToFile(get_helpFileName(), "###  To wait for 5 thousand milli-seconds before continuing onto the next step.");
            WriteToFile(get_helpFileName(), "╠n/a ; Wait ╬ 5000 ; n/a ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ WAITING FOR THE PRESENCE OF AN ELEMENT ]==================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ WAITING FOR THE PRESENCE OF AN ELEMENT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To wait for an element to be present, requires checking for the element using an accessor unlike waiting a specific amount of time.");
            WriteToFile(get_helpFileName(), "###  To wait for for a maximum of 15 seconds for an element to be present and making this check crucial, use the following.");
            WriteToFile(get_helpFileName(), "###  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "╠/html/body/div[4]/div/div[2]/div[4]/div[1]/div[2]/div ; wait ╬ 15 ; xPath ; true ; true╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ WAITING FOR DOCUMENT READY STATE COMPLETE ]==================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ WAITING FOR DOCUMENT READY STATE COMPLETE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To wait for the page to fully load and document state to be complete, use the following command.");
            WriteToFile(get_helpFileName(), "###  Please note that the accessor is set to page and that an accessor type is present.  Any Accessor Type must be present, although it is not used,");
            WriteToFile(get_helpFileName(), "###  to distinguish this document ready state complete wait from a time interval wait.");
            WriteToFile(get_helpFileName(), "###  To wait for for a maximum of 15 seconds for document state complete and to make this check crucial, use the following.");
            WriteToFile(get_helpFileName(), "###  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "//╠page ; wait ╬ 15 ; xPath ; true ; true╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");


            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ UNIQUE IDENTIFIER ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  Before explaining how to fill in text fields, we need to cover the Unique Identifier.");
            WriteToFile(get_helpFileName(), "###  By default, every time a test is run, a unique identifier is created.");
            WriteToFile(get_helpFileName(), "###  This unique identifier is composed of the date and time with no delimiters.");
            WriteToFile(get_helpFileName(), "###  The purpose of this Unique Identifier is to allow rerunning the same tests and generating unique ");
            WriteToFile(get_helpFileName(), "###  values by appending this Unique Identifier to the string.");
            WriteToFile(get_helpFileName(), "###  The Unique Identifier is 17 characters long and has the following format (yyyyMMddHHmmssSSS) ie.(20190402095619991).");
            WriteToFile(get_helpFileName(), "###  -  4 digit year, 2 digit month, 2 digit day, 2 digit hours, 2 digit minutes, 2 digit seconds, 3 digit milliseconds ");
            WriteToFile(get_helpFileName(), "###  In the Filling in and SendKeys sections, there are examples of exactly how to use this.");
            WriteToFile(get_helpFileName(), "###  Anytime, the character sequence without parenthesis (**_uid_**), is used, that value is replaced with the Unique Identifier.");
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ FILLING IN TEXT FIELDS ]=============================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ FILLING IN TEXT FIELDS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To fill in a field by ID and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; John ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To fill in a field by ID, add the Unique Identifier, and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; John**_uid_** ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "");


            WriteToFile(get_helpFileName(), "To fill in a field by ID and to make it non-crucial when it contains a reserved command like click.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; sendkeys ╬ click ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field using the value you persisted in an earlier step use the following.");
            WriteToFile(get_helpFileName(), "╠first-name ; Sendkeys ╬ PersistedString ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field by ID, add the Unique Id, and to make it non-crucial when it contains a reserved command like click.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; sendkeys ╬ click**_uid_** ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field by ID with the persisted value, add the Unique Id, and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "╠first-name ; sendkeys ╬ PersistedString**_uid_** ; ID ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CHECKING A CHECKBOX/RADIOBUTTON - CLICKING A BUTTON ]================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CHECKING A CHECKBOX/RADIOBUTTON - CLICKING A BUTTON ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To click an element by ID");
            WriteToFile(get_helpFileName(), "╠checkbox-2 ; click ; ID ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CLICKING AN ELEMENT THAT FORCES NAVIGATION ]=========================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CLICKING AN ELEMENT THAT FORCES NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To click an element by xPath that navigates to a new page and check the url of the new page after waiting 5 thousand milli-seconds for the page to load.");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[1]/h4[3] ; click ╬ https://www.davita.com/education ╬ 5000 ; xPath ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ DOUBLE CLICKING AN ELEMENT ]=========================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ DOUBLE CLICKING AN ELEMENT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To double click an element by ID.  If this is a text field, double clicking it will select the first word.");
            WriteToFile(get_helpFileName(), "╠first-name ; doubleclick ; ID ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ SELECTING A MENU ITEM ELEMENT ]=========================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ SELECTING A MENU ITEM ELEMENT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  When sending up, down, right and left arrows as a list of keystrokes, you can use the sendkeys action with a space afterward and an optional time between sending,");
            WriteToFile(get_helpFileName(), "###  along with a list of keys to send, to mimic human typing.  The default interval is 400 milli-seconds but in the example below 600 milli-seconds is used.");
            WriteToFile(get_helpFileName(), "╠/html/body/div[4]/div/div[2]/div[4]/div[1]/div[2]/div ; sendkeys 600 ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down\r\n ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Arrow_Right ╬ Keys.Arrow_Down ╬ Keys.Arrow_Down ╬ Keys.Return\r\n ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  You can send these as individual commands as shown below if you need to change the time between commands or to track a particular issue when sending commands.");
            WriteToFile(get_helpFileName(), "╠ /html/body/div[4]/div/div[2]/div[4]/div[1]/div[2]/div ; Keys.ARROW_RIGHT ; xPath ; true ; false ╣");
            WriteToFile(get_helpFileName(), "╠ n/a ; Wait ╬ 400 ; n/a ; true ; false ╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ RETRIEVING TEXT FROM AN ELEMENT ]====================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ RETRIEVING TEXT FROM AN ELEMENT AND MAKING AN EQUALS ASSERTION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  Retrieving text is usually non-crucial and test execution can usually continue so the following examples are all non-crucial.  Update based on your requirements.");
            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element by ClassName and make the assertion non-crucial");
            WriteToFile(get_helpFileName(), "╠alert ; The form was successfully submitted! ; ClassName ; false ; false╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element by xPath");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/div[1]/div[1]/ul[1]/li[1]/div[1]/div[1]/h1[1] ; Empower Yourself with Kidney Knowledge ; xPath ; false ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ RETRIEVING TEXT FROM AN ELEMENT AND MAKING A NOT EQUAL ASSERTION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  Just as there are times when you need to ensure an element's text equals a value, there are times ");
            WriteToFile(get_helpFileName(), "###  when you need to ensure that an element's text does not equal a specific value.");
            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element by xPath and make the not equal assertion non-crucial.");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/form[1]/div[1]/div[4]/div[1] ; != ╬ Highest levl of education ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "###");
            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element by xPath and compare it to the persisted value and assert that it is not equal.");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/form[1]/div[1]/div[4]/div[1] ; != ╬ PersistedString ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ RETRIEVING TEXT FROM AN ELEMENT IN AN IFRAME ]=======================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ RETRIEVING TEXT FROM AN ELEMENT IN AN IFRAME ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  When you are attempting to access an element in an iFrame, you must first switch to that iframe.");
            WriteToFile(get_helpFileName(), "###  The syntax for doing so is placed in the second parameter using the key phrase Switch to iframe ");
            WriteToFile(get_helpFileName(), "###  followed by the name in square brackets as shown in the following example.");
            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element in an iFrame by xPath");
            WriteToFile(get_helpFileName(), "╠/html/body/select ; Switch to iframe [iframeResult] ╬ Volvo ; xPath ; false ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ PERSISTING RETRIEVED TEXT IN A VARIABLE FOR LATER USE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  There may be a need to compare the value retrieved from one element with the value of another.");
            WriteToFile(get_helpFileName(), "###  Unfortunately, this cannot be done directly, but a persist action can be enacted allowing the storage of ");
            WriteToFile(get_helpFileName(), "###  an element value that can then be compared to the value of another element.");
            WriteToFile(get_helpFileName(), "###  This accomplishes comparing one element value with another.");
            WriteToFile(get_helpFileName(), "###  To persist the value of an element, use the following:");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/form[1]/div[1]/div[4]/div[1] ; PersistString ; xPath ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To compare the persisted value to an element, use the following:");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/form[1]/div[1]/div[4]/div[1] ; PersistedString ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To retrieve the text of an element by xPath and compare it to the persisted value and assert that it is not equal.");
            WriteToFile(get_helpFileName(), "╠/html[1]/body[1]/div[1]/form[1]/div[1]/div[4]/div[1] ; != ╬ PersistedString ; xPath ; false ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  Although the following can be found in the sendkeys section, in an effort to group all persistence in one ");
            WriteToFile(get_helpFileName(), "###  location it is duplicated here.");
            WriteToFile(get_helpFileName(), "###  There may be a need to send a persisted value to a control and that can be done as follows.");
            WriteToFile(get_helpFileName(), "###  To send the persisted value to a textbox or textarea form control, use the following:");
            WriteToFile(get_helpFileName(), "╠first-name ; Sendkeys ╬ PersistedString ; ID ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ CLICK AN ELEMENT IN AN IFRAME ]=====================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ CLICK AN ELEMENT IN AN IFRAME ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To click an element by xPath in an iFrame");
            WriteToFile(get_helpFileName(), "╠/html/body/div/div/ul/li[1]/a ; Switch to iframe [iframeResult] ╬ click ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "###  To select an option from an HTML Select (drop down/list) element.");
            WriteToFile(get_helpFileName(), "╠option[value='1'] ; click ; CssSelector ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ TAKING SCREENSHOTS ]================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ TAKING SCREENSHOTS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  To take a screen shot/print screen.  The browser will be resized automatically to capture all page content.");
            WriteToFile(get_helpFileName(), "╠n/a ; ScreenShot ; n/a ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            //WriteToFile(get_helpFileName(), "###  =========[ SWITCHING BROWSER TABS ]============================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ SWITCHING BROWSER TABS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  Some actions are related and to avoid unnecessary steps the enter key will be pressed after right clicking and arrowing to a particular item.");
            WriteToFile(get_helpFileName(), "###  To Right click on an element, move down to the first menu item, click it to open in a new tab and switch to the new tab:");
            WriteToFile(get_helpFileName(), "╠//*[@id=\"rso\"]/div[1]/div/div[1]/div/div/div[1]/a ; right click ╬ Keys.Arrow_Down ╬ Switch to tab ; xPath ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To Switch back to the first tab after switching to the second tab");
            WriteToFile(get_helpFileName(), "╠n/a ; Switch to tab 0 ; n/a ; true ; false╣");
//            WriteToFile(get_helpFileName(), "###  ===============================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ FIND ELEMENTS THAT HAVE SPECIFIC TEXT ]=============================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ FIND ELEMENTS THAT HAVE SPECIFIC TEXT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  There are times when you may need to search for text but do not know the accessor necessary to find that text.");
            WriteToFile(get_helpFileName(), "###  The Find functionality allows you search all elements regardless of type or just all tags of a specific type.");
            WriteToFile(get_helpFileName(), "###  Additionally, the Find functionality returns the xPath of all elements where the text is found but when searching ");
            WriteToFile(get_helpFileName(), "###  for text without specifying a tag, only the actual tag containing the text is returned, not elements in the upper ");
            WriteToFile(get_helpFileName(), "###  hierarchy; however, when using a specific tag, if a child tag of that tag contains the text, the searched tag will be returned ");
            WriteToFile(get_helpFileName(), "###  as successfully containing that text. ");
            WriteToFile(get_helpFileName(), "###  To Find text searching all elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "╠n/a ; find  ╬  ╬ Highest level of education ; n/a ; false ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To Find text searching all div elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "╠n/a ; find  ╬ div ╬ Highest level of education ; n/a ; false ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  To Find text searching all label elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "╠n/a ; find  ╬ label ╬ Highest level of education ; n/a ; false ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

//            WriteToFile(get_helpFileName(), "###  =========[ FIND ELEMENTS THAT CONTAIN TEXT ]=============================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ FIND ELEMENTS THAT CONTAIN TEXT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  There are times when you may need to search for a portion of text but do not know the accessor necessary to find that text.");
            WriteToFile(get_helpFileName(), "###  A specific instance might be when searching for text that would be in a paragraph.  You wouldn't want to add the entire paragraph when you can add a ");
            WriteToFile(get_helpFileName(), "###  snippet to verify that part of it is there. ");
            WriteToFile(get_helpFileName(), "###  Additionally, the Find functionality returns the xPath of all elements where the text is found.");
            WriteToFile(get_helpFileName(), "###  To Find element containing text searching all div elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "╠n/a ; find contains  ╬ div ╬ Highest level ; n/a ; false ; false╣");
//            WriteToFile(get_helpFileName(), "###  ================================================================================================================================================================");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("[ FIND ELEMENTS ON A PAGE TO HELP MAKE A TEST FILE - NOT FOR TESTING BUT FOR HELPING TO CREATE TESTS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "###  IMPORTANT NOTE #1: ANY PARENT ELEMENT WILL CONTAIN THE TEXT OF IT'S CHILD ELEMENT(s) SO TO GET THE ELEMENT THAT ACTUALLY ");
            WriteToFile(get_helpFileName(), "###                  CONTAINS THE INFORMATION DESIRED, TRY TO ELIMINATE THE HIERARCHICAL ITEMS ABOVE THAT ARE NOT DESIRED, ");
            WriteToFile(get_helpFileName(), "###                  LIKE CONTAINER ELEMENTS.  Examples include (html,head,body,div,table)");
            WriteToFile(get_helpFileName(), "###  IMPORTANT NOTE #2: ENSURE THAT YOUR FILE PATH DOES NOT CONTAIN ANY KEYWORD USED FOR ANY OTHER ACTION, OR YOU WILL GET UNEXPECTED RESULTS!!!");
            WriteToFile(get_helpFileName(), "###  A test file needs to be created and you would like to spare yourself the hassle of looking up elements, associated properties and attributes.");
            WriteToFile(get_helpFileName(), "###  To do this, create a test script, with a Navigate command, to Navigate to the page to be tested and then use the ");
            WriteToFile(get_helpFileName(), "###  create_test_page command or the create_test_page_formatted command.");
            WriteToFile(get_helpFileName(), "###  The create_test_page command outputs key value information so that a determination can be made to as to whether an item should be tested and ");
            WriteToFile(get_helpFileName(), "###  it provides all of the information to create the test command but it is not formatted as a test command.");
            WriteToFile(get_helpFileName(), "###  The create_test_page_formatted command outputs the element information in a test command format allowing for quick copy and paste to a test file.");
            WriteToFile(get_helpFileName(), "###  Both files indicate if an element is visible, if an a tag is acting as an anchor or a link.");
            WriteToFile(get_helpFileName(), "###  The Formatted File, will create tests for a tags that check text and href, for images that check src, for text fields it create tests that compare text ");
            WriteToFile(get_helpFileName(), "###        provided with the element text, for text input it creates a sendkeys, for buttons, checkboxes and radio buttons it creates a click, ");
            WriteToFile(get_helpFileName(), "###        and for selects it creates a select command, allowing the user to enter one of the option values that is to be selected.");
            WriteToFile(get_helpFileName(), "###  The create_test_page command and the create_test_page_formatted command take the following test parameters:");
            WriteToFile(get_helpFileName(), "###    - Takes n/a as the accessor ([URL/XPath/CssSelector/TagName/ID/ClassName])");
            WriteToFile(get_helpFileName(), "###    - The Action/Expected Value field takes the create_test_page command and the following parameters:");
            WriteToFile(get_helpFileName(), "###        -    Element Type: A single element with * being all elements and the default if left empty.");
            WriteToFile(get_helpFileName(), "###            -   Elements Include but are not limited to: *, html, head, title, body, a, ol, ul, li, select, input etc...");
            WriteToFile(get_helpFileName(), "###            -   If omitted, this will be * for all elements.");
            WriteToFile(get_helpFileName(), "###        -    File Path and File Name: This is where the results will be written.");
            WriteToFile(get_helpFileName(), "###            -   If omitted, this will be written to the config folder. (/config/newTestFile.txt)");
            WriteToFile(get_helpFileName(), "###        -    A comma delimited list of elements to skip when retrieving all element (*) information.");
            WriteToFile(get_helpFileName(), "###            -   These would usually be elements that do not have text themselves but contain elements that do have text.");
            WriteToFile(get_helpFileName(), "###            -   Do not include spaces between elements, just a comma as follows: html,head,title,body,div");
            WriteToFile(get_helpFileName(), "###            -   Skip elements are ONLY APPLIED WHEN RETRIEVING ALL ELEMENTS and IGNORED WHEN RETRIEVING A SPECIFIC TAG TYPE.");
            WriteToFile(get_helpFileName(), "###    - Takes n/a as the Element Lookup Type.");
            WriteToFile(get_helpFileName(), "###    - Takes true as Perform Action other than Read because it is retrieving information and saving it to a file.");
            WriteToFile(get_helpFileName(), "###    - Takes false, as this is not a test and nothing is being asserted; therefore, nothing is crucial and this setting will be ignored.");
            WriteToFile(get_helpFileName(), "###  The following two examples gets all page elements, saves them to a file, skips a list of container and other elements.");
            WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬  ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_All.txt ╬ html,head,title,meta,script,body,style,a,nav,br,strong,div ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬ * ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_All.txt ╬ html,head,title,meta,script,body,style,a,nav,br,strong,div ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  The following example gets all anchor tag elements, saves them to a file, and ignores the skips list because all elements are not being retrieved.");
            WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬ a ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_A_Only.txt ╬ html,head,title,meta,script,body,style,a,nav,br,strong,div ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  The following example is the correct equivalent of the previous command.");
            WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬ a ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_A.txt ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
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
