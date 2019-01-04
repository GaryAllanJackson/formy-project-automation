import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;


public class FileUpload {
    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("webdriver.chrome.driver", "/Users/meaghanlewis/Downloads/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get("https://formy-project.herokuapp.com/fileupload");
        //file-upload-field
        WebElement fileUploadField = driver.findElement(By.id("file-upload-field"));
        fileUploadField.sendKeys("C:\\Gary\\Utilities_VS-2015\\ConvertImageToIcon\\images\\ImageToDB.png");

        Thread.sleep(3000);
        WebElement resetButton = ((ChromeDriver) driver).findElementByCssSelector("body > div > form > div > div > span:nth-child(3) > button");
        resetButton.click();
        Thread.sleep(3000);

        fileUploadField.sendKeys("file-to-upload.png");
        Thread.sleep(3000);

        driver.quit();
    }
}
