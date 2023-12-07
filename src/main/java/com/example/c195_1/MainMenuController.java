package com.example.c195_1;

import DAO.AppointmentsQuery;
import DAO.CustomerQuery;
import DAO.UserQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML private ListView<Label> upcomingListView;

    @FXML private Button logoutBtn;
   public static User activeUser;

   public  ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();

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

        appointmentWithinFifteen();

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

    public void toReports(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reports-view.fxml"));
        Parent addPartForm = loader.load();
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();

    }

    public void appointmentWithinFifteen(){
        upcomingAppointments.clear();
        upcomingListView.getItems().clear();
        LocalDateTime currentTime = LocalDateTime.now();

        for (Appointment app : Appointment.getAllAppointments()) {

            long minutesDifference = Duration.between(currentTime, app.getStart()).toMinutes();

            if (currentTime.isAfter(app.getStart())){
                break;
            } else if (Math.abs(minutesDifference) <= 15) {
                upcomingAppointments.add(app);
            }
        }

        if (upcomingAppointments.isEmpty()){
            Label none = new Label("There are no upcoming appointments.");
            upcomingListView.getItems().add(none);
        }

        for (Appointment a: upcomingAppointments){
            Label label = new Label("ID: " + a.getAppointmentId() + " | Date: " + a.getStart().toLocalDate() + " | Time: " + a.getStart().toLocalTime());
            upcomingListView.getItems().add(label);
        }


    }

    public void logout(ActionEvent actionEvent) {
        User.setActiveUser(null);
        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.close();
    }


}
