import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;


public class Main {


    private String _webAddress;

    public String getWebAddress() {
        return this._webAddress;
    }

    public void setWebAddress(String _webAddress) {
        this._webAddress = _webAddress;
    }

    public static void main(String[] args) throws InterruptedException {
        // write your code here

        System.setProperty("webdriver.chrome.driver", "/Users/gjackson/Downloads/chromedriver_win32/chromedriver.exe");

        // Create new instance of ChromeDriver
        WebDriver driver = new ChromeDriver();

        navigate(driver, "https://www.davita.com/");
        waitForPageLoad(5000);

        //boolean status = clickElement(driver, "", "", "", "", "http://blogs.davita.com/kidney-diet-tips/");
        //boolean status = hoverAndClickChildElementByHrefLinkTexts(driver,
        //        "/diet-nutrition", "http://blogs.davita.com/kidney-diet-tips/");

        driver.findElement(By.cssSelector("a[href='/diet-nutrition']")).click();
        waitForPageLoad(5000);


        // Add expected result values and xPath Selectors to equal length arrays so that the
        // xPath elements can be retrieved and compared with the expected values
        String[] expectedResults = { "Diet & Nutrition", "Download Cookbooks", "Kidney-Friendly Recipes", "<h3 class=\"dv-action-tile__title\">DaVita Diet Helper<sup><small>TM</small></sup></h3>", "Kidney Diet Tips Blog"};

        String[] xpathSelectors = {"//*[@id=\"content\"]/div[1]/div/ul/li/div/div/h1", "//*[@id=\"content\"]/div[4]/div[1]/div[1]/div/h3",
                "//*[@id=\"content\"]/div[4]/div[2]/div[1]/div/h3", "//*[@id=\"content\"]/div[6]/div[1]/div[1]/div/h3",
                "//*[@id=\"content\"]/div[6]/div[2]/div[1]/div/h3"};


        if (expectedResults.length == xpathSelectors.length) {
            for (int x=0;x<expectedResults.length;x++) {
                String expected = expectedResults[x];
                String actual = !expected.contains("<") ? driver.findElement(By.xpath(xpathSelectors[x])).getText() :
                        driver.findElement(By.xpath(xpathSelectors[x])).getAttribute("outerHTML");
                assertEquals(expected, actual);
            }
        }




        //assertEquals(expected, actual);

        driver.quit();
    }

    public static void waitForPageLoad(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    public static void navigate(WebDriver driver, String webAddress) {
        //https://www.davita.com/

        if ((webAddress != null) && !webAddress.isEmpty()) {
            driver.get(webAddress);
        } else {
            driver.get("https://www.yahoo.com/");
        }

    }

    /*
      Clicks an element based first on id, then name, class, cssSelector then hrefLinkText
     */
    public static boolean clickElement(WebDriver driver, String idText, String nameText, String className, String cssSelector, String hrefLinkText) {
        //driver.findElement(By.cssSelector("a[href*='long']")).click();
        boolean status = true;
        WebDriverWait wait = new WebDriverWait(driver, 10);

        if (idText != null && !idText.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(idText)));
            driver.findElement(By.id(idText)).click();
        } else if (nameText != null && !nameText.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(nameText)));
            driver.findElement(By.name(nameText)).click();
        } else if (className != null && !className.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(className)));
            driver.findElement(By.className(className)).click();
        } else if (cssSelector != null && !cssSelector.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
            driver.findElement(By.cssSelector(cssSelector)).click();
        } else if (hrefLinkText != null && !hrefLinkText.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='" + hrefLinkText + "']")));
            driver.findElement(By.cssSelector("a[href='" + hrefLinkText + "']")).click();
        } else {
            status = false;
        }
        return status;
    }

    //public static boolean hoverAndClickChildElementByIds(WebDriver driver, String idText, String nameText, String className, String cssSelector, String hrefLinkText) {
    public static boolean hoverAndClickChildElementByIds(WebDriver driver, String parentIdText, String childIdText) {
        //driver.findElement(By.cssSelector("a[href*='long']")).click();
        boolean status = true;
        WebDriverWait wait = new WebDriverWait(driver, 10);

        Actions action = new Actions(driver);
        //WebElement we = driver.findElement(By.xpath("html/body/div[13]/ul/li[4]/a"));


        if (parentIdText != null && !parentIdText.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(parentIdText)));
            WebElement we = driver.findElement(By.id(parentIdText));
            action.moveToElement(we).moveToElement(driver.findElement(By.id(childIdText))).click().build().perform();
        } else {
            status = false;
        }

        return status;
    }

    public static boolean hoverAndClickChildElementByNames(WebDriver driver, String parentName, String childName) {
        boolean status = true;
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Actions action = new Actions(driver);

        if (parentName != null && !parentName.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(parentName)));
            WebElement we = driver.findElement(By.name(parentName));
            action.moveToElement(we).moveToElement(driver.findElement(By.name(childName))).click().build().perform();
            //driver.findElement(By.name(nameText)).click();
        } else {
            status = false;
        }
        return status;
    }

    public static boolean hoverAndClickChildElementByClassNames(WebDriver driver, String parentClassName, String childClassName) {
        boolean status = true;
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Actions action = new Actions(driver);

        if (parentClassName != null && !parentClassName.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(parentClassName)));
            WebElement we = driver.findElement(By.className(parentClassName));
            action.moveToElement(we).moveToElement(driver.findElement(By.className(childClassName))).click().build().perform();
            //driver.findElement(By.className(className)).click();
        } else {
            status = false;
        }
        return status;
    }

    public static boolean hoverAndClickChildElementByCssSelectors(WebDriver driver, String parentCssSelector, String childCssSelector) {
        boolean status = true;
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Actions action = new Actions(driver);

        if (parentCssSelector != null && !parentCssSelector.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(parentCssSelector)));
            WebElement we = driver.findElement(By.cssSelector(parentCssSelector));
            action.moveToElement(we).moveToElement(driver.findElement(By.cssSelector(childCssSelector))).click().build().perform();
            //driver.findElement(By.cssSelector(cssSelector)).click();
        } else {
            status = false;
        }
        return status;
    }

    public static boolean hoverAndClickChildElementByHrefLinkTexts(WebDriver driver, String parentHrefLinkText, String childHrefLinkText) {
        boolean status = true;
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Actions action = new Actions(driver);

        if (parentHrefLinkText != null && !parentHrefLinkText.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='" + parentHrefLinkText + "']")));
            WebElement we = driver.findElement(By.cssSelector(parentHrefLinkText));
            action.moveToElement(we).moveToElement(driver.findElement(By.cssSelector("a[href='" + childHrefLinkText + "']"))).click().build().perform();
            //driver.findElement(By.cssSelector("a[href='" + hrefLinkText + "']")).click();
        }
        else {
            status = false;
        }
        return status;
    }
}
