package pollingapp.by.mohammadnihalls3451385.mypolls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import pollingapp.by.mohammadnihalls3451385.PollingAppTheme

@Composable
fun ManageMyPollsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Manage Your Polls",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate("addPoll") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
                .padding(bottom = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Info, contentDescription = "Add Poll Icon", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add New Poll")
        }

        Button(
            onClick = { navController.navigate("myPolls") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Info, contentDescription = "View My Polls Icon", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("View/Delete My Polls")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManageMyPollsScreen() {
    PollingAppTheme {
        ManageMyPollsScreen(navController = rememberNavController())
    }
}