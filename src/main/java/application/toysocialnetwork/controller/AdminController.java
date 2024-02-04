package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.SocialNetworkApplication;
import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.service.CommunityService;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.UserService;
import application.toysocialnetwork.utils.events.AlertEvent;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.events.UserChangeEvent;
import application.toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AdminController implements Observer<ProfileUpdateEvent> {
    private SocialNetworkApplicationContext applicationContext;
    private UserService userService;
    private FriendshipService friendshipService;
    private CommunityService communityService;

    ObservableList<User> userModel = FXCollections.observableArrayList();
    ObservableList<Friend> friendsModel = FXCollections.observableArrayList();

    @FXML
    TableView<User> usersTableView;
    @FXML
    TableView<Friend> friendsTableView;
    @FXML
    TableColumn<User, Long> idUser;
    @FXML
    TableColumn<User, Long> firstNameUser;
    @FXML
    TableColumn<User, String> lastNameUser;
    @FXML
    TableColumn<User, String> emailUser;
    @FXML
    TableColumn<User, String> birthDateUser;

    @FXML
    TableColumn<Friend, Long> idFriend;
    @FXML
    TableColumn<Friend, String> firstNameFriend;
    @FXML
    TableColumn<Friend, String> lastNameFriend;
    @FXML
    TableColumn<Friend, LocalDateTime> friendsSince;

    @FXML
    TextField communityCount;

    public void initController(SocialNetworkApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setUserService(applicationContext.getUserService());
        setFriendshipService(applicationContext.getFriendshipService());
        setCommunityService(applicationContext.getCommunityService());
        initUserModel();
        initFriendsModel();
        communityCount.setText(String.valueOf(communityService.countCommunities()));
        userService.addObserver(this);
    }

    @Override
    public void update(ProfileUpdateEvent profileUpdateEvent) {
        if(profileUpdateEvent instanceof UserChangeEvent) {
            initUserModel();
        }
    }

    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    private void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    private void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    @FXML
    public void initialize() {
        firstNameUser.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameUser.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailUser.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        birthDateUser.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        idUser.setCellValueFactory(new PropertyValueFactory<>("id"));

        usersTableView.setItems(userModel);

        idFriend.setCellValueFactory(new PropertyValueFactory<>("friendId"));
        firstNameFriend.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameFriend.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendsSince.setCellValueFactory(new PropertyValueFactory<>("friendsSince"));

        friendsTableView.setItems(friendsModel);
    }

    public void initUserModel() {
    List<User> userList = userService.findAll();
    userModel.setAll(userList);
    usersTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
        if(newValue != null) {
            initFriendsModel();
        }
    }));
    }

    public void initFriendsModel() {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser !=  null) {
            List<Friend> friendList = friendshipService.findFriendsOf(selectedUser.getId());
            friendsModel.setAll(friendList);
        }
    }

    @FXML
    public void handleRemoveUser(ActionEvent actionEvent) {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null) {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Deletion Failure",
                    "Please select the user you wish to delete!");
        }
        else  {
            Long userId = selectedUser.getId();
            try {
                userService.removeUser(userId);
                AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Deletion success",
                        "User with ID " + userId + " has been successfully deleted!");
            }
            catch (InvalidIdentifierException exception) {
                AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Deletion Failure",
                        exception.getMessage());
            }
        }
    }

    @FXML
    public void handleAddUser(ActionEvent actionEvent) {
        initUserEditor(null);
    }

    @FXML
    public void handleUpdateUser(ActionEvent actionEvent) {
        User toBeUpdated = usersTableView.getSelectionModel().getSelectedItem();
        if(toBeUpdated == null) {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Update failure",
                    "Please select the user you wish to update!");
            return;
        }

        initUserEditor(toBeUpdated);
    }

    private void initUserEditor(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource
                    ("views/user-edit-view.fxml"));

            Stage dialogStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            dialogStage.setTitle("User edit");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            UserEditController userEditController = fxmlLoader.getController();
            userEditController.initController(applicationContext, dialogStage, user);
            dialogStage.show();
        }
        catch (IOException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR,
                    "Window action failure", exception.getMessage());
            exception.printStackTrace();
        }
    }

}

