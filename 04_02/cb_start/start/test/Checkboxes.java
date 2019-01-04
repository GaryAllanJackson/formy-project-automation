import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class Checkboxes {
    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("webdriver.chrome.driver", "/Users/meaghanlewis/Downloads/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get("https://formy-project.herokuapp.com/checkbox");

        WebElement checkbox1 = ((ChromeDriver) driver).findElementByCssSelector("input[type='checkbox']");
        checkbox1.click();

        Thread.sleep(3000);

        WebElement checkbox2 = ((ChromeDriver) driver).findElement(By.id("checkbox-2"));
        //WebElement checkbox2 = ((ChromeDriver) driver).findElementByCssSelector("#checkbox-2");
        checkbox2.click();

        Thread.sleep(3000);

        WebElement checkbox3 = ((ChromeDriver) driver).findElementByCssSelector("input[value='checkbox-3'");
        checkbox3.click();

        Thread.sleep(3000);
        driver.quit();
    }
}