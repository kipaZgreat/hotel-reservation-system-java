package hotel_reservation_systems;
public class SuiteRoom extends Room {
    public SuiteRoom(int roomNumber) { super(roomNumber); }
    @Override
    public String getRoomType() { return "Suite"; }
    @Override
    public double calculatePrice(int nights) { return nights * 400; }
}