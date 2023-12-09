# Getting Started

`Ex 2023` Android application is an example.
The purpose here is to practice implementing features using some popular 3rd party libraries while
respecting Repository architecture principles.

The app interfaces with [Random User API](https://randomuser.me/) and displays the result as a list.
A user returned by the API can be saved locally and accessed offline.

## Libraries used

* [Retrofit2](https://square.github.io/retrofit/)
* [Coil](https://coil-kt.github.io/coil/)
* [OkHttp3](http://square.github.io/okhttp/) (Cache + Logging)
* [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* [Room](https://developer.android.com/training/data-storage/room)
* [AndroidX](https://developer.android.com/jetpack/androidx/)
* [Mockito](https://github.com/mockito/mockito) (Tests)

## Project setup

* Proguard
* Version catalog (using `libs.versions.toml` file)
* `dev`, `uat` and `prod` environments
* App icon, name, version and apk file name depending on the environment

## Android project

* [Compose UI Theme](https://m3.material.io/theme-builder)
* [Sono font](https://fonts.google.com/specimen/Sono)
