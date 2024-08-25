package br.com.fiap.nautilus

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SubmarineControl(
    onUp: () -> Unit,
    onDown: () -> Unit,
    onRight: () -> Unit,
    onLeft: () -> Unit,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    receivedData: String
) {
    var isUp by remember { mutableStateOf(false) }
    var isDown by remember { mutableStateOf(false) }
    var isRight by remember { mutableStateOf(false) }
    var isLeft by remember { mutableStateOf(false) }
    var isForward by remember { mutableStateOf(false) }
    var isBackward by remember { mutableStateOf(false) }

    // Launches coroutines to send commands while buttons are pressed
    LaunchedEffect(isUp) {
        while (isUp) {
            onUp()
            delay(100) // Send the command every 100 milliseconds
        }
    }

    LaunchedEffect(isDown) {
        while (isDown) {
            onDown()
            delay(100) // Send the command every 100 milliseconds
        }
    }

    LaunchedEffect(isRight) {
        while (isRight) {
            onRight()
            delay(100) // Send the command every 100 milliseconds
        }
    }

    LaunchedEffect(isLeft) {
        while (isLeft) {
            onLeft()
            delay(100) // Send the command every 100 milliseconds
        }
    }

    LaunchedEffect(isForward) {
        while (isForward) {
            onForward()
            delay(100) // Send the command every 100 milliseconds
        }
    }

    LaunchedEffect(isBackward) {
        while (isBackward) {
            onBackward()
            delay(100) // Send the command every 100 milliseconds
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Button(
                onClick = { /* No action needed for simple click */ },
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isUp = true
                                tryAwaitRelease()
                                isUp = false
                            }
                        )
                    }
            ) {
                Text("Subir")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { /* No action needed for simple click */ },
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isDown = true
                                tryAwaitRelease()
                                isDown = false
                            }
                        )
                    }
            ) {
                Text("Descer")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { /* No action needed for simple click */ },
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isLeft = true
                                tryAwaitRelease()
                                isLeft = false
                            }
                        )
                    }
            ) {
                Text("Esquerda")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { /* No action needed for simple click */ },
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isRight = true
                                tryAwaitRelease()
                                isRight = false
                            }
                        )
                    }
            ) {
                Text("Direita")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { /* No action needed for simple click */ },
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isForward = true
                                tryAwaitRelease()
                                isForward = false
                            }
                        )
                    }
            ) {
                Text("Frente")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { /* No action needed for simple click */ },
                modifier = Modifier
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isBackward = true
                                tryAwaitRelease()
                                isBackward = false
                            }
                        )
                    }
            ) {
                Text("Trás")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display received data
        Text(
            text = receivedData,
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
}

