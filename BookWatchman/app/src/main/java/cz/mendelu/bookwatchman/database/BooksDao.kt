package cz.mendelu.bookwatchman.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BooksDao {
    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): Book

    @Insert
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("DELETE FROM books")
    suspend fun deleteAll()

    @Query("UPDATE books SET state = :state WHERE id = :id")
    suspend fun changeState(id: Long, state: Int)

    // HELP METHODS
    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBooksCount(): Int
}