package com.example.ritecsmobile.data.remote.dto
data class TrainingDto(
    val id: Int?,
    val image_path: String?,
    val title: String?,
    val description: String?,
    val schedule: String?,
    val contact_person: String?,
    val price: String?,
    val price_period: String?,
    val price_note: String?,
    val button_text: String?,
    val button_url: String?
)

data class TrainingHakiServiceDto(
    val id: Int?,
    val title: String?,
    val description: String?
)

data class TrainingDataDto(
    val trainings: List<TrainingDto>?,
    val haki_services: List<TrainingHakiServiceDto>?,
    val haki_title: String?,
    val haki_description: String?
)

data class TrainingResponse(
    val status: String,
    val data: TrainingDataDto
)