package com.xiaojianchi.app.update

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xiaojianchi.app.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed interface UpdateState {
    data object Idle : UpdateState

    data object Checking : UpdateState

    data object UpToDate : UpdateState

    data class Available(
        val manifest: UpdateManifest,
    ) : UpdateState

    data class Downloading(
        val manifest: UpdateManifest,
        val progress: Int,
    ) : UpdateState

    data class Downloaded(
        val manifest: UpdateManifest,
        val apk: File,
        val needsPermission: Boolean = false,
    ) : UpdateState

    data class Error(
        val message: String,
    ) : UpdateState
}

class UpdateViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val manager = UpdateManager(application)
    private val _state = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = _state.asStateFlow()
    private var checkJob: Job? = null

    fun check(showResult: Boolean = true) {
        if (checkJob?.isActive == true || _state.value is UpdateState.Downloading) return
        checkJob =
            viewModelScope.launch {
                _state.value = UpdateState.Checking
                try {
                    val latest = manager.checkForUpdate(listOf(BuildConfig.UPDATE_MANIFEST_URL, BuildConfig.UPDATE_FALLBACK_URL))
                    _state.value =
                        if (latest.versionCode > BuildConfig.VERSION_CODE) {
                            UpdateState.Available(latest)
                        } else if (showResult) {
                            UpdateState.UpToDate
                        } else {
                            UpdateState.Idle
                        }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _state.value = if (showResult) UpdateState.Error("暂时无法连接更新服务，请稍后再试。") else UpdateState.Idle
                }
            }
    }

    fun download(manifest: UpdateManifest) {
        viewModelScope.launch {
            _state.value = UpdateState.Downloading(manifest, 0)
            try {
                val apk =
                    manager.download(manifest) { progress ->
                        _state.value = UpdateState.Downloading(manifest, progress)
                    }
                _state.value = UpdateState.Downloaded(manifest, apk)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.value = UpdateState.Error("更新包下载或校验失败，请稍后重试。")
            }
        }
    }

    fun install(
        manifest: UpdateManifest,
        apk: File,
    ) {
        val opened = manager.openInstaller(apk)
        _state.value = UpdateState.Downloaded(manifest, apk, needsPermission = !opened)
    }

    fun dismiss() {
        if (_state.value !is UpdateState.Downloading && _state.value !is UpdateState.Checking) _state.value = UpdateState.Idle
    }
}
