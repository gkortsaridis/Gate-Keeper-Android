package gr.gkortsaridis.gatekeeper.UI.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.toColor
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperDevelopMockData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme


@Preview
@Composable
fun GateKeeperNote(
    note: Note = GateKeeperDevelopMockData.mockNote
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier
            .background(color = note.color?.toColor() ?: GateKeeperTheme.white)
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { }
        ) {
            Text(text = note.title)
            Text(text = note.body)
        }
    }

}