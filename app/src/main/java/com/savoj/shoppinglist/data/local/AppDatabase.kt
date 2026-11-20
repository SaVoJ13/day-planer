package com.savoj.shoppinglist.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.savoj.shoppinglist.data.local.dao.CategoryDao
import com.savoj.shoppinglist.data.local.dao.RecentItemDao
import com.savoj.shoppinglist.data.local.dao.ShoppingItemDao
import com.savoj.shoppinglist.data.local.dao.ShoppingListDao
import com.savoj.shoppinglist.data.local.entity.CategoryEntity
import com.savoj.shoppinglist.data.local.entity.RecentItemEntity
import com.savoj.shoppinglist.data.local.entity.ShoppingItemEntity
import com.savoj.shoppinglist.data.local.entity.ShoppingListEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ShoppingListEntity::class,
        ShoppingItemEntity::class,
        CategoryEntity::class,
        RecentItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recentItemDao(): RecentItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE shopping_lists ADD COLUMN is_shared INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE shopping_lists ADD COLUMN share_code TEXT DEFAULT NULL")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shopping_list.db"
                ).addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).categoryDao().insertCategories(DEFAULT_CATEGORIES)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_CATEGORIES = listOf(
            CategoryEntity(1, "category_fruits_vegetables", "fruit", "#4CAF50", 1),
            CategoryEntity(2, "category_dairy", "dairy", "#2196F3", 2),
            CategoryEntity(3, "category_meat_fish", "meat", "#F44336", 3),
            CategoryEntity(4, "category_bakery", "bakery", "#FF9800", 4),
            CategoryEntity(5, "category_grocery", "grocery", "#795548", 5),
            CategoryEntity(6, "category_drinks", "drinks", "#9C27B0", 6),
            CategoryEntity(7, "category_household", "household", "#009688", 7),
            CategoryEntity(8, "category_other", "other", "#607D8B", 8)
        )
    }
}
