package gr.gkortsaridis.gatekeeper.UI.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Entities.toColor
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme

@Preview
@Composable
fun GateKeeperCardFront(
    card: CreditCard = GateKeeperDevelopMockData.mockCard,
    vault: Vault = GateKeeperDevelopMockData.mockVault,
    onCardFlip: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(16.dp)
                .background(vault?.color?.toColor() ?: GateKeeperTheme.white),
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = GateKeeperTheme.error_red)
            .clickable { onCardFlip() }
        ) {

        }
    }
}

@Preview
@Composable
fun GateKeeperCardBack(
    card: CreditCard = GateKeeperDevelopMockData.mockCard,
    vault: Vault = GateKeeperDevelopMockData.mockVault,
    onCardFlip: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(16.dp)
                .background(vault?.color?.toColor() ?: GateKeeperTheme.white),
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = GateKeeperTheme.done_green)
            .clickable { onCardFlip() }
        ) {

        }
    }
}