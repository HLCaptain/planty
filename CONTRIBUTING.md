# Contributing to Planty

If you are not sure about something, feel free to ask in the discussion of the issue. We are happy to help!

## How to contribute

0. Check for already existing issues if you have a feature request.
1. **Create new issue** for any bugs or feature requests.
2. **Fork** the repository.
3. **Create a new branch** from `dev` branch for each issue.
4. Make your changes.
5. Pull in the `dev` branch into your branch and resolve any merge conflicts.
6. Open a **draft pull request** (prepending title with "Draft: ") into `dev` branch.
7. Link the pull request by mentioning the **issue number** `#<issue_number>` in the description or any other way.
8. Set reviewer on pull request (or mention people in comments if feature is not available on free version).
9. Discuss any problems or suggestions.
10. Make changes if necessary.
11. **Congratulations!** You have contributed to Planty!

## Conventions

To keep the code clean and readable, we are using coding conventions and rules. Make sure to follow them where possible.

### Client

The client is using [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html).

### Sensor Broker

The broker is written in Python and is using [PEP8](https://www.python.org/dev/peps/pep-0008/) conventions.

## Documentation

Comment code where necessary, follow coding convention rules.

For `Markdown` documentation, use a linter to check for formatting errors. We strongly recommend [markdownlint](https://github.com/DavidAnson/markdownlint) to help with consistency. VSCode extension available [here](https://marketplace.visualstudio.com/items?itemName=DavidAnson.vscode-markdownlint).

### Keep your code readable

Keep your comments simple and try to use descriptive variable names.

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

### Feature Documentation

For each larger feature, like communication between the broker and sensors via `MQTT` protocol, make sure to write a proper, but not too extensive documentation using `Markdown`. It should be understandable for someone who is not familiar with the project.

Try to use the template from [Make a README](https://www.makeareadme.com/) and place it in the proper folder.

If you see an architectural document referencing related features or issues, make sure to **link them**.

### Diagrams and Sketches

If you want to include diagrams or sketches, use [Excalidraw](https://excalidraw.com/) and export them as `SVG` files to the locally created `assets` folder. You can save the `.excalidraw` files in the `assets` folder to modify later if needed.
