package cz.mendelu.bookwatchman.communication

import cz.mendelu.bookwatchman.communication.model.BookItem
import cz.mendelu.bookwatchman.communication.model.BookResponse
import java.io.File

interface IBooksRemoteRepository : IBaseRemoteRepository{
    suspend fun searchBooks(query: String): CommunicationResult<BookResponse>
    suspend fun getBookById(id: String): CommunicationResult<BookItem>

    // Pro stahování obrázku
    suspend fun downloadImage(url: String, outputDir: File, outputFileName: String): Any?
}