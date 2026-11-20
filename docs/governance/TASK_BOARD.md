# TASK BOARD / ДОШКА ЗАВДАНЬ

> Оновлюється Агентом 0 на початку й наприкінці кожної сесії та при зміні статусів завдань.
> Статуси: `Backlog` → `In Progress` → `Review / QA` → `Done`

---

## Етап 0: Аудит і governance (Поточний етап)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-000** | Аудит репозиторію, середовища та розгортання Governance-файлів | Агент 0 | **Done** | `main` | Створено файли пам'яті, підготовлено звіт аудиту, отримано `Owner Approved` |

---

## Етап 1: Дослідження та PRD (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-001** | Дослідження конкурентів списків покупок у Google Play | Product Researcher | **Done** | `main` | Конкурентна таблиця, аналіз болів і відгуків ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE1_QA_VERIFICATION.md)) |
| **TASK-002** | Формування детального PRD для MVP | Product Researcher | **Done** | `main` | Затверджені межі MVP, 4 мови, структура сутностей ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE1_QA_VERIFICATION.md)) |

---

## Етап 2: UX і локалізація (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-003** | Проєктування інтерфейсу (екран списків, деталі, швидке додавання, налаштування) | UX/UI Agent | **Done** | `main` | Повні макети, адаптивність, темна тема ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE2_QA_VERIFICATION.md)) |
| **TASK-004** | Базова таблиця рядків локалізації (UK, RU, PL, EN) | Localization Agent | **Done** | `main` | Всі рядки без хардкоду, перевірені плюрали ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE2_QA_VERIFICATION.md)) |

---

## Етап 3: Архітектура й дозволи (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-005** | Технічне проєктування (Room entities, DAO, MVVM architecture, zero-network audit) | Android Architect | **Done** | `main` | ADR, схема БД, план тестів ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE3_QA_VERIFICATION.md)) |
| **TASK-006** | Дослідження монетизації, Data Safety та Privacy Policy | Monetization Agent | **Done** | `main` | Специфікація безпеки, чернетка Privacy Policy ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE3_QA_VERIFICATION.md)) |

---

## Етап 4: Реалізація MVP (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-007** | Ініціалізація Android-проєкту (Gradle, Compose, Room, навігація, ресурси 4 мов) | Android Dev | **Done** | `main` | Структура Gradle, Room, локалізація, компіляція ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE4_5_QA_VERIFICATION.md)) |
| **TASK-008** | Сценарій 1: Створення, перейменування, видалення списків | Android Dev | **Done** | `main` | Списки, прогрес, скидання відміток ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE4_5_QA_VERIFICATION.md)) |
| **TASK-009** | Сценарій 2: Додавання, редагування, видалення товарів (кількість, категорії) | Android Dev | **Done** | `main` | Категорії, сортування, операції над товарами ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE4_5_QA_VERIFICATION.md)) |
| **TASK-010** | Сценарій 3: Позначення купленого, секція завершеного, повернення (Undo) | Android Dev | **Done** | `main` | Акордеон завершеного, чекбокси, Undo ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE4_5_QA_VERIFICATION.md)) |
| **TASK-011** | Сценарій 4: Повторне використання списків, шаблони, часті товари, Multi-line paste | Android Dev | **Done** | `main` | Пакетне вставлення тексту, RecentItems ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE4_5_QA_VERIFICATION.md)) |
| **TASK-012** | Сценарій 5: Мультимовність (налаштування мови, автовизначення, теми) | Android Dev | **Done** | `main` | Перемикання UK/EN/PL/RU, темна/світла теми ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE4_5_QA_VERIFICATION.md)) |

---

## Етап 5: Незалежне QA-тестування (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-013** | Комплексна незалежна QA-перевірка функціоналу та локалізації | QA Agent | **Done** | `main` | `QA Verified` від окремого субагента ([Звіт QA](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE4_5_QA_VERIFICATION.md)) |

---

## Етап 6: Підготовка до закритого тестування й ASO (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-014** | Підготовка метаданих Google Play (4 мови), скриншотів, Data Safety | ASO Agent | **Done** | `main` | Повний пакет для Play Console ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE6_QA_VERIFICATION.md)) |
| **TASK-015** | План закритого тестування (вимоги 14 днів / 20 тестувальників) | Lead | **Done** | `main` | Документований план дій для Власника ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE6_QA_VERIFICATION.md)) |

---

## Етап 7: Релізне рішення (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-016** | Підсумковий релізний пакет і перехід до публікації | Власник / Lead | **Done** | `main` | Підсумковий релізний звіт ([FINAL_RELEASE_REPORT.md](file:///c:/Users/cocro/Downloads/day%20planer/docs/reports/FINAL_RELEASE_REPORT.md)) |

---

## Етап 8: Спільні групові списки покупок (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-017** | Реалізація спільних групових списків покупок (Firebase Firestore, Anonymous Auth, 6-значний код, QR/Share, реалтайм-синхронізація) | Android Dev / QA | **Done** | `main` | Інтеграція Firebase, Room міграція v2, UI приєднання та спільного доступу ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/STAGE8_QA_VERIFICATION.md)) |

---

## Виправлення та UX-покращення (Завершено)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-018** | Виправлення перекриття поля вводу системною навігаційною панеллю Android 15 (`enableEdgeToEdge`, `navigationBarsPadding`, `imePadding`) | Android Dev / QA | **Done** | `main` | Панель вводу повністю піднята над панеллю навігації, 11/11 тестів ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/TASK-018_QA_VERIFICATION.md)) |
| **TASK-019** | Виправлення генерації коду (local-first у Room) та покращення UX запрошення до спільних покупок (кнопка шерингу на картці, розділення текстів приєднання та запрошення) | Android Dev / QA | **Done** | `main` | Код генерується миттєво без мережевих затримок, кнопка Share та меню на картці, 11/11 тестів ([QA Verified](file:///c:/Users/cocro/Downloads/day%20planer/docs/qa/TASK-019_QA_VERIFICATION.md)) |
