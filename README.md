This is a Jenkins plugin which notifies to Typetalk ( https://typetalk.in/ )

# How to build
- ref : https://wiki.jenkins-ci.org/display/JENKINS/Gradle+JPI+Plugin

## Import to IDE

```
gradle cleanEclipse eclipse
gradle cleanIdea idea
```

## Launch for development

```
gradle clean server
```

## Package a plugin file

```
gradle clean jpi
```

If you don't install gradle, you can use gradlew / gradlew.bat instead of gradle.
