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

