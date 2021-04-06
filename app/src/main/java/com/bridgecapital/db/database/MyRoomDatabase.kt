package com.bridgecapital.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bridgecapital.db.dao.TransactionDao
import com.bridgecapital.db.table.TransactionTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [TransactionTable::class],version = 1,exportSchema = true)
abstract class MyRoomDatabase: RoomDatabase() {
    abstract fun transactionDao():TransactionDao

    companion object{
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null
        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): MyRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "my_database"
                ).addCallback(TransactionDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    class TransactionDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    //populateDatabase(database.userDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
        }
    }
}