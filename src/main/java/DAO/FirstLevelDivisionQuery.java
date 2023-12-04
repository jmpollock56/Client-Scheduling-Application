package DAO;

import helper.JDBC;
import model.FirstLevelDivisions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FirstLevelDivisionQuery {

    public static void loadFirstLevelDivisions() throws SQLException {
        String sql = "SELECT * FROM first_Level_divisions";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            int divisionId = rs.getInt("Division_ID");
            String division = rs.getString("Division");
            Timestamp createDate = rs.getTimestamp("Create_Date");
            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdated = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            int countryId = rs.getInt("Country_ID");

            FirstLevelDivisions newDivision = new FirstLevelDivisions(divisionId, division, createDate, createdBy, lastUpdated, lastUpdatedBy, countryId);
            FirstLevelDivisions.addFirstLevelDivision(newDivision);
        }
    }

}
