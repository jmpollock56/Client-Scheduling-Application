package com.example.c195_1;

import DAO.AppointmentsQuery;
import DAO.CustomerQuery;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Country;
import model.Customer;
import model.FirstLevelDivisions;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.control.ButtonType.*;


public class UpdateCustomerViewController implements Initializable {
    @FXML public TextField id;
    @FXML public TextField name;
    @FXML public TextField address;
    @FXML public TextField postalCode;
    @FXML public TextField phoneNumber;
    @FXML public ComboBox<Country> countryCombo;
    @FXML public ComboBox<FirstLevelDivisions> stateCombo;
    @FXML public TableView<Appointment> appsTable;
    @FXML public TableColumn<Appointment, Integer> appIdCol;
    @FXML public TableColumn<Appointment, Integer>  appTitleCol;
    @FXML public TableColumn<Appointment, String>  appDescCol;
    @FXML public TableColumn<Appointment, String>  appLocationCol;
    @FXML public TableColumn<Appointment, String>  appContactCol;
    @FXML public TableColumn<Appointment, String>  appTypeCol;
    @FXML public TableColumn<Appointment, LocalDateTime> appStartCol;
    @FXML public TableColumn<Appointment, LocalDateTime> appEndCol;
    @FXML public TableColumn<Appointment, Integer>  appCustomerIdCol;
    @FXML public TableColumn<Appointment, Integer>  appUserIdCol;

    public static Customer customer;
    public static Country customerCountry;
    public static FirstLevelDivisions customerDivision = null;
    private static final ObservableList<FirstLevelDivisions> firstLevelDivisions = FirstLevelDivisions.getFirstLevelDivisions();
    private static final ObservableList<FirstLevelDivisions> updatedfirstLevelDivisions = FXCollections.observableArrayList();
    private static final ObservableList<Country> countries = Country.getCountries();

    private ObservableList<Appointment> appointments = customer.getAppointments();

    public static void loadCustomer(Customer selectedCustomer){
        customer = selectedCustomer; // get selected customer
        int customerDivisionId = customer.getDivisionId();

        for (FirstLevelDivisions divisions: firstLevelDivisions){
            if (customerDivisionId == divisions.getDivisionId()){
                customerDivision = divisions;
            }
        }

        for (Country country: countries){
            if (customerDivision.getCountryId() == country.getCountryId()){
                customerCountry = country;
            }
        }
    }

    public void loadAppointmentsTable() throws SQLException {
        appointments.clear();
        AppointmentsQuery.loadAppointments();

        appsTable.setItems(appointments);

        appIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appContactCol.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        appTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        appStartCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("start"));
        appStartCol.setCellFactory(getDateTimeCellFactory("yyyy-MM-dd HH:mm:ss"));

        appEndCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("end"));
        appEndCol.setCellFactory(getDateTimeCellFactory("yyyy-MM-dd HH:mm:ss"));

        appCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            loadAppointmentsTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        id.setText(Integer.toString(customer.getId()));
        name.setText(customer.getName());
        address.setText(customer.getAddress());
        postalCode.setText(customer.getPostalCode());
        phoneNumber.setText(customer.getPhoneNumber());
        countryCombo.setItems(countries);
        countryCombo.setValue(customerCountry);
        stateCombo.setValue(customerDivision);

        for(FirstLevelDivisions initialDivisions: firstLevelDivisions){
            if (initialDivisions.getCountryId() == customerCountry.getCountryId()){
                updatedfirstLevelDivisions.add(initialDivisions);
            }
        }

        stateCombo.setItems(updatedfirstLevelDivisions);

    }

    public void confirmBtn(ActionEvent event) throws SQLException, IOException {
        int newId = Integer.parseInt(id.getText());
        String newName = name.getText();
        String newAddress = address.getText();
        String newPostalCode = postalCode.getText();
        String newPhoneNumber = phoneNumber.getText();
        Timestamp newCreateDate = customer.getCreateDate();
        String newCreatedBy = customer.getCreatedBy();
        int newDivisionId = stateCombo.getSelectionModel().getSelectedItem().getDivisionId();

        int rowsAffected = CustomerQuery.updateCustomer(newId, newName, newAddress, newPostalCode, newPhoneNumber, newCreateDate, newCreatedBy, newDivisionId);

        if (rowsAffected == 1){
            System.out.println("User " + newName + " has been updated!");

            Parent addPartForm = FXMLLoader.load(getClass().getResource("customer-list-view.fxml"));
            Scene addPartScene = new Scene(addPartForm);
            Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            addPartStage.setScene(addPartScene);
            addPartStage.show();

        } else {
            System.out.println("Updated Unsuccessful");
        }


    }

    public void onCountrySelection(ActionEvent event) {

        Country countrySelection = countryCombo.getSelectionModel().getSelectedItem();

        if (countrySelection != null){
            updatedfirstLevelDivisions.clear();
            stateCombo.setItems(updatedfirstLevelDivisions);
            for (FirstLevelDivisions division : firstLevelDivisions) {

                if (division.getCountryId() == countrySelection.getCountryId()) {
                    updatedfirstLevelDivisions.add(division);
                }
            }
            stateCombo.setPromptText("Choose a State");
            stateCombo.setItems(updatedfirstLevelDivisions);
        }
    }

    public void cancelBtn(ActionEvent event) throws IOException {
        Parent addPartForm = FXMLLoader.load(getClass().getResource("customer-list-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
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

}
