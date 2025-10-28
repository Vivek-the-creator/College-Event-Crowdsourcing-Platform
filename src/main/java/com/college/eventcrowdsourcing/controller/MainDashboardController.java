package com.college.eventcrowdsourcing.controller;

import com.college.eventcrowdsourcing.database.DatabaseManager;
import com.college.eventcrowdsourcing.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main dashboard controller
 * Handles navigation between different sections and displays user information
 */
public class MainDashboardController {
    
    // Navigation buttons
    @FXML private Button dashboardButton;
    @FXML private Button eventsButton;
    @FXML private Button proposeButton;
    @FXML private Button contributionsButton;
    @FXML private Button adminButton;
    @FXML private Button logoutButton;
    
    // User info
    @FXML private Label userInfoLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label pointsLabel;
    @FXML private Label proposedEventsLabel;
    @FXML private Label contributionsLabel;
    @FXML private Label pageTitleLabel;
    @FXML private Label pageSubtitleLabel;
    
    // Content panes
    @FXML private StackPane contentStack;
    @FXML private VBox eventsContent;
    @FXML private VBox proposeEventContent;
    @FXML private VBox contributionsContent;
    @FXML private VBox adminPanelContent;
    @FXML private GridPane statsGrid;
    
    // Dashboard content
    @FXML private ListView<String> recentActivityList;
    
    // Events content
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ListView<Event> eventsList;
    @FXML private Button refreshEventsButton;
    
    // Propose event content
    @FXML private TextField eventTitleField;
    @FXML private TextArea eventDescriptionField;
    @FXML private ComboBox<String> eventCategoryComboBox;
    @FXML private TextField budgetEstimateField;
    @FXML private TextField targetFundsField;
    @FXML private TextField eventLocationField;
    @FXML private Button submitEventButton;
    @FXML private Label eventStatusLabel;
    
    // Contributions content
    @FXML private ListView<Contribution> contributionsList;
    
    // Admin panel content
    @FXML private ListView<Event> pendingEventsList;
    @FXML private ListView<Event> allEventsList;
    @FXML private ListView<User> usersList;
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> fundingChart;
    
    private User currentUser;
    private DatabaseManager dbManager;
    private ObservableList<Event> events;
    private ObservableList<Contribution> contributions;
    
    /**
     * Initialize the controller
     */
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        events = FXCollections.observableArrayList();
        contributions = FXCollections.observableArrayList();
        
        setupEventCategories();
        setupStatusFilter();
        setupEventList();
        setupContributionsList();
        setupAdminPanel();
        
        showDashboard();
    }
    
    /**
     * Set current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }
    
    /**
     * Update user information display
     */
    private void updateUserInfo() {
        if (currentUser != null) {
            userInfoLabel.setText(currentUser.getName());
            userRoleLabel.setText(currentUser.getRole().getValue());
            pointsLabel.setText(String.valueOf(currentUser.getPoints()));
            
            // Show admin button only for admins
            adminButton.setVisible(currentUser.isAdmin());
            
            // Load user statistics
            loadUserStatistics();
        }
    }
    
    /**
     * Setup event categories
     */
    private void setupEventCategories() {
        eventCategoryComboBox.getItems().addAll(
            "Technology", "Entertainment", "Education", "Cultural", 
            "Professional", "Sports", "Community", "Environmental"
        );
    }
    
    /**
     * Setup status filter
     */
    private void setupStatusFilter() {
        statusFilterComboBox.getItems().addAll(
            "All Statuses", "Proposed", "Approved", "Funded", "Scheduled", "Completed", "Cancelled"
        );
        statusFilterComboBox.setValue("All Statuses");
    }
    
    /**
     * Setup event list
     */
    private void setupEventList() {
        eventsList.setCellFactory(listView -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (%s) - %d votes", 
                        event.getTitle(), event.getCategory(), event.getStatus().getValue(), event.getVoteCount()));
                }
            }
        });
        
        eventsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Event selectedEvent = eventsList.getSelectionModel().getSelectedItem();
                if (selectedEvent != null) {
                    openEventDetails(selectedEvent);
                }
            }
        });
    }
    
    /**
     * Setup contributions list
     */
    private void setupContributionsList() {
        contributionsList.setCellFactory(listView -> new ListCell<Contribution>() {
            @Override
            protected void updateItem(Contribution contribution, boolean empty) {
                super.updateItem(contribution, empty);
                if (empty || contribution == null) {
                    setText(null);
                } else {
                    setText(String.format("%s: %s - %s", 
                        contribution.getType().getValue(), contribution.getDetails(), 
                        contribution.getStatus().getValue()));
                }
            }
        });
    }
    
    /**
     * Setup admin panel
     */
    private void setupAdminPanel() {
        // Setup pending events list
        pendingEventsList.setCellFactory(listView -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (%d votes)", 
                        event.getTitle(), event.getCategory(), event.getVoteCount()));
                }
            }
        });
        
        // Setup all events list
        allEventsList.setCellFactory(listView -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (%s)", 
                        event.getTitle(), event.getCategory(), event.getStatus().getValue()));
                }
            }
        });
        
        // Setup users list
        usersList.setCellFactory(listView -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (%s) - %d points", 
                        user.getName(), user.getRole().getValue(), user.getPoints()));
                }
            }
        });
    }
    
    // ==================== NAVIGATION METHODS ====================
    
    @FXML
    private void showDashboard() {
        hideAllContent();
        statsGrid.setVisible(true);
        statsGrid.setManaged(true);
        contentStack.setVisible(false);
        contentStack.setManaged(false);
        updatePageHeader("Dashboard", "Welcome back! Here's what's happening.");
        loadUserStatistics();
    }
    
    @FXML
    private void showEvents() {
        hideAllContent();
        statsGrid.setVisible(false);
        statsGrid.setManaged(false);
        contentStack.setVisible(true);
        contentStack.setManaged(true);
        eventsContent.setVisible(true);
        updatePageHeader("Browse Events", "Explore and vote on campus events");
        loadEvents();
    }
    
    @FXML
    private void showProposeEvent() {
        hideAllContent();
        statsGrid.setVisible(false);
        statsGrid.setManaged(false);
        contentStack.setVisible(true);
        contentStack.setManaged(true);
        proposeEventContent.setVisible(true);
        updatePageHeader("Propose Event", "Submit a new event proposal for the campus");
        clearProposeForm();
    }
    
    @FXML
    private void showContributions() {
        hideAllContent();
        statsGrid.setVisible(false);
        statsGrid.setManaged(false);
        contentStack.setVisible(true);
        contentStack.setManaged(true);
        contributionsContent.setVisible(true);
        updatePageHeader("My Contributions", "View your contributions and activities");
        loadUserContributions();
    }
    
    @FXML
    private void showAdminPanel() {
        if (!currentUser.isAdmin()) {
            return;
        }
        hideAllContent();
        statsGrid.setVisible(false);
        statsGrid.setManaged(false);
        contentStack.setVisible(true);
        contentStack.setManaged(true);
        adminPanelContent.setVisible(true);
        updatePageHeader("Admin Panel", "Manage events, users, and platform settings");
        loadAdminData();
    }
    
    private void updatePageHeader(String title, String subtitle) {
        if (pageTitleLabel != null) {
            pageTitleLabel.setText(title);
        }
        if (pageSubtitleLabel != null) {
            pageSubtitleLabel.setText(subtitle);
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Scene loginScene = new Scene(loader.load());
            
            // Set the Scene background color to white (for login page)
            loginScene.setFill(Color.WHITE);
            
            // Apply dark theme CSS
            loginScene.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("College Event Crowdsourcing Platform - Login");
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ==================== EVENT METHODS ====================
    
    @FXML
    private void refreshEvents() {
        loadEvents();
    }
    
    @FXML
    private void submitEvent() {
        if (eventTitleField == null || eventDescriptionField == null || eventCategoryComboBox == null ||
            budgetEstimateField == null || targetFundsField == null || eventLocationField == null) {
            return;
        }
        
        String title = eventTitleField.getText().trim();
        String description = eventDescriptionField.getText().trim();
        String category = eventCategoryComboBox.getValue();
        String budgetText = budgetEstimateField.getText().trim();
        String targetText = targetFundsField.getText().trim();
        String location = eventLocationField.getText().trim();
        
        if (title.isEmpty() || description.isEmpty() || category == null || 
            budgetText.isEmpty() || targetText.isEmpty() || location.isEmpty()) {
            eventStatusLabel.setText("Please fill in all fields.");
            eventStatusLabel.setStyle("-fx-text-fill: #ff0000;");
            return;
        }
        
        try {
            double budgetEstimate = Double.parseDouble(budgetText);
            double targetFunds = Double.parseDouble(targetText);
            
            // Create event without date (using current date as default)
            LocalDate eventDate = LocalDate.now().plusWeeks(2); // Default to 2 weeks from now
            Event newEvent = new Event(title, description, category, currentUser.getUserId(), 
                                     budgetEstimate, targetFunds, eventDate, location);
            
            if (dbManager.createEvent(newEvent)) {
                eventStatusLabel.setText("Event proposal submitted successfully!");
                eventStatusLabel.setStyle("-fx-text-fill: #00aa00;");
                
                // Award points for proposing
                dbManager.updateUserPoints(currentUser.getUserId(), 10);
                currentUser.addPoints(10);
                updateUserInfo();
                
                clearProposeForm();
            } else {
                eventStatusLabel.setText("Failed to submit event proposal.");
                eventStatusLabel.setStyle("-fx-text-fill: #ff0000;");
            }
        } catch (NumberFormatException e) {
            eventStatusLabel.setText("Please enter valid numbers for budget and target funds.");
            eventStatusLabel.setStyle("-fx-text-fill: #ff0000;");
        }
    }
    
    // ==================== DATA LOADING METHODS ====================
    
    private void loadUserStatistics() {
        if (currentUser == null) return;
        
        // Load user's proposed events count
        List<Event> userEvents = dbManager.getAllEvents().stream()
            .filter(event -> event.getProposerId() == currentUser.getUserId())
            .collect(Collectors.toList());
        proposedEventsLabel.setText(String.valueOf(userEvents.size()));
        
        // Load user's contributions count (from DB by user)
        List<Contribution> userContributions = dbManager.getContributionsForUser(currentUser.getUserId());
        contributionsLabel.setText(String.valueOf(userContributions.size()));

        // Refresh points label from DB (ensures latest points after actions)
        dbManager.getUserById(currentUser.getUserId()).ifPresent(u -> {
            pointsLabel.setText(String.valueOf(u.getPoints()));
            currentUser.setPoints(u.getPoints());
        });
        
        // Load recent activity
        loadRecentActivity();
    }
    
    private void loadRecentActivity() {
        ObservableList<String> activities = FXCollections.observableArrayList();
        
        // Add recent events
        List<Event> recentEvents = dbManager.getAllEvents().stream()
            .limit(10)
            .collect(Collectors.toList());
        for (Event event : recentEvents) {
            activities.add(String.format("%d|%s|%s", event.getEventId(), event.getTitle(), event.getCategory()));
        }
        recentActivityList.setItems(activities);
        // Click to open feedback form or view feedback
        recentActivityList.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String[] parts = item.split("\\|", 3);
                    setText(String.format("%s (%s)", parts[1], parts[2]));
                }
            }
        });
        recentActivityList.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 1) {
                String sel = recentActivityList.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    String[] parts = sel.split("\\|", 3);
                    int eventId = Integer.parseInt(parts[0]);
                    Event ev = dbManager.getAllEvents().stream().filter(e -> e.getEventId() == eventId).findFirst().orElse(null);
                    if (ev != null) {
                        if (currentUser.isStudent()) {
                            openFeedbackDialog(ev);
                        } else {
                            openFeedbackViewer(ev);
                        }
                    }
                }
            }
        });
    }
    
    private void loadEvents() {
        List<Event> allEvents = dbManager.getAllEvents();
        String selectedStatus = statusFilterComboBox.getValue();
        
        if (selectedStatus != null && !selectedStatus.equals("All Statuses")) {
            Event.EventStatus status = Event.EventStatus.fromString(selectedStatus);
            allEvents = allEvents.stream()
                .filter(event -> event.getStatus() == status)
                .collect(Collectors.toList());
        }
        
        events.clear();
        events.addAll(allEvents);
        eventsList.setItems(events);

        // Admin: enable status change via context menu
        if (currentUser != null && currentUser.isAdmin()) {
            eventsList.setCellFactory(listView -> {
                ListCell<Event> cell = new ListCell<Event>() {
                    @Override
                    protected void updateItem(Event event, boolean empty) {
                        super.updateItem(event, empty);
                        if (empty || event == null) {
                            setText(null);
                            setContextMenu(null);
                        } else {
                            setText(String.format("%s - %s (%s)", event.getTitle(), event.getCategory(), event.getStatus().getValue()));
                            ContextMenu menu = new ContextMenu();
                            menu.getStyleClass().add("admin-status-menu");
                            // Ensure theme is applied to the context menu skin
                            menu.getScene(); // force init
                            for (Event.EventStatus st : Event.EventStatus.values()) {
                                MenuItem mi = new MenuItem("Set " + st.getValue());
                                mi.setOnAction(a -> {
                                    if (dbManager.updateEventStatus(event.getEventId(), st)) {
                                        loadEvents();
                                    }
                                });
                                menu.getItems().add(mi);
                            }
                            setContextMenu(menu);
                        }
                    }
                };
                return cell;
            });
        }
    }

    // ==================== FEEDBACK DIALOGS ====================
    private void openFeedbackDialog(Event event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Submit Feedback - " + event.getTitle());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ToggleGroup stars = new ToggleGroup();
        HBox starBox = new HBox(6);
        for (int i = 1; i <= 5; i++) {
            RadioButton rb = new RadioButton("☆");
            rb.setUserData(i);
            rb.setToggleGroup(stars);
            starBox.getChildren().add(rb);
        }
        TextArea comment = new TextArea();
        comment.setPromptText("Add a comment (optional)");
        comment.setWrapText(true);
        comment.getStyleClass().add("text-area");
        VBox content = new VBox(10, new Label("Rate this event:"), starBox, new Label("Comment:"), comment);
        dialog.getDialogPane().setContent(content);
        // Apply global theme to dialog
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Toggle sel = stars.getSelectedToggle();
                int rating = sel == null ? 0 : (int) sel.getUserData();
                if (rating > 0 && currentUser != null) {
                    Feedback fb = new Feedback(event.getEventId(), currentUser.getUserId(), rating, comment.getText());
                    if (dbManager.addFeedback(fb)) {
                        // Award small points for feedback
                        dbManager.updateUserPoints(currentUser.getUserId(), 3);
                        dbManager.getUserById(currentUser.getUserId()).ifPresent(u -> {
                            currentUser.setPoints(u.getPoints());
                            pointsLabel.setText(String.valueOf(u.getPoints()));
                        });
                    }
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void openFeedbackViewer(Event event) {
        List<Feedback> feedback = dbManager.getFeedbackForEvent(event.getEventId());
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Feedback - " + event.getTitle());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        ListView<String> list = new ListView<>();
        list.getItems().addAll(feedback.stream()
            .map(f -> String.format("%s - %d★\n%s", f.getUserName() == null ? ("User " + f.getUserId()) : f.getUserName(), f.getRating(), f.getComment() == null ? "" : f.getComment()))
            .collect(Collectors.toList()));
        list.getStyleClass().add("list-view");
        dialog.getDialogPane().setContent(list);
        // Apply global theme to dialog
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        dialog.showAndWait();
    }
    
    private void loadUserContributions() {
        if (currentUser == null) return;
        
        List<Contribution> userContributions = dbManager.getContributionsForUser(currentUser.getUserId());
        contributions.clear();
        contributions.addAll(userContributions);
        contributionsList.setItems(contributions);
    }
    
    private void loadAdminData() {
        if (!currentUser.isAdmin()) return;
        
        // Load pending events
        List<Event> pendingEvents = dbManager.getEventsByStatus(Event.EventStatus.PROPOSED);
        pendingEventsList.getItems().clear();
        pendingEventsList.getItems().addAll(pendingEvents);
        
        // Load all events
        List<Event> allEvents = dbManager.getAllEvents();
        allEventsList.getItems().clear();
        allEventsList.getItems().addAll(allEvents);
        
        // Load users
        List<User> users = dbManager.getAllUsers();
        usersList.getItems().clear();
        usersList.getItems().addAll(users);
        
        // Load analytics
        loadAnalytics();
    }
    
    private void loadAnalytics() {
        // Load category distribution
        Map<String, Long> categoryCounts = dbManager.getAllEvents().stream()
            .collect(Collectors.groupingBy(Event::getCategory, Collectors.counting()));
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : categoryCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        categoryChart.setData(pieChartData);
    }
    
    // ==================== HELPER METHODS ====================
    
    private void hideAllContent() {
        eventsContent.setVisible(false);
        proposeEventContent.setVisible(false);
        contributionsContent.setVisible(false);
        adminPanelContent.setVisible(false);
        contentStack.setVisible(false);
        contentStack.setManaged(false);
    }
    
    private void clearProposeForm() {
        if (eventTitleField != null) eventTitleField.clear();
        if (eventDescriptionField != null) eventDescriptionField.clear();
        if (eventCategoryComboBox != null) eventCategoryComboBox.setValue(null);
        if (budgetEstimateField != null) budgetEstimateField.clear();
        if (targetFundsField != null) targetFundsField.clear();
        if (eventLocationField != null) eventLocationField.clear();
        if (eventStatusLabel != null) eventStatusLabel.setText("");
    }
    
    private void openEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventDetailsView.fxml"));
            Scene eventDetailsScene = new Scene(loader.load(), 900, 700);
            
            // Set the Scene background color to dark blue
            eventDetailsScene.setFill(Color.valueOf("#181E2A"));
            
            // Apply dark theme CSS
            eventDetailsScene.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
            
            EventDetailsController controller = loader.getController();
            controller.setEvent(event);
            controller.setCurrentUser(currentUser);
            controller.initialize();
            
            Stage stage = new Stage();
            stage.setScene(eventDetailsScene);
            stage.setTitle("Event Details - " + event.getTitle());
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

