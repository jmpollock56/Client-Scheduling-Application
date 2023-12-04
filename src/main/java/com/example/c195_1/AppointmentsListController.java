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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentsListController implements Initializable {

    @FXML public TableView<Appointment> appsTable;
    @FXML public TableColumn<Appointment, Integer> idColumn;
    @FXML public TableColumn<Appointment, String> titleColumn;
    @FXML public TableColumn<Appointment, String> descriptionCol;
    @FXML public TableColumn<Appointment, String> locationCol;
    @FXML public TableColumn<Appointment, String> contactCol;
    @FXML public TableColumn<Appointment, String> typeCol;
    @FXML public TableColumn<Appointment, LocalDateTime> startCol;
    @FXML public TableColumn<Appointment, LocalDateTime> endCol;
    @FXML public TableColumn<Appointment, Integer> custIdCol;
    @FXML public TableColumn<Appointment, Integer> userIdCol;
    private ObservableList<Appointment> allAppointments = Appointment.getAllAppointments();
    private ObservableList<Appointment> appointmentsByWeek = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentsByMonth = FXCollections.observableArrayList();

    public void loadAppointmentInfo(){
        allAppointments.clear();

        try {
            AppointmentsQuery.loadAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        appsTable.setItems(allAppointments);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAppointmentInfo();

        startCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("start"));
        startCol.setCellFactory(getDateTimeCellFactory("yyyy-MM-dd HH:mm:ss"));

        endCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("end"));
        endCol.setCellFactory(getDateTimeCellFactory("yyyy-MM-dd HH:mm:ss"));

        custIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    public void filterWeek(ActionEvent event) {
        appointmentsByWeek.clear();
        LocalDateTime weekStart = LocalDateTime.now();
        LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);


        for (Appointment appointment: Appointment.getAllAppointments()){
            if((appointment.getEnd().isAfter(weekStart)) && appointment.getEnd().isBefore(weekEnd)){
                appointmentsByWeek.add(appointment);

            }
        }

        appsTable.setItems(appointmentsByWeek);
    }

    public void filterMonth(ActionEvent event) {
        appointmentsByMonth.clear();
        LocalDateTime currentDate = LocalDateTime.now();

        for (Appointment appointment: Appointment.getAllAppointments()){
            if ((currentDate.getMonth() == appointment.getStart().getMonth()) && (currentDate.getYear() == appointment.getStart().getYear())){
                appointmentsByMonth.add(appointment);
            }
        }

        appsTable.setItems(appointmentsByMonth);
    }

    public void filterAll(ActionEvent event) {
        loadAppointmentInfo();
    }

    public void toAddAppointment(ActionEvent event) throws IOException {
        Parent addPartForm = FXMLLoader.load(getClass().getResource("add-appointment-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

    public void toUpdateAppointment(ActionEvent event) throws IOException {
        Appointment selectedAppointment = appsTable.getSelectionModel().getSelectedItem();
        UpdateAppointmentController.loadAppointment(selectedAppointment);

        Parent addPartForm = FXMLLoader.load(getClass().getResource("update-appointment-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

    public void deleteAppointment(ActionEvent event) throws SQLException {
        Appointment selectedAppointment = appsTable.getSelectionModel().getSelectedItem();
        AppointmentsQuery.deleteAppointment(selectedAppointment);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Deleted");
        alert.setHeaderText("Deleted");
        alert.setContentText("Appointment " + selectedAppointment.getAppointmentId() + " of Type " + selectedAppointment.getType() + " was deleted");

        alert.getButtonTypes().setAll(ButtonType.OK);

        Optional<ButtonType> result = alert.showAndWait();

        AppointmentsQuery.loadAppointments();
        loadAppointmentInfo();
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
        Parent addPartForm = FXMLLoader.load(getClass().getResource("main-menu-view.fxml"));
        Scene addPartScene = new Scene(addPartForm);
        Stage addPartStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        addPartStage.setScene(addPartScene);
        addPartStage.show();
    }

}
