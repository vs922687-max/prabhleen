package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.UserEntity
import com.example.ui.viewmodel.SurveyViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SurveyViewModel,
    user: UserEntity?,
    surveys: List<SurveyEntity>,
    onStartSurvey: (SurveyEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val categories = listOf("All", "Lifestyle", "Technology", "Finance", "Gaming", "Food")
    
    // Filter surveys based on completion and category
    val filteredSurveys = surveys.filter { survey ->
        !survey.isCompleted && (selectedCategory == "All" || survey.category.equals(selectedCategory, ignoreCase = true))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Welcome and Header (Sleek Interface Style)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WELCOME BACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekOnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user?.name ?: "Guest User",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SleekOnBackground
                    )
                }
                
                // Sleek Pill Balance Widget
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(SleekPrimaryContainer)
                        .clickable { viewModel.setScreen("wallet") }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${String.format("%.2f", user?.balance ?: 0.0)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekOnPrimaryContainer,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    // Custom interactive badge design (circle inside circle ring)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SleekPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }

        // Daily Goal Card (Sleek Lavendar/Purple style)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SleekSecondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Earning Goal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekOnSecondaryContainer
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "70% Done",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekOnSecondaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Styled progress bar matching the design theme
                    LinearProgressIndicator(
                        progress = { 0.70f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = SleekSecondary,
                        trackColor = Color.White.copy(alpha = 0.35f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Complete 2 more surveys to earn ₹100 bonus!",
                        fontSize = 13.sp,
                        color = SleekOnSecondaryContainer.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quick Action Buttons matching design colors and requirements
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setScreen("wallet") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("withdraw_shortcut_button")
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Withdraw", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        
                        Button(
                            onClick = {
                                if (!isScanning) {
                                    coroutineScope.launch {
                                        isScanning = true
                                        delay(1800)
                                        viewModel.generateNewSurveyMatch()
                                        isScanning = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.45f),
                                contentColor = SleekOnSecondaryContainer
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("request_survey_button"),
                            enabled = !isScanning
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    color = SleekOnSecondaryContainer,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Matching...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.TravelExplore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Find Polls", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Category Selection Row
        item {
            Text(
                text = "Surveys by Category",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SleekOnBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SleekPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = SleekOnSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = SleekOutline,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // Survey List Header count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "High Paying Surveys",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SleekOnBackground
                )
                if (filteredSurveys.isNotEmpty()) {
                    Text(
                        text = "Total: ${filteredSurveys.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SleekOnSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Survey List Items
        if (filteredSurveys.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SleekOutline),
                    colors = CardDefaults.cardColors(
                        containerColor = SleekSurface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "No Surveys Available",
                            tint = SleekOnSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No available surveys right now!",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            color = SleekOnBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Click 'Find Polls' above to generate quick personalized surveys instantly!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekOnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isScanning = true
                                    delay(1200)
                                    viewModel.generateNewSurveyMatch()
                                    isScanning = false
                                }
                            },
                        ) {
                            Text("Scan for Live Polls")
                        }
                    }
                }
            }
        } else {
            items(filteredSurveys) { survey ->
                SurveyCard(
                    survey = survey,
                    onStart = { onStartSurvey(survey) }
                )
            }
        }
    }
}

// Model Category Visual Data Mapping Helper
data class CategoryStyle(
    val emoji: String,
    val containerColor: Color,
    val textColor: Color
)

fun getCategoryStyle(category: String): CategoryStyle {
    return when (category.lowercase()) {
        "technology", "tech" -> CategoryStyle("📊", IconBgTech, IconTextTech)
        "finance" -> CategoryStyle("💰", IconBgFinance, IconTextFinance)
        "food" -> CategoryStyle("🍔", IconBgFinance, IconTextFinance)
        "lifestyle" -> CategoryStyle("🚲", IconBgLifestyle, IconTextLifestyle)
        "gaming" -> CategoryStyle("🎮", IconBgLifestyle, IconTextLifestyle)
        else -> CategoryStyle("📋", SleekPrimaryContainer, SleekOnPrimaryContainer)
    }
}

@Composable
fun SurveyCard(
    survey: SurveyEntity,
    onStart: () -> Unit
) {
    val style = getCategoryStyle(survey.category)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("survey_card_${survey.id}"),
        colors = CardDefaults.cardColors(
            containerColor = SleekSurface
        ),
        border = BorderStroke(1.dp, SleekOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStart() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Styled Icon with Category Color
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(style.containerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = style.emoji,
                            fontSize = 22.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = survey.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = SleekOnBackground,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${survey.category} • ${survey.timeInMinutes} mins",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekOnSurfaceVariant
                        )
                    }
                }
                
                // Reward block with explicit design colors
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "+₹${String.format("%.0f", survey.reward)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = SleekPrimary
                    )
                    Text(
                        text = "Instant",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = SleekMutedText,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = survey.description,
                style = MaterialTheme.typography.bodySmall,
                color = SleekOnSurfaceVariant,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action button inside survey details card
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SleekPrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("start_button_${survey.id}")
                ) {
                    Text("Start Earn", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

// Helper state function
private fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)

