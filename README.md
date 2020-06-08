## MarsRover App

An end-to-end web app built with Spring Boot and React.
Key Features:

- On start up:
  - Read dates from specified text file `dates.txt`
  - Fetch first image available for the Curiosity rover for each date
  - Save image in local cache
  - Image fetching and saving is done concurrently for each date. Probably overkill for now, but would greatly speed up the process with a large dates file. The number of threads is configurable.
- Caching:
  - Caching works by forming a consistent UUID from a rover and date strings
  - If an image exists for a given rover and a date, that image in the cache will be returned
- React App:
  - View images from dates in `dates.txt`
  - Enter a rover name and a date and view that image
  - Uses hooks for state management
  - Responsive

## REST API

- `GET` `/api/rovers/<rover>/images?date=<date>`
- Returns the first image available for rover <rover> and date <date>
- <date> must be url encoded
- Supported <date> formats:
  - MM/dd/yy
  - MMM dd, yyyy
  - MMM-dd-yyyy
  - yyyy-MM-dd

## Dependencies

- OpenJDK 14
- Gradle 6.4.1
- Docker 19.03.8
- Node 12.17.0

## Run App as a Docker Container

### 1. Build ui:

```
cd ui
npm install
npm run build
```

### 2. Build jar file and Docker image:

```
./gradlew docker
```

### 3. Run Docker image:

```
docker run -p 8080:8080 -t marsrover -d
```

## Run Locally

### 1. Watch ui directory for changes:

```
cd ui
npm run dev
```

### 2. Start server:

```
Run from IDE (Recommended) OR
./gradlew bootRun
```

## Run Unit Tests

```
./gradlew test
```

## Areas for Improvement

### Backend:

- Follow standard for Dockerizing a Spring / React webapp
- Use environment variables for values like the NASA API key
- Cache eviction policy
- Cache persistence within container
- More specific error handling and usage of HTTP codes
- More thorough testing
- Static analysis

### Frontend:

- Show loading state in cards
- Handle errors
- Handle no images for a given date
- Frontend unit tests
