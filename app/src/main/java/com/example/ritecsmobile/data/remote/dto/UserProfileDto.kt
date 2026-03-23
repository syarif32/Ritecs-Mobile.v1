package com.example.ritecsmobile.data.remote.dto

data class UserProfileResponse(
    val status: String,
    val data: UserProfileDataDto
)

data class UserProfileDataDto(
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val img_path: String?,
    val is_member: Boolean,
    val member_number: String?,
    // Tambahan untuk Form:
    val nik: String?,
    val birthday: String?,
    val phone: String?,
    val address: String?,
    val city: String?,
    val province: String?,
    val institution: String?,
    val ktp_path: String?,
    val card_image_path: String?,
    val card_back_image_path: String?,
    val has_pending_transaction: Boolean? = false,
)

data class BankResponse(
    val status: String,
    val data: List<BankDto>
)

data class BankDto(
    val bank_id: Int,
    val bank_name: String,
    val account_name: String,
    val account_number: String
) {
}
//  simpan data
data class SimpleResponse(
    val status: String,
    val message: String
)