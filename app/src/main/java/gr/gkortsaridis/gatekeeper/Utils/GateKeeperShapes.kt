package gr.gkortsaridis.gatekeeper.Utils

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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

    fun getLoginCardShape(cornerDiagonalDp: Int): GenericShape {
        return GenericShape { size, _ ->
            arcTo(
                rect = Rect(
                    Offset(0f, 0f),
                    Size(cornerDiagonalDp.toFloat(), cornerDiagonalDp.toFloat())
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(size.width, 0f)

            lineTo(size.width, size.height)

            lineTo(cornerDiagonalDp.toFloat(), size.height)

            arcTo(
                rect = Rect(
                    Offset(0f, size.height-cornerDiagonalDp),
                    Size(cornerDiagonalDp.toFloat(), cornerDiagonalDp.toFloat())
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(0f,0f)
        }
    }

    fun getArcButtonShape(diagonalDp: Int): GenericShape {
        return GenericShape { size, _ ->

            arcTo(
                rect = Rect(
                    Offset(0f, 0f),
                    Size(diagonalDp.toFloat(), diagonalDp.toFloat())
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(size.width-diagonalDp, 0f)

            arcTo(
                rect = Rect(
                    Offset(size.width-diagonalDp, 0F),
                    Size(diagonalDp.toFloat(), diagonalDp.toFloat())
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(size.width, size.height)

            lineTo(0F, size.height)

            lineTo(0f,0f)
        }
    }

}