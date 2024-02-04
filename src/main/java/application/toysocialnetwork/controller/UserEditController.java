package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.service.UserService;
import application.toysocialnetwork.utils.events.AlertEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

import static application.toysocialnetwork.utils.Constants.DATE_FORMATTER;

public class UserEditController {
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public DatePicker birthDatePicker;
    @FXML
    public TextField emailAddressField;
    @FXML
    public PasswordField passwordField;

    private SocialNetworkApplicationContext applicationContext;
    private UserService userService;
    public Stage modalStage;
    public User user;

    public void initController(SocialNetworkApplicationContext applicationContext, Stage modalStage, User user) {
        this.applicationContext = applicationContext;
        setUserService(applicationContext.getUserService());
        this.modalStage = modalStage;
        this.user = user;
        if(user != null) {
            initTextFields(user.getFirstName(), user.getLastName(), user.getBirthDate(),
                     user.getEmailAddress(), user.getPassword());
        }

    }

    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void initTextFields(String firstName, String lastName, LocalDate birthDate, String email, String password) {
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        birthDatePicker.setValue(birthDate);
        emailAddressField.setText(email);
        passwordField.setText(password);

    }

    @FXML
    public void handleCancel() {
        modalStage.close();
    }

    public void handleSave() {
        String newFirstName = firstNameField.getText();
        String newLastName = lastNameField.getText();
        LocalDate newBirthDate = birthDatePicker.getValue();
        String newEmailAddress = emailAddressField.getText();
        String newPassword = passwordField.getText();

        if(this.user == null) {
            try {
                userService.addUser(newFirstName, newLastName, newEmailAddress, newPassword, newBirthDate);
                AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Save success",
                        "New user has been successfully added!");
                handleCancel();
            }
            catch (RuntimeException exception) {
                AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Save failure",
                        "Error on adding new user: " + exception.getMessage());
            }
        }
        else {
            try {
                userService.updateUser(user.getId(), newFirstName, newLastName,
                        newEmailAddress, newPassword, newBirthDate);
                AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Update success",
                        "User has been updated successfully!");
                handleCancel();
            }
            catch (RuntimeException exception) {
                AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Update failure",
                        "Error on updating user: " + exception.getMessage());
            }
        }

    }
}
