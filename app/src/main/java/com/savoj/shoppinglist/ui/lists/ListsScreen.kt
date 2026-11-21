package com.savoj.shoppinglist.ui.lists

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savoj.shoppinglist.R
import com.savoj.shoppinglist.domain.model.ShoppingList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    viewModel: ListsViewModel,
    onNavigateToListDetails: (Long) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val lists by viewModel.lists.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var newListName by remember { mutableStateOf("") }
    var editListName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lists_screen_title), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.showJoinDialog() }) {
                        Icon(Icons.Default.GroupAdd, contentDescription = stringResource(R.string.action_join_list))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.action_settings))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (uiState.selectedTab == 1) {
                        viewModel.showCreateWishlistDialog()
                    } else {
                        newListName = ""
                        viewModel.showCreateDialog()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = if (uiState.selectedTab == 1) Icons.Default.CardGiftcard else Icons.Default.Add,
                    contentDescription = if (uiState.selectedTab == 1) stringResource(R.string.create_wishlist_title) else stringResource(R.string.lists_create_button)
                )
            }
        }
    ) { paddingValues ->
        val displayedLists = remember(lists, uiState.selectedTab) {
            if (uiState.selectedTab == 1) {
                lists.filter { it.isWishlist }
            } else {
                lists.filter { !it.isWishlist }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    text = { Text(stringResource(R.string.tab_shopping_lists), fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    text = { Text(stringResource(R.string.tab_wishlists), fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            if (displayedLists.isEmpty()) {
                EmptyListsView(
                    isWishlist = uiState.selectedTab == 1,
                    onCreateClick = {
                        if (uiState.selectedTab == 1) {
                            viewModel.showCreateWishlistDialog()
                        } else {
                            newListName = ""
                            viewModel.showCreateDialog()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedLists, key = { it.id }) { list ->
                        ShoppingListCard(
                            list = list,
                            onClick = { onNavigateToListDetails(list.id) },
                            onEditClick = {
                                editListName = list.name
                                viewModel.showEditDialog(list)
                            },
                            onDeleteClick = { viewModel.showDeleteDialog(list) },
                            onResetClick = { viewModel.resetAllItems(list.id) },
                            onShareClick = { viewModel.showShareDialog(list) },
                            onToggleLockClick = { viewModel.toggleListLocked(list) }
                        )
                    }
                }
            }

            // Non-intrusive Ad Banner placeholder on main screen only (Zero ads in active shopping details)
            AdBannerPlaceholder()
        }
    }

    // Create Wishlist Dialog
    if (uiState.showCreateWishlistDialog) {
        var wishlistName by remember { mutableStateOf("") }
        val occasions = listOf(
            stringResource(R.string.occasion_birthday),
            stringResource(R.string.occasion_new_year),
            stringResource(R.string.occasion_wedding),
            stringResource(R.string.occasion_housewarming),
            stringResource(R.string.occasion_other)
        )
        var selectedOccasion by remember { mutableStateOf(occasions.first()) }

        AlertDialog(
            onDismissRequest = { viewModel.hideCreateWishlistDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.create_wishlist_title), fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = wishlistName,
                        onValueChange = { wishlistName = it },
                        label = { Text(stringResource(R.string.wishlist_name_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.wishlist_occasion_label),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(occasions) { occasion ->
                            FilterChip(
                                selected = selectedOccasion == occasion,
                                onClick = { selectedOccasion = occasion },
                                label = { Text(occasion, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.createWishlist(wishlistName, selectedOccasion) },
                    enabled = wishlistName.isNotBlank()
                ) {
                    Text(stringResource(R.string.action_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideCreateWishlistDialog() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Create List Dialog
    if (uiState.showCreateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideCreateDialog() },
            title = { Text(stringResource(R.string.dialog_new_list_title)) },
            text = {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text(stringResource(R.string.list_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.createList(newListName) },
                    enabled = newListName.isNotBlank()
                ) {
                    Text(stringResource(R.string.action_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideCreateDialog() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Edit List Dialog
    uiState.listToEdit?.let { targetList ->
        AlertDialog(
            onDismissRequest = { viewModel.hideEditDialog() },
            title = { Text(stringResource(R.string.dialog_edit_list_title)) },
            text = {
                OutlinedTextField(
                    value = editListName,
                    onValueChange = { editListName = it },
                    label = { Text(stringResource(R.string.list_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateList(targetList, editListName) },
                    enabled = editListName.isNotBlank()
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideEditDialog() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Delete List Dialog
    uiState.listToDelete?.let { targetList ->
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text(stringResource(R.string.dialog_delete_list_title)) },
            text = { Text(stringResource(R.string.dialog_delete_list_message)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteList(targetList.id) }
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Join Shared List Dialog
    if (uiState.showJoinDialog) {
        var codeInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.hideJoinDialog() },
            title = { Text(stringResource(R.string.action_join_list), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.join_list_desc),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = {
                            if (it.length <= 6) codeInput = it.uppercase()
                        },
                        label = { Text(stringResource(R.string.join_list_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.joinSharedList(codeInput) { joinedId ->
                            onNavigateToListDetails(joinedId)
                        }
                    },
                    enabled = codeInput.trim().length == 6 && !uiState.isJoining
                ) {
                    if (uiState.isJoining) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.join_btn))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideJoinDialog() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Share Code Dialog for list on main screen
    uiState.listToShare?.let { sharedList ->
        val shareCode = sharedList.shareCode ?: ""
        AlertDialog(
            onDismissRequest = { viewModel.hideShareDialog() },
            title = {
                Text(
                    text = "${stringResource(R.string.share_list_title)}: ${sharedList.name}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
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
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clip = ClipData.newPlainText("Share Code", shareCode)
                                clipboard?.setPrimaryClip(clip)
                                Toast.makeText(context, context.getString(R.string.toast_code_copied), Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.action_copy_code))
                        }
                        Button(
                            onClick = {
                                val templateRes = if (sharedList.isWishlist) {
                                    R.string.wishlist_invite_template
                                } else {
                                    R.string.share_invite_template
                                }
                                val inviteText = context.getString(
                                    templateRes,
                                    sharedList.name,
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
fun ShoppingListCard(
    list: ShoppingList,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onResetClick: () -> Unit,
    onShareClick: () -> Unit,
    onToggleLockClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val progress = if (list.totalItems > 0) list.completedItems.toFloat() / list.totalItems else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = list.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (list.isWishlist) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = list.occasion ?: stringResource(R.string.badge_wishlist),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                        if (list.isLocked) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(R.string.badge_locked),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                        if (list.isShared && list.shareCode != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.clickable { onShareClick() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Group,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${stringResource(R.string.badge_shared)}: ${list.shareCode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onShareClick) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.menu_share_list),
                            tint = if (list.isShared) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            if (list.isLocked) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_unlock_list)) },
                                    leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        onToggleLockClick()
                                    }
                                )
                            } else if (list.totalItems > 0 && list.completedItems == list.totalItems) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_close_list)) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        onToggleLockClick()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_share_list)) },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onShareClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_reset_all_items)) },
                                onClick = {
                                    menuExpanded = false
                                    onResetClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_edit)) },
                                onClick = {
                                    menuExpanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_delete)) },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

        Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.progress_format, list.completedItems, list.totalItems),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyListsView(
    isWishlist: Boolean = false,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isWishlist) Icons.Default.CardGiftcard else Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(if (isWishlist) R.string.wishlist_empty_title else R.string.lists_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(if (isWishlist) R.string.wishlist_empty_desc else R.string.lists_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onCreateClick) {
            Icon(if (isWishlist) Icons.Default.CardGiftcard else Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(if (isWishlist) R.string.action_create_wishlist_btn else R.string.lists_create_button))
        }
    }
}

@Composable
fun AdBannerPlaceholder() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ad Space (Safe, Non-intrusive)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
