package application.toysocialnetwork.domain;

import java.util.List;
import java.util.Objects;

/**
 * Class representing the Community entity formed within a social media network
 */
public class Community {

    private final List<Long> userList;
    private final int socialScore;

    /**
     * Constructs a community based on the friendships formed between users
     * @param users List of Users part of the community
     * @param socialScore int, score representing the longest path existing in the community network
     */
    public Community(List<Long> users, int socialScore) {
        this.userList = users;
        this.socialScore = socialScore;
    }

    /**
     * @return List of users part of the community
     */
    public List<Long> getUserList() {
        return userList;
    }

    /**
     * @return the social score of the community
     */
    public int getSocialScore() {
        return socialScore;
    }

    /**
     * @return number of users in the community
     */
    public int getUserCount() {
        return userList.size();
    }

    @Override
    public String toString() {
        return "Community | " +
                "users: " + userList +
                " | socialScore:" + socialScore +
                " | ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Community community = (Community) o;
        return socialScore == community.socialScore && Objects.equals(userList, community.userList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userList, socialScore);
    }
}

