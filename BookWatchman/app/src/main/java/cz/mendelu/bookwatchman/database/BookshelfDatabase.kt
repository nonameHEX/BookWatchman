package cz.mendelu.bookwatchman.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class BookshelfDatabase : RoomDatabase() {

     abstract fun booksDao(): BooksDao

    companion object {

        private var INSTANCE: BookshelfDatabase? = null

        fun getDatabase(context: Context): BookshelfDatabase {
            if (INSTANCE == null) {
                synchronized(BookshelfDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            BookshelfDatabase::class.java, "bookshelf_database"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}