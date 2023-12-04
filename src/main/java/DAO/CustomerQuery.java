package DAO;

import helper.JDBC;
import javafx.collections.ObservableList;
import com.example.c195_1.CustomerViewController;
import model.Customer;
import model.User;

import java.sql.*;
import java.time.*;
import java.util.Date;
import java.util.TimeZone;

public class CustomerQuery {

    private static int id;
    private static String name;
    private static String address;
    private static String postCode;
    private static String phoneNumber;
    private static Timestamp createDate;
    private static String createBy;
    private static Timestamp lastUpdated;
    private static String lastUpdateBy;
    private static int divisionId;

    public static int insertCustomer(int id, String name, String address, String postCode, String phoneNumber,
                                     String createBy, String lastUpdateBy, int divId) throws SQLException {

        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        System.out.println(timestamp);

        String sql = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, id);
        ps.setString(2, name);
        ps.setString(3, address);
        ps.setString(4, postCode);
        ps.setString(5, phoneNumber);
        ps.setObject(6, timestamp); //datetime in sql
        ps.setString(7, createBy);
        ps.setObject(8, timestamp); //timestamp in sql
        ps.setString(9, lastUpdateBy);
        ps.setInt(10, divId);

        int rowsAffected = ps.executeUpdate();

        return rowsAffected;

    }


    public static void loadCustomers() throws SQLException {
        String sql = "SELECT * FROM customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        Customer.getCustomers().clear();

        while(rs.next()){
            id = rs.getInt("Customer_ID");
            name = rs.getString("Customer_Name");
            address = rs.getString("Address");
            postCode = rs.getString("Postal_Code");
            phoneNumber = rs.getString("Phone");
            createDate = rs.getTimestamp("Create_Date");
            createBy = rs.getString("Created_By");
            lastUpdated = rs.getTimestamp("Last_Update");
            lastUpdateBy = rs.getString("Last_Updated_By");
            divisionId = rs.getInt("Division_ID");

            Customer customer = new Customer(id, name, address, postCode, phoneNumber, createDate, createBy, lastUpdated, lastUpdateBy, divisionId);

            Customer.addCustomer(customer);
        }
    }

    public static int updateCustomer(int id, String name, String address, String postalCode, String phoneNumber, Timestamp createDate, String createdBy, int divisionId) throws SQLException {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, " +
                "Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);


        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ldt.atZone(ZoneOffset.UTC);
        Timestamp timestamp = Timestamp.from(zdt.toInstant());



        String lastUpdatedBy = User.getActiveUser().getUserName();


        ps.setString(1, name);
        ps.setString(2, address);
        ps.setString(3, postalCode);
        ps.setString(4, phoneNumber);
        ps.setTimestamp(5, createDate);
        ps.setString(6, createdBy);
        ps.setObject(7, timestamp);
        ps.setString(8, lastUpdateBy);
        ps.setInt(9, divisionId);
        ps.setInt(10, id);

        int rowsAffected = ps.executeUpdate();
        Customer updatedCustomer = new Customer(id, name, address, postalCode, phoneNumber, createDate, createdBy, timestamp, lastUpdatedBy, divisionId);

        return rowsAffected;
    }

    public static void deleteCustomer(Customer customer) throws SQLException {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, customer.getId());

        int affectedRows = ps.executeUpdate();

        if (affectedRows == 1){
            System.out.println(customer.getName() + " has been deleted.");
        }
    }
}
