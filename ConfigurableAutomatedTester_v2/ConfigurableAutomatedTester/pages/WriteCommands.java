import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WriteCommands {

    TestCentral testCentral;
    TestHelper testHelper;  // = new TestHelper();
    ReadCommands readCommands;
    HelperUtilities helperUtilities; // = new HelperUtilities();
    TestCreatorUtility testCreatorUtility;
    WebDriver driver;
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }


    public WriteCommands(TestCentral testCentral) {
        this.testCentral = testCentral;
        readCommands = testCentral.readCommands; // new ReadCommands(testCentral);
        helperUtilities = new HelperUtilities(testCentral);


        if (testHelper == null) {
            testHelper = new TestHelper(testCentral);
            testHelper.set_executedFromMain(testCentral.is_executedFromMain());
        }
        testCreatorUtility = new TestCreatorUtility(testCentral, testHelper);
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
    void PerformWriteActions(TestStep ts, String fileStepIndex) throws Exception {
        //Perform all non read actions below that use an accessor
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? testCentral.GetArgumentValue(ts, 1, null) : null;

        if (ts.get_accessorType() != null && (((ts.get_accessorType().toLowerCase().contains(AppConstants.xpathCheckValue)) || (ts.get_accessorType().toLowerCase().contains(AppConstants.cssSelectorCheckValue)) ||
                (ts.get_accessorType().toLowerCase().contains(AppConstants.tagNameCheckValue)) || (ts.get_accessorType().toLowerCase().contains(AppConstants.idCheckValue)) ||
                (ts.get_accessorType().toLowerCase().contains(AppConstants.classNameCheckValue)))
                && (!ts.get_command().toLowerCase().contains(AppCommands.SendKeys) && !ts.get_command().toLowerCase().contains(AppCommands.Send_Keys)
                && !ts.get_command().toLowerCase().contains(AppCommands.Wait) && !ts.get_command().toLowerCase().contains(AppConstants.persistStringCheckValue)))) {
            PerformAccessorActionController(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().contains(AppCommands.SendKeys) || ts.get_command().toLowerCase().contains(AppCommands.Send_Keys) ||
                (command != null && (command.toLowerCase().equals(AppCommands.SendKeys) || command.toLowerCase().equals(AppCommands.Send_Keys)))) {
            SendKeysController(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().contains(AppCommands.WaitFor)) {
            //wait for a speficic element to load
            WaitForElement(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Connect_To_Database)) {
            String databaseType = testCentral.GetArgumentValue(ts, 0, null);
            if (databaseType.toLowerCase().equals(AppCommands.MongoDb) || databaseType.toLowerCase().contains(AppCommands.Mongo)) {
                //connect to mongo db or close an open mongo db connection
                readCommands.SetMongoClient(ts, fileStepIndex);
            } else if (databaseType.toLowerCase().contains(AppConstants.SqlServer.toLowerCase())) {
                //establish a connection to a sql server database - connection lives until closed or end of the test
                readCommands.SetSqlServerClient(ts, fileStepIndex);
            } else {
                testCentral.ArgumentOrderErrorMessage(ts, ts.get_command());
            }
        } else if (ts.get_command().toLowerCase().equals(AppCommands.CloseDatabaseConnection) || ts.get_command().toLowerCase().equals(AppCommands.CloseDatabase) ) {
            String databaseType = testCentral.GetArgumentValue(ts, 0, null);
            if (databaseType.toLowerCase().equals(AppConstants.MongoDb.toLowerCase()) || databaseType.toLowerCase().contains(AppCommands.Mongo)) {
                testCentral.CloseOpenConnections(AppConstants.MongoDb, fileStepIndex);
            } else if (databaseType.toLowerCase().contains(AppConstants.SqlServer.toLowerCase())) {
                testCentral.CloseOpenConnections(AppConstants.SqlServer, fileStepIndex);
            }
        } else if (ts.get_command() != null && ts.get_command().toLowerCase().contains(AppConstants.persistStringCheckValue)) {
            readCommands.PersistValueController(ts,fileStepIndex);
        } else if (ts.get_accessorType() == null || ts.get_accessorType().toLowerCase().contains("n/a")) {
            //TODO: FIGURE OUT WHAT YOU WERE TROUBLESHOOTING WITH THESE MESSAGES WHEN YOU SET THIS APPLICATION ASIDE
//                        pageHelper.UpdateTestResults("SearchType = n/a  - Good so far - Accessor: " + ts.get_xPath() +
//                                " Expected Value:" + ts.get_expectedValue() + " Lookup Type: " + ts.get_searchType() +
//                                " Perform Action: " + ts.getPerformWrite() + " IsCrucial: " + ts.get_isCrucial());
            //perform all non-read actions below that do not use an accessor
            if (ts.get_command().toLowerCase().contains(AppCommands.Navigate)) {
                testCentral.PerformExplicitNavigation(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Wait) || ts.get_command().toLowerCase().equals(AppCommands.Delay)) {
                int delayMilliSeconds = testCentral.GetArgumentNumericValue(ts, 0, 0);
                testCentral.DelayCheck(delayMilliSeconds, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.ScreenShot)) {
                //scheduled screenshot capture action
                testHelper.UpdateTestResults(AppConstants.indent5 + "Taking Screenshot for step " + fileStepIndex, false);
                testCentral.CheckScreenShotArgumentOrder(ts);
                String fileName = testCentral.GetArgumentValue(ts, 0, null);
                String stringDimensions = testCentral.GetArgumentValue(ts, 1, null);
                if (stringDimensions != null) {
                    SetScreenShotDimensions(stringDimensions);
                }
                if (fileName == null) {
                    //PerformScreenShotCapture(GetBrowserUsed() + "_" + ts.get_expectedValue() + "_" + fileStepIndex + "_", fileStepIndex);
                    PerformScreenShotCapture(testCentral.GetBrowserUsed() + "_" + ts.get_command() + "_" + fileStepIndex + "_", fileStepIndex);
                }else {
                    PerformScreenShotCapture(fileName, fileStepIndex);
                }
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.URL)) {
                CheckUrlWithoutNavigation(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.SwitchToTab)) {
                testCentral.SwitchToTab(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Login)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing login for step " + fileStepIndex, true);
                String userId = testCentral.GetArgumentValue(ts, 0, null);
                String password = testCentral.GetArgumentValue(ts, 1, null);
                String url = testCentral.GetArgumentValue(ts, 2, testCentral.GetCurrentPageUrl());
                if (testHelper.CheckIsUrl(url)) {
                    testCentral.Login(url, userId, password, fileStepIndex);
                    testHelper.UpdateTestResults(AppConstants.indent5 + "Login complete for step " + fileStepIndex, true);
                } else {
                    testCentral.ArgumentOrderErrorMessage(ts, ts.get_command());
                }
            } else if (ts.get_command().toLowerCase().contains(AppCommands.CreateTestPage) || ts.get_command().toLowerCase().contains(AppCommands.Create_Test_Page)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Create Test Page for step " + fileStepIndex, true);
                testCentral.CheckCreateTestFileArgumentOrder(ts);
                //String createTestFileName = CreateTestPage(ts, fileStepIndex);
                String createTestFileName = testCreatorUtility.CreateTestPage(ts);
                testHelper.UpdateTestResults("Create Test Page results written to file: " + createTestFileName, false);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Close_Child_Tab)) {
                testCentral.CloseOpenChildTab(ts, fileStepIndex);
            }  else if (ts.get_command().toLowerCase().equals(AppCommands.Compare_Images)) {
                CompareImagesController(ts, fileStepIndex);
            }
        }
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
                subAction = testCentral.GetArgumentValue(ts, 1, null);
            }
            status = PerformAction(ts, subAction, fileStepIndex);
        }

        //if not a right click context command
        if (!ts.get_command().toLowerCase().contains(AppCommands.Right_Click) && !ts.get_command().toLowerCase().contains(AppCommands.SendKeys)
                && !ts.get_command().toLowerCase().contains(AppCommands.Send_Keys) && !ts.get_command().toLowerCase().contains(AppCommands.Switch_To_IFrame)) {
            //url has changed, check url against expected value
            String expectedUrl = ts.get_expectedValue();

            if (ts.ArgumentList != null && ts.ArgumentList.size() > 1) {
                delayMilliSeconds = testCentral.GetArgumentNumericValue(ts, 0, AppConstants.DefaultTimeDelay);
                testCentral.DelayCheck(delayMilliSeconds, fileStepIndex);
                expectedUrl = ts.get_expectedValue();
            }

            String actualUrl = testCentral.GetCurrentPageUrl();
            if (expectedUrl != null) {
                if (ts.get_crucial()) {
                    assertEquals(expectedUrl, actualUrl);
                } else {
                    try {
                        assertEquals(expectedUrl, actualUrl);
                    } catch (AssertionError ae) {
                        //if the non-crucial test fails, take a screenshot and keep processing remaining tests
                        if (testCentral.screenShotSaveFolder == null || testCentral.screenShotSaveFolder.isEmpty()) {
                            testHelper.CaptureScreenShot(driver, testCentral.GetBrowserUsed() + ts.get_accessorType() + fileStepIndex + "Element_Not_Found" + ts.get_accessor().replace(' ', '_'), testCentral.configurationFolder, true, fileStepIndex);
                        } else {
                            testHelper.CaptureScreenShot(driver, testCentral.GetBrowserUsed() + ts.get_accessorType() + fileStepIndex + "Element_Not_Found" + ts.get_accessor().replace(' ', '_'), testCentral.screenShotSaveFolder, true, fileStepIndex);
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

        isNumeric = testCentral.CheckArgumentNumeric(ts, ts.ArgumentList.size() -1);
        if (isNumeric && (ts.get_command().toLowerCase().contains(AppCommands.SendKeys) || ts.get_command().toLowerCase().contains(AppCommands.Send_Keys)))
        {
            timeDelay = testCentral.GetArgumentNumericValue(ts, ts.ArgumentList.size() -1, 400);
        }

        for (Argument argument : ts.ArgumentList) {
            item = argument.get_parameter();
            //if this is a switch to iframe command skip the first argument.
            if ((ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) && counter > 0) || !ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame)) {
                //if this is a switch to iframe command skip the "sendkeys" subcommand
                if (!item.toLowerCase().contains(AppCommands.SendKeys) && !item.toLowerCase().contains(AppCommands.Send_Keys)) {
                    boolean status = PerformAction(ts, item, fileStepIndex);
                    testCentral.DelayCheck(timeDelay, fileStepIndex);
                }
            }
            counter++;
        }
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
        testCentral.CheckWaitArgumentOrder(ts);
        String elementIdentifier = ts.get_command().toLowerCase().trim().contains(AppCommands.Page) ? testCentral.GetArgumentValue(ts, 0, "n/a") : testCentral.GetArgumentValue(ts, 0, null);
        int maxTimeInSeconds = testCentral.GetArgumentNumericValue(ts, 1, AppConstants.DefaultElementWaitTimeInSeconds);

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
            if (!accessorType.equals("page")) {
                element = new WebDriverWait(driver, maxTimeInSeconds).until(ExpectedConditions.presenceOfElementLocated((By) readCommands.GetWebElementByAccessor(ts)));
            } else {
                if (!elementIdentifier.toLowerCase().trim().contains("n/a")) {
                    try {
                        testHelper.NavigateToPage(driver, elementIdentifier);
                    } catch (Exception ex) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to navigate error: " + ex.getMessage() + " for step " + fileStepIndex, true);
                    }
                }
                pageLoadComplete = new WebDriverWait(driver, maxTimeInSeconds).until(
                        webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            }

            if (!ts.get_command().toLowerCase().trim().contains(AppCommands.Page)) {
                if (element != null) {
                    testHelper.UpdateTestResults("Successful load of element " + accessor + " within max time setting of " + maxTimeInSeconds + " for step " + fileStepIndex, true);
                }
            } else {
                if (pageLoadComplete) {
                    testHelper.UpdateTestResults("Successful load of page " + testCentral.GetCurrentPageUrl() + " within max time setting of " + maxTimeInSeconds + " for step " + fileStepIndex, true);
                }
            }
        } catch (TimeoutException ae) {
            if (ts.get_command().toLowerCase().trim().contains(AppCommands.Page)) {
                //TODO: INVESTIGATE WHY YOU ADDED AL+ IN THE INITIAL MESSAGE
                //testHelper.UpdateTestResults("Failed to find the element " + GetCurrentPageUrl() + " within the set max time of " + maxTimeInSeconds + " for step " + fileStepIndex + " AL+", true);
                testHelper.UpdateTestResults("Failed to find the element " + testCentral.GetCurrentPageUrl() + " within the set max time of " + maxTimeInSeconds + " for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults("Failed to load element " + accessor + " within max time setting of " + maxTimeInSeconds + " at step " + fileStepIndex, true);
            }
            if (ts.get_crucial()){
                throw (ae);
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


    /*************************************************************
     * DESCRIPTION: Performs a screen shot capture by calling the
     *      screen shot capture method in the pageHelper class.
     * @param value - file name or portion of file name
     * @param fileStepIndex - the file index and the step index.
     ************************************************************ */
    private void PerformScreenShotCapture(String value, String fileStepIndex) {
        String delimiter = System.getProperty("file.separator");
        if (!value.contains(delimiter)) {
            testHelper.CaptureScreenShot(driver, value, testCentral.screenShotSaveFolder, false, fileStepIndex);
        } else {
            String folder = value.substring(0, value.lastIndexOf(delimiter));
            String fileName = value.substring(value.lastIndexOf(delimiter));
            testHelper.CaptureScreenShot(driver, fileName, folder, false, fileStepIndex);
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
            int delayMilliSeconds = testCentral.GetArgumentNumericValue(ts, 0, AppConstants.DefaultTimeDelay);
            testCentral.DelayCheck(delayMilliSeconds, fileStepIndex);
        }
        String actualUrl = testCentral.GetCurrentPageUrl();
        if (ts.get_crucial()) {
            assertEquals(expectedUrl, actualUrl);
        } else {
            if (expectedUrl.trim().equals(actualUrl.trim())) {
                testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Successful URL Check.  Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ") for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed URL Check.   Expected: (" + expectedUrl + ") Actual: (" + actualUrl + ") for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
            }
        }
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
                if (!command.toLowerCase().contains(AppCommands.Right_Click)) {
                    if (!command.toLowerCase().contains(AppCommands.DoubleClick)) {
                        if (testCentral.get_selectedBrowserType().equals(BrowserTypes.Internet_Explorer)) {
                            JavascriptExecutor js = (JavascriptExecutor) driver;
                            js.executeScript("arguments[0].click()", readCommands.GetWebElementByAccessor(ts));
                        } else {
                            readCommands.GetWebElementByAccessor(ts).click();
                        }
                        testHelper.UpdateTestResults("Successful - Click performed for step " + fileStepIndex, false);
                    } else {
                        //doubleclick
                        if (testCentral.get_selectedBrowserType().equals(BrowserTypes.Internet_Explorer)) {
                            JavascriptExecutor js = (JavascriptExecutor) driver;
                            String script;
                            if (ts.get_accessorType().equals(AppConstants.xpathCheckValue)) {
                                script = "var input = document.evaluate('" + ts.get_accessor() + "', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;\r\n" +
                                        "input.focus();\r\n" +
                                        "input.select();";
                            } else {
                                WebElement tmpElement = readCommands.GetWebElementByAccessor(ts);
                                String tmpXpath = testCentral.GenerateXPath(tmpElement,  "");
                                script = "var input = document.evaluate('" + tmpXpath + "', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;\r\n" +
                                        "input.focus();\r\n" +
                                        "input.select();";
                            }
                            js.executeScript(script);
                        } else {
                            Actions action = new Actions(driver);
                            action.doubleClick(readCommands.GetWebElementByAccessor(ts)).build().perform();
                        }
                        testHelper.UpdateTestResults("Successful - Double Click performed for step " + fileStepIndex, false);
                    }
                } else {  //right click element  //TODO: FIND A WAY TO RIGHT CLICK WITH JAVASCRIPT FOR INTERNET EXPLORER
                    Actions action = new Actions(driver);
                    if (command.toLowerCase().contains(AppCommands.Keys)) {
                        action.contextClick(readCommands.GetWebElementByAccessor(ts)).build().perform();
                        testHelper.UpdateTestResults("Successful - Right Click performed for step " + fileStepIndex, false);
                    } else {
                        action.contextClick(readCommands.GetWebElementByAccessor(ts)).build().perform();
                        SelectFromContextMenu(ts, fileStepIndex);
                        testHelper.UpdateTestResults("Successful - Right Click and Context menu sendkeys performed for step " + fileStepIndex, false);
                    }
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        } else if (command.toLowerCase().contains("screenshot")) {
            try {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Taking Screenshot for step " + fileStepIndex, true);
                subAction = testCentral.GetArgumentValue(ts, 0, subAction);
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
                //String uidReplacementChars = "**_uid_**";
                if (command.contains(AppCommands.SendKeys)) {
                    //added the below structure so that the unique identifier could be used with the persisted string.
                    if (subAction.toLowerCase().contains(AppConstants.persistedStringCheckValue) && !subAction.trim().contains(AppConstants.uidReplacementChars)) {
                        testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_CYAN + "Using Persisted value (" + testCentral.persistedString + ")" + AppConstants.ANSI_RESET, true);
                        subAction = testCentral.persistedString;
                    } else {
                        if (subAction.trim().toLowerCase().contains(AppConstants.persistedStringCheckValue) && subAction.trim().contains(AppConstants.uidReplacementChars)) {
                            testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_CYAN + "Using Persisted value (" + testCentral.persistedString + ")" + AppConstants.ANSI_RESET, true);
                            if (subAction.trim().indexOf(AppConstants.persistedStringCheckValue) < subAction.trim().indexOf(AppConstants.uidReplacementChars)) {
                                if (subAction.trim().indexOf(" ") > subAction.trim().indexOf(AppConstants.persistedStringCheckValue))
                                {
                                    subAction = testCentral.persistedString + " " + testCentral.uniqueId;
                                } else {
                                    subAction = testCentral.persistedString + testCentral.uniqueId;
                                }
                            } else {
                                if (subAction.trim().indexOf(" ") > subAction.trim().indexOf(AppConstants.uidReplacementChars))
                                {
                                    subAction = testCentral.uniqueId + " " + testCentral.persistedString;
                                } else {
                                    subAction =  testCentral.uniqueId + testCentral.persistedString;
                                }
                            }
                        }
                    }
                    testHelper.UpdateTestResults(AppConstants.indent5 + "Performing SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                }
                if (subAction.contains(AppCommands.Keys) || subAction.toLowerCase().contains(AppCommands.Keys)) {
                    testHelper.UpdateTestResults(AppConstants.indent8 + "Performing special SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                    readCommands.GetWebElementByAccessor(ts).sendKeys(testCentral.GetKeyValue(subAction, fileStepIndex));

                } else {
                    if (subAction.contains(AppConstants.uidReplacementChars)) {
                        testHelper.UpdateTestResults(AppConstants.indent5 + "Replacing Unique Identifier placeholder for step " + fileStepIndex, true);
                    }
                    subAction = subAction.replace(AppConstants.uidReplacementChars, testCentral.uniqueId);
                    testHelper.UpdateTestResults(AppConstants.indent8 + "Performing default SendKeys value = " + subAction + " for step " + fileStepIndex, true);
                    readCommands.GetWebElementByAccessor(ts).sendKeys(subAction);
                }
                status = true;
            } catch (Exception e) {
                status = false;
            }
        }
        return status;
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
            testCentral.DelayCheck(3000, fileStepIndex);
            testCentral.SwitchToTab(true, fileStepIndex);
            //DelayCheck(7000, fileStepIndex);
            //SwitchToTab(false, fileStepIndex);
            //SwitchBackToMainTab(fileStepIndex);
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
        String baseLineImage = testCentral.GetArgumentValue(ts,0, null);
        String actualImage = testCentral.GetArgumentValue(ts, 1, null);
        String differenceImage = testCentral.GetArgumentValue(ts, 2, null);
        String globalDifferenceImage = testCentral.GetArgumentValue(ts, 3, null);
        double acceptableDifference = testCentral.GetArgumentNumericDoubleValue(ts, 4, 0);

        if (!testHelper.IsNullOrEmpty(baseLineImage) && !testHelper.IsNullOrEmpty(actualImage) && !testHelper.IsNullOrEmpty(differenceImage)) {
            helperUtilities.testHelper = testHelper;
            helperUtilities.set_acceptableDifference(acceptableDifference);
            testHelper.CreateSectionHeader("[ Start Image Comparison Test ]", "", AppConstants.ANSI_CYAN, true, false, true);
            testHelper.UpdateTestResults(AppConstants.indent5 + "Performing Image Comparison for step " + fileStepIndex + "\r\n" +
                    AppConstants.indent8 + "(Baseline)Expected Image:" + baseLineImage + "\r\n" +
                    AppConstants.indent8 +  "Actual Image: " + actualImage, true);
            if (!testHelper.IsNullOrEmpty(globalDifferenceImage)) {
                helperUtilities.differenceFileForParent = new File(testHelper.GetUnusedFileName(globalDifferenceImage));
            } else {
                helperUtilities.differenceFileForParent = null;
            }
            helperUtilities.set_executedFromMain(testCentral.is_executedFromMain());
            helperUtilities.CompareImagesWithImageMagick(baseLineImage, actualImage, differenceImage, fileStepIndex);
            testHelper.CreateSectionHeader("[ End Image Comparison Test ]", "", AppConstants.ANSI_CYAN, false, false, true);
        }
    }
}
