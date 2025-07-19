package project.app.pollingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.app.pollingapp.ui.theme.PollingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PollingAppTheme {
                PollingAppSplashScreenDesign()
            }
        }
    }
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