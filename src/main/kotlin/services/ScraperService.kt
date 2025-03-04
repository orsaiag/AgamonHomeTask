package services

import ScrapingTask
import Status
import org.jsoup.Jsoup
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import javax.ws.rs.BadRequestException

class ScraperService {
    private val tasks = ConcurrentHashMap<Int, ScrapingTask>()
    private val urlToTaskId = ConcurrentHashMap<String, Int>()
    private val executor = Executors.newFixedThreadPool(5)


    fun getScrapingResult(taskId: Int): ScrapingTask? {
        return tasks[taskId]
    }

    fun extractLinks(url: String): Int {
        return getHtmlFromUrl(url)
    }

    private fun generateTaskId(url: String): Int {
        return url.hashCode()
    }

    private fun getHtmlFromUrl(url: String): Int {
        if (url.isBlank()) {
            throw BadRequestException("URL parameter is required")
        } else {
            urlToTaskId[url]?.let { existingTaskId -> return existingTaskId }
            val taskId = generateTaskId(url)
            createNewTask(url, taskId)
            startScrapingJob(url, taskId)

            return taskId
        }
    }

    private fun createNewTask(url: String, taskId: Int) {
        urlToTaskId[url] = taskId
        tasks[taskId] = ScrapingTask(status = Status.IN_PROGRESS)
    }

    private fun startScrapingJob(url: String, taskId: Int) {
        executor.submit {
            try {
                val links = extractLinksFromUrl(url)
                tasks[taskId] = ScrapingTask(status = Status.COMPLETED, links = links)
            } catch (e: Exception) {
                tasks[taskId] = ScrapingTask(status = Status.FAILED, error = e.message)
            }
        }
    }

    private fun extractLinksFromUrl(url: String): List<String> {
        val document = Jsoup.connect(url).get()
        val links = document.select("link").map { it.outerHtml() }
        return links
    }
}