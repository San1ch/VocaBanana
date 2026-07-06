# VocaBanana



**VocaBanana** is an English vocabulary management and learning assistant that helps users build their own vocabulary database, learn through personal texts, and track learning progress over time.



Unlike traditional vocabulary applications, VocaBanana focuses on learning through user-provided content rather than pre-built word lists.



---



## Core Idea



The main goal of the project is to provide full control over the vocabulary learning process.



Instead of studying random collections of words, users can clearly distinguish:



* words they already know;

* words currently being learned;

* words they want to ignore;

* words extracted from their own reading materials.



Learning is built around personal texts and individual vocabulary progression.



---



## Features



### Text Reading



* Read English texts directly inside the application;

* Import texts manually or from files;

* Save reading progress automatically;

* Customize the reading experience.



### Vocabulary Management



* Generate vocabulary automatically from texts;

* Manage words using custom learning statuses:



  * Known

  * Learning

  * Unknown

  * Ignored

* Track vocabulary growth;

* View word definitions and metadata.



### Interactive Learning



* Tap any word while reading;

* View dictionary information instantly;

* Open external dictionary resources when necessary;

* Build a personal vocabulary database from real content.



---



## Architecture Overview



The project follows a feature-oriented, fully modular architecture designed for scalability, maintainability, and build performance optimization.



### Architectural Principles



* Feature-oriented modular architecture;

* Strict separation of concerns;

* Dependency inversion via interfaces;

* Convention-based Gradle build logic;

* MVVM + MVI presentation architecture;

* Single source of truth state management;

* Platform-independent business logic where possible.



### Technical Highlights



* Multi-module Gradle architecture;

* Custom Gradle Convention Plugins;

* Gradle Composite Build (`build-logic`);

* Feature-based navigation abstraction;

* Custom Kotlin module generation system;

* Reactive state management using Kotlin Flow;

* Compile-time safe event processing.



For detailed technical documentation, see:



**ARCHITECTURE.md**



---



## Project Structure



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



## Usage



1. Open the **Texts** section;

2. Create a new text;

3. Add content manually or import it from a file;

4. Start reading;

5. Generate vocabulary from the text;

6. Review generated words;

7. Assign learning statuses;

8. Track your learning progress.



---



## Installation



Download the latest APK from the GitHub Releases page and install it on your Android device.



---



## Technology Stack



### Language & Concurrency



* Kotlin

* Coroutines

* Flow



### UI



* Jetpack Compose

* Material 3



### Architecture



* Clean Architecture

* MVVM

* MVI

* Multi-Module Architecture



### Android Components



* ViewModel

* Room

* DataStore

* Navigation Compose



### Dependency Injection



* Hilt



### Build System



* Gradle

* Composite Builds

* Convention Plugins

* Custom Module Generator



### Development Tools



* Android Studio

* Git

* Logcat



---



## Current Status



The project is currently in active development.



The core functionality is already implemented and usable, while the user experience and learning features continue to evolve.



---



## Planned Features



* Word frequency analysis;

* Vocabulary statistics;

* Translation exercises;

* Sentence-based practice;

* Vocabulary-based exercise generation;

* AI-assisted learning tools;

* Extended progress analytics.



---



## Known Limitations



* Some UI components require additional polish;

* Several workflows are still being refined;

* Performance optimization is ongoing.



---



## Contributing



This project is currently maintained as a personal learning and portfolio project. External contributions are not being accepted at this time.



---



## Author



GitHub: **San1ch**
