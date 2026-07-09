import java.util.*;

class Bank {
    Map<Integer, Customer> customers = new HashMap<>();
    Map<String, Account> accounts = new HashMap<>();
    Map<String, Admin> admins = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();
    List<Loan> loans = new ArrayList<>();
    List<FixedDeposit> fixedDeposits = new ArrayList<>();
    List<Beneficiary> beneficiaries = new ArrayList<>();
    List<NotificationMessage> notifications = new ArrayList<>();
    List<Complaint> complaints = new ArrayList<>();
    List<Employee> employees = new ArrayList<>();
    List<String> branches = new ArrayList<>();
    List<String> auditLogs = new ArrayList<>();
    Random random = new Random();
    int customerId = 1, transactionId = 1, loanId = 1, fdId = 1, beneficiaryId = 1, notificationId = 1, complaintId = 1, employeeId = 1;
    double savingsInterest = 4.0, fdInterest = 6.5, loanInterest = 9.0;

    void createDefaultAdmin() {
        admins.put("admin", new Admin("admin", "admin123"));
        branches.add("Main Branch");
    }

    String registerCustomer(String name, String dob, String mobile, String email, String aadhaar, String pan, String address, String username, String password) {
        if (password.length() < 6) return "0";
        for (Customer c : customers.values()) {
            if (c.email.equalsIgnoreCase(email)) return "1";
            if (c.mobile.equals(mobile)) return "2";
            if (c.aadhaar.equals(aadhaar)) return "3";
            if (c.username.equals(username)) return "4";
        }
        Customer c = new Customer(customerId++, name, dob, mobile, email, aadhaar, pan, address, username, password);
        customers.put(c.id, c);
        return "Registration successful. Customer ID: " + c.id;
    }

    Customer customerLogin(String username, String password) {
        for (Customer c : customers.values()) if (c.username.equals(username) && c.password.equals(password)) return c;
        return null;
    }

    Admin adminLogin(String username, String password) {
        Admin admin = admins.get(username);
        if (admin != null && admin.password.equals(password)) {
            audit(username + " logged in");
            return admin;
        }
        return null;
    }

    Account openAccount(int customerId, String type, double balance) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(customerId, accountNumber, type, "BANK0001234", "Main Branch", balance);
        accounts.put(accountNumber, account);
        addTransaction(accountNumber, "DEPOSIT", balance, balance, "Opening Balance");
        notifyCustomer(customerId, "Account created: " + accountNumber);
        return account;
    }

    String deposit(String accountNumber, double amount) {
        if (amount <= 0) return "Amount must be greater than zero";
        Account account = accounts.get(accountNumber);
        double oldBalance = account.balance;
        account.balance += amount;
        addTransaction(accountNumber, "DEPOSIT", amount, account.balance, "Old Balance " + oldBalance + ", New Balance " + account.balance);
        notifyCustomer(account.customerId, "Rs." + amount + " Deposited");
        return "Deposit successful. Old Balance: Rs." + oldBalance + ", New Balance: Rs." + account.balance;
    }

    String withdraw(String accountNumber, double amount) {
        if (amount <= 0) return "Amount must be greater than zero";
        Account account = accounts.get(accountNumber);
        if (account.balance < amount) return "Insufficient Balance";
        account.balance -= amount;
        addTransaction(accountNumber, "WITHDRAW", amount, account.balance, "Cash Withdrawal");
        notifyCustomer(account.customerId, "Rs." + amount + " Debited");
        return "Withdraw successful. Balance: Rs." + account.balance;
    }

    String transfer(String senderNumber, String receiverNumber, double amount) {
        if (senderNumber.equals(receiverNumber)) return "Cannot transfer to own account";
        Account sender = accounts.get(senderNumber);
        Account receiver = accounts.get(receiverNumber);
        if (receiver == null) return "Receiver does not exist";
        if (!receiver.status.equals("ACTIVE")) return "Receiver account is not active";
        if (amount <= 0) return "Amount must be greater than zero";
        if (sender.balance < amount) return "Insufficient Balance";
        sender.balance -= amount;
        receiver.balance += amount;
        addTransaction(senderNumber, "TRANSFER", amount, sender.balance, "Transfer to " + receiverNumber);
        addTransaction(receiverNumber, "DEPOSIT", amount, receiver.balance, "Transfer from " + senderNumber);
        notifyCustomer(sender.customerId, "Rs." + amount + " transferred");
        notifyCustomer(receiver.customerId, "Rs." + amount + " received");
        return "Transfer successful. Balance: Rs." + sender.balance;
    }

    void printTransactions(Customer customer, int limit) {
        Account account = getAccountByCustomerId(customer.id);
        if (account == null) { System.out.println("No account found"); return; }
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : transactions) if (t.accountNumber.equals(account.accountNumber)) list.add(t);
        int start = limit == 0 ? 0 : Math.max(0, list.size() - limit);
        System.out.println("Date | Type | Amount | Balance | Remark");
        for (int i = start; i < list.size(); i++) System.out.println(list.get(i));
    }

    void printCsvStatement(Customer customer) {
        Account account = getAccountByCustomerId(customer.id);
        if (account == null) { System.out.println("No account found"); return; }
        System.out.println("Date,Type,Amount,Balance,Remark");
        for (Transaction t : transactions) if (t.accountNumber.equals(account.accountNumber)) System.out.println(t.toCsv());
    }

    String updateProfile(Customer customer, String address, String mobile, String email) {
        for (Customer c : customers.values()) {
            if (c.id != customer.id && c.mobile.equals(mobile)) return "Mobile already exists";
            if (c.id != customer.id && c.email.equalsIgnoreCase(email)) return "Email already exists";
        }
        customer.address = address;
        customer.mobile = mobile;
        customer.email = email;
        return "Profile updated. Aadhaar, PAN, Account Number cannot be updated";
    }

    String changePassword(Customer customer, String oldPassword, String newPassword, String confirmPassword) {
        if (!customer.password.equals(oldPassword)) return "Old password is wrong";
        if (!newPassword.equals(confirmPassword)) return "New password and confirm password not matched";
        if (newPassword.length() < 6) return "Password minimum length is 6";
        customer.password = newPassword;
        return "Password changed successfully";
    }

    String forgotPassword(String username, String email) {
        for (Customer c : customers.values()) if (c.username.equals(username) && c.email.equalsIgnoreCase(email)) return "OTP sent. Demo OTP: " + (100000 + random.nextInt(900000));
        return "Invalid username or email";
    }

    String applyLoan(int customerId, String type, double amount) {
        loans.add(new Loan(loanId++, customerId, type, amount, "PENDING"));
        return "Loan applied. Status: Pending";
    }

    String createFixedDeposit(int customerId, String accountNumber, double amount, int years) {
        Account account = accounts.get(accountNumber);
        if (amount <= 0 || years <= 0) return "Invalid FD details";
        if (account.balance < amount) return "Insufficient Balance";
        account.balance -= amount;
        double interest = amount * fdInterest * years / 100;
        double maturity = amount + interest;
        fixedDeposits.add(new FixedDeposit(fdId++, customerId, amount, years, interest, maturity));
        addTransaction(accountNumber, "WITHDRAW", amount, account.balance, "Fixed Deposit Created");
        return "FD created. Interest: Rs." + interest + ", Maturity Amount: Rs." + maturity;
    }

    String addBeneficiary(int customerId, String name, String accountNumber, String category) {
        if (!accounts.containsKey(accountNumber)) return "Beneficiary account not found";
        beneficiaries.add(new Beneficiary(beneficiaryId++, customerId, name, accountNumber, category));
        return "Beneficiary added";
    }

    String raiseComplaint(int customerId, String message) {
        complaints.add(new Complaint(complaintId++, customerId, message, "OPEN"));
        return "Complaint raised";
    }

    void adminDashboard() {
        double bankBalance = 0, totalDeposits = 0;
        int pendingLoans = 0;
        for (Account a : accounts.values()) bankBalance += a.balance;
        for (Transaction t : transactions) if (t.type.equals("DEPOSIT")) totalDeposits += t.amount;
        for (Loan l : loans) if (l.status.equals("PENDING")) pendingLoans++;
        System.out.println("Total Customers: " + customers.size());
        System.out.println("Total Accounts: " + accounts.size());
        System.out.println("Today Transactions: " + transactions.size());
        System.out.println("Pending Loans: " + pendingLoans);
        System.out.println("Total Deposits: Rs." + totalDeposits);
        System.out.println("Bank Balance: Rs." + bankBalance);
    }

    void viewCustomers() {
        for (Customer c : customers.values()) System.out.println(c.id + " | " + c.name + " | " + c.mobile + " | " + c.email + " | Blocked: " + c.blocked);
    }

    void search(String key) {
        for (Customer c : customers.values()) {
            Account a = getAccountByCustomerId(c.id);
            boolean accountMatch = a != null && a.accountNumber.equals(key);
            if (("" + c.id).equals(key) || c.mobile.equals(key) || c.aadhaar.equals(key) || accountMatch) System.out.println(c.id + " | " + c.name + " | " + c.mobile);
        }
    }

    String blockCustomer(int id, boolean block, String adminUsername) {
        Customer customer = customers.get(id);
        if (customer == null) return "Customer not found";
        customer.blocked = block;
        audit(adminUsername + (block ? " blocked " : " activated ") + "customer " + id);
        return block ? "Customer blocked" : "Customer activated";
    }

    String changeAccountStatus(String accountNumber, String status, String adminUsername) {
        Account account = accounts.get(accountNumber);
        if (account == null) return "Account not found";
        account.status = status;
        audit(adminUsername + " changed account " + accountNumber + " to " + status);
        return "Account status updated to " + status;
    }

    void monitorTransactions() { for (Transaction t : transactions) System.out.println(t); }
    void showLoans() { for (Loan l : loans) System.out.println(l.id + " | Customer " + l.customerId + " | " + l.type + " | Rs." + l.amount + " | " + l.status); }

    String updateLoan(int id, String status, String adminUsername) {
        for (Loan l : loans) if (l.id == id) { l.status = status; notifyCustomer(l.customerId, "Loan " + status); audit(adminUsername + " updated loan " + id); return "Loan updated"; }
        return "Loan not found";
    }

    void updateInterest(double savings, double fd, double loan, String adminUsername) {
        savingsInterest = savings;
        fdInterest = fd;
        loanInterest = loan;
        audit(adminUsername + " updated interest rates");
        System.out.println("Interest rates updated");
    }

    String addEmployee(String name, String branch, String adminUsername) {
        employees.add(new Employee(employeeId++, name, branch));
        audit(adminUsername + " added employee " + name);
        return "Employee added";
    }

    String addBranch(String branch, String adminUsername) {
        branches.add(branch);
        audit(adminUsername + " added branch " + branch);
        return "Branch added";
    }

    void reports() {
        System.out.println("Daily Report Transactions: " + transactions.size());
        System.out.println("Monthly Report Transactions: " + transactions.size());
        System.out.println("Yearly Report Transactions: " + transactions.size());
        System.out.println("Customer Report: " + customers.size());
        System.out.println("Transaction Report: " + transactions.size());
    }

    void customerDetails(int id) {
        Customer c = customers.get(id);
        if (c == null) { System.out.println("Customer not found"); return; }
        System.out.println(c.name + " | " + c.mobile + " | " + c.email + " | " + c.address);
        Account a = getAccountByCustomerId(id);
        if (a != null) System.out.println("Account: " + a.accountNumber + " | Balance: Rs." + a.balance + " | " + a.status);
        for (Loan l : loans) if (l.customerId == id) System.out.println("Loan: " + l.type + " | " + l.status);
        for (FixedDeposit f : fixedDeposits) if (f.customerId == id) System.out.println("FD: Rs." + f.amount + " | Maturity Rs." + f.maturity);
        for (Beneficiary b : beneficiaries) if (b.customerId == id) System.out.println("Beneficiary: " + b.name + " | " + b.accountNumber);
    }

    String sendNotification(int id, String message, String adminUsername) {
        if (!customers.containsKey(id)) return "Customer not found";
        notifyCustomer(id, message);
        audit(adminUsername + " sent notification to customer " + id);
        return "Notification sent";
    }

    void showComplaints() { for (Complaint c : complaints) System.out.println(c.id + " | Customer " + c.customerId + " | " + c.message + " | " + c.status); }

    String resolveComplaint(int id, String adminUsername) {
        for (Complaint c : complaints) if (c.id == id) { c.status = "RESOLVED"; notifyCustomer(c.customerId, "Complaint resolved"); audit(adminUsername + " resolved complaint " + id); return "Complaint resolved"; }
        return "Complaint not found";
    }

    String deleteCustomer(int id, String adminUsername) {
        Customer customer = customers.remove(id);
        if (customer == null) return "Customer not found";
        accounts.values().removeIf(a -> a.customerId == id);
        audit(adminUsername + " deleted customer " + id);
        return "Customer deleted";
    }

    void printNotifications(int customerId) {
        for (NotificationMessage n : notifications) if (n.customerId == customerId) System.out.println(n.date + " | " + n.message);
    }

    void printAuditLogs() {
        for (String log : auditLogs) System.out.println(log);
    }

    Account getAccountByCustomerId(int id) {
        for (Account a : accounts.values()) if (a.customerId == id) return a;
        return null;
    }

    String generateAccountNumber() {
        String number;
        do { number = "100" + (10000000 + random.nextInt(90000000)); } while (accounts.containsKey(number));
        return number;
    }

    void addTransaction(String accountNumber, String type, double amount, double balance, String remark) { transactions.add(new Transaction(transactionId++, accountNumber, type, amount, balance, remark)); }
    void notifyCustomer(int customerId, String message) { notifications.add(new NotificationMessage(notificationId++, customerId, message)); }
    void audit(String message) { auditLogs.add(new Date() + " | " + message + " | IP: 127.0.0.1"); }
}