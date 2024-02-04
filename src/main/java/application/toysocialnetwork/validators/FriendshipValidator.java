package application.toysocialnetwork.validators;

import application.toysocialnetwork.domain.Friendship;
import application.toysocialnetwork.exceptions.ValidationException;

import java.time.LocalDateTime;

/**
 * Validator Class for the Friendship entity
 */
public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validateQuick(Friendship entity) throws ValidationException {
        if(entity.getFirstUserId().equals(entity.getSecondUserId())) {
            throw new ValidationException("The user ID's cannot be the same!\n");
        }
    }

    @Override
    public void validateExhaustively(Friendship entity) throws ValidationException {
        String errors = "";
        if(entity.getFirstUserId().equals(entity.getSecondUserId())) {
            errors += "The user ID's cannot be the same!\n";
        }

        if(entity.getFriendsSince().isAfter(LocalDateTime.now())) {
            errors += "Invalid date!\n";
        }

        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}

