package com.example.ritecsmobile.data.remote.dto

data class BookDto(
    val book_id: Int,
    val title: String,
    val synopsis: String?,
    val publisher: String?,
    val cover_path: String?,

    val pages: Int?,
    val width: Double?,
    val length: Double?,
    val thickness: Double?,
    val publish_date: String?,
    val isbn: String?,
    val ebook_path: String?,

    val print_price: Int?,
    val ebook_price: Int?,
    val visitor_count: Int?,
    val download_count: Int?,

    val writers: List<WriterDto>?,
    val categories: List<CategoryDto>?
)


data class WriterDto(val writer_id: Int, val name: String)
data class CategoryDto(val category_id: Int, val name: String)

// DTO untuk Keyword Jurnal
data class KeywordDto(
    val keyword_id: Int,
    val name: String
)

// DTO untuk Jurnal
data class JournalDto(
    val journal_id: Int,
    val title: String,
    val cover_path: String?,
    val url_path: String?,
    val keywords: List<KeywordDto>?
)

// Response pembungkusnya
data class JournalResponse(
    val status: String,
    val data: List<JournalDto>
)