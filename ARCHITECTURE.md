# Architecture

## Design Principles

The project follows several architectural principles aimed at improving scalability, maintainability, and long-term development efficiency:

* Feature-oriented modular architecture
* Strict separation of concerns
* Dependency inversion via interfaces
* Convention-based Gradle build logic
* MVVM + MVI presentation architecture
* Single source of truth state management

---

# Build Logic & Convention Plugins

The project uses a dedicated `build-logic` composite build to centralize Gradle configuration and eliminate duplication across modules.

```text
build-logic/
 ├── build.gradle.kts
 ├── settings.gradle.kts
 └── convention plugins
```

The build logic is connected through Gradle Composite Builds:

```kotlin
includeBuild("build-logic")
```

Instead of duplicating Android and Kotlin configuration in every module, the project uses custom convention plugins:

* `custom-android-application`
* `custom-android-library`
* `custom-kotlin-library`

These plugins centralize:

* Android SDK configuration
* Java/Kotlin compiler settings
* Compose configuration
* build types
* shared build features
* common project conventions

This approach provides:

* consistent configuration across all modules;
* reduced Gradle boilerplate;
* simplified module creation;
* improved maintainability;
* easier project scaling.

Shared build constants are also centralized to avoid configuration duplication.

---

# Modular Architecture

The project follows a feature-oriented modular architecture designed to maximize separation of concerns and independent feature development.

```text
app
navigation

core
 ├── essentials
 ├── ui
 └── android
      ├── database
      └── commonandroid

feature
 ├── init
 ├── main
 ├── text
 ├── vocabulary
 ├── word
 └── ...
```

---

# App Module

The `app` module acts as the application entry point.

Its responsibilities are intentionally minimal:

* application initialization;
* Hilt setup;
* dependency wiring;
* hosting the root navigation graph.

Most business and presentation logic is delegated to feature modules.

---

# Core Modules

The `core` layer contains reusable infrastructure shared across the entire application.

## core:essentials

This module contains platform-independent domain abstractions and shared business logic.

Examples include:

* domain models;
* repository contracts;
* shared interfaces;
* business abstractions;
* common use cases;
* cross-feature domain entities.

A common architectural pattern used throughout the project is defining repository contracts inside `core:essentials`, while platform-specific implementations are provided by Android modules.

The module intentionally avoids Android framework dependencies whenever possible.

---

## core:ui

This module contains reusable UI infrastructure shared between features.

Examples include:

* reusable composables;
* application theme and typography;
* UI models and mappers;
* base ViewModel implementations;
* UI event handling;
* state management utilities;
* common UI components.


---


## core:android

This layer contains Android-specific implementations isolated from business logic.

Current modules include:

### database

Contains:

* Room database implementation;
* entities;
* DAOs;
* mappers;
* repository implementations;
* local data sources.

### commonandroid

Contains Android-specific infrastructure such as:

* DataStore implementations;
* file storage services;
* string providers;
* dependency injection bindings;
* platform service implementations.

This separation allows business logic to remain platform-independent while isolating Android framework dependencies.

---

# Navigation Architecture

Navigation is implemented using feature-specific router contracts.

Each feature defines its own navigation interface:

```kotlin
interface FeatureRouter
```

The actual navigation implementation resides exclusively inside the `navigation` module.

```text
Feature
    ↓
Router Interface
    ↓
Navigation Module
    ↓
AppRouter
```

This approach provides:

* complete feature isolation;
* no direct dependencies between features;
* easier navigation refactoring;
* improved testability.

The project also uses a centralized `AppRouter` abstraction that exposes common navigation operations:

* navigate;
* reset navigation stack;
* navigate back.

---

# Feature Architecture

Each feature is isolated into its own module and divided into two layers:

```text
feature/example
 ├── domain
 └── presentation
```

## Domain

Contains:

* business logic;
* use cases;
* feature contracts;
* domain abstractions.

This layer remains independent from Android UI components whenever possible.

## Presentation

Contains:

* Compose screens;
* ViewModels;
* UI state;
* UI intents;
* navigation contracts;
* presentation-specific logic.

This separation allows features to evolve independently while maintaining clear architectural boundaries.

---

# Presentation Architecture

The presentation layer combines MVVM and MVI approaches.

Each screen follows a consistent structure:

```text
Screen
 ├── Screen()
 ├── ScreenContent()
 ├── UiIntent
 ├── UiState
 └── ViewModel
```

## Screen

Responsible only for:

* obtaining ViewModel instances;
* collecting UI state;
* collecting UI events;
* forwarding data to content composables.

## ScreenContent

Contains the actual UI implementation and remains as stateless as possible.

## UiIntent

User interactions are represented as sealed classes:

```kotlin
sealed class UiIntent
```

Every ViewModel exposes a single entry point:

```kotlin
fun onIntent(intent: UiIntent)
```

This approach provides:

* a single event processing pipeline;
* compile-time safety;
* exhaustive event handling;
* reduced callback complexity.

## UiState

Every screen exposes a single source of truth:

```kotlin
data class UiState(...)
```

which simplifies state management and reactive UI updates.

---

# UI State Management

The project uses a lightweight state management system based on sealed UI states:

* Loading
* Success
* Empty
* Error

ViewModels expose reactive state streams that are transformed into UI states using shared abstractions.

UI state consumption is centralized through reusable observer components, which handle:

* loading states;
* empty states;
* error states;
* successful content rendering.

This approach significantly reduces boilerplate while maintaining consistent UI behavior.

---

# Custom Architectural Decisions

## UI Event System

The project uses a dedicated one-time UI event system:

```text
ViewModel
     ↓
UiEvent Flow
     ↓
CollectUiEvents()
     ↓
Android UI actions
```

Current implementations include:

* Toast messages;
* external URL navigation;
* one-time UI effects.

This approach separates transient UI events from persistent UI state.

---

## Navigation Abstraction

Navigation contracts are defined by features and implemented centrally.

This allows:

* feature independence;
* navigation decoupling;
* easier testing;
* simplified maintenance.

---

## Module Generator

To reduce boilerplate during development, the project includes a custom Kotlin-based module generation script.

The generator automatically:

* creates module structure;
* generates Gradle configuration;
* creates package hierarchy;
* registers modules in `settings.gradle.kts`;
* injects required dependencies.

This significantly reduces manual setup time and ensures a consistent module structure throughout the project.

---

# Dependency Direction Rules

The project follows strict dependency direction rules:

```text
app
 ↓
navigation
 ↓
feature
 ↓
core
```

Additional rules:

* features never depend on other features;
* business logic never depends on Android implementations;
* Android implementations depend on abstractions;
* navigation implementation remains isolated;
* shared functionality belongs to the `core` layer.

These constraints help preserve architectural boundaries as the project grows.
