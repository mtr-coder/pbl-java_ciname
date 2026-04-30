package models;

public class seat {
    private int id;
    private int roomId;
    private String seatNumber;

    public seat() {}

    public seat(int id, int roomId, String seatNumber) {
        this.id = id;
        this.roomId = roomId;
        this.seatNumber = seatNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
}
