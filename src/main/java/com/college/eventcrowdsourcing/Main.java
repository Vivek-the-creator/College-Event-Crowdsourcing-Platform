package com.college.eventcrowdsourcing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for College Event Crowdsourcing Platform
 * JavaFX application entry point
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Apply dark theme CSS
            scene.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
            
            // Set up the primary stage
            primaryStage.setTitle("College Event Crowdsourcing Platform - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            
            // Set minimum window size
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);
            
            // Start maximized for better UI experience
            primaryStage.setMaximized(true);
            
            // Show the application
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        // Clean up database connections when application closes
        try {
            com.college.eventcrowdsourcing.database.DatabaseManager.getInstance().closeConnection();
        } catch (Exception e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
