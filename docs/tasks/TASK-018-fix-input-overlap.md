# КАРТКА ЗАВДАННЯ: TASK-018
## Виправлення перекриття поля вводу системною навігаційною панеллю (Edge-to-Edge Navigation Bar Insets Fix)

---

- **ID завдання:** TASK-018
- **Назва:** Усунення перекриття поля швидкого додавання товару в `ListDetailsScreen`
- **Тип:** Bugfix (UI / Accessibility)
- **Виконавці:** Android Developer, Independent QA Agent
- **Статус:** `In Progress`
- **Гілка:** `task/009-fix-input-field-overlap`
- **Дата створення:** 2026-11-20

---

## 1. Опис проблеми та причина
- **Симптом:** Користувач надіслав скриншот екрана покупок (`media_1795172971721_562dd1ea.png`), де видно, що поле вводу «Додати товар...» та нижня панель дій майже повністю обрізані та перекриваються нижньою гранню екрана / системною панеллю навігації Android.
- **Причина:**
  1. На Android 15 (`targetSdk 35`) увімкнено примусовий Edge-to-Edge режим.
  2. У `ListDetailsScreen.kt` компонент `bottomBar` у `Scaffold` не враховував відступи системної панелі навігації (`navigationBarsPadding()`) та екранної клавіатури (`imePadding()`).
  3. У `MainActivity.kt` був відсутній виклик `enableEdgeToEdge()`.
- **Рішення:**
  1. Додано `enableEdgeToEdge()` в `MainActivity.onCreate()`.
  2. Додано `Modifier.imePadding()` на рівні контейнера `Surface` нижньої панелі для коректного підйому при відкритті клавіатури.
  3. Додано `Modifier.navigationBarsPadding()` на рівні внутрішнього рядка `Row` для гарантування повного підняття поля вводу та кнопок над системною панеллю жестів/кнопок Android.

---

## 2. Критерії готовності (DoD)
- [x] Додано `enableEdgeToEdge()` у `MainActivity.kt`.
- [x] Додано `navigationBarsPadding()` та `imePadding()` у `ListDetailsScreen.kt`.
- [x] Успішне модульне тестування (11/11 тестів).
- [x] Збірка оновленого APK.
- [ ] Встановлення на фізичний пристрій та перевірка відсутності перекриття.
- [x] Незалежна верифікація окремим QA-субагентом.

---

## 3. Журнал виконання (Append-only)
- **2026-11-20 12:11:** Створено гілку `task/009-fix-input-field-overlap`. Внесено виправлення в `MainActivity.kt` та `ListDetailsScreen.kt`.
- **2026-11-20 12:16:** Проведено незалежну QA-верифікацію (звіт `docs/qa/TASK-018_QA_VERIFICATION.md`). Модульні тести: 11/11 PASS. APK актуальний (`app-debug.apk`). Вердикт: QA Verified 🟢.
