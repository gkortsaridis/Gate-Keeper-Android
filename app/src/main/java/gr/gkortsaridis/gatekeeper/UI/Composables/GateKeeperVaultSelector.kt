package gr.gkortsaridis.gatekeeper.UI.Composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperShapes
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme

@Preview
@Composable
fun vaultSelector(
    currentVault: Vault = GateKeeperDevelopMockData.mockVault,
    onVaultClick: () -> Unit = {},
){

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Card(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(),
            shape = GateKeeperShapes.getBottomCornerCutShape(cornerDiagonalDp = 10),
            backgroundColor = GateKeeperTheme.colorPrimaryDark,
            elevation = 4.dp
        ) {}

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .height(56.dp)
                .fillMaxWidth()
                .clickable { onVaultClick() },
            shape = RoundedCornerShape(26.dp),
            backgroundColor = currentVault.getVaultColorResource(),
            elevation = 5.dp,
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vault),
                    contentDescription = "Localized description",
                    colorFilter = ColorFilter.tint(currentVault.getVaultColorAccent()),
                    modifier = Modifier
                        .size(30.dp, 30.dp)
                )

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = currentVault.name,
                    color = currentVault.getVaultColorAccent(),
                    fontSize = 19.sp
                )
            }
        }
    }
}