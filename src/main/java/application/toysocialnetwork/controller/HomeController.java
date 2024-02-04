package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.SocialNetworkApplication;
import application.toysocialnetwork.service.CommunityService;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HomeController {
    private SocialNetworkApplicationContext applicationContext;
    @FXML
    private AnchorPane rootPane;

    public void initController(SocialNetworkApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void initAdminWindow() {
       try {
           FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource
                   ("views/admin-view.fxml"));

           Stage dialogStage = new Stage();
           Scene scene = new Scene(fxmlLoader.load());
           dialogStage.setTitle("Admin mode");
           dialogStage.initModality(Modality.WINDOW_MODAL);
           dialogStage.setScene(scene);

           AdminController adminController = fxmlLoader.getController();
           adminController.initController(applicationContext);
           dialogStage.show();
       }
       catch (IOException exception) {
           exception.printStackTrace();
       }
    }

    public void handleLogIn() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SocialNetworkApplication.class.
                    getResource("views/login-view.fxml")));
            AnchorPane pane = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.initController(applicationContext);
            rootPane.getChildren().setAll(pane);

        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void handleSignUp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SocialNetworkApplication.class.
                    getResource("views/register-view.fxml")));
            AnchorPane pane = fxmlLoader.load();
            RegisterController registerController = fxmlLoader.getController();
            registerController.initController(applicationContext);
            rootPane.getChildren().setAll(pane);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void handleAdminMode(ActionEvent actionEvent) {
       initAdminWindow();
    }
}
