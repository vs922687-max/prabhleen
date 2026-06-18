package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SurveyEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET balance = balance + :amount WHERE id = 1")
    suspend fun updateBalance(amount: Double)

    @Query("UPDATE users SET surveysCompletedCount = surveysCompletedCount + 1 WHERE id = 1")
    suspend fun incrementCompletedCount()

    @Query("UPDATE users SET upiId = :upiId, paytmPhone = :paytmPhone WHERE id = 1")
    suspend fun updateWithdrawalDetails(upiId: String, paytmPhone: String)
}

@Dao
interface SurveyDao {
    @Query("SELECT * FROM surveys")
    fun getAllSurveysFlow(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM surveys WHERE id = :id LIMIT 1")
    fun getSurveyByIdFlow(id: String): Flow<SurveyEntity?>

    @Query("SELECT * FROM surveys WHERE id = :id LIMIT 1")
    suspend fun getSurveyById(id: String): SurveyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurveys(surveys: List<SurveyEntity>)

    @Query("UPDATE surveys SET isCompleted = 1 WHERE id = :id")
    suspend fun markSurveyAsCompleted(id: String)

    @Query("UPDATE surveys SET isCompleted = 0")
    suspend fun resetAllSurveys()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurvey(survey: SurveyEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("UPDATE transactions SET status = :status WHERE id = :id")
    suspend fun updateTransactionStatus(id: Int, status: String)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
}
