package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import enums.InvoiceStatus;

public class Invoice {
    private long id;
    private long bookingId;
    private long guestId;
    private String guestName;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private LocalDateTime issueDate;
    private BigDecimal discount;
    private BigDecimal penalty;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime paidAt;

    public Invoice(long bookingId, long guestId, String guestName, String roomNumber,
                   LocalDate checkIn, LocalDate checkOut, LocalDateTime issueDate,
                   BigDecimal discount, BigDecimal penalty, BigDecimal totalAmount, InvoiceStatus status) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.issueDate = issueDate;
        this.discount = discount;
        this.penalty = penalty;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getBookingId() { return bookingId; }

    public long getGuestId() { return guestId; }
    public String getGuestName() { return guestName; }
    public String getRoomNumber() { return roomNumber; }

    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }

    public LocalDateTime getIssueDate() { return issueDate; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getPenalty() { return penalty; }
    public void setPenalty(BigDecimal penalty) { this.penalty = penalty; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    @Override
    public String toString() {
        return "Fattura " + id + " (" + status + ")";
    }
}
