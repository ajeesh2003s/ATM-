import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/newATM";
    private static final String USER = "root";  // Update with your DB username
    private static final String PASSWORD = "Ananthi@123";  // Update with your DB password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("WELCOME TO TMB ATM!");
        System.out.println("ENTER YOUR DEBIT NO:");
        long Atmno = sc.nextLong();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM users WHERE atm_no = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, Atmno);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String userName = rs.getString("name");
                        System.out.println("WELCOME " + userName + "!");
                        System.out.println("PLEASE ENTER YOUR PIN NO!");
                        int pin = sc.nextInt();
                        if (pin == rs.getInt("pin")) {
                            long bal = rs.getLong("balance");
                            while (true) {
                                System.out.println("PLEASE ENTER YOUR CHOICE " + userName);
                                System.out.println("Choice 1 : CHECK BALANCE");
                                System.out.println("Choice 2 : CASH WITHDRAWAL");
                                System.out.println("Choice 3 : CASH DEPOSIT");
                                System.out.println("PLEASE ENTER 4 TO EXIT");
                                int choice = sc.nextInt();
                                if (choice == 4) {
                                    // Update balance in the database
                                    String updateQuery = "UPDATE users SET balance = ? WHERE atm_no = ?";
                                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                        updateStmt.setLong(1, bal);
                                        updateStmt.setLong(2, Atmno);
                                        updateStmt.executeUpdate();
                                    }
                                    System.out.println("THANK YOU FOR USING OUR ATM");
                                    System.out.println(LocalDate.now());
                                    System.out.println(LocalTime.now());
                                    break;
                                } else {
                                    switch (choice) {
                                        case 1:
                                            System.out.printf("YOUR CURRENT BALANCE: %d\n", bal);
                                            break;
                                        case 2:
                                            System.out.printf("ENTER THE AMOUNT NEED TO BE WITHDRAWN:");
                                            long withdrawal = sc.nextLong();
                                            if (withdrawal <= bal) {
                                                bal -= withdrawal;
                                            } else {
                                                System.out.println("INSUFFICIENT BALANCE");
                                                System.out.printf("PLEASE ENTER THE AMOUNT LESS THAN OR EQUAL TO : %d\n", bal);
                                            }
                                            break;
                                        case 3:
                                            System.out.println("ENTER THE AMOUNT TO BE DEPOSITED");
                                            long deposit = sc.nextLong();
                                            bal += deposit;
                                            System.out.printf("YOUR NEW BALANCE: %d\n", bal);
                                            break;
                                        default:
                                            System.out.println("INVALID CHOICE, ENTER A VALID CHOICE FROM THE LIST");
                                    }
                                }
                            }
                        } else {
                            System.out.println("INVALID PIN");
                        }
                    } else {
                        System.out.println("INVALID DEBIT NO");
                        System.out.println("PLEASE ENTER A VALID DEBIT NO");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
