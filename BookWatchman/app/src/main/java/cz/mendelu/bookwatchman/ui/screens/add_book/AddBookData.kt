package cz.mendelu.bookwatchman.ui.screens.add_book

import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.BookState

class AddBookData {
    var book: Book = Book(
        state = BookState.TO_READ.value,
        title = "",
        author = "",
        pageCount = 0,
    )
}