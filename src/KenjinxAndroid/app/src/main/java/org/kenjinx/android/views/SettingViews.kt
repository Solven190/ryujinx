package org.kenjinx.android.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Panorama
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.kenjinx.android.widgets.ActionButton
import org.kenjinx.android.widgets.DropdownSelector
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
                Triple("Graphics", Icons.Outlined.Panorama, 0),
                Triple("User Interface", Icons.Outlined.BarChart, 1),
                Triple("Input", Icons.Outlined.VideogameAsset, 2),
                Triple("System", Icons.Outlined.Settings, 3),
                Triple("CPU", Icons.Outlined.Memory, 4),
                Triple("Logging", Icons.Outlined.FileOpen, 5)
            )

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
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
                            IconButton(onClick = { /* Search action placeholder */ }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        })
                }) { contentPadding ->
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                ) {
                    
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
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
                            0 -> GraphicsTab(
                                enableShaderCache, enableTextureRecompression, enableMacroHLE,
                                disableThreadedRendering, stretchToFullscreen, resScale,
                                maxAnisotropy, settingsViewModel, mainViewModel, threadToggleInitialized
                            )
                            1 -> UserInterfaceTab(
                                orientationPref, overlayMenuPosition, overlayOpacity,
                                isGrid, firmwareVersion, showKeyDialog, showFirwmareDialog,
                                showDataResetDialog, showDataImportDialog, settingsViewModel, mainViewModel
                            )
                            2 -> InputTab(
                                useVirtualController, useSwitchLayout, vcPreset,
                                controllerScale, controllerStickSensitivity, enableDocked,
                                enableMotion, mainViewModel
                            )
                            3 -> SystemTab(
                                systemLanguage, regionCode, vSyncMode, memoryConfiguration,
                                enableFsIntegrityChecks, ignoreMissingServices, enablePerformanceMode
                            )
                            4 -> CpuTab(
                                useNce, enablePptc, enableLowPowerPptc,
                                enableJitCacheEviction, memoryManagerMode
                            )
                            5 -> LoggingTab(
                                enableStubLogs, enableInfoLogs, enableWarningLogs, enableErrorLogs,
                                enableGuestLogs, enableTraceLogs, enableFsAccessLogs, enableDebugLogs,
                                enableGraphicsLogs, fsGlobalAccessLogMode, mainViewModel
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
        fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    content()
                }
            }
        }

        @Composable
        fun SettingSwitchItem(
            title: String,
            description: String,
            isChecked: Boolean,
            onCheckedChange: (Boolean) -> Unit
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
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
            onOptionSelected: (T) -> Unit
        ) {
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
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
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 24.dp)
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
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = option == selectedValue,
                                            onClick = null,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                        Text(
                                            text = getDisplayText(option),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
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
            actionContent: @Composable () -> Unit
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
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

        @Composable
        fun GraphicsTab(
            enableShaderCache: androidx.compose.runtime.MutableState<Boolean>,
            enableTextureRecompression: androidx.compose.runtime.MutableState<Boolean>,
            enableMacroHLE: androidx.compose.runtime.MutableState<Boolean>,
            disableThreadedRendering: androidx.compose.runtime.MutableState<Boolean>,
            stretchToFullscreen: androidx.compose.runtime.MutableState<Boolean>,
            resScale: androidx.compose.runtime.MutableFloatState,
            maxAnisotropy: androidx.compose.runtime.MutableFloatState,
            settingsViewModel: SettingsViewModel,
            mainViewModel: MainViewModel,
            threadToggleInitialized: androidx.compose.runtime.MutableState<Boolean>
        ) {
            val scaleOptions = listOf(
                0.25f to "0.25X (180p/270p)",
                0.5f to "0.5X (360p/540p)",
                0.75f to "0.75X (540p/810p)",
                1f to "1X (720p/1080p)",
                1.25f to "1.25X (900p/1350p)",
                1.5f to "1.5X (1080p/1620p)",
                2f to "2X (1440p/2160p) (Slow)",
                3f to "3X (2160p/3240p) (Slow)",
                4f to "4X (2880p/4320p) (Slow)"
            )
            
            val anisotropyOptions = listOf(
                0.0f to "0x",
                1.0f to "2x",
                2.0f to "4x",
                3.0f to "8x",
                4.0f to "16x"
            )

            SettingsCard(title = "Rendering") {
                SettingSelectionItem(
                    title = "Resolution (Handheld/Docked)",
                    description = "Takes effect at the next game start.",
                    selectedValue = resScale.floatValue,
                    options = scaleOptions.map { it.first },
                    getDisplayText = { scale -> scaleOptions.find { it.first == scale }?.second ?: "${scale}x" },
                    onOptionSelected = { scale -> resScale.floatValue = scale }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSelectionItem(
                    title = "Anisotropic Filtering",
                    description = "Enhances texture quality at oblique angles.",
                    selectedValue = maxAnisotropy.floatValue,
                    options = anisotropyOptions.map { it.first },
                    getDisplayText = { anisotropy -> anisotropyOptions.find { it.first == anisotropy }?.second ?: "${anisotropy}x" },
                    onOptionSelected = { anisotropy -> maxAnisotropy.floatValue = anisotropy }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Disable Threaded Rendering",
                    description = "Forces single-threaded rendering which may improve stability at the cost of performance.",
                    isChecked = disableThreadedRendering.value,
                    onCheckedChange = { disableThreadedRendering.value = it }
                )
            }

            SettingsCard(title = "Post-Processing") {
                SettingSwitchItem(
                    title = "Stretch to Fullscreen",
                    description = "Forces the image to fill the entire display, ignoring aspect ratio.",
                    isChecked = stretchToFullscreen.value,
                    onCheckedChange = { stretchToFullscreen.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Shader Cache",
                    description = "Caches compiled shaders to reduce stuttering during gameplay.",
                    isChecked = enableShaderCache.value,
                    onCheckedChange = { enableShaderCache.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Texture Recompression",
                    description = "Recompresses textures to save memory at the cost of some CPU overhead.",
                    isChecked = enableTextureRecompression.value,
                    onCheckedChange = { enableTextureRecompression.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Macro HLE",
                    description = "Enables high-level emulation for certain macros.",
                    isChecked = enableMacroHLE.value,
                    onCheckedChange = { enableMacroHLE.value = it }
                )
            }

            SettingsCard(title = "GPU driver") {
                var isDriverSelectorOpen by remember { mutableStateOf(false) }
                SettingActionItem(
                    title = "Install custom driver",
                    description = "Opens the driver installation menu."
                ) {
                    Button(
                        onClick = { isDriverSelectorOpen = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Install")
                    }
                }

                SimpleAlertDialog.Custom(
                    showDialog = isDriverSelectorOpen,
                    onDismissRequest = { isDriverSelectorOpen = false },
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

        @Composable
        fun UserInterfaceTab(
            orientationPref: androidx.compose.runtime.MutableState<OrientationPreference>,
            overlayMenuPosition: androidx.compose.runtime.MutableState<OverlayMenuPosition>,
            overlayOpacity: androidx.compose.runtime.MutableFloatState,
            isGrid: androidx.compose.runtime.MutableState<Boolean>,
            firmwareVersion: androidx.compose.runtime.MutableState<String>,
            showKeyDialog: androidx.compose.runtime.MutableState<Boolean>,
            showFirwmareDialog: androidx.compose.runtime.MutableState<Boolean>,
            showDataResetDialog: androidx.compose.runtime.MutableState<Boolean>,
            showDataImportDialog: androidx.compose.runtime.MutableState<Boolean>,
            settingsViewModel: SettingsViewModel,
            mainViewModel: MainViewModel
        ) {
            SettingsCard(title = "Display") {
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
                    }
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
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
                SettingActionItem(
                    title = "Overlay transparency",
                    description = "Adjust the opacity of the floating menu."
                ) {
                    Slider(
                        modifier = Modifier.width(150.dp),
                        value = overlayOpacity.floatValue,
                        onValueChange = {
                            val clamped = it.coerceIn(0f, 1f)
                            overlayOpacity.floatValue = clamped
                            val qs = QuickSettings(mainViewModel.activity)
                            qs.overlayMenuOpacity = clamped
                            qs.save()
                        },
                        valueRange = 0f..1f,
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
                                        Text("${(overlayOpacity.floatValue * 100f).toInt()}%")
                                    }
                                },
                                interactionSource = interactionSource
                            ) {
                                Icon(
                                    imageVector = org.kenjinx.android.Icons.circle(
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Use Grid",
                    description = "Display games in a grid layout.",
                    isChecked = isGrid.value,
                    onCheckedChange = { isGrid.value = it }
                )
            }

            SettingsCard(title = "System & Data") {
                SettingActionItem(
                    title = "System Firmware",
                    description = "Installed version: ${firmwareVersion.value}"
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Install Keys",
                    description = "Install prod.keys or title.keys."
                ) {
                    Button(onClick = { showKeyDialog.value = true }) { Text("Install") }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Install Firmware",
                    description = "Install from a zip or xci file."
                ) {
                    Button(onClick = { showFirwmareDialog.value = true }) { Text("Install") }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Add Game Folder",
                    description = "Select a folder to scan for games."
                ) {
                    Button(onClick = { settingsViewModel.openGameFolder() }) { Text("Add") }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Select Updates/DLC Folder",
                    description = "Select a folder for updates and DLC."
                ) {
                    Button(onClick = { settingsViewModel.openUpdatesFolder() }) { Text("Select") }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Open App Folder",
                    description = "Open the internal app data folder."
                ) {
                    Button(onClick = {
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
                            return@Button
                        } catch (_: ActivityNotFoundException) {}
                        try {
                            mainViewModel.activity.startActivity(createIntent("android.provider.action.BROWSE"))
                            return@Button
                        } catch (_: ActivityNotFoundException) {}
                        try {
                            mainViewModel.activity.startActivity(createIntent("com.google.android.documentsui"))
                            return@Button
                        } catch (_: ActivityNotFoundException) {}
                        try {
                            mainViewModel.activity.startActivity(createIntent("com.android.documentsui"))
                            return@Button
                        } catch (_: ActivityNotFoundException) {}
                    }) { Text("Open") }
                }
            }

            SettingsCard(title = "App Data Management") {
                SettingActionItem(
                    title = "Reinit App Data",
                    description = "Resets games, profiles, and system folders."
                ) {
                    Button(onClick = { showDataResetDialog.value = true }) { Text("Reset") }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Import App Data",
                    description = "Import from a previously exported zip file."
                ) {
                    Button(onClick = { showDataImportDialog.value = true }) { Text("Import") }
                }
            }
        }

        @Composable
        fun InputTab(
            useVirtualController: androidx.compose.runtime.MutableState<Boolean>,
            useSwitchLayout: androidx.compose.runtime.MutableState<Boolean>,
            vcPreset: androidx.compose.runtime.MutableState<VirtualControllerPreset>,
            controllerScale: androidx.compose.runtime.MutableFloatState,
            controllerStickSensitivity: androidx.compose.runtime.MutableFloatState,
            enableDocked: androidx.compose.runtime.MutableState<Boolean>,
            enableMotion: androidx.compose.runtime.MutableState<Boolean>,
            mainViewModel: MainViewModel
        ) {
            SettingsCard(title = "Virtual Controller") {
                SettingSwitchItem(
                    title = "Use Virtual Controller",
                    description = "Enable the on-screen overlay controller.",
                    isChecked = useVirtualController.value,
                    onCheckedChange = { useVirtualController.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Use Switch Controller Layout",
                    description = "Use the standard Switch button arrangement.",
                    isChecked = useSwitchLayout.value,
                    onCheckedChange = { useSwitchLayout.value = it }
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
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    val interactionSourceScale: MutableInteractionSource = remember { MutableInteractionSource() }
                    SettingActionItem(
                        title = "Controller Scale",
                        description = "Adjust the size of the virtual buttons."
                    ) {
                        Slider(
                            modifier = Modifier.width(150.dp),
                            value = controllerScale.floatValue,
                            onValueChange = { v ->
                                val clamped = v.coerceIn(0.5f, 1.5f)
                                controllerScale.floatValue = clamped
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
                                            Text("${(controllerScale.floatValue * 100f).roundToInt()}%")
                                        }
                                    },
                                    interactionSource = interactionSourceScale
                                ) {
                                    Icon(
                                        imageVector = org.kenjinx.android.Icons.circle(
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onValueChangeFinished = {
                                val qs = QuickSettings(mainViewModel.activity)
                                qs.controllerScale = controllerScale.floatValue.coerceIn(0.5f, 1.5f)
                                qs.save()
                            }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
                    SettingActionItem(
                        title = "Stick Sensitivity",
                        description = "Adjust the responsiveness of the analog sticks."
                    ) {
                        Slider(
                            modifier = Modifier.width(150.dp),
                            value = controllerStickSensitivity.floatValue,
                            onValueChange = { controllerStickSensitivity.floatValue = it },
                            valueRange = 0.1f..2f,
                            steps = 20,
                            interactionSource = interactionSource,
                            thumb = {
                                Label(
                                    label = {
                                        PlainTooltip(modifier = Modifier
                                            .sizeIn(45.dp, 25.dp)
                                            .wrapContentWidth()) {
                                            Text("%.2f".format(controllerStickSensitivity.floatValue))
                                        }
                                    },
                                    interactionSource = interactionSource
                                ) {
                                    Icon(
                                        imageVector = org.kenjinx.android.Icons.circle(
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                }
            }

            SettingsCard(title = "Hardware Input") {
                SettingSwitchItem(
                    title = "Docked Mode",
                    description = "Simulate the console being in its dock.",
                    isChecked = enableDocked.value,
                    onCheckedChange = { enableDocked.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Motion Sensor",
                    description = "Enable gyroscope and accelerometer input.",
                    isChecked = enableMotion.value,
                    onCheckedChange = { enableMotion.value = it }
                )
            }
        }

        @Composable
        fun SystemTab(
            systemLanguage: androidx.compose.runtime.MutableState<SystemLanguage>,
            regionCode: androidx.compose.runtime.MutableState<RegionCode>,
            vSyncMode: androidx.compose.runtime.MutableState<VSyncMode>,
            memoryConfiguration: androidx.compose.runtime.MutableState<MemoryConfiguration>,
            enableFsIntegrityChecks: androidx.compose.runtime.MutableState<Boolean>,
            ignoreMissingServices: androidx.compose.runtime.MutableState<Boolean>,
            enablePerformanceMode: androidx.compose.runtime.MutableState<Boolean>
        ) {
            SettingsCard(title = "Region & Language") {
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
                    onOptionSelected = { lang -> systemLanguage.value = lang }
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
                    onOptionSelected = { reg -> regionCode.value = reg }
                )
            }

            SettingsCard(title = "System Behaviour") {
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
                    onOptionSelected = { mode -> vSyncMode.value = mode }
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
                    onOptionSelected = { configuration -> memoryConfiguration.value = configuration }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Fs Integrity Check",
                    description = "Verify file system integrity during emulation.",
                    isChecked = enableFsIntegrityChecks.value,
                    onCheckedChange = { enableFsIntegrityChecks.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Ignore Missing Services",
                    description = "Continue execution if a system service is missing.",
                    isChecked = ignoreMissingServices.value,
                    onCheckedChange = { ignoreMissingServices.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Performance Mode",
                    description = "Optimize emulation for maximum performance.",
                    isChecked = enablePerformanceMode.value,
                    onCheckedChange = { enablePerformanceMode.value = it }
                )
            }
        }

        @Composable
        fun CpuTab(
            useNce: androidx.compose.runtime.MutableState<Boolean>,
            enablePptc: androidx.compose.runtime.MutableState<Boolean>,
            enableLowPowerPptc: androidx.compose.runtime.MutableState<Boolean>,
            enableJitCacheEviction: androidx.compose.runtime.MutableState<Boolean>,
            memoryManagerMode: androidx.compose.runtime.MutableState<MemoryManagerMode>
        ) {
            SettingsCard(title = "CPU Emulation") {
                SettingSwitchItem(
                    title = "NCE (Native Code Execution)",
                    description = "Execute translated code natively for better performance.",
                    isChecked = useNce.value,
                    onCheckedChange = { useNce.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "PPTC",
                    description = "Profiled Persistent Translation Cache.",
                    isChecked = enablePptc.value,
                    onCheckedChange = { enablePptc.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Low-Power PPTC",
                    description = "Reduce CPU usage for PPTC at the cost of slower loading.",
                    isChecked = enableLowPowerPptc.value,
                    onCheckedChange = { enableLowPowerPptc.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Jit Cache Eviction",
                    description = "Evict old JIT compiled code to save memory.",
                    isChecked = enableJitCacheEviction.value,
                    onCheckedChange = { enableJitCacheEviction.value = it }
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
                    onOptionSelected = { mode -> memoryManagerMode.value = mode }
                )
            }
        }

        @Composable
        fun LoggingTab(
            enableStubLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableInfoLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableWarningLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableErrorLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableGuestLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableTraceLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableFsAccessLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableDebugLogs: androidx.compose.runtime.MutableState<Boolean>,
            enableGraphicsLogs: androidx.compose.runtime.MutableState<Boolean>,
            fsGlobalAccessLogMode: androidx.compose.runtime.MutableIntState,
            mainViewModel: MainViewModel
        ) {
            SettingsCard(title = "Log Levels") {
                SettingSwitchItem(
                    title = "Stub Logs",
                    description = "Log calls to unimplemented functions.",
                    isChecked = enableStubLogs.value,
                    onCheckedChange = { enableStubLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Info Logs",
                    description = "General informational messages.",
                    isChecked = enableInfoLogs.value,
                    onCheckedChange = { enableInfoLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Warning Logs",
                    description = "Potential issues that don't stop execution.",
                    isChecked = enableWarningLogs.value,
                    onCheckedChange = { enableWarningLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Error Logs",
                    description = "Critical errors and failures.",
                    isChecked = enableErrorLogs.value,
                    onCheckedChange = { enableErrorLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Guest Logs",
                    description = "Logs generated by the emulated system.",
                    isChecked = enableGuestLogs.value,
                    onCheckedChange = { enableGuestLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Trace Logs",
                    description = "Detailed execution traces.",
                    isChecked = enableTraceLogs.value,
                    onCheckedChange = { enableTraceLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Fs Access Logs",
                    description = "Log file system access operations.",
                    isChecked = enableFsAccessLogs.value,
                    onCheckedChange = { enableFsAccessLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Debug Logs",
                    description = "Internal debugging information.",
                    isChecked = enableDebugLogs.value,
                    onCheckedChange = { enableDebugLogs.value = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingSwitchItem(
                    title = "Graphics Logs",
                    description = "Logs related to the rendering pipeline.",
                    isChecked = enableGraphicsLogs.value,
                    onCheckedChange = { enableGraphicsLogs.value = it }
                )
            }

            SettingsCard(title = "Advanced") {
                val fsGlobalAccessOptions = listOf(0, 1, 2, 3)
                SettingSelectionItem(
                    title = "Fs Global Access Log Mode",
                    description = "Set the verbosity level for file system logs.",
                    selectedValue = fsGlobalAccessLogMode.intValue,
                    options = fsGlobalAccessOptions,
                    getDisplayText = { fsGlobalAccess -> fsGlobalAccess.toString() },
                    onOptionSelected = { fsGlobalAccess -> fsGlobalAccessLogMode.intValue = fsGlobalAccess }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingActionItem(
                    title = "Export Logs",
                    description = "Send the current log file for analysis."
                ) {
                    Button(onClick = { mainViewModel.logging.requestExport() }) { Text("Send") }
                }
            }
        }

        // ---- Overlay position dropdown ----
        @Composable
        fun OverlayPositionDropdown(
            selectedPosition: OverlayMenuPosition,
            onPositionSelected: (OverlayMenuPosition) -> Unit
        ) {
            val options = listOf(
                OverlayMenuPosition.BottomMiddle,
                OverlayMenuPosition.BottomLeft,
                OverlayMenuPosition.BottomRight,
                OverlayMenuPosition.TopMiddle,
                OverlayMenuPosition.TopLeft,
                OverlayMenuPosition.TopRight
            )

            DropdownSelector(
                label = "Overlay Menu Position",
                selectedValue = selectedPosition,
                options = options,
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
                onOptionSelected = onPositionSelected
            )
        }

        // ---- Dropdowns for language & region ----

        @Composable
        fun LanguageDropdown(
            selectedLanguage: SystemLanguage,
            onLanguageSelected: (SystemLanguage) -> Unit
        ) {
            val options = SystemLanguage.entries.toTypedArray()
            DropdownSelector(
                label = "System Language",
                selectedValue = selectedLanguage,
                options = options.toList(),
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
                onOptionSelected = onLanguageSelected
            )
        }

        @Composable
        fun RegionDropdown(
            selectedRegion: RegionCode,
            onRegionSelected: (RegionCode) -> Unit
        ) {
            val options = RegionCode.entries.toTypedArray()
            DropdownSelector(
                label = "Region",
                selectedValue = selectedRegion,
                options = options.toList(),
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
                onOptionSelected = onRegionSelected
            )
        }

        // ---- Dropdown for orientation ----
        @Composable
        fun OrientationDropdown(
            selectedOrientation: OrientationPreference,
            onOrientationSelected: (OrientationPreference) -> Unit
        ) {
            val options = listOf(
                OrientationPreference.Sensor,
                OrientationPreference.SensorLandscape,
                OrientationPreference.SensorPortrait
            )

            DropdownSelector(
                label = "Screen Orientation",
                selectedValue = selectedOrientation,
                options = options,
                getDisplayText = { opt ->
                    when (opt) {
                        OrientationPreference.Sensor -> "Sensor"
                        OrientationPreference.SensorLandscape -> "Sensor Landscape"
                        OrientationPreference.SensorPortrait -> "Sensor Portrait"
                    }
                },
                onOptionSelected = onOrientationSelected
            )
        }

        @Composable
        fun VirtualControllerPresetDropdown(
            selectedPreset: VirtualControllerPreset,
            onPresetSelected: (VirtualControllerPreset) -> Unit
        ) {
            val options = listOf(
                VirtualControllerPreset.Default,
                VirtualControllerPreset.Layout2,
                VirtualControllerPreset.Layout3,
                VirtualControllerPreset.Layout4,
                VirtualControllerPreset.Layout5,
                VirtualControllerPreset.Layout6
            )

            DropdownSelector(
                label = "Controller Layout",
                selectedValue = selectedPreset,
                options = options,
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
                onOptionSelected = onPresetSelected
            )
        }


        @Composable
        fun MemoryModeDropdown(
            selectedMemoryManagerMode: MemoryManagerMode,
            onModeSelected: (MemoryManagerMode) -> Unit
        ) {
            val modes = MemoryManagerMode.entries.toTypedArray()

            DropdownSelector(
                label = "Memory Manager Mode",
                selectedValue = selectedMemoryManagerMode,
                options = modes.toList(),
                getDisplayText = { mode ->
                    when(mode) {
                        MemoryManagerMode.SoftwarePageTable -> "Software"
                        MemoryManagerMode.HostMapped -> "Host (fast)"
                        MemoryManagerMode.HostMappedUnsafe -> "Host Unchecked (fastest, unsafe)"
                    }
                },
                onOptionSelected = onModeSelected
            )
        }

        @Composable
        fun VSyncDropdown(
            selectedVSyncMode: VSyncMode,
            onModeSelected: (VSyncMode) -> Unit
        ) {
            val modes = VSyncMode.entries.toTypedArray()

            DropdownSelector(
                label = "VSync",
                selectedValue = selectedVSyncMode,
                options = modes.toList(),
                getDisplayText = { mode ->
                    when(mode) {
                        VSyncMode.Switch -> "Switch"
                        VSyncMode.Unbounded -> "Unbounded"
                    }
                },
                onOptionSelected = onModeSelected
            )
        }

        @Composable
        fun MemoryDropdown(
            selectedMemoryConfiguration: MemoryConfiguration,
            onConfigurationSelected: (MemoryConfiguration) -> Unit
        ) {
            val modes = MemoryConfiguration.entries.toTypedArray()

            DropdownSelector(
                label = "DRAM Size",
                selectedValue = selectedMemoryConfiguration,
                options = modes.toList(),
                getDisplayText = { configuration ->
                    when(configuration) {
                        MemoryConfiguration.MemoryConfiguration4GiB -> "4GiB"
                        MemoryConfiguration.MemoryConfiguration6GiB -> "6GiB"
                        MemoryConfiguration.MemoryConfiguration8GiB -> "8GiB"
                        MemoryConfiguration.MemoryConfiguration10GiB -> "10GiB"
                        MemoryConfiguration.MemoryConfiguration12GiB -> "12GiB"
                    }
                },
                onOptionSelected = onConfigurationSelected
            )
        }

        @Composable
        fun ResolutionScaleDropdown(
            selectedScale: Float,
            onScaleSelected: (Float) -> Unit
        ) {
            val scaleOptions = listOf(
                0.25f to "0.25X (180p/270p)",
                0.5f to "0.5X (360p/540p)",
                0.75f to "0.75X (540p/810p)",
                1f to "1X (720p/1080p)",
                1.25f to "1.25X (900p/1350p)",
                1.5f to "1.5X (1080p/1620p)",
                2f to "2X (1440p/2160p) (Slow)",
                3f to "3X (2160p/3240p) (Slow)",
                4f to "4X (2880p/4320p) (Slow)"
            )

            DropdownSelector(
                label = "Resolution Scale",
                selectedValue = selectedScale,
                options = scaleOptions.map { it.first },
                getDisplayText = { scale ->
                    scaleOptions.find { it.first == scale }?.second ?: "${scale}x"
                },
                onOptionSelected = onScaleSelected
            )
        }

        @Composable
        fun AnisotropicFilteringDropdown(
            selectedAnisotropy: Float,
            onAnisotropySelected: (Float) -> Unit
        ) {
            val anisotropyOptions = listOf(
                0.0f to "0x",
                1.0f to "2x",
                2.0f to "4x",
                3.0f to "8x",
                4.0f to "16x"
            )

            DropdownSelector(
                label = "Anisotropic Filtering",
                selectedValue = selectedAnisotropy,
                options = anisotropyOptions.map { it.first },
                getDisplayText = { anisotropy ->
                    anisotropyOptions.find { it.first == anisotropy }?.second ?: "${anisotropy}x"
                },
                onOptionSelected = onAnisotropySelected
            )
        }

        @Composable
        fun FsGlobalAccessLogModeDropdown(
            selectedFsGlobalAccess: Int,
            onFsGlobalAccessSelected: (Int) -> Unit
        ) {
            val fsGlobalAccessOptions = listOf(
                0 to "0",
                1 to "1",
                2 to "2",
                3 to "3"
            )

            DropdownSelector(
                label = "Fs Global Access Log Mode",
                selectedValue = selectedFsGlobalAccess,
                options = fsGlobalAccessOptions.map { it.first },
                getDisplayText = { fsGlobalAccess ->
                    fsGlobalAccessOptions.find { it.first == fsGlobalAccess }?.second ?: "${fsGlobalAccess}x"
                },
                onOptionSelected = onFsGlobalAccessSelected
            )
        }
    }
}