package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import constant.GenderEnum;
import domain.User;

public class Parser {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static User parseUser(String text) {
        String[] parts = text.strip().split("\\\\");

        if (parts.length != 8) {
            System.out.println(
                    ">>> ERROR: Failed to parse user. Expected 8 fields but received " + parts.length
                            + " fields.");
            return null;
        }

        User user = new User();

        try {
            user.setId(parts[0].strip());
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to parse user's id.");
            return null;
        }

        user.setName(parts[1].strip());
        user.setUsername(parts[2].strip());
        user.setPassword(parts[3].strip());
        user.setEmail(parts[4].strip());

        try {
            user.setDob(LocalDate.parse(parts[5], dateTimeFormatter));
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to parse user's date of birth.");
            return null;
        }

        try {
            user.setGender(GenderEnum.valueOf(parts[6]));
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to parse user's gender.");
            return null;
        }

        try {
            user.setCreatedAt(LocalDate.parse(parts[7], dateTimeFormatter));
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to parse user's creation date.");
            return null;
        }

        return user;
    }

    public static String toUserRow(User user) {
        return String.format("%s\\%s\\%s\\%s\\%s\\%s\\%s\\%s", user.getId(),
                user.getName(), user.getUsername(), user.getPassword(), user.getEmail(),
                user.getDob().format(dateTimeFormatter),
                user.getGender(), user.getCreatedAt().format(dateTimeFormatter));
    }

}