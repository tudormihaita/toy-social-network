package application.toysocialnetwork.service;

import application.toysocialnetwork.domain.Community;
import application.toysocialnetwork.domain.Friendship;
import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.exceptions.InvalidInputException;
import application.toysocialnetwork.utils.GraphManager;
import application.toysocialnetwork.utils.events.NetworkEvent;
import application.toysocialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.Predicate;

import static application.toysocialnetwork.utils.Constants.MINIMUM_COMMUNITY_MEMBERS;

/**
 * Service Class for the Community entity, responsible for all business logic
 */
public class CommunityService {
    private static final GraphManager<Long> graphManager = new GraphManager<>();
    private final FriendshipService friendshipService;
    private final UserService userService;

    private List<Observer<NetworkEvent>> observers = new ArrayList<>();

    /**
     * Initializes the Service
     * @param friendshipService FriendshipService, reference to the service
     * @param userService UserService, reference to the service
     */
    public CommunityService(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    /**
     * Counts all formed communities within the social media network
     * @return int, number of communities
     */
    public int countCommunities() {
        return findAllCommunities().size();
    }

    /**
     * Computes the social score of all existing communities and returns the one with the maximum score
     * @return Community, with the maximum social score
     */
    public Community findMostSociableCommunity() {
        List<Community> communities = findAllCommunities();
        return communities.stream().max(
                Comparator.comparingInt(Community::getSocialScore)).orElse(null);
    }

    /**
     * Finds all communities and returns a list of them
     * @return List of Communities
     */
    private List<Community> findAllCommunities() {
        List<Long> userIds = userService.findAll().stream().map(User::getId).toList();
        Map<Long, Set<Long>> networkGraph = findNetwork(userIds);
        graphManager.setGraph(networkGraph);

        List<List<Long>> connectedComponents = graphManager.findConnectedComponents();
        List<Community> communities = new ArrayList<>();

        for(List<Long> connectedComponent: connectedComponents) {
            Community community = createCommunity(connectedComponent);
            Predicate<Community> isValidCommunity = c -> c.getUserCount() >= MINIMUM_COMMUNITY_MEMBERS;
            if(isValidCommunity.test(community)) {
                communities.add(community);
            }
        }

        return communities;
    }

    /**
     * Computes and creates a community with a given list of users
     * @param users List of user identifiers
     * @return Community, the new created community
     */
    private Community createCommunity(List<Long> users) {
        Map<Long, Set<Long>> communityGraph = findNetwork(users);
        graphManager.setGraph(communityGraph);
        int socialScore = graphManager.findLongestPath().size()-1;

        return new Community(users, socialScore);
    }

    /**
     * Computes a network of users
     * @param userIds List of user identifiers
     * @return Map, representing and Adjacency List for a community within the social media network
     */
    private Map<Long, Set<Long>> findNetwork(List<Long> userIds) {
        Map<Long, Set<Long>> networkMap = new HashMap<>();
        for(Long userId: userIds) {
            Set<Long> friends = friendshipService.findFriendsGroup(userId);
            networkMap.put(userId, friends);
        }

        return networkMap;
    }

    public Community findCommunityOf(Long userId) {
        return findAllCommunities().stream().filter(
                c -> c.getUserList().contains(userId)).findFirst().orElse(null);
    }

    public List<UserDTO> findCommunityMembers(Community community) {
        List<UserDTO> members = new ArrayList<>();
        if(community == null) {
            return members;
        }

        List<Long> memberIds = community.getUserList();
        members = memberIds.stream().map(id -> {
            User member = userService.findUser(id);
            return new UserDTO(id, member.getFirstName(), member.getLastName(), member.getBirthDate(),
                    member.getEmailAddress());
        }).toList();

        return members;
    }

    public Double findAgeAverage(Community community) throws InvalidInputException {
        List<Integer> ageMembers = findCommunityMembers(community).stream().map(member ->
                Period.between(member.getBirthDate(), LocalDate.now()).getYears()).toList();

        OptionalDouble ageAverage = ageMembers.stream().mapToInt(Integer::intValue).average();
        if(ageAverage.isPresent()) {
            return ageAverage.getAsDouble();
        }
        else {
            throw new InvalidInputException("Cannot compute age average for given community!");
        }
    }

    public List<Friendship> findFriendships(Community community) {
        List<Long> members = community.getUserList();

        return friendshipService.findAll().stream().filter(
               friendship -> members.contains(friendship.getFirstUserId()) &&
                       members.contains(friendship.getSecondUserId())).toList();
    }
}

