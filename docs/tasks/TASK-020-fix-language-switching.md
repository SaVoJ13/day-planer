# КАРТКА ЗАВДАННЯ: TASK-020
## Виправлення динамічного перемикання мови інтерфейсу (Dynamic Language Switching Fix)

---

- **ID завдання:** TASK-020
- **Назва:** Виправлення застосування вибраної мови в інтерфейсі (UK, EN, PL, RU)
- **Тип:** Bugfix / Localization
- **Виконавці:** Android Developer, Localization Agent, Independent QA Agent
- **Статус:** `QA Verified`
- **Гілка:** `task/011-fix-dynamic-language-switching`
- **Дата створення:** 2026-11-20

---

## 1. Опис проблеми та корінна причина
- **Симптом:** Користувач повідомив, що при перемиканні мови в налаштуваннях (Settings) мова інтерфейсу не змінюється.
- **Корінна причина:**
  1. У Jetpack Compose функція `stringResource(id)` внутрішньо викликає `LocalContext.current.resources.getString(id)`.
  2. У `MainActivity.kt` використовувався `CompositionLocalProvider(LocalConfiguration provides localizedConfig)`, але `LocalContext provides localizedContext` був відсутній. Через це `stringResource` продовжував звертатися до початкового контексту Activity із системною локаллю, ігноруючи `localizedConfig`.
  3. В `app/src/main/res/` була відсутня директорія `values-uk/` (були лише `values`, `values-en`, `values-pl`, `values-ru`). На пристроях із системною мовою EN або RU запит до локалі `uk` міг спричиняти некоректну резолюцію ресурсів без явного каталогу `values-uk/`.
  4. У `SettingsScreen.kt` не було окремого радіо-пункту для вибору «Системна мова» (`lang_system`), а значення `system` за замовчуванням позначало прапорець української мови.

---

## 2. Рішення
1. **Динамічний контекст у Compose:**
   - Оновити `LocaleHelper.kt`: додано підтримку `config.setLocales(LocaleList(locale))` та `config.setLayoutDirection(locale)`.
   - У `MainActivity.kt`: оновити `CompositionLocalProvider`, передаючи і `LocalConfiguration provides localizedContext.resources.configuration`, і `LocalContext provides localizedContext`.
   - Додати `key(languageSetting)` для гарантованого миттєвого повного оновлення дерева Compose при зміні мови.
   - Додати `attachBaseContext` у `MainActivity.kt` для збереження локалі після перезапуску.
2. **Явний каталог ресурсів `values-uk`:**
   - Створити `app/src/main/res/values-uk/strings.xml` для однозначної резолюції української мови системою Android.
3. **Оновлення вибору мови в Налаштуваннях:**
   - Додати рядок `lang_system` у всі 4 мовні файли.
   - Додати радіо-кнопку «Системна мова» в `SettingsScreen.kt`.

---

## 3. Критерії готовності (DoD)
- [x] Оновлено `LocaleHelper.kt` з підтримкою `LocaleList`.
- [x] Оновлено `MainActivity.kt` (`attachBaseContext`, `LocalContext provides localizedContext`, `key(languageSetting)`).
- [x] Створено каталог та файл `app/src/main/res/values-uk/strings.xml`.
- [x] Додано ключ `lang_system` у всі файли локалізації (`values`, `values-uk`, `values-en`, `values-pl`, `values-ru`).
- [x] Оновлено `SettingsScreen.kt` з вибором `Системна / UK / EN / PL / RU`.
- [x] Успішні модульні тести (`.\gradlew testDebugUnitTest`).
- [x] Збірка APK (`.\gradlew assembleDebug`).
- [x] Встановлення на реальний телефон та перевірка перемикання мови на живому пристрої.
- [x] Незалежна верифікація окремим QA-субагентом.

---

## 4. Журнал виконання (Append-only)
- **2026-11-20 23:03:** Створено гілку `task/011-fix-dynamic-language-switching`. Сформовано план виправлення.
- **2026-11-20 23:10:** Виконано зміни коду в `MainActivity.kt`, `LocaleHelper.kt`, `SettingsScreen.kt`, створено `values-uk/strings.xml`, синхронізовано всі 5 файлів strings.xml (коміт `b5954ab`).
- **2026-11-20 23:19:** Незалежний QA-агент провів повну сувору верифікацію: 11/11 тестів пройдено, APK зібрано, перевірено інсталяцію та живе динамічне перемикання мови (PL -> UK -> EN -> UK) на фізичному пристрої `RZCX326349A`. Складено звіт `docs/qa/TASK-020_QA_VERIFICATION.md`. Вердикт: `QA VERIFIED` 🟢.
