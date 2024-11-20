public class Account {
    public static void deposit(User user, double amount) {
        user.setBalance(user.getBalance() + amount);
        user.addTransaction("Deposited: $" + amount);
    }

    public static boolean withdraw(User user, double amount) {
        if (amount <= user.getBalance()) {
            user.setBalance(user.getBalance() - amount);
            user.addTransaction("Withdrew: $" + amount);
            return true;
        } else {
            System.out.println("Insufficient balance!");
            return false;
        }
    }

    public static boolean transfer(User user, User recipient, double amount) {
        if (amount <= user.getBalance()) {
            user.setBalance(user.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);
            user.addTransaction("Transferred: $" + amount);
            recipient.addTransaction("Received: $" + amount);
            return true;
        } else {
            System.out.println("Insufficient balance!");
            return false;
        }
    }
}
