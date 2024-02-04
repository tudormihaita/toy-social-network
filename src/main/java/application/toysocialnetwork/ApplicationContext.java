package application.toysocialnetwork;

import application.toysocialnetwork.repository.FriendRequestRepository;
import application.toysocialnetwork.repository.FriendshipRepository;
import application.toysocialnetwork.repository.UserRepository;
import application.toysocialnetwork.repository.database.FriendRequestDBRepository;
import application.toysocialnetwork.repository.database.FriendshipDBRepository;
import application.toysocialnetwork.repository.database.UserDBRepository;
import application.toysocialnetwork.service.CommunityService;
import application.toysocialnetwork.service.FriendRequestService;
import application.toysocialnetwork.service.FriendshipService;
import application.toysocialnetwork.service.UserService;
import application.toysocialnetwork.utils.DataBase;
import application.toysocialnetwork.validators.FriendshipValidator;
import application.toysocialnetwork.validators.UserValidator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ApplicationContext {
    private final Map<Class<?>, Supplier<?>> instanceMap = new ConcurrentHashMap<>();

    public<T> T getComponent(Class<T> instanceClass) {
        Supplier<?> supplier = instanceMap.computeIfAbsent(instanceClass, this::initComponent);
        return instanceClass.cast(supplier.get());
    }

    private<T> Supplier<?> initComponent(Class<T> instanceClass) throws RuntimeException {
        if(instanceClass == DataBase.class) {
            return () -> new DataBase(
                    "jdbc:postgresql://localhost:5432/socialnetwork",
                    "postgres", "postgres");
        }
        else if(instanceClass == UserValidator.class) {
            return UserValidator::new;
        }
        else if(instanceClass == FriendshipValidator.class) {
            return FriendshipValidator::new;
        }
        else if(instanceClass == UserRepository.class) {
            return () -> new UserDBRepository(getComponent(DataBase.class));
        }
        else if(instanceClass == FriendshipRepository.class) {
            return () -> new FriendshipDBRepository(getComponent(DataBase.class));
        }
        else if(instanceClass == FriendRequestRepository.class) {
            return () -> new FriendRequestDBRepository(getComponent(DataBase.class));
        }
        else if(instanceClass == UserService.class) {
            return () -> new UserService(getComponent(UserRepository.class),
                    getComponent(UserValidator.class));
        }
        else if(instanceClass == FriendshipService.class) {
            return () -> new FriendshipService(getComponent(FriendshipRepository.class),
                    getComponent(FriendshipValidator.class));
        }
        else if(instanceClass == CommunityService.class) {
            return () -> new CommunityService(getComponent(FriendshipService.class),
                    getComponent(UserService.class));
        }
        else if(instanceClass == FriendRequestService.class) {
            return () -> new FriendRequestService(getComponent(FriendRequestRepository.class));
        }
        else {
            throw new RuntimeException("Unable to instantiate given class!");
        }
    }


}
