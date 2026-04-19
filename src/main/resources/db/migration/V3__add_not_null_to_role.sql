-- V3__add_not_null_to_role.sql

-- Таблица Users
-- Заполняем существующие NULL значения дефолтной ролью
UPDATE users SET role = 'CANDIDATE' WHERE role IS NULL;

-- Роль для будущих записей по умолчанию - кандидат
ALTER TABLE users ALTER COLUMN role SET DEFAULT 'CANDIDATE';

-- Запрещаем NULL
ALTER TABLE users ALTER COLUMN role SET NOT NULL;

-- Таблица Vacancies
-- Заполняем существующие NULL значения дефолтной ролью
UPDATE vacancies SET employment_type = 'OFFICE' WHERE employment_type IS NULL;

-- Тип занятости нужно указать явно
ALTER TABLE vacancies ALTER COLUMN employment_type SET NOT NULL;

-- При создании вакансии она по умолчанию будет в черновиках
ALTER TABLE vacancies ALTER COLUMN status SET DEFAULT 'DRAFT';