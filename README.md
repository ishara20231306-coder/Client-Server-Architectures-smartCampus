# Client-Server-Architectures-smartCampus

A RESTful API built with JAX-RS (Jersey) and Grizzly HTTP Server for managing smart campus rooms and IoT sensors.



## API Overview

This API provides a versioned interface at `/api/v1` for managing:
 **Rooms** - Physical spaces on campus
 **Sensors** - IoT devices assigned to rooms
 **Readings** - Historical sensor data logs

All data is stored in-memory using `HashMap` and `ArrayList` structures. No database is used.

### Base URL that I have used
```
http://localhost:8081/api/v1
```

### Endpoint Summary

| Method   | Path |                        | Description |
| GET      | /api/v1 |                     | Discovery / API metadata |
| GET      | /api/v1/rooms |               | List all rooms |
| POST     | /api/v1/rooms |               | Create a room |
| GET      | /api/v1/rooms/{id} |          | Get a room by ID |
| DELETE   | /api/v1/rooms/{id} |          | Delete a room (fails if sensors exist) |
| GET      | /api/v1/sensors |             | List all sensors (optional `?type=` filter) |
| POST     | /api/v1/sensors?roomId={id}|  | Register a sensor to a room |
| GET      | /api/v1/sensors/{id}/readings|| Get all readings for a sensor |
| POST     | /api/v1/sensors/{id}/readings|| Add a new reading for a sensor |

---

## Technology Stack

 Java 17+
 JAX-RS (Jakarta EE)
 Jersey (JAX-RS implementation)
 Grizzly HTTP Server (embedded)
 Maven (build tool)
 Jackson (JSON serialisation)

---

## How to Build and Run

### Prerequisites
 Java 17 or higher installed
 Maven 3.6+ installed

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/ishara20231306-coder/Client-Server-Architectures-smartCampus.git
cd smart-campus-api
```

**2. Build the project**
```bash
mvn clean package
```

**3. Run the server**
```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

**4. Confirm the server is running**

You should see:
```
Server is running at http://localhost:8081
```

The API is now accessible at `http://localhost:8081/api/v1`

---

## Sample curl Commands

### 1. Create a Room
```bash
curl -X POST http://localhost:8081/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "room1", "name": "Lab A", "capacity": 30}'
```

### 2. Get All Rooms
```bash
curl -X GET http://localhost:8081/api/v1/rooms
```

### 3. Register a Sensor to a Room
```bash
curl -X POST "http://localhost:8081/api/v1/sensors?roomId=room1" \
  -H "Content-Type: application/json" \
  -d '{"id": "sensor1", "type": "CO2", "status": "ACTIVE"}'
```

### 4. Filter Sensors by Type
```bash
curl -X GET "http://localhost:8081/api/v1/sensors?type=CO2"
```

### 5. Add a Reading to a Sensor
```bash
curl -X POST http://localhost:8081/api/v1/sensors/sensor1/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 412.5}'
```

### 6. Get All Readings for a Sensor
```bash
curl -X GET http://localhost:8081/api/v1/sensors/sensor1/readings
```

### 7. Delete a Room (will fail if sensors exist)
```bash
curl -X DELETE http://localhost:8081/api/v1/rooms/room1
```



## Report - Answers to Coursework Questions

### Part 1 - Service Architecture & Setup

**Q: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance created per request or treated as a singleton?**

Normally, JAX-RS creates a **new instance of a resource class for every incoming HTTP request** (per-request scope). This means each request gets its own isolated object, so instance variables are not shared between concurrent requests. However, this design has a direct implication for in-memory data storage: if sensor or room data were stored as regular instance fields, they would be lost after each request. To prevent this, all data structures in this project are declared as `static` fields on the resource class (e.g., `private static Map<String, Room> rooms = new HashMap<>()`). Because static fields belong to the class itself rather than any specific instance, they persist across all requests for the lifetime of the JVM. In a production environment with concurrent users, these static structures would need to be protected with synchronised collections (e.g., `ConcurrentHashMap`) or explicit `synchronized` blocks to prevent race conditions and data corruption.



**Q: Why is HATEOAS considered a hallmark of advanced RESTful design? How does it benefit client developers?**

HATEOAS (Hypermedia as the Engine of Application State) means that API responses include navigational links to related or next-step resources, rather than just raw data. For example, a response for a room might include a link to its sensors endpoint. This benefits client developers significantly because it reduces tight coupling between the client and the server: the client does not need to hard-code URL structures or memorise an entire API map. Instead, it can follow links dynamically from one response to the next, similar to how a browser navigates a website. This makes clients more resilient to server-side URL changes and allows the API to evolve without breaking existing integrations. It is considered a hallmark of advanced REST design because it moves the API closer to the true stateless, self-descriptive nature that Roy Fielding originally defined.



### Part 2 - Room Management

**Q: When returning a list of rooms, what are the implications of returning only IDs versus full room objects?**

Returning only IDs is bandwidth-efficient and fast for large collections, but it forces the client to make N additional HTTP requests to retrieve each room's details — a well-known "N+1 request problem." This increases latency, especially on mobile or low-bandwidth networks. Returning full room objects in a single response trades higher initial payload size for fewer round-trips, which is generally preferable when clients need most of the data anyway. In this API, full room objects are returned by default since the dataset is expected to be small. For very large collections, a paginated approach returning full objects per page would be the best balance.



**Q: Is the DELETE operation idempotent in your implementation?**

Idempotency means that calling the same operation multiple times produces the same result as calling it once. In this implementation, DELETE is **partially idempotent**. The first successful DELETE removes the room and returns "Deleted". Any subsequent DELETE for the same room ID finds no entry in the map and returns "Room not found" — a different response message, but no harmful side effect occurs (the room remains absent both times). The server state is consistent after any number of DELETE calls on the same non-existent room. According to the HTTP specification, idempotency refers to server-state effects, not response bodies, so this implementation can be considered effectively idempotent. Importantly, if sensors are still present, every DELETE attempt will return a 409 Conflict, which is also consistent and idempotent behaviour.



### Part 3 - Sensor Operations & Linking

**Q: Explain the consequences if a client sends data in a format other than `application/json` to a `@Consumes(APPLICATION_JSON)` endpoint.**

When a resource method is annotated with `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS inspects the `Content-Type` header of incoming requests. If a client sends a request with a different content type — for example `text/plain` or `application/xml` — the JAX-RS runtime will not match that request to the annotated method. It will return an **HTTP 415 Unsupported Media Type** response automatically, before the method body is ever executed. This is a contract enforcement mechanism built into the framework. It protects the API from receiving malformed or unexpected input formats and removes the need for manual content-type validation inside business logic.



**Q: Contrast `@QueryParam` for filtering versus embedding the type in the URL path (e.g., `/sensors/type/CO2`).**

Using `@QueryParam` for filtering (e.g., `GET /sensors?type=CO2`) is semantically more accurate because query parameters are specifically designed in the HTTP standard to represent optional filtering, sorting, or searching criteria on a collection. The path `/sensors` already identifies the resource (all sensors), and the query parameter narrows the result set. Embedding the type in the path (e.g., `/sensors/type/CO2`) implies that `CO2` is a distinct sub-resource or entity identifier, which is conceptually incorrect — the sensors are the resources, not the type filter itself. The query parameter approach also makes the filter genuinely optional: `GET /sensors` and `GET /sensors?type=CO2` both resolve cleanly. A path-based approach would require maintaining a separate route definition. Query parameters are also easier to extend with multiple filters (e.g., `?type=CO2&status=ACTIVE`) without restructuring the URL hierarchy.



### Part 4 - Sub-Resources

**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern.**

The Sub-Resource Locator pattern allows a parent resource class to delegate handling of nested paths to a separate, dedicated class. In this project, `SensorResource` returns a `SensorReadingResource` instance for the path `{sensorId}/readings`, keeping reading-specific logic cleanly isolated. The primary benefit is **separation of concerns**: each resource class has a single, focused responsibility. In large APIs, consolidating all nested paths into one controller quickly becomes unmanageable — a single class could grow to hundreds of methods covering rooms, sensors, readings, alerts, etc. By distributing logic across dedicated classes, the codebase becomes easier to read, test, and maintain. It also enables independent versioning and modification of sub-resources without touching the parent class, supporting the open/closed principle of software design.



### Part 5 - Error Handling & Exception Mapping

**Q: Why is HTTP 422 (Unprocessable Entity) more semantically accurate than 404 when a `roomId` reference inside a JSON body doesn't exist?**

HTTP 404 Not Found means the requested URL/resource itself could not be located. In this scenario, the URL `/api/v1/sensors` is perfectly valid and found — the problem is not with the endpoint but with the **content of the request body**. The `roomId` value inside the JSON refers to a room that does not exist in the system. HTTP 422 Unprocessable Entity is defined precisely for this case: the request was syntactically valid (well-formed JSON, correct content type) but semantically invalid (a referenced dependency is missing). Using 422 communicates a much more precise error to the client — "your request was understood but cannot be acted upon due to a logical validation failure" — rather than the misleading 404 which would suggest the endpoint itself is missing.



**Q: From a cybersecurity standpoint, explain the risks of exposing internal Java stack traces to API consumers.**

Exposing raw stack traces to external clients is a serious security vulnerability. A stack trace reveals the internal package structure and class names of the application (e.g., `com.smartcampus.resource.SensorResource`), which tells an attacker the technology stack, framework versions, and code organisation. Method names and line numbers in the trace can pinpoint exactly where a failure occurred, guiding targeted exploitation. Exception messages sometimes include sensitive data such as database connection strings, file system paths, or configuration values. Dependency names visible in traces can be cross-referenced against public vulnerability databases (e.g., CVE lists) to identify known exploits. The Global Exception Mapper in this project addresses this by catching all `Throwable` instances and returning a generic 500 response, ensuring internal implementation details are never leaked to the client.



**Q: Why is it better to use JAX-RS filters for cross-cutting concerns like logging rather than inserting `Logger.info()` calls in every resource method?**

Using JAX-RS `ContainerRequestFilter` and `ContainerResponseFilter` for logging implements the **cross-cutting concern** in a single, centralised place. If logging were added manually to every resource method, the same boilerplate code would be repeated dozens of times across the codebase — violating the DRY (Don't Repeat Yourself) principle. Filters are applied automatically to every request and response by the JAX-RS runtime without modifying any resource class. This means adding a new resource automatically gets logging without any extra effort. Filters also allow logging to be enabled or disabled globally in one place. Furthermore, separating infrastructure concerns (logging, authentication, compression) from business logic (room creation, sensor registration) produces cleaner, more testable resource classes that focus solely on their intended responsibility.
