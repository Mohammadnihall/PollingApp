package pollingapp.by.mohammadnihalls3451385.mypolls

import pollingapp.by.mohammadnihalls3451385.Poll


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import pollingapp.by.mohammadnihalls3451385.PollingAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyPollsScreen(navController: NavController, pollViewModel: PollViewModel) {
    val myPolls by pollViewModel.myPolls.collectAsState()

    val context = LocalContext.current

    val currentUserId = AppData.getUserEmail(context)!!.replace(".",",")

        // Main content column for My Polls screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (myPolls.isEmpty()) {
                // Case: No polls created by the user
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "You haven't created any polls yet.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Button(
                        onClick = { navController.navigate("addPoll") },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Poll Icon", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Poll")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myPolls.filter { it.creatorId == currentUserId }, key = { it.id }) { poll ->
                        MyPollCard(
                            poll = poll,
                            onDelete = { pollViewModel.deletePoll(poll.id) }
                        )
                    }
                }
                // Button at the bottom when polls exist
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("addPoll") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Poll Icon", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Poll")
                }
            }
        }

}


@Composable
fun MyPollCard(poll: Poll, onDelete: () -> Unit) {
    // Date formatter
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = poll.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Topic: ${poll.topic} | Location: ${poll.location}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Posted by: ${poll.creatorName} on ${dateFormatter.format(Date(poll.creationDate))} at ${timeFormatter.format(Date(poll.creationDate))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display voting results
            Text(
                text = "Voting Results:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val totalVotes = poll.getTotalVotes()
            poll.options.forEach { option ->
                val percentage = if (totalVotes > 0) (option.votes.toFloat() / totalVotes) * 100 else 0f
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${option.text}:", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "${option.votes} votes (${"%.1f".format(percentage)}%)", style = MaterialTheme.typography.bodyMedium)
                }
                LinearProgressIndicator(
                    progress = if (totalVotes > 0) option.votes.toFloat() / totalVotes else 0f,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Poll")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Poll")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyPollsScreen() {
    PollingAppTheme {
        MyPollsScreen(navController = rememberNavController(), pollViewModel = viewModel())
    }
}