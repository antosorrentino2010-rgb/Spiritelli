package com.example.collezioni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val ComponentActivity.dataStore by preferencesDataStore("collections")

@Serializable
data class Item(val id: Long, val name: String, val checked: Boolean = false)

@Serializable
data class CollectionModel(
    val id: Long,
    val name: String,
    val emoji: String = "📁",
    val color: Long = 0xFF6750A4,
    val items: List<Item> = emptyList()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CollezioniApp() }
    }
}

@Composable
fun CollezioniApp() {
    val activity = androidx.compose.ui.platform.LocalContext.current as ComponentActivity
    val scope = rememberCoroutineScope()
    var collections by remember { mutableStateOf(emptyList<CollectionModel>()) }
    var selectedId by remember { mutableStateOf<Long?>(null) }
    var query by remember { mutableStateOf("") }
    var dark by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = activity.dataStore.data.first()
        val raw = prefs[stringPreferencesKey("data")]
        collections = if (raw.isNullOrBlank()) defaultCollections()
        else runCatching { Json.decodeFromString<List<CollectionModel>>(raw) }.getOrElse { defaultCollections() }
        initialized = true
    }

    fun save(list: List<CollectionModel>) {
        collections = list
        scope.launch {
            activity.dataStore.edit { it[stringPreferencesKey("data")] = Json.encodeToString(list) }
        }
    }

    MaterialTheme(
        colorScheme = if (dark) darkColorScheme() else lightColorScheme(
            primary = Color(0xFF6750A4),
            secondary = Color(0xFF006874),
            surface = Color(0xFFF8F7FF)
        )
    ) {
        if (!initialized) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        } else {
            val selected = collections.firstOrNull { it.id == selectedId }
            if (selected == null) {
                Home(
                    collections, query, { query = it }, dark, { dark = !dark },
                    { selectedId = it },
                    { name, emoji, color -> save(collections + CollectionModel(System.currentTimeMillis(), name, emoji, color)) },
                    { id -> save(collections.filterNot { it.id == id }) }
                )
            } else {
                Detail(
                    selected,
                    { selectedId = null },
                    { itemId ->
                        save(collections.map { c ->
                            if (c.id == selected.id) c.copy(items = c.items.map { if (it.id == itemId) it.copy(checked = !it.checked) else it }) else c
                        })
                    },
                    { name -> save(collections.map { c -> if (c.id == selected.id) c.copy(items = c.items + Item(System.currentTimeMillis(), name)) else c }) },
                    { itemId -> save(collections.map { c -> if (c.id == selected.id) c.copy(items = c.items.filterNot { it.id == itemId }) else c }) },
                    { save(collections.map { c -> if (c.id == selected.id) c.copy(items = c.items.filterNot { it.checked }) else c }) },
                    { newItems -> save(collections.map { c -> if (c.id == selected.id) c.copy(items = newItems) else c }) },
                    { name, emoji, color -> save(collections.map { c -> if (c.id == selected.id) c.copy(name = name, emoji = emoji, color = color) else c }) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    collections: List<CollectionModel>, query: String, onQuery: (String) -> Unit,
    dark: Boolean, onDark: () -> Unit, onOpen: (Long) -> Unit,
    onAdd: (String, String, Long) -> Unit, onDelete: (Long) -> Unit
) {
    var dialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("📁") }
    var color by remember { mutableStateOf(0xFF6750A4L) }
    var deleteId by remember { mutableStateOf<Long?>(null) }
    val filtered = collections.filter { it.name.contains(query, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column {
                    Text("Le tue collezioni", fontWeight = FontWeight.Bold)
                    Text("${collections.size} raccolte", style = MaterialTheme.typography.labelMedium)
                }},
                actions = {
                    IconButton(onClick = onDark) { Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { dialog = true }, icon = { Icon(Icons.Default.Add, null) }, text = { Text("Nuova") })
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            OutlinedTextField(
                query, onQuery, Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Cerca una collezione…") }, leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true, shape = RoundedCornerShape(18.dp)
            )
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(filtered, key = { _, it -> it.id }) { _, c ->
                    val done = c.items.count { it.checked }
                    val progress = if (c.items.isEmpty()) 0f else done.toFloat() / c.items.size
                    ElevatedCard(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().clickable { onOpen(c.id) }.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(Color(c.color).copy(.14f)), Alignment.Center) {
                                Text(c.emoji, style = MaterialTheme.typography.headlineSmall)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(c.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("$done/${c.items.size} completati", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator({ progress }, Modifier.fillMaxWidth(), color = Color(c.color))
                            }
                            IconButton(onClick = { deleteId = c.id }) { Icon(Icons.Default.DeleteOutline, "Elimina") }
                        }
                    }
                }
            }
        }
    }
    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            title = { Text("Nuova collezione") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Nome") }, singleLine = true)
                    OutlinedTextField(emoji, { emoji = it }, label = { Text("Emoji") }, singleLine = true)
                    Text("Colore", style = MaterialTheme.typography.labelLarge)
                    ColorChoices(color) { color = it }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) { onAdd(name.trim(), emoji.ifBlank { "📁" }.take(2), color); name = ""; emoji = "📁"; color = 0xFF6750A4; dialog = false }
                }) { Text("Crea") }
            },
            dismissButton = { TextButton(onClick = { dialog = false }) { Text("Annulla") } }
        )
    }
    deleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text("Eliminare la collezione?") },
            text = { Text("Tutti gli elementi al suo interno verranno rimossi.") },
            confirmButton = { TextButton(onClick = { onDelete(id); deleteId = null }) { Text("Elimina") } },
            dismissButton = { TextButton(onClick = { deleteId = null }) { Text("Annulla") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail(
    collection: CollectionModel, onBack: () -> Unit, onToggle: (Long) -> Unit,
    onAdd: (String) -> Unit, onDelete: (Long) -> Unit, onClearCompleted: () -> Unit,
    onReorder: (List<Item>) -> Unit, onEdit: (String, String, Long) -> Unit
) {
    var addDialog by remember { mutableStateOf(false) }
    var editDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(collection.emoji) }
    var color by remember { mutableStateOf(collection.color) }
    var search by remember { mutableStateOf("") }
    var sortDoneFirst by remember { mutableStateOf(false) }
    var items by remember(collection.id, collection.items) { mutableStateOf(collection.items) }
    val visibleItems = items.filter { it.name.contains(search, true) }
        .let { list -> if (sortDoneFirst) list.sortedByDescending { it.checked } else list }

    val done = collection.items.count { it.checked }
    val progress = if (collection.items.isEmpty()) 0f else done.toFloat() / collection.items.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(collection.name, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Indietro") } },
                actions = {
                    IconButton(onClick = { sortDoneFirst = !sortDoneFirst }) { Icon(Icons.Default.Sort, "Ordina") }
                    IconButton(onClick = { name = collection.name; emoji = collection.emoji; color = collection.color; editDialog = true }) { Icon(Icons.Default.Edit, "Modifica") }
                    if (done > 0) IconButton(onClick = onClearCompleted) { Icon(Icons.Default.CleaningServices, "Pulisci") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { addDialog = true }, icon = { Icon(Icons.Default.Add, null) }, text = { Text("Aggiungi") })
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(collection.emoji, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("$done di ${collection.items.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(if (progress == 1f && collection.items.isNotEmpty()) "Completata 🎉" else "${(progress * 100).toInt()}% completato")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator({ progress }, Modifier.fillMaxWidth(), color = Color(collection.color))
                }
            }
            OutlinedTextField(
                search, { search = it }, Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Cerca elementi…") }, leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true, shape = RoundedCornerShape(18.dp)
            )
            val state = rememberLazyListState()
            LazyColumn(state = state, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                itemsIndexed(visibleItems, key = { _, it -> it.id }) { index, item ->
                    var offsetY by remember { mutableStateOf(0f) }
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(if (item.checked) MaterialTheme.colorScheme.surfaceVariant.copy(.45f) else Color.Transparent)
                            .pointerInput(item.id) {
                                detectDragGesturesAfterLongPress(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        offsetY += dragAmount.y
                                    },
                                    onDragEnd = {
                                        val from = items.indexOfFirst { it.id == item.id }
                                        val to = (from + if (offsetY > 30) 1 else if (offsetY < -30) -1 else 0).coerceIn(0, items.lastIndex)
                                        if (from != to) {
                                            val mutable = items.toMutableList()
                                            val moved = mutable.removeAt(from)
                                            mutable.add(to, moved)
                                            items = mutable
                                            onReorder(mutable)
                                        }
                                        offsetY = 0f
                                    }
                                )
                            }.clickable { onToggle(item.id) }.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(item.checked, { onToggle(item.id) })
                        Text(item.name, Modifier.weight(1f), fontWeight = if (item.checked) FontWeight.Normal else FontWeight.Medium)
                        IconButton(onClick = { onDelete(item.id) }) { Icon(Icons.Default.DeleteOutline, "Elimina") }
                    }
                }
            }
        }
    }
    if (addDialog) {
        AlertDialog(
            onDismissRequest = { addDialog = false },
            title = { Text("Aggiungi elemento") },
            text = { OutlinedTextField(name, { name = it }, label = { Text("Nome") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { onAdd(name.trim()); name = ""; addDialog = false } }) { Text("Aggiungi") } },
            dismissButton = { TextButton(onClick = { addDialog = false }) { Text("Annulla") } }
        )
    }
    if (editDialog) {
        AlertDialog(
            onDismissRequest = { editDialog = false },
            title = { Text("Modifica collezione") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Nome") }, singleLine = true)
                    OutlinedTextField(emoji, { emoji = it }, label = { Text("Emoji") }, singleLine = true)
                    ColorChoices(color) { color = it }
                }
            },
            confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { onEdit(name.trim(), emoji.ifBlank { "📁" }.take(2), color); editDialog = false } }) { Text("Salva") } },
            dismissButton = { TextButton(onClick = { editDialog = false }) { Text("Annulla") } }
        )
    }
}

@Composable
fun ColorChoices(selected: Long, onSelect: (Long) -> Unit) {
    val colors = listOf(0xFF6750A4L, 0xFF006874L, 0xFF9C4146L, 0xFF7A5900L, 0xFF006E1CL, 0xFF3F5F90L)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        colors.forEach { c ->
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(50)).background(Color(c)).clickable { onSelect(c) },
                contentAlignment = Alignment.Center
            ) { if (c == selected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
        }
    }
}

fun defaultCollections() = listOf(
    CollectionModel(1, "Giochi da finire", "🎮", 0xFF6750A4, listOf(
        Item(1, "Minecraft"), Item(2, "GTA V", true), Item(3, "Red Dead Redemption 2")
    )),
    CollectionModel(2, "Film da vedere", "🎬", 0xFF006874, listOf(
        Item(4, "Interstellar", true), Item(5, "Inception"), Item(6, "Il Gladiatore")
    ))
)
