package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

import Dao.DBConnect;
import application.DynamicTable;
import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import models.CustomerModel;

public class AdminController extends DBConnect {

	// Necessary variables
	@FXML
	private TextField txtLevel;
	@FXML
	private TextField txtLot;
	@FXML
	private Pane pane1;
	@FXML
	private Pane pane2;
	@FXML
	private Label lblError;
	@FXML
	private Label msg;

	public AdminController() {

	}

	// Function to view all current bookings
	public void viewBookings() {
		DynamicTable d = new DynamicTable();
		// call method from DynamicTable class and pass some arbitrary query string
		d.buildData("Select * from vp_bookings", false);
		pane1.setVisible(false);
		pane2.setVisible(false);
	}

	// Function to view parking lot occupancy
	public void viewParkingLot() {
		DynamicTable d = new DynamicTable();
		// call method from DynamicTable class and pass some arbitrary query string
		d.buildData("Select * from vp_parking_lot", false);
		pane1.setVisible(false);
		pane2.setVisible(false);
	}

	// Function that determine which pane to display
	public void toggleParking() {
		pane2.setVisible(false);
		pane1.setVisible(true);

	}

	public void editOptions() {

		pane1.setVisible(false);
		pane2.setVisible(true);
	}

	// Function to toggle parking details
	public void submitUpdate() {
		String level = this.txtLevel.getText();
		String lot = this.txtLot.getText();
		System.out.println("Levl" + level + " lot" + lot);

		if ((level == null || level.trim().equals("")) || (lot == null || lot.trim().equals(""))) {
			lblError.setText("Fields cannot be empty or spaces");
			return;
		}
		if (!level.matches("[A-E]{1}")) {
			lblError.setText("Lot input wrong.\na)Ranges only from A to E(Upper case)\nb)A single character is only accepted");
			return;
		}
		if (!lot.matches("[0-9]+")) {
			lblError.setText("Only numbers are accepted for level");
			return;
		}
		if(existLevel(lot)) {
			toggleUpdate(level, lot);
			if(deleteTheirBooking(level,lot)) {
				lblError.setText("Updated!!");	
			}
		}
		else
		{
			lblError.setText("Level doesn't exist.\nChoose a correct level");
		}
		System.out.println("Update Submit button pressed");

	}

	// Function for logout
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

	// Function to delete a level
	public void levelDelete() {
		System.out.println("deleteOneLevel called");
		// Check for no parking entries made in row
		if (canDelete() > 0) {
			deleteOneLevel();
			msg.setText("Level Deleted!!");
		}
		// Inform inability to delete
		else {
			msg.setText("Level cannot be deleted!!\nCheck occupancy");
		}
	}

	// Function to add a level of parking
	public void addLevel() {
		System.out.println("addOneLevel called");
		addOneLevel();
		msg.setText("Level Added!!");
	}
}
