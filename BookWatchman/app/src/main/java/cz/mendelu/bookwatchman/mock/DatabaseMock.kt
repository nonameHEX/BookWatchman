package cz.mendelu.bookwatchman.mock

import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.BookState

object DatabaseMock {
    val book1 = Book(
        title = "Mock Book 1",
        author = "Author 1",
        pageCount = 200,
        state = BookState.TO_READ.value
    ).apply {
        id = 1
        genre = "Fiction"
        description = "Description for Mock Book 1"
        isbn = "1234567890"
        pictureUri = "http://example.com/image1.jpg"
    }

    val book2 = Book(
        title = "Mock Book 2",
        author = "Author 2",
        pageCount = 350,
        state = BookState.READ.value
    ).apply {
        id = 2
        genre = "Non-fiction"
        description = "Description for Mock Book 2"
        isbn = "0987654321"
        pictureUri = "http://example.com/image2.jpg"
    }

    val booksList = mutableListOf(book1, book2)
}