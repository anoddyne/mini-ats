-- V2__add_not_null_constraints.sql

-- Резюме без пользователя не может существовать
ALTER TABLE resume
    ALTER COLUMN user_id SET NOT NULL;

-- Вакансия без компании не может существовать
ALTER TABLE vacancies
    ALTER COLUMN company_id SET NOT NULL;