# THIRD-PARTY COMPONENTS REGISTER / РЕЄСТР СТОРОННІХ КОМПОНЕНТІВ

> Реєстр усіх зовнішніх бібліотек, плагінів та залежностей проєкту «Список покупок».
> Згідно з п. 3.1 Конституції та `PROJECT_PROFILE.md`, будь-яка бібліотека додається сюди **до** внесення в конфігурацію збірки.

---

## 1. Політика допустимих ліцензій
- **Дозволені:** Apache-2.0, MIT, BSD-2-Clause, BSD-3-Clause.
- **Умовно (потрібна оцінка):** LGPL, MPL-2.0.
- **Заборонені:** GPL, AGPL, CC-BY-NC, пропрієтарні платні без ліцензії Власника, залежності без ліцензії.

---

## 2. Перелік компонентів

| Компонент / Бібліотека | Версія | Автор / Організація | Ліцензія | Посилання на репозиторій | Призначення | Статус перевірки |
|---|---|---|---|---|---|---|
| `androidx.core:core-ktx` | 1.15.0 | Google / AndroidX | Apache-2.0 | https://android.googlesource.com/platform/frameworks/support | Базові Kotlin KTX розширення | Перевірено ✅ |
| `androidx.lifecycle:*` | 2.8.7 | Google / AndroidX | Apache-2.0 | https://android.googlesource.com/platform/frameworks/support | ViewModel, Lifecycle, StateFlow | Перевірено ✅ |
| `androidx.activity:activity-compose` | 1.9.3 | Google / AndroidX | Apache-2.0 | https://android.googlesource.com/platform/frameworks/support | Інтеграція Compose з Activity | Перевірено ✅ |
| `androidx.compose:*` | 2024.11.00 BOM | Google / AndroidX | Apache-2.0 | https://android.googlesource.com/platform/frameworks/support | Декларативний UI (Material 3) | Перевірено ✅ |
| `androidx.navigation:navigation-compose` | 2.8.4 | Google / AndroidX | Apache-2.0 | https://android.googlesource.com/platform/frameworks/support | Навігація в Compose | Перевірено ✅ |
| `androidx.room:*` | 2.6.1 | Google / AndroidX | Apache-2.0 | https://android.googlesource.com/platform/frameworks/support | Локальна SQLite БД | Перевірено ✅ |
| `kotlinx.coroutines:*` | 1.9.0 | JetBrains | Apache-2.0 | https://github.com/Kotlin/kotlinx.coroutines | Асинхронні операції та Flow | Перевірено ✅ |
| `junit:junit` | 4.13.2 | JUnit Team | EPL-1.0 | https://github.com/junit-team/junit4 | Модульне тестування (test scope) | Перевірено ✅ |
