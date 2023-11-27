# Planty Multiplatform Client

This is the multiplatform client for the Planty project. It is written in Kotlin and uses the [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) feature to share code between the Android, Web and Desktop clients.

## Client Architecture

Planty's architecture is based on several clean architecture principles, which aims to deliver a modern, highly expandable, robust development experience. There are multiple advantages and disadvantages of combining having a single codebase for multiple platforms and having to deal with the restrains of this singlular codebase:

- Multiplatform nature of the project allows for a single codebase to be used across multiple platforms, which reduces the amount of code that needs to be written and maintained.
- Limited number of multiplatform libraries and constaints of a single shared codebase are hard to digest, that is why architecture must be taken seriously in early stages of the project.

Planty's architecture aims to be as simple as possible, while still being able to provide basic features, like **navigation**, **data persistance** and **networking**.

### Model-View-ViewModel

The client is built using a slightly modified version of the Model-View-ViewModel (MVVM) architecture:

![MVVM Diagram](assets/uml/mvvm-arch.svg)

- `Screen`: The Screen is responsible for displaying the data to the user and for handling user input. It is implemented using the [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) framework.
- `ScreenModel`: The ScreenModel is responsible for preparing and managing the data for the `Screen`. Data propagation is implemented using the [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) framework.
- `Manager`: The manager is responsible for handling business logic. It works with one-or-more `Repositories` to fetch data from.
- `Repository`: The Repository is responsible for handling data operations. It provides a clean API to the rest of the app to access app data from a single, platform independent domain (independent from underlying frameworks). It knows where to get the data from and what API calls to make when data is updated. It is relying on simpler `DataSources` to manage the data. It is implemented with the help of the [Store5](https://github.com/MobileNativeFoundation/Store) framework.
- `DataSource`: accesses the data from a single source, for example a database or a remote server. Local persistance is implemented using the [SQLDelight](https://github.com/cashapp/sqldelight) framework, remote persistance is implemented using GitLive's [Firebase Firestore](https://firebase.google.com/docs/firestore/) multiplatform port.

## Navigation

Navigation is implemented using the pragmatic [Voyager](https://github.com/adrielcafe/voyager) library for [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/). It provides a simple and easy to use interface to setup user flows and nested navigation.

## Organization

The project is organized into several modules (or packages):

- `data`: contains data related `DataSource` and framework dependent classes.
  - `network`: contains interfaces for network related `DataSource`s.
  - `sqldelight`: SQLDelight related classes used for local persistency.
  - `firestore`: Firebase Firestore related classes used for remote persistency. Implements `network` interfaces.
- `ui`: contains UI related classes. Single screen is organized into a single package.
  - `screen-name`: contains files for a single screen. Usually contains:
    - `ScreenNameScreen.kt`: Voyager screen.
    - `ScreenNameScreenModel.kt`: Voyager equivalent of `ViewModel`, manages UI state and delegates business logic to `Manager`s.
- `repository`: contains `Repositories`, which depend on other `Repositories` and `Manager`s.
- `domain`: contains classes independent from UI and Platform.
  - `model`: contains the data models.
- `manager`: contains `Manager` classes.
- `di`: contains dependency injection related classes.
- `util`: additional classes that are used across the project.

This structure can be altered if needed, but it is recommended to keep it as simple as possible.

## Development

Few things to keep in mind when developing the project.

Have your JDK compatible with 17 version. If using [Android Studio](https://developer.android.com/studio) (recommended for development) you can download and set it there. This is required to build.

It is recommended to enable `Settings` -> `Experimental` -> `Configure all Gradle tasks during Gradle Sync` inside Android Studio to help you figure out what task can you run. This way, you can easily run your desired `run` or `build` task from the `Gradle` tab.

### Android

Have your Android SDK installed and set up. You can download it from Android Studio. Your emulator should support API 21 (Android 5.0 Lollipop) or higher (recommended at least API 26).

### Desktop

Desktop app is based on Java Swing under the hood. Due to a [dependency issue](https://github.com/adrielcafe/voyager/issues/147) related to [Voyager](https://github.com/adrielcafe/voyager), all `Coroutine` calls must be dispatched explicitly with a `CoroutineDispatcher`. Otherwise, the desktop app will crash.

#### Bad

```kotlin
screenModelScope.launch {
    clickManager.incrementClickCount()
}
```

#### Good

```kotlin
screenModelScope.launch(dispatcherIO) {
    clickManager.incrementClickCount()
}
```

#### Firebase Java implementation

As the [Firebase SDK](https://firebase.google.com/docs/android/setup) is Android dependent, it is not possible to use it in the desktop app. However, there is a [Java implementation](https://github.com/GitLiveApp/firebase-java-sdk) of the Firebase SDK, which can be used in the desktop app. It is not recommended to use it, as it is not officially supported by Google and it is not guaranteed to work. However, it is possible to use basic features, like a small portion of Firestore, with it.

### Web

When changing dependencies, run `./gradlew kotlinUpgradeYarnLock` to update the `yarn.lock` file. Android Studio will throw a compile time error prompting the user to run this command if dependencies are out of date.
