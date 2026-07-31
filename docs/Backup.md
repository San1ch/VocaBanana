# Backup

## Overview

The backup system consists of two independent managers:

- `LocalBackupManager`
- `CloudBackupManager`

Each manager works independently and can be enabled or disabled separately.

Supported configurations:

- Local backup only
- Cloud backup only
- Both enabled simultaneously

The managers do not depend on each other.

---

# Architecture

## BackupSettingsRepository

Stores all backup-related settings.

Examples:

- Local backup path
- Local backup enabled
- Cloud backup enabled
- Cloud account information
- Last backup timestamp

---

## SettingsRepository

Stores general application settings.

It also contains:

- `lastDatabaseUpdateTime`

This value represents the last successful database modification and is used to determine whether an existing backup is outdated.

---

## DataChangeTracker

A single entry point for notifying the application that data has changed.

Every operation that modifies the database must call:

```kotlin
dataChangeTracker.notifyDataChanged()
```

This updates `lastDatabaseUpdateTime` automatically.

---

# Detecting Outdated Backups

The backup system determines whether a backup is outdated by comparing two timestamps:

- `lastDatabaseUpdateTime`
- `lastBackupTime`

The repository exposes:

```kotlin
val isBackupNeedUpdateFlow: Flow<Boolean>
```

Implementation:

```kotlin
combine(
    lastBackupTimeFlow,
    settingsRepository.lastDatabaseUpdateTime
) { lastBackupTime, lastDatabaseUpdateTime ->
    lastDatabaseUpdateTime > lastBackupTime
}
```

If the result is `true`, the backup is considered outdated and should be recreated.

---

# Local Backup

The current implementation creates a ZIP archive.

The archive contains:

- `databases/`
- `files/`
- `metadata.json`

`metadata.json` currently stores:

- Backup timestamp
- Application version

Additional metadata can be added in the future without changing the backup format.

---

# Restore

The restore operation accepts a backup file URI.

```kotlin
localBackupManager.restore(fileUri)
```

The manager extracts the archive and restores all stored application files.

---

# Usage

Create a backup:

```kotlin
localBackupManager.backup()
```

Restore a backup:

```kotlin
localBackupManager.restore(fileUri)
```

Observe whether the backup is outdated:

```kotlin
backupSettingsRepository.isBackupNeedUpdateFlow
```

---

# Rules

- Always call `DataChangeTracker.notifyDataChanged()` after a successful database modification.
- Never update `lastDatabaseUpdateTime` manually.
- Update `lastBackupTime` only after a successful backup.
- Keep `LocalBackupManager` and `CloudBackupManager` completely independent.
- Business logic should rely on `isBackupNeedUpdateFlow` instead of comparing timestamps manually.