package cz.mendelu.bookwatchman.database

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.mendelu.bookwatchman.R

@Entity(tableName = "books")
data class Book(
    @ColumnInfo(name = "state")
    var state: Int = BookState.TO_READ.value,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "author")
    var author: String = "",

    @ColumnInfo(name = "pageCount")
    var pageCount: Int = 0,
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "pagesRead")
    var pagesRead: Int? = null

    @ColumnInfo(name = "genre")
    var genre: String? = null

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "isbn")
    var isbn: String? = null

    @ColumnInfo(name = "picture_uri")
    var pictureUri: String? = null

    val bookState: BookState
        get() = BookState.fromInt(state)
}

enum class BookState(val value: Int) {
    TO_READ(0),
    READING(1),
    READ(2);

    fun getDisplayName(context: Context): String {
        return when (this) {
            TO_READ -> context.getString(R.string.to_be_read)
            READING -> context.getString(R.string.reading)
            READ -> context.getString(R.string.read)
        }
    }

    companion object {
        fun fromInt(value: Int): BookState {
            return entries.first { it.value == value }
        }
    }
}