package cz.mendelu.bookwatchman.communication

import android.util.Log
import cz.mendelu.bookwatchman.BuildConfig
import cz.mendelu.bookwatchman.communication.model.BookItem
import cz.mendelu.bookwatchman.communication.model.BookResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

import java.io.File
import java.io.FileOutputStream
import okhttp3.OkHttpClient
import okhttp3.Request

class BooksRemoteRepositoryImpl @Inject constructor(private val booksAPI: BooksAPI) : IBooksRemoteRepository {
    override suspend fun searchBooks(query: String): CommunicationResult<BookResponse> {
        return makeApiCall { booksAPI.searchBooks(query = query, apiKey = BuildConfig.API_KEY)}
    }

    override suspend fun getBookById(id: String): CommunicationResult<BookItem> {
        return makeApiCall { booksAPI.getBookById(id = id) }
    }

    private val client = OkHttpClient()
    override suspend fun downloadImage(
        url: String,
        outputDir: File,
        outputFileName: String
    ): Any? {
        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    inputStream?.let {
                        val outputFile = File(outputDir, outputFileName)
                        FileOutputStream(outputFile).use { outputStream ->
                            it.copyTo(outputStream)
                        }
                        inputStream.close()
                        return@withContext outputFile
                    }
                } else {
                    Log.d("BooksRemoteRepository", "Image download failed: ${response.code}")
                    return@withContext null
                }
            } catch (ex: Exception) {
                Log.d("BooksRemoteRepository", "Error downloading image: ${ex.message}")
                return@withContext null
            }
        }
    }
}