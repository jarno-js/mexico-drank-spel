# Mexico - Dobbelsteenspel App

Een complete Android app voor het dobbelsteenspel "Mexico" gebouwd met Kotlin en Jetpack Compose.

## Features

### Spel Functionaliteit
- **Setup**: 2-10 spelers kunnen deelnemen
- **Initial Roll**: Bepaal wie begint door hoogste worp
- **Worpen Kiezen**: Eerste speler kiest 1-3 worpen
- **Dobbelstenen Vastzetten**: Zet 1 of 2 vast om Mexico te proberen
- **Speciale Scores**:
  - **MEXICO (1+2)**: Hoogste score, 5 slokken in pot, alle honderdtallen weg
  - **ZAND (2+3)**: Laagste score, direct half atje drinken
  - **WIJZEN (1+3)**: Telt niet als worp, wijs naar spelers
  - **Honderdtallen**: Dubbels (5+5=500, 5 slokken in pot)
  - **Normaal**: Hoogste cijfer eerst (6+4=64)
- **Death Match**: Bij gelijke laagste scores
- **Pot Systeem**: Track drankjes per ronde

### UI/UX
- Donker bar thema met oranje/goud accenten
- Geanimeerde dobbelstenen met roll effect
- Duidelijke visuele feedback voor locked dice
- Scoreboard met real-time updates
- Smooth navigation flow
- Portrait mode optimized

## Technische Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Navigation Compose
- **State Management**: StateFlow + Coroutines
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Project Structuur

```
app/src/main/java/com/mexico/game/
├── data/
│   └── model/
│       ├── GamePhase.kt        # Game state enum
│       ├── GameState.kt        # Main game state
│       ├── Player.kt           # Player model
│       └── ScoreResult.kt      # Score types (sealed class)
├── ui/
│   ├── components/
│   │   ├── DiceView.kt         # Animated dice component
│   │   ├── PlayerCard.kt       # Player display cards
│   │   └── PotDisplay.kt       # Pot indicator
│   ├── navigation/
│   │   └── NavGraph.kt         # Navigation setup
│   ├── screens/
│   │   ├── SetupScreen.kt      # Player setup
│   │   ├── InitialRollScreen.kt # Turn order determination
│   │   ├── GameScreen.kt       # Main game screen
│   │   ├── PointingScreen.kt   # Wijzen mechanic
│   │   └── ResultScreen.kt     # Round results
│   └── theme/
│       ├── Color.kt            # Color palette
│       ├── Theme.kt            # Material 3 theme
│       └── Type.kt             # Typography
├── utils/
│   └── DiceLogic.kt           # Game logic & score calculation
├── viewmodel/
│   └── GameViewModel.kt       # State management
└── MainActivity.kt            # App entry point
```

## Spelregels Implementatie

### Score Berekening
De `DiceLogic.kt` bevat alle score berekeningen:
- Detecteert automatisch alle speciale combinaties
- Berekent normale scores (hoogste eerst)
- Valideert welke dobbelstenen vastgezet kunnen worden

### State Management
De `GameViewModel` beheert:
- Complete game state met StateFlow
- Player turns en throwscounting
- Pot management
- Locked dice state
- Navigation events
- Death match handling

### Navigation Flow
1. **Setup** → Spelers toevoegen
2. **InitialRoll** → Volgorde bepalen
3. **ChoosingThrows** → Max worpen kiezen
4. **Game** → Hoofdspel
5. **Pointing** → Wijzen mechanic (indien 1+3)
6. **Result** → Ronde resultaten
7. **Loop** → Terug naar ChoosingThrows voor nieuwe ronde

## Belangrijke Features

### Dobbelstenen Vastzetten
- Alleen 1 of 2 kunnen vastgezet worden
- Visual feedback met gouden achtergrond en lock icon
- Tap om vast te zetten, separate button om te ontgrendelen
- Als na vastzetten 1+3 → automatisch naar Wijzen scherm

### Wijzen Mechanic
- Speciale screen waar je spelers selecteert
- Laatste niet-geselecteerde speler drinkt 1 slok
- Dobbelsteen wordt automatisch ontgrendeld
- Worp telt niet mee, speler mag opnieuw

### Mexico Mode
- Wanneer MEXICO gegooid wordt:
  - 5 slokken gaan in de pot
  - Alle honderdtallen (doubles) worden verwijderd
  - Mexico blijft hoogste score
- Visual indicator wanneer actief

### Death Match
- Automatisch geactiveerd bij gelijke laagste scores
- Alle betrokken spelers gooien 1 keer
- Laagste van death match drinkt pot

## Build Instructies

1. Open het project in Android Studio
2. Sync Gradle files
3. Run op emulator of physical device (Android 8.0+)

## Dependencies

Zie `app/build.gradle.kts` voor volledige lijst. Belangrijkste:
- Jetpack Compose BOM
- Material 3
- Navigation Compose
- ViewModel Compose
- Kotlin Coroutines

## Toekomstige Verbeteringen

Mogelijke uitbreidingen:
- Sound effects voor dobbelstenen
- Haptic feedback
- Statistieken per speler
- Geschiedenis van rondes
- Custom pot regels
- Multiplayer over netwerk
- Verschillende game modes
- Achievements/badges

## Licentie

Dit is een educatief project voor het Mexico dobbelsteenspel.
