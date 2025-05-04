import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReschedulingRequest implements Serializable {
    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }

    private final String requestId;
    private final Appointment originalAppointment;
    private final LocalDateTime proposedTime;
    private RequestStatus status;
    private final String requesterName;
    private final String requestReason;
    private final LocalDateTime requestDate;

    // Constructor
    public ReschedulingRequest(Appointment originalAppointment,
                               LocalDateTime proposedTime,
                               String requesterName,
                               String requestReason) {
        this.requestId = UUID.randomUUID().toString();
        this.originalAppointment = originalAppointment;
        this.proposedTime = proposedTime;
        this.status = RequestStatus.PENDING;
        this.requesterName = requesterName;
        this.requestReason = requestReason;
        this.requestDate = LocalDateTime.now();
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public Appointment getOriginalAppointment() {
        return originalAppointment;
    }

    public LocalDateTime getProposedTime() {
        return proposedTime;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    // Status modification methods
    public void approve() {
        this.status = RequestStatus.APPROVED;
    }

    public void reject() {
        this.status = RequestStatus.REJECTED;
    }

    // Validation method
    public boolean isValidReschedulingRequest() {
        return proposedTime.isAfter(LocalDateTime.now()) &&
               !proposedTime.equals(originalAppointment.getAppointmentTime());
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
            requestId,
            originalAppointment.getAppointmentId(),
            proposedTime.toString(),
            status.name(),
            requesterName,
            requestReason,
            requestDate.toString()
        );
    }

    // Method to get a readable summary of the request
    public String getRequestSummary() {
        return String.format("""
            Rescheduling Request
            Original Appointment: %s
            Proposed Time: %s
            Requester: %s
            Reason: %s
            Status: %s
            Request Date: %s
            """,
            originalAppointment.getAppointmentId(),
            proposedTime.toString(),
            requesterName,
            requestReason,
            status.name(),
            requestDate.toString()
        );
    }
}