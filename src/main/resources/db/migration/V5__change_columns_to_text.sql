-- V5__change_columns_to_text.sql

-- Изменяем таблицу вакансий
ALTER TABLE vacancies
    ALTER COLUMN required_skills TYPE TEXT USING required_skills::TEXT;

-- Изменяем таблицу резюме
ALTER TABLE resume
    ALTER COLUMN skills TYPE TEXT USING skills::TEXT,
    ALTER COLUMN experience TYPE TEXT USING experience::TEXT;

ALTER TABLE vacancies
    ALTER COLUMN experience_level TYPE TEXT USING experience_level::TEXT;