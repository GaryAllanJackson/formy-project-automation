import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class Dropdown {
    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("webdriver.chrome.driver", "/Users/meaghanlewis/Downloads/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");


        WebDriver driver = new ChromeDriver();

        driver.get("https://formy-project.herokuapp.com/dropdown");
        WebElement dropDown = driver.findElement(By.id("dropdownMenuButton"));
        dropDown.click();

        Thread.sleep(3000);

        WebElement autocompleteItem = driver.findElement(By.id("autocomplete"));
        autocompleteItem.click();


        Thread.sleep(3000);

        driver.quit();
    }
}
