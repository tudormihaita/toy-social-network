package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.MessageService;
import application.toysocialnetwork.utils.events.AlertEvent;
import application.toysocialnetwork.utils.events.FriendshipChangeEvent;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ComposeMessageController implements Observer<ProfileUpdateEvent> {
    private SocialNetworkApplicationContext applicationContext;
    private User loggedUser;
    private MessageService messageService;
    private FriendshipService friendshipService;

    private ObservableList<Friend> receiversModel = FXCollections.observableArrayList();

    @FXML
    private Label messageSendStatusLabel;

    @FXML
    private Button sendComposedMessageButton;
    @FXML
    TextField sendComposedMessageField;

    @FXML
    private ListView<Friend> receiversListView;


    public void initController(User user, SocialNetworkApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.loggedUser = user;
        setMessageService(applicationContext.getMessageService());
        setFriendshipService(applicationContext.getFriendshipService());

        initReceiversModel();
    }

    private void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    private void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @Override
    public void update(ProfileUpdateEvent profileUpdateEvent) {
        if(profileUpdateEvent instanceof FriendshipChangeEvent) {
            initReceiversModel();
        }
    }

    public void initReceiversModel() {
        List<Friend> receivers = friendshipService.findFriendsOf(loggedUser.getId());
        receiversModel.setAll(receivers);
    }

    @FXML
    public void initialize() {
        sendComposedMessageButton.setDisable(true);
        receiversListView.setItems(receiversModel);

        receiversListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        receiversListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue)
                -> { sendComposedMessageButton.setDisable(newValue == null);
        } ));

    }

    public void handleSendComposedMessage(ActionEvent actionEvent) {
        String text = sendComposedMessageField.getText();
        if(text.isEmpty()) {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Send failure",
                    "Cannot send null message, please insert the desired message as string!");
        }

        Long idSender = loggedUser.getId();
        List<Friend> receivers = receiversListView.getSelectionModel().getSelectedItems();
        List<Long> idReceivers = receivers.stream().map(Friend::getFriendId).toList();
        try {
            messageService.sendMessage(idSender, text, idReceivers);
            sendComposedMessageField.clear();
            messageSendStatusLabel.setText("Message successfully sent!");
        }
        catch (RuntimeException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Send failure",
                    exception.getMessage());
        }
    }

    public void handleClearComposedMessage(ActionEvent actionEvent) {
        sendComposedMessageField.clear();
    }
}
