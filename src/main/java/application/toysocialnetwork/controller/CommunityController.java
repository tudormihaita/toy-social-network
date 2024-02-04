package application.toysocialnetwork.controller;

import application.toysocialnetwork.SocialNetworkApplicationContext;
import application.toysocialnetwork.domain.Community;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.service.CommunityService;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

import java.util.List;

public class CommunityController extends MenuController implements Observer<ProfileUpdateEvent> {
    private CommunityService communityService;
    private ObservableList<UserDTO> communityMembersModel = FXCollections.observableArrayList();

    @FXML
    private Label userCommunityLabel;
    @FXML
    private ListView<UserDTO> communityMembersListView;
    @FXML
    private TextField usersField;
    @FXML
    private TextField friendshipsField;
    @FXML
    private TextField ageAverageField;
    @FXML
    private TextField socialScoreField;

    public void initController(User loggedUser, SocialNetworkApplicationContext applicationContext) {
        super.initController(loggedUser, applicationContext);
        setCommunityService(applicationContext.getCommunityService());
        initMembersModel();
        initCommunityLabel();
        updateCommunityStats();
    }

    private void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Override
    public void update(ProfileUpdateEvent profileUpdateEvent) {
        updateCommunityStats();
    }

    public void initCommunityLabel() {
        userCommunityLabel.setFont(new Font("System", 34));
        userCommunityLabel.setText(loggedUser.getFirstName() + " " +  loggedUser.getLastName() + "'s Community");
    }

    public void initMembersModel() {
        Community userCommunity = communityService.findCommunityOf(loggedUser.getId());
        if(userCommunity != null) {
            List<UserDTO> membersList = communityService.findCommunityMembers(userCommunity);
            communityMembersModel.setAll(membersList);
        }
    }

    @FXML
    public void initialize() {
        communityMembersListView.setItems(communityMembersModel);
    }

    private void updateCommunityStats() {
        Community userCommunity = communityService.findCommunityOf(loggedUser.getId());
        if(userCommunity != null) {
            usersField.setText(String.valueOf(userCommunity.getUserCount()));
            friendshipsField.setText(String.valueOf(communityService.findFriendships(userCommunity).size()));
            ageAverageField.setText(String.valueOf(communityService.findAgeAverage(userCommunity)));
            socialScoreField.setText(String.valueOf(userCommunity.getSocialScore()));
        }

    }
}
