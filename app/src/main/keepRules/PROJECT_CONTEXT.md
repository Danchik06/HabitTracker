# HABIT TRACKER — БАЗА ЗНАНИЙ ПРОЕКТА

**Құжат мақсаты:** Жобаның барлық маңызды ақпаратын, архитектурасын, қабылданған шешімдерін және даму барысын сақтау. Бұл құжат жаңа чатқа немесе жаңа ChatGPT Project-ке көшкенде негізгі контекст ретінде пайдаланылады.

**Соңғы жаңарту:** 04.11.2026

---

# 1. ЖОБА ТУРАЛЫ ЖАЛПЫ АҚПАРАТ

## 1.1. Жоба атауы

Habit Tracker — Android қосымшасы.

## 1.2. Жобаның мақсаты

Пайдаланушыларға күнделікті пайдалы әдеттерді қалыптастыруға, оларды жүйелі түрде орындауға және өз прогресін статистика арқылы бақылауға мүмкіндік беретін Android қосымшасын әзірлеу.

## 1.3. Платформа және технологиялар

| Санат | Технология |
|---|---|
| Platform | Android |
| Programming Language | Kotlin |
| Minimum API | 24+ (Android 7.0) |
| UI Framework | Jetpack Compose |
| Design System | Material 3 |
| Architecture | MVVM + Clean Architecture |
| Database | Room Database (SQLite) |
| Dependency Injection | Hilt (Dagger) |
| Async Operations | Kotlin Coroutines |
| Reactive Data | Flow / StateFlow |
| Background Tasks | WorkManager |
| Navigation | Navigation Compose |

## 1.4. Негізгі функционалдық талаптар

### Habit Management (CRUD)

1. Әдет қосу:
    - Title — міндетті атауы.
    - Description — сипаттамасы.
    - Frequency — күнделікті немесе аптаның белгілі күндері.
    - Target Count — күніне орындалу мақсаты.
    - Category, Color, Icon.
    - Reminder Time.

2. Әдеттер тізімін көру:
    - Бүгінгі күнге жоспарланған әдеттер.
    - Орындалған және орындалмаған әдеттерді көрсету.

3. Әдетті өңдеу:
    - Атауы, уақыты, жиілігі және басқа параметрлері.
    - Бүгінгі күн үшін орындалды деп белгілеу.

4. Әдетті өшіру:
    - Әдетті және оның орындалу тарихын жою.
    - Растау диалогы.

### Analytics & Streaks

- Current Streak — үздіксіз орындалған күндер саны.
- Best Streak — ең ұзақ орындалған кезең.
- Weekly / Monthly Calendar.
- Heatmap.
- Completion Rate — орындалу пайызы.

### Notifications

- Белгіленген уақытта хабарлама жіберу.
- Хабарламадан әдетті орындалды деп белгілеу.

---

# 2. АРХИТЕКТУРА

## 2.1. Clean Architecture

Жоба үш негізгі қабатқа бөлінеді:

1. Data Layer — деректерді сақтау және алу.
2. Domain Layer — бизнес-логика.
3. Presentation Layer — пайдаланушы интерфейсі.

### Data Layer

Міндеті:
- Room Database арқылы деректерді сақтау.
- DAO арқылы CRUD операцияларын орындау.
- Repository реализациясын қамтамасыз ету.
- Entity мен Domain Model арасындағы түрлендіру.

Ұсынылатын құрылым:

```text
data/
├── local/
│   ├── HabitEntity.kt
│   ├── HabitLogEntity.kt
│   ├── HabitDao.kt
│   ├── HabitLogDao.kt
│   ├── AppDatabase.kt
│   └── Converters.kt
│
├── mapper/
│   ├── HabitMapper.kt
│   └── HabitLogMapper.kt
│
└── repository/
    ├── HabitRepositoryImpl.kt
    └── HabitLogRepositoryImpl.kt
```

### Domain Layer

Міндеті:
- Бизнес-логиканы басқару.
- Use Case арқылы операцияларды ұйымдастыру.
- Data Layer-ден тәуелсіз болу.

Ұсынылатын құрылым:

```text
domain/
├── model/
│   ├── Habit.kt
│   └── HabitLog.kt
│
├── repository/
│   ├── HabitRepository.kt
│   └── HabitLogRepository.kt
│
└── usecase/
    ├── GetAllHabitsUseCase.kt
    ├── AddHabitUseCase.kt
    ├── UpdateHabitUseCase.kt
    ├── DeleteHabitUseCase.kt
    ├── CompleteHabitUseCase.kt
    └── GetHabitHistoryUseCase.kt
```

### Presentation Layer

Міндеті:
- UI көрсету.
- ViewModel арқылы бизнес-логикамен байланысу.
- UI State және Events басқару.

Ұсынылатын құрылым:

```text
ui/
├── theme/
├── home/
│   ├── HomeScreen.kt
│   ├── HomeViewModel.kt
│   └── HomeUiState.kt
│
├── add_edit/
│   ├── AddEditScreen.kt
│   ├── AddEditViewModel.kt
│   └── AddEditUiState.kt
│
├── analytics/
│   ├── AnalyticsScreen.kt
│   ├── AnalyticsViewModel.kt
│   └── AnalyticsUiState.kt
│
└── components/
```

---

# 3. ДЕРЕКТЕР БАЗАСЫНЫҢ КОНЦЕПЦИЯСЫ

## 3.1. HabitEntity

Негізгі әдеттер кестесі.

| Өріс | Тип | Түсіндірме |
|---|---|---|
| id | Long | Primary Key, autoGenerate |
| title | String | Әдет атауы |
| description | String? | Сипаттамасы |
| targetDays | List<String> | Жоспарланған апта күндері |
| targetFrequency | Int | Күніне орындалу саны |
| reminderTime | LocalTime? | Еске салу уақыты |
| colorHex | String | Әдеттің түсі |
| createdAt | LocalDate | Құрылған күні |

Room `List<String>` және `LocalTime` сияқты типтерді сақтау үшін TypeConverter қажет.

## 3.2. HabitLog

Әдеттердің орындалу тарихы.

| Өріс | Тип | Түсіндірме |
|---|---|---|
| id | Long | Primary Key |
| habitId | Long | HabitEntity Foreign Key |
| date | LocalDate | Орындалған күн |
| completedCount | Int | Орындалу саны |
| isCompleted | Boolean | Толық орындалды ма |

Бір Habit бірнеше HabitLog жазбасымен байланысады (One-to-Many).

**Маңызды архитектуралық шешім:** HabitEntity ішіндегі isCompleted өрісі күнделікті орындалу тарихын сақтауға жеткіліксіз. Орындалу тарихы HabitLog арқылы жүргізілуі керек.

---

# 4. ҚАЗІРГІ ЖОБА ЖАҒДАЙЫ

## 4.1. Аяқталған бірінші кезең

**Phase 1 — Project Planning**

- [x] Жоба тақырыбы мен мақсаты анықталды.
- [x] Функционалдық талаптар жазылды.
- [x] Технологиялық стек таңдалды.
- [x] MVVM + Clean Architecture таңдалды.
- [x] Деректер моделінің бастапқы жобасы жасалды.
- [x] Жоба архитектурасы жоспарланды.

Бірінші кезеңнің техникалық құжаттамасы Word форматында сақталған.

## 4.2. Екінші кезең — Data Layer

Қазіргі жұмыс кезеңі.

Төмендегі файлдар пайдаланушының мәліметі бойынша жасалған:

- [x] HabitEntity.kt
- [x] HabitDao.kt
- [x] AppDatabase.kt
- [x] HabitRepositoryImpl.kt

Төмендегі жұмыстардың толық тексерілгені әзірше расталмаған:

- [ ] TypeConverter тексеру.
- [ ] DAO CRUD операцияларын тестілеу.
- [ ] Repository операцияларын тестілеу.
- [ ] HabitLogEntity және HabitLogDao қосу.
- [ ] Entity мен Domain Model арасындағы Mapper.
- [ ] Database Migration стратегиясы.

**Ескерту:** Файлдардың жасалғаны олардың дұрыс жұмыс істейтінін білдірмейді. Тест нәтижелері алынғаннан кейін ғана тиісті тапсырмалар аяқталды деп белгіленеді.

## 4.3. Соңғы жұмыс нүктесі

Соңғы талқыланған тапсырма:

**Room Database арқылы CRUD операцияларын Android Studio-да тестілеу.**

Тестілеудің төрт негізгі операциясы:

1. Create — жаңа әдет қосу.
2. Read — барлық әдеттерді оқу.
3. Update — әдеттің деректерін жаңарту.
4. Delete — әдетті өшіру.

Бұған дейін Android Instrumented Test тәсілі ұсынылды. `HabitDaoTest.kt` файлын жасау және тестілеу алгоритмі көрсетілді.

Алайда тесттің нақты сәтті орындалғаны әлі расталмаған.

Бұрын ұсынылған тест коды мысал ретінде берілген. Оны нақты жобаға бейімдеу үшін HabitEntity конструкторы, DAO әдістері, package атаулары және тәуелділіктер тексерілуі керек.

---

# 5. ЖОБАНЫ ДАМЫТУ ROADMAP

## Phase 1 — Project Planning
Мәртебесі: аяқталған.

## Phase 2 — Data Layer
Мәртебесі: аяқталу үстінде.

- [ ] TypeConverter тексеру.
- [ ] CRUD Instrumented Tests.
- [ ] Repository Tests.
- [ ] HabitLogEntity.
- [ ] HabitLogDao.
- [ ] Mapper.
- [ ] Database Migration жоспары.

Нәтиже: толық тексерілген деректер қабаты.

## Phase 3 — Domain Layer

- [ ] Habit Domain Model.
- [ ] HabitLog Domain Model.
- [ ] Repository интерфейстерін тексеру.
- [ ] GetAllHabitsUseCase.
- [ ] AddHabitUseCase.
- [ ] UpdateHabitUseCase.
- [ ] DeleteHabitUseCase.
- [ ] CompleteHabitUseCase.
- [ ] GetHabitHistoryUseCase.
- [ ] Unit Tests.

Нәтиже: бизнес-логика дайын.

## Phase 4 — Dependency Injection

- [ ] Hilt конфигурациясы.
- [ ] Application класын дайындау.
- [ ] Database Module.
- [ ] DAO Provider.
- [ ] Repository Binding.
- [ ] ViewModel интеграциясы.

Нәтиже: тәуелділіктер автоматты басқарылады.

## Phase 5 — Presentation Layer

- [ ] HomeViewModel.
- [ ] HomeUiState.
- [ ] AddEditViewModel.
- [ ] AddEditUiState.
- [ ] StateFlow.
- [ ] Loading / Success / Error.
- [ ] Event Handling.
- [ ] ViewModel Tests.

Нәтиже: UI мен Domain Layer байланысады.

## Phase 6 — UI Development

- [ ] Material 3 Theme.
- [ ] Home Screen.
- [ ] Habit Card.
- [ ] Add Habit Screen.
- [ ] Edit Habit Screen.
- [ ] Delete Confirmation Dialog.
- [ ] Form Validation.
- [ ] Navigation Compose.
- [ ] Analytics Screen.
- [ ] ViewModel Integration.

Нәтиже: қолданбаның негізгі интерфейсі дайын.

## Phase 7 — Notifications

- [ ] WorkManager.
- [ ] ReminderWorker.
- [ ] Notification Channel.
- [ ] Notification Permission.
- [ ] Reminder Scheduling.
- [ ] Notification Actions.
- [ ] Reminder Cancellation.
- [ ] Қажет болса AlarmManager.

Нәтиже: еске салу жүйесі жұмыс істейді.

## Phase 8 — Analytics

- [ ] Current Streak.
- [ ] Best Streak.
- [ ] Completion Rate.
- [ ] Weekly Statistics.
- [ ] Monthly Statistics.
- [ ] Calendar / Heatmap.
- [ ] Statistics Tests.

Нәтиже: прогресс пен статистика дайын.

## Phase 9 — Testing & Release

- [ ] Unit Testing.
- [ ] Integration Testing.
- [ ] UI Testing.
- [ ] Database Testing.
- [ ] Migration Testing.
- [ ] Error Handling.
- [ ] Performance Testing.
- [ ] App Icon.
- [ ] Version Configuration.
- [ ] Release APK.
- [ ] AAB (қажет болса).
- [ ] Final Documentation.

Нәтиже: толық жұмыс істейтін Android қосымшасы.

---

# 6. ЖҰМЫС ІСТЕУ ЕРЕЖЕЛЕРІ

## 6.1. Негізгі принцип

Пайдаланушы кодты өзі жазып, Android әзірлеуді тәжірибе арқылы меңгеруі керек.

ИИ-дің міндеті — дайын жобаны жасап беру емес, пайдаланушыны жобаны өз бетімен жасай алатын деңгейге жеткізу.

## 6.2. Тапсырма беру форматы

Әр тапсырма келесі құрылымда беріледі:

1. Task — не істеу керек?
2. Purpose — не үшін қажет?
3. Location — қай папкада және қандай файл жасау керек?
4. Steps — қалай орындау керек?
5. Expected Result — қандай нәтиже күтіледі?
6. Validation — қалай тексеріледі?
7. Common Mistakes — қандай қателер болуы мүмкін?

Бір уақытта тым көп тапсырма бермеу керек.

## 6.3. Кодпен жұмыс

- Пайдаланушы кодты өзі жазады.
- ИИ кодты тек пайдаланушы арнайы сұрағанда толық көрсетеді.
- Кодты бірден бермей, алдымен оның логикасын түсіндіреді.
- Қате шыққанда тек түзетуді емес, қатенің себебін де түсіндіреді.
- Пайдаланушы жіберген нақты кодты талдайды.
- Жобаның бар архитектурасын себепсіз өзгертпейді.

## 6.4. Кезеңдерді аяқтау

Кезең аяқталды деп есептеу үшін:

- Негізгі тапсырмалар орындалуы керек.
- Код компиляциядан өтуі керек.
- Қажетті тесттер орындалуы керек.
- Нәтиже тексерілуі керек.
- PROGRESS.md жаңартылуы керек.
- Git commit жасалуы керек.

---

# 7. ҚҰЖАТТАМАНЫ ЖҮРГІЗУ

Жобада келесі құжаттарды сақтау ұсынылады:

```text
docs/
├── PROJECT_OVERVIEW.md
├── ARCHITECTURE.md
├── DATABASE.md
├── ROADMAP.md
├── PROGRESS.md
└── DECISIONS.md
```

README.md — жобаның негізгі сипаттамасы мен іске қосу нұсқаулығы.

PROGRESS.md — орындалған, орындалып жатқан және қалған тапсырмалар.

DECISIONS.md — архитектуралық шешімдер және олардың себептері.

DATABASE.md — деректер базасының құрылымы.

ARCHITECTURE.md — қабаттар мен компоненттердің байланысы.

---

# 8. GIT COMMIT СТАНДАРТЫ

Commit атаулары қысқа және түсінікті болуы керек.

Мысалдар:

```text
feat(data): add HabitEntity
feat(data): implement HabitDao
feat(data): configure Room database
feat(data): implement habit repository
test(data): add CRUD tests
feat(domain): add habit use cases
feat(ui): implement home screen
fix(database): correct type converter
```

Әр тексерілген функционалды жеке commit ретінде сақтау ұсынылады.

---

# 9. КЕЛЕСІ ЧАТТА ЖҰМЫСТЫ ЖАЛҒАСТЫРУ

Жаңа чаттағы бірінші тапсырма:

**Phase 2 — Data Layer: CRUD операцияларын толық тестілеу.**

Жұмыс реті:

1. Пайдаланушының нақты HabitEntity.kt файлын тексеру.
2. HabitDao.kt әдістерін тексеру.
3. AppDatabase.kt конфигурациясын тексеру.
4. HabitRepositoryImpl.kt реализациясын тексеру.
5. Қажетті Gradle dependencies тексеру.
6. Instrumented Test жасау.
7. Create, Read, Update, Delete операцияларын тексеру.
8. Нәтижелерді бағалау.
9. Қателерді түзету.
10. Data Layer аяқталған соң Domain Layer-ге көшу.

**Маңызды:** жаңа чатта пайдаланушының нақты файлдарын көрмей тұрып, олардың ішкі құрылымы туралы болжам жасамау керек. Қажет болса, файлдарды сұрау немесе жобаның құрылымын тексеру қажет.

---

# 10. ЖОБАНЫҢ СОҢҒЫ МАҚСАТЫ

Habit Tracker жобасы келесі қасиеттерге ие болуы керек:

- Тұрақты жұмыс істейтін Android қосымшасы.
- Жергілікті Room Database.
- Толық CRUD функционалдығы.
- Күнделікті әдеттерді бақылау.
- Әдеттердің орындалу тарихы.
- Streak және статистика.
- Хабарламалар.
- Түсінікті Material 3 интерфейсі.
- MVVM + Clean Architecture.
- Тестіленген және құжатталған код.

**Негізгі нәтиже:** пайдаланушының күнделікті әдеттерін бақылауға арналған толыққанды, тұрақты және болашақта кеңейтуге болатын Android қосымшасы.