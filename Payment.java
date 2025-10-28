package hotel_reservation_systems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Payment {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public boolean makePayment(double amount) throws IOException {
        while (true) {
            System.out.println("Choose payment method:\n1. Cash\n2. online\n3.cancel");
            String method = reader.readLine();
            if (method.equals("1") || method.equals("2")) {
            } else if (method.equals("3")) {
                System.out.println("Payment cancelled.");
                return false;
            } else {
                System.out.println("Invalid option. Please enter '1' for Cash, '2' for online, or '3' to cancel.");
                continue; 
            }

            System.out.print("Enter payment amount (Required: " + amount + " ETB): ");
            double paid;
            try {
                paid = Double.parseDouble(reader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric amount.");
                continue;
            }

            if (paid < amount) {
                System.out.println("Insufficient amount.");
                System.out.println("1. Try again\n2. Cancel");
                String choice = reader.readLine();
                if (choice.equals("2")) {
                    System.out.println("Payment cancelled.");
                    return false;
                }

            } else if (paid > amount) {
                System.out.println("Payment successful. Cash back: " + (paid - amount) + " ETB.");
                return true;
            } else {
                System.out.println("Payment successful.");
                return true;
            }
        }
    }

    public void refund(double amount) {
        System.out.println("Refunded " + amount + " ETB to customer.");
    }
}