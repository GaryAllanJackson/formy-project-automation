import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import io.restassured.RestAssured;
import org.bson.BSONObject;
import org.bson.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.*;

public class ReadCommands {

    final TestCentral testCentral;  // = new TestCentral();
    TestHelper testHelper; // = new TestHelper();
    WriteCommands writeCommands;  // = new WriteCommands();
    WebDriver driver;
    private String _testPage;
    public List<GtmTag> GtmTagList;
    public List<GA4Tag> GA4TagList;
    private String _testFileName;
    void set_testFileName(String _testFileName) {
        this._testFileName = _testFileName;
    }
    String get_testFileName() {return _testFileName; }
    private Boolean _showAdditionalGa4Parameters;
    void set_showAdditionalGa4Parameters(Boolean _showAdditionalGa4Parameters) {this._showAdditionalGa4Parameters = _showAdditionalGa4Parameters;}
    Boolean get_showAdditionalGa4Parameters() {return _showAdditionalGa4Parameters;}

    /*****************************************************************
     * Description: This is the constructor where testCentral and
     *              writeCommands objects are set using the
     *              testCentral object so that they are referencing
     *              the same instance of the objects.
     * @param testCentral - TestCentral object so that the reference
     *                    is the same.
     *****************************************************************/
    public ReadCommands(TestCentral testCentral) {
        this.testCentral = testCentral;
        writeCommands =  testCentral.writeCommands;  //new WriteCommands(testCentral);
        testHelper = testCentral.testHelper;
        set_showAdditionalGa4Parameters(testCentral.get_showAdditionalGA4Parameters());
        if (writeCommands == null) {
            writeCommands =  testCentral.writeCommands != null ? testCentral.writeCommands : new WriteCommands(testCentral);;
        }
        if (testHelper == null) {
            testHelper = new TestHelper(testCentral);
            testHelper.set_executedFromMain(testCentral.is_executedFromMain());
            testHelper.set_csvFileName(testCentral.get_csvFileName());
            testHelper.set_testFileName(testCentral.get_testFileName());
            testHelper.set_showAdditionalGA4Parameters(testCentral.get_showAdditionalGA4Parameters());
            //testHelper.DebugDisplay("ReadCommands - testHelper.is_executedFromMain() = " + testHelper.is_executedFromMain() );
        }
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void set_testPage(String _testPage) {
        this._testPage = _testPage;
    }

    public String get_testPage() {
        return this._testPage;
    }


    /*****************************************************************
     * Description: This method performs all Read related actions.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************/
    void PerformReadActions(TestStep ts, String fileStepIndex) throws Exception {
        if (ts.get_accessorType() != null && !ts.get_accessorType().toLowerCase().equals("n/a")) {
            //add different types of element checks here like img src, img alt, a href
            if (ts.get_command().toLowerCase().contains(AppCommands.CheckImage) || ts.get_command().toLowerCase().contains(AppCommands.Check_Image) ) {
                CheckImageSrcAlt(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.CheckAHref) || ts.get_command().toLowerCase().contains(AppCommands.Check_A_Href)) {
                CheckAnchorHref(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.PersistString) || ts.get_command().toLowerCase().equals(AppCommands.Persist_String)) {
                PersistValueController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_JSON)) {
                JsonController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_XML)) {
                XmlController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.ParseAndCalculateDouble)) {
                ParseAndCalculateDoubleController(ts, fileStepIndex);
            }  else if (ts.get_command().toLowerCase().equals(AppCommands.ParseAndCalculateLong)) {
                ParseAndCalculateLongController(ts, fileStepIndex);
            } else {
                CheckElementText(ts, fileStepIndex);
            }
        } else {
            if (ts.get_command().toLowerCase().contains(AppCommands.Check) && (ts.get_command().toLowerCase().contains(AppCommands.Post) ||
                    ts.get_command().toLowerCase().contains(AppCommands.Get))) {
                //refactored and moved to separate method
                CheckGetPostStatus(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Links)) {
                String url = testCentral.GetArgumentValue(ts, 0, get_testPage());
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking page links for " + url, false);
                CheckBrokenLinks(ts, url, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Image))  {
                String url = testCentral.GetArgumentValue(ts, 0, null);
                if (ts.get_command().toLowerCase().contains(AppCommands.Alt)) {
                    CheckADAImages(ts, url, AppCommands.Alt, fileStepIndex);
                } else if (ts.get_command().toLowerCase().contains(AppCommands.Src)) {
                    CheckADAImages(ts, url, AppCommands.Src, fileStepIndex);
                }
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Count)) {
                CheckElementCountController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Check) && ts.get_command().toLowerCase().contains(AppCommands.Contrast)) {
                ColorContrastController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Query)) {
                //perform a database query
                DatabaseQueryController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Find)) {
                FindPhraseController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Get_JSON) || ts.get_command().toLowerCase().equals(AppCommands.Save_JSON) ) {
                JsonController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.Get_XML) || ts.get_command().toLowerCase().equals(AppCommands.Save_XML) ) {
                XmlController(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.CheckJavaScriptValue)) {
                CheckJavaScriptReturnValue(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.CheckGtmTag)) {
                //testHelper.DebugDisplay("Call to CheckGtmTagValues()");
                CheckGtmTagValues(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.CheckGtmGa4Tag)) {
                CheckGA4TagValues(ts, fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.SaveConsoleLog)) {
                testCentral.WriteLogContent(ts,fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.CombineConsoleLogs)) {
                testCentral.CombineLogContent(ts,fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.SpyderSite) || ts.get_command().toLowerCase().equals(AppCommands.SpiderSite)) {
                testCentral.SpiderSite(ts,fileStepIndex);
            } else if (ts.get_command().toLowerCase().equals(AppCommands.GetAllCookies)) {
                //testCentral.GetAllCookies(ts,fileStepIndex);
                GetAllCookies(ts,fileStepIndex);
            }
        }
    }


    /*****************************************************************
     * Description: This method checks a UA Tag against UA Tag values
     *              retrieved from the HAR file.
     *              This method is written with a specific set of
     *              parameter names that must match.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************/
    private void CheckGtmTagValues(TestStep ts, String fileStepIndex) {
        //testHelper.DebugDisplay("in CheckGtmTagValues()");
        if (GtmTagList != null && GtmTagList.size() > 0) {
            //testHelper.DebugDisplay("in CheckGtmTagValues() if statement");
            testHelper.set_csvFileName(testCentral.testHelper.get_csvFileName());
            testHelper.set_testFileName(testCentral.get_testFileName());
            testHelper.UpdateTestResults(AppConstants.indent5 + "Checking GTM Tag for step " + fileStepIndex, true);
            GtmTag item = new GtmTag();
            GtmTag listItem = new GtmTag();
            boolean doesMatch = false;
            int index = -1;
            String beginsWith = "=";
            //retrieve the values from the test step
            item = testCentral.GetGtmArguments(ts, null);
            for (int x=0;x< GtmTagList.size();x++) {
                listItem = GtmTagList.get(x);
                /*testHelper.DebugDisplay("dl=" + listItem.get_documentLocation() + " t=" + listItem.get_hitType() +
                        " ec=" + listItem.get_eventCategory() + " ea=" + listItem.get_eventAction() +
                        " el=" + listItem.get_eventLabel() + " cg1=" + listItem.get_contentGroup1());*/

                if (!testHelper.IsNullOrEmpty(listItem.get_documentLocation()) && !testHelper.IsNullOrEmpty(listItem.get_hitType()) &&
                        !testHelper.IsNullOrEmpty(listItem.get_eventCategory()) && !testHelper.IsNullOrEmpty(listItem.get_eventAction()) &&
                                !testHelper.IsNullOrEmpty(listItem.get_eventLabel()) && !testHelper.IsNullOrEmpty(listItem.get_contentGroup1())) {
                    if (listItem.get_documentLocation().equals(item.get_documentLocation()) && listItem.get_hitType().equals(item.get_hitType()) &&
                            listItem.get_eventCategory().equals(item.get_eventCategory()) && listItem.get_eventAction().equals(item.get_eventAction()) &&
                            listItem.get_eventLabel().equals(item.get_eventLabel()) && listItem.get_contentGroup1().equals(item.get_contentGroup1())) {
                        //testHelper.DebugDisplay("item.get_contentGroup2() = " + item.get_contentGroup2());  //debugging
                        doesMatch = CheckOptionalGtmValues(item, listItem);

                        index = x;
                        break;
                    } else if (listItem.get_documentLocation().equals(item.get_documentLocation())) {
                        //testHelper.DebugDisplay("x = " + x + " index = " + index + " listItem.get_documentLocation() = " + listItem.get_documentLocation() + "\r\n item.get_documentLocation() = " + item.get_documentLocation());
                        if ((listItem.get_hitType().equals(item.get_hitType()) && listItem.get_eventCategory().equals(item.get_eventCategory()))
                                && (RemoveInvalidCharacters(listItem.get_eventAction()).equals(RemoveInvalidCharacters(item.get_eventAction())) ||
                                RemoveInvalidCharacters(listItem.get_eventLabel()).equals(RemoveInvalidCharacters(item.get_eventLabel())))) {
                            //don't break here, let it keep searching for an exact match and fall out of the loop
                            if (RemoveInvalidCharacters(listItem.get_eventAction()).equals(RemoveInvalidCharacters(item.get_eventAction()))) {
                                index = x;
                            } else {
                                index = index == -1 ? x : index;
                            }
                        }
                    }
                }
            }
            if (index > -1 && doesMatch) {
                //testHelper.UpdateTestResults("The Save Har File command must first be used before using this command!", true);
                testHelper.UpdateTestResults(AppConstants.indent5 + "Successful GTM Tag found matching specified criteria: \r\n" +
                        AppConstants.indent8 + "Expected: (dl=" + item.get_documentLocation() + ") Actual: (dl=" + listItem.get_documentLocation() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (t=" + item.get_hitType() + ")  Actual: (t=" + listItem.get_hitType() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (ec=" + item.get_eventCategory() + ") Actual: (ec=" + listItem.get_eventCategory() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (ea=" + item.get_eventAction() + ") Actual: (ea=" + listItem.get_eventAction() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (el=" + item.get_eventLabel() + ") Actual: (el=" + listItem.get_eventLabel() + ")\r\n" +
                        (!testHelper.IsNullOrEmpty(item.get_contentGroup2()) ? AppConstants.indent8 + "Expected: (cg2" + beginsWith + item.get_contentGroup2() + ") Actual: (cg2" + beginsWith + listItem.get_contentGroup2() + ")\r\n" : "") +
                        (!testHelper.IsNullOrEmpty(item.get_documentTitle()) ? AppConstants.indent8 + "Expected: (dt" + beginsWith + item.get_documentTitle() + ") Actual: (dt" + beginsWith + listItem.get_documentTitle() + ")\r\n" : "") +
                        (!testHelper.IsNullOrEmpty(item.get_trackingId()) ? AppConstants.indent8 + "Expected: (tid=" + item.get_trackingId() + ") Actual: (tid=" + listItem.get_trackingId() + ")\r\n" : "") +
                        AppConstants.indent5  + " for step " + fileStepIndex, true);
            } else if (index > -1 && !doesMatch) {
                listItem = GtmTagList.get(index);
                testHelper.UpdateTestResults("Failed No GTM Tag found matching specified (optional) criteria: \r\n" +
                        AppConstants.indent8 + (item.get_documentLocation().equals(listItem.get_documentLocation()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (dl=" + item.get_documentLocation() + ") Actual: (dl=" + listItem.get_documentLocation() + ")\r\n" +
                        AppConstants.indent8 + (item.get_hitType().equals(listItem.get_hitType()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (t=" + item.get_hitType() + ")  Actual: (t=" + listItem.get_hitType() + ")\r\n" +
                        AppConstants.indent8 + (item.get_eventCategory().equals(listItem.get_eventCategory()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (ec=" + item.get_eventCategory() + ") Actual: (ec=" + listItem.get_eventCategory() + ")\r\n" +
                        AppConstants.indent8 + (item.get_eventAction().equals(listItem.get_eventAction()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (ea=" + item.get_eventAction() + ") Actual: (ea=" + listItem.get_eventAction() + ")\r\n" +
                        AppConstants.indent8 + (item.get_eventLabel().equals(listItem.get_eventLabel()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (el=" + listItem.get_eventLabel() + ") Actual: (el=" + listItem.get_eventLabel() + ")\r\n" +
                        //(!testHelper.IsNullOrEmpty(item.get_contentGroup2()) ? AppConstants.indent8 + (item.get_contentGroup2().equals(listItem.get_contentGroup2()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (cg2" + beginsWith + item.get_contentGroup2() + ") Actual: (cg2" + beginsWith + listItem.get_contentGroup2() + ")\r\n" : "") +
                        (!testHelper.IsNullOrEmpty(item.get_contentGroup2()) ? AppConstants.indent8 + (item.get_contentGroup2().equals(listItem.get_contentGroup2()) || listItem.get_contentGroup2().indexOf(item.get_contentGroup2().replace("+","")) > -1 ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (cg2" + beginsWith + item.get_contentGroup2() + ") Actual: (cg2" + beginsWith + listItem.get_contentGroup2() + ")\r\n" : "") +
                        (!testHelper.IsNullOrEmpty(item.get_documentTitle()) ? AppConstants.indent8 + (item.get_documentTitle().equals(listItem.get_documentTitle()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (dt" + beginsWith + item.get_documentTitle() + ") Actual: (dt" + beginsWith + listItem.get_documentTitle() + ")\r\n" : "") +
                        //(!testHelper.IsNullOrEmpty(item.get_documentTitle()) ? AppConstants.indent8 + (listItem.get_documentTitle().indexOf(item.get_documentTitle().replace("+","")) > -1 ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (dt" + beginsWith + item.get_documentTitle() + ") Actual: (dt" + beginsWith + listItem.get_documentTitle() + ")\r\n" : "") +
                        (!testHelper.IsNullOrEmpty(item.get_trackingId()) ? AppConstants.indent8 + (item.get_trackingId().equals(listItem.get_trackingId()) ? AppConstants.ANSI_GREEN : AppConstants.ANSI_RED) + "Expected: (tid=" + item.get_trackingId() + ") Actual: (tid=" + listItem.get_trackingId() + ")\r\n" : "") +
                        AppConstants.indent5  + " for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults("Failed No GTM Tag found matching specified criteria: \r\n" +
                        AppConstants.indent8 + "Expected: (dl=" + item.get_documentLocation() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (t=" + item.get_hitType() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (ec=" + item.get_eventCategory() + ")\n" +
                        AppConstants.indent8 + "Expected: (ea=" + item.get_eventAction() + ")\n" +
                        AppConstants.indent8 + "Expected: (el=" + item.get_eventLabel() + ")\n" +
                        (!testHelper.IsNullOrEmpty(item.get_contentGroup2()) ? AppConstants.indent8 + "Expected: (cg2" + beginsWith + item.get_contentGroup2() + ")\r\n" : "") +
                        (!testHelper.IsNullOrEmpty(item.get_documentTitle()) ? AppConstants.indent8 + "Expected: (dt" + beginsWith + item.get_documentTitle() + ")\r\n" : "") +
                        (!testHelper.IsNullOrEmpty(item.get_trackingId()) ? AppConstants.indent8 + "Expected: (tid=" + item.get_trackingId() + ")\r\n" : "") +
                        AppConstants.indent5 + " for step " + fileStepIndex, true);
            }
        } else {
            testHelper.UpdateTestResults("The Save Har File command must first be used before using this command!", true);
        }
    }


    /*****************************************************************
     * Description: This method checks a GA4 Tag against GA4 Tag values
     *              retrieved from the HAR file.
     *              This method is written so that parameter name does
     *              not have to be limited to just a few parameters
     *              but rather can be any parameter that can be retrieved
     *              from the tag.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************/
    private void CheckGA4TagValues(TestStep ts, String fileStepIndex) {
        if (GA4TagList != null && GA4TagList.size() > 0) {
            testHelper.set_csvFileName(testCentral.testHelper.get_csvFileName());
            testHelper.set_testFileName(testCentral.get_testFileName());
            testHelper.UpdateTestResults(AppConstants.indent5 + "Checking GA4 Tag for step " + fileStepIndex, true);
            GA4Tag tsItem = new GA4Tag();
            String tsName;
            String tsValue;
            String additionalMessage = "";
            int maxAdditionalItemsPerLine = 4;
            int additionalItemsPerLine = 0;
            int selectedIndex = 0;

            String testMessage = "";
            String testParams = "";

            //retrieve the values from the test step
            tsItem = testCentral.GetGa4Arguments(ts, null);

            for (int tsIndex = 0; tsIndex < tsItem.getGA4Parameters().size(); tsIndex++) {
                tsName = tsItem.getGA4Parameter(tsIndex).get_parameterName();
                tsValue = tsItem.getGA4Parameter(tsIndex).get_parameterValue();
                testParams += AppConstants.indent8 + "(" + tsName + "=" + tsValue + ")\r\n";
            }
            GA4Tag ga4Tag = GetMatchingGA4Tag(tsItem);
            if (ga4Tag != null) {
                testMessage = AppConstants.indent5 + "Successful GA4 GTM Tag found matching specified criteria: \r\n";
                for (int tsIndex = 0; tsIndex < tsItem.getGA4Parameters().size(); tsIndex++) {
                    tsName = tsItem.getGA4Parameter(tsIndex).get_parameterName();
                    tsValue = tsItem.getGA4Parameter(tsIndex).get_parameterValue();
                    for (int tagIndex=0;tagIndex<ga4Tag.getGA4Parameters().size();tagIndex++) {
                        if (tsName.equals(ga4Tag.getGA4Parameter(tagIndex).get_parameterName())) {
                            testMessage += AppConstants.indent8 + "Expected:(" + tsName + "=" + tsValue + ") Actual: (" + ga4Tag.getGA4Parameter(tagIndex).get_parameterName() +
                                    "=" + ga4Tag.getGA4Parameter(tagIndex).get_parameterValue() + ")\r\n";
                            selectedIndex = tagIndex;
                            //additionalMessage.replace("(" + tsName + "=" + tsValue + ")","");
                        } else {
                            additionalMessage = additionalMessage.length() <= 0 ? AppConstants.indent8 + "Additional: " + additionalMessage : additionalMessage;
                            if (additionalItemsPerLine == maxAdditionalItemsPerLine) {
                                additionalMessage += "\r\n";
                                additionalItemsPerLine = 0;
                            }
                            if (additionalMessage.indexOf(" - (" + ga4Tag.getGA4Parameter(tagIndex).get_parameterName() + "=" + ga4Tag.getGA4Parameter(tagIndex).get_parameterValue() + ")") < 0) {
                                additionalMessage += AppConstants.indent8 + " - (" + ga4Tag.getGA4Parameter(tagIndex).get_parameterName() + "=" + ga4Tag.getGA4Parameter(tagIndex).get_parameterValue() + ")\t";
                                additionalItemsPerLine++;
                            }
                            //additionalMessage.replace("(" + tsName + "=" + tsValue + ")","");
                        }
                    }
                }
                for (int tsIndex = 0; tsIndex < tsItem.getGA4Parameters().size(); tsIndex++) {
                    tsName = tsItem.getGA4Parameter(tsIndex).get_parameterName();
                    tsValue = tsItem.getGA4Parameter(tsIndex).get_parameterValue();
                    additionalMessage.replace(" - (" + tsName + "=" + tsValue + ")\t","");
                    additionalMessage.replace("\r\n\t","\r\n");
                }
                if (this._showAdditionalGa4Parameters) {
                    testMessage += additionalMessage + AppConstants.indent5 + " for step " + fileStepIndex;
                } else {
                    testMessage += AppConstants.indent5 + " for step " + fileStepIndex;
                }
                testHelper.UpdateTestResults(testMessage, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Failed GA4 GTM Tag NOT found matching specified criteria: \r\n" + testParams + AppConstants.indent5 + " for step " + fileStepIndex, true);
            }
        }
    }

    /*****************************************************************
     * Description: This method searches all GA4 Tags, retrieved from
     *              the HAR file, for a GA4 Tag that matches the
     *              names and values of the test step configuration
     *              for that tag or returns null if nothing is found.
     *
     * @param tsItem - GA4 Tag Object containing all configured information
     *           for the GA4 Tag being checked.
     ******************************************************************/
    private GA4Tag GetMatchingGA4Tag(GA4Tag tsItem) {
        GA4Tag listItem = new GA4Tag();
        int tagParamCount=0, tsParamCount = tsItem.getGA4Parameters().size();
        String tsName, tsValue;
        String additionalFields;

        for (int x = 0; x < GA4TagList.size(); x++) {
            listItem = GA4TagList.get(x);
            tagParamCount = 0;
            for (int tsIndex=0;tsIndex<tsItem.getGA4Parameters().size();tsIndex++) {
                tsName = tsItem.getGA4Parameter(tsIndex).get_parameterName();
                tsValue = tsItem.getGA4Parameter(tsIndex).get_parameterValue();
                for (int ga4Index = 0; ga4Index < listItem.getGA4Parameters().size(); ga4Index++) {
                    if (listItem.getGA4Parameters().get(ga4Index).get_parameterName().equals(tsName) &&
                            listItem.getGA4Parameters().get(ga4Index).get_parameterValue().equals(tsValue)) {
                        tagParamCount++;
                        if (tsParamCount == tagParamCount) {
                            return listItem;
                        }
                        break;
                    } else if (listItem.getGA4Parameters().get(ga4Index).get_parameterName().equals(tsName) &&
                            !listItem.getGA4Parameters().get(ga4Index).get_parameterValue().equals(tsValue)) {
                        break;
                    }
                }
            }
        }
        return null;
    }



    // This was the original GA4 Tag Check Routine which relied upon just a few fields being checked
    private void CheckGA4TagValues_old(TestStep ts, String fileStepIndex) {
        if (GA4TagList != null && GA4TagList.size() > 0) {
            testHelper.set_csvFileName(testCentral.testHelper.get_csvFileName());
            testHelper.set_testFileName(testCentral.get_testFileName());
            testHelper.UpdateTestResults(AppConstants.indent5 + "Checking GA4 Tag for step " + fileStepIndex, true);
            GA4Tag item = new GA4Tag();
            GA4Tag listItem = new GA4Tag();
            boolean doesMatch = false;
            int index = -1;
            String beginsWith = "=";
            //retrieve the values from the test step
            item = testCentral.GetGa4Arguments(ts, null);
            for (int x = 0; x < GA4TagList.size(); x++) {
                listItem = GA4TagList.get(x);
                if (!testHelper.IsNullOrEmpty(listItem.get_documentLocation()) && !testHelper.IsNullOrEmpty(listItem.get_documentLocation())) {
                    if (listItem.get_gtmTagName().equals(item.get_gtmTagName()) && listItem.get_eventName().equals(item.get_eventName()) && ((!testHelper.IsNullOrEmpty(listItem.get_productName()) && listItem.get_productName().equals(item.get_productName())) || testHelper.IsNullOrEmpty(listItem.get_productName()))) {
                          // && listItem.get_productName().equals(item.get_productName())) {
                        testHelper.UpdateTestResults("Successful GA4 GTM Tag found matching specified criteria: \r\n" +
                                AppConstants.indent8 + "Expected: (gtm_tag_name=" + item.get_gtmTagName() + ") Actual: (gtm_tag_name=" + listItem.get_gtmTagName() + ")\r\n" +
                                AppConstants.indent8 + "Expected: (event_name=" + item.get_eventName() + ") Actual: (event_name=" + listItem.get_eventName() + ")\r\n" +
                                //AppConstants.indent8 + "Expected: (sitesection=" + item.get_siteSection() + ") Actual: (sitesection=" + listItem.get_siteSection() + ")\r\n" +
                                //AppConstants.indent8 + "Expected: (pagetemplate=" + item.get_pageTemplate() + ") Actual: (pagetemplate=" + listItem.get_pageTemplate() + ")\r\n" +
                                //AppConstants.indent8 + "Expected: (id_field=" + item.get_idField() + ") Actual: (id_field=" + listItem.get_idField() + ")\r\n" +
                                AppConstants.indent8 + "Expected: (dl=" + item.get_documentLocation() + ") Actual: (dl=" + listItem.get_documentLocation() + ")\r\n" +
                                //AppConstants.indent8 + "Expected: (t=" + item.get_hitType() + ")  Actual: (t=" + listItem.get_hitType() + ")\r\n" +
                                AppConstants.indent8 + "Expected: (product_name=" + item.get_productName() + ")  Actual: (product_name=" + listItem.get_productName() + ")\r\n" +
                                AppConstants.indent5 + " for step " + fileStepIndex, true);

                                for (int ga4Index = 0; ga4Index < listItem.getGA4Parameters().size(); ga4Index++) {
                                    testHelper.UpdateTestResults(AppConstants.indent8 + "Parameter Name = " + listItem.getGA4Parameter(ga4Index).get_parameterName() + "\r\n" +
                                            AppConstants.indent8 + "Parameter Value = " + listItem.getGA4Parameter(ga4Index).get_parameterValue() + "\r\n" +
                                            AppConstants.indent5 + " for step " + fileStepIndex, true);
                                }
                                doesMatch = true;
                                break;
                        //need to iterate through the parameters here and list them.
                    }
                }
            }
            //only if it has iterated through the entire list and not found the item do we list the fail message
            if (!doesMatch) {
                testHelper.UpdateTestResults("Failed No GTM Tag found matching specified criteria: \r\n" +
                        AppConstants.indent8 + "Expected: (gtm_tag_name=" + item.get_gtmTagName() + ") Actual: (gtm_tag_name=" + listItem.get_gtmTagName() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (event_name=" + item.get_eventName() + ") Actual: (event_name=" + listItem.get_eventName() + ")\r\n" +
                        //AppConstants.indent8 + "Expected: (sitesection=" + item.get_siteSection() + ") Actual: (sitesection=" + listItem.get_siteSection() + ")\r\n" +
                        //AppConstants.indent8 + "Expected: (pagetemplate=" + item.get_pageTemplate() + ") Actual: (pagetemplate=" + listItem.get_pageTemplate() + ")\r\n" +
                        //AppConstants.indent8 + "Expected: (id_field=" + item.get_idField() + ") Actual: (id_field=" + listItem.get_idField() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (dl=" + item.get_documentLocation() + ") Actual: (dl=" + listItem.get_documentLocation() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (t=" + item.get_hitType() + ")  Actual: (t=" + listItem.get_hitType() + ")\r\n" +
                        AppConstants.indent8 + "Expected: (product_name=" + item.get_productName() + ")  Actual: (product_name=" + listItem.get_productName() + ")\r\n" +
                        AppConstants.indent5 + " for step " + fileStepIndex, true);
            }
        }
    }

    private String RemoveInvalidCharacters(String inputString) {
        //String validCharacters = "[^abcdefghijklmnopqrstuvwxyz -']";
        /*if (inputString.indexOf("cinnamania") > 0) {
            testHelper.DebugDisplay("Before Removal inputString = " + inputString);
        }*/
        inputString = inputString.replace("\t"," ");
        inputString = inputString.replace("&nbsp;"," ");
        //inputString = inputString.replaceAll("[^abcdefghijklmnopqrstuvwxyz -']","");
        inputString = inputString.replaceAll("[^a-z -\'~|]","");

        /*if (inputString.indexOf("cinnamania") > 0) {
            testHelper.DebugDisplay("After Removal inputString = " + inputString);
        }*/

        return inputString;
    }



    private boolean CheckOptionalGtmValues(GtmTag item, GtmTag listItem) {
        String beginsWith = "=";
        boolean doesMatch = true;

        doesMatch = (!testHelper.IsNullOrEmpty(item.get_contentGroup2()) && item.get_contentGroup2().startsWith("+")) ? listItem.get_contentGroup2().startsWith(item.get_contentGroup2().substring(1)) : true;
        if (!doesMatch) {
            return doesMatch;
        }
        doesMatch = (!testHelper.IsNullOrEmpty(item.get_documentTitle()) && item.get_documentTitle().startsWith("+")) ? listItem.get_documentTitle().startsWith(item.get_documentTitle().substring(1)) : true;
        if (!doesMatch) {
            return doesMatch;
        }
        doesMatch = (!testHelper.IsNullOrEmpty(item.get_trackingId()) && item.get_trackingId().equals(listItem.get_trackingId())) ? true : false;
        if (!doesMatch) {
            return doesMatch;
        }
        return doesMatch;
    }


    /*******************************************************************************
     * DESCRIPTION:  Checks an Image Src or Alt attribute
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *******************************************************************************/
    private void CheckImageSrcAlt(TestStep ts, String fileStepIndex) {
        String actualValue="";
        String srcAlt = testCentral.GetArgumentValue(ts, 0, "src");
        testHelper.UpdateTestResults(AppConstants.indent8 + "Checking Image " + srcAlt + " for " + ts.get_expectedValue() + " for step " + fileStepIndex, true);

        actualValue = GetWebElementByAccessor(ts).getAttribute(srcAlt);

        if (ts.get_crucial()) {
            assertEquals(ts.get_expectedValue(), actualValue);
        } else {
            try {
                assertEquals(ts.get_expectedValue(), actualValue);
            } catch (AssertionError ae) {
                //do nothing, this just traps the assertion error so that processing can continue
            }
        }
        if (ts.get_expectedValue().trim().equals(actualValue.trim())) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Successful Image " + srcAlt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
        } else {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed Image " + srcAlt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = false;
        }
    }


    /*******************************************************************************
     * DESCRIPTION: Checks the Anchor href attribute.
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *******************************************************************************/
    private void CheckAnchorHref(TestStep ts, String fileStepIndex) {
        String actualValue="";
        //Not wired for checking text because text is already wired up through the default assert method
        String hrefTxt = "href";
        actualValue = GetWebElementByAccessor(ts).getAttribute(hrefTxt);

        if (ts.get_crucial()) {
            assertEquals(ts.get_expectedValue(), actualValue);
        } else {
            try {
                assertEquals(ts.get_expectedValue(), actualValue);
            } catch (AssertionError ae) {
                //do nothing, this just traps the assertion error so that processing can continue
            }
        }
        if (ts.get_expectedValue().trim().equals(actualValue.trim())) {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Successful Anchor " + hrefTxt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            //testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
            testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || testCentral.conditionalSuccessful;
        } else {
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed Anchor " + hrefTxt + " Check.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ") for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = false;
        }
    }


    /******************************************************************************
     * DESCRIPTION: Control method used to Persist a value.
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     *
     ******************************************************************************/
    void PersistValueController(TestStep ts, String fileStepIndex) throws Exception {
        testCentral.persistedString = null;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Element Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisting value found by: " + ts.get_accessorType() + " accessor: " + ts.get_accessor(), true);
        testCentral.persistedString = PersistValue(ts, ts.get_accessor(), fileStepIndex);
        testHelper.UpdateTestResults(AppConstants.indent8 + "Persisted value = (" + testCentral.persistedString + ")", true);
        testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock() && testCentral.persistedString != null); // ? true : false;
        testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting action, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
    }


    /*************************************************************************
     * Description: Control method used to Retrieve JSON from an API end point,
     *              persist it into a local variable and this method also
     *              Querries the local JSON variable for values.
     * @param ts - Test Step Object containing all related information
     *             for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @throws Exception - May throw exception if JSON retrieval fails.
     *************************************************************************/
    private void JsonController(TestStep ts, String fileStepIndex) throws Exception {
        if (ts.get_command().toLowerCase().equals(AppCommands.Get_JSON)) {
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_PURPLE_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start JSON Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
            testCentral.jsonContent = GetHttpResponse(ts, fileStepIndex);
            if (testCentral.jsonContent != null && !testCentral.jsonContent.isEmpty()) {
                testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Successful JSON content retrieval for step " + fileStepIndex, true);
            } else {
                testCentral.conditionalSuccessful = false;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Failed to retrieve JSON content for step " + fileStepIndex, true);
            }
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_PURPLE_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End JSON Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_JSON)) {
            QueryJSON(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Save_JSON)) {
            SaveJsonToFile(ts, fileStepIndex);
        }
    }


    /*************************************************************************
     * Description: Control method used to Retrieve XML from an API end point,
     *              persist it into a local variable and this method also
     *              Querries the local XML variable for values.
     * @param ts - Test Step Object containing all related information
     *             for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @throws Exception - May throw exception if JSON retrieval fails.
     *************************************************************************/
    private void XmlController(TestStep ts, String fileStepIndex) throws Exception {
        if (ts.get_command().toLowerCase().equals(AppCommands.Get_XML)) {
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_CYAN_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start XML Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
            testCentral.xmlContent = GetHttpResponse(ts, fileStepIndex);
            if (!testHelper.IsNullOrEmpty(testCentral.xmlContent)) {
                testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Successful XML content retrieval for step " + fileStepIndex, true);
            } else {
                testCentral.conditionalSuccessful = false;
                testHelper.UpdateTestResults(AppConstants.indent8 + "Failed to retrieve XML content for step " + fileStepIndex, true);
            }
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_CYAN_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End XML Retrieval and Persistence Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Query_XML)) {
            QueryXML(ts, fileStepIndex);
        } else if (ts.get_command().toLowerCase().equals(AppCommands.Save_XML)) {
            SaveXmlToFile(ts, fileStepIndex);
        }
    }


    /***************************************************************************************
     * Description: This method retrieves the simple calculation from a page element,
     *              parses that calculation text based on the arguments and performs
     *              the intended calculation using double data types and if the expected
     *              value is present, compares the calculated value to the expected
     *              value and reports the success or failure of that comparison.
     * @param ts -  Test Step Object containing all related information
     *              for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************************************/
    private void ParseAndCalculateDoubleController(TestStep ts, String fileStepIndex) {
        int firstIndex, secondIndex, operatorIndex;
        double firstNumber;
        double secondNumber;
        double resultNumber = 0;
        String delimiter, operator;
        String accessorPersist;
        String [] equation;
        delimiter = testCentral.GetArgumentValue(ts, 0, " ");
        firstIndex = testCentral.GetArgumentNumericValue(ts, 1, 0);
        secondIndex= testCentral.GetArgumentNumericValue(ts, 2, 2);
        operatorIndex = testCentral.GetArgumentNumericValue(ts, 3, 1);
        accessorPersist = testCentral.GetArgumentValue(ts, 4, "persist");

        String actual = GetElementText(ts, fileStepIndex);
        if (actual != null && actual.contains(delimiter)) {
            equation = actual.split(delimiter);
            firstNumber = Double.parseDouble(equation[firstIndex]);
            secondNumber = Double.parseDouble(equation[secondIndex]);
            operator = equation[operatorIndex];
            if (operator.equals("+")) {
                resultNumber = firstNumber + secondNumber;
            } else if (operator.equals("-")) {
                resultNumber = firstNumber - secondNumber;
            } else if (operator.equals("*")) {
                resultNumber = firstNumber * secondNumber;
            } else if (operator.equals("/")) {
                resultNumber = firstNumber / secondNumber;
            }
            testHelper.UpdateTestResults("Solving equation: " + firstNumber + " " + operator + " " + secondNumber + " = " + resultNumber + " for step " + fileStepIndex, true);

            if (ts.get_expectedValue() != null) {
                double expected = Double.parseDouble(ts.get_expectedValue());
                if (resultNumber == expected) {
                    testHelper.UpdateTestResults("Successful Parse and Calculate Double Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                } else {
                    testHelper.UpdateTestResults("Failed Parse and Calculate Double Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                }
            }

            if (accessorPersist.toLowerCase().equals("persist")) {
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Calculated Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
                testHelper.UpdateTestResults("Persisting Calculated value: (" + resultNumber +  ")", true);
                testCentral.persistedString = String.valueOf(resultNumber);
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting Calculated Value, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
            } else {
                TestStep testStep = new TestStep();
                testStep.set_command(AppCommands.SendKeys);
                testStep.set_crucial(ts.get_crucial());
                testStep.set_isConditionalBlock(ts.get_isConditionalBlock());
                testStep.set_accessor(accessorPersist);
                testStep.set_actionType("write");
                testStep.set_accessorType(ts.get_accessorType());
                Argument argument = new Argument();
                argument.set_parameter(String.valueOf(resultNumber));
                List<Argument> arguments = new ArrayList<>();
                arguments.add(argument);
                testStep.ArgumentList = arguments;
                //TODO: FIGURE OUT WHERE BEST TO KEEP THIS METHOD AND THEN UPDATE THE CALL BELOW
//                testHelper.DebugDisplay("resultNumber = " + resultNumber);
//                testHelper.DebugDisplay("fileStepIndex = " + fileStepIndex);
                writeCommands.PerformAction(testStep, String.valueOf(resultNumber), fileStepIndex);
            }
        }
    }


    /***************************************************************************************
     * Description: This method retrieves the simple calculation from a page element,
     *              parses that calculation text based on the arguments and performs
     *              the intended calculation using long data types and if the expected
     *              value is present, compares the calculated value to the expected
     *              value and reports the success or failure of that comparison.
     * @param ts -  Test Step Object containing all related information
     *              for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************************************/
    private void ParseAndCalculateLongController(TestStep ts, String fileStepIndex) {
        int firstIndex, secondIndex, operatorIndex;
        long firstNumber;
        long secondNumber;
        long resultNumber = 0;
        String delimiter, operator;
        String accessorPersist;
        //https://timesofindia.indiatimes.com/poll.cms
        String [] equation;
        delimiter = testCentral.GetArgumentValue(ts, 0, " ");
        firstIndex = testCentral.GetArgumentNumericValue(ts, 1, 0);
        secondIndex= testCentral.GetArgumentNumericValue(ts, 2, 2);
        operatorIndex = testCentral.GetArgumentNumericValue(ts, 3, 1);
        accessorPersist = testCentral.GetArgumentValue(ts, 4, "persist");

        String actual = GetElementText(ts, fileStepIndex);
        if (actual != null && actual.contains(delimiter)) {
            equation = actual.split(delimiter);
            firstNumber = !equation[firstIndex].contains(".") ? Long.parseLong(equation[firstIndex]) : Long.parseLong(equation[firstIndex].substring(0, equation[firstIndex].indexOf(".")));
            secondNumber = !equation[secondIndex].contains(".") ? Long.parseLong(equation[secondIndex]) : Long.parseLong(equation[secondIndex].substring(0, equation[secondIndex].indexOf(".")));

            operator = equation[operatorIndex];
            if (operator.equals("+")) {
                resultNumber = firstNumber + secondNumber;
            } else if (operator.equals("-")) {
                resultNumber = firstNumber - secondNumber;
            } else if (operator.equals("*")) {
                resultNumber = firstNumber * secondNumber;
            } else if (operator.equals("/")) {
                resultNumber = firstNumber / secondNumber;
            }
            testHelper.UpdateTestResults("Solving equation: " + firstNumber + " " + operator + " " + secondNumber + " = " + resultNumber + " for step " + fileStepIndex, true);

            if (ts.get_expectedValue() != null) {
                double expected = Double.parseDouble(ts.get_expectedValue());
                if (resultNumber == expected) {
                    testHelper.UpdateTestResults("Successful Parse and Calculate Long Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                } else {
                    testHelper.UpdateTestResults("Failed Parse and Calculate Long Expected: (" + expected + ") Actual: (" + resultNumber + ") for step " + fileStepIndex, true);
                }
            }

            if (accessorPersist.toLowerCase().equals("persist")) {
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ Start Persisting Calculated Value ]", "", AppConstants.ANSI_CYAN, true, false, true);
                testHelper.UpdateTestResults(AppConstants.indent8 + "Persisting Calculated value: (" + resultNumber +  ")", true);
                testCentral.persistedString = String.valueOf(resultNumber);
                testHelper.CreateSectionHeader(AppConstants.indent5 + "[ End Persisting Calculated Value, but value persisted and usable until end of test file ]", "", AppConstants.ANSI_CYAN, false, false, true);
            } else {
                TestStep testStep = new TestStep();
                testStep.set_command(AppCommands.SendKeys);
                testStep.set_crucial(ts.get_crucial());
                testStep.set_isConditionalBlock(ts.get_isConditionalBlock());
                testStep.set_accessor(accessorPersist);
                testStep.set_actionType("write");
                testStep.set_accessorType(ts.get_accessorType());
                Argument argument = new Argument();
                argument.set_parameter(String.valueOf(resultNumber));
                List<Argument> arguments = new ArrayList<>();
                arguments.add(argument);
                testStep.ArgumentList = arguments;
                writeCommands.PerformAction(testStep, String.valueOf(resultNumber), fileStepIndex);
            }
        }
    }


    /*******************************************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the text of the element against the expected value.
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *
     * IMPORTANT NOTE: NOTICED THAT FOR INPUT CONTROLS GETTING TEXT IS NOT WORKING.
     *                 ADDED CHECK IN CHECKELEMENTWITHXXX MEHTODS AND IF TEXT IS NULL
     *                 FOR INPUT TYPE=TEXT RETURN THE VALUE ATTRIBUTE INSTEAD.
     ****************************************************************************************/
    private void CheckElementText(TestStep ts, String fileStepIndex) throws Exception {
        String actual = "";
        boolean notEqual = false;
        final String elementTypeCheckedAtStep = "Element type being checked at step ";
        String expected = ts.get_expectedValue();

        testHelper.UpdateTestResults(AppConstants.indent5 + elementTypeCheckedAtStep + fileStepIndex + " by " + ts.get_accessorType() + " " + ts.get_accessor(), true);
        actual = CheckElementWithAccessor(ts, fileStepIndex);
        //testHelper.DebugDisplay("testHelper.is_executedFromMain() = " + testHelper.is_executedFromMain());

        String arg = testCentral.GetArgumentValue(ts, 0, null);
        if (arg != null && arg.equals("!=")) {
            notEqual = true;
        }

        if (ts.get_expectedValue() != null && ts.get_expectedValue().toLowerCase().contains(AppConstants.persistedStringCheckValue)) {
            if (testCentral.persistedString != null) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Grabbing " + AppConstants.ANSI_CYAN + "persisted" + AppConstants.ANSI_RESET + " value: (" + testCentral.persistedString + ") for comparison.", true);
                expected = testCentral.persistedString;
            } else {
                testHelper.UpdateTestResults("", false);
                testHelper.CreateSectionHeader(AppConstants.indent5 +"[ Start of Persistence Usage Error ]", "", AppConstants.ANSI_RED, true, false, true);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "ERROR: No value previously persisted!!! " + AppConstants.ANSI_RESET + "Using empty string () instead of null for comparison.", true);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "IMPORTANT:" + AppConstants.ANSI_RESET + " A value must first be persisted before that persisted value can be used for comparison.", false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_RED + "NOTE:" + AppConstants.ANSI_RESET + " Values persisted in one test file are reset before the start of the next test file.", false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.indent5 + "Any values you want persisted for comparison, must first be persisted in the test file performing the comparison!!!",false);
                testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.indent5 + "Refer to the help file for more information regarding persisting and comparing persisted values.", false);
                testHelper.CreateSectionHeader(AppConstants.indent5 +"[ End of Persistence Usage Error ]", "", AppConstants.ANSI_RED, false, false, true);
                expected = "";
            }
        }

        if (ts.get_expectedValue() != null && ts.get_expectedValue().contains("<")) {
            expected = expected.replace("<![CDATA[ ", "").replace(" ]]>","").trim();
            expected = expected.substring(expected.indexOf("<"), expected.lastIndexOf(">"));
            if (actual.contains("<")) {
                actual = actual.substring(actual.indexOf("<"), actual.lastIndexOf(">"));
            }
        }

        //if one value is missing and the other is null, make them equivalent
        if ((expected.isEmpty() && actual == null) || (expected == null && actual.isEmpty()) ) {
            expected = "";
            actual = "";
        }

        if (ts.get_crucial()) {
            if (!notEqual) {
                assertEquals(expected, actual);
            } else {
                assertFalse(expected.equals(actual));
            }
        } else {
            try {
                if (!notEqual) {
                    assertEquals(expected, actual);
                } else {
                    assertFalse(expected.equals(actual));
                }
            } catch (AssertionError ae) {
                // do not capture screen shot here, if element not found, check methods will capture screen shot
            }
        }
        if (expected.equals(actual) && !notEqual) {
            testHelper.UpdateTestResults("Successful equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex,  true);
            testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
        } else if (!expected.equals(actual) && notEqual) {
            testHelper.UpdateTestResults("Successful NOT equal (!=) comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
        } else if (!expected.equals(actual) && !notEqual) {
            testHelper.UpdateTestResults("Failed equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = false;
            if (testCentral.screenShotSaveFolder != null && !testCentral.screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, testCentral.GetBrowserUsed() + fileStepIndex + "Assert_Fail", testCentral.screenShotSaveFolder, false, fileStepIndex);
            }
        } else if (expected.equals(actual) && notEqual) {
            testHelper.UpdateTestResults("Failed not equal comparison results.  Expected value: (" + expected + ") Actual value: (" + actual + ") for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = false;
            if (testCentral.screenShotSaveFolder != null && !testCentral.screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, testCentral.GetBrowserUsed() + fileStepIndex + "Assert_Fail", testCentral.screenShotSaveFolder, false, fileStepIndex);
            }
        }
    }


    /*************************************************************
     * DESCRIPTION: (Refactored and extracted as separate method)
     *      Checks the status of the Get or Post against the
     *      expected value.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ************************************************************ */
    private void CheckGetPostStatus(TestStep ts, String fileStepIndex) {
        int expectedStatus = ts.get_expectedValue() != null ? parseInt(ts.get_expectedValue()) : 200;   //GetArgumentNumericValue(ts, 0, 200);
        int actualStatus;
        String url = testCentral.GetArgumentValue(ts, 0, null);
        String getPost = "Get";

        if (url != null) {
            if (ts.get_command().toLowerCase().contains(AppCommands.Post)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking Post status of " + url, false);
                actualStatus = httpResponseCodeViaPost(url);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Get)) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking Get status of " + url, false);
                actualStatus = httpResponseCodeViaGet(url);
            } else {
                testCentral.ImproperlyFormedTest(fileStepIndex);
                actualStatus = -1;
            }
            if (actualStatus != -1) {
                if (ts.get_crucial()) {
                    assertEquals(expectedStatus, actualStatus);
                } else {
                    try {
                        assertEquals(expectedStatus, actualStatus);
                    } catch (AssertionError ae) {
                        // do not capture screen shot here, if element not found, check methods will capture screen shot
                    }
                }
            }
            if (expectedStatus == actualStatus) {
                testHelper.UpdateTestResults("Successful " + getPost + " HTTP Response.  Expected: (" + expectedStatus + ") Actual: (" + actualStatus + ") for step " + fileStepIndex, true);
                testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
            } else if (expectedStatus != actualStatus) {
                testHelper.UpdateTestResults("Failed " + getPost + " HTTP Response.  Expected: (" + expectedStatus + ") Actual: (" + actualStatus + ") for step " + fileStepIndex, true);
                testCentral.conditionalSuccessful = false;
            }
        } else {
            testHelper.UpdateTestResults("Error: Required URL not provided as Argument 1 aborting for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = false;
        }
    }


    /*************************************************************
     * DESCRIPTION: Retrieves all anchor tags in a page and
     *              reports the status of all anchor tags that
     *              have an href attribute.
     * @param url - url to check
     ************************************************************ */
    void CheckBrokenLinks(TestStep ts, String url, String fileStepIndex) {
        if (url != null && !driver.getCurrentUrl().equals(url)) {
            driver.get(url);
        }
        int linkCount = 0;

        //Get all the links on the page
        List<WebElement> links = driver.findElements(By.cssSelector("a"));

        String href;
        String text;

        testHelper.UpdateTestResults(AppConstants.indent5 + "Retrieved " + links.size() + " anchor tags", true);
        for(WebElement link : links) {
            href = link.getAttribute("href");
            text = link.getAttribute("text");
            text = text.trim();

            if (href != null) {
                try {
                    text = text.isEmpty() ? "[Possible Image] " + link.findElement(By.tagName("img")).getAttribute("alt") : text;
                } catch (Exception nse) {
                    text = text.isEmpty() ? "[Possible Image] " : text;
                }
            }

            if (href != null) {
                linkCount++;
                int brokenLinksStatusCode = httpResponseCodeViaGet(href);
                if (200 != brokenLinksStatusCode) {
                    testHelper.UpdateTestResults("Failed link test " + href + " gave a response code of " + brokenLinksStatusCode + " for step " + fileStepIndex, true);
                    testCentral.conditionalSuccessful = false;
                } else {
                    testHelper.UpdateTestResults( "Successful link test text: " + text + " href: " + href + " xPath: " + testCentral.GenerateXPath(link, "") + " gave a response code of " + brokenLinksStatusCode + " for step " + fileStepIndex, true);
                    testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
                }
            }
        }
        testHelper.UpdateTestResults(AppConstants.indent5 + "Discovered " + linkCount + " links amongst " + links.size() + " anchor tags.\r\n", true);
    }


    /*************************************************************
     * DESCRIPTION:
     *      Checks all image tags for the presence of the checkType
     *      property passed in (alt, src).
     *      For alt property, it checks that the alt tag is present
     *      and that it contains information and displays a
     *      success or fail message accorgingly.
     *      For src property, it checks that the src tag is present
     *      and that it resolves to a 200 response status.
     * @param url - url to check
     * @param checkType - Set to Image alt or src attribute
     ************************************************************ */
    void CheckADAImages(TestStep ts, String url, String checkType, String fileStepIndex) {
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        List<WebElement> images = driver.findElements(By.cssSelector("img"));
        String altTag;
        String imgSrc;
        int altTagCount = 0;
        int brokenImageSrcStatusCode = 0;

        testHelper.UpdateTestResults(AppConstants.indent5 + "Retrieved " + images.size() + " image tags", true);
        for(WebElement link : images) {
            altTag = link != null ? link.getAttribute("alt") : null;
            imgSrc = link != null ? link.getAttribute("src") : null;
            if (checkType.toLowerCase().trim().equals("alt")) {
                if (altTag != null && !altTag.trim().isEmpty()) {
                    altTagCount++;
                    testHelper.UpdateTestResults("Successful image alt tag found: (" + altTag + ") for img src: " + imgSrc + " for step " + fileStepIndex, true);
                    testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || testCentral.conditionalSuccessful;
                } else {
                    testHelper.UpdateTestResults("Failed image alt tag missing for img src: " + imgSrc + " for step " + fileStepIndex, true);
                    testCentral.conditionalSuccessful = false;
                }
            } else if (checkType.toLowerCase().trim().equals("src")) {
                if (imgSrc != null && !imgSrc.trim().isEmpty()) {
                    altTagCount++;
                    try {
                        brokenImageSrcStatusCode = httpResponseCodeViaGet(imgSrc);
                    } catch (Exception ex) {
                        testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed Error when attempting to validate image src " + imgSrc + " Error: " + ex.getMessage() + " for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
                        testCentral.conditionalSuccessful = false;
                    }
                    if (200 != brokenImageSrcStatusCode) {
                        testHelper.UpdateTestResults("Failed image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + " for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = false;
                    } else {
                        testHelper.UpdateTestResults("Successful image src test " + imgSrc + " gave a response code of " + brokenImageSrcStatusCode + " for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || testCentral.conditionalSuccessful;
                    }
                } else {
                    if (altTag != null) {
                        testHelper.UpdateTestResults("Failed image src tag missing for image with alt tag: " + altTag + " for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = false;
                    } else {
                        testHelper.UpdateTestResults("Failed image src tag missing for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = false;
                    }
                }
            }
        }
        testHelper.UpdateTestResults(AppConstants.indent5 + "Discovered " + altTagCount + " image " + checkType.toLowerCase().trim()  + " attributes  amongst " + images.size() + " image tags.\r\n", true);
    }


    /******************************************************************************
     * DESCRIPTION: Control method used to Check the count of a specific element type.
     *  @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************************/
    private void CheckElementCountController(TestStep ts, String fileStepIndex) {
        String checkItem = testCentral.GetArgumentValue(ts, 0, null);
        String url = testCentral.GetArgumentValue(ts,1,null);
        url = (url != null && url.contains("!=")) ? null:
                (url != null && url.contains("=")) ? null : url;
        String page = (url == null || url.equals("!=") || url.equals("=")) ? driver.getCurrentUrl() : url;

        if (checkItem != null) {
            testHelper.UpdateTestResults(AppConstants.indent5 + "Checking count of " + checkItem + " on page " + page, false);
            int expectedCount = ts.get_expectedValue() != null ? parseInt(ts.get_expectedValue()) : 0;
            CheckElementCount(ts, url, checkItem, expectedCount, fileStepIndex, ts.get_crucial());
        } else {
            testCentral.ImproperlyFormedTest(fileStepIndex);
        }
    }


    /******************************************************************************
     * DESCRIPTION: Control method used to Check Color Contrast a value.
     *               actionType: read
     *               arg1: Type of Element to check against background
     *               arg2: [Optional and not recommended]
     *                      - Allows Overriding Acceptable Contrast settings b
     *                        for color brightness default is (125)
     *               arg3: [Optional and not recommended]
     *                      - Allows Overriding Acceptable Contrast settings d
     *                        for color difference default is (500)
     *
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     ****************************************************************************** */
    private void ColorContrastController(TestStep ts, String fileStepIndex) {
        testCentral.CheckColorContrastArgumentOrder(ts);
        //region { Debugging - Looping to see all arguments }
//        for(int x=0; x < ts.ArgumentList.size();x++) {
//            testHelper.DebugDisplay("For loop ts.ArgumentList.get(" + x + ").get_parameter() = " + ts.ArgumentList.get(x).get_parameter());
//        }
        //endregion
        String tagType =  testCentral.GetArgumentValue(ts, 0, null);

        testHelper.UpdateTestResults(AppConstants.indent5 + "Checking color contrast of " + tagType + " on page " + TestCentral.testPage + " for step " + fileStepIndex, false);
        CheckColorContrast(ts, fileStepIndex);
    }


    /********************************************************************************
     * DESCRIPTION: Control method used to Query Databases for a particular field value
     * NOT FULLY IMPLEMENTED - The method that this method calls is not fully baked.
     *                      Need to decide on the different databases that will be
     *                      supported for this.
     *                      Initial concept was with MongoDB but it was a blind exercise
     *                      in which I did not have any direct access to the database
     *                      to check against.
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     *  @param fileStepIndex - the file index and the step index.
     ********************************************************************************/
    private void DatabaseQueryController(TestStep ts, String fileStepIndex)  {
        try {
            if (ts.get_command().toLowerCase().contains(AppCommands.Mongo)) {
                testHelper.UpdateTestResults("Found query then mongo....", false);
                //make sure that this connection has been established
                if (testCentral.mongoClient != null) {
                    testHelper.UpdateTestResults("Found query, and mongo and in the if before RunMongoQuery....", false);
                    RunMongoQuery(ts, fileStepIndex);
                } else {
                    testHelper.UpdateTestResults("Connection is not available!!!", false);
                }
                testHelper.UpdateTestResults("Found query, and mongo after the if before RunMongoQuery....", false);
            } else if (ts.get_command().toLowerCase().contains(AppCommands.Sql_Server) || ts.get_command().toLowerCase().contains(AppCommands.SqlServer)) {
                testHelper.CreateSectionHeader("[ Start Sql Server Query Event ]", "", AppConstants.ANSI_CYAN, true, false, true);
                RunSqlServerQuery(ts, fileStepIndex);
                testHelper.CreateSectionHeader("[ End Sql Server Query Event ]", "", AppConstants.ANSI_CYAN, false, false, true);
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed in DatabaseQueryController for step " + fileStepIndex  + "\r\n" + e.getMessage() + AppConstants.ANSI_RESET, true);
        }
    }


    /******************************************************************************
     * DESCRIPTION: Control method used to find a Phrase.
     *  @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************************/
    private void FindPhraseController(TestStep ts, String fileStepIndex) {
        String cssSelector = testCentral.GetArgumentValue(ts, 0, "*");
        String phrase = ts.get_expectedValue();
        String message;
        String containsOrEquals = testCentral.GetArgumentValue(ts, 1, "equals");

        if (cssSelector != null && !cssSelector.trim().isEmpty() ) {
            if (!containsOrEquals.toLowerCase().equals("contains")) {
                message = "Performing find searching all '" + cssSelector.trim() + "' elements for '" + phrase.trim() + "'";
            } else {
                message = "Performing find searching all '" + cssSelector.trim() + "' elements containing '" + phrase.trim() + "'";
            }
        } else {
            if (!containsOrEquals.toLowerCase().contains("contains")) {
                message = "Performing find searching all elements for '" + phrase.trim() + "'";
            } else {
                message = "Performing find searching all elements containing '" + phrase.trim() + "'";
            }
        }
        testHelper.UpdateTestResults(AppConstants.indent5 + message + " for step " + fileStepIndex, true);
        FindPhrase(ts, fileStepIndex);
    }


    /*********************************************************************
     * DESCRIPTION:
     *      Retrieves the value of the element using the configured accessor
     *      and returns it to the calling method where it will be
     *      persisted in a string variable.
     *  @param ts - Test Step Object containing all related information
     *            for the particular test step.
     * @param accessor - The element accessor
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************** */
    private String PersistValue(TestStep ts, String accessor, String fileStepIndex) throws Exception  {
        String actual = null;
        testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved by " + ts.get_accessorType() + ": " + accessor + " for step " + fileStepIndex, true);
        actual = CheckElementWithAccessor(ts, fileStepIndex);
        return actual;
    }


    /****************************************************************************
     * DESCRIPTION: This method starts by checking if a URL parameter was
     *              supplied and if so, calls the navigation method to
     *              perform a Navigation event and make that the current page.
     *              Next, it retrieves the http response from the current
     *              page and returns it to the calling method where it will
     *              be persisted in a global variable for use throughout
     *              the application.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - the retrieved http response (JSON/XML) if successful else null
     * @throws Exception - Possible Exception attempting to retrieve JSON/XML
     *                      from API endpoint
     * *******************************************************************************/
    public String GetHttpResponse(TestStep ts, String fileStepIndex) throws Exception {
        String url = testCentral.GetArgumentValue(ts, 0, testCentral.GetCurrentPageUrl());
        StringBuffer response = new StringBuffer();

        if (url != null && !url.isEmpty()) {
            testHelper.setNavigationMessageIndent(AppConstants.indent8 + AppConstants.indent5);
            testCentral.PerformExplicitNavigation(ts, fileStepIndex);
            testHelper.setNavigationMessageIndent(null);
            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int responseCode = con.getResponseCode();
            String color = (responseCode == 200) ? AppConstants.ANSI_GREEN_BRIGHT : AppConstants.ANSI_RED_BRIGHT;
            testHelper.UpdateTestResults(AppConstants.indent8 + color + "Response Code " + responseCode + " for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        return response.toString();
    }



    /************************************************************************************
     * Description: This method searches the retrieved JSON for the Key/value pair
     *              and displays the count of all matching keys and whether one or more
     *              key contained the specific value.  If a key/value matches the search
     *              criteria the test is marked as successful, else the test is marked as
     *              failed.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***********************************************************************************/
    private void QueryJSON(TestStep ts, String fileStepIndex) {
        if (!testHelper.IsNullOrEmpty(testCentral.jsonContent)) {
            String jsonTemp = testCentral.jsonContent;
            ArrayList<String> searchList = new ArrayList<>();
            int count  = 0;
            int endPos;
            String searchString = "\"" + ts.get_accessor() + "\":" + ts.get_expectedValue();
            String searchKey = "\"" + ts.get_accessor() + "\":";
            int keyPos = 0;

            testHelper.UpdateTestResults(AppConstants.indent8 + "Searching JSON for " + searchString + " for step " + fileStepIndex,true);

            if (jsonTemp == null || jsonTemp.isEmpty()) {
                testHelper.UpdateTestResults(AppConstants.indent8 + "JSON not previously retrieved prior to query attempt for step " + fileStepIndex + " skipping.",true);
                return;
            }

            String elementContent;
            while (jsonTemp.contains(searchKey)) {
                endPos = 0;
                keyPos = jsonTemp.indexOf(searchKey);
                endPos = keyPos + searchString.length();
                elementContent = "";

                if (jsonTemp.substring(keyPos, endPos).equals(searchString)) {
                    elementContent = jsonTemp.substring(keyPos, endPos).trim();
                    elementContent = elementContent.substring(elementContent.indexOf(":") + 1).trim();
                    searchList.add(elementContent);
                }
                jsonTemp = jsonTemp.substring(endPos + 1);
                count++;
            }

            if (searchList.size() > 0) {
                testHelper.UpdateTestResults("Successful JSON Search.  Searched all " + ts.get_accessor() + " for: (" +  ts.get_expectedValue() + ") Found: (" +  searchList.get(0) + ")\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + ts.get_accessor()  + " keys with " + searchList.size() + " values containing the expected value for step " + fileStepIndex, true);
                if (ts.get_crucial()) {
                    assertEquals( ts.get_expectedValue(), searchList.get(0));
                }
            } else {
                testHelper.UpdateTestResults("Failed JSON Search.  Searched for: (" +  searchString + ") but did not find this!\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + searchString + " keys but none contained the expected value for step " + fileStepIndex, true);
                //If this is crucial and the search string is not found, force an assertion failure
                if (ts.get_crucial()) {
                    assertEquals(ts.get_expectedValue(), null);
                }
            }
        } else {
            testHelper.UpdateTestResults("Failed JSON Search.  JSON content was not previously retrieved successfully\r\n." +
                    "Either the Get JSON retrieval step failed or no Get JSON step preceded this JSON Query attempt for step " + fileStepIndex, true);
        }
    }


    /*****************************************************************************
     * Description: Saves previously retrieved JSON to the file specified in the
     *              command argument.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *****************************************************************************/
    private void SaveJsonToFile(TestStep ts, String fileStepIndex) {
        String fileName = testCentral.GetArgumentValue(ts, 0, null);
        String overWriteExisting = testCentral.GetArgumentValue(ts, 1, "false");
        boolean overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        String originalFileName = fileName;
        String updateFileNameMessage = "";

        if (overWriteExisting.contains("\\") || overWriteExisting.contains("//")) {
            testCentral.ArgumentOrderErrorMessage(ts, "save json");
            String [] items = {fileName, overWriteExisting};
            testCentral.RearrangeArgumentOrder(ts, items, ts.get_command());
            fileName = testCentral.GetArgumentValue(ts, 0, null);
            originalFileName = fileName;
            overWriteExisting = testCentral.GetArgumentValue(ts, 1, "false");
            overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        }

        if (testCentral.jsonContent != null && !testCentral.jsonContent.isEmpty() && fileName != null) {
            if (!overwriteExistingFile) {
                fileName = testHelper.GetUnusedFileName(fileName);
                if (!originalFileName.equals(fileName)) {
                    updateFileNameMessage = "A File with the original file name existed.\r\n" +
                            AppConstants.indent8 + "File name updated from: " + originalFileName + " to " + fileName;
                    testHelper.UpdateTestResults(AppConstants.indent8 + updateFileNameMessage, true);
                }
            } else {
                testHelper.DeleteFile(fileName);
            }

            testHelper.UpdateTestResults(AppConstants.indent8 + "Saving JSON to file:" + fileName + " for step " + fileStepIndex, true);
            testHelper.WriteToFile(fileName, testCentral.jsonContent);
            testHelper.UpdateTestResults("Successful JSON saved to file " + fileName + " for step " + fileStepIndex, true);
        } else {
            String errorMessage;
            if (testCentral.jsonContent != null && !testCentral.jsonContent.isEmpty()) {
                errorMessage = "Aborting!!!  No JSON content was previously retrieved.";
            } else {
                errorMessage = "Aborting!!!  No File Name was specified as the destination for the downloaded JSON content.";
            }
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed JSON not saved to file because: " + errorMessage + " for step " + fileStepIndex, true);
        }
    }


    /************************************************************************************
     * Description: This method searches the retrieved XML for the Element/Node value
     *              and displays the count of all matching Elements/Nodes and whether
     *              one or more Elements/Nodes contained the specific value.
     *              If an Element/Node matches the search criteria the test is
     *              marked as successful, else the test is marked as failed.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***********************************************************************************/
    private void QueryXML(TestStep ts, String fileStepIndex) {
        if (!testHelper.IsNullOrEmpty(testCentral.xmlContent)) {
            String xmlTemp = testCentral.xmlContent;
            //testHelper.DebugDisplay("xmlTemp = " + xmlTemp);
            ArrayList<String> searchList = new ArrayList<>();
            int count  = 0;
            int startPos, endPos;
            String elementStart = "<" + ts.get_accessor() + ">";
            String elementEnd = !ts.get_accessor().contains(" ") ?  "</" + ts.get_accessor() + ">" : "</" + ts.get_accessor().substring(0, ts.get_accessor().indexOf(" ")).trim() + ">";
            String elementContent;

            while (xmlTemp.contains(elementStart)) {
                startPos = xmlTemp.indexOf(elementStart) + elementStart.length();
                endPos = xmlTemp.indexOf(elementEnd);
                elementContent = xmlTemp.substring(startPos, endPos).trim();
                //testHelper.DebugDisplay("elementContent(" + count + ") = " + elementContent);
                if (elementContent.equals(ts.get_expectedValue())) {
                    searchList.add(elementContent);
                }
                xmlTemp = xmlTemp.substring(endPos + elementEnd.length());
                count++;
            }

            testHelper.UpdateTestResults(AppConstants.indent8 + "Searching XML for " + elementStart + ts.get_expectedValue() + elementEnd + " for step " + fileStepIndex,true);
            if (searchList.size() > 0) {
                testHelper.UpdateTestResults("Successful XML Search.  Searched for: (" +  ts.get_expectedValue() + ") Found: (" +  searchList.get(0) + ")\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + elementStart + elementEnd + " elements with " + searchList.size() + " elements containing the expected value for step " + fileStepIndex, true);
                if (ts.get_crucial()) {
                    assertEquals( ts.get_expectedValue(), searchList.get(0));
                }
            } else {
                testHelper.UpdateTestResults("Failed XML Search.  Searched for: (" +  elementStart + ts.get_expectedValue() + elementEnd + ") but did not find this!\r\n" +
                        AppConstants.indent5 +  "- There were " + count + " " + elementStart + elementEnd + " elements but none contained the expected value for step " + fileStepIndex, true);
                //If this is crucial and the search string is not found, force an assertion failure
                if (ts.get_crucial()) {
                    assertEquals(ts.get_expectedValue(), null);
                }
            }
        } else {
            testHelper.UpdateTestResults("Failed XML Search.  XML content was not previously retrieved successfully\r\n." +
                    "Either the Get XML retrieval step failed or no Get XML step preceded this XML Query attempt for step " + fileStepIndex, true);
        }
    }


    /*****************************************************************************
     * Description: Saves previously retrieved XML to the file specified in the
     *              command argument.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     *****************************************************************************/
    private void SaveXmlToFile(TestStep ts, String fileStepIndex) {
        String fileName = testCentral.GetArgumentValue(ts, 0, null);
        String overWriteExisting =  testCentral.GetArgumentValue(ts, 1, "false");
        boolean overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        String originalFileName = fileName;
        String updateFileNameMessage = "";

        if (overWriteExisting.contains("\\") || overWriteExisting.contains("//")) {
            testCentral.ArgumentOrderErrorMessage(ts, "save xml");
            String [] items = {fileName, overWriteExisting};
            testCentral.RearrangeArgumentOrder(ts, items, ts.get_command());
            fileName = testCentral.GetArgumentValue(ts, 0, null);
            originalFileName = fileName;
            overWriteExisting = testCentral.GetArgumentValue(ts, 1, "false");
            overwriteExistingFile = (overWriteExisting.toLowerCase().equals("true") || overWriteExisting.toLowerCase().equals("overwrite")) ? true : false;
        }

        if (!testHelper.IsNullOrEmpty(testCentral.xmlContent) && !testHelper.IsNullOrEmpty(fileName)) {
            if (!overwriteExistingFile) {
                fileName = testHelper.GetUnusedFileName(fileName);
                if (!originalFileName.equals(fileName)) {
                    updateFileNameMessage = "A File with the original file name existed.\r\n" +
                            AppConstants.indent8 + "File name updated from: " + originalFileName + " to " + fileName;
                    testHelper.UpdateTestResults(AppConstants.indent8 + updateFileNameMessage, true);
                }
            } else {
                testHelper.DeleteFile(fileName);
            }

            testHelper.UpdateTestResults(AppConstants.indent8 + "Saving XML to file:" + fileName + " for step " + fileStepIndex, true);
            testHelper.WriteToFile(fileName, testCentral.xmlContent);
            testHelper.UpdateTestResults("Successful XML saved to file " + fileName + " for step " + fileStepIndex, true);
        } else {
            String errorMessage;
            if (testCentral.xmlContent != null && !testCentral.xmlContent.isEmpty()) {
                errorMessage = "Aborting!!!  No XML content was previously retrieved.";
            } else {
                errorMessage = "Aborting!!!  No File Name was specified as the destination for the downloaded XML content.";
            }
            testHelper.UpdateTestResults(AppConstants.indent8 + "Failed XML not saved to file because: " + errorMessage + " for step " + fileStepIndex, true);
        }
    }
    //endregion


    /***************************************************************************************
     * Description: This method retrieves the text from a page element and returns it to
     *              the calling method.
     * @param ts -  Test Step Object containing all related information
     *              for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return  - Text from page element.
     ***************************************************************************************/
    private String GetElementText(TestStep ts, String fileStepIndex) {
        String actual = null;  //element equation retrieved from the page
        testHelper.UpdateTestResults(AppConstants.indent8 + "Element text being retrieved at step " + fileStepIndex + " by " + ts.get_accessorType() + " " + ts.get_accessor(), true);
        actual = CheckElementWithAccessor(ts, fileStepIndex);
        return actual;
    }

    /*************************************************************
     * DESCRIPTION: Retrieves element text or element value,
     *              if text is null, using its xPath accessor
     *              and returns it to the calling method.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @return - Actual Element Text
     ************************************************************ */
    String CheckElementWithAccessor(TestStep ts, String fileStepIndex) {
        String actualValue;
        String accessor = ts.get_accessor();
        String command = ts.get_command().toLowerCase().equals(AppCommands.Switch_To_IFrame) ? testCentral.GetArgumentValue(ts, 1, null) : null;
        String accessorType = ts.get_accessorType();

        try {
            String typeOfElement = GetWebElementByAccessor(ts).getAttribute("type");
            if (typeOfElement!= null && ((typeOfElement.contains("select-one") || typeOfElement.contains("select-many")))) {
                Select select = new Select(GetWebElementByAccessor(ts));
                WebElement option = select.getFirstSelectedOption();
                actualValue = option.getText();
            } else {
                boolean isVisible = GetWebElementByAccessor(ts).isDisplayed();
                if (isVisible) {
                    actualValue = GetWebElementByAccessor(ts).getText();
                } else {
                    //String script = "return arguments[0].innerHTML";
                    String script = "return arguments[0].innerText";
                    actualValue = (String) ((JavascriptExecutor) driver).executeScript(script, GetWebElementByAccessor(ts));
                }

                if (actualValue == null || actualValue.isEmpty()) {
                    actualValue = GetWebElementByAccessor(ts).getAttribute("value");
                }
                if (command != null && command.toLowerCase().equals(AppConstants.persistStringCheckValue)) {
                    testCentral.PersistProvidedValueController(ts, actualValue, fileStepIndex);
                }
           }

            //TODO: REWRITE THE CALL TO ELEMENTTYPELOOKUP SO THAT IT ONLY LOOKS UP ELEMENTS THAT ARE SEARCHED FOR BY TAGNAME, XPATH OR CSSSELECTOR AND SKIPS CLASSNAME AND ID
            if (!ts.get_command().toLowerCase().contains(AppConstants.persistStringCheckValue) && (command == null || !command.toLowerCase().contains(AppConstants.persistStringCheckValue))) {
                testHelper.UpdateTestResults(AppConstants.indent5 + "Checking element by " + ts.get_accessorType() + ":"  + testCentral.ElementTypeLookup(accessor, accessorType) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Retrieving element text by " + ts.get_accessorType() + ":"  + testCentral.ElementTypeLookup(accessor, accessorType) + " for script " + fileStepIndex + " Actual Value: \"" + actualValue + "\"", true);
            }
        } catch (Exception e) {
            if (testCentral.screenShotSaveFolder == null || testCentral.screenShotSaveFolder.isEmpty()) {
                testHelper.CaptureScreenShot(driver, testCentral.GetBrowserUsed() + "_" + fileStepIndex + ts.get_accessorType() + "_Element_Not_Found", testCentral.configurationFolder, true, fileStepIndex);
            } else {
                testHelper.CaptureScreenShot(driver, testCentral.GetBrowserUsed() + "_" + fileStepIndex + ts.get_accessorType() + "_Element_Not_Found", testCentral.screenShotSaveFolder, true, fileStepIndex);
            }
            actualValue = null;
        }
        return actualValue;
    }

    /*********************************************************************************************
     * DESCRIPTION: Retrieves element based on the AccessorType and the Accessor and returns it
     *              to the calling method.
     * @param ts
     * @return
     *********************************************************************************************/
    WebElement GetWebElementByAccessor(TestStep ts) {
        String accessor = ts.get_accessor();
        WebElement element = ts.get_accessorType().toLowerCase().equals(AppConstants.xpathCheckValue) ? this.driver.findElement(By.xpath(accessor)) :
                ts.get_accessorType().toLowerCase().equals(AppConstants.cssSelectorCheckValue) ? this.driver.findElement(By.cssSelector(accessor)) :
                        ts.get_accessorType().toLowerCase().equals(AppConstants.classNameCheckValue) ? this.driver.findElement(By.className(accessor)) :
                                ts.get_accessorType().toLowerCase().equals(AppConstants.tagNameCheckValue) ? this.driver.findElement(By.tagName(accessor)) :
                                        this.driver.findElement(By.id(accessor));

        return element;
    }

    /*************************************************************
     * DESCRIPTION: Returns the status code of the url passed in for a
     *              GET request.
     * @param url - url to check
     ************************************************************ */
    int httpResponseCodeViaGet(String url) {
        try {
            return RestAssured.get(url).statusCode();
        } catch(Exception e) {
            return 404;
        }
    }


    /*************************************************************
     * DESCRIPTION:
     *      Returns the status code of the url passed in for a
     *      POST request.
     * @param url - url to check
     ************************************************************ */
    int httpResponseCodeViaPost(String url) {
        try {
            return RestAssured.post(url).statusCode();
        } catch(Exception e) {
            return 404;
        }
    }


    /*********************************************************************************************
     * DESCRIPTION:
     *      Performs a count of all checkElement tags for the url
     *      passed in or the current url, if not passed in, and
     *      compares that count to the expectedCount passed in.
     *      If this test is marked as crucial, all testing stops
     *      if the counts do not match.
     *      If this test is not marked as crucial, testing
     *      continues and the status is reported.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param url - URL if checking page other than current page
     * @param checkElement - Element Type to count
     * @param expectedCount - The number of elements expected
     * @param fileStepIndex - the file index and the step index.
     * @param isCrucial - Indicates if this is a crucial or non-crucial assertion
     ***********************************************************************************************/
    private void CheckElementCount(TestStep ts, String url, String checkElement, int expectedCount, String fileStepIndex, boolean isCrucial) {
        int actualCount;
        if (url != null && !url.isEmpty() && !url.toLowerCase().trim().equals("n/a")) {
            driver.get(url);
        }
        String comparisonType = CheckComparisonOperator(testCentral.GetArgumentValue(ts, ts.ArgumentList.size()-1, "="));

        List<WebElement> elements = driver.findElements(By.cssSelector(checkElement));
        actualCount = elements.size();

        if (isCrucial) {
            assertEquals(expectedCount, actualCount);
        } else {
            try {
                assertEquals(expectedCount, actualCount);
            } catch (AssertionError ae) {
                //do nothing, just trap the error so that testing can continue
            }

            if (comparisonType.equals("=")) {
                if (actualCount != expectedCount) {
                    testHelper.UpdateTestResults("Failed to match count of '" + checkElement + "' tags.  Expected: " + expectedCount + "  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    testCentral.conditionalSuccessful = false;
                } else {
                    testHelper.UpdateTestResults("Successful matching count of '" + checkElement + "' tags.  Expected: " + expectedCount + "  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    //conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : conditionalSuccessful;
                    testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || testCentral.conditionalSuccessful;
                }
            } else {
                if (actualCount != expectedCount) {
                    testHelper.UpdateTestResults("Successful not equal count of '" + checkElement + "' tags.  Expected: " + expectedCount + " !=  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    testCentral.conditionalSuccessful = false;
                } else {
                    testHelper.UpdateTestResults("Failed not equal count of '" + checkElement + "' tags.  Expected: " + expectedCount + " !=  Actual: " + actualCount + " for step " + fileStepIndex, true);
                    testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || testCentral.conditionalSuccessful;
                }
            }
        }
    }

    /****************************************************************
     * Description: Checks the argument value passed in to determine
     *              if it is one of the acceptable comparison
     *              operators.
     *
     * @param comparisonType - Operator used for comparison, can be
     *                       "=" or "!="
     *                       (quotes used for clarity only).
     * @return - returns the comparison type as a string.
     ****************************************************************/
    private String CheckComparisonOperator(String comparisonType) {
        //in case the comparison type is not passed in, default it to equals.
        if (comparisonType == null || (!comparisonType.equals("!=") && !comparisonType.equals("="))) {
            comparisonType = "=";
        }
        return comparisonType;
    }

    /***********************************************************************
     * DESCRIPTION: If a Tag is passed in as an argument, search the text
     *              of all tags of that type for the phrase, but if no tag
     *              is passed in, search the text of all page elements for
     *              the phrase.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ********************************************************************** */
    private void FindPhrase(TestStep ts, String fileStepIndex) {
        String cssSelector = testCentral.GetArgumentValue(ts, 0, "*");
        String searchType = testCentral.GetArgumentValue(ts, 1, "equals");
        boolean wasFound = false;

        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        List<String> foundElements = new ArrayList<>();

        //region {Implemented in the bottom for loop}
        // When searching a specific tag type for the phrase,
        // iterate through all child elements to see if one of them contains the text.
        // if a child or grandchild contains the text, eliminate the element as containing the text
        //endregion

        if (searchType.toLowerCase().equals("contains")) {
            for (WebElement element : elements) {
                try {
                    if (element != null && element.getText().trim().contains(ts.get_expectedValue().trim())) {
                        wasFound = true;
                        foundElements.add(testCentral.GenerateXPath(element, ""));
                    }
                } catch (StaleElementReferenceException stale) {
                    //do nothing if the element reference is stale
                }
            }
        } else {
            for (WebElement element : elements) {
                try {
                    if (element != null && element.getText().equals(ts.get_expectedValue().trim())) {
                        wasFound = true;
                        foundElements.add(testCentral.GenerateXPath(element, ""));
                    }
                } catch (StaleElementReferenceException stale) {
                    //do nothing if the element reference is stale
                }
            }
        }

        if (!wasFound) {
            String message = "Failed to find (" + ts.get_expectedValue().trim() + ") searching all elements.";
            if (!cssSelector.trim().isEmpty()) {
                message = "Failed to find (" + ts.get_expectedValue().trim() + ") searching all " + cssSelector + " elements.";
                testCentral.conditionalSuccessful = false;
            }
            //testHelper.UpdateTestResults(AppConstants.ANSI_RED + message + AppConstants.ANSI_RESET, true);
            testHelper.UpdateTestResults(message, true);
        } else {
            testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) || testCentral.conditionalSuccessful;
            //eliminate any hierarchical elements that don't actually contain the text
            for (int y = foundElements.size() -1;y>= 0;y--) {
                for (int x = foundElements.size() - 1;x>=0;x--) {
                    if (y != x) {
                        try {
                            if (foundElements.get(y).contains(foundElements.get(x))) {
                                foundElements.remove(x);
                            } else if (foundElements.get(x).contains(foundElements.get(y))) {
                                foundElements.remove(y);
                            }
                        } catch(IndexOutOfBoundsException io) {
                            //try moving on to the next item doing nothing here
                            //testHelper.UpdateTestResults("Error y = " + y + " and x = " + x + " - " + io.getMessage(), false);
                        }
                    }
                }
            }
            for (String foundElement : foundElements) {
                testHelper.UpdateTestResults("Successful found (" + ts.get_expectedValue().trim() + ") in element: " + foundElement + " for step " + fileStepIndex, true);
            }
        }
    }


    void CheckJavaScriptReturnValue(TestStep ts, String fileStepIndex) {
        String javaScriptText = testCentral.GetArgumentValue(ts, 0, null);
        boolean showJavascriptCommand = Boolean.parseBoolean(testCentral.GetArgumentValue(ts, 1, "true"));
        String actualValue = null;
        String comparisonType = CheckComparisonOperator(testCentral.GetArgumentValue(ts, ts.ArgumentList.size()-1, "="));
        if (javaScriptText != null) {
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Start JavaScript Execution Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
            if (showJavascriptCommand == true) {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Executing Javascript command: " + javaScriptText + " for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults(AppConstants.indent8 + "Executing Javascript command: {{JavaScript Command}} for step " + fileStepIndex, true);
            }
            actualValue = (String) ((JavascriptExecutor) driver).executeScript(javaScriptText);
            //testHelper.DebugDisplay("actualValue = " + actualValue);
            if (ts.get_crucial()) {
                if ("=".equals(comparisonType)) {
                    assertEquals(ts.get_expectedValue(), actualValue);
                } else {
                    assertNotEquals(ts.get_expectedValue(), actualValue);
                }
            } else {
                if ("=".equals(comparisonType)) {
                    if (!testHelper.IsNullOrEmpty(actualValue) && actualValue.equals(ts.get_expectedValue())) {
                        testHelper.UpdateTestResults(AppConstants.indent8 + "Successful JavaScript Value - Expected (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")" + " for step " + fileStepIndex, true);
                    } else {
                        testHelper.UpdateTestResults(AppConstants.indent8 + "Failed JavaScript Value - Expected (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")" + " for step " + fileStepIndex, true);
                    }
                } else {
                    if (!testHelper.IsNullOrEmpty(actualValue) && !actualValue.equals(ts.get_expectedValue())) {
                        testHelper.UpdateTestResults(AppConstants.indent8 + "Successful JavaScript Value - Expected (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")" + " for step " + fileStepIndex, true);
                    } else {
                        testHelper.UpdateTestResults(AppConstants.indent8 + "Failed JavaScript Value - Expected (" + ts.get_expectedValue() + ") Actual: (" + actualValue + ")" + " for step " + fileStepIndex, true);
                    }
                }
            }
            testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End JavaScript Execution Event ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        }
    }

    /******************************************************************************************
     * DESCRIPTION: This method checks brightness and degree of difference in color between
     *              the font color and the background color.
     *              If the background color cannot be found on the container element acting
     *              as the background, this method climbs the ancestral hierarchy until it
     *              finds the color used for the background.
     *
     * This page was used as a reference: https://www.w3.org/TR/AERT/#color-contrast
     * The ADA Approved range for Color Brightness difference is 125.
     * Color Brightness Formula = (299*R + 587*G + 114*B) / 1000
     *
     * The ADA Approved range for Color Difference is 500.
     * Color Difference Formula = (maximum (Red value 1, Red value 2) - minimum (Red value 1, Red value 2)) +
     *                            (maximum (Green value 1, Green value 2) - minimum (Green value 1, Green value 2)) +
     *                            (maximum (Blue value 1, Blue value 2) - minimum (Blue value 1, Blue value 2))
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ******************************************************************************************/
    void CheckColorContrast(TestStep ts, String fileStepIndex) {
        String tagType = testCentral.GetArgumentValue(ts, 0, null);
        String bContrast = testCentral.GetArgumentValue(ts, 1, AppConstants.DefaultContrastBrightnessSetting );
        String dContrast = testCentral.GetArgumentValue(ts, 2, AppConstants.DefaultContrastDifferenceSetting);
        bContrast = bContrast.contains("=") ? bContrast.substring(bContrast.indexOf("=") + 1).trim() : bContrast;
        dContrast = dContrast.contains("=") ? dContrast.substring(dContrast.indexOf("=") + 1).trim() : dContrast;
        int treeClimb;  // = 0;
        String color;
        String backColor;
//        String color_hex[];
//        String backColor_hex[];
        String cHex, bHex;
        int brightnessStandard = bContrast.equals("125") ? 125 : parseInt(bContrast);
        int contrastStandard = dContrast.equals("500") ? 500 : parseInt(dContrast);
        boolean anyFailure = false;

        List<WebElement> elements = driver.findElements(By.cssSelector(tagType));
        testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowLeft + testHelper.PrePostPad(AppConstants.ANSI_RESET + "[ Start Check Color Contrast ]" + AppConstants.ANSI_YELLOW, "═", 9, 80) + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        if (!bContrast.equals("125") || !dContrast.equals("500")) {
            testCentral.AdaApprovedContrastValuesOverriddenMessage(brightnessStandard, contrastStandard);
        }

        for(WebElement element : elements) {
            treeClimb = 0;
            color = element.getCssValue("color").trim();
            backColor = element.getCssValue("background-color").trim();


            cHex = Color.fromString(color).asHex();
            bHex = Color.fromString(backColor).asHex();
            WebElement parent = null;

            while (cHex.equals(bHex)) {
                try {
                    //testHelper.UpdateTestResults("Font color and background-color match!!!!", false);
                    treeClimb++;
                    if (parent == null) {
                        parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].parentNode;", element);
                    } else {
                        parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].parentNode;", parent);
                    }
                    backColor = parent.getCssValue("background-color").trim();
                    bHex = Color.fromString(backColor).asHex();
                } catch (Exception ex) {
                    //in case you walk the entire tree and no difference is found
                    break;
                }
            }

            // reference: https://www.w3.org/TR/AERT/#color-contrast
            //color brightness The rage for color brightness difference is 125.
            //brightness = (299*R + 587*G + 114*B) / 1000
            String[] foreColors = color.substring(color.indexOf("(") + 1, color.indexOf(")")).split(",");
            String[] backColors = backColor.substring(backColor.indexOf("(") + 1, backColor.indexOf(")")).split(",");
            double foreColorBrightness = ((parseInt(foreColors[0].trim()) * 299) + (parseInt(foreColors[1].trim()) * 587) + (parseInt(foreColors[2].trim()) * 114)) / 1000;
            double backColorBrightness = ((parseInt(backColors[0].trim()) * 299) + (parseInt(backColors[1].trim()) * 587) + (parseInt(backColors[2].trim()) * 114)) / 1000;
            double brightness;
            double contrast;

            //color difference The range for color difference is 500.
            //(maximum (Red value 1, Red value 2) - minimum (Red value 1, Red value 2)) + (maximum (Green value 1, Green value 2) - minimum (Green value 1, Green value 2)) + (maximum (Blue value 1, Blue value 2) - minimum (Blue value 1, Blue value 2))
            int maxRed = parseInt(foreColors[0].trim()) > (parseInt(backColors[0].trim())) ? parseInt(foreColors[0].trim()) : (parseInt(backColors[0].trim()));
            int minRed = parseInt(foreColors[0].trim()) > (parseInt(backColors[0].trim())) ? (parseInt(backColors[0].trim())) : parseInt(foreColors[0].trim());
            int maxGreen = parseInt(foreColors[1].trim()) > (parseInt(backColors[1].trim())) ? parseInt(foreColors[1].trim()) : (parseInt(backColors[1].trim()));
            int minGreen = parseInt(foreColors[1].trim()) > (parseInt(backColors[1].trim())) ? (parseInt(backColors[1].trim())) : parseInt(foreColors[1].trim());
            int maxBlue = parseInt(foreColors[2].trim()) > (parseInt(backColors[2].trim())) ? parseInt(foreColors[2].trim()) : (parseInt(backColors[2].trim()));
            int minBlue = parseInt(foreColors[2].trim()) > (parseInt(backColors[2].trim())) ? (parseInt(backColors[2].trim())) : parseInt(foreColors[2].trim());

            contrast = (maxRed - minRed) + (maxGreen - minGreen) + (maxBlue - minBlue);

            if (foreColorBrightness > backColorBrightness) {
                brightness = foreColorBrightness - backColorBrightness;
            } else {
                brightness = backColorBrightness - foreColorBrightness;
            }

            String backColorAncestor = treeClimb > 0 ? "^" + treeClimb : "";

            testHelper.UpdateTestResults(AppConstants.indent8 + "Element being checked: " + testCentral.GenerateXPath(element,"") + " for step " + fileStepIndex, false);
            if (brightness >= brightnessStandard && contrast >= contrastStandard) {
                testHelper.UpdateTestResults(AppConstants.ANSI_GREEN + "Good brightness and Good contrast (Brightness Difference: " + brightness + " Color Difference: " + contrast + ")\r\n - forecolor(" + color + ") forecolor Brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + AppConstants.ANSI_RESET, false);
            } else if (brightness >= brightnessStandard && contrast < contrastStandard) {
                testHelper.UpdateTestResults( AppConstants.ANSI_GREEN + "Good brightness " + AppConstants.ANSI_RED + "Warning contrast (" + AppConstants.ANSI_GREEN + " Brightness Difference: " + brightness + AppConstants.ANSI_RED + " Color Difference: " + contrast + ")\r\n - forecolor(" + color + ") brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + AppConstants.ANSI_RESET , false);
                anyFailure = true;
            } else if (brightness < brightnessStandard && contrast >= contrastStandard) {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Warning brightness" + AppConstants.ANSI_GREEN +" and Good contrast (" + AppConstants.ANSI_RED + "Brightness Difference:: " + brightness + AppConstants.ANSI_GREEN + " Color Difference: " + contrast + ")\r\n" + AppConstants.ANSI_RED + " - forecolor(" + color + ") brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness + AppConstants.ANSI_RESET, false);
                anyFailure = true;
            } else {
                testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Warning brightness and Warning contrast (Contrast: " + brightness + " Color Difference: " + contrast + ")\r\n - forecolor(" + color + ") brightness: " + foreColorBrightness + "\r\n - backcolor(" + backColor + ")" + backColorAncestor + " Back-Color Brightness: " + backColorBrightness +  AppConstants.ANSI_RESET, false);
                anyFailure = true;
            }
            testHelper.UpdateTestResults("", true);
        }
        testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? !anyFailure : testCentral.conditionalSuccessful;
        testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowLeft + testHelper.PrePostPad(AppConstants.ANSI_RESET + "[ End Check Color Contrast ]" + AppConstants.ANSI_YELLOW, "═", 9, 80) + AppConstants.ANSI_YELLOW + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
    }

    /***************************************************************
     * Description: Gets All Cookies and displays them and writes them to
     *              the logs.
     *              I currently only see cookies for the current domain.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************/
    public void GetAllCookies(TestStep ts, String fileStepIndex) {
        Set<Cookie> cookies = driver.manage().getCookies();
        System.out.println(cookies);
        String cookieName, cookieValue, cookieExpiration, cookiePath,cookieDomain;
        String fileContents = "URL: " + driver.getCurrentUrl() + "\n\n";
        String fileName = testCentral.GetArgumentValue(ts,0,null);
        String originalFileName = fileName;
        String fileInstructions = testCentral.GetArgumentValue(ts,1,null);
        boolean cookieSecure = false;
        testHelper.DebugDisplay("fileName = " + fileName);
        testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_CYAN_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ Displaying Cookie Information for Step " +  fileStepIndex + " ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
        for (Cookie cookie :cookies) {
            cookieName = cookie.getName();
            cookieValue = cookie.getValue();
            cookieExpiration = !testHelper.IsNullOrEmpty(String.valueOf(cookie.getExpiry())) && String.valueOf(cookie.getExpiry()) != "null" ? String.valueOf(cookie.getExpiry()) : "unknown";
            cookiePath = !testHelper.IsNullOrEmpty(cookie.getPath()) ? cookie.getPath() : "unknown";
            cookieDomain = !testHelper.IsNullOrEmpty(cookie.getDomain()) ? cookie.getDomain() : "unknown";
            cookieSecure = cookie.isSecure();

            testHelper.UpdateTestResults(AppConstants.indent8 + AppConstants.ANSI_CYAN_BRIGHT + "Cookie Name: " + cookieName + "\n" +
                    AppConstants.indent8 + "Cookie Value: " + cookieValue + "\n" +
                    AppConstants.indent8 + "Cookie Expires: " + cookieExpiration + "\n" +
                    AppConstants.indent8 + "Cookie Path: " + cookiePath + "\n" +
                    AppConstants.indent8 + "Cookie Domain: " + cookieDomain + "\n" +
                    AppConstants.indent8 + "Cookie Secure: " + cookieSecure + AppConstants.ANSI_RESET + "\n", true);
            fileContents += "Cookie Name: " + cookieName + "\n" + "Cookie Value: " + cookieValue + "\n" + "Cookie Expires: " + cookieExpiration + "\n" + "Cookie Path: " + cookiePath + "\n" + "Cookie Domain: " + cookieDomain + "\n" + "Cookie Secure:" + cookieSecure + "\n\n";
        }
        if (!testHelper.IsNullOrEmpty(fileName)) {
            if (fileInstructions.toLowerCase().equals("overwrite")) {
                testHelper.DeleteFile(fileName);
            } else if (fileInstructions.toLowerCase().equals("new")) {
                fileName = testHelper.GetUnusedFileName(fileName);
            }
            testHelper.WriteToFile(fileName, fileContents);
            testHelper.UpdateTestResults( AppConstants.indent8 + "Cookies saved to file: " + fileName + " for step " + fileStepIndex, true);
        }
        testHelper.UpdateTestResults( AppConstants.indent5 + AppConstants.ANSI_CYAN_BRIGHT + AppConstants.subsectionArrowLeft + testHelper.PrePostPad("[ End of Display Cookie Information ]", "═", 9, 80) + AppConstants.subsectionArrowRight + AppConstants.ANSI_RESET, true);
    }

    //region { SQL Server Methods }
    /**************************************************************************
     * Description: Opens a Sql Server connection and sets the global
     *              sqlConnection object so that it can be used
     *              throughout the rest of the test.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     **************************************************************************/
    void SetSqlServerClient(TestStep ts, String fileStepIndex) {
        String sqlDatabaseName = testCentral.GetArgumentValue(ts, 1, null);
        String sqlUserId = testCentral.GetArgumentValue(ts, 2, null);
        String sqlPassword = testCentral.GetArgumentValue(ts, 3, null);
        String sqlConnectionString = sqlDatabaseName.contains("jdbc:sqlserver") ? sqlDatabaseName : null;

        if (sqlDatabaseName != null && sqlUserId != null && sqlPassword != null && sqlConnectionString == null) {
            sqlConnectionString = sqlConnectionString == null ? "database=" + sqlDatabaseName + ";user=" + sqlUserId + ";password=" + sqlPassword + ";" : sqlConnectionString;
            if (sqlConnectionString != null && !sqlConnectionString.isEmpty()) {
                //sqlConnectionString = "jdbc:sqlserver://localhost:1433;" + sqlConnectionString + "encrypt=true;trustServerCertificate=false;loginTimeout=30;";
                sqlConnectionString = "jdbc:sqlserver://localhost:1433;" + sqlConnectionString + "encrypt=false;trustServerCertificate=true;loginTimeout=30;";
            }
        }

        try {
            if (sqlConnectionString != null) {
                testCentral.sqlConnection = DriverManager.getConnection(sqlConnectionString);
                testHelper.UpdateTestResults("Successful establishment of connection to SQL Server Database for step " + fileStepIndex, true);
            } else {
                testHelper.UpdateTestResults("Failed to establish a connection to the SQL Server for step " + fileStepIndex, true);
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults("Failure", true);
            testHelper.UpdateTestResults("Failed to establish a connection to the SQL Server.\r\n Error Message: " + e.getMessage() + " for step " + fileStepIndex, true);
        }
    }

    /******************************************************************************************
     * Description: Runs a Sql Server query that returns one field, retrieves that fields value
     *              and compares it to the expected value.
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     * @throws SQLException - SQL Server Exception
     ******************************************************************************************/
    private void RunSqlServerQuery(TestStep ts, String fileStepIndex) throws SQLException {
        String sqlTable = testCentral.GetArgumentValue(ts, 0, null);
        String sqlField = testCentral.GetArgumentValue(ts, 1, null);
        String whereClause = testCentral.GetArgumentValue(ts, 2, null);
        String sqlStatement = sqlTable.toLowerCase().contains("select") ? sqlTable : null;
        String actual = null;
        String comparisonType = CheckComparisonOperator(testCentral.GetArgumentValue(ts, ts.ArgumentList.size()-1, "="));

        if (sqlTable.toLowerCase().startsWith("where ") || (sqlField != null &&  sqlField.toLowerCase().contains("where "))) {
            testCentral.ArgumentOrderErrorMessage(ts, "sql server query");
            return;
        }

        if (testCentral.sqlConnection == null) {
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "Failed to find active Sql Server connection to the SQL Server for step " + fileStepIndex + AppConstants.ANSI_RESET, true);
            testCentral.conditionalSuccessful = false;
        }

        Statement statement = testCentral.sqlConnection.createStatement();
        ResultSet resultSet;

        try {
            if (sqlStatement == null || sqlStatement.isEmpty()) {
                sqlStatement = "Select " + sqlField + " from " + sqlTable + " " + whereClause;
            }

            testHelper.UpdateTestResults(AppConstants.indent5 + "Executing Sql Statement: " + sqlStatement, true);

            resultSet = statement.executeQuery(sqlStatement);
            if (resultSet != null) {
                resultSet.next();
                actual = resultSet.getString(1);
            }

            if (ts.get_crucial()) {
                if ("=".equals(comparisonType)) {
                    assertEquals(ts.get_expectedValue(), actual);
                } else {
                    assertNotEquals(ts.get_expectedValue(), actual);
                }
            } else {
                if ("=".equals(comparisonType)) {
                    if (ts.get_expectedValue() != null && actual != null && ts.get_expectedValue().trim().equals(actual.trim())) {
                        testHelper.UpdateTestResults("Successful Sql Query.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actual + ") for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
                    } else {
                        testHelper.UpdateTestResults("Failed Sql Server.  Expected: (" + ts.get_expectedValue() + ") Actual: (" + actual + ") for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = false;
                    }
                } else {
                    if (ts.get_expectedValue() != null && actual != null && !ts.get_expectedValue().trim().equals(actual.trim())) {
                        testHelper.UpdateTestResults("Successful Sql Server Query.  Expected: (" + ts.get_expectedValue() + ") != Actual: (" + actual + ") for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = (ts.get_isConditionalBlock() != null && ts.get_isConditionalBlock()) ? true : testCentral.conditionalSuccessful;
                    } else {
                        testHelper.UpdateTestResults("Failed Sql Server Query.  Expected: (" + ts.get_expectedValue() + ") != Actual: (" + actual + ") for step " + fileStepIndex, true);
                        testCentral.conditionalSuccessful = false;
                    }
                }
            }
        } catch(SQLException e) {
            testHelper.UpdateTestResults("Failed to execute query successfully.\r\n Error: " + e.getMessage() + " for step " + fileStepIndex, true);
            testCentral.conditionalSuccessful = false;
        }
    }
    //endregion




    //region {Partially implemented MongoDb connectivity }
    /***************************************************************************
     * DESCRIPTION: Method under development!!!
     *              Eventually this method will run single value
     *              MongoDB queries just like the corresponding SQL Server
     *              method.
     *              DO NOT REMOVE ANY CODE FROM THIS METHOD UNTIL IT HAS
     *              BEEN FULLY FLESHED OUT AS THE COMMENTED CODE MAY
     *              PROVE USEFUL!!!!!
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***************************************************************************/
    private void RunMongoQuery(TestStep ts, String fileStepIndex) {
        testHelper.UpdateTestResults("In RunMongoQuery method", false);
        testHelper.UpdateTestResults("RunMongoQuery....in first If Statement", false);
        String queryDataBase = testCentral.GetArgumentValue(ts, 0, null);
        String queryTable = testCentral.GetArgumentValue(ts, 1, null);
        String queryField = testCentral.GetArgumentValue(ts, 2, null);
        String whereClause = testCentral.GetArgumentValue(ts, 3, null);
        String objectElement = whereClause != null && whereClause.isEmpty() && whereClause.toLowerCase().contains("where") ? null : whereClause;
        if (objectElement != null) {
            whereClause = null;
        }

        if (queryDataBase == null || queryTable == null || queryField == null) {
            String errorMissingStructure = queryDataBase == null ? "Database" : queryTable == null ? "Table" : "Field";
            testHelper.UpdateTestResults(AppConstants.ANSI_RED + "ERROR: Invalid Query Command, " + errorMissingStructure +
                    " is missing! Aborting Test Step " + fileStepIndex, true);
            return;
        }

        testHelper.UpdateTestResults("RunMongoQuery: queryDataBase = " + queryDataBase, false);

        if (whereClause != null && !whereClause.isEmpty()) {
            testHelper.UpdateTestResults("RunMongoQuery: queryTable.toLowerCase().trim() = " + queryTable.toLowerCase().trim(), false);
            testHelper.UpdateTestResults("RunMongoQuery: queryField.toLowerCase() = " + queryField.toLowerCase(), false);

            testHelper.UpdateTestResults("whereClause = " + whereClause, false);
            MongoDatabase db = testCentral.mongoClient.getDatabase(queryDataBase);
            MongoCollection<Document> col = db.getCollection(queryTable);

            List<Document> documents = (List<Document>) col.find().into(
                    new ArrayList<Document>());

            if (documents.size() > 0) {
                for (Document document : documents) {
                    testHelper.UpdateTestResults("document = " + document, false);
                }
            } else {
                testHelper.UpdateTestResults("No matching items found", false);
            }
            //region {Commented for now}
                        /*
                        BasicDBObject whereQuery = new BasicDBObject();
                        pageHelper.UpdateTestResults("queryParameters[4] = " + queryParameters[4]);
                        pageHelper.UpdateTestResults("queryParameters[5] = " + queryParameters[5].replace(",", ""));
                        if (!queryParameters[6].contains("\"")) {
                            whereQuery.put(queryParameters[4].replace("\"", "") + " " + queryParameters[5].replace(",", "").replace("\"", ""), parseInt(queryParameters[6]));
                        } else {
                            whereQuery.put(queryParameters[4].replace("\"", "") + " " + queryParameters[5].replace(",", "").replace("\"", ""), queryParameters[6]);
                        }
                        FindIterable<Document> iterableString = col.find(whereQuery);
                        pageHelper.UpdateTestResults("iterableString = " + iterableString);
                        if (iterableString != null) {
                            for (Document item : iterableString) {
                                pageHelper.UpdateTestResults("item = " + item);
                            }
                        }
                        else {
                            pageHelper.UpdateTestResults("No matching items found");
                        }*/
            //endregion
        } else {  //get the entire table of data if no where clause exists
            MongoDatabase db = testCentral.mongoClient.getDatabase(queryDataBase);
            MongoCollection<Document> col = db.getCollection(queryTable);
            //List<Document> documents;
            FindIterable<Document> documents = null;
            Document doc = null;
            //if (queryParameters.length > 2) {
            if (queryField != null) {

                //region {commented code block 2}
//                    documents = (List<Document>) col.find("{" + queryParameters[3].toString() + ":" + queryParameters[4].toString() + "}").into(
//                            new ArrayList<Document>());
//                    documents = db.getCollection(queryParameters[2]).find("{ " +  queryParameters[3].toString() + ":" + queryParameters[4].toString() + " }"));
                //endregion
                BSONObject bsonObj = BasicDBObject.parse("{" + queryField.toString() + ":" + objectElement + "}");
//                    documents = db.getCollection(queryParameters[2]).find(((BasicDBObject) bsonObj)).first();
                doc = db.getCollection(queryTable).find(((BasicDBObject) bsonObj)).first();

                //NOTE: { Everything remaining in this if statement is for formatting and not necessary for the testing application }
                //code used below for troubleshooting not necessarily testing
                testHelper.UpdateTestResults("Doc = " + doc.toString(), false);
                //region { commented code block 3}
//                    pageHelper.UpdateTestResults("Doc = " + doc.toString()
//                            .replace("{{","\r\n" + pageHelper.indent5 + "{{\r\n " + pageHelper.indent5)
//                            .replace("}},","\r\n" + pageHelper.indent5 + "}},\r\n")
//                            .replace(",",",\r\n" + pageHelper.indent5));
                //endregion
                String[] docString = doc.toString().split(", ");
                int indent = 0;
                int padSize = 2;
                String tempItem = "";
                String tempItem2 = "";
                for (String item : docString) {
                    tempItem = "";
                    tempItem2 = "";
                    testHelper.UpdateTestResults("[indent set to: " + indent + "]", false);
                    //pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.trim() + " - (Unformatted)");
                    if ((item.contains("{{") || item.contains("[")) && !item.contains("[]")) {

                        while (item.indexOf("{{") > 0 || item.indexOf("[") > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("{{") + 2).trim().replace("{{", "\r\n" + testHelper.PadIndent(padSize, indent) + "{{\r\n");
                            testHelper.UpdateTestResults("tempItem = " + tempItem, false);
                            item = item.substring(item.indexOf("{{") + 2).trim();
                            testHelper.UpdateTestResults("item = " + item, false);
                            indent++;
                            testHelper.UpdateTestResults("[indent now set to: " + indent + "]", false);
                        }
                        if (item.length() > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item.trim();
                        }


                        testHelper.UpdateTestResults(AppConstants.ANSI_YELLOW + tempItem + AppConstants.ANSI_RESET, false);
                        //region { commented code block 4 }
//                            while (tempItem.indexOf("[") > 0) {
//                                tempItem2 += pageHelper.PadIndent(padSize, indent) + tempItem.substring(0, tempItem.indexOf("[") + 1).trim().replace("[", "\r\n" + pageHelper.PadIndent(padSize, indent) + "[\r\n");
//                                tempItem = tempItem.substring(tempItem.indexOf("[") + 1).trim();
//                                indent++;
//                            }
//                            if (tempItem.length() > 0) {
//                                tempItem2 += pageHelper.PadIndent(padSize, indent) + tempItem.trim();
//                            }
//
//
//
//                            pageHelper.UpdateTestResults(pageHelper.ANSI_BLUE + tempItem2 + pageHelper.ANSI_RESET);
//                            pageHelper.UpdateTestResults(color + tempItem + pageHelper.ANSI_RESET);
                        //endregion
                    } else if ((item.contains("}}") || item.contains("]")) && !item.contains("[]")) {
                        while (item.indexOf("}}") > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("}}") + 2).replace("}}", "\r\n" + testHelper.PadIndent(padSize, indent - 1) + "}}");
//                                tempItem += pageHelper.PadIndent(padSize, indent) + item.substring(0, item.indexOf("}}") + 2).replace("}}", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "}}\r\n");
                            item = item.substring(item.indexOf("}}") + 2).trim();
                            indent--;
                        }
                        if (item.length() > 0) {
                            tempItem += testHelper.PadIndent(padSize, indent) + item;
//                                tempItem += pageHelper.PadIndent(padSize, indent) +  item + " - (also left over)";
                        }
//                            if (tempItem.contains("]")) {
//                                indent--;
//                            }
                        testHelper.UpdateTestResults(tempItem, false);
                        //pageHelper.UpdateTestResults(item);
                        //indent--;
                    } else {
                        testHelper.UpdateTestResults(testHelper.PadIndent(padSize, indent) + item.trim() + " - (No delimiters)", false);
                    }
                    //region { commented code block 5 }
                       /*
                        if (item.contains("{{") || item.contains("[")) {
                            if (item.contains("{{")) {
                                if (item.contains("=") && (item.indexOf("=") < item.indexOf("{{"))) {
                                    pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("{{", pageHelper.PadIndent(padSize, indent)) + "\r\n" + pageHelper.PadIndent(padSize, indent) + "{{ ");
                                } else {
                                    pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("{{", pageHelper.PadIndent(padSize, indent)) + "{{\r\n " + pageHelper.PadIndent(padSize, indent + 1));
                                    //pageHelper.UpdateTestResults(item.replace("{{", "\r\n" + pageHelper.PadIndent(4, indent)) + "{{\r\n " + pageHelper.PadIndent(4, indent + 1));
                                }
                            }
                            if (item.contains("["))  {
                                if (item.contains("]")) {
                                    String temp = pageHelper.PadIndent(padSize, indent) + item.replace("[", "\r\n" + pageHelper.PadIndent(padSize, indent) + "[\r\n" + pageHelper.PadIndent(padSize, indent + 1));
                                    temp += pageHelper.PadIndent(padSize, indent) + temp.replace("]", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "]\r\n");

                                } else {
                                    pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("[", "\r\n" + pageHelper.PadIndent(padSize, indent) + "[\r\n" + pageHelper.PadIndent(padSize, indent + 1)));
                                }
                            }
//                            pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                            indent++;
                        } else if (item.contains("}}") || item.contains("]")) {
                            if (item.contains("}}")) {
                                pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("}}", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "}}") + ",");
                            }
                            if (item.contains("]")) {
                                pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item.replace("]", "\r\n" + pageHelper.PadIndent(padSize, indent - 1) + "]\r\n"));
                            }
//                            pageHelper.UpdateTestResults(item.replace("}}", pageHelper.PadIndent(4, indent) + "\r\n" + pageHelper.PadIndent(4, indent - 1) + "}}") + ",");
//                            pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                            indent--;
                        } else {
                            pageHelper.UpdateTestResults(pageHelper.PadIndent(padSize, indent) + item  + ",");
//                            pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                        } */
                    // pageHelper.UpdateTestResults("--[ Indent = " + indent + "]------");
                    //endregion
                }
            } else {
                List<Document> documents2;
                documents2 = (List<Document>) col.find().into(
                        new ArrayList<Document>());
            }
            //region { commented code block 6 }
                /*
                if (documents != null) {
                    for (Document document : documents) {
//                        pageHelper.UpdateTestResults("document = " + document.replace(",", ",\r\n"));
                        pageHelper.UpdateTestResults("document = " + document.toString().replace(",",",\r\n"));
                    }
                }
                else {
                    pageHelper.UpdateTestResults("No matching items found");
                } */
            //endregion
        }
    }


    /***********************************************************************
     * DESCRIPTION: Method under development!!!
     *              Creates a new MongoDb Client Connection or closes
     *              an open connection.
     *
     * IMPORTANT: Once able to successfully connect to and query the
     *              database, figure out what is worth logging but for now
     *              do not log anything except to the screen.
     *
     *              DO NOT REMOVE ANY CODE FROM THIS METHOD UNTIL IT HAS
     *              BEEN FULLY FLESHED OUT AS THE COMMENTED CODE MAY
     *              PROVE USEFUL!!!!!
     *
     * @param ts - Test Step Object containing all related information
     *           for the particular test step.
     * @param fileStepIndex - the file index and the step index.
     ***********************************************************************/
    void SetMongoClient(TestStep ts, String fileStepIndex) {
        //determine the type of mongo connection that needs to be used
        String connectionType = testCentral.GetArgumentValue(ts, 3, null);
        String connectionString = testCentral.GetArgumentValue(ts, 2, null);

        if (connectionType.toLowerCase().trim().equals("uri") && !connectionString.toLowerCase().contains("close")) {
            testCentral.mongoClient = new MongoClient(new MongoClientURI(connectionString));
        } else if (!connectionString.toLowerCase().contains("close")) {
            //local connection?
            testCentral.mongoClient = new MongoClient(connectionString);
        } else {
            testCentral.mongoClient.close();  //close the connection
        }

        MongoCursor<String> dbsCursor = testCentral.mongoClient.listDatabaseNames().iterator();
        while (dbsCursor.hasNext()) {
            try {
                testHelper.UpdateTestResults(dbsCursor.next(), false);
                MongoDatabase db = testCentral.mongoClient.getDatabase(dbsCursor.next());

                testHelper.UpdateTestResults("--[Tables - Start]----", false);
                MongoIterable<String> col = db.listCollectionNames();


                for (String table : col) {
                    testHelper.UpdateTestResults(AppConstants.indent5 + "Table = " + table, false);
                    FindIterable<Document> fields = db.getCollection(table).find();
                    testHelper.UpdateTestResults(AppConstants.indent5 + "--[Fields - Start]----", false);
                    /*
                    try {
                        int maxRecords = 1;
                        int recordCount = 0;
                        if (dbsCursor.next().equals("project-tracker-dev")) {
                            pageHelper.UpdateTestResults("db." + table + ".find() = " + db.getCollection(table).find());
                            for (Document field : fields) {
                                pageHelper.UpdateTestResults(pageHelper.indent8 + "Field = " + field.toString().replace("Document{{id=", "\r\nDocument{{id="));
//                                recordCount++;
//                                if (recordCount > maxRecords) {
                                    break;
//                                }
                            }
                        }
                    } catch (MongoQueryException qx) {
                        pageHelper.UpdateTestResults("Field Retrieval MongoDb error occurred: " + qx.getErrorMessage());
                    } catch (Exception ex) {
                        pageHelper.UpdateTestResults("Field Retrieval error occurred: " + ex.getMessage());
                    }*/
                    testHelper.UpdateTestResults(AppConstants.indent5 + "--[Fields - End]----", false);
                }

                testHelper.UpdateTestResults("--[Tables - End]----", false);
                //col.forEach(String table : col)
                testHelper.UpdateTestResults("", false);
            } catch(Exception ex) {
                testHelper.UpdateTestResults("MongoDB error occurred: " + ex.getMessage(), false);
            }
        }
    }
    //endregion

}
