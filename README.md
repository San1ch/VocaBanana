## Vocabanana

**Vocabanana** is an English vocabulary management and learning assistant that helps you build your own dictionary, read texts in English, and track your learning progress.

Learn vocabulary through your own texts, not pre-made lists.

---

## Core Idea

Vocabanana gives you control over your vocabulary learning.

Instead of random word lists, you can clearly see:
- which words you already know  
- which ones you are learning  
- which ones you ignore  

Learning is based on your own texts, not pre-made content.

---

## Features

- Read English texts inside the app  
- Add your own texts (manually or via file)  
- Automatically generate words from text  
- Manage words with statuses:
  - Known  
  - Learning
  - Don't known
  - Ignored  
- Track your vocabulary progress  
- Tap a word in a text to view its data from your dictionary  

---

## Architecture Overview

The project is built using **Clean Architecture** principles and is fully **multi-modular**. It is strictly decoupled into independent layers to ensure high scalability, separation of concerns, and build optimization.

[Read the detailed Technical & Architecture Documentation here](./ARCHITECTURE.md)

---
## Usage

1. Go to the **Texts** section  
2. Tap **"+"**  
3. Add:
   - title  
   - text (manually or from a file)  
4. Open the text → tap **Start Reading**  
5. Tap **Generate words from text**  
6. Get a list of words in your vocabulary  
7. Mark words as:
   - known / learning / don't known / ignored  

---

## Installation

Download the app from the **Releases** section on GitHub and install it on your Android device.

[Latest release](https://github.com/San1ch/VocaBanana/releases)

---

## Tech Stack

### Core
- Kotlin
- Coroutines
- Flow

### UI
- Jetpack Compose
- Material 3

### Architecture
- MVVM / MVI 
- Clean Architecture

### Jetpack Components
- ViewModel
- Navigation (Compose)
- Room
- DataStore

### Dependency Injection
- Hilt

### Tools
- Git
- Gradle
- Android Studio
- Logcat
---

## Project Status

MVP / Active Development.

The app is already usable, but the UX is still basic and will be improved.

---

## Roadmap

- Text analysis (word frequency)  
- Learning tools:
  - fast translation exercises  
  - sentence-based practice  
- Exercise generation based on your vocabulary  
- AI integration into your own lessons  

---

## Known Issues

- UI is not fully optimized  
- Some flows may feel non-intuitive  

---

## Contributing

This is currently a personal project. Contributions are not accepted.

---

## Contact

GitHub: https://github.com/San1ch
