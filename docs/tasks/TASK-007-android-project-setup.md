# [TASK-007] Базова ініціалізація Android-проєкту та конфігурація Gradle

- **Етап:** Етап 4 — Реалізація MVP
- **Виконавець:** Android Dev / Агент 0
- **Статус:** In Review (Готовий до верифікації QA)
- **Гілка:** `task/005-mvp-implementation`
- **Дата створення:** 2026-11-20

---

## 1. Мета завдання
Розгорнути робочу структуру Android Native проєкту з Gradle 9.6.0, Kotlin 2.1.0, Jetpack Compose Material 3, Room SQLite 2.6.1 та KSP 2.1.0. Забезпечити успішну компіляцію під Android API 35/37.

---

## 2. Критерії приймання
- [x] Налаштовано `settings.gradle.kts`, `build.gradle.kts`, `app/build.gradle.kts`, `gradle.properties`, `gradle-wrapper`.
- [x] Підключено Room SQLite із генерацією коду Kotlin (`room.generateKotlin = true`).
- [x] Підключено Jetpack Compose Material 3, Activity Compose та Navigation Compose.
- [x] Дозволи в `AndroidManifest.xml` відповідають політиці Zero Network (жодного дозволу INTERNET).
- [x] Успішна збірка APK (`assembleDebug`) та виконання тестів (`testDebugUnitTest`).

---

## 3. Журнал роботи
### 2026-11-20 11:10 — Android Dev
- **Зроблено:** Створено структуру проєкту, налаштовано сумісність із Java 25 середовища Android Studio JBR. Успішно зібрано `app-debug.apk` (розмір ~18.8 МБ).
- **Стан:** Готово до верифікації QA.
