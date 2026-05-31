package repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.User;
import util.ObjectMapperFactory;

public class UserRepository implements Repository {

    private static UserRepository userRepository = null;
    private final ObjectMapper objectMapper;

    private static final Map<String, User> storage = new HashMap<>();
    private static final Map<String, User> usernameIndexedStorage = new HashMap<>();

    private UserRepository() {
        this.objectMapper = ObjectMapperFactory.create();
    }

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }

        return userRepository;
    }

    public void importData(String path) {
        try {
            File file = new File(path);

            List<User> users = objectMapper.readValue(file, new TypeReference<List<User>>() {
            });

            storage.putAll(users.stream().collect(Collectors.toMap(User::getId, user -> user)));

            users.forEach(item -> usernameIndexedStorage.put(item.getUsername(), item));

            System.out.println(">>> Imported total " + users.size() + " user(s) from directory '" + path + "'.");

        } catch (FileNotFoundException ex) {
            System.out.println(">>> ERROR: File name '" + path + "' not found.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println(">>> ERROR: Failed to import user from file '" + path + "''.");
            ex.printStackTrace();
        }
    }

    public void exportData(String path) {
        List<User> users = new ArrayList<>(storage.values());
        try {
            // BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            // bw.write("id\\name\\username\\password\\email\\dob\\gender\\createdAt\n");
            // for (User user : storage.values()) {
            // bw.write(Parser.toUserRow(user) + "\n");
            // }

            // System.out.println(">>> Written total " + storage.size() + " user(s) to file
            // '" + path + "'.");

            // bw.close();
            File file = new File(path);
            objectMapper.writeValue(file, users);
            System.out.println(">>> Exported total " + storage.size() + " user(s) to directory '" + path + "'.");
        } catch (IOException ex) {
            System.out.println(">>> ERROR: Failed to import user from file '" + path + "'.");
            ex.printStackTrace();
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    public User findById(String id) {
        return storage.get(id);
    }

    public User findByUsername(String username) {
        return usernameIndexedStorage.get(username);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public User save(User user) {
        String id = user.getId();

        if (storage.containsKey(id)) {
            User currentUser = storage.get(id);

            currentUser.setName(user.getName());
            currentUser.setUsername(user.getUsername());
            currentUser.setPassword(user.getPassword());
            currentUser.setEmail(user.getEmail());
            currentUser.setDob(user.getDob());
            currentUser.setGender(user.getGender());
            currentUser.setCreatedAt(user.getCreatedAt());

            return currentUser;
        }

        storage.put(id, user);
        usernameIndexedStorage.put(user.getUsername(), user);

        return user;
    }

    public void deleteById(String id) {
        if (storage.containsKey(id)) {
            User userToRemove = storage.get(id);
            storage.remove(id, userToRemove);
            usernameIndexedStorage.remove(userToRemove.getUsername(), userToRemove);
        }
    }
}
