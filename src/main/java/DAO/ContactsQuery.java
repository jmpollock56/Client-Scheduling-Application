package DAO;

import helper.JDBC;
import model.Contacts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactsQuery {

    private static int contactId;
    private static String contactName;
    private static String contactEmail;

    public static void loadContacts() throws SQLException {
        String sql = "SELECT * FROM contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            contactId = rs.getInt("Contact_ID");
            contactName = rs.getString("Contact_Name");
            contactEmail = rs.getString("Email");

            Contacts newContact = new Contacts(contactId, contactName, contactEmail);
            Contacts.addContacts(newContact);
        }




    }



}
