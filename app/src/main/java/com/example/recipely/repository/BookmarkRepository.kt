package com.example.recipely.repository

import com.example.recipely.model.BookmarkModel

interface BookingRepository {
    fun createBookmark(bookmark: BookmarkModel, callback: (Boolean, String, String) -> Unit)
    fun getBookmark(bookmarkId: String, callback: (BookmarkModel?, Boolean, String) -> Unit)
    fun updateBookmark(
        bookmarkId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    )
    fun deleteBookmark(bookmarkId: String, callback: (Boolean, String) -> Unit)
    fun getUserBookmarks(userId: String, callback: (List<BookmarkModel>, Boolean, String) -> Unit)
    fun getRecipeBookmarks(
        eventId: String,
        callback: (List<BookmarkModel>, Boolean, String) -> Unit
    )
}