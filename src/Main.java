import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

  public static void main(String[] args){
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Price Checker");
    try {
      Parent root = FXMLLoader.load(this.getClass().getResource("/fxml/AppScene.fxml"));
      primaryStage.setScene(new Scene(root, 800, 600));
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
