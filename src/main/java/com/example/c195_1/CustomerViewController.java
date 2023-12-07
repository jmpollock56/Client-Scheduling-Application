package com.example.c195_1;

import DAO.AppointmentsQuery;
import DAO.CustomerQuery;
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
import model.Appointment;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;


public class CustomerViewController implements Initializable {

    @FXML
    public TableView<Customer> customerTable;
    @FXML
    public TableColumn<Customer, Integer> idColumn;
    @FXML
    public TableColumn<Customer, String> nameColumn;
    @FXML
    public TableColumn<Customer, String> addressColumn;
    @FXML
    public TableColumn<Customer, String> postalCodeColumn;
    @FXML
    public TableColumn<Customer, String> phoneColumn;
    @FXML
    public Button logoutBtn;


    public void loadCustomersInfo() {

        customerTable.setItems(Customer.getCustomers());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCustomersInfo();
    }

    public void addCustomerBtn(ActionEvent event) throws IOException {
        Parent addPartForm = FXMLLoader.load(getClass().getResource("add-customer-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

    public void updateCustomerBtn(ActionEvent event) throws IOException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        UpdateCustomerViewController.loadCustomer(selectedCustomer);

        Parent addPartForm = FXMLLoader.load(getClass().getResource("update-customer-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

    public void deleteCustomerBtn(ActionEvent event) throws SQLException {

        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer.getAppointments().isEmpty()) {
            CustomerQuery.deleteCustomer(selectedCustomer);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Customer Deleted");
            alert.setHeaderText("Deleted");
            alert.setContentText("Customer " + selectedCustomer.getName() + " was deleted");

            alert.getButtonTypes().setAll(ButtonType.OK);

            Optional<ButtonType> result = alert.showAndWait();

            CustomerQuery.loadCustomers();

            loadCustomersInfo();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Delete Customer");
            alert.setHeaderText("Warning");
            alert.setContentText("Can not delete customer with scheduled appointments");

            alert.getButtonTypes().setAll(ButtonType.OK);

            Optional<ButtonType> result = alert.showAndWait();

            return;
        }
    }


    public void back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu-view.fxml"));
        Parent mainMenuParent = loader.load();
        Scene mainMenuScene = new Scene(mainMenuParent);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(mainMenuScene);
        stage.show();
    }
}
