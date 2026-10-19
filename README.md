# Mini-ATS

Учебный проект: мини-система трекинга кандидатов (Applicant Tracking System). Работодатели публикуют вакансии, кандидаты откликаются с резюме, рекрутеры назначают и проводят собеседования.

## Стек

**Backend** — Java 21, Spring Boot 4 (Web, Data JPA, Security, Validation), PostgreSQL 17 + Flyway, MinIO (хранение логотипов компаний и файлов резюме), JWT-аутентификация (java-jwt), MapStruct, Lombok, springdoc-openapi (Swagger UI).

**Frontend** — React 19, Vite, Tailwind CSS, axios, react-router.

**Инфраструктура** — Docker Compose: PostgreSQL, MinIO, backend, Nginx (раздача статики фронтенда и проксирование `/api/` на backend).

## Доменная модель

| Сущность | Описание |
|---|---|
| `User` | Пользователь с ролью `CANDIDATE`, `RECRUITER` или `ADMIN` |
| `Company` | Компания (создаёт рекрутер, логотип хранится в MinIO) |
| `Vacancy` | Вакансия с типом занятости, уровнем опыта и статусом |
| `Resume` | Резюме кандидата (файл в MinIO) |
| `ResumeReaction` | Отклик кандидата на вакансию |
| `Interview` | Собеседование: планирование, обратная связь, статусы `SCHEDULED / COMPLETED / CANCELLED / POSTPONED` |

## API

Все эндпоинты начинаются с `/api/v1`. Полная интерактивная документация — Swagger UI: `http://localhost:8080/swagger-ui/index.html` (после запуска backend).

| Раздел | Эндпоинты |
|---|---|
| Auth | `POST /auth/login` |
| Users | `POST /users/register`, `GET /users`, `GET /users/{id}`, `PUT /users/me`, `PATCH /users/{id}/role`, `DELETE /users/{id}` |
| Companies | `POST /companies` (multipart, с логотипом), `GET /companies`, `GET /companies/{id}`, `PUT /companies/{id}`, `DELETE /companies/{id}`, `GET /companies/my` |
| Vacancies | `POST /vacancies`, `GET /vacancies`, `GET /vacancies/{id}`, `PUT /vacancies/{id}`, `PATCH /vacancies/{id}/close`, `DELETE /vacancies/{id}` |
| Resume | `POST /resume` (загрузка файла), `GET /resume`, `GET /resume/download`, `DELETE /resume` |
| Reactions | `POST /reactions/apply`, `GET /reactions/vacancies/{vacancyId}`, `GET /reactions/my` |
| Interviews | `POST /interviews/schedule`, `PATCH /interviews/{id}/feedback`, `GET /interviews/companies/{companyId}`, `DELETE /interviews/{id}` |

Доступ без аутентификации: `POST /auth/login`, `POST /users/register`, Swagger. Компании — только `ADMIN`/`RECRUITER`, управление пользователями — `ADMIN`, остальное — любой аутентифицированный пользователь.

Схема авторизации: зарегистрируйтесь через `POST /users/register`, получите токен через `POST /auth/login` и передавайте его в заголовке `Authorization: Bearer <token>`.

## Запуск через Docker

```bash
docker compose up --build
```

Поднимаются 4 сервиса:

| Сервис | Адрес | Назначение |
|---|---|---|
| nginx | http://localhost:80 | Фронтенд + проксирование API |
| backend | http://localhost:8080 | REST API, Swagger UI |
| postgres | localhost:5432 | БД (`admin`/`admin`, база `mini-ats`) |
| minio | http://localhost:9001 | Консоль хранилища (`admin123`/`admin123`) |

## Локальная разработка

1. Инфраструктура (PostgreSQL и MinIO):

   ```bash
   docker compose up postgres minio
   ```

2. Backend (нужен JDK 21):

   ```bash
   ./mvnw spring-boot:run
   ```

3. Frontend (Vite поднимется на http://localhost:5173 — он уже прописан в CORS backend):

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## Тесты

```bash
./mvnw test
```

Интеграционные тесты используют Testcontainers (нужен запущенный Docker): репозитории проверяются на реальном PostgreSQL в контейнере, контроллеры и сервисы — через MockMvc/Mockito.

## Структура проекта

```
src/main/java/ru/practice/mini_ats/
├── config/        # CORS, MinIO, OpenAPI
├── controllers/   # REST-контроллеры
├── dto/           # Request/Response DTO (records)
├── exception/     # GlobalExceptionHandler
├── mapper/        # MapStruct-мапперы
├── models/        # JPA-сущности и enum'ы
├── repositories/  # Spring Data JPA
├── security/      # JWT-фильтр, SecurityConfig, UserDetails
└── services/      # Бизнес-логика
src/main/resources/db/migration/  # Flyway-миграции
frontend/          # React-приложение (Vite)
nginx/             # Dockerfile и конфиг Nginx
```

## Конфигурация

Основные параметры задаются в `src/main/resources/application.properties` и переопределяются переменными окружения (так делает `docker-compose.yml`):

| Переменная | Назначение | По умолчанию |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5432/mini-ats` |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Креды БД | `admin` / `admin` |
| `JWT_SECRET` | Секрет для подписи токенов | значение из application.properties |
| `JWT_EXPIRATION` | Время жизни токена, мс | `86400000` (24 ч) |
| `MINIO_URL` | Адрес MinIO | `http://localhost:9000` |
| `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` | Креды MinIO | `admin123` / `admin123` |
