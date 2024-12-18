package io.github.suwasto.zkpsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suwasto.zkpschnorrproofs.SchnorrMobileClient
import io.github.suwasto.zkpschnorrproofs.SchnorrServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginLog by remember { mutableStateOf("") }
    val userMap = remember { mutableStateMapOf<String, String>() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
            ) {
                // First Column for TextFields
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(16.dp)
                ) {
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Second Column for Buttons
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Button(onClick = {
                        scope.launch {
                            loginLog = "fetching challenge..."
                            val verifier = userMap[username]
                            if (verifier.isNullOrBlank()) {
                                isLoading = false
                                loginLog = "User not found"
                                scope.launch {
                                    snackbarHostState.showSnackbar("User not found")
                                }
                                loginLog = ""
                                return@launch
                            }
                            // fetch challenge from server
                            val challenge = SchnorrServer.generateChallenge(
                                verifier
                            )
                            delay(1000)
                            loginLog = "$loginLog\n$challenge\n\ngenerating proof..."
                            // client generate proof
                            val generateKey = SchnorrMobileClient.deriveKeys(
                                username, password
                            )
                            val proof = SchnorrMobileClient.generateProof(
                                generateKey.privateKey, challenge
                            )
                            delay(1000)
                            val verifyProof = SchnorrServer.verifyProof(
                                proof.first, verifier, challenge, proof.second
                            )
                            loginLog = "$loginLog\n" +
                                    " commitment : ${proof.first} \n" +
                                    " response : ${proof.second}\nVerifying proof..."
                            delay(1000)
                            if (verifyProof) {
                                loginLog = "$loginLog\nAuthenticated $username"
                                scope.launch {
                                    snackbarHostState.showSnackbar("Authenticated $username")
                                }
                            } else {
                                loginLog = "$loginLog\npassword is incorrect"
                                scope.launch {
                                    snackbarHostState.showSnackbar("password is incorrect")
                                }
                            }

                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Login")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.Default) {
                                isLoading = true
                                val generateKey = SchnorrMobileClient.deriveKeys(
                                    username, password
                                )
                                userMap[username] = generateKey.publicKey
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Register",
                        )
                    }
                }
            }

            if (isLoading) {
                Text(
                    text = "Registering...", modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (loginLog.isNotBlank()) {
                Text(
                    text = "Login Log\n$loginLog",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "USERS : ", modifier = Modifier.padding(8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            UsersList(userMap)

        }
    }
}

@Composable
fun UsersList(
    users: Map<String, String>,
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        users.forEach { (key, value) ->
            item {
                Column {
                    Text("Name : $key")
                    Text("Verifier: $value")
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    LoginScreen()
}