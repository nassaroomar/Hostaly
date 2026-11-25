package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import enums.BookingStatus;

public class Booking {
    private long id;
    private long guestId;
    private String guestName;
    private long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private BigDecimal cancellationPenalty;
    private String note;

    public Booking(long guestId, String guestName, long roomId, LocalDate checkIn, LocalDate checkOut,
                   BookingStatus status, LocalDateTime createdAt, String note) {
        this.guestId = guestId;
        this.guestName = guestName;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.createdAt = createdAt;
        this.cancellationPenalty = BigDecimal.ZERO;
        this.note = note;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getGuestId() { return guestId; }
    public String getGuestName() { return guestName; }

    public long getRoomId() { return roomId; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }

    public BigDecimal getCancellationPenalty() { return cancellationPenalty; }
    public void setCancellationPenalty(BigDecimal cancellationPenalty) { this.cancellationPenalty = cancellationPenalty; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "Prenotazione " + id + " (" + status + ")";
    }
}
