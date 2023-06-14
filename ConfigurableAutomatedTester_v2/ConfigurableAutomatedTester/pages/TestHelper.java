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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

/*******************************************************
 * DESCRIPTION: This class logs test steps and status
 *              and is used for some common formatting
 *              and field checking functionality.
 ******************************************************/
public class TestHelper{

    //region {Properties}
    private String _logFileName;
    private String get_logFileName() {
        return _logFileName;
    }
    void set_logFileName(String _logFileName) {
        this._logFileName = _logFileName;
    }

    private String _helpFileName;
    String get_helpFileName() {
        return _helpFileName;
    }

    private String _csvFileName;
    String get_csvFileName() {
        return _csvFileName;
    }
    void set_csvFileName(String _csvFileName) {
        this._csvFileName = _csvFileName;
    }


    void set_helpFileName(String _helpFileName) {
        this._helpFileName = _helpFileName;
    }

    private String _fileStepIndex;
    void set_fileStepIndex(String _fileStepIndex) {
        this._fileStepIndex = _fileStepIndex;
    }
    String get_fileStepIndex() { return _fileStepIndex; }
    //endregion

    private int screenShotsTaken = 0;
    private int maxScreenShotsToTake = 0;

    private String navigationMessageIndent;
//    private String getNavigationMessageIndent() {
//        return navigationMessageIndent;
//    }

    void setNavigationMessageIndent(String navigationMessageIndent) {
        this.navigationMessageIndent = navigationMessageIndent;
    }

    private boolean _executedFromMain;
    boolean is_executedFromMain() {
        return _executedFromMain;
    }


    void set_executedFromMain(boolean _executedFromMain) {
        this._executedFromMain = _executedFromMain;
    }

    private boolean _is_Maximized;
    private boolean get_is_Maximized() {return _is_Maximized; }
    void set_is_Maximized(boolean _is_Maximized) { this._is_Maximized = _is_Maximized ;}

    private double _backEndPageLoadDuration;
    private double _frontEndPageLoadDuration;
    void set_backEndPageLoadDuration(double _backEndPageLoadDuration) { this._backEndPageLoadDuration = _backEndPageLoadDuration; }
    double get_backEndPageLoadDuration() { return _backEndPageLoadDuration;}
    void set_frontEndPageLoadDuration(double _frontEndPageLoadDuration) { this._frontEndPageLoadDuration = _frontEndPageLoadDuration; }
    double get_frontEndPageLoadDuration() { return _frontEndPageLoadDuration;}
    private String _testFileName;
    void set_testFileName(String _testFileName) {
        this._testFileName = _testFileName;
    }
    String get_testFileName() {return _testFileName; }

    Dimension savedDimension = null;
    TestCentral testCentral;
    HelpWriter helpWriter;

    public TestHelper(TestCentral testCentral) {
        this.testCentral = testCentral;
        helpWriter = new HelpWriter(testCentral, this);
        set_executedFromMain(testCentral.is_executedFromMain());
    }

    /**************************************************************************************************
     * Description: This method reads the XML based Configuration file, populates the ConfigSettings
     *              object with the values from the file and returns that ConfigSettings object to the
     *              calling method.
     * @param configurationXmlFile - XML Configuration File
     * @param isExecutedFromMain - boolean value true if Executed from Main indicating that this is
     *                           being run as a stand-alone application.
     *
     * @return - ConfigSettings object
     **************************************************************************************************/
    ConfigSettings ReadConfigurationSettingsXmlFile(String configurationXmlFile, boolean isExecutedFromMain) {
        helpWriter.set_helpFileName(get_helpFileName());
        helpWriter.PrintSamples();
        helpWriter.WriteHtmlHelpFile();
        ConfigSettings configSettings = new ConfigSettings();
        String configValue;
        ArrayList<String> tempFiles = new ArrayList<>();

        File configFile = new File(configurationXmlFile);
        if (!configFile.exists() && isExecutedFromMain) {
            Scanner scanner = new Scanner(System.in);
            UpdateTestResults("Configuration File not found (" + configurationXmlFile + ")", false);
            UpdateTestResults("Enter the path to the config file: ", false);
            configurationXmlFile = scanner.nextLine();
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
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "BrowserType = " + AppConstants.ANSI_RESET + configSettings.get_browserType(), false);

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

                //CreateCSVStatusFiles - default set to false
                configValue = (eElement.getElementsByTagName(AppConstants.CreateCSVStatusFiles).item(0) != null) ?
                        eElement.getElementsByTagName(AppConstants.CreateCSVStatusFiles).item(0).getTextContent() : "none";
                configSettings.set_createCsvStatusFiles(configValue.toLowerCase());
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "CreateCSVStatusFiles = "  + AppConstants.ANSI_RESET + configSettings.get_createCsvStatusFiles(), false);


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
                    CreateSectionHeader(AppConstants.indent5 + "[ Start - Retrieving Files in specified folder. ]", AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW, true, true, false);
                    configSettings.reset_testFiles();
                    File temp = new File(configSettings.get_testFolderName());
                    //noinspection ConstantConditions
                    configSettings = GetAllFilesInFolder(temp, configSettings);
                    CreateSectionHeader(AppConstants.indent5 + "[ End Retrieving Files in specified folder. ]", AppConstants.FRAMED + AppConstants.ANSI_BLUE_BACKGROUND + AppConstants.ANSI_BOLD, AppConstants.ANSI_YELLOW, false, true, false);
                }
            }
        } catch (Exception e) {
            UpdateTestResults("The following error occurred while reading the Configuration Settings XML file: \r\n" + e.getMessage(), false);
        }

        if (tempFiles.size() > 0 && configSettings.get_specifyFileNames() && configSettings.get_sortSpecifiedTestFiles()) {
            SortTestXmlFiles(tempFiles, configSettings);
            UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent5 + "[ TestFileNames Re-Sorted, new order shown below - Case Sensitive Sort A-Z < a-z - to eliminate case, make all file paths and names the same case ]" + AppConstants.ANSI_RESET, false);
            for (int index=0;index<tempFiles.size();index++) {
                UpdateTestResults(AppConstants.ANSI_YELLOW + AppConstants.indent8 + "TestFileName = " + AppConstants.ANSI_RESET + tempFiles.get(index), false);
            }
        }
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
    List<TestStep> ReadTestSettingsXmlFile(List<TestStep> testSteps, String testXmlFileName) {

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(testXmlFileName);
            Argument argument;
            List<Argument> argumentList;
            int argCount;
            StringBuilder argumentMessage = null;

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            CreateSectionHeader("[ Start of Reading Test Settings File  ]", "", AppConstants.ANSI_PURPLE + AppConstants.ANSI_BOLD, true, true, false);
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
                                        final StringBuilder appendTemp = argumentMessage.append("\t\t ").append(AppConstants.ANSI_PURPLE).append(argNode.getNodeName()).append(": ").append(AppConstants.ANSI_RESET).append(argument.get_parameter());
                                        UpdateTestResults(argumentMessage.toString(), false);
                                        argumentMessage = null;
                                    } else {
                                        argumentMessage = new StringBuilder(AppConstants.ANSI_PURPLE + AppConstants.indent8 + "Argument " + argNode.getNodeName() + ": " + AppConstants.ANSI_RESET + argument.get_parameter() + ((argument.get_parameter().length() > 60) ? "\r\n\t\t " : ""));
                                    }
                                    argumentList.add(argument);
                                }
                            }
                            testStep.ArgumentList = argumentList;
                            if (argumentMessage != null) {
                                if (argumentMessage.toString().endsWith("\r\n\t\t ")) {
                                    //argumentMessage = new StringBuilder(argumentMessage.substring(0, argumentMessage.lastIndexOf("\r") - 1));
                                    argumentMessage = new StringBuilder(argumentMessage.substring(0, argumentMessage.lastIndexOf("\r")));
                                }
                                UpdateTestResults(argumentMessage.toString(), false);
                                argumentMessage = null;
                            }
                        }
                    }
                    testSteps.add(testStep);
                }
            }
            CreateSectionHeader("[ End of Reading Test Settings File  ]", "", AppConstants.ANSI_PURPLE + AppConstants.ANSI_BOLD, false, true, false);
        } catch(Exception e) {
            UpdateTestResults("The following error occurred while reading the Test Settings XML file: \r\n" + e.getMessage(), false);
        }
        return testSteps;
    }


    //TODO: WORKING HERE GAJ - for reading the API xml file as an xml document for xPath access
//    String ReadTestSettingsXmlFile(String xmlEndPointFileContent, String xpath_expression, String searchValue) {
//        String matchType = "No match";
//        try {
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(xmlEndPointFileContent.getBytes()));
//            XPathFactory xPathfactory = XPathFactory.newInstance();
//            XPath xpath = xPathfactory.newXPath();
//            XPathExpression expr = xpath.compile(xpath_expression);
//            NodeList searchNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
//            String textContent;
//            for (int x = 0; x < searchNodes.getLength(); x++) {
//                Node checkNode = searchNodes.item(x);
//                textContent = checkNode.getTextContent();
//                if (textContent.equals(searchValue)) {
//                    matchType = "Exact";
//                    break;
//                } else if (textContent.toLowerCase().equals(checkNode.getTextContent().toLowerCase())) {
//                    matchType = "Case Incorrect match";
//                    break;
//                }
//            }
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (XPathExpressionException e) {
//            e.printStackTrace();
//        }
//        return matchType;
//    }


//    public static org.w3c.dom.Document loadXMLFrom(String xml)
//            throws org.xml.sax.SAXException, java.io.IOException {
//        return loadXMLFrom(new java.io.ByteArrayInputStream(xml.getBytes()));
//    }
//    public static org.w3c.dom.Document loadXMLFrom(java.io.InputStream is)
//            throws org.xml.sax.SAXException, java.io.IOException {
//        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        javax.xml.parsers.DocumentBuilder builder = null;
//        try {
//            builder = factory.newDocumentBuilder();
//        }
//        catch (javax.xml.parsers.ParserConfigurationException ex) {
//        }
//        org.w3c.dom.Document doc = builder.parse(is);
//        is.close();
//        return doc;
//    }

    /**********************************************************************************************
     * Description: Creates the Section Headers based on the input parameters.
     * @param sectionHeading - Heading Text but may include colors
     * @param backgroundColor - Background Color, use empty string if not used.
     * @param foregroundColor - Foreground Color, use empty string if not used.
     * @param isSectionStart - True if Section Start, else False
     * @param hasMessageBackground - True if Message portion has background color, else False
     * @param writeToLog - True if writing to Log File, else False
     **********************************************************************************************/
    void CreateSectionHeader(String sectionHeading, String backgroundColor, String foregroundColor, Boolean isSectionStart, Boolean hasMessageBackground, Boolean writeToLog) {
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

        //this will supplement the length for items with the background stripped out behind the header
        if (!hasMessageBackground) {
            totalSize = totalSize + (backgroundColor.length() * 2);
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
     * @param driver - the webdriver
     * @param webAddress - The URL to navigate to.
     **************************************************************** */
    void NavigateToPage(WebDriver driver, String webAddress) throws InterruptedException{
        String indent = navigationMessageIndent != null ? navigationMessageIndent : AppConstants.indent8;
        int defaultMilliSecondsForNavigation = 10000;
        UpdateTestResults(indent + "Waiting the default wait time of " + defaultMilliSecondsForNavigation + " milliseconds for navigation to complete!", false);
        driver.get(webAddress);
        CheckPageLoadTiming(driver, indent);
        Thread.sleep(defaultMilliSecondsForNavigation);
    }

    /***************************************************************************
     * DESCRIPTION: Gets the Back-End and Front-End Page Load Timing and sets
     *              the associated class properties so that they can be retrieved
     *              by the method in TestCentral that initiated the call hierarchy.
     * @param driver - the webdriver
     * @param indent - the amount of space to indent to keep the output consistent.
     ******************************************************************************/
    private void CheckPageLoadTiming(WebDriver driver, String indent) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            double backendPerformance_calc = (double) js.executeScript("return (window.performance.timing.responseStart - window.performance.timing.navigationStart) / 1000");
            double frontendPerformance_calc = (double) js.executeScript("return (window.performance.timing.domComplete - window.performance.timing.responseStart) / 1000");
            //region { alternate method for doing this with separate variables but not as good as combinations above }
            //double navigationStart = (long)js.executeScript("return window.performance.timing.navigationStart");
            //double responseStart = (long)js.executeScript("return window.performance.timing.responseStart");
            //double domComplete = (long)js.executeScript("return window.performance.timing.domComplete");
            //double backendPerformance_calc = (double) (responseStart - navigationStart);
            //double frontendPerformance_calc = (double) (domComplete - responseStart);
            //endregion
            UpdateTestResults(indent + "Backend Performance Timing: " + backendPerformance_calc + " seconds.", true);
            UpdateTestResults(indent + "Frontend Performance Timing: " + frontendPerformance_calc + " seconds.", true);
            set_backEndPageLoadDuration(backendPerformance_calc);
            set_frontEndPageLoadDuration(frontendPerformance_calc);
        } catch (Exception e) {
            UpdateTestResults("Error getting page timings: " + e.getMessage() + " for step " + (get_fileStepIndex() != null ? get_fileStepIndex() : "F_S_"), true);
        }
    }


    /***************************************************************************************
     *  DESCRIPTION:
     *  Navigates to the web address passed in and sleeps for the
     *  number of milliseconds passed in.
     * @param driver - the WebDriver
     * @param webAddress - web page URL
     * @param milliseconds - number of milliseconds to wait after loading the page
     *                       to allow it to load
     ************************************************************************************** */
    void NavigateToPage(WebDriver driver, String webAddress, int milliseconds) throws InterruptedException{
        if (milliseconds > 0) {
            UpdateTestResults(AppConstants.indent8 + "Waiting " + milliseconds  + " milliseconds, as directed, for navigation to complete!", false);
            driver.get(webAddress);
            CheckPageLoadTiming(driver, AppConstants.indent8);
            Thread.sleep(milliseconds);
        } else {
            NavigateToPage(driver, webAddress);
        }
    }



    /***************************************************************************************
     * DESCRIPTION:  This method checks to ensure that Screenshots are configured
     * to be taken.
     * If screenshots are configured, it checks the current number of screenshots against
     * the total taken to ensure that it doesn't take more than configured.
     * For error screen captures, this count is ignored so that issues can be troubleshot
     * by the user.
     * Saves the current screen dimensions, calls the resize method to expand the screen
     * to capture the entire page and saves a screenshot to the the specified folder.
     * If a screenshot folder is not configured screenshots will be saved in config folder.
     * @param driver - the WebDriver
     * @param screenShotName - Name to save the ScreenShot being taken
     * @param screenShotFolder - Folder where the ScreenShot should be saved.
     * @param isError - Is this ScreenShot for an error condition.
     ************************************************************************************** */
    void CaptureScreenShot(WebDriver driver, String screenShotName, String screenShotFolder, boolean isError, String fileStepIndex) {
        if ((maxScreenShotsToTake > 0 && screenShotsTaken < maxScreenShotsToTake) || (maxScreenShotsToTake == 0)) {
            try {
                //get the original dimensions and save them
                Dimension originalDimension = driver.manage().window().getSize();
                //region { This is how to get the screen dimensions but found that the maximized value and screen dimensions didn't match }
                //savedDimension = savedDimension == null ? originalDimension : savedDimension;
                //int height = originalDimension.height;
                //int width = originalDimension.width;
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
                    if (HelperUtilities.isWindows() &&  !screenShotFolder.endsWith("\\")) {
                        screenShotFolder = screenShotFolder + "\\";
                    } else if (HelperUtilities.isMac() && !screenShotFolder.endsWith("/")) {
                        screenShotFolder = screenShotFolder + "/";
                    }
                    FileUtils.copyFile(source, new File(screenShotFolder + screenShotName + fileExtension));
                } else { //this will never happen, as the configuration folder is set in the calling method for errors
                    if (!Files.exists(Paths.get("Config/ScreenShots"))) {
                        Files.createDirectory(Paths.get("Config/ScreenShots"));
                    }
                    FileUtils.copyFile(source, new File("Config/ScreenShots/" + screenShotName + fileExtension));
                    screenShotFolder = "Config/ScreenShots/";
                }

                if (!isError) {
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
     * @param driver - the WebDriver
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
        StringBuilder cleanValue = new StringBuilder();

        for (int x=0;x<=(screenShotName.length()-1);x++) {
            if (allowedCharacters.contains(screenShotName.substring(x, x + 1).toLowerCase()))
            {
                cleanValue.append(screenShotName, x, x + 1);
            }
        }
        return cleanValue.toString();
    }


    /****************************************************************************
     * DESCRIPTION:  This new method will display the message on screen and
     * may optionally write the message to
     *
     * @param testMessage - The message that is displayed and optionally
     *                    written to the log.
     * @param  writeToLog - Indicates if this message should be written to the log.
     ****************************************************************************  */
    void UpdateTestResults(String testMessage, boolean writeToLog) {
        set_executedFromMain(testCentral.is_executedFromMain());
        /*System.out.println("====================================================");
        System.out.println("testMessage = " + testMessage);
        System.out.println("====================================================");
        */

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
                    //System.out.println("#1 in the else");  //debugging
                    if (testMessage.contains("Successful") || testMessage.contains("Failed") || testMessage.contains("Error")) {
                        WriteToFile(get_logFileName(), ""); //write blank line
                        //System.out.println("#2 in the else inner if  " + get_csvFileName()); //debugging
                        if (get_csvFileName() != null) {
                            //System.out.println("#3 in the else inner inner if");  //debugging
                            WriteToCSV(CleanMessage(testMessage));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (testMessage.contains("Successful")) {
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
                System.out.println();
            }
        } else if (testMessage.contains("[") && ((testMessage.toLowerCase().contains("end") && !testMessage.toLowerCase().contains("send") && !testMessage.toLowerCase().contains("end conditional"))
                || testMessage.toLowerCase().contains("revert"))) {
            if (!is_executedFromMain()) {
                System.out.println(PadSection(testMessage));
            } else {
                System.out.println(PadSection(CleanMessage(testMessage)));
            }
            System.out.println();
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
                //System.out.println(CleanMessage(testMessage));
                System.out.println(PadSection(CleanMessage(testMessage)));
            }
        }
    }

    /*****************************************************************************
     * Description: Parses the Successful/Failed message from the UpdateResults()
     *              method and creates/writes to a comma delimited text output file.
     *              Uses keywords that are in every message to parse most values
     *              and searches for special keywords for one field and if present
     *              uses the parsed value else uses an empty value.
     *
     * @param testMessage - The Successful or Failed Message from the
     *                    UpdateResults() method.
     ******************************************************************************/
    private void WriteToCSV(String testMessage) {
        String fileName = this.get_csvFileName();
        String step = testMessage.substring(testMessage.indexOf("for step ") + "for step ".length());
        if (step.contains(" ")) {
            step = step.substring(0, step.indexOf(" "));
        }
        String status = testMessage.contains("Successful") ? "Successful" : testMessage.trim().startsWith("Error") ? "Error" : testMessage.trim().startsWith("Fail") ? "Failure" : null;

        if (status == null) {
            return;
        }

        int startPos = testMessage.trim().startsWith("Failed") ? testMessage.indexOf("Failed") + "Failed".length() : testMessage.indexOf(status) + status.length();
        int endPos = testMessage.indexOf(" for step ");
        //DebugDisplay("testMessage =" + testMessage);
        String message = testMessage.substring(startPos, endPos).trim();
        message = message.substring(0,1).toUpperCase() + message.substring(1);
        //region {Debugging - CSV Column data}
        //DebugDisplay("step = (" + step + ")");
        //DebugDisplay("message = (" + message + ")");
        //DebugDisplay("status = (" + status + ")");
        //endregion
        if (message.contains("\r\n")) {
            message = "\"" + message + "\"";
        }
        String variableOutput = "";
        //TODO: ADD PARSING FOR FRONT END AND BACK END TIMINGS.  WILL REQUIRE SOME THOUGHT BEFORE IMPLEMENTING TO BE USEFUL BECAUSE RETURNS 2 VALUES (MAY NEED TO SPLIT RESPONSE OUTPUT) (2 numeric values)
        //TODO: ADD PARSING FOR HTTP RESPONSE FOR GET AND POST COMMANDS (1 numeric value) (Done)
        //TODO: ADD PARSING FOR ELEMENT COUNT   (1 numeric value) (Done)
        //TODO: ADD PARSING FOR SQL SERVER, JSON AND XML QUERY (Maybe not as this can be a string)
        if (message.contains("Difference Percentage")) {
            startPos = message.indexOf(":") + 1;
            endPos = message.indexOf("(") -1;
            variableOutput = message.substring(startPos, endPos).trim();
        } else if (message.contains("count of")) {
            startPos = message.indexOf("Actual: ") + "Actual: ".length();
            endPos = message.length();
            variableOutput =  message.substring(startPos, endPos).trim();
        } else if (message.contains("HTTP Response")) {
            startPos = message.indexOf("Actual: (") + "Actual: (".length();
            endPos = message.lastIndexOf(")");
            variableOutput = "[" + message.substring(startPos, endPos).trim() + "]";
        } else if (message.contains("Parse and Calculate")) {
            startPos = message.indexOf("Actual: (") + "Actual: (".length();
            endPos = message.lastIndexOf(")");
            variableOutput = "=\"(" + message.substring(startPos, endPos).trim() + ")\"";
        }

        String csv = step.replace("\n","") + "," + message.replace("\n","") + "," + status + "," + variableOutput + "," + get_testFileName();
        WriteToFile(fileName, csv);
    }

    /*************************************************************************
     * Description: Checks if a value is Numeric or not and returns true if
     *              the value is numeric and false if the value is not
     *              numeric.
     * @param character
     * @return
     *************************************************************************/
    private Boolean isNumeric(String character) {

        boolean status = true;

        try {
            if (character != null) {
                int returnValueCheck = Integer.parseInt(character);
            }
        } catch (NumberFormatException ne){
            status = false;
        }

        return status;
    }

    /*****************************************************************************
     * Description: Used for outputting to the screen for debugging purposes.
     *              This method may not be in use at times but never delete it as
     *              it separates the output from the UpdateResults() output method
     *              and never writes to the log.
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
     ********************************************************************************/
    void WriteToFile(String fileName, String fileContents) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(fileContents);
            writer.newLine();
            //writer.close();
        }
        catch(Exception ex) {
            //if (ex != null && ex.getMessage() != null) {
            if (ex.getMessage() != null) {
                UpdateTestResults(AppConstants.ANSI_RED + AppConstants.ANSI_BOLD + "The following error occurred when attempting to write to the test log file:" + ex.getMessage(), false);
            }
        }
    }


    /******************************************************************************
     * Description: Deletes the file using the fileName passed in
     * @param fileName - Name of the file to delete.
     *******************************************************************************/
    void DeleteFile(String fileName) {
        try {
            File fileToDelete = new File(fileName);
            if (fileToDelete.exists()) {
                boolean result = fileToDelete.delete();
                if (!result) {
                    UpdateTestResults("Unable to delete File, " + fileName + "!!!\r\nIf this has been set to Read Only, please remove that attribute as this help file is regenerated each time the application runs!!!", false);
                }
            }
        }
        catch (Exception ex) {
            UpdateTestResults("Error Deleting file: " + ex.getMessage(), false);
        }
    }


    /********************************************************************************
     * Description: A safe way to test if the text passed in can be converted to
     *              an integer.
     * @param text - String to be parsed into an Integer
     * @return - Integer if parseable, else null.
     *********************************************************************************/
    static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /***********************************************************************************
     * Description: Gets the Dimensions (Width and Height) of the Image File passed in.
     * @param imageFileName - Image File
     * @return  - String of dimensions with descriptors
     * @throws Exception - Missing file exception
     **********************************************************************************/
    String GetImageDimensions(String imageFileName) throws Exception {
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

    /*********************************************************************
     * DESCRIPTION:
     *      Creates a new timestamp to act as a unique id so that
     *      the same test can be used over and over and append this value
     *      to create a new value.
     ******************************************************************** */
    String GetUniqueIdentifier() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);

        return ts.toString().replace("-","").replace(" ","").replace(":","").replace(".","");
    }



    /**********************************************************************
     * Description: This method sorts the test files by file path and name
     *              alphanumerically.
     * @param tempFiles - ArrayList of files
     * @param configSettings - Configuration Settings Object containing
     *                       an ArrayList of files and where the sorted
     *                       files will be updated.
     **********************************************************************/
    private void SortTestXmlFiles(ArrayList<String> tempFiles, ConfigSettings configSettings) {
        String configValue;
        String firstFile;
        String secondFile;

        for (int y=0;y<tempFiles.size();y++) {
            for (int x = 0; x < tempFiles.size() - 1; x++) {
                firstFile = tempFiles.get(x);
                secondFile = tempFiles.get(x + 1);
                if (firstFile.compareTo(secondFile) > 0) {
                    tempFiles.remove(x + 1);
                    //noinspection SuspiciousListRemoveInLoop
                    tempFiles.remove(x);
                    tempFiles.add(x, secondFile);
                    tempFiles.add(x + 1, firstFile);
                }
            }
        }

        configSettings.reset_testFiles();

        for (int z=0;z<tempFiles.size();z++) {
            configValue = tempFiles.get(z).substring(tempFiles.get(z).indexOf("=") + 1);
            configSettings.set_testSettingsFile(configValue, z);
        }
    }


    /***********************************************************************
     * Description: This method gets all files in the folder passed in
     *              based on the the config settings allowing filtering of
     *              files based on what the files start with, contain, or
     *              ends with.
     * @param folder - folder where the files exist
     * @param configSettings - Configuration Settings Object where the list of files
     *                       will be updated.
     * @return - ConfigSettings object with a list of files from the folder provided.
     ***********************************************************************/
    private ConfigSettings GetAllFilesInFolder(final File folder, ConfigSettings configSettings) {
        //List<String> testFiles = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            String temp;
            if (fileEntry.isFile()) {
                temp = fileEntry.getAbsoluteFile().toString(); //  fileEntry.getName();
                switch (configSettings.get_folderFileFilterType().toLowerCase()) {
                    case "ends_with":
                        if (temp.toLowerCase().endsWith(configSettings.get_folderFileFilter().toLowerCase())) {
                            configSettings.set_testSettingsFile(temp);
                            UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_YELLOW + "File: " + AppConstants.ANSI_RESET + temp, false);
                        }
                        break;
                    case "starts_with":
                        if (temp.toLowerCase().startsWith(configSettings.get_folderFileFilter().toLowerCase())) {
                            configSettings.set_testSettingsFile(temp);
                            UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_YELLOW + "File: " + AppConstants.ANSI_RESET + temp, false);
                        }
                        break;
                    case "contains":
                        if (temp.toLowerCase().contains(configSettings.get_folderFileFilter().toLowerCase())) {
                            configSettings.set_testSettingsFile(temp);
                            UpdateTestResults(AppConstants.indent5 + AppConstants.ANSI_YELLOW + "File: " + AppConstants.ANSI_RESET + temp, false);
                        }
                        break;
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
     * @param testMessage - The string to clean
     * @return - A string with all Ansi character codes removed
     ********************************************************************/
    private String CleanMessage(String testMessage) {

        return testMessage.replace(AppConstants.ANSI_YELLOW,"")
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
                .replace(AppConstants.ANSI_GREEN_BACKGROUND_BRIGHT,"").replace(AppConstants.ANSI_WHITE_BACKGROUND_BRIGHT,"")
                .replace(AppConstants.ANSI_CYAN_BRIGHT,"");

    }



    /************************************************************************
     * DESCRIPTION: This method pads the section outline format to a
     *              specific length with additional "═" characters to
     *              meet the maxCharacters length.
     * @param sectionTitle - Title of the section being created.
     * @return - Section header string.
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
     * @param padSize - Number of spaces to pad the text.
     * @param multiplier  - Number of spaces to indent.
     * @return - returns a padded string
     ***********************************************************************/
    String PadIndent(int padSize, int multiplier) {
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
    String PrePostPad(String value, String chr, int prePad, int totalSize) {
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
    void SetWindowContentDimensions(WebDriver driver, int width, int height)
    {
        Dimension sessionDimension = new Dimension(width, height);
        driver.manage().window().setSize(sessionDimension);
    }

    /***********************************************************************************
     * Description: This method attempts to ascertain whether the passed
     *              in string is a URL.
     * @param navigateUrl - Parameter being checked to determine if it is a URL
     * @return - True if determined to be a URL, else false;
     **********************************************************************************/
    boolean CheckIsUrl(String navigateUrl) {
        boolean status = false;
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
    String GetUnusedFileName(String fileName) {
        File tmpFile = new File(fileName);
        String checkFileName = fileName;
        int counter = 1;
        if (tmpFile.exists()) {
            while (tmpFile.exists()) {
                //checkFileName = fileName.substring(0,fileName.lastIndexOf(".") - 1) + counter + fileName.substring(fileName.lastIndexOf("."));
                checkFileName = fileName.substring(0,fileName.lastIndexOf(".")) + counter + fileName.substring(fileName.lastIndexOf("."));
                tmpFile = new File(checkFileName);
                if (!tmpFile.exists()) {
                    break;
                }
                counter++;
            }
        }
        return checkFileName;
    }

    /*******************************************************************
     * Description: Tests a string to determine if it is null or empty.
     *
     * @param testString - String to test if is null or empty.
     * @return - Returns True if null or Empty, else False.
     ********************************************************************/
    boolean IsNullOrEmpty(String testString) {
        boolean status = false;
        if (testString == null || testString.isEmpty()) {
            status = true;
        }
        return status;
    }

}

