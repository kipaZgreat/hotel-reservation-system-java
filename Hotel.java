package hotel_reservation_systems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Hotel {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Booking> cancelledBookings = new ArrayList<>();
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Hotel() {
        for (int i = 1; i <= 15; i++) rooms.add(new SingleRoom(i));
        for (int i = 16; i <= 30; i++) rooms.add(new SuiteRoom(i));
    }
    public void showMenu() {
        while (true) {
            try {
                System.out.println("\n--- Hotel Reservation System ---");
                System.out.println("1. Book a Room");
                System.out.println("2. Cancel Booking");
                System.out.println("3. Show Available Rooms");
                System.out.println("4. Admin Login (View Guests & Refunds)");
                System.out.println("5. Modify Booking");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(reader.readLine());

                switch (choice) {
                    case 1 -> bookRoom();
                    case 2 -> cancelBooking();
                    case 3 -> showAvailableRooms();
                    case 4 -> adminLogin();
                    case 5 -> modifyBooking();
                    case 6 -> {
                        System.out.println("Thank you for using the system.");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Input error: " + e.getMessage());
            }
        }
    }
   

    private void bookRoom() {
    try {
        if (rooms.stream().noneMatch(Room::isAvailable)) {
            System.out.println("All rooms are booked! Try again later.");
            return;
        }

        System.out.print("Enter guest name: ");
        String name = reader.readLine();
        String phone;
        System.out.print("Enter phone number : ");
        phone = reader.readLine();
        if (!phone.matches("[+\\d\\-() ]+")) {
            while (true) {
                System.out.println("Invalid phone number.");
                System.out.println("1. Try again");
                System.out.println("2. Cancel");
                String choice = reader.readLine();

                if (choice.equals("2")) return;
                if (choice.equals("1")) {
                    System.out.print("Enter phone number ): ");
                    phone = reader.readLine();
                    if (phone.matches("[+\\d\\-() ]+")) break;
                } else {
                    System.out.println("Invalid option.");
                }
            }
        }

        System.out.print("Enter number of nights: ");
        int nights = Integer.parseInt(reader.readLine());

        LocalDate date;
        while (true) {
            System.out.print("Enter booking date (YYYY-MM-DD): ");
            String dateStr = reader.readLine();
            try {
                date = LocalDate.parse(dateStr);
                if (date.isBefore(LocalDate.now())) {
                    throw new Exception("Past date");
                }
                break;
            } catch (Exception e) {
                while (true) {
                    System.out.println("Invalid or past date.");
                    System.out.println("1. Try again");
                    System.out.println("2. Cancel");
                    String choice = reader.readLine();
                    if (choice.equals("2")) return;
                    if (choice.equals("1")) {
                        System.out.print("Enter booking date (YYYY-MM-DD): ");
                        dateStr = reader.readLine();
                        try {
                            date = LocalDate.parse(dateStr);
                            if (date.isBefore(LocalDate.now())) {
                                System.out.println("Date cannot be in the past.");
                                continue;
                            }
                            break;
                        } catch (Exception ex) {
                            System.out.println("Invalid date format.");
                        }
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
                break;
            }
        }

        while (true) {
            Guest guest = new Guest(name, phone);  

            System.out.println("Choose room type:");
            System.out.println("1. Single Room (ETB 200/night)");
            System.out.println("2. Suite Room (ETB 400/night)");
            System.out.print("Enter choice: ");
            int option = Integer.parseInt(reader.readLine());

            String type = option == 1 ? "Single" : option == 2 ? "Suite" : null;
            if (type == null) {
                System.out.println("Invalid room type.");
                continue;
            }

            Room availableRoom = rooms.stream()
                    .filter(r -> r.isAvailable() && r.getRoomType().equalsIgnoreCase(type))
                    .findFirst().orElse(null);

            if (availableRoom == null) {
                System.out.println("No " + type + " rooms available.");
                return;
            }

            double totalPrice = availableRoom.calculatePrice(nights);
            Payment payment = new Payment();
            if (!payment.makePayment(totalPrice)) return;

            availableRoom.bookRoom();
            Booking booking = new Booking(guest, availableRoom, nights);
            booking.setBookingDate(date);
            bookings.add(booking);

            System.out.println("Room " + availableRoom.getRoomNumber()
                    + " booked successfully for Guest ID: " + guest.getGuestId());

            System.out.print("Book another room for the same guest? (1 = Yes, 2 = No): ");
            int anotherChoice = Integer.parseInt(reader.readLine());
            if (anotherChoice != 1) break;
        }
    } catch (IOException | NumberFormatException e) {
        System.out.println("Booking error: " + e.getMessage());
    }
    }

    private void cancelBooking() {
        try {
            System.out.print("Enter guest ID to cancel: ");
            int id = Integer.parseInt(reader.readLine());
            System.out.print("Enter guest name: ");
            String name = reader.readLine();

            Booking found = null;
            for (Booking booking : bookings) {
                if (booking.getGuest().getGuestId() == id &&
                        booking.getGuest().getName().equalsIgnoreCase(name)) {
                    found = booking;
                    break;
                }
            }

            if (found != null) {
                found.getRoom().cancelBooking();
                double refundAmount = found.getRoom().calculatePrice(found.getNights());
                Payment refund = new Payment();
                refund.refund(refundAmount);
                found.setCancelled(true);
                found.setRefunded(true);
                found.setRefundAmount(refundAmount);
                cancelledBookings.add(found);
                bookings.remove(found);
                System.out.println("Booking cancelled for Guest ID " + id + ". Refund processed.");
            } else {
                System.out.println("Booking not found.");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showAvailableRooms() {
        System.out.println("--- Available Rooms ---");
        rooms.stream().filter(Room::isAvailable).forEach(room ->
                System.out.println("Room No: " + room.getRoomNumber() + " | Type: " + room.getRoomType()));
    }

    private void modifyBooking() {
        try {
            System.out.print("Enter your Guest ID: ");
            int id = Integer.parseInt(reader.readLine());
            System.out.print("Enter your Name: ");
            String name = reader.readLine();

            Booking targetBooking = bookings.stream().filter(b ->
                    b.getGuest().getGuestId() == id && b.getGuest().getName().equalsIgnoreCase(name)
            ).findFirst().orElse(null);

            if (targetBooking == null) {
                System.out.println("Booking not found.");
                return;
            }

            Room oldRoom = targetBooking.getRoom();
            int oldNights = targetBooking.getNights();
            double oldTotal = oldRoom.calculatePrice(oldNights);

            System.out.print("Enter new number of nights: ");
            int newNights = Integer.parseInt(reader.readLine());

            System.out.println("Choose new room type:");
            System.out.println("1. Single Room (ETB 200/night)");
            System.out.println("2. Suite Room (ETB 400/night)");
            int roomChoice = Integer.parseInt(reader.readLine());

            String newType = roomChoice == 1 ? "Single" : roomChoice == 2 ? "Suite" : null;
            if (newType == null) {
                System.out.println("Invalid room type.");
                return;
            }

            Room newRoom = rooms.stream().filter(r ->
                    r.isAvailable() && r.getRoomType().equalsIgnoreCase(newType)
            ).findFirst().orElse(null);

            if (newRoom == null) {
                System.out.println("No available rooms of selected type.");
                return;
            }
            double newTotal = newRoom.calculatePrice(newNights);
            double difference = newTotal - oldTotal;

            if (difference > 0) {
                System.out.println("Price increased. Pay extra " + difference + " ETB.");
                if (!new Payment().makePayment(difference)) {
                    System.out.println("Modification cancelled.");
                    return;
                }
            } else if (difference < 0) {
                double refundAmount = Math.abs(difference);
                System.out.println("Price decreased. Refund: " + refundAmount + " ETB.");
                new Payment().refund(refundAmount);
            }

            oldRoom.cancelBooking();
            newRoom.bookRoom();
            Booking modified = new Booking(targetBooking.getGuest(), newRoom, newNights);
            bookings.remove(targetBooking);
            bookings.add(modified);

            System.out.println("Booking modified successfully!");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error modifying booking: " + e.getMessage());
        }
    }

    private void adminLogin() {
        try {
            System.out.print("Enter admin username: ");
            String username = reader.readLine();
            System.out.print("Enter admin password: ");
            String password = reader.readLine();

            if (username.equals("admin") && password.equals("admin123")) {
                System.out.println("Login successful.\n");
                System.out.println("=== Booking Summary ===");
                System.out.println("Total bookings: " + (bookings.size() + cancelledBookings.size()));
                System.out.println("Active bookings: " + bookings.size());
                System.out.println("Cancelled bookings: " + cancelledBookings.size());

                System.out.println("\n=== Active Bookings ===");
                bookings.stream()
                        .sorted(Comparator.comparing(Booking::getBookingDate))
                        .forEach(booking -> {
                            System.out.println("--------------------------------------");
                            System.out.println("Guest Name: " + booking.getGuest().getName());
                            System.out.println("Guest ID: " + booking.getGuest().getGuestId());
                            System.out.println("Phone: " + booking.getGuest().getPhone());
                            System.out.println("Room No: " + booking.getRoom().getRoomNumber());
                            System.out.println("Room Type: " + booking.getRoom().getRoomType());
                            System.out.println("Nights: " + booking.getNights());
                            System.out.println("Booking Date: " + booking.getBookingDate());
                            System.out.println("Total: " + booking.getRoom().calculatePrice(booking.getNights()) + " ETB");
                        });

                System.out.println("\n=== Cancelled Bookings ===");
                if (cancelledBookings.isEmpty()) {
                    System.out.println("No cancellations.");
                } else {
                    cancelledBookings.forEach(booking -> {
                        System.out.println("--------------------------------------");
                        System.out.println("Guest Name: " + booking.getGuest().getName());
                        System.out.println("Guest ID: " + booking.getGuest().getGuestId());
                        System.out.println("Phone: " + booking.getGuest().getPhone());
                        System.out.println("Room No: " + booking.getRoom().getRoomNumber());
                        System.out.println("Room Type: " + booking.getRoom().getRoomType());
                        System.out.println("Nights: " + booking.getNights());
                        System.out.println("Booking Date: " + booking.getBookingDate());
                        System.out.println("Refunded: " + booking.getRefundAmount() + " ETB");
                    });
                }
                } else {
                System.out.println("Invalid admin credentials.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}