package cz.mendelu.bookwatchman

import cz.mendelu.bookwatchman.database.BookState
import cz.mendelu.bookwatchman.mock.DatabaseMock
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DashboardFilterTests {

    private val books = DatabaseMock.booksList

    @Test
    fun filterBooksByReadState() {
        // Filter books with state READ
        val readBooks = books.filter { it.state == BookState.READ.value }

        // Assert the result
        assertEquals(1, readBooks.size)
        assertEquals("Mock Book 2", readBooks[0].title)
    }

    @Test
    fun filterBooksByReadingState() {
        // Filter books with state READING
        val readingBooks = books.filter { it.state == BookState.READING.value }

        // Assert the result
        assertEquals(0, readingBooks.size)
    }

    @Test
    fun filterBooksByToReadState() {
        // Filter books with state TO_READ
        val toReadBooks = books.filter { it.state == BookState.TO_READ.value }

        // Assert the result
        assertEquals(1, toReadBooks.size)
        assertEquals("Mock Book 1", toReadBooks[0].title)
    }

    @Test
    fun calculateTotalPagesRead() {
        // Calculate total pages read
        val totalPagesRead = books.sumOf {
            when (it.state) {
                BookState.READ.value -> it.pageCount
                BookState.READING.value -> it.pagesRead ?: 0
                else -> 0
            }
        }

        // Assert the result
        assertEquals(350, totalPagesRead)
    }
}