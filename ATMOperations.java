import java.util.Scanner;
public class ATMOperations {
    private User user;
    public ATMOperations(User user) {
        this.user = user;
    }
    public void showMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("\n1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    showTransactionHistory();
                    break;
                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    Account.withdraw(user, withdrawAmount);
                    break;
                case 3:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    Account.deposit(user, depositAmount);
                    break;
                case 4:
                    System.out.print("Enter recipient User ID: ");
                    scanner.nextLine(); // clear buffer
                    String recipientId = scanner.nextLine();
                    System.out.print("Enter amount to transfer: ");
                    double transferAmount = scanner.nextDouble();
                    User recipient = new User(recipientId, ""); // Normally would retrieve recipient from database
                    Account.transfer(user, recipient, transferAmount);
                    break;
                case 5:
                    System.out.println("Thank you for using the ATM.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 5);
    }

    private void showTransactionHistory() {
        System.out.println("Transaction History:");
        for (String transaction : user.getTransactionHistory()) {
            System.out.println(transaction);
        }
    }
}
