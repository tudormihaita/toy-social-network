package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplication;
import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.exceptions.NotFoundException;
import application.toysocialnetwork.exceptions.ValidationException;
import application.toysocialnetwork.service.UserService;
import application.toysocialnetwork.utils.events.AlertEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class RegisterController {

    private SocialNetworkApplicationContext applicationContext;
    private UserService userService;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private DatePicker birthDatePicker;

    public void initController(SocialNetworkApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setUserService(applicationContext.getUserService());
    }

    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void handleRegisterUser(ActionEvent actionEvent) {
        if(!firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty()
                && !emailField.getText().isEmpty() && !passwordField.getText().isEmpty() &&
        birthDatePicker.getValue() != null) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String emailAddress = emailField.getText();
            String password = passwordField.getText();
            LocalDate birthDate = birthDatePicker.getValue();

            try {
                userService.addUser(firstName, lastName, emailAddress, password, birthDate);
                AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Register success",
                        "You have successfully registered, welcome to Toy Social Network!");
                initProfileView(emailAddress, password);
                handleReturnHome(actionEvent);
            }
            catch(InvalidIdentifierException | ValidationException exception) {
                AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Register failure",
                        exception.getMessage());
            }
        }
        else {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Register failure",
                    "Please enter all your profile credentials before submitting!");
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
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Return failure",
                    exception.getMessage());
        }
    }

    private void initProfileView(String registeredEmailAddress, String registeredPassword) {
        try {
            User loggedUser = userService.findByLoginCredentials(registeredEmailAddress, registeredPassword);
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
        catch (IOException | NotFoundException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Login failure",
                    exception.getMessage());
        }
    }
}
