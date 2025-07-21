package project.app.pollingapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class HomeScreen {

    Column(
    modifier = Modifier
    .fillMaxWidth(0.9f)
    .clip(RoundedCornerShape(20.dp))
    .background(MaterialTheme.colorScheme.surface)
    .padding(24.dp), // Add elevation for a card-like effect
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}