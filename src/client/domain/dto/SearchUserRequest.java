package domain.dto;

public class SearchUserRequest {
    private String keyword;
    private String requesterId;
    private String status;

    public SearchUserRequest() {
    }

    public SearchUserRequest(String keyword, String requesterId, String status) {
        this.keyword = keyword;
        this.requesterId = requesterId;
        this.status = status;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getRequesterId() {
        return this.requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
