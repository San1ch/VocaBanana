# Navigation
## Quick Start

To add a new screen:

1. Create a new `Route` in `Routes.kt`.
2. Register the destination in `buildAppNavGraph()` in `AppNavGraph.kt`.
3. Create a router interface in the feature module.
4. In the `navigation` module:
    - Create a router implementation in the `routers` package.
    - Bind it in `RouterModule` using Hilt.
5. Inject the router into the ViewModel.

### Routes with Arguments

If the destination requires navigation arguments:

1. Add the required properties to the route.


```kotlin
@Serializable
data class TextItemRoute(
    val id: Long
) : Route
```
2. Read the route inside the `composable` and pass the values to the screen or ViewModel.
```kotlin
composable<TextItemRoute> {
    val route = it.toRoute<TextItemRoute>()

    TextItemScreen(
        id = route.id
    )
}
```

## Overview

The `navigation` module is responsible for application navigation and isolates feature modules from `Navigation Compose`.

Instead of interacting with `NavController` directly, feature modules communicate through router interfaces. Navigation requests are converted into commands, temporarily stored in a `Channel`, and executed by `AppNavHost` when a `NavController` is available.

This approach keeps the presentation layer independent from the UI implementation and follows the Dependency Inversion Principle.

---

# Architecture

```
Feature UI
    │
    ▼
ViewModel
    │
    ▼
Feature Router (interface)
    │
    ▼
Feature Router Implementation
    │
    ▼
AppRouter
    │
    ▼
Channel
    │
    ▼
AppNavHost
    │
    ▼
NavController
    │
    ▼
Navigation Compose
```

---

# Main Components

## Route

Each destination is represented by a serializable route object.

```kotlin
@Serializable
data object MainRoute : Route
```

Using typed routes improves type safety and avoids string-based navigation.

---

## Feature Routers

Each feature defines its own router interface.

Example:

```kotlin
interface MainRouter {
    fun launchVocabulary()
    fun launchSettings()
}
```

The feature knows only **what** it wants to open, not **how** navigation is performed.

---

## Router Implementations

Router implementations belong to the `navigation` module.

Example:

```kotlin
class MainRouterImpl(
    private val appRouter: AppRouter
) : MainRouter {

    override fun launchVocabulary() {
        appRouter.navigateTo(VocabularyRoute)
    }
}
```

Their only responsibility is mapping feature navigation requests to application routes.

---

## AppRouter

`AppRouter` is the central navigation abstraction.

```kotlin
interface AppRouter {

    fun navigateTo(route: Route)

    fun resetTo(route: Route)

    fun navigateBack()

}
```

All feature routers delegate navigation to this interface.

---

## AppRouter Implementation

`NavComponentAppRouterImpl` receives navigation requests and converts them into executable navigation commands.

Instead of navigating immediately, commands are placed into a `Channel`.

```kotlin
_navigationCommands.trySend { navController ->
    navController.navigate(route)
}
```

The router does not hold a reference to `NavController`, allowing navigation requests to be made even before the UI is ready.

---

## Navigation Commands

Navigation commands are stored as functions:

```kotlin
Channel<(NavController) -> Unit>
```

Each command receives a `NavController` and performs a navigation operation.

Examples:

* Navigate to a destination
* Reset the back stack
* Navigate back
* Any future custom navigation action

Storing functions instead of routes makes the system flexible and independent from specific navigation operations.

---

## AppNavHost

`AppNavHost` owns the application's `NavController`.

It continuously listens for navigation commands.

```kotlin
LaunchedEffect(navController) {
    router.navigationCommands.collect { command ->
        command(navController)
    }
}
```

Whenever a new command arrives, it is executed using the current `NavController`.

There should be only one consumer of the navigation channel.

---

# Navigation Flow

1. A user performs an action.
2. The ViewModel calls a feature router.
3. The feature router delegates to `AppRouter`.
4. `AppRouter` places a navigation command into the `Channel`.
5. `AppNavHost` receives the command.
6. The command executes using `NavController`.
7. Navigation Compose performs the navigation.

---

# Design Principles

* Feature modules never depend on `NavController`.
* Navigation logic is centralized.
* Features communicate through interfaces.
* Navigation requests are asynchronous events.
* Commands are executed only by `AppNavHost`.
* Each navigation event is handled exactly once.
* Navigation history is managed by `NavController`; the `Channel` is not a history storage.

---

# Benefits

* Decouples features from Navigation Compose.
* Improves testability.
* Keeps ViewModels UI-independent.
* Supports navigation requests before the UI is fully initialized.
* Centralizes navigation behavior.
* Easy to extend with new navigation operations.
