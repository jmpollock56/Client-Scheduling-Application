package com.example.c195_1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Contacts;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportsViewController implements Initializable {
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<Month> monthComboBox;
    @FXML private Label totalLabel;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, LocalDateTime> startCol;
    @FXML private TableColumn<Appointment, LocalDateTime> endCol;
    @FXML private TableColumn<Appointment, Integer> customerIdCol;
    @FXML private ComboBox<Contacts> contactComboBox;

    private String selectedType;
    private Month selectedMonth;
    private int totalAppointments = 0;
    private ObservableList<Contacts> contacts = Contacts.getContacts();
    private ObservableList<Appointment> associatedAppointments = FXCollections.observableArrayList();
    private ObservableList<String> types = FXCollections.observableArrayList();
    private ObservableList<String> months = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onContactScheduleTab(Event event) {
        associatedAppointments.clear();
        contactComboBox.setItems(contacts);
    }

    public void toTotalAppointmentTab(Event event) {
        // ---- Total Appointments By Type and Month ----
        totalAppointments = 0;
        for (Appointment app : Appointment.getAllAppointments()){
            if (!(types.contains(app.getType()))){
                types.add(app.getType());
            }
        }
        monthComboBox.getItems().addAll(Month.values());
        typeComboBox.setItems(types);
    }

    public void onContactSelection(ActionEvent event) {
        // Clear the associatedAppointments list
        associatedAppointments.clear();

        // Fetch all appointments and load them into associatedAppointments
        for (Appointment app : Appointment.getAllAppointments()) {
            Contacts selectedContact = contactComboBox.getSelectionModel().getSelectedItem();
            if (selectedContact != null && selectedContact.getContactId() == app.getContactId()) {
                associatedAppointments.add(app);
            }
        }

        // Load the table with the updated associatedAppointments
        loadTable();
    }

    public void loadTable(){
        appointmentTable.setItems(associatedAppointments);

        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("start"));
        startCol.setCellFactory(getDateTimeCellFactory("yyyy-MM-dd HH:mm:ss"));

        endCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("end"));
        endCol.setCellFactory(getDateTimeCellFactory("yyyy-MM-dd HH:mm:ss"));

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }

    private Callback<TableColumn<Appointment, LocalDateTime>, TableCell<Appointment, LocalDateTime>> getDateTimeCellFactory(String pattern) {
        return new Callback<TableColumn<Appointment, LocalDateTime>, TableCell<Appointment, LocalDateTime>>() {
            @Override
            public TableCell<Appointment, LocalDateTime> call(TableColumn<Appointment, LocalDateTime> param) {
                return new TableCell<Appointment, LocalDateTime>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            // Convert the LocalDateTime to user's computer timezone
                            ZoneId userTimeZone = ZoneId.systemDefault();
                            LocalDateTime localDateTime = item.atZone(ZoneId.systemDefault()).withZoneSameInstant(userTimeZone).toLocalDateTime();
                            setText(localDateTime.format(formatter));
                        }
                    }
                };
            }
        };
    }

    public void back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu-view.fxml"));
        Parent mainMenuParent = loader.load();
        Scene mainMenuScene = new Scene(mainMenuParent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(mainMenuScene);
        stage.show();
    }


    public void onTypeSelection(ActionEvent event) {
        selectedType = typeComboBox.getSelectionModel().getSelectedItem();
        checkUserSelections(selectedType, selectedMonth);

    }

    public void onMonthSelection(ActionEvent event) {
        selectedMonth = monthComboBox.getSelectionModel().getSelectedItem();
        checkUserSelections(selectedType, selectedMonth);
    }

    public void checkUserSelections(String type, Month month){
        if (!(month == null || type == null)){
            determineTypeAmount(type, month);
        }
    }

    public void determineTypeAmount(String type, Month month){
        System.out.println(type);
        for (Appointment app : Appointment.getAllAppointments()){
            if (app.getType().equals(type) && app.getStart().getMonth() == month){
                totalAppointments++;
                System.out.println(app.getStart().getMonth() + " | " + app.getType());
            }
        }

        totalLabel.setText("The total Appointments in " + month.name() + " of Type " + type + " is " + totalAppointments);
    }
}
