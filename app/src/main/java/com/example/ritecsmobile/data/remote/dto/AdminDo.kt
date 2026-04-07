package com.example.ritecsmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ActivationResponse(
    val status: String,
    val data: List<ActivationRequestDto>
)

data class ActivationRequestDto(
    val id: Int,
    val email: String,
    val reason: String,
    val other_reason_detail: String?,
    val status: String,
    val created_at: String
)
data class UserManageResponse(
    val status: String,
    val data: List<UserAdminDto>
)

data class UserAdminDto(
    val user_id: Int,
    val first_name: String,
    val last_name: String?,
    val email: String,
    val role: String,
    val is_active: Int,
    val created_at: String
)

data class AdminActionResponse(
    val status: String,
    val message: String
)
// File: com.example.ritecsmobile.data.remote.dto.AdminDashboardDto.kt

data class AdminDashboardResponse(
    val status: String,
    val data: AdminDashboardData
)

//data class AdminDashboardData(
//    // Revenue Stats
//    val totalRevenue: Long,
//    val monthlyGrowth: Double,
//    val successRate: Double,
//    val revenueGrowth: Double,
//    val todayRevenue: Long,
//
//    // Content Stats
//    val totalContent: Int,
//    val totalBooks: Int,
//    val totalJournals: Int,
//    val totalWriters: Int,
//
//    // User Stats
//    val totalUsers: Int,
//    val activeMemberships: Int,
//    val regularUsers: Int,
//
//    // Recent Transactions
//    val recentTransactions: List<TransactionDto>
//)

data class AdminDashboardData(
    val totalRevenue: Long? = 0L,
    val monthlyGrowth: Double? = 0.0,
    val successRate: Double? = 0.0,
    val revenueGrowth: Double? = 0.0,
    val todayRevenue: Long? = 0L,
    val totalContent: Int? = 0,
    val totalBooks: Int? = 0,
    val totalJournals: Int? = 0,
    val totalWriters: Int? = 0,
    val totalUsers: Int? = 0,
    val activeMemberships: Int? = 0,
    val regularUsers: Int? = 0,
    val recentTransactions: List<TransactionDto>? = emptyList()
)

data class TransactionDto(
    val id: Int,
    val user_name: String,
    val amount: Long,
    val status: String,
    val created_at: String
)
// File DTO
data class MembershipTransactionResponse(
    val status: String,
    val data: List<MembershipTransactionDto>
)

data class MembershipTransactionDto(
    val id: Int,
    val user_name: String,
    val email: String,
    val bank_name: String,
    val sender_name: String,
    val bank_account_number: String?,
    val bank_account_name: String?,
    val amount: Long,
    val status: String,
    val type: String,
    val proof_url: String?,
    val created_at: String,
    val is_extended: Int
)

data class UpdateTransactionStatusRequest(
    val status: String // "paid", "pending", atau "rejected"
)
data class RoleManageResponse(
    val status: String,
    val data: List<RoleManageUserDto>
)

data class RoleManageUserDto(
    val user_id: Int,
    val name: String,
    val email: String,
    val role: String
)
//bank
data class AdminBankResponse(
    val status: String,
    val data: List<AdminBankDto>
)

data class AdminBankDto(
    val id: Int,
    val bank_name: String,
    val account_name: String,
    val account_number: String
)

data class BankRequest(
    val bank_name: String,
    val account_name: String,
    val account_number: String
)
data class AdminUserManageResponse(
    val status: String,
    val data: List<AdminUserManageDto>
)

data class AdminUserManageDto(
    val user_id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val is_member: Boolean,
    val member_number: String?,
    val img_path: String?
)

data class MakeMemberRequest(
    val member_number: String,
    val start_date: String,
    val end_date: String
)
//publish buku
data class AdminBookResponse(val status: String, val data: List<AdminBookDto>)
data class AdminBookDto(
    val book_id: Int,
    val title: String,
    val synopsis: String?,
    val publisher: String?,
    val isbn: String?,
    val publish_date: String?,
    val cover_path: String?,
    val ebook_path: String?,
    val pages: Int?,
    val width: Double?,
    val length: Double?,
    val thickness: Double?,
    val print_price: Long?,
    val ebook_price: Long?,
    val categories: List<IdNameDto>,
    val writers: List<IdNameDto>
)



data class IdNameDto(
    @SerializedName(value = "id", alternate = ["writer_id", "category_id", "keyword_id"])
    val id: Int,

    val name: String
)

data class BookFormDataResponse(val status: String, val data: BookFormData)
data class BookFormData(val categories: List<IdNameDto>, val writers: List<IdNameDto>)
//jurnal
data class AdminJournalResponse(val status: String, val data: List<AdminJournalDto>)

data class AdminJournalDto(
    val journal_id: Int,
    val title: String,
    val url_path: String?,
    val cover_path: String?,
    val keywords: List<IdNameDto>
)

data class JournalFormDataResponse(val status: String, val data: JournalFormData)
data class JournalFormData(val keywords: List<IdNameDto>)