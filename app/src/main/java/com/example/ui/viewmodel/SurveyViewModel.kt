package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.SurveyEntity
import com.example.data.model.SurveyQuestion
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.data.repository.SurveyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SurveyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SurveyRepository(application)

    val userState: StateFlow<UserEntity?> = repository.userFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val surveysState: StateFlow<List<SurveyEntity>> = repository.surveysFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactionsState: StateFlow<List<TransactionEntity>> = repository.transactionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active session survey state
    private val _activeSurvey = MutableStateFlow<SurveyEntity?>(null)
    val activeSurvey = _activeSurvey.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // Question Index -> Option Index
    val selectedAnswers = _selectedAnswers.asStateFlow()

    // Screen navigation support (using simple composable state rather than complex library backstabbing)
    private val _currentScreen = MutableStateFlow("dashboard") // dashboard, survey_taking, wallet, profile, info
    val currentScreen = _currentScreen.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfNeeded()
        }
    }

    fun setScreen(screenName: String) {
        _currentScreen.value = screenName
    }

    fun startSurvey(survey: SurveyEntity) {
        _activeSurvey.value = survey
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _currentScreen.value = "survey_taking"
    }

    fun selectAnswer(questionIdx: Int, optionIdx: Int) {
        val updated = _selectedAnswers.value.toMutableMap()
        updated[questionIdx] = optionIdx
        _selectedAnswers.value = updated
    }

    fun nextQuestion() {
        val currentSurvey = _activeSurvey.value ?: return
        if (_currentQuestionIndex.value < currentSurvey.questions.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun completeActiveSurvey(onDone: (reward: Double) -> Unit) {
        val survey = _activeSurvey.value ?: return
        viewModelScope.launch {
            repository.completeSurvey(survey.id, survey.reward)
            _activeSurvey.value = null
            _currentQuestionIndex.value = 0
            _selectedAnswers.value = emptyMap()
            onDone(survey.reward)
            _currentScreen.value = "dashboard"
        }
    }

    fun cancelActiveSurvey() {
        _activeSurvey.value = null
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _currentScreen.value = "dashboard"
    }

    fun handleWithdrawal(
        amount: Double,
        method: String,
        upiId: String,
        paytmPhone: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val user = userState.value
            if (user == null) {
                onResult(false, "User session not active.")
                return@launch
            }
            if (amount < 100.0) {
                onResult(false, "Minimum withdrawal amount is ₹100.")
                return@launch
            }
            if (user.balance < amount) {
                onResult(false, "Insufficient balance! Your current balance is ₹${String.format("%.2f", user.balance)}")
                return@launch
            }
            if (method == "UPI" && !upiId.contains("@")) {
                onResult(false, "Please enter a valid UPI ID (e.g. name@upi)")
                return@launch
            }
            if (method == "Paytm" && paytmPhone.length < 10) {
                onResult(false, "Please enter a valid 10-digit mobile number")
                return@launch
            }

            val success = repository.requestWithdrawal(amount, method, upiId, paytmPhone)
            if (success) {
                onResult(true, "Withdrawal request of ₹${String.format("%.2f", amount)} submitted successfully and is processing!")
            } else {
                onResult(false, "Withdrawal failed. Please check details and try again.")
            }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            if (name.trim().isNotEmpty()) {
                repository.updateUserName(name.trim())
            }
        }
    }

    fun resetAllAppletData() {
        viewModelScope.launch {
            repository.resetUserAndSurveys()
            _activeSurvey.value = null
            _currentQuestionIndex.value = 0
            _selectedAnswers.value = emptyMap()
            _currentScreen.value = "dashboard"
        }
    }

    fun generateNewSurveyMatch() {
        viewModelScope.launch {
            val subChoice = listOf(
                Pair("OTT & Streaming Entertainment", listOf("Movie streaming services", "Direct feedback on shows")),
                Pair("Automobile & E-Bikes Trends", listOf("Electric two wheelers", "Commuting habits opinions")),
                Pair("Online Learning & EdTech", listOf("Digital tutorials", "Remote learning systems")),
                Pair("Travel & Holiday Budgets", listOf("Vacation planning", "Tourist destinations preferences")),
                Pair("Fitness Devices & Trackers", listOf("Smart fitness bands", "Daily steps metrics tracking"))
            ).random()

            val title = subChoice.first
            val category = "General"
            val reward = (15..45).random().toDouble()
            val minutes = (1..3).random()

            val qs = listOf(
                SurveyQuestion(
                    text = "How frequently do you engage with ${subChoice.second[0]}?",
                    options = listOf("Daily", "Every weekend", "Maybe once a month", "Almost never")
                ),
                SurveyQuestion(
                    text = "What is the primary factor you review prior to purchasing ${subChoice.second[1]}?",
                    options = listOf("Overall cost and promos", "High raw durability", "Peer group recommendations", "Modern stylish appearance")
                )
            )

            repository.addNewCustomSurvey(title, "Quick personalized poll regarding $title.", reward, minutes, category, qs)
        }
    }
}
