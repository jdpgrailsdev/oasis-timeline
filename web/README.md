# Oasis Timeline Web

[Typescript](https://www.typescriptlang.org/) based web application that hosts a visual timeline of events.

## Build and Deploy

### Build

```sh
> ./gradlew :oasis-timeline-web:clean :oasis-timeline-web:build
```

### Deploy

```sh
> ./gradlew :oasis-timeline-web:clean :oasis-timeline-web:build :oasis-timeline-web:gitPublishPush
```

### Local Execution

```sh
> cd web; npm run-script start
```

#### Docker

```sh
> cd web
> docker build -t oasis-timeline-web .
> docker run -p 8080:80 oasis-timeline-web
```

### Updating depencencies

```sh
> cd web
> npm outdated
> npm update
```

To update a specific dependency:

```sh
> cd web
> npm update "react"
```

To update a specific dependency to a specific/latest version:

```sh
> cd web
> npm install react@latest
```

### Testing

To execute the tests:
```sh
> cd web
> npm run test -- --watchAll=false
```

To view the coverage report:
```sh
> cd web
> npm run test -- --watchAll=false --coverage
```

