package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import domain.User;
import util.Parser;

public class UserRepository implements Repository {

    private static UserRepository userRepository = null;
    private final ObjectMapper objectMapper;

    private static final Map<String, User> storage = new HashMap<>();
    private static final Map<String, User> usernameIndexedStorage = new HashMap<>();

    private UserRepository() {
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }

        return userRepository;
    }

    public void importData(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            String line;
            int lineCount = 0;

            br.readLine(); // Ignore the header line
            while ((line = br.readLine()) != null) {
                User parsedUser = Parser.parseUser(line);

                if (parsedUser != null) {
                    storage.put(parsedUser.getId().toString(), parsedUser);
                    usernameIndexedStorage.put(parsedUser.getUsername(), parsedUser);
                } else {
                    System.out.println(">>> ERROR: Failed to parse user at line " + lineCount);
                    --lineCount;
                }

                ++lineCount;
            }

            System.out.println(">>> Parsed total " + lineCount + " user(s) from file '" + path + "'.");

            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println(">>> ERROR: File name '" + path + "' not found.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println(">>> ERROR: Failed to import user from file '" + path + "''.");
            ex.printStackTrace();
        }
    }

    public void exportData(String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            bw.write("id\\name\\username\\password\\email\\dob\\gender\\createdAt\n");
            for (User user : storage.values()) {
                bw.write(Parser.toUserRow(user) + "\n");
            }

            System.out.println(">>> Written total " + storage.size() + " user(s) to file '" + path + "'.");

            bw.close();
        } catch (FileNotFoundException ex) {
            System.out.println(">>> ERROR: File name '" + path + "' not found.");
            ex.printStackTrace();
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
        String id = user.getId().toString();

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
