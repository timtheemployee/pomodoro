package shared.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import shared.domain.OverlayColor

class SharedRepository {

    private val _overlayColor = MutableSharedFlow<OverlayColor>()

    val overlayColor = _overlayColor.asSharedFlow()

    suspend fun setOverlayColor(color: OverlayColor) {
        _overlayColor.emit(color)
    }
}