# Bank Service

A Spring Boot backend for a simple banking system, covering user accounts and card operations with role-based access and MongoDB storage.

## Features

### User Account Management
* **Registration**: Create new user accounts.
* **Authentication**: Secure login using Spring Security (Basic Auth).
* **Profile Management**: Update user information (first name, last name).
* **Security**: Role-based access control and password hashing (BCrypt).
* **Administrative Tools**: Manage user roles and delete accounts.

### Card Operations
* **Card Issuance**: Admins can create new cards for users with automatic generation of card numbers and expiration date validation.
* **Card Activation**: Admins can activate cards.
* **Balance Management**: Users can view their card balances.
* **Money Transfer**: Securely transfer funds between cards owned by the same user.
* **Card Blocking**: Users can request to block their cards, and admins can manage card status.
* **Search and Retrieval**: Paginated card lists for users and various search options for admins.

## Technologies Used

* **Java 21**
* **Spring Boot 3.2.5**
* **Spring Security**
* **Spring Data MongoDB**
* **MongoDB**
* **ModelMapper** (for DTO mapping)
* **Lombok**
* **Maven**
* **JUnit 5 & Mockito** (for testing)
* **Docker & Docker Compose**

## Getting Started

### Prerequisites

* Java 21 JDK
* Maven 3.x
* Docker and Docker Compose (optional, for running MongoDB)

### Setup and Running

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd bank-service
   ```

2. **Database Setup**:
   The application requires MongoDB. You can run it using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   If you want to override defaults, create a `.env` file next to `docker-compose.yml`:
   ```dotenv
   MONGO_INITDB_ROOT_USERNAME=admin
   MONGO_INITDB_ROOT_PASSWORD=admin
   MONGO_INITDB_DATABASE=bank_service
   ```

3. **Configure the application**:
   Update `src/main/resources/application.properties` if necessary:
   ```properties
   spring.data.mongodb.uri=mongodb://<username>:<password>@localhost:27017/<db_name>?authSource=admin
   ```

4. **Build the project**:
   ```bash
   ./mvnw clean package
   ```

5. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   The service will start on `http://localhost:8080`.

### Optional: Bootstrap Admin (First-Run Admin User)
Admin bootstrap is **disabled by default** for safety. To enable it explicitly:
```properties
bank.bootstrap-admin.enabled=true
bank.bootstrap-admin.login=admin
bank.bootstrap-admin.password=your-strong-password
```
If `bank.bootstrap-admin.enabled=true` and the password is empty, the application will fail to start.

### Running Tests
```bash
./mvnw test
```

## API Documentation

### Account Endpoints (`/account`)
* `POST /account/register`: Register a new user.
* `POST /account/login`: Authenticate and get user details (Basic Auth).
* `GET /account/user/{login}`: Get user details (Self-access only).
* `PUT /account/user/{login}`: Update user profile (Admin only).
* `DELETE /account/user/{login}`: Delete a user (Admin only).
* `PUT /account/user/{login}/role/{role}`: Add a role to a user (Admin only).
* `DELETE /account/user/{login}/role/{role}`: Remove a role from a user (Admin only).
* `PUT /account/password`: Change current user's password (Header: `X-Password`).

### User Card Endpoints (`/api/user`)
* `GET /api/user/cards`: Get all cards of the authenticated user (paginated).
* `GET /api/user/cards/{id}`: Get specific card details.
* `GET /api/user/cards/{id}/balance`: Get card balance.
* `POST /api/user/cards/transfer`: Transfer money between user's own cards.
* `POST /api/user/cards/{id}/request-block`: Request to block a card.

### Admin Card Endpoints (`/card`)
* `POST /card/createNewCard`: Issue a new card.
* `GET /card/getAllCards`: List all cards in the system (paginated).
* `GET /card/findCardsByName/{name}`: Find cards by owner name (paginated).
* `POST /card/activate`: Activate a card.
* `DELETE /card/{ownerName}/deleteCard/{cardNumberLast4}`: Remove a card from the system.

## Security Roles

* `USER`: Standard user role.
* `ADMINISTRATOR`: Full access to user management and card administrative tasks.
* `MODERATOR`: Role exists but is not uniquely restricted in current access rules.

### Authorization Notes
Admin-only:
- `PUT /account/user/{login}`
- `DELETE /account/user/{login}`
- `PUT /account/user/{login}/role/{role}`
- `DELETE /account/user/{login}/role/{role}`
- `POST /card/createNewCard`
- `GET /card/getAllCards`
- `GET /card/findCardsByName/{name}`
- `POST /card/activate`
- `DELETE /card/{ownerName}/deleteCard/{cardNumberLast4}`

User-only (self):
- `GET /account/user/{login}` only for the authenticated user

## Testing

Run the test suite using Maven:
```bash
./mvnw test
```

### Common Issues
**Compilation errors `TypeTag :: UNKNOWN`**  
This project targets **Java 21**. Make sure Maven runs under JDK 21:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -version
```
