import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class ExecuteJavascript {
    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("webdriver.chrome.driver", "/Users/meaghanlewis/Downloads/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get("https://formy-project.herokuapp.com/modal");

        WebElement modalButton = driver.findElement(By.id("modal-button"));
        modalButton.click();

        Thread.sleep(3000);

        WebElement closeButton = driver.findElement(By.id("close-button"));
        //closeButton.click();   //this worked fine but the tutorial wanted to show the executeScript functionality below
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("arguments[0].click();", closeButton);

        //Async callback not working, need to figure this out
        //Object value = js.executeAsyncScript("window.setTimeout(arguments[0].click()), 500)", closeButton);



        Thread.sleep(3000);

        driver.quit();
    }
}
