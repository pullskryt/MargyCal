# MargyCal · Тилкайо

Автономный калькулятор для Android с интерфейсом Jetpack Compose и Material 3. Поддерживает арифметические выражения, решение в столбик, действия с дробями и расчёт вклада с капитализацией и пополнениями. В игре есть ставки и кредиты на вымышленные фишки, без покупок и вывода. В настройках можно выбрать режим «Белая груша».

## Сборка

Понадобятся JDK 17 или новее и Android SDK Platform 35. Укажите путь к SDK через `ANDROID_HOME` или в локальном файле `local.properties`.

```sh
./gradlew :app:assembleDebug
```

На Windows команда — `gradlew.bat :app:assembleDebug`. Gradle создаст APK в `app/build/outputs/apk/debug/app-debug.apk`. Приложение поддерживает Android 7.0 (API 24) и новее. Имя пакета — `com.margycal.tilkayo`.

Исходный код проекта распространяется по лицензии [MIT](LICENSE).
