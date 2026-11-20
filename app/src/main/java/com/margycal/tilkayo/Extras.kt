package com.margycal.tilkayo

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FractionScreen(onCopy: (String) -> Unit) {
    var a by rememberSaveable { mutableStateOf("2") }
    var b by rememberSaveable { mutableStateOf("3") }
    var c by rememberSaveable { mutableStateOf("3") }
    var d by rememberSaveable { mutableStateOf("4") }
    var op by rememberSaveable { mutableStateOf("+") }
    var solved by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(true) }
    val focus = LocalFocusManager.current
    val solution = remember(a, b, c, d, op, solved) {
        if (solved) runCatching { FractionEngine.solve(a, b, c, d, op) } else null
    }

    Text("Дроби", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
    FilledTonalButton(onClick = { a = "42"; b = "56"; op = "↓"; solved = true }) { Text("Сократить 42/56") }
    ElevatedCard(shape = CutCornerShape(topEnd = 30.dp, bottomStart = 22.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                @Composable fun field(value: String, update: (String) -> Unit, label: String) {
                    OutlinedTextField(value, { update(it.take(19)); solved = false }, label = { Text(label) },
                        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    field(a, { a = it }, "Числитель")
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
                    field(b, { b = it }, "Знаменатель")
                }
                if (op != "↓") {
                    Text(op, style = MaterialTheme.typography.headlineSmall)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        field(c, { c = it }, "Числитель")
                        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
                        field(d, { d = it }, "Знаменатель")
                    }
                }
            }
            val operations = listOf("+", "−", "×", "÷")
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                operations.forEachIndexed { i, symbol ->
                    SegmentedButton(op == symbol, { op = symbol; solved = false },
                        SegmentedButtonDefaults.itemShape(i, operations.size)) { Text(symbol) }
                }
            }
            FilterChip(op == "↓", { op = if (op == "↓") "+" else "↓"; solved = false }, label = { Text("Сокращение") })
        }
    }
    Button({ focus.clearFocus(); solved = true }, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Решить дроби") }
    solution?.exceptionOrNull()?.let { ErrorCard(it.message ?: "Проверьте ввод") }
    solution?.getOrNull()?.let { result ->
        ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Ответ", style = MaterialTheme.typography.labelLarge)
                Text(result.answer(), Modifier.horizontalScroll(rememberScrollState()), style = MaterialTheme.typography.headlineMedium)
                if (result.mixed().isNotEmpty()) Text("Смешанное число  " + result.mixed())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton({ onCopy(result.answer()) }) { Text("Копировать") }
                    FilterChip(expanded, { expanded = !expanded }, label = { Text("Шаги") })
                }
            }
        }
        if (expanded) OutlinedCard {
            result.steps().forEachIndexed { i, step ->
                if (i > 0) HorizontalDivider()
                ListItem(headlineContent = { Text(step) }, leadingContent = { Badge { Text((i + 1).toString()) } })
            }
        }
    }
}

private val reelSymbols = listOf("●", "◆", "✦", "▲", "⬟", "♥")
private fun Long.tokens() = java.text.NumberFormat.getIntegerInstance(java.util.Locale.forLanguageTag("ru-RU")).format(this)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CasinoScreen(wallet: CasinoWallet, mascot: MascotMode, save: (CasinoWallet) -> Unit) {
    var stake by rememberSaveable { mutableLongStateOf(BETS.first()) }
    var storedReels by rememberSaveable { mutableStateOf("014") }
    var banner by rememberSaveable { mutableStateOf("") }
    var revealed by remember { mutableStateOf(storedReels.map { it.digitToInt() }) }
    var spinning by remember { mutableStateOf(false) }
    var creditDialog by remember { mutableStateOf(false) }
    var info by rememberSaveable { mutableStateOf(false) }
    val random = Random.Default
    LaunchedEffect(spinning) {
        if (spinning) {
            repeat(8) {
                revealed = List(3) { random.nextInt(reelSymbols.size) }
                delay(85)
            }
            revealed = storedReels.map { it.digitToInt() }
            spinning = false
        }
    }
    Text(mascot.casinoTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
    Text("Вымышленные фишки. Без покупок и вывода.", style = MaterialTheme.typography.bodyMedium)
    ElevatedCard(shape = RoundedCornerShape(topStart = 32.dp, bottomEnd = 32.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(if (wallet.angry) mascot.anger else "Чистый баланс", style = MaterialTheme.typography.titleMedium)
            Text(wallet.balance.tokens(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
            HorizontalDivider()
            Text("Фишки на руках  ${wallet.chips.tokens()}")
            Text("Долг  ${wallet.loan.tokens()}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilledTonalButton({ creditDialog = true }, enabled = !spinning && wallet.loan < 10 * CREDIT) { Text("Кредит") }
                if (wallet.loan > 0) OutlinedButton({
                    save(wallet.repay()); banner = "Долг уменьшен"
                }, enabled = !spinning && wallet.chips > 0) { Text("Погасить") }
            }
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        revealed.forEachIndexed { i, symbol ->
            val scheme = MaterialTheme.colorScheme
            val colors = listOf(scheme.primaryContainer, scheme.secondaryContainer, scheme.tertiaryContainer)
            val ink = listOf(scheme.onPrimaryContainer, scheme.onSecondaryContainer, scheme.onTertiaryContainer)
            ElevatedCard(Modifier.weight(1f), shape = if (i == 1) CutCornerShape(20.dp) else RoundedCornerShape(28.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = colors[i], contentColor = ink[i])) {
                Box(Modifier.fillMaxWidth().height(82.dp), contentAlignment = Alignment.Center) {
                    Text(reelSymbols[symbol], style = MaterialTheme.typography.displaySmall)
                }
            }
        }
    }
    if (spinning) MaterialLoading("Кручу барабаны")
    else if (banner.isNotEmpty()) Card { Text(banner, Modifier.fillMaxWidth().padding(10.dp), textAlign = TextAlign.Center) }
    Text("Ставка", style = MaterialTheme.typography.titleMedium)
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        BETS.forEach { bet ->
            FilterChip(bet == stake, { stake = bet }, enabled = !spinning, label = { Text(bet.tokens()) })
        }
    }
    Button(
        onClick = {
            val reels = List(3) { random.nextInt(reelSymbols.size) }
            val round = wallet.spin(stake, reels)
            save(round.wallet)
            storedReels = reels.joinToString("")
            banner = if (round.payout == 0L) "Мимо · −${stake.tokens()}" else "Выплата · ${round.payout.tokens()}"
            spinning = true
        },
        enabled = !spinning && wallet.chips >= stake, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
    ) { Text(if (spinning) "Барабаны крутятся" else "Крутить за ${stake.tokens()}") }
    if (wallet.chips < stake && !spinning) Text("Для этой ставки не хватает фишек.", style = MaterialTheme.typography.bodyMedium)
    AssistChip({ info = true }, label = { Text("Как считаются выплаты") })
    if (info) ModalBottomSheet(onDismissRequest = { info = false }) {
        Column(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 28.dp)) {
            Text("Выплаты", style = MaterialTheme.typography.headlineSmall)
            ListItem(headlineContent = { Text("Три одинаковых символа") }, trailingContent = { Badge { Text("×6") } })
            HorizontalDivider()
            ListItem(headlineContent = { Text("Два одинаковых символа") }, trailingContent = { Badge { Text("×2") } })
            HorizontalDivider()
            ListItem(headlineContent = { Text("Три разных символа") }, trailingContent = { Badge { Text("×0") } })
            Text("Чистый баланс равен фишкам на руках за вычетом долга. При −20 000 включается красный режим.")
        }
    }
    if (creditDialog) AlertDialog(
        onDismissRequest = { creditDialog = false },
        title = { Text("Кредит на 10 000 фишек") },
        text = { Text("Вы получите 10 000 фишек для ставок. Долг вырастет на ту же сумму.") },
        confirmButton = { Button({
            save(wallet.borrow()); banner = "Кредит выдан · +10 000 фишек"; creditDialog = false
        }) { Text("Взять кредит") } },
        dismissButton = { TextButton({ creditDialog = false }) { Text("Отмена") } }
    )
}

@Composable
internal fun ExtraLoading(key: String) {
    var loading by remember(key) { mutableStateOf(true) }
    LaunchedEffect(key) { delay(850); loading = false }
    if (loading) Box(Modifier.padding(horizontal = 12.dp, vertical = 3.dp)) {
        MaterialLoading("Загружаю экран")
    }
}

@Composable
internal fun MaterialLoading(label: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp, 12.dp, 24.dp, 12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(Modifier.size(19.dp), strokeWidth = 3.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, style = MaterialTheme.typography.labelMedium)
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer) { Text("MD3") }
        }
    }
}

@Composable
internal fun MaterialParticles(angry: Boolean, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "Фон")
    val phase = transition.animateFloat(0f, 1f, infiniteRepeatable(tween(18900, easing = LinearEasing)), label = "Частицы")
    val scheme = MaterialTheme.colorScheme
    val colors = listOf(scheme.primary, scheme.tertiary, scheme.secondary, scheme.error)
    val triangle = remember { Path() }
    Canvas(modifier) {
        val t = phase.value
        repeat(48) { i ->
            val drift = (t * (1 + i % 2) + i * .137f) % 1f
            val horizontal = ((i * .618f) % 1f) + sin((2 * PI * t + i).toFloat()) * .055f
            val x = (horizontal + 1f) % 1f * size.width
            val y = drift * size.height
            val r = (3 + i % 8).dp.toPx()
            val edgeFade = minOf(drift * 10f, (1f - drift) * 10f, 1f)
            val ink = colors[i % colors.size].copy(alpha = edgeFade * if (angry) .34f else .22f)
            rotate(360f * drift + 13f * i, Offset(x, y)) {
                when (i % 5) {
                    0 -> drawCircle(ink, r, Offset(x, y))
                    1 -> drawRoundRect(ink, Offset(x - r, y - r), Size(2 * r, 2 * r), CornerRadius(r * .38f))
                    2 -> {
                        triangle.reset()
                        triangle.moveTo(x, y - r * 1.4f)
                        triangle.lineTo(x + r, y + r)
                        triangle.lineTo(x - r, y + r)
                        triangle.close()
                        drawPath(triangle, ink)
                    }
                    3 -> {
                        drawLine(ink, Offset(x - r, y), Offset(x + r, y), r / 1.8f, StrokeCap.Round)
                        drawLine(ink, Offset(x, y - r), Offset(x, y + r), r / 1.8f, StrokeCap.Round)
                    }
                    else -> drawRoundRect(ink, Offset(x - 1.4f * r, y - .52f * r), Size(2.8f * r, 1.04f * r), CornerRadius(r))
                }
            }
        }
    }
}

@Composable
internal fun MascotTrack(mascot: MascotMode, angry: Boolean) {
    val transition = rememberInfiniteTransition(label = "Бег маскота")
    val phase = transition.animateFloat(0f, 1f, infiniteRepeatable(tween(7100, easing = LinearEasing)), label = "Забег")
    val colors = MaterialTheme.colorScheme
    val sprite = ImageBitmap.imageResource(mascot.sprite(angry))
    Surface(color = colors.surfaceContainerHigh) {
        Canvas(Modifier.fillMaxWidth().height(55.dp)) {
            val speed = phase.value * if (angry) 2 else 1
            val ground = size.height - 3.dp.toPx()
            drawLine(colors.outlineVariant, Offset(0f, ground), Offset(size.width, ground), 1.dp.toPx())
            val height = 43.dp.toPx()
            val width = height * sprite.width / sprite.height
            val target = IntSize(width.roundToInt(), height.roundToInt())
            repeat(if (angry) 5 else 3) { i ->
                val x = ((speed + i * .37f) % 1f) * (size.width + width * 2f) - width
                val stride = ((32 * speed + i * .61f) * PI).toFloat()
                val y = ground - height - abs(sin(stride)) * 7.dp.toPx()
                rotate(7f * sin(stride), Offset(x + width / 2f, y + height / 2f)) {
                    drawImage(sprite, dstOffset = IntOffset(x.roundToInt(), y.roundToInt()),
                        dstSize = target, filterQuality = FilterQuality.Medium)
                }
            }
        }
    }
}
