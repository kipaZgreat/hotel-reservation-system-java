package hotel_reservation_systems;
public abstract class Room {
    protected int roomNumber;
    protected boolean available = true;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isAvailable() { return available; }
    public void bookRoom() { available = false; }
    public void cancelBooking() { available = true; }
    public int getRoomNumber() { return roomNumber; }

    public abstract String getRoomType();
    public abstract double calculatePrice(int nights);
}