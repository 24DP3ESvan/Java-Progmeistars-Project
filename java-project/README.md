# PC Builder Java Project

This is a Spring Boot rewrite of the Python PC Builder project.

## Features
- Spring Boot REST API
- JPA entity storage using H2
- DTO-based request/response model
- RAWG API integration for game search and details
- UserBenchmark search integration for part benchmarking
- JSON part data loaded from the existing `../parts` directory
- Unit tests for part search and build persistence

## Install JDK and Maven on Windows
1. Install JDK 17 or newer:
   - Recommended: Azul Zulu or Eclipse Temurin.
   - Download from: https://adoptium.net/
   - Install, then set `JAVA_HOME` to the JDK install folder.
2. Add Java to the PATH:
   - In PowerShell:
     ```powershell
     setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.0.8.1-hotspot"
     setx PATH "%JAVA_HOME%\bin;%PATH%"
     ```
3. Install Maven:
   - Use winget:
     ```powershell
     winget install Apache.Maven
     ```
   - Or download from: https://maven.apache.org/download.cgi
4. Verify install:
   ```powershell
   java -version
   mvn -version
   ```

## VS Code setup
1. Install the Java Extension Pack.
2. Install Lombok support extension.
3. Open the `java-project` folder in VS Code.
4. If prompted, allow the Java language server to import the Maven project.

## Run
From the `java-project` folder, use the web API mode:
```powershell
mvn spring-boot:run
```

To start the project in interactive console mode like the Python version, run:
```powershell
mvn spring-boot:run -Dspring-boot.run.arguments=--console
```

Or, after packaging the jar:
```powershell
mvn package
java -jar target/pc-builder-0.0.1-SNAPSHOT.jar --console
```

## RAWG and UserBenchmark usage
- Set your RAWG API key in `src/main/resources/application.properties`:
  ```properties
  rawg.api.key=YOUR_RAWG_API_KEY
  ```
- Search games:
  `GET /api/games/search?query=cyberpunk&pageSize=10`
- Get game details:
  `GET /api/games/{id}`
- Search UserBenchmark-like part data:
  `GET /api/benchmarks/search?query=RTX`

## API Endpoints
- `GET /api/parts` - list available categories
- `GET /api/parts/{category}` - search components by category
- `GET /api/parts/{category}/detail?name={name}` - component details
- `GET /api/games/search?query={query}&pageSize={n}` - search games via RAWG
- `GET /api/games/{id}` - get RAWG game details
- `GET /api/benchmarks/search?query={query}` - search part benchmark entries
- `GET /api/builds` - list saved builds
- `POST /api/builds` - save a build
- `GET /api/builds/{id}` - load a build
- `PUT /api/builds/{id}` - update a build
