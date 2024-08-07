import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TestCreatorUtility {

    final TestCentral testCentral;
    final TestHelper testHelper;

    ReadCommands readCommands;
    WriteCommands writeCommands;
    WebDriver driver;

    public TestCreatorUtility(TestCentral testCentral, TestHelper testHelper) {
        this.testCentral = testCentral;
        this.testHelper = testHelper;
        readCommands = testCentral.readCommands;
        writeCommands = testCentral.writeCommands;
    }

    //region { Create Test Page Methods }
    /****************************************************************************
     * DESCRIPTION: This method Creates an XML Test file or a text file, depending
     *          upon the test configuration, with element properties and
     *          attributes to allow users to more quickly create test files
     *          without inspecting each page element to do so.
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     *
     * @return - Returns the file name of the created test page
     *************************************************************************** */
    //private String CreateTestPage(TestStep ts, String fileStepIndex) {
    String CreateTestPage(TestStep ts) {
        driver = testCentral.driver;
        String cssSelector = testCentral.GetSpecificArgumentValue(ts,"includeelements","=","*");     //testCentral.GetArgumentValue(ts, 0, "*");
        String newFileName = testCentral.GetSpecificArgumentValue(ts,"filename","=","/config/newTestFile.txt");   //testCentral.GetArgumentValue(ts, 1, "/config/newTestFile.txt");
        String tagsToSkip = testCentral.GetSpecificArgumentValue(ts,"excludeElements","=",null);    //testCentral.GetArgumentValue(ts, 2, null);
        String [] skipTags = tagsToSkip.split(",");
        boolean formatted = ts.get_command().toLowerCase().contains(AppCommands.Format) ? true : false;
        String elementXPath = "";
        String consentScript = "";

        if (formatted) {
            consentScript = "\t<step>\r\n" +
                    "\t\t<command>check javascript value</command>\r\n" +
                    "\t\t<actionType>read</actionType>\r\n" +
                    "\t\t<expectedValue>Pass</expectedValue>\r\n" +
                    "\t\t<crucial>False</crucial>\r\n" +
                    "\t\t<arguments>\r\n" +
                    "\t\t\t<arg1>\r\n" +
                    "\t\t\t//close onetrust cookie consent form by accepting all cookies\r\n" +
                    "\t\t\t\tif (document.querySelector(\"#onetrust-accept-btn-handler\")) {\r\n" +
                    "\t\t\t\t\tdocument.querySelector(\"#onetrust-accept-btn-handler\").click();\r\n" +
                    "\t\t\t\t\treturn \"Pass\";\n" +
                    "\t\t\t\t}\r\n" +
                    "\t\t\t\treturn \"Fail\";\r\n" +
                    "\t\t\t</arg1>\r\n" +
                    "\t\t\t<arg2>showscript=false</arg2>\n" +
                    "\t\t</arguments>\n" +
                    "\t</step>\r\n";

        }


        //region { add feature - additional argument that allows existing file to be overwritten or kept and a new filename created if file exists }
        //String updateFileNameMessage;

//        String fileName = testHelper.GetUnusedFileName(newFileName);
//        if (!newFileName.equals(fileName)) {
//            updateFileNameMessage = "A File with the original file name existed.\r\n" +
//                    AppConstants.indent8 + "File name updated from: " + newFileName + " to " + fileName;
//            testHelper.UpdateTestResults(AppConstants.indent8 + updateFileNameMessage, true);
//        }
        //endregion

        //delete this file if it exists
        try {
            testHelper.DeleteFile(newFileName);
        } catch(Exception ex) {
            //let the delete file method handle this exception
        }

        //elements to skip if all elements used (*) - don't put this within the cssSelector assignment in case it is not provided
        testHelper.UpdateTestResults("Skipping Configured Tags To Skip: " + tagsToSkip, false);
        //testHelper.DebugDisplay("#1 cssSelector = " + cssSelector);
        try {
            //boolean wasFound = false;
            boolean canProceed;  // = true;
            List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
            //List<String> foundElements = new ArrayList<>();
            String elementType;
            //String elementXPath = "";
            String elementText;
            String elementHref;
            String elementSrc;
            String outputDescription;
            String elementAltText;
            String inputType;
            boolean isVisible = true;
            String script;

            //testHelper.DebugDisplay("formatted = " + formatted);
            if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(true));
                testHelper.WriteToFile(newFileName, CreateNavigationXmlTestStep(TestCentral.testPage, "TRUE"));
                testHelper.WriteToFile(newFileName, CreateScreenShotTestStep("FALSE"));
                testHelper.WriteToFile(newFileName, consentScript);
            } else {
                testHelper.WriteToFile(newFileName, "URL being used: " + TestCentral.testPage);
            }

            //testHelper.DebugDisplay("elements.size() = " + elements.size());
            for (WebElement element : elements) {
                try {
                    canProceed = true;
                    outputDescription = "";
                    elementType = element.getTagName();
                    isVisible = element.isDisplayed();
                    if (cssSelector.equals("*")) {
                        if (skipTags != null && skipTags.length > 0) {
                            for (String skipTag : skipTags) {
                                if (elementType.equals(skipTag)) {
                                    canProceed = false;
                                    break;
                                }
                            }
                        }
                    }

                    if (canProceed) {

                        elementXPath = testCentral.GenerateXPath(element, "");
                        //testHelper.DebugDisplay("#1 element " + elementXPath);
                        elementText = element.getText();

                        if (formatted) {
                            if (!elementType.equals("img")) {
                                elementXPath = testCentral.GenerateXPath(element, "");
                                if (isVisible) {
                                    if (elementText != null && !elementText.isEmpty()) {
                                        outputDescription = CreateReadActionXmlTestStep(elementXPath, elementText, "FALSE");
                                    }
                                } else {
                                    if (elementText != null && !elementText.isEmpty()) {
                                        outputDescription = "\t<!-- The following element is not visible by default -->\r\n";
                                        //script = "return arguments[0].innerHTML";
                                        script = "return arguments[0].innerText";
                                        elementText = (String) ((JavascriptExecutor) driver).executeScript(script, element);
                                        outputDescription += CreateReadActionXmlTestStep(elementXPath, elementText, "FALSE");
                                    }
                                }
                            }
                        } else {
                            if (elementText != null && !elementText.isEmpty()) {
                                outputDescription = "Element Type: " + elementType + " - Element xPath: " + elementXPath + " - Element Text: " + elementText;
                            }
                        }

                        if (elementType.equals("img")) {
                            elementSrc = element.getAttribute("src") != null ? element.getAttribute("src").replace("&","&amp;") : "";
                            elementAltText = element.getAttribute("alt");
                            if (formatted) {
                                outputDescription = CreateImageReadActionsXmlTestSteps(elementXPath, elementSrc, elementAltText, "FALSE");
                            } else {
                                outputDescription += " - Element Src: " + elementSrc;
                            }
                        } else if (elementType.equals("a")) {
                            //elementHref = element.getAttribute("href");
                            //elementHref = java.net.URLEncoder.encode(element.getAttribute("href"), StandardCharsets.UTF_8.name());
                            elementHref = element.getAttribute("href") != null ? element.getAttribute("href").replace("&","&amp;") : "";
                            if (formatted && !elementHref.isEmpty()) {  //make sure that this is not an anchor
                                outputDescription += outputDescription.contains(">assert<") ? "\r\n" : "";
                                outputDescription += CreateAHrefReadActionXmlTestStep(elementXPath, elementHref, "FALSE");
                            } else if (!elementHref.isEmpty()) {
                                outputDescription += " - Element Href: " + elementHref;
                            } else {
                                outputDescription = "\t<!--  The following element is an Anchor, not a link. -->\r\n" + outputDescription;
                            }
                        } else if (elementType.equals("input")) {
                            inputType = element.getAttribute("type");
                            if (formatted) {
                                if (inputType.equals("text")) {
                                    outputDescription = CreateSendKeysWriteActionXmlTestStep(elementXPath, "[keys to send]", "FALSE");
                                } else if (inputType.equals("button") || inputType.equals("checkbox") || inputType.equals("radio")) {
                                    outputDescription = CreateClickWriteActionXmlTestStep(elementXPath, AppCommands.Click, "FALSE");
                                }
                            }
                        } else if (elementType.equals("select") && formatted) {
                            outputDescription = CreateSelectWriteActionXmlTestStep(elementXPath, "[value of option to select]", "FALSE");
                        }
                        if (!formatted) {
                            outputDescription += " Element Visible: " + isVisible;
                        }
                        if (outputDescription != null && !outputDescription.isEmpty()) {
                            testHelper.UpdateTestResults(outputDescription, true);
                            testHelper.WriteToFile(newFileName, outputDescription);
                        }

                    }
                } catch (Exception fex) {
                    if (fex.getMessage().equals("stale element reference: element is not attached to the page document")) {
                        continue;
                    }
                }
            }
            //testHelper.DebugDisplay("Just before Tag Checking!");
            String tagInfo = GetTaggingChecks(ts);
            if (!formatted) {
                outputDescription = "Google Analytics Tags Checking...";
                if (tagInfo != null && tagInfo.length() > 0) {
                    if (tagInfo.indexOf("check ga4 tag") > 0 && tagInfo.indexOf("check gtm tag") > 0)
                        outputDescription = "GA4 and UA Tags detected";
                    else if (tagInfo.indexOf("check ga4 tag") > 0 && tagInfo.indexOf("check gtm tag") < 0) {
                        outputDescription = "GA4 Tags detected";
                    } else if (tagInfo.indexOf("check ga4 tag") < 0 && tagInfo.indexOf("check gtm tag") > 0) {
                        outputDescription = "UA Tags detected";
                    }

                    testHelper.UpdateTestResults(outputDescription, true);
                    testHelper.WriteToFile(newFileName, outputDescription);
                }
            } else if (tagInfo != null && tagInfo.length() > 0) {
                testHelper.WriteToFile(newFileName, tagInfo);
            }

            if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(false));
            }
            testHelper.WriteToFile(newFileName, "");
        } catch (Exception ex) {
            testHelper.UpdateTestResults("Error: " + ex.getMessage(), false);
            testHelper.DebugDisplay("#2 element " + elementXPath);

            /*if (formatted) {
                testHelper.WriteToFile(newFileName, CreateXmlFileStartAndEnd(false));
            }
            testHelper.WriteToFile(newFileName, "");*/
        }
        return newFileName;
    }

    private String GetTaggingChecks(TestStep ts) {
        String returnValue = null;
        String ga4ReturnValue = "";
        String gaReturnValue = "";
        int index = 1;
        String ga4TagCommandTemplateStart, ga4TagCommandTemplateEnd;
        String gaTagCommandTemplateStart, gaTagCommandTemplateEnd;
        String additioanlGA4ExcludeParameters = testCentral.GetSpecificArgumentValue(ts, "exclude additioanl ga4 parameters","=","");
        //get ga4 tag information
        ga4TagCommandTemplateStart = "\t<step>\r\n\t\t<command>check ga4 tag</command>\r\n\t\t<actionType>read</actionType>\r\n" +
                "\t\t<arguments>\r\n";
        ga4TagCommandTemplateEnd = "\t\t</arguments>\r\n\t</step>\r\n";
        //String invalidParameters = "ep.hit_timestamp,_p,cid,sr,sid,gcs,ir,uaa,uab,uafv1,ouamb,uap,uapv,uaw,_eu,_s,sct,seg,ep.user_agent,up.jmsa_id";
        String invalidParameters = "ep.hit_timestamp,_p,cid,sr,sid,gcs,ir,uaa,uab,uafv1,ouamb,uap,uapv,uaw,_eu,_s,sct,seg,ep.user_agent,up.ga_client_id" ;
        if (!testHelper.IsNullOrEmpty(additioanlGA4ExcludeParameters) && additioanlGA4ExcludeParameters.length() > 0) {
            invalidParameters += "," + additioanlGA4ExcludeParameters;
        }
        String [] invalidParamsList = invalidParameters.split(",");
        Boolean isInvalid = false;

        try {
            if (readCommands != null && readCommands.GA4TagList != null && readCommands.GA4TagList.size() > 0) {
                //testHelper.DebugDisplay("In GetTaggingChecks() in GA4 If statement.");
                for (GA4Tag gaTag : readCommands.GA4TagList) {
                    index = 1;
                    ga4ReturnValue += ga4TagCommandTemplateStart;
                    for (int x=0; x < gaTag.getGA4Parameters().size(); x++) {
                        isInvalid = false;
                        for (int i = 0; i < invalidParamsList.length; i++) {
                            //if this is not an invalid parameter, add it to the list of parameters to test
                            if (gaTag.getGA4Parameter(x).get_parameterName().trim().equals(invalidParamsList[i].trim())) {
                                isInvalid = true;
                                break;
                            }
                        }
                        if (!isInvalid) {
                            ga4ReturnValue += GetKeyValuePair(gaTag.getGA4Parameter(x).get_parameterName(), gaTag.getGA4Parameter(x).get_parameterValue(), index);
                            index++;
                        }
                    }
                    ga4ReturnValue += ga4TagCommandTemplateEnd;
                }
            }
        } catch (Exception exG4) {
            testHelper.UpdateTestResults("Error " + exG4.getMessage() + " while attempting to create GA4 Tagging Test Steps.", true);
        }

        gaTagCommandTemplateStart = "\t<step>\r\n\t\t<command>check gtm tag</command>\r\n\t\t<actionType>read</actionType>\r\n" +
                "\t\t<arguments>\r\n";
        gaTagCommandTemplateEnd = "\t\t</arguments>\r\n\t</step>\r\n";

        try {
            if (readCommands != null && readCommands.GtmTagList != null && readCommands.GtmTagList.size() > 0) {
                //testHelper.DebugDisplay("In GetTaggingChecks() in UA If statement.");
                for (GtmTag gaTag : readCommands.GtmTagList) {
                    index = 1;

                    if (gaTag.get_eventCategory() != null && gaTag.get_eventAction() != null && gaTag.get_eventLabel() != null) {
                        gaReturnValue += gaTagCommandTemplateStart +
                                GetKeyValuePair("dl", gaTag.get_documentLocation(), index) +
                                GetKeyValuePair("t", gaTag.get_hitType() != null ? gaTag.get_hitType() : "", index++) +
                                GetKeyValuePair("ec", gaTag.get_eventCategory() != null ? gaTag.get_eventCategory() : "", index++) +
                                GetKeyValuePair("ea", gaTag.get_eventAction() != null ? gaTag.get_eventAction() : "", index++) +
                                GetKeyValuePair("el", gaTag.get_eventLabel() != null ? gaTag.get_eventLabel() : "", index++) +
                                GetKeyValuePair("tid", gaTag.get_trackingId() != null ? gaTag.get_trackingId() : "", index++) +
                                GetKeyValuePair("cg1", gaTag.get_contentGroup1() != null ? gaTag.get_contentGroup1() : "", index++) +
                                gaTagCommandTemplateEnd;
                    }

                }
            }
        } catch (Exception exUA) {
            testHelper.UpdateTestResults("Error " + exUA.getMessage() + " while attempting to create UA Tagging Test Steps.", true);
        }
        returnValue = ga4ReturnValue + gaReturnValue;
        if (returnValue != null && returnValue.length() > 0) {
            String saveHarFile = "\t<step>\r\n\t\t<command>save har file</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<crucial>True</crucial>\r\n" +
                    "\t\t<arguments>\r\n" +
                    "\t\t\t<arg1>filename=MyCoolSite-Page-har-test.txt</arg1>\r\n" +
                    "\t\t</arguments>\r\n" +
                    "\t</step>\r\n";
            returnValue = saveHarFile + returnValue;
        }
        return returnValue;
    }

    private String GetKeyValuePair(String key, String value, int index) {
        //testHelper.DebugDisplay("In GetKeyValuePair key = " + key + " value = " + value);
        return "\t\t\t<arg" + index + ">" + key + "=" + value + "</arg" + index + ">\r\n";
    }


    /**************************************************************************
     * Description: This Creates the start and end XML tags that contain the
     *              Test Steps.
     *
     * @param isStart - boolean determining if start or end should be created
     * @return - Start or End XML tags.
     **************************************************************************/
    private String CreateXmlFileStartAndEnd(boolean isStart) {
        String returnValue;

        if (isStart) {
            returnValue = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" +
                    "<testSteps>";
        } else {
            returnValue = "</testSteps>";
        }
        return returnValue;
    }


    /*******************************************************************************************
     * Description: This Creates the Navigation Test Step to navigate to the page to be tested.
     *
     * @param testPage - Page URL where testing is to begin.
     * @param isCrucial - Flag to set the crucial
     * @return - XML Test Step
     *******************************************************************************************/
    String CreateNavigationXmlTestStep(String testPage, String isCrucial) {
        String returnValue = "";

        if (testPage != null && !testPage.isEmpty()) {
            returnValue = "\t<step>\r\n" +
                    "\t\t<!-- Navigate to a page - The driver will go to the page in arg1 and compare that URL with the expected value if provided -->\r\n" +
                    "\t\t<command>navigate</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<!-- Expected value - required only when validating successful navigation. For this command it is optional but suggested. -->\r\n" +
                    "\t\t<expectedValue>" + testPage + "</expectedValue>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<arguments>\r\n" +
                    "\t\t\t<!-- first argument expected by the command - A URL is expected for this command; It is also Required!!! -->\r\n" +
                    "\t\t\t<arg1>url=" + testPage + "</arg1>\r\n" +
                    "\t\t\t<!-- second argument, can be optional. For this command it is the time in milliseconds to wait before the assertion is made. -->\n" +
                    "\t\t\t<arg2>delay=1000</arg2> \n" +
                    "\t\t\t<!-- third argument is optional and is for the window dimensions. Add them like this if desired. (w=800 h=800) -->\n" +
                    "\t\t\t<arg3></arg3> \n" +
                    "\t\t</arguments>\n" +
                    "\t</step>";
        }
        testHelper.UpdateTestResults(returnValue, true);

        return returnValue;
    }


    /***************************************************************************
     * Description: This method Creates the ScreenShot test step.
     * @param isCrucial - True or false string value indicating whether the
     *                  step is marked as crucial
     * @return - returns a ScreenShot XML Test Step
     **************************************************************************/
    private String CreateScreenShotTestStep(String isCrucial) {
        String returnValue = "";

        if (TestCentral.testPage != null && !TestCentral.testPage.isEmpty()) {
            returnValue = "\t<step>\n" +
                    "\t\t<command>screenshot</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t</step>";
        }

        testHelper.UpdateTestResults(returnValue, true);
        return returnValue;
    }


    /**************************************************************************
     * Description: This Creates a Test Step that Selects an Option from
     *              a Select list.
     * @param elementXPath - xPath for the element
     * @param selectedItem - value to select
     * @param isCrucial - Flag to set the crucial
     * @return - SendKeys XML Test Step
     ************************************************************************ */
    private String CreateSelectWriteActionXmlTestStep(String elementXPath, String selectedItem, String isCrucial) {
        String returnValue = "";

        if (elementXPath != null && !elementXPath.isEmpty()) {
            returnValue = "\t<step>\r\n" +
                    "\t\t<!-- multiple keystroke command... SENDKEYS! -->\r\n" +
                    "\t\t<command>sendkeys</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<!-- the accessor is the target element where the key strokes will be sent to -->\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<arguments>\r\n" +
                    "\t\t\t<!-- We can have as many key strokes as we want. We will use the arg tags for storing each character/command. The order or writing would be sequetial: arg1 > arg2 > arg3 > etc. If you want to send a string, just enter the whole string in one arg tag-->\r\n" +
                    "\t\t\t<arg1>" + selectedItem + "</arg1>\r\n" +
                    "\t\t\t<!-- Last argument is the override time delay between sending keystrokes and is not required -->\r\n" +
                    "\t\t\t<arg4>delay=500</arg4>\r\n" +
                    "\t\t</arguments>\r\n" +
                    "\t</step>";
        }
        return returnValue;
    }


    /*************************************************************************************
     * Description: This Creates a Test Step that Clicks an element.
     *
     * @param elementXPath - xPath for the element
     * @param clickCommand - Command to perform.
     * @param isCrucial - Flag to set the crucial
     * @return - Click XML Test Step
     *************************************************************************************/
    private String CreateClickWriteActionXmlTestStep(String elementXPath, String clickCommand, String isCrucial) {
        String returnValue = "";
        if (elementXPath != null && !elementXPath.isEmpty()) {
            returnValue = "\t<step>\n" +
                    "\t\t<!-- Click command... clicks on the element based on the accessor -->\r\n" +
                    "\t\t<command>" + clickCommand + "</command>\r\n" +
                    "\t\t<actionType>write</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<!-- the accessor is the target element where the key strokes will be sent -->\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t</step>";
        }
        return returnValue;
    }


    /*************************************************************************************
     * Description: This Creates a Test Step that Sends key strokes to an input type element.
     *
     * @param elementXPath - xPath for the element
     * @param argumentString - Command to perform.
     * @param isCrucial - Flag to set the crucial
     * @return - SendKeys XML Test Step
     *************************************************************************************/
    private String CreateSendKeysWriteActionXmlTestStep(String elementXPath, String argumentString, String isCrucial) {
        String returnValue = "";

        if (elementXPath != null && !elementXPath.isEmpty()) {
            returnValue = "\t<step>\n" +
                    "\t\t<!-- multiple keystroke command... SENDKEYS! -->\n" +
                    "\t\t<command>sendkeys</command>\n" +
                    "\t\t<actionType>write</actionType>\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\n" +
                    "\t\t<!-- the accessor is the target element where the key strokes will be sent to -->\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\n" +
                    "\t\t<accessorType>xPath</accessorType>\n" +
                    "\t\t<arguments>\n" +
                    "\t\t\t<!-- Send as many key strokes as needed. Each arg tag can store a key stroke. Arguments sent in sequetial order: arg1 > arg2 > arg3 > etc. -->\r\n" +
                    "\t\t\t<!-- When sending a string, just enter the whole string in one arg tag (arg1) -->\n" +
                    "\t\t\t<arg1>" + argumentString + "</arg1>\n" +
                    "\t\t</arguments>\n" +
                    "\t</step>";
        }
        return returnValue;
    }


    /*************************************************************************************
     * Description: This Creates a Test Step that Reads the href value of an anchor element.
     *
     * @param elementXPath - xPath for the element
     * @param elementHref - expected value of the anchor's href attribute
     * @param isCrucial  - Flag to set the crucial
     * @return - Href read action XML Test Step
     *************************************************************************************/
    private String CreateAHrefReadActionXmlTestStep(String elementXPath, String elementHref, String isCrucial) {
        String returnValue = "";
        if (elementXPath != null && !elementXPath.isEmpty() && elementHref != null && !elementHref.isEmpty()) {
            returnValue = "\t<step>\r\n" +
                    "\t\t<!-- Compares the href of the anchor link element using the accessor against the expectedValue -->\r\n" +
                    "\t\t<!--<command>CHECK A HREF</command>-->\r\n" +
                    "\t\t<command>CHECK A HREF</command>\r\n" +
                    "\t\t<actionType>read</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial +"</crucial>\r\n" +
                    "\t\t<!-- the accessor is the anchor whose href attribute will be checked against the expectedValue -->\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<expectedValue>" + elementHref + "</expectedValue>\r\n" +
                    "\t</step>";
        }
        return returnValue;
    }


    /*************************************************************************************
     * Description: This Creates up to two Test Steps if the image's src and alt
     *              attributes are present, but if not, creates one based on what is provided.
     *              The first Test Step Reads the src value of an image element.
     *              The second Test Step Reads the alt value of the image element.
     *
     * @param elementXPath  - xPath for the element
     * @param elementSrc - expected value of the image's src attribute
     * @param elementAltText - expected value of the image's alt attribute
     * @param isCrucial  - Flag to set the crucial
     * @return - Image Src and Alt XML Test Steps
     *************************************************************************************/
    private String CreateImageReadActionsXmlTestSteps(String elementXPath, String elementSrc, String elementAltText, String isCrucial) {
        String returnValue = "";

        if (elementSrc.indexOf("&") > -1) {
            if (elementSrc.indexOf("&amp;") < 0) {
                elementSrc = elementSrc.replace("&", "&amp;");
            }
        }

        if (elementSrc != null && !elementSrc.isEmpty()) {
            returnValue = "\t<step>\r\n" +
                    "\t\t<command>check image src</command>\r\n" +
                    "\t\t<actionType>read</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<expectedValue>" + elementSrc + "</expectedValue>\r\n" +
                    "\t</step>";
        }
        testHelper.DebugDisplay("...new line added before <step>");
        if (elementAltText != null && !elementAltText.isEmpty()) {
            returnValue += "\r\n\t<step>\r\n" +
                    "\t\t<command>check image alt</command>\r\n" +
                    "\t\t<actionType>read</actionType>\r\n" +
                    "\t\t<crucial>" + isCrucial + "</crucial>\r\n" +
                    "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                    "\t\t<accessorType>xPath</accessorType>\r\n" +
                    "\t\t<expectedValue>" + elementAltText + "</expectedValue>\r\n" +
                    "\t</step>";
        }

        return returnValue;
    }


    /**********************************************************************************
     * Description: Helper method for the Create test method that creates the assert
     *              test step for formatted tests.
     *
     * @param elementXPath - xPath for the element
     * @param elementText - expected value of the page element
     * @param isCrucial - Flag to set the crucial
     * @return - Assert Command XML Test Step
     **********************************************************************************/
    private String CreateReadActionXmlTestStep(String elementXPath, String elementText, String isCrucial) {

        if (elementText.contains("<")) {
            elementText = "<![CDATA[ " + elementText.trim() + " ]]>";
        }

        return "\t<step>\r\n" +
                "\t\t<command>assert</command>\r\n" +
                "\t\t<actionType>read</actionType>\r\n" +
                "\t\t<expectedValue>" + elementText + "</expectedValue>\r\n" +
                "\t\t<crucial>"+ isCrucial + "</crucial>\r\n" +
                "\t\t<accessor>" + elementXPath + "</accessor>\r\n" +
                "\t\t<accessorType>xPath</accessorType>\r\n" +
                "\t</step>";
    }
    //endregion

}
