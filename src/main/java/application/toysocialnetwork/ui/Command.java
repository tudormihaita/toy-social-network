package application.toysocialnetwork.ui;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Enum for mapping String user input commands to app functionalities
 */
public enum Command {
    ADD_USER("add_user"),
    FIND_USER("find_user"),
    GET_USERS("get_users"),
    UPDATE_USER("update_user"),
    DELETE_USER("delete_user"),
    ADD_FRIEND("add_friend"),
    REMOVE_FRIEND("remove_friend"),
    FIND_FRIENDS("find_friends"),
    FIND_COMMUNITIES("find_communities"),
    MOST_SOCIABLE_COMMUNITY("most_sociable_community"),
    FIND_FRIENDS_SINCE("find_friends_since"),
    HELP("help"),
    EXIT("exit");

    private final String commandString;

    Command(String commandString) {
        this.commandString = commandString;
    }

    public String getCommandString() {
        return commandString;
    }

    public static Command fromString(String commandStr) {
        return Arrays.stream(values())
                .filter(command -> command.getCommandString().equals(commandStr))
                .findFirst()
                .orElse(null);
    }
}

