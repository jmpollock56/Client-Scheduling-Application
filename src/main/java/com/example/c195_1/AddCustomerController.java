package com.example.c195_1;

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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.FirstLevelDivisions;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    @FXML public TextField idField;
    @FXML public TextField nameField;
    @FXML public TextField addressField;
    @FXML public TextField postalCodeField;
    @FXML public TextField phoneNumberField;
    @FXML public ComboBox<Country> countryCombo;
    @FXML
    public ComboBox<FirstLevelDivisions> stateCombo;

    private ObservableList<FirstLevelDivisions> firstLevelDivisions = FirstLevelDivisions.getFirstLevelDivisions();
    private ObservableList<FirstLevelDivisions> updatedfirstLevelDivisions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryCombo.setPromptText("Choose a Country");
        countryCombo.setItems(Country.getCountries());

    }


    public void confirmBtn(ActionEvent event) throws IOException {
        Random random = new Random();
        FirstLevelDivisions division = stateCombo.getSelectionModel().getSelectedItem();

        if (nameField.getText().isEmpty() || addressField.getText().isEmpty() || postalCodeField.getText().isEmpty() || phoneNumberField.getText().isEmpty() ||
                stateCombo.getItems().isEmpty() || countryCombo.getItems().isEmpty()){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("Warning");
            alert.setContentText("Please fill out all of the fields.");

            alert.getButtonTypes().setAll(ButtonType.OK);

            Optional<ButtonType> result = alert.showAndWait();

            return;

        }


        int id = random.nextInt(9000) + 1000;
        String name = nameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        String createdBy = User.getActiveUser().getCreatedBy();
        int divisionId = division.getDivisionId();

        try {
            int rowsAffected = CustomerQuery.insertCustomer(id, name, address, postalCode, phoneNumber, createdBy, createdBy, divisionId);

            if (rowsAffected > 0){
                System.out.println("New Customer " + name + " created!");
                CustomerQuery.loadCustomers();

            } else {
                System.out.println("Creation Unsuccessful");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Parent addPartForm = FXMLLoader.load(getClass().getResource("customer-list-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addPartStage.setScene(addPartScene);
        addPartStage.show();


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
}
