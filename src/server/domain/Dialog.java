package domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Dialog {
    private String id;
    private String name;
    private List<String> participants;

    @JsonIgnore
    private List<Message> messages;

    public Dialog() {
    }

    public Dialog(String id, String name, List<String> participants, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.participants = participants;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
