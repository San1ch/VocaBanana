# String Providers

## Overview

A `StringProvider` is an abstraction used to access localized string resources outside the UI layer.

It allows ViewModels and other non-UI classes to use localized text without depending directly on Android's `Context`.

Each feature owns its own provider interface that extends the base `StringProvider` interface.

Example:

```kotlin
interface BackupStringProvider : StringProvider
```

---

# Why use String Providers?

Android resources (`Context.getString()`) should not be accessed directly from ViewModels, coordinators, handlers, or other presentation classes.

Instead, inject a feature-specific `StringProvider`.

Benefits:

- supports localization
- removes direct `Context` dependency
- improves testability
- keeps presentation logic platform-independent

---

# Architecture

Each feature consists of three parts.

## 1. Provider interface

Located in `core.essentials.resources.featureproviders`.

Example:

```kotlin
interface TextStringProvider : StringProvider
```

The interface contains every user-visible string required by the feature.

---

## 2. Android implementation

Located inside the feature's presentation module.

Example:

```kotlin
class TextStringProviderImpl : TextStringProvider
```

The implementation retrieves strings using:

```kotlin
context.getString(...)
```

---

## 3. Dependency Injection

Every implementation is bound through Hilt.

```kotlin
@Binds
fun bindTextStringProvider(
    impl: TextStringProviderImpl
): TextStringProvider
```

The provider is then injected wherever it is needed.

---

# Properties vs Functions

Use properties for constant strings.

```kotlin
val backupSuccess: String
```

Use functions whenever formatting arguments are required.

```kotlin
fun backupCompleted(fileName: String): String
```

or

```kotlin
fun validationTooLong(maxLength: Int): String
```

---

# Naming

Group related strings together.

Example:

- validation
- loading
- success
- errors

Example:

```kotlin
val generateWordsPreparingText

val generateWordsAnalyzingLexicon

val generateWordsSavingWords

val generateWordsSuccess

val generateWordsNetworkError
```

This keeps large providers easy to navigate.

---

# Rules

- Every feature should have its own `StringProvider`.
- All feature providers must extend the base `StringProvider` interface.
- Never call `Context.getString()` from ViewModels, Coordinators, or Handlers.
- Store every user-visible message in string resources.
- Use properties for constant strings.
- Use functions for formatted strings.
- Keep providers focused on a single feature.
- Do not share feature-specific strings between unrelated features.