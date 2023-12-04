package DAO;

import helper.JDBC;
import model.Country;

import java.sql.*;

public class CountryQuery {

    public static void loadCountries() throws SQLException {
        String sql = "SELECT * FROM countries";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            int countryId = rs.getInt("Country_ID");
            String country = rs.getString("Country");
            Timestamp createDate = rs.getTimestamp("Create_Date");
            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");

            Country newCountry = new Country(countryId, country, createDate, createdBy, lastUpdate, lastUpdatedBy);
            Country.addCountries(newCountry);

        }
    }
}
