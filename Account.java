

class Account {
    int customerId;
    String accountNumber, type, ifsc, branch, status = "ACTIVE";
    double balance;
    Account(int customerId, String accountNumber, String type, String ifsc, String branch, double balance) {
        this.customerId = customerId; this.accountNumber = accountNumber; this.type = type; this.ifsc = ifsc; this.branch = branch; this.balance = balance;
    }
}
