package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SurveyViewModel
import com.example.ui.theme.*

@Composable
fun InfoScreen(
    viewModel: SurveyViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Welcome Tip
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SleekPrimaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SleekPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Complete daily polls on various brands and trending topics to build up real Indian Rupee (₹) rewards.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = SleekOnPrimaryContainer
                    )
                }
            }
        }

        // Section: How to Earn Cash
        item {
            Text(
                text = "How to Earn Cash?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SleekOnBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EarnStepItem(
                    index = "1",
                    title = "Locate Live Polls",
                    desc = "Navigate to the Home screen. Pick any available survey from categorized chips: Tech, Finance, Lifestyle."
                )
                EarnStepItem(
                    index = "2",
                    title = "Answer Sincerely",
                    desc = "Complete options in the questionnaire. Your feedback helps multinational companies shape their designs."
                )
                EarnStepItem(
                    index = "3",
                    title = "Get Paid Instantly",
                    desc = "Once finished, your specified cash coins are added immediately into your ledger wallet."
                )
                EarnStepItem(
                    index = "4",
                    title = "Cash Out securely",
                    desc = "Upon reaching ₹100 threshold, input your GPay/PhonePe UPI ID or Paytm wallet phone number to securely cash out."
                )
            }
        }

        // Section: FAQ Accordion
        item {
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SleekOnBackground
            )
        }

        item {
            FaqAccordionItem(
                question = "What is the minimum withdrawal rate?",
                answer = "The minimum threshold to invoke withdrawal is ₹100.00. This maintains high processing speed and matches partner transactions rules."
            )
        }

        item {
            FaqAccordionItem(
                question = "How long does it take for money processing?",
                answer = "Sandbox simulations process wallet requests immediately. In real service integrations, payments are reviewed and complete within 3 to 12 business hours."
            )
        }

        item {
            FaqAccordionItem(
                question = "Why does my survey list look empty?",
                answer = "Available polls refresh throughout the day. Click on 'Find Polls' matching tool on the home card at any time to instantly trigger new personalized surveys!"
            )
        }

        item {
            FaqAccordionItem(
                question = "Can I customize my username profile?",
                answer = "Absolutely. Go into the Profile tab, hit the edit icon next to your name, enter your preferred title, and hit checkmark to save."
            )
        }

        // Return button
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.setScreen("dashboard") },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("info_back_home_button")
            ) {
                Text("Start Earning Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun EarnStepItem(index: String, title: String, desc: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SleekOutline),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(SleekPrimaryContainer, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = SleekPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = SleekOnBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = SleekOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FaqAccordionItem(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SleekOutline),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("faq_item_${question.take(15).replace(" ", "_").lowercase()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = SleekOnBackground,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = SleekPrimary
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodySmall,
                        color = SleekOnSurfaceVariant
                    )
                }
            }
        }
    }
}
