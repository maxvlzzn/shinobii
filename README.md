# Shinobi Simulator — Kotlin Multiplatform

Симулятор синоби на Kotlin Multiplatform (Android) с Jetpack Compose Multiplatform.

## Возможности

- **Главное меню** — кнопки «Новая игра» и «Продолжить» (доступна только если есть сохранение)
- **Выбор клана** — Узумаки, Учиха, Хьюга (у каждого свои уникальные навыки)
- **Игровой цикл:**
  - Синоби начинает с 6 лет, максимум — 50 лет
  - Рулетка навыков (1–10 очков с разными шансами выпадения)
  - Прокачка рейтинга синоби (20–100, +5 за 1 очко)
  - Зелёная кнопка «+» для увеличения возраста (новый спин рулетки)
- **Дерево навыков клана** — уникальные техники для каждого клана
- **Дерево стихий** — общее для всех кланов: Огонь, Вода, Ветер, Молния
- **Автосохранение** — прогресс сохраняется в Supabase автоматически

## Шансы рулетки

| Очки | Шанс |
|------|------|
| 1    | 25%  |
| 2    | 20%  |
| 3    | 16%  |
| 4    | 13%  |
| 5    | 10%  |
| 6    | 7%   |
| 7    | 5%   |
| 8    | 2%   |
| 9    | 1%   |
| 10   | 1%   |

## Запуск в Android Studio

1. Откройте папку проекта в Android Studio (File → Open)
2. Дождитесь синхронизации Gradle
3. Нажмите Run (▶) для запуска на эмуляторе или устройстве

## Структура проекта

```
composeApp/
  build.gradle.kts
  src/
    commonMain/kotlin/com/shinobisim/
      App.kt                        — навигация между экранами
      logic/
        GameManager.kt              — игровая логика
        Roulette.kt                 — рулетка с шансами
      model/
        Clan.kt                     — 3 клана
        Shinobi.kt                  — модель персонажа
        SkillTree.kt                — деревья навыков
      network/
        HttpClientFactory.kt        — Ktor HTTP клиент
        SupabaseRepository.kt       — сохранения в Supabase
      ui/
        game/
          GameScreen.kt             — главный игровой экран
          ClanSkillTreeScreen.kt    — дерево клана
          ElementalSkillTreeScreen.kt — дерево стихий
        menu/
          MainMenuScreen.kt         — главное меню
          ClanSelectionScreen.kt    — выбор клана
        theme/                      — цвета, типографика, тема
    androidMain/
      AndroidManifest.xml
      kotlin/com/shinobisim/
        MainActivity.kt             — точка входа Android
        network/HttpClientFactory.android.kt
```

## Технологии

- Kotlin Multiplatform (Kotlin 1.9.22)
- Jetpack Compose Multiplatform 1.5.12
- Android Gradle Plugin 8.2.2
- Kotlin DSL (build.gradle.kts)
- Ktor 2.3.7 (HTTP клиент для Supabase REST API)
- kotlinx.coroutines 1.7.3
- Supabase (PostgreSQL — сохранения игры)
