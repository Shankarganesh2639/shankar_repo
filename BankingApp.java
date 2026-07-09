import java.util.*;

public class BankingApp {
    static Scanner sc = new Scanner(System.in);
    static Bank bank = new Bank();

    public static void main(String[] args) {
        bank.createDefaultAdmin();
        while (true) {
            System.out.println("\n===== BANKING SYSTEM =====");
            System.out.println("1. Customer Module");
            System.out.println("2. Admin Module");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = readInt();
            if (choice == 1) customerModule();
            else if (choice == 2) adminModule();
            else if (choice == 3) break;
            else System.out.println("Invalid choice");
        }
    }

    static void customerModule() {
        while (true) {
            System.out.println("\n===== CUSTOMER MODULE =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");
            int choice = readInt();
            if (choice == 1) registerCustomer();
            else if (choice == 2) customerLogin();
            else if (choice == 3) forgotPassword();
            else if (choice == 4) return;
            else System.out.println("Invalid choice");
        }
    }

    static void registerCustomer() {
        System.out.print("Full Name: "); String name = sc.nextLine();
        System.out.print("DOB: "); String dob = sc.nextLine();
        System.out.print("Mobile: "); String mobile = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Aadhaar: "); String aadhaar = sc.nextLine();
        System.out.print("PAN: "); String pan = sc.nextLine();
        System.out.print("Address: "); String address = sc.nextLine();
        System.out.print("Username: "); String username = sc.nextLine();
        System.out.print("Password: "); String password = sc.nextLine();

        while(true){
           String res= bank.registerCustomer(name, dob, mobile, email, aadhaar, pan, address, username, password);
           if(res.equals("0")){
            System.err.println("password length is less than 6 so enter valid password");
            System.out.print("enter new password :");
           password= sc.nextLine();
           }
           else if(res.equals("1")){
            System.out.println("email already exist \n enter valid email: ");
            email=sc.nextLine();
           }
           else if(res.equals("2")){
            System.err.println("Mobile number already exist \n enter valid mobile number: ");
            mobile=sc.nextLine();
           }
           else if(res.equals("3")){
            System.out.println("Aadhaar already exists \n enter valid aadhaar no: ");
            aadhaar=sc.nextLine();
           }
           else if(res.equals("4")){
            System.err.println("Usernmae is already exist \n enter valid  username  name: ");
            username=sc.nextLine();
           }
           else{
            System.out.println(res);
            break;
           }


        }
        
    }

    static void customerLogin() {
         Customer customer;
        while(true){
        System.out.print("Username: "); String username = sc.nextLine();
        System.out.print("Password: "); String password = sc.nextLine();
        customer = bank.customerLogin(username, password);
        if (customer == null) {
            System.out.println("Invalid Credentials");
           
        }
        else if (customer.blocked) {
            System.out.println("Customer is blocked. Contact admin.");
            return;
        }
        else{
            break;
        }
        
    }
        customer.lastLogin = new Date();
        customerDashboard(customer);
    }

    static void customerDashboard(Customer customer) {
        while (true) {
            Account account = bank.getAccountByCustomerId(customer.id);
            System.out.println("\n===== CUSTOMER DASHBOARD =====");
            System.out.println("Welcome " + customer.name);
            if (account == null) {
                System.out.println("No account opened yet");
            } else {
                System.out.println("Account Type: " + account.type);
                System.out.println("Account Number: " + account.accountNumber);
                System.out.println("Available Balance: Rs." + account.balance);
                System.out.println("IFSC: " + account.ifsc);
                System.out.println("Branch: " + account.branch);
                System.out.println("Status: " + account.status);
            }
            System.out.println("Last Login: " + customer.lastLogin);
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Mini Statement");
            System.out.println("7. Download CSV Statement");
            System.out.println("8. Update Profile");
            System.out.println("9. Change Password");
            System.out.println("10. Apply Loan");
            System.out.println("11. Fixed Deposit");
            System.out.println("12. Add Beneficiary");
            System.out.println("13. View Notifications");
            System.out.println("14. Raise Complaint");
            System.out.println("15. Logout");
            System.out.print("Enter choice: ");
            int choice = readInt();
            if (choice == 1) openAccount(customer);
            else if (choice == 2) deposit(customer);
            else if (choice == 3) withdraw(customer);
            else if (choice == 4) transfer(customer);
            else if (choice == 5) bank.printTransactions(customer, 0);
            else if (choice == 6) bank.printTransactions(customer, 10);
            else if (choice == 7) bank.printCsvStatement(customer);
            else if (choice == 8) updateProfile(customer);
            else if (choice == 9) changePassword(customer);
            else if (choice == 10) applyLoan(customer);
            else if (choice == 11) createFixedDeposit(customer);
            else if (choice == 12) addBeneficiary(customer);
            else if (choice == 13) bank.printNotifications(customer.id);
            else if (choice == 14) raiseComplaint(customer);
            else if (choice == 15) return;
            else System.out.println("Invalid choice");
        }
    }

    static void openAccount(Customer customer) {
        if (bank.getAccountByCustomerId(customer.id) != null) {
            System.out.println("Account already exists");
            return;
        }
        System.out.println("1. Savings");
        System.out.println("2. Current");
        System.out.print("Choose account type: ");
        String type = readInt() == 1 ? "Savings" : "Current";
        System.out.print("Opening Balance: ");
        Account account = bank.openAccount(customer.id, type, readDouble());
        System.out.println("Account created. Account Number: " + account.accountNumber);
    }

    static Account activeAccount(Customer customer) {
        Account account = bank.getAccountByCustomerId(customer.id);
        if (account == null) {
            System.out.println("Open account first");
            return null;
        }
        if (!account.status.equals("ACTIVE")) {
            System.out.println("Account is " + account.status + ". Transaction not allowed.");
            return null;
        }
        return account;
    }

    static void deposit(Customer customer) {
        Account account = activeAccount(customer);
        if (account == null) return;
        System.out.print("Amount: ");
        System.out.println(bank.deposit(account.accountNumber, readDouble()));
    }

    static void withdraw(Customer customer) {
        Account account = activeAccount(customer);
        if (account == null) return;
        System.out.print("Amount: ");
        System.out.println(bank.withdraw(account.accountNumber, readDouble()));
    }

    static void transfer(Customer customer) {
        Account account = activeAccount(customer);
        if (account == null) return;
        System.out.print("Receiver Account Number: "); String receiver = sc.nextLine();
        System.out.print("Amount: "); double amount = readDouble();
        System.out.println(bank.transfer(account.accountNumber, receiver, amount));
    }

    static void updateProfile(Customer customer) {
        System.out.print("New Address: "); String address = sc.nextLine();
        System.out.print("New Mobile: "); String mobile = sc.nextLine();
        System.out.print("New Email: "); String email = sc.nextLine();
        System.out.println(bank.updateProfile(customer, address, mobile, email));
    }

    static void changePassword(Customer customer) {
        System.out.print("Old Password: "); String oldPassword = sc.nextLine();
        System.out.print("New Password: "); String newPassword = sc.nextLine();
        System.out.print("Confirm Password: "); String confirmPassword = sc.nextLine();
        System.out.println(bank.changePassword(customer, oldPassword, newPassword, confirmPassword));
    }

    static void forgotPassword() {
        System.out.print("Username: "); String username = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.println(bank.forgotPassword(username, email));
    }

    static void applyLoan(Customer customer) {
        System.out.println("1. Home Loan");
        System.out.println("2. Personal Loan");
        System.out.println("3. Education Loan");
        System.out.println("4. Vehicle Loan");
        int choice = readInt();
        String type = choice == 1 ? "Home Loan" : choice == 2 ? "Personal Loan" : choice == 3 ? "Education Loan" : "Vehicle Loan";
        System.out.print("Amount: ");
        System.out.println(bank.applyLoan(customer.id, type, readDouble()));
    }

    static void createFixedDeposit(Customer customer) {
        Account account = activeAccount(customer);
        if (account == null) return;
        System.out.print("FD Amount: "); double amount = readDouble();
        System.out.print("Years: "); int years = readInt();
        System.out.println(bank.createFixedDeposit(customer.id, account.accountNumber, amount, years));
    }

    static void addBeneficiary(Customer customer) {
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Account Number: "); String accountNumber = sc.nextLine();
        System.out.print("Category Friend/Family/Business: "); String category = sc.nextLine();
        System.out.println(bank.addBeneficiary(customer.id, name, accountNumber, category));
    }

    static void raiseComplaint(Customer customer) {
        System.out.print("Complaint: ");
        System.out.println(bank.raiseComplaint(customer.id, sc.nextLine()));
    }

    static void adminModule() {
        System.out.print("Admin Username: "); String username = sc.nextLine();
        System.out.print("Admin Password: "); String password = sc.nextLine();
        Admin admin = bank.adminLogin(username, password);
        if (admin == null) {
            System.out.println("Invalid admin login");
            return;
        }
        while (true) {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            bank.adminDashboard();
            System.out.println("1. View Customers");
            System.out.println("2. Search Customer");
            System.out.println("3. Block Customer");
            System.out.println("4. Activate Customer");
            System.out.println("5. Freeze Account");
            System.out.println("6. Close Account");
            System.out.println("7. Transaction Monitoring");
            System.out.println("8. Loan Management");
            System.out.println("9. Interest Management");
            System.out.println("10. Employee Management");
            System.out.println("11. Branch Management");
            System.out.println("12. Reports");
            System.out.println("13. View Customer Details");
            System.out.println("14. Audit Logs");
            System.out.println("15. Send Notification");
            System.out.println("16. Complaint Management");
            System.out.println("17. Delete Customer");
            System.out.println("18. Logout");
            System.out.print("Enter choice: ");
            int choice = readInt();
            if (choice == 1) bank.viewCustomers();
            else if (choice == 2) searchCustomer();
            else if (choice == 3) blockCustomer(true, admin);
            else if (choice == 4) blockCustomer(false, admin);
            else if (choice == 5) changeAccountStatus("FROZEN", admin);
            else if (choice == 6) changeAccountStatus("CLOSED", admin);
            else if (choice == 7) bank.monitorTransactions();
            else if (choice == 8) manageLoan(admin);
            else if (choice == 9) updateInterest(admin);
            else if (choice == 10) manageEmployee(admin);
            else if (choice == 11) manageBranch(admin);
            else if (choice == 12) bank.reports();
            else if (choice == 13) viewCustomerDetails();
            else if (choice == 14) bank.printAuditLogs();
            else if (choice == 15) sendNotification(admin);
            else if (choice == 16) manageComplaint(admin);
            else if (choice == 17) deleteCustomer(admin);
            else if (choice == 18) return;
            else System.out.println("Invalid choice");
        }
    }

    static void searchCustomer() {
        System.out.print("Search Customer ID / Account No / Mobile / Aadhaar: ");
        bank.search(sc.nextLine());
    }

    static void blockCustomer(boolean block, Admin admin) {
        System.out.print("Customer ID: ");
        System.out.println(bank.blockCustomer(readInt(), block, admin.username));
    }

    static void changeAccountStatus(String status, Admin admin) {
        System.out.print("Account Number: ");
        System.out.println(bank.changeAccountStatus(sc.nextLine(), status, admin.username));
    }

    static void manageLoan(Admin admin) {
        bank.showLoans();
        System.out.print("Loan ID: "); int id = readInt();
        System.out.print("1 Approve, 2 Reject: ");
        String status = readInt() == 1 ? "APPROVED" : "REJECTED";
        System.out.println(bank.updateLoan(id, status, admin.username));
    }

    static void updateInterest(Admin admin) {
        System.out.print("Savings Interest: "); double savings = readDouble();
        System.out.print("FD Interest: "); double fd = readDouble();
        System.out.print("Loan Interest: "); double loan = readDouble();
        bank.updateInterest(savings, fd, loan, admin.username);
    }

    static void manageEmployee(Admin admin) {
        System.out.print("Employee Name: "); String name = sc.nextLine();
        System.out.print("Branch: "); String branch = sc.nextLine();
        System.out.println(bank.addEmployee(name, branch, admin.username));
    }

    static void manageBranch(Admin admin) {
        System.out.print("Branch Name: ");
        System.out.println(bank.addBranch(sc.nextLine(), admin.username));
    }

    static void viewCustomerDetails() {
        System.out.print("Customer ID: ");
        bank.customerDetails(readInt());
    }

    static void sendNotification(Admin admin) {
        System.out.print("Customer ID: "); int id = readInt();
        System.out.print("Message: ");
        System.out.println(bank.sendNotification(id, sc.nextLine(), admin.username));
    }

    static void manageComplaint(Admin admin) {
        bank.showComplaints();
        System.out.print("Complaint ID to resolve: ");
        System.out.println(bank.resolveComplaint(readInt(), admin.username));
    }

    static void deleteCustomer(Admin admin) {
        System.out.print("Customer ID: ");
        System.out.println(bank.deleteCustomer(readInt(), admin.username));
    }

    static int readInt() {
        try { return Integer.parseInt(sc.nextLine()); }
        catch (Exception e) { return -1; }
    }

    static double readDouble() {
        try { return Double.parseDouble(sc.nextLine()); }
        catch (Exception e) { return -1; }
    }
}






class Admin { String username, password; Admin(String username, String password) { this.username = username; this.password = password; } }
class Loan { int id, customerId; String type, status; double amount; Loan(int id, int customerId, String type, double amount, String status) { this.id = id; this.customerId = customerId; this.type = type; this.amount = amount; this.status = status; } }
class FixedDeposit { int id, customerId, years; double amount, interest, maturity; FixedDeposit(int id, int customerId, double amount, int years, double interest, double maturity) { this.id = id; this.customerId = customerId; this.amount = amount; this.years = years; this.interest = interest; this.maturity = maturity; } }
class Beneficiary { int id, customerId; String name, accountNumber, category; Beneficiary(int id, int customerId, String name, String accountNumber, String category) { this.id = id; this.customerId = customerId; this.name = name; this.accountNumber = accountNumber; this.category = category; } }
class NotificationMessage { int id, customerId; String message; Date date = new Date(); NotificationMessage(int id, int customerId, String message) { this.id = id; this.customerId = customerId; this.message = message; } }
class Complaint { int id, customerId; String message, status; Complaint(int id, int customerId, String message, String status) { this.id = id; this.customerId = customerId; this.message = message; this.status = status; } }
class Employee { int id; String name, branch; Employee(int id, String name, String branch) { this.id = id; this.name = name; this.branch = branch; } }
