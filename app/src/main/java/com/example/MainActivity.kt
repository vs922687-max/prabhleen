package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SurveyViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: SurveyViewModel = viewModel()
                
                // Observe states
                val userState by viewModel.userState.collectAsState()
                val surveysState by viewModel.surveysState.collectAsState()
                val transactionsState by viewModel.transactionsState.collectAsState()
                val currentScreen by viewModel.currentScreen.collectAsState()
                
                // Active Survey state
                val activeSurvey by viewModel.activeSurvey.collectAsState()
                val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
                val selectedAnswers by viewModel.selectedAnswers.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Avoid showing navigation bar during active survey taking to block accidental exit.
                        if (currentScreen != "survey_taking") {
                            NavigationBar(
                                modifier = Modifier.testTag("app_bottom_nav"),
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == "dashboard",
                                    onClick = { viewModel.setScreen("dashboard") },
                                    icon = { Icon(Icons.Default.Poll, contentDescription = "Polls") },
                                    label = { Text("Surveys", style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("nav_surveys")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "wallet",
                                    onClick = { viewModel.setScreen("wallet") },
                                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                                    label = { Text("Wallet", style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("nav_wallet")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "profile",
                                    onClick = { viewModel.setScreen("profile") },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("nav_profile")
                                )
                            }
                        }
                    },
                    contentWindowInsets = WindowInsets.statusBars
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            "dashboard" -> {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    user = userState,
                                    surveys = surveysState,
                                    onStartSurvey = { srv -> viewModel.startSurvey(srv) }
                                )
                            }
                            "survey_taking" -> {
                                activeSurvey?.let { srv ->
                                    SurveyTakingScreen(
                                        viewModel = viewModel,
                                        survey = srv,
                                        currentQuestionIdx = currentQuestionIndex,
                                        selectedAnswers = selectedAnswers,
                                        onCancel = { viewModel.setScreen("dashboard") }
                                    )
                                } ?: viewModel.setScreen("dashboard")
                            }
                            "wallet" -> {
                                WalletScreen(
                                    viewModel = viewModel,
                                    user = userState,
                                    transactions = transactionsState
                                )
                            }
                            "profile" -> {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    user = userState
                                )
                            }
                            "info" -> {
                                InfoScreen(
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
