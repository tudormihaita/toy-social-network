package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplication;
import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.service.FriendRequestService;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.UserService;
import application.toysocialnetwork.utils.Page;
import application.toysocialnetwork.utils.Pageable;
import application.toysocialnetwork.utils.events.*;
import application.toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipController extends MenuController implements Observer<ProfileUpdateEvent> {
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;

    private int currentPage = 0;
    private int pageSize = 5;
    private int totalCount = 0;

    private ObservableList<Friend> friendsModel = FXCollections.observableArrayList();
    private ObservableList<UserDTO> pendingRequestsModel = FXCollections.observableArrayList();

    @FXML
    private Label userFriendsLabel;
    @FXML
    private Button acceptRequestButton;
    @FXML
    private Button declineRequestButton;
    @FXML
    private Button removeFriendButton;

    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private TextField currentPageField;
    @FXML
    private TextField maxPageField;
    @FXML
    private TextField pageSizeField;

    @FXML
    private TableView<Friend> friendsTableView;
    @FXML
    private ListView<UserDTO> pendingRequestsListView;
    @FXML
    private TableColumn<Friend, String> firstNameColumn;
    @FXML
    private TableColumn<Friend, String> lastNameColumn;
    @FXML
    private TableColumn<Friend, LocalDateTime> friendsSinceColumn;



    public void initController(User user, SocialNetworkApplicationContext applicationContext) {
        super.initController(user, applicationContext);
        setUserService(applicationContext.getUserService());
        setFriendshipService(applicationContext.getFriendshipService());
        setFriendRequestService(applicationContext.getFriendRequestService());

        initFriendsLabel();
        initFriendsModel();
        initPendingRequestsModel();
        userService.addObserver(this);
        friendshipService.addObserver(this);
        friendRequestService.addObserver(this);
    }

    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    private void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    private void setFriendRequestService(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    public void initFriendsLabel() {
        userFriendsLabel.setFont(new Font("System", 34));
        userFriendsLabel.setText("Friends of " + loggedUser.getFirstName() + " " + loggedUser.getLastName());
    }

    public void initFriendsModel() {
        Page<Friend> friendPage = friendshipService.findFriendsOf(new Pageable(currentPage, pageSize), loggedUser.getId());

        int maxPage = (int) Math.ceil((double) friendPage.getTotalElementCount() / pageSize) - 1;
        if(maxPage == -1) {
            maxPage = 0;
        }

        if(currentPage > maxPage) {
            currentPage = maxPage;
            friendPage = friendshipService.findFriendsOf(new Pageable(currentPage, pageSize), loggedUser.getId());
        }

        friendsModel.setAll(StreamSupport.stream(friendPage.getElementsOnPage().spliterator(),
                false).collect(Collectors.toList()));
        totalCount = friendPage.getTotalElementCount();

        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*pageSize >= totalCount);
        currentPageField.setText(String.valueOf(currentPage+1));
        maxPageField.setText(String.valueOf(maxPage+1));
    }

    public void initPendingRequestsModel() {
        List<UserDTO> pendingRequests = friendRequestService.findPendingRequestsOf(loggedUser.getId());
        pendingRequestsModel.setAll(pendingRequests);
    }

    @Override
    public void update(ProfileUpdateEvent profileUpdateEvent) {
        if(profileUpdateEvent instanceof UserChangeEvent) {
            initFriendsModel();
        }
        else if(profileUpdateEvent instanceof FriendRequestResponseEvent) {
            initPendingRequestsModel();
        }
        else if(profileUpdateEvent instanceof FriendshipChangeEvent) {
            initFriendsModel();
        }
    }

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendsSinceColumn.setCellValueFactory(new PropertyValueFactory<>("friendsSince"));

        friendsTableView.setItems(friendsModel);
        pendingRequestsListView.setItems(pendingRequestsModel);

        removeFriendButton.setDisable(true);
        acceptRequestButton.setDisable(true);
        declineRequestButton.setDisable(true);
        friendsButton.setDisable(true);

        friendsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> removeFriendButton.setDisable(newValue == null));
        pendingRequestsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> { acceptRequestButton.setDisable(newValue == null);
            declineRequestButton.setDisable(newValue == null);
        });

        currentPageField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                try {
                    currentPage = Integer.parseInt(currentPageField.getText()) - 1;
                    if(currentPage < 0) {
                        currentPage = 0;
                    }
                    initFriendsModel();
                }
                catch (NumberFormatException exception) {
                    currentPage = 0;
                    AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Update page failure",
                            exception.getMessage());
                }
            }
        });
    }

    public void handleAddFriend(ActionEvent actionEvent) {
        handleHomeProfileTab(actionEvent);
    }

    public void handleRemoveFriend(ActionEvent actionEvent) {
        Friend selectedFriend = friendsTableView.getSelectionModel().getSelectedItem();
        if(selectedFriend != null) {
            friendshipService.removeFriend(loggedUser.getId(), selectedFriend.getFriendId());
            AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Friend removed",
                    "Successfully removed " + selectedFriend.getFirstName() + " " +
                            selectedFriend.getLastName() + " from friends!");
        }
        else {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Remove friend failure",
                    "Failed to remove friend, please select the user you want to remove from friends!");
        }
    }

    public void handleAcceptRequest(ActionEvent actionEvent) {
        UserDTO selectedPendingUser = pendingRequestsListView.getSelectionModel().getSelectedItem();
        if(selectedPendingUser != null) {
            friendRequestService.acceptFriendRequest(selectedPendingUser.getId(), loggedUser.getId());
            friendshipService.addFriend(loggedUser.getId(), selectedPendingUser.getId());
            AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Request accepted",
                    "Friend request from " + selectedPendingUser.getFirstName() + " " +
                            selectedPendingUser.getLastName() + " successfully accepted!");
        }
        else {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Request failure",
                    "Failed to accept request, please select the request you want to process!");
        }
    }

    public void handleDeclineRequest(ActionEvent actionEvent) {
        UserDTO selectedPendingUser = pendingRequestsListView.getSelectionModel().getSelectedItem();
        if(selectedPendingUser != null) {
            friendRequestService.declineFriendRequest(selectedPendingUser.getId(), loggedUser.getId());
            AlertEvent.showMessage(null, Alert.AlertType.CONFIRMATION, "Request declined",
                    "Friend request from " + selectedPendingUser.getFirstName() + " " +
                            selectedPendingUser.getLastName() + " successfully declined!");
        }
        else {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Request failure",
                    "Failed to decline request, please select the request you want to process!");
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        currentPage++;
        initFriendsModel();
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initFriendsModel();
    }

    public void handleUpdatePageSize(ActionEvent actionEvent) {
        try {
            pageSize = Integer.parseInt(pageSizeField.getText());
            currentPage = 0;
            initFriendsModel();
        }
        catch (NumberFormatException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Paging failure",
                    exception.getMessage());
        }
    }
}
