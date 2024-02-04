package application.toysocialnetwork.ui;

import application.toysocialnetwork.domain.Community;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.exceptions.InvalidInputException;
import application.toysocialnetwork.exceptions.ValidationException;
import application.toysocialnetwork.service.CommunityService;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static application.toysocialnetwork.utils.Constants.DATE_TIME_FORMATTER;

/**
 * User Interface Class, using a Console type design
 */
public class ConsoleUI {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final CommunityService communityService;

    private final HashMap<Command, CommandRunner> cmdList = new HashMap<>();


    public ConsoleUI(UserService userService, FriendshipService friendshipService, CommunityService communityService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.communityService = communityService;
        initCommands();
    }

    private void initCommands() {
        cmdList.put(Command.HELP,this::help);
        cmdList.put(Command.ADD_USER,this::addUser);
        cmdList.put(Command.FIND_USER,this::findUser);
        cmdList.put(Command.DELETE_USER,this::deleteUser);
        cmdList.put(Command.UPDATE_USER,this::updateUser);
        cmdList.put(Command.GET_USERS,this::getUsers);
        cmdList.put(Command.ADD_FRIEND,this::addFriend);
        cmdList.put(Command.REMOVE_FRIEND,this::removeFriend);
        cmdList.put(Command.FIND_FRIENDS,this::findFriends);
        cmdList.put(Command.FIND_COMMUNITIES,this::findCommunities);
        cmdList.put(Command.MOST_SOCIABLE_COMMUNITY,this::findMostSociableCommunity);
        cmdList.put(Command.FIND_FRIENDS_SINCE, this::findFriendsSince);
    }

    public void help() {

    }

    private void addUser() throws InvalidIdentifierException, ValidationException {
        System.out.println("Please input data for the new user: ");
        Scanner dataInput = new Scanner(System.in);
        System.out.println("User's First Name: ");
        String firstName = dataInput.nextLine();
        System.out.println("User's Last Name: ");
        String lastName = dataInput.nextLine();
        System.out.println("User's email address: ");
        String emailAddress = dataInput.nextLine();
        System.out.println("User's password: ");
        String password = dataInput.nextLine();
        System.out.println("User's Birth Date: ");
        LocalDate birthDate = LocalDate.parse(dataInput.nextLine());

        userService.addUser(firstName, lastName, emailAddress, password, birthDate);
        System.out.println("User successfully added!");
    }

    private void findUser() throws InvalidInputException, InvalidIdentifierException {
        System.out.println("Input the ID of the desired user: ");
        Scanner dataInput = new Scanner(System.in);
        try {
            Long id = dataInput.nextLong();
            dataInput.nextLine();

            User foundUser = userService.findUser(id);
            System.out.println(foundUser);
        }
        catch (InputMismatchException error) {
            throw new InvalidInputException("Invalid input provided for conversion!");
        }
    }

    private void deleteUser() throws InvalidInputException, InvalidIdentifierException {
        System.out.println("Input the ID of the desired user: ");
        Scanner dataInput = new Scanner(System.in);
        try {
            Long id = dataInput.nextLong();
            dataInput.nextLine();

            for(Long friendId: friendshipService.findFriendsGroup(id)) {
                friendshipService.removeFriend(id, friendId);
            }
            userService.removeUser(id);
            System.out.println("User removed successfully!");
        }
        catch (InputMismatchException error) {
            throw new InvalidInputException("Invalid input provided for conversion!");
        }
    }

    private void updateUser() throws InvalidInputException, InvalidIdentifierException, ValidationException, DateTimeParseException {
        System.out.println("Input the ID of the desired user: ");
        Scanner dataInput = new Scanner(System.in);

        try {
            Long id = dataInput.nextLong();
            dataInput.nextLine();

            System.out.println("User's new First Name: ");
            String firstName = dataInput.nextLine();
            System.out.println("User's new Last Name: ");
            String lastName = dataInput.nextLine();
            System.out.println("User's new email address: ");
            String emailAddress = dataInput.nextLine();
            System.out.println("User's new password: ");
            String password = dataInput.nextLine();
            System.out.println("User's new Birth Date: ");
            LocalDate birthDate = LocalDate.parse(dataInput.nextLine());

            userService.updateUser(id, firstName, lastName, emailAddress, password, birthDate);
            System.out.println("User updated successfully!");
        }
        catch (InputMismatchException error) {
            throw new InvalidInputException("Invalid input provided for conversion!");
        }
    }

    private void getUsers() {
        List<User> userList = userService.findAll();
        userList.forEach(System.out::println);
        if(userList.isEmpty()) {
            System.out.println("There are no users to display!");
        }
    }

    private void addFriend() throws InvalidInputException, InvalidIdentifierException, ValidationException{
        System.out.println("Input the ID of the desired user: ");
        Scanner dataInput = new Scanner(System.in);

        try {
            Long userId = dataInput.nextLong();
            dataInput.nextLine();

            System.out.println("Input the id of the user to be added as friend: ");
            Long newFriendId = dataInput.nextLong();
            dataInput.nextLine();

            friendshipService.addFriend(userId, newFriendId);
            System.out.println("Friend added successfully!");
        }
        catch (InputMismatchException error) {
            throw new InvalidInputException("Invalid input provided for conversion!");
        }
    }

    public void removeFriend() throws InvalidInputException, InvalidIdentifierException {
        System.out.println("Input the ID of the desired user: ");
        Scanner dataInput = new Scanner(System.in);

        try {
            Long userId = dataInput.nextLong();
            dataInput.nextLine();

            System.out.println("Input the id of the user to be removed from friends: ");
            Long friendId = dataInput.nextLong();
            dataInput.nextLine();

            friendshipService.removeFriend(userId, friendId);
            System.out.println("Friend removed successfully!");
        }
        catch (InputMismatchException error) {
            throw new InvalidInputException("Invalid input provided for conversion!");
        }
    }

    public void findFriends() throws InvalidInputException, InvalidIdentifierException {
        System.out.println("Input the ID of the desired user: ");
        Scanner dataInput = new Scanner(System.in);

        try {
            Long userId = dataInput.nextLong();
            dataInput.nextLine();

            Set<Long> friendsIds = friendshipService.findFriendsGroup(userId);
            if(friendsIds.isEmpty()) {
                System.out.println("User " + userService.findUser(userId).getFirstName() + " has no friends yet!");
                return;
            }

            System.out.println("Friends of user " + userService.findUser(userId).getFirstName() + ": ");
            friendsIds.forEach(id -> System.out.println(userService.findUser(id)));
        } catch (InputMismatchException error) {
            throw new InvalidInputException("Invalid input provided for conversion!");
        }
    }

    public void findCommunities() {
        int communityCount = communityService.countCommunities();
        System.out.println("Number of communities in the social network is: " + communityCount);
    }

    public void findMostSociableCommunity() {
        Community mostSociableCommunity = communityService.findMostSociableCommunity();
        if(mostSociableCommunity != null) {
            System.out.println("Most sociable community with the score: " + mostSociableCommunity.getSocialScore());
            if (!mostSociableCommunity.getUserList().isEmpty()) {
                mostSociableCommunity.getUserList().forEach(id -> System.out.println(userService.findUser(id)));
            }
        }
        else {
            System.out.println("There are no communities formed yet!");
        }
    }

    public void findFriendsSince() throws InvalidInputException {
        System.out.println("Input the ID of the desired user: ");
        Scanner dataInput = new Scanner(System.in);

        try {
            Long userId = dataInput.nextLong();
            dataInput.nextLine();

            System.out.println("Input the month since the searched users are friends: ");
            Integer month = dataInput.nextInt();
            dataInput.nextLine();

            if(month < 1 || month > 12) {
                throw new InputMismatchException("Invalid month, should be a number between 1 and 12!");
            }

            Map<Long, LocalDateTime> friendsSince = friendshipService.findFriendshipsSince(userId, month);
            if(!friendsSince.isEmpty()) {
                friendsSince.forEach((id, date) -> {
                    User friend = userService.findUser(id);
                    System.out.println(friend.getFirstName() + " | " +
                            friend.getLastName() + " | " +
                            date.format(DATE_TIME_FORMATTER));
                });
            }
            else {
                System.out.println("There were no friendships found for the given user in the month " + month);
            }
        }
        catch (InputMismatchException error) {
            throw new InvalidInputException("Invalid input provided for conversion: " + error.getMessage());
        }
    }


    public void run() {
        Scanner userInput = new Scanner(System.in);
        while(true) {
            System.out.println("Enter a command:");
            try {
                String cmd = userInput.nextLine();
                Command command = Command.fromString(cmd);
                if(command == null) {
                    throw new IOException("Invalid command! Use help to see all available options.");
                }
                if(command.equals(Command.EXIT)) {
                    System.out.println("Quitting app...");
                    return;
                }
                CommandRunner commandRunner = cmdList.get(Command.fromString(cmd));
                commandRunner.runCommand();
            }
            catch (IOException | RuntimeException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
