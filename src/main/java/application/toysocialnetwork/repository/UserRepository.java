package application.toysocialnetwork.repository;

import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.domain.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interface specific for the User Repository, adding to the CRUD operations interface other
 * user-specific operations
 */
//TODO: ask if use of interfaces for each entity is still justified
public interface UserRepository extends Repository<Long, User> {

    Optional<User> findByEmail(String emailAddress);

    Optional<User> findByLoginCredentials(String emailAddress, String password);

    List<UserDTO> matchByName(String searchedName);

}