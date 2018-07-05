DELETE FROM message;
DELETE FROM message_thread;
DELETE FROM topic;
DELETE FROM category;

INSERT INTO category (id, name, description) VALUES
	(1, "General", "General discussion"),
	(2, "Other", "Other topics"),
	(3, "Empty", "Empty category");
INSERT INTO topic (id, name, description, category_id) VALUES
	(1, "General discussion", "Discussion about this program", 1),
	(2, "Issues", "Known issues affecting the project", 1),
	(3, "Off-topic", "Non-related discussions", 2);
INSERT INTO message_thread (id, title, stickied, last_update, topic_id) VALUES
	(1, "Thread of many replies", FALSE, "2018-02-02 13:37", 1),
	(2, "Which things are important?", TRUE, "2018-02-01 08:55", 1),
	(3, "TESTING THINGS!", FALSE, "2018-03-22", 1),
	(4, "New year!", FALSE, "2018-01-01", 1),
	(5, "Completely unrelated topic", FALSE, "2017-05-05", 3);
INSERT INTO message (id, content, moderator_note, time_posted, thread_id) VALUES
	(1, "First!", "Really?", "2018-01-01", 1),
	(2, "This is a fancy message", null, "2018-01-02", 1),
	(3, "Another poster has joined this party", null, "2018-01-03", 1),
	(4, "Let's introduce another poster into the fray, shall we?", null, "2018-01-03", 1),
	(5, "I don't know what I should reply with", null, "2018-01-04", 1),
	(6, "This is the sixth message to this chain", null, "2018-01-05", 1),
	(7, "Another message has been added", "But why?", "2018-01-06", 1),
	(8, "Messages ceep on coming", null, "2018-01-07", 1),
	(9, "This is probably the last message", null, "2018-01-08", 1);