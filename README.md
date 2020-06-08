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

- Use environment variables for values like the NASA API key
- Cache eviction policy
- Cache persistence within container
- More specific error handling and usage of HTTP codes
- More thorough testing
- Static analysis

### Frontend:

- Show loading state in cards
- Handle no images for a given date
- Frontend unit tests
