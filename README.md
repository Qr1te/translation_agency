# Translation Agency

Spring Boot REST API для агентства переводов.

## Стек
- Java 21
- Spring Boot 4.0.2
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Maven
- Checkstyle

## Структура
Архитектура по слоям:
- `controller` — REST endpoint-ы
- `service` / `service.impl` — бизнес-логика
- `repository` — доступ к БД
- `model` — JPA-сущности
- `dto` — входные/выходные модели
- `mapper` — преобразования `Entity <-> DTO`

Базовый пакет приложения: `com.qritiooo.translationagency`

## Требования
- JDK 21+
- Maven (или использовать `./mvnw`)
- PostgreSQL

## Конфигурация БД
Файл: `src/main/resources/application.properties`

По умолчанию:
- URL: `jdbc:postgresql://localhost:5432/translation_agency`
- User: `postgres`
- Password: из переменной окружения `DB_PASSWORD`

Перед запуском установи пароль:

```powershell
$env:DB_PASSWORD="your_postgres_password"
```

## Запуск

```powershell
./mvnw spring-boot:run
```

Приложение стартует на `http://localhost:8080`.

## Сборка

Полная сборка:

```powershell
./mvnw clean package
```

Артефакт:
- `target/translation_agency-0.0.1-SNAPSHOT.jar`

Запуск jar:

```powershell
java -jar target/translation_agency-0.0.1-SNAPSHOT.jar
```

## Docker

Подготовь `.env` на основе `.env.example` и при необходимости измени значения:

```powershell
Copy-Item .env.example .env
```

Сборка образа:

```powershell
docker build -t translation-agency .
```

Запуск backend + PostgreSQL через Docker Compose:

```powershell
docker compose up --build
```

После старта приложение доступно на:
- `http://localhost:8080`
- healthcheck: `http://localhost:8080/actuator/health`

## Frontend Env

Для frontend можно создать `frontend/.env` на основе `frontend/.env.example`.

- `VITE_API_BASE_URL` — базовый URL backend API для отдельного frontend-хостинга
- `VITE_DEV_API_PROXY_TARGET` — target для локального Vite proxy

Если `VITE_API_BASE_URL` пустой, frontend использует относительные пути (`/api/...`).

## CI/CD

Добавлен workflow `.github/workflows/ci-cd.yml` со стадиями:
- backend tests
- backend build
- frontend lint/build
- docker image build
- docker compose smoke healthcheck
- deploy hook + remote healthcheck

Для автоматического деплоя на Render в GitHub Secrets нужно добавить:
- `RENDER_DEPLOY_HOOK_URL`
- `RENDER_HEALTHCHECK_URL`

Если секреты не заданы, workflow выполнит только CI-проверки без деплоя.

## Render PaaS

В репозитории добавлен `render.yaml`, который поднимает:
- web service для backend API
- free Render Postgres

Что уже настроено для Render:
- Docker-based deploy из этого репозитория
- healthcheck `GET /actuator/health`
- free Postgres как источник `DATABASE_URL`
- поддержка `DATABASE_URL` в приложении

Как развернуть:
1. Зайди в Render и выбери `New +` -> `Blueprint`.
2. Подключи GitHub-репозиторий.
3. Подтверди создание сервисов из `render.yaml`.
4. После создания backend будет доступен по адресу вида `https://<service-name>.onrender.com`.
5. Создай deploy hook у web service и добавь его в GitHub secret `RENDER_DEPLOY_HOOK_URL`.
6. Добавь `https://<service-name>.onrender.com/actuator/health` в GitHub secret `RENDER_HEALTHCHECK_URL`.

Важно: free Render Postgres по официальной документации истекает через 30 дней, поэтому такой вариант подходит для демо, курсовой или защиты, но не для постоянного production.

## Checkstyle

Проверка стиля:

```powershell
./mvnw checkstyle:check
```

Конфиги:
- `config/checkstyle.xml`
- `config/checkstyle-suppressions.xml`

## API

### Clients
- `POST /api/clients`
- `PUT /api/clients/{id}`
- `GET /api/clients/{id}`
- `GET /api/clients`
- `DELETE /api/clients/{id}`

### Languages
- `POST /api/languages`
- `PUT /api/languages/{id}`
- `GET /api/languages/{id}`
- `GET /api/languages`
- `DELETE /api/languages/{id}`

### Translators
- `POST /api/translators`
- `PUT /api/translators/{id}`
- `GET /api/translators/{id}`
- `GET /api/translators`
- `DELETE /api/translators/{id}`

### Orders
- `POST /api/orders/create`
- `PUT /api/orders/update/{id}`
- `GET /api/orders/{id}`
- `GET /api/orders?title=...`
- `GET /api/orders?status=...&clientId=...&translatorId=...`
- `GET /api/orders/search/jpql?...`
- `GET /api/orders/search/native?...`
- `DELETE /api/orders/delete/{id}`

### Documents
- `POST /api/documents`
- `PUT /api/documents/{id}`
- `GET /api/documents/{id}`
- `GET /api/documents?orderId=...`
- `DELETE /api/documents/{id}`
