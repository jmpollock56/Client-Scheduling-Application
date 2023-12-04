package com.example.c195_1;

import DAO.ContactsQuery;
import DAO.CountryQuery;
import DAO.FirstLevelDivisionQuery;
import DAO.UserQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LoginController implements Initializable {
    @FXML public PasswordField userPassword;
    @FXML public TextField userUserName;
    @FXML public Label userLocation;
    @FXML public Button closeBtn;
    public String userName;
    public String password;
    public boolean isUser = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Please Log in.");

        try {
            CountryQuery.loadCountries();
            FirstLevelDivisionQuery.loadFirstLevelDivisions();
            ContactsQuery.loadContacts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID()); //Get zoneID of user's system.

        userLocation.setText("Your Location: " + localZoneId.toString());
    }

    @FXML
    public void login(ActionEvent event) throws IOException {

        userName = userUserName.getText();
        password = userPassword.getText();

        if (userUserName.getText().isEmpty() || userPassword.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing UserName and/or Password");
            alert.setHeaderText("Warning");
            alert.setContentText("Please fill out both Username and Password.");

            alert.getButtonTypes().setAll(ButtonType.OK);

            Optional<ButtonType> result = alert.showAndWait();

            return;
        }

        try {
            isUser = UserQuery.VerifyUser(userName, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        if(isUser){

            Parent addPartForm = FXMLLoader.load(getClass().getResource("main-menu-view.fxml"));
            Scene addPartScene = new Scene(addPartForm);
            Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            addPartStage.setScene(addPartScene);
            addPartStage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText("Warning");
            alert.setContentText("Not a valid Username/Password.");

            alert.getButtonTypes().setAll(ButtonType.OK);

            Optional<ButtonType> result = alert.showAndWait();
            userUserName.clear();
            userPassword.clear();
            return;

        }

    }

    public void close(ActionEvent event) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}