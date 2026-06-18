package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurveyEntity
import com.example.ui.viewmodel.SurveyViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SurveyTakingScreen(
    viewModel: SurveyViewModel,
    survey: SurveyEntity,
    currentQuestionIdx: Int,
    selectedAnswers: Map<Int, Int>,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showCelebration by remember { mutableStateFlowOf(false) }
    var earnedReward by remember { mutableStateFlowOf(0.0) }
    var showExitWarning by remember { mutableStateFlowOf(false) }

    val totalQuestions = survey.questions.size
    val activeQuestion = survey.questions[currentQuestionIdx]
    val selectedOptionIdx = selectedAnswers[currentQuestionIdx]

    val progress = (currentQuestionIdx + 1).toFloat() / totalQuestions

    if (showCelebration) {
        CelebrationLayout(
            reward = earnedReward,
            surveyTitle = survey.title,
            onBackToHome = {
                showCelebration = false
                onCancel() // Resets active states
            }
        )
    } else {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(SleekBackground)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showExitWarning = true },
                            modifier = Modifier.testTag("exit_survey_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Exit Survey", tint = SleekOnBackground)
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Playing Poll",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = SleekPrimary
                            )
                            Text(
                                text = survey.category,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = SleekOnBackground
                            )
                        }
                        
                        // Fake secure connection lock indicator
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted Connection",
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // Linear progress indicator
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = SleekPrimary,
                        trackColor = SleekOutline,
                    )
                }
            },
            bottomBar = {
                Surface(
                    tonalElevation = 0.dp,
                    color = SleekSurface,
                    border = BorderStroke(1.dp, SleekOutline),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prev Question Button
                        TextButton(
                            onClick = { viewModel.prevQuestion() },
                            enabled = currentQuestionIdx > 0,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = SleekPrimary
                            ),
                            modifier = Modifier.testTag("prev_question_button")
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", fontWeight = FontWeight.Bold)
                        }

                        // Progress count label
                        Text(
                            text = "${currentQuestionIdx + 1} of $totalQuestions",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = SleekOnSurfaceVariant
                        )

                        // Next Question / Complete Button
                        val hasSelected = selectedOptionIdx != null
                        if (currentQuestionIdx == totalQuestions - 1) {
                            Button(
                                onClick = {
                                    if (hasSelected) {
                                        viewModel.completeActiveSurvey { reward ->
                                            earnedReward = reward
                                            showCelebration = true
                                        }
                                    }
                                },
                                enabled = hasSelected,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SuccessGreen
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.testTag("claim_reward_button")
                            ) {
                                Text("Claim ₹${String.format("%.2f", survey.reward)}", fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                enabled = hasSelected,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SleekPrimary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.testTag("next_question_button")
                            ) {
                                Text("Next", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(SleekBackground)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Question text Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SleekPrimaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = null,
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "QUESTION ${currentQuestionIdx + 1}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = SleekPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = activeQuestion.text,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = SleekOnPrimaryContainer
                            )
                        }
                    }

                    Text(
                        text = "Choose your honest option:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SleekOnSurfaceVariant
                    )

                    // Options list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        activeQuestion.options.forEachIndexed { optIdx, optionText ->
                            val isSelected = selectedOptionIdx == optIdx
                            
                            OutlinedCard(
                                onClick = { viewModel.selectAnswer(currentQuestionIdx, optIdx) },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) SleekPrimary else SleekOutline
                                ),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = if (isSelected) SleekPrimaryContainer.copy(alpha = 0.5f) else SleekSurface
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("option_${currentQuestionIdx}_$optIdx")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                  ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) SleekPrimary else SleekSurfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = ('A'.code + optIdx).toChar().toString(),
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelected) Color.White else SleekOnSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = SleekOnBackground
                                        )
                                    }
                                    
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.selectAnswer(currentQuestionIdx, optIdx) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = SleekPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Exit Survey Warning Dialog
    if (showExitWarning) {
        AlertDialog(
            onDismissRequest = { showExitWarning = false },
            title = { Text("Exit Survey?", fontWeight = FontWeight.Bold) },
            text = { Text("Exiting will lose your current survey replies and you will miss your ₹${String.format("%.2f", survey.reward)} cash reward!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitWarning = false
                        viewModel.cancelActiveSurvey()
                        onCancel()
                    }
                ) {
                    Text("Exit", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitWarning = false }) {
                    Text("Resume")
                }
            }
        )
    }
}

@Composable
fun CelebrationLayout(
    reward: Double,
    surveyTitle: String,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekOnPrimaryContainer) // Highlight in solid dark navy overlay
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFFFFA000)), // Gold coin
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = SleekOnPrimaryContainer,
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Survey Completed! 🎉",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "We have added the reward for completing '$surveyTitle' directly into your survey cash wallet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB0BEC5),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Value Added Callout
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SleekPrimaryContainer),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = SuccessGreen,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            text = "Reward Credited",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = SleekOnPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "+ ₹${String.format("%.2f", reward)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = SleekPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onBackToHome,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("celebration_home_button")
            ) {
                Text("Proceed to Dashboard", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
            }
        }
    }
}

// Helper state function (local alias)
private fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)

