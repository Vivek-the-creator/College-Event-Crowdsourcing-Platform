package com.college.eventcrowdsourcing.controller;

import com.college.eventcrowdsourcing.database.DatabaseManager;
import com.college.eventcrowdsourcing.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the login view
 * Handles user authentication and navigation to main dashboard
 */
public class LoginController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
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
    }
    
    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both email and password.", true);
            return;
        }
        
        try {
            // For demo purposes, we'll use simple password checking
            // In a real application, you would hash the password and compare with stored hash
            Optional<User> userOpt = dbManager.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Simple password check for demo (in real app, use proper password hashing)
                if (password.equals("password")) {
                    showStatus("Login successful!", false);
                    openMainDashboard(user);
                } else {
                    showStatus("Invalid password.", true);
                }
            } else {
                showStatus("User not found.", true);
            }
        } catch (Exception e) {
            showStatus("Login failed: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
            Scene registerScene = new Scene(loader.load());
            
            // Apply dark theme CSS
            registerScene.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
            
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setTitle("Register - College Event Crowdsourcing Platform");
        } catch (IOException e) {
            showStatus("Error loading registration page: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    /**
     * Open main dashboard with logged-in user
     */
    private void openMainDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainDashboard.fxml"));
            Scene dashboardScene = new Scene(loader.load());
            
            // Set the Scene background color to dark blue
            dashboardScene.setFill(Color.web("#181E2A"));
            
            // Apply dark theme CSS
            dashboardScene.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
            
            // Pass user to main dashboard controller
            MainDashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUser(user);
            dashboardController.initialize();
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard - College Event Crowdsourcing Platform");
            stage.setMaximized(true);
        } catch (IOException e) {
            showStatus("Error loading dashboard: " + e.getMessage(), true);
            e.printStackTrace();
        }
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

