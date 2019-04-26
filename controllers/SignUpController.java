package controllers;

import java.util.Base64;

import Dao.DBConnect;
import application.Main;
import models.CustomerModel;
import models.LoginModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class SignUpController extends DBConnect {

	// Needed variables
	@FXML
	private TextField fname;

	@FXML
	private PasswordField password;
	@FXML
	private TextField lname;

	@FXML
	private TextField username;

	@FXML
	private Label labelSts;

	private CustomerModel model;
	
	public SignUpController() {
		model = new CustomerModel();

	}

	// Function for new user to signing up
	public void signUp() {

		String firstName = this.fname.getText();
		String lastName = this.lname.getText();
		String passwordField = this.password.getText();
		String userName = this.username.getText();
		LoginModel model = new LoginModel();

		// Validations
		if (firstName == null || firstName.trim().equals("")) {
			labelSts.setText("Firstname cannot be empty or spaces");
			System.out.println(labelSts);
			return;

		}
		if (lastName == null || lastName.trim().equals("")) {
			labelSts.setText("Lastname cannot be empty or spaces");
			System.out.println(labelSts);
			return;
		}
		if (userName == null || userName.trim().equals("")) {
			labelSts.setText("Username cannot be empty or spaces");
			System.out.println(labelSts);
			return;
		}
		if (passwordField == null || passwordField.trim().equals("")) {
			labelSts.setText("Password cannot be empty or spaces");
			System.out.println(labelSts);
			return;
		}
		if ((userName == null || userName.trim().equals(""))
				&& (passwordField == null || passwordField.trim().equals(""))
				&& (lastName == null || lastName.trim().equals(""))
				&& (firstName == null || firstName.trim().equals(""))

		) {
			labelSts.setText("Fields cannot be empty or spaces");
			System.out.println(labelSts);
			return;
		}

		int user_id = model.getCredentials(userName, passwordField);
		Boolean isValid = user_id > 0 ? true : false;
		if (isValid) {
			labelSts.setText("User alredy exists!");
			System.out.println(labelSts);
			return;
		}
		
		//Hashcoding password
		String hashedPswd = Base64.getEncoder().encodeToString(passwordField.getBytes());
		
		//byte[]

		// Insertion of user into DB
		boolean success = insertUser(userName, hashedPswd, firstName, lastName);
		int uid = model.getCredentials(userName, hashedPswd);
		if (success)
			try {
				AnchorPane root;
				root = (AnchorPane) FXMLLoader.load(getClass().getResource("/views/CustomerView.fxml"));
				Main.stage.setTitle("CustomerView");
				Scene scene = new Scene(root);
				Main.stage.setScene(scene);

			} catch (Exception e) {
				System.out.println("Error occured while inflating view: " + e);
			}
		else
			labelSts.setText("Unable to create user");
		return;

	}
	
	public void cancelSignUp() {
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