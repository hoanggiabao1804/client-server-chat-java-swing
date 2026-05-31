package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class FieldValidator {

    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String CITIZEN_ID_REGEX = "^\\d+$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){1,18}[a-zA-Z0-9]$";
    private static final String IPV4_REGEX = "^(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)){3}$";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            return "Tên không được để trống.";
        }

        return "";
    }

    public static String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            return "Username không được để trống.";
        }

        if (!Pattern.matches(USERNAME_REGEX, username)) {
            return "Username chỉ chứa các ký tự (a-z, A-Z, 0-9, .,-,_) và có độ dài từ 3-20 ký tự.";
        }

        return "";
    }

    public static String validatePassword(String password) {
        if (password == null || password.isBlank()) {
            return "Mật khẩu không được để trống.";
        }
        return "";
    }

    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Email không được để trống.";
        }

        if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
            return "Email không đúng định dạng.";
        }

        return "";
    }

    public static String validateISBN(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return "Mã sách (ISBN) không được để trống.";
        }

        return "";
    }

    public static String validateAuthor(String author) {
        if (author == null || author.isBlank()) {
            return "Tác giả không được để trống.";
        }

        return "";
    }

    public static String validatePublisher(String publisher) {
        if (publisher == null || publisher.isBlank()) {
            return "Nhà xuất bản không được để trống.";
        }

        return "";
    }

    public static String validateCitizenID(String citizenID) {
        if (citizenID == null || citizenID.isBlank()) {
            return "CCCD/CMND không được để trống.";
        }

        if (!Pattern.matches(CITIZEN_ID_REGEX, citizenID)) {
            return "CCCD/CMND chỉ được chứa các chữ số từ 0-9.";
        }

        return "";
    }

    public static String validateDate(String dateText) {
        if (dateText == null || dateText.isBlank()) {
            return "Thời gian không được để trống.";
        }

        try {
            LocalDate.parse(dateText, dateTimeFormatter);
        } catch (Exception ex) {
            return "Thời gian phải đúng định dạng <dd-MM-yyyy>.";
        }

        return "";
    }

    public static String validateIPv4Address(String ipv4) {
        if (ipv4 == null || ipv4.isBlank()) {
            return "Địa chỉ IPv4 không được để trống.";
        }

        if (ipv4.equals("localhost")) {
            return "";
        }

        if (!Pattern.matches(IPV4_REGEX, ipv4)) {
            return "Địa chỉ IPv4 không hợp lệ.\nIPv4 phải có dạng A.B.C.D trong đó A,B,C,D nằm trong khoảng 0-255.\nĐồng thời không được có leading zeros.\nVí dụ hợp lệ: 192.168.1.10, 255.255.255.255, 0.0.0.0,...\nVí dụ không hợp lệ: 256.1.2.3, 192.168.01.1,...";
        }

        return "";
    }
}
