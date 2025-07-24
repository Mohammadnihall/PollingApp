package pollingapp.by.mohammadnihalls3451385.livepolls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pollingapp.by.mohammadnihalls3451385.Poll
import pollingapp.by.mohammadnihalls3451385.PollingAppTheme
import pollingapp.by.mohammadnihalls3451385.R
import pollingapp.by.mohammadnihalls3451385.mypolls.PollViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePollsScreen(pollViewModel: PollViewModel) {
    val allPolls by pollViewModel.livePolls.collectAsState()
    val votedPolls by pollViewModel.votedPolls.collectAsState()
    var selectedTopic by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }

    val categories = remember {
        listOf(
            "All Topics",
            "Technology", "Sports", "Politics", "Entertainment", "Food",
            "Travel", "Health", "Education", "Science", "Art",
            "Music", "Fashion", "Business", "Environment", "Gaming",
            "Books", "Movies", "Television", "Lifestyle", "General"
        )
    }

    val ukStates = remember {
        listOf(
            "All Locations",
            "England", "Scotland", "Wales", "Northern Ireland",
            "London", "Manchester", "Birmingham", "Glasgow", "Edinburgh",
            "Cardiff", "Belfast", "Liverpool", "Bristol", "Leeds",
            "Sheffield", "Newcastle", "Nottingham", "Leicester", "Southampton",
            "Plymouth"
        )
    }


    val filteredPolls = remember(allPolls, selectedTopic, selectedLocation) {

        var newSelTopic = selectedTopic
        var newSelLocation = selectedLocation

        if (selectedTopic == "All Topics")
            newSelTopic = ""

        if (selectedLocation == "All Locations")
            newSelLocation = ""

        pollViewModel.filterLivePolls(newSelTopic, newSelLocation)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Live Polls",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filter Dropdowns (Spinners)
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Topic Filter Spinner
            var expandedTopic by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedTopic,
                onExpandedChange = { expandedTopic = !expandedTopic },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedTopic,
                    onValueChange = { selectedTopic = it },
                    readOnly = true,
                    label = { Text("Filter by Topic") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_filter_list_alt_24),
                            contentDescription = "Topic Filter"
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTopic) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTopic,
                    onDismissRequest = { expandedTopic = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(if (category == "All Topics") "All Topics" else category) },
                            onClick = {
                                selectedTopic = category
                                expandedTopic = false
                            }
                        )
                    }
                }
            }

            // Location Filter Spinner
            var expandedLocation by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedLocation,
                onExpandedChange = { expandedLocation = !expandedLocation },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedLocation,
                    onValueChange = { selectedLocation = it },
                    readOnly = true, // Make it a true spinner
                    label = { Text("Filter by Location") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_filter_list_alt_24),
                            contentDescription = "Topic Filter"
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocation) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedLocation,
                    onDismissRequest = { expandedLocation = false }
                ) {
                    ukStates.forEach { location ->
                        DropdownMenuItem(
                            text = {
                                Text(if (location == "All Locations") "All Topics" else location)  //Text(location.ifEmpty { "All Locations" })
                            },
                            onClick = {
                                selectedLocation = location
                                expandedLocation = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredPolls.isEmpty()) {
            Text(
                text = "No live polls available matching your criteria.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredPolls, key = { it.id }) { poll ->
                    val hasVoted = votedPolls.contains(poll.id)
                    PollCard(
                        poll = poll,
                        onVote = { optionId -> pollViewModel.voteOnPoll(poll.id, optionId) },
                        hasVoted = hasVoted
                    )
                }
            }
        }
    }
}

@Composable
fun PollCard(poll: Poll, onVote: (String) -> Unit, hasVoted: Boolean) {
    var selectedOptionId by remember(poll.id) { mutableStateOf<String?>(null) } // Reset selection when poll changes

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
                text = "Posted by: ${poll.creatorName} on ${dateFormatter.format(Date(poll.creationDate))} at ${
                    timeFormatter.format(
                        Date(poll.creationDate)
                    )
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (hasVoted) {
                Text(
                    text = "You have voted on this poll.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Show voting results if already voted
                val totalVotes = poll.getTotalVotes()
                poll.options.forEach { option ->
                    val percentage =
                        if (totalVotes > 0) (option.votes.toFloat() / totalVotes) * 100 else 0f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${option.text}:", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "${option.votes} votes (${"%.1f".format(percentage)}%)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    LinearProgressIndicator(
                        progress = if (totalVotes > 0) option.votes.toFloat() / totalVotes else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            } else {
                // Show voting options if not voted
                Column(Modifier.selectableGroup()) {
                    poll.options.forEach { option ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = (option.id == selectedOptionId),
                                    onClick = { selectedOptionId = option.id },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (option.id == selectedOptionId),
                                onClick = null // null recommended for accessibility with selectable
                            )
                            Text(
                                text = option.text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedOptionId?.let {
                            onVote(it)
                            selectedOptionId = null
                        }
                    },
                    enabled = selectedOptionId != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Vote")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLivePollsScreen() {
    PollingAppTheme {
        LivePollsScreen(pollViewModel = viewModel())
    }
}