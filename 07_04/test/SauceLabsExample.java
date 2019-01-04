import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

public class SauceLabsExample {

    public static final String USERNAME = "YOUR_USERNAME";
    public static final String ACCESS_KEY = "YOUR_ACCESS_KEY";
    public static final String URL = "http://" + USERNAME + ":" + ACCESS_KEY + "@ondemand.saucelabs.com:80/wd/hub";

    public static void main(String[] args) throws Exception {

        DesiredCapabilities caps = DesiredCapabilities.chrome();
        caps.setCapability("platform", "macOS 10.12");
        caps.setCapability("version", "66.0");

        WebDriver driver = new RemoteWebDriver(new URL(URL), caps);

        driver.get("http://formy-project.herokuapp.com/");
    }
}