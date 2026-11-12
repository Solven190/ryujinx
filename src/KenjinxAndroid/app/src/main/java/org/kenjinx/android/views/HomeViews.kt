package org.kenjinx.android.views

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.anggrayudi.storage.extension.launchOnUiThread
import java.util.Base64
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kenjinx.android.R
import org.kenjinx.android.viewmodels.FileType
import org.kenjinx.android.viewmodels.GameModel
import org.kenjinx.android.viewmodels.HomeViewModel
import org.kenjinx.android.viewmodels.QuickSettings
import org.kenjinx.android.widgets.SimpleAlertDialog
import org.kenjinx.android.ShortcutUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableFloatStateOf
import androidx.documentfile.provider.DocumentFile

// Cheats
import org.kenjinx.android.cheats.CheatPrefs
import org.kenjinx.android.cheats.CheatItem
import org.kenjinx.android.cheats.loadCheatsFromDisk
import org.kenjinx.android.cheats.applyCheatSelectionOnDisk
import org.kenjinx.android.cheats.importCheatTxt

// Mods
import org.kenjinx.android.cheats.listMods
import org.kenjinx.android.cheats.deleteMod
import org.kenjinx.android.cheats.importModsZip

// Saves
import org.kenjinx.android.saves.*

class HomeViews {
    companion object {
        const val ListImageSize = 150
        const val GridImageSize = 300

        // -- Helper for Shortcut Flow
        private fun resolveGameUri(gm: GameModel): Uri? = gm.file.uri

        private fun decodeGameIcon(gm: GameModel): Bitmap? {
            return try {
                val b64 = gm.icon ?: return null
                val bytes = Base64.getDecoder().decode(b64)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (_: Throwable) {
                null
            }
        }

        @Composable
        private fun HomeBottomBar(
            userPicture: ByteArray?,
            onSettingsClick: () -> Unit,
            onLibraryClick: () -> Unit,
            onUserClick: () -> Unit
        ) {
            val avatar = remember(userPicture) {
                userPicture?.let { BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap() }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                shape = RoundedCornerShape(32.dp),
                color = elevatedSurfaceColor().copy(alpha = 0.94f),
                shadowElevation = 12.dp,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeBottomBarItem(
                        label = "Settings",
                        selected = false,
                        onClick = onSettingsClick
                    ) { tint ->
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(26.dp),
                            tint = tint
                        )
                    }
                    HomeBottomBarItem(
                        label = "Library",
                        selected = true,
                        onClick = onLibraryClick
                    ) { tint ->
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Library",
                            modifier = Modifier.size(26.dp),
                            tint = tint
                        )
                    }
                    HomeBottomBarItem(
                        label = "User",
                        selected = false,
                        onClick = onUserClick
                    ) { tint ->
                        if (avatar != null) {
                            Image(
                                bitmap = avatar,
                                contentDescription = "user image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "User",
                                modifier = Modifier.size(26.dp),
                                tint = tint
                            )
                        }
                    }
                }
            }
        }

        @Composable
        private fun HomeBottomBarItem(
            label: String,
            selected: Boolean,
            onClick: () -> Unit,
            icon: @Composable (tint: Color) -> Unit
        ) {
            val contentColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
                        else Color.Transparent
                    )
                    .clickable(onClick = onClick)
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon(contentColor)
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = contentColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        @Composable
        private fun GameActionMenu(
            anchor: IntRect,
            groups: List<List<GameMenuAction>>,
            onDismissRequest: () -> Unit
        ) {
            val margin = with(LocalDensity.current) { 8.dp.roundToPx() }
            val maxHeight = (LocalConfiguration.current.screenHeightDp * 0.8f).dp

            Popup(
                popupPositionProvider = remember(anchor, margin) {
                    GameMenuPositionProvider(anchor, margin)
                },
                onDismissRequest = onDismissRequest,
                properties = PopupProperties(focusable = true)
            ) {
                Surface(
                    modifier = Modifier
                        .width(250.dp)
                        .heightIn(max = maxHeight),
                    shape = RoundedCornerShape(20.dp),
                    color = elevatedSurfaceColor().copy(alpha = 0.96f),
                    shadowElevation = 16.dp
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        groups.forEachIndexed { groupIndex, group ->
                            if (groupIndex > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                )
                            }
                            group.forEachIndexed { actionIndex, action ->
                                if (actionIndex > 0) {
                                    HorizontalDivider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    )
                                }
                                GameMenuRow(action)
                            }
                        }
                    }
                }
            }
        }

        @Composable
        private fun GameMenuRow(action: GameMenuAction) {
            val colors = MaterialTheme.colorScheme
            val contentColor = when {
                action.highlighted -> colors.primary
                action.destructive -> colors.error
                else -> colors.onSurface
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = action.onClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = action.label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    action.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
            }
        }

        @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
        @Composable
        fun Home(
            viewModel: HomeViewModel = HomeViewModel(),
            navController: NavHostController? = null,
            isPreview: Boolean = false
        ) {
            viewModel.ensureReloadIfNecessary()
            val showAppActions = remember { mutableStateOf(false) }
            val showLoading = remember { mutableStateOf(false) }
            val openTitleUpdateDialog = remember { mutableStateOf(false) }
            val canClose = remember { mutableStateOf(true) }
            val openDlcDialog = remember { mutableStateOf(false) }
            val showError = remember { mutableStateOf("") }
            val selectedModel = remember { mutableStateOf(viewModel.mainViewModel?.selected) }
            val query = remember { mutableStateOf("") }
            var refreshUser by remember { mutableStateOf(true) }
            var isFabVisible by remember { mutableStateOf(true) }
            val isNavigating = remember { mutableStateOf(false) }
            val menuAnchor = remember { mutableStateOf<IntRect?>(null) }
            val gridState = rememberLazyGridState()
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            // Amiibo slot picker state
            val showAmiiboSlotDialog = remember { mutableStateOf(false) }
            val pendingSlot = remember { mutableStateOf(1) }

            // Cheats UI state
            val openCheatsDialog = remember { mutableStateOf(false) }
            val cheatsForSelected = remember { mutableStateOf(listOf<CheatItem>()) }
            val enabledCheatKeys = remember { mutableStateOf(mutableSetOf<String>()) }

            // Mods UI state
            val openModsDialog = remember { mutableStateOf(false) }
            val modsForSelected = remember { mutableStateOf(listOf<String>()) }
            val modsImportProgress = remember { mutableFloatStateOf(0f) }
            val modsImportBusy = remember { mutableStateOf(false) }
            val modsImportStatusText = remember { mutableStateOf("") }

            // Save Manager State
            val openSavesDialog = remember { mutableStateOf(false) }
            val saveImportBusy = remember { mutableStateOf(false) }
            val saveExportBusy = remember { mutableStateOf(false) }
            val saveImportProgress = remember { mutableFloatStateOf(0f) }
            val saveExportProgress = remember { mutableFloatStateOf(0f) }
            val saveImportStatus = remember { mutableStateOf("") }
            val saveExportStatus = remember { mutableStateOf("") }

            val activity = LocalContext.current as? Activity

            // Import: OpenDocument (ZIP)
            val importZipLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                val act = activity
                // Guard on selected game – optional
                val tIdNow = viewModel.mainViewModel?.selected?.titleId.orEmpty()
                if (uri != null && act != null && tIdNow.isNotEmpty()) {
                    saveImportBusy.value = true
                    saveImportProgress.floatValue = 0f
                    saveImportStatus.value = "Starting…"

                    thread {
                        val res = importSaveFromZip(act, uri) { prog ->
                            val frac = if (prog.total > 0) prog.bytes.toFloat() / prog.total else 0f
                            saveImportProgress.floatValue = frac.coerceIn(0f, 1f)
                            saveImportStatus.value = "Importing: ${prog.currentEntry}"
                        }
                        saveImportBusy.value = false
                        launchOnUiThread {
                            Toast.makeText(act, res.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // Export: CreateDocument (ZIP)
            val exportZipLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/zip")
            ) { uri: Uri? ->
                val act = activity
                val tIdNow = viewModel.mainViewModel?.selected?.titleId.orEmpty()
                if (uri != null && act != null && tIdNow.isNotEmpty()) {
                    saveExportBusy.value = true
                    saveExportProgress.floatValue = 0f
                    saveExportStatus.value = "Starting…"

                    thread {
                        val res = exportSaveToZip(act, tIdNow, uri) { prog ->
                            val frac = if (prog.total > 0) prog.bytes.toFloat() / prog.total else 0f
                            saveExportProgress.floatValue = frac.coerceIn(0f, 1f)
                            saveExportStatus.value = "Exporting: ${prog.currentPath}"
                        }
                        saveExportBusy.value = false
                        launchOnUiThread {
                            Toast.makeText(
                                act,
                                if (res.ok) "save exported" else (res.error ?: "export failed"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            // Shortcut Dialog State
            val showShortcutDialog = remember { mutableStateOf(false) }
            val shortcutName = remember { mutableStateOf("") }

            val context = LocalContext.current

            // Amiibo Launcher (OpenDocument)
            val pickAmiiboLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                if (uri != null && activity != null) {
                    try {
                        activity.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: Exception) {}
                    val name = DocumentFile.fromSingleUri(activity, uri)?.name ?: "amiibo.bin"
                    val qs = QuickSettings(activity)
                    when (pendingSlot.value) {
                        1 -> { qs.amiibo1Uri = uri.toString(); qs.amiibo1Name = name }
                        2 -> { qs.amiibo2Uri = uri.toString(); qs.amiibo2Name = name }
                        3 -> { qs.amiibo3Uri = uri.toString(); qs.amiibo3Name = name }
                        4 -> { qs.amiibo4Uri = uri.toString(); qs.amiibo4Name = name }
                        5 -> { qs.amiibo5Uri = uri.toString(); qs.amiibo5Name = name }
                    }
                    qs.save()
                    Toast.makeText(activity, "Amiibo saved to slot ${pendingSlot.value}", Toast.LENGTH_SHORT).show()
                }
            }

            // Cheats Import (.txt)
            val importCheatLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                val gm = viewModel.mainViewModel?.selected
                val act = viewModel.activity
                val titleId = gm?.titleId ?: ""
                if (uri != null && act != null && titleId.isNotEmpty()) {
                    // Only accept .txt files
                    val okExt = runCatching {
                        DocumentFile.fromSingleUri(act, uri)?.name?.lowercase()?.endsWith(".txt") == true
                    }.getOrElse { false }
                    if (!okExt) {
                        Toast.makeText(act, "Please select a .txt file", Toast.LENGTH_SHORT).show()
                        return@rememberLauncherForActivityResult
                    }

                    val res = importCheatTxt(act, titleId, uri)
                    if (res.isSuccess) {
                        Toast.makeText(act, "Imported: ${res.getOrNull()?.name}", Toast.LENGTH_SHORT).show()
                        // Then update the list
                        cheatsForSelected.value = loadCheatsFromDisk(act, titleId)
                    } else {
                        Toast.makeText(act, "Import failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            // Launcher for Mods
            val pickModZipLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                val gm = viewModel.mainViewModel?.selected
                val act = viewModel.activity
                val titleId = gm?.titleId ?: ""
                if (uri != null && act != null && titleId.isNotEmpty()) {
                    // Persist permission (read)
                    try {
                        act.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (_: Exception) {}

                    modsImportBusy.value = true
                    modsImportProgress.floatValue = 0f
                    modsImportStatusText.value = "Starting…"

                    thread {
                        val res = importModsZip(
                            act,
                            titleId,
                            uri
                        ) { prog ->
                            modsImportProgress.floatValue = prog.fraction
                            modsImportStatusText.value = if (prog.currentEntry.isNotEmpty())
                                "Copying: ${prog.currentEntry}"
                            else
                                "Copying… ${(prog.fraction * 100).toInt()}%"
                        }

                        // Update list
                        modsForSelected.value = listMods(act, titleId)
                        modsImportBusy.value = false

                        launchOnUiThread {
                            val msg = if (res.ok)
                                "Imported: ${res.imported.joinToString(", ")}"
                            else
                                "Import failed"
                            Toast.makeText(act, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }


            // Launcher for "Custom icon" (OpenDocument)
            val pickImageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                val gm = viewModel.mainViewModel?.selected
                if (uri != null && gm != null && activity != null) {
                    val bmp = runCatching {
                        context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it) }
                    }.getOrNull()

                    val label = shortcutName.value.ifBlank { gm.titleName ?: "Start Game" }
                    val gameUri = resolveGameUri(gm)
                    if (gameUri != null) {
                        ShortcutUtils.persistReadWrite(activity, gameUri)

                        ShortcutUtils.pinShortcutForGame(
                            activity = activity,
                            gameUri = gameUri,
                            label = label,
                            iconBitmap = bmp
                        ) {

                        }
                    } else {
                        showError.value = "Shortcut failed (no game URI found)."
                    }
                }
            }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        if (available.y < -1) isFabVisible = false
                        if (available.y > 1) isFabVisible = true
                        return Offset.Zero
                    }
                }
            }

            val navigateTo: (String) -> Unit = { route ->
                if (!isNavigating.value) {
                    isNavigating.value = true
                    val currentRoute = navController?.currentDestination?.route
                    if (currentRoute != route) {
                        navController?.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        isNavigating.value = false
                    }
                }
            }

            val dismissAppActions = {
                showAppActions.value = false
                selectedModel.value = null
            }

            val userPicture = if (refreshUser) {
                viewModel.mainViewModel?.userViewModel?.openedUser?.userPicture?.takeIf { it.isNotEmpty() }
            } else {
                null
            }

            // --- Box around scaffold
            Box(Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp)
                        ) {
                            Text(
                                text = "Library",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = query.value,
                                onValueChange = { query.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                placeholder = {
                                    Text("Search", modifier = Modifier.padding(bottom = 4.dp))
                                },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    errorContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                )
                            )
                        }
                    },
                    bottomBar = {
                        HomeBottomBar(
                            userPicture = userPicture,
                            onSettingsClick = { navigateTo("settings") },
                            onLibraryClick = {
                                query.value = ""
                                coroutineScope.launch {
                                    val isGridLayout = viewModel.activity?.let { QuickSettings(it).isGrid } == true
                                    if (isGridLayout) {
                                        gridState.animateScrollToItem(0)
                                    } else {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                            },
                            onUserClick = { navigateTo("user") }
                        )
                    },
                    floatingActionButton = {
                        AnimatedVisibility(visible = isFabVisible) {
                            // Import Amiibo
                            FloatingActionButton(
                                onClick = { showAmiiboSlotDialog.value = true },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Icon(
                                    org.kenjinx.android.Icons.folderOpen(MaterialTheme.colorScheme.onSurface),
                                    contentDescription = "Import Amiibo"
                                )
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End
                ) { contentPadding ->
                    Column(modifier = Modifier.padding(contentPadding)) {
                        val list = remember { viewModel.gameList }
                        val isLoading = remember { viewModel.isLoading }

                        PullToRefreshBox(
                            isRefreshing = isLoading.value,
                            onRefresh = {
                                viewModel.requestReload()
                                viewModel.ensureReloadIfNecessary()
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            viewModel.filter(query.value)

                            if (!isPreview) {
                                val settings = QuickSettings(viewModel.activity!!)
                                if (isLoading.value) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .width(64.dp)
                                                .align(Alignment.Center),
                                            color = MaterialTheme.colorScheme.secondary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                } else {
                                    if (settings.isGrid) {
                                        val size = GridImageSize / Resources.getSystem().displayMetrics.density
                                        LazyVerticalGrid(
                                            columns = GridCells.Adaptive(minSize = (size + 4).dp),
                                            state = gridState,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(4.dp)
                                                .nestedScroll(nestedScrollConnection),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            items(list) {
                                                it.titleName?.apply {
                                                    if (this.isNotEmpty() && (query.value.trim()
                                                            .isEmpty() || this.lowercase(Locale.getDefault())
                                                            .contains(query.value))) {
                                                        GridGameItem(
                                                            it,
                                                            viewModel,
                                                            showAppActions,
                                                            showLoading,
                                                            selectedModel,
                                                            showError,
                                                            menuAnchor
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        LazyColumn(Modifier.fillMaxSize(), state = listState) {
                                            items(list) {
                                                it.titleName?.apply {
                                                    if (this.isNotEmpty() && (query.value.trim()
                                                            .isEmpty() || this.lowercase(Locale.getDefault())
                                                            .contains(query.value))) {
                                                        Box(modifier = Modifier.animateItem()) {
                                                            ListGameItem(
                                                                it,
                                                                viewModel,
                                                                showAppActions,
                                                                showLoading,
                                                                selectedModel,
                                                                showError,
                                                                menuAnchor
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SimpleAlertDialog.Loading(showDialog = showLoading)
                    SimpleAlertDialog.Custom(
                        showDialog = openTitleUpdateDialog,
                        onDismissRequest = { openTitleUpdateDialog.value = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        val titleId = viewModel.mainViewModel?.selected?.titleId ?: ""
                        val name = viewModel.mainViewModel?.selected?.titleName ?: ""
                        TitleUpdateViews.Main(titleId, name, openTitleUpdateDialog, canClose)
                    }
                    SimpleAlertDialog.Custom(
                        showDialog = openDlcDialog,
                        onDismissRequest = { openDlcDialog.value = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        val titleId = viewModel.mainViewModel?.selected?.titleId ?: ""
                        val name = viewModel.mainViewModel?.selected?.titleName ?: ""
                        DlcViews.Main(titleId, name, openDlcDialog, canClose)
                    }

                    // Amiibo slot chooser dialog (outside of game)
                    if (showAmiiboSlotDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showAmiiboSlotDialog.value = false },
                            title = { Text("Import Amiibo") },
                            text = {
                                Column {
                                    Text("Choose a slot to save this Amiibo:", modifier = Modifier.padding(bottom = 8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TextButton(onClick = {
                                            pendingSlot.value = 1
                                            pickAmiiboLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                                            showAmiiboSlotDialog.value = false
                                        }) { Text("Slot 1") }
                                        TextButton(onClick = {
                                            pendingSlot.value = 2
                                            pickAmiiboLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                                            showAmiiboSlotDialog.value = false
                                        }) { Text("Slot 2") }
                                        TextButton(onClick = {
                                            pendingSlot.value = 3
                                            pickAmiiboLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                                            showAmiiboSlotDialog.value = false
                                        }) { Text("Slot 3") }
                                    }
                                    Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TextButton(onClick = {
                                            pendingSlot.value = 4
                                            pickAmiiboLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                                            showAmiiboSlotDialog.value = false
                                        }) { Text("Slot 4") }
                                        TextButton(onClick = {
                                            pendingSlot.value = 5
                                            pickAmiiboLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                                            showAmiiboSlotDialog.value = false
                                        }) { Text("Slot 5") }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showAmiiboSlotDialog.value = false }) { Text("Close") }
                            }
                        )
                    }
                }

                if (viewModel.mainViewModel?.loadGameModel?.value != null)
                    LaunchedEffect(viewModel.mainViewModel.loadGameModel.value) {
                        if (viewModel.mainViewModel.bootPath.value ==
                            "gameItem_${viewModel.mainViewModel.loadGameModel.value!!.titleName}"
                        ) {
                            viewModel.mainViewModel.bootPath.value = null

                            thread {
                                showLoading.value = true

                                // Push cheats before starting (Auto-start path)
                                val gm = viewModel.mainViewModel.loadGameModel.value!!
                                val tId = gm.titleId ?: ""
                                val act = viewModel.activity

                                val success = viewModel.mainViewModel.loadGame(
                                    gm,
                                    true,
                                    viewModel.mainViewModel.forceNceAndPptc.value
                                )
                                if (success == 1) {
                                    launchOnUiThread {
                                        viewModel.mainViewModel.navigateToGame()
                                    }
                                } else {
                                    if (success == -2)
                                        showError.value = "Error loading update. Please re-add update file"
                                    viewModel.mainViewModel.loadGameModel.value!!.close()
                                }
                                showLoading.value = false
                            }
                        }
                    }

                if (showAppActions.value) {
                    val anchorBounds = menuAnchor.value
                    if (anchorBounds != null) {
                        GameActionMenu(
                            anchor = anchorBounds,
                            onDismissRequest = dismissAppActions,
                            groups = listOf(
                                listOf(
                                    GameMenuAction(
                                        label = "Start game",
                                        icon = Icons.Filled.PlayArrow,
                                        highlighted = true,
                                        onClick = {
                                            dismissAppActions()
                                            if (viewModel.mainViewModel?.selected != null) {

                                                // Press the cheat buttons before starting (Run button).
                                                val gmSel = viewModel.mainViewModel.selected!!
                                                val tId = gmSel.titleId ?: ""
                                                val act = viewModel.activity

                                                thread {
                                                    showLoading.value = true
                                                    val success = viewModel.mainViewModel.loadGame(
                                                        gmSel
                                                    )
                                                    if (success == 1) {
                                                        launchOnUiThread {
                                                            viewModel.mainViewModel.navigateToGame()
                                                        }
                                                    } else {
                                                        if (success == -2)
                                                            showError.value =
                                                                "Error loading update. Please re-add update file"
                                                        viewModel.mainViewModel.selected!!.close()
                                                    }
                                                    showLoading.value = false
                                                }
                                            }
                                        }
                                    )
                                ),
                                listOf(
                                    GameMenuAction(
                                        label = "Manage Updates",
                                        icon = Icons.Filled.Refresh,
                                        onClick = {
                                            dismissAppActions()
                                            openTitleUpdateDialog.value = true
                                        }
                                    ),
                                    GameMenuAction(
                                        label = "Manage DLC",
                                        icon = Icons.Filled.ShoppingCart,
                                        onClick = {
                                            dismissAppActions()
                                            openDlcDialog.value = true
                                        }
                                    ),
                                    // Manage Cheats
                                    GameMenuAction(
                                        label = "Manage Cheats",
                                        icon = Icons.Filled.Star,
                                        onClick = {
                                            dismissAppActions()
                                            val gm = viewModel.mainViewModel?.selected
                                            val act = viewModel.activity
                                            if (gm != null && !gm.titleId.isNullOrEmpty() && act != null) {
                                                val titleId = gm.titleId!!
                                                cheatsForSelected.value = loadCheatsFromDisk(act, titleId)
                                                enabledCheatKeys.value = CheatPrefs(act).getEnabled(titleId)
                                                openCheatsDialog.value = true
                                            } else {
                                                showError.value = "No title selected."
                                            }
                                        }
                                    ),
                                    // Manage Mods
                                    GameMenuAction(
                                        label = "Manage Mods",
                                        icon = Icons.Filled.Build,
                                        onClick = {
                                            dismissAppActions()
                                            val gm = viewModel.mainViewModel?.selected
                                            val act = viewModel.activity
                                            if (gm != null && !gm.titleId.isNullOrEmpty() && act != null) {
                                                val titleId = gm.titleId!!
                                                modsForSelected.value = listMods(act, titleId)
                                                openModsDialog.value = true
                                            } else {
                                                showError.value = "No title selected."
                                            }
                                        }
                                    ),
                                    GameMenuAction(
                                        label = "Manage Saves",
                                        icon = Icons.Filled.Share,
                                        onClick = {
                                            dismissAppActions()
                                            openSavesDialog.value = true
                                        }
                                    )
                                ),
                                listOf(
                                    // Create Shortcut
                                    GameMenuAction(
                                        label = "Create shortcut",
                                        icon = Icons.Filled.Add,
                                        onClick = {
                                            dismissAppActions()
                                            val gm = viewModel.mainViewModel?.selected
                                            if (gm != null) {
                                                shortcutName.value = gm.titleName ?: ""
                                                showShortcutDialog.value = true
                                            }
                                        }
                                    )
                                ),
                                listOf(
                                    GameMenuAction(
                                        label = "Clear PPTC Cache",
                                        icon = Icons.Filled.Clear,
                                        destructive = true,
                                        onClick = {
                                            dismissAppActions()
                                            viewModel.mainViewModel?.clearPptcCache(
                                                viewModel.mainViewModel.selected?.titleId ?: ""
                                            )
                                        }
                                    ),
                                    GameMenuAction(
                                        label = "Purge Shader Cache",
                                        icon = Icons.Filled.Clear,
                                        destructive = true,
                                        onClick = {
                                            dismissAppActions()
                                            viewModel.mainViewModel?.purgeShaderCache(
                                                viewModel.mainViewModel.selected?.titleId ?: ""
                                            )
                                        }
                                    ),
                                    GameMenuAction(
                                        label = "Delete All Cache",
                                        icon = Icons.Filled.Delete,
                                        destructive = true,
                                        onClick = {
                                            dismissAppActions()
                                            viewModel.mainViewModel?.deleteCache(
                                                viewModel.mainViewModel.selected?.titleId ?: ""
                                            )
                                        }
                                    )
                                )
                            )
                        )
                    }
                }

                // --- Cheats Bottom Sheet ---
                if (openCheatsDialog.value) {
                    ModalBottomSheet(
                        onDismissRequest = { openCheatsDialog.value = false }
                    ) {
                        val gm = viewModel.mainViewModel?.selected
                        val act = viewModel.activity
                        val titleId = gm?.titleId ?: ""

                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // LINKS: Import .txt
                                TextButton(onClick = {
                                    importCheatLauncher.launch(arrayOf("text/plain", "text/*", "*/*"))
                                }) { Text("Import .txt") }

                                // RIGHT: Cancel + Save
                                Row {
                                    TextButton(onClick = { openCheatsDialog.value = false }) { Text("Cancel") }
                                    TextButton(onClick = {
                                        val act2 = act
                                        if (act2 != null && titleId.isNotEmpty()) {
                                            CheatPrefs(act2).setEnabled(titleId, enabledCheatKeys.value)
                                            applyCheatSelectionOnDisk(act2, titleId, enabledCheatKeys.value)
                                            cheatsForSelected.value = loadCheatsFromDisk(act2, titleId)
                                        }
                                        openCheatsDialog.value = false
                                    }) { Text("Save") }
                                }
                            }

                            Text("Manage Cheats", style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = gm?.titleName ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            if (cheatsForSelected.value.isEmpty()) {
                                Text("No cheats found for this title.")
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    items(cheatsForSelected.value) { cheat ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(
                                                Modifier
                                                    .weight(1f)
                                                    .padding(end = 12.dp)
                                            ) {
                                                Text(cheat.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                                Text(
                                                    cheat.buildId,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            }
                                            val checked = enabledCheatKeys.value.contains(cheat.key)
                                            androidx.compose.material3.Switch(
                                                checked = checked,
                                                onCheckedChange = { isOn ->
                                                    enabledCheatKeys.value =
                                                        enabledCheatKeys.value.toMutableSet().apply {
                                                            if (isOn) add(cheat.key) else remove(cheat.key)
                                                        }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- Mods Bottom Sheet ---
                if (openModsDialog.value) {
                    ModalBottomSheet(
                        onDismissRequest = { openModsDialog.value = false }
                    ) {
                        val gm = viewModel.mainViewModel?.selected
                        val act = viewModel.activity
                        val titleId = gm?.titleId ?: ""

                        Column(Modifier.padding(16.dp)) {
                            Text("Manage Mods", style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = gm?.titleName ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Import line
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    enabled = !modsImportBusy.value,
                                    onClick = { pickModZipLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*")) }
                                ) { Text("Import .zip") }
                            }

                            // Progress
                            if (modsImportBusy.value) {
                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = { modsImportProgress.value },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                )
                                Text(
                                    modsImportStatusText.value,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            // List of mods
                            if (modsForSelected.value.isEmpty()) {
                                Text("No mods found for this title.")
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    items(modsForSelected.value) { modName ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(modName, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                            Row {
                                                TextButton(
                                                    onClick = {
                                                        val a = act
                                                        if (a != null && titleId.isNotEmpty()) {
                                                            thread {
                                                                val ok = deleteMod(a, titleId, modName)
                                                                if (ok) {
                                                                    modsForSelected.value = listMods(a, titleId)
                                                                }
                                                            }
                                                        }
                                                    }
                                                ) { Text("Delete") }
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { openModsDialog.value = false }) { Text("Close") }
                            }
                        }
                    }
                }

                // --- Saves Bottom Sheet ---
                if (openSavesDialog.value) {
                    ModalBottomSheet(
                        onDismissRequest = { openSavesDialog.value = false }
                    ) {
                        val act = activity

                        Column(Modifier.padding(16.dp)) {
                            Text("Save Manager", style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = viewModel.mainViewModel?.selected?.titleName ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                // Import Button
                                androidx.compose.material3.Button(
                                    enabled = !saveImportBusy.value && !saveExportBusy.value &&
                                        (viewModel.mainViewModel?.selected?.titleId?.isNotEmpty() == true),
                                    onClick = {
                                        saveImportProgress.value = 0f
                                        saveImportStatus.value = ""
                                        importZipLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))
                                    }
                                ) { Text("Import ZIP") }

                                // Export Button
                                androidx.compose.material3.Button(
                                    enabled = !saveImportBusy.value && !saveExportBusy.value &&
                                        (viewModel.mainViewModel?.selected?.titleId?.isNotEmpty() == true),
                                    onClick = {
                                        val actLocal = activity
                                        val tIdNow = viewModel.mainViewModel?.selected?.titleId.orEmpty()
                                        if (actLocal != null && tIdNow.isNotEmpty()) {
                                            val fname = suggestedCreateDocNameForExport(actLocal, tIdNow)
                                            saveExportProgress.value = 0f
                                            saveExportStatus.value = ""
                                            exportZipLauncher.launch(fname)
                                        }
                                    }
                                ) { Text("Export ZIP") }
                            }

                            if (saveImportBusy.value) {
                                Column(Modifier.padding(top = 12.dp)) {
                                    androidx.compose.material3.LinearProgressIndicator(progress = { saveImportProgress.value })
                                    Text(saveImportStatus.value, modifier = Modifier.padding(top = 6.dp))
                                }
                            }
                            if (saveExportBusy.value) {
                                Column(Modifier.padding(top = 12.dp)) {
                                    androidx.compose.material3.LinearProgressIndicator(progress = { saveExportProgress.value })
                                    Text(saveExportStatus.value, modifier = Modifier.padding(top = 6.dp))
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { openSavesDialog.value = false }
                                ) { Text("Close") }
                            }
                        }
                    }
                }

                // --- Shortcut Dialog
                if (showShortcutDialog.value) {
                    val gm = viewModel.mainViewModel?.selected
                    AlertDialog(
                        onDismissRequest = { showShortcutDialog.value = false },
                        title = { Text("Create shortcut") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = shortcutName.value,
                                    onValueChange = { shortcutName.value = it },
                                    label = { Text("Name") },
                                    singleLine = true
                                )
                                Text(
                                    text = "Choose icon:",
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    TextButton(onClick = {
                                        // App icon (Grid image)
                                        if (gm != null && activity != null) {
                                            val gameUri = resolveGameUri(gm)
                                            if (gameUri != null) {
                                                // Persist rights for the game file
                                                ShortcutUtils.persistReadWrite(activity, gameUri)

                                                val bmp = decodeGameIcon(gm)
                                                val label = shortcutName.value.ifBlank { gm.titleName ?: "Start Game" }

                                                ShortcutUtils.pinShortcutForGame(
                                                    activity = activity,
                                                    gameUri = gameUri,
                                                    label = label,
                                                    iconBitmap = bmp
                                                ) { }
                                                showShortcutDialog.value = false
                                            } else {
                                                showShortcutDialog.value = false
                                            }
                                        } else {
                                            showShortcutDialog.value = false
                                        }
                                    }) { Text("App icon") }

                                    TextButton(onClick = {
                                        // Custom icon: open picker
                                        pickImageLauncher.launch(arrayOf("image/*"))
                                        showShortcutDialog.value = false
                                    }) { Text("Custom icon") }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showShortcutDialog.value = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
            } // End of box
        }

        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        fun ListGameItem(
            gameModel: GameModel,
            viewModel: HomeViewModel,
            showAppActions: MutableState<Boolean>,
            showLoading: MutableState<Boolean>,
            selectedModel: MutableState<GameModel?>,
            showError: MutableState<String>,
            menuAnchor: MutableState<IntRect?>
        ) {
            remember { selectedModel }
            val color =
                if (selectedModel.value == gameModel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

            val decoder = Base64.getDecoder()
            var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onGloballyPositioned { coordinates = it }
                    .combinedClickable(
                        onClick = {
                            if (viewModel.mainViewModel?.selected != null) {
                                showAppActions.value = false
                                viewModel.mainViewModel.apply { selected = null }
                                selectedModel.value = null
                            } else if (gameModel.titleId.isNullOrEmpty()
                                || gameModel.titleId != "0000000000000000"
                                || gameModel.type == FileType.Nro
                            ) {
                                thread {
                                    showLoading.value = true

                                    // Push the cheats before starting
                                    val tId = gameModel.titleId ?: ""
                                    val act = viewModel.activity

                                    val success = viewModel.mainViewModel?.loadGame(gameModel) ?: false
                                    if (success == 1) {
                                        launchOnUiThread { viewModel.mainViewModel?.navigateToGame() }
                                    } else {
                                        if (success == -2)
                                            showError.value =
                                                "Error loading update. Please re-add update file"
                                        gameModel.close()
                                    }
                                    showLoading.value = false
                                }
                            }
                        },
                        onLongClick = {
                            menuAnchor.value = coordinates?.takeIf { it.isAttached }?.toWindowIntRect()
                            viewModel.mainViewModel?.selected = gameModel
                            showAppActions.value = true
                            selectedModel.value = gameModel
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        if (!gameModel.titleId.isNullOrEmpty()
                            && (gameModel.titleId != "0000000000000000" || gameModel.type == FileType.Nro)
                        ) {
                            if (gameModel.icon?.isNotEmpty() == true) {
                                val pic = decoder.decode(gameModel.icon)
                                val size = ListImageSize / Resources.getSystem().displayMetrics.density
                                Image(
                                    bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.size).asImageBitmap(),
                                    contentDescription = gameModel.titleName + " icon",
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .width(size.roundToInt().dp)
                                        .height(size.roundToInt().dp)
                                )
                            } else if (gameModel.type == FileType.Nro) NROIcon()
                            else NotAvailableIcon()
                        } else NotAvailableIcon()
                        Column {
                            Text(text = gameModel.titleName ?: "")
                            Text(text = gameModel.developer ?: "")
                            Text(text = gameModel.titleId ?: "")
                        }
                    }
                    Column {
                        Text(text = gameModel.version ?: "")
                        Text(text = String.format(Locale.getDefault(), "%.3f", gameModel.fileSize))
                    }
                }
            }
        }

        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        fun GridGameItem(
            gameModel: GameModel,
            viewModel: HomeViewModel,
            showAppActions: MutableState<Boolean>,
            showLoading: MutableState<Boolean>,
            selectedModel: MutableState<GameModel?>,
            showError: MutableState<String>,
            menuAnchor: MutableState<IntRect?>
        ) {
            remember { selectedModel }
            val color =
                if (selectedModel.value == gameModel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

            val decoder = Base64.getDecoder()
            var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onGloballyPositioned { coordinates = it }
                    .combinedClickable(
                        onClick = {
                            if (viewModel.mainViewModel?.selected != null) {
                                showAppActions.value = false
                                viewModel.mainViewModel.apply { selected = null }
                                selectedModel.value = null
                            } else if (gameModel.titleId.isNullOrEmpty()
                                || gameModel.titleId != "0000000000000000"
                                || gameModel.type == FileType.Nro
                            ) {
                                thread {
                                    showLoading.value = true

                                    // Push the cheats before starting
                                    val tId = gameModel.titleId ?: ""
                                    val act = viewModel.activity

                                    val success = viewModel.mainViewModel?.loadGame(gameModel) ?: false
                                    if (success == 1) {
                                        launchOnUiThread { viewModel.mainViewModel?.navigateToGame() }
                                    } else {
                                        if (success == -2)
                                            showError.value =
                                                "Error loading update. Please re-add update file"
                                        gameModel.close()
                                    }
                                    showLoading.value = false
                                }
                            }
                        },
                        onLongClick = {
                            menuAnchor.value = coordinates?.takeIf { it.isAttached }?.toWindowIntRect()
                            viewModel.mainViewModel?.selected = gameModel
                            showAppActions.value = true
                            selectedModel.value = gameModel
                        }
                    )
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    if (!gameModel.titleId.isNullOrEmpty()
                        && (gameModel.titleId != "0000000000000000" || gameModel.type == FileType.Nro)
                    ) {
                        if (gameModel.icon?.isNotEmpty() == true) {
                            val pic = decoder.decode(gameModel.icon)
                            Image(
                                bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.size).asImageBitmap(),
                                contentDescription = gameModel.titleName + " icon",
                                modifier = Modifier
                                    .padding(0.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .align(Alignment.CenterHorizontally)
                            )
                        } else if (gameModel.type == FileType.Nro) NROIcon()
                        else NotAvailableIcon()
                    } else NotAvailableIcon()
                    Text(
                        text = gameModel.titleName ?: "N/A",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .basicMarquee()
                    )
                }
            }
        }

        @Composable
        fun NotAvailableIcon() {
            val size = ListImageSize / Resources.getSystem().displayMetrics.density
            Icon(
                Icons.Filled.Add,
                contentDescription = "N/A",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(size.roundToInt().dp)
                    .height(size.roundToInt().dp)
            )
        }

        @Composable
        fun NROIcon() {
            val size = ListImageSize / Resources.getSystem().displayMetrics.density
            Image(
                painter = painterResource(id = R.drawable.icon_nro),
                contentDescription = "NRO",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(size.roundToInt().dp)
                    .height(size.roundToInt().dp)
            )
        }
    }

    @Preview
    @Composable
    fun HomePreview() {
        Home(isPreview = true)
    }
}

internal data class GameMenuAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val highlighted: Boolean = false,
    val destructive: Boolean = false
)

@Composable
private fun elevatedSurfaceColor(): Color =
    lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface, 0.08f)

private fun LayoutCoordinates.toWindowIntRect(): IntRect {
    val bounds = boundsInWindow()
    return IntRect(
        bounds.left.roundToInt(),
        bounds.top.roundToInt(),
        bounds.right.roundToInt(),
        bounds.bottom.roundToInt()
    )
}

private class GameMenuPositionProvider(
    private val anchor: IntRect,
    private val margin: Int
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val maxY = windowSize.height - popupContentSize.height - margin
        val fitsRight = anchor.right + margin + popupContentSize.width <= windowSize.width - margin
        val fitsLeft = anchor.left - margin - popupContentSize.width >= margin

        return when {
            fitsRight -> IntOffset(
                anchor.right + margin,
                anchor.top.coerceIn(margin, maxY.coerceAtLeast(margin))
            )
            fitsLeft -> IntOffset(
                anchor.left - margin - popupContentSize.width,
                anchor.top.coerceIn(margin, maxY.coerceAtLeast(margin))
            )
            else -> {
                val maxX = windowSize.width - popupContentSize.width - margin
                val x = (anchor.center.x - popupContentSize.width / 2)
                    .coerceIn(margin, maxX.coerceAtLeast(margin))
                val below = anchor.bottom + margin
                val y = if (below + popupContentSize.height + margin <= windowSize.height) {
                    below
                } else {
                    (anchor.top - margin - popupContentSize.height).coerceAtLeast(margin)
                }
                IntOffset(x, y)
            }
        }
    }
}