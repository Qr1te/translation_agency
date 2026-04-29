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

Конфигурация БД задаётся только через переменные окружения или file-based secrets:
- `SPRING_DATASOURCE_URL` или `DATABASE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD` или `DB_PASSWORD`
- `SPRING_DATASOURCE_PASSWORD_FILE` или `DB_PASSWORD_FILE`

Пример локального запуска без хранения пароля в репозитории:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/translation_agency"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD_FILE=(Resolve-Path .\secrets\postgres_password.txt)
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

Подготовь `.env` и локальный secret-файл:

```powershell
Copy-Item .env.example .env
New-Item -ItemType Directory -Force secrets | Out-Null
Copy-Item secrets/postgres_password.txt.example secrets/postgres_password.txt
```

Запиши в `secrets/postgres_password.txt` сильный пароль.
Важно: `secrets/postgres_password.txt` должен быть именно файлом, не папкой.
`docker compose` монтирует его в контейнеры как read-only secret file, поэтому пароль не хранится в `docker-compose.yml`
и не попадает в tracked env-файлы.

Сборка образа:

```powershell
docker build -t translation-agency .
```

Запуск всей локальной среды через Docker Compose
база + backend + frontend одной командой:

```powershell
docker compose up --build
```

После старта приложение доступно на:
- `http://localhost:5173` — frontend
- `http://localhost:8080`
- healthcheck: `http://localhost:8080/actuator/health`

Если нужно запустить всё в фоне:

```powershell
docker compose up --build -d
```

Остановить всё:

```powershell
docker compose down
```

## Frontend Env

Для frontend можно создать `frontend/.env` на основе `frontend/.env.example`.

- `VITE_API_BASE_URL` — базовый URL backend API для отдельного frontend-хостинга
- `VITE_DEV_API_PROXY_TARGET` — target для локального Vite proxy

Если `VITE_API_BASE_URL` пустой, frontend использует относительные пути (`/api/...`).

## CI/CD

В репозитории есть tracked workflow `.github/workflows/ci-cd.yml`.
Он не читает реальный `.env` из Git и собирает временный `.env` в CI из GitHub Actions secrets/variables.

Что добавить в `GitHub -> Settings -> Secrets and variables -> Actions`:
- Secret `CI_POSTGRES_PASSWORD`
  Используется в `docker-smoke` для локального Postgres внутри CI. Не обязателен: если не задан, workflow возьмёт fallback `ci-postgres-password`.
- Secret `RENDER_DEPLOY_HOOK_URL`
  Обязателен для deploy job. Это deploy hook Render web service.
- Variable `RENDER_HEALTHCHECK_URL`
  Не секрет. Рекомендуемое значение: `https://<service-name>.onrender.com/actuator/health`.

Опциональные GitHub Variables для `docker-smoke`, если хочешь переопределять дефолты без правки YAML:
- `CI_SERVER_PORT`
- `CI_FRONTEND_PORT`
- `CI_POSTGRES_PORT`
- `CI_POSTGRES_DB`
- `CI_POSTGRES_USER`
- `CI_SPRING_JPA_HIBERNATE_DDL_AUTO`
- `CI_APP_CORS_ALLOWED_ORIGINS`

Если эти variables не заданы, workflow использует безопасные дефолты прямо в CI.
Реальные пароли, токены и ключи в репозиторий не коммить.

## Render PaaS

В репозитории добавлен `render.yaml`, который поднимает:
- web service для backend API
- free Render Postgres

Что уже настроено для Render:
- Docker-based deploy из этого репозитория
- healthcheck `GET /actuator/health`
- free Postgres как источник `DATABASE_URL`
- поддержка `DATABASE_URL` в приложении
- отсутствие inline credentials в `render.yaml`

Как развернуть:
1. Зайди в Render и выбери `New +` -> `Blueprint`.
2. Подключи GitHub-репозиторий.
3. Подтверди создание сервисов из `render.yaml`.
4. После создания backend будет доступен по адресу вида `https://<service-name>.onrender.com`.
5. Создай deploy hook у web service и добавь его в GitHub secret `RENDER_DEPLOY_HOOK_URL`.
6. Добавь `https://<service-name>.onrender.com/actuator/health` в GitHub variable `RENDER_HEALTHCHECK_URL`.

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
