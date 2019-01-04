import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Form {
    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("webdriver.chrome.driver", "/Users/meaghanlewis/Downloads/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get("https://formy-project.herokuapp.com/form");
        //WebElement item = driver.findElement(By.id(""));
        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("job-title")).sendKeys("Lead Developer");
        driver.findElement(By.id("radio-button-2")).click();
        driver.findElement(By.id("checkbox-1")).click();
        driver.findElement(By.cssSelector("option[value='4']")).click();
        driver.findElement(By.id("datepicker")).sendKeys("01/4/2019");
        driver.findElement(By.id("datepicker")).sendKeys(Keys.RETURN);
        Thread.sleep(10000);
        driver.findElement(By.cssSelector(".btn.btn-lg.btn-primary")).click();
        Thread.sleep(3000);

        driver.quit();
    }
}
