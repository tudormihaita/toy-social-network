package application.toysocialnetwork.repository.memory;

import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Repository class for the User entity, which extends the Abstract Memory Repository by
 * implementing the UserRepository interface, with user-specific operations also added
 */
public class UserMemoryRepository extends AbstractMemoryRepository<Long, User> implements UserRepository {

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