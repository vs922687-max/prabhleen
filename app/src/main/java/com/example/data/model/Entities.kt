package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class SurveyQuestion(
    val text: String,
    val options: List<String>
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "User",
    val balance: Double = 50.0, // Starting balance ₹50.00 to make it welcoming!
    val surveysCompletedCount: Int = 0,
    val upiId: String = "",
    val paytmPhone: String = ""
)

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val reward: Double,
    val timeInMinutes: Int,
    val category: String,
    val isCompleted: Boolean = false,
    val questions: List<SurveyQuestion>
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String, // "EARNING", "WITHDRAWAL"
    val status: String, // "SUCCESS", "PENDING", "PROCESSING"
    val timestamp: Long = System.currentTimeMillis()
)
