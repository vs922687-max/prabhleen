package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.converter.SurveyTypeConverter
import com.example.data.dao.SurveyDao
import com.example.data.dao.TransactionDao
import com.example.data.dao.UserDao
import com.example.data.model.SurveyEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity

@Database(
    entities = [UserEntity::class, SurveyEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(SurveyTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun surveyDao(): SurveyDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "survey_earn_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
