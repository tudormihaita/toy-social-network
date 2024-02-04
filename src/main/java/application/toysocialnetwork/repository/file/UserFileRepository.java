package application.toysocialnetwork.repository.file;

import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.UserRepository;
import application.toysocialnetwork.repository.memory.UserMemoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class UserFileRepository extends AbstractFileRepository<Long, User> implements UserRepository {

    private static final UserMemoryRepository userMemoryRepository = new UserMemoryRepository();

    public UserFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    protected User extractEntity(List<String> attributes) {
        User user = new User(String.valueOf(attributes.get(1)),
                String.valueOf(attributes.get(2)), String.valueOf(attributes.get(3)),
                String.valueOf(attributes.get(4)), String.valueOf(attributes.get(5)));
        user.setId(Long.valueOf(attributes.get(0)));

        return user;
    }

    @Override
    protected String entityAsString(User entity) {
        return entity.getId() + "," + entity.getFirstName() + "," + entity.getLastName() + "," + entity.getBirthDate();
    }

    @Override
    public Optional<User> save(User user) {
        Long id = new Random().nextLong(1,999);
        user.setId(id);
        return super.save(user);
    }

    @Override
    public Optional<User> findByEmail(String emailAddress) {
        throw new UnsupportedOperationException("This method has not been implemented yet for this type of class!");
    }

    @Override
    public Optional<User> findByLoginCredentials(String emailAddress, String password) {
        throw new UnsupportedOperationException("This method has not been implemented yet for this type of class!");
    }

    @Override
    public List<UserDTO> matchByName(String name) {
        throw new UnsupportedOperationException("This method has not been implemented yet for this type of class!");
    }
}