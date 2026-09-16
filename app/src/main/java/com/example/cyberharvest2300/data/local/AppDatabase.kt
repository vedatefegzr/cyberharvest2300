package com.example.cyberharvest2300.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cyberharvest2300.data.local.dao.CustomerProgressDao
import com.example.cyberharvest2300.data.local.dao.DailyOrderDao
import com.example.cyberharvest2300.data.local.dao.InventoryDao
import com.example.cyberharvest2300.data.local.dao.PlayerCreatureProgressDao
import com.example.cyberharvest2300.data.local.dao.PlayerProfileDao
import com.example.cyberharvest2300.data.local.dao.PlayerWeaponProgressDao
import com.example.cyberharvest2300.data.local.dao.RegionStateDao
import com.example.cyberharvest2300.data.local.entity.CustomerProgress
import com.example.cyberharvest2300.data.local.entity.DailyOrder
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import com.example.cyberharvest2300.data.local.entity.PlayerCreatureProgress
import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import com.example.cyberharvest2300.data.local.entity.PlayerWeaponProgress
import com.example.cyberharvest2300.data.local.entity.RegionState

@Database(
    entities = [
        PlayerProfile::class,
        InventoryItem::class,
        PlayerCreatureProgress::class,
        PlayerWeaponProgress::class,
        RegionState::class,
        DailyOrder::class,
        CustomerProgress::class
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerProfileDao():
            PlayerProfileDao

    abstract fun inventoryDao():
            InventoryDao

    abstract fun playerCreatureProgressDao():
            PlayerCreatureProgressDao

    abstract fun playerWeaponProgressDao():
            PlayerWeaponProgressDao

    abstract fun regionStateDao():
            RegionStateDao

    abstract fun dailyOrderDao():
            DailyOrderDao

    abstract fun customerProgressDao():
            CustomerProgressDao

    companion object {

        @Volatile
        private var INSTANCE:
                AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {

            return INSTANCE
                ?: synchronized(this) {

                    val instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "cyber_harvest_database"
                        )
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE =
                        instance

                    instance
                }
        }
    }
}