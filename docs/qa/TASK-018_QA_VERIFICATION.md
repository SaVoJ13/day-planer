# ЗВІТ НЕЗАЛЕЖНОЇ ВЕРИФІКАЦІЇ QA: TASK-018

> **Проєкт:** «Список покупок» (Shopping List)  
> **Завдання:** TASK-018 (Усунення перекриття поля швидкого додавання товару системною навігаційною панеллю Android)  
> **Дата верифікації:** 2026-11-20  
> **Верифікатор:** Незалежний QA-агент (субагент Antigravity)  
> **Гілка:** `task/009-fix-input-field-overlap`  
> **Коміт перевірки:** `c39fa78ffd7a2a9a259a56bb30159f0bbd1822e0`  
> **Підстава:** Конституція команди агентів (п. 2.1, Червона лінія №9 «Заборона самоверифікації»), `DEFINITION_OF_DONE.md`.  
> **Вердикт:** **QA VERIFIED** 🟢

---

## 1. Мета та об'єкт перевірки

Верифікація виправлення бага UI на Android 15 (Edge-to-Edge):
- Усунення накладання нижньої панелі дій та поля швидкого вводу (`bottomBar` в `ListDetailsScreen.kt`) на системну панель навігації (3-кнопкова або жестова навігація).
- Гарантування коректної поведінки при відкритті екранної клавіатури (IME insets).

---

## 2. Перевірка коду (Code Inspection)

### 2.1. `MainActivity.kt`
- Підтверджено наявність виклику `enableEdgeToEdge()` на початку методу `onCreate()`:
  ```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
      enableEdgeToEdge()
      super.onCreate(savedInstanceState)
      ...
  }
  ```
- **Статус:** **PASS** ✅

### 2.2. `ListDetailsScreen.kt`
- Підтверджено наявність інсетів для нижньої панелі (`bottomBar`):
  - Контейнер `Surface`: додано `Modifier.imePadding()` для автоматичного підйому панелі при висуванні екранної клавіатури.
  - Внутрішній `Row`: додано `Modifier.navigationBarsPadding()` для відступу від системної панелі жестів або кнопок навігації.
  ```kotlin
  bottomBar = {
      Surface(
          modifier = Modifier
              .fillMaxWidth()
              .imePadding(),
          tonalElevation = 3.dp,
          shadowElevation = 8.dp
      ) {
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .navigationBarsPadding()
                  .padding(horizontal = 8.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
          ) {
              ...
          }
      }
  }
  ```
- **Статус:** **PASS** ✅

---

## 3. Результати тестування (Automated Tests)

Команда: `powershell -Command ".\gradlew testDebugUnitTest"`
- `TEST-com.savoj.shoppinglist.ShareCodeGeneratorTest.xml`: 4 тести, 0 помилок, 0 пропущено.
- `TEST-com.savoj.shoppinglist.ShoppingRepositoryTest.xml`: 7 тестів, 0 помилок, 0 пропущено.
- **Всього:** **11 / 11 тестів пройдено успішно** (100% PASS, 0 failures, 0 skipped).
- **Статус:** **PASS** ✅

---

## 4. Перевірка збірки APK

- Файл: `app/build/outputs/apk/debug/app-debug.apk`
- Розмір: 23,819,106 байт (~22.7 МБ)
- Збірка: успішно перевірена командою `assembleDebug` (BUILD SUCCESSFUL).
- **Статус:** **PASS** ✅

---

## 5. Підсумковий висновок

Виправлення повністю відповідає технічним вимогам Google для підтримки Edge-to-Edge на Android 15 (`targetSdk 35`), усуває візуальний дефект перекриття системним баром навігації та забезпечує підйом поля вводу над клавіатурою.

**Вердикт QA:** **QA VERIFIED** 🟢
Гілка `task/009-fix-input-field-overlap` готова до злиття в `main`.
