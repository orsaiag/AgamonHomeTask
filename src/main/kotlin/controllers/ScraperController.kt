package controllers

import LinkRequest
import ScrapingTask
import services.ScraperService
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/links")
class ScraperController(private val service: ScraperService) {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun extractLinks(urlRequest: LinkRequest): Response {
        val result = service.extractLinks(urlRequest.url)
        return Response.ok(result).build()
    }

    @GET
    @Path("/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getLinks(@PathParam("taskId") taskId: Int): Response {
        val result = service.getScrapingResult(taskId)
        return getResponse(result)
    }

    private fun getResponse(response: ScrapingTask?): Response {
        return if (response == null)
            Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "TaskId wasn't found")).build()
        else Response.ok(response).build()
    }
}