# ЗВІТ НЕЗАЛЕЖНОЇ ВЕРИФІКАЦІЇ QA: TASK-020

> **Проєкт:** «Список покупок» (Shopping List)  
> **Завдання:** TASK-020 (Виправлення динамічного перемикання мови інтерфейсу)  
> **Дата верифікації:** 2026-11-20  
> **Верифікатор:** Незалежний QA-агент (субагент Antigravity)  
> **Гілка:** `task/011-fix-dynamic-language-switching`  
> **Коміт перевірки:** `b5954abe4fcc03c8489bd7b156b19feb4e643721`  
> **Підстава:** Конституція команди агентів (п. 2.1, 6, 7, Червона лінія №9 «Заборона самоверифікації»), `DEFINITION_OF_DONE.md`.  
> **Вердикт:** **QA VERIFIED** 🟢 (VERDICT: PASSED)

---

## 1. Мета та об'єкт перевірки

Верифікація виконання завдань TASK-020:
1. **Виправлення непрацюючого перемикання мови в Jetpack Compose:**
   - Compose-функція `stringResource` звертається до `LocalContext.current.resources`. Раніше `MainActivity` надавала лише `LocalConfiguration`, через що контекст Activity продовжував читати початкову системну локаль.
   - Рішення: у `CompositionLocalProvider` надано як `LocalConfiguration`, так і `LocalContext` через локалізований `createConfigurationContext`, а також додано `key(languageSetting)` навколо `ShoppingListTheme` для негайного повного оновлення дерева інтерфейсу в пам'яті.
   - У `MainActivity.kt` реалізовано `attachBaseContext` для коректного збереження та завантаження локалі при холодному старті.
2. **Створення каталогу `app/src/main/res/values-uk/`:**
   - Гарантія однозначної резолюції української мови системою Android незалежно від системної мови пристрою.
3. **Синхронізація ресурсів локалізації:**
   - Додано ключ `lang_system` у всі мовні каталоги (`values`, `values-uk`, `values-en`, `values-pl`, `values-ru`).
   - Повна синхронізація 81 рядкового ресурсу та специфікаторів форматування по всіх 5 файлах `strings.xml`.
4. **Оновлення вибору мови в налаштуваннях (`SettingsScreen.kt`):**
   - Додано окремий радіо-пункт «Системна (за замовчуванням)» (`SettingsRepositoryImpl.LANG_SYSTEM`).
   - Наявні окремі радіо-пункти для uk, en, pl, ru.

---

## 2. Результати перевірки коду (Code Inspection)

### 2.1. `LocaleHelper.kt`
- Підтверджено роботу з `LocaleList`:
  ```kotlin
  val config = Configuration(context.resources.configuration)
  config.setLocale(locale)
  config.setLocales(LocaleList(locale))
  config.setLayoutDirection(locale)
  return context.createConfigurationContext(config)
  ```
- Для `languageCode == "system"` коректно береться системна локаль через `Resources.getSystem().configuration.locales`.
- Примусово оновлюється `Locale.setDefault(locale)` для коректного форматування дат/чисел.
- **Статус:** **PASS** ✅

### 2.2. `MainActivity.kt`
- **Збереження локалі при старті:**
  ```kotlin
  override fun attachBaseContext(newBase: Context) {
      val prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
      val lang = prefs.getString(SettingsRepositoryImpl.KEY_LANGUAGE, SettingsRepositoryImpl.LANG_SYSTEM)
          ?: SettingsRepositoryImpl.LANG_SYSTEM
      val context = LocaleHelper.updateLocale(newBase, lang)
      super.attachBaseContext(context)
  }
  ```
- **Реактивне оновлення дерева Compose:**
  ```kotlin
  val currentContext = LocalContext.current
  val localizedContext = remember(languageSetting, currentContext) {
      LocaleHelper.updateLocale(currentContext, languageSetting)
  }

  CompositionLocalProvider(
      LocalConfiguration provides localizedContext.resources.configuration,
      LocalContext provides localizedContext
  ) {
      key(languageSetting) {
          ShoppingListTheme(themeSetting = themeSetting) { ... }
      }
  }
  ```
  `LocalContext provides localizedContext` гарантує, що всі виклики `stringResource` читають рядки вибраної локалі. `key(languageSetting)` примусово перестворює дерево інтерфейсу без необхідності перезапуску Activity.
- **Статус:** **PASS** ✅

### 2.3. `SettingsScreen.kt`
- Розділено вибір:
  - `lang_system` -> `SettingsRepositoryImpl.LANG_SYSTEM` ("system")
  - `lang_uk` -> `SettingsRepositoryImpl.LANG_UK` ("uk")
  - `lang_en` -> `SettingsRepositoryImpl.LANG_EN` ("en")
  - `lang_pl` -> `SettingsRepositoryImpl.LANG_PL` ("pl")
  - `lang_ru` -> `SettingsRepositoryImpl.LANG_RU` ("ru")
- Кожен пункт має незалежний стан `selected = currentLang == ...` та викликає `viewModel.setLanguage(...)`.
- **Статус:** **PASS** ✅

### 2.4. Ресурси локалізації (`strings.xml`)
Перевірено 5 файлів:
1. `app/src/main/res/values/strings.xml`
2. `app/src/main/res/values-uk/strings.xml`
3. `app/src/main/res/values-en/strings.xml`
4. `app/src/main/res/values-pl/strings.xml`
5. `app/src/main/res/values-ru/strings.xml`

**Фактичні результати перевірки:**
- Кількість ключів у кожному файлі: рівно **81 ключ** (100% симетричний збіг).
- Перевірка специфікаторів формату (`%s`, `%d`, `%1$s` тощо): **0 невідповідностей**.
- Значення ключа `lang_system`:
  - `values/strings.xml`: «Системна (за замовчуванням)»
  - `values-uk/strings.xml`: «Системна (за замовчуванням)»
  - `values-en/strings.xml`: «System default»
  - `values-pl/strings.xml`: «Domyślny systemu»
  - `values-ru/strings.xml`: «Системный (по умолчанию)»
- **Статус:** **PASS** ✅

---

## 3. Результати модульного тестування (Unit Tests)

Виконано команду:
`.\gradlew.bat testDebugUnitTest --rerun-tasks`

### Результати:
| Тестовий набір | Кількість тестів | Помилки | Пропущено | Час виконання | Статус |
|---|---|---|---|---|---|
| `ShareCodeGeneratorTest` | 4 | 0 | 0 | 0.034 с | **PASS** ✅ |
| `ShoppingRepositoryTest` | 7 | 0 | 0 | 0.026 с | **PASS** ✅ |
| **Разом** | **11** | **0** | **0** | **0.060 с** | **100% PASS** ✅ |

Факти звітів:
- `app/build/test-results/testDebugUnitTest/TEST-com.savoj.shoppinglist.ShareCodeGeneratorTest.xml`
- `app/build/test-results/testDebugUnitTest/TEST-com.savoj.shoppinglist.ShoppingRepositoryTest.xml`
- **Загальний результат:** `BUILD SUCCESSFUL in 1m 30s` (27 actionable tasks: 27 executed).

---

## 4. Збірка інсталяційного пакету (APK Build)

Виконано команду:
`.\gradlew.bat assembleDebug`

### Результати:
- **Шлях:** `app/build/outputs/apk/debug/app-debug.apk`
- **Розмір:** 24,414,628 байт (~23.28 МБ)
- **Час створення:** 2026-11-20 23:14:08
- **Статус компіляції:** `BUILD SUCCESSFUL in 28s` (38 actionable tasks: 3 executed, 35 up-to-date).

---

## 5. Верифікація на фізичному пристрої (Live Device Verification)

Перевірку проведено на підключеному фізичному смартфоні:
- **Серійний номер:** `RZCX326349A` (`adb devices`)
- **Інсталяція:** `adb install -r app/build/outputs/apk/debug/app-debug.apk` -> `Success`
- **Запуск:** `adb shell am start -n com.savoj.shoppinglist/.MainActivity` -> PID 7097 (активний, без крешів)

### Факти дампів інтерфейсу (`uiautomator dump`):
1. **Початковий стан (була активна польська мова):**
   - Дамп показав польські рядки: `Moje listy`, `Wspólna: DH56QN`, `Dołącz do listy`, `Ustawienia`, `Utwórz listę`.
2. **Перемикання на українську мову:**
   - Натиснуто пункт `🇺🇦 Українська` у налаштуваннях (`adb shell input tap 350 1740`).
   - Інтерфейс **миттєво динамічно змінився на українську мову** без перезапуску застосунку:
     * `Мої списки`
     * `Спільний: DH56QN`
     * `Спільний доступ / Запросити`
     * `0 з 2 куплено`
     * `Приєднатися до списку`
     * `Налаштування`
     * `Створити список`
3. **Перевірка холодного перезапуску (`attachBaseContext`):**
   - Виконано `adb shell am start -S -n com.savoj.shoppinglist/.MainActivity`.
   - Застосунок перезапустився з нуля, зберігши українську мову одразу при відкритті.
4. **Перемикання на англійську мову:**
   - Відкрито екран Налаштування (`Налаштування`, `Зовнішній вигляд`, `Мова застосунку`, `Про застосунок`).
   - Натиснуто `🇬🇧 English` (`adb shell input tap 400 1956`).
   - Інтерфейс **миттєво змінився на англійську мову**:
     * `My Lists`
     * `Shared: DH56QN`
     * `Share / Invite Friends`
     * `0 of 2 bought`
     * `Join Shared List`
     * `Settings`
     * `Create list`
5. **Фінальний стан:**
   - Повернуто українську мову для користувача.

- **Статус фізичної перевірки:** **PASS** ✅

---

## 6. Підсумковий висновок та рекомендації

Усі критерії DoD завдання TASK-020 виконані повністю:
1. Вирішено корінну причину затримки локалізації в Compose (`LocalContext provides localizedContext` + `key(languageSetting)` + `attachBaseContext`).
2. Створено та повністю наповнено каталог `values-uk`.
3. Усі 5 файлів `strings.xml` синхронізовані по 81 ключу без помилок форматування.
4. Додано системний вибір мови та окремі радіо-кнопки у `SettingsScreen.kt`.
5. 11 модульних тестів виконано на 100% успішно.
6. Збірка APK `assembleDebug` пройшла успішно.
7. Робота перемикача перевірена та підтверджена фактами на реальному фізичному пристрої.

**Вердикт QA:** **QA VERIFIED** 🟢 (VERDICT: PASSED)  
Гілка `task/011-fix-dynamic-language-switching` готова до злиття в `main`.
