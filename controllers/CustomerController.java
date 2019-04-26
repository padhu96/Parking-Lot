package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.javafx.geom.Rectangle;
import com.sun.prism.paint.Color;

import Dao.DBConnect;
import application.DynamicTable;
import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import models.CustomerModel;
import javafx.stage.Stage;

import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;

public class CustomerController extends DBConnect {

	// Variable to maintain customer login id to carry further operations
	static int user_id;
	static CustomerModel cust;

	// Needed variables
	@FXML
	private TextField lot;

	@FXML
	private TextField level;

	@FXML
	private Label bookingError;

	@FXML
	private Label status;

	@FXML
	private TableView<CustomerModel> tblAccounts;
	@FXML
	private TableColumn<CustomerModel, String> tid;
	@FXML
	private TableColumn<CustomerModel, String> balance;

	// Constructor
	public CustomerController() {
	}

	// Set the user login id
	public static void setUser(int user_id) {
		CustomerController.user_id = user_id;
	}

	// Function to view all the current parking occupancy
	public void viewParkingLot() throws IOException {

		CustomerModel cm = new CustomerModel();
		System.out.println(CustomerController.user_id);
		DynamicTable d = new DynamicTable();
		// call method from DynamicTable class and pass some arbitrary query string
		d.buildData("Select * from vp_parking_lot", false);

	}

	// Function to create a new booking
	public void createBooking() {
		try {
			// Display parking lot information
			viewParkingLot();
			// Display booking form
			// AnchorPane root = (AnchorPane)
			// FXMLLoader.load(getClass().getResource("/views/CreateBooking.fxml"));
			// Scene scene = new Scene(root);
			// Main.stage.setScene(scene);
			// Main.stage.setTitle("CreateBooking");

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateBooking.fxml"));
			Parent root1 = (Parent) loader.load();
			Stage stage = new Stage();
			stage.setTitle("Make your booking");
			stage.setScene(new Scene(root1));
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Function that will make the actual booking
	public void confirmBooking() {
		System.out.println("User id in confimr booking" + user_id);
		String level = this.level.getText();
		String lot = this.lot.getText();
		System.out.println("Levl" + level + " lot" + lot);

		// Validations
		if ((level == null || level.trim().equals("")) || (lot == null || lot.trim().equals(""))) {
			bookingError.setText("Fields cannot be empty or spaces");
			return;
		}
		if (!level.matches("[A-E]{1}")) {
			bookingError.setText(
					"Lot input wrong.\na)Ranges only from A to E (Capital letters) \nb)A single character is only accepted");
			return;
		}
		if (!lot.matches("[0-9]+$")) {
			bookingError.setText("Only numbers are accepted for level");
			return;
		}
		// Check if slot is available for booking
		// Then make sure only one booking can be made by one user
		// Then book the slot
		// Inform user based on what conditions they satisfy
		if (existLevel(lot)) {
			if (canBook(level, lot)) {
				if (onlyOneBooking(user_id) == 0) {
					bookTheSlot(level, lot, user_id);
					bookingError.setText("Slot Booked");
				} else
					bookingError
							.setText("Slot cannot be booked.\nYou already have a booking\nOnly one booking allowed!!");
			} else
				bookingError.setText("Slot cannot be booked\nAlready occupied!!");
		} else
			bookingError.setText("Level input is more than existing levels.\n Please check !!");
	}

	// Function to cancel the booking
	public void cancelBooking() {
		// Get the booked slot by user
		String bkdslot = getBookedSlot(user_id);
		//Get the individual string values
		String level=String.valueOf(bkdslot.charAt(0));
		String lot =String.valueOf(bkdslot.charAt(1));
		// Inform if no records exist
		if (bkdslot.equals("No records")) {
			status.setText("You do not have any bookings");
		}
		// Perform a deletion by confirmation through a dialog box
		else {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Delete your booking?\n" + bkdslot);
			alert.showAndWait();
			// If given yes to delete, then perform delete
			if (alert.getResult() == ButtonType.OK) {
				System.out.println("Alert given as yes");
				if (confirmCancel(user_id)) {
					{
						toggleUpdate(lot,level);
					status.setText("Your booking has been cancelled");
					}
				} else {
					status.setText("Cannot cancel the booking.\nThere was an error");
				}
			} else {
				System.out.println("if alert escaped");
			}
		}
	}

	// Function for logging out
	public void logout() {
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
			Scene scene = new Scene(root);
			Main.stage.setScene(scene);
			Main.stage.setTitle("Login");
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
}
