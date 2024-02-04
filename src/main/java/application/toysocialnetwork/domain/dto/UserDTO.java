package application.toysocialnetwork.domain.dto;

import java.time.LocalDate;
import java.util.Objects;

public class UserDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final String emailAddress;

    public UserDTO(Long id, String firstName, String lastName, LocalDate birthDate, String emailAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.emailAddress = emailAddress;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO userDTO)) return false;
        return Objects.equals(getId(), userDTO.getId()) && Objects.equals(getFirstName(), userDTO.getFirstName()) && Objects.equals(getLastName(), userDTO.getLastName()) && Objects.equals(getBirthDate(), userDTO.getBirthDate()) && Objects.equals(getEmailAddress(), userDTO.getEmailAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getBirthDate(), getEmailAddress());
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
