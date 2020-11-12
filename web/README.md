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
> cd web; yarn start
```

#### Docker

```sh
> cd web
> docker build -t oasis-timeline-web .
> docker run -p 8080:80 oasis-timeline-web
```

For more information, see the [Yarn bootstrap README](YARN.md).
