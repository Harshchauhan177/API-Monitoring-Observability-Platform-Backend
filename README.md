# API Monitoring & Observability Platform

A comprehensive platform for tracking API requests across multiple microservices, storing performance metrics, analyzing issues, and displaying them on a dashboard.

## 🏗️ Architecture

The platform consists of three main components:

### 1. API Tracking Client
A reusable Spring Boot library that can be integrated into any microservice to automatically track API requests.

**Location:** `api-tracking-client/`

**Key Components:**
- `TrackingInterceptor`: Spring MVC interceptor that captures request/response data
- `RateLimiter`: Per-service rate limiting with configurable limits
- `LogSender`: Sends tracking data to the collector service via REST

### 2. Central Collector Service
A Spring Boot service that receives logs from multiple microservices, stores them, and performs analysis.

**Location:** `collector-service/`

**Key Features:**
- Dual MongoDB connections (logs and metadata databases)
- Alert generation for slow APIs, errors, and rate limits
- Issue tracking and resolution with audit trails
- RESTful APIs for dashboard consumption

### 3. Next.js Dashboard
A modern web dashboard for visualizing API metrics, logs, alerts, and issues.

**Location:** `dashboard/`

**Key Features:**
- JWT-based authentication
- Real-time dashboard widgets
- Filterable logs explorer
- Issue resolution UI
- Error rate visualization

---

## 📊 Database Schemas

### Logs Database (MongoDB Port 27017)

#### Collection: `log_entries`
```json
{
  "_id": "ObjectId",
  "serviceName": "string",
  "endpoint": "string",
  "method": "string",
  "requestSize": "long",
  "responseSize": "long",
  "statusCode": "int",
  "latencyMs": "long",
  "timestamp": "long"
}
```

#### Collection: `rate_limit_hits`
```json
{
  "_id": "ObjectId",
  "serviceName": "string",
  "ipAddress": "string",
  "timestamp": "long"
}
```

### Metadata Database (MongoDB Port 27018)

#### Collection: `users`
```json
{
  "_id": "ObjectId",
  "username": "string",
  "password": "string",
  "role": "string"
}
```

#### Collection: `alerts`
```json
{
  "_id": "ObjectId",
  "serviceName": "string",
  "endpoint": "string",
  "message": "string",
  "alertType": "string",
  "timestamp": "long"
}
```

#### Collection: `api_issues`
```json
{
  "_id": "ObjectId",
  "version": "long",
  "serviceName": "string",
  "endpoint": "string",
  "errorMessage": "string",
  "issueType": "string",
  "resolved": "boolean",
  "timestamp": "long"
}
```

#### Collection: `rate_limit_configs`
```json
{
  "_id": "ObjectId",
  "serviceName": "string",
  "limitPerSecond": "int",
  "enabled": "boolean",
  "createdAt": "long",
  "updatedAt": "long"
}
```

#### Collection: `issue_audit_trail`
```json
{
  "_id": "ObjectId",
  "issueId": "string",
  "action": "string",
  "performedBy": "string",
  "previousState": "string",
  "newState": "string",
  "timestamp": "long"
}
```

#### Collection: `alert_audit_trail`
```json
{
  "_id": "ObjectId",
  "alertId": "string",
  "action": "string",
  "performedBy": "string",
  "previousState": "string",
  "newState": "string",
  "timestamp": "long"
}
```

---

## 🔧 How Dual MongoDB Setup Works

The collector service maintains **two separate MongoDB connections** to isolate concerns:

### Configuration (`MongoConfig.kt`)

1. **Logs Database (Primary)**
   - Port: `27017`
   - Database: `logs-db`
   - Stores: Raw API logs, rate limit hits
   - Bean names: `logsMongoClient`, `logsDatabaseFactory`, `logsMongoTemplate`, `logsTransactionManager`

2. **Metadata Database (Secondary)**
   - Port: `27018`
   - Database: `metadata-db`
   - Stores: User accounts, alerts, issues, configs, audit trails
   - Bean names: `metadataMongoClient`, `metadataDatabaseFactory`, `metadataMongoTemplate`, `metadataTransactionManager`

### Implementation Details

- **MongoTemplate Beans**: Each database has its own `MongoTemplate` bean qualified by name
- **Transaction Managers**: Separate `MongoTransactionManager` beans for each database
- **Repository Pattern**: Repositories use `@Qualifier` to inject the correct `MongoTemplate`
- **Primary Annotation**: Logs database is marked as `@Primary` to handle default Spring Boot auto-configuration

### Benefits

- **Separation of Concerns**: High-volume logs don't impact metadata queries
- **Independent Scaling**: Can scale databases independently
- **Data Isolation**: Logs and metadata can have different retention policies
- **Performance**: Optimized indexes for each database type

---

## ⚡ How Rate Limiter Works

### Architecture

The rate limiter is implemented in the **API Tracking Client** and operates at the interceptor level.

### Implementation (`RateLimiter.kt`)

```kotlin
class RateLimiter(private val limitPerSecond: Int) {
    private var lastTimestamp = System.currentTimeMillis()
    private var requestCount = 0

    fun hit(): Boolean {
        val now = System.currentTimeMillis()
        
        // Reset counter every second
        if (now - lastTimestamp >= 1000) {
            lastTimestamp = now
            requestCount = 0
        }
        
        requestCount++
        
        // Returns true if limit exceeded
        return requestCount > limitPerSecond
    }
}
```

### Configuration

Rate limits are configured via `application.yaml`:

```yaml
monitoring:
  collector:
    url: http://localhost:8080
  rateLimit:
    service: orders-service  # Service name
    limit: 100               # Requests per second
```

### Behavior

1. **Per-Request Check**: Every API request is checked against the rate limit
2. **Sliding Window**: Uses a 1-second sliding window
3. **Non-Blocking**: If limit is exceeded:
   - Request continues normally (not rejected)
   - Rate limit hit event is sent to collector
   - Alert is generated in the dashboard
4. **Configurable**: Each service can have its own limit via YAML or API

### Rate Limit Overrides

The collector service provides a REST API to override default rate limits:

- `GET /api/rate-limit-config` - List all configs
- `POST /api/rate-limit-config` - Create/update config
- `GET /api/rate-limit-config/service/{serviceName}` - Get config for service

---

## 🔒 Concurrency Safety

### Optimistic Locking

Issue resolution uses **optimistic locking** to prevent race conditions:

1. **Version Field**: `ApiIssue` model includes `@Version` field
2. **Atomic Updates**: `resolveIssueAtomically()` uses MongoDB atomic operations
3. **Version Check**: Update only succeeds if version matches
4. **Conflict Handling**: Returns conflict status if version mismatch detected

### Implementation

```kotlin
fun resolveIssueAtomically(id: String, expectedVersion: Long?): Boolean {
    val query = Query.query(
        Criteria.where("_id").`is`(id)
            .and("resolved").`is`(false)
            .apply {
                expectedVersion?.let {
                    and("version").`is`(it)
                }
            }
    )
    
    val update = Update()
        .set("resolved", true)
        .inc("version", 1)
    
    return mongoTemplate.updateFirst(query, update, ...).modifiedCount > 0
}
```

---

## 🚨 Alerting Rules

The collector automatically generates alerts when:

1. **Slow API**: Latency > 500ms
2. **Server Error**: Status code >= 500 (5xx)
3. **Rate Limit Exceeded**: Rate limiter detects violation

Alerts are:
- Stored in metadata database
- Visible on dashboard
- Tracked in audit trail

---

## 📋 Key Design Decisions

### 1. Dual MongoDB Setup
**Decision**: Separate databases for logs and metadata  
**Rationale**: 
- Logs are high-volume, write-heavy
- Metadata is lower-volume, read-heavy
- Independent scaling and optimization

### 2. Optimistic Locking for Issues
**Decision**: Use `@Version` field with atomic updates  
**Rationale**:
- Simpler than distributed locks
- Good performance for low contention
- Built-in MongoDB support

### 3. Non-Blocking Rate Limiting
**Decision**: Rate limit violations don't block requests  
**Rationale**:
- Better user experience
- Monitoring focus (not enforcement)
- Can be changed to blocking if needed

### 4. REST over gRPC
**Decision**: Use REST for log transmission  
**Rationale**:
- Simpler integration
- Better debugging
- Sufficient for current scale

### 5. Audit Trails
**Decision**: Store complete state snapshots  
**Rationale**:
- Full history for compliance
- Easy to reconstruct past states
- JSON format for flexibility

---

## 🚀 Setup Instructions

### Prerequisites

- Java 17+
- Node.js 18+
- MongoDB (two instances on ports 27017 and 27018)

### 1. Start MongoDB Instances

```bash
# Start logs database (port 27017)
mongod --dbpath /path/to/logs-db --port 27017

# Start metadata database (port 27018)
mongod --dbpath /path/to/metadata-db --port 27018
```

### 2. Start Collector Service

```bash
cd collector-service
./gradlew bootRun
```

Service runs on `http://localhost:8080`

### 3. Start Dashboard

```bash
cd dashboard
npm install
npm run dev
```

Dashboard runs on `http://localhost:3000`

### 4. Integrate Tracking Client

Add to your microservice's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":api-tracking-client"))
}
```

Add `application.yaml`:

```yaml
monitoring:
  collector:
    url: http://localhost:8080
  rateLimit:
    service: your-service-name
    limit: 100
```

---

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Logs
- `POST /api/logs` - Save log entry
- `GET /api/logs/all` - Get all logs
- `GET /api/logs/filtered` - Get filtered logs (query params: serviceName, endpoint, startDate, endDate, statusCode, slowOnly, brokenOnly, rateLimitOnly)
- `POST /api/logs/rate-limit` - Save rate limit hit

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics

### Issues
- `GET /api/issues` - Get all issues
- `GET /api/issues/unresolved` - Get unresolved issues
- `GET /api/issues/{id}` - Get issue by ID
- `GET /api/issues/{id}/audit` - Get issue audit trail
- `PUT /api/issues/{id}/resolve` - Resolve issue (with optimistic locking)

### Alerts
- `GET /api/alerts` - Get all alerts
- `GET /api/alerts/{id}` - Get alert by ID
- `GET /api/alerts/{id}/audit` - Get alert audit trail
- `POST /api/alerts` - Create alert
- `DELETE /api/alerts/{id}` - Delete alert

### Rate Limit Config
- `GET /api/rate-limit-config` - Get all configs
- `GET /api/rate-limit-config/service/{serviceName}` - Get config for service
- `POST /api/rate-limit-config` - Create/update config
- `PUT /api/rate-limit-config/{id}` - Update config
- `DELETE /api/rate-limit-config/service/{serviceName}` - Delete config

---

## 🧪 Testing Concurrent Writes

The system is designed to handle 50+ concurrent log writes. To test:

```bash
# Use Apache Bench or similar
ab -n 1000 -c 50 -p log.json -T application/json http://localhost:8080/api/logs
```

The collector service uses:
- Async processing (can be added)
- Connection pooling
- MongoDB write concerns optimized for throughput

---

## 📝 Project Structure

```
API-Monitoring-Observability-Platform-Backend/
├── api-tracking-client/          # Reusable tracking library
│   ├── src/main/kotlin/
│   │   └── tracking/
│   │       ├── TrackingInterceptor.kt
│   │       ├── RateLimiter.kt
│   │       ├── LogSender.kt
│   │       └── TrackingConfig.kt
│   └── src/main/resources/
│       └── application.yaml
│
├── collector-service/           # Central collector
│   ├── src/main/kotlin/
│   │   └── collector_service/
│   │       ├── config/
│   │       │   └── MongoConfig.kt      # Dual DB setup
│   │       ├── controllers/
│   │       ├── models/
│   │       │   ├── logs/               # Logs DB models
│   │       │   └── metadata/           # Metadata DB models
│   │       └── repositories/
│   │           ├── logs/               # Logs DB repos
│   │           └── metadata/           # Metadata DB repos
│   └── src/main/resources/
│       └── application.yaml
│
└── dashboard/                    # Next.js frontend
    ├── app/
    │   ├── (auth)/               # Auth pages (no sidebar)
    │   │   ├── login/
    │   │   └── signup/
    │   ├── dashboard/            # Dashboard widgets
    │   ├── logs/                 # Logs explorer
    │   ├── alerts/                # Alerts viewer
    │   └── issues/               # Issue management
    └── lib/
        └── api.ts                # API client
```

---

## 🔐 Security

- **JWT Authentication**: All protected endpoints require valid JWT token
- **CORS Configuration**: Configured for frontend-backend communication
- **Password Storage**: Currently plain text (should use BCrypt in production)
- **Rate Limiting**: Per-service rate limits to prevent abuse

---

## 🎯 Future Enhancements

- [ ] Password hashing (BCrypt)
- [ ] Real-time WebSocket updates
- [ ] Advanced filtering and search
- [ ] Export logs functionality
- [ ] Custom alert rules
- [ ] Service health monitoring
- [ ] Distributed tracing integration

---

## 📄 License

This project is part of an assignment submission.

---

## 👥 Author

Built as part of API Monitoring & Observability Platform assignment.

