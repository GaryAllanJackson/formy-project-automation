//import com.sun.java.util.jar.pack.Attribute;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
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

    private String navigationMessageIndent;
    public String getNavigationMessageIndent() {
        return navigationMessageIndent;
    }

    public void setNavigationMessageIndent(String navigationMessageIndent) {
        this.navigationMessageIndent = navigationMessageIndent;
    }

    private boolean _executedFromMain;
    public boolean is_executedFromMain() {
        return _executedFromMain;
    }

    public void set_executedFromMain(boolean _executedFromMain) {
        this._executedFromMain = _executedFromMain;
    }

    private boolean _is_Maximized;
    public boolean get_is_Maximized() {return _is_Maximized; }
    public void set_is_Maximized(boolean _is_Maximized) { this._is_Maximized = _is_Maximized ;}

    public Dimension savedDimension = null;


    public ConfigSettings ReadConfigurationSettingsXmlFile(String configurationXmlFile, boolean isExecutedFromMain) throws Exception {
//        DebugDisplay("#1 IN ReadConfigurationSettingsXmlFile method");
        PrintSamples();
        ConfigSettings configSettings = new ConfigSettings();
        String configValue;
        ArrayList<String> tempFiles = new ArrayList<>();
        int testFileCount;

        File configFile = new File(configurationXmlFile);
        if (!configFile.exists() && isExecutedFromMain) {
            Scanner scanner = new Scanner(System.in);
            UpdateTestResults("Configuration File not found (" + configurationXmlFile + ")", false);
            UpdateTestResults("Enter the path to the config file: ", false);
            String tempconfigurationFile = scanner.nextLine();
            configurationXmlFile = tempconfigurationFile;
            UpdateTestResults("configurationFile = " + configurationXmlFile, false);
        }
        else if (!configFile.exists() && !isExecutedFromMain) {
            UpdateTestResults(  AppConstants.ANSI_RED + AppConstants.ANSI_BOLD + "Configuration File not found! (" + configurationXmlFile + ")", false);
            UpdateTestResults("Place the configuration file in the location above with the name specified and re-run the test.\r\nExiting!!!", false);
            return null;
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(configurationXmlFile);

            doc.getDocumentElement().normalize();
            //UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + AppConstants.sectionLeftDown + PrePostPad("[ Reading Config (" + configurationXmlFile + ") file ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
            CreateSectionHeader("[ Reading Config (" + configurationXmlFile + ") file ]", AppConstants.FRAMED + AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, true, true, false);

            //get all steps
            NodeList settings = doc.getElementsByTagName(AppConstants.RootConfigurationNode);
            //get the root node as a node
            Node nNode = settings.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                //ScreenShotSaveFolder - default not set
                configValue = (eElement.getElementsByTagName(AppConstants.ScreenShotSaveFolderNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.ScreenShotSaveFolderNode).item(0).getTextContent() : null;
                configSettings.set_screenShotSaveFolder(configValue);
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "ScreenShotSaveFolder = "  + AppConstants.ANSI_RESET + configSettings.get_screenShotSaveFolder(), false);

                //MaxScreenShotsToTake - Default set to 0
                configValue = (eElement.getElementsByTagName(AppConstants.MaxScreenShotsToTakeNode).item(0) != null) ?
                    eElement.getElementsByTagName(AppConstants.MaxScreenShotsToTakeNode).item(0).getTextContent() : "0";
                configSettings.set_maxScreenShots(parseInt(configValue));
                maxScreenShotsToTake = parseInt(configValue);
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "MaxScreenShotsToTake = "  + AppConstants.ANSI_RESET + configSettings.get_maxScreenShots(), false);

                //BrowserType - default set to Chrome
                configValue = (eElement.getElementsByTagName(AppConstants.BrowserTypeNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.BrowserTypeNode).item(0).getTextContent() : "Chrome";
                configSettings.set_browserType(configValue);
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "BrowserType = " + AppConstants.ANSI_RESET + configSettings.get_browserType().toString(), false);

                //RunHeadless - default set to false
                configValue = (eElement.getElementsByTagName(AppConstants.RunHeadlessNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.RunHeadlessNode).item(0).getTextContent() : "false";
                configSettings.set_runHeadless(Boolean.parseBoolean(configValue));
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "RunHeadless = "  + AppConstants.ANSI_RESET + configSettings.get_runHeadless().toString(), false);

                //TestAllBrowsers - default set to false
                configValue = (eElement.getElementsByTagName(AppConstants.TestAllBrowsersNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.TestAllBrowsersNode).item(0).getTextContent() : "false";
                configSettings.set_testAllBrowsers(Boolean.parseBoolean(configValue));
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "TestAllBrowsers = "  + AppConstants.ANSI_RESET + configSettings.get_testAllBrowsers().toString(), false);

                //SpecifyTestFileNames - default set to true
                configValue = (eElement.getElementsByTagName(AppConstants.SpecifyTestFilesNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.SpecifyTestFilesNode).item(0).getTextContent() : "true";
                configSettings.set_specifyFileNames(Boolean.parseBoolean(configValue));
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "SpecifyTestFileNames = "  + AppConstants.ANSI_RESET + configSettings.get_specifyFileNames(), false);

                //SortSpecifiedTestFiles - default set to false
                configValue = (eElement.getElementsByTagName(AppConstants.SortSpecifiedTestFilesNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.SortSpecifiedTestFilesNode).item(0).getTextContent() : "false";
                configSettings.set_sortSpecifiedTestFiles(Boolean.parseBoolean(configValue));
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "SortSpecifiedTestFiles = "  + AppConstants.ANSI_RESET + configSettings.get_sortSpecifiedTestFiles().toString(), false);

                //TestFolderName - default set to null
                configValue = (eElement.getElementsByTagName(AppConstants.TestFolderNameNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.TestFolderNameNode).item(0).getTextContent() : null;
                configSettings.set_testFolderName(configValue);
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "TestFolderName = "  + AppConstants.ANSI_RESET + configSettings.get_testFolderName(), false);

                //FolderFileFilterType - default set to null
                configValue = (eElement.getElementsByTagName(AppConstants.FolderFileFilterTypeNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.FolderFileFilterTypeNode).item(0).getTextContent() : null;
                configSettings.set_folderFileFilterType(configValue);
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "FolderFileFilterType = "  + AppConstants.ANSI_RESET + configSettings.get_folderFileFilterType(), false);

                //FolderFileFilter - default set to null
                configValue = (eElement.getElementsByTagName(AppConstants.FolderFileFilterNode).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.FolderFileFilterNode).item(0).getTextContent() : null;
                configSettings.set_folderFileFilter(configValue);
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "FolderFileFilter = "  + AppConstants.ANSI_RESET + configSettings.get_folderFileFilter(), false);


                if (eElement.getElementsByTagName(AppConstants.TestFilesNode).item(0) != null) {
                    NodeList testFiles = eElement.getElementsByTagName(AppConstants.TestFilesNode).item(0).getChildNodes();
                    if (testFiles != null && testFiles.getLength() > 0) {
                        tempFiles = new ArrayList<>();
                        testFileCount = 0;
//                        DebugDisplay("TestFiles = " + testFiles.getLength());
                        for (int a = 0; a < testFiles.getLength(); a++) {
                            Node testFileNode = testFiles.item(a);
                            configValue = testFileNode.getTextContent().trim();
                            if (testFileNode.getNodeType() == AppConstants.XmlElementNode && (configValue != null && !configValue.isEmpty())) {
                                tempFiles.add(configValue);
                                configSettings.set_testSettingsFile(configValue);
                                if (configSettings.get_specifyFileNames()) {
                                    UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "TestFileName = " + AppConstants.ANSI_RESET + configSettings.get_testSettingsFile(), false);
                                }
                            }
                        }
                    }
                }

                if (!configSettings.get_specifyFileNames() && (configSettings.get_testFolderName() != null && !configSettings.get_testFolderName().isEmpty())) {
                    //UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_YELLOW + AppConstants.sectionLeftDown + PrePostPad("[ Start - Retrieving Files in specified folder. ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
                    CreateSectionHeader(AppConstants.indent5 + "[ Start - Retrieving Files in specified folder. ]", AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW, true, true, false);
                    configSettings.reset_testFiles();
                    File temp = new File(configSettings.get_testFolderName());
                    //configSettings = GetAllFilesInFolder(temp, "txt", configSettings);
                    configSettings = GetAllFilesInFolder(temp, "xml", configSettings);
                    //UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_YELLOW + AppConstants.sectionLeftUp + PrePostPad("[ End Retrieving Files in specified folder. ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
                    CreateSectionHeader(AppConstants.indent5 + "[ End Retrieving Files in specified folder. ]", AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW, false, true, false);
                }
            }
        } catch (Exception e) {
            UpdateTestResults("The following error occurred while reading the Configuration Settings XML file: \r\n" + e.getMessage(), false);
        }

        if (tempFiles.size() > 0 && configSettings.get_specifyFileNames() && configSettings.get_sortSpecifiedTestFiles()) {
            //SortTestFiles(tempFiles, configSettings);
            SortTestXmlFiles(tempFiles, configSettings);
            UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "[ TestFileNames Re-Sorted, new order shown below ]" + AppConstants.ANSI_RESET, false);
            //CreateSectionHeader(AppConstants.indent5 + "[ TestFileNames Re-Sorted, new order shown below ]", "", AppConstants.ANSI_YELLOW, true, false, false);
            for (int index=0;index<tempFiles.size();index++) {
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent8 + "TestFileName = " + AppConstants.ANSI_RESET + tempFiles.get(index), false);
            }
        }
        //UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + AppConstants.sectionLeftUp + PrePostPad("[ End of Reading Configuration File ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
        CreateSectionHeader("[ End of Reading Configuration File ]", AppConstants.FRAMED + AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_BLUE, false, true, false);
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

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(testXmlFileName);
            Argument argument;
            List<Argument> argumentList;
            String argumentString = "";
            int argCount = 0;
            String argumentMessage = null;

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            CreateSectionHeader("[ Start of Reading Test Settings File  ]", "", AppConstants.ANSI_PURPLE, true, false, false);
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
                        if (eElement.getElementsByTagName(AppConstants.CrucialCheckNode).item(0).getTextContent().toLowerCase().equals("true")) {
                            testStep.set_crucial(true);
                        } else {
                            testStep.set_crucial(false);
                        }
                    } else {
                        testStep.set_crucial(null);
                    }

                    //expected value
                    if (eElement.getElementsByTagName(AppConstants.ExpectedValueNode).item(0) != null) {
                        testStep.set_expectedValue(eElement.getElementsByTagName(AppConstants.ExpectedValueNode).item(0).getTextContent());
                    } else {
                        testStep.set_expectedValue(null);
                    }

                    //Conditional Block Setting
                    if (eElement.getElementsByTagName(AppConstants.IsConditionalBlockNode).item(0) != null) {
                        if (eElement.getElementsByTagName(AppConstants.IsConditionalBlockNode).item(0).getTextContent().toLowerCase().equals("true")) {
                            testStep.set_isConditionalBlock(true);
                        } else {
                            testStep.set_isConditionalBlock(false);
                        }
                    } else {
                        testStep.set_isConditionalBlock(null);
                    }

                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent5 + "Reading Test Step:" + AppConstants.ANSI_RESET, false);
                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Command: " + AppConstants.ANSI_RESET + testStep.get_command() +
                            AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Expected Value: " + AppConstants.ANSI_RESET + testStep.get_expectedValue(), false);
                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Accessor Type: " + AppConstants.ANSI_RESET + testStep.get_accessorType() +
                            AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Accessor: " + AppConstants.ANSI_RESET + testStep.get_accessor(), false);
                    UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Conditional Block Start: " + AppConstants.ANSI_RESET + testStep.get_isConditionalBlock(), false);

                    //get all arguments
                    if (eElement.getElementsByTagName(AppConstants.ArgumentsNode).item(0) != null) {
                        NodeList arguments = eElement.getElementsByTagName(AppConstants.ArgumentsNode).item(0).getChildNodes();
                        if (arguments != null && arguments.getLength() > 0) {
                            argumentList = new ArrayList<>();
                            argCount = 0;
                            for (int a = 0; a < arguments.getLength(); a++) {
                                Node argNode = arguments.item(a);
                                argument = new Argument();
                                if (argNode != null && argNode.getNodeName().startsWith("arg")) {
                                    argCount = argCount + 1;
                                    argument.set_parameter(argNode.getTextContent());
                                    if (argCount % 2 == 0) {
                                        argumentMessage = argumentMessage + "\t\t " + AppConstants.ANSI_PURPLE + argNode.getNodeName() + ": " + AppConstants.ANSI_RESET + argument.get_parameter();
                                        UpdateTestResults(argumentMessage, false);
                                        argumentMessage = null;
                                    } else {
                                        argumentMessage = AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Argument " + argNode.getNodeName() + ": " + AppConstants.ANSI_RESET + argument.get_parameter() + ((argument.get_parameter().length() > 60) ? "\r\n\t\t " : "");
                                    }
                                    argumentList.add(argument);
                                }
                            }
                            testStep.ArgumentList = argumentList;
                            if (argumentMessage != null) {
                                if (argumentMessage.endsWith("\r\n\t\t ")) {
                                    argumentMessage = argumentMessage.substring(0, argumentMessage.lastIndexOf("\r")-1);
                                }
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
            CreateSectionHeader("[ End of Reading Test Settings File  ]", "", AppConstants.ANSI_PURPLE, false, false, false);
        } catch(Exception e) {
            UpdateTestResults("The following error occurred while reading the Test Settings XML file: \r\n" + e.getMessage(), false);
        }
        return testSteps;
    }

    /**********************************************************************************************
     * Description: Creates the Section Headers based on the input parameters.
     * @param sectionHeading - Heading Text but may include colors
     * @param backgroundColor - Background Color, use empty string if not used.
     * @param foregroundColor - Foreground Color, use empty string if not used.
     * @param isSectionStart - True if Section Start, else False
     * @param hasMessageBackground - True if Message portion has background color, else False
     * @param writeToLog - True if writing to Log File, else False
     **********************************************************************************************/
    public void CreateSectionHeader(String sectionHeading, String backgroundColor, String foregroundColor, Boolean isSectionStart, Boolean hasMessageBackground, Boolean writeToLog) {
        //UpdateTestResults(AppConstants.ANSI_PURPLE + AppConstants.sectionLeftUp + PrePostPad("[ End of Reading Test Settings File  ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
        int prePad = 9;
        int totalSize = 157;
        String sectionHeader;
        String leftUpDown = isSectionStart ? AppConstants.sectionLeftDown : AppConstants.sectionLeftUp;
        String rightUpDown = isSectionStart ? AppConstants.sectionRightDown : AppConstants.sectionRightUp;
        String indent = "";
        if (sectionHeading.contains(AppConstants.indent5)) {
            indent = AppConstants.indent5;
            sectionHeading = sectionHeading.replace(indent, "");
        } else if (sectionHeading.contains(AppConstants.indent8)) {
            indent = AppConstants.indent8;
            sectionHeading = sectionHeading.replace(indent, "");
        }

        if (hasMessageBackground) {
            sectionHeader = backgroundColor + foregroundColor + leftUpDown + PrePostPad(sectionHeading, "═", prePad, totalSize) + rightUpDown + AppConstants.ANSI_RESET;
        } else {
            //testHelper.UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + "╚" + testHelper.PrePostPad(AppConstants.ANSI_RESET + AppConstants.ANSI_BRIGHTBLUE  +"[ End Test Application Initialization ]" + AppConstants.FRAMED + AppConstants.ANSI_WHITE_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD, "═", 9, 157) + "╝" + AppConstants.ANSI_RESET, false);
            sectionHeader = backgroundColor + foregroundColor + leftUpDown +  PrePostPad(AppConstants.ANSI_RESET + foregroundColor + sectionHeading + backgroundColor + foregroundColor, "═", prePad, totalSize) + rightUpDown + AppConstants.ANSI_RESET;
        }

        UpdateTestResults(indent + sectionHeader, writeToLog);
    }


    /*****************************************************************
     * DESCRIPTION: Navigates to the web address passed in.
     * @param driver -
     * @param webAddress -
     **************************************************************** */
    public void NavigateToPage(WebDriver driver, String webAddress) throws InterruptedException{
        String indent = getNavigationMessageIndent() != null ? getNavigationMessageIndent() : AppConstants.indent8;
        UpdateTestResults(indent + "Waiting the default wait time of " + defaultMilliSecondsForNavigation + " milliseconds for navigation to complete!", false);
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
        } else {
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
    public void captureScreenShot(WebDriver driver, String screenShotName, String screenShotFolder, boolean isError, String fileStepIndex) {
        if ((maxScreenShotsToTake > 0 && screenShotsTaken < maxScreenShotsToTake) || (maxScreenShotsToTake == 0)) {
            try {
                //get the original dimensions and save them
                Dimension originalDimension = driver.manage().window().getSize();
                //savedDimension = savedDimension == null ? originalDimension : savedDimension;
                int height = originalDimension.height;
                int width = originalDimension.width;
                //region { This is how to get the screen dimensions but found that the maximized value and screen dimensions didn't match }
//                java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//                int screenHeight = screenSize.height;
//                int screenWidth = screenSize.width;
//                DebugDisplay("height = " + height);
//                DebugDisplay("width = " + width);
//                DebugDisplay("screenHeight = " + screenHeight);
//                DebugDisplay("screenWidth = " + screenWidth);
                //endregion

                //reset the browser dimensions to capture all content
                Dimension dimension = GetWindowContentDimensions(driver);
                //if (savedDimension != originalDimension) {
                if (savedDimension != null) {
                    driver.manage().window().setSize(savedDimension);
                } else {
                    driver.manage().window().setSize(dimension);
                }

                screenShotName = MakeValidFileName(screenShotName);
                String fileExtension = screenShotName.endsWith(".png") ? "" : ".png";

                //take the screen shot
                TakesScreenshot ts = (TakesScreenshot) driver;
                File source = ts.getScreenshotAs(OutputType.FILE);

                if (screenShotFolder != null && !screenShotFolder.isEmpty() && Files.exists(Paths.get(screenShotFolder))) {
                    if (!screenShotFolder.endsWith("\\")) {
                        screenShotFolder = screenShotFolder + "\\";
                    }

                    //FileUtils.copyFile(source, new File(screenShotFolder + screenShotName + ".png"));
                    FileUtils.copyFile(source, new File(screenShotFolder + screenShotName + fileExtension));
                } else { //this will never happen, as the configuration folder is set in the calling method for errors
                    if (!Files.exists(Paths.get("Config/ScreenShots"))) {
                        Files.createDirectory(Paths.get("Config/ScreenShots"));
                    }
                    //FileUtils.copyFile(source, new File("Config/ScreenShots/" + screenShotName + ".png"));
                    FileUtils.copyFile(source, new File("Config/ScreenShots/" + screenShotName + fileExtension));
                    screenShotFolder = "Config/ScreenShots/";
                }

                if (!isError) {
                    //UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_GREEN + "Screenshot successfully taken for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
                    String asSpecified = (savedDimension != null) ? " as specified." : " per content area check. ";
                    UpdateTestResults(AppConstants.ANSI_GREEN + "Screenshot successfully taken for step " + fileStepIndex + "\r\n\tImage saved to: " + screenShotFolder + screenShotName + fileExtension + "\r\n\tImage Dimensions: " + GetImageDimensions(screenShotFolder + screenShotName + fileExtension) + asSpecified + AppConstants.ANSI_RESET, true);
                } else {
                    UpdateTestResults(AppConstants.ANSI_RED + "Screenshot taken for step " + fileStepIndex + " - Error condition!" + AppConstants.ANSI_RESET, true);
                }

                //resize the browser to the original dimensions
                if (get_is_Maximized()) {
                    driver.manage().window().maximize();
                } else {
                    driver.manage().window().setSize(originalDimension);
                }
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
        UpdateTestResults("", true);
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
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyz1234567890_-.";
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


    /****************************************************************************
     * DESCRIPTION:  This new method will display the message on screen and
     * may optionally write the message to
     *
     * @param testMessage - The message that is displayed and optionally
     *                    written to the log.
     * @param  writeToLog - Indicates if this message should be written to the log.
     ****************************************************************************  */
    public void UpdateTestResults(String testMessage, boolean writeToLog) {
        try {
            if (writeToLog) {
                if (testMessage.contains(AppConstants.sectionRightDown) || testMessage.contains(AppConstants.sectionRightUp)
                        || testMessage.contains(AppConstants.subsectionArrowRight)) {
                    WriteToFile(get_logFileName(), PadSection(CleanMessage(testMessage)));
                    if (testMessage.toLowerCase().contains("end") || testMessage.contains(AppConstants.sectionRightUp)) {
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

        if (testMessage.contains("Successful")) {
            //System.out.println(AppConstants.ANSI_GREEN + testMessage + AppConstants.ANSI_RESET);
            if (!is_executedFromMain()) {
                System.out.println(AppConstants.ANSI_GREEN_BRIGHT + testMessage + AppConstants.ANSI_RESET);
            } else {
                System.out.println(PadSection(CleanMessage(testMessage)));
            }
        } else if (testMessage.contains("Failed")) {
            if (!is_executedFromMain()) {
                System.out.println(AppConstants.ANSI_RED + testMessage + AppConstants.ANSI_RESET);
            }else {
                System.out.println(PadSection(CleanMessage(testMessage)));
            }
        } else if (testMessage.contains("Navigation")) {
            if (!is_executedFromMain()) {
                System.out.println(AppConstants.ANSI_BLUE + testMessage + AppConstants.ANSI_RESET);
            }else {
                System.out.println(PadSection(CleanMessage(testMessage)));
            }
            if (testMessage.toLowerCase().contains("end")) {
                System.out.println("");
            }
        } else if (testMessage.contains("[") && ((testMessage.toLowerCase().contains("end") && !testMessage.toLowerCase().contains("send") && !testMessage.toLowerCase().contains("end conditional"))
                || testMessage.toLowerCase().contains("revert"))) {
            if (!is_executedFromMain()) {
                System.out.println(PadSection(testMessage));
            } else {
                System.out.println(PadSection(CleanMessage(testMessage)));
            }
            System.out.println("");
        } else if (testMessage.contains("]") && (testMessage.toLowerCase().contains("start") || testMessage.toLowerCase().contains("begin")
                || testMessage.toLowerCase().contains("read") || testMessage.toLowerCase().contains("run")))  {
            if (!is_executedFromMain()) {
                System.out.println(PadSection(testMessage));
            } else {
                System.out.println(PadSection(CleanMessage(testMessage)));
            }
        } else {
            if (!is_executedFromMain()) {
                System.out.println(testMessage);
            } else {
                System.out.println(CleanMessage(testMessage));
            }
        }
    }


    /*****************************************************************************
     * Description: Used for outputting to the screen for debugging purposes
     * @param message - The message that is displayed
     *****************************************************************************/
    public void DebugDisplay(String message) {
        message = AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BLUE + "Debugging: - " + AppConstants.ANSI_RESET + message;
        UpdateTestResults(message, false);
    }


    /********************************************************************************
     * DESCRIPTION: Writes the passed in file contents into the passed in file.
     *              Will append to an existing file or create a new one if the file doesn't
     *              already exist.
     * @param fileName - file to write content into
     * @param fileContents - content to be written to the file.
     * @throws Exception
     ********************************************************************************/
    public void WriteToFile(String fileName, String fileContents) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(fileContents);
            writer.newLine();
            //writer.close();
        }
        catch(Exception ex) {
            if (ex != null && ex.getMessage() != null) {
                UpdateTestResults(AppConstants.ANSI_RED + AppConstants.ANSI_BOLD + "The following error occurred when attempting to write to the test log file:" + ex.getMessage(), false);
            }
        }
    }


    /******************************************************************************
     * Description: Deletes the file using the fileName passed in
     * @param fileName - Name of the file to delete.
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


    /********************************************************************************
     * Description: A safe way to test if the text passed in can be converted to
     *              an integer.
     * @param text
     * @return
     *********************************************************************************/
    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String GetImageDimensions(String imageFileName) throws Exception {
        BufferedImage img = ImageIO.read(new File(imageFileName));
        int width = img.getWidth();
        int height = img.getHeight();

        return "width: " + width + " height: " + height;
    }



    //move these methods to a utility class
    /****************************************************************************
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
            WriteToFile(get_helpFileName(), "║                                              TABLE OF CONTENTS                                                                                                         ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), AppConstants.indent5 + PrePostPad( "[ HELP FILE OVERVIEW  ]", "═", 9, 100));
            WriteToFile(get_helpFileName(), "\t\tAPPLICATION OVERVIEW");
            WriteToFile(get_helpFileName(), "\t\tHELP FILE SECTIONS");
            WriteToFile(get_helpFileName(), "\t\tFUTURE FUNCTIONALITY\r\n");

            WriteToFile(get_helpFileName(), AppConstants.indent5 + PrePostPad( "[ Configuration ]", "═", 9, 100));
            WriteToFile(get_helpFileName(), "\t\tCONFIGURATION OVERVIEW");
            WriteToFile(get_helpFileName(), "\t\tCONFIGURATION FILE FIELDS AND DESCRIPTIONS");
            WriteToFile(get_helpFileName(), "\t\tCONFIGURATION FILE EXAMPLES");
            WriteToFile(get_helpFileName(), "\t\tEXAMPLES EXPLAINED \r\n");

            WriteToFile(get_helpFileName(), AppConstants.indent5 + PrePostPad( "[ Test Steps ]", "═", 9, 100));
            WriteToFile(get_helpFileName(), "\t\tTEST FILE OVERVIEW");
            WriteToFile(get_helpFileName(), "\t\tTEST FILE FIELDS AND DESCRIPTIONS");
            WriteToFile(get_helpFileName(), "\t\tNAVIGATION");
            WriteToFile(get_helpFileName(), "\t\tNAVIGATION WITH SUCCESSFUL NAVIGATION CONFIRMATION");
            WriteToFile(get_helpFileName(), "\t\tNAVIGATION WITH AUTHENTICATION WITH AND WITHOUT NAVIGATION CONFIRMATION");
            WriteToFile(get_helpFileName(), "\t\tLOGIN WITH NAVIGATION");
            WriteToFile(get_helpFileName(), "\t\tALERT POPUP LOGIN");
            WriteToFile(get_helpFileName(), "\t\tCHECK URL WITHOUT NAVIGATION");
            WriteToFile(get_helpFileName(), "\t\tCHECK GET REQUEST STATUS WITHOUT NAVIGATION");
            WriteToFile(get_helpFileName(), "\t\tCHECK POST REQUEST STATUS WITHOUT NAVIGATION");
            WriteToFile(get_helpFileName(), "\t\tCHECK DOCUMENT READY STATE COMPLETE WITHOUT NAVIGATION AS A POST NAVIGATION STEP");
            WriteToFile(get_helpFileName(), "\t\tCHECK DOCUMENT READY STATE COMPLETE WITH NAVIGATION IN A SINGLE STEP");
            WriteToFile(get_helpFileName(), "\t\tSWITCH TO IFRAME");
            WriteToFile(get_helpFileName(), "\t\tCONDITIONAL BLOCKS");
            WriteToFile(get_helpFileName(), "\t\tCHECK AN ANCHOR HREF");
            WriteToFile(get_helpFileName(), "\t\tCHECK ALL PAGE LINKS USING URL");
            WriteToFile(get_helpFileName(), "\t\tCHECK ALL PAGE LINKS WITHOUT USING URL");
            WriteToFile(get_helpFileName(), "\t\tCHECK THE COUNT OF A SPECIFIC ELEMENT ON A PAGE");
            WriteToFile(get_helpFileName(), "\t\tCHECK ALL PAGE IMAGE SRC TAGS WITH SEPARATE NAVIGATION STEP");
            WriteToFile(get_helpFileName(), "\t\tCHECK ALL PAGE IMAGE SRC TAGS WITH NO SEPARATE NAVIGATION STEP");
            WriteToFile(get_helpFileName(), "\t\tCHECK ALL PAGE IMAGE ALT TAGS WITH SEPARATE NAVIGATION STEP");
            WriteToFile(get_helpFileName(), "\t\tCHECK ALL PAGE IMAGE ALT TAGS WITH NO SEPARATE NAVIGATION STEP");
            WriteToFile(get_helpFileName(), "\t\tWAITING A SPECIFIC AMOUNT OF TIME FOR ITEMS TO BE AVAILABLE");
            WriteToFile(get_helpFileName(), "\t\tWAITING FOR THE PRESENCE OF AN ELEMENT");
            WriteToFile(get_helpFileName(), "\t\tWAITING FOR DOCUMENT READY STATE COMPLETE");
            WriteToFile(get_helpFileName(), "\t\tUNIQUE IDENTIFIER");
            WriteToFile(get_helpFileName(), "\t\tPERSISTING RETRIEVED TEXT IN A VARIABLE FOR LATER USE");
            WriteToFile(get_helpFileName(), "\t\tFILLING IN TEXT FIELDS");
            WriteToFile(get_helpFileName(), "\t\tCLICK AN ELEMENT IN AN IFRAME");
            WriteToFile(get_helpFileName(), "\t\tSELECT AN OPTION FROM AN HTML SELECT ELEMENT");
            WriteToFile(get_helpFileName(), "\t\tTAKING SCREENSHOTS");
            WriteToFile(get_helpFileName(), "\t\tSWITCHING BROWSER TABS");
            WriteToFile(get_helpFileName(), "\t\tCLOSE ONE OR ALL OPEN TABS");
            WriteToFile(get_helpFileName(), "\t\tFIND ELEMENTS THAT HAVE SPECIFIC TEXT");
            WriteToFile(get_helpFileName(), "\t\tFIND ELEMENTS THAT CONTAIN TEXT");
            WriteToFile(get_helpFileName(), "\t\tCREATE TEST PAGE COMMAND TO CREATE PAGE TESTS OR FOR PROVIDING DATA TO HELP CREATE TESTS");
            WriteToFile(get_helpFileName(), "\t\tCONNECT TO SQL SERVER DATABASE AND CLOSE THE CONNECTION");
            WriteToFile(get_helpFileName(), "\t\tCLOSING THE DATABASE CONNECTION");
            WriteToFile(get_helpFileName(), "\t\tQUERYING THE SQL SERVER DATABASE");
            WriteToFile(get_helpFileName(), "\t\tRETRIEVING JSON FROM AN API ENDPOINT");
            WriteToFile(get_helpFileName(), "\t\tQUERYING JSON FROM AN API ENDPOINT");
            WriteToFile(get_helpFileName(), "\t\tSAVE JSON TO FILE");
            WriteToFile(get_helpFileName(), "\t\tCHECK COLOR CONTRAST  \r\n");
            WriteToFile(get_helpFileName(), "\t\tCOMPARE IMAGES AND CREATE DIFFERENCE IMAGE \r\n");
            WriteToFile(get_helpFileName(), AppConstants.indent5 + PrePostPad("[ Troubleshooting ]", "═", 9, 100));
            WriteToFile(get_helpFileName(), "\t\tDRIVER ISSUES");
            WriteToFile(get_helpFileName(), "\t\tURL VALIDATION FAILURE");
            WriteToFile(get_helpFileName(), "\t\tMISSING CONFIGURATION FILE");
            WriteToFile(get_helpFileName(), "\t\tUNEXPECTED OUTPUT FROM A TEST STEP");
            WriteToFile(get_helpFileName(), "\t\tOVERALL TEST RESULT SHOWS FAILURE ALTHOUGH TEST STEPS PASS (LAST TEST STEP PASSED)");
            WriteToFile(get_helpFileName(), "\t\tXML DOCUMENT MUST START WITH AND END WITH THE SAME ELEMENT");
            WriteToFile(get_helpFileName(), "\t\tCONTEXT MENU TAB SWITCHING IS FAILING");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ END OF TABLE OF CONTENTS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              HELP FILE OVERVIEW                                                                                                        ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "WELCOME TO THE CONFIGURABLE AUTOMATED TEST APPLICATION!!!");
            WriteToFile(get_helpFileName(), "This application was designed to perform automated testing using configuration and test step files.");
            WriteToFile(get_helpFileName(), "It was intended to remove the need for per project automated test coding, but grew out of the curiosity to ");
            WriteToFile(get_helpFileName(), "learn more about headless testing with Java and Selenium.\r\n");
            WriteToFile(get_helpFileName(), "It covers most testing, including: Navigation, Form Population, Value checking, Value Persistence for use in ");
            WriteToFile(get_helpFileName(), "upcoming test step comparisons or form populations, context menu access, iFrame access, switching  browser tabs, ");
            WriteToFile(get_helpFileName(), "SQL Server Connectivity and Querying, anchor href and text property checking, image src and alt property");
            WriteToFile(get_helpFileName(), "checking, checking color contrast, conditional block to run tests only if a condition step passes,");
            WriteToFile(get_helpFileName(), "and unique value generation so that form population tests can be run over and over using this value to ");
            WriteToFile(get_helpFileName(), "ensure that entry is unique each time.\r\n");
            WriteToFile(get_helpFileName(), "Additionally, it includes waiting a specific length of time, for document state complete, for a specific element, ");
            WriteToFile(get_helpFileName(), "taking screenshots and comparing images using ImageMagick and getting a pixel difference percentage along with a.");
            WriteToFile(get_helpFileName(), "difference image with the differences highlighted.");
            WriteToFile(get_helpFileName(), "An added test step configuration can be used to create a test step file for a specific page and while " +
                                                        "this is not a test, it can make creating test files much faster.\r\n");
            WriteToFile(get_helpFileName(), PrePostPad("[ IMPORTANT NOTES ]", "*", 10, 100));
            WriteToFile(get_helpFileName(), "NOTE: The != operator is currently only supported for the following commands: Assert, Sql Server Query, Check Count.");
            WriteToFile(get_helpFileName(), "\tThere was no viable use case for implementing this functionality for other commands ");
            WriteToFile(get_helpFileName(), "\tand the reason it is not supported in the JSON query is explained in the JSON Query section.\r\n");
            WriteToFile(get_helpFileName(), "NOTE: For most verifiable operations the success or failure output will be left aligned, regardless of the margin of the ");
            WriteToFile(get_helpFileName(), "\tprocess it is reporting on.\r\n\tThis was done purposely so that the results are easy to find and always begin with Success or Failure.");
            WriteToFile(get_helpFileName(), "\tAdditionally, in the console, Successful results are displayed in green, while Failure results are displayed in Red.");
            WriteToFile(get_helpFileName(), "\tAny item appear in the console in Red means that either the item failed or it was configured improperly or an important item");
            WriteToFile(get_helpFileName(), "\twas not configured at all.\r\n\tLOOK AT ALL ITEMS IN RED AND MAKE UPDATES ACCORDINGLY!!!");
            WriteToFile(get_helpFileName(), PrePostPad("", "*", 30, 100) + "\r\n");

            WriteToFile(get_helpFileName(), "This help file is broken up into 4 separate sections:\r\n");
            WriteToFile(get_helpFileName(), "\t1.\tHELP FILE OVERVIEW - This section, which provides an overview of the application and this help file.\r\n");
            WriteToFile(get_helpFileName(), "\t2.\tCONFIGURATION FILE FORMAT  - describes the format of the configuration file and details the use and settings of each field.");
            WriteToFile(get_helpFileName(), "\t\tTwo example configurations are provided to showcase two different means of configuring the way test files are specified.\r\n");
            WriteToFile(get_helpFileName(), "\t3.\tTEST FILE FORMAT - describes the format of the test file(s) and details the use and settings of each field.");
            WriteToFile(get_helpFileName(), "\t\tVarious examples and alternate steps are outlined describing the test settings necessary to perform each test function.\r\n");
            WriteToFile(get_helpFileName(), "\t4.\tTROUBLESHOOTING - describes common issues and how to address them to get the desired results.\r\n");
            WriteToFile(get_helpFileName(), "Future functionality to be added to this application:");
            WriteToFile(get_helpFileName(), "\tGreater Than and Less Than Operator.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis would be a good addition when used with Condtional Blocks.");
            WriteToFile(get_helpFileName(), "\t\t-\tCurrently, only validatable(read actionType) commands can be used for the conditional statement such as an text, src, alt or href assertion or element found.\r\n");
            WriteToFile(get_helpFileName(), "\tColor Contrast code has been implemented to allow for color contrast checking using this page's formula.");
            WriteToFile(get_helpFileName(), "\t\t-\thttps://www.w3.org/TR/AERT/#color-contrast");
            WriteToFile(get_helpFileName(), "\t\t-\tCurrently reviewing and comparing pages to determine whether to implement color contrast using this page:");
            WriteToFile(get_helpFileName(), "\t\t-\thttps://www.w3.org/TR/WCAG20-TECHS/G17.html\r\n");
            WriteToFile(get_helpFileName(), "\tMongoDb Connectivity and Querying will be implemented.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis was partially implemented but abandoned for a later time when there is access to a MongoDB instance.\r\n");

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              CONFIGURATION FILE FORMAT                                                                                                 ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), PrePostPad("[ CONFIGURATION OVERVIEW ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "NOTES: The file format is XML, so to make comments use <!-- to begin the comment and --> to end the comment.");
            WriteToFile(get_helpFileName(), "Comments can span lines but comments cannot include other comment blocks.");
            WriteToFile(get_helpFileName(), "Comments are lightly sprinkled in some of the help XML examples below.");
            WriteToFile(get_helpFileName(), "\tTHIS APPLICATION IGNORES ALL COMMENTS.\r\nCOMMENTS ARE FOR THE USER.");
            WriteToFile(get_helpFileName(), "Some of the comments are descriptive and some show alternative options, but it is suggested that you read the help file");
            WriteToFile(get_helpFileName(), "to best understand how to use this Configurable Automated Testing Application.");
            WriteToFile(get_helpFileName(), "Refer to an XML guide for proper commenting.  Google it!!!!");
            WriteToFile(get_helpFileName(), "Both configuration file examples can be used as starting points, just substitute values accordingly.");
            WriteToFile(get_helpFileName(), "The terms element and node may be used interchangeably below to refer to the same thing.");
            WriteToFile(get_helpFileName(), "The following is an example of a configuration file which will be explained below.");
            WriteToFile(get_helpFileName(), "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
                    "<automatedTestConfiguration>\r\n" +
                    "\t<!-- folder where screenshots should be saved -->\r\n" +
                    "\t<screenShotSaveFolder>C:\\ScreenShots\\Mashup</screenShotSaveFolder>\r\n" +
                    "\t<maxScreenShotsToTake>5</maxScreenShotsToTake>\r\n" +
                    "\t<browserType>Chrome</browserType>\r\n" +
                    "\t<!--<browserType>Firefox</browserType>-->\r\n" +
                    "\t<runHeadless>false</runHeadless>\r\n" +
                    "\t<testAllBrowsers>false</testAllBrowsers>\r\n" +
                    "\t<specifyTestFiles>true</specifyTestFiles>\r\n" +
                    "\t<sortSpecifiedTestFiles>false</sortSpecifiedTestFiles>\r\n" +
                    "\t<!-- Individual File Settings -->\r\n" +
                    "\t<testFiles>\r\n" +
                    "\t\t<!--<testFileName1>C:\\ConfigurableAutomatedTester\\Tests\\SqlServerAccess-Test.xml</testFileName1>\r\n" +
                    "\t\t<testFileName2>C:\\ConfigurableAutomatedTester\\Tests\\RunSqlQueries_Alternate-Test.xml</testFileName2>-->\r\n" +
                    "\t\t<testFileName1>C:\\ConfigurableAutomatedTester\\Tests\\Fill_out_FormMy_Form_and_submit-Test.xml</testFileName1>\r\n" +
                    "\t\t<testFileName2>C:\\ConfigurableAutomatedTester\\Tests\\CheckImageSource-Test.xml</testFileName2>\r\n" +
                    "\t</testFiles>\r\n" +
                    "\t<!-- Folder Testing Settings -->\r\n" +
                    "\t<testFolderName></testFolderName>\r\n" +
                    "\t<folderFileFilterType></folderFileFilterType>\r\n" +
                    "\t<folderFileFilter></folderFileFilter>\r\n" +
                    "</automatedTestConfiguration>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The first line is an XML file declaration and must be present as is.");
            WriteToFile(get_helpFileName(), "All elements are required but not all elements have to have a value.  Read on.");
            WriteToFile(get_helpFileName(), "The <automatedTestConfiguration> is the root element in which all configuration elements are contained.");
            WriteToFile(get_helpFileName(), "\tIt contains no text itself.\r\n");
            WriteToFile(get_helpFileName(), "<!-- folder where screenshots should be saved --> is a comment and was purposely included to demonstrate how to add comments.");
            WriteToFile(get_helpFileName(), "\tComments were added to describe the function of the element that follows it.");
            WriteToFile(get_helpFileName(), "\tSubsequent comments will not be described.\r\n");
            WriteToFile(get_helpFileName(), "The <screenShotSaveFolder></screenShotSaveFolder> element specifies the location where Screen Shots will be saved.");
            WriteToFile(get_helpFileName(), "\tThis folder must already exist!\r\n");
            WriteToFile(get_helpFileName(), "The <maxScreenShotsToTake></maxScreenShotsToTake> element specifies the maximum number of Screen Shots to take.");
            WriteToFile(get_helpFileName(), "\tThis allows the user to understand any hard drive space limitations and prevent screenshots from eating up space.");
            WriteToFile(get_helpFileName(), "\tIn the event that tests fail, this setting can be changed to try to target the issue with a screenshot if the ");
            WriteToFile(get_helpFileName(), "\treported error is insufficient for troubleshooting thoroughly.");
            WriteToFile(get_helpFileName(), "\t\t-   When -1, no screen shots will be taken even if an error occurs.");
            WriteToFile(get_helpFileName(), "\t\t-   When 0, there is no limit and all screenshots will be taken.");
            WriteToFile(get_helpFileName(), "\t\t-   When any other number, that number of screenshots or less will be taken depending upon the test and the max value set.");
            WriteToFile(get_helpFileName(), "\t\t-   Errors like, Element not found, will create a screenshot to allow you to see the page the application was on when ");
            WriteToFile(get_helpFileName(), "\t\t\tthe error occurred.\r\n");
            WriteToFile(get_helpFileName(), "The <browserType></browserType> element specifies the type of browser to use when running the test(s).");
            WriteToFile(get_helpFileName(), "\t-Valid values are: Firefox, Chrome, PhantomJS\r\n");
            WriteToFile(get_helpFileName(), "The <runHeadless></runHeadless> element specifies whether to run the test(s) in headless mode.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis is a boolean field and can be true or false.  The case does not matter.");
            WriteToFile(get_helpFileName(), "\tHeadless mode means that the browser does not display on-screen and is used with automation servers");
            WriteToFile(get_helpFileName(), "\tto allow running automated tests as part of the build process.");
            WriteToFile(get_helpFileName(), "\t\t-\tPhantomJS always runs headless if selected regardless of the runHeadless setting.");
            WriteToFile(get_helpFileName(), "\t\t-\tTo watch the test, use Chrome or Firefox.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf not specified, Chrome is the default and will be used.\r\n");
            WriteToFile(get_helpFileName(), "The <testAllBrowsers></testAllBrowsers> element specifies whether the test(s) should run in all browsers or just the selected browser.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis is a boolean field and can be true or false.  The case does not matter.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf false, BrowserType must be set and only that browser will be used when running tests.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf true, BrowserType is ignored and the program will cycle through all browsers.\r\n");
            WriteToFile(get_helpFileName(), "The <specifyTestFiles></specifyTestFiles> element specifies whether or not the configuration file will list the test files to be used.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis is a boolean field and can be true or false.  The case does not matter.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf true, the files specified will be used in the order in which they are listed.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf false, the files will be ignored as this indicates that the folder settings will be used instead.\r\n");
            WriteToFile(get_helpFileName(), "The <sortSpecifiedTestFiles></sortSpecifiedTestFiles> element specifies whether or not to sort the test files.");
            WriteToFile(get_helpFileName(), "\tThis setting made sense in the old system but makes much less sense now that files are physically listed numerically.");
            WriteToFile(get_helpFileName(), "\tThe number no longer has meaning in the sort as each entry should be entered in numerical order, so the number will not be used for sorting.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf false, files are taken in the order in which they are physically listed, which should be numerically.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf true, files will be sorted alphabetically and re-listed on the screen to show the order in which they will execute.\r\n");
            WriteToFile(get_helpFileName(), "The <testFiles></testFiles> element is a container element for the testFileName elements and has no textual content of its own.\r\n");
            WriteToFile(get_helpFileName(), "The <testFileName1></testFileName1> element specifies the test file to use for the test and each element should end with an incremental numeric value.");
            WriteToFile(get_helpFileName(), "\tExample:\r\n\t\t<testFileName1></testFileName1>\r\n\t\t<testFileName2></testFileName2>\r\n");
            WriteToFile(get_helpFileName(), "\tIt should be noted that while this ending numeric value should be incremental, the application will read all ");
            WriteToFile(get_helpFileName(), "\ttestFileName nodes in the order they are entered regardless of the numbering.");
            WriteToFile(get_helpFileName(), "\tTo avoid any possible issues related to this, it is suggested that you follow the best practice and number appropriately, as described.");
            WriteToFile(get_helpFileName(), "\tThe commented test file lines were included to show a means in which different files can be setup but can be commented so that only ");
            WriteToFile(get_helpFileName(), "\tthe intended test files run.\r\n");
            WriteToFile(get_helpFileName(), "---------------------[ Start of Grouped Configuration Settings ]-------------------");
            WriteToFile(get_helpFileName(), "The following three settings need to be talked about together since they work together to provide a particularly useful piece of functionality.");
            WriteToFile(get_helpFileName(), "Together, the following settings allow for filtering a particular folder for the files used for testing.");
            WriteToFile(get_helpFileName(), "If a common naming convention is used for test files that include the project name, these settings allow for running ");
            WriteToFile(get_helpFileName(), "Just one Project's test files, just some of a Project's test files or just one test file in a project or one type of test file across all test projects.\r\n");
            WriteToFile(get_helpFileName(), "The <testFolderName></testFolderName> element specifies the folder where test files are located to allow for reading a folder of ");
            WriteToFile(get_helpFileName(), "\ttest files instead of naming each test file individually.\r\n");
            WriteToFile(get_helpFileName(), "The <folderFileFilterType></folderFileFilterType> element specifies the type of filtering to perform on the files in the folder.  Options are: ");
            WriteToFile(get_helpFileName(), "\t-\t[Starts With], [Contains] and [Ends With] ");
            WriteToFile(get_helpFileName(), "\t\t-\t[Starts With] - will select only the test files starting with the filter entered");
            WriteToFile(get_helpFileName(), "\t\t-\t[Contains] - will select only test files containing the filter entered");
            WriteToFile(get_helpFileName(), "\t\t-\t[Ends With] - will select only test files ending with the filter entered\r\n");
            WriteToFile(get_helpFileName(), "The <folderFileFilter></folderFileFilter> element specifies the phrase to use to when selecting files in the specified folder.\r\n");
            WriteToFile(get_helpFileName(), "\tWhen used with the other folder settings, a folder containing a multitude of test files can be pointed to using the <testFolderName></testFolderName> element.");
            WriteToFile(get_helpFileName(), "\tThen, using the <folderFileFilterType></folderFileFilterType> element, [Starts With] can be used to return only files starting with a specific value.");
            WriteToFile(get_helpFileName(), "\tFinally, using the <folderFileFilter></folderFileFilter> element, a phrase like the project name can be used to select only files in the selected folder that start with the project name.");
            WriteToFile(get_helpFileName(), "---------------------[ End of Grouped Configuration Settings ]-------------------");

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "In the example configuration file provided above the explanations, two specific test files are being tested, the screen shot folder is specified, ");
            WriteToFile(get_helpFileName(), "a maximum of 5 screenshots will be taken, only the test Chrome browser will be used and it will be visible, the files will be run in the order entered,");
            WriteToFile(get_helpFileName(), "the TestFolderName, FolderFileFilterType and FolderFileFilter have not been specified but would have been disregarded if specified because");
            WriteToFile(get_helpFileName(), "<specifyTestFiles></specifyTestFiles> is true, meaning only files specifically specified will be used.");
            WriteToFile(get_helpFileName(), "The commented test file lines were included to show a means in which different files can be setup but can be commented so that only");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "In the example configuration file provided below, a folder of test files are being tested, but only the files that contain the phrase \"sql\"");
            WriteToFile(get_helpFileName(), "will be used, the screen shot folder is specified, but no screenshots will be taken, only the Chrome browser will be used and it will be visible,");
            WriteToFile(get_helpFileName(), "and although test files are specified, they will be ignored because <specifyTestFiles></specifyTestFiles> is false meaning the folder settings will be used to ");
            WriteToFile(get_helpFileName(), "determine the test files to be used.");
            WriteToFile(get_helpFileName(), "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
                    "<automatedTestConfiguration>\r\n" +
                    "\t<!-- folder where screenshots should be saved -->\r\n" +
                    "\t<screenShotSaveFolder>C:\\ScreenShots\\Mashup</screenShotSaveFolder>\r\n" +
                    "\t<maxScreenShotsToTake>5</maxScreenShotsToTake>\r\n" +
                    "\t<browserType>Chrome</browserType>\r\n" +
                    "\t<!--<browserType>Firefox</browserType>-->\r\n" +
                    "\t<runHeadless>false</runHeadless>\r\n" +
                    "\t<testAllBrowsers>false</testAllBrowsers>\r\n" +
                    "\t<specifyTestFiles>false</specifyTestFiles>\r\n" +
                    "\t<sortSpecifiedTestFiles>true</sortSpecifiedTestFiles>\r\n" +
                    "\t<!-- Individual File Settings -->\r\n" +
                    "\t<testFiles>\r\n" +
                    "\t\t<!--<testFileName1>C:\\ConfigurableAutomatedTester\\Tests\\SqlServerAccess-Test.xml</testFileName1>\r\n" +
                    "\t\t<testFileName2>C:\\ConfigurableAutomatedTester\\Tests\\RunSqlQueries_Alternate-Test.xml</testFileName2>-->\r\n" +
                    "\t\t<testFileName1>C:\\ConfigurableAutomatedTester\\Tests\\Fill_out_FormMy_Form_and_submit-Test.xml</testFileName1>\r\n" +
                    "\t\t<testFileName2>C:\\ConfigurableAutomatedTester\\Tests\\CheckImageSource-Test.xml</testFileName2>\r\n" +
                    "\t</testFiles>\r\n" +
                    "\t<!-- Folder Testing Settings -->\r\n" +
                    "\t<testFolderName>C:\\ConfigurableAutomatedTester\\Tests\\</testFolderName>\r\n" +
                    "\t<folderFileFilterType>contains</folderFileFilterType>\r\n" +
                    "\t<folderFileFilter>sql</folderFileFilter>\r\n" +
                    "</automatedTestConfiguration>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              TEST FILE FORMAT                                                                                                          ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ TEST FILE OVERVIEW ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Test file is an xml file, that can include comments just like any xml file.");
            WriteToFile(get_helpFileName(), "A test file can consist of one or more test steps and there are currently no known limitations as some tests have contained");
            WriteToFile(get_helpFileName(), "tens of thousands of steps.");
            WriteToFile(get_helpFileName(), "The drawback to having large test files is that at some point the display will cut off older messages, and the log file will be the ");
            WriteToFile(get_helpFileName(), "only way to track the status of all test steps.");
            WriteToFile(get_helpFileName(), "Below, there are compound commands that allow for completing a step without creating a separate Navigation step.");
            WriteToFile(get_helpFileName(), "While this functionality was added for convenience, we strongly suggest using the separate Navigation step and making it crucial ");
            WriteToFile(get_helpFileName(), "as will be explained later.");
            WriteToFile(get_helpFileName(), "The test file allows for:");
            WriteToFile(get_helpFileName(), "1.\tCertain steps to be a point of failure for the entire test if it fails to prevent dependant steps from ");
            WriteToFile(get_helpFileName(), "  \tattempting to execute if the prerequisite step fails, such as Navigation, Establishing a Database connection or Getting JSON from an endpoint.");
            WriteToFile(get_helpFileName(), "2.\tCertain steps to be decisional so that steps based on the success of the prerequesite step only execute if ");
            WriteToFile(get_helpFileName(), "  \tthe prerequisite step is successful, otherwise dependent steps are skipped and steps subsequent to this block are executed.");
            WriteToFile(get_helpFileName(), "3. Comments to be plentiful.  Comments can be used to document, or to comment out steps or step values for quickly switching");
            WriteToFile(get_helpFileName(), "  \tbetween different values when running tests over and over to try to track down a specific behavior.");
            WriteToFile(get_helpFileName(), "4.\tTest files are run one after another and most objects are scoped to just the test that required them so");
            WriteToFile(get_helpFileName(), "  \tPersisted Values, JSON object and SQL Connecton object will all be reset and the Unique Identifiers will be reinitialized to a new value.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ TEST FILE FIELDS AND DESCRIPTIONS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "It begins with the XML declaration followed by the <testSteps> root element.");
            WriteToFile(get_helpFileName(), "Each test is grouped in <step> elements, which consist of the all or some of the following nodes:\r\n");
            WriteToFile(get_helpFileName(), "<command>Command to execute</command> - The command node, describes the command to execute and is always required.\r\n");
            WriteToFile(get_helpFileName(), "<actionType>read/write</actionType> - The actionType node can be set to read or write describing this as a read or write action.\r\n");
            WriteToFile(get_helpFileName(), "\tAssertions where an element value is being checked against a supplied value is a read.");
            WriteToFile(get_helpFileName(), "\tNavigation, clicking, populating text boxes, selecting select options, accessing context menu etc.. ");
            WriteToFile(get_helpFileName(), "\t\tare write actionTypes because they are performing an action rather than just reading a value.  ");
            WriteToFile(get_helpFileName(), "<accessor>select-menu</accessor> - The element identifier.\r\n");
            WriteToFile(get_helpFileName(), "<accessorType>ID</accessorType> - The type of element identifier. (xPath, ClassName, CssSelector, Id, TagName)\r\n");
            WriteToFile(get_helpFileName(), "<expectedValue>What you expect to retrieve</expectedValue> - The optional expectedValue node, if present, ");
            WriteToFile(get_helpFileName(), "\tacts as the expected value of the element value being retrieved as the actual value.\r\n");
            WriteToFile(get_helpFileName(), "<crucial>TRUE</crucial> - The crucial node can be set to True or False and determines if testing should stop ");
            WriteToFile(get_helpFileName(), "\tor proceed if the step fails.  Set to True for stop and False for proceed.\r\n");
            WriteToFile(get_helpFileName(), "<conditional>true</conditional> - The conditional node is used to start a conditional block of steps.");
            WriteToFile(get_helpFileName(), "\tAll steps within the block depend upon the success of this step being successful.");
            WriteToFile(get_helpFileName(), "\tThe Condition node should only ever be used when starting a conditional block of steps.");
            WriteToFile(get_helpFileName(), "\tA separate commmand is used to end the conditional block, so setting this to false is useless");
            WriteToFile(get_helpFileName(), "\tThis can only be used on read actionTypes as performing an action is not verifiable.");
            WriteToFile(get_helpFileName(), "\tWhen applied if successful, all subsequent steps within the block will execute along with all steps after the block.");
            WriteToFile(get_helpFileName(), "\tWhen applied and unsuccessful for any reason, all subsequent steps within the block will be skipped");
            WriteToFile(get_helpFileName(), "\t and all steps after the block will execute.\r\n");
            WriteToFile(get_helpFileName(), "<arguments></arguments> - The arguments node is a container of numbered argument nodes.");
            WriteToFile(get_helpFileName(), "\tArgument order is crucial and out of order arguments can have unpredictable results.");
            WriteToFile(get_helpFileName(), "\tSee the help sections below to learn the order of arguments for each command type.");
            WriteToFile(get_helpFileName(), "<arg1>First argument</arg1> The numbered arguments vary greatly depending upon the type of command.");
            WriteToFile(get_helpFileName(), "\t<arg2>Second argument</arg2>\r\n\t<arg3>Third argument</arg3>");
            WriteToFile(get_helpFileName(), "\tGenerally speaking, these numbered arguments are arranged so that the most relevant pieces of information ");
            WriteToFile(get_helpFileName(), "\tare the first items and the less relevant pieces of information are last.");
            WriteToFile(get_helpFileName(), "\tThe order of the arguments is crucial, while properly numbering is important but improperly numbering is forgivable as long as the xml is valid.\r\n");
            WriteToFile(get_helpFileName(), "There are few, if any, examples that use all nodes, so in the Navigation example below note that this does not use ");
            WriteToFile(get_helpFileName(), "Accessor and AccessorType nodes because it accesses the URL instead of a page element when making an assertion. ");
            WriteToFile(get_helpFileName(), "Also, keep in mind that while this represents the structure of a test file, it consists of only one of many possible test steps.\r\n");
            WriteToFile(get_helpFileName(), "Usually, the test file will consist of multiple steps like (navigation, check element values(multiple steps) click element...etc...");
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
            WriteToFile(get_helpFileName(), PrePostPad("[ NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Navigation command navigates the browser to the provided URL.");
            WriteToFile(get_helpFileName(), "All Navigation steps should be marked as crucial, as all subsequent checks require that navigation complete successfully!!!");
            WriteToFile(get_helpFileName(), "An assertion does not have to be part of navigation, but it probably should be!!!");
            WriteToFile(get_helpFileName(), "To navigate without checking the URL, remove the expectedValue node completely as displayed in the example below.");
            WriteToFile(get_helpFileName(), "For the Navigation command only, although the arguments should be in the order shown, if they ");
            WriteToFile(get_helpFileName(), "are out of order the application will attempt to discern the order and rearranage them.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Navigate, without checking the URL to ensure that navigation occurred properly, ");
            WriteToFile(get_helpFileName(), "to wait 4000 milli-seconds and to set the window dimensions to (800 x 800)");
            WriteToFile(get_helpFileName(), "Please note that making this crucial is irrelevant as no assertions will be made.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<!-- Command - ALWAYS REQUIRED!!! -->\r\n" +
                    "\t<command>navigate</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- first argument expected by the command - The URL is required --> \r\n" +
                    "\t\t<arg1>https://formy-project.herokuapp.com/form</arg1>\r\n" +
                    "\t\t<!-- second argument optional - Time Delay - default is 4000 if not included \r\n" +
                    "\t\t     but should be included if entering the third parameter --> \r\n" +
                    "\t\t<arg2>4000</arg2>\r\n" +
                    "\t\t<!-- third argument, optional - Window Dimensions width then height separated by space --> \r\n" +
                    "\t\t<arg3>w=800 h=800</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ NAVIGATION WITH SUCCESSFUL NAVIGATION CONFIRMATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "To Navigate, assert that the URL is what is in the expectedValue node and to wait 4 thousand milli-seconds before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "PLEASE NOTE: Asserting that the URL is correct does not mean that a server transfer didn't redirect the URL to a different page but leave the URL untouched. ");
            WriteToFile(get_helpFileName(), "<step>\r\n\t<command>navigate</command>\r\n\t<actionType>write</actionType>\r\n" +
                                                        "\t<expectedValue>https://formy-project.herokuapp.com/form</expectedValue>\r\n" +
                                                        "\t<crucial>TRUE</crucial>\r\n\t<arguments>\r\n" +
                                                        "\t\t<arg1>https://formy-project.herokuapp.com/form</arg1>\r\n" +
                                                        "\t\t<arg2>4000</arg2>\r\n\t</arguments>\r\n" +
                                                        "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Navigate, assert that the URL is as expected, add a time delay and set the browser dimensions to 800 width by 800 height:");
            WriteToFile(get_helpFileName(), "<step>\r\n\t<command>navigate</command>\r\n\t<actionType>write</actionType>\r\n" +
                    "\t<expectedValue>https://formy-project.herokuapp.com/form</expectedValue>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n\t<arguments>\r\n" +
                    "\t\t<arg1>https://formy-project.herokuapp.com/form</arg1>\r\n" +
                    "\t\t<arg2>4000</arg2>\r\n\t\t<arg3>w=800 h=800</arg3>\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ NAVIGATION WITH AUTHENTICATION WITH AND WITHOUT NAVIGATION CONFIRMATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Navigate command can be used to authenticate while navigating.");
            WriteToFile(get_helpFileName(), "This is done by passing the username and password as part of the url delimited by a : and ending with the @ symbol.");
            WriteToFile(get_helpFileName(), "It should be mentioned that this does not work for all authentication which is why there are different authentication methods.");
            WriteToFile(get_helpFileName(), "Additionally, it should be mentioned that the Authentication and Login commands are not for form based authentication but ");
            WriteToFile(get_helpFileName(), "rather for alert style login popups.");
            WriteToFile(get_helpFileName(), "For logging into a page that has two input text type form fields, use the send keys command coupled with the click command.");
            WriteToFile(get_helpFileName(), "To Navigate and Authenticate with username and password and assert that the URL is in the expectedValue node and to wait 4 thousand milli-seconds ");
            WriteToFile(get_helpFileName(), "before making the assertion to allow the page to load:");
            WriteToFile(get_helpFileName(), "<step>\r\n\t<command>navigate</command>\r\n\t<actionType>write</actionType>\r\n" +
                                                        "\t<expectedValue>https://formy-project.herokuapp.com/form</expectedValue>\r\n" +
                                                        "\t<crucial>TRUE</crucial>\r\n\t<arguments>\r\n" +
                                                        "\t\t<arg1>https://username:password@formy-project.herokuapp.com/form</arg1>\r\n" +
                                                        "\t\t<arg2>4000</arg2>\r\n\t</arguments>\r\n" +
                                                        "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Navigate and Authenticate with username and password:");
            WriteToFile(get_helpFileName(), "<step>\r\n\t<command>navigate</command>\r\n\t<actionType>write</actionType>\r\n" +
                                                        "\t<crucial>TRUE</crucial>\r\n\t<arguments>\r\n" +
                                                        "\t\t<arg1>https://username:password@formy-project.herokuapp.com/form</arg1>\r\n" +
                                                        "\t\t<arg2>4000</arg2>\r\n\t</arguments>\r\n" +
                                                        "</step>");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ LOGIN WITH NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Login command logs into a site.");
            WriteToFile(get_helpFileName(), "This command is intended for sites that have alert style popup login forms.");
            WriteToFile(get_helpFileName(), "There may be instances in which one or more Login/Authentication commands do not work");
            WriteToFile(get_helpFileName(), "on a particular operating system, like iOs.");
            WriteToFile(get_helpFileName(), "A mixed approach may work, but there is no guarantee.");
            WriteToFile(get_helpFileName(), "Although the login command is a compound command there are instances in which ");
            WriteToFile(get_helpFileName(), "having it work in this manner actually makes a difference over separate steps.");
            WriteToFile(get_helpFileName(), "To login and navigate in one step.");
            WriteToFile(get_helpFileName(), "Please note this is for normal passwords which cannot contain spaces or characters that require escaping.");
            WriteToFile(get_helpFileName(), "<step>\r\n\t<command>login</command>\r\n\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n\t<arguments>\r\n" +
                    "\t\t<arg1>username</arg1>\r\n" +
                    "\t\t<arg2>password</arg2>\r\n" +
                    "\t\t<arg3>http://www.myCoolPage.com</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ ALERT POPUP LOGIN ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The following Login command does not include the navigation step.");
            WriteToFile(get_helpFileName(), "As mentioned earlier, there may be isseus with logging in related to the operating system or alert type.");
            WriteToFile(get_helpFileName(), "Try different methods if failures happen as one approach may work best for a specific scenario.");
            WriteToFile(get_helpFileName(), "To login when presented with an alert style popup, which could happen upon landing on the site or after the site redirects you, and to make this crucial.");
            WriteToFile(get_helpFileName(), "Please note this is for normal passwords which cannot contain spaces or characters that require escaping.");
            WriteToFile(get_helpFileName(), "<step>\r\n\t<command>login</command>\r\n\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n\t<arguments>\r\n" +
                    "\t\t<arg1>username</arg1>\r\n" +
                    "\t\t<arg2>password</arg2>\r\n\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK URL WITHOUT NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Check URL command allows for checking the current page's URL without the need to navigate.");
            WriteToFile(get_helpFileName(), "When an command, like a button or link click occurs that navigates to a new page, there is no need to");
            WriteToFile(get_helpFileName(), "perform a navigation command but there is a need to check that the page is where expected before further testing.");
            WriteToFile(get_helpFileName(), "NOTE: The <actionType></actionType> for this command is write, not a read.");
            WriteToFile(get_helpFileName(), "This command is designed to work with event navigation to ensure that the navigation has occurred prior to subsequent testing.");
            WriteToFile(get_helpFileName(), "To check a URL without navigating and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check url</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<expectedValue>https://formy-project.herokuapp.com/form</expectedValue>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK GET REQUEST STATUS WITHOUT NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The CheckGet command checks the response status of a get.");
            WriteToFile(get_helpFileName(), "To check the Get Requests status of a URL without navigating and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "The Space between check and get is optional as shown below.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>checkget</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>200</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://www.swtestacademy.com/about-software-test-academy/ </arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternative way splitting the words apart.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check get</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>200</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://www.swtestacademy.com/about-software-test-academy/ </arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK POST REQUEST STATUS WITHOUT NAVIGATION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The CheckPost command checks the response status of a post.");
            WriteToFile(get_helpFileName(), "<!-- Test results unconfirmed!!! Need to find suitable URL that allows posting. -->");
            WriteToFile(get_helpFileName(), "To check the Post Requests status of a URL without navigating and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "The Space between check and post is optional as shown below.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>checkpost</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>200</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://www.swtestacademy.com/about-software-test-academy/ </arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternative way splitting the words apart.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check post</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>200</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://www.swtestacademy.com/about-software-test-academy/ </arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK DOCUMENT READY STATE COMPLETE WITHOUT NAVIGATION AS A POST NAVIGATION STEP]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Wait for page command waits until document ready state is complete.");
            WriteToFile(get_helpFileName(), "To check that the document ready state is complete after previously navigating to a new page and to make it crucial. ");
            WriteToFile(get_helpFileName(), "NOTE: The first argument must be n/a as shown below.  ");
            WriteToFile(get_helpFileName(), "- Omitting this argument or leaving it empty will result in an invalid format exception.");
            WriteToFile(get_helpFileName(), "To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "This will be most useful for triggered navigation.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>wait for page</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>n/a</arg1>\r\n" +
                    "\t\t<arg2>30</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK DOCUMENT READY STATE COMPLETE WITH NAVIGATION IN A SINGLE STEP]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "To check that the document ready state is complete after navigating to a new page and to make it crucial. ");
            WriteToFile(get_helpFileName(), "To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>wait for page</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://formy-project.herokuapp.com/form</arg1>\r\n" +
                    "\t\t<arg2>30</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            //SWITCH TO IFRAME"
            WriteToFile(get_helpFileName(), PrePostPad("[ SWITCHING TO AN IFRAME ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "IMPORTANT: EACH TIME A SWITCH TO IFRAME COMMAND IS ISSUED, THE CONTEXT IS REVERTED TO THE MAIN WINDOW");
            WriteToFile(get_helpFileName(), "ONCE THAT COMMAND COMPLETES EXECUTION, SO IF SUBSEQUENT TESTS NEED TO ACCESS THE IFRAME, THEY MUST BE");
            WriteToFile(get_helpFileName(), "ENCAPSULATED WITHIN SWITCH TO IFRAME COMMANDS.");
            WriteToFile(get_helpFileName(), "The Switch to iFrame command temporarily switches the current context to the iframe mentioned in the firs argument.");
            WriteToFile(get_helpFileName(), "Since only the command and first argument are needed to switch to the iFrame, the remaining test step elements");
            WriteToFile(get_helpFileName(), "can be used to perform the desired action within the iframe.");
            WriteToFile(get_helpFileName(), "The default action when switching to an iFrame is the Assert, which checks the text of an element against the expected");
            WriteToFile(get_helpFileName(), "value provided, so the assert command need not be used but any other commands should be placed into <arg2></arg2>.");
            WriteToFile(get_helpFileName(), "Commands that can be used in the iFrame are assert, sendkeys, click, right click, double click, persist values and  check against persisted values.");
            WriteToFile(get_helpFileName(), "A navigation event cannot be directed to take place in an iFrame.");
            WriteToFile(get_helpFileName(), "If the arguments are not in the proper order, depending upon the command being performed in the iFrame, an attempt to reorder");
            WriteToFile(get_helpFileName(), "the arguments will be made.  Unfortunately, some commands can be quite complex and interpreting arguments for reordering can be");
            WriteToFile(get_helpFileName(), "even more complex, therefore, a message will be displayed indicating that the arguments are out of order so that they can be corrected");
            WriteToFile(get_helpFileName(), "by the tester in the test steps file.");
            WriteToFile(get_helpFileName(), "Failing to do so for complex commands may yield unexpected results.");
            WriteToFile(get_helpFileName(), "The following example is an example of switching to an iframe, specified in <arg1></arg1> and performing an assert");
            WriteToFile(get_helpFileName(), "to check the element text pointed to by the <accessor></accessor> and <accessorType></accessorType> against the");
            WriteToFile(get_helpFileName(), "expected value provided in the <expectedValue></expectedValue> element and since <crucial></crucial> is false");
            WriteToFile(get_helpFileName(), "the status will be reported and subsequent tests will run regardless.");
            WriteToFile(get_helpFileName(), "Specifically, the test is switching to the iframeResult iFrame, and checking the element specified for the text Tutorial.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to iFrame</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>Tutorials</expectedValue>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<accessor>//button[contains(@id,'menu1')]</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>iframeResult</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To switch to an iFrame and perform a click, refer to the following example.");
            WriteToFile(get_helpFileName(), "Note that you have to specify the command in <arg2></arg2> as click is not the default action.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to iFrame</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<accessor>//button[contains(@id,'menu1')]</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>iframeResult</arg1>\r\n" +
                    "\t\t<arg2>click</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CONDITIONAL BLOCKS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "IMPORTANT: CONDITIONAL BLOCKS CAN NOT BE NESTED!!!!\r\n");
            WriteToFile(get_helpFileName(), "Creating a conditional block is a two step process.");
            WriteToFile(get_helpFileName(), "1.\tFirst, the conditional block must be started by adding the <conditional>true</conditional> node to a ");
            WriteToFile(get_helpFileName(), "\t\ttest step that has the actionType set to read (<actionType>read</actionType>).");
            WriteToFile(get_helpFileName(), "\t\tOnly read actionTypes are allowed to begin a conditional block because read steps ");
            WriteToFile(get_helpFileName(), "\t\tare checked against an expected value and are validatable.");
            WriteToFile(get_helpFileName(), "\t\tAny test steps based on the success of this condition can be placed after the condition.");
            WriteToFile(get_helpFileName(), "\t\tAn example of a conditional block start is shown below.");
            WriteToFile(get_helpFileName(), "\t\tThis command will be discussed in detail later, but note the presence of the conditional field, ");
            WriteToFile(get_helpFileName(), "\t\twhich signifies the start of a conditional block.");
            WriteToFile(get_helpFileName(), "\t\tSimply stated, the following command checks the element using the xPath accessor and validates that the text of that ");
            WriteToFile(get_helpFileName(), "\t\telement is the expectedValue, and if so, it is marked as successful and block statements will execute, else block");
            WriteToFile(get_helpFileName(), "\t\tstatements will be skipped.");
            WriteToFile(get_helpFileName(), "<step>\n" +
                    "\t<command>assert</command>\r\n" +
                    "\t<conditional>true</conditional>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>FORMY</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>/html[1]/body[1]/div[1]/nav[1]/a[1]</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "</step>\r\n");
            WriteToFile(get_helpFileName(), "1.\tSecond, the conditional block must be ended by adding an end condition command.");
            WriteToFile(get_helpFileName(), "\t\tThe end conditional command node itself is the most important and only required");
            WriteToFile(get_helpFileName(), "\t\t node of this test step, as shown below.");
            WriteToFile(get_helpFileName(), "\t\tThis command neither reads nor writes and asserts nothing and therefore, cannot be marked as crucial.");
            WriteToFile(get_helpFileName(), "\t\tOnce this command is executed, all remaining test steps will not longer dependent upon the condition");
            WriteToFile(get_helpFileName(), "\t\tand each will be executed as expected.");
            WriteToFile(get_helpFileName(), "\t\tIf this command is not issued all steps after the start of the Conditional Block start will be considered");
            WriteToFile(get_helpFileName(), "\t\tpart of the Conditional Block and will be executed or skipped accordingly.");
            WriteToFile(get_helpFileName(), "The following is the End Conditional command used to end the Conditional Block.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>end conditional</command>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK AN ANCHOR HREF ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Check_A_Href command checks the href attribute of a HyperLink.");
            WriteToFile(get_helpFileName(), "To check an anchor's href url and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "This will check the value of the href and compare it to the expected value provided.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check_a_href</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>https://www.swtestacademy.com/about-software-test-academy/ </expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>//*[@id=\"menu-item-21\"]/a</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>alt</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>\r\n");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternative way splitting the words.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check a href</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>https://www.swtestacademy.com/about-software-test-academy/ </expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>//*[@id=\"menu-item-21\"]/a</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>alt</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>\r\n");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK ALL PAGE LINKS USING URL ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Check Links command checks all Hyperlinks on the page.");
            WriteToFile(get_helpFileName(), "To check all page links and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "This will check for a status code of 200 for all links on the page, based on the URL in the arg1 node, ");
            WriteToFile(get_helpFileName(), "but will report the status code for all links.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check links</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://nutrish.com/</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK ALL PAGE LINKS WITHOUT USING URL ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "This will check for a status code of 200 for all links on the current page, but will report the status code for all links.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check links</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK THE COUNT OF A SPECIFIC ELEMENT ON A PAGE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Check Count command checks the number of occurrences of a particular element.");
            WriteToFile(get_helpFileName(), "To check the count of a specific element on a page and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "This will count the number of times an element is found on a page and compare that to the expected value.");
            WriteToFile(get_helpFileName(), "In the example below, the test compares the number of \"a\" tags on the page with the expected number of 18.");
            WriteToFile(get_helpFileName(), "If the page has 18 \"a\" tags, the test passes, otherwise it fails.");
            WriteToFile(get_helpFileName(), "An optional last argument can be included to use the != operator.");

            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check count</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>18</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n"  +
                    "\t<arguments>\r\n"  +
                    "\t\t<arg1>a</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK ALL PAGE IMAGE SRC TAGS WITH SEPARATE NAVIGATION STEP ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Check Img Src command checks the src of all image tags.");
            WriteToFile(get_helpFileName(), "To check all page image src tags, on the current page, to ensure a source exists and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "The src tag will be checked to see if it exists and if it returns a status code of 200 for all image sources but will report the status of all image sources.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check images src</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK ALL PAGE IMAGE SRC TAGS WITH NO SEPARATE NAVIGATION STEP ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "To check all page image src tags, on the page specified in the arg1 node, to ensure a source exists and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "The src tag will be checked to see if it exists and if it returns a status code of 200 for all image sources but will report the status of all image sources.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check images src</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://semantic-ui.com/modules/dropdown.html</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK ALL PAGE IMAGE ALT TAGS WITH SEPARATE NAVIGATION STEP ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Check Image Alt command checks all page image alt tags for text.");
            WriteToFile(get_helpFileName(), "To check all page image alt tags, for ADA compliance and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "The alt tag will checked to see if it exists and is not empty.  Empty tags will be flagged as failed.");
            WriteToFile(get_helpFileName(), "This is a small part of 508 compliance.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check images alt</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK ALL PAGE IMAGE ALT TAGS WITH NO SEPARATE NAVIGATION STEP ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "To check all page image alt tags, for ADA compliance and to make it crucial.  To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "The alt tag will checked to see if it exists and is not empty.  Empty tags will be flagged as failed.");
            WriteToFile(get_helpFileName(), "This is a small part of 508 compliance.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check images alt</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://semantic-ui.com/modules/dropdown.html</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ WAITING A SPECIFIC AMOUNT OF TIME FOR ITEMS TO BE AVAILABLE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Wait command pauses execution for a specified amount of time to allow for page load completion.");
            WriteToFile(get_helpFileName(), "In the event that arguments are not in the correct order, this application attempts to re-order the arguments.");
            WriteToFile(get_helpFileName(), "If the results are not as expected, refer to the examples in this help file for proper argument order.");
            WriteToFile(get_helpFileName(), "To wait for a specific amount of time before continuing to allow for page loading or script completion.");
            WriteToFile(get_helpFileName(), "To wait for 5 thousand milli-seconds before continuing onto the next step.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>wait</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>10000</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ WAITING FOR THE PRESENCE OF AN ELEMENT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Wait for Element command waits a maximum amount of time for an element to be available.");
            WriteToFile(get_helpFileName(), "To wait for an element to be present, requires checking for the element using an accessor unlike waiting a specific amount of time.");
            WriteToFile(get_helpFileName(), "To wait for for a maximum of 15 seconds for an element to be present and making this check crucial, use the following.");
            WriteToFile(get_helpFileName(), "To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>wait for element</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>//*[@id=\"slider-3\"]/div/div[1]/div/h3</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ WAITING FOR DOCUMENT READY STATE COMPLETE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Wait for Page command waits for Document Ready State to be Complete.");
            WriteToFile(get_helpFileName(), "To wait for the page to fully load and document state to be complete, use the following command.");
            WriteToFile(get_helpFileName(), "Please note that the accessor is set to page and that an accessor type is present.  Any Accessor Type must be present, although it is not used,");
            WriteToFile(get_helpFileName(), "to distinguish this document ready state complete wait from a time interval wait.");
            WriteToFile(get_helpFileName(), "To wait for for a maximum of 15 seconds for document state complete and to make this check crucial, use the following.");
            WriteToFile(get_helpFileName(), "To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>wait for page</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>https://www.marvel.com/</arg1>\r\n" +
                    "\t\t<arg2>30</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ UNIQUE IDENTIFIER ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "Before explaining how to fill in text fields, we need to cover the Unique Identifier.");
            WriteToFile(get_helpFileName(), "By default, every time a test is run, a unique identifier is created.");
            WriteToFile(get_helpFileName(), "This unique identifier is composed of the date and time with no delimiters.");
            WriteToFile(get_helpFileName(), "The purpose of this Unique Identifier is to allow rerunning the same tests and generating unique ");
            WriteToFile(get_helpFileName(), "values by appending this Unique Identifier to the string, thus creating a unique data set each test run.");
            WriteToFile(get_helpFileName(), "The Unique Identifier is 17 characters long and has the following format (yyyyMMddHHmmssSSS) ie.(20190402095619991).");
            WriteToFile(get_helpFileName(), " -  4 digit year, 2 digit month, 2 digit day, 2 digit hours, 2 digit minutes, 2 digit seconds, 3 digit milliseconds ");
            WriteToFile(get_helpFileName(), "In the Filling in and SendKeys sections, there are examples of exactly how to use this.");
            WriteToFile(get_helpFileName(), "Anytime, the character sequence without parenthesis (**_uid_**), is used, that value is replaced with the Unique Identifier.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ PERSISTING RETRIEVED TEXT IN A VARIABLE FOR LATER USE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The PersistString command stores the element's text in memory for use later.");
            WriteToFile(get_helpFileName(), "There may be a need to compare the value retrieved from one element with the value of another.");
            WriteToFile(get_helpFileName(), "Unfortunately, this cannot be done directly, but a Persist action can be enacted allowing the storage of ");
            WriteToFile(get_helpFileName(), "an element's value that can then be compared to the value of another element.");
            WriteToFile(get_helpFileName(), "This accomplishes comparing one element value with another.");
            WriteToFile(get_helpFileName(), "To persist the value of an element, use the following:");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>PersistString</command>\r\n" +
                    "\t<accessor>/html/body/div/h1</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ FILLING IN TEXT FIELDS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The SendKeys command sends keystrokes and phrases to form elements and Hyperlink context menus.");
            WriteToFile(get_helpFileName(), "To fill in a field by ID and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>first-name</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>John</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field by ID, appending the Unique Identifier to the name John, and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>first-name</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>John**_uid_**</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field by ID and to make it non-crucial when it contains a reserved command like click.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>first-name</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>click</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field using the value you persisted in an earlier step use the following.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>first-name</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>PersistedString</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field by ID, add the Unique Id, and to make it non-crucial when it contains a reserved command like click.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>first-name</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>click**_uid_**</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To fill in a field by ID with the persisted value, add the Unique Id, and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>first-name</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>PersistedString**_uid_**</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To compare the persisted value to an element, use the following:");
            WriteToFile(get_helpFileName(), "The Assert command compares an element's ACTUAL value with the EXPECTED value supplied.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>assert</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>/html[1]/body[1]/div[1]/form[1]/div[1]/div[4]/div[1]</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<expectedValue>PersistedString</expectedValue>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To retrieve the text of an element by xPath and compare it to the persisted value and assert that it is not equal.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>assert</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>/html[1]/body[1]/div[1]/form[1]/div[1]/div[4]/div[1]</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<expectedValue>PersistedString</expectedValue>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>!=</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Although the following can be found in the sendkeys section, in an effort to group all persistence in one ");
            WriteToFile(get_helpFileName(), "location it is duplicated here.");
            WriteToFile(get_helpFileName(), "There may be a need to send a persisted value to a control and that can be done as follows.");
            WriteToFile(get_helpFileName(), "To send the persisted value to a textbox or textarea form control, use the following:");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>first-name</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>PersistedString</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CLICK AN ELEMENT IN AN IFRAME ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Switch to iFrame command switches the current scope to the iFrame specified.");
            WriteToFile(get_helpFileName(), "To click an element by xPath in an iFrame.");
            WriteToFile(get_helpFileName(), "Note that the name of the iFrame is the first argument and the action to take is the second argument.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to iFrame</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<accessor>//button[contains(@id,'menu1')]</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>iframeResult</arg1>\r\n" +
                    "\t\t<arg2>click</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ SELECT AN OPTION FROM AN HTML SELECT ELEMENT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The SendKeys command can also be used to select an Option from an HTML Select control.");
            WriteToFile(get_helpFileName(), "To select an option from an HTML Select (drop down/list) element there are two methods.");
            WriteToFile(get_helpFileName(), "Sendkeys can be used to select an item based on its text or Click can be used on the option");
            WriteToFile(get_helpFileName(), "actually being selected.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>sendkeys</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<accessor>select-menu</accessor>\r\n" +
                    "\t<accessorType>ID</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>0-1</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternate method of selecting an option from an HTML select element.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>click</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<accessor>option[value='1']</accessor>\r\n" +
                    "\t<accessorType>CssSelector</accessorType>\r\n");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ TAKING SCREENSHOTS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The ScreenShot command takes a screenshot of the current page.");
            WriteToFile(get_helpFileName(), "To take a screen shot/print screen, the browser will be resized automatically to capture all page content,");
            WriteToFile(get_helpFileName(), "if browser dimensions are not supplied; however, in the event that browser dimensions are supplied, the browser");
            WriteToFile(get_helpFileName(), "will resize to the supplied dimensions.");
            WriteToFile(get_helpFileName(), "The ability to resize the browser to specific dimensions allows for image pixel comparison as images being compared");
            WriteToFile(get_helpFileName(), "must have the same width and height dimensions.");
            WriteToFile(get_helpFileName(), "This command allows for overriding the configured screenshot folder and dynamic filename creation and either saving ");
            WriteToFile(get_helpFileName(), "the images with a specified name in the configured screenshot folder or saving the images to a file naame and path ");
            WriteToFile(get_helpFileName(), "specified as <arg1></arg1> but if <arg1></arg1> is not provided a name is constructed and the file is saved in the ");
            WriteToFile(get_helpFileName(), "configured folder.");
            WriteToFile(get_helpFileName(), "This command also allows for specifying the browser dimensions so that you get an image the exact size that you need.");
            WriteToFile(get_helpFileName(), "Specifying the browser dimensions is the same as when navigating, where w= identifies the width value and h= identifies");
            WriteToFile(get_helpFileName(), "the height value as shown here: <arg2>w=1400 h=1000</arg2>.");
            WriteToFile(get_helpFileName(), "The ability to name the screenshot allows for executing subsequent Image Comparison steps and speficying the file names");
            WriteToFile(get_helpFileName(), "based on the screenshots being taken.");
            WriteToFile(get_helpFileName(), "In the following example, a default name will be provided for the  screenshot consisting of the browser used and the test step");
            WriteToFile(get_helpFileName(), "where the command was called. (This is the default functionality.)");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>screenshot</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternatively, including the filename as an argument will allow you to save the Screenshot with the name of your choosing.");
            WriteToFile(get_helpFileName(), "If a full path is included with the file name, and the folder structure exists, the screenshot will be saved to the location");
            WriteToFile(get_helpFileName(), "provided, otherwise the screenshot will be saved to the configured screenshots folder with the file name provided.");
            WriteToFile(get_helpFileName(), "In the following example, the screenshot will be saved to the configured screenshots folder with the file name provided.");
            WriteToFile(get_helpFileName(), "This functionality allows for pointing to the file for subsequent image comparison.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>screenshot</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>MyScreenShot.png</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "In the following example, the screenshot will be saved to the folder and file name provided.");
            WriteToFile(get_helpFileName(), "This functionality allows for pointing to the file for subsequent image comparisons.");
            WriteToFile(get_helpFileName(), "This is the preferred command structure when subsequently performing image comparisons.");
            WriteToFile(get_helpFileName(), "This structure allows for separation of images into separate folders for quickly referencing differences.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>screenshot</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>c:\\myScreehShots\\Actual\\MyScreenShot.png</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ SWITCHING BROWSER TABS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Right Click command can be used to access a link's context menu to open that link in a new tab and switch to that tab.");
            WriteToFile(get_helpFileName(), "Some actions are related and to avoid unnecessary steps the enter key will be pressed after right clicking and arrowing to a particular item.");
            WriteToFile(get_helpFileName(), "When running tests that access the context menu, the mouse and keyboard should not be used as they can change the context and cause the test to fail.");
            WriteToFile(get_helpFileName(), "To Right click on an element, move down to the first menu item, click it to open in a new tab and switch to the new tab:");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>right click</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<accessor>//*[@id=\"block-menu-menu-dc-menu\"]/div/div/ul/li[2]/a</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>Keys.Arrow_Down</arg1>\r\n" +
                    "\t\t<arg2>Keys.Enter</arg2>\r\n" +
                    "\t\t<!-- the following line switches to the new tab, if this is the first new tab opened, for subsequent tabs used the switch to tab command -->\r\n" +
                    "\t\t<arg3>switch to tab</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternatively, the steps can be separated into two separate steps where the ");
            WriteToFile(get_helpFileName(), "context menu opens a new tab and then the next step switches to the second tab.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>right click</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<accessor>//*[@id=\"block-menu-menu-dc-menu\"]/div/div/ul/li[2]/a</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>Keys.Arrow_Down</arg1>\r\n" +
                    "\t\t<arg2>Keys.Enter</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The Switch To Tab command switches to another Tab but if Tab argument is not provided, switches back to the first(parent) tab.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "Alternate Switch to tab command with the tab specified as an argument.");
            //WriteToFile(get_helpFileName(), "Currently, 1 and 0 are the only acceptable values as only one child tab should be opened per test");
            //WriteToFile(get_helpFileName(), "and to switch to the child tab use 1.  To switch to the main (parent) tab use 0.");
            WriteToFile(get_helpFileName(), "Recently updated, this method allows switching between all open tabs by passing the tab index as <arg1>.");
            WriteToFile(get_helpFileName(), "The parent tab, where the test originated is tab 0, the first child is tab 1, etc...");
            WriteToFile(get_helpFileName(), "If you open a tab from the parent and then open another tab from the parent, the first is tab 1 and the second is tab 2,");
            WriteToFile(get_helpFileName(), "however, switching to child tabs and opening tabs betwen the current child and the last child may have unexpected results.");
            WriteToFile(get_helpFileName(), "As a rule of thumb, open the tabs manually testing the layout of the tabs before attempting to automate this functionality.");
            WriteToFile(get_helpFileName(), "The following command switches to the first child tab and is an alternate way of doing this separating the functionality from the");
            WriteToFile(get_helpFileName(), "right click command allowing more granual control of switching between tabs.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>1</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The following command switches to the second child tab.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>2</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Switch back to the first tab after switching to the second tab.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab 0</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>0</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "An alternative to the above command allows the tab number argument to be excluded, as the default is tab 0 - the parent tab.");
            WriteToFile(get_helpFileName(), "Note that this has no argument and is relying upon the default setting to return to the first tab.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab 0</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CLOSE ONE OR ALL OPEN TABS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Close Child Tab command closes one or all child tabs but not the main tab");
            WriteToFile(get_helpFileName(), "If the test that is being run opens multiple tabs, at some point before all tests end, there may be a need to close one or all open child tabs.");
            WriteToFile(get_helpFileName(), "The most important reason to do this would be to avoid confusion as opening multiple tabs and keeping track of multiple tabs can become");
            WriteToFile(get_helpFileName(), "quite complex, whereas, closing and opening new tabs may be far less complex.");
            WriteToFile(get_helpFileName(), "Tabs are numbered 0 for main, 1 for first child(left most tab), 2 for second child etc...");
            WriteToFile(get_helpFileName(), "The Close Child Tab command takes one optional argument which is the tab number.");
            WriteToFile(get_helpFileName(), "If this optional argument is missing or set to the 0, which is the main tab, all child tabs are closed.");
            WriteToFile(get_helpFileName(), "The following command closes the first child tab specified by the supplied argument.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Close Child tab</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>1</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The following command closes all tabs because no specific child tab was provided.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Close Child tab</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ FIND ELEMENTS THAT HAVE SPECIFIC TEXT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Find command finds elements that have the supplied text.");
            WriteToFile(get_helpFileName(), "There are times when you may need to search for text but do not know the accessor necessary to find that text.");
            WriteToFile(get_helpFileName(), "The Find functionality allows you search all elements regardless of type or just all tags of a specific type for a phrase.");
            WriteToFile(get_helpFileName(), "Additionally, the Find functionality returns the xPath of all elements where the text is found, but when searching ");
            WriteToFile(get_helpFileName(), "for text without specifying a tag, only the actual tag containing the text is returned, not elements in the upper ");
            WriteToFile(get_helpFileName(), "hierarchy; however, when using a specific tag, if a child tag of that tag contains the text, the searched tag will be returned ");
            WriteToFile(get_helpFileName(), "as successfully containing that text. ");
            WriteToFile(get_helpFileName(), "To Find text searching all elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>find</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>Highest level of education</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Find text searching all div elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>find</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>Highest level of education</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>div</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Find text searching all label elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>find</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>Highest level of education</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>label</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ FIND ELEMENTS THAT CONTAIN TEXT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "There are times when you may need to search for a portion of text but do not know the accessor necessary to find that text.");
            WriteToFile(get_helpFileName(), "A specific instance might be when searching for text that would be in a paragraph.  ");
            WriteToFile(get_helpFileName(), "You wouldn't want to add the entire paragraph when you can add a snippet to verify that part of it is there. ");
            WriteToFile(get_helpFileName(), "Additionally, the Find functionality returns the xPath of all elements where the text is found.");
            WriteToFile(get_helpFileName(), "To Find element containing text searching all div elements and make this non-crucial, use the following.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>find</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>Highest level</expectedValue>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>div</arg1>\r\n" +
                    "\t\t<arg1>contains</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ CREATE TEST PAGE COMMAND TO CREATE PAGE TESTS OR FOR PROVIDING DATA TO HELP CREATE TESTS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "IMPORTANT NOTE #1: ANY PARENT ELEMENT WILL CONTAIN THE TEXT OF IT'S CHILD ELEMENT(s) SO TO GET THE ELEMENT THAT ACTUALLY ");
            WriteToFile(get_helpFileName(), "\t\t\tCONTAINS THE INFORMATION DESIRED, TRY TO ELIMINATE THE HIERARCHICAL ITEMS ABOVE THAT ARE NOT DESIRED, ");
            WriteToFile(get_helpFileName(), "\t\t\tLIKE CONTAINER ELEMENTS.  Examples include (html,head,body,div,table)");
            WriteToFile(get_helpFileName(), "IMPORTANT NOTE #2: ENSURE THAT YOUR FILE PATH DOES NOT CONTAIN ANY KEYWORD USED FOR ANY OTHER ACTION, OR YOU WILL GET UNEXPECTED RESULTS!!!");
            WriteToFile(get_helpFileName(), "  A test file needs to be created and you would like to spare yourself the hassle of looking up elements, associated properties and attributes.");
            WriteToFile(get_helpFileName(), "  This may be especially helpful in the early stages of testing where with development and content updates, test step updates can occur but this");
            WriteToFile(get_helpFileName(), "  process can quickly get the initial test steps created.");
            WriteToFile(get_helpFileName(), "  To do this, create a test script, with a Navigate command, to Navigate to the page to be tested and then use the ");
            WriteToFile(get_helpFileName(), "  create_test_page command or the create_test_page_formatted command.");
            WriteToFile(get_helpFileName(), "  The create_test_page command outputs key value information so that a determination can be made to as to whether an item should be tested and ");
            WriteToFile(get_helpFileName(), "  it provides all of the information to create the test command but it is not formatted as a test command.");
            WriteToFile(get_helpFileName(), "  The create_test_page_formatted command outputs the element information in a test command format allowing for quick copy and paste to a test file.");
            WriteToFile(get_helpFileName(), "  Both files indicate if an element is visible, if an a tag is acting as an anchor or a link.");
            WriteToFile(get_helpFileName(), "  The Formatted File, will create tests for a tags that check text and href, for images that check src, for text fields it create tests that compare text ");
            WriteToFile(get_helpFileName(), "\t provided with the element text, for text input it creates a sendkeys, for buttons, checkboxes and radio buttons it creates a click, ");
            WriteToFile(get_helpFileName(), "\t and for selects it creates a select command, allowing the user to enter one of the option values that is to be selected.");
            WriteToFile(get_helpFileName(), "  The create_test_page command and the create_test_page_formatted command take the following test parameters:");
            WriteToFile(get_helpFileName(), "   - arg1 is the Tag Type.  Use * for all tags, or div for just div tags etc...");
            WriteToFile(get_helpFileName(), "\t\t -    Element Type: A single element with * being all elements and the default if left empty.");
            WriteToFile(get_helpFileName(), "\t\t\t -   Elements Include but are not limited to: *, html, head, title, body, a, ol, ul, li, select, input etc...");
            WriteToFile(get_helpFileName(), "\t\t\t -   If omitted, this will be * for all elements.");
            WriteToFile(get_helpFileName(), "   - arg2 is the File where the Tags will be written.");
            WriteToFile(get_helpFileName(), "\t\t -    File Path and File Name: This is where the results will be written.");
            WriteToFile(get_helpFileName(), "\t\t\t -   If omitted, this will be written to the config folder. (/config/newTestFile.txt)");
            WriteToFile(get_helpFileName(), "   - arg3 is the comma delimited list of Tags to exclude.");
            WriteToFile(get_helpFileName(), "\t\t -    A comma delimited list of elements to skip when retrieving all element (*) information.");
            WriteToFile(get_helpFileName(), "\t\t\t -   These would usually be elements that do not have text themselves but contain elements that do have text.");
            WriteToFile(get_helpFileName(), "\t\t\t -   Do not include spaces between elements, just a comma as follows: html,head,title,body,div");
            WriteToFile(get_helpFileName(), "\t\t\t -   Skip elements are ONLY APPLIED WHEN RETRIEVING ALL ELEMENTS and IGNORED WHEN RETRIEVING A SPECIFIC TAG TYPE.");
            WriteToFile(get_helpFileName(), "  The following examples get all page elements except those container elements listed to skip, save them to a file, ");
            WriteToFile(get_helpFileName(), "  and create test step XML files that can be used immediately.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>create_test_page_formatted</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>*</arg1>\r\n" +
                    "\t\t<arg2>C:\\Tests\\TestPages\\Formy-Test.xml</arg2>\r\n" +
                    "\t\t<arg3>html,head,title,meta,script,body,style,nav,br,div,form</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The following example gets all anchor tag elements, saves them to a file, and ignores the skips list because all elements are not being retrieved.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>create_test_page_formatted</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>a</arg1>\r\n" +
                    "\t\t<arg2>C:\\Tests\\TestPages\\TestFileOutput_A_Only.txt</arg2>\r\n" +
                    "\t\t<arg3>html,head,title,meta,script,body,style,nav,br,div,form</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The following example is the correct equivalent of the previous command.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>create_test_page_formatted</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>a</arg1>\r\n" +
                    "\t\t<arg2>C:\\Tests\\TestPages\\TestFileOutput_A_Only.txt</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "  Alternatively, for checking content or for manually creating tests, using the unformatted command may prove more useful.");
            WriteToFile(get_helpFileName(), "  This command works just like the formatted command except it simply lists the element information, but does not make test steps.");
            WriteToFile(get_helpFileName(), "  Example of some element information: Element Type, xPath, Text, Href, Src, Alt, Visibility.");
            WriteToFile(get_helpFileName(), "  This file type is a plain text file and should be output accordingly.");
            WriteToFile(get_helpFileName(), "  The following example creates an unformatted file(arg2) listing the described element attributes for all elements (arg1)");
            WriteToFile(get_helpFileName(), "  except those in the skipped list (arg3)");
            WriteToFile(get_helpFileName(), "<step>\n" +
                    "\t<command>create_test_page</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\n" +
                    "\t\t<arg1>*</arg1>\n" +
                    "\t\t<arg2>C:\\TestPages\\Formy-Unformatted-Test.txt</arg2>\n" +
                    "\t\t<arg3>html,head,title,meta,script,body,style,nav,br,div,form</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "\t</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CONNECT TO SQL SERVER DATABASE AND CLOSE THE CONNECTION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Connect To Database command opens and persists a connection object until it is closed.");
            WriteToFile(get_helpFileName(), "There will be times during the course of QA'ing a site where querying the database can confirm that a value has been ");
            WriteToFile(get_helpFileName(), "added or removed.");
            WriteToFile(get_helpFileName(), "NOTE: ALTHOUGH MONGODB IS MENTIONED IN THE COMMENTS BELOW, IT IS NOT FULLY IMPLEMENTED.");
            WriteToFile(get_helpFileName(), "AT THIS TIME ONLY SQL SERVER CONNECTIVITY IS CURRENTLY IMPLEMENTED!!!");
            WriteToFile(get_helpFileName(), "IMPORTANT: ENSURE THAT YOU ALWAYS CREATE A CLOSE CONNECTION TEST STEP TO CLOSE THE CONNECTION YOU OPEN!!!!");
            WriteToFile(get_helpFileName(), "An emergency clean up process will attempt to shut down any open connections, but it is recommended that ");
            WriteToFile(get_helpFileName(), "you do this with a test step to ensure that connection limits are not exhausted.");
            WriteToFile(get_helpFileName(), "To do this, you must first establish a connection to the database and this is how to do that.");
            WriteToFile(get_helpFileName(), "There are two ways to establish a connection to Sql Server. ");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<!-- Open Connection to Sql Server -->\r\n" +
                    "\t<!-- Allows you to connect to a SQL Server database.  Make this step crucial or conditional as subsequent steps depend on it's success. -->\r\n" +
                    "\t<command>Connect to Database</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- Type of Database (MongoDb, Sql Server)-->\r\n" +
                    "\t\t<arg1>SQL Server</arg1>\r\n" +
                    "\t\t<!-- Database to connect to -->\r\n" +
                    "\t\t<arg2>PocFisForumV2</arg2>\r\n" +
                    "\t\t<!-- user name -->\r\n" +
                    "\t\t<arg3>forum_user</arg3>\r\n" +
                    "\t\t<!-- password -->\r\n" +
                    "\t\t<arg4>forum_user</arg4>\r\n" +
                    "\t\t<!-- connection type - not currently used -->\r\n" +
                    "\t\t<arg5>local</arg5>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The alternative method with more control but with more risk of failure.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<!-- Allows you to connect to a SQL Server database.  Make this step crucial or conditional as subsequent steps depend on it's success. -->\r\n" +
                    "\t<command>Connect to Database</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>true</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- Type of Database (MongoDb, Sql Server)-->\r\n" +
                    "\t\t<arg1>SQL Server</arg1>\r\n" +
                    "\t\t<!-- Connection String or Close to close the connection -->\r\n" +
                    "\t\t<!-- when using a uri, escape all illegal xml characters (escape: ampersands with &amp;) -->\r\n" +
                    "\t\t<arg2>jdbc:sqlserver://localhost:1433;database=PocFisForumV2;user=forum_user;password=forum_user;encrypt=false;trustServerCertificate=true;loginTimeout=30;</arg2>\r\n" +
                    "\t\t<!-- arg2>jdbc:sqlserver://local.database.windows.net:1433;database=PocFisForumV2;user=forum_user;password=forum_user;encrypt=true;trustServerCertificate=false;loginTimeout=30;</arg2 -->\r\n" +
                    "\t\t<!-- currently not implemented but created to distinguish between local and non-local SQL databases -->\r\n" +
                    "\t\t<arg3>local</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CLOSING THE DATABASE CONNECTION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Close Database Connection command closes an open database connection and destroys the connection object releasing the resource.");
            WriteToFile(get_helpFileName(), "Equally important as opening the database connection object, is closing the database connection object.");
            WriteToFile(get_helpFileName(), "Open connections consume resources and unclosed connections can use up all available memory or an ");
            WriteToFile(get_helpFileName(), "allotment of connections depending on where the connection lives.");
            WriteToFile(get_helpFileName(), "To avoid this, always write a close connection command whenever writing an open connection command.");
            WriteToFile(get_helpFileName(), "The close command is simple and as follows:");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Close Database Connection</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>SQL Server</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>\r\n");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ QUERYING THE SQL SERVER DATABASE  ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Sql Server Query command executes a Sql Server Query for a single field value and compares that to the expected value.");
            WriteToFile(get_helpFileName(), "When testing a site, there sometimes needs to be verification that a value was ");
            WriteToFile(get_helpFileName(), "actually written to, or removed from the database.  ");
            WriteToFile(get_helpFileName(), "This is the purpose of the Sql Server Query Command.");
            WriteToFile(get_helpFileName(), "There are two ways to query the database.");
            WriteToFile(get_helpFileName(), "First option: Specify the Table, Field and Where clause separately.");
            WriteToFile(get_helpFileName(), "The last argument is the comparison type and must be included for != test steps, ");
            WriteToFile(get_helpFileName(), "but is optional for = test steps.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Sql Server Query</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>General</expectedValue>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- Table to query or select statement -->\r\n" +
                    "\t\t<arg1>[POCFISForumV2].[dbo].[Forums]</arg1>\r\n" +
                    "\t\t<!-- Field to query -->\r\n" +
                    "\t\t<arg2>Forum</arg2>\r\n" +
                    "\t\t<!-- where clause - optional -->\r\n" +
                    "\t\t<arg3>where ForumId = 1</arg3>\r\n" +
                    "\t\t<!-- Optional 4th argument can be used to signify != comparison -->\r\n" +
                    "\t\t<!--<arg4>!=</arg4>-->\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Second Option: Specify the entire Select statement.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Sql Server Query</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>FAQ</expectedValue>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- Table to query or select statement -->\r\n" +
                    "\t\t<arg1>Select Forum from [POCFISForumV2].[dbo].[Forums] where ForumId = 2</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Second Option Alternative type of SQL Select Statement: Specify the entire Select statement.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Sql Server Query</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<expectedValue>FAQ</expectedValue>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- Table to query or select statement -->\r\n" +
                    "\t\t<arg1>Select Top(1) Forum from [POCFISForumV2].[dbo].[Forums]</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ RETRIEVING JSON FROM AN API ENDPOINT  ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Get JSON command like the SQL Server Connection, persists an object for use by subsequent commands.");
            WriteToFile(get_helpFileName(), "Unlike the SQL Server Connection command, which opens a connection object, the Get JSON command");
            WriteToFile(get_helpFileName(), "downloads and stores the JSON into a local variable that can later be used to SEARCH for key value pairs.");
            WriteToFile(get_helpFileName(), "This local variable will contain the retrieved JSON until overwritten by a subsequent Get JSON request or ");
            WriteToFile(get_helpFileName(), "until the test file executing ends.");
            WriteToFile(get_helpFileName(), "Each time a test is run this variable is reset to null until populated by the Get JSON command.");
            WriteToFile(get_helpFileName(), "The Get JSON command is used to retrieve the JSON from either the current page/url or a different page/url.");
            WriteToFile(get_helpFileName(), "If the optional argument URL is not included, the current page/url will be used to retrieve the JSON.");
            WriteToFile(get_helpFileName(), "If the URL is included as the command's optional sole argument, it will trigger a navigation event and then");
            WriteToFile(get_helpFileName(), "that page/url will be used to retrieve the JSON.");
            WriteToFile(get_helpFileName(), "The preferred way to perform the Get JSON is shown below and requires a previous navigation step.");
            WriteToFile(get_helpFileName(), "This step should be conditional or crucial depending upon the subsequent steps in the test file.");
            WriteToFile(get_helpFileName(), "In the example below, JSON is retrieved from the current URL and starts a Conditional Block so that");
            WriteToFile(get_helpFileName(), "any query statements can be contained within the Conditional Block preventing their execution if this step fails.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<!-- Allows you to retrieve JSON from the current end point.  Make this step crucial or conditional as subsequent steps depend on it's success. -->\r\n" +
                    "\t<command>Get JSON</command>\r\n" +
                    "\t<conditional>true</conditional>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternatively, navigation can be included in the same step to first trigger a navigation event and then get the JSON.");
            WriteToFile(get_helpFileName(), "The only difference between the example below and the one above is that the below example first triggers a navigation event");
            WriteToFile(get_helpFileName(), "by including the URL as an argument in the test step.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<!-- Allows you to retrieve JSON from a different end point.  Make this step crucial or conditional as subsequent steps depend on it's success.  -->\r\n" +
                    "\t<command>Get JSON</command>\r\n" +
                    "\t<conditional>true</conditional>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>http://local.forums.com/?productId=2&amp;id=true</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ QUERYING JSON FROM AN API ENDPOINT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "A QUICK NOTE ABOUT JSON BEFORE REVIEWING THIS COMMAND");
            WriteToFile(get_helpFileName(), "\tReturned JSON is a string so numbers are represented without quotation marks while strings");
            WriteToFile(get_helpFileName(), "\tare represented with quotation marks.");
            WriteToFile(get_helpFileName(), "\tThis means that 1 and \"1\" are not the same, as the former is a number and the latter is a string representation of that number.");
            WriteToFile(get_helpFileName(), "\tJSON files are key value pairs where the key is a string and the value is either a number or a string.");
            WriteToFile(get_helpFileName(), "\tThe Key is like a variable name and the Value holds the value of the variable.");
            WriteToFile(get_helpFileName(), "The Query JSON command is actually just a CASE SENSITIVE search and not a querying framework like the SQL Query command.");
            WriteToFile(get_helpFileName(), "For this reason, the != operator is not supported for JSON Queries.");
            WriteToFile(get_helpFileName(), "The key, which is placed into the <accessor></accessor> node along with the expected value which is placed into the");
            WriteToFile(get_helpFileName(), "<expectedValue></expectedValue> node are both case sensitive.");
            WriteToFile(get_helpFileName(), "Additionally, the <expectedValue></expectedValue> node must represent the expected value exactly so include quotes for");
            WriteToFile(get_helpFileName(), "strings and exclude quotes for numbers.");
            WriteToFile(get_helpFileName(), "To best determine how to represent the expected value, just copy the text between the colon and the end of the ");
            WriteToFile(get_helpFileName(), "line excluding the comma, if present.");
            WriteToFile(get_helpFileName(), "For the following examples, refer to the following JSON excerpt:");
            WriteToFile(get_helpFileName(), "{\r\n" +
                    "\"ForumId\": 1,\r\n" +
                    "\"Forum\": \"General\",\r\n" +
                    "\"IsActive\": true\r\n" +
                    "}");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "For the first example, the Query JSON command is being used to find the ForumId value with a numeric value of 1.");
            WriteToFile(get_helpFileName(), "Notice that the <expectedValue></expectedValue> contains 1 and not \"1\".");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Query JSON</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>ForumId</accessor>\r\n" +
                    "\t<accessorType>JSON</accessorType>\r\n" +
                    "\t<expectedValue>1</expectedValue>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "For the second example, the Query JSON command is being used to find the Forum value with a string value of \"General\".");
            WriteToFile(get_helpFileName(), "When searching for string values the quotation marks must be used exactly as they are in the JSON returned.");
            WriteToFile(get_helpFileName(), "Notice that the <expectedValue></expectedValue> contains \"General\" and not General.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Query JSON</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<accessor>Forum</accessor>\r\n" +
                    "\t<accessorType>JSON</accessorType>\r\n" +
                    "\t<expectedValue>\"General\"</expectedValue>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ SAVE JSON TO FILE  ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Save JSON command saves the JSON retrieved with the Get JSON command to the path provided.");
            WriteToFile(get_helpFileName(), "Having the downloaded JSON on hand when testing is important for showing proof when providing feedback.");
            WriteToFile(get_helpFileName(), "The command is a read actionType and not a write actionType as it has to read the JSON before writing it.");
            WriteToFile(get_helpFileName(), "There are two arguments that can be used with this command.");
            WriteToFile(get_helpFileName(), "The first argument is the file path and name where the JSON should be saved and is required.");
            WriteToFile(get_helpFileName(), "The second argument is for overwriting the existing file.");
            WriteToFile(get_helpFileName(), "If set to True or Overwrite, an existing file with the same name will be overwritten.");
            WriteToFile(get_helpFileName(), "If set to False, which is the default, and a file exists with the same name, the application will append an incremental integer value ");
            WriteToFile(get_helpFileName(), "to the file name and retest until it finds a file name that does not exist. ");
            WriteToFile(get_helpFileName(), "The updated name will be used to name the downloaded JSON file and will be reported back via the console and log.");
            WriteToFile(get_helpFileName(), "If the second parameter is missing, the value will be defaulted to false.");
            WriteToFile(get_helpFileName(), "In the example below, the JSON previously retrieved will be saved to the file outlined in <arg1></arg1>,");
            WriteToFile(get_helpFileName(), "but since <arg2></arg2> overwrite is set to false, if that file exists a new name will be created.");
            WriteToFile(get_helpFileName(), "A message indicating that the file existed along with the original file name and the updated file name will be");
            WriteToFile(get_helpFileName(), "reported to the tester via the console and will also be included in the log file.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Save JSON</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- first parameter expected by the command - The file name; it is also required -->\r\n" +
                    "\t\t<arg1>C:\\JSON downloads\\forums.json</arg1>\r\n" +
                    "\t\t<arg2>false</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "In the below example, <arg2></arg2> has been set to true for overwrite, which will delete an existing file with ");
            WriteToFile(get_helpFileName(), "the same name before saving the file.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Save JSON</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- first parameter expected by the command - The file name; it is also required -->\r\n" +
                    "\t\t<arg1>C:\\JSON downloads\\forums.json</arg1>\r\n" +
                    "\t\t<arg2>true</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "Alternatively, the previous example could have also been written using the word overwrite, as shown below.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Save JSON</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- first parameter expected by the command - The file name; it is also required -->\r\n" +
                    "\t\t<arg1>C:\\JSON downloads\\forums.json</arg1>\r\n" +
                    "\t\t<arg2>overwrite</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK COLOR CONTRAST  ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Check Contrast command compares the forecolor brightness with the backcolor brightness and the color difference between the two.");
            WriteToFile(get_helpFileName(), "Check contrast uses the formula found on the following page to calculate and compare the foreground and background brightness and color differences:");
            WriteToFile(get_helpFileName(), "https://www.w3.org/TR/AERT/#color-contrast\r\n");
            WriteToFile(get_helpFileName(), "When testing a page, one of the most important things to determine is if the color contrast is within acceptable ranges.");
            WriteToFile(get_helpFileName(), "Good contrast allows users to easily read information without struggling to discern text from background.");
            WriteToFile(get_helpFileName(), "A site with bad contrast is less likely to maintain interest and even less likely to receive repeat visits.");
            WriteToFile(get_helpFileName(), "If the background color cannot be found on the container element acting as the background, this method climbs the");
            WriteToFile(get_helpFileName(), "container's ancestral hierarchy until it finds the color used for the background.");
            WriteToFile(get_helpFileName(), "In the results if you see ^1 it means that it had to use the parent's backcolor, ^2 is grandparent, ^3 great grand parent etc...");
            WriteToFile(get_helpFileName(), "If you don't see the ^ sign, the value was taken directly from the element itself.");
            WriteToFile(get_helpFileName(), "In the partial output example shown below, notice the ^2 following the backcolor listing, this indicates");
            WriteToFile(get_helpFileName(), "that the grandparent of the container element was used as the source for the element's background color.");
            WriteToFile(get_helpFileName(), "backcolor(rgba(248, 249, 250, 1))^2 Back-Color Brightness: 248.0\r\n");
            WriteToFile(get_helpFileName(), "The first argument is the element type that you want to check.");
            WriteToFile(get_helpFileName(), "It is suggested that you create focused tests targeting the lowest element that actually contains text instead of it's");
            WriteToFile(get_helpFileName(), "parent element, which will contain that element thus also containing that element's text.");
            WriteToFile(get_helpFileName(), "It is best to use p for <p> tags, label for <label> tags, span for <span> tags");
            WriteToFile(get_helpFileName(), "With that said, it is possible to use an asterisk(*) to target all elements.");
            WriteToFile(get_helpFileName(), "\t-Note that doing so will take a long long time to complete and the results will likely be too much for the terminal display.");
            WriteToFile(get_helpFileName(), "\t-At the time of writing this, a test using an asterisk ran for over 2 hours for one page and was stopped having tested " +
                    "only a fraction of the page!!!");
            WriteToFile(get_helpFileName(), "It is suggested that you never use the overriding arguments but they were included to allow for slightly relaxed requirements.");
            WriteToFile(get_helpFileName(), "The default brightness contrast which can be overridden in the second argument is 125.");
            WriteToFile(get_helpFileName(), "The default color contrast which can be overridden in the third argument is 500.");
            WriteToFile(get_helpFileName(), "In the following Check Contrast Test step, the tags being checked are all <p> tags against their container elements.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>check contrast</command>\r\n" +
                    "\t<actionType>read</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- Type of tag(s) to check contrast on -->\r\n" +
                    "\t\t<arg1>p</arg1>\r\n" +
                    "\t\t<!--  [Optional and not recommended] Allows Overriding Acceptable Contrast settings b for color brightness default is (125)-->\r\n" +
                    "\t\t<arg2>b=86</arg2>\r\n" +
                    "\t\t<!-- [Optional and not recommended] Allows Overriding Acceptable Contrast settings d for color difference default is (500) -->\r\n" +
                    "\t\t<arg3>d=450</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ COMPARE IMAGES AND CREATE DIFFERENCE IMAGE  ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "The Compare Images command, uses ImageMagick to compares 2 images on your local system and creates a difference image");
            WriteToFile(get_helpFileName(), "outlining all things that differ between the images whether it is spacing, item dimensions, fonts, colors or anything");
            WriteToFile(get_helpFileName(), "else that is not pixel exact.");
            WriteToFile(get_helpFileName(), "The difference image can be used to quickly see all differences and use it to provide feedback to other teams.");
            WriteToFile(get_helpFileName(), "This command also provides a numeric Pixel percentage of difference between the two images.");
            WriteToFile(get_helpFileName(), "If the images are exactly the same, the percentage of difference will be 0.0 and will be displayed in green to ");
            WriteToFile(get_helpFileName(), "indicate that the images are identical.");
            WriteToFile(get_helpFileName(), "If the images are not exactly the same, the percentage of difference will be more than 0.0 and will be displayed in red");
            WriteToFile(get_helpFileName(), "to indicate that there are differences between the images.");
            WriteToFile(get_helpFileName(), "It is suggested that you create the following three folders when comparing images: Baseline, Actual, Difference");
            WriteToFile(get_helpFileName(), "\tThe Baseline folder - This is where your comp image should be stored and images within this folder act as the");
            WriteToFile(get_helpFileName(), "\t\tbase line images that the actual images should match.");
            WriteToFile(get_helpFileName(), "\t\tThis image must exist in the file system!");
            WriteToFile(get_helpFileName(), "\tThe Actual folder - This is where your captured screenshots can be saved to compare against the Baseline folder images.");
            WriteToFile(get_helpFileName(), "\t\tHaving the images separated avoids confusion and allows for saving over previously saved actual images for continued comparisons.");
            WriteToFile(get_helpFileName(), "\t\tThis image must exist in the file system!");
            WriteToFile(get_helpFileName(), "\tThe Difference folder - This is where your difference images can be stored, separated from other images to allow for quickly ");
            WriteToFile(get_helpFileName(), "\t\treviewing the differences.");
            WriteToFile(get_helpFileName(), "\t\tThis image will be created in the file system and need not exist prior to running the test.");
            WriteToFile(get_helpFileName(), "IMPORTANT!!! If tests are rerun, difference images will be overwritten!!");
            WriteToFile(get_helpFileName(), "Take time to backup needed difference images prior to rerunning tests or the previous difference image state will be lost.");
            WriteToFile(get_helpFileName(), "The following command compares the baseline image in <arg1></arg1> with the actual image in <arg2></arg2> and ");
            WriteToFile(get_helpFileName(), "creates the difference image <arg3></arg3> where specified.");
            WriteToFile(get_helpFileName(), "Additionally, this command reports the numeric percentage of difference for a quick reference so that the tester can identify");
            WriteToFile(get_helpFileName(), "which images need further examination to detail the differences to the corresponding team members.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<!-- compares two images -->\r\n" +
                    "\t<command>compare images</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>FALSE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<!-- first argument is the comp image filename. It is Required!!! -->\r\n" +
                    "\t\t<arg1>C:\\ScreenShots\\Mashup\\MyScreenShot.png</arg1>\r\n" +
                    "\t\t<!-- second argument, is the actual image filename.   It is Required!!! -->\r\n" +
                    "\t\t<arg2>C:\\ScreenShots\\Mashup\\Actual\\MyScreenShot.png</arg2>\r\n" +
                    "\t\t<!-- third argument the name of the Difference filename. It is Required!!!   -->\r\n" +
                    "\t\t<arg3>C:\\ScreenShots\\Mashup\\Difference\\MyScreenShot-DifferenceImage.png</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              TROUBLESHOOTING                                                                                                           ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), PrePostPad("[ DRIVER ISSUES ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "If you run the application and the browser briefly opens and then closes:");
            WriteToFile(get_helpFileName(), "Check you local browser version and compare that with the corresponding web driver for that browser.");
            WriteToFile(get_helpFileName(), "If these are not the same, upgrade the web driver for this browser and it should work.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ URL VALIDATION FAILURE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "When you enter a url into your web browser although the trailing slash may be there or may not be there, the returned URL from the test app differs.");
            WriteToFile(get_helpFileName(), "Update your test to reflect what the test app is returning as this is the actual URL for this page.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ MISSING CONFIGURATION FILE ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "If you are running in JUnit and see the following message:\r\n" +
                    " the config file is not in the correct location or has the wrong name.");
            WriteToFile(get_helpFileName(), "Configuration File not found! (Config/ConfigurationSetup.xml)");
            WriteToFile(get_helpFileName(), "Place the configuration file in the location above with the name specified and re-run the test.");
            WriteToFile(get_helpFileName(), "Exiting!!!");
            WriteToFile(get_helpFileName(), "configSettings is null!!!");
            WriteToFile(get_helpFileName(), "Although the reported error seems self explanatory, this means that the configuration file is not where");
            WriteToFile(get_helpFileName(), "the application expects it to be, so place it into the correct location, specified in the error message, and re-run the test.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ UNEXPECTED OUTPUT FROM A TEST STEP ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "If you have an unexpected output or outcome of a test step, check the Action/Expected value field in your test ");
            WriteToFile(get_helpFileName(), "and ensure that there is no keyword in there that the application may attempt to execute instead of the action intended.");
            WriteToFile(get_helpFileName(), "The test will have to be re-written to account for this.");
            WriteToFile(get_helpFileName(), "A specific SendKeys keyword was added to send text that could be misconstrued because it contains keywords.");
            WriteToFile(get_helpFileName(), "While this particular solution may not be the one you need, there is likely a solution but if not, please document the issue ");
            WriteToFile(get_helpFileName(), "so that it can be addressed in future implementations.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ OVERALL TEST RESULT SHOWS FAILURE ALTHOUGH TEST STEPS PASS (LAST TEST STEP PASSED) ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "When running tests, if a step marked as crucial does not fail, the overall JUnit test should ");
            WriteToFile(get_helpFileName(), "show as having passed.");
            WriteToFile(get_helpFileName(), "If the last step passes but the overall JUnit test shows a failure and the error doesn't point to ");
            WriteToFile(get_helpFileName(), "anything in the test steps, re-run the test and it will likely show as having passed.");
            WriteToFile(get_helpFileName(), "This intermittent failure was noticed during testing and while it is believed to have been a race condition");
            WriteToFile(get_helpFileName(), "that was fixed, this has been added as the exact cause has not been identified.");
            WriteToFile(get_helpFileName(), "If during testing, this no longer occurs, this tip may be removed.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ XML DOCUMENT MUST START WITH AND END WITH THE SAME ELEMENT ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "This means that the start and end element are not proper opening and closing XML tags.");
            WriteToFile(get_helpFileName(), "1.\tFirst, ensure that the document has the start and end tags.");
            WriteToFile(get_helpFileName(), "2.\tNext, check that the end tag contains a </ as the first two characters as a common mistake is ");
            WriteToFile(get_helpFileName(), "\t\tcopying and pasting and forgetting to update.");
            WriteToFile(get_helpFileName(), "3.\tFinally, check that the spelling, if this was not a copy paste issue as it may have been a typo.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ CONTEXT MENU TAB SWITCHING IS FAILING ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "If a test is running that accesses the context menu or switches tabs, the MOUSE and KEYBOARD CANNOT BE USED");
            WriteToFile(get_helpFileName(), "WHILE THE TEST IS IN PROGRESS or it will change the context and thus cause the test step to fail, which may in turn");
            WriteToFile(get_helpFileName(), "cause subsequent test steps to fail.");
            WriteToFile(get_helpFileName(), "Keep hands off mouse and keyboard while running context menu and tab switching tests and when possible, when running any other type of test.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");

            WriteToFile(get_helpFileName(), PrePostPad("[ SCREENSHOTS SAVING TO CONFIG FOLDER INSTEAD OF CONFIGURATION FILE SETTING  ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "If a <screenShotSaveFolder></screenShotSaveFolder> setting was added to the configuration file but");
            WriteToFile(get_helpFileName(), "the screenshots are not being saved there, check that the entire specified folder structure exists!!!");
            WriteToFile(get_helpFileName(), "The most likely cause of this issue is that all or part of the path specified does not exist.");
            WriteToFile(get_helpFileName(), "Copy the path from Windows Explorer or File Explorer and paste it into the  <screenShotSaveFolder></screenShotSaveFolder> setting.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(),  PrePostPad("═", "═", 1, 159));
//            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
//            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
//            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
//            WriteToFile(get_helpFileName(), "");
//            WriteToFile(get_helpFileName(), "");
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



    /**********************************************************************
     * Description: This method sorts the test files by file path and name
     *              alphanumerically.
     * @param tempFiles
     * @param configSettings
     **********************************************************************/
    private void SortTestXmlFiles(ArrayList<String> tempFiles, ConfigSettings configSettings) {
        String configValue;
        String firstFile;
        String secondFile;
        String temp1;
        String temp2;

        for (int y=0;y<tempFiles.size();y++) {
            firstFile = tempFiles.get(y);
            for (int x = 0; x < tempFiles.size(); x++) {
                secondFile = tempFiles.get(x);
                //if (firstFile.compareTo(secondFile) > 0 && y < x) {
                if (secondFile.compareTo(firstFile) > 0 && y > x) {
                    temp1 = tempFiles.get(y);
                    temp2 = tempFiles.get(x);
                    tempFiles.remove(x);
                    tempFiles.remove(y);
                    tempFiles.add(x, temp1);
                    tempFiles.add(y, temp2);
                } else if (firstFile.compareTo(secondFile) > 0 && x > y) {
                    temp1 = tempFiles.get(y);
                    temp2 = tempFiles.get(x);
                    tempFiles.remove(x);
                    tempFiles.remove(y);
                    tempFiles.add(y, temp2);
                    tempFiles.add(x, temp1);
                }
            }
        }

        configSettings.reset_testFiles();

        for (int x=0;x<tempFiles.size();x++) {
            configValue = tempFiles.get(x).substring(tempFiles.get(x).indexOf("=") + 1);
            configSettings.set_testSettingsFile(configValue, x);
        }
    }


    /***********************************************************************
     * Description: This method gets all files in the folder passed in
     *              based on the the config settings allowing filtering of
     *              files based on what the files start with, contain, or
     *              ends with.
     * @param folder - folder where the files exist
     * @param extension - files with this extension are examined
     * @param configSettings
     * @return
     ***********************************************************************/
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


    /********************************************************************
     *  DESCRIPTION: This method removes all ANSI code that does console
     *               colorization so that the plain text can be written
     *               to the file.
     *
     * @param testMessage
     * @return
     ********************************************************************/
    private String CleanMessage(String testMessage) {

        String cleanMessage = testMessage.replace(AppConstants.ANSI_YELLOW,"")
                .replace(AppConstants.ANSI_RED,"").replace(AppConstants.ANSI_GREEN,"")
                .replace(AppConstants.ANSI_BLUE, "").replace(AppConstants.ANSI_PURPLE,"")
                .replace(AppConstants.ANSI_RESET,"").replace(AppConstants.ANSI_CYAN,"")
                .replace(AppConstants.ANSI_BOLD,"").replace(AppConstants.FRAMED,"")
                .replace(AppConstants.ANSI_YELLOW_BACKGROUND,"").replace(AppConstants.ANSI_GREEN_BACKGROUND,"")
                .replace(AppConstants.ANSI_BLUE_BACKGROUND,"").replace(AppConstants.ANSI_PURPLE_BACKGROUND,"")
                .replace(AppConstants.ANSI_WHITE_BACKGROUND,"")
                .replace(AppConstants.ANSI_PURPLE_BACKGROUND,"").replace(AppConstants.ANSI_YELLOW_BRIGHT,"")
                .replace(AppConstants.ANSI_PURPLE_BRIGHT,"").replace(AppConstants.ANSI_RED_BRIGHT,"")
                .replace(AppConstants.ANSI_GREEN_BRIGHT,"").replace(AppConstants.ANSI_BLUE_BRIGHT,"")
                .replace(AppConstants.ANSI_GREEN_BACKGROUND_BRIGHT,"").replace(AppConstants.ANSI_WHITE_BACKGROUND_BRIGHT,"");

        return cleanMessage;
    }



    /************************************************************************
     * DESCRIPTION: This method pads the section outline format to a
     *              specific length with additional "═" characters to
     *              meet the maxCharacters length.
     * @param sectionTitle
     * @return
     ************************************************************************/
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

    /***********************************************************************************
     * Description: This method attempts to ascertain whether the passed
     *              in string is a URL.
     * @param navigateUrl - Parameter being checked to determine if it is a URL
     * @return
     **********************************************************************************/
    public Boolean CheckIsUrl(String navigateUrl) {
        Boolean status = false;
        if (navigateUrl != null && navigateUrl.startsWith("http")) {
            status = true;
        } else if (navigateUrl != null && (navigateUrl.contains(".com") || navigateUrl.contains(".net")
                || navigateUrl.contains(".org") || navigateUrl.contains(".gov"))) {
                    status = true;
        }
        return status;
    }


    /************************************************************************************
     * Description: This method checks to discover if the fileName parameter passed in
     *              already exists.
     *              If it does exist, this program iterates checking if the file exists
     *              while adding an incrementally increasing integer value to the end
     *              of the origial filename until it finds one that does not exist and
     *              it returns that value to the calling method.
     *
     * @param fileName - Name of the file to check to discover if it exists.
     * @return - Filename that does not exist.
     ************************************************************************************/
    public String GetUnusedFileName(String fileName) {
        File tmpFile = new File(fileName);
        String checkFileName = fileName;
        int counter = 1;
        if (tmpFile.exists()) {
            while (tmpFile.exists()) {
                checkFileName = fileName.substring(0,fileName.lastIndexOf(".") - 1) + counter + fileName.substring(fileName.lastIndexOf("."));
                tmpFile = new File(checkFileName);
                if (!tmpFile.exists()) {
                    break;
                }
                counter++;
            }
        }
        return checkFileName;
    }
}

