package dev.shreyaspatil.composeCompilerMetricsGenerator.samples.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme {
                ContactRow(
                    Contact("John Doe", 23, listOf("tech", "music", "sport")),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

data class Contact(val name: String, val number: Int, val tags: List<String>)

@Composable
fun ContactRow(contact: Contact, modifier: Modifier = Modifier) {
    var selected by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactDetails(contact, Modifier.weight(1f))
        ToggleButton(selected, onToggled = { selected = !selected })
    }
}

@Composable
fun ContactDetails(contact: Contact, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = contact.name,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row {
            Text(
                text = contact.number.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = contact.tags.joinToString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ToggleButton(selected: Boolean, onToggled: () -> Unit) {
    Checkbox(
        checked = selected,
        onCheckedChange = { onToggled() }
    )
}
