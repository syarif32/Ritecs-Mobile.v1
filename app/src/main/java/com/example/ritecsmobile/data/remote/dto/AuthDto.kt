package com.example.ritecsmobile.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData? = null
)

data class LoginData(
    val user: UserDto,
    val role: String,
    val token: String
)
// Data Class Tambahan untuk Auth

data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class RegisterResponse(
    val status: String,
    val message: String,
    val user_id: Int? = null
)

data class VerifyOtpRequest(
    val email: String,
    val otp_code: String
)

data class ResendOtpRequest(
    val email: String
)
data class ManualActivationRequest(
    val email: String,
    val reason: String,
    val other_reason_detail: String? = null
)
data class GoogleLoginRequest(
    val email: String,
    val google_id: String,
    val first_name: String,
    val last_name: String?,
    val img_path: String?
)
data class PasswordConfirmRequest(
    val admin_password: String
)
data class BaseResponse(
    val status: String,
    val message: String
)


data class UserDto(
    val user_id: Int,
    val first_name: String,
    val email: String,
    val membership: MembershipDto? = null
)

data class MembershipDto(
    val id: Int,
    val status: String,
    val package_name: String?,
    val end_date: String?,
    val card_image_path: String?
)
data class BookResponse(
    val status: String,
    val data: List<BookDto>
)


data class MemberResponse(
    val status: String,
    val data: List<MemberDto>
)

data class MemberDto(
    val id: Int,
    val first_name: String?,
    val last_name: String?,
    val guest_first_name: String?,
    val guest_last_name: String?,
    val member_number: String?,
    val status: Int,
    val img_path: String?
)
data class HomeResponse(
    val status: String,
    val data: HomeDataDto
)

data class HomeDataDto(
    val vision: String,
    val mission: String,
    val stats: StatsDto
)

data class StatsDto(
    val members: Int,
    val books: Int,
    val journals: Int,
    val teams: Int
)
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)
