package cz.mendelu.bookwatchman.ui.screens.add_book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.communication.CommunicationResult
import cz.mendelu.bookwatchman.communication.IBooksRemoteRepository
import cz.mendelu.bookwatchman.communication.model.BookItem
import cz.mendelu.bookwatchman.database.IBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val apiRepository: IBooksRemoteRepository,
    private val databaseRepository: IBookRepository
) : ViewModel(){
    private val _uiState: MutableStateFlow<AddBookUIState> = MutableStateFlow(value = AddBookUIState.Loading)
    val uiState: StateFlow<AddBookUIState> get() = _uiState.asStateFlow()

    var data: AddBookData = AddBookData()

    fun loadBook(id: String?){
        if (!id.isNullOrEmpty()) {
            viewModelScope.launch {
                val result = withContext(Dispatchers.IO) {
                    apiRepository.getBookById(id)
                }

                when(result) {
                    is CommunicationResult.Success -> {
                        _uiState.update {
                            AddBookUIState.DataLoaded(result.data)
                        }
                    }
                    is CommunicationResult.ConnectionError -> {
                        _uiState.update {
                            AddBookUIState.Error(AddBookError(R.string.no_internet_connection))
                        }
                    }
                    is CommunicationResult.Error -> {
                        if (result.error.message == "Empty response body") {
                            _uiState.update {
                                AddBookUIState.Error(AddBookError(R.string.empty_response_body))
                            }
                        } else {
                            _uiState.update {
                                AddBookUIState.Error(AddBookError(R.string.generic_error))
                            }
                        }
                    }
                    is CommunicationResult.Exception -> {
                        _uiState.update {
                            AddBookUIState.Error(AddBookError(R.string.generic_error))
                        }
                    }

                }
            }
        } else {
            _uiState.value = AddBookUIState.ManualAdd
        }
    }

    fun setData(book: BookItem?){
        if (book != null){
            data.book.title = book.volumeInfo.title
            data.book.author = book.volumeInfo.authors?.joinToString(", ") ?: ""
            data.book.pageCount = book.volumeInfo.printedPageCount ?: 0
            data.book.pagesRead = 0
            data.book.genre = book.volumeInfo.categories?.first() ?: ""
            data.book.isbn = book.volumeInfo.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier ?: ""
            data.book.description = cleanHtml(book.volumeInfo.description ?: "")
        }else{
            data.book.pagesRead = 0
            data.book.genre = ""
            data.book.isbn = ""
            data.book.description = ""
        }
        _uiState.update {
            AddBookUIState.Changed
        }
    }

    fun updateToDefault(){
        _uiState.update {
            AddBookUIState.Default
        }
    }

    private fun cleanHtml(html: String): String {
        return html.replace(Regex("<[^>]*>"), "")
    }

    fun onTitleChange(title: String){
        data.book.title = title
        _uiState.update {
            AddBookUIState.Changed
        }
    }
    fun onAuthorChange(author: String){
        data.book.author = author
        _uiState.update {
            AddBookUIState.Changed
        }
    }
    fun onPageCountChange(pageCount: Int) {
        data.book.pageCount = pageCount
        _uiState.update {
            AddBookUIState.Changed
        }
    }
    fun onGenreChange(genre: String){
        data.book.genre = genre
        _uiState.update {
            AddBookUIState.Changed
        }
    }
    fun onIsbnChange(isbn: String) {
        data.book.isbn = isbn
        _uiState.update {
            AddBookUIState.Changed
        }
    }
    fun onDescriptionChange(description: String) {
        data.book.description = description
        _uiState.update {
            AddBookUIState.Changed
        }
    }

    fun downloadBookImage(imageUrl: String, outputDir: File) {
        viewModelScope.launch {
            val fileNameUri = "image_${System.currentTimeMillis()}.jpg"
            val outputFile = apiRepository.downloadImage(imageUrl, outputDir, fileNameUri) as File?
            if (outputFile != null) {
                data.book.pictureUri = fileNameUri
                _uiState.update { AddBookUIState.Changed }
            } else {
                _uiState.update { AddBookUIState.Error(AddBookError(R.string.error_during_book_cover_download)) }
            }
        }
    }
    fun onPictureChange(pictureUri: String) {
        data.book.pictureUri = pictureUri
        _uiState.update {
            AddBookUIState.Changed
        }
    }

    fun saveBook(){
        viewModelScope.launch {
            if(data.book != null){
                databaseRepository.insert(data.book)
            }
        }
    }
}
