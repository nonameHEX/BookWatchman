package cz.mendelu.bookwatchman.communication

data class CommunicationError(
    val code: Int,
    val message: String? = null
)