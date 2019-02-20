public class Form {

    public static void main(String[] args) throws Exception {
        Form form = new Form();
        form.checkHomePage();
    }


    private void checkHomePage() throws Exception {
        //instantiate a new HomePage object and set the executedFromMain property to true
        //to allow for inputting a path to the correct config file if the config file
        //is not in the default location
        HomePage homePage = new HomePage();
        homePage.set_executedFromMain(true);
        homePage.TestHomePage();
    }


}
