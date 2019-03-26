import HomePageTest.HomePageTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DavitaTest {
    private String siteRootURL;

    public DavitaTest() {
        setSiteRootURL("https://www.davita.com");
    }

    public String getSiteRootURL() {
        return siteRootURL;
    }

    public void setSiteRootURL(String siteRootURL) {
        this.siteRootURL = siteRootURL;
    }

    //public static void main(String[] args) {
    public static void main(String[] args) {

        WebDriver driver = new ChromeDriver();
        HomePageTest homePageTest = new HomePageTest();
        homePageTest.setDriver(driver);

        homePageTest.navigateToSite(siteRootURL);



    }

}
