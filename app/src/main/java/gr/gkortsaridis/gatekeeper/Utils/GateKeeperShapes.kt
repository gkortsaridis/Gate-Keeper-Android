package gr.gkortsaridis.gatekeeper.Utils

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.unit.dp

object GateKeeperShapes {
    fun getDiagonalShape(diagonalHeightDp: Int): GenericShape {
        return GenericShape { size, _ ->
            moveTo(0f,0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - diagonalHeightDp.dp.value)
            lineTo(0f, size.height)
        }
    }

    fun getBottomCornerCutShape(cornerDiagonalDp: Int): GenericShape {
        return GenericShape { size, _ ->
            moveTo(0f,0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - cornerDiagonalDp.dp.value)
            lineTo(size.width - cornerDiagonalDp.dp.value, size.height)
            lineTo(cornerDiagonalDp.dp.value, size.height)
            lineTo(0f, size.height - cornerDiagonalDp.dp.value)
        }

    }

}