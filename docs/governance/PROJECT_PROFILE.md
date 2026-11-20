# PROJECT PROFILE / ПРОФІЛЬ ПРОЄКТУ: «Список покупок» (Shopping List)

> Заповнює 🔧-параметри конституції (`CONSTITUTION.uk.md` / `CONSTITUTION.en.md`). Може лише заповнювати й **посилювати** правила, а не послаблювати.
> Версія: 1.0-draft · Дата: 2026-11-20 · Конституція: 2.0

---

## 1. Project / Проєкт
| Field / Поле | Value / Значення |
|---|---|
| Name / Назва | **«Список покупок»** (Shopping List) — робоча назва. Фінальна комерційна назва для Google Play: *не вирішено* (вимагає узгодження на Етапі 1 / ASO). |
| Short description / Короткий опис | Швидкий, простий і надійний Android-застосунок для складання, ведення та повторного використання списків покупок із шаблонами, частими товарами та офлайн-збереженням. |
| Project licence / Ліцензія проєкту | **Proprietary (All Rights Reserved) / Пропрієтарна ліцензія (Усі права захищено)**. Обрано за рішенням Власника від 2026-11-20 для комерційного застосунку з рекламною монетизацією у Google Play. Сирцевий код та активи захищені від несанкціонованого копіювання чи перевикористання. |
| Main branch / Головна гілка | `main` |
| Repository / Репозиторій | `https://github.com/SaVoJ13/day-planer` |

---

## 2. Owner / Власник
| Field / Поле | Value / Значення |
|---|---|
| Time for the project / Час на проєкт | *Не вирішено* (орієнтир: 1–2 години на тиждень для перегляду звітів і схвалення воріт; бережемо час за п. 4 Конституції). |
| Technical level / Технічний рівень | *Не вирішено* (за замовчуванням вважаємо «не програміст»: готові команди, жодного аналізу сирих логів, 1 дія на повідомлення). |
| Language of reports and chat / Мова звітів і спілкування | **Українська** (вимога Конституції). |
| Language of code, comments, commits / Мова коду, коментарів, комітів | Англійська (стандарт індустрії для Android-розробки) для коду, назв змінних та git-комітів; українська для документації governance, звітів та завдань. |
| Time zone / Часовий пояс | UTC+1 (CET) / UTC+2 (EET) — за локальним системним часом середовища. На підтвердження Власником. |

---

## 3. Autonomy level (constitution 3.4) / Рівень автономії
- **Level / Рівень:** Автономний режим виконання за прямим розпорядженням Власника від 2026-11-20 (Рішення [Р-006]): Агент 0 координує виконання етапів 1–5 без зупинки на очікування Власника, **за суворої умови обов'язкової верифікації кожного блоку робіт незалежним QA-субагентом**.
- **Who may give `QA Verified` / Хто може ставити `QA Verified`:**
  - Незалежний QA-субагент (окремий процес `invoke_subagent` із детальною перевіркою фактів, коду, логів та тестів) або безпосередньо Власник.
  - **Червона лінія №9:** Автор змін (включно з Агентом 0) категорично не має права ставити `QA Verified` на власну роботу.

---

## 4. Roles and areas / Ролі й зони
| Role / Роль | Folder / Папка | May touch / Може змінювати | May not touch / Не чіпає |
|---|---|---|---|
| Agent 0 (Керівник / Lead) | Увесь репозиторій | Планування, координація, governance-файли, архітектурний нагляд | Не ставить `QA Verified` на власний код, не порушує п. 3.2 Конституції |
| Product Researcher / Дослідник продукту | `docs/research/`, `docs/product/` | Звіти про конкурентів, дослідження ринку, чорновий PRD | Сирцевий код `app/`, конфігурацію агентів |
| UX/UI Designer | `docs/design/`, `app/src/main/res/` (плейсхолдери) | Сценарії, схеми екранів, дизайн-специфікації | Бізнес-логіку `app/src/main/java/` |
| Localization Agent | `docs/localization/`, `app/src/main/res/values*/strings.xml` | Таблиці локалізації (UK, RU, PL, EN), ресурси рядків | Архітектуру, бізнес-логіку коду |
| Android Architect / Dev | `app/`, `build.gradle.kts`, `settings.gradle.kts` | Код застосунку, Room БД, UI Compose, модульні тести | Governance без Агента 0, не ставить `QA Verified` на свій код |
| Monetization & Privacy Agent | `docs/monetization/`, `docs/privacy/` | Чернетки Privacy Policy, Data Safety, аналіз рекламних форматів | Не вмикає реальні SDK, не вставляє бойові ключі |
| Independent QA Agent | `app/src/test/`, `app/src/androidTest/`, `docs/qa/` | Тестові сценарії, автотести, QA-звіти, верифікація доказів | Не змінює виробничий код для проходження тестів |
| Play Store & ASO Agent | `docs/store/` | Описи для Google Play, метадані, ключові слова, скриншоти | Не публікує застосунок, не входить у консоль |

---

## 5. Technology and environment / Технології й середовище
- **Stack / Стек:**
  - Мова: Kotlin (актуальна стабільна версія).
  - UI: Jetpack Compose (Material Design 3).
  - Архітектура: Clean Architecture / MVVM / MVI з Android Architecture Components (ViewModel, StateFlow).
  - Локальна база даних: Room (SQLite) з міграціями.
  - Dependency Injection: Koin або Hilt (за рішенням технічного етапу; перевага Koin/manual DI для максимальної простоти й швидкості збірки).
  - Збірка: Gradle (Kotlin DSL).
- **Platforms / Платформи:** Android (minSdk: 24 (Android 7.0) або 26 (Android 8.0) — *на узгодження в PRD*; targetSdk: 35/36 відповідно до вимог Google Play 2026).
- **Owner's computer / Комп'ютер Власника:**
  - ОС: Windows 10/11 (виявлено x64 Build 26300).
  - Shell: PowerShell.
  - Встановлено: Android Studio (з вбудованою OpenJDK 25 за шляхом `C:\Program Files\Android\Android Studio\jbr`), Android SDK (API 34, 35, 36) у `C:\Users\cocro\AppData\Local\Android\Sdk`, platform-tools (adb).
  - Обмеження: не перевантажувати важкими процесами; запускати збірки послідовно.
- **Devices for testing / Пристрої для перевірки:**
  - Локальний Android Emulator / AVD (через `android` CLI або Android Studio).
  - Фізичний пристрій Власника: тестується виключно Власником за покроковою інструкцією.
- **Enabled modules / Підключені модулі:**
  - Android-додаток (offline-first).
  - Локалізація (4 мови: uk, ru, pl, en).
  - На початковому етапі модуль реклами та мережевий модуль **вимкнені**.

---

## 6. Licences and third-party components / Ліцензії й сторонні компоненти
- **Register file / Файл реєстру:** `docs/THIRD_PARTY.md`.
- **Accepted licences / Прийнятні ліцензії:** Apache-2.0, MIT, BSD-2-Clause, BSD-3-Clause.
- **Conditional (after separate check) / Умовно (після окремої перевірки):** LGPL, MPL-2.0 (вимагає оцінки динамічного зв'язування).
- **Forbidden / Заборонені:** GPL (якщо проєкт комерційний/пропрієтарний), AGPL, CC-BY-NC, комерційні платні бібліотеки без ліцензії Власника, бібліотеки без чіткої відкритої ліцензії.
- **Rule of verification / Правило перевірки:**
  - Перевірити першоджерело репозиторію кожної залежності.
  - Внести запис у `docs/THIRD_PARTY.md` (назва, версія, автор, ліцензія, посилання на репозиторій, призначення) **до** додавання у `build.gradle`.

---

## 7. Owner-only list (constitution 3.2) / Лише Власник
- **Accounts and consoles / Акаунти й консолі:** Google Play Console, Google Cloud Console, AdMob / AppLovin / інші рекламні кабінети, GitHub Secrets, платіжні профілі.
- **Paid services / Платні сервіси:** Будь-які платні API чи підписки — заборонено без прямої вказівки Власника.
- **Publishing channels / Канали публікації:** Публікація в Google Play (Alpha/Beta/Production) здійснюється виключно Власником або за його прямою командою.
- **Approved in advance / Затверджено наперед:** Жодних бойових рекламних SDK, жодних мережевих дозволів. MVP повністю офлайновий.
- **Phases and gates / Етапи й ворота:**
  1. **Етап 0 (Аудит і governance):** Гейт `Owner Approved` (затвердження профілю, governance, структури).
  2. **Етап 1 (Дослідження та PRD):** Гейт `Owner Approved` (затвердження продуктової концепції, фіч MVP, конкурентної стратегії).
  3. **Етап 2 (UX і локалізація):** Гейт `Owner Approved` (затвердження сценаріїв екранів, макетів та текстової бази 4 мовами).
  4. **Етап 3 (Архітектура й технічне рішення):** Гейт `Owner Approved` (затвердження техстеку, моделі Room, дозволів).
  5. **Етап 4 (Реалізація MVP):** Гейт `QA Verified` (незалежний QA-субагент) + зелений CI/тести.
  6. **Етап 5 (Комплексне тестування):** Гейт `Owner Approved` на перехід до закритого тестування.
  7. **Етап 6 (Закритий тест і підготовка ASO):** Підготовка метаданих Google Play і білду.
  8. **Етап 7 (Релізне рішення):** Остаточне рішення Власника про публікацію.

---

## 8. Reports and commands / Звіти й команди
| Purpose / Призначення | Command or file / Команда або файл |
|---|---|
| One Owner action / Одна дія Власника | Формат повідомлення «Дія для Власника» (1 крок, команда + очікувана відповідь) |
| Period report / Звіт за період / етап | Звіт у чаті + збереження в `docs/reports/` |
| Phase gate / Ворота етапу | Формальний звіт перевірки критеріїв переходу з вердиктом `Готовий до Owner Approved` |
| Session close / Закриття сесії | Оновлення `CURRENT_STATE.md`, `TASK_BOARD.md`, `SESSION_LOG.md` |
| Report template / Шаблон звіту | `docs/templates/OWNER_REPORT.md` |

---

## 9. Paths and checks / Шляхи й перевірки
| What / Що | Path / Шлях |
|---|---|
| Constitution and profile / Конституція й профіль | `docs/governance/CONSTITUTION.uk.md`, `docs/governance/CONSTITUTION.en.md`, `docs/governance/PROJECT_PROFILE.md` |
| Decision log / Журнал рішень | `docs/governance/DECISIONS.md` |
| Current state / Поточний стан | `docs/governance/CURRENT_STATE.md` |
| Task board / Дошка завдань | `docs/governance/TASK_BOARD.md` |
| Session log / Журнал сесій | `docs/governance/SESSION_LOG.md` |
| Lessons learned / Уроки | `docs/governance/LESSONS.md` |
| Owner inbox / Питання до Власника | `docs/governance/OWNER_INBOX.md` |
| Task cards / Картки завдань | `docs/tasks/TASK-<ID>.md` |
| Definition of Done | `docs/governance/DEFINITION_OF_DONE.md` |
| Third-party components registry | `docs/THIRD_PARTY.md` |
| Key terms for overwrite check (5.5) | `«Список покупок»`, `CONSTITUTION`, `PROJECT_PROFILE`, `TASK-`, `DECISION-` |
| Required CI checks / Обов'язкові перевірки CI | Unit tests (`./gradlew testDebugUnitTest`), Lint (`./gradlew lintDebug`), Build (`./gradlew assembleDebug`) |

---

## 10. Project-specific tightening and exceptions / Посилення й винятки проєкту
1. **Повна автономність офлайн у першому релізі (Zero Network):**
   - У маніфесті заборонено додавати дозвіл `android.permission.INTERNET` до окремого письмового рішення Власника щодо монетизації/аналітики.
   - Усі списки, категорії, налаштування та шаблони зберігаються виключно на пристрої (Room SQLite).
2. **Заборона самоверифікації (п. 2.1 Конституції):**
   - Жоден розробник чи лід не верифікує свій код. Лише незалежний QA-агент із наданням виводу тестів.
3. **Мовна дисципліна:**
   - Жодного хардкоду рядків у коді UI. Усі текстові елементи виключно через ресурси `strings.xml` із синхронним заповненням для 4 мов (uk, ru, pl, en).
   - Автоматичний переклад користувацьких назв товарів суворо заборонений (як вказано у вимогах Власника).
