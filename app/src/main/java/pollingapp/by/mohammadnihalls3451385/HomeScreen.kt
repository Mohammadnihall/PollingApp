package pollingapp.by.mohammadnihalls3451385

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import pollingapp.by.mohammadnihalls3451385.livepolls.LivePollsScreen
import pollingapp.by.mohammadnihalls3451385.mypolls.AboutUsScreen
import pollingapp.by.mohammadnihalls3451385.mypolls.MyPollsScreen
import pollingapp.by.mohammadnihalls3451385.mypolls.PollViewModel
import pollingapp.by.mohammadnihalls3451385.mypolls.ProfileScreen
import java.util.UUID


// Define your color palette here
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)


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
    var password: String = "",
    var area: String = ""
) {
}

// Represents a single option within a poll
@IgnoreExtraProperties
data class PollOption(
    var id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var votes: Int = 0
) {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, pollViewModel: PollViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    // Updated tabs list to include About Us and Profile
    val tabs = listOf("Live Polls", "My Polls", "About Us", "Profile")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Polling App") },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            // Using placeholder icons as requested
                            when (title) {
                                "Live Polls" -> Icon(Icons.Default.Home, contentDescription = title)
                                "My Polls" -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = title)
                                "About Us" -> Icon(Icons.Default.Info, contentDescription = title)
                                "Profile" -> Icon(Icons.Default.Person, contentDescription = title)
                                else -> Icon(Icons.Default.Info, contentDescription = title) // Fallback
                            }
                        },
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
                    1 -> MyPollsScreen(navController = navController, pollViewModel = pollViewModel)
                    2 -> AboutUsScreen()
                    3 -> ProfileScreen(navController = navController, pollViewModel = pollViewModel) // Navigate to ProfileScreen
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
