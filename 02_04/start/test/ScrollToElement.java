import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class ScrollToElement {
    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("webdriver.chrome.driver", "/Users/meaghanlewis/Downloads/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");


        WebDriver driver = new ChromeDriver();

        driver.get("https://formy-project.herokuapp.com/scroll");

        WebElement name = driver.findElement(By.id("name"));

        Actions actions = new Actions(driver);
        actions.moveToElement(name);
        name.sendKeys("Gary Jackson");

        WebElement date = driver.findElement(By.id("date"));
        date.sendKeys("01/01/2020");

        Thread.sleep(3000);


        driver.quit();
    }
}
