CREATE TABLE company_recruiters (
    company_id INTEGER REFERENCES companies(company_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (company_id, user_id)
);