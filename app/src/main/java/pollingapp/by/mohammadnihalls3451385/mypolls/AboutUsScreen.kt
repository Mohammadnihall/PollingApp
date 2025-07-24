package pollingapp.by.mohammadnihalls3451385.mypolls

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutUsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Section 1: About Us
        Text(
            text = "About Us",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "The Mohammad Nihal Vote App is a mobile application that enables users to create and share polls on any topic of their choice. While setting up a poll, users can choose the poll type—such as single choice, multiple choice, or open-ended response—and define the available answer options.\n" +
                    "\n" +
                    "Once the poll is live, all registered users can participate by submitting their votes. The app also displays the number of participants and the responses received in real-time. It provides a platform for anyone, including students, to express their opinions and gather feedback through interactive polls.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Section 2: Contact Us
        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Name: Mohammadnihall",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Email: Mohammadnihalteesmsc@gmail.com",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}