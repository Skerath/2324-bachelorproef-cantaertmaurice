package mauricecantaert

import io.quarkus.logging.Log
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import mauricecantaert.ai.ImageAnalyzer
import mauricecantaert.ai.SuggestionGenerator
import mauricecantaert.domain.*
import mauricecantaert.dto.SuggestionDto
import mauricecantaert.dto.toSuggestionDto
import org.jboss.resteasy.reactive.RestForm
import java.io.File


@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
class UserResource {

    @Inject
    lateinit var imageAnalyzer: ImageAnalyzer

    @Inject
    lateinit var suggestionGenerator: SuggestionGenerator

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    fun getSuggestions(
        @RestForm("image") file: File,
    ): SuggestionDto {
        val user = User.byName("Maurice Cantaert") ?: throw BadRequestException("User can't be null")

        // Fetch equipment details using a custom Langchain4j implementation
        val equipmentDetails = imageAnalyzer.getEquipmentDetails(file)

        // Fetch latest input from trainer for corresponding equipment type, if there is any
        val trainerInput = TrainerInput.latest(user, equipmentDetails.type)

        // Assign the user to the request and save it to the database
        equipmentDetails.client = user
        equipmentDetails.persist()

        // Generate suggestions and save them to the database
        val response = suggestionGenerator.getSuggestion(equipmentDetails, trainerInput)
        response.persist()

        // Return the suggestion's content
        Log.info(response.suggestion)
        return response.suggestion.toSuggestionDto()
    }
}