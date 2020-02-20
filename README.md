<h1 align="center">
    Version Master
</h1>

<p align="center">
    <a href="https://github.com/SUPERCILEX/version-master/actions">
        <img src="https://github.com/SUPERCILEX/version-master/workflows/CI/CD/badge.svg" />
    </a>
    <!-- TODO -->
    <a href="https://plugins.gradle.org/plugin/com.supercilex.gradle.versions">
        <img src="https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/supercilex/gradle/versions/com.supercilex.gradle.versions.gradle.plugin/maven-metadata.xml.svg?label=Gradle%20Plugins%20Portal" />
    </a>
</p>

Version Master provides an effortless and performant way to automate versioning your Android app.

## Table of contents

1. [Installation](#installation)
   1. [Snapshot builds](#snapshot-builds)
1. [Configuring Version Master](#configuring-version-master)
   1. [Disabling version code configuration](#disabling-version-code-configuration)
   1. [Disabling version name configuration](#disabling-version-name-configuration)
   1. [Enabling debug build configuration](#enabling-debug-build-configuration)
   1. [For existing apps](#for-existing-apps)

## Installation

Apply the plugin to each individual `com.android.application` module where you want to use Version
Master through the `plugins {}` DSL:

<details open><summary>Kotlin</summary>

```kt
plugins {
    id("com.android.application")
    id("com.supercilex.gradle.versions") version "0.1.0"
}
```

</details>

<details><summary>Groovy</summary>

```groovy
plugins {
    id 'com.android.application'
    id 'com.supercilex.gradle.versions' version '0.1.0'
}
```

</details>

### Snapshot builds

If you're prepared to cut yourself on the bleeding edge of Version Master development, snapshot
builds are available from
[Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/com/supercilex/gradle/version-master/):

<details open><summary>Kotlin</summary>

```kt
buildscript {
    repositories {
        // ...
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        // ...
        classpath("com.supercilex.gradle:version-master:0.2.0-SNAPSHOT")
    }
}
```

</details>

<details><summary>Groovy</summary>

```groovy
buildscript {
    repositories {
        // ...
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
    }

    dependencies {
        // ...
        classpath 'com.supercilex.gradle:version-master:0.2.0-SNAPSHOT'
    }
}
```

</details>

## Configuring Version Master

Version master offers several options to fit your use case.

### Disabling version code configuration

To handle version codes yourself, disable version code configuration:

```kt
versionMaster {
    configureVersionCode.set(false)
}
```

### Disabling version name configuration

To handle version names yourself, disable version name configuration:

```kt
versionMaster {
    configureVersionName.set(false)
}
```

### Enabling debug build configuration

To make debug builds as fast as possible, version codes and names are never changed in debug builds
by default. To enable versioning, enable debug build configuration:

```kt
versionMaster {
    configureDebugBuilds.set(true)
}
```

### For existing apps

If your app already has an established version code, you can tell Version Master about it:

```kt
versionMaster {
    versionCodeOffset.set(123)
}
```
