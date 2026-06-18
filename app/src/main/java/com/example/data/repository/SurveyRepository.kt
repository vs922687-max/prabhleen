package com.example.data.repository

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.model.SurveyEntity
import com.example.data.model.SurveyQuestion
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SurveyRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val surveyDao = database.surveyDao()
    private val transactionDao = database.transactionDao()

    val userFlow: Flow<UserEntity?> = userDao.getUserFlow()
    val surveysFlow: Flow<List<SurveyEntity>> = surveyDao.getAllSurveysFlow()
    val transactionsFlow: Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()

    suspend fun seedDatabaseIfNeeded() {
        withContext(Dispatchers.IO) {
            // 1. Seed User if missing
            val currentUser = userDao.getUserSync()
            if (currentUser == null) {
                val defaultUser = UserEntity(
                    id = 1,
                    name = "Premium Earner",
                    balance = 50.0, // Starting bonus ₹50.00
                    surveysCompletedCount = 0
                )
                userDao.insertUser(defaultUser)

                // Add a welcome transaction
                val welcomeTx = TransactionEntity(
                    title = "Sign-up Bonus Received",
                    amount = 50.0,
                    type = "EARNING",
                    status = "SUCCESS"
                )
                transactionDao.insertTransaction(welcomeTx)
            }

            // 2. Seed Surveys if empty
            val currentSurveys = surveyDao.getAllSurveysFlow().firstOrNull()
            if (currentSurveys.isNullOrEmpty()) {
                val seedList = listOf(
                    SurveyEntity(
                        id = "srv_001",
                        title = "Consumer Habits & Shopping",
                        description = "Share your daily shopping preferences and spending priorities.",
                        reward = 45.0,
                        timeInMinutes = 3,
                        category = "Lifestyle",
                        questions = listOf(
                            SurveyQuestion(
                                text = "Where do you spend the majority of your monthly lifestyle budget?",
                                options = listOf("Online platforms (Amazon, Myntra, etc.)", "Shopping malls & brand showrooms", "Local markets & street shops", "I rarely spend on lifestyle")
                            ),
                            SurveyQuestion(
                                text = "What factor is most critical for your shopping purchases?",
                                options = listOf("High discounts / Promo codes", "Premium raw quality / Brand warranty", "User reviews & rating scores", "Delivery speed & post-sale support")
                            ),
                            SurveyQuestion(
                                text = "Do you prefer digital payments (UPI, Card) or cash for shopping?",
                                options = listOf("Strictly cashless (UPI, wallets)", "Credit or Debit cards", "Cash on delivery", "Depends on the order value")
                            )
                        )
                    ),
                    SurveyEntity(
                        id = "srv_002",
                        title = "Tech World & AI Opinions",
                        description = "Let us know your views on rapid AI advancement and daily tech tools.",
                        reward = 60.0,
                        timeInMinutes = 4,
                        category = "Technology",
                        questions = listOf(
                            SurveyQuestion(
                                text = "How often do you employ generative AI tools like Chat GPT or Gemini?",
                                options = listOf("Multiple times per day for support", "Once a day for study/work", "Rarely (A few times a month)", "Never used any AI tools")
                            ),
                            SurveyQuestion(
                                text = "Which tech category holds the greatest promise for the next five years?",
                                options = listOf("Artificial Intelligence (AI)", "Smart Devices & Internet of Things", "Electric Vehicles / Green Energy", "Virtual/Augmented Reality")
                            ),
                            SurveyQuestion(
                                text = "Which primary device do you rely on for online tasks?",
                                options = listOf("Android Smartphone", "Apple iPhone", "Laptop / Desktop", "Tablet/iPad")
                            ),
                            SurveyQuestion(
                                text = "Do you pay for premium content or software subscriptions?",
                                options = listOf("Yes, multiple active subscriptions", "Yes, only one subscription", "No, I prefer free services", "Never paid for digital goods")
                            )
                        )
                    ),
                    SurveyEntity(
                        id = "srv_003",
                        title = "Finance & Smart Investments",
                        description = "Tell us about your digital transaction behavior and financial goals.",
                        reward = 75.0,
                        timeInMinutes = 5,
                        category = "Finance",
                        questions = listOf(
                            SurveyQuestion(
                                text = "At what age did you begin actively tracking your budget or savings?",
                                options = listOf("Under 18 years old", "Between 18 and 22", "Between 23 and 27", "28 or older / Have not started")
                            ),
                            SurveyQuestion(
                                text = "What is your primary investment mode today?",
                                options = listOf("Mutual Funds / Monthly SIPs", "Direct share trading & stocks", "Fixed Deposits, Gold & Bonds", "I do not invest my money yet")
                            ),
                            SurveyQuestion(
                                text = "What is your main financial milestone for this calendar year?",
                                options = listOf("Building a solid emergency fund", "Exploring high-yield investing", "Acquiring a vehicle or house", "Paying off historical loans")
                            )
                        )
                    ),
                    SurveyEntity(
                        id = "srv_004",
                        title = "Gaming & Digital Pass-time",
                        description = "Give us feedback on your favorite digital games and platform choices.",
                        reward = 30.0,
                        timeInMinutes = 2,
                        category = "Gaming",
                        questions = listOf(
                            SurveyQuestion(
                                text = "On average, how many hours do you game every week?",
                                options = listOf("Under 2 hours", "Between 2 to 7 hours", "Between 8 to 15 hours", "Greater than 15 hours (Hardcore)")
                            ),
                            SurveyQuestion(
                                text = "What platform does your gaming happen on most?",
                                options = listOf("Mobile phone screen", "Gaming laptop/PC setup", "Playstation/Xbox/Switch console", "I don't play electronic games")
                            ),
                            SurveyQuestion(
                                text = "Have you ever purchased visual cosmetics, battle passes, or keys?",
                                options = listOf("Yes, regularly buy in-game", "Yes, bought once or twice", "No, I am strictly free-to-play", "Not applicable")
                            )
                        )
                    ),
                    SurveyEntity(
                        id = "srv_005",
                        title = "Food Trends & Dining Out",
                        description = "Tell us about your culinary choices, fast food habits, and home cooking.",
                        reward = 40.0,
                        timeInMinutes = 3,
                        category = "Food",
                        questions = listOf(
                            SurveyQuestion(
                                text = "How often do you order food online from Swiggy, Zomato, etc.?",
                                options = listOf("Almost daily", "2 to 4 times a week", "Once a week", "Rarely or never")
                            ),
                            SurveyQuestion(
                                text = "What is your absolute priority when choosing a restaurant?",
                                options = listOf("Excellent food taste & portions", "High discounts & coupons", "Cleanliness & user feedback", "Speedy delivery time")
                            ),
                            SurveyQuestion(
                                text = "How do you balance restaurant food versus homemade dishes?",
                                options = listOf("Fully homemade daily", "Homemade mostly, restaurants on weekends", "Frequent dining out / takeaway", "Equally distributed")
                            )
                        )
                    )
                )
                surveyDao.insertSurveys(seedList)
            }
        }
    }

    suspend fun completeSurvey(surveyId: String, reward: Double) {
        withContext(Dispatchers.IO) {
            val survey = surveyDao.getSurveyById(surveyId)
            if (survey != null && !survey.isCompleted) {
                // 1. Mark Survey completed
                surveyDao.markSurveyAsCompleted(surveyId)
                // 2. Add Reward double to wallet
                userDao.updateBalance(reward)
                // 3. Increment completion counters
                userDao.incrementCompletedCount()
                // 4. Create direct earning Transaction
                val earningsTx = TransactionEntity(
                    title = "Earning: ${survey.title}",
                    amount = reward,
                    type = "EARNING",
                    status = "SUCCESS"
                )
                transactionDao.insertTransaction(earningsTx)
            }
        }
    }

    suspend fun requestWithdrawal(amount: Double, method: String, upiId: String, paytmPhone: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserSync()
            if (user != null && user.balance >= amount) {
                // 1. Deduct money (negative balance update)
                userDao.updateBalance(-amount)
                // 2. Update user's withdrawal records
                userDao.updateWithdrawalDetails(upiId, paytmPhone)
                // 3. Add to Transactions
                val details = if (method == "UPI") "UPI ID: $upiId" else "Paytm: $paytmPhone"
                val tx = TransactionEntity(
                    title = "Withdrawal to $method",
                    amount = -amount,
                    type = "WITHDRAWAL",
                    status = "PROCESSING" // Simulated active transfer which goes "SUCCESS" later or stays
                )
                transactionDao.insertTransaction(tx)
                true
            } else {
                false
            }
        }
    }

    suspend fun updateUserName(newName: String) {
        withContext(Dispatchers.IO) {
            val user = userDao.getUserSync()
            if (user != null) {
                userDao.updateUser(user.copy(name = newName))
            }
        }
    }

    suspend fun resetUserAndSurveys() {
        withContext(Dispatchers.IO) {
            userDao.insertUser(UserEntity(
                id = 1,
                name = "Premium Earner",
                balance = 50.0,
                surveysCompletedCount = 0
            ))
            surveyDao.resetAllSurveys()
            transactionDao.clearTransactions()
            transactionDao.insertTransaction(TransactionEntity(
                title = "Sign-up Bonus Received",
                amount = 50.0,
                type = "EARNING",
                status = "SUCCESS"
            ))
        }
    }

    suspend fun addNewCustomSurvey(title: String, desc: String, reward: Double, mins: Int, cat: String, questions: List<SurveyQuestion>) {
        withContext(Dispatchers.IO) {
            val srvId = "srv_custom_" + System.currentTimeMillis()
            val newSrv = SurveyEntity(
                id = srvId,
                title = title,
                description = desc,
                reward = reward,
                timeInMinutes = mins,
                category = cat,
                questions = questions,
                isCompleted = false
            )
            surveyDao.insertSurvey(newSrv)
        }
    }
}
