package hotel_reservation_systems;
public class Guest {
    private static int counter = 1000;
    private final int guestId;
    private final String name;
    private final String phone;

    public Guest(String name, String phone) {
        this.guestId = counter++;
        this.name = name;
        this.phone = phone;
    }

    public int getGuestId() { return guestId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}