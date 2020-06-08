## Run App as Docker Container:

### 1. Build frontend:

```
cd ui
npm install
npm run build
```

### 2. Build jar and docker image:

```
./gradlew docker
```

### 3. Run docker image:

```
docker run -p 8080:8080 -t marsrover -d
```

## To run locally:

### 1. Watch for ui changes:

```
cd ui
npm run dev
```

### 2. Run backend:

```
Run from IDE (Recommended) OR
./gradlew bootRun
```

## Unit Tests

```
./gradlew test
```
