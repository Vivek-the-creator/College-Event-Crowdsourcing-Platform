-- Sample data for College Event Crowdsourcing Platform
-- Insert sample users, events, and related data

USE college_events;

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


