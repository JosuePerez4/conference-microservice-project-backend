# Conference microservice backend

Spring Boot service that owns conference metadata for the conference platform. It exposes
REST endpoints under `/conferences`, stores conference records in PostgreSQL, and keeps a
small enrollment summary table in sync from RabbitMQ enrollment events so conferences with
active enrollments cannot be deleted.

## Architecture

- **HTTP API:** `ConferenceController` handles create, update, read, list, and delete
  operations for conferences.
- **Domain layer:** `ConferenceService`, `ConferenceMapper`, and `ConferenceValidator`
  map request DTOs, normalize dates/lists, and enforce business rules.
- **Persistence:** Spring Data JPA stores `Conference` rows plus element collections for
  topics and speakers. `ConferenceEnrollmentSummary` stores total inscriptions per
  conference.
- **Security:** Spring Security OAuth2 Resource Server validates JWTs with an RSA public
  key and authorizes endpoints by role.
- **Messaging:** Spring AMQP consumes enrollment lifecycle events from RabbitMQ and updates
  the enrollment summary.
- **Author history:** author enrollment events are mirrored into
  `author_conference_participation` so authors can read their own participation history.

## Requirements

- Java 21
- Maven wrapper (`./mvnw`) or Maven 3.9+
- PostgreSQL
- RabbitMQ

## Configuration

The app loads environment variables directly and also imports a local `.env` file when one
exists. Do not commit `.env`; it is ignored by git.

| Variable | Required | Default | Purpose |
| --- | --- | --- | --- |
| `PORT` | No | `8081` | HTTP port. |
| `SPRING_DATASOURCE_URL` | Yes | none | JDBC URL for PostgreSQL. |
| `SPRING_DATASOURCE_USERNAME` | No | empty | Database user. |
| `SPRING_DATASOURCE_PASSWORD` | No | empty | Database password. |
| `RABBITMQ_HOST` | No | `localhost` | RabbitMQ host. |
| `RABBITMQ_PORT` | No | `5672` | RabbitMQ port. |
| `RABBITMQ_USERNAME` | No | `guest` | RabbitMQ user. |
| `RABBITMQ_PASSWORD` | No | `guest` | RabbitMQ password. |
| `RABBITMQ_VHOST` | No | `/` | RabbitMQ virtual host. |
| `RABBITMQ_SSL_ENABLED` | No | `false` | Enables RabbitMQ TLS. |
| `JWT_PUBLIC_KEY` | Yes | none | RSA public key used to validate JWT signatures. PEM text and escaped `\n` are accepted. |
| `EUREKA_SERVER_URL` | Yes | none | Eureka server URL used by service discovery. |

Example `.env` for local development:

```properties
PORT=8081
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/conferences
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
JWT_PUBLIC_KEY=-----BEGIN PUBLIC KEY-----\n...\n-----END PUBLIC KEY-----
EUREKA_SERVER_URL=http://localhost:8761/eureka
```

`spring.jpa.hibernate.ddl-auto=update` is enabled in `application.properties`; review that
setting before using this service in environments where schema changes must be controlled.
CORS is currently disabled in this service; configure browser CORS at the API Gateway. The
commented service-level CORS bean in `SecurityConfig` references `FRONTEND_URL` only if it is
reactivated.

## Running locally

```bash
./mvnw spring-boot:run
```

OpenAPI is available when the service is running:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Docker

Build and run the image:

```bash
docker build -t conference-service .
docker run --rm -p 8081:8081 --env-file .env conference-service
```

The image uses Java 21, exposes port `8081`, and accepts optional JVM flags through
`JAVA_OPTS`.

## Authentication and authorization

All protected endpoints require a bearer JWT. Roles are read from either:

- `roles`: a JSON array of strings, for example `["ADMIN", "CHAIR"]`
- `role`: a single string, for example `"AUTHOR"`

The service adds the `ROLE_` prefix internally when it is not already present.
Endpoints that use the current user read a UUID from the `userId` claim, falling back to the
JWT subject (`sub`) when `userId` is absent.

| Method | Path | Access |
| --- | --- | --- |
| `POST` | `/conferences/create` | `ADMIN`, `CHAIR` |
| `PUT` | `/conferences/edit/{id}` | `ADMIN`, `CHAIR` |
| `GET` | `/conferences/get/{id}` | `ADMIN`, `AUTHOR`, `CHAIR`, `ASISTANT` |
| `GET` | `/conferences/get-all` | Public |
| `GET` | `/conferences/my-participation-history` | `AUTHOR` |
| `DELETE` | `/conferences/delete/{id}` | `ADMIN`, `CHAIR` |
| `GET` | `/swagger-ui/**`, `/v3/api-docs/**` | Public |
| `GET` | `/actuator/**` | Public |

## Conference API

### Create a conference

```http
POST /conferences/create
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{
  "name": "Distributed Systems Conf",
  "description": "Research and practice sessions",
  "location": "Bogota",
  "virtual": false,
  "inscriptionPrice": 120.0,
  "startDate": "2026-09-10",
  "endDate": "2026-09-12",
  "submissionDeadline": "2026-09-10",
  "topics": ["distributed systems", "messaging"],
  "speakers": ["Ada Lovelace"],
  "state": "DRAFT"
}
```

Returns `200 OK` with the created conference payload, including its UUID.

### Update a conference

```http
PUT /conferences/edit/{id}
Authorization: Bearer <jwt>
Content-Type: application/json
```

Uses the same JSON fields as create. The update path applies scalar fields from the request
and only replaces `topics` or `speakers` when those arrays are present.

### Read conferences

```http
GET /conferences/get/{id}
Authorization: Bearer <jwt>
```

```http
GET /conferences/get-all
```

### Read my author participation history

```http
GET /conferences/my-participation-history
Authorization: Bearer <jwt with AUTHOR role and UUID userId or sub>
```

Returns the current author's conferences ordered by the stored participation timestamp,
newest first:

```json
[
  {
    "conference": {
      "id": "5b91f8c0-2c76-41c9-b46f-89952d82b4d6",
      "name": "Distributed Systems Conf",
      "description": "Research and practice sessions",
      "location": "Bogota",
      "virtual": false,
      "inscriptionPrice": 120.0,
      "startDate": "2026-09-10",
      "endDate": "2026-09-12",
      "submissionDeadline": "2026-09-10",
      "topics": ["distributed systems", "messaging"],
      "speakers": ["Ada Lovelace"],
      "state": "DRAFT"
    },
    "participatedAt": "2026-06-01T11:00:00"
  }
]
```

The history table is maintained from enrollment events with `tipo: "AUTOR"`. If the related
conference row no longer exists, that history entry is skipped in the response.

### Delete a conference

```http
DELETE /conferences/delete/{id}
Authorization: Bearer <jwt>
```

Deletion succeeds only when the conference has no recorded enrollments. A successful delete
returns `200 OK` with `Conference deleted successfully`.

## Conference validation rules

- `name` and `location` are required, including for virtual conferences.
- `startDate`, `endDate`, and `submissionDeadline` are required for create and must remain
  valid after update.
- `startDate` must be after the current date on create.
- `endDate` must be after `startDate`.
- `submissionDeadline` must be between `startDate` and `endDate`, inclusive.
- `inscriptionPrice` cannot be negative.
- `topics` and `speakers` must contain at least one non-blank value.
- Accepted input date formats are `YYYY-MM-DD` and `DD-MM-YYYY`; responses use
  `YYYY-MM-DD`.
- Valid `state` values are `DRAFT`, `PUBLISHED`, `IN_PROGRESS`, and `CLOSED`. Input is
  uppercased before matching.

Business validation errors return `400 Bad Request`; missing conference IDs return
`404 Not Found`. Error responses include `timestamp`, `status`, `error`, and `message`.

## RabbitMQ enrollment events

The service declares this topology:

| Resource | Name |
| --- | --- |
| Topic exchange | `enrollment.events` |
| Created queue | `conference.enrollment.created` |
| Cancelled queue | `conference.enrollment.cancelled` |
| Created routing key | `enrollment.created` |
| Cancelled routing key | `enrollment.cancelled` |

Expected event payload:

```json
{
  "conferenceId": "5b91f8c0-2c76-41c9-b46f-89952d82b4d6",
  "userId": "2baf7988-f703-4043-9711-8e5c89d144b2",
  "tipo": "ASISTENTE"
}
```

`enrollment.created` increments `totalInscriptions` for the conference, creating the summary
row when needed. `enrollment.cancelled` decrements the total without going below zero. The
listener also records author participation when `tipo` is `AUTOR`, using `(userId,
conferenceId)` as the history key and `participatedAt` as the time the event was consumed.
Cancelling an `AUTOR` enrollment removes that author history row. Non-author enrollments only
affect the enrollment summary.

## Troubleshooting

- **Startup fails with `JWT_PUBLIC_KEY`:** verify that the value is an RSA public key in PEM
  or base64 form. Escaped newlines (`\n`) are normalized by the service.
- **Service does not register in Eureka:** verify `EUREKA_SERVER_URL`; it is required and has
  no default value.
- **CORS requests fail:** configure CORS at the API Gateway. The microservice-level CORS bean
  is commented out.
- **Database connection fails:** confirm `SPRING_DATASOURCE_URL` is set; it has no default.
- **Protected endpoints return 403:** make sure the JWT contains `roles` or `role` with one
  of the roles required by the endpoint.
- **Author history returns 400:** the token must include `userId` or `sub` as a valid UUID.
- **Delete returns a validation error:** the conference has enrollment summary count greater
  than zero; consume or correct enrollment cancellation events before deleting.
