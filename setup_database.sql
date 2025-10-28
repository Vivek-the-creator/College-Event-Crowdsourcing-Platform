-- Complete Database Setup for College Event Crowdsourcing Platform
-- Run this single query in MySQL Workbench to set up everything

-- Create database
CREATE DATABASE IF NOT EXISTS college_events;
USE college_events;

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS event_organizers;
DROP TABLE IF EXISTS contributions;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student', 'faculty', 'admin') NOT NULL DEFAULT 'student',
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create events table
CREATE TABLE events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    status ENUM('proposed', 'approved', 'funded', 'scheduled', 'completed', 'cancelled') DEFAULT 'proposed',
    proposer_id INT NOT NULL,
    budget_estimate DOUBLE DEFAULT 0,
    total_funds DOUBLE DEFAULT 0,
    target_funds DOUBLE DEFAULT 0,
    event_date DATE,
    location VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (proposer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create votes table
CREATE TABLE votes (
    vote_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_vote (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create comments table
CREATE TABLE comments (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    comment TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create contributions table
CREATE TABLE contributions (
    contribution_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    type ENUM('skill', 'resource', 'fund') NOT NULL,
    details TEXT,
    amount DOUBLE DEFAULT 0,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create event organizers table
CREATE TABLE event_organizers (
    organizer_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(50) DEFAULT 'organizer',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_events_category ON events(category);
CREATE INDEX idx_events_proposer ON events(proposer_id);
CREATE INDEX idx_votes_event ON votes(event_id);
CREATE INDEX idx_votes_user ON votes(user_id);
CREATE INDEX idx_comments_event ON comments(event_id);
CREATE INDEX idx_contributions_event ON contributions(event_id);
CREATE INDEX idx_contributions_user ON contributions(user_id);

-- Insert sample users
INSERT INTO users (name, email, password_hash, role, points) VALUES
('John Smith', 'john.smith@college.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'student', 150),
('Sarah Johnson', 'sarah.johnson@college.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'student', 200),
('Mike Wilson', 'mike.wilson@college.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'student', 75),
('Dr. Emily Davis', 'emily.davis@college.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'faculty', 300),
('Prof. Robert Brown', 'robert.brown@college.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'faculty', 250),
('Admin User', 'admin@college.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'admin', 500);

-- Insert sample events
INSERT INTO events (title, description, category, proposer_id, budget_estimate, target_funds, status, event_date, location) VALUES
('Tech Innovation Hackathon', 'A 48-hour hackathon focusing on innovative solutions for campus problems. Open to all students with prizes for top teams.', 'Technology', 1, 5000.00, 5000.00, 'approved', '2024-03-15', 'Computer Science Building'),
('Spring Music Festival', 'Annual spring music festival featuring student bands, food trucks, and outdoor activities. Family-friendly event.', 'Entertainment', 2, 8000.00, 8000.00, 'funded', '2024-04-20', 'Main Quad'),
('Environmental Awareness Week', 'Week-long series of events promoting environmental consciousness including workshops, clean-up drives, and guest speakers.', 'Education', 4, 3000.00, 3000.00, 'scheduled', '2024-05-01', 'Various Campus Locations'),
('Cultural Diversity Fair', 'Celebration of different cultures with food, music, dance performances, and cultural displays from various student organizations.', 'Cultural', 1, 4000.00, 4000.00, 'proposed', '2024-06-10', 'Student Center'),
('Career Development Workshop', 'Professional development workshop with industry experts covering resume building, interview skills, and networking.', 'Professional', 5, 2000.00, 2000.00, 'approved', '2024-03-30', 'Business School'),
('Sports Tournament', 'Inter-departmental sports tournament including basketball, soccer, and volleyball with trophies and prizes.', 'Sports', 3, 1500.00, 1500.00, 'proposed', '2024-04-15', 'Sports Complex');

-- Insert sample votes
INSERT INTO votes (event_id, user_id) VALUES
(1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(2, 1), (2, 3), (2, 4), (2, 5),
(3, 1), (3, 2), (3, 3), (3, 5), (3, 6),
(4, 2), (4, 3), (4, 4),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 6),
(6, 1), (6, 2), (6, 4), (6, 5);

-- Insert sample comments
INSERT INTO comments (event_id, user_id, comment) VALUES
(1, 2, 'This sounds amazing! I would love to participate in the hackathon.'),
(1, 4, 'Great initiative! I can help with judging and mentoring.'),
(2, 1, 'The music festival was fantastic last year. Looking forward to this one!'),
(2, 3, 'I can help with sound equipment setup.'),
(3, 5, 'Environmental awareness is crucial. I fully support this initiative.'),
(3, 2, 'Count me in for the clean-up drives!'),
(4, 4, 'Cultural diversity is important for our campus community.'),
(5, 1, 'This workshop would be very helpful for students preparing for job interviews.'),
(6, 2, 'Sports tournaments are great for building team spirit!');

-- Insert sample contributions
INSERT INTO contributions (event_id, user_id, type, details, amount, status) VALUES
(1, 4, 'skill', 'Can provide technical mentoring and judging', 0, 'approved'),
(1, 5, 'resource', 'Can provide laptops and development tools', 0, 'approved'),
(1, 2, 'fund', 'Pledge for hackathon prizes', 200.00, 'approved'),
(2, 3, 'skill', 'Can help with sound and lighting setup', 0, 'approved'),
(2, 1, 'fund', 'Support for music festival', 150.00, 'approved'),
(2, 4, 'resource', 'Can provide stage and equipment', 0, 'approved'),
(3, 5, 'skill', 'Can lead environmental workshops', 0, 'approved'),
(3, 2, 'skill', 'Can organize clean-up activities', 0, 'approved'),
(3, 1, 'fund', 'Support for environmental awareness', 100.00, 'approved'),
(4, 4, 'skill', 'Can coordinate cultural performances', 0, 'pending'),
(5, 6, 'skill', 'Can provide career counseling expertise', 0, 'approved'),
(5, 3, 'fund', 'Support for career workshop', 75.00, 'approved'),
(6, 1, 'skill', 'Can help with tournament organization', 0, 'pending'),
(6, 2, 'fund', 'Support for sports tournament', 50.00, 'pending');

-- Insert sample event organizers
INSERT INTO event_organizers (event_id, user_id, role) VALUES
(1, 4, 'Lead Organizer'),
(1, 5, 'Technical Coordinator'),
(2, 2, 'Event Coordinator'),
(2, 3, 'Logistics Coordinator'),
(3, 5, 'Program Director'),
(3, 2, 'Activities Coordinator'),
(5, 6, 'Workshop Director');

-- Update total funds for events based on contributions
UPDATE events SET total_funds = (
    SELECT COALESCE(SUM(amount), 0) 
    FROM contributions 
    WHERE event_id = events.event_id AND type = 'fund' AND status = 'approved'
) WHERE event_id IN (1, 2, 3, 5, 6);

-- Display setup completion message
SELECT 'Database setup completed successfully!' as Status,
       (SELECT COUNT(*) FROM users) as Users_Count,
       (SELECT COUNT(*) FROM events) as Events_Count,
       (SELECT COUNT(*) FROM votes) as Votes_Count,
       (SELECT COUNT(*) FROM comments) as Comments_Count,
       (SELECT COUNT(*) FROM contributions) as Contributions_Count;


