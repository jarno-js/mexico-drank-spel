# Mexico Dice Game - Presentatie Tekst

## Projectoverzicht

Voor dit project heb ik een complete Android app ontwikkeld voor het dobbelsteenspel "Mexico". Het is een drankspel waarbij 2-10 spelers om de beurt dobbelstenen gooien en de laagste score aan het einde van elke ronde moet drinken uit een pot. De app is volledig gebouwd met **Kotlin** en **Jetpack Compose**.

---

## Belangrijkste Code Highlights

### 1. **Sealed Classes in Kotlin** (ScoreResult.kt)

Een van de krachtigste features van Kotlin zijn **sealed classes**. Deze heb ik gebruikt om alle mogelijke score types van het spel te representeren:

```kotlin
sealed class ScoreResult {
    abstract val displayText: String
    abstract val numericValue: Int

    data class Normal(val score: Int) : ScoreResult()
    data class Hundred(val value: Int, val drinks: Int) : ScoreResult()
    data object Mexico : ScoreResult()
    data object Sand : ScoreResult()
    data object Pointing : ScoreResult()
}
```

**Waarom is dit belangrijk?**
- **Type safety**: De Kotlin compiler garandeert dat alle mogelijke cases worden afgehandeld
- **When expressions**: Met sealed classes kan je exhaustive when statements maken
- **Data objects**: Kotlin feature voor singleton cases zoals Mexico en Sand
- **Pattern matching**: Elegante manier om verschillende score types te behandelen

### 2. **StateFlow voor Reactive State** (GameViewModel.kt:13-23)

Met Kotlin's **StateFlow** heb ik alle game state reactive gemaakt:

```kotlin
private val _gameState = MutableStateFlow(GameState())
val gameState: StateFlow<GameState> = _gameState.asStateFlow()

private val _currentDice = MutableStateFlow<Pair<Int, Int>?>(null)
val currentDice: StateFlow<Pair<Int, Int>?> = _currentDice.asStateFlow()

private val _isRolling = MutableStateFlow(false)
val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()
```

**Wat maakt dit krachtig?**
- **Single source of truth**: Alle state op één plek
- **Reactive updates**: Jetpack Compose UI update automatisch wanneer state verandert
- **Immutability**: State kan alleen via de ViewModel worden aangepast
- **Null safety**: Kotlin's `?` operator maakt nullable types expliciet

### 3. **Jetpack Compose UI** (DiceView.kt:26-98)

Jetpack Compose laat je UI bouwen met Kotlin code in plaats van XML:

```kotlin
@Composable
fun DiceView(
    value: Int?,
    isRolling: Boolean = false,
    isLocked: Boolean = false,
    canLock: Boolean = false,
    onLockClick: (() -> Unit)? = null
) {
    val rotationAnimation = rememberInfiniteTransition(label = "dice_roll")
    val animatedRotation by rotationAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .size(120.dp)
            .rotate(if (isRolling) animatedRotation else 0f)
            .background(
                color = if (isLocked) DarkSurfaceVariant else Color.White
            )
            .border(
                width = if (canLock) 3.dp else if (isLocked) 3.dp else 0.dp,
                color = if (canLock) AccentPrimary else AccentSecondary
            )
    ) {
        if (value != null && !isRolling) {
            DiceDots(value, isLocked)
        }
    }
}
```

**Waarom is Compose revolutionair?**
- **Declarative**: Je beschrijft WAT de UI moet zijn in Kotlin, niet HOE je er komt
- **Composable functions**: UI componenten zijn gewoon Kotlin functions
- **Built-in animations**: Animaties zijn supersimpel met Compose animation API's
- **No XML**: Alles in Kotlin, type-safe, met autocompletion
- **State-driven**: UI reageert automatisch op state changes via Kotlin coroutines

### 4. **Kotlin Object voor Game Logic** (DiceLogic.kt:14-41)

Kotlin's **object** keyword maakt het makkelijk om utility singletons te maken:

```kotlin
object DiceLogic {
    fun rollSingleDie(): Int = Random.nextInt(1, 7)

    fun calculateScore(die1: Int, die2: Int): ScoreResult {
        // Check for Mexico (1+2)
        if ((die1 == 1 && die2 == 2) || (die1 == 2 && die2 == 1)) {
            return ScoreResult.Mexico
        }

        // Check for Honderdtallen (doubles)
        if (die1 == die2) {
            return ScoreResult.Hundred(value = die1, drinks = die1)
        }

        // Normal score: highest first
        val higher = maxOf(die1, die2)
        val lower = minOf(die1, die2)
        val normalScore = higher * 10 + lower

        return ScoreResult.Normal(normalScore)
    }
}
```

**Kotlin voordelen:**
- **Object singleton**: Geen instanties nodig, automatisch singleton
- **Extension functions mogelijk**: Kotlin laat je functies toevoegen aan bestaande types
- **Concise syntax**: `maxOf()`, `minOf()` zijn Kotlin standard library functions
- **Expression body**: `fun rollSingleDie(): Int = Random.nextInt(1, 7)` is super kort

### 5. **Compose Modifiers voor Styling** (DiceView.kt:48-70)

Jetpack Compose gebruikt **modifiers** voor styling, in plaats van XML attributes:

```kotlin
Box(
    modifier = modifier
        .size(120.dp)
        .clickable(onClick = onLockClick)
        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
        .rotate(if (isRolling) animatedRotation else 0f)
        .background(color = if (isLocked) DarkSurfaceVariant else Color.White)
        .border(width = 3.dp, color = AccentPrimary)
        .padding(16.dp)
)
```

**Compose Modifiers voordelen:**
- **Chainable**: Modifiers worden aan elkaar gekoppeld met `.`
- **Type-safe**: Kotlin compiler checkt alle parameters
- **Reusable**: Je kan modifier chains opslaan en herbruiken
- **Conditional**: Kotlin's `if` werkt direct in de modifier chain

---

## Wat Ik Geleerd Heb

### 1. **Kotlin Language Features**

**Null Safety**
Kotlin forceert je om expliciet om te gaan met null:
```kotlin
val currentPlayer = _gameState.value.currentPlayer ?: return
// currentPlayer is nu non-null voor de rest van de functie
```
- `?` voor nullable types
- `?.` voor safe calls
- `?:` Elvis operator voor defaults
- `!!` voor force unwrap (zelden nodig)

**Data Classes**
Automatische `equals()`, `hashCode()`, `copy()` methods:
```kotlin
data class Player(
    val id: String,
    val name: String,
    var score: ScoreResult? = null
)

// Copy with changes
val updatedPlayer = player.copy(score = ScoreResult.Mexico)
```

**Sealed Classes**
Type-safe hierarchies:
```kotlin
when (score) {
    is ScoreResult.Mexico -> // handle Mexico
    is ScoreResult.Sand -> // handle Sand
    is ScoreResult.Normal -> // handle normal
    is ScoreResult.Hundred -> // handle hundred
    is ScoreResult.Pointing -> // handle pointing
}
// Compiler waarschuwt als je een case vergeet!
```

**Object Declarations**
Singletons zonder boilerplate:
```kotlin
object DiceLogic {
    fun rollDice() = Random.nextInt(1, 7)
}
// Gebruik: DiceLogic.rollDice()
```

**Smart Casts**
Compiler weet automatisch types:
```kotlin
if (score is ScoreResult.Hundred) {
    // score is automatisch gecast naar Hundred hier
    println(score.drinks)
}
```

**Extension Functions** (niet gebruikt in dit project, maar wel geleerd)
```kotlin
fun Int.isEven() = this % 2 == 0
// 4.isEven() returns true
```

### 2. **Jetpack Compose**

**Composable Functions**
UI bouwen met Kotlin functions:
```kotlin
@Composable
fun PlayerCard(player: Player) {
    Card {
        Text(text = player.name)
        Text(text = player.score?.displayText ?: "Not rolled")
    }
}
```

**State Management in Compose**
```kotlin
// In Composable:
val gameState by viewModel.gameState.collectAsState()

// UI reageert automatisch op changes
Text(text = "Pot: ${gameState.pot}")
```

**Compose Layouts**
- **Column**: Verticale layout
- **Row**: Horizontale layout
- **Box**: Overlay layout
- **LazyColumn**: Scrollable list (zoals RecyclerView)

```kotlin
Column {
    Text("Title")
    Row {
        Button(onClick = {}) { Text("Left") }
        Button(onClick = {}) { Text("Right") }
    }
}
```

**Compose Animations**
```kotlin
val rotation by rememberInfiniteTransition().animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
        animation = tween(200),
        repeatMode = RepeatMode.Restart
    )
)
```

**Material 3 Theming**
```kotlin
MaterialTheme(
    colorScheme = darkColorScheme(
        primary = AccentPrimary,
        secondary = AccentSecondary
    )
) {
    // App content
}
```

**Navigation Compose**
```kotlin
NavHost(navController, startDestination = "setup") {
    composable("setup") { SetupScreen() }
    composable("game") { GameScreen() }
    composable("result") { ResultScreen() }
}
```

### 3. **MVVM Architecture Pattern**

**ViewModel** houdt state en business logic:
```kotlin
class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun rollDice() {
        // Update state
        _gameState.value = _gameState.value.copy(...)
    }
}
```

**View** (Compose) observeert en reageert:
```kotlin
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()

    Button(onClick = { viewModel.rollDice() }) {
        Text("Roll!")
    }
}
```

### 4. **Project Structure**

Georganiseerd per feature:
```
com.mexico.game/
├── data/model/          # Data classes (Player, GameState, ScoreResult)
├── ui/
│   ├── components/      # Reusable Compose components
│   ├── screens/         # Screen-level Composables
│   ├── navigation/      # NavGraph setup
│   └── theme/           # Material 3 theme
├── utils/               # Utility functions (DiceLogic)
└── viewmodel/           # ViewModels
```

---

## Aanbeveling voor Studenten

### ✅ **JA, ik raad Kotlin + Jetpack Compose ABSOLUUT aan!**

### Waarom Kotlin?

#### 1. **Moderne Taal met Veiligheid**
Kotlin's null safety voorkomt de meest voorkomende bron van crashes in Android apps. De compiler is streng, maar dat betekent minder runtime errors.

**Voorbeeld:**
```kotlin
// Kotlin forceert je om null te handelen
var name: String? = null
println(name.length)  // Compile error!
println(name?.length) // OK, returns null
println(name?.length ?: 0) // OK, returns 0 if null
```

In Java zou `name.length()` crashen op runtime. Kotlin vangt dit op compile time.

#### 2. **Minder Code, Meer Duidelijkheid**
Kotlin elimineert veel Java boilerplate:

**Java:**
```java
public class Player {
    private String name;
    private int score;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    // equals, hashCode, toString...
}
```

**Kotlin:**
```kotlin
data class Player(val name: String, val score: Int)
```

Dat is 1 regel vs 20+ regels!

#### 3. **Betere Developer Experience**
- Autocomplete werkt perfect
- Extension functions maken code leesbaar
- Smart casts reduceren type casting
- String templates: `"Player $name has score $score"`
- Named parameters: `Player(name = "John", score = 42)`

#### 4. **Type Safety Everywhere**
Kotlin's type system vangt fouten vroeg:
```kotlin
sealed class ScoreResult  // Compiler garandeert exhaustive when

fun handle(score: ScoreResult) = when (score) {
    is Mexico -> // ...
    is Sand -> // ...
    // Vergeet je een case? Compile error!
}
```

### Waarom Jetpack Compose?

#### 1. **Declarative UI is Intuïtiever**
**Oude manier (XML + Java):**
```xml
<!-- layout.xml -->
<TextView
    android:id="@+id/textView"
    android:text="Hello" />
```
```java
// Activity.java
TextView textView = findViewById(R.id.textView);
textView.setText("Hello " + name);
```

**Compose manier:**
```kotlin
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name")
}
```

Je beschrijft WAT je wilt zien, niet HOE je het moet updaten.

#### 2. **Geen XML, Alles in Kotlin**
- Type-safe properties
- Autocomplete voor alles
- Refactoring werkt perfect
- No more `findViewById`
- No more layout inflation

#### 3. **State Management is Simpel**
```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Clicked $count times")
    }
}
```

UI update automatisch wanneer `count` verandert. Geen `notifyDataSetChanged()`, geen listeners, gewoon werkt.

#### 4. **Reusable Components**
Compose components zijn gewoon functions:
```kotlin
@Composable
fun DiceView(value: Int) { /* ... */ }

// Gebruik overal:
DiceView(value = 1)
DiceView(value = 6)
```

#### 5. **Animations zijn Gemakkelijk**
```kotlin
val rotation by animateFloatAsState(
    targetValue = if (isRolling) 360f else 0f
)

Box(modifier = Modifier.rotate(rotation))
```

Vergelijk dit met XML animations, ObjectAnimators, etc. Compose is zoveel simpeler!

#### 6. **Material 3 Support**
```kotlin
MaterialTheme {
    Button(onClick = {}) {
        Text("Click me")
    }
}
```

Automatisch Material Design styling, dark mode support, theming.

#### 7. **Preview in Real-Time**
```kotlin
@Preview
@Composable
fun PreviewDice() {
    DiceView(value = 5, isLocked = true)
}
```

Zie je UI direct in Android Studio, zonder app te runnen!

### Uitdagingen (eerlijk zijn)

#### Kotlin Uitdagingen:
1. **Leercurve**: Als je van Java komt, moet je nieuwe syntax leren
2. **Null safety**: In het begin voelt het streng aan
3. **Coroutines**: Async programming is een nieuw concept

#### Compose Uitdagingen:
1. **Declarative thinking**: Je moet anders denken dan imperative UI
2. **Recomposition**: Je moet begrijpen wanneer en waarom Compose herbouwt
3. **Performance**: Je moet weten hoe je onnodige recompositions vermijdt
4. **Debugging**: Stack traces kunnen verwarrend zijn

**Maar:** Deze uitdagingen zijn tijdelijk. Eenmaal over de leercurve heen, schrijf je sneller en beter code dan ooit.

### Praktische Voordelen voor Studenten

1. **Marktrelevantie**: Google pushes Compose, nieuwe Android jobs verwachten het
2. **Modern skillset**: Je leert state-of-the-art Android development
3. **Portfolio**: Compose apps zien er professioneel uit
4. **Fun factor**: Compose is echt leuker om mee te werken dan XML
5. **Future-proof**: Dit is de toekomst van Android development

---

## Conclusie

### Wat Dit Project Me Leerde

**Kotlin heeft me geleerd:**
- Null safety maakt apps stabieler
- Data classes elimineren boilerplate
- Sealed classes maken code type-safe
- Extension functions maken code leesbaarder
- Kotlin's standard library is krachtig (maxOf, minOf, map, filter, etc.)

**Jetpack Compose heeft me geleerd:**
- Declarative UI is intuïtiever dan XML
- State management kan simpel zijn
- Animations hoeven niet complex te zijn
- Components herbruiken is natuurlijk
- Material 3 theming werkt out-of-the-box

**MVVM Pattern heeft me geleerd:**
- Separation of concerns maakt code onderhoudbaarder
- ViewModels overleven configuration changes (screen rotation)
- StateFlow maakt reactive programming eenvoudig
- Business logic gescheiden van UI is testbaarder

### Eindoordeel

**Voor studenten die Android development willen leren: Start met Kotlin en Jetpack Compose.**

Waarom?
1. **Het is moderne technologie** - Dit is wat de industrie gebruikt
2. **Het is veiliger** - Null safety voorkomt crashes
3. **Het is sneller** - Minder boilerplate, meer focus op features
4. **Het is leuker** - Betere developer experience
5. **Het is toekomstbestendig** - Google's officiële aanbeveling

Ja, er is een leercurve. Ja, je moet nieuwe concepten leren. Maar het resultaat is:
- **Betere apps** (minder bugs)
- **Snellere development** (minder code)
- **Meer plezier** (beter tooling)
- **Betere carrièrekansen** (market demand)

### Key Takeaways

1. **Kotlin > Java**: Modern, safe, concise
2. **Compose > XML**: Declarative, type-safe, reusable
3. **StateFlow**: Reactive state management is krachtig
4. **MVVM**: Clean architecture pattern die werkt
5. **Sealed Classes**: Type-safe hierarchies zijn essential
6. **Null Safety**: De compiler is je vriend
7. **Composable Functions**: UI = function(state)
8. **Developer Experience**: Tooling maakt enorm verschil

Dit project was niet alleen leuk om te bouwen, maar ook zeer educatief. Ik heb hands-on ervaring gekregen met production-ready Kotlin en Compose patterns die worden gebruikt in echte Android apps bij bedrijven als Google, Twitter, Airbnb, etc.

**Voor toekomstige studenten: Kotlin + Compose is de toekomst. Start vandaag!**
