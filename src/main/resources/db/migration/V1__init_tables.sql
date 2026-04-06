-- 1. Таблица компаний
CREATE TABLE companies
(
    company_id  SERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    description TEXT,
    logo_url    TEXT
);

-- 2. Таблица пользователей
CREATE TABLE users
(
    user_id      SERIAL PRIMARY KEY,
    name         TEXT        NOT NULL,
    surname      TEXT        NOT NULL,
    patronymic   TEXT,
    age          INTEGER,
    phone_number TEXT,
    email        TEXT UNIQUE NOT NULL,
    login        TEXT UNIQUE NOT NULL,
    password     TEXT        NOT NULL,
    role         TEXT
);

-- 3. Таблица вакансий
CREATE TABLE vacancies
(
    vacancy_id       SERIAL PRIMARY KEY,
    title            TEXT NOT NULL,
    description      TEXT,
    salary_from      INTEGER,
    salary_to        INTEGER,
    location         TEXT,
    employment_type  TEXT,
    status           TEXT,
    required_skills  jsonb,
    experience_level INTEGER,
    company_id       INTEGER,
    CONSTRAINT fk_company FOREIGN KEY (company_id) REFERENCES companies (company_id) ON DELETE CASCADE
);

-- 4. Таблица резюме
CREATE TABLE resume
(
    resume_id       SERIAL PRIMARY KEY,
    summary         TEXT,
    education       TEXT,
    desired_salary  INTEGER,
    resume_file_url TEXT,
    skills          jsonb,
    experience      jsonb,
    user_id         INTEGER,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- 5. Таблица откликов на вакансии (resume_reactions)
CREATE TABLE resume_reactions
(
    resume_reaction_id SERIAL PRIMARY KEY,
    cover_letter       TEXT,
    applied_at         DATE DEFAULT CURRENT_DATE,
    status             TEXT,
    vacancy_id         INTEGER,
    resume_id          INTEGER,
    CONSTRAINT fk_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancies (vacancy_id) ON DELETE CASCADE,
    CONSTRAINT fk_resume FOREIGN KEY (resume_id) REFERENCES resume (resume_id) ON DELETE CASCADE
);

-- 6. Таблица интервью
CREATE TABLE interviews
(
    interview_id       SERIAL PRIMARY KEY,
    date               DATE,
    type               TEXT,
    status             TEXT,
    feedback           TEXT,
    resume_reaction_id INTEGER,
    CONSTRAINT fk_reaction FOREIGN KEY (resume_reaction_id) REFERENCES resume_reactions (resume_reaction_id) ON DELETE CASCADE
);