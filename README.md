# Manga Download for Android
[![CircleCI](https://img.shields.io/circleci/project/github/ArnaudPiroelle/manga-downloader-android/develop.svg?style=for-the-badge)](https://circleci.com/gh/ArnaudPiroelle/manga-downloader-android/tree/develop)

This application allow you to download automatically your manga chapters.

## Disclaimer
This application is still a prototype and is not meant to finish on the store.

For personal usage only!
Buy your manga;)


## Technical stack
* Koin
* OkHttp
* Room
* PermissionDispatcher
* Kotlin

## TODO
* Move to Coroutine Workers
* Upgrade ConstraintLayout to alpha05

## Change logs

### 3.0.0
- Toothpick is good, but not enough. I choose to use Koin for his kotlin DSL.

### 2.0.0
- From scratch rewrite of the application with true MVP architecture
- Migrate Dagger to Toothpick
- Migrate Sprinkles to Room
- Migrate RxJava to RxJava2
- Rewrite JapScan Provider to use Jsoup instead of regex
- Remove Retrofit and use directly OkHttp
- Use JobScheduler to improve battery usage
- Add eBooks folder selection at first start

### 1.2.0
- ...

