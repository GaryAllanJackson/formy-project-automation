import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class KeyboardAndMouseInput {
    public static void main(String[] args) {

        //System.setProperty("webdriver.chrome.driver", "/Users/meaghanlewis/Downloads/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");



        WebDriver driver = new ChromeDriver();

        driver.get("https://formy-project.herokuapp.com/keypress");

        //add lines to automate keypress page
        WebElement name = driver.findElement(By.id("name"));
        name.click();
        name.sendKeys("Gary Jackson");


        WebElement button = driver.findElement(By.id("button"));
        button.click();

        driver.quit();
    }
}
