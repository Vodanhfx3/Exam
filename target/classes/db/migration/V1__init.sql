-- Exam table with extra fields for duration, location, and timeslot
CREATE TABLE exam (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration_minutes INTEGER,
    location VARCHAR(255),
    timeslot TIMESTAMP
);

-- Question table
CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    text VARCHAR(1000) NOT NULL,
    correct_answer_index INTEGER NOT NULL
);

-- Each row is one choice for a question, order is preserved by serial
CREATE TABLE question_choices (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    choice TEXT NOT NULL
);

-- Exam attempt table
CREATE TABLE exam_attempt (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    user_id VARCHAR(255) NOT NULL,
    tries INTEGER NOT NULL DEFAULT 1,
    score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- User answers: each row is the answer index for a question in an attempt
CREATE TABLE attempt_answers (
    attempt_id BIGINT NOT NULL REFERENCES exam_attempt(id) ON DELETE CASCADE,
    answer INTEGER NOT NULL
);