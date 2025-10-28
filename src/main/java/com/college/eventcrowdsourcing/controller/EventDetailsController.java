package com.college.eventcrowdsourcing.controller;

import com.college.eventcrowdsourcing.database.DatabaseManager;
import com.college.eventcrowdsourcing.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for event details view
 * Handles displaying event information, voting, commenting, and contributing
 */
public class EventDetailsController {
    
    @FXML private Label eventTitleLabel;
    @FXML private Label eventStatusLabel;
    @FXML private Label eventCategoryLabel;
    @FXML private Label eventDescriptionLabel;
    @FXML private Label proposerLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label locationLabel;
    @FXML private Label budgetEstimateLabel;
    @FXML private Label targetFundsLabel;
    @FXML private Label totalFundsLabel;
    @FXML private Label fundingProgressLabel;
    @FXML private Label voteCountLabel;
    
    @FXML private ProgressBar fundingProgressBar;
    @FXML private Button voteButton;
    @FXML private Button contributeButton;
    @FXML private Button backButton;
    
    @FXML private TextArea commentTextArea;
    @FXML private Button addCommentButton;
    @FXML private ListView<Comment> commentsList;
    @FXML private ListView<Contribution> contributionsList;
    
    private Event currentEvent;
    private User currentUser;
    private DatabaseManager dbManager;
    private ObservableList<Comment> comments;
    private ObservableList<Contribution> contributions;
    
    /**
     * Initialize the controller
     */
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        comments = FXCollections.observableArrayList();
        contributions = FXCollections.observableArrayList();
        
        setupCommentsList();
        setupContributionsList();
        updateEventDisplay();
        loadComments();
        loadContributions();
        updateVoteButton();
    }
    
    /**
     * Set current event
     */
    public void setEvent(Event event) {
        this.currentEvent = event;
    }
    
    /**
     * Set current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Setup comments list
     */
    private void setupCommentsList() {
        commentsList.setCellFactory(listView -> new ListCell<Comment>() {
            @Override
            protected void updateItem(Comment comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) {
                    setText(null);
                } else {
                    setText(String.format("%s: %s\n%s", 
                        comment.getUserName(), comment.getComment(), 
                        comment.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
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
                    String amountText = contribution.getAmount() > 0 ? 
                        String.format("$%.2f", contribution.getAmount()) : "N/A";
                    setText(String.format("%s: %s - %s (%s)", 
                        contribution.getUserName(), contribution.getDetails(), 
                        contribution.getStatus().getValue(), amountText));
                }
            }
        });
    }
    
    /**
     * Update event display
     */
    private void updateEventDisplay() {
        if (currentEvent == null) return;
        
        eventTitleLabel.setText(currentEvent.getTitle());
        eventStatusLabel.setText(currentEvent.getStatus().getValue().toUpperCase());
        eventCategoryLabel.setText(currentEvent.getCategory());
        eventDescriptionLabel.setText(currentEvent.getDescription());
        proposerLabel.setText(currentEvent.getProposerName());
        
        if (currentEvent.getEventDate() != null) {
            eventDateLabel.setText(currentEvent.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            eventDateLabel.setText("TBD");
        }
        
        locationLabel.setText(currentEvent.getLocation());
        budgetEstimateLabel.setText(String.format("$%.2f", currentEvent.getBudgetEstimate()));
        targetFundsLabel.setText(String.format("$%.2f", currentEvent.getTargetFunds()));
        totalFundsLabel.setText(String.format("$%.2f", currentEvent.getTotalFunds()));
        
        // Update funding progress
        double progress = currentEvent.getFundingPercentage() / 100.0;
        fundingProgressBar.setProgress(progress);
        fundingProgressLabel.setText(String.format("%.1f%%", currentEvent.getFundingPercentage()));
        
        // Update vote count
        voteCountLabel.setText(String.format("%d votes", currentEvent.getVoteCount()));
        
        // Update button states
        updateButtonStates();
    }
    
    /**
     * Update button states based on user permissions and event status
     */
    private void updateButtonStates() {
        if (currentUser == null || currentEvent == null) return;
        
        // Vote button
        boolean hasVoted = dbManager.hasUserVoted(currentEvent.getEventId(), currentUser.getUserId());
        voteButton.setText(hasVoted ? "Remove Vote" : "Vote for Event");
        
        // Contribute button
        contributeButton.setDisable(false);
    }
    
    /**
     * Update vote button text and state
     */
    private void updateVoteButton() {
        if (currentUser == null || currentEvent == null) return;
        
        boolean hasVoted = dbManager.hasUserVoted(currentEvent.getEventId(), currentUser.getUserId());
        voteButton.setText(hasVoted ? "Remove Vote" : "Vote for Event");
    }
    
    /**
     * Handle vote button click
     */
    @FXML
    private void handleVote() {
        if (currentUser == null || currentEvent == null) return;
        
        boolean hasVoted = dbManager.hasUserVoted(currentEvent.getEventId(), currentUser.getUserId());
        
        if (hasVoted) {
            // Remove vote
            if (dbManager.removeVote(currentEvent.getEventId(), currentUser.getUserId())) {
                showAlert("Vote removed successfully!", Alert.AlertType.INFORMATION);
                // Award points for voting
                dbManager.updateUserPoints(currentUser.getUserId(), 5);
                currentUser.addPoints(5);
            }
        } else {
            // Add vote
            if (dbManager.addVote(currentEvent.getEventId(), currentUser.getUserId())) {
                showAlert("Vote added successfully!", Alert.AlertType.INFORMATION);
                // Award points for voting
                dbManager.updateUserPoints(currentUser.getUserId(), 5);
                currentUser.addPoints(5);
            }
        }
        
        updateVoteButton();
        // Refresh event data
        refreshEventData();
    }
    
    /**
     * Handle contribute button click
     */
    @FXML
    private void handleContribute() {
        if (currentUser == null || currentEvent == null) return;
        
        // Create contribution dialog
        ContributionDialog dialog = new ContributionDialog();
        dialog.showAndWait().ifPresent(contribution -> {
            contribution.setEventId(currentEvent.getEventId());
            contribution.setUserId(currentUser.getUserId());
            
            if (dbManager.addContribution(contribution)) {
                showAlert("Contribution submitted successfully!", Alert.AlertType.INFORMATION);
                // Award points for contributing
                dbManager.updateUserPoints(currentUser.getUserId(), 10);
                currentUser.addPoints(10);
                loadContributions();
                refreshEventData();
            } else {
                showAlert("Failed to submit contribution.", Alert.AlertType.ERROR);
            }
        });
    }
    
    /**
     * Add comment
     */
    @FXML
    private void addComment() {
        if (currentUser == null || currentEvent == null) return;
        
        String commentText = commentTextArea.getText().trim();
        if (commentText.isEmpty()) {
            showAlert("Please enter a comment.", Alert.AlertType.WARNING);
            return;
        }
        
        Comment comment = new Comment(currentEvent.getEventId(), currentUser.getUserId(), commentText);
        if (dbManager.addComment(comment)) {
            showAlert("Comment added successfully!", Alert.AlertType.INFORMATION);
            // Award points for commenting
            dbManager.updateUserPoints(currentUser.getUserId(), 2);
            currentUser.addPoints(2);
            commentTextArea.clear();
            loadComments();
        } else {
            showAlert("Failed to add comment.", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Handle back button
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Load comments for the event
     */
    private void loadComments() {
        if (currentEvent == null) return;
        
        List<Comment> eventComments = dbManager.getCommentsForEvent(currentEvent.getEventId());
        comments.clear();
        comments.addAll(eventComments);
        commentsList.setItems(comments);
    }
    
    /**
     * Load contributions for the event
     */
    private void loadContributions() {
        if (currentEvent == null) return;
        
        List<Contribution> eventContributions = dbManager.getContributionsForEvent(currentEvent.getEventId());
        contributions.clear();
        contributions.addAll(eventContributions);
        contributionsList.setItems(contributions);
    }
    
    /**
     * Refresh event data
     */
    private void refreshEventData() {
        // This would reload the event data from database
        // For now, we'll just update the display
        updateEventDisplay();
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Event Crowdsourcing Platform");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Contribution dialog class
     */
    private static class ContributionDialog extends Dialog<Contribution> {
        private TextField detailsField;
        private TextField amountField;
        private ComboBox<Contribution.ContributionType> typeComboBox;
        
        public ContributionDialog() {
            setTitle("Add Contribution");
            setHeaderText("Contribute to this event");
            
            // Create form
            HBox typeBox = new HBox(10);
            typeBox.getChildren().addAll(
                new Label("Type:"),
                typeComboBox = new ComboBox<>()
            );
            typeComboBox.getItems().addAll(
                Contribution.ContributionType.SKILL,
                Contribution.ContributionType.RESOURCE,
                Contribution.ContributionType.FUND
            );
            typeComboBox.setValue(Contribution.ContributionType.SKILL);
            
            HBox detailsBox = new HBox(10);
            detailsBox.getChildren().addAll(
                new Label("Details:"),
                detailsField = new TextField()
            );
            detailsField.setPromptText("Describe your contribution");
            
            HBox amountBox = new HBox(10);
            amountBox.getChildren().addAll(
                new Label("Amount:"),
                amountField = new TextField()
            );
            amountField.setPromptText("0.00");
            
            // Add to dialog
            getDialogPane().setContent(new VBox(10, typeBox, detailsBox, amountBox));
            
            // Add buttons
            ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
            
            // Set result converter
            setResultConverter(dialogButton -> {
                if (dialogButton == submitButtonType) {
                    try {
                        double amount = amountField.getText().isEmpty() ? 0.0 : 
                                       Double.parseDouble(amountField.getText());
                        return new Contribution(0, 0, typeComboBox.getValue(), 
                                              detailsField.getText(), amount);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            });
        }
    }
}
