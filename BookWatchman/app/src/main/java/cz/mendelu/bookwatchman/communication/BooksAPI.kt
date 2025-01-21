package cz.mendelu.bookwatchman.communication

import cz.mendelu.bookwatchman.communication.model.BookItem
import cz.mendelu.bookwatchman.communication.model.BookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksAPI {
    @Headers("Content-Type: application/json")
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Response<BookResponse>

    @Headers("Content-Type: application/json")
    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id")id: String
    ): Response<BookItem>
}