//import com.sun.java.util.jar.pack.Attribute;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class TestHelper{

    //region {Properties}
    private String _logFileName;
    public String get_logFileName() {
        return _logFileName;
    }
    public void set_logFileName(String _logFileName) {
        this._logFileName = _logFileName;
    }

    private String _helpFileName;
    public String get_helpFileName() {
        return _helpFileName;
    }

    public void set_helpFileName(String _helpFileName) {
        this._helpFileName = _helpFileName;
    }
    //endregion

    private int screenShotsTaken = 0;
    private int maxScreenShotsToTake = 0;
    private int defaultMilliSecondsForNavigation = 10000;


    /*******************************************************************
     * Description: This method reads the test configuration file
     * and populates the ConfigSettings variable with these settings
     * which in turn direct the test to use the selected browser and
     * to test the configured site.
     * @param configurationFile - Name and Path of the Configuration file
     * @param isExecutedFromMain -
     ****************************************************************** */
    public ConfigSettings ReadConfigurationSettings(String configurationFile, boolean isExecutedFromMain) throws Exception  {
        PrintSamples();
        ConfigSettings configSettings = new ConfigSettings();
        String configValue;
        ArrayList<String> tempFiles = new ArrayList<>();

        File configFile = new File(configurationFile);
        if (!configFile.exists() && isExecutedFromMain) {
            Scanner scanner = new Scanner(System.in);
            UpdateTestResults("Configuration File not found (" + configurationFile + ")", false);
            UpdateTestResults("Enter the path to the config file: ", false);
            String tempconfigurationFile = scanner.nextLine();
            configurationFile = tempconfigurationFile;
            UpdateTestResults("configurationFile = " + configurationFile, false);
        }
        else if (!configFile.exists() && !isExecutedFromMain) {
            UpdateTestResults(  AppConstants.ANSI_RED + AppConstants.ANSI_BOLD + "Configuration File not found! (" + configurationFile + ")", false);
            UpdateTestResults("Place the configuration file in the location above with the name specified and re-run the test.\r\nExiting!!!", false);
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(configurationFile))) {
            String line;
//            UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionStartFormatLeft + "Reading Config (" + configurationFile +  ") file" + sectionStartFormatRight + ANSI_RESET);
            UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + AppConstants.sectionLeftDown + PrePostPad("[ Reading Config (" + configurationFile + ") file ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
            while ((line = br.readLine()) != null) {
                if (line.substring(0,2).indexOf("//") < 0) {
                    configValue = line.substring(line.indexOf("=") + 1);
                    if (line.toLowerCase().indexOf("browsertype") >= 0) {
                        configSettings.set_browserType(configValue);
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "BrowserType = " + AppConstants.ANSI_RESET + configSettings.get_browserType().toString(), false);
                    }
                    else if (line.toLowerCase().indexOf("testpageroot") >= 0) {
                        configSettings.set_testPageRoot(configValue);
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "TestPageRoot = "  + AppConstants.ANSI_RESET + configSettings.get_testPageRoot(), false);
                    }
                    else if (line.toLowerCase().indexOf("runheadless") >= 0) {
                        configSettings.set_runHeadless(Boolean.parseBoolean(configValue));
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "RunHeadless = "  + AppConstants.ANSI_RESET + configSettings.get_runHeadless().toString(), false);
                    }
                    else if (line.toLowerCase().indexOf("sortspecifiedtestfiles") >= 0) {
                        //UpdateTestResults("SortSpecifiedTestFiles - line = " + line);
                        configSettings.set_sortSpecifiedTestFiles(Boolean.parseBoolean(configValue));
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "SortSpecifiedTestFiles = "  + AppConstants.ANSI_RESET + configSettings.get_sortSpecifiedTestFiles().toString(), false);
                    }
                    else if (line.toLowerCase().indexOf("screenshotsavefolder") >= 0) {
                        configSettings .set_screenShotSaveFolder(configValue);
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "ScreenShotSaveFolder = "  + AppConstants.ANSI_RESET + configSettings.get_screenShotSaveFolder(), false);
                    }
                    else if (line.toLowerCase().indexOf("testallbrowsers") >= 0) {
                        configSettings.set_testAllBrowsers(Boolean.parseBoolean(configValue));
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "TestAllBrowsers = "  + AppConstants.ANSI_RESET + configSettings.get_testAllBrowsers().toString(), false);
                    }
                    else if (line.toLowerCase().indexOf("testfilename") >= 0) {
                        tempFiles.add(line);
                        configSettings.set_testSettingsFile(configValue);
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "TestFileName = "  + AppConstants.ANSI_RESET + configSettings.get_testSettingsFile(), false);
                    }
                    else if (line.toLowerCase().indexOf("testfoldername") >= 0) {
                        configSettings.set_testFolderName(configValue);
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "TestFolderName = "  + AppConstants.ANSI_RESET + configSettings.get_testFolderName(), false);
                    }
                    else if (line.toLowerCase().indexOf("specifytestfiles") >= 0) {
                        configSettings.set_specifyFileNames(Boolean.parseBoolean(configValue));
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "SpecifyTestFileNames = "  + AppConstants.ANSI_RESET + configSettings.get_specifyFileNames(), false);
                    }
                    else if (line.toLowerCase().indexOf("folderfilefiltertype") >= 0) {
                        configSettings.set_folderFileFilterType(configValue);
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "FolderFileFilterType = "  + AppConstants.ANSI_RESET + configSettings.get_folderFileFilterType(), false);
                    }
                    else if (line.toLowerCase().indexOf("folderfilefilter") >= 0) {
                        configSettings.set_folderFileFilter(configValue);
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "FolderFileFilter = "  + AppConstants.ANSI_RESET + configSettings.get_folderFileFilter(), false);
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
                        UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "MaxScreenShotsToTake = "  + AppConstants.ANSI_RESET + configSettings.get_maxScreenShots(), false);
                    }
                }
            }
            if (!configSettings.get_specifyFileNames()) {
//                UpdateTestResults( FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionStartFormatLeft + " Start - Retrieving Files in specified folder." + sectionStartFormatRight + ANSI_RESET);
                UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_YELLOW  + AppConstants.sectionLeftDown + PrePostPad("[ Start - Retrieving Files in specified folder. ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
                configSettings.reset_testFiles();
                File temp = new File(configSettings.get_testFolderName());
                configSettings = GetAllFilesInFolder(temp, "txt", configSettings);
//                UpdateTestResults(FRAMED + ANSI_BLUE_BACKGROUND + ANSI_YELLOW + sectionEndFormatLeft + "End Retrieving Files in specified folder." + sectionEndFormatRight + ANSI_RESET);
                UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_YELLOW  + AppConstants.sectionLeftDown + PrePostPad("[ End Retrieving Files in specified folder. ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
            }
        }
        catch (Exception e) {
            UpdateTestResults(AppConstants.ANSI_RED + "The following error occurred while attempting to read the configuration file:" + configurationFile + "\r\n" + e.getMessage() + AppConstants.ANSI_RESET, false);
        }

        if (tempFiles.size() > 0 && configSettings.get_specifyFileNames() && configSettings.get_sortSpecifiedTestFiles()) {
            SortTestFiles(tempFiles, configSettings);
        }

//        UpdateTestResults(FRAMED + ANSI_YELLOW_BACKGROUND + ANSI_BLUE + ANSI_BOLD + sectionEndFormatLeft + "End of Reading Configuration File" + sectionEndFormatRight + ANSI_RESET);
        UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + AppConstants.sectionLeftUp + PrePostPad("[ End of Reading Configuration File ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
        return configSettings;
    }

    /********************************************************************************************
     * DESCRIPTION:  This method reads in the test file, parsing each line and creating a
     * new <TestSettings> object, placing the xPath string into the xPath Property, placing
     * the Expected value string into the Expected value Property and adding that to the
     * List<TestSettings> ArrayList.
     * @param testSteps - List of Test Steps that will be returned to the calling method.
     * @param testXmlFileName - Name and Path of the Test Settings file.
     ******************************************************************************************* */
    public List<TestStep> ReadTestSettingsXmlFile(List<TestStep> testSteps, String testXmlFileName) throws Exception {
        DebugDisplay("#1 IN ReadTestSettingsXmlFile method");
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(testXmlFileName);
            Argument argument;
            List<Argument> argumentList;
            String argumentString = "";
            int argCount = 0;
            DebugDisplay("#2 IN ReadTestSettingsXmlFile method");
            String argumentMessage = null;

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.sectionLeftDown + PrePostPad("[ Start of Reading Test Settings File  ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
            UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent5 + "Reading file: " + AppConstants.ANSI_RESET + testXmlFileName, false);
//            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            //get all steps
            NodeList steps = doc.getElementsByTagName(AppConstants.TestStepNode);

            for (int x = 0; x < steps.getLength(); x++) {
                TestStep testStep = new TestStep();
                //get individual step
                Node nNode = steps.item(x);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    //check that the node exists and if so, get the value or set the corresponding variable to null
                    //command
                    if (eElement.getElementsByTagName(AppConstants.CommandNode).item(0) != null) {
                        testStep.set_command(eElement.getElementsByTagName(AppConstants.CommandNode).item(0).getTextContent());
                    } else {
                        testStep.set_command(null);
                    }

                    //actionType
                    if (eElement.getElementsByTagName(AppConstants.ActionTypeNode).item(0) != null) {
                        testStep.set_actionType(eElement.getElementsByTagName(AppConstants.ActionTypeNode).item(0).getTextContent());
                    } else {
                        testStep.set_actionType(null);
                    }

                    //accessor
                    if (eElement.getElementsByTagName(AppConstants.AccessorNode).item(0) != null) {
                        testStep.set_accessor(eElement.getElementsByTagName(AppConstants.AccessorNode).item(0).getTextContent());
                    } else {
                        testStep.set_accessor(null);
                    }

                    //accessorType
                    if (eElement.getElementsByTagName(AppConstants.AccessorTypeNode).item(0) != null) {
                        testStep.set_accessorType(eElement.getElementsByTagName(AppConstants.AccessorTypeNode).item(0).getTextContent());
                    } else {
                        testStep.set_accessorType(null);
                    }

                    //Crucial
                    if (eElement.getElementsByTagName(AppConstants.CrucialCheckNode).item(0) != null) {
                        if (eElement.getElementsByTagName(AppConstants.CrucialCheckNode).item(0).getTextContent().toLowerCase() == "true") {
                            testStep.set_crucial(true);
                        } else {
                            testStep.set_crucial(false);
                        }
//                        testStep.set_crucial(eElement.getElementsByTagName(AppConstants.CrucialCheckNode).item(0).getTextContent());
                    } else {
                        testStep.set_crucial(null);
                    }

                    if (eElement.getElementsByTagName(AppConstants.ExpectedValueNode).item(0) != null) {
                        testStep.set_expectedValue(eElement.getElementsByTagName(AppConstants.ExpectedValueNode).item(0).getTextContent());
                    } else {
                        testStep.set_expectedValue(null);
                    }

                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent5 + "Reading Test Step:" + AppConstants.ANSI_RESET, false);
//                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Command: " + AppConstants.ANSI_RESET + testStep.get_command());
//                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Accessor: " + AppConstants.ANSI_RESET + testStep.get_accessor());
                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Command: " + AppConstants.ANSI_RESET + testStep.get_command() +
                            AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Expected Value: " + AppConstants.ANSI_RESET + testStep.get_expectedValue(), false);
//                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Expected Value: " + AppConstants.ANSI_RESET + testStep.get_expectedValue());
                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Accessor Type: " + AppConstants.ANSI_RESET + testStep.get_accessorType() +
                            AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Accessor: " + AppConstants.ANSI_RESET + testStep.get_accessor(), false);

                    //get all arguments
                    if (eElement.getElementsByTagName(AppConstants.ArgumentsNode).item(0) != null) {
                        NodeList arguments = eElement.getElementsByTagName(AppConstants.ArgumentsNode).item(0).getChildNodes();
                        if (arguments != null && arguments.getLength() > 0) {
                            argumentList = new ArrayList<>();
                            argCount = 0;
//                            UpdateTestResults("arguments = " + arguments.getLength());
                            for (int a = 0; a < arguments.getLength(); a++) {
                                Node argNode = arguments.item(a);
                                argument = new Argument();
                                if (argNode != null && argNode.getNodeName().startsWith("arg")) {
                                    argCount = argCount + 1;
                                    argument.set_parameter(argNode.getTextContent());
                                    if (argCount % 2 == 0) {
                                        argumentMessage = argumentMessage + "\t\t" + AppConstants.ANSI_PURPLE + argNode.getNodeName() + ": " + AppConstants.ANSI_RESET + argument.get_parameter();
                                        UpdateTestResults(argumentMessage, false);
                                        argumentMessage = null;
                                    } else {
                                        argumentMessage = AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Argument " + argNode.getNodeName() + ": " + AppConstants.ANSI_RESET + argument.get_parameter();
                                    }
                                    //UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Argument " + argNode.getNodeName() + ": " + AppConstants.ANSI_RESET + argument.get_parameter(), false);
                                    argumentList.add(argument);
                                }
                            }
                            testStep.ArgumentList = argumentList;
                            if (argumentMessage != null) {
                                UpdateTestResults(argumentMessage, false);
                                argumentMessage = null;
                            }
                        }
                    }
                    if (testStep != null) {
                        testSteps.add(testStep);
                    }
                }
            }
            UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.sectionLeftUp + PrePostPad("[ End of Reading Test Settings File  ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
        } catch(Exception e) {
            UpdateTestResults("The following error occurred while reading the Test Settings XML file: \r\n" + e.getMessage(), false);
        }
        return testSteps;
    }


    /*****************************************************************
     * DESCRIPTION: Navigates to the web address passed in.
     * @param driver -
     * @param webAddress -
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress) throws InterruptedException{
        UpdateTestResults(AppConstants.indent8 + "Waiting the default wait time of " + defaultMilliSecondsForNavigation + " milliseconds for navigation to complete!", false);
        driver.get(webAddress);
        Thread.sleep(defaultMilliSecondsForNavigation);
    }


    /*****************************************************************
     *  DESCRIPTION:
     *  Navigates to the web address passed in and sleeps for the
     *  number of milliseconds passed in.
     * @param driver -
     * @param webAddress -
     * @param milliseconds -
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress, int milliseconds) throws InterruptedException{
        if (milliseconds > 0) {
            UpdateTestResults(AppConstants.indent8 + "Waiting " + milliseconds  + " milliseconds, as directed, for navigation to complete!", false);
            driver.get(webAddress);
            Thread.sleep(milliseconds);
        }
        else
        {
            NavigateToPage(driver, webAddress);
        }
    }



    /*****************************************************************
     * DESCRIPTION:  This method checks to ensure that Screenshots
     * are configured to be taken.
     * If screenshots are configured, it checks the current number of
     * screenshots against the total taken to ensure that it doesn't
     * take more than configured.  For error screen captures, this
     * count is ignored so that issues can be troubleshot by the user.
     * Saves the current screen dimensions, calls the resize method to
     * expand the screen to capture the entire page and saves a
     * screenshot to the the specified folder.
     * If a screenshot folder is not configured screenshots will be saved in
     * config folder.
     * @param driver -
     * @param screenShotName -
     * @param screenShotFolder -
     * @param isError -
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
                    UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_GREEN + "Screenshot successfully taken for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
                } else {
                    UpdateTestResults(AppConstants.ANSI_RED + "Screenshot taken for step " + fileStepIndex + " - Error condition!" + AppConstants.ANSI_RESET, true);
                }

                //resize the browser to the original dimensions
                driver.manage().window().setSize(originalDimension);
                //increment the counter only for non-error conditions
//                if (!isError) {
                screenShotsTaken++;
//                }
            } catch (Exception e) {
                UpdateTestResults(AppConstants.ANSI_RED + "Exception while taking screenshot (" + screenShotName + "): " + e.getMessage() + AppConstants.ANSI_RESET, true);
            }
        }
        else if (isError) {
            UpdateTestResults(AppConstants.indent5 + "Screenshot (" + screenShotName + ") for error condition not taken due to screenshot limit.  Increase MaxScreenShotsToTake in configuration file to capture this screenshot.", true);
        }
        else {
            UpdateTestResults(AppConstants.indent5 + "Screenshot (" + screenShotName + ") not taken due to screenshot limit.  Increase MaxScreenShotsToTake in configuration file to capture this screenshot.", true);
        }
//        UpdateTestResults("");
    }

    /*****************************************************************
     * DESCRIPTION:  This method gets the dimensions of the content
     * area so that the screen dimensions can be reset before a
     * screen capture to ensure that all content is in the captured image.
     * @param driver
     ***************************************************************** */
    private Dimension GetWindowContentDimensions(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int contentHeight = ((Number) js.executeScript("return document.documentElement.scrollHeight")).intValue();
        int contentWidth = ((Number) js.executeScript("return document.documentElement.scrollWidth")).intValue();

        return new Dimension(contentWidth, contentHeight);
    }

    /*****************************************************************
     * DESCRIPTION:  This method removes all characters not specified
     * below from the screen screenshot name passed in to create a
     * legitimate file name.
     * @param screenShotName - Name of the file where this screenshot
     *                       will be saved.
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

    /*****************************************************************
     * DESCRIPTION:
     * This overloaded method writes to the standard output but not
     * to the log file.
     * @param testMessage - The message that will be displayed on screen.
     ***************************************************************** */
    /*public void UpdateTestResults(String testMessage) {
        //UpdateTestResults(testMessage, null);

        if (testMessage.contains(AppConstants.sectionRightDown) || testMessage.contains(AppConstants.sectionRightUp)) {
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
    }*/

    /****************************************************************************
     *  DESCRIPTION:
     *  Adds a message to the List<String> testResults and writes out the current status to
     *  the log file and then to the screen.
     *  (testResults is not necessary and may be removed or you can write all test
     *  results out when the program ends in the destructor.)
     **************************************************************************** */
   /*public List<String> UpdateTestResults(String testMessage, List<String> testResults) {
        if (testResults != null) {
            testResults.add(testMessage);
        }
        try {
            if (testMessage.contains(AppConstants.sectionRightDown) || testMessage.contains(AppConstants.sectionRightUp)) {
                WriteToFile(get_logFileName(), PadSection(CleanMessage(testMessage)));
                if (testMessage.contains("end") || testMessage.contains(AppConstants.sectionRightUp)) {
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
            System.out.println(AppConstants.ANSI_GREEN + testMessage + AppConstants.ANSI_RESET);
        }
        else if (testMessage.indexOf("Failed") >= 0) {
            System.out.println(AppConstants.ANSI_RED + testMessage + AppConstants.ANSI_RESET);
        }
        else if (testMessage.indexOf("Navigation") >= 0) {
            System.out.println(AppConstants.ANSI_BLUE + testMessage + AppConstants.ANSI_RESET);
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
    }*/

    /****************************************************************************
     * DESCRIPTION:  This new method will display the message on screen and
     * may optionally write the message to
     *
     * @param testMessage - The message that is displayed and optionally
     *                    written to the log.
     * @param  writeToLog - Indicates if this message should be written to the log.
     ****************************************************************************  */
    public void UpdateTestResults(String testMessage, boolean writeToLog) {
//        System.out.println("testMessage = " + testMessage);
//        System.out.println("get_logFileName = " + get_logFileName());
        try {
            if (writeToLog) {
                if (testMessage.contains(AppConstants.sectionRightDown) || testMessage.contains(AppConstants.sectionRightUp)) {
                    WriteToFile(get_logFileName(), PadSection(CleanMessage(testMessage)));
                    if (testMessage.contains("end") || testMessage.contains(AppConstants.sectionRightUp)) {
                        WriteToFile(get_logFileName(), "");
                    }
                } else {
                    WriteToFile(get_logFileName(), CleanMessage(testMessage));
                    if (testMessage.startsWith("Successful") || testMessage.startsWith("Failed")) {
                        WriteToFile(get_logFileName(), "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (testMessage.indexOf("Successful") >= 0) {
            System.out.println(AppConstants.ANSI_GREEN + testMessage + AppConstants.ANSI_RESET);
        }
        else if (testMessage.indexOf("Failed") >= 0) {
            System.out.println(AppConstants.ANSI_RED + testMessage + AppConstants.ANSI_RESET);
        }
        else if (testMessage.indexOf("Navigation") >= 0) {
            System.out.println(AppConstants.ANSI_BLUE + testMessage + AppConstants.ANSI_RESET);
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
    }

    public void DebugDisplay(String message) {
        message = AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BLUE + "Debugging: - " + AppConstants.ANSI_RESET + message;
        UpdateTestResults(message, false);
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
            //writer.close();
        }
        catch(Exception ex) {
            UpdateTestResults(AppConstants.ANSI_RED + AppConstants.ANSI_BOLD + "The following error occurred when attempting to write to the test log file:" + ex.getMessage(), false);
        }
    }

    /******************************************************************************
     * Description: Deletes the file using the fileName passed in
     * @param fileName
     * @throws Exception
     *******************************************************************************/
    public void DeleteFile(String fileName) throws Exception {
        try {
            File fileToDelete = new File(fileName);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
        catch (Exception ex) {
            UpdateTestResults("Error Deleting file: " + ex.getMessage(), false);
        }
    }

    //move these methods to a utility class
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
            UpdateTestResults(AppConstants.ANSI_YELLOW + "Help file name: " + AppConstants.ANSI_RESET + helpFile, false);

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              CONFIGURATION FILE FORMAT                                                                                                 ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "// NOTES: Lines beginning with double slashes denote comments, in the configuration file, and will be ignored by the configuration reader.");
            WriteToFile(get_helpFileName(), "// BLANK LINES ARE NOT PERMITTED!!!  If you need visual space, start the blank line with double slashes and that is acceptable.");
            WriteToFile(get_helpFileName(), "// Configuration files are key=value pairs where you are setting a configurable value using the equal assignment operator");
            WriteToFile(get_helpFileName(), "// TestFileName - entries beginning with this are used to point to the file/files containing the test setting commands.");
            WriteToFile(get_helpFileName(), "//    -   These entries can be numbered so that they can be sorted and taken in order when reading a folder of files or ");
            WriteToFile(get_helpFileName(), "//         - they can have the same entry and be either sorted alphabetically, if SortSpecifiedTestFiles is true, or read ");
            WriteToFile(get_helpFileName(), "//         - in the order in which they appear, if SortSpecifiedTestFiles is set to false.");
            WriteToFile(get_helpFileName(), "//    -   The Test Setting Commands file is a described in detail below under the Test File Format Section.");
            WriteToFile(get_helpFileName(), "//");
            WriteToFile(get_helpFileName(), "// ScreenShotSaveFolder - folder where screenshots should be saved - Must already exist");
            WriteToFile(get_helpFileName(), "// BrowserType values: Firefox, Chrome, PhantomJS");
            WriteToFile(get_helpFileName(), "// RunHeadless - can be true to run headless or false to show the browser, but PhantomJs is always headless");
            WriteToFile(get_helpFileName(), "// TestAllBrowsers - can be true or false.  If false, BrowserType must be set.  If true, BrowserType is ignored and the program will cycle through all browsers.");
            WriteToFile(get_helpFileName(), "// SpecifyTestFiles - Can be true to specifiy each file and the order that files are run, or false to select a folder of files that will be ordered alphabetically.");
            WriteToFile(get_helpFileName(), "// SortSpecifiedTestFiles - This setting depends upon SpecifyTestFiles being true.");
            WriteToFile(get_helpFileName(), "//    -   Can be set to false to manually place the files in the order that you want them to be executed. (Default)");
            WriteToFile(get_helpFileName(), "//    -   Can be true to sort the files alphabetically and numerically using the number following the word TestFileName.");
            WriteToFile(get_helpFileName(), "//       -   An example of the sorted order follows: (TestFileName0, TestFileName1, TestFileName2 etc..)");
            WriteToFile(get_helpFileName(), "//       -   This forces a sort to be performed on the names so these will sort numerically.");
            WriteToFile(get_helpFileName(), "//       -   If multiple entries have the same number, like (TestFileName0, TestFileName0) those entries will also be sorted alphabetically.");
            WriteToFile(get_helpFileName(), "// TestFolderName - will contain the folder where test files exist when SpecifyTestFiles is false.");
            WriteToFile(get_helpFileName(), "// FolderFileFilterType - type of filtering you want to use to select similarly named files within a folder.  Options are: ");
            WriteToFile(get_helpFileName(), "//    -   [Starts With], [Contains] and [Ends With] ");
            WriteToFile(get_helpFileName(), "//    -   [Starts With] - will select only the test files starting with the filter entered");
            WriteToFile(get_helpFileName(), "//    -   [Contains] - will select only test files containing the filter entered");
            WriteToFile(get_helpFileName(), "//    -   [Ends With] - will select only test files ending with the filter entered");
            WriteToFile(get_helpFileName(), "// FolderFileFilter - the filter used to select only matching files within the Test Folder.");
            WriteToFile(get_helpFileName(), "// MaxScreenShotsToTake - the maximum number of screen shots to take including any unscheduled screenshots taken due to an error.");
            WriteToFile(get_helpFileName(), "//    -   When -1, only errors will create screen shots.");
            WriteToFile(get_helpFileName(), "//    -   When 0, there is no limit and all screenshots will be taken.");
            WriteToFile(get_helpFileName(), "//    -   When any other number, that number of screenshots or less will be taken depending upon the test and the max set.");
            WriteToFile(get_helpFileName(), "//    -   Errors like, Element not found, will create a screenshot to allow you to see the page the application was on when ");
            WriteToFile(get_helpFileName(), "//         the error occurred.");
            WriteToFile(get_helpFileName(), "//");
            WriteToFile(get_helpFileName(), "// In the example configuration file provided below, a single specific test file is being tested, the screen shot folder is specified, ");
            WriteToFile(get_helpFileName(), "// but no screenshots will be taken, only the test Chrome browser will be used and it will be visible, the TestFolderName specified, ");
            WriteToFile(get_helpFileName(), "// FolderFileFilterType specified, and FolderFileFilter specified  are all disregarded because SpecifiyTestFiles is true, meaning ");
            WriteToFile(get_helpFileName(), "// only files specifically specified will be used.");
            WriteToFile(get_helpFileName(), "// The commented test file lines were included to show a means in which different files can be setup but can be commented so that only ");
            WriteToFile(get_helpFileName(), "// the intended test files run.   ");
            WriteToFile(get_helpFileName(),"//  These comments were also included  to show that duplicate TestFileName0 keys can be used as well as uniquely named ");
            WriteToFile(get_helpFileName(), "// incremental TestFileNames like TestFileName1, TestFileName2 etc.. can be used.  Just ensure that they are not preceded by ");
            WriteToFile(get_helpFileName(), "// comment characters, if intended to run.  ");
            WriteToFile(get_helpFileName(), "//");
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
            WriteToFile(get_helpFileName(), "The Test file is an xml file that begins with the XML declaration followed by the <testSteps> root element.");
            WriteToFile(get_helpFileName(), "Each test is grouped in <step> elements, which consist of the all or some of the following nodes:\r\n");
            WriteToFile(get_helpFileName(), "<command>Command to execute</command> - The command node, describes the command to execute and is always required.");
            WriteToFile(get_helpFileName(), "<actionType>read/write</actionType> - The actionType node can be set to read or write describing this as a read or write action.");
            WriteToFile(get_helpFileName(), "\tAssertions where an element value is being checked against a supplied value is a read.");
            WriteToFile(get_helpFileName(), "\tNavigation, clicking, populating text boxes, selecting select options, accessing context menu etc.. ");
            WriteToFile(get_helpFileName(), "\t\tare write actionTypes because they are performing an action rather than just reading a value.  ");
            WriteToFile(get_helpFileName(), "<accessor>select-menu</accessor> - The element identifier.");
            WriteToFile(get_helpFileName(), "<accessorType>ID</accessorType> - The type of element identifier. (xPath, ClassName, CssSelector, Id, TagName)");
            WriteToFile(get_helpFileName(), "<expectedValue>What you expect to retrieve</expectedValue> - The optional expectedValue node, if present, ");
            WriteToFile(get_helpFileName(), "\tacts as the expected value of the element value being retrieved as the actual value.");
            WriteToFile(get_helpFileName(), "<crucial>TRUE</crucial> - The crucial node can be set to True or False and determines if testing should stop ");
            WriteToFile(get_helpFileName(), "\tor proceed if the step fails.  Set to True for stop and False for proceed.");
            WriteToFile(get_helpFileName(), "<arguments></arguments> - The arguments node is a container of numbered argument nodes.");
            WriteToFile(get_helpFileName(), "\tArgument order is crucial and out of order arguments can have unpredictable results.");
            WriteToFile(get_helpFileName(), "\tSee the help sections below to learn the order of arguments for each command type.");
            WriteToFile(get_helpFileName(), "<arg1>First argument</arg1> The numbered arguments vary greatly depending upon the type of command.");
            WriteToFile(get_helpFileName(), "\t<arg2>Second argument</arg2>\r\n\t<arg3>Third argument</arg3>");
            WriteToFile(get_helpFileName(), "\tGenerally speaking, these numbered arguments are arranged so that the most relevant pieces are information ");
            WriteToFile(get_helpFileName(), "\tare the first items and the less relevant pieces of information are last.");
            WriteToFile(get_helpFileName(), "There are few, if any, examples that use all nodes, so in the Navigation example below note that this does not use ");
            WriteToFile(get_helpFileName(), "Accessor and AccessorType nodes because it accesses the URL instead of a page element when making an assertion. ");
            WriteToFile(get_helpFileName(), "Also, keep in mind that while this represents a test file, it consists of only one of many possible test steps.\r\n");
            WriteToFile(get_helpFileName(), "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            WriteToFile(get_helpFileName(), "<testSteps>");
            WriteToFile(get_helpFileName(), "\t<step>");
            WriteToFile(get_helpFileName(), "\t<command>navigate</command>");
            WriteToFile(get_helpFileName(), "\t<actionType>write</actionType>");
            WriteToFile(get_helpFileName(), "\t<expectedValue>https://www.marvel.com/</expectedValue>");
            WriteToFile(get_helpFileName(), "\t<crucial>TRUE</crucial>");
            WriteToFile(get_helpFileName(), "\t<arguments>");
            WriteToFile(get_helpFileName(), "\t\t<arg1>https://www.marvel.com</arg1>");
            WriteToFile(get_helpFileName(), "\t\t<arg2>1000</arg2>");
            WriteToFile(get_helpFileName(), "\t</arguments>");
            WriteToFile(get_helpFileName(), "\t</step>");
            WriteToFile(get_helpFileName(), "</testSteps>\r\n");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
        } catch (Exception e) {

        }
    }

    /*********************************************************************
     * DESCRIPTION:
     *      Creates a new timestamp to act as a unique id so that
     *      the same test can be used over and over and append this value
     *      to create a new value.
     ******************************************************************** */
    public String GetUniqueIdentifier() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        String tsString = ts.toString().replace("-","").replace(" ","").replace(":","").replace(".","");

        return tsString;
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
                        UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_YELLOW + "File: " + AppConstants.ANSI_RESET + temp, false);
                    }
                }
                else if (configSettings.get_folderFileFilterType().toLowerCase().equals("starts_with")) {
                    if (temp.toLowerCase().startsWith(configSettings.get_folderFileFilter().toLowerCase())) {
                        configSettings.set_testSettingsFile(temp);
                        //UpdateTestResults(temp);
                        UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_YELLOW + "File: " + AppConstants.ANSI_RESET + temp, false);
                    }
                }
                else if (configSettings.get_folderFileFilterType().toLowerCase().equals("contains")) {
                    if (temp.toLowerCase().contains(configSettings.get_folderFileFilter().toLowerCase())) {
                        configSettings.set_testSettingsFile(temp);
                        //UpdateTestResults(temp);
                        UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_YELLOW + "File: " + AppConstants.ANSI_RESET + temp, false);
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

    /* ****************************************************************
     *   DESCRIPTION:
     *   This method removes all ANSI code that does console colorization
     *   so that the plain text can be written to the file.
     ***************************************************************** */
    private String CleanMessage(String testMessage) {

        String cleanMessage = testMessage.replace(AppConstants.ANSI_YELLOW,"")
                .replace(AppConstants.ANSI_RED,"").replace(AppConstants.ANSI_GREEN,"")
                .replace(AppConstants.ANSI_BLUE, "").replace(AppConstants.ANSI_PURPLE,"")
                .replace(AppConstants.ANSI_RESET,"").replace(AppConstants.ANSI_CYAN,"")
                .replace(AppConstants.ANSI_BOLD,"").replace(AppConstants.ANSI_YELLOW_BACKGROUND,"")
                .replace(AppConstants.ANSI_GREEN_BACKGROUND,"").replace(AppConstants.FRAMED,"")
                .replace(AppConstants.ANSI_PURPLE_BACKGROUND,"");

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
            sectionTitle = sectionTitle.replace(AppConstants.sectionRightDown, sRepeated + AppConstants.sectionRightDown).replace(AppConstants.sectionRightUp, sRepeated + AppConstants.sectionRightUp);
        }
        return sectionTitle;
    }

    /********************************************************************
     * DESCRIPTION: This method creates padding for the left side of
     *              text for alignment purposes.
     * @param padSize
     * @param multiplier
     * @return
     ***********************************************************************/
    public String PadIndent(int padSize, int multiplier) {
        String s = " ";
        int n = padSize * multiplier;
        String sRepeated = IntStream.range(0, n).mapToObj(i -> s).collect(Collectors.joining(""));

        return sRepeated;
    }

    /********************************************************************
     * DESCRIPTION: This method creates padding for the left side of
     *              text for alignment purposes.
     * @param chr -
     * @param padSize
     * @param value
     * @return - string passed in with padding.
     ***********************************************************************/
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

    /********************************************************************
     * DESCRIPTION: This method adds padding before and after the value
     *              passed in to meet the totalSize requirement.
     * @param value - Text requiring padding.
     * @param chr - Character to use as padding.
     * @param prePad - Number of padding characters to add to the
     *               beginning of the text.
     * @param totalSize - Desired Total Size of the text and padding
     * @return - Text with left and right padding to meet the size
     *          requirement passed in.
     ********************************************************************/
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

    /*****************************************************************
     * DESCRIPTION:
     * This method sets the dimensions of the content area to the
     * dimensions specified by the test script.
     * @param driver -
     * @param width -
     * @param height -
     ***************************************************************** */
    public void SetWindowContentDimensions(WebDriver driver, int width, int height)
    {
        Dimension sessionDimension = new Dimension(width, height);
        driver.manage().window().setSize(sessionDimension);
    }

}
