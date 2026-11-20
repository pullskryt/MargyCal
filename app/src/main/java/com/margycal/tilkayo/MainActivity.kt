package com.margycal.tilkayo

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MargyCal(window) }
    }
}

private val Day = lightColorScheme(
    primary = Color(0xFF743B83), onPrimary = Color.White,
    primaryContainer = Color(0xFFF8D7FF), onPrimaryContainer = Color(0xFF310C3D),
    secondary = Color(0xFF68506C), secondaryContainer = Color(0xFFF2DDF3),
    onSecondaryContainer = Color(0xFF27182D),
    tertiary = Color(0xFF795314), tertiaryContainer = Color(0xFFFFDDA6),
    onTertiaryContainer = Color(0xFF281900),
    background = Color(0xFFFFF7FC), onBackground = Color(0xFF241A25),
    surface = Color(0xFFFFF7FC), onSurface = Color(0xFF241A25),
    surfaceVariant = Color(0xFFF0E1EE), onSurfaceVariant = Color(0xFF514451),
    outline = Color(0xFF827382), outlineVariant = Color(0xFFD4C3D1),
    surfaceContainer = Color(0xFFF7EAF4), surfaceContainerHigh = Color(0xFFF2E3F0)
)
private val Night = darkColorScheme(
    primary = Color(0xFFF0AFFC), onPrimary = Color(0xFF481652),
    primaryContainer = Color(0xFF5B2A68), onPrimaryContainer = Color(0xFFFFD7FF),
    secondary = Color(0xFFDFC1E2), secondaryContainer = Color(0xFF4F3A54),
    onSecondaryContainer = Color(0xFFF7DDF8),
    tertiary = Color(0xFFF7BF67), tertiaryContainer = Color(0xFF5E410F),
    onTertiaryContainer = Color(0xFFFFDDA6),
    background = Color(0xFF1B141D), onBackground = Color(0xFFF1E2F0),
    surface = Color(0xFF1B141D), onSurface = Color(0xFFF1E2F0),
    surfaceVariant = Color(0xFF4D414F), onSurfaceVariant = Color(0xFFD4C2D2),
    outline = Color(0xFFA391A3), outlineVariant = Color(0xFF504451),
    surfaceContainer = Color(0xFF2B202D), surfaceContainerHigh = Color(0xFF362838)
)
private fun angryPalette(dark: Boolean): ColorScheme {
    val base = if (dark) Night else Day
    return if (dark) base.copy(
        primary = Color(0xFFFFB4A5), onPrimary = Color(0xFF641008),
        primaryContainer = Color(0xFF862417), onPrimaryContainer = Color(0xFFFFDAD2),
        secondary = Color(0xFFF8BAAF), secondaryContainer = Color(0xFF6C322B), onSecondaryContainer = Color(0xFFFFDAD4),
        tertiary = Color(0xFFFFB3AA), tertiaryContainer = Color(0xFF7E261F), onTertiaryContainer = Color(0xFFFFDAD5),
        surface = Color(0xFF281210), background = Color(0xFF281210),
        onSurface = Color(0xFFFFDBD5), onSurfaceVariant = Color(0xFFF2BDB4),
        surfaceVariant = Color(0xFF58302A), surfaceContainerLowest = Color(0xFF200C0A),
        surfaceContainerLow = Color(0xFF321613), surfaceContainer = Color(0xFF3C1A16),
        surfaceContainerHigh = Color(0xFF531F19), surfaceContainerHighest = Color(0xFF622C25),
        inverseSurface = Color(0xFFFFDBD5), inverseOnSurface = Color(0xFF3F211D), inversePrimary = Color(0xFFAA2619),
        surfaceTint = Color(0xFFFFB4A5),
        outline = Color(0xFFBD8A83), outlineVariant = Color(0xFF794842)
    ) else base.copy(
        primary = Color(0xFFAA2619), onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDAD2), onPrimaryContainer = Color(0xFF440901),
        secondary = Color(0xFF943A30), secondaryContainer = Color(0xFFFFD8D0), onSecondaryContainer = Color(0xFF3C0D09),
        tertiary = Color(0xFF8D2C25), tertiaryContainer = Color(0xFFFFCEC5), onTertiaryContainer = Color(0xFF430A06),
        surface = Color(0xFFFFEAE5), background = Color(0xFFFFEAE5), onSurface = Color(0xFF381410),
        onSurfaceVariant = Color(0xFF58302A), surfaceVariant = Color(0xFFF6D1CA),
        surfaceContainerLowest = Color(0xFFFFF6F3), surfaceContainerLow = Color(0xFFFFE4DD),
        surfaceContainer = Color(0xFFFFDFD8), surfaceContainerHigh = Color(0xFFFFC8BE), surfaceContainerHighest = Color(0xFFF4B8AD),
        inverseSurface = Color(0xFF512721), inverseOnSurface = Color(0xFFFFEBE5), inversePrimary = Color(0xFFFFB4A5),
        surfaceTint = Color(0xFFAA2619), outline = Color(0xFF87574F), outlineVariant = Color(0xFFD8A498)
    )
}

internal enum class MascotMode(val label: String, val casinoTitle: String, val anger: String) {
    TILKAYO("Тилкайо", "Казино Тилкайо", "Тилкайо злится"),
    PEAR("Белая груша", "Казино Белой груши", "Белая груша злится");

    fun sprite(angry: Boolean): Int = when (this) {
        PEAR -> R.drawable.white_pear
        TILKAYO -> if (angry) R.drawable.tilkayo_angry else R.drawable.tilkayo_runner
    }
}

private enum class Destination(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    CALC("Счёт", Icons.Filled.Home), COLUMN("Столбик", Icons.AutoMirrored.Filled.List), DEPOSIT("Деп", Icons.Filled.Star),
    FRACTION("Дроби", Icons.Filled.Add), CASINO("Казино", Icons.Filled.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MargyCal(window: android.view.Window) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("margycal", 0) }
    var wallet by remember { mutableStateOf(CasinoWallet(
        prefs.getLong("casinoChips", CREDIT), prefs.getLong("casinoDebt", 0)
    )) }
    val saveWallet: (CasinoWallet) -> Unit = {
        prefs.edit().putLong("casinoChips", it.chips).putLong("casinoDebt", it.loan).apply()
        wallet = it
    }
    var whitePear by remember { mutableStateOf(prefs.getBoolean("whitePear", false)) }
    val mascot = if (whitePear) MascotMode.PEAR else MascotMode.TILKAYO
    var dark by remember { mutableStateOf(prefs.getString("palette", "auto") ?: "auto") }
    var page by rememberSaveable { mutableStateOf(Destination.CALC.name) }
    var sheet by rememberSaveable { mutableStateOf("") }
    var erase by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val history = remember {
        mutableStateListOf<String>().apply {
            val saved = runCatching { JSONArray(prefs.getString("history", "[]")) }.getOrDefault(JSONArray())
            repeat(saved.length()) { add(saved.getString(it)) }
        }
    }
    fun saveHistory() { prefs.edit().putString("history", JSONArray(history).toString()).apply() }
    fun notify(message: String) { scope.launch { snackbar.showSnackbar(message) } }
    fun copy(value: String) {
        context.getSystemService(ClipboardManager::class.java)
            .setPrimaryClip(ClipData.newPlainText("MargyCal", value))
        if (android.os.Build.VERSION.SDK_INT < 33) notify("Скопировано")
    }
    var expression by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }
    var calcError by rememberSaveable { mutableStateOf("") }
    var pendingExpression by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(pendingExpression) {
        val input = pendingExpression ?: return@LaunchedEffect
        delay(490)
        runCatching { MathEngine.calculate(input) }
            .onSuccess {
                result = it
                calcError = ""
                val entry = "$input = $it"
                history.remove(entry)
                history.add(0, entry)
                if (history.size > 40) history.removeAt(40)
                saveHistory()
            }
            .onFailure { calcError = it.message ?: "Ошибка"; result = "" }
        pendingExpression = null
    }
    val current = Destination.valueOf(page)
    BackHandler(current != Destination.CALC && sheet.isEmpty()) { page = Destination.CALC.name }
    val darkMode = dark == "dark" || (dark == "auto" && isSystemInDarkTheme())
    SideEffect {
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !darkMode
            isAppearanceLightNavigationBars = !darkMode
        }
    }

    MaterialTheme(colorScheme = if (wallet.angry) angryPalette(darkMode) else if (darkMode) Night else Day) {
        val savedScreens = androidx.compose.runtime.saveable.rememberSaveableStateHolder()
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth >= 720.dp
            Scaffold(
                topBar = {
                    Column {
                        CenterAlignedTopAppBar(
                            title = { Text(if (wallet.angry) mascot.anger else "MargyCal", fontWeight = FontWeight.Black) },
                            navigationIcon = {
                                IconButton(onClick = { sheet = "about" }) {
                                    Icon(Icons.Filled.Info, "О маскоте")
                                }
                            },
                            actions = {
                                IconButton(onClick = { sheet = "history" }) { Icon(Icons.AutoMirrored.Filled.List, "История") }
                                IconButton(onClick = { sheet = "settings" }) { Icon(Icons.Filled.Settings, "Настройки") }
                            }
                        )
                        ExtraLoading(page)
                    }
                },
                snackbarHost = { SnackbarHost(snackbar) },
                bottomBar = {
                    Column {
                        MascotTrack(mascot, wallet.angry)
                        if (!wide) NavigationBar {
                            Destination.entries.forEach { tab ->
                                NavigationBarItem(current == tab, { page = tab.name },
                                    icon = { Icon(tab.icon, null) }, label = { Text(tab.title) })
                            }
                        }
                        else Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                    }
                }
            ) { inset ->
                Row(Modifier.fillMaxSize().padding(inset).imePadding()) {
                    if (wide) NavigationRail {
                        Destination.entries.forEach { tab ->
                            NavigationRailItem(current == tab, { page = tab.name },
                                icon = { Icon(tab.icon, null) }, label = { Text(tab.title) })
                        }
                    }
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                        MaterialParticles(wallet.angry, Modifier.matchParentSize())
                        savedScreens.SaveableStateProvider(page) {
                            Column(
                                Modifier.widthIn(max = 690.dp).fillMaxWidth()
                                    .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                when (current) {
                                    Destination.CALC -> Calculator(
                                        expression, result, calcError, mascot, wallet.angry, pendingExpression != null,
                                        onInput = {
                                            pendingExpression = null
                                            expression = it.take(250); result = ""; calcError = ""
                                        },
                                        onEvaluate = { pendingExpression = expression; calcError = "" },
                                        onTryLetters = {
                                            expression = "цлтплцтп"
                                            pendingExpression = expression
                                            calcError = ""
                                        },
                                        onCopy = ::copy,
                                        onAbout = { sheet = "about" }
                                    )
                                    Destination.COLUMN -> ColumnSolver(::copy)
                                    Destination.DEPOSIT -> DepositScreen(::copy)
                                    Destination.FRACTION -> FractionScreen(::copy)
                                    Destination.CASINO -> CasinoScreen(wallet, mascot, saveWallet)
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
        if (sheet.isNotEmpty()) {
            ModalBottomSheet(onDismissRequest = { sheet = "" }) {
                Column(
                    Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (sheet) {
                        "about" -> {
                            MascotBanner(mascot, wallet.angry)
                            Text("Калькулятор, столбик, дроби, вклад и казино на вымышленные фишки.", style = MaterialTheme.typography.bodyLarge)
                            FilledTonalButton(onClick = { sheet = "" }) { Text("К расчётам") }
                        }
                        "settings" -> {
                            Text("Маскот", style = MaterialTheme.typography.headlineMedium)
                            ElevatedCard {
                                ListItem(
                                    headlineContent = { Text("Белая груша") },
                                    supportingContent = { Text("Заменяет Тилкайо в приложении") },
                                    modifier = Modifier.toggleable(value = whitePear, role = Role.Switch) {
                                        whitePear = it
                                        prefs.edit().putBoolean("whitePear", it).apply()
                                    },
                                    trailingContent = { Switch(checked = whitePear, onCheckedChange = null) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                            Text("Тема", style = MaterialTheme.typography.headlineMedium)
                            listOf("auto" to "Системная", "light" to "Светлая", "dark" to "Тёмная").forEach { (key, label) ->
                                Card(onClick = { dark = key; prefs.edit().putString("palette", key).apply() }) {
                                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = dark == key, onClick = null, modifier = Modifier.size(48.dp))
                                        Text(label)
                                    }
                                }
                            }
                        }
                        "history" -> {
                            Text("История", style = MaterialTheme.typography.headlineMedium)
                            if (history.isEmpty()) Text("Здесь появятся результаты калькулятора.")
                            history.forEach { entry ->
                                ElevatedCard(onClick = {
                                    pendingExpression = null
                                    expression = entry.substringBefore(" = ")
                                    result = entry.substringAfter(" = ")
                                    calcError = ""
                                    page = Destination.CALC.name
                                    sheet = ""
                                }) { Text(entry, Modifier.fillMaxWidth().padding(16.dp)) }
                            }
                            if (history.isNotEmpty()) OutlinedButton(onClick = { erase = true }) { Text("Очистить историю") }
                        }
                    }
                }
            }
        }
        if (erase) AlertDialog(
            onDismissRequest = { erase = false },
            title = { Text("Очистить историю?") },
            confirmButton = {
                TextButton(onClick = { history.clear(); saveHistory(); erase = false }) { Text("Очистить") }
            },
            dismissButton = { TextButton(onClick = { erase = false }) { Text("Отмена") } }
        )
    }
}

@Composable
private fun MascotBanner(mascot: MascotMode, angry: Boolean, onAbout: (() -> Unit)? = null) {
    val pear = mascot == MascotMode.PEAR
    val shadow = when {
        angry -> Color(0xFF600E09)
        pear -> Color(0xFF363026)
        else -> Color(0xFF251427)
    }
    Card(shape = RoundedCornerShape(28.dp)) {
        Box(Modifier.fillMaxWidth().height(148.dp).background(shadow)) {
            if (pear) {
                Image(
                    painterResource(R.drawable.white_pear), mascot.label,
                    Modifier.align(Alignment.CenterEnd).fillMaxHeight().fillMaxWidth(.43f).padding(10.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Image(
                    painterResource(R.drawable.tilkayo), mascot.label,
                    Modifier.align(Alignment.CenterEnd).widthIn(max = 380.dp).fillMaxSize(),
                    contentScale = ContentScale.Crop, alignment = BiasAlignment(0f, -.85f)
                )
                Box(Modifier.matchParentSize().background(Brush.horizontalGradient(
                    0f to shadow.copy(alpha = .95f), .42f to shadow.copy(alpha = .7f),
                    .85f to shadow.copy(alpha = if (angry) .35f else 0f)
                )))
            }
            Column(Modifier.align(Alignment.CenterStart).padding(horizontal = 18.dp)) {
                Text(mascot.label, color = Color(0xFFFFEDF9), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                if (onAbout != null) AssistChip(onClick = onAbout, label = { Text("О маскоте") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer))
            }
        }
    }
}

@Composable
private fun Calculator(expression: String, result: String, error: String, mascot: MascotMode, angry: Boolean, loading: Boolean,
                       onInput: (String) -> Unit, onEvaluate: () -> Unit, onTryLetters: () -> Unit,
                       onCopy: (String) -> Unit, onAbout: () -> Unit) {
    val focus = LocalFocusManager.current
    MascotBanner(mascot, angry, onAbout)
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedTextField(expression, onInput, Modifier.fillMaxWidth(), label = { Text("Выражение или абракадабра") },
                placeholder = { Text("(12 + 8) × 3") }, singleLine = true, isError = error.isNotEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, autoCorrectEnabled = false, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (expression.isNotBlank()) { focus.clearFocus(); onEvaluate() }
                }))
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(4.dp))
            Row(Modifier.fillMaxWidth().heightIn(min = 42.dp), verticalAlignment = Alignment.CenterVertically) {
                if (loading) MaterialLoading(if (MathEngine.isLetterInput(expression)) "Шуршу буквами" else "Считаю")
                else {
                    if (result.isNotEmpty()) TextButton(onClick = { onCopy(result) }) { Text("Копировать") }
                    else SuggestionChip(onClick = { focus.clearFocus(); onTryLetters() }, label = { Text("цлтплцтп") })
                    Text(result.ifEmpty { "0" }, Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                        style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.End)
                }
            }
        }
    }
    val keys = listOf(
        listOf("AC", "(", ")", "⌫"),
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "−"),
        listOf("0", ",", "%", "+")
    )
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        keys.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                row.forEach { key ->
                    val action = {
                        focus.clearFocus()
                        val base = if (result.isNotEmpty() && !MathEngine.isLetterInput(result) && key in listOf("+", "−", "×", "÷", "%")) result
                            else if (result.isNotEmpty() && key != "⌫") "" else expression
                        onInput(when (key) { "AC" -> ""; "⌫" -> base.dropLast(1); else -> base + key })
                    }
                    val modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    if (key.first().isDigit() || key == ",") ElevatedButton(onClick = action, modifier = modifier,
                        contentPadding = PaddingValues(0.dp)) { Text(key, style = MaterialTheme.typography.titleLarge) }
                    else FilledTonalButton(onClick = action, modifier = modifier, contentPadding = PaddingValues(0.dp),
                        colors = if (key == "AC") ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ) else ButtonDefaults.filledTonalButtonColors()
                    ) { Text(key, style = MaterialTheme.typography.titleLarge) }
                }
            }
        }
    }
    Button(
        onClick = { focus.clearFocus(); onEvaluate() }, enabled = expression.isNotBlank() && !loading,
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
    ) { Text("=  Посчитать", style = MaterialTheme.typography.titleMedium) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnSolver(onCopy: (String) -> Unit) {
    var a by rememberSaveable { mutableStateOf("347") }
    var b by rememberSaveable { mutableStateOf("28") }
    var op by rememberSaveable { mutableStateOf("+") }
    var solved by rememberSaveable { mutableStateOf(false) }
    var showSteps by rememberSaveable { mutableStateOf(true) }
    val focus = LocalFocusManager.current
    val calculation = remember(a, b, op, solved) { if (solved) runCatching { MathEngine.column(a, b, op) } else null }
    Text("Решение в столбик", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text("Целые неотрицательные числа, до 14 цифр.", style = MaterialTheme.typography.bodyMedium)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        NumberField(a, { a = it; solved = false }, "Первое число", Modifier.weight(1f), decimal = false)
        NumberField(b, { b = it; solved = false }, "Второе число", Modifier.weight(1f), decimal = false)
    }
    val operators = listOf("+", "−", "×", "÷")
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        operators.forEachIndexed { index, symbol ->
            SegmentedButton(op == symbol, { op = symbol; solved = false },
                SegmentedButtonDefaults.itemShape(index, operators.size)) { Text(symbol, style = MaterialTheme.typography.titleLarge) }
        }
    }
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SuggestionChip(onClick = { a = "1002"; b = "789"; op = "−"; solved = true }, label = { Text("1002 − 789") })
        SuggestionChip(onClick = { a = "1248"; b = "32"; op = "÷"; solved = true }, label = { Text("1248 ÷ 32") })
    }
    Button(onClick = { focus.clearFocus(); solved = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
        Text("Решить")
    }
    calculation?.exceptionOrNull()?.let { ErrorCard(it.message ?: "Проверьте числа") }
    calculation?.getOrNull()?.let { solution ->
        ElevatedCard(Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Ответ  " + solution.answer(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                HorizontalDivider()
                Text(solution.layout(), Modifier.horizontalScroll(rememberScrollState()), fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyLarge)
                TextButton(onClick = { onCopy(solution.layout() + "\nОтвет: " + solution.answer()) }) { Text("Копировать решение") }
            }
        }
        FilterChip(selected = showSteps, onClick = { showSteps = !showSteps }, label = { Text("Показать шаги") })
        if (showSteps) OutlinedCard(Modifier.fillMaxWidth()) {
            solution.steps().forEachIndexed { index, step ->
                if (index > 0) HorizontalDivider()
                ListItem(headlineContent = { Text(step) }, leadingContent = {
                    Badge { Text((index + 1).toString()) }
                }, colors = ListItemDefaults.colors(containerColor = Color.Transparent))
            }
        }
    }
}

@Composable
private fun NumberField(value: String, onChange: (String) -> Unit, label: String, modifier: Modifier = Modifier,
                        decimal: Boolean = true) {
    OutlinedTextField(value, { onChange(it.take(24)) }, modifier, label = { Text(label) }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number))
}

@Composable
internal fun ErrorCard(message: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer)) {
        Text(message, Modifier.padding(16.dp))
    }
}

@Composable
private fun DepositScreen(onCopy: (String) -> Unit) {
    var start by rememberSaveable { mutableStateOf("100000") }
    var topUp by rememberSaveable { mutableStateOf("5000") }
    var rate by rememberSaveable { mutableStateOf("12") }
    var term by rememberSaveable { mutableStateOf("12") }
    var compound by rememberSaveable { mutableStateOf(true) }
    var calculated by rememberSaveable { mutableStateOf(true) }
    var timeline by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val calculation = remember(start, topUp, rate, term, compound, calculated) {
        if (calculated) runCatching { MathEngine.deposit(start, topUp, rate, term, compound) } else null
    }
    Text("Депозит", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberField(start, { start = it; calculated = false }, "Вклад, ₽", Modifier.weight(1f))
                NumberField(topUp, { topUp = it; calculated = false }, "В месяц, ₽", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberField(rate, { rate = it; calculated = false }, "Годовых, %", Modifier.weight(1f))
                NumberField(term, { term = it; calculated = false }, "Месяцев", Modifier.weight(1f), decimal = false)
            }
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("3", "6", "12", "24").forEach { months ->
                    FilterChip(term == months, { term = months; calculated = false },
                        label = { Text("$months мес.") })
                }
            }
            HorizontalDivider()
            ListItem(headlineContent = { Text("Капитализация") },
                supportingContent = { Text(if (compound) "Начисленные проценты увеличивают вклад" else "Проценты выплачиваются в конце срока") },
                trailingContent = { Switch(compound, { compound = it; calculated = false }) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent))
            Text("Пополнения в конце каждого месяца. Налоги не учитываются.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Button(onClick = { focus.clearFocus(); calculated = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
        Text("Рассчитать вклад")
    }
    calculation?.exceptionOrNull()?.let { ErrorCard(it.message ?: "Проверьте параметры") }
    calculation?.getOrNull()?.let { deposit ->
        val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("ru-RU")) }
        val total = currency.format(deposit.total())
        ElevatedCard(Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("К концу срока", style = MaterialTheme.typography.titleMedium)
                Text(total, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                HorizontalDivider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = .22f))
                Text("Внесено  " + currency.format(deposit.paid()))
                Text("Проценты  +" + currency.format(deposit.interest()))
                TextButton(onClick = { onCopy(total) }) { Text("Копировать итог", color = MaterialTheme.colorScheme.onTertiaryContainer) }
            }
        }
        FilterChip(timeline, { timeline = !timeline }, label = { Text("График по месяцам") })
        if (timeline) OutlinedCard(Modifier.fillMaxWidth()) {
            val shown = rememberSaveable(term) { mutableIntStateOf(12) }
            deposit.schedule().take(shown.intValue).forEach { month ->
                if (month.number() > 1) HorizontalDivider()
                ListItem(headlineContent = { Text("${month.number()} мес. · ${currency.format(month.total())}") },
                    supportingContent = { Text("Проценты за месяц  +" + currency.format(month.interest())) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent))
            }
            if (shown.intValue < deposit.schedule().size) TextButton(
                onClick = { shown.intValue += 12 }, modifier = Modifier.padding(horizontal = 12.dp)
            ) { Text("Ещё 12 месяцев") }
        }
    }
}
