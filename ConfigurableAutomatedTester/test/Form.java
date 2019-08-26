public class Form {

    public static void main(String[] args) throws Exception {
        Form form = new Form();
        //form.checkHomePage();
        form.RunTestCentral();
    }

    /***************************************************************
     *  Description: Standalone start for the XML configuration
     *              file types. (New)
     * @throws Exception
     ***************************************************************/
    private void RunTestCentral() throws Exception {
        TestCentral testCentral = new TestCentral();
        testCentral.set_executedFromMain(true);
        //testCentral.TestCentralStart();
        testCentral.ConfigurableTestController();

    }

    /*************************************************************
     * Description: Standalone start for the parameterized file types
     *          that use ANSI delimiters and assignment statements.
     *          (Old) - to use this, update main to call this method.
     * @throws Exception
     *************************************************************/
    private void checkHomePage() throws Exception {
        //instantiate a new HomePage object and set the executedFromMain property to true
        //to allow for inputting a path to the correct config file if the config file
        //is not in the default location
        HomePage homePage = new HomePage();
//        homePage.StupidUtility();
        homePage.set_executedFromMain(true);
        homePage.TestHomePage();
    }


}
