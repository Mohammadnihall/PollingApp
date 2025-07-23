package pollingapp.by.mohammadnihalls3451385

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp // Import FirebaseApp
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.filled.Info // Using Info as a placeholder icon
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock

import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import java.util.UUID
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            PollingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val pollViewModel: PollViewModel = viewModel()

                    // Since there's no Firebase Auth, we'll always start at splash,
                    // and LoginScreen will handle the "session" state.
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController = navController)
                        }
                        composable("login") {
                            LoginScreen(navController = navController, pollViewModel = pollViewModel)
                        }
                        composable("register") {
                            RegistrationScreen(navController = navController, pollViewModel = pollViewModel)
                        }
                        composable("home") {
                            HomeScreen(navController = navController, pollViewModel = pollViewModel)
                        }
                        composable("addPoll") {
                            AddPollScreen(navController = navController, pollViewModel = pollViewModel)
                        }
                        composable("myPolls") {
                            MyPollsScreen(navController = navController, pollViewModel = pollViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2000) // Display splash for 2 seconds
        // Always navigate to login, as there's no persistent auth state check here
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true } // Remove splash from back stack
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Polling App",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontSize = 48.sp // Larger font for splash
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, pollViewModel: PollViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email ID") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        usersRef.orderByChild("email").equalTo(email)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var userFound = false
                                    for (userSnapshot in snapshot.children) {
                                        val user = userSnapshot.getValue(User::class.java)
                                        if (user != null && user.password == password) { // WARNING: Insecure password comparison
                                            pollViewModel.setCurrentUser(user) // Set logged-in user in ViewModel
                                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                            userFound = true
                                            break
                                        }
                                    }
                                    if (!userFound) {
                                        Toast.makeText(context, "Invalid email or password.", Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Login Failed: ${error.message}", Toast.LENGTH_LONG).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Login", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* Handle forgot password */ }) {
                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Register",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { navController.navigate("register") }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    PollingAppTheme {
        LoginScreen(navController = rememberNavController(), pollViewModel = viewModel())
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController, pollViewModel: PollViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full Name Icon") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email ID") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text("Area") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Area Icon") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && area.isNotBlank()) {
                        // Check if email already exists
                        usersRef.orderByChild("email").equalTo(email)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        Toast.makeText(context, "Email already registered.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val newUserId = usersRef.push().key ?: UUID.randomUUID().toString()
                                        val newUser = User(
                                            uid = newUserId,
                                            fullName = fullName,
                                            email = email,
                                            password = password, // WARNING: Storing password directly is INSECURE
                                            area = area
                                        )
                                        usersRef.child(newUserId).setValue(newUser)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                                navController.navigate("login") {
                                                    popUpTo("register") { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Registration Failed: ${error.message}", Toast.LENGTH_LONG).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Register", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Login",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegistrationScreen() {
    PollingAppTheme {
        RegistrationScreen(navController = rememberNavController(), pollViewModel = viewModel())
    }
}





// Define your color palette here
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF673AB7), // A deep purple for primary
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9C27B0), // A slightly different purple for container
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF4CAF50), // A green for secondary actions
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF8BC34A),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF03A9F4), // A light blue for tertiary
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2196F3),
    onTertiaryContainer = Color.White,
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFCF6679),
    onErrorContainer = Color.White,
    background = Color(0xFFF0F2F5), // Light grey background
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White, // White card surfaces
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE0E0E0), // Lighter grey for surface variants
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFF4EFF4),
    inverseSurface = Color(0xFF313033),
    inversePrimary = Purple80,
    surfaceTint = Color(0xFF673AB7),
    // You can also define other colors like outlineVariant, scrim, etc.
)

@Composable
fun PollingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}




val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)





// Represents a user in the system
@IgnoreExtraProperties
data class User(
    var uid: String = "",
    var fullName: String = "",
    var email: String = "",
    var password: String = "", // WARNING: Storing password directly is INSECURE
    var area: String = ""
) {
    // No-argument constructor required for Firebase deserialization
    constructor() : this("", "", "", "", "")
}

// Represents a single option within a poll
@IgnoreExtraProperties
data class PollOption(
    var id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var votes: Int = 0
) {
    // No-argument constructor required for Firebase deserialization
    constructor() : this("", "", 0)
}

// Represents a poll
@IgnoreExtraProperties
data class Poll(
    @get:Exclude var id: String = "", // Firebase will provide the key, so exclude from object
    var title: String = "",
    var topic: String = "",
    var location: String = "",
    var options: List<PollOption> = emptyList(),
    var creatorId: String = "",
    var creatorName: String = "", // New: Store creator's name
    var creationDate: Long = System.currentTimeMillis() // New: Store creation timestamp
) {
    // No-argument constructor required for Firebase deserialization
    constructor() : this("", "", "", "", emptyList(), "", "", 0L)

    // Helper to calculate total votes for a poll
    fun getTotalVotes(): Int = options.sumOf { it.votes }

    // Helper to convert Poll object to a Map for Firebase
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "topic" to topic,
            "location" to location,
            "options" to options.map { option ->
                mapOf(
                    "id" to option.id,
                    "text" to option.text,
                    "votes" to option.votes
                )
            },
            "creatorId" to creatorId,
            "creatorName" to creatorName,
            "creationDate" to creationDate
        )
    }
}






class PollViewModel : ViewModel() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val pollsRef: DatabaseReference = database.getReference("polls")
    private val usersRef: DatabaseReference = database.getReference("users")
    private val userVotesRef: DatabaseReference = database.getReference("userVotes")

    // StateFlow to hold the currently logged-in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _livePolls = MutableStateFlow<List<Poll>>(emptyList())
    val livePolls: StateFlow<List<Poll>> = _livePolls.asStateFlow()

    private val _myPolls = MutableStateFlow<List<Poll>>(emptyList())
    val myPolls: StateFlow<List<Poll>> = _myPolls.asStateFlow()

    // Map to track which polls the current user has voted on
    private val _votedPolls = MutableStateFlow<Set<String>>(emptySet())
    val votedPolls: StateFlow<Set<String>> = _votedPolls.asStateFlow()

    init {
        // Listener for all polls
        pollsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pollsList = mutableListOf<Poll>()
                for (pollSnapshot in snapshot.children) {
                    val poll = pollSnapshot.getValue(Poll::class.java)
                    poll?.id = pollSnapshot.key ?: ""
                    if (poll != null) {
                        pollsList.add(poll)
                    }
                }
                _livePolls.value = pollsList
                updateMyPolls() // Update my polls when live polls change
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PollViewModel", "Failed to load polls: ${error.message}")
            }
        })

        // Observe currentUser changes to update user-specific data
        viewModelScope.launch {
            _currentUser.collect { user ->
                if (user != null) {
                    // User logged in, set up listeners for their votes
                    userVotesRef.child(user.uid).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val votedPollIds = mutableSetOf<String>()
                            for (voteSnapshot in snapshot.children) {
                                votedPollIds.add(voteSnapshot.key ?: "")
                            }
                            _votedPolls.value = votedPollIds
                            Log.d("PollViewModel", "Voted polls for ${user.uid}: ${votedPollIds.size}")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("PollViewModel", "Failed to load user votes: ${error.message}")
                        }
                    })
                    updateMyPolls() // Also update my polls for the new user
                } else {
                    // User logged out
                    _myPolls.value = emptyList()
                    _votedPolls.value = emptySet()
                }
            }
        }
    }

    // Call this from LoginScreen on successful login
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    // Call this from HomeScreen on logout
    fun logoutUser() {
        _currentUser.value = null
    }

    private fun updateMyPolls() {
        val currentUid = _currentUser.value?.uid
        _myPolls.value = _livePolls.value.filter { it.creatorId == currentUid }
    }

    /**
     * Adds a new poll to Firebase Realtime Database.
     */
    fun addPoll(newPoll: Poll) {
        val newPollRef = pollsRef.push()
        newPoll.id = newPollRef.key ?: UUID.randomUUID().toString()
        newPollRef.setValue(newPoll.toMap())
            .addOnSuccessListener {
                Log.d("PollViewModel", "Poll added successfully: ${newPoll.id}")
            }
            .addOnFailureListener { e ->
                Log.e("PollViewModel", "Error adding poll: ${e.message}")
            }
    }

    /**
     * Votes on a poll option using a transaction for atomic updates, and records the user's vote.
     */
    fun voteOnPoll(pollId: String, optionId: String) {
        val currentUid = _currentUser.value?.uid
        if (currentUid == null) {
            Log.w("PollViewModel", "User not logged in, cannot vote.")
            return
        }

        // Record that this user has voted on this poll
        userVotesRef.child(currentUid).child(pollId).setValue(true)
            .addOnSuccessListener {
                Log.d("PollViewModel", "User $currentUid voted on poll $pollId recorded.")
            }
            .addOnFailureListener { e ->
                Log.e("PollViewModel", "Failed to record user vote: ${e.message}")
            }

        // Update the vote count for the option
        val pollRef = pollsRef.child(pollId)
        pollRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val p = currentData.getValue(Poll::class.java) ?: return Transaction.success(currentData)

                val updatedOptions = p.options.toMutableList()
                val optionIndex = updatedOptions.indexOfFirst { it.id == optionId }

                if (optionIndex != -1) {
                    val currentVotes = updatedOptions[optionIndex].votes
                    updatedOptions[optionIndex].votes = currentVotes + 1
                    p.options = updatedOptions
                    currentData.value = p
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    Log.e("PollViewModel", "Transaction failed: ${error.message}")
                } else if (committed) {
                    Log.d("PollViewModel", "Vote committed successfully for poll $pollId, option $optionId")
                } else {
                    Log.d("PollViewModel", "Transaction not committed for poll $pollId, option $optionId")
                }
            }
        })
    }

    /**
     * Deletes a poll from Firebase Realtime Database.
     */
    fun deletePoll(pollId: String) {
        pollsRef.child(pollId).removeValue()
            .addOnSuccessListener {
                Log.d("PollViewModel", "Poll deleted successfully: $pollId")
            }
            .addOnFailureListener { e ->
                Log.e("PollViewModel", "Error deleting poll: ${e.message}")
            }
        // Optionally, remove user votes associated with this poll
        // This would involve querying all users' votes, which can be inefficient.
        // For simplicity, we'll only remove the poll itself.
    }

    /**
     * Filters live polls based on topic and location.
     */
    fun filterLivePolls(topic: String, location: String): List<Poll> {
        return _livePolls.value.filter { poll ->
            (topic.isBlank() || poll.topic.equals(topic, ignoreCase = true)) &&
                    (location.isBlank() || poll.location.equals(location, ignoreCase = true))
        }
    }

    fun getCurrentUserId(): String? = _currentUser.value?.uid
    fun getCurrentUserNameValue(): String = _currentUser.value?.fullName ?: "Guest"
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, pollViewModel: PollViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Live Polls", "Manage My Polls")
    val currentUserName by pollViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Polling App") },
                actions = {
                    Text(
                        text = "Logged in as: ${currentUserName?.fullName ?: "Guest"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = {
                        pollViewModel.logoutUser() // Clear user state in ViewModel
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "Logout") // Placeholder for Logout icon
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = title) },
                        label = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(targetState = selectedTabIndex, label = "TabContentTransition") { tabIndex ->
                when (tabIndex) {
                    0 -> LivePollsScreen(pollViewModel = pollViewModel)
                    1 -> ManageMyPollsScreen(navController = navController, pollViewModel = pollViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    PollingAppTheme {
        HomeScreen(navController = rememberNavController(), pollViewModel = viewModel())
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePollsScreen(pollViewModel: PollViewModel) {
    val allPolls by pollViewModel.livePolls.collectAsState()
    val votedPolls by pollViewModel.votedPolls.collectAsState() // Observe voted polls
    var selectedTopic by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }

    val categories = remember {
        listOf(
            "", // Empty string for "All"
            "Technology", "Sports", "Politics", "Entertainment", "Food",
            "Travel", "Health", "Education", "Science", "Art",
            "Music", "Fashion", "Business", "Environment", "Gaming",
            "Books", "Movies", "Television", "Lifestyle", "General"
        )
    }

    val ukStates = remember {
        listOf(
            "", // Empty string for "All"
            "England", "Scotland", "Wales", "Northern Ireland",
            "London", "Manchester", "Birmingham", "Glasgow", "Edinburgh",
            "Cardiff", "Belfast", "Liverpool", "Bristol", "Leeds",
            "Sheffield", "Newcastle", "Nottingham", "Leicester", "Southampton",
            "Plymouth"
        )
    }

    // Filtered polls based on current filter criteria
    val filteredPolls = remember(allPolls, selectedTopic, selectedLocation) {
        pollViewModel.filterLivePolls(selectedTopic, selectedLocation)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Live Polls",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filter Dropdowns (Spinners)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Topic Filter Spinner
            var expandedTopic by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedTopic,
                onExpandedChange = { expandedTopic = !expandedTopic },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedTopic,
                    onValueChange = { selectedTopic = it },
                    readOnly = true, // Make it a true spinner (not typeable)
                    label = { Text("Filter by Topic") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Topic Filter") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTopic) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTopic,
                    onDismissRequest = { expandedTopic = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(if (category.isEmpty()) "All Topics" else category) },
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
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedLocation,
                    onValueChange = { selectedLocation = it },
                    readOnly = true, // Make it a true spinner
                    label = { Text("Filter by Location") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Location Filter") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocation) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedLocation,
                    onDismissRequest = { expandedLocation = false }
                ) {
                    ukStates.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(if (location.isEmpty()) "All Locations" else location) },
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
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 32.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
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
                text = "Posted by: ${poll.creatorName} on ${dateFormatter.format(Date(poll.creationDate))} at ${timeFormatter.format(Date(poll.creationDate))}",
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
                            selectedOptionId = null // Clear selection after voting
                        }
                    },
                    enabled = selectedOptionId != null, // Enable only if an option is selected
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



@Composable
fun ManageMyPollsScreen(navController: NavController, pollViewModel: PollViewModel) {
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
        ManageMyPollsScreen(navController = rememberNavController(), pollViewModel = viewModel())
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPollScreen(navController: NavController, pollViewModel: PollViewModel) {
    var pollTitle by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    val pollOptions = remember { mutableStateListOf("") } // List of strings for options
    val context = LocalContext.current
    val currentUser by pollViewModel.currentUser.collectAsState()

    val categories = remember {
        listOf(
            "", // Empty string for "Select Topic"
            "Technology", "Sports", "Politics", "Entertainment", "Food",
            "Travel", "Health", "Education", "Science", "Art",
            "Music", "Fashion", "Business", "Environment", "Gaming",
            "Books", "Movies", "Television", "Lifestyle", "General"
        )
    }

    val ukStates = remember {
        listOf(
            "", // Empty string for "Select Location"
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
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Poll Title") },
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
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Topic") },
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
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Location") },
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
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Option") },
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
                    if (currentUser == null) {
                        Toast.makeText(context, "You must be logged in to create a poll.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (pollTitle.isNotBlank() && selectedTopic.isNotBlank() && selectedLocation.isNotBlank() &&
                        pollOptions.all { it.isNotBlank() } && pollOptions.size >= 2
                    ) {
                        val newPoll = Poll(
                            title = pollTitle,
                            topic = selectedTopic,
                            location = selectedLocation,
                            options = pollOptions.map { PollOption(id = UUID.randomUUID().toString(), text = it, votes = 0) },
                            creatorId = currentUser!!.uid,
                            creatorName = currentUser!!.fullName,
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPollsScreen(navController: NavController, pollViewModel: PollViewModel) {
    val myPolls by pollViewModel.myPolls.collectAsState()
    val currentUserId = pollViewModel.getCurrentUserId()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Polls") },
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
            if (myPolls.isEmpty()) {
                Text(
                    text = "You haven't created any polls yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myPolls.filter { it.creatorId == currentUserId }, key = { it.id }) { poll ->
                        MyPollCard(
                            poll = poll,
                            onDelete = { pollViewModel.deletePoll(poll.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
