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

import domain.User;
import util.Parser;

public class UserRepository implements Repository {

    private static UserRepository userRepository = null;

    private static final Map<String, User> storage = new HashMap<>();
    private static final Map<String, User> usernameIndexedStorage = new HashMap<>();

    private UserRepository() {
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

    public List<Object> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Object findById(String id) {
        return storage.get(id);
    }

    public Object findByUsername(String username) {
        return usernameIndexedStorage.get(username);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public Object save(Object user) {
        User castedUser = (User) user;
        String id = castedUser.getId().toString();

        if (storage.containsKey(id)) {
            User currentUser = storage.get(id);

            currentUser.setName(castedUser.getName());
            currentUser.setUsername(castedUser.getUsername());
            currentUser.setPassword(castedUser.getPassword());
            currentUser.setEmail(castedUser.getEmail());
            currentUser.setDob(castedUser.getDob());
            currentUser.setGender(castedUser.getGender());
            currentUser.setCreatedAt(castedUser.getCreatedAt());

            return currentUser;
        }

        storage.put(id, castedUser);
        usernameIndexedStorage.put(castedUser.getUsername(), castedUser);

        return castedUser;
    }

    public void deleteById(String id) {
        if (storage.containsKey(id)) {
            User userToRemove = storage.get(id);
            storage.remove(id, userToRemove);
            usernameIndexedStorage.remove(userToRemove.getUsername(), userToRemove);
        }
    }
}
