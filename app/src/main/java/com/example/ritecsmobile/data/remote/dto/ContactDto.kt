package com.example.ritecsmobile.data.remote.dto

data class ContactInfoResponse(
    val status: String,
    val data: ContactInfoData
)

data class ContactInfoData(
    val address: String?,
    val email: String?,
    val phone: String?,
    val site: String?,
    val map_link: String?
)

data class ContactSendRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val subject: String,
    val message: String
)