 package by.bsuir.sporttracker

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import by.bsuir.sporttracker.db.AppDatabase
import by.bsuir.sporttracker.di.checkLocationModule
import com.github.florent37.androidnosql.AndroidNoSql
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    companion object{
        lateinit var appDatabase: AppDatabase
            private set
    }
    override fun onCreate() {
        super.onCreate()
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `plan` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`name` TEXT  NOT NULL, " +
                        "`trackId` INTEGER  NOT NULL, " +
                        "`sectionString` TEXT  NOT NULL)")
            }
        }
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        )
            //.fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_3_4)
            .build()
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(listOf(checkLocationModule))
        }
        AndroidNoSql.initWithDefault(applicationContext);
    }
}