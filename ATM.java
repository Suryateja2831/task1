import java.util.Scanner;

public class ATM {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        User user = new User("1234", "4321"); // Example userId and pin

        System.out.println("Welcome to the ATM!");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String userPin = scanner.nextLine();

        if (user.authenticate(userId, userPin)) {
            ATMOperations operations = new ATMOperations(user);
            operations.showMenu(scanner);
        } else {
            System.out.println("Invalid credentials.");
        }
        Database db = new Database();
        if (db.authenticateUser("1234", "4321")) {
            db.deposit("1234", 200);
            db.showTransactionHistory("1234");
        }
        db.close();
        scanner.close();
       
    }
  

}