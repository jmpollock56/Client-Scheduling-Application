package com.example.c195_1;

import DAO.AppointmentsQuery;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class UpdateAppointmentController implements Initializable {

    @FXML
    public TextField apptIdField;
    @FXML
    public TextField apptTitleField;
    @FXML
    public TextArea apptDescField;
    @FXML
    public TextField apptLocationField;
    @FXML
    public TextField apptTypeField;
    @FXML
    public ComboBox<Contacts> apptContactCombo;
    @FXML
    public DatePicker apptDatePicker;
    @FXML
    public ComboBox<LocalTime> apptStartCombo;
    @FXML
    public ComboBox<LocalTime> apptEndCombo;
    public ComboBox<Customer> apptCustomerCombo;
    @FXML
    private ComboBox<User> apptUserCombo;
    private static int id;
    private static String title;
    private static String description;
    private static String location;
    private static String type;
    private static Contacts contact;
    private static User user;
    private static Customer customer;
    private LocalTime businessStart = LocalTime.of(8,0);
    private LocalTime businessEnd = LocalTime.of(22,0);
    private ZoneId userZoneId = ZoneId.systemDefault();
    private ZoneId companyTimezone = ZoneId.of("America/New_York");
    private ZonedDateTime userEndZDT;
    private static LocalDate date;
    private static LocalTime startTime;
    private static LocalTime endTime;
    private LocalTime endTimeStart;
    private static Appointment selectedAppointment;
    private static int userId;
    private static int customerId;
    private ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    private ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();
    private boolean overlap = false;

    public static void loadAppointment(Appointment appointment) {

        selectedAppointment = appointment;

        id = appointment.getAppointmentId();
        title = appointment.getTitle();
        description = appointment.getDescription();
        location = appointment.getLocation();
        type = appointment.getType();

        for (Contacts c : Contacts.getContacts()) {
            if (appointment.getContactId() == c.getContactId()) {
                contact = c;
            }
        }

        date = appointment.getStart().toLocalDate();
        startTime = appointment.getStart().toLocalTime();
        endTime = appointment.getEnd().toLocalTime();

        for (Customer cust: Customer.getCustomers()){
            if (cust.getId() == appointment.getCustomerId()){
                customer = cust;
            }
        }

        for (User u: User.getUsers()){
            if (u.getId() == appointment.getUserId()){
                user = u;
            }
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        apptIdField.setText(Integer.toString(id));
        apptTitleField.setText(title);
        apptDescField.setText(description);
        apptLocationField.setText(location);
        apptTypeField.setText(type);
        apptContactCombo.setItems(Contacts.getContacts());
        apptContactCombo.setValue(contact);
        apptDatePicker.setValue(date);

        LocalDateTime startLDT = LocalDateTime.of(date, startTime); //imported start time
        LocalDateTime endLDT = LocalDateTime.of(date, endTime);     //imported end time

        apptStartCombo.setValue(startLDT.toLocalTime());
        apptEndCombo.setValue(endLDT.toLocalTime());

        LocalDate userDate = date;

        LocalDateTime businessStartLDT = LocalDateTime.of(userDate, businessStart);
        ZonedDateTime businessStartZDT = ZonedDateTime.of(businessStartLDT, companyTimezone);
        ZonedDateTime userStartZDT = businessStartZDT.withZoneSameInstant(userZoneId);

        LocalDateTime businessEndLDT = LocalDateTime.of(userDate, businessEnd);
        ZonedDateTime businessEndZDT = ZonedDateTime.of(businessEndLDT, companyTimezone);
        userEndZDT = businessEndZDT.withZoneSameInstant(userZoneId);

        while(userStartZDT.isBefore(userEndZDT)){
            LocalTime newTime = LocalTime.from(userStartZDT);
            startTimes.add(newTime);

            userStartZDT = userStartZDT.plusMinutes(30);
        }
        apptStartCombo.setItems(startTimes);

        apptUserCombo.setValue(user);
        apptUserCombo.setItems(User.getUsers());

        apptCustomerCombo.setValue(customer);
        apptCustomerCombo.setItems(Customer.getCustomers());
    }

    private void populateEndComboBox(LocalTime userStartTime) {
        endTimes.clear();
        endTimeStart = userStartTime.plusMinutes(30);

        while (endTimeStart.isBefore((userEndZDT.toLocalTime()).plusMinutes(30))) {
            endTimes.add(endTimeStart);
            endTimeStart = endTimeStart.plusMinutes(30);
        }

        apptEndCombo.setItems(endTimes);
    }

    public void onStartSelection(ActionEvent event) {
        LocalTime startTimeSelect = apptStartCombo.getSelectionModel().getSelectedItem();
        endTimes.clear();
        populateEndComboBox(startTimeSelect);
    }

    public void save(ActionEvent event) throws SQLException, IOException {
        int newId = id;
        String newTitle = apptTitleField.getText();
        String newDescription = apptDescField.getText();
        String newLocation = apptLocationField.getText();
        String newType = apptTypeField.getText();
        int newContactId = apptContactCombo.getValue().getContactId();
        LocalDate newDate = apptDatePicker.getValue();
        LocalTime newStartTime = apptStartCombo.getValue();
        LocalTime newEndTime = apptEndCombo.getValue();
        int newUserId = apptUserCombo.getValue().getId();
        int newCustomerId = apptCustomerCombo.getValue().getId();

        LocalDateTime newStart = LocalDateTime.of(newDate, newStartTime);
        LocalDateTime newEnd = LocalDateTime.of(newDate, newEndTime);

        for (Appointment app: Appointment.getAllAppointments()){
            if ((newStart.isAfter(app.getStart()) || newStart.isEqual(app.getStart())) && newStart.isBefore(app.getEnd())){
                overlap = true;
                break;
            } else if ((newEnd.isAfter(app.getStart())) && (newEnd.isBefore(app.getEnd()) || newEnd.isEqual(app.getEnd()))) {
                overlap = true;
                break;
            } else if ((newStart.isBefore(app.getStart()) || newStart.isEqual(app.getStart())) && (newEnd.isAfter(app.getEnd()) || newEnd.isEqual(app.getEnd()))) {
                overlap = true;
                break;
            }
        }

        if (overlap){
            showOverlapAlert();
            return;
        }

        Instant instant = Instant.now();
        Timestamp updateDate = Timestamp.from(instant);

        Appointment newAppointment = new Appointment(newId, newTitle, newDescription, newLocation, newType,
                newStart, newEnd, selectedAppointment.getCreateDate(), selectedAppointment.getCreatedBy(),
                updateDate, User.getActiveUser().getUserName(), newCustomerId, newUserId, newContactId);

        int affectedRows = AppointmentsQuery.updateAppointment(newAppointment);

        if(affectedRows > 0){

            Parent addPartForm = FXMLLoader.load(getClass().getResource("appointments-list-view.fxml"));
            Scene addPartScene = new Scene(addPartForm);
            Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            addPartStage.setScene(addPartScene);
            addPartStage.show();

            System.out.println("Appointment has been updated!");
        }


    }

    private void showOverlapAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Appointment Overlap");
        alert.setHeaderText("ERROR");
        alert.setContentText("There is one or more appointments that are in conflict with the current one.");

        alert.getButtonTypes().setAll(ButtonType.OK);

        Optional<ButtonType> result = alert.showAndWait();

    }

    public void cancel(ActionEvent event) throws IOException {
        Parent addPartForm = FXMLLoader.load(getClass().getResource("appointments-list-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }
}
