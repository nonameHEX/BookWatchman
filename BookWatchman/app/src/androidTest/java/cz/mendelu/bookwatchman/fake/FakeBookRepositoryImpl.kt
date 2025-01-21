package cz.mendelu.bookwatchman.fake

import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.IBookRepository
import cz.mendelu.bookwatchman.mock.DatabaseMock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeBookRepositoryImpl @Inject constructor() : IBookRepository {
    // Získání všech knih
    override fun getAll(): Flow<List<Book>> {
        return flow {
            emit(DatabaseMock.booksList)  // Vrací seznam knih z DatabaseMock
        }
    }

    // Získání knihy podle ID
    override suspend fun getBookById(id: Long): Book {
        return DatabaseMock.booksList.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Book not found") // Nebo vrátit defaultní hodnotu
    }

    // Vložení nové knihy
    override suspend fun insert(book: Book) {
        val newId = (DatabaseMock.booksList.maxOfOrNull { it.id ?: 0 } ?: 0) + 1
        book.id = newId
        DatabaseMock.booksList.add(book) // Přidání knihy do seznamu
    }

    // Aktualizace knihy
    override suspend fun update(book: Book) {
        val index = DatabaseMock.booksList.indexOfFirst { it.id == book.id }
        if (index != -1) {
            DatabaseMock.booksList[index] = book // Aktualizace existující knihy
        } else {
            throw NoSuchElementException("Book not found")
        }
    }

    // Smazání knihy
    override suspend fun delete(book: Book) {
        DatabaseMock.booksList.removeIf { it.id == book.id } // Odstranění knihy
    }

    // Smazání všech knih
    override suspend fun deleteAll() {
        DatabaseMock.booksList.clear() // Vyprázdnění seznamu knih
    }

    // Změna stavu knihy
    override suspend fun changeState(id: Long, state: Int) {
        val book = DatabaseMock.booksList.firstOrNull { it.id == id }
        book?.let {
            it.state = state // Změna stavu knihy
        }
    }

    // Získání počtu knih
    override suspend fun getBooksCount(): Int {
        return DatabaseMock.booksList.size // Počet knih v seznamu
    }

}