package model;

import java.math.BigDecimal;

import enums.RoomStatus;
import enums.RoomType;

public class Room {
    private long id;
    private String number;
    private int floor;
    private RoomType type;
    private BigDecimal basePrice;
    private int maxGuests;
    private RoomStatus status;
    private String note;

    public Room(String number, int floor, RoomType type, BigDecimal basePrice,
                int maxGuests, RoomStatus status, String note) {
        this.number = number;
        this.floor = floor;
        this.type = type;
        this.basePrice = basePrice;
        this.maxGuests = maxGuests;
        this.status = status;
        this.note = note;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "Camera " + number + " (" + type + ")";
    }
}
