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

## Етап 4: Реалізація MVP (Поточний етап)
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-007** | Ініціалізація Android-проєкту (Gradle, Compose, Room, навігація, ресурси 4 мов) | Android Dev | `In Progress` | `task/005-mvp-implementation` | Структура Gradle, Room, локалізація, компіляція |
| **TASK-008** | Сценарій 1: Створення, перейменування, видалення списків | Android Dev | `In Progress` | `task/005-mvp-implementation` | Списки, прогрес, скидання відміток |
| **TASK-009** | Сценарій 2: Додавання, редагування, видалення товарів (кількість, категорії) | Android Dev | `In Progress` | `task/005-mvp-implementation` | Категорії, сортування, операції над товарами |
| **TASK-010** | Сценарій 3: Позначення купленого, секція завершеного, повернення (Undo) | Android Dev | `In Progress` | `task/005-mvp-implementation` | Акордеон завершеного, чекбокси, Undo |
| **TASK-011** | Сценарій 4: Повторне використання списків, шаблони, часті товари, Multi-line paste | Android Dev | `In Progress` | `task/005-mvp-implementation` | Пакетне вставлення тексту, RecentItems |
| **TASK-012** | Сценарій 5: Мультимовність (налаштування мови, автовизначення, теми) | Android Dev | `In Progress` | `task/005-mvp-implementation` | Перемикання UK/EN/PL/RU, темна/світла теми |

---

## Етап 5: Незалежне QA-тестування
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-013** | Комплексна незалежна QA-перевірка функціоналу та локалізації | QA Agent | `Backlog` | - | `QA Verified` від окремого субагента, звіти перевірок |

---

## Етап 6: Підготовка до закритого тестування й ASO
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-014** | Підготовка метаданих Google Play (4 мови), скриншотів, Data Safety | ASO Agent | `Backlog` | - | Повний пакет для Play Console |
| **TASK-015** | План закритого тестування (вимоги 14/20 тестувальників) | Lead | `Backlog` | - | Документований план дій для Власника |

---

## Етап 7: Релізне рішення
| ID | Завдання | Виконавець | Статус | Гілка / PR | Критерій готовності |
|---|---|---|---|---|---|
| **TASK-016** | Підсумковий релізний пакет і перехід до публікації | Власник / Lead | `Backlog` | - | Рішення Власника |
