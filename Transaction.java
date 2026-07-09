
import java.util.*;
class Transaction {
    int id;
    String accountNumber, type, remark;
    double amount, balance;
    Date date = new Date();
    Transaction(int id, String accountNumber, String type, double amount, double balance, String remark) {
        this.id = id; this.accountNumber = accountNumber; this.type = type; this.amount = amount; this.balance = balance; this.remark = remark;
    }
    public String toString() { return date + " | " + type + " | Rs." + amount + " | Balance Rs." + balance + " | " + remark; }
    String toCsv() { return date + "," + type + "," + amount + "," + balance + "," + remark.replace(",", " "); }
}