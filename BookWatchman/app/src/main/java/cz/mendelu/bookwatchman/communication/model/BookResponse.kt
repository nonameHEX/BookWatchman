package cz.mendelu.bookwatchman.communication.model

data class BookResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<BookItem> = emptyList()
)