package application.toysocialnetwork.validators;

import application.toysocialnetwork.domain.User;
import application.toysocialnetwork.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static application.toysocialnetwork.utils.Constants.DATE_FORMATTER;
import static application.toysocialnetwork.utils.Constants.EMAIL_PATTERN;

/**
 * Validator Class for the User entity
 */
public class UserValidator  implements Validator<User> {

    @Override
    public void validateQuick(User entity) throws ValidationException {
        ArrayList<String> errors = new ArrayList<>();

        if(!entity.getFirstName().matches("[A-Z][a-z]+(-[A-z][a-z]*)?")) {
            errors.add("First name should contain only letters and start with uppercase letter!\n");
        }
        if(!entity.getLastName().matches("[A-Z][a-z]+(-[A-z][a-z]*)?")) {
            errors.add("First name should contain only letters and start with uppercase letter!\n");
        }

        if(LocalDate.now().getYear() - entity.getBirthDate().getYear() < 14) {
            errors.add("Invalid birthdate, user should be at least 14 years old!\n");
        }

        if(!errors.isEmpty()) {
            throw new ValidationException(errors.stream().
                    reduce("", (str1, str2) -> str1.concat(" ").concat(str2)));
        }
    }

    @Override
    public void validateExhaustively(User entity) throws ValidationException {
        ArrayList<String> errors = new ArrayList<>();
        if (entity.getFirstName().isEmpty()) {
            errors.add("First name cannot be empty string!\n");
        } else if (entity.getFirstName().length() < 3) {
            errors.add("First name should at least contain 2 characters!\n");
        } else if (!Character.isUpperCase(entity.getFirstName().charAt(0))) {
            errors.add("First name needs to start with uppercase letter!\n");
        } else if (!entity.getFirstName().matches("[A-Z][a-z]+(-[A-z][a-z]*)?")) {

            errors.add("First name should contain only letters!\n");
        }

        if (entity.getLastName().isEmpty()) {
            errors.add("Last name cannot be empty string!\n");
        }
        else if (entity.getLastName().length() < 3) {
            errors.add("Last name should at least contain 2 characters!\n");
        }
        else if (!Character.isUpperCase(entity.getLastName().charAt(0))) {
            errors.add("Last name needs to start with uppercase letter!\n");
        }
        else if(!entity.getLastName().matches("[A-Z][a-z]+(-[A-z][a-z]*)?")) {
            errors.add("Last name should contain only letters!\n");
        }

        try {
            String birthDate = entity.getBirthDate().toString();
            LocalDate formattedDate = LocalDate.parse(birthDate,
                    DATE_FORMATTER.withResolverStyle(ResolverStyle.LENIENT));
        }
        catch (DateTimeParseException e) {
            errors.add(e.getMessage());
        }

        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = emailPattern.matcher(entity.getEmailAddress());
        if(!matcher.matches()) {
            errors.add("Email pattern invalid! Please follow the pattern: username@domain.com/ro!");
        }

        if(entity.getPassword().length() != 64) {
            errors.add("Invalid password, please provide a valid format for password!");
        }

        if(!errors.isEmpty()) {
            throw new ValidationException(errors.stream().
                    reduce("", (str1, str2) -> str1.concat(" ").concat(str2)));
        }

    }
}

