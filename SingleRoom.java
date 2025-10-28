package hotel_reservation_systems;
public class SingleRoom extends Room {
    public SingleRoom(int roomNumber) { super(roomNumber); }
    @Override
    public String getRoomType() { return "Single"; }
    @Override
    public double calculatePrice(int nights) { return nights * 200; }
}