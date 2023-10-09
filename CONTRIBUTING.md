# Contributing to Planty

## How to contribute

0. Check for already existing issues if you have a feature reqest.
1. Open issues for any bugs or feature requests.
2. Fork the repository.
3. Create a new branch from `dev` branch for each issue.
4. Make your changes.
5. Merge the `dev` branch into your branch and resolve any merge conflicts.
6. Link the branch to the issue with a pull request by mentioning the issue number `#<issue_number>`.
7. Set reviewer on pull request.
8. Discuss any problems or suggestions.
9. Make changes if necessary.
10. **Congratulations!** You have contributed to Planty!

## Conventions

If you are not sure about something, feel free to ask in the discussion of the issue. We are happy to help!

### Client

The client is using [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html). Make sure to follow them.

### Sensor Broker

The broker is written in Python and is using [PEP8](https://www.python.org/dev/peps/pep-0008/) conventions. Make sure to follow them.

## Documentation

Comment code where necessary. Keep your comments simple and try to use descriptive variable names.

```kotlin
// Good
val appName = "Planty"
```

```kotlin
// Bad
val string = "Planty"
```

Don't be afraid to extract variables from `if` statements.

```kotlin
// Good
val isPlantThirsty = plant.soilMoisture < 0.5
val isMorning = currentTime.hour < 10 && currentTime.hour > 6
val shouldNotifyUser = isPlantThirsty && isMorning

if (shouldNotifyUser) {
    // Give notification to water the plant
}
```

```kotlin
// Bad
if (plant.soilMoisture < 0.5 && currentTime.hour < 10 && currentTime.hour > 6) {
    // Give notification to water the plant
}
```

For each larger feature, like communication between the broker and sensors via `MQTT` protocol, make sure to write a proper, but not too extensive documentation using `Markdown`. It should be understandable for someone who is not familiar with the project.

Try to use the template from [Make a README](https://www.makeareadme.com/) and place it in the proper folder.

If you see an architectural document referencing related features or issues, make sure to **link them**.
