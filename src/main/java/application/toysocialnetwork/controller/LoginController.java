package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.SocialNetworkApplication;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.exceptions.NotFoundException;
import application.toysocialnetwork.service.UserService;
import application.toysocialnetwork.utils.events.AlertEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    private SocialNetworkApplicationContext applicationContext;
    private UserService userService;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginStatusLabel;

    public void initController(SocialNetworkApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setUserService(applicationContext.getUserService());
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void handleLogIn(ActionEvent actionEvent) {
        if(emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            setLoginStatus("Please provide the Login Credentials!");
        }

        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            User loggedUser = userService.findByLoginCredentials(email, password);
            initProfileView(loggedUser);
        }
        catch (NotFoundException notFoundException) {
            setLoginStatus(notFoundException.getMessage());
        }

    }

    private void setLoginStatus(String message) {
        loginStatusLabel.setTextFill(Color.RED);
        loginStatusLabel.setText(message);
    }

    private void initProfileView(User loggedUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.
                    getResource("views/profile-view.fxml"));
            Stage dialogStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            dialogStage.setTitle("Profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            ProfileController profileController = fxmlLoader.getController();
            profileController.initController(loggedUser, applicationContext);
            dialogStage.show();
        }
        catch (IOException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Login failure",
                    exception.getMessage());
        }
    }

    public void handleReturnHome(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SocialNetworkApplication.class.
                    getResource("views/home-view.fxml")));
            AnchorPane pane = fxmlLoader.load();
            HomeController homeController = fxmlLoader.getController();
            homeController.initController(applicationContext);
            rootPane.getChildren().setAll(pane);

        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
