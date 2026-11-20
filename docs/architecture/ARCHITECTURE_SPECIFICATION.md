# ТЕХНІЧНА АРХІТЕКТУРА ТА СХЕМА ДАНИХ (ANDROID NATIVE)

> Версія: 1.0 · Дата: 2026-11-20 · Роль: Android Architect · Статус: Готово до QA

---

## 1. Загальний огляд архітектури
Застосунок «Список покупок» будується за принципами **Clean Architecture** та **MVVM (Model-View-ViewModel)** з використанням сучасного Android-стеку:
- **Мова:** 100% Kotlin.
- **UI:** Jetpack Compose + Material Design 3 (декларативний UI, підтримка Dynamic Color та Dark Theme).
- **Сховище (Local Persistence):** Room SQLite (реактивні потоки через Kotlin `Flow`).
- **Керування станом:** `StateFlow` у `ViewModel` (Single Source of Truth, Unidirectional Data Flow).
- **Асинхронність:** Kotlin Coroutines (`Dispatchers.IO`, `Dispatchers.Main`).
- **Навігація:** Jetpack Navigation Compose (Type-Safe Navigation).

---

## 2. Структура пакетів (Package Structure)

```
com.savoj.shoppinglist/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/
│   │   │   ├── ShoppingListDao.kt
│   │   │   ├── ShoppingItemDao.kt
│   │   │   ├── CategoryDao.kt
│   │   │   └── RecentItemDao.kt
│   │   └── entity/
│   │       ├── ShoppingListEntity.kt
│   │       ├── ShoppingItemEntity.kt
│   │       ├── CategoryEntity.kt
│   │       └── RecentItemEntity.kt
│   ├── repository/
│   │   ├── ShoppingRepositoryImpl.kt
│   │   └── SettingsRepositoryImpl.kt
│   └── preferences/
│       └── AppPreferences.kt (Theme & Language storage)
├── domain/
│   ├── model/
│   │   ├── ShoppingList.kt
│   │   ├── ShoppingItem.kt
│   │   ├── Category.kt
│   │   └── RecentItem.kt
│   └── repository/
│       ├── ShoppingRepository.kt
│       └── SettingsRepository.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── navigation/
│   │   └── Screen.kt
│   ├── lists/
│   │   ├── ListsScreen.kt
│   │   ├── ListsViewModel.kt
│   │   └── components/
│   ├── details/
│   │   ├── ListDetailsScreen.kt
│   │   ├── ListDetailsViewModel.kt
│   │   └── components/
│   │       ├── QuickAddBar.kt
│   │       ├── MultiLinePasteDialog.kt
│   │       └── RecentItemsSheet.kt
│   └── settings/
│       ├── SettingsScreen.kt
│       └── SettingsViewModel.kt
└── MainActivity.kt
```

---

## 3. Схема бази даних Room (SQLite)

### 3.1. ShoppingListEntity
Таблиця `shopping_lists`:
- `id: Long` (Primary Key, autoGenerate = true)
- `name: String` (Назва списку)
- `color_hex: String` (Колір картки/маркера)
- `created_at: Long` (Timestamp створення)
- `is_archived: Boolean` (Статус архівації, default = false)
- `order_index: Int` (Індекс сортування)

### 3.2. ShoppingItemEntity
Таблиця `shopping_items`:
- `id: Long` (Primary Key, autoGenerate = true)
- `list_id: Long` (Foreign Key -> `shopping_lists.id`, onDelete = CASCADE)
- `name: String` (Назва товару)
- `quantity: Double?` (Кількість, null якщо не вказано)
- `unit: String?` (Одиниця виміру: pcs, kg, g, l, pack)
- `category_id: Long?` (Foreign Key -> `categories.id`, onDelete = SET_NULL)
- `is_completed: Boolean` (Куплено / Не куплено, default = false)
- `created_at: Long` (Timestamp)
- `order_index: Int` (Індекс порядку)
- **Індекси:** `Index("list_id")`, `Index("category_id")` для швидких вибірок.

### 3.3. CategoryEntity
Таблиця `categories`:
- `id: Long` (Primary Key)
- `name_key: String` (Ключ ресурсу локалізації, напр., `category_dairy`)
- `icon_name: String` (Ідентифікатор іконки)
- `color_hex: String` (Колір категорії)
- `order_index: Int`

### 3.4. RecentItemEntity
Таблиця `recent_items`:
- `id: Long` (Primary Key, autoGenerate = true)
- `name: String` (Назва товару, COLLATE NOCASE, Unique index)
- `category_id: Long?`
- `last_used_at: Long`
- `use_count: Int` (Лічильник популярності)

---

## 4. Політика безпеки та дозволів (Zero Network Audit)
- У маніфесті `AndroidManifest.xml` для MVP **відсутній будь-який дозвіл**:
  - ❌ `android.permission.INTERNET` — НЕ додається.
  - ❌ `android.permission.ACCESS_NETWORK_STATE` — НЕ додається.
  - ❌ Жодних небезпечних (Runtime) дозволів.
- Застосунок не має технічної можливості виходу в мережу, що гарантує 100% збереження приватності користувача.

---

## 5. Стратегія тестування (Testing Strategy)
1. **Unit-тести Room DAO (In-Memory Database):**
   - Перевірка створення списку, додавання товарів, каскадного видалення.
   - Перевірка дії «Скинути всі відмітки» (`resetAllItemsAsIncomplete`).
   - Перевірка парсера мультирядкового вставлення (очищення префіксів `- `, `* `, `1. `).
2. **Unit-тести ViewModel:**
   - Перевірка реактивного оновлення `StateFlow` при додаванні/видаленні товарів.
   - Перевірка перемикання локалі та теми.
