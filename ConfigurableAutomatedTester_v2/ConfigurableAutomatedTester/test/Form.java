public class Form {

    public static void main(String[] args) throws Exception {
        Form form = new Form();
        form.RunTestCentral();
    }

    /***************************************************************
     *  Description: Standalone start for the XML configuration
     *              file types. (New)
     * @throws Exception
     ***************************************************************/
    private void RunTestCentral() throws Exception {
        System.out.println("In RunTestCentral");
        TestCentral testCentral = new TestCentral();
        testCentral.set_executedFromMain(true);
        //testCentral.TestCentralStart();
        testCentral.ConfigurableTestController();

    }
}
