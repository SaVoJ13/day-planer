package com.savoj.shoppinglist.ui.details

import android.app.Activity
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savoj.shoppinglist.R
import com.savoj.shoppinglist.data.repository.ShoppingRepositoryImpl
import com.savoj.shoppinglist.domain.model.ShoppingItem
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailsScreen(
    viewModel: ListDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var inputItemName by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }

    val activeItems = uiState.items.filter { !it.isCompleted }
    val completedItems = uiState.items.filter { it.isCompleted }
    val isLocked = uiState.currentList?.isLocked == true
    val isFullyCompleted = uiState.items.isNotEmpty() && activeItems.isEmpty()

    val undoLabel = stringResource(R.string.btn_undo)
    val itemDeletedMessage = stringResource(R.string.toast_item_deleted)

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                viewModel.onVoiceInput(spokenText)
            }
        }
    }

    val launchVoiceInput = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.voice_input_prompt))
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
        }
        try {
            voiceLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.voice_input_unavailable), Toast.LENGTH_SHORT).show()
        }
    }

    val currentLocaleLang = Locale.getDefault().language
    val stapleItems = remember(currentLocaleLang) {
        when (currentLocaleLang) {
            "en" -> listOf("Bread", "Milk", "Eggs", "Cheese", "Butter", "Apples", "Bananas", "Water", "Coffee", "Tea")
            "pl" -> listOf("Chleb", "Mleko", "Jajka", "Ser", "Masło", "Jabłka", "Banany", "Woda", "Kawa", "Herbata")
            "ru" -> listOf("Хлеб", "Молоко", "Яйца", "Сыр", "Масло", "Яблоки", "Бананы", "Вода", "Кофе", "Чай")
            else -> listOf("Хліб", "Молоко", "Яйця", "Сир", "Масло", "Яблука", "Банани", "Вода", "Кава", "Чай")
        }
    }

    val activeNames = remember(uiState.items) {
        uiState.items.filter { !it.isCompleted }.map { it.name.lowercase(Locale.ROOT) }.toSet()
    }
    val suggestions = remember(uiState.recentItems, stapleItems, activeNames) {
        val recentNames = uiState.recentItems.map { it.name }
        (recentNames + stapleItems)
            .distinctBy { it.lowercase(Locale.ROOT) }
            .filter { it.lowercase(Locale.ROOT) !in activeNames }
            .take(10)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.currentList?.name ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isLocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = stringResource(R.string.badge_locked),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.progress_format, completedItems.size, uiState.items.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back))
                    }
                },
                actions = {
                    if (isLocked) {
                        IconButton(onClick = {
                            viewModel.toggleListLocked()
                            Toast.makeText(context, context.getString(R.string.toast_list_unlocked), Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Default.LockOpen,
                                contentDescription = stringResource(R.string.action_unlock_list),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else if (isFullyCompleted) {
                        IconButton(onClick = {
                            viewModel.toggleListLocked()
                            Toast.makeText(context, context.getString(R.string.toast_list_locked), Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = stringResource(R.string.action_close_list),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.showShareDialog() }) {
                        Icon(
                            Icons.Default.GroupAdd,
                            contentDescription = stringResource(R.string.share_list_title),
                            tint = if (uiState.currentList?.isShared == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        val shareText = buildString {
                            appendLine(uiState.currentList?.name ?: "")
                            appendLine("---")
                            activeItems.forEach { appendLine("• ${it.name}") }
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.action_share))
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            if (isLocked) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_unlock_list)) },
                                    leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        viewModel.toggleListLocked()
                                        Toast.makeText(context, context.getString(R.string.toast_list_unlocked), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else if (isFullyCompleted) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_close_list)) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        viewModel.toggleListLocked()
                                        Toast.makeText(context, context.getString(R.string.toast_list_locked), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_reset_all_items)) },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.resetAllItems()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_clear_completed)) },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.deleteCompletedItems()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (isLocked) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.list_locked_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.toggleListLocked()
                                Toast.makeText(context, context.getString(R.string.toast_list_unlocked), Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = stringResource(R.string.action_unlock_list))
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Quick Suggestion Chips Row
                        if (suggestions.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(suggestions, key = { it }) { suggestion ->
                                    SuggestionChip(
                                        onClick = { viewModel.addItem(suggestion) },
                                        label = { Text("+ $suggestion", style = MaterialTheme.typography.labelMedium) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        border = null
                                    )
                                }
                            }
                        }

                        // Input Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                    if (clipText.isNotBlank()) {
                                        viewModel.onClipboardPasted(clipText)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ContentPaste, contentDescription = stringResource(R.string.paste_action_tooltip))
                            }

                            IconButton(
                                onClick = { viewModel.setRecentSheetVisible(true) }
                            ) {
                                Icon(Icons.Default.Star, contentDescription = stringResource(R.string.recent_sheet_title))
                            }

                            OutlinedTextField(
                                value = inputItemName,
                                onValueChange = { inputItemName = it },
                                placeholder = { Text(stringResource(R.string.item_quick_add_hint)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    capitalization = KeyboardCapitalization.Sentences
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (inputItemName.isNotBlank()) {
                                            val count = ShoppingRepositoryImpl.parseMultiLineText(inputItemName).size
                                            viewModel.addSmartItems(inputItemName)
                                            inputItemName = ""
                                            if (count > 1) {
                                                Toast.makeText(context, context.getString(R.string.smart_added_toast, count), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                ),
                                trailingIcon = {
                                    if (inputItemName.isNotBlank()) {
                                        IconButton(onClick = { inputItemName = "" }) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = stringResource(R.string.action_cancel),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                            )

                            if (inputItemName.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val count = ShoppingRepositoryImpl.parseMultiLineText(inputItemName).size
                                        viewModel.addSmartItems(inputItemName)
                                        inputItemName = ""
                                        if (count > 1) {
                                            Toast.makeText(context, context.getString(R.string.smart_added_toast, count), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = stringResource(R.string.action_add),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { launchVoiceInput() }
                                ) {
                                    Icon(
                                        Icons.Default.Mic,
                                        contentDescription = stringResource(R.string.voice_input_tooltip),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Locked banner
            if (isLocked) {
                item(key = "locked_banner") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.list_locked_title),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = stringResource(R.string.list_locked_desc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    viewModel.toggleListLocked()
                                    Toast.makeText(context, context.getString(R.string.toast_list_unlocked), Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    Icons.Default.LockOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = stringResource(R.string.action_unlock_list))
                            }
                        }
                    }
                }
            } else if (isFullyCompleted) {
                // Celebration & Close List Banner
                item(key = "celebration_close_banner") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.all_items_completed_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.all_items_completed_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.toggleListLocked()
                                    Toast.makeText(context, context.getString(R.string.toast_list_locked), Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.action_close_list))
                            }
                        }
                    }
                }
            }

            if (uiState.items.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.items_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.items_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Active items
                items(activeItems, key = { it.id }) { item ->
                    ShoppingItemRow(
                        item = item,
                        isLocked = isLocked,
                        onToggle = { viewModel.toggleItemCompleted(item) },
                        onDelete = {
                            viewModel.deleteItem(item)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = itemDeletedMessage,
                                    actionLabel = undoLabel,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.undoDelete()
                                }
                            }
                        },
                        onLockedAttempt = {
                            Toast.makeText(context, context.getString(R.string.toast_list_locked), Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Completed items section (Accordion)
                if (completedItems.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleCompletedSection() }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.section_completed, completedItems.size),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = if (uiState.showCompletedSection) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (uiState.showCompletedSection) {
                        items(completedItems, key = { it.id }) { item ->
                            ShoppingItemRow(
                                item = item,
                                isLocked = isLocked,
                                onToggle = { viewModel.toggleItemCompleted(item) },
                                onDelete = { viewModel.deleteItem(item) },
                                onLockedAttempt = {
                                    Toast.makeText(context, context.getString(R.string.toast_list_locked), Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Smart Multi-line Paste Dialog
    if (uiState.showPasteDialog) {
        val selectedItems = remember(uiState.parsedPasteItems) {
            mutableStateListOf<String>().apply { addAll(uiState.parsedPasteItems) }
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissPasteDialog() },
            title = { Text(stringResource(R.string.paste_dialog_title)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.paste_dialog_desc, uiState.parsedPasteItems.size),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(uiState.parsedPasteItems) { name ->
                            val isChecked = selectedItems.contains(name)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedItems.remove(name) else selectedItems.add(name)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedItems.add(name) else selectedItems.remove(name)
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmPaste(selectedItems.toList()) },
                    enabled = selectedItems.isNotEmpty()
                ) {
                    Text(stringResource(R.string.paste_btn_add_all, selectedItems.size))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissPasteDialog() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Frequent Items Bottom Sheet
    if (uiState.showRecentSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { viewModel.setRecentSheetVisible(false) },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.recent_sheet_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.recentItems.isEmpty()) {
                    Text(
                        text = stringResource(R.string.recent_empty_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.recentItems, key = { it.id }) { recent ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.addItem(recent.name)
                                },
                                label = { Text(recent.name) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Share Group List Dialog
    if (uiState.showShareDialog) {
        val shareCode = uiState.currentList?.shareCode ?: ""
        AlertDialog(
            onDismissRequest = { viewModel.hideShareDialog() },
            title = { Text(stringResource(R.string.share_list_title), fontWeight = FontWeight.Bold) },
            text = {
                if (uiState.isSharingLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.shareError != null) {
                    Column {
                        Text(
                            text = stringResource(R.string.error_cloud_sync),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.shareError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.share_code_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = shareCode,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Share Code", shareCode)
                                    clipboard?.setPrimaryClip(clip)
                                    android.widget.Toast.makeText(context, context.getString(R.string.toast_code_copied), android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.action_copy_code))
                            }
                            Button(
                                onClick = {
                                    val inviteText = context.getString(
                                        R.string.share_invite_template,
                                        uiState.currentList?.name ?: "",
                                        shareCode
                                    )
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, inviteText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.action_send_invite))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.hideShareDialog() }) {
                    Text(stringResource(R.string.action_close))
                }
            }
        )
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    isLocked: Boolean = false,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onLockedAttempt: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                if (isLocked) onLockedAttempt() else onToggle()
            }),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isCompleted,
                enabled = !isLocked,
                onCheckedChange = {
                    if (isLocked) onLockedAttempt() else onToggle()
                },
                modifier = Modifier.size(48.dp) // Accessibility: 48dp touch target
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (item.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            if (!isLocked) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
