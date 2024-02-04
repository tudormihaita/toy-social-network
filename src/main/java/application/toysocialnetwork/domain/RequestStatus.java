package application.toysocialnetwork.domain;


import java.util.Arrays;

public enum RequestStatus {
    APPROVED("approved"),
    REJECTED("rejected"),
    PENDING("pending"),
    CANCELLED("cancelled");

    private final String status;

    RequestStatus(String status) { this.status = status; }

    public String getStatusAsString() { return status; }

    public static RequestStatus fromString(String statusStr) {
        return Arrays.stream(values()).
                filter(status -> status.getStatusAsString().equals(statusStr)).
                findFirst().orElse(null);
    }
}
