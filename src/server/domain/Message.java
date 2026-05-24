package domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextMessage.class, name = "text"),
        @JsonSubTypes.Type(value = FileMessage.class, name = "file"),
        @JsonSubTypes.Type(value = IconMessage.class, name = "icon")
})
public interface Message {
    String getType();

    String getId();

    String getDialogId();

    String getContent();

    String getSenderId();

    String getReceiverId();

    LocalDateTime getTimestamp();

    String getTag();
}
