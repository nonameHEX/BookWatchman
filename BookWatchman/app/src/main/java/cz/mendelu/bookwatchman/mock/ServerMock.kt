package cz.mendelu.bookwatchman.mock

import cz.mendelu.bookwatchman.communication.model.AccessInfo
import cz.mendelu.bookwatchman.communication.model.BookItem
import cz.mendelu.bookwatchman.communication.model.BookResponse
import cz.mendelu.bookwatchman.communication.model.ImageLinks
import cz.mendelu.bookwatchman.communication.model.SaleInfo
import cz.mendelu.bookwatchman.communication.model.SearchInfo
import cz.mendelu.bookwatchman.communication.model.VolumeInfo

object ServerMock {
    fun getMockBooksResponse(): BookResponse {
        val bookItem = BookItem(
            kind = "books#volume",
            id = "1",
            etag = "etag1",
            selfLink = "https://www.googleapis.com/books/v1/volumes/1",
            volumeInfo = VolumeInfo(
                title = "Mock Book Title",
                authors = listOf("Author Name"),
                publisher = "Mock Publisher",
                publishedDate = "2020-01-01",
                description = "A mock description of the book.",
                industryIdentifiers = null,
                readingModes = null,
                pageCount = 300,
                printedPageCount = 300,
                printType = null,
                categories = null,
                maturityRating = null,
                allowAnonLogging = null,
                contentVersion = null,
                panelizationSummary = null,
                imageLinks = ImageLinks(
                    smallThumbnail = "http://example.com/small_thumbnail.jpg",
                    thumbnail = "http://example.com/thumbnail.jpg"
                ),
                language = null,
                previewLink = null,
                infoLink = null,
                canonicalVolumeLink = null
            ),
            saleInfo = SaleInfo(
                country = null,
                saleability = null,
                isEbook = null
            ),
            accessInfo = AccessInfo(
                country = null,
                viewability = null,
                embeddable = null,
                publicDomain = null,
                textToSpeechPermission = null,
                epub = null,
                pdf = null,
                webReaderLink = null,
                accessViewStatus = null,
                quoteSharingAllowed = null
            ),
            searchInfo = SearchInfo(
                textSnippet = null
            )
        )

        return BookResponse(
            kind = "books#volumes",
            totalItems = 1,
            items = listOf(bookItem)
        )
    }

    fun getMockBookById(id: String): BookItem? {
        return if (id == "1") {
            BookItem(
                kind = "books#volume",
                id = "1",
                etag = "etag1",
                selfLink = "https://www.googleapis.com/books/v1/volumes/1",
                volumeInfo = VolumeInfo(
                    title = "Mock Book Title",
                    authors = listOf("Author Name"),
                    publisher = "Mock Publisher",
                    publishedDate = "2020-01-01",
                    description = "A mock description of the book.",
                    industryIdentifiers = null,
                    readingModes = null,
                    pageCount = 300,
                    printedPageCount = 300,
                    printType = null,
                    categories = null,
                    maturityRating = null,
                    allowAnonLogging = null,
                    contentVersion = null,
                    panelizationSummary = null,
                    imageLinks = ImageLinks(
                        smallThumbnail = "http://example.com/small_thumbnail.jpg",
                        thumbnail = "http://example.com/thumbnail.jpg"
                    ),
                    language = null,
                    previewLink = null,
                    infoLink = null,
                    canonicalVolumeLink = null
                ),
                saleInfo = SaleInfo(
                    country = null,
                    saleability = null,
                    isEbook = null
                ),
                accessInfo = AccessInfo(
                    country = null,
                    viewability = null,
                    embeddable = null,
                    publicDomain = null,
                    textToSpeechPermission = null,
                    epub = null,
                    pdf = null,
                    webReaderLink = null,
                    accessViewStatus = null,
                    quoteSharingAllowed = null
                ),
                searchInfo = SearchInfo(
                    textSnippet = null
                )
            )
        } else {
            null
        }
    }
}