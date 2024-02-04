package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplication;
import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MenuController {
    protected SocialNetworkApplicationContext applicationContext;
    protected User loggedUser;

    @FXML
    protected AnchorPane rootPane;
    @FXML
    protected Button homeProfileButton;
    @FXML
    protected Button friendsButton;
    @FXML
    protected Button communityButton;
    @FXML
    protected Button chatButton;
    @FXML
    protected Button settingsButton;
    @FXML
    protected Button logOutButton;


    public void initController(User user, SocialNetworkApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setLoggedUser(user);
    }

    private void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public void handleFriendsTab(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SocialNetworkApplication.class.
                    getResource("views/friends-view.fxml")));
            AnchorPane pane = fxmlLoader.load();
            FriendshipController friendshipController = fxmlLoader.getController();
            friendshipController.initController(loggedUser, applicationContext);
            rootPane.getChildren().setAll(pane);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void handleCommunityTab(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SocialNetworkApplication.class.
                    getResource("views/community-view.fxml")));
            AnchorPane pane = fxmlLoader.load();
            CommunityController communityController = fxmlLoader.getController();
            communityController.initController(loggedUser, applicationContext);
            rootPane.getChildren().setAll(pane);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void handleChatTab(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SocialNetworkApplication.class.
                    getResource("views/chat-view.fxml")));
            AnchorPane pane = fxmlLoader.load();
            ChatController chatController = fxmlLoader.getController();
            chatController.initController(loggedUser, applicationContext);
            rootPane.getChildren().setAll(pane);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void handleSettingsTab(ActionEvent actionEvent) {
    }

    public void handleHomeProfileTab(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SocialNetworkApplication.class.
                    getResource("views/profile-view.fxml")));
            AnchorPane pane = fxmlLoader.load();
            ProfileController profileController = fxmlLoader.getController();
            profileController.initController(loggedUser, applicationContext);
            rootPane.getChildren().setAll(pane);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void handleLogOut(ActionEvent actionEvent) {
        Stage currentStage  = (Stage) logOutButton.getScene().getWindow();
        currentStage.close();
    }
}
