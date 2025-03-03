class LinkRequest(
    val url: String
)

enum class Status{
    COMPLETED,
    IN_PROGRESS,
    FAILED
}

data class ScrapingTask(
    val status: Status,
    val links: List<String>? = null,
    val error: String? = null
)