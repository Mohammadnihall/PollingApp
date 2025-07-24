package pollingapp.by.mohammadnihalls3451385.mypolls

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import pollingapp.by.mohammadnihalls3451385.Poll
import pollingapp.by.mohammadnihalls3451385.PollOption
import pollingapp.by.mohammadnihalls3451385.PollingAppTheme
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPollScreen(navController: NavController, pollViewModel: PollViewModel) {
    var pollTitle by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    val pollOptions = remember { mutableStateListOf("") }
    val context = LocalContext.current

    val categories = remember {
        listOf(
            "",
            "Technology", "Sports", "Politics", "Entertainment", "Food",
            "Travel", "Health", "Education", "Science", "Art",
            "Music", "Fashion", "Business", "Environment", "Gaming",
            "Books", "Movies", "Television", "Lifestyle", "General"
        )
    }

    val ukStates = remember {
        listOf(
            "",
            "England", "Scotland", "Wales", "Northern Ireland",
            "London", "Manchester", "Birmingham", "Glasgow", "Edinburgh",
            "Cardiff", "Belfast", "Liverpool", "Bristol", "Leeds",
            "Sheffield", "Newcastle", "Nottingham", "Leicester", "Southampton",
            "Plymouth"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Poll") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = pollTitle,
                onValueChange = { pollTitle = it },
                label = { Text("Poll Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            // Topic Spinner
            var expandedTopic by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedTopic,
                onExpandedChange = { expandedTopic = !expandedTopic },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedTopic,
                    onValueChange = { selectedTopic = it },
                    readOnly = true, // Make it a true spinner
                    label = { Text("Select Topic") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTopic) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTopic,
                    onDismissRequest = { expandedTopic = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(if (category.isEmpty()) "Select Topic" else category) },
                            onClick = {
                                selectedTopic = category
                                expandedTopic = false
                            }
                        )
                    }
                }
            }

            // Location Spinner
            var expandedLocation by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedLocation,
                onExpandedChange = { expandedLocation = !expandedLocation },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedLocation,
                    onValueChange = { selectedLocation = it },
                    readOnly = true, // Make it a true spinner
                    label = { Text("Select Location") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocation) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedLocation,
                    onDismissRequest = { expandedLocation = false }
                ) {
                    ukStates.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(if (location.isEmpty()) "Select Location" else location) },
                            onClick = {
                                selectedLocation = location
                                expandedLocation = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Poll Options:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f) // Take available space
            ) {
                itemsIndexed(pollOptions) { index, optionText ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = optionText,
                            onValueChange = { pollOptions[index] = it },
                            label = { Text("Option ${index + 1}") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (pollOptions.size > 2) { // Ensure at least two options remain
                                    pollOptions.removeAt(index)
                                } else {
                                    Toast.makeText(context, "A poll must have at least two options.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Option")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Button(
                onClick = { pollOptions.add("") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Option")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Option")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val userId = AppData.getUserEmail(context)!!.replace(".",",")
                    val userName = AppData.getUserFullName(context)!!

                    if (pollTitle.isNotBlank() && selectedTopic.isNotBlank() && selectedLocation.isNotBlank() &&
                        pollOptions.all { it.isNotBlank() } && pollOptions.size >= 2
                    ) {
                        val newPoll = Poll(
                            id = userId,
                            title = pollTitle,
                            topic = selectedTopic,
                            location = selectedLocation,
                            options = pollOptions.map { PollOption(id = UUID.randomUUID().toString(), text = it, votes = 0) },
                            creatorId = userId,
                            creatorName = userName,
                            creationDate = System.currentTimeMillis()
                        )
                        pollViewModel.addPoll(newPoll)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Please fill all fields and add at least two options.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Create Poll", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddPollScreen() {
    PollingAppTheme {
        AddPollScreen(navController = rememberNavController(), pollViewModel = viewModel())
    }
}