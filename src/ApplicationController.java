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
  private TextField searchBar;
  @FXML
  private Label valueLabel;
  @FXML
  private ImageView webview;

  private String pageSource = "";

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

    /* // Screenshot
    try {
      File src = driver.getScreenshotAs(OutputType.FILE);
      webview.setImage(SwingFXUtils.toFXImage(ImageIO.read(src), null));
    } catch (IOException e) {
      e.printStackTrace();
    }
    */

  }

  private void evaluate() {
    int total = 0;
    int count = 0;
    boolean nosellers = false;
    boolean nobuyers = false;

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

//        System.out.println("This order is " + curOrderCount + " items, for " + curOrderPrice + " platinum each.");
      }
    } catch (NoSuchElementException e){
      System.out.println("There are no sell orders for this item.");
      nosellers = true;
    }

    // Find the buy orders
    try {
      WebElement buyTable = driver.findElementByCssSelector("div[id=buy]")
          .findElement(By.cssSelector("tbody[aria-live=polite]"));
      List<WebElement> buyOrders = buyTable.findElements(By.cssSelector("tr[role=row]"));
      System.out.println("There are " + buyOrders.size() + " buy orders.");
      for(int i = 0; i < buyOrders.size(); i++){
        WebElement curOrder = buyOrders.get(i);
        List<WebElement> curOrderComponents = curOrder.findElements(By.cssSelector("td"));
        int curOrderPrice = Integer.parseInt(curOrderComponents.get(1).getText());
        int curOrderCount = Integer.parseInt(curOrderComponents.get(2).getText());
//        System.out.println("This order is " + curOrderCount + " items, for " + curOrderPrice + " platinum each.");
      }
    } catch (NoSuchElementException e){
      System.out.println("There are no buy orders for this item.");
      nobuyers = true;
    }
    //print average price
    if (nobuyers && nosellers){
      valueLabel.setText("Could not find item.");
    }
    else {
      valueLabel.setText("Sell Value: ~" + (count > 0 ? ((int) total / count) : 0) + " platinum");
    }
  }

  private void parsePage(){
    String sellTableString = "<div role=\"tabpanel\" class=\"tab-pane fade in active\" id=\"sell\">";
    int sellTableIndex = pageSource.indexOf(sellTableString);
    String buyTableString = "<div role=\"tabpanel\" class=\"tab-pane fade\" id=\"buy\">";
    int buyTableIndex = pageSource.indexOf(buyTableString);
    String endBuyTable = "::after";
    int endBuyTableIndex = pageSource.indexOf(endBuyTable, buyTableIndex);
    String sellTable = pageSource.substring(sellTableIndex, buyTableIndex);
    String ordersExistString = "<tbody aria-live=\"polite\" aria-relevant=\"all\">";
    if(sellTable.contains(ordersExistString)){
      String rowString = "role=\"row\"";
      // Gets rid of the first junk element that we don't need
      int prevIndex = sellTable.indexOf(rowString);
      sellTable = sellTable.substring(prevIndex + 1);

      System.out.println(sellTable);

      // Iterate through the orders
      while(sellTable.contains(rowString)){
        prevIndex = sellTable.indexOf(rowString);
        sellTable = sellTable.substring(prevIndex + 1);
        // Should now be at the start of an order

        String orderElementString = "<td>";
        prevIndex = sellTable.indexOf(orderElementString);
        sellTable = sellTable.substring(prevIndex + 1);
        // Should now be just before the name field of an order

        prevIndex = sellTable.indexOf(orderElementString);
        sellTable = sellTable.substring(prevIndex + 1);
        // Should be at the price field of an order
        System.out.println(sellTable.substring(prevIndex + 4));
        int price = Integer.parseInt(sellTable.substring(prevIndex + 4, sellTable.indexOf("</td>")));


        prevIndex = sellTable.indexOf(orderElementString);
        sellTable = sellTable.substring(prevIndex + 1);
        // Should be at the quantity field of an order
        int count = Integer.parseInt(sellTable.substring(prevIndex + 4, sellTable.indexOf("</td>")));
        System.out.println("The price of this item is: " + price + " platinum for each of " + count + " items.");
      };

    }else{
      // There are no sell orders
    }
    String buyTable = pageSource.substring(buyTableIndex, endBuyTableIndex);
    if(buyTable.contains(ordersExistString)){

    }else{
      // There are no buy orders
    }
    System.out.println(sellTable);
  }

  @FXML
  private void searchButtonClicked() {
    searchMarket(searchBar.getText());
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    pageSource = driver.getPageSource();
    parsePage();
//    evaluate();
  }
}
