package com.example.c195_1;

import DAO.AppointmentsQuery;
import DAO.UserQuery;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Contacts;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AddAppointmentController implements Initializable {
    @FXML public ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<User> userComboBox;
    @FXML private TextField apptTitleField;
    @FXML private TextArea apptDescField;
    @FXML private TextField apptLocationField;
    @FXML private TextField apptTypeField;
    @FXML private ComboBox<Contacts> apptContactCombo;
    @FXML private DatePicker apptDatePicker;
    @FXML private ComboBox<LocalTime> apptStartTimeCombo;
    @FXML private ComboBox<LocalTime> apptEndTimeCombo;
    private Customer currentCustomer;
    private LocalTime startBusinessHour = LocalTime.of(8,0);
    private LocalTime endBusinessHour = LocalTime.of(22,0);
    private ZoneId companyTimezone = ZoneId.of("America/New_York"); //EST
    private ZonedDateTime userEndZDT;
    private boolean overlap = false;
    private ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    private ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();
    private ObservableList<User> users = User.getUsers();
    private ObservableList<Customer> customers = Customer.getCustomers();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        apptContactCombo.setItems(Contacts.getContacts());
        customerComboBox.setItems(customers);
        userComboBox.setItems(users);
    }

    public void saveAppointment(ActionEvent event) throws IOException {
        Random random = new Random();

        //check if all fields have values before saving
        if (apptTitleField.getText().isEmpty() || apptDescField.getText().isEmpty() || apptLocationField.getText().isEmpty() || apptTypeField.getText().isEmpty() ||
                apptContactCombo.getItems().isEmpty() || apptDatePicker.getValue() == null || apptStartTimeCombo.getItems().isEmpty() || apptEndTimeCombo.getItems().isEmpty()){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("Warning");
            alert.setContentText("Please fill out all of the fields.");

            alert.getButtonTypes().setAll(ButtonType.OK);

            Optional<ButtonType> result = alert.showAndWait();

            return;
        }

        //Get field values for input
        int apptId = random.nextInt(90000) + 10000;
        String apptTitle = apptTitleField.getText();
        String apptDesc = apptDescField.getText();
        String location = apptLocationField.getText();
        String type = apptTypeField.getText();
        int contactId = apptContactCombo.getSelectionModel().getSelectedItem().getContactId();
        LocalDate userDate =  apptDatePicker.getValue();
        LocalTime startTime = apptStartTimeCombo.getSelectionModel().getSelectedItem();
        LocalTime endTime = apptEndTimeCombo.getSelectionModel().getSelectedItem();
        int userId = userComboBox.getSelectionModel().getSelectedItem().getId();
        int customerId = customerComboBox.getSelectionModel().getSelectedItem().getId();


        //create the LDT's
        LocalDateTime startLDT = LocalDateTime.of(userDate, startTime);
        LocalDateTime endLDT = LocalDateTime.of(userDate, endTime);
        System.out.println("Start: " + startLDT + " ---- End: " + endLDT);


        if (startLDT.isBefore(LocalDateTime.now())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Date Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please choose a date and time that is after date.");

            alert.getButtonTypes().setAll(ButtonType.OK);

            Optional<ButtonType> result = alert.showAndWait();

            return;
        }


        for (Appointment app: Appointment.getAllAppointments()){
            if ((startLDT.isAfter(app.getStart()) || startLDT.isEqual(app.getStart())) && startLDT.isBefore(app.getEnd())){ // new app time overlap comparison
                overlap = true;
                break;
            } else if ((endLDT.isAfter(app.getStart())) && (endLDT.isBefore(app.getEnd()) || endLDT.isEqual(app.getEnd()))) {
                overlap = true;
                break;
            } else if ((startLDT.isBefore(app.getStart()) || startLDT.isEqual(app.getStart())) && (endLDT.isAfter(app.getEnd()) || endLDT.isEqual(app.getEnd()))) {
                overlap = true;
                break;
            }
        }

        if (overlap){
            showOverlapAlert();
            return;
        }

        //create TS's for create/last-update
        Instant instant = Instant.now();
        Timestamp createDate = Timestamp.from(instant);

        //create new Appointment
        Appointment newAppointment = new Appointment(apptId, apptTitle, apptDesc, location, type, startLDT, endLDT,
                createDate, User.getActiveUser().getUserName(), createDate, User.getActiveUser().getUserName(), customerId,
                userId, contactId);

        //insert appointment into db
        try {
            int rowsAffected = AppointmentsQuery.insertAppointments(newAppointment);

            if (rowsAffected > 0){
                System.out.println(customerComboBox.getSelectionModel().getSelectedItem().getName() + " has been updated with an appt.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Parent addPartForm = FXMLLoader.load(getClass().getResource("appointments-list-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();


    }

    public void onDateSelection(ActionEvent event) {
        LocalDate userDate = apptDatePicker.getValue(); // User date selection

        LocalDateTime startLDT = LocalDateTime.of(userDate, startBusinessHour);
        ZonedDateTime startZDT = ZonedDateTime.of(startLDT, companyTimezone);
        ZonedDateTime userStartZDT = startZDT.withZoneSameInstant(ZoneId.systemDefault());


        LocalDateTime endLDT = LocalDateTime.of(userDate, endBusinessHour);
        ZonedDateTime endZDT = ZonedDateTime.of(endLDT, companyTimezone);
        userEndZDT = endZDT.withZoneSameInstant(ZoneId.systemDefault());



        while(userStartZDT.isBefore(userEndZDT)){
            LocalTime newTime = LocalTime.from(userStartZDT);
            startTimes.add(newTime);

            userStartZDT = userStartZDT.plusMinutes(30);
        }

        apptStartTimeCombo.setPromptText("Choose a start time");
        apptStartTimeCombo.setItems(startTimes);
    }

    private void onStartSelection(ActionEvent event) {
        LocalTime startTimeSelect = apptStartTimeCombo.getSelectionModel().getSelectedItem();
        endTimes.clear();
        populateEndComboBox(startTimeSelect);
    }

    private void populateEndComboBox(LocalTime userStartTime) {
        LocalTime endTimeStart = userStartTime.plusMinutes(30);

        while (endTimeStart.isBefore((userEndZDT.toLocalTime()).plusMinutes(30))) {
            endTimes.add(endTimeStart);
            endTimeStart = endTimeStart.plusMinutes(30);
        }

        apptEndTimeCombo.setPromptText("Choose an end time");
        apptEndTimeCombo.setItems(endTimes);
    }

    private void showOverlapAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Appointment Overlap");
        alert.setHeaderText("ERROR");
        alert.setContentText("There is one or more appointments that are in conflict with the current one.");

        alert.getButtonTypes().setAll(ButtonType.OK);

        Optional<ButtonType> result = alert.showAndWait();

    }

    public void cancelBtn(ActionEvent event) throws IOException {
        Parent addPartForm = FXMLLoader.load(getClass().getResource("appointments-list-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

}
