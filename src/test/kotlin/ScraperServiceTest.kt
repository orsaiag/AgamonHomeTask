import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.ScraperService
import javax.ws.rs.BadRequestException

class ScraperServiceTest {

    private lateinit var scraperService: ScraperService

    @BeforeEach
    fun setUp() {
        scraperService = ScraperService()
    }

    @Test
    fun startScrapingValidUrl() {
        val taskId = scraperService.extractLinks("https://github.com/orsaiag/AgamonHomeTask")
        assertNotNull(taskId)
        val task = scraperService.getScrapingResult(taskId)
        assertNotNull(task)
        assertEquals(Status.IN_PROGRESS, task!!.status)
        assertNull(task.links)
    }

    @Test
    fun scrapingTwiceExistingUrl() {
        val url = "https://github.com/orsaiag/AgamonHomeTask"
        val taskId1 = scraperService.extractLinks(url)
        val taskId2 = scraperService.extractLinks(url)

        assertEquals(taskId1, taskId2) // Same task ID should be returned for the same URL
    }

    @Test
    fun scrapingWithEmptyURLThrowsError() {
        assertThrows<BadRequestException> {
            scraperService.extractLinks("")
        }
    }

    @Test
    fun scrapingSuccessfully() {
        val url = "https://www.w3.org/"

        val taskId = scraperService.extractLinks(url)
        Thread.sleep(3000) // Wait for async task to complete

        val task = scraperService.getScrapingResult(taskId)
        assertNotNull(task)
        assertEquals(Status.COMPLETED, task?.status)
        assertNotNull(task!!.links)
        assertTrue(task.links!!.isNotEmpty()) // Ensure links were extracted
    }

    @Test
    fun getMissingUrl() {
        val task = scraperService.getScrapingResult(12)
        assertNull(task)
    }

    @Test
    fun startScrapingInvalidUrl() {
        val taskId = scraperService.extractLinks("http")
        assertNotNull(taskId)

        Thread.sleep(3000)
        val task = scraperService.getScrapingResult(taskId)
        assertNotNull(task)
        assertEquals(Status.FAILED, task!!.status)
        assertNull(task.links)
        assertTrue(task.error!!.contains("The supplied URL, 'http', is malformed."))
    }
}
