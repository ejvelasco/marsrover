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
