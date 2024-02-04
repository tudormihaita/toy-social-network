package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplication;
import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.Friend;
import application.toysocialnetwork.domain.message.Message;
import application.toysocialnetwork.domain.message.ReplyMessage;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.MessageService;
import application.toysocialnetwork.utils.events.AlertEvent;
import application.toysocialnetwork.utils.events.FriendshipChangeEvent;
import application.toysocialnetwork.utils.events.MessageSendEvent;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static application.toysocialnetwork.utils.Constants.TIME_FORMATTER;

public class ChatController extends MenuController implements Observer<ProfileUpdateEvent> {
    private MessageService messageService;
    private FriendshipService friendshipService;
    private Friend selectedContact = null;

    private ObservableList<Friend> contactsModel = FXCollections.observableArrayList();
    private ObservableList<Message> messagesModel = FXCollections.observableArrayList();

    @FXML
    private Label userChatLabel;

    @FXML
    private Button sendMessageButton;
    @FXML
    private Button sendReplyButton;
    @FXML
    private Button unsendMessageButton;

    @FXML
    private ListView<Friend> contactsListView;
    @FXML
    private ListView<Message> chatListView;
    @FXML
    private TextField sendMessageField;

    public void initController(User user, SocialNetworkApplicationContext applicationContext) {
        super.initController(user, applicationContext);
        setMessageService(applicationContext.getMessageService());
        setFriendshipService(applicationContext.getFriendshipService());

        initChatLabel();
        initContactsModel();

        messageService.addObserver(this);
        friendshipService.addObserver(this);
    }

    private void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    private void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @Override
    public void update(ProfileUpdateEvent profileUpdateEvent) {
        if(profileUpdateEvent instanceof MessageSendEvent) {
            Friend selectedContact = contactsListView.getSelectionModel().getSelectedItem();
            initChatMessages(selectedContact);
        }
        if(profileUpdateEvent instanceof FriendshipChangeEvent) {
            initContactsModel();
        }
    }

    public void initChatLabel() {
        userChatLabel.setFont(new Font("System", 30));
        userChatLabel.setText(loggedUser.getFirstName() + " " + loggedUser.getLastName() + ": Chat with your friends!");
    }

    @FXML
    public void initialize() {
        contactsListView.setItems(contactsModel);
        chatListView.setItems(messagesModel);
        chatButton.setDisable(true);
        sendMessageButton.setDisable(true);

        contactsListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue)
                -> { sendMessageButton.setDisable(newValue == null);
            if(newValue != null) {
                    initChatMessages(newValue);
                    selectedContact = newValue;
                }
        } ));

        chatListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue)
                ->  {
            unsendMessageButton.setDisable(newValue == null);
            sendReplyButton.setDisable(newValue == null);
        }));

        chatListView.setCellFactory(message -> new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    VBox chatBubble = new VBox();
                    double messageSize = 0.0;

                    if(item instanceof ReplyMessage) {
                        String text = ((ReplyMessage) item).getRepliedText();
                        Text repliedText = new Text("Reply to: " + (text.substring(0, text.length()/2)) + "...");

                        repliedText.setFill(Color.DARKGREY);
                        chatBubble.getChildren().add(repliedText);
                        messageSize = repliedText.getLayoutBounds().getWidth();
                    }

                    Text messageText = new Text(item.getText());
                    if(item.getIdSender().equals(loggedUser.getId())) {
                        chatBubble.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, new CornerRadii(15), null)));
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                    else {
                        setAlignment(Pos.CENTER_LEFT);
                    }

                    chatBubble.getChildren().add(messageText);
                    chatBubble.setPadding(new Insets(10, 10, 10, 10));
                    messageSize = Math.max(messageText.getLayoutBounds().getWidth(), messageSize);

                    chatBubble.setMaxWidth(messageSize + 20);
                    chatBubble.setMinWidth(messageSize + 20);
                    chatBubble.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                            new CornerRadii(15), BorderWidths.DEFAULT)));

                    setGraphic(chatBubble);
                    setTooltip(new Tooltip(item.getSentAt().format(TIME_FORMATTER)));
                    chatListView.scrollTo(messagesModel.size() - 1);
                }
            }
        });
    }

    public void initContactsModel() {
        List<Friend> contacts = friendshipService.findFriendsOf(loggedUser.getId());
        contactsModel.setAll(contacts);
    }

    private void initChatMessages(Friend selectedFriend) {
        List<Message> messages = messageService.findMessagesBetween(loggedUser.getId(), selectedFriend.getFriendId());
        messagesModel.setAll(messages);
    }

    public void handleSendMessage(ActionEvent actionEvent) {
        String text = sendMessageField.getText();
        if(text.isEmpty()) {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Send failure",
                    "Cannot send null message, please insert the desired message as string!");
            return;
        }

        Long idSender = loggedUser.getId();
        Long idReceiver = contactsListView.getSelectionModel().getSelectedItem().getFriendId();
        try {
            messageService.sendMessage(idSender, text, Collections.singletonList(idReceiver));
            sendMessageField.clear();
        }
        catch (RuntimeException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Send failure",
                    exception.getMessage());
        }
    }

    public void handleComposeMessage(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.
                    getResource("views/compose-message-view.fxml"));
            Stage dialogStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            dialogStage.setTitle("Compose Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            ComposeMessageController composeMessageController = fxmlLoader.getController();
            composeMessageController.initController(loggedUser, applicationContext);
            dialogStage.show();
        }
        catch (IOException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Compose message failure",
                    exception.getMessage());
        }
    }

    public void handeSendReply(ActionEvent actionEvent) {
        String text = sendMessageField.getText();
        if(text.isEmpty()) {
            AlertEvent.showMessage(null, Alert.AlertType.WARNING, "Send failure",
                    "Cannot send null reply, please insert the desired message as string!");
            return;
        }

        Long idSender = loggedUser.getId();
        Long idReceiver = contactsListView.getSelectionModel().getSelectedItem().getFriendId();
        Long repliedMessage = chatListView.getSelectionModel().getSelectedItem().getId();
        try {
            messageService.sendReply(idSender, text, Collections.singletonList(idReceiver), repliedMessage);
            sendMessageField.clear();
        }
        catch (RuntimeException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Send failure",
                    exception.getMessage());
        }
    }

    public void handleUnsendMessage(ActionEvent actionEvent) {
        Message selectedMessage = chatListView.getSelectionModel().getSelectedItem();
        try {
            messageService.unsendMessage(selectedMessage.getId());
        }
        catch (InvalidIdentifierException exception) {
            AlertEvent.showMessage(null, Alert.AlertType.ERROR, "Send failure",
                    exception.getMessage());
        }
    }


}
