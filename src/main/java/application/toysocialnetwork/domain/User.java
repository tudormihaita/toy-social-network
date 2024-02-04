package application.toysocialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static application.toysocialnetwork.utils.Constants.DATE_FORMATTER;

/**
 * Class representing the client using the social media app
 */
public class User  extends Entity<Long> {
    private final String firstName;
    private final String lastName;
    private final String emailAddress;
    private final String password;
    private final LocalDate birthDate;
    private final LocalDateTime registerDate;

    /**
     * Constructs a new User and initializes all its attributes
     *
     * @param firstName String, first name of the User
     * @param lastName  String, last name of the User
     * @param birthDate LocalDate, birthdate of the User, having a general format of yyyy-MM-dd
     * @param registerDate LocalDateTime, time of registration in the network
     */
    public User(String firstName, String lastName, String email, String password, LocalDate birthDate, LocalDateTime registerDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = email;
        this.password = password;
        this.birthDate = birthDate;
        this.registerDate = registerDate;
    }

    public User(String firstName, String lastName, String email, String password, String birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = email;
        this.password = password;
        this.birthDate = LocalDate.parse(birthDate);
        this.registerDate = LocalDateTime.now();
    }

    public User(String firstName, String lastName, String email, String password, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = email;
        this.password = password;
        this.birthDate = birthDate;
        this.registerDate = LocalDateTime.now();
    }

    /**
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the email address of the user
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @return the password of the user account
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return the last name of the user
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * @return the date when the user registered for the social media app
     */
    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    @Override
    public String toString() {
        return "User: " +
                firstName +
                " " + lastName +
                " | " + birthDate.format(DATE_FORMATTER) + " | " +
                "[" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User other = (User) o;
        return getFirstName().equals(other.getFirstName()) &&
                getLastName().equals(other.getLastName()) &&
                getBirthDate().equals(other.getBirthDate()) &&
                getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName,
                emailAddress, password, birthDate, registerDate);
    }
}