package DAO;

import helper.JDBC;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class AppointmentsQuery {

    private static int id;
    private static String title;
    private static String description;
    private static String location;
    private static String type;
    private static LocalDateTime start;
    private static LocalDateTime end;
    private static Timestamp createDate;
    private static String createdBy;
    private static Timestamp lastUpdate;
    private static String lastUpdatedBy;
    private static int customerId;
    private static int userId;
    private static int contactId;


    public static void loadAppointments() throws SQLException {
        String sql = "SELECT * FROM appointments";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();


        while(rs.next()){
            id = rs.getInt("Appointment_ID");
            title = rs.getString("Title");
            description = rs.getString("Description");
            location = rs.getString("Location");
            type = rs.getString("Type");
            start = (rs.getTimestamp("Start").toLocalDateTime());
            end = (rs.getTimestamp("End").toLocalDateTime());
            createDate = rs.getTimestamp("Create_Date");
            createdBy = rs.getString("Created_By");
            lastUpdate = rs.getTimestamp("Last_Update");
            lastUpdatedBy = rs.getString("Last_Updated_By");
            customerId = rs.getInt("Customer_ID");
            userId = rs.getInt("User_ID");
            contactId = rs.getInt("Contact_ID");


            Appointment appointment = new Appointment(id, title, description, location, type, start, end, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);

            Appointment.addAppointment(appointment);


            for (Customer c: Customer.getCustomers()){
                if (c.getId() == appointment.getCustomerId()){
                    c.setAppointments(appointment);
                }
            }
        }
    }

    public static int insertAppointments(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By," +
                "Customer_ID, User_ID, Contact_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        Timestamp startTS = Timestamp.valueOf(appointment.getStart());
        Timestamp endTS = Timestamp.valueOf(appointment.getEnd());

        ps.setInt(1, appointment.getAppointmentId());
        ps.setString(2, appointment.getTitle());
        ps.setString(3, appointment.getDescription());
        ps.setString(4, appointment.getLocation());
        ps.setString(5, appointment.getType());
        ps.setObject(6, startTS);
        ps.setObject(7, endTS);
        ps.setObject(8, appointment.getCreateDate());
        ps.setString(9, appointment.getCreatedBy());
        ps.setObject(10, appointment.getLastUpdate());
        ps.setString(11, appointment.getLastUpdatedBy());
        ps.setInt(12, appointment.getCustomerId());
        ps.setInt(13, appointment.getUserId());
        ps.setInt(14, appointment.getContactId());

        int rowsAffected = ps.executeUpdate();

        return rowsAffected;
    }

    public static void deleteAppointment(Appointment appointment) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, appointment.getAppointmentId());

        int affectedRows = ps.executeUpdate();

        if (affectedRows == 1){
            System.out.println("Appointment has been deleted.");
        }
    }

    public static int updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET  Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Create_Date = ?, Created_By = ?, " +
                "Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        Timestamp startTS = Timestamp.valueOf(appointment.getStart());
        Timestamp endTS = Timestamp.valueOf(appointment.getEnd());

        ps.setString(1, appointment.getTitle());
        ps.setString(2, appointment.getDescription());
        ps.setString(3, appointment.getLocation());
        ps.setString(4, appointment.getType());
        ps.setObject(5, startTS);
        ps.setObject(6, endTS);
        ps.setObject(7, appointment.getCreateDate());
        ps.setString(8, appointment.getCreatedBy());
        ps.setObject(9, appointment.getLastUpdate());
        ps.setString(10, appointment.getLastUpdatedBy());
        ps.setInt(11, appointment.getCustomerId());
        ps.setInt(12, appointment.getUserId());
        ps.setInt(13, appointment.getContactId());
        ps.setInt(14, appointment.getAppointmentId());

        int affectedRows = ps.executeUpdate();
        return affectedRows;
    }

}
