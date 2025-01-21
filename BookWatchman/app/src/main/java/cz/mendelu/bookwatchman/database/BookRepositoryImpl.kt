package cz.mendelu.bookwatchman.database

import kotlinx.coroutines.flow.Flow

class BookRepositoryImpl(private val booksDao: BooksDao) : IBookRepository {
    override fun getAll(): Flow<List<Book>> {
        return booksDao.getAll()
    }

    override suspend fun getBookById(id: Long): Book {
        return booksDao.getBookById(id)
    }

    override suspend fun insert(book: Book) {
        booksDao.insert(book)
    }

    override suspend fun update(book: Book) {
        booksDao.update(book)
    }

    override suspend fun delete(book: Book) {
        booksDao.delete(book)
    }

    override suspend fun deleteAll() {
        booksDao.deleteAll()
    }

    override suspend fun changeState(id: Long, state: Int) {
        booksDao.changeState(id, state)
    }

    // HELP METHODS
    override suspend fun getBooksCount(): Int {
        return booksDao.getBooksCount()
    }
}