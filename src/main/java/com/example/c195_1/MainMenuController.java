package com.example.c195_1;

import DAO.AppointmentsQuery;
import DAO.CustomerQuery;
import DAO.UserQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

   @FXML
   private Button logoutBtn;

   public static User activeUser;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            CustomerQuery.loadCustomers();
            AppointmentsQuery.loadAppointments();
            UserQuery.loadUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        activeUser = User.getActiveUser();
        System.out.println("Welcome!");
    }

    public void toCustomers(ActionEvent event) throws IOException {
        Parent addPartForm = FXMLLoader.load(getClass().getResource("customer-list-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

    public void toAppointments(ActionEvent event) throws IOException {
        Parent addPartForm = FXMLLoader.load(getClass().getResource("appointments-list-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

    public void toReports(ActionEvent actionEvent) {
    }

    public void logout(ActionEvent actionEvent) {
        User.setActiveUser(null);
        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.close();
    }


}
