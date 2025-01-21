package cz.mendelu.bookwatchman.database

import kotlinx.coroutines.flow.Flow

interface IBookRepository {
    fun getAll(): Flow<List<Book>>
    suspend fun getBookById(id: Long): Book
    suspend fun insert(book: Book)
    suspend fun update(book: Book)
    suspend fun delete(book: Book)
    suspend fun deleteAll()
    suspend fun changeState(id: Long, state: Int)

    // HELP METHODS
    suspend fun getBooksCount(): Int
}