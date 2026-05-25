package domain.dto;

import java.util.List;

public class CreateGroupRequest {
    private String groupName;
    private String creatorId;
    private String type;
    private List<String> participantIds;

    public CreateGroupRequest() {
    }

    public CreateGroupRequest(String groupName, String creatorId, String type, List<String> participantIds) {
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.type = type;
        this.participantIds = participantIds;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getParticipantIds() {
        return this.participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }
}
