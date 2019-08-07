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
                UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_YELLOW  + AppConstants.sectionLeftUp + PrePostPad("[ End Retrieving Files in specified folder. ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
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
            UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_YELLOW_BACKGROUND + AppConstants.ANSI_BLUE + AppConstants.ANSI_BOLD + AppConstants.sectionLeftDown + PrePostPad("[ Reading Config (" + configurationXmlFile + ") file ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);

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
                    UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_YELLOW + AppConstants.sectionLeftDown + PrePostPad("[ Start - Retrieving Files in specified folder. ]", "═", 9, 157) + AppConstants.sectionRightDown + AppConstants.ANSI_RESET, false);
                    configSettings.reset_testFiles();
                    File temp = new File(configSettings.get_testFolderName());
                    configSettings = GetAllFilesInFolder(temp, "txt", configSettings);
                    UpdateTestResults(AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_YELLOW + AppConstants.sectionLeftUp + PrePostPad("[ End Retrieving Files in specified folder. ]", "═", 9, 157) + AppConstants.sectionRightUp + AppConstants.ANSI_RESET, false);
                }
            }
        } catch (Exception e) {
            UpdateTestResults("The following error occurred while reading the Configuration Settings XML file: \r\n" + e.getMessage(), false);
        }

        if (tempFiles.size() > 0 && configSettings.get_specifyFileNames() && configSettings.get_sortSpecifiedTestFiles()) {
            //SortTestFiles(tempFiles, configSettings);
            SortTestXmlFiles(tempFiles, configSettings);
            UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "[ TestFileNames Re-Sorted, new order shown below ]" + AppConstants.ANSI_RESET, false);
            for (int index=0;index<tempFiles.size();index++) {
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent8 + "TestFileName = " + AppConstants.ANSI_RESET + tempFiles.get(index), false);
            }
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
//                           DebugDisplay("arguments = " + arguments.getLength());
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
                    //UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_GREEN + "Screenshot successfully taken for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
                    UpdateTestResults(AppConstants.ANSI_GREEN + "Screenshot successfully taken for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
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

            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              CONFIGURATION FILE FORMAT                                                                                                 ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "NOTES: The file format is XML, so to make comments use <!-- to begin the comment and --> to end the comment.");
            WriteToFile(get_helpFileName(), "Comments can span lines but comments cannot include other comment blocks.");
            WriteToFile(get_helpFileName(), "Refer to an XML guide for proper commenting.  Google it!!!!");
            WriteToFile(get_helpFileName(), "Both configuration file examples can be used as starting points, just substitute values accordingly.");
            WriteToFile(get_helpFileName(), "The terms element and node may be used interchangeably below to refer to the same thing.");
            WriteToFile(get_helpFileName(), "The following is an example of a configuration file which will be explained below.");
            WriteToFile(get_helpFileName(), "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
                    "<automatedTestConfiguration>\r\n" +
                    "\t<!-- folder where screenshots should be saved -->\r\n" +
                    "\t<screenShotSaveFolder>C:\\Gary\\ScreenShots\\Mashup</screenShotSaveFolder>\r\n" +
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
            WriteToFile(get_helpFileName(), "\tIt contains no text itself.");
            WriteToFile(get_helpFileName(), "<!-- folder where screenshots should be saved --> is a comment and was purposely included to demonstrate how to add comments.");
            WriteToFile(get_helpFileName(), "\tComments were added to describe the function of the element that follows it.");
            WriteToFile(get_helpFileName(), "\tSubsequent comments will not be described.");
            WriteToFile(get_helpFileName(), "The <screenShotSaveFolder></screenShotSaveFolder> element specifies the location where Screen Shots will be saved.");
            WriteToFile(get_helpFileName(), "\tThis folder must already exist!");
            WriteToFile(get_helpFileName(), "The <maxScreenShotsToTake></maxScreenShotsToTake> element specifies the maximum number of Screen Shots to take.");
            WriteToFile(get_helpFileName(), "\t\t-   When -1, only errors will create screen shots.");
            WriteToFile(get_helpFileName(), "\t\t-   When 0, there is no limit and all screenshots will be taken.");
            WriteToFile(get_helpFileName(), "\t\t-   When any other number, that number of screenshots or less will be taken depending upon the test and the max set.");
            WriteToFile(get_helpFileName(), "\t\t-   Errors like, Element not found, will create a screenshot to allow you to see the page the application was on when ");
            WriteToFile(get_helpFileName(), "\t\t\tthe error occurred.");
            WriteToFile(get_helpFileName(), "The <browserType></browserType> element specifies the type of browser to use when running the test(s).");
            WriteToFile(get_helpFileName(), "\t-Valid values are: Firefox, Chrome, PhantomJS");
            WriteToFile(get_helpFileName(), "The <runHeadless></runHeadless> element specifies whether to run the test(s) in headless mode.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis is a boolean field and can be true or false.  The case does not matter.");
            WriteToFile(get_helpFileName(), "\tHeadless mode means that the browser does not display on-screen and is used with automation servers");
            WriteToFile(get_helpFileName(), "\tto allow running automated tests as part of the build process.");
            WriteToFile(get_helpFileName(), "\t\t-\tPhantomJS always runs headless.  To watch the test, use Chrome or Firefox.");
            WriteToFile(get_helpFileName(), "\t\t-\tTo watch the test, use Chrome or Firefox.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf not specified, Chrome is the default and will be used.");
            WriteToFile(get_helpFileName(), "The <testAllBrowsers></testAllBrowsers> element specifies whether the test(s) should run in all browsers or just the selected browser.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis is a boolean field and can be true or false.  The case does not matter.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf false, BrowserType must be set and only that browser will be used when running tests.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf true, BrowserType is ignored and the program will cycle through all browsers.");
            WriteToFile(get_helpFileName(), "The <specifyTestFiles></specifyTestFiles> element specifies whether or not the configuration file will list the test files to be used.");
            WriteToFile(get_helpFileName(), "\t\t-\tThis is a boolean field and can be true or false.  The case does not matter.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf true, the files specified will be used in the order in which they are listed.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf false, the files will be ignored as this indicates that the folder settings will be used instead.");
            WriteToFile(get_helpFileName(), "The <sortSpecifiedTestFiles></sortSpecifiedTestFiles> element specifies whether or not to sort the test files.");
            WriteToFile(get_helpFileName(), "\tThis setting made sense in the old system but makes much less sense now since files are physically listed in numerical order.");
            WriteToFile(get_helpFileName(), "\tThe number no longer has meaning in the sort as each entry should be entered in numerical order, so the number will not be used for sorting.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf false, files are taken in the order in which they are physically and numerically listed.");
            WriteToFile(get_helpFileName(), "\t\t-\tIf true, files will be sorted alphabetically and re-listed on the screen to show the order in which they will execute.");
            WriteToFile(get_helpFileName(), "The <testFiles></testFiles> element is a container element for the testFileName elements and has no textual content of its own.");
            WriteToFile(get_helpFileName(), "The <testFileName1></testFileName1> element specifies the test file to use for the test and each element should end with an incremental numeric value.");
            WriteToFile(get_helpFileName(), "\tIt should be noted that while this ending numeric value should be incremental, the application will read all ");
            WriteToFile(get_helpFileName(), "\ttestFileName nodes in the order they are entered regardless of the numbering.");
            WriteToFile(get_helpFileName(), "\tTo avoid any possible issues related to this, it is suggested that you follow the best practice and number appropriately.");
            WriteToFile(get_helpFileName(), "\tThe commented test file lines were included to show a means in which different files can be setup but can be commented so that only ");
            WriteToFile(get_helpFileName(), "\tthe intended test files run.   ");

            WriteToFile(get_helpFileName(), "---------------------");
            WriteToFile(get_helpFileName(), "The following three settings need to be talked about together since they work together to provide a particularly useful piece of functionality.");
            WriteToFile(get_helpFileName(), "The <testFolderName></testFolderName> element specifies the folder where test files are located to allow for reading a folder of ");
            WriteToFile(get_helpFileName(), "test files instead of naming each test file individually.");
            WriteToFile(get_helpFileName(), "The <folderFileFilterType></folderFileFilterType> element specifies the type of filtering to perform on the files in the folder.  Options are: ");
            WriteToFile(get_helpFileName(), "\t-\t[Starts With], [Contains] and [Ends With] ");
            WriteToFile(get_helpFileName(), "\t\t-\t[Starts With] - will select only the test files starting with the filter entered");
            WriteToFile(get_helpFileName(), "\t\t-\t[Contains] - will select only test files containing the filter entered");
            WriteToFile(get_helpFileName(), "\t\t-\t[Ends With] - will select only test files ending with the filter entered");
            WriteToFile(get_helpFileName(), "The <folderFileFilter></folderFileFilter> element specifies the phrase to use to when selecting files in the specified folder.");
            WriteToFile(get_helpFileName(), "\tWhen used with the other folder settings, a folder containing a multitude of test files can be pointed to using the <testFolderName></testFolderName> element.");
            WriteToFile(get_helpFileName(), "\tThen, using the <folderFileFilterType></folderFileFilterType> element, [Starts With] can be used to return only files starting with a specific value.");
            WriteToFile(get_helpFileName(), "\tFinally, using the <folderFileFilter></folderFileFilter> element, a phrase like the project name can be used to select only files in the selected folder that start with the project name.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "In the example configuration file provided above the explanations, two specific test files are being tested, the screen shot folder is specified, ");
            WriteToFile(get_helpFileName(), "a maximum of 5 screenshots will be taken, only the test Chrome browser will be used and it will be visible, the files will be run in the order entered,");
            WriteToFile(get_helpFileName(), "the TestFolderName, FolderFileFilterType and FolderFileFilter have not been specified but would have been disregarded if specified because");
            WriteToFile(get_helpFileName(), "SpecifiyTestFiles is true, meaning only files specifically specified will be used.");
            WriteToFile(get_helpFileName(), "The commented test file lines were included to show a means in which different files can be setup but can be commented so that only");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "In the example configuration file provided below, a folder of test files are being tested, but only the files that contain the phrase \"sql\"");
            WriteToFile(get_helpFileName(), "will be used, the screen shot folder is specified, but no screenshots will be taken, only the Chrome browser will be used and it will be visible,");
            WriteToFile(get_helpFileName(), "and although test files are specified, they will be ignored because SpecifiyTestFiles is false meaning the folder settings will be used to ");
            WriteToFile(get_helpFileName(), "determine the test files to be used.");
            WriteToFile(get_helpFileName(), "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
                    "<automatedTestConfiguration>\r\n" +
                    "\t<!-- folder where screenshots should be saved -->\r\n" +
                    "\t<screenShotSaveFolder>C:\\Gary\\ScreenShots\\Mashup</screenShotSaveFolder>\r\n" +
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
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              TEST FILE FORMAT                                                                                                          ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "The Test file is an xml file, that can include comments just like any xml file.");
            WriteToFile(get_helpFileName(), "It begins with the XML declaration followed by the <testSteps> root element.");
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
            WriteToFile(get_helpFileName(), "\tGenerally speaking, these numbered arguments are arranged so that the most relevant pieces of information ");
            WriteToFile(get_helpFileName(), "\tare the first items and the less relevant pieces of information are last.");
            WriteToFile(get_helpFileName(), "\tThe order of the arguments is crucial, while properly numbering is important but improperly numbering is forgivable as long as the xml is valid.");
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
            WriteToFile(get_helpFileName(), "All Navigation steps should be marked as crucial, as all subsequent checks require that navigation complete successfully!!!");
            WriteToFile(get_helpFileName(), "An assertion does not have to be part of navigation, but it probably should be!!!");
            WriteToFile(get_helpFileName(), "To navigate without checking the URL, remove the expectedValue node completely as displayed in the example below.");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Navigate, without checking the URL to ensure that navigation occurred properly.");
            WriteToFile(get_helpFileName(), "Please note that making this crucial is irrelevant as no assertions will be made.");
            WriteToFile(get_helpFileName(), "<step>\r\n\t<command>navigate</command>\r\n\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n\t<arguments>\r\n" +
                    "\t\t<arg1>https://formy-project.herokuapp.com/form</arg1>\r\n" +
                    "\t\t<arg2>4000</arg2>\r\n\t</arguments>\r\n" +
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
            WriteToFile(get_helpFileName(), "");
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
            WriteToFile(get_helpFileName(), "");
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
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK AN ANCHOR HREF ]", "═", 9, 159));
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
            WriteToFile(get_helpFileName(), "To check the count of a specific element on a page and to make it non-crucial.  To make it crucial change the last parameter to true.");
            WriteToFile(get_helpFileName(), "This will count the number of times an element is found on a page and compare that to the expected value.");
            WriteToFile(get_helpFileName(), "In the example below, the test compares the number of \"a\" tags on the page with the expected number of 18.");
            WriteToFile(get_helpFileName(), "If the page has 18 \"a\" tags, the test passes, otherwise it fails.");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
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
            WriteToFile(get_helpFileName(), PrePostPad("[ CHECK ALL PAGE IMAGE SRC TAGS WITH SEPARATE NAVIGATION STEP ]", "═", 9, 159));
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
            WriteToFile(get_helpFileName(), "To wait for an element to be present, requires checking for the element using an accessor unlike waiting a specific amount of time.");
            WriteToFile(get_helpFileName(), "To wait for for a maximum of 15 seconds for an element to be present and making this check crucial, use the following.");
            WriteToFile(get_helpFileName(), "To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "╠/html/body/div[4]/div/div[2]/div[4]/div[1]/div[2]/div ; wait ╬ 15 ; xPath ; true ; true╣");
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
            WriteToFile(get_helpFileName(), "To wait for the page to fully load and document state to be complete, use the following command.");
            WriteToFile(get_helpFileName(), "Please note that the accessor is set to page and that an accessor type is present.  Any Accessor Type must be present, although it is not used,");
            WriteToFile(get_helpFileName(), "to distinguish this document ready state complete wait from a time interval wait.");
            WriteToFile(get_helpFileName(), "To wait for for a maximum of 15 seconds for document state complete and to make this check crucial, use the following.");
            WriteToFile(get_helpFileName(), "To make it non-crucial change the last parameter to false.");
            WriteToFile(get_helpFileName(), "╠page ; wait ╬ 15 ; xPath ; true ; true╣");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
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
            WriteToFile(get_helpFileName(), "###  To fill in a field by ID and to make it non-crucial.  To make it crucial change the last parameter to true.");
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
            WriteToFile(get_helpFileName(), PrePostPad("[ CLICK AN ELEMENT IN AN IFRAME ]", "═", 9, 159));
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
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ TAKING SCREENSHOTS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "To take a screen shot/print screen.  The browser will be resized automatically to capture all page content.");
            WriteToFile(get_helpFileName(), "╠n/a ; ScreenShot ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>screenshot</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>false</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ SWITCHING BROWSER TABS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "Some actions are related and to avoid unnecessary steps the enter key will be pressed after right clicking and arrowing to a particular item.");
            WriteToFile(get_helpFileName(), "To Right click on an element, move down to the first menu item, click it to open in a new tab and switch to the new tab:");
            WriteToFile(get_helpFileName(), "╠//*[@id=\"rso\"]/div[1]/div/div[1]/div/div/div[1]/a ; right click ╬ Keys.Arrow_Down ╬ Switch to tab ; xPath ; true ; false╣");

            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>right click</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<accessor>//*[@id=\"block-menu-menu-dc-menu\"]/div/div/ul/li[2]/a</accessor>\r\n" +
                    "\t<accessorType>xPath</accessorType>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>Keys.Arrow_Down</arg1>\r\n" +
                    "\t\t<arg2>Keys.Enter</arg2>\r\n" +
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
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "Alternate Switch to tab command with the tab specified as an argument.");
            WriteToFile(get_helpFileName(), "Currently, 1 and 0 are the only acceptable values as only one child tab should be opened per test");
            WriteToFile(get_helpFileName(), "and to switch to the child tab use 1.  To switch to the main (parent) tab use 0.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>1</arg1>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "To Switch back to the first tab after switching to the second tab.");
            WriteToFile(get_helpFileName(), "Note that this requires 0 (Zero) to be specified in the command.");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>Switch to tab 0</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ FIND ELEMENTS THAT HAVE SPECIFIC TEXT ]", "═", 9, 159));
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
            WriteToFile(get_helpFileName(), PrePostPad("[ FIND ELEMENTS ON A PAGE TO HELP MAKE A TEST FILE - NOT FOR TESTING BUT FOR HELPING TO CREATE TESTS ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "IMPORTANT NOTE #1: ANY PARENT ELEMENT WILL CONTAIN THE TEXT OF IT'S CHILD ELEMENT(s) SO TO GET THE ELEMENT THAT ACTUALLY ");
            WriteToFile(get_helpFileName(), "                  CONTAINS THE INFORMATION DESIRED, TRY TO ELIMINATE THE HIERARCHICAL ITEMS ABOVE THAT ARE NOT DESIRED, ");
            WriteToFile(get_helpFileName(), "                  LIKE CONTAINER ELEMENTS.  Examples include (html,head,body,div,table)");
            WriteToFile(get_helpFileName(), "  IMPORTANT NOTE #2: ENSURE THAT YOUR FILE PATH DOES NOT CONTAIN ANY KEYWORD USED FOR ANY OTHER ACTION, OR YOU WILL GET UNEXPECTED RESULTS!!!");
            WriteToFile(get_helpFileName(), "  A test file needs to be created and you would like to spare yourself the hassle of looking up elements, associated properties and attributes.");
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
            WriteToFile(get_helpFileName(), "  The following two examples gets all page elements, saves them to a file, skips a list of container and other elements.");
//            WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬  ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_All.txt ╬ html,head,title,meta,script,body,style,a,nav,br,strong,div ; n/a ; true ; false╣");
//            WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬ * ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_All.txt ╬ html,head,title,meta,script,body,style,a,nav,br,strong,div ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>create_test_page_formatted</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>*</arg1>\r\n" +
                    "\t\t<arg2>C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\ConfigurableAutomatedTester\\Tests\\TestPages\\Formy-Test.xml</arg2>\r\n" +
                    "\t\t<arg3>html,head,title,meta,script,body,style,nav,br,div,form</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The following example gets all anchor tag elements, saves them to a file, and ignores the skips list because all elements are not being retrieved.");
            //WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬ a ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_A_Only.txt ╬ html,head,title,meta,script,body,style,a,nav,br,strong,div ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>create_test_page_formatted</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>a</arg1>\r\n" +
                    "\t\t<arg2>C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\ConfigurableAutomatedTester\\Tests\\TestPages\\TestFileOutput_A_Only.txt</arg2>\r\n" +
                    "\t\t<arg3>html,head,title,meta,script,body,style,nav,br,div,form</arg3>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "The following example is the correct equivalent of the previous command.");
            //WriteToFile(get_helpFileName(), "╠n/a ; create_test_page  ╬ a ╬ C:\\MyLocalPath\\MyLocalFolder\\My Test Page Creation Folder\\TestFileOutput_A.txt ; n/a ; true ; false╣");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<command>create_test_page_formatted</command>\r\n" +
                    "\t<actionType>write</actionType>\r\n" +
                    "\t<crucial>TRUE</crucial>\r\n" +
                    "\t<arguments>\r\n" +
                    "\t\t<arg1>a</arg1>\r\n" +
                    "\t\t<arg2>C:\\Users\\gjackson\\Downloads\\Ex_Files_Selenium_EssT\\Ex_Files_Selenium_EssT\\Exercise Files\\ConfigurableAutomatedTester\\Tests\\TestPages\\TestFileOutput_A_Only.txt</arg2>\r\n" +
                    "\t</arguments>\r\n" +
                    "</step>");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("[ CONNECT TO SQL SERVER DATABASE AND CLOSE THE CONNECTION ]", "═", 9, 159));
            WriteToFile(get_helpFileName(), "There will be times during the course of QAing a site where querying the database can confirm that a value has been ");
            WriteToFile(get_helpFileName(), "added or removed.");
            WriteToFile(get_helpFileName(), "IMPORTANT: ENSURE THAT YOU ALWAYS CREATE A CLOSE CONNECTION TEST STEP TO CLOSE THE CONNECTION YOU OPEN!!!!");
            WriteToFile(get_helpFileName(), "To do this, you must first establish a connection to the database and this is how to do that.");
            WriteToFile(get_helpFileName(), "There are two ways to establish a connection to Sql Server. ");
            WriteToFile(get_helpFileName(), "<step>\r\n" +
                    "\t<!-- Open Connection to Sql Server -->\r\n" +
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
            WriteToFile(get_helpFileName(), "╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            WriteToFile(get_helpFileName(), "║                                              TROUBLESHOOTING                                                                                                           ║");
            WriteToFile(get_helpFileName(), "╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            WriteToFile(get_helpFileName(), "DRIVER ISSUES");
            WriteToFile(get_helpFileName(), "If you run the application and the browser briefly opens and then closes:");
            WriteToFile(get_helpFileName(), "Check you local browser version and compare that with the corresponding web driver for that browser.");
            WriteToFile(get_helpFileName(), "If these are not the same, upgrade the web driver for this browser and it should work.");
            WriteToFile(get_helpFileName(), "═════════════════════════════════════════════════════════");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "URL VALIDATION FAILURE");
            WriteToFile(get_helpFileName(), "When you enter a url into your web browser although the trailing slash may be there or may not be there, the returned URL from the test app differs.");
            WriteToFile(get_helpFileName(), "Update your test to reflect what the test app is returning as this is the actual URL for this page.");
            WriteToFile(get_helpFileName(), "═════════════════════════════════════════════════════════");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "MISSING CONFIGURATION FILE");
            WriteToFile(get_helpFileName(), "If you are running in JUnit and see the following message, the config file is not in the correct location or has the wrong name.");
            WriteToFile(get_helpFileName(), "Configuration File not found! (Config/ConfigurationSetup.tconfig)");
            WriteToFile(get_helpFileName(), "Place the configuration file in the location above with the name specified and re-run the test.");
            WriteToFile(get_helpFileName(), "Exiting!!!");
            WriteToFile(get_helpFileName(), "configSettings is null!!!");
            WriteToFile(get_helpFileName(), "═════════════════════════════════════════════════════════");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "UNEXPECTED OUTPUT FROM A TEST STEP");
            WriteToFile(get_helpFileName(), "If you have an unexpected output or outcome of a test step, check the Action/Expected value field in your test ");
            WriteToFile(get_helpFileName(), "and ensure that there is no keyword in there that the application may attempt to execute instead of the action intended.");
            WriteToFile(get_helpFileName(), "The test will have to be re-written to account for this.");
            WriteToFile(get_helpFileName(), "A specific SendKeys keyword was added to send text that could be misconstrued because it contains keywords.");
            WriteToFile(get_helpFileName(), "While this particular solution may not be the one you need, there is likely a solution but if not, please document the issue ");
            WriteToFile(get_helpFileName(), "so that it can be addressed in future implementations.");
            WriteToFile(get_helpFileName(), "═════════════════════════════════════════════════════════");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "###  " + PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), "");
            WriteToFile(get_helpFileName(), PrePostPad("═", "═", 1, 159));
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

    private void SortTestXmlFiles(ArrayList<String> tempFiles, ConfigSettings configSettings) {
        String configValue;
        String firstFile;
        String secondFile;
        String temp1;
        String temp2;

        for (int y=0;y<tempFiles.size();y++) {
            firstFile = tempFiles.get(y);
//            UpdateTestResults("In outer y loop num1 = " + num1);
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

