package controllers;

import java.util.Base64;

import application.Main;
import models.LoginModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController {

	// Necessary variables
	@FXML
	private TextField txtUsername;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Label lblError;

	private LoginModel model;

	public LoginController() {
		model = new LoginModel();

	}

	public void login() {
		String username = this.txtUsername.getText();
		String password = this.txtPassword.getText();
		//Hashcoding password
		String hashedPswd = Base64.getEncoder().encodeToString(password.getBytes());
				

		// Validations
		if (username == null || username.trim().equals("")) {
			lblError.setText("Username cannot be empty or spaces");
			return;

		}
		if (password == null || password.trim().equals("")) {
			lblError.setText("Password cannot be empty or spaces");
			return;
		}
		if (username == null || username.trim().equals("") && (password == null || password.trim().equals(""))) {
			lblError.setText("Username / Password cannot be empty or spaces");
			return;
		}

		// Authentication check
		checkCredentials(username, hashedPswd);
	}

	public void checkCredentials(String username, String password) {

		int user_id = model.getCredentials(username, password);

		Boolean isValid = user_id > 0 ? true : false;

		System.out.println(isValid);
		if (!isValid) {
			lblError.setText("User does not exist!");
			return;
		}
		try {
			AnchorPane root;
			if (model.isAdmin() && isValid) {
				// If user is admin, inflate admin view
				root = (AnchorPane) FXMLLoader.load(getClass().getResource("/views/AdminView.fxml"));
				Main.stage.setTitle("Admin View");

			} else {
				// If user is customer, inflate customer view
				root = (AnchorPane) FXMLLoader.load(getClass().getResource("/views/CustomerView.fxml"));

				CustomerController.setUser(user_id);
				Main.stage.setTitle("Customer View");
			}

			Scene scene = new Scene(root);
			Main.stage.setScene(scene);

		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}

	// New User
	public void Signup() {
		try {
			AnchorPane root;
			root = (AnchorPane) FXMLLoader.load(getClass().getResource("/views/SignUp.fxml"));
			Main.stage.setTitle("Sign Up");
			Scene scene = new Scene(root);
			Main.stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}

	}
}