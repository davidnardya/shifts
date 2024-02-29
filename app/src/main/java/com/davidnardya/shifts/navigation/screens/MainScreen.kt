package com.davidnardya.shifts.navigation.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.davidnardya.shifts.R
import com.davidnardya.shifts.viewmodels.MainViewModel

@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = {
            viewModel.getGuardList()
            navController.navigate(Screen.Guards.route)
        }) {
            Text(text = "רשימת שומרים")
        }

        Button(onClick = {
            viewModel.getGuardList()
            viewModel.createShifts()
            viewModel.getGuardsOnVacation()
            navController.navigate(Screen.Shifts.route)
        }) {
            Text(text = "הפק רשימת שמירות שגרה")
        }

        Button(onClick = {
            viewModel.getGuardList()
            viewModel.createShifts(true)
            navController.navigate(Screen.Shifts.route)
        }) {
            Text(text = "הפק רשימת שמירות חירום")
        }
        EmailDialogButton()
    }
}
@Composable
fun EmailDialogButton() {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Box(modifier = Modifier.clickable { showDialog = true }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "About the app")
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "אודות")
            }

        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("פותח על ידי דוד נרדיה") },
                text = {
                    Column {
                        Text(
                            text = "ניתן ליצור קשר במייל",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        ClickableEmailOrGitHubText(link = "davidnardya@gmail.com")
                        Text(
                            text = "GitHub",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        ClickableEmailOrGitHubText(link = "https://github.com/davidnardya/", true)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("סגור")
                    }
                }
            )
        }
    }
}

@Composable
fun ClickableEmailOrGitHubText(link: String, isGithub: Boolean = false) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = colorResource(id = R.color.highlight_blue), fontSize = 16.sp)) {
            append(link)
            addStringAnnotation(
                tag = "URL",
                annotation = link,
                start = 0,
                end = link.length
            )
        }
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = if (isGithub) {
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(annotation.item)
                        }
                    } else {
                        Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${annotation.item}")
                        }

                    }
                    launcher.launch(intent)
                }
        },
        style = TextStyle(
            textDirection = TextDirection.Ltr,
        ),
    )
}