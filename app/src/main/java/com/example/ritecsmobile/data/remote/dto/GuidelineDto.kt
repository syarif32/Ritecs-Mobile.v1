package com.example.ritecsmobile.data.remote.dto

data class GuidelineResponse(
    val status: String,
    val data: GuidelineDataDto
)

data class GuidelineDataDto(
    val schemes: List<SchemeDto>,
    val steps: List<StepDto>
)

data class SchemeDto(
    val id: Int,
    val title: String,
    val description: String?
)

data class StepDto(
    val id: Int,
    val title: String,
    val description: String?
)