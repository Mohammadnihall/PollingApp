package pollingapp.by.mohammadnihalls3451385

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay
import pollingapp.by.mohammadnihalls3451385.mypolls.AddPollScreen
import pollingapp.by.mohammadnihalls3451385.mypolls.MyPollsScreen
import pollingapp.by.mohammadnihalls3451385.mypolls.PollViewModel
import pollingapp.by.mohammadnihalls3451385.mypolls.AppData

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val fragAc = this

        setContent {
            PollingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val pollViewModel: PollViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController = navController,fragAc)
                        }
                        composable("login") {
                            LoginScreen(navController = navController,pollViewModel)
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
fun SplashScreen(navController: NavController,fragmentActivity: FragmentActivity) {
    var visible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        visible = true
        delay(2000)


        if (AppData.isLoggedIn(context)) {
            val biometricManager = BiometricManager.from(fragmentActivity)
            // Use BIOMETRIC_STRONG for strong biometrics, or DEVICE_CREDENTIAL for PIN/Pattern/Password fallback
            val authenticatorType = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

            when (biometricManager.canAuthenticate(authenticatorType)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    val executor = ContextCompat.getMainExecutor(fragmentActivity)
                    val biometricPrompt =
                        BiometricPrompt(
                            fragmentActivity,
                            executor,
                            object : BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                    super.onAuthenticationSucceeded(result)
                                    Toast.makeText(context, "Authentication Succeeded!", Toast.LENGTH_SHORT).show()
                                    // ONLY navigate to home AFTER successful authentication
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }

                                override fun onAuthenticationError( // Corrected function name
                                    errorCode: Int,
                                    errString: CharSequence
                                ) {
                                    super.onAuthenticationError(errorCode, errString)
                                    Toast.makeText(context, "Authentication Error: $errString", Toast.LENGTH_LONG).show()
                                    // If authentication fails or is cancelled, go back to login
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }

                                override fun onAuthenticationFailed() {
                                    super.onAuthenticationFailed()
                                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_LONG).show()
                                    // If authentication fails, go back to login
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            })

                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Fingerprint Verification")
                        .setSubtitle("Place your finger to continue")
                        // REMOVED: .setNegativeButtonText("Use Account Password")
                        .setAllowedAuthenticators(authenticatorType) // Specify allowed authenticators
                        .build()

                    biometricPrompt.authenticate(promptInfo)
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Toast.makeText(context, "No biometric hardware available on this device.", Toast.LENGTH_LONG).show()
                    // No biometric hardware, navigate to login
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Toast.makeText(context, "Biometric hardware is currently unavailable.", Toast.LENGTH_LONG).show()
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Toast.makeText(context, "No fingerprints or screen lock enrolled. Please set up a screen lock or fingerprint in settings.", Toast.LENGTH_LONG).show()
                    // Optionally, guide user to settings or proceed to login
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                else -> {
                    // Other errors or unknown status
                    Toast.makeText(context, "Biometric authentication not available.", Toast.LENGTH_LONG).show()
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        } else {
            // User is not logged in, navigate to login screen
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // This is your splash screen UI design
    PollingAppSplashScreenDesign()
}

@Composable
fun PollingAppSplashScreenDesign(
    appName: String = "PollWise", // Default app name
    personName: String = "By Mohammad" // Default developer name
) {
    // Load custom colors from resources
    val primaryBlue = colorResource(id = R.color.polling_primary_blue)
    val backgroundLight = colorResource(id = R.color.polling_background_light)
    val textDark = colorResource(id = R.color.polling_text_dark)
    val textMedium = colorResource(id = R.color.polling_text_medium)

    // Animatable for a fade-in effect
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate the alpha from 0f to 1f over 1 second
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLight), // Use the clean light background color
        contentAlignment = Alignment.Center // Center all content in the box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha.value) // Apply the fade-in animation to the column
        ) {
            Image(
                painter = painterResource(id = R.drawable.polling_app),
                contentDescription = "Medicine Reminder App",
                modifier = Modifier.size(140.dp)
            )
            // App Logo Icon

            Spacer(modifier = Modifier.height(32.dp)) // Space between logo and app name

            // App Name Text
            Text(
                text = appName,
                fontSize = 52.sp, // Very large font size for strong presence
                fontWeight = FontWeight.ExtraBold, // Make the app name bold
                color = textDark // Use dark text for excellent contrast
            )
            Spacer(modifier = Modifier.height(12.dp)) // Space between app name and person name

            // By Person Name Text
            Text(
                text = personName,
                fontSize = 20.sp, // Readable but secondary font size
                fontWeight = FontWeight.Medium,
                color = textMedium // Slightly subdued color for secondary text
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewPollingAppSplashScreen() {
    // Preview the splash screen with example names
    PollingAppSplashScreenDesign(
        appName = "VoteFlow",
        personName = "Built by The Poll Masters"
    )
}