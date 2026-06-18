package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.viewmodel.SurveyViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: SurveyViewModel,
    user: UserEntity?,
    modifier: Modifier = Modifier
) {
    var editNameInput by remember { mutableStateOf(user?.name ?: "") }
    var isEditingName by remember { mutableStateOf(false) }
    var showResetWarning by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        if (editNameInput.isEmpty() && user != null) {
            editNameInput = user.name
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Profile Info Card (Aesthetic avatar & details)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = BorderStroke(1.dp, SleekOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Space with Brand gradient
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(SleekPrimary, SleekSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(46.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(14.dp))

                if (isEditingName) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = editNameInput,
                            onValueChange = { editNameInput = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("name_edit_field")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                viewModel.updateUserName(editNameInput)
                                isEditingName = false
                            },
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = SuccessGreen)
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = user?.name ?: "Premium Earner",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = SleekOnBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { isEditingName = true },
                            modifier = Modifier.size(24.dp).testTag("edit_name_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                tint = SleekPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = "ID: SE-EARN-${user?.id ?: 1}055",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = SleekOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SleekOutline)
                Spacer(modifier = Modifier.height(14.dp))

                // Quick Statistics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user?.surveysCompletedCount ?: 0}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = SleekPrimary
                        )
                        Text(
                            text = "Polls Done",
                            style = MaterialTheme.typography.labelSmall,
                            color = SleekOnSurfaceVariant
                        )
                    }
                    
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(SleekOutline))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "₹${String.format("%.0f", (user?.balance ?: 0.0) + (user?.surveysCompletedCount ?: 0) * 20.0)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = SleekSecondary
                        )
                        Text(
                            text = "Total Earned",
                            style = MaterialTheme.typography.labelSmall,
                            color = SleekOnSurfaceVariant
                        )
                    }
                    
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(SleekOutline))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val level = when ((user?.surveysCompletedCount ?: 0)) {
                            in 0..1 -> "Bronze"
                            in 2..4 -> "Silver"
                            else -> "Gold Pro"
                        }
                        Text(
                            text = level,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = SuccessGreen
                        )
                        Text(
                            text = "Member Rank",
                            style = MaterialTheme.typography.labelSmall,
                            color = SleekOnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Daily Earnings Graph Card (Custom Canvas representation)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = BorderStroke(1.dp, SleekOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Weekly Earning Curve",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = SleekOnBackground
                )
                Text(
                    text = "Historical trajectory of completed survey credits (Last 7 Days)",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = SleekOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful custom visual Graph drawing in Slate emerald
                val graphColor = SleekPrimary
                val gridColor = SleekOutline
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            SleekSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        
                        // Last 7 days points (Simulated Rupees: 50, 10, 45, 0, 75, 40, 60 based on stats)
                        val points = listOf(20f, 50f, 30f, 75f, 40f, 65f, 90f)
                        val maxVal = 100f
                        
                        // Draw grid horizontal line guides
                        val gridLines = 3
                        for (i in 0..gridLines) {
                            val y = height * (i.toFloat() / gridLines)
                            drawLine(
                                color = gridColor.copy(alpha = 0.4f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Plot coordinate paths
                        val path = Path()
                        val fillPath = Path()
                        
                        val stepX = width / (points.size - 1)
                        points.forEachIndexed { idx, value ->
                            val x = idx * stepX
                            val fraction = value / maxVal
                            val y = height - (height * fraction)
                            
                            if (idx == 0) {
                                path.moveTo(x, y)
                                fillPath.moveTo(x, height)
                                fillPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }
                            
                            if (idx == points.size - 1) {
                                fillPath.lineTo(x, height)
                                fillPath.close()
                            }
                            
                            // Draw point circles
                            drawCircle(
                                color = SleekSecondary, // golden coin markers
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        // Draw rich emerald gradient fill under line
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(graphColor.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )

                        // Draw primary stroke curve
                        drawPath(
                            path = path,
                            color = graphColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Days subtitle row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed")
                    days.forEach { dayName ->
                        Text(
                            text = dayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekOnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action settings area
        Spacer(modifier = Modifier.height(4.dp))
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = BorderStroke(1.dp, SleekOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Info button Row
                ListItem(
                    modifier = Modifier.clickable { viewModel.setScreen("info") },
                    headlineContent = { Text("App Guidelines & Rules", fontWeight = FontWeight.SemiBold, color = SleekOnBackground) },
                    supportingContent = { Text("Find out how cash-out rates and polls function.", color = SleekOnSurfaceVariant) },
                    leadingContent = { Icon(Icons.Default.HelpCenter, contentDescription = null, tint = SleekPrimary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SleekOnSurfaceVariant) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                
                ListItem(
                    modifier = Modifier
                        .clickable { showResetWarning = true }
                        .testTag("reset_data_item"),
                    headlineContent = { Text("Reset Sandbox Data", fontWeight = FontWeight.SemiBold, color = ErrorRed) },
                    supportingContent = { Text("Clears wallet ledger history and restarts setup.", color = SleekOnSurfaceVariant) },
                    leadingContent = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = ErrorRed) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SleekOnSurfaceVariant) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }

    // Reset Sandbox Warning Alerts
    if (showResetWarning) {
        AlertDialog(
            onDismissRequest = { showResetWarning = false },
            title = { Text("Danger Zone: Clear sandbox data?", color = ErrorRed, fontWeight = FontWeight.Bold) },
            text = { Text("This will clear out the database completely, removing cash ledger history and returning your balance to first-turn sign-up state.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetWarning = false
                        viewModel.resetAllAppletData()
                    }
                ) {
                    Text("Wipe Database", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetWarning = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
