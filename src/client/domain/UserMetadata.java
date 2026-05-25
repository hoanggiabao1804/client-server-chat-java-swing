package domain;

import java.time.LocalDate;

import constant.GenderEnum;

public class UserMetadata {
    private String id;
    private String name;
    // private String username;
    private String email;
    private LocalDate dob;
    private GenderEnum gender;

    public UserMetadata() {
    }

    public UserMetadata(String id, String name, String email, LocalDate dob, GenderEnum gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return this.dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public GenderEnum getGender() {
        return this.gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }
}
