package domain;

import java.time.LocalDate;
import java.util.UUID;

import constant.GenderEnum;

public class User {
    private UUID id;
    private String name;
    private String username;
    private String password;
    private String email;
    private LocalDate dob;
    private GenderEnum gender;

    private LocalDate createdAt;

    public User() {
    }

    public User(String name, String username, String password, String email, LocalDate dob, GenderEnum gender) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.createdAt = LocalDate.now();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public LocalDate getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
