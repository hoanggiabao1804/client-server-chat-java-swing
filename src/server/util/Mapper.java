package util;

import domain.User;
import domain.UserMetadata;

public class Mapper {
    public static UserMetadata userToUserMetadata(User user) {
        return new UserMetadata(user.getId().toString(), user.getName(), user.getEmail(), user.getDob(),
                user.getGender());
    }
}
