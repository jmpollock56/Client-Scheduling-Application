package DAO;

import helper.JDBC;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;

public class UserQuery {
    private static String userName;
    private static String password;
    private static int userId;
    private static Timestamp createDate;
    private static String createdBy;
    private static Timestamp lastUpdated;
    private static String lastUpdatedBy;



    public static boolean VerifyUser(String sentUserName, String sentPassword) throws SQLException {
        String sql = "SELECT * FROM users";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            userId = rs.getInt("User_ID");
            userName = rs.getString("User_Name");
            password = rs.getString("Password");
            createDate = rs.getTimestamp("Create_Date");
            createdBy = rs.getString("Created_By");
            lastUpdated = rs.getTimestamp("Last_Update");
            lastUpdatedBy = rs.getString("Last_Updated_By");

            User user = new User(userId, userName, password, createDate, createdBy, lastUpdated, lastUpdatedBy);




            if (sentUserName.equals(userName) && sentPassword.equals(password)){
                User.setActiveUser(user);
                return true;
            }
        }
        return false;
    }

    public static void loadUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        User.getUsers().clear();

        while (rs.next()) {
            userId = rs.getInt("User_ID");
            userName = rs.getString("User_Name");
            password = rs.getString("Password");
            createDate = rs.getTimestamp("Create_Date");
            createdBy = rs.getString("Created_By");
            lastUpdated = rs.getTimestamp("Last_Update");
            lastUpdatedBy = rs.getString("Last_Updated_By");

            User user = new User(userId, userName, password, createDate, createdBy, lastUpdated, lastUpdatedBy);

            User.addUser(user);


        }
    }
}
