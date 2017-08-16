import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.xml.xpath.XPath;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;


/**
 * Created by John on 8/13/2017.
 */
public class ApplicationController implements Initializable {

  private PhantomJSDriver driver;
  private WebElement searchElement;
  private WebElement buttonElement;

  @FXML
  private Button searchButton;
  @FXML
  private TextField searchBar;
  @FXML
  private Label valueLabel;

  //  @Override
//  public void initialize(URL location, ResourceBundle resources) {
//    System.setProperty("webdriver.chrome.driver",
//        "C:\\Users\\John\\IdeaProjects\\Warframe Price Checker\\out\\production\\Warframe Price Checker\\drivers\\chromedriver.exe");
//
//    driver = new ChromeDriver();
//    driver.get("https://warframe.market");
//
//    searchElement = driver.findElement(By.xpath("//input[@id='search-item']"));
//    buttonElement = driver.findElement(By.xpath(
//        "/html[@class=' js flexbox canvas canvastext webgl no-touch geolocation postmessage websqldatabase indexeddb hashchange history draganddrop websockets rgba hsla multiplebgs backgroundsize borderimage borderradius boxshadow textshadow opacity cssanimations csscolumns cssgradients cssreflections csstransforms csstransforms3d csstransitions fontface no-generatedcontent video audio localstorage sessionstorage webworkers applicationcache svg inlinesvg smil svgclippaths']/body[@class='site-warframe']/div[@class='wrapper']/div[@class='container w-wrapper']/div[@class='row'][3]/div[@class='col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2 inner-blocks']/div[@class='search-item input-group']/span[@class='input-group-btn']/button[@class='btn btn-default']"));
//  }
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Capabilities caps = new DesiredCapabilities();
    ((DesiredCapabilities) caps).setJavascriptEnabled(true);
    ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
    ((DesiredCapabilities) caps)
        .setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            "resources/drivers/phantomjs.exe");

    driver = new PhantomJSDriver(caps);
    driver.get("https://warframe.market");

    searchElement = driver.findElement(By.xpath("//input[@id='search-item']"));
    buttonElement = driver.findElement(By.cssSelector("span[class=input-group-btn]"));

  }

  private void searchMarket(String item) {
    searchElement.clear();
    searchElement.sendKeys(item);
    buttonElement.click();
  }

  private void evaluate() {
    int total = 0;
    int count = 0;

//    WebElement elmt = driver.findElement(By.cssSelector("tbody[aria-live=polite]"));
//    System.out.println("e" + elmt.getText());

    List<WebElement> elements = driver.findElements(By.cssSelector("tr[role=row]"));
    System.out.println(elements.size());
    for (int i = 0; i < elements.size(); i++) {
      String pricePath =
          "/html[@class=' js flexbox canvas canvastext webgl no-touch geolocation postmessage websqldatabase indexeddb hashchange history draganddrop websockets rgba hsla multiplebgs backgroundsize borderimage borderradius boxshadow textshadow opacity cssanimations csscolumns cssgradients cssreflections csstransforms csstransforms3d csstransitions fontface no-generatedcontent video audio localstorage sessionstorage webworkers applicationcache svg inlinesvg smil svgclippaths']/body[@class='site-warframe']/div[@class='wrapper']/div[@class='container w-wrapper']/div[@class='row ']/div[@class='col-sm-12 inner-blocks']/div[@id='order-cont']/div[@id='sell']/div[@class='order-table']/table[@id='sell-table']/tbody/tr["
              + (i + 1) + "]/td[2]";
      String numPath =
          "/html[@class=' js flexbox canvas canvastext webgl no-touch geolocation postmessage websqldatabase indexeddb hashchange history draganddrop websockets rgba hsla multiplebgs backgroundsize borderimage borderradius boxshadow textshadow opacity cssanimations csscolumns cssgradients cssreflections csstransforms csstransforms3d csstransitions fontface no-generatedcontent video audio localstorage sessionstorage webworkers applicationcache svg inlinesvg smil svgclippaths']/body[@class='site-warframe']/div[@class='wrapper']/div[@class='container w-wrapper']/div[@class='row ']/div[@class='col-sm-12 inner-blocks']/div[@id='order-cont']/div[@id='sell']/div[@class='order-table']/table[@id='sell-table']/tbody/tr["
              + (i + 1) + "]/td[3]";
      WebElement priceElement = driver.findElement(By.xpath(pricePath));
      WebElement numElement = driver.findElement(By.xpath(numPath));
      System.out.println(i);
      int price = Integer.parseInt(priceElement.getText());
      int num = Integer.parseInt(numElement.getText());
      total += price * num;
      count += num;
    }
    valueLabel.setText("Value: ~" + (count > 0 ? ((int) total / count) : 0) + " platinum");

    List<WebElement> els = driver.findElements(By.xpath("//*[@class]"));
    for(int i = 0; i < els.size(); i++){
      System.out.println(els.get(i).getText());
    }
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
