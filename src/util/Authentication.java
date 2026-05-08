package util;

import domain.User;
import repository.UserRepository;

public class Authentication {
    private static Authentication authentication = null;
    private static User userLogin = null;

    private Authentication() {
    }

    public static Authentication getInstance() {
        if (authentication == null) {
            authentication = new Authentication();
        }

        return authentication;
    }

    public String authenticate(String username, String password) {
        User foundUser = (User) UserRepository.getInstance().findByUsername(username);
        if (foundUser == null) {
            return "Không tìm thấy người dùng.";
        }

        if (!foundUser.getPassword().equals(password)) {
            return "Sai tài khoản hoặc mật khẩu.";
        }

        userLogin = foundUser;

        return "";
    }

    public User getUserLogin() {
        return userLogin;
    }
}
