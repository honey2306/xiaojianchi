package com.xiaojianchi.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojianchi.app.data.HabitEntity
import com.xiaojianchi.app.ui.DayDetailSheet
import com.xiaojianchi.app.ui.HabitEditorSheet
import com.xiaojianchi.app.ui.HistoryScreen
import com.xiaojianchi.app.ui.HomeScreen
import com.xiaojianchi.app.ui.LittleStepsTheme
import com.xiaojianchi.app.ui.Muted
import com.xiaojianchi.app.ui.NoteEditorSheet
import com.xiaojianchi.app.ui.OnSage
import com.xiaojianchi.app.ui.Paper
import com.xiaojianchi.app.ui.PaperTexture
import com.xiaojianchi.app.ui.Sage
import com.xiaojianchi.app.ui.SageSoft
import com.xiaojianchi.app.ui.SettingsDialog
import com.xiaojianchi.app.update.UpdateDialog
import com.xiaojianchi.app.update.UpdateViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val updateViewModel: UpdateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LittleStepsTheme {
                LittleStepsApp(
                    viewModel = viewModel,
                    updateViewModel = updateViewModel,
                    notificationHabitId = intent.getLongExtra("habitId", -1L).takeIf { it >= 0 },
                )
            }
        }
    }
}

private sealed interface AppSheet {
    data class HabitEditor(
        val habit: HabitEntity?,
    ) : AppSheet

    data class DayDetail(
        val habitId: Long,
        val date: LocalDate,
    ) : AppSheet

    data class NoteEditor(
        val habitId: Long,
        val date: LocalDate,
    ) : AppSheet
}

@Composable
private fun LittleStepsApp(
    viewModel: MainViewModel,
    updateViewModel: UpdateViewModel,
    notificationHabitId: Long?,
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf("home") }
    var selectedHabitId by rememberSaveable { mutableLongStateOf(-1L) }
    var sheet by remember { mutableStateOf<AppSheet?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    val updateState by updateViewModel.state.collectAsStateWithLifecycle()
    var today by remember { mutableStateOf(LocalDate.now()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) today = LocalDate.now()
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) { updateViewModel.check(showResult = false) }

    LaunchedEffect(notificationHabitId) {
        if (notificationHabitId != null) {
            selectedHabitId = notificationHabitId
            tab = "history"
        }
    }

    fun showMessage(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
    ) {
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            val result = snackbar.showSnackbar(message = message, actionLabel = actionLabel, withDismissAction = true)
            if (result == SnackbarResult.ActionPerformed) onAction?.invoke()
        }
    }

    fun openNote(
        habitId: Long,
        date: LocalDate,
    ) {
        sheet = AppSheet.NoteEditor(habitId, date)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Paper).statusBarsPadding(),
        containerColor = Paper,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar(
                containerColor = Paper,
                tonalElevation = 0.dp,
            ) {
                NavigationBarItem(
                    selected = tab == "home",
                    onClick = { tab = "home" },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                    label = { Text("今天") },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = Sage,
                            selectedTextColor = Sage,
                            indicatorColor = SageSoft,
                            unselectedIconColor = Muted,
                            unselectedTextColor = Muted,
                        ),
                )
                NavigationBarItem(
                    selected = tab == "history",
                    onClick = { tab = "history" },
                    icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
                    label = { Text("足迹") },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = Sage,
                            selectedTextColor = Sage,
                            indicatorColor = SageSoft,
                            unselectedIconColor = Muted,
                            unselectedTextColor = Muted,
                        ),
                )
            }
        },
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            PaperTexture()
            if (tab == "home") {
                HomeScreen(
                    habits = habits,
                    today = today,
                    onAdd = { sheet = AppSheet.HabitEditor(null) },
                    onSettings = { showSettings = true },
                    onToggle = { id ->
                        viewModel.toggleToday(id) { checked ->
                            if (checked) {
                                showMessage("今天也做到了，明天见。", "写一句") { openNote(id, today) }
                            } else {
                                showMessage("已撤销今天的记录，没关系。")
                            }
                        }
                    },
                    onOpenHistory = { id ->
                        selectedHabitId = id
                        tab = "history"
                    },
                )
            } else {
                HistoryScreen(
                    habits = habits,
                    selectedId = selectedHabitId,
                    today = today,
                    onSelect = { selectedHabitId = it },
                    onDaySelected = { id, date -> sheet = AppSheet.DayDetail(id, date) },
                    onEdit = { id -> habits.firstOrNull { it.habit.id == id }?.let { sheet = AppSheet.HabitEditor(it.habit) } },
                    onAdd = { sheet = AppSheet.HabitEditor(null) },
                )
            }
        }
    }

    when (val current = sheet) {
        is AppSheet.HabitEditor -> {
            HabitEditorSheet(
                existing = current.habit,
                nextSortOrder = habits.size,
                onDismiss = { sheet = null },
                onSave = { habit ->
                    viewModel.saveHabit(habit) {
                        sheet = null
                        showMessage(if (current.habit == null) "新习惯已加入，从一次开始。" else "改变已保存。")
                    }
                },
                onArchive = { id ->
                    viewModel.archiveHabit(id) {
                        sheet = null
                        selectedHabitId = -1L
                        showMessage("习惯已归档，过去的坚持仍然保留。")
                    }
                },
                onRequestNotifications = {
                    if (Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
            )
        }

        is AppSheet.DayDetail -> {
            val habit = habits.firstOrNull { it.habit.id == current.habitId }
            val checkIn = habit?.checkIns?.firstOrNull { it.date == current.date }
            if (habit != null) {
                DayDetailSheet(
                    habitName = habit.habit.name,
                    date = current.date,
                    checkIn = checkIn,
                    today = today,
                    onDismiss = { sheet = null },
                    onCheckIn = {
                        viewModel.checkIn(current.habitId, current.date, backfill = current.date != today) {
                            sheet = null
                            showMessage("这一天的坚持，也已经好好记下。")
                        }
                    },
                    onEditNote = { openNote(current.habitId, current.date) },
                    onUncheck = {
                        viewModel.uncheck(current.habitId, current.date) {
                            sheet = null
                            showMessage("这一天的记录已撤销。")
                        }
                    },
                )
            }
        }

        is AppSheet.NoteEditor -> {
            val habit = habits.firstOrNull { it.habit.id == current.habitId }
            val checkIn = habit?.checkIns?.firstOrNull { it.date == current.date }
            if (habit != null && checkIn != null) {
                NoteEditorSheet(
                    habitName = habit.habit.name,
                    checkIn = checkIn,
                    onDismiss = { sheet = null },
                    onSave = { note ->
                        viewModel.saveNote(current.habitId, current.date, note) {
                            sheet = null
                            showMessage("今天的感受已收藏。")
                        }
                    },
                )
            }
        }

        null -> {
            Unit
        }
    }

    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false },
            updateState = updateState,
            onCheckUpdate = { updateViewModel.check(showResult = true) },
        )
    }
    UpdateDialog(
        state = updateState,
        onDismiss = updateViewModel::dismiss,
        onDownload = updateViewModel::download,
        onInstall = updateViewModel::install,
    )
}
