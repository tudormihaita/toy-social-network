package application.toysocialnetwork;

import application.toysocialnetwork.controller.HomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SocialNetworkApplication extends Application {
    private SocialNetworkApplicationContext applicationContext;

    public SocialNetworkApplication() {}

    @Override
    public void start(Stage stage) throws IOException {
        applicationContext = new SocialNetworkApplicationContext();
        initView(stage);
        stage.setTitle("ChatBot Social Network");
        stage.show();
    }

    public void initView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SocialNetworkApplication.class.getResource("views/home-view.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.getIcons().add(new Image(Objects.requireNonNull(SocialNetworkApplication.
                class.getResourceAsStream("images/chatbot-app-icon.png"))));
        HomeController homeController = fxmlLoader.getController();
        homeController.initController(applicationContext);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
