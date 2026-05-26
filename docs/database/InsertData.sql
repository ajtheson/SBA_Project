USE QuizOnlineSystem;
GO

-- Seed data for current schema.
-- All sample accounts use plaintext password: 1
-- BCrypt hash generated for "1"; Spring Security can verify this value.

DELETE FROM Answer;
DELETE FROM Submission;
DELETE FROM Exam;
DELETE FROM Choice;
DELETE FROM Question;
DELETE FROM Quiz;
DELETE FROM Student;
DELETE FROM Teacher;
GO

DECLARE @PasswordHash varchar(255) = '$2b$12$xu5bUTJnODExucbYT4IAe.WohBB2Qt08RAaVHUxL9fYjNYyct/.uK';

INSERT INTO Teacher (email, password, fullname, school) VALUES
('teacher1@example.com', @PasswordHash, 'Nguyen Van A', 'High School A'),
('teacher2@example.com', @PasswordHash, 'Tran Thi B', 'High School B');

INSERT INTO Student (email, password, fullname, className, school) VALUES
('student1@example.com', @PasswordHash, 'Le Van C', '12A1', 'High School A'),
('student2@example.com', @PasswordHash, 'Pham Thi D', '12A2', 'High School B'),
('student3@example.com', @PasswordHash, 'Nguyen Van E', '12A3', 'High School C'),
('student4@example.com', @PasswordHash, 'Tran Thi F', '12A4', 'High School D'),
('student5@example.com', @PasswordHash, 'Hoang Van G', '12A1', 'High School A');

DECLARE @Teacher1Id int = (SELECT teacherID FROM Teacher WHERE email = 'teacher1@example.com');
DECLARE @Teacher2Id int = (SELECT teacherID FROM Teacher WHERE email = 'teacher2@example.com');

INSERT INTO Quiz (quizName, quantity, isDeleted, teacherID) VALUES
('SSL101 Communication Skills', 5, 0, @Teacher1Id),
('PRF192 C Programming', 5, 0, @Teacher2Id);

DECLARE @QuizSslId int = (SELECT quizID FROM Quiz WHERE quizName = 'SSL101 Communication Skills');
DECLARE @QuizPrfId int = (SELECT quizID FROM Quiz WHERE quizName = 'PRF192 C Programming');

INSERT INTO Question (content, isMultipleChoice, isDeleted, quizID) VALUES
('How is bias displayed in this statement?', 0, 0, @QuizSslId),
('According to the lesson, how long does it take to master communication skills?', 0, 0, @QuizSslId),
('Taking notes during lectures helps students get the most out of their courses. What should students note down? (Choose 2)', 1, 0, @QuizSslId),
('Which listeners interrupt whenever they do not understand?', 0, 0, @QuizSslId),
('What is the relationship between critical thinking and communication?', 0, 0, @QuizSslId),
('Who is the inventor of the C programming language?', 0, 0, @QuizPrfId),
('How does compilation differ from interpretation in C programming?', 0, 0, @QuizPrfId),
('Errors caused by syntax mistakes belong to which category?', 0, 0, @QuizPrfId),
('Which of the following statements is incorrect?', 0, 0, @QuizPrfId),
('Which statement accurately describes the scope of a local variable in C?', 0, 0, @QuizPrfId);

DECLARE @Q1 int = (SELECT questionID FROM Question WHERE quizID = @QuizSslId AND content = 'How is bias displayed in this statement?');
DECLARE @Q2 int = (SELECT questionID FROM Question WHERE quizID = @QuizSslId AND content = 'According to the lesson, how long does it take to master communication skills?');
DECLARE @Q3 int = (SELECT questionID FROM Question WHERE quizID = @QuizSslId AND content LIKE 'Taking notes during lectures%');
DECLARE @Q4 int = (SELECT questionID FROM Question WHERE quizID = @QuizSslId AND content = 'Which listeners interrupt whenever they do not understand?');
DECLARE @Q5 int = (SELECT questionID FROM Question WHERE quizID = @QuizSslId AND content = 'What is the relationship between critical thinking and communication?');
DECLARE @Q6 int = (SELECT questionID FROM Question WHERE quizID = @QuizPrfId AND content = 'Who is the inventor of the C programming language?');
DECLARE @Q7 int = (SELECT questionID FROM Question WHERE quizID = @QuizPrfId AND content = 'How does compilation differ from interpretation in C programming?');
DECLARE @Q8 int = (SELECT questionID FROM Question WHERE quizID = @QuizPrfId AND content = 'Errors caused by syntax mistakes belong to which category?');
DECLARE @Q9 int = (SELECT questionID FROM Question WHERE quizID = @QuizPrfId AND content = 'Which of the following statements is incorrect?');
DECLARE @Q10 int = (SELECT questionID FROM Question WHERE quizID = @QuizPrfId AND content = 'Which statement accurately describes the scope of a local variable in C?');

INSERT INTO Choice (choiceContent, isCorrectChoice, isDeleted, questionID) VALUES
('False classification schemes', 0, 0, @Q1),
('Use of passive voice to hide responsibility', 1, 0, @Q1),
('Ad hominem', 0, 0, @Q1),
('Appealing to membership of a common group', 0, 0, @Q1),
('1 year', 0, 0, @Q2),
('5 years', 0, 0, @Q2),
('2-3 years', 0, 0, @Q2),
('Your whole life', 1, 0, @Q2),
('As much as possible', 0, 0, @Q3),
('Main ideas, processes, formulas, arguments', 1, 0, @Q3),
('All information from the textbook', 0, 0, @Q3),
('Questions for the lecturer or tutor', 1, 0, @Q3),
('Effective listeners', 0, 0, @Q4),
('Ineffective listeners', 1, 0, @Q4),
('Empathic listeners', 0, 0, @Q4),
('Surface listeners', 0, 0, @Q4),
('Communication is needed to form an argument and display critical thinking', 1, 0, @Q5),
('These two have no relationship at all', 0, 0, @Q5),
('Critical thinking only helps you win discussions', 0, 0, @Q5),
('Communication is unrelated to thinking', 0, 0, @Q5),
('Dennis Ritchie', 1, 0, @Q6),
('Bjarne Stroustrup', 0, 0, @Q6),
('Brian Kernighan', 0, 0, @Q6),
('Niklaus Wirth', 0, 0, @Q6),
('Compilation converts source code to machine code; interpretation executes code line by line', 1, 0, @Q7),
('Compilation and interpretation are the same process', 0, 0, @Q7),
('Algorithm is another term for interpretation', 0, 0, @Q7),
('IDE tools are responsible for both compilation and interpretation', 0, 0, @Q7),
('Compile-time error', 1, 0, @Q8),
('Run-time error', 0, 0, @Q8),
('Input error', 0, 0, @Q8),
('Linking error', 0, 0, @Q8),
('Higher-level languages are used to make programs longer', 0, 0, @Q9),
('High-level code must be translated before running', 0, 0, @Q9),
('C supports structured programming', 0, 0, @Q9),
('Simple assembly programs contain very few statements compared with high-level code', 1, 0, @Q9),
('It is visible only within the function where it is declared', 1, 0, @Q10),
('It is accessible throughout the entire program', 0, 0, @Q10),
('It can be accessed by any function in the same source file', 0, 0, @Q10),
('It is globally available but cannot be modified', 0, 0, @Q10);

INSERT INTO Exam (examCode, examName, duration, startTime, endTime, attempts, isReview, quizID) VALUES
(12345, 'SSL101 Practice - Ongoing', 30, DATEADD(hour, -1, GETDATE()), DATEADD(day, 1, GETDATE()), 2, 1, @QuizSslId),
(23456, 'SSL101 Completed Review', 30, DATEADD(day, -3, GETDATE()), DATEADD(day, -2, GETDATE()), 2, 1, @QuizSslId),
(34567, 'PRF192 Practice - Ongoing', 45, DATEADD(hour, -1, GETDATE()), DATEADD(day, 1, GETDATE()), 1, 1, @QuizPrfId);

DECLARE @CompletedExamId int = (SELECT examID FROM Exam WHERE examCode = 23456);
DECLARE @Student1Id int = (SELECT studentID FROM Student WHERE email = 'student1@example.com');
DECLARE @Student2Id int = (SELECT studentID FROM Student WHERE email = 'student2@example.com');
DECLARE @Student3Id int = (SELECT studentID FROM Student WHERE email = 'student3@example.com');

INSERT INTO Submission (submitTime, duration, selected, correctAnswers, score, isSubmit, studentID, examID) VALUES
(DATEADD(day, -2, DATEADD(minute, -30, GETDATE())), 25, 5, 4, 8.0, 1, @Student1Id, @CompletedExamId),
(DATEADD(day, -2, DATEADD(minute, -20, GETDATE())), 22, 5, 3, 6.0, 1, @Student2Id, @CompletedExamId),
(DATEADD(day, -2, DATEADD(minute, -15, GETDATE())), 28, 4, 3, 6.0, 1, @Student3Id, @CompletedExamId);

DECLARE @Submission1Id int = (SELECT submissionID FROM Submission WHERE studentID = @Student1Id AND examID = @CompletedExamId);
DECLARE @Submission2Id int = (SELECT submissionID FROM Submission WHERE studentID = @Student2Id AND examID = @CompletedExamId);
DECLARE @Submission3Id int = (SELECT submissionID FROM Submission WHERE studentID = @Student3Id AND examID = @CompletedExamId);

DECLARE @C1Correct int = (SELECT choiceID FROM Choice WHERE questionID = @Q1 AND choiceContent = 'Use of passive voice to hide responsibility');
DECLARE @C2Correct int = (SELECT choiceID FROM Choice WHERE questionID = @Q2 AND choiceContent = 'Your whole life');
DECLARE @C3CorrectA int = (SELECT choiceID FROM Choice WHERE questionID = @Q3 AND choiceContent = 'Main ideas, processes, formulas, arguments');
DECLARE @C3CorrectB int = (SELECT choiceID FROM Choice WHERE questionID = @Q3 AND choiceContent = 'Questions for the lecturer or tutor');
DECLARE @C3Wrong int = (SELECT choiceID FROM Choice WHERE questionID = @Q3 AND choiceContent = 'All information from the textbook');
DECLARE @C4Correct int = (SELECT choiceID FROM Choice WHERE questionID = @Q4 AND choiceContent = 'Ineffective listeners');
DECLARE @C4Wrong int = (SELECT choiceID FROM Choice WHERE questionID = @Q4 AND choiceContent = 'Effective listeners');
DECLARE @C5Correct int = (SELECT choiceID FROM Choice WHERE questionID = @Q5 AND choiceContent = 'Communication is needed to form an argument and display critical thinking');
DECLARE @C5Wrong int = (SELECT choiceID FROM Choice WHERE questionID = @Q5 AND choiceContent = 'These two have no relationship at all');

INSERT INTO Answer (studentChoice, isCorrect, questionID, submissionID) VALUES
(CAST(@C1Correct AS varchar(255)), 1, @Q1, @Submission1Id),
(CAST(@C2Correct AS varchar(255)), 1, @Q2, @Submission1Id),
(CAST(@C3CorrectA AS varchar(20)) + ' ' + CAST(@C3CorrectB AS varchar(20)), 1, @Q3, @Submission1Id),
(CAST(@C4Wrong AS varchar(255)), 0, @Q4, @Submission1Id),
(CAST(@C5Correct AS varchar(255)), 1, @Q5, @Submission1Id),

(CAST(@C4Wrong AS varchar(255)), 0, @Q1, @Submission2Id),
(CAST(@C2Correct AS varchar(255)), 1, @Q2, @Submission2Id),
(CAST(@C3CorrectA AS varchar(20)) + ' ' + CAST(@C3CorrectB AS varchar(20)), 1, @Q3, @Submission2Id),
(CAST(@C4Correct AS varchar(255)), 1, @Q4, @Submission2Id),
(CAST(@C5Wrong AS varchar(255)), 0, @Q5, @Submission2Id),

(CAST(@C1Correct AS varchar(255)), 1, @Q1, @Submission3Id),
('', 0, @Q2, @Submission3Id),
(CAST(@C3CorrectA AS varchar(20)) + ' ' + CAST(@C3Wrong AS varchar(20)), 0, @Q3, @Submission3Id),
(CAST(@C4Correct AS varchar(255)), 1, @Q4, @Submission3Id),
(CAST(@C5Correct AS varchar(255)), 1, @Q5, @Submission3Id);
GO
