import java.util.*;
class Customer {
    int id;
    String name, dob, mobile, email, aadhaar, pan, address, username, password;
    boolean blocked;
    Date lastLogin = new Date();
    Customer(int id, String name, String dob, String mobile, String email, String aadhaar, String pan, String address, String username, String password) {
        this.id = id; this.name = name; this.dob = dob; this.mobile = mobile; this.email = email; this.aadhaar = aadhaar; this.pan = pan; this.address = address; this.username = username; this.password = password;
    }
}