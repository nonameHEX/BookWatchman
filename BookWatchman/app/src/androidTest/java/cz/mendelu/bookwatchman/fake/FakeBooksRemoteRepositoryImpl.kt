package cz.mendelu.bookwatchman.fake

import cz.mendelu.bookwatchman.communication.CommunicationResult
import cz.mendelu.bookwatchman.communication.IBooksRemoteRepository
import cz.mendelu.bookwatchman.communication.model.BookItem
import cz.mendelu.bookwatchman.communication.model.BookResponse
import cz.mendelu.bookwatchman.mock.ServerMock
import java.io.File
import javax.inject.Inject

class FakeBooksRemoteRepositoryImpl @Inject constructor() : IBooksRemoteRepository {
    override suspend fun searchBooks(query: String): CommunicationResult<BookResponse> {
        return CommunicationResult.Success(
            ServerMock.getMockBooksResponse()
        )
    }

    override suspend fun getBookById(id: String): CommunicationResult<BookItem> {
        return CommunicationResult.Success(ServerMock.getMockBookById(id)!!)
    }

    override suspend fun downloadImage(url: String, outputDir: File, outputFileName: String): Any? {
        return null
    }

}