import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;


/**
 * Created by John on 8/13/2017.
 */
public class ApplicationController implements Initializable {

  private ChromeDriver driver;
  private WebElement searchElement;
  private WebElement buttonElement;

  @FXML
  private Button searchButton;
  @FXML
  private TextField searchBar;
  @FXML
  private Label valueLabel;
  @FXML
  private ImageView webview;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

//    Capabilities caps = new DesiredCapabilities();
//    ((DesiredCapabilities) caps).setJavascriptEnabled(true);
//    ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
//    ((DesiredCapabilities) caps)
//        .setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
//            "resources/drivers/phantomjs.exe");

    System.setProperty("webdriver.chrome.driver","resources/drivers/chromedriver.exe");
    ChromeOptions chromeOptions = new ChromeOptions();
//    chromeOptions.addArguments("--headless");
//    chromeOptions.addArguments("--start-maximized");
    driver = new ChromeDriver(chromeOptions);

//    driver.manage().window().setSize(new Dimension(1920, 1080));
    driver.get("https://warframe.market");

    searchElement = driver.findElementByCssSelector("input[id=search-item]");
    buttonElement = driver.findElementByCssSelector("span[class=input-group-btn]");

  }

  private void searchMarket(String item) {
    searchElement.clear();
    searchElement.sendKeys(item);
    buttonElement.click();
    File src = driver.getScreenshotAs(OutputType.FILE);

    try {
      webview.setImage(SwingFXUtils.toFXImage(ImageIO.read(src), null));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void evaluate() {
    int total = 0;
    int count = 0;

    // Find the Sell orders
    try {
      WebElement sellTable = driver.findElementByCssSelector("div[id=sell]")
          .findElement(By.cssSelector("tbody[aria-live=polite]"));
      List<WebElement> sellOrders = sellTable.findElements(By.cssSelector("tr[role=row]"));
      System.out.println("There are " + sellOrders.size() + " sell orders.");
      for(int i = 0; i < sellOrders.size(); i++){
        WebElement curOrder = sellOrders.get(i);
        List<WebElement> curOrderComponents = curOrder.findElements(By.cssSelector("td"));
        int curOrderPrice = Integer.parseInt(curOrderComponents.get(1).getText());
        int curOrderCount = Integer.parseInt(curOrderComponents.get(2).getText());
        total += curOrderPrice*curOrderCount;
        count += curOrderCount;
//        System.out.println("This order is " + curOrderCount + " items, for " + curOrderPrice + " platinum each.");
      }
    } catch (NoSuchElementException e){
      System.out.println("There are no sell orders for this item.");
    }

    // Find the buy orders
    try {
      WebElement buyTable = driver.findElementByCssSelector("div[id=buy]")
          .findElement(By.cssSelector("tbody[aria-live=polite]"));
      List<WebElement> buyOrders = buyTable.findElements(By.cssSelector("tr[role=row]"));
      System.out.println("There are " + buyOrders.size() + " buy orders.");
    } catch (NoSuchElementException e){
      System.out.println("There are no buy orders for this item.");
    }

    valueLabel.setText("Sell Value: ~" + (count > 0 ? ((int) total / count) : 0) + " platinum");
  }

  @FXML
  private void searchButtonClicked() {
    searchMarket(searchBar.getText());
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    evaluate();
  }
}
