package models;

import java.sql.Timestamp;

public class showtime {
    private int id;
    private int movieId;
    private int roomId;
    private Timestamp startTime;

    public showtime(int id, int movieId, int roomId, Timestamp startTime) {
        this.id = id;
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

}
