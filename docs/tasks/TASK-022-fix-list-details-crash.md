# КАРТКА ЗАВДАННЯ: TASK-022
## Усунення аварійного завершення (Crash) при вході в список покупок

---

- **ID завдання:** TASK-022
- **Назва:** Усунення IllegalStateException: No ActivityResultRegistryOwner при переході на ListDetailsScreen
- **Тип:** Critical Bugfix / Stability
- **Виконавці:** Android Developer, Independent QA Agent
- **Статус:** `In Progress`
- **Гілка:** `task/013-fix-list-details-crash`
- **Дата створення:** 2026-11-21

---

## 1. Опис проблеми та корінна причина за logcat
- **Симптом:** При натисканні на будь-який список на головному екрані додаток негайно вилітає (FATAL EXCEPTION).
- **Стек-трейс logcat:**
  ```
  java.lang.IllegalStateException: No ActivityResultRegistryOwner was provided via LocalActivityResultRegistryOwner
      at androidx.activity.compose.ActivityResultRegistryKt.rememberLauncherForActivityResult(ActivityResultRegistry.kt:93)
      at com.savoj.shoppinglist.ui.details.ListDetailsScreenKt.ListDetailsScreen(ListDetailsScreen.kt:121)
  ```
- **Корінна причина:**
  У завданні TASK-020 для динамічного перемикання мови було передано:
  `CompositionLocalProvider(LocalContext provides localizedContext)`.
  Оскільки `localizedContext` створено через `createConfigurationContext`, він є обгорткою `Context`, а не інстансом `ComponentActivity` (`ActivityResultRegistryOwner`).
  У Compose функція `rememberLauncherForActivityResult` (використана в `ListDetailsScreen` для голосового вводу) шукає `LocalActivityResultRegistryOwner`. За замовчуванням Compose намагається скастувати `LocalContext.current as? ActivityResultRegistryOwner`. Оскільки `localizedContext` не реалізує цей інтерфейс, `LocalActivityResultRegistryOwner.current` дорівнює `null`, що призводить до `IllegalStateException`.

---

## 2. Рішення
1. У `MainActivity.kt` явно надати `LocalActivityResultRegistryOwner provides this` всередині `CompositionLocalProvider`:
   ```kotlin
   CompositionLocalProvider(
       LocalConfiguration provides localizedContext.resources.configuration,
       LocalContext provides localizedContext,
       LocalActivityResultRegistryOwner provides this
   )
   ```
2. Обов'язкове тестування на реальному пристрої:
   - Запустити додаток на телефоні `RZCX326349A`.
   - Програмно виконати тап / відкриття списку `Screen.ListDetails`.
   - Перевірити `adb logcat -b crash` на повну відсутність збоїв (0 crash).
   - Зняти дамп UI і переконатися, що екран деталей відображається коректно.
3. Провести незалежну верифікацію окремим QA-субагентом з реальними доказами відкриття списку.

---

## 3. Критерії готовності (DoD)
- [ ] Додано `LocalActivityResultRegistryOwner provides this` у `MainActivity.kt`.
- [ ] Успішні модульні тести (`.\gradlew testDebugUnitTest`).
- [ ] Збірка APK (`.\gradlew assembleDebug`).
- [ ] Інсталяція на реальний телефон `RZCX326349A`.
- [ ] Фактичний перехід у список на телефоні без падіння (доказ у logcat).
- [ ] Незалежна верифікація окремим QA-субагентом.

---

## 4. Журнал виконання (Append-only)
- **2026-11-21 08:27:** Зафіксовано аварійне завершення з logcat при вході в список. Створено гілку `task/013-fix-list-details-crash`.
