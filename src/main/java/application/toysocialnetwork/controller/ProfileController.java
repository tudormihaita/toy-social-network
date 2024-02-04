package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.service.CommunityService;
import application.toysocialnetwork.service.FriendRequestService;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.UserService;
import application.toysocialnetwork.utils.events.*;
import application.toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;

import java.util.List;

public class ProfileController extends MenuController implements Observer<ProfileUpdateEvent> {
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private CommunityService communityService;

    private ObservableList<UserDTO> matchedUsersModel = FXCollections.observableArrayList();

    @FXML
    private Label userWelcomeLabel;
    @FXML
    public TextField searchNameField;
    @FXML
    private Button sendRequestButton;
    @FXML
    private Button cancelRequestButton;
    @FXML
    private TableView<UserDTO> matchedUsersTableView;
    @FXML
    private TableColumn<UserDTO, String> firstNameMatchColumn;
    @FXML
    private TableColumn<UserDTO, String> lastNameMatchColumn;

    @FXML
    private TextField usersCount;
    @FXML
    private TextField friendshipsCount;
    @FXML
    private TextField communitiesCount;

    public void initController(User user, SocialNetworkApplicationContext applicationContext) {
        super.initController(user, applicationContext);
        setUserService(applicationContext.getUserService());
        setFriendshipService(applicationContext.getFriendshipService());
        setFriendRequestService(applicationContext.getFriendRequestService());
        setCommunityService(applicationContext.getCommunityService());

        initWelcomeLabel();
        initSocialNetworkStats();
        userService.addObserver(this);
        friendRequestService.addObserver(this);
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

    private void setFriendRequestService(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @Override
    public void update(ProfileUpdateEvent profileUpdateEvent) {
        if(profileUpdateEvent instanceof FriendshipChangeEvent) {
            setFriendshipsCount();
            setCommunitiesCount();
        }
        else if(profileUpdateEvent instanceof UserChangeEvent) {
            setUsersCount();
        }
    }

    public void initWelcomeLabel() {
        userWelcomeLabel.setFont(new Font("System", 34));
        userWelcomeLabel.setText("Welcome, " + loggedUser.getFirstName() + " " + loggedUser.getLastName() + "!");
    }

    public void initSocialNetworkStats() {
        setUsersCount();
        setFriendshipsCount();
        setCommunitiesCount();
    }

    private void setUsersCount() {
        usersCount.setText(String.valueOf(userService.findAll().size()));
    }

    private void setFriendshipsCount() {
        friendshipsCount.setText(String.valueOf(friendshipService.findAll().size()));
    }

    private void setCommunitiesCount() {
        communitiesCount.setText(String.valueOf(communityService.countCommunities()));
    }

    public void reloadMatchedUsersModel() {
        String searchedName = searchNameField.getText();
        List<UserDTO> matchedUsers = userService.matchByName(searchedName);

        matchedUsersModel.setAll(matchedUsers);
    }

    @FXML
    public void initialize() {
        firstNameMatchColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameMatchColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        matchedUsersTableView.setItems(matchedUsersModel);

        sendRequestButton.setDisable(true);
        cancelRequestButton.setDisable(true);
        homeProfileButton.setDisable(true);

        searchNameField.textProperty().addListener(observable -> {
            reloadMatchedUsersModel();
        });

        matchedUsersTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                UserDTO selectedUser = newValue;
                UserDTO requestingUser = new UserDTO(loggedUser.getId(), loggedUser.getFirstName(),
                        loggedUser.getLastName(), loggedUser.getBirthDate(), loggedUser.getEmailAddress());
                if(friendRequestService.areFriends(loggedUser.getId(), selectedUser.getId())){
                    sendRequestButton.setDisable(true);
                }
                else if(selectedUser.equals(requestingUser)) {
                    sendRequestButton.setDisable(true);
                }
                else if(friendRequestService.findPendingRequestsOf(selectedUser.getId()).contains(requestingUser)) {
                    sendRequestButton.setDisable(true);
                    cancelRequestButton.setDisable(false);
                }
                else {
                    sendRequestButton.setDisable(false);
                    cancelRequestButton.setDisable(true);
                }
            }
        } ));
    }

    public void handleSendRequest(ActionEvent actionEvent) {
        UserDTO selectedUser = matchedUsersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            friendRequestService.sendFriendRequest(loggedUser.getId(), selectedUser.getId());
            AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Friend request sent",
                    "Successfully sent a friend request to " + selectedUser.getFirstName() + " " +
                            selectedUser.getLastName());
        }
        else {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Friend request failure",
                    "Failed to send friend request, please select the user you want to add as a friend!");
        }
    }

    public void handleCancelRequest(ActionEvent actionEvent) {
        UserDTO selectedUser = matchedUsersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            friendRequestService.cancelFriendRequest(loggedUser.getId(), selectedUser.getId());
            AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Friend request cancelled",
                    "Friend request to " + selectedUser.getFirstName() + " " + selectedUser.getLastName() +
                            " successfully cancelled!");
        }
        else {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Friend request cancel failure",
                    "Failed to cancel friend request, please select the user first!");
        }

    }
}
