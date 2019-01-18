import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.Assert.assertEquals;


public class Form {

    public static String homePageRoot = "https://www.davita.com/";

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        //driver.get("https://www.davita.com");
        driver.get(homePageRoot);

        //checkHomePage(driver, homePageRoot);



        /*FormPage formPage = new FormPage();
        formPage.submitForm(driver);

        ConfirmationPage confirmationPage = new ConfirmationPage();
        confirmationPage.waitForAlertBanner(driver);

        assertEquals("The form was successfully submitted!", confirmationPage.getAlertBannerText(driver));
*/
        driver.quit();
    }

    private static void checkHomePage(WebDriver driver, String homePageRoot) {

        /*HomePage homePage = new HomePage();
        homePage.CheckPageUrl(driver, homePageRoot);
        homePage.CheckHeading(driver, "Empower Yourself with Kidney Knowledge");*/

    }


}
