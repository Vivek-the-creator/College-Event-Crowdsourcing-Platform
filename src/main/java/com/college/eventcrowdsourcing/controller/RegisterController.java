package com.college.eventcrowdsourcing.controller;

import com.college.eventcrowdsourcing.database.DatabaseManager;
import com.college.eventcrowdsourcing.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

/**
 * Controller for the registration view
 * Handles new user registration
 */
public class RegisterController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label statusLabel;
    
    private DatabaseManager dbManager;
    
    /**
     * Initialize the controller
     */
    @FXML
    private void initialize() {
        dbManager = DatabaseManager.getInstance();
        statusLabel.setText("");
        
        // Initialize role combo box
        roleComboBox.getItems().addAll("Student", "Faculty");
        roleComboBox.setValue("Student");
    }
    
    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();
        
        // Validate input
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showStatus("Please fill in all fields.", true);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStatus("Passwords do not match.", true);
            return;
        }
        
        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters long.", true);
            return;
        }
        
        if (!isValidEmail(email)) {
            showStatus("Please enter a valid email address.", true);
            return;
        }
        
        try {
            // Check if user already exists
            if (dbManager.getUserByEmail(email).isPresent()) {
                showStatus("User with this email already exists.", true);
                return;
            }
            
            // Create new user
            User.UserRole userRole = role.equals("Faculty") ? User.UserRole.FACULTY : User.UserRole.STUDENT;
            User newUser = new User(name, email, password, userRole);
            
            if (dbManager.createUser(newUser)) {
                showStatus("Registration successful! You can now login.", false);
                // Clear form
                clearForm();
            } else {
                showStatus("Registration failed. Please try again.", true);
            }
        } catch (Exception e) {
            showStatus("Registration failed: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Scene loginScene = new Scene(loader.load());
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login - College Event Crowdsourcing Platform");
        } catch (IOException e) {
            showStatus("Error loading login page: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    /**
     * Clear form fields
     */
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue("Student");
    }
    
    /**
     * Show status message
     */
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #ff0000;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #00aa00;");
        }
    }
}


