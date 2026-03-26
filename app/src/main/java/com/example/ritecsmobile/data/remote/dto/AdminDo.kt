package com.example.ritecsmobile.data.remote.dto

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