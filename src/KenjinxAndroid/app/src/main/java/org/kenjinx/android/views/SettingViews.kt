package org.kenjinx.android.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.extension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import org.kenjinx.android.MainActivity
import org.kenjinx.android.providers.DocumentProvider
import org.kenjinx.android.viewmodels.DataImportState
import org.kenjinx.android.viewmodels.DataResetState
import org.kenjinx.android.viewmodels.FirmwareInstallState
import org.kenjinx.android.viewmodels.KeyInstallState
import org.kenjinx.android.viewmodels.MainViewModel
import org.kenjinx.android.viewmodels.SettingsViewModel
import org.kenjinx.android.viewmodels.MemoryConfiguration
import org.kenjinx.android.viewmodels.MemoryManagerMode
import org.kenjinx.android.viewmodels.VSyncMode
import org.kenjinx.android.widgets.SimpleAlertDialog

// >>> QuickSettings + OrientationPreference
import org.kenjinx.android.viewmodels.QuickSettings
import org.kenjinx.android.viewmodels.QuickSettings.OrientationPreference
import org.kenjinx.android.viewmodels.QuickSettings.OverlayMenuPosition // ← NEU

// Import enums
import org.kenjinx.android.SystemLanguage
import org.kenjinx.android.RegionCode

import org.kenjinx.android.viewmodels.QuickSettings.VirtualControllerPreset

// >>> KenjinxNative for live switching of the threading backend
import org.kenjinx.android.KenjinxNative

class SettingViews {
    companion object {
        const val EXPANSTION_TRANSITION_DURATION = 450
        const val IMPORT_CODE = 12341

        @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
        @Composable
        fun Main(settingsViewModel: SettingsViewModel, mainViewModel: MainViewModel) {
            var selectedCategoryIndex by remember { mutableIntStateOf(0) }
            var isSearchActive by remember { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf("") }
            
            val loaded = remember { mutableStateOf(false) }
            val memoryManagerMode = remember { mutableStateOf(MemoryManagerMode.HostMappedUnsafe) }
            val useNce = remember { mutableStateOf(false)  }
            val memoryConfiguration = remember { mutableStateOf(MemoryConfiguration.MemoryConfiguration4GiB)  }
            val vSyncMode = remember { mutableStateOf(VSyncMode.Switch) }
            val enableDocked = remember { mutableStateOf(false) }
            val enablePptc = remember { mutableStateOf(false) }
            val enableLowPowerPptc = remember { mutableStateOf(false) }
            val enableJitCacheEviction = remember { mutableStateOf(false) }
            val enableFsIntegrityChecks = remember { mutableStateOf(false) }
            val fsGlobalAccessLogMode = remember { mutableIntStateOf(0) }
            val ignoreMissingServices = remember { mutableStateOf(false) }
            val enableShaderCache = remember { mutableStateOf(false) }
            val enableTextureRecompression = remember { mutableStateOf(false) }
            val enableMacroHLE = remember { mutableStateOf(false) }
            val stretchToFullscreen = remember { mutableStateOf(false) }
            val resScale = remember { mutableFloatStateOf(1f) }
            val maxAnisotropy = remember { mutableFloatStateOf(0f) }
            val useVirtualController = remember { mutableStateOf(true) }
            val showKeyDialog = remember { mutableStateOf(false) }
            val keyInstallState = remember { mutableStateOf(KeyInstallState.File) }
            val showFirwmareDialog = remember { mutableStateOf(false) }
            val firmwareInstallState = remember { mutableStateOf(FirmwareInstallState.File) }
            val firmwareVersion = remember { mutableStateOf(mainViewModel.firmwareVersion) }
            val showDataResetDialog = remember { mutableStateOf(false) }
            val showDataImportDialog = remember { mutableStateOf(false) }
            val dataResetState = remember { mutableStateOf(DataResetState.Query) }
            val dataImportState = remember { mutableStateOf(DataImportState.File) }
            val dataFile = remember { mutableStateOf<DocumentFile?>(null) }
            val isGrid = remember { mutableStateOf(true) }
            val useSwitchLayout = remember { mutableStateOf(true) }
            val enableMotion = remember { mutableStateOf(true) }
            val vcPreset = remember {
                mutableStateOf(QuickSettings(mainViewModel.activity).virtualControllerPreset)
            }
            val enablePerformanceMode = remember { mutableStateOf(true) }
            val controllerStickSensitivity = remember { mutableFloatStateOf(1.0f) }

            // --- Controller Scale (0.5 .. 1.5), loaded directly from QuickSettings
            val controllerScale = remember {
                mutableFloatStateOf(QuickSettings(mainViewModel.activity).controllerScale)
            }

            val enableStubLogs = remember { mutableStateOf(true) }
            val enableInfoLogs = remember { mutableStateOf(true) }
            val enableWarningLogs = remember { mutableStateOf(true) }
            val enableErrorLogs = remember { mutableStateOf(true) }
            val enableGuestLogs = remember { mutableStateOf(true) }
            val enableFsAccessLogs = remember { mutableStateOf(true) }
            val enableTraceLogs = remember { mutableStateOf(true) }
            val enableDebugLogs = remember { mutableStateOf(true) }
            val enableGraphicsLogs = remember { mutableStateOf(true) }
            val isNavigating = remember { mutableStateOf(false) }

            // Load orientation from QuickSettings
            val orientationPref = remember {
                mutableStateOf(QuickSettings(mainViewModel.activity).orientationPreference)
            }

            // Language & Region States
            val systemLanguage = remember { mutableStateOf(SystemLanguage.AmericanEnglish) }
            val regionCode = remember { mutableStateOf(RegionCode.USA) }

            // Load overlay settings from QuickSettings
            val overlayMenuPosition = remember {
                mutableStateOf(QuickSettings(mainViewModel.activity).overlayMenuPosition)
            }
            val overlayOpacity = remember {
                mutableFloatStateOf(QuickSettings(mainViewModel.activity).overlayMenuOpacity.coerceIn(0f, 1f))
            }

            // --- Disable Threaded Rendering (Load from QuickSettings)
            val disableThreadedRendering = remember {
                mutableStateOf(QuickSettings(mainViewModel.activity).disableThreadedRendering)
            }
            val threadToggleInitialized = remember { mutableStateOf(false) }

            if (!loaded.value) {
                settingsViewModel.initializeState(
                    memoryManagerMode,
                    useNce,
                    memoryConfiguration,
                    vSyncMode,
                    enableDocked,
                    enablePptc,
                    enableLowPowerPptc,
                    enableJitCacheEviction,
                    enableFsIntegrityChecks,
                    fsGlobalAccessLogMode,
                    ignoreMissingServices,
                    enableShaderCache,
                    enableTextureRecompression,
                    enableMacroHLE,
                    stretchToFullscreen,
                    resScale,
                    maxAnisotropy,
                    useVirtualController,
                    isGrid,
                    useSwitchLayout,
                    enableMotion,
                    enablePerformanceMode,
                    controllerStickSensitivity,
                    enableStubLogs,
                    enableInfoLogs,
                    enableWarningLogs,
                    enableErrorLogs,
                    enableGuestLogs,
                    enableFsAccessLogs,
                    enableTraceLogs,
                    enableDebugLogs,
                    enableGraphicsLogs,
                    systemLanguage,
                    regionCode
                )
                loaded.value = true
            }

            val categories = listOf(
                Triple("System", Icons.Outlined.Settings, 0),
                Triple("Graphics", Icons.Outlined.Palette, 1),
                Triple("CPU", Icons.Outlined.DeveloperBoard, 2),
                Triple("Input", Icons.Outlined.VideogameAsset, 3),
                Triple("User Interface", Icons.Outlined.Dashboard, 4),
                Triple("System Files", Icons.Outlined.Folder, 5),
                Triple("Logging", Icons.Outlined.FileOpen, 6)
            )

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (isSearchActive) {
                        TopAppBar(
                            title = {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search settings...") },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { isSearchActive = false; searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Search")
                                }
                            }
                        )
                    } else {
                        TopAppBar(title = {
                            Text(text = "Settings")
                        },
                            navigationIcon = {
                                IconButton(onClick = {
                                    settingsViewModel.save(
                                        memoryManagerMode,
                                        useNce,
                                        memoryConfiguration,
                                        vSyncMode,
                                        enableDocked,
                                        enablePptc,
                                        enableLowPowerPptc,
                                        enableJitCacheEviction,
                                        enableFsIntegrityChecks,
                                        fsGlobalAccessLogMode,
                                        ignoreMissingServices,
                                        enableShaderCache,
                                        enableTextureRecompression,
                                        enableMacroHLE,
                                        stretchToFullscreen,
                                        resScale,
                                        maxAnisotropy,
                                        useVirtualController,
                                        isGrid,
                                        useSwitchLayout,
                                        enableMotion,
                                        enablePerformanceMode,
                                        controllerStickSensitivity,
                                        enableStubLogs,
                                        enableInfoLogs,
                                        enableWarningLogs,
                                        enableErrorLogs,
                                        enableGuestLogs,
                                        enableFsAccessLogs,
                                        enableTraceLogs,
                                        enableDebugLogs,
                                        enableGraphicsLogs,
                                        systemLanguage,
                                        regionCode
                                    )

                                    // Controller Scale wird separat direkt in QuickSettings gespeichert.
                                    if (!isNavigating.value) {
                                        isNavigating.value = true
                                        mainViewModel.navController?.popBackStack()

                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(500)
                                            isNavigating.value = false
                                        }
                                    }

                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            },
                            actions = {
                                IconButton(onClick = { isSearchActive = true }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            })
                    }
                }) { contentPadding ->
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                ) {
                    
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(categories) { index, category ->
                            FilterChip(
                                selected = selectedCategoryIndex == index,
                                onClick = { selectedCategoryIndex = index },
                                label = { Text(category.first) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = category.second,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (selectedCategoryIndex) {
                            0 -> SystemTab(
                                systemLanguage, regionCode, vSyncMode, memoryConfiguration,
                                enableFsIntegrityChecks, ignoreMissingServices, enablePerformanceMode,
                                searchQuery
                            )
                            1 -> GraphicsTab(
                                enableShaderCache, enableTextureRecompression, enableMacroHLE,
                                disableThreadedRendering, stretchToFullscreen, resScale,
                                maxAnisotropy, settingsViewModel, mainViewModel, threadToggleInitialized,
                                searchQuery
                            )
                            2 -> CpuTab(
                                useNce, enablePptc, enableLowPowerPptc,
                                enableJitCacheEviction, memoryManagerMode, searchQuery
                            )
                            3 -> InputTab(
                                useVirtualController, useSwitchLayout, vcPreset,
                                controllerScale, controllerStickSensitivity, enableDocked,
                                enableMotion, mainViewModel, searchQuery
                            )
                            4 -> UserInterfaceTab(
                                orientationPref, overlayMenuPosition, overlayOpacity,
                                isGrid, mainViewModel, searchQuery
                            )
                            5 -> SystemFilesTab(
                                showKeyDialog, showFirwmareDialog, firmwareVersion,
                                showDataResetDialog, showDataImportDialog, settingsViewModel, mainViewModel,
                                searchQuery
                            )
                            6 -> LoggingTab(
                                enableStubLogs, enableInfoLogs, enableWarningLogs, enableErrorLogs,
                                enableGuestLogs, enableTraceLogs, enableFsAccessLogs, enableDebugLogs,
                                enableGraphicsLogs, fsGlobalAccessLogMode, mainViewModel, searchQuery
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // --- Dialogs ---

                SimpleAlertDialog.Custom(
                    showDialog = showKeyDialog,
                    onDismissRequest = {
                        if (keyInstallState.value != KeyInstallState.Install) {
                            showKeyDialog.value = false
                            settingsViewModel.clearKeySelection(keyInstallState)
                            keyInstallState.value = KeyInstallState.File
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Key Installation",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        when (keyInstallState.value) {
                            KeyInstallState.File -> {
                                Text(
                                    text = "Select a key file to install key from.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            settingsViewModel.selectKey(
                                                keyInstallState
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Select File")
                                    }
                                    Button(
                                        onClick = {
                                            showKeyDialog.value = false
                                            settingsViewModel.clearKeySelection(keyInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Cancel")
                                    }
                                }
                            }
                            KeyInstallState.Query -> {
                                Text(
                                    text = "Key file will be installed. Do you want to continue?",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            settingsViewModel.installKey(
                                                keyInstallState
                                            )

                                            if (keyInstallState.value == KeyInstallState.File) {
                                                showKeyDialog.value = false
                                                settingsViewModel.clearKeySelection(keyInstallState)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Yes")
                                    }
                                    Button(
                                        onClick = {
                                            showKeyDialog.value = false
                                            settingsViewModel.clearKeySelection(keyInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "No")
                                    }
                                }
                            }
                            KeyInstallState.Install -> {
                                Text(
                                    text = "Installing key file...",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                )
                            }
                            KeyInstallState.Done -> {
                                Text(
                                    text = "Key file installed.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            showKeyDialog.value = false
                                            settingsViewModel.clearKeySelection(keyInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Close")
                                    }
                                }
                            }
                            KeyInstallState.Cancelled -> {
                                val file = settingsViewModel.selectedKeyFile
                                if (file != null) {
                                    if (file.extension == "key") {
                                        Text(
                                            text = "Unknown Error has occurred. Please check logs.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 8.dp, bottom = 8.dp),
                                            textAlign = TextAlign.Start
                                        )
                                    } else {
                                        Text(
                                            text = "File type is not supported.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 8.dp, bottom = 8.dp),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "File type is not supported.",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, bottom = 8.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            showKeyDialog.value = false
                                            settingsViewModel.clearKeySelection(keyInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Close")
                                    }
                                }
                            }
                        }
                    }
                }
                SimpleAlertDialog.Custom(
                    showDialog = showFirwmareDialog,
                    onDismissRequest = {
                        if (firmwareInstallState.value != FirmwareInstallState.Install) {
                            showFirwmareDialog.value = false
                            settingsViewModel.clearFirmwareSelection(firmwareInstallState)
                            firmwareInstallState.value = FirmwareInstallState.File
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Firmware Installation",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        when (firmwareInstallState.value) {
                            FirmwareInstallState.File -> {
                                Text(
                                    text = "Select a zip or xci file to install firmware from.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            settingsViewModel.selectFirmware(firmwareInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Select File")
                                    }
                                    Button(
                                        onClick = {
                                            showFirwmareDialog.value = false
                                            settingsViewModel.clearFirmwareSelection(firmwareInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Cancel")
                                    }
                                }
                            }
                            FirmwareInstallState.Query -> {
                                Text(
                                    text = "Firmware ${settingsViewModel.selectedFirmwareVersion} will be installed. Do you want to continue?",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            settingsViewModel.installFirmware(firmwareInstallState)

                                            if (firmwareInstallState.value == FirmwareInstallState.File) {
                                                showFirwmareDialog.value = false
                                                settingsViewModel.clearFirmwareSelection(firmwareInstallState)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Yes")
                                    }
                                    Button(
                                        onClick = {
                                            showFirwmareDialog.value = false
                                            settingsViewModel.clearFirmwareSelection(firmwareInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "No")
                                    }
                                }
                            }
                            FirmwareInstallState.Verifying -> {
                                Text(
                                    text = "Verifying selected file...",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                )
                            }
                            FirmwareInstallState.Install -> {
                                Text(
                                    text = "Installing firmware ${settingsViewModel.selectedFirmwareVersion}...",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                )
                            }
                            FirmwareInstallState.Done -> {
                                Text(
                                    text = "Firmware ${settingsViewModel.selectedFirmwareVersion}.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                firmwareVersion.value = mainViewModel.firmwareVersion

                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            showFirwmareDialog.value = false
                                            settingsViewModel.clearFirmwareSelection(firmwareInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Close")
                                    }
                                }
                            }
                            FirmwareInstallState.Cancelled -> {
                                val file = settingsViewModel.selectedFirmwareFile
                                if (file != null) {
                                    if (file.extension == "xci" || file.extension == "zip") {
                                        if (settingsViewModel.selectedFirmwareVersion.isEmpty()) {
                                            Text(
                                                text = "Unable to find version in selected file.",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 8.dp, bottom = 8.dp),
                                                textAlign = TextAlign.Start
                                            )
                                        } else {
                                            Text(
                                                text = "Unknown Error has occurred. Please check logs.",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 8.dp, bottom = 8.dp),
                                                textAlign = TextAlign.Start
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "File type is not supported.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 8.dp, bottom = 8.dp),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "File type is not supported.",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, bottom = 8.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            showFirwmareDialog.value = false
                                            settingsViewModel.clearFirmwareSelection(firmwareInstallState)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Close")
                                    }
                                }
                            }
                        }
                    }
                }
                SimpleAlertDialog.Custom(
                    showDialog = showDataResetDialog,
                    onDismissRequest = {
                        if (dataResetState.value != DataResetState.Reset) {
                            showDataResetDialog.value = false
                            dataResetState.value = DataResetState.Query
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "App Data Reinit",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        when (dataResetState.value) {
                            DataResetState.Query -> {
                                Text(
                                    text = "Current bis, games, profiles and system folders will be reset. Do you want to continue?",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            thread {
                                                settingsViewModel.resetAppData(dataResetState)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Yes")
                                    }
                                    Button(
                                        onClick = {
                                            showDataResetDialog.value = false
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "No")
                                    }
                                }
                            }
                            DataResetState.Reset -> {
                                Text(
                                    text = "Resetting app data...",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                )
                            }
                            DataResetState.Done -> {
                                Text(
                                    text = "Data reset completed successfully.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )

                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            showDataResetDialog.value = false
                                            firmwareVersion.value = mainViewModel.firmwareVersion
                                            dataResetState.value = DataResetState.Query
                                            mainViewModel.userViewModel.refreshUsers()
                                            mainViewModel.homeViewModel.requestReload()
                                            mainViewModel.activity.shutdownAndRestart()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Close")
                                    }
                                }
                            }
                        }
                    }
                }
                SimpleAlertDialog.Custom(
                    showDialog = showDataImportDialog,
                    onDismissRequest = {
                        if (dataImportState.value != DataImportState.Import) {
                            showDataImportDialog.value = false
                            dataFile.value = null
                            dataImportState.value = DataImportState.File
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "App Data Import",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        when (dataImportState.value) {
                            DataImportState.File -> {
                                Text(
                                    text = "Select a zip file to import bis, games, profiles and system folders from another Android installation.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val storage = MainActivity.StorageHelper
                                            storage?.apply {
                                                val callBack = this.onFileSelected
                                                onFileSelected = { requestCode, files ->
                                                    run {
                                                        onFileSelected = callBack
                                                        if (requestCode == IMPORT_CODE) {
                                                            val file = files.firstOrNull()
                                                            file?.apply {
                                                                if (this.extension == "zip") {
                                                                    dataFile.value = this
                                                                    dataImportState.value = DataImportState.Query
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                openFilePicker(
                                                    IMPORT_CODE,
                                                    filterMimeTypes = arrayOf("application/zip")
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Select File")
                                    }
                                    Button(
                                        onClick = {
                                            showDataImportDialog.value = false
                                            dataFile.value = null
                                            dataImportState.value = DataImportState.File
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Cancel")
                                    }
                                }
                            }
                            DataImportState.Query -> {
                                Text(
                                    text = "Current app data will be wiped and replaced. Do you want to continue?",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val file = dataFile.value
                                            dataFile.value = null
                                            file?.apply {
                                                thread {
                                                    settingsViewModel.importAppData(this, dataImportState)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Yes")
                                    }
                                    Button(
                                        onClick = {
                                            showDataImportDialog.value = false
                                            dataFile.value = null
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "No")
                                    }
                                }
                            }
                            DataImportState.Import -> {
                                Text(
                                    text = "Importing app data...",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                )
                            }

                            DataImportState.Done -> {
                                Text(
                                    text = "Data import completed successfully.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 8.dp),
                                    textAlign = TextAlign.Start
                                )

                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            showDataImportDialog.value = false
                                            dataFile.value = null
                                            firmwareVersion.value = mainViewModel.firmwareVersion
                                            dataImportState.value = DataImportState.File
                                            mainViewModel.userViewModel.refreshUsers()
                                            mainViewModel.homeViewModel.requestReload()
                                            mainViewModel.activity.shutdownAndRestart()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Close")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun SettingsCard(title: String, searchQuery: String = "", content: @Composable ColumnScope.() -> Unit) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (searchQuery.isEmpty() || title.contains(searchQuery, true)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    content()
                }
            }
        }

        @Composable
        fun SettingSwitchItem(
            title: String,
            description: String,
            isChecked: Boolean,
            onCheckedChange: (Boolean) -> Unit,
            searchQuery: String = ""
        ) {
            if (searchQuery.isNotEmpty() && !title.contains(searchQuery, true) && !description.contains(searchQuery, true)) return

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange
                )
            }
        }

        @Composable
        fun <T> SettingSelectionItem(
            title: String,
            description: String = "",
            selectedValue: T,
            options: List<T>,
            getDisplayText: (T) -> String,
            onOptionSelected: (T) -> Unit,
            searchQuery: String = ""
        ) {
            if (searchQuery.isNotEmpty() && !title.contains(searchQuery, true) && !description.contains(searchQuery, true)) return

            var showDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDialog = true }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Text(
                    text = getDisplayText(selectedValue),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        tonalElevation = 6.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            )
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                options.forEach { option ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onOptionSelected(option)
                                                showDialog = false
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = getDisplayText(option),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.weight(1f)
                                        )
                                        RadioButton(
                                            selected = option == selectedValue,
                                            onClick = null,
                                            modifier = Modifier.padding(start = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun SettingActionItem(
            title: String,
            description: String,
            searchQuery: String = "",
            actionContent: @Composable () -> Unit
        ) {
            if (searchQuery.isNotEmpty() && !title.contains(searchQuery, true) && !description.contains(searchQuery, true)) return

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                actionContent()
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun GraphicsTab(
            enableShaderCache: MutableState<Boolean>,
            enableTextureRecompression: MutableState<Boolean>,
            enableMacroHLE: MutableState<Boolean>,
            disableThreadedRendering: MutableState<Boolean>,
            stretchToFullscreen: MutableState<Boolean>,
            resScale: MutableState<Float>,
            maxAnisotropy: MutableState<Float>,
            settingsViewModel: SettingsViewModel,
            mainViewModel: MainViewModel,
            threadToggleInitialized: MutableState<Boolean>,
            searchQuery: String
        ) {
            val scaleOptions = listOf(
                0.5f to "0.5x (360p/540p)",
                0.75f to "0.75x (540p/810p)",
                1f to "1.0x (720p/1080p)",
                2f to "2.0x (1440p/2160p)",
                3f to "3.0x (2160p/3240p)",
                4f to "4.0x (2800p/4320p)"
            )
            
            val anisotropyOptions = listOf(
                0.0f to "0x",
                1.0f to "2x",
                2.0f to "4x",
                3.0f to "8x",
                4.0f to "16x"
            )

            SettingsCard(title = "Rendering", searchQuery = searchQuery) {
                SettingSelectionItem(
                    title = "Resolution (Handheld/Docked)",
                    description = "Takes effect at the next game start.",
                    selectedValue = resScale.value,
                    options = scaleOptions.map { it.first },
                    getDisplayText = { scale -> scaleOptions.find { it.first == scale }?.second ?: "${scale}x" },
                    onOptionSelected = { scale -> resScale.value = scale },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSelectionItem(
                    title = "Anisotropic Filtering",
                    description = "Enhances texture quality at oblique angles.",
                    selectedValue = maxAnisotropy.value,
                    options = anisotropyOptions.map { it.first },
                    getDisplayText = { anisotropy -> anisotropyOptions.find { it.first == anisotropy }?.second ?: "${anisotropy}x" },
                    onOptionSelected = { anisotropy -> maxAnisotropy.value = anisotropy },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Disable Threaded Rendering",
                    description = "Forces single-threaded rendering which may improve stability at the cost of performance.",
                    isChecked = disableThreadedRendering.value,
                    onCheckedChange = { disableThreadedRendering.value = it },
                    searchQuery = searchQuery
                )
            }

            SettingsCard(title = "Post-Processing", searchQuery = searchQuery) {
                SettingSwitchItem(
                    title = "Stretch to Fullscreen",
                    description = "Forces the image to fill the entire display, ignoring aspect ratio.",
                    isChecked = stretchToFullscreen.value,
                    onCheckedChange = { stretchToFullscreen.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Shader Cache",
                    description = "Caches compiled shaders to reduce stuttering during gameplay.",
                    isChecked = enableShaderCache.value,
                    onCheckedChange = { enableShaderCache.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Texture Recompression",
                    description = "Recompresses textures to save memory at the cost of some CPU overhead.",
                    isChecked = enableTextureRecompression.value,
                    onCheckedChange = { enableTextureRecompression.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Macro HLE",
                    description = "Enables high-level emulation for certain macros.",
                    isChecked = enableMacroHLE.value,
                    onCheckedChange = { enableMacroHLE.value = it },
                    searchQuery = searchQuery
                )
            }

            SettingsCard(title = "GPU driver", searchQuery = searchQuery) {
                val isDriverSelectorOpen = remember { mutableStateOf(false) }
                SettingActionItem(
                    title = "Install custom driver",
                    description = "Opens the driver installation menu.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(
                        onClick = { isDriverSelectorOpen.value = true }
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Install")
                    }
                }

                SimpleAlertDialog.Custom(
                    showDialog = isDriverSelectorOpen,
                    onDismissRequest = { isDriverSelectorOpen.value = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                ) {
                    VulkanDriverViews.Main(settingsViewModel.activity, isDriverSelectorOpen)
                }
            }

            LaunchedEffect(disableThreadedRendering.value) {
                if (!threadToggleInitialized.value) {
                    threadToggleInitialized.value = true
                } else {
                    val qs = QuickSettings(mainViewModel.activity)
                    qs.disableThreadedRendering = disableThreadedRendering.value
                    qs.save()

                    val mode = if (disableThreadedRendering.value) 1 /*Single*/ else 2 /*Threaded*/

                    thread {
                        try {
                            KenjinxNative.graphicsSetPresentEnabled(false)
                            KenjinxNative.deviceWaitForGpuDone(500)
                            KenjinxNative.graphicsSetBackendThreading(mode)
                            KenjinxNative.deviceRecreateSwapchain()
                        } finally {
                            KenjinxNative.graphicsSetPresentEnabled(true)
                        }
                    }
                }
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun UserInterfaceTab(
            orientationPref: MutableState<OrientationPreference>,
            overlayMenuPosition: MutableState<OverlayMenuPosition>,
            overlayOpacity: MutableState<Float>,
            isGrid: MutableState<Boolean>,
            mainViewModel: MainViewModel,
            searchQuery: String
        ) {
            SettingsCard(title = "Display", searchQuery = searchQuery) {
                SettingSelectionItem(
                    title = "Screen Orientation",
                    description = "Select how the app handles screen rotation.",
                    selectedValue = orientationPref.value,
                    options = listOf(
                        OrientationPreference.Sensor,
                        OrientationPreference.SensorLandscape,
                        OrientationPreference.SensorPortrait
                    ),
                    getDisplayText = { opt ->
                        when (opt) {
                            OrientationPreference.Sensor -> "Sensor"
                            OrientationPreference.SensorLandscape -> "Sensor Landscape"
                            OrientationPreference.SensorPortrait -> "Sensor Portrait"
                        }
                    },
                    onOptionSelected = { sel ->
                        orientationPref.value = sel
                        val qs = QuickSettings(mainViewModel.activity)
                        qs.orientationPreference = sel
                        qs.save()

                        val act = mainViewModel.activity
                        act.requestedOrientation = sel.value

                        val rot = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            act.display?.rotation
                        } else {
                            TODO("VERSION.SDK_INT < R")
                        }
                        mainViewModel.gameHost?.onOrientationOrSizeChanged(rot)
                    },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSelectionItem(
                    title = "Overlay Menu Position",
                    description = "Where the floating overlay menu appears.",
                    selectedValue = overlayMenuPosition.value,
                    options = listOf(
                        OverlayMenuPosition.BottomMiddle,
                        OverlayMenuPosition.BottomLeft,
                        OverlayMenuPosition.BottomRight,
                        OverlayMenuPosition.TopMiddle,
                        OverlayMenuPosition.TopLeft,
                        OverlayMenuPosition.TopRight
                    ),
                    getDisplayText = { opt ->
                        when (opt) {
                            OverlayMenuPosition.BottomMiddle -> "bottom middle"
                            OverlayMenuPosition.BottomLeft   -> "bottom left"
                            OverlayMenuPosition.BottomRight  -> "bottom right"
                            OverlayMenuPosition.TopMiddle    -> "top middle"
                            OverlayMenuPosition.TopLeft      -> "top left"
                            OverlayMenuPosition.TopRight     -> "top right"
                        }
                    },
                    onOptionSelected = { pos ->
                        overlayMenuPosition.value = pos
                        val qs = QuickSettings(mainViewModel.activity)
                        qs.overlayMenuPosition = pos
                        qs.save()
                    },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
                SettingActionItem(
                    title = "Overlay transparency",
                    description = "Adjust the opacity of the floating menu.",
                    searchQuery = searchQuery
                ) {
                    Slider(
                        modifier = Modifier.width(150.dp),
                        value = overlayOpacity.value,
                        onValueChange = {
                            val clamped = it.coerceIn(0f, 1f)
                            overlayOpacity.value = clamped
                            val qs = QuickSettings(mainViewModel.activity)
                            qs.overlayMenuOpacity = clamped
                            qs.save()
                        },
                        valueRange = 0f..1f,
                        steps = 20,
                        interactionSource = interactionSource
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Use Grid",
                    description = "Display games in a grid layout.",
                    isChecked = isGrid.value,
                    onCheckedChange = { isGrid.value = it },
                    searchQuery = searchQuery
                )
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun InputTab(
            useVirtualController: MutableState<Boolean>,
            useSwitchLayout: MutableState<Boolean>,
            vcPreset: MutableState<VirtualControllerPreset>,
            controllerScale: MutableState<Float>,
            controllerStickSensitivity: MutableState<Float>,
            enableDocked: MutableState<Boolean>,
            enableMotion: MutableState<Boolean>,
            mainViewModel: MainViewModel,
            searchQuery: String
        ) {
            SettingsCard(title = "Virtual Controller", searchQuery = searchQuery) {
                SettingSwitchItem(
                    title = "Use Virtual Controller",
                    description = "Enable the on-screen overlay controller.",
                    isChecked = useVirtualController.value,
                    onCheckedChange = { useVirtualController.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Use Switch Controller Layout",
                    description = "Use the standard Switch button arrangement.",
                    isChecked = useSwitchLayout.value,
                    onCheckedChange = { useSwitchLayout.value = it },
                    searchQuery = searchQuery
                )
                
                if (useVirtualController.value) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingSelectionItem(
                        title = "Controller Layout",
                        description = "Select a predefined overlay layout.",
                        selectedValue = vcPreset.value,
                        options = listOf(
                            VirtualControllerPreset.Default,
                            VirtualControllerPreset.Layout2,
                            VirtualControllerPreset.Layout3,
                            VirtualControllerPreset.Layout4,
                            VirtualControllerPreset.Layout5,
                            VirtualControllerPreset.Layout6
                        ),
                        getDisplayText = { opt ->
                            when (opt) {
                                VirtualControllerPreset.Default -> "Default"
                                VirtualControllerPreset.Layout2 -> "Layout 2"
                                VirtualControllerPreset.Layout3 -> "Layout 3"
                                VirtualControllerPreset.Layout4 -> "Layout 4"
                                VirtualControllerPreset.Layout5 -> "Layout 5"
                                VirtualControllerPreset.Layout6 -> "Layout 6"
                            }
                        },
                        onOptionSelected = { preset ->
                            vcPreset.value = preset
                            val qs = QuickSettings(mainViewModel.activity)
                            qs.virtualControllerPreset = preset
                            qs.save()
                        },
                        searchQuery = searchQuery
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    val interactionSourceScale: MutableInteractionSource = remember { MutableInteractionSource() }
                    SettingActionItem(
                        title = "Controller Scale",
                        description = "Adjust the size of the virtual buttons.",
                        searchQuery = searchQuery
                    ) {
                        Slider(
                            modifier = Modifier.width(150.dp),
                            value = controllerScale.value,
                            onValueChange = { v ->
                                val clamped = v.coerceIn(0.5f, 1.5f)
                                controllerScale.value = clamped
                                mainViewModel.controller?.let { c ->
                                    try {
                                        val m = c::class.java.getMethod("setScale", Float::class.javaPrimitiveType)
                                        m.invoke(c, clamped)
                                    } catch (_: Throwable) { }
                                }
                            },
                            valueRange = 0.5f..1.5f,
                            steps = 19,
                            interactionSource = interactionSourceScale,
                            thumb = {
                                Label(
                                    label = {
                                        PlainTooltip(
                                            modifier = Modifier
                                                .sizeIn(45.dp, 25.dp)
                                                .wrapContentWidth()
                                        ) {
                                            Text("${(controllerScale.value * 100f).roundToInt()}%")
                                        }
                                    },
                                    interactionSource = interactionSourceScale
                                ) {
                                    Box(Modifier.size(ButtonDefaults.IconSize))
                                }
                            },
                            onValueChangeFinished = {
                                val qs = QuickSettings(mainViewModel.activity)
                                qs.controllerScale = controllerScale.value.coerceIn(0.5f, 1.5f)
                                qs.save()
                            }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
                    SettingActionItem(
                        title = "Stick Sensitivity",
                        description = "Adjust the responsiveness of the analog sticks.",
                        searchQuery = searchQuery
                    ) {
                        Slider(
                            modifier = Modifier.width(150.dp),
                            value = controllerStickSensitivity.value,
                            onValueChange = { controllerStickSensitivity.value = it },
                            valueRange = 0.1f..2f,
                            steps = 20,
                            interactionSource = interactionSource,
                            thumb = {
                                Label(
                                    label = {
                                        PlainTooltip(
                                            modifier = Modifier
                                                .sizeIn(45.dp, 25.dp)
                                                .wrapContentWidth()
                                        ) {
                                            Text("%.2f".format(controllerStickSensitivity.value))
                                        }
                                    },
                                    interactionSource = interactionSource
                                ) {
                                    Box(Modifier.size(ButtonDefaults.IconSize))
                                }
                            }
                        )
                    }
                }
            }

            SettingsCard(title = "Hardware Input", searchQuery = searchQuery) {
                SettingSwitchItem(
                    title = "Docked Mode",
                    description = "Simulate the console being in its dock.",
                    isChecked = enableDocked.value,
                    onCheckedChange = { enableDocked.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Motion Sensor",
                    description = "Enable gyroscope and accelerometer input.",
                    isChecked = enableMotion.value,
                    onCheckedChange = { enableMotion.value = it },
                    searchQuery = searchQuery
                )
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun SystemTab(
            systemLanguage: MutableState<SystemLanguage>,
            regionCode: MutableState<RegionCode>,
            vSyncMode: MutableState<VSyncMode>,
            memoryConfiguration: MutableState<MemoryConfiguration>,
            enableFsIntegrityChecks: MutableState<Boolean>,
            ignoreMissingServices: MutableState<Boolean>,
            enablePerformanceMode: MutableState<Boolean>,
            searchQuery: String
        ) {
            SettingsCard(title = "Region & Language", searchQuery = searchQuery) {
                val languages = SystemLanguage.entries.toTypedArray()
                SettingSelectionItem(
                    title = "System Language",
                    description = "Language used by the emulated system.",
                    selectedValue = systemLanguage.value,
                    options = languages.toList(),
                    getDisplayText = { lang ->
                        when (lang) {
                            SystemLanguage.Japanese -> "Japanese"
                            SystemLanguage.AmericanEnglish -> "English (US)"
                            SystemLanguage.French -> "French"
                            SystemLanguage.German -> "German"
                            SystemLanguage.Italian -> "Italian"
                            SystemLanguage.Spanish -> "Spanish (EU)"
                            SystemLanguage.Chinese -> "Chinese"
                            SystemLanguage.Korean -> "Korean"
                            SystemLanguage.Dutch -> "Dutch"
                            SystemLanguage.Portuguese -> "Portuguese (EU)"
                            SystemLanguage.Russian -> "Russian"
                            SystemLanguage.Taiwanese -> "Chinese (Taiwan)"
                            SystemLanguage.BritishEnglish -> "English (UK)"
                            SystemLanguage.CanadianFrench -> "French (Canada)"
                            SystemLanguage.LatinAmericanSpanish -> "Spanish (LatAm)"
                            SystemLanguage.SimplifiedChinese -> "Chinese (Simplified)"
                            SystemLanguage.TraditionalChinese -> "Chinese (Traditional)"
                            SystemLanguage.BrazilianPortuguese -> "Portuguese (Brazil)"
                        }
                    },
                    onOptionSelected = { lang -> systemLanguage.value = lang },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                val regions = RegionCode.entries.toTypedArray()
                SettingSelectionItem(
                    title = "Region",
                    description = "Region code used by the emulated system.",
                    selectedValue = regionCode.value,
                    options = regions.toList(),
                    getDisplayText = { region ->
                        when (region) {
                            RegionCode.Japan -> "Japan"
                            RegionCode.USA -> "USA"
                            RegionCode.Europe -> "Europe"
                            RegionCode.Australia -> "Australia"
                            RegionCode.China -> "China"
                            RegionCode.Korea -> "Korea"
                            RegionCode.Taiwan -> "Taiwan"
                        }
                    },
                    onOptionSelected = { reg -> regionCode.value = reg },
                    searchQuery = searchQuery
                )
            }

            SettingsCard(title = "System Behaviour", searchQuery = searchQuery) {
                val vSyncModes = VSyncMode.entries.toTypedArray()
                SettingSelectionItem(
                    title = "VSync",
                    description = "Select the VSync mode.",
                    selectedValue = vSyncMode.value,
                    options = vSyncModes.toList(),
                    getDisplayText = { mode ->
                        when(mode) {
                            VSyncMode.Switch -> "Switch"
                            VSyncMode.Unbounded -> "Unbounded"
                        }
                    },
                    onOptionSelected = { mode -> vSyncMode.value = mode },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                val memoryModes = MemoryConfiguration.entries.toTypedArray()
                SettingSelectionItem(
                    title = "DRAM Size",
                    description = "Select the amount of memory allocated.",
                    selectedValue = memoryConfiguration.value,
                    options = memoryModes.toList(),
                    getDisplayText = { configuration ->
                        when(configuration) {
                            MemoryConfiguration.MemoryConfiguration4GiB -> "4GiB"
                            MemoryConfiguration.MemoryConfiguration6GiB -> "6GiB"
                            MemoryConfiguration.MemoryConfiguration8GiB -> "8GiB"
                            MemoryConfiguration.MemoryConfiguration10GiB -> "10GiB"
                            MemoryConfiguration.MemoryConfiguration12GiB -> "12GiB"
                        }
                    },
                    onOptionSelected = { configuration -> memoryConfiguration.value = configuration },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Fs Integrity Check",
                    description = "Verify file system integrity during emulation.",
                    isChecked = enableFsIntegrityChecks.value,
                    onCheckedChange = { enableFsIntegrityChecks.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Ignore Missing Services",
                    description = "Continue execution if a system service is missing.",
                    isChecked = ignoreMissingServices.value,
                    onCheckedChange = { ignoreMissingServices.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Performance Mode",
                    description = "Optimize emulation for maximum performance.",
                    isChecked = enablePerformanceMode.value,
                    onCheckedChange = { enablePerformanceMode.value = it },
                    searchQuery = searchQuery
                )
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun SystemFilesTab(
            showKeyDialog: MutableState<Boolean>,
            showFirwmareDialog: MutableState<Boolean>,
            firmwareVersion: MutableState<String>,
            showDataResetDialog: MutableState<Boolean>,
            showDataImportDialog: MutableState<Boolean>,
            settingsViewModel: SettingsViewModel,
            mainViewModel: MainViewModel,
            searchQuery: String
        ) {
            SettingsCard(title = "System & Data", searchQuery = searchQuery) {
                SettingActionItem(
                    title = "System Firmware",
                    description = "Installed version: ${firmwareVersion.value}",
                    searchQuery = searchQuery
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Install Keys",
                    description = "Install prod.keys or title.keys.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = { showKeyDialog.value = true }) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Install")
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Install Firmware",
                    description = "Install from a zip or xci file.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = { showFirwmareDialog.value = true }) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Install")
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Add Game Folder",
                    description = "Select a folder to scan for games.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = { settingsViewModel.openGameFolder() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Select Updates/DLC Folder",
                    description = "Select a folder for updates and DLC.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = { settingsViewModel.openUpdatesFolder() }) {
                        Icon(Icons.Default.FolderOpen, contentDescription = "Select")
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Open App Folder",
                    description = "Open the internal app data folder.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = {
                        fun createIntent(action: String): Intent {
                            val intent = Intent(action)
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = DocumentsContract.buildRootUri(
                                DocumentProvider.AUTHORITY,
                                DocumentProvider.ROOT_ID
                            )
                            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            return intent
                        }
                        try {
                            mainViewModel.activity.startActivity(createIntent(Intent.ACTION_VIEW))
                            return@FilledTonalIconButton
                        } catch (_: ActivityNotFoundException) {}
                        try {
                            mainViewModel.activity.startActivity(createIntent("android.provider.action.BROWSE"))
                            return@FilledTonalIconButton
                        } catch (_: ActivityNotFoundException) {}
                        try {
                            mainViewModel.activity.startActivity(createIntent("com.google.android.documentsui"))
                            return@FilledTonalIconButton
                        } catch (_: ActivityNotFoundException) {}
                        try {
                            mainViewModel.activity.startActivity(createIntent("com.android.documentsui"))
                            return@FilledTonalIconButton
                        } catch (_: ActivityNotFoundException) {}
                    }) {
                        Icon(Icons.Default.Folder, contentDescription = "Open")
                    }
                }
            }

            SettingsCard(title = "App Data Management", searchQuery = searchQuery) {
                SettingActionItem(
                    title = "Reinit App Data",
                    description = "Resets games, profiles, and system folders.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = { showDataResetDialog.value = true }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Import App Data",
                    description = "Import from a previously exported zip file.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = { showDataImportDialog.value = true }) {
                        Icon(Icons.Default.Upload, contentDescription = "Import")
                    }
                }
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun CpuTab(
            useNce: MutableState<Boolean>,
            enablePptc: MutableState<Boolean>,
            enableLowPowerPptc: MutableState<Boolean>,
            enableJitCacheEviction: MutableState<Boolean>,
            memoryManagerMode: MutableState<MemoryManagerMode>,
            searchQuery: String
        ) {
            SettingsCard(title = "CPU Emulation", searchQuery = searchQuery) {
                SettingSwitchItem(
                    title = "NCE (Native Code Execution)",
                    description = "Execute translated code natively for better performance.",
                    isChecked = useNce.value,
                    onCheckedChange = { useNce.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "PPTC",
                    description = "Profiled Persistent Translation Cache.",
                    isChecked = enablePptc.value,
                    onCheckedChange = { enablePptc.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Low-Power PPTC",
                    description = "Reduce CPU usage for PPTC at the cost of slower loading.",
                    isChecked = enableLowPowerPptc.value,
                    onCheckedChange = { enableLowPowerPptc.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Jit Cache Eviction",
                    description = "Evict old JIT compiled code to save memory.",
                    isChecked = enableJitCacheEviction.value,
                    onCheckedChange = { enableJitCacheEviction.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                val modes = MemoryManagerMode.entries.toTypedArray()
                SettingSelectionItem(
                    title = "Memory Manager Mode",
                    description = "Select how memory is mapped to the host.",
                    selectedValue = memoryManagerMode.value,
                    options = modes.toList(),
                    getDisplayText = { mode ->
                        when(mode) {
                            MemoryManagerMode.SoftwarePageTable -> "Software"
                            MemoryManagerMode.HostMapped -> "Host (fast)"
                            MemoryManagerMode.HostMappedUnsafe -> "Host Unchecked (fastest, unsafe)"
                        }
                    },
                    onOptionSelected = { mode -> memoryManagerMode.value = mode },
                    searchQuery = searchQuery
                )
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun LoggingTab(
            enableStubLogs: MutableState<Boolean>,
            enableInfoLogs: MutableState<Boolean>,
            enableWarningLogs: MutableState<Boolean>,
            enableErrorLogs: MutableState<Boolean>,
            enableGuestLogs: MutableState<Boolean>,
            enableTraceLogs: MutableState<Boolean>,
            enableFsAccessLogs: MutableState<Boolean>,
            enableDebugLogs: MutableState<Boolean>,
            enableGraphicsLogs: MutableState<Boolean>,
            fsGlobalAccessLogMode: MutableState<Int>,
            mainViewModel: MainViewModel,
            searchQuery: String
        ) {
            SettingsCard(title = "Log Levels", searchQuery = searchQuery) {
                SettingSwitchItem(
                    title = "Stub Logs",
                    description = "Log calls to unimplemented functions.",
                    isChecked = enableStubLogs.value,
                    onCheckedChange = { enableStubLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Info Logs",
                    description = "General informational messages.",
                    isChecked = enableInfoLogs.value,
                    onCheckedChange = { enableInfoLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Warning Logs",
                    description = "Potential issues that don't stop execution.",
                    isChecked = enableWarningLogs.value,
                    onCheckedChange = { enableWarningLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Error Logs",
                    description = "Critical errors and failures.",
                    isChecked = enableErrorLogs.value,
                    onCheckedChange = { enableErrorLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Guest Logs",
                    description = "Logs generated by the emulated system.",
                    isChecked = enableGuestLogs.value,
                    onCheckedChange = { enableGuestLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Trace Logs",
                    description = "Detailed execution traces.",
                    isChecked = enableTraceLogs.value,
                    onCheckedChange = { enableTraceLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Fs Access Logs",
                    description = "Log file system access operations.",
                    isChecked = enableFsAccessLogs.value,
                    onCheckedChange = { enableFsAccessLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Debug Logs",
                    description = "Internal debugging information.",
                    isChecked = enableDebugLogs.value,
                    onCheckedChange = { enableDebugLogs.value = it },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Graphics Logs",
                    description = "Logs related to the rendering pipeline.",
                    isChecked = enableGraphicsLogs.value,
                    onCheckedChange = { enableGraphicsLogs.value = it },
                    searchQuery = searchQuery
                )
            }

            SettingsCard(title = "Advanced", searchQuery = searchQuery) {
                val fsGlobalAccessOptions = listOf(0, 1, 2, 3)
                SettingSelectionItem(
                    title = "Fs Global Access Log Mode",
                    description = "Set the verbosity level for file system logs.",
                    selectedValue = fsGlobalAccessLogMode.value,
                    options = fsGlobalAccessOptions,
                    getDisplayText = { fsGlobalAccess -> fsGlobalAccess.toString() },
                    onOptionSelected = { fsGlobalAccess -> fsGlobalAccessLogMode.value = fsGlobalAccess },
                    searchQuery = searchQuery
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Export Logs",
                    description = "Send the current log file for analysis.",
                    searchQuery = searchQuery
                ) {
                    FilledTonalIconButton(onClick = { mainViewModel.logging.requestExport() }) {
                        Icon(Icons.Default.Upload, contentDescription = "Send")
                    }
                }
            }
        }
    }
}