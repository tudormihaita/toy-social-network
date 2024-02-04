package application.toysocialnetwork.service;


import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.exceptions.NotFoundException;
import application.toysocialnetwork.exceptions.ValidationException;
import application.toysocialnetwork.repository.UserRepository;
import application.toysocialnetwork.utils.PasswordEncoder;
import application.toysocialnetwork.utils.events.ChangeEventType;
import application.toysocialnetwork.utils.events.ProfileUpdateEvent;
import application.toysocialnetwork.utils.events.UserChangeEvent;
import application.toysocialnetwork.utils.observer.Observable;
import application.toysocialnetwork.utils.observer.Observer;
import application.toysocialnetwork.validators.ValidationStrategy;
import application.toysocialnetwork.validators.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Class for the User entity, responsible for all business logic
 */
public class UserService implements Observable<ProfileUpdateEvent> {
    private final UserRepository repository;

    private final Validator<User> validator;

    private List<Observer<ProfileUpdateEvent>> observers = new ArrayList<>();

    /**
     * Initializes a User Service
     * @param repository UserRepository, reference to the repo
     * @param validator Validator, for the User entity
     */
    public UserService(UserRepository repository, Validator<User> validator) {
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Searches for a user and returns it
     * @param id Long, user identifier
     * @return User, searched user if it exists
     * @throws InvalidIdentifierException  if the provided identifier doesn't exist
     */
    public User findUser(Long id) throws InvalidIdentifierException {
        Optional<User> user = repository.findOne(id);
        if(user.isEmpty()) {
            throw new InvalidIdentifierException("Entity with the specified identifier doesn't exist!");
        }
        return user.get();
    }

    /**
     * Finds all users and returns them as a List
     * @return List<User>, all users
     */
    public List<User> findAll() {
        Iterable<User> users = repository.findAll();
        List<User> userList = new ArrayList<>();
        users.forEach(userList::add);

        return userList;
    }

    /**
     * Adds a new user
     * @param firstName String, first name of the user
     * @param lastName String, last name of the user
     * @param emailAddress String, the email address of the user account
     * @param password String, the password of the user account
     * @param birthDate LocalDate, birthdate of the user
     * @throws InvalidIdentifierException if the provided user already exists
     * @throws ValidationException if provided attributes are not validated
     */
    public void addUser(String firstName, String lastName, String emailAddress, String password, LocalDate birthDate) throws InvalidIdentifierException, ValidationException {
        String hashedPassword = PasswordEncoder.hashPassword(password);

        User user = new User(firstName, lastName, emailAddress, hashedPassword, birthDate);
        validator.validate(user, ValidationStrategy.EXHAUSTIVE);

        if(repository.findByEmail(emailAddress).isPresent()) {
            throw new InvalidIdentifierException("User with given email address already exists!");
        }

        if(repository.save(user).isPresent()) {
            throw new InvalidIdentifierException("Given identifier already taken!");
        }

        notify(new UserChangeEvent(ChangeEventType.ADD, null, user));
    }

    /**
     * Removes a user
     * @param id Long, the user identifier
     * @throws InvalidIdentifierException if there is no user with the given identifier
     */
    public void removeUser(Long id) throws InvalidIdentifierException {
        Optional<User> oldUser = repository.delete(id);

        if(oldUser.isEmpty()) {
            throw new InvalidIdentifierException("Entity with given identifier doesn't exist!");
        }

        notify(new UserChangeEvent(ChangeEventType.DELETE, oldUser.get()));
    }

    /**
     * Updates a user with the new attributes
     * @param id Long, the identifier of the desired user
     * @param firstName String, new first name
     * @param lastName String, new last name
     * @param birthDate String, new birthdate
     * @throws InvalidIdentifierException if there is no user with the given identifier
     * @throws ValidationException if the constructed updated user is not validated
     */
    public void updateUser(Long id, String firstName, String lastName, String emailAddress, String password, LocalDate birthDate) throws InvalidIdentifierException, ValidationException {
        User user = new User(firstName, lastName, emailAddress, password, birthDate);
        user.setId(id);
        validator.validate(user, ValidationStrategy.EXHAUSTIVE);

        if(repository.update(user).isPresent()) {
            throw new InvalidIdentifierException("Entity with given identifier doesn't exist!");
        }

        notify(new UserChangeEvent(ChangeEventType.UPDATE, null, user));
    }

    public User findByLoginCredentials(String emailAddress, String password) throws NotFoundException {
        String hashedPassword = PasswordEncoder.hashPassword(password);
        Optional<User> loggedUser = repository.findByLoginCredentials(emailAddress, hashedPassword);
        if(loggedUser.isEmpty()) {
            throw new NotFoundException("Provided Login Credentials are invalid!");
        }

        return loggedUser.get();
    }

    public List<UserDTO> matchByName(String searchedName) throws RuntimeException {
        return repository.matchByName(searchedName);
    }

    @Override
    public void addObserver(Observer<ProfileUpdateEvent> o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer<ProfileUpdateEvent> o) {
        observers.remove(o);
    }

    @Override
    public void notify(ProfileUpdateEvent t) {
        observers. forEach(o -> o.update(t));
    }
}