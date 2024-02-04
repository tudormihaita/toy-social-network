package application.toysocialnetwork;

import application.toysocialnetwork.repository.FriendRequestRepository;
import application.toysocialnetwork.repository.FriendshipRepository;
import application.toysocialnetwork.repository.MessageRepository;
import application.toysocialnetwork.repository.UserRepository;
import application.toysocialnetwork.repository.database.FriendRequestDBRepository;
import application.toysocialnetwork.repository.database.FriendshipDBRepository;
import application.toysocialnetwork.repository.database.MessageDBRepository;
import application.toysocialnetwork.repository.database.UserDBRepository;
import application.toysocialnetwork.service.*;
import application.toysocialnetwork.utils.DataBase;
import application.toysocialnetwork.validators.FriendshipValidator;
import application.toysocialnetwork.validators.UserValidator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SocialNetworkApplicationContext {
    private Map<Class<?>, AtomicReference<?>> instanceMap = new ConcurrentHashMap<>();
    private final AtomicReference<DataBase> DATABASE_INSTANCE = new AtomicReference<>();
    private final AtomicReference<UserRepository> USER_REPOSITORY_INSTANCE = new AtomicReference<>();
    private final AtomicReference<FriendshipRepository> FRIENDSHIP_REPOSITORY_INSTANCE = new AtomicReference<>();
    private final AtomicReference<FriendRequestRepository> FRIEND_REQUEST_REPOSITORY_INSTANCE = new AtomicReference<>();
    private final AtomicReference<MessageRepository> MESSAGE_REPOSITORY_INSTANCE = new AtomicReference<>();
    private final AtomicReference<UserService> USER_SERVICE_INSTANCE = new AtomicReference<>();
    private final AtomicReference<FriendshipService> FRIENDSHIP_SERVICE_INSTANCE = new AtomicReference<>();
    private final AtomicReference<CommunityService> COMMUNITY_SERVICE_INSTANCE = new AtomicReference<>();
    private final AtomicReference<FriendRequestService> FRIEND_REQUEST_SERVICE_INSTANCE = new AtomicReference<>();
    private final AtomicReference<MessageService> MESSAGE_SERVICE_INSTANCE = new AtomicReference<>();

//    public<T> T getComponent(Class<?> instanceClass) {
//        AtomicReference<Object> reference = new AtomicReference<>(instanceMap.get(instanceClass));
//        if(reference.get() == null) {
//            throw new IllegalArgumentException("No instance found for class: " + instanceClass.getName());
//        }
//
//        Object instance = reference.get();
//        if(instance == null) {
//            instance = createInstance(instanceClass);
//            reference.compareAndSet(null, instance);
//        }
//
//        return (T) instance;
//
//    }
//
//    private Object createInstance(Class<?> instanceClass) {
//        if(instanceClass.equals(DataBase.class)) {
//            return new DataBase(
//                    "jdbc:postgresql://localhost:5432/socialnetwork",
//                    "postgres", "postgres");
//        }
//        else if(instanceClass.equals(UserRepository.class)) {
//            return new UserDBRepository();
//        }
//        else if()
//    }

    public DataBase getDataBase() {
        if(DATABASE_INSTANCE.get() == null) {
            DATABASE_INSTANCE.compareAndSet(null, new DataBase(
                    "jdbc:postgresql://localhost:5432/socialnetwork",
                    "postgres", "postgres"));
        }

        return DATABASE_INSTANCE.get();
    }

    public UserRepository getUserRepository() {
        if(USER_REPOSITORY_INSTANCE.get() == null) {
            USER_REPOSITORY_INSTANCE.compareAndSet(null, new UserDBRepository(getDataBase()));
        }


        return USER_REPOSITORY_INSTANCE.get();
    }

    public FriendshipRepository getFriendshipRepostitory() {
        if(FRIENDSHIP_REPOSITORY_INSTANCE.get() == null) {
            FRIENDSHIP_REPOSITORY_INSTANCE.compareAndSet(null, new FriendshipDBRepository(getDataBase()));
        }

        return FRIENDSHIP_REPOSITORY_INSTANCE.get();
    }

    public FriendRequestRepository getFriendRequestRepository() {
        if(FRIEND_REQUEST_REPOSITORY_INSTANCE.get() == null) {
            FRIEND_REQUEST_REPOSITORY_INSTANCE.compareAndSet(null, new FriendRequestDBRepository(getDataBase()));
        }

        return FRIEND_REQUEST_REPOSITORY_INSTANCE.get();
    }

    public MessageRepository getMessageRepository() {
        if(MESSAGE_REPOSITORY_INSTANCE.get() == null) {
            MESSAGE_REPOSITORY_INSTANCE.compareAndSet(null, new MessageDBRepository(getDataBase()));
        }

        return MESSAGE_REPOSITORY_INSTANCE.get();
    }

    public UserService getUserService() {
        if(USER_SERVICE_INSTANCE.get() == null) {
            USER_SERVICE_INSTANCE.compareAndSet(null, new UserService(getUserRepository(),
                    new UserValidator()));
        }

        return USER_SERVICE_INSTANCE.get();
    }

    public FriendshipService getFriendshipService() {
        if(FRIENDSHIP_SERVICE_INSTANCE.get() == null) {
            FRIENDSHIP_SERVICE_INSTANCE.compareAndSet(null, new FriendshipService(getFriendshipRepostitory(),
                    new FriendshipValidator()));
        }

        return FRIENDSHIP_SERVICE_INSTANCE.get();
    }

    public CommunityService getCommunityService() {
        if(COMMUNITY_SERVICE_INSTANCE.get() == null) {
            COMMUNITY_SERVICE_INSTANCE.compareAndSet(null, new CommunityService(getFriendshipService(),
                    getUserService()));
        }

        return COMMUNITY_SERVICE_INSTANCE.get();
    }

    public FriendRequestService getFriendRequestService() {
        if(FRIEND_REQUEST_SERVICE_INSTANCE.get() == null) {
            FRIEND_REQUEST_SERVICE_INSTANCE.compareAndSet(null, new FriendRequestService(getFriendRequestRepository()));
        }

        return FRIEND_REQUEST_SERVICE_INSTANCE.get();
    }

    public MessageService getMessageService() {
        if(MESSAGE_SERVICE_INSTANCE.get() == null) {
            MESSAGE_SERVICE_INSTANCE.compareAndSet(null, new MessageService(getMessageRepository()));
        }

        return MESSAGE_SERVICE_INSTANCE.get();
    }
}
