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

For more information, see the [Yarn bootstrap README](YARN.md).
