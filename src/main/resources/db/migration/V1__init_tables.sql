-- 1. Компании
CREATE TABLE companies
(
    company_id  SERIAL PRIMARY KEY,
    name        TEXT    NOT NULL,
    description TEXT,
    logo_url    TEXT,
    active      BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Пользователи
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
    role         TEXT        NOT NULL DEFAULT 'CANDIDATE',
    active       BOOLEAN     NOT NULL DEFAULT TRUE
);

-- 3. Вакансии
CREATE TABLE vacancies
(
    vacancy_id       SERIAL PRIMARY KEY,
    title            TEXT    NOT NULL,
    description      TEXT,
    salary_from      INTEGER,
    salary_to        INTEGER,
    location         TEXT,
    employment_type  TEXT    NOT NULL,                 -- V3: NOT NULL, предварительно заполнено 'OFFICE'
    status           TEXT    NOT NULL DEFAULT 'DRAFT', -- V3: DEFAULT 'DRAFT'
    required_skills  TEXT,                             -- V5: изменён с JSONB на TEXT
    experience_level TEXT,                             -- V5: изменён с INTEGER на TEXT
    company_id       INTEGER NOT NULL,                 -- V2: NOT NULL
    active           BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_company FOREIGN KEY (company_id) REFERENCES companies (company_id) ON DELETE CASCADE
);

-- 4. Резюме (после всех удалений полей из V5)
CREATE TABLE resume
(
    resume_id       SERIAL PRIMARY KEY,
    resume_file_url TEXT,             -- ссылка на файл (публичная или presigned)
    file_name       TEXT,             -- уникальное имя файла в MinIO (добавлено в V5)
    user_id         INTEGER NOT NULL, -- V2: NOT NULL
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- 5. Отклики на вакансии
CREATE TABLE resume_reactions
(
    resume_reaction_id SERIAL PRIMARY KEY,
    cover_letter       TEXT,
    applied_at         DATE             DEFAULT CURRENT_DATE,
    status             TEXT,
    vacancy_id         INTEGER,
    resume_id          INTEGER,
    active             BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancies (vacancy_id) ON DELETE CASCADE,
    CONSTRAINT fk_resume FOREIGN KEY (resume_id) REFERENCES resume (resume_id) ON DELETE CASCADE
);

-- 6. Интервью
CREATE TABLE interviews
(
    interview_id       SERIAL PRIMARY KEY,
    date               DATE,
    type               TEXT,
    status             TEXT,
    feedback           TEXT,
    resume_reaction_id INTEGER,
    active             BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_reaction FOREIGN KEY (resume_reaction_id) REFERENCES resume_reactions (resume_reaction_id) ON DELETE CASCADE
);